package com.mtk.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.mtk.MyApp;
import com.mtk.activity.SettingsActivity;
import com.mtk.btconnection.BluetoothManager;
import com.mtk.data.AppList;
import com.mtk.data.BlockList;
import com.mtk.data.CallMessageBody;
import com.mtk.data.Log;
import com.mtk.data.LogUtil;
import com.mtk.data.MessageHeader;
import com.mtk.data.MessageObj;
import com.mtk.data.NoDataException;
import com.mtk.data.NotificationMessageBody;
import com.mtk.data.PreferenceData;
import com.mtk.data.SmsMessageBody;
import com.mtk.data.Util;
import com.mtk.map.BTMapService;
import com.mtk.map.MapConstants;
import com.mtk.map.SmsController;
import com.mtk.remotecamera.RemoteCameraService;
import com.mtk.util.LogUtils;
import com.mtk.util.ToastUtils;
import com.ruanan.btnotification.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;

/**
 * This class is the main service, it will process the most logic and interact with other modules.
 */
public final class MainService extends Service {
    // Application context
    public static final Context mContext = MyApp.getInstance().getApplicationContext();
    // Whether user need to start BTMapService
    public static final boolean mIsNeedStartBTMapService = true;
    // Debugging
    private static final String LOG_TAG = "MainService";
    // Manage bluetooth connection and data transform
    public static BluetoothManager mBluetoothManager = new BluetoothManager(mContext);
    // Global instance
    private static MainService sInstance = null;
    // Register and unregister SMS service dynamically
    private static NotificationService mNotificationService = null;

