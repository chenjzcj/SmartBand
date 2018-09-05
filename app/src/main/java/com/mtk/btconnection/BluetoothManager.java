/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mtk.btconnection;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.mtk.Constants;
import com.mtk.data.Log;
import com.mtk.data.Util;
import com.mtk.map.MapConstants;
import com.mtk.remotecamera.RemoteCameraService;
import com.mtk.service.MainService;
import com.mtk.util.LogUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 蓝牙管理
 *
 * @author zhongchengjun
 *         Create at 2016/6/7 15:43
 */
public class BluetoothManager {
    // 发送广播到其他
    public static final String BT_BROADCAST_ACTION = "com.mtk.connection.BT_CONNECTION_CHANGED";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    // 广播类型
    public static final int TYPE_BT_CONNECTED = 0x01;//蓝牙连接成功
    public static final int TYPE_BT_CONNECTION_LOST = 0x02;//连接失效
    public static final int TYPE_DATA_SENT = 0x03;//数据发送成功
    public static final int TYPE_DATA_ARRIVE = 0x04;//接收数据成功
    public static final int TYPE_MAPCMD_ARRIVE = 0x05;//接收打开地图请求命令
    // 从蓝牙连接获取的消息类型
    public static final int MESSAGE_STATE_CHANGE = 1;//蓝牙连接状态改变
    public static final int MESSAGE_READ = 2;//从远程设备读取数据(如手表控制手机拍照,找手机)
    public static final int MESSAGE_WRITE = 3;//写数据到远程设备(如找手表)
    public static final int MESSAGE_DEVICE_NAME = 4;//设备名称
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_RING = 6;//找手机
    // State of ReadBuffer
    public static final int READ_IDLE = 0;//读空闲
    public static final int READ_PRE = 1;//读准备
    public static final int READ_CMD = 2;//读命令
    public static final int READ_DATA = 3;

    public int READBUFFER_STATE = READ_IDLE;
    // 错误码
    public static final int BLOCKED = 1;
    public static final int SUCCESS = 0;
    public static final int FAILED = -1;
    //蓝牙连接状态码
    public static final int BLUETOOTH_CONNECT_SUCCESS = 0;
    public static final int BLUETOOTH_NOT_SUPPORT = -1;
    public static final int BLUETOOTH_NOT_ENABLE = -2;
    public static final int BLUETOOTH_NOT_CONNECT = -3;
    // Key names received from the BluetoothConnection Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final byte[] reciveBuffer = new byte[50 * 1024];
    // Debugging
    private static final String LOG_TAG = "BluetoothManager";
    // Buffer of cmd and data
    public static byte[] commandBuffer = null;
    public static byte[] dataBuffer = null;
    public static int reciveBufferLenth = 0;
    public static int cmdBufferLenth = 0;
    public static int dataBufferLenth = 0;
    private static boolean isHandshake = false;
    private static boolean isOlderThanVersionTow = true;
    public int CMD_TYPE = LoadJniFunction.CMD_1;
    public Timer mTimer = new Timer(true);
    // Name of the connected device
    private String mConnectedDeviceName = null;

