package com.mtk.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import com.ruanan.btnotification.R;

/**
 * 铃声服务(当手表点击找手机的时候调用)
 */
public class RingService extends Service {
    private static final String TAG = "RingService";
    private static MediaPlayer mMediaPlayer;
    private Dialog dialog;

    @Override
    public IBinder onBind(Intent paramIntent) {
        Log.i(TAG, "onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        initMediaPlayer();
        initAlertDialog();
        dialog.show();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化媒体播放器
     */
    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        //此方法为获取当前的来电铃声音频(如果为多卡,则为第一张卡的来电铃声)
        Uri localUri = RingtoneManager.getDefaultUri(1);
        try {
            mMediaPlayer.setDataSource(this, localUri);
            if (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(2) != 0) {
                mMediaPlayer.setAudioStreamType(2);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer.setLooping(true);
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 初始化提示铃声
     */
    private void initAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("取消铃声");
        builder.setPositiveButton(R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mMediaPlayer.isPlaying()) {
                    stopSelf();
                }
            }
        });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
    }

    /**
     * 停止铃声播放
     */
    private void stopMediaPlayer() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        stopMediaPlayer();
        dialog.dismiss();
        super.onDestroy();
    }
}