    private final ContentObserver mCallLogObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // When the database of Calllog changed
            if (getMissedCallCount() == 0) sendReadMissedCallData();
        }
    };
    // Flag to indicate whether main service has been start
    private boolean mIsMainServiceActive = false;
    // Flag to indicate whether the connection status icon shows
    private boolean mIsConnectionStatusIconShow = false;
    // Register and unregister SMS service dynamically
    private SmsReceiver mSmsReceiver = null;
    private SystemNotificationService mSystemNotificationService = null;

    // Register and unregister call service dynamically
    private CallService mCallService = null;
    private BTMapService mBTMapService = null;

    // The BroadcastReceiver that listens for bluetooth manager broadcast
    private final BroadcastReceiver mBTManagerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothManager.BT_BROADCAST_ACTION.equals(action)) {
                int extraType = intent.getIntExtra(BluetoothManager.EXTRA_TYPE, 0);
                byte[] mIncomingMessageBuffer = intent.getByteArrayExtra(BluetoothManager.EXTRA_DATA);
                Log.i(LOG_TAG, "onReceive(), extraType=" + extraType);

                switch (extraType) {
                    case BluetoothManager.TYPE_BT_CONNECTED:
                        updateConnectionStatus(false);
                        break;
                    case BluetoothManager.TYPE_BT_CONNECTION_LOST:
                        // mIsNeedStartBTMapService = false;
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(MapConstants.BT_MAP_BROADCAST_ACTION);
                        broadcastIntent.putExtra(MapConstants.DISCONNECT, MapConstants.DISCONNECT);
                        mContext.sendBroadcast(broadcastIntent);

                        Intent broadcastExitIntent = new Intent();
                        broadcastExitIntent.setAction(RemoteCameraService.BT_REMOTECAMERA_EXIT_ACTION);
                        // Fill extra data, it is optional
                        context.sendBroadcast(broadcastExitIntent);

                        updateConnectionStatus(false);
                        break;
                    case BluetoothManager.TYPE_DATA_SENT:
                        //ToastUtils.showShortToast(context, "数据发送成功");
                        break;
                    case BluetoothManager.TYPE_DATA_ARRIVE:
                        ToastUtils.showShortToast(context, "数据接收成功");
                        try {
                            parseReadBuffer(mIncomingMessageBuffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case BluetoothManager.TYPE_MAPCMD_ARRIVE:
                        Log.i(LOG_TAG, "MAP REQUEST ARRIVE");
                        try {
                            String mMapRequest = new String(intent.getByteArrayExtra(BluetoothManager.EXTRA_DATA), MessageObj.CHARSET);
                            String[] mMapRequests = mMapRequest.split(" ");
                            if (Integer.valueOf(mMapRequests[0]) == MapConstants.SRV_MAPC_ADP_CONNECT_REQUEST) {
                                sendMapResult(String.valueOf(MapConstants.SRV_MAPC_ADP_CMD_SET_FOLDER));
                                if (mBTMapService == null) {
                                    startMapService();
                                }

                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    };
    private RemoteCameraService mRemoteCameraService = null;

    public MainService() {
        Log.i(LOG_TAG, "MainService(), MainService in construction!");
    }

    /**
     * Return the instance of main service.
     *
     * @return main service instance
     */
    public static MainService getInstance() {
        if (sInstance == null) {
            Log.i(LOG_TAG, "getInstance(), Main service is null.");
            startMainService();
        }
        return sInstance;
    }

    /**
     * 开启MainService服务
     */
    private static void startMainService() {
        Log.i(LOG_TAG, "startMainService()");
        Intent startServiceIntent = new Intent(mContext, MainService.class);
        mContext.startService(startServiceIntent);
    }

    private static void stopMainService() {
        Log.i(LOG_TAG, "stopMainService()");

        // Now no need to stop main service.
        /*
         * Intent stopServiceIntent = new Intent(mContext, MainService.class); mContext.stopService(stopServiceIntent);
         */
    }

    /**
     * Save notification service instance.
     *
     * @param notificationService
     */
    public static void setNotificationService(NotificationService notificationService) {
        mNotificationService = notificationService;
    }

    /**
     * Clear notification service instance.
     */
    public static void clearNotificationService() {
        mNotificationService = null;
    }

    /**
     * Return whether notification service is started.
     */
    public static boolean isNotificationServiceActived() {
        return mNotificationService != null;
    }

    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "onCreate()");
        updateConnectionStatus(false);
        super.onCreate();
        sInstance = this;
        mIsMainServiceActive = true;

        Map<Object, Object> applist = AppList.getInstance().getAppList();
        if (applist.size() == 0) {
            applist.put(AppList.MAX_APP, (int) AppList.CREATE_LENTH);
            applist.put(AppList.CREATE_LENTH, AppList.BETTRYLOW_APPID);
            applist.put(AppList.CREATE_LENTH, AppList.SMSRESULT_APPID);
            AppList.getInstance().saveAppList(applist);
        }
        if (!applist.containsValue(AppList.BETTRYLOW_APPID)) {
            int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
            applist.remove(AppList.MAX_APP);
            max = max + 1;
            applist.put(AppList.MAX_APP, max);
            applist.put(max, AppList.BETTRYLOW_APPID);
            AppList.getInstance().saveAppList(applist);
        }
        if (!applist.containsValue(AppList.SMSRESULT_APPID)) {
            int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
            applist.remove(AppList.MAX_APP);
            max = max + 1;
            applist.put(AppList.MAX_APP, max);
            applist.put(max, AppList.SMSRESULT_APPID);
            AppList.getInstance().saveAppList(applist);
        }
        initBluetoothManager();
        registerService();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    /**
     * Return whether main service is started.
     *
     * @return Return true, if main service start, otherwise, return false.
     */
    public boolean isMainServiceActive() {
        return mIsMainServiceActive;
    }

    /**
     * Setup bluetooth spp connection
     */
    private void initBluetoothManager() {
        mBluetoothManager.setupConnection();
        // Register broadcast receiver for BluetoothManager
        IntentFilter filter = new IntentFilter(BluetoothManager.BT_BROADCAST_ACTION);
        mContext.registerReceiver(mBTManagerReceiver, filter);
    }

    /**
     * Remove bluetooth spp connection
     */
    private void destoryBluetoothManager() {
        mBluetoothManager.saveData();
        mBluetoothManager.removeConnection();

        mContext.unregisterReceiver(mBTManagerReceiver);
    }

    /**
     * Send notification message to remote device.
     *
     * @param notiMessage notification message data
     */
    public void sendNotiMessage(MessageObj notiMessage) {
        Log.i(LOG_TAG, "sendNotiMessage(),  notiMessageId=" + notiMessage.getDataHeader().getMsgId());
        sendData(notiMessage);
    }

    /**
     * Send system notification message to remote device.
     *
     * @param systemnotiMessage system notification message data
     */
    public void sendSystemNotiMessage(MessageObj systemnotiMessage) {
        Log.i(LOG_TAG, "sendSystemNotiMessage(),  SystemNotiMessageID=" + systemnotiMessage.getDataHeader().getMsgId());
        sendData(systemnotiMessage);
    }

    /**
     * Send SMS message to remote device.
     *
     * @param smsMessage SMS message data
     */
    public void sendSmsMessage(MessageObj smsMessage) {
        Log.i(LOG_TAG, "sendSmsMessage(),  smsMessageId=" + smsMessage.getDataHeader().getMsgId());
        sendData(smsMessage);
    }

    public void sendMapResult(String result) {
        mBluetoothManager.sendMapResult(result);
    }

    public void sendMapDResult(String result) {
        mBluetoothManager.sendMapDResult(result);
    }

    public void sendMapData(byte[] data) {
        mBluetoothManager.sendMAPData(data);
    }

    /**
     * 发送照相机结果与找手表命令
     *
     * @param result 命令
     */
    public void sendCAPCResult(String result) {
        mBluetoothManager.sendCAPCResult(result);
    }

    public void sendCAPCData(byte[] data) {
        mBluetoothManager.sendCAPCData(data);
    }

    public void sendMREEResult(String result) {
        mBluetoothManager.sendMREEResult(result);
    }

    public void sendMREEData(byte[] data) {
        mBluetoothManager.sendMREEData(data);
    }

    /**
     * 发送数据
     *
     * @param dataObj MessageObj
     */
    private void sendData(MessageObj dataObj) {
        byte[] data = genBytesFromObject(dataObj);
        if (data == null) {
            Log.i(LOG_TAG, "sendData(),  genBytesFromObject failed!");
            return;
        }
        // Send data
        mBluetoothManager.sendData(data);
        Log.i(LOG_TAG, "sendData(), data=" + Arrays.toString(data));
    }

    /**
     * 从MessageObj对象中生成字节数组
     *
     * @param dataObj MessageObj
     * @return byte[]
     */
    private byte[] genBytesFromObject(MessageObj dataObj) {
        Log.i(LOG_TAG, "genBytesFromObject(), dataObj=" + dataObj);
        if (dataObj == null) {
            return null;
        }
        // Generate data bytes
        byte[] data = null;
        try {
            data = dataObj.genXmlBuff();
        } catch (IllegalArgumentException | IllegalStateException
                | IOException | XmlPullParserException | NoDataException e1) {
            e1.printStackTrace();
        }
        return data;
    }

    public void receiveData(byte[] data) {
        Log.i(LOG_TAG, "sendData(), data.length=" + data.length);

        // Create message object
        MessageObj dataObj = createObjectFromBytes(data);
        if (dataObj == null) { // No content
        }
    }

    private MessageObj createObjectFromBytes(byte[] data) {
        if (data == null) { // No content
            return null;
        }

        // Create message object
        MessageObj dataObj = null;
        try {
            dataObj = new MessageObj().parseXml(data);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "genObjectFromBytes(), dataObj=" + dataObj);
        return dataObj;
    }

    /*
     * Update connection status, if bluetooth connected, show a notification.
     */
    public void updateConnectionStatus(boolean isCrash) {
        boolean isShowNotification = PreferenceData.isShowConnectionStatus() && mBluetoothManager.isBTConnected();
        Log.i(LOG_TAG, "updateConnectionStatus(), showNotification=" + isShowNotification);
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (isCrash) {
            manager.cancel(R.string.app_name);
            mIsConnectionStatusIconShow = false;
        } else {
            if (isShowNotification) {
                //Create a notification to show connection status
                Notification notification = new Notification();
                notification.icon = R.drawable.ic_connected_status;
                notification.tickerText = mContext.getText(R.string.notification_ticker_text);

                // Set it no clear, it will auto disappear when connection lost
                notification.flags = Notification.FLAG_ONGOING_EVENT;

                Intent intent = new Intent(mContext, SettingsActivity.class);
                @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                        intent, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                /*notification.setLatestEventInfo(mContext, mContext.getText(R.string.notification_title),
                        mContext.getText(R.string.notification_content), pendingIntent);*/

                // Show notification
                Log.i(LOG_TAG, "updateConnectionStatus(), show notification=" + notification);
                manager.notify(R.string.app_name, notification);
                mIsConnectionStatusIconShow = true;
            } else {
                if (mIsConnectionStatusIconShow) {
                    // Remove notification
                    manager.cancel(R.string.app_name);
                    mIsConnectionStatusIconShow = false;
                    Log.i(LOG_TAG, "updateConnectionStatus(),  cancel notification id=" + R.string.app_name);
                }
            }
        }

    }

    // TODO: Only for test, will removed later
    public boolean sendDataTest(byte[] data) {
        return mBluetoothManager.sendData(data);
    }

    /**
     * 开启服务,注册广播接收者
     */
    private void registerService() {
        Log.i(LOG_TAG, "registerService()");
        // 注册低电量,电量变化等广播监听
        startSystemNotificationService();
        //注册远程相机服务
        startRemoteCameraService();
        // showChoicePopup();
        if (mIsNeedStartBTMapService) {
            startMapService();
        }
        // start SMS service
        if (PreferenceData.isSmsServiceEnable()) {
            startSmsService();
        }
        // start call service
        if (PreferenceData.isCallServiceEnable()) {
            // regist call state
            getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mCallLogObserver);
            startCallService();
        }
        //showChoiceNotification();
    }

    private boolean isAllServiceDisable() {
        boolean allServiceDisable = !(PreferenceData.isNotificationServiceEnable() || PreferenceData.isSmsServiceEnable() || PreferenceData.isCallServiceEnable());

        Log.i(LOG_TAG, "isAllServiceDisable(), allServiceDisable=" + allServiceDisable);
        return allServiceDisable;
    }

    /**
     * Start notification service to push notification.
     */
    public void startNotificationService() {
        Log.i(LOG_TAG, "startNotifiService()");

        // Ensure main service is started
        if (!mIsMainServiceActive) {
            startMainService();
        }
    }

    /**
     * Stop notification service
     */
    public void stopNotificationService() {
        Log.i(LOG_TAG, "stopNotifiService()");
        // If no service is enable, stop main service
        if (isAllServiceDisable()) {
            stopMainService();
        }
    }

    /**
     * Start SystemNotificationService to push some other message.
     */
    private void startSystemNotificationService() {
        mSystemNotificationService = new SystemNotificationService();
        //低电量监听
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        registerReceiver(mSystemNotificationService, filter);

        //电量改变监听
        filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mSystemNotificationService, filter);

        // regist adaptor pluged 监听外部电源已连接
        filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        registerReceiver(mSystemNotificationService, filter);

        // regist sms send state
        filter = new IntentFilter(SmsReceiver.SMS_ACTION);
        registerReceiver(mSystemNotificationService, filter);
    }

    /**
     * Start Remote Camera service to get Remote Camera Command and Data.
     */
    private void startRemoteCameraService() {
        Log.i(LOG_TAG, "startRemoteCameraService()");
        // Ensure main service is started
        if (!mIsMainServiceActive) {
            startMainService();
        }
        if (mRemoteCameraService == null) {
            // Start Remote Camera service
            mRemoteCameraService = new RemoteCameraService();
            IntentFilter filter = new IntentFilter(RemoteCameraService.BT_REMOTECAMERA_BROADCAST_ACTION);
            registerReceiver(mRemoteCameraService, filter);
        }
    }

    /**
     * Start Map service to get Map Command and Data.
     */
    private void startMapService() {
        Log.i(LOG_TAG, "startMapService()");
        // Ensure main service is started
        if (!mIsMainServiceActive) {
            startMainService();
        }
        if (mBTMapService == null) {
            // Start BTMap service
            mBTMapService = new BTMapService();
            IntentFilter filter = new IntentFilter(MapConstants.BT_MAP_BROADCAST_ACTION);
            registerReceiver(mBTMapService, filter);
        }
    }

    /**
     * Start SMS service to push new SMS.
     */
    public void startSmsService() {
        Log.i(LOG_TAG, "startSmsService()");
        // Ensure main service is started
        if (!mIsMainServiceActive) {
            startMainService();
        }
        if (mBTMapService == null) {
            // Start SMS service
            mSmsReceiver = new SmsReceiver();
            IntentFilter filter = new IntentFilter(SmsReceiver.SMS_RECEIVED);
            registerReceiver(mSmsReceiver, filter);
        }
    }

    /**
     * Stop SMS service.
     */
    public void stopSmsService() {
        Log.i(LOG_TAG, "stopSmsService()");

        // Stop SMS service
        if (mSmsReceiver != null) {
            unregisterReceiver(mSmsReceiver);
            mSmsReceiver = null;
        }

        // If no service is enable, stop main service
        if (isAllServiceDisable()) {
            stopMainService();
        }
    }

    /**
     * Start call service to push new missed call.
     */
    public void startCallService() {
        Log.i(LOG_TAG, "startCallService()");

        // Ensure main service is started
        if (!mIsMainServiceActive) {
            startMainService();
        }

        // Start SMS service
        mCallService = new CallService(mContext);
        TelephonyManager telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(mCallService, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * Stop call service.
     */
    public void stopCallService() {
        Log.i(LOG_TAG, "stopCallService()");

        // Stop call service
        if (mCallService != null) {
            TelephonyManager telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(mCallService, PhoneStateListener.LISTEN_NONE);
            mCallService = null;
        }

        // If no service is enable, stop main service
        if (isAllServiceDisable()) {
            stopMainService();
        }
    }


    /**
     * Stop Map service.
     */
    void stopMapService() {
        Log.i(LOG_TAG, "stopMapService()");

        // Stop SMS service
        if (mBTMapService != null) {
            SmsController mSmsController = new SmsController(mContext);
            mSmsController.clearDeletedMessage();
            unregisterReceiver(mBTMapService);
            mBTMapService = null;
        }
        // If no service is enable, stop main service
        if (isAllServiceDisable()) {
            stopMainService();
        }
    }

    /**
     * Stop RemoteCamera service.
     */
    void stopRemoteCameraService() {
        Log.i(LOG_TAG, "stopRemoteCameraService()");

        // Stop SMS service
        if (mRemoteCameraService != null) {
            unregisterReceiver(mRemoteCameraService);
            mRemoteCameraService = null;
        }
        // If no service is enable, stop main service
        if (isAllServiceDisable()) {
            stopMainService();
        }
    }

    /**
     * 打开铃声服务(点击手表上的"找手机"功能,会开启此服务播放铃声)
     */
    public void startRingService() {
        Log.i(LOG_TAG, "startRingService()");
        mContext.startService(new Intent(mContext, RingService.class));
    }

    /**
     * 停止铃声服务
     */
    public void stopRingService() {
        Log.i(LOG_TAG, "stopRingService()");
        mContext.stopService(new Intent(mContext, RingService.class));
    }

    /**
     * 解析数据
     *
     * @param mIncomingMessageBuffer byte[]
     * @throws IOException
     */
    private void parseReadBuffer(byte[] mIncomingMessageBuffer) throws IOException {
        String filename = "ReadData";
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write(mIncomingMessageBuffer);
        fos.close();

        MessageObj mIncomingMessage = new MessageObj();
        MessageHeader mIncomingMessageHeader;
        String messageSubType;
        try {
            mIncomingMessage = mIncomingMessage.parseXml(mIncomingMessageBuffer);
            mIncomingMessageHeader = mIncomingMessage.getDataHeader();
            messageSubType = mIncomingMessageHeader.getSubType();
            Log.i(LOG_TAG, "parseReadBuffer(),  mIncomingMessage is " + mIncomingMessage.getDataBody().toString());
            Log.i(LOG_TAG, "parseReadBuffer(),  mIncomingMessageHeader is " + mIncomingMessageHeader.toString());
            if (messageSubType.equals(MessageObj.SUBTYPE_BLOCK)) {
                addBlockList(mIncomingMessage);
            } else if (messageSubType.equals(MessageObj.SUBTYPE_SMS)) {
                sendSMS(mIncomingMessage);
            } else if (messageSubType.equals(MessageObj.SUBTYPE_MISSED_CALL)) {
                updateMissedCallCountToZero();
            }
            // sendSMSMessage(mIncomingMessage);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    private void sendSMS(MessageObj smsMessage) {
        Log.i(LOG_TAG, "sendSmsMessage(),  notiMessageId=" + smsMessage.getDataHeader().getMsgId());

        String address = ((SmsMessageBody) (smsMessage.getDataBody())).getNumber();
        String message = smsMessage.getDataBody().getContent();

        if (message == null) {
            message = "\n";
        }
        if (message.equals("")) {
            message = "\n";
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(SmsController.MESSAGE_STATUS_SEND_ACTION);
        sendIntent.putExtra("ADDRESS", address);
        sendIntent.putExtra("MESSAGE", message);
        mContext.sendBroadcast(sendIntent);
        /*
         * use the smscontroller to push message. if (message == null) { message = "\n"; } Log.i(LOG_TAG,
         * "sendSmsMessage(),  address & message is " + address + ":"+ message); if (address != null && (message.length() <=
         * 70)) { smsMgr.sendTextMessage(address, null, message, pi, null); } else if (address != null && (message.length() >
         * 70)) { ArrayList<String> mSMSMessages = smsMgr.divideMessage(message); smsMgr.sendMultipartTextMessage(address,
         * null, mSMSMessages,sentPendingIntents, null); }
         */
    }

    /**
     * 未接来电清零
     */
    @SuppressLint("MissingPermission")
    private void updateMissedCallCountToZero() {
        /*
         * Intent showCallLog = new Intent(); showCallLog.setAction(Intent.ACTION_VIEW);
         * showCallLog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); showCallLog.setType(CallLog.Calls.CONTENT_TYPE);
         * mContext.startActivity(showCallLog);
         */
        /*
         * StringBuilder where = new StringBuilder(); where.append(Calls.NEW).append(" = 1"); where.append(" AND ");
         * where.append(Calls.TYPE).append(" = ").append(Calls.MISSED_TYPE); Log.i(LOG_TAG,
         * "updateMissedCallCountToZero, query string=" + where.toString());
         *
         * ContentValues values = new ContentValues(1); values.put(Calls.NEW, 0);
         * mContext.getContentResolver().update(Calls.CONTENT_URI,values,where.toString(),null);
         */
        ContentValues values = new ContentValues();
        values.put(Calls.NEW, 0);
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            values.put(Calls.IS_READ, 1);
        }
        StringBuilder where = new StringBuilder();
        where.append(Calls.NEW);
        where.append(" = 1 AND ");
        where.append(Calls.TYPE);
        where.append(" = ?");
        mContext.getContentResolver().update(Calls.CONTENT_URI, values, where.toString(), new String[]{Integer.toString(Calls.MISSED_TYPE)});
    }

    private void addBlockList(MessageObj blockMessage) {

        String appId = ((NotificationMessageBody) (blockMessage.getDataBody())).getAppID();
        Map<Object, Object> applist = AppList.getInstance().getAppList();
        String appPackageName = (String) applist.get(Integer.parseInt(appId));
        Log.i(LOG_TAG, "addBlockList() appPackageName is :" + appPackageName);
        HashSet<String> blockList = BlockList.getInstance().getBlockList();
        if (!(blockList.contains(appPackageName)) && appPackageName != null) {
            blockList.add(appPackageName);
            BlockList.getInstance().saveBlockList(blockList);
        }
    }

    /**
     * 获取未接来电数量
     *
     * @return 未接来电数量
     */
    private int getMissedCallCount() {
        // setup query spec, look for all Missed calls that are new.
        StringBuilder queryStr = new StringBuilder("type = ");
        queryStr.append(Calls.MISSED_TYPE);
        queryStr.append(" AND new = 1");
        Log.i(LOG_TAG, "getMissedCallCount(), query string=" + queryStr.toString());

        // start the query
        int missedCallCount = 0;
        Cursor cur = mContext.getContentResolver().query(Calls.CONTENT_URI,
                new String[]{Calls._ID}, queryStr.toString(), null, Calls.DEFAULT_SORT_ORDER);
        if (cur != null) {
            missedCallCount = cur.getCount();
            cur.close();
        }

        Log.i(LOG_TAG, "getMissedCallCount(), missed call count=" + missedCallCount);
        return missedCallCount;
    }

    /**
     * 通过蓝牙连接发送已经读取了未接来电的数据给远程设备
     */
    private void sendReadMissedCallData() {
        // Fill message header
        MessageHeader header = new MessageHeader();
        header.setMsgId(Util.genMessageId());
        header.setCategory(MessageObj.CATEGORY_CALL);
        header.setSubType(MessageObj.SUBTYPE_MISSED_CALL);
        header.setAction(MessageObj.ACTION_ADD);

        // Get message body content
        String phoneNum = "";
        String sender = "";
        String content = "";
        int timestamp = Util.getUtcTime(Calendar.getInstance().getTimeInMillis());

        // Fill message body
        CallMessageBody body = new CallMessageBody();
        body.setSender(sender);
        body.setNumber(phoneNum);
        body.setContent(content);
        body.setMissedCallCount(0);
        body.setTimestamp(timestamp);

        Log.i(LOG_TAG, "sendReadMissedCallData() sender:phoneNum:content" + sender + phoneNum + content);

        // Create call message
        MessageObj callMessageData = new MessageObj();
        callMessageData.setDataHeader(header);
        callMessageData.setDataBody(body);

        sendCallMessage(callMessageData);
    }

    /**
     * Send call message to remote device.
     *
     * @param callMessage call message data
     */
    public void sendCallMessage(MessageObj callMessage) {
        Log.i(LOG_TAG, "sendSmsMessage(),  smsMessageId=" + callMessage.getDataHeader().getMsgId());
        sendData(callMessage);
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy()");

        mIsMainServiceActive = false;
        unregisterReceiver(mSystemNotificationService);
        mSystemNotificationService = null;

        getContentResolver().unregisterContentObserver(mCallLogObserver);
        // Remove spp connection
        stopRemoteCameraService();
        stopMapService();
        stopSmsService();
        destoryBluetoothManager();

        // Print log end
        LogUtil.getInstance(mContext).stop();
    }
}
