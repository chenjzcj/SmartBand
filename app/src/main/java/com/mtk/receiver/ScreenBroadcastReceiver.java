package com.mtk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.mtk.band.SportActivity;
import com.mtk.band.utils.Tools;
import com.mtk.util.AppUtils;
import com.mtk.util.LogUtils;

/**
 * Created by MZIA(527633405@qq.com) on 2016/10/24 0024 13:45
 * http://blog.csdn.net/w250shini11/article/details/16983177
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("onReceive");
        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            LogUtils.i("screen on");
            if (AppUtils.isForeground(context, SportActivity.class.getName())) {
                Tools.stepStart();
            }
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            LogUtils.i("screen off");
            Tools.stepPause();
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
            LogUtils.i("screen unlock");
            if (AppUtils.isForeground(context, SportActivity.class.getName())) {
                Tools.stepStart();
            }
        } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
            LogUtils.i("press homekey");
            Tools.stepPause();
        } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
            LogUtils.i(" receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
        }
    }

    public static IntentFilter genIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 监听home键
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        return filter;
    }
}
