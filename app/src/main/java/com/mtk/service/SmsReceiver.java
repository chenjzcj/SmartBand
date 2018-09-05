package com.mtk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.mtk.data.Log;
import com.mtk.data.MessageHeader;
import com.mtk.data.MessageObj;
import com.mtk.data.PreferenceData;
import com.mtk.data.SmsMessageBody;
import com.mtk.data.Util;

/**
 * This class will receive and process all new SMS.
 */
public class SmsReceiver extends BroadcastReceiver {
    // Debugging
    private static final String LOG_TAG = "SmsReceiver";
    public static final String SMS_RECEIVED = "com.mtk.btnotification.SMS_RECEIVED";
    public static final String SMS_ACTION = "SenderSMSFromeFP";
    private static String preID = null;
    private Context mContext = null;

    public SmsReceiver() {
        Log.i(LOG_TAG, "SmsReceiver(), SmsReceiver created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "onReceive()");
        boolean isServiceEnabled = PreferenceData.isSmsServiceEnable();
        boolean needForward = PreferenceData.isNeedPush();
        if (isServiceEnabled && needForward) {
            mContext = context;
            if (intent.getAction().equals(SMS_RECEIVED)) {
                sendSms();
            }
            //sendSmsMessage();
        }
    }

    /**
     * 将短信推送到远程设备
     */
    private void sendSms() {
        String msgbody;
        String address;
        String ID;
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(Uri.parse("content://sms/inbox"),
                    null, null, null, "_id desc");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    msgbody = cursor.getString(cursor.getColumnIndex("body"));
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    ID = cursor.getString(cursor.getColumnIndex("_id"));
                    if (ID.equals(preID)) {
                        break;
                    } else {
                        preID = ID;
                        if ((msgbody != null) && (address != null)) {
                            sendSmsMessage(msgbody, address);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 将手机短信推送到远程设备
     *
     * @param msgbody 短信内容
     * @param address 短信发送人
     */
    private void sendSmsMessage(String msgbody, String address) {
        // Create message data object
        MessageObj smsMessageData = new MessageObj();
        smsMessageData.setDataHeader(createSmsHeader());
        smsMessageData.setDataBody(createSmsBody(msgbody, address));

        Log.i(LOG_TAG, "sendSmsMessage(), smsMessageData=" + smsMessageData);
        // Call main service to send data
        MainService service = MainService.getInstance();
        if (service != null) {
            service.sendSmsMessage(smsMessageData);
        }
    }

    /**
     * 创建短信通知消息头
     *
     * @return MessageHeader
     */
    private MessageHeader createSmsHeader() {
        // Fill message header
        MessageHeader header = new MessageHeader();
        header.setMsgId(Util.genMessageId());
        header.setCategory(MessageObj.CATEGORY_NOTI);
        header.setSubType(MessageObj.SUBTYPE_SMS);
        header.setAction(MessageObj.ACTION_ADD);
        Log.i(LOG_TAG, "createSmsHeader(), header=" + header);
        return header;
    }

    /**
     * 创建短信通知消息体
     *
     * @param msgbody 短信内容
     * @param address 短信发送人
     * @return SmsMessageBody
     */
    private SmsMessageBody createSmsBody(String msgbody, String address) {
        // Get message body content
        String sender = Util.getContactName(mContext, address);
        int timestamp = Util.getUtcTime(System.currentTimeMillis());

        // Fill message body
        SmsMessageBody body = new SmsMessageBody();
        body.setSender(sender);
        body.setNumber(address);
        body.setContent(msgbody);
        body.setTimestamp(timestamp);

        Log.i(LOG_TAG, "createSmsBody(), body=" + body);
        return body;
    }
}