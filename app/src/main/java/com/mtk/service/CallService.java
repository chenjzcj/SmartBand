package com.mtk.service;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.mtk.data.CallMessageBody;
import com.mtk.data.Log;
import com.mtk.data.MessageHeader;
import com.mtk.data.MessageObj;
import com.mtk.data.PreferenceData;
import com.mtk.data.Util;
import com.ruanan.btnotification.R;

/**
 * This class will receive and process phone information, when phone state changes.
 */
public class CallService extends PhoneStateListener {
    // Debugging
    private static final String LOG_TAG = "CallService";
    // the last phone state
    private int mLastState = TelephonyManager.CALL_STATE_IDLE;
    private String mIncomingNumber = null;

    private Context mContext = null;

    public CallService(Context context) {
        Log.i(LOG_TAG, "CallService(), CallService created!");
        mContext = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Log.i(LOG_TAG, "onCallStateChanged(), incomingNumber" + incomingNumber);

        // If current state is idle and last state is ringing, then it is a miss call. 
        if ((mLastState == TelephonyManager.CALL_STATE_RINGING)
                && (state == TelephonyManager.CALL_STATE_IDLE)) {
            mIncomingNumber = incomingNumber;

            boolean callServiceEnable = PreferenceData.isCallServiceEnable();
            boolean needForward = PreferenceData.isNeedPush();
            if (callServiceEnable && needForward) {
                // Process miss call
                sendCallMessage();
            }
        }
        // Save phone state
        mLastState = state;
    }

    /**
     * 发送电话信息到远程设备
     */
    private void sendCallMessage() {
        // Create message data object
        MessageObj callMessageData = new MessageObj();
        callMessageData.setDataHeader(createCallHeader());
        callMessageData.setDataBody(createCallBody());

        // Call main service to send data
        Log.i(LOG_TAG, "sendCallMessage(), callMessageData=" + callMessageData);
        MainService service = MainService.getInstance();
        if (service != null) {
            service.sendCallMessage(callMessageData);
        }
    }

    /**
     * 创建电话消息头
     *
     * @return MessageHeader
     */
    private MessageHeader createCallHeader() {
        // Fill message header
        MessageHeader header = new MessageHeader();
        header.setMsgId(Util.genMessageId());
        header.setCategory(MessageObj.CATEGORY_CALL);
        header.setSubType(MessageObj.SUBTYPE_MISSED_CALL);
        header.setAction(MessageObj.ACTION_ADD);

        Log.i(LOG_TAG, "createCallHeader(), header=" + header);
        return header;
    }

    /**
     * 创建电话消息体
     *
     * @return CallMessageBody
     */
    private CallMessageBody createCallBody() {
        // Get message body content
        String phoneNum = mIncomingNumber;
        String sender = Util.getContactName(mContext, phoneNum);
        String content = getMessageContent(sender);
        // Add this missed call
        int missedCallCount = getMissedCallCount();
        int timestamp = Util.getUtcTime(System.currentTimeMillis());

        // Fill message body
        CallMessageBody body = new CallMessageBody();
        body.setNumber(phoneNum);
        body.setSender(sender);
        body.setContent(content);
        body.setMissedCallCount(missedCallCount);
        body.setTimestamp(timestamp);

        Log.i(LOG_TAG, "createCallBody(), body=" + body);
        return body;
    }

    /**
     * 通过发送者获取消息内容
     *
     * @param sender 发送人
     * @return 消息内容
     */
    private String getMessageContent(String sender) {
        StringBuilder content = new StringBuilder();
        content.append(mContext.getText(R.string.missed_call));
        content.append(": ");
        content.append(sender);

        // TODO: Only for test
        content.append("\r\n");
        content.append("Missed Call Count:");
        content.append(getMissedCallCount());

        Log.i(LOG_TAG, "getMessageContent(), content=" + content);
        return content.toString();
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
        Log.i(LOG_TAG, "getMissedCallCount(), query string=" + queryStr);

        // start the query
        int missedCallCount = 0;
        Cursor cur = mContext.getContentResolver().query(Calls.CONTENT_URI, new String[]{Calls._ID},
                queryStr.toString(), null, Calls.DEFAULT_SORT_ORDER);
        if (cur != null) {
            missedCallCount = cur.getCount();
            cur.close();
        }
        Log.i(LOG_TAG, "getMissedCallCount(), missed call count=" + missedCallCount);
        return missedCallCount;
    }
}
