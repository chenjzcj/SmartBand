package com.mtk.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.BatteryManager;

import com.mtk.data.AppList;
import com.mtk.data.BlockList;
import com.mtk.data.Log;
import com.mtk.data.MessageHeader;
import com.mtk.data.MessageObj;
import com.mtk.data.NotificationMessageBody;
import com.mtk.data.Util;
import com.ruanan.btnotification.R;

import java.util.HashSet;

/**
 * This class will receive and process all new LowBattery
 */
public class SystemNotificationService extends BroadcastReceiver {
    // Debugging
    private static final String LOG_TAG = "SystemNotificationService";

    // Received parameters
    private Context mContext = null;
    private static float mBettryCapacity = 0;
    private static float mLastBettryCapacity = 0;

    public SystemNotificationService() {
        Log.i(LOG_TAG, "SystemNotificationService(), SystemNotificationService created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "onReceive()");
        mContext = context;
        String intentAction = intent.getAction();
        if (Intent.ACTION_BATTERY_LOW.equalsIgnoreCase(intentAction)) {
            Log.i(LOG_TAG, "onReceive(), action=" + intent.getAction());
            Log.i(LOG_TAG, "mLastBettryCapacity = " + mLastBettryCapacity);
            Log.i(LOG_TAG, "mBettryCapacity = " + mBettryCapacity);
            if (mLastBettryCapacity == 0) {
                Log.i(LOG_TAG, "mLastBettryCapacity = 0");
                sendLowBatteryMessage(String.valueOf(mBettryCapacity * 100));
                mLastBettryCapacity = mBettryCapacity;
            } else {
                if (mLastBettryCapacity != mBettryCapacity) {
                    sendLowBatteryMessage(String.valueOf((int) (mBettryCapacity * 100)));
                    mLastBettryCapacity = mBettryCapacity;
                }
            }
        } else if (Intent.ACTION_BATTERY_CHANGED.equalsIgnoreCase(intentAction)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float) scale;
            Log.i(LOG_TAG, "Batery level scale and pct is " + level + scale);
            Log.i(LOG_TAG, "BettryCapacity = " + batteryPct);
            mBettryCapacity = batteryPct;
        } else if (Intent.ACTION_POWER_CONNECTED.equalsIgnoreCase(intentAction)) {
            mLastBettryCapacity = 0;
        } else if (SmsReceiver.SMS_ACTION.equals(intentAction)) {
            HashSet<String> blockList = BlockList.getInstance().getBlockList();
            if (blockList.contains(AppList.SMSRESULT_APPID)) {
            } else {
                int resultCode = getResultCode();
                if (resultCode == Activity.RESULT_OK) {
                    sendSMSSuccessMessage();
                } else {
                    sendSMSFailMessage();
                }
            }
        }
    }

    /**
     * 发送低电量消息
     *
     * @param value String
     */
    private void sendLowBatteryMessage(String value) {
        // Get all messages
        Log.i(LOG_TAG, "sendLowBatteryMessage()");
        String titile = mContext.getResources().getString(R.string.batterylow);
        String content = mContext.getResources().getString(R.string.pleaseconnectcharger);
        content += ":" + value + "%";
        MessageObj smsMessageData = new MessageObj();
        smsMessageData.setDataHeader(createNotificationHeader());
        smsMessageData.setDataBody(createNotificationBody(titile, content));

        // Call main service to send data
        Log.i(LOG_TAG, "sendSmsMessage(), smsMessageData=" + smsMessageData);

        HashSet<String> blockList = BlockList.getInstance().getBlockList();

        MainService service = MainService.getInstance();
        if (service != null && !blockList.contains(AppList.BETTRYLOW_APPID)) {
            service.sendSystemNotiMessage(smsMessageData);
        }
    }

    /**
     * 发送短信成功提示
     */
    private void sendSMSSuccessMessage() {
        String titile = mContext.getResources().getString(R.string.sms_send);
        String content = mContext.getResources().getString(R.string.sms_send_success);
        Log.i(LOG_TAG, "sendSMSSuccessMessage()" + titile + content);

        MessageObj sendSMSSuccessMessageData = new MessageObj();
        sendSMSSuccessMessageData.setDataHeader(createNotificationHeader());
        sendSMSSuccessMessageData.setDataBody(createNotificationBody(titile, content));

        MainService service = MainService.getInstance();
        if (service != null) {
            service.sendSystemNotiMessage(sendSMSSuccessMessageData);
        }
    }

    /**
     * 发送短信失败提示
     */
    private void sendSMSFailMessage() {
        String titile = mContext.getResources().getString(R.string.sms_send);
        String content = mContext.getResources().getString(R.string.sms_send_fail);
        Log.i(LOG_TAG, "sendSMSFailMessage()" + titile + content);

        MessageObj sendSMSSuccessMessageData = new MessageObj();
        sendSMSSuccessMessageData.setDataHeader(createNotificationHeader());
        sendSMSSuccessMessageData.setDataBody(createNotificationBody(titile, content));

        MainService service = MainService.getInstance();
        if (service != null) {
            service.sendSystemNotiMessage(sendSMSSuccessMessageData);
        }
    }

    /**
     * 创建消息头实体
     *
     * @return MessageHeader
     */
    private MessageHeader createNotificationHeader() {
        // Fill message header
        MessageHeader header = new MessageHeader();
        header.setMsgId(Util.genMessageId());
        header.setCategory(MessageObj.CATEGORY_NOTI);
        header.setSubType(MessageObj.SUBTYPE_NOTI);
        header.setAction(MessageObj.ACTION_ADD);
        Log.i(LOG_TAG, "createNotificationHeader(), header=" + header);
        return header;
    }

    /**
     * 创建通知消息体实体
     *
     * @param title   消息标题
     * @param content 消息内容
     * @return NotificationMessageBody
     */
    private NotificationMessageBody createNotificationBody(String title, String content) {
        // Get message body content
        ApplicationInfo appinfo = mContext.getApplicationInfo();
        String appName = Util.getAppName(mContext, appinfo);
        Bitmap sendIcon = Util.getMessageIcon(mContext, appinfo);
        int timestamp = Util.getUtcTime(System.currentTimeMillis());
        String tickerText = "";
        NotificationMessageBody body = new NotificationMessageBody();

        if (title.equals(mContext.getResources().getString(R.string.batterylow))) {
            String appID = Util.getKeyFromValue(AppList.BETTRYLOW_APPID);
            body.setAppID(appID);
        } else if (title.equals(mContext.getResources().getString(R.string.sms_send))) {
            String appID = Util.getKeyFromValue(AppList.SMSRESULT_APPID);
            body.setAppID(appID);
        }
        // Fill message body

        body.setSender(appName);
        body.setTitle(title);
        body.setContent(content);
        body.setTickerText(tickerText);
        body.setTimestamp(timestamp);
        body.setIcon(sendIcon);

        Log.i(LOG_TAG, "createLowBatteryBody(), body=" + body.toString().substring(0, 20));
        return body;
    }
}