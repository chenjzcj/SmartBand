package com.mtk.remotecamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mtk.data.Log;
import com.mtk.data.MessageObj;
import com.mtk.data.Util;
import com.mtk.map.MapConstants;
import com.mtk.service.MainService;
import com.mtk.service.RingService;
import com.mtk.util.LogUtils;
import com.mtk.util.SystemUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 远程相机调用接收器
 */
public class RemoteCameraService extends BroadcastReceiver {

    public static final String BT_REMOTECAMERA_BROADCAST_ACTION = "com.mtk.RemoteCamera";

    public static final String BT_REMOTEOPENRING_BROADCAST_ACTION = "com.mtk.RemoteOpenRing";
    public static final String BT_REMOTECAMERA_EXIT_ACTION = "com.mtk.RemoteCamera.EXIT";
    public static final String BT_REMOTECAMERA_CAPTURE_ACTION = "com.mtk.RemoteCamera.CAPTURE";
    private static final String TAG = "REMOTECAMERAService";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static boolean isLaunched = false;
    public static boolean needPreview = false;//是否需要打开预览
    public static boolean isIntheProgressOfExit = false;
    private String mRemoteCameraCommand = null;

    public static class Commands {
        // Command Position
        public static final int POSITION_OF_COMMAND = 0;
        public static final int POSITION_OF_START_ACTIVITY = 1;//启动activity
        public static final int POSITION_OF_CAP = 2;//拍摄
        public static final int POSITION_OF_EXIT_ACTIVITY = 3;//退出activity
        public static final int POSITION_OF_PREVIEW = 4;//开始预览

        public static final int OPEN_PHONE_RING = 5;//打开找手机对话框
        public static final int CLOSE_PHONE_RING = 6;//关闭找手机对话框

        public static final String NUM_OF_START_ACTIFITY_ARGS = " 0 ";
        public static final String NUM_OF_EXIT_ACTIFITY_ARGS = " 0 ";
        public static final String NUM_OF_CAP_ACTIFITY_ARGS = " 1 ";
    }

    public RemoteCameraService() {
        Log.i(TAG, "RemoteCameraService(), RemoteCameraService created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        needPreview = false;
        String action = intent.getAction();
        MainService service = MainService.getInstance();
        if (BT_REMOTECAMERA_BROADCAST_ACTION.equals(action)) {
            try {
                mRemoteCameraCommand = new String(intent.getByteArrayExtra(EXTRA_DATA), MessageObj.CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // MainService service = MainService.getInstance();
            String[] commands = mRemoteCameraCommand.split(" ");
            Log.i(TAG, "RemoteCameraService onReceive(), commands :" + Arrays.toString(commands));
            switch (Integer.valueOf(commands[MapConstants.POSITION_OF_COMMAND])) {
                case Commands.POSITION_OF_START_ACTIVITY:
                    Log.i(TAG, "isLaunched: " + isLaunched);
                    Log.i(TAG, "isIntheProgressOfExit: " + isIntheProgressOfExit);

                    if (Util.isScreenLocked(context)) {
                        String cmdOfList = String.valueOf(-RemoteCameraService.Commands.POSITION_OF_START_ACTIVITY)
                                + RemoteCameraService.Commands.NUM_OF_START_ACTIFITY_ARGS;
                        service.sendCAPCResult(cmdOfList);
                    } else {
                        if (!Util.isScreenOn(context)) {
                            String cmdOfList = String.valueOf(-RemoteCameraService.Commands.POSITION_OF_START_ACTIVITY)
                                    + RemoteCameraService.Commands.NUM_OF_START_ACTIFITY_ARGS;
                            service.sendCAPCResult(cmdOfList);
                        } else {
                            if ((isLaunched) && (!isIntheProgressOfExit)) {
                                String cmdOfList = String.valueOf(RemoteCameraService.Commands.POSITION_OF_START_ACTIVITY)
                                        + RemoteCameraService.Commands.NUM_OF_START_ACTIFITY_ARGS;
                                service.sendCAPCResult(cmdOfList);
                            } else if (isIntheProgressOfExit) {
                                String cmdOfList = String.valueOf(-RemoteCameraService.Commands.POSITION_OF_START_ACTIVITY)
                                        + RemoteCameraService.Commands.NUM_OF_START_ACTIFITY_ARGS;
                                service.sendCAPCResult(cmdOfList);
                            } else {
                                Intent launchIntent = new Intent();
                                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                launchIntent.setClass(context, RemoteCamera.class);
                                context.startActivity(launchIntent);
                            }

                        }
                    }
                    break;
                case Commands.POSITION_OF_EXIT_ACTIVITY:
                    if (isLaunched) {
                        isIntheProgressOfExit = true;
                    }
                    Intent broadcastExitIntent = new Intent();
                    broadcastExitIntent.setAction(BT_REMOTECAMERA_EXIT_ACTION);
                    // Fill extra data, it is optional
                    context.sendBroadcast(broadcastExitIntent);
                    break;
                case Commands.POSITION_OF_CAP:
                    Intent broadcastCaptureIntent = new Intent();
                    broadcastCaptureIntent.setAction(BT_REMOTECAMERA_CAPTURE_ACTION);
                    // Fill extra data, it is optional
                    context.sendBroadcast(broadcastCaptureIntent);
                    break;
                case Commands.POSITION_OF_PREVIEW:
                    Log.i(TAG, "needPreview = true");
                    needPreview = true;
                    break;
                case Commands.OPEN_PHONE_RING:
                    String cmdOfLista = String.valueOf(5) + Commands.NUM_OF_START_ACTIFITY_ARGS;
                    service.sendCAPCResult(cmdOfLista);
                    //判断服务是否在运行
                    boolean state = SystemUtil.isServiceRunning(MainService.mContext, RingService.class.getName());
                    //在运行就停止手机铃声后再重启手机铃声，否则直接启动铃声服务
                    if (state) {
                        MainService.getInstance().stopRingService();
                        MainService.getInstance().startRingService();
                    } else {
                        Log.i(TAG, "service is stop");
                        MainService.getInstance().startRingService();
                    }

                    // MainService.getInstance().startRingService();
                    // RingService mRingReciverService = RingService.getInstance();
                    // Log.e(TAG, "pass2");
                    // if (mRingReciverService.isRingReciverServiceActive()) {
                    // Log.e(TAG, "pass3");
                    // // mRingReciverService.getInstance().onDestroy();
                    // mRingReciverService.StopMediaPlayer();
                    // } else {
                    // Log.e(TAG, "pass4");
                    // mRingReciverService.getInstance();
                    // Log.e(TAG, "pass5");
                    // }
                    break;
                // 当音乐停止时返回数值
                case Commands.CLOSE_PHONE_RING:
                    LogUtils.i("找到了手机,关闭手机铃声");
                    MainService.getInstance().stopRingService();
                    break;
            }
        }
    }
}