    // Member object for the chat services
    private BluetoothConnection mBluetoothConnection = null;
    private MessageHandler mHandler = null;
    private Context mContext = null;
    private LoadJniFunction mLoadJniFunction = null;
    //用于监听蓝牙状态变化，建立或者删除蓝牙连接
    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int connectionState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                Log.i(LOG_TAG, "onReceive(), action=" + intent.getAction());
                // 根据蓝牙状态建立或删除蓝牙连接
                switch (connectionState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        setupConnection();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        removeConnection();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                }
            }
        }
    };
    private MessageDataList mMessageDataList = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    public BluetoothManager(Context context) {
        Log.i(LOG_TAG, "BluetoothManager(), BluetoothManager created!");
        mHandler = new MessageHandler(this);
        mContext = context;
        mLoadJniFunction = new LoadJniFunction();
        //注册本地广播用来监听蓝牙的开关状态
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBTReceiver, filter);
        //创建消息列表用来临时保存数据
        mMessageDataList = new MessageDataList(mContext);
        //获取蓝牙adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /*
     * private BluetoothDevice getRemoteDevice() { Log.d(LOG_TAG, "getRemoteDevice()");
     *
     * String address = "29:8F:FE:73:62:60"; BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
     * Log.d(LOG_TAG, "getRemoteDevice(), remoteDevice=" + device.getName() + "\n" + device.getAddress());
     *
     * return device; }
     */

    /**
     * 建立蓝牙连接
     *
     * @return 蓝牙连接状态
     */
    public int setupConnection() {
        Log.d(LOG_TAG, "setupConnection()");
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            return BLUETOOTH_NOT_SUPPORT;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            return BLUETOOTH_NOT_ENABLE;
        }
        // Initialize the BluetoothConnection to perform bluetooth connections
        mBluetoothConnection = new BluetoothConnection(mHandler);
        mBluetoothConnection.startAccept();
        // mBluetoothConnection.connectRemoteDevice(getRemoteDevice());

        Log.d(LOG_TAG, "setupConnection(), setupConnection successfully!");
        return BLUETOOTH_CONNECT_SUCCESS;
    }

    /**
     * 获取连接设备名
     *
     * @return
     */
    public String getConnectedDeviceName() {
        Log.i(LOG_TAG, "getConnectedDeviceName(), mConnectedDeviceName=" + mConnectedDeviceName);
        return mConnectedDeviceName;
    }

    /**
     * 设置蓝牙连接设备名
     *
     * @param connectedDeviceName
     */
    public void setConnectedDeviceName(String connectedDeviceName) {
        Log.i(LOG_TAG, "setConnectedDeviceName(), deviceName=" + mConnectedDeviceName);

        mConnectedDeviceName = connectedDeviceName;
    }

    /**
     * 删除连接
     *
     * @return
     */
    public int removeConnection() {
        Log.i(LOG_TAG, "removeConnection(), Bluetooth connection is removed!");

        if (mBluetoothConnection != null) {
            mBluetoothConnection.stop();
        }

        return BLUETOOTH_CONNECT_SUCCESS;
    }

    /**
     * 蓝牙是否已连接
     *
     * @return 返回true, 表示蓝牙连接成功
     */
    public boolean isBTConnected() {
        boolean isConnected = ((mBluetoothConnection != null) && isHandshake &&
                (mBluetoothConnection.getState() == BluetoothConnection.STATE_CONNECTED));
        Log.i(LOG_TAG, "isBTConnected(), mBluetoothConnection != null=" + (mBluetoothConnection != null));
        Log.i(LOG_TAG, "isBTConnected(), isHandshake=" + isHandshake);
        Log.i(LOG_TAG, "isBTConnected(), isConnected=" + isConnected);
        return isConnected;
    }

    /**
     * 发送数据到手表
     */
    public boolean sendData(byte[] data) {
        boolean isDataSent = false;
        if ((data != null) && isBTConnected() && !RemoteCameraService.isLaunched) {
            sendDataToRemote(LoadJniFunction.CMD_1, data);
            isDataSent = true;
            // Whether has message data not been sent.
            if (mMessageDataList.getMessageDataList().size() > 0) {
                sendDataFromFile();
                // For test
                Log.i(LOG_TAG, "Notice!!!!, has message data not been sent.");
            }
        } else {
            // 如果数据发送失败，就保存数据以后发送它
            mMessageDataList.saveMessageData(data);
        }

        Log.i(LOG_TAG, "sendData(), isDataSent=" + isDataSent);
        return isDataSent;
    }

    /**
     * 发送地图数据到远程设备
     */
    public boolean sendMAPData(byte[] data) {
        if (isBTConnected() && (!RemoteCameraService.isLaunched)) {
            mBluetoothConnection.write(data);
            Log.i(LOG_TAG, "sendMAPData(), isDataSent=" + true);
            return true;
        }
        Log.i(LOG_TAG, "sendMAPData(), isDataSent=" + false);
        return false;
    }

    /**
     * Sends CAP data to remote device.
     */
    public boolean sendCAPCData(byte[] data) {
        if (isBTConnected()) {
            mBluetoothConnection.write(data);
            Log.i(LOG_TAG, "sendCAPCData(), isDataSent=" + true);
            return true;
        }
        Log.i(LOG_TAG, "sendCAPCData(), isDataSent=" + false);
        return false;
    }

    /**
     * Sends MREE data to remote device.
     */
    public boolean sendMREEData(byte[] data) {
        if (isBTConnected()) {
            mBluetoothConnection.write(data);
            Log.i(LOG_TAG, "sendMREEData(), isDataSent=" + true);
            return true;
        }
        Log.i(LOG_TAG, "sendMREEData(), isDataSent=" + false);
        return false;
    }

    /**
     * Sends messages that saved in the file.
     */
    private boolean sendDataFromFile() {
        List<byte[]> messageList = mMessageDataList.getMessageDataList();
        Log.i(LOG_TAG, "sendDataFromFile(), message count=" + messageList.size());
        if (messageList.size() > 0) {
            final int messageCount = messageList.size();
            for (int index = 0; index < messageCount; index++) {
                if ((messageList.get(0) != null) && isBTConnected()) {
                    sendDataToRemote(LoadJniFunction.CMD_1, messageList.get(0));
                    messageList.remove(0);
                    Log.i(LOG_TAG, "sendDataFromFile(), message index=" + index);
                } else {
                    break;
                }
            }
        }
        return false;
    }

    // Send data to remote device, will send data cmd first
    private void sendDataToRemote(int cmd, byte[] data) {
        if (data.length == 0)
            Log.i(LOG_TAG, "sendDataToRemote cmd and data()" + "dataLengthis0");
        Log.i(LOG_TAG, "sendDataToRemote cmd and data()" + Arrays.toString(getCmdBuffer(cmd, String.valueOf(data.length))));
        mBluetoothConnection.write(getCmdBuffer(cmd, String.valueOf(data.length)));
        mBluetoothConnection.write(data);
        Log.i(LOG_TAG, "sendDataToRemote cmd and data() String" + Arrays.toString(data));
        // for Test
        // testReadData(getCmdBuffer(i, String.valueOf(data.length)),getCmdBuffer(i, String.valueOf(data.length)).length);
        // testReadData(data,data.length);
    }

    public void sendMapResult(String result) {
        if (isBTConnected() && !RemoteCameraService.isLaunched) {
            sendCommandToRemote(LoadJniFunction.CMD_5, result);
        }
    }

    public void sendMapDResult(String result) {
        if (isBTConnected() && !RemoteCameraService.isLaunched) {
            sendCommandToRemote(LoadJniFunction.CMD_6, result);
        }
    }

    /**
     * 发送照相机结果与找手表命令
     *
     * @param result 命令
     */
    public void sendCAPCResult(String result) {
        if (isBTConnected()) {
            sendCommandToRemote(LoadJniFunction.CMD_7, result);
        }
    }

    public void sendMREEResult(String result) {
        if (isBTConnected()) {
            sendCommandToRemote(LoadJniFunction.CMD_8, result);
        }
    }

    /**
     * 给远程设备发送命令
     *
     * @param cmd     指令编号
     * @param command 指令详情
     */
    private void sendCommandToRemote(int cmd, String command) {
        Log.i(LOG_TAG, "Send Command to Remote:" + cmd + ":" + command);
        mBluetoothConnection.write(getCmdBuffer(cmd, command));
    }

    // Get CMD string buffer
    private byte[] getCmdBuffer(int cmd, String bufferString) {
        return mLoadJniFunction.getDataCmd(cmd, bufferString);
    }

    /**
     * Save the data haven't sent to file
     */
    public void saveData() {
        Log.i(LOG_TAG, "saveData()");
        mMessageDataList.saveMessageDataList();
    }

    /**
     * 发送广播
     *
     * @param extraType 广播类型
     * @param extraData 广播数据
     */
    private void sendBroadcast(int extraType, byte[] extraData) {
        Log.i(LOG_TAG, "sendBroadcast(), extraType=" + extraType);

        // Fill action and extra type
        Intent broadcastIntent = new Intent(BT_BROADCAST_ACTION);
        broadcastIntent.putExtra(EXTRA_TYPE, extraType);

        // Fill extra data, it is optional
        if (extraData != null) {
            broadcastIntent.putExtra(EXTRA_DATA, extraData);
        }
        // Send broadcast
        mContext.sendBroadcast(broadcastIntent);
    }

    /**
     * 同步时间到手表
     *
     * @throws IOException
     */
    private void sendSyncTime() throws IOException {
        Log.i(LOG_TAG, "sendSyncTime()");
        long currentTimeMillis = System.currentTimeMillis();
        int timestamp = Util.getUtcTime(currentTimeMillis);
        int rawoffSet = Util.getUtcTimeZone(currentTimeMillis);
        String snycTime = String.valueOf(timestamp) + " " + String.valueOf(rawoffSet);
        sendCommandToRemote(LoadJniFunction.CMD_2, snycTime);
    }

    /**
     * 读取手表通过蓝牙连接发送过来的数据
     */
    private void runningReadFSM() {
        Log.i(LOG_TAG, "runningReadFSM() READBUFFER_STATE = " + READBUFFER_STATE);
        switch (READBUFFER_STATE) {
            case READ_IDLE:
                getCommandLenth();
                break;
            case READ_PRE:
                getCmdAndDataLenth();
                break;
            case READ_CMD:
                getData();
                break;
        }
    }

    /**
     * Get the Length of Command.
     * 获取命令长度
     */
    private void getCommandLenth() {
        Log.i(LOG_TAG, "getCommandLenth()");
        if (READBUFFER_STATE != READ_IDLE) {
            return;
        }
        int cmdpos = -1;
        if (reciveBufferLenth < Constants.NOTIFYMINIHEADERLENTH) {
            Log.i(LOG_TAG, "getCommandLenth(): reciveBufferLenth < Constants.NOTIFYMINIHEADERLENTH");
        } else {
            int i;
            for (i = 0; i < reciveBufferLenth - Constants.NOTIFYSYNCLENTH; i++) {
                if ((reciveBuffer[i] == (byte) (0xF0)) && (reciveBuffer[i + 1] == (byte) (0xF0)) && (reciveBuffer[i + 2] == (byte) (0xF0)) && (reciveBuffer[i + 3] == (byte) (0xF1))) {
                    cmdpos = i;
                    Log.i(LOG_TAG, "getCommandLenth(): Get F0F0F0F1 Success");
                    break;
                }
            }
            if (cmdpos != -1) {
                cmdBufferLenth = reciveBuffer[i + 4] << 24 | reciveBuffer[i + 5] << 16 | reciveBuffer[i + 6] << 8 | reciveBuffer[i + 7];
                System.arraycopy(reciveBuffer, Constants.NOTIFYMINIHEADERLENTH, reciveBuffer, 0, reciveBufferLenth - Constants.NOTIFYMINIHEADERLENTH);
                reciveBufferLenth = reciveBufferLenth - Constants.NOTIFYMINIHEADERLENTH;
                READBUFFER_STATE = READ_PRE;
                Log.i(LOG_TAG, "getCommandLenth(): Get cmdBufferLenth Success " + "cmdBufferLenth is " + cmdBufferLenth + "reciveBufferLenth is " + reciveBufferLenth);
                runningReadFSM();

            } else {
                System.arraycopy(reciveBuffer, Constants.NOTIFYMINIHEADERLENTH, reciveBuffer, 0, reciveBufferLenth - Constants.NOTIFYMINIHEADERLENTH);
                reciveBufferLenth = reciveBufferLenth - Constants.NOTIFYMINIHEADERLENTH;
                READBUFFER_STATE = READ_IDLE;
                Log.i(LOG_TAG, "getCommandLenth(): Get cmdBufferLenth Success " + "cmdBufferLenth is " + cmdBufferLenth + "reciveBufferLenth is " + reciveBufferLenth);
                runningReadFSM();
            }
        }
    }

    /**
     * 获取命令与数据长度
     */
    private void getCmdAndDataLenth() {
        Log.i(LOG_TAG, "getCmdAndDataLenth() CMDTYPE = " + CMD_TYPE);
        if (reciveBufferLenth < cmdBufferLenth) {
            Log.i(LOG_TAG, "getDataLenth():reciveBufferLenth < cmdBufferLenth");
        } else {
            commandBuffer = new byte[cmdBufferLenth];
            System.arraycopy(reciveBuffer, 0, commandBuffer, 0, cmdBufferLenth);
            System.arraycopy(reciveBuffer, cmdBufferLenth, reciveBuffer, 0, reciveBufferLenth - cmdBufferLenth);
            reciveBuffer[reciveBufferLenth - cmdBufferLenth] = 0;
            reciveBufferLenth = reciveBufferLenth - cmdBufferLenth;
            Log.i(LOG_TAG, "getDataLenth() :Get cmdBuffer Success " + "cmdBufferLenth is " + cmdBufferLenth + "reciveBufferLenth is " + reciveBufferLenth);
            CMD_TYPE = mLoadJniFunction.getCmdType(commandBuffer, cmdBufferLenth);
            Log.i(LOG_TAG, "Get data Success and the CMD_TYPE is " + CMD_TYPE);

            // noinspection PointlessBooleanExpression
            if (!isBTConnected()) {
                if (mLoadJniFunction.getCmdType(commandBuffer, cmdBufferLenth) == 3) {
                    isHandshake = true;
                    Log.i(LOG_TAG, "isHandshake = true");
                    sendBroadcast(TYPE_BT_CONNECTED, null);
                    sendDataFromFile();
                    READBUFFER_STATE = READ_IDLE;
                    runningReadFSM();
                    return;
                } else if (mLoadJniFunction.getCmdType(commandBuffer, cmdBufferLenth) == 4) {
                    reciveBuffer[0] = 0;
                    reciveBufferLenth = 0;
                    isOlderThanVersionTow = false;
                    READBUFFER_STATE = READ_IDLE;
                    Log.i(LOG_TAG, "getDataLenth():Get the Version Success");
                    return;
                } else {
                    READBUFFER_STATE = READ_IDLE;
                    return;
                }

            }

            if (CMD_TYPE == LoadJniFunction.CMD_1 || CMD_TYPE == LoadJniFunction.CMD_5 || CMD_TYPE == LoadJniFunction.CMD_6 || CMD_TYPE == LoadJniFunction.CMD_7) {
                dataBufferLenth = mLoadJniFunction.getDataLenth(commandBuffer, cmdBufferLenth);
                Log.i(LOG_TAG, "getDataLenth():Get dataBufferLenth Success " + "dataBufferLenth is " + dataBufferLenth);
                if (dataBufferLenth == -1) {
                    READBUFFER_STATE = READ_IDLE;
                    return;
                }
            } else {
                READBUFFER_STATE = READ_IDLE;
                return;
            }
            READBUFFER_STATE = READ_CMD;
            runningReadFSM();
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        Log.i(LOG_TAG, "getData() CMDTYPE = " + CMD_TYPE);
        if (dataBufferLenth <= reciveBufferLenth) {
            dataBuffer = new byte[dataBufferLenth];
            System.arraycopy(reciveBuffer, 0, dataBuffer, 0, dataBufferLenth);
            System.arraycopy(reciveBuffer, dataBufferLenth, reciveBuffer, 0, reciveBufferLenth - dataBufferLenth);
            reciveBuffer[reciveBufferLenth - dataBufferLenth] = 0;
            reciveBufferLenth = reciveBufferLenth - dataBufferLenth;
            READBUFFER_STATE = READ_IDLE;
            // reset dataBufferLenth and cmdBufferLenth
            dataBufferLenth = cmdBufferLenth = 0;

            if (CMD_TYPE == LoadJniFunction.CMD_1) {
                sendBroadcast(TYPE_DATA_ARRIVE, dataBuffer);
            } else if (CMD_TYPE == LoadJniFunction.CMD_5 || CMD_TYPE == LoadJniFunction.CMD_6) {
                // Case it's MAP CMD ,send it to MAP Service
                Log.i(LOG_TAG, "sendBroadcast of MAPX OR MAPD :" + CMD_TYPE);
                Log.i(LOG_TAG, "mIsNeedStartBTMapService is :" + MainService.mIsNeedStartBTMapService);
                // noinspection PointlessBooleanExpression
                if (MainService.mIsNeedStartBTMapService) {
                    sendBroadcastToMapService(dataBuffer);
                } else {
                    sendBroadcast(TYPE_MAPCMD_ARRIVE, dataBuffer);
                }
            } else if (CMD_TYPE == LoadJniFunction.CMD_7) {
                sendBroadcastToCapService(dataBuffer);
            }
            // construct a string from the valid bytes in the buffer
            Log.i(LOG_TAG, "reciveBufferLenth is " + reciveBufferLenth);
            if (reciveBufferLenth != 0) {
                runningReadFSM();
            }
        }
    }

    /**
     * 发送广播到地图服务
     */
    public void sendBroadcastToMapService(byte[] dataBuffer) {
        Log.i(LOG_TAG, "sendBroadcastToMapService() dataBuffer = " + Arrays.toString(dataBuffer));
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MapConstants.BT_MAP_BROADCAST_ACTION);
        // Fill extra data, it is optional
        if (dataBuffer != null) {
            broadcastIntent.putExtra(MapConstants.EXTRA_DATA, dataBuffer);
        }
        mContext.sendBroadcast(broadcastIntent);
    }

    /**
     * 发送广播到摄像机服务
     */
    public void sendBroadcastToCapService(byte[] dataBuffer) {
        //找手机[53, 32, 48],关闭找手机[54, 32, 48]
        Log.i(LOG_TAG, "sendBroadcastToCapService() dataBuffer = " + Arrays.toString(dataBuffer));
        Intent broadcastIntent = new Intent(RemoteCameraService.BT_REMOTECAMERA_BROADCAST_ACTION);
        // Fill extra data, it is optional
        if (dataBuffer != null) {
            broadcastIntent.putExtra(RemoteCameraService.EXTRA_DATA, dataBuffer);
        }
        mContext.sendBroadcast(broadcastIntent);
    }

    /**
     * 设置蓝牙状态变化监听
     */
    public interface setBlueToothStateListener {
        void BlueToothSateChanged(int state);
    }

    /**
     * 同步时间
     */
    private void runningSyncTimer() {
        TimerTask task = new TimerTask() {
            public void run() {
                Log.i(LOG_TAG, "Timer Task Run ... isHandshake = " + isHandshake);
                if (isOlderThanVersionTow) {
                    isHandshake = true;
                    sendBroadcast(TYPE_BT_CONNECTED, null);
                    sendDataFromFile();
                    this.cancel();
                    Log.i(LOG_TAG, "mTimer is canceled verstion is old");
                } else {
                    try {
                        sendSyncTime();
                        this.cancel();
                        Log.i(LOG_TAG, "mTimer is canceled verstion is new");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mTimer.schedule(task, 3000);
    }

    private static final class MessageHandler extends Handler {
        private WeakReference<BluetoothManager> mBluetoothManager;

        public MessageHandler(BluetoothManager bluetoothManager) {
            mBluetoothManager = new WeakReference<>(bluetoothManager);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.i(LOG_TAG, "handleMessage(), msg.what=" + msg.what);
            BluetoothManager bluetoothManager = mBluetoothManager.get();
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.i(LOG_TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothConnection.STATE_NONE:        //无蓝牙状态
                            LogUtils.i(LOG_TAG, "STATE_NONE");
                            break;
                        case BluetoothConnection.STATE_CONNECTING:  //蓝牙连接中
                            LogUtils.i(LOG_TAG, "STATE_CONNECTING");
                            break;
                        case BluetoothConnection.STATE_LISTEN:      //蓝牙监听中
                            LogUtils.i(LOG_TAG, "STATE_LISTEN");
                            break;
                        case BluetoothConnection.STATE_CONNECTED:   //蓝牙已连接
                            bluetoothManager.runningSyncTimer();
                            break;
                        case BluetoothConnection.STATE_CONNECT_LOST: //蓝牙连接断开
                            bluetoothManager.sendBroadcast(TYPE_BT_CONNECTION_LOST, null);
                            dataBufferLenth = reciveBufferLenth = cmdBufferLenth = 0;
                            isHandshake = false;
                            isOlderThanVersionTow = true;
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    // 1. merge readBuf to reciveBuf
                    byte[] readBuf = (byte[]) msg.obj;
                    int bytes = msg.arg1;
                    for (int i = 0; i < bytes; i++) {
                        Log.e(LOG_TAG, i + ":" + readBuf[i]);
                    }

                    /*for (int i = reciveBufferLenth; i < reciveBufferLenth + bytes; i++) {
                        reciveBuffer[i] = readBuf[i -
                                reciveBufferLenth];
                    }*/

                    System.arraycopy(readBuf, 0, reciveBuffer, reciveBufferLenth, bytes);
                    reciveBufferLenth = reciveBufferLenth + bytes;
                    Log.i(LOG_TAG, "reciveBufferLenth is " + reciveBufferLenth);
                    bluetoothManager.runningReadFSM();
                    break;
                case MESSAGE_WRITE:
                    bluetoothManager.sendBroadcast(TYPE_DATA_SENT, null);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    bluetoothManager.setConnectedDeviceName(msg.getData().getString(DEVICE_NAME));
                    break;
                case MESSAGE_TOAST:
                    break;
                case MESSAGE_RING:
                    MainService.getInstance().startRingService();
                    break;
            }
        }
    }

}
