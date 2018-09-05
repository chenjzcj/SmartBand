package com.mtk.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Xml;

import com.mtk.MyApp;
import com.mtk.data.Log;
import com.mtk.data.MessageObj;
import com.mtk.service.MainService;

import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * This class will receive and process all new SMS.
 */
public class BTMapService extends BroadcastReceiver {
    private static final String TELECOM_MSG_INBOX = "telecom/msg/inbox";
    private static final String TAG = "BTMapService";
    private static final Context mContext = MyApp.getInstance().getApplicationContext();
    private static final SmsController mSmsController = new SmsController(mContext);
    private String mMapCommand = null;
    private String mMapDisconnect = null;
    private String mFolder = TELECOM_MSG_INBOX;
    public static final ArrayList<Long> mKeys = new ArrayList<Long>();

    // Received parameters
    public BTMapService() {
        Log.i(TAG, "BTMapReceiver(), BTMapReceiver created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (MapConstants.BT_MAP_BROADCAST_ACTION.equals(action)) {
            if (intent.hasExtra(MapConstants.DISCONNECT)) {
                mMapDisconnect = intent.getStringExtra(MapConstants.DISCONNECT);
                if (mMapDisconnect.equals(MapConstants.DISCONNECT)) {
                    mSmsController.onStop();
                    return;
                }
            }
            try {
                mMapCommand = new String(intent.getByteArrayExtra(MapConstants.EXTRA_DATA), MessageObj.CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            MainService service = MainService.getInstance();
            String[] commands = mMapCommand.split(" ");
            try {
                Log.i(TAG, "BTMapService onReceive(), commands :" + mMapCommand);
            } catch (Exception e) {
                e.getStackTrace();
            }
            switch (Integer.valueOf(commands[MapConstants.POSITION_OF_COMMAND])) {
                case MapConstants.SRV_MAPC_ADP_CMD_SET_FOLDER:
                    handleSetFolder(service, commands);
                    break;
                case MapConstants.SRV_MAPC_ADP_CMD_GET_LISTING:
                    handleGetList(service, commands);
                    break;
                case MapConstants.SRV_MAPC_ADP_CMD_GET_MSG:
                    handleGetMsg(service, commands);
                    break;
                case MapConstants.SRV_MAPC_ADP_CMD_PUSH_MSG:
                    handlePushMsg(service, mMapCommand);
                    break;
                case MapConstants.SRV_MAPC_ADP_CMD_SET_STATUS:
                    handleSetStatus(service, commands);
                    break;
                default:
                    break;

            }

        }
    }

    /**
     * @param commands
     */
    private void handleSetStatus(MainService service, String[] commands) {
        if (commands.length < MapConstants.POSITION_OF_MSG_ID + 1) {
            return;
        }
        int status = Integer.valueOf(commands[MapConstants.POSITION_OF_STATUS]);
        long id = Long.valueOf(commands[MapConstants.POSITION_OF_MSG_ID]) & MapConstants.MESSAGE_HANDLE_MASK;
        if (status == MapConstants.READ_STATUS || status == MapConstants.UNREAD_STATUS) {
            mSmsController.setMessageStatus(id, status);
        } else {
            if (mKeys.contains(id)) {
                Log.i(TAG, "BTMapReceiver(), The message has been deleted!");
                service.sendMapResult(String.valueOf(MapConstants.SRV_MAPC_ADP_CMD_SET_STATUS));

                return;
            }
            mSmsController.deleteMessage(id);
        }
    }

    /**
     * @param service
     * @param commands
     */
    private void handlePushMsg(MainService service, String commands) {
        String messageVcard = commands;
        String telephone = parse(messageVcard);
        String startIndex = "BEGIN:MSG\r\n";
        String endIndex = "\r\nEND:MSG";
        int startOfMsg = messageVcard.indexOf(startIndex) + startIndex.length();
        int endOfMsg = messageVcard.indexOf(endIndex);
        Log.i(TAG, "send msg result success");
        if (startOfMsg > endOfMsg) {
            service.sendMapResult(String.valueOf(-MapConstants.SRV_MAPC_ADP_CMD_PUSH_MSG));
            return;
        }
        String text = messageVcard.substring(startOfMsg, endOfMsg);
        if (text.equals("")) {
            text = "\n";
        }
        Log.i(TAG, "send msg result success");
        service.sendMapResult(String.valueOf(MapConstants.SRV_MAPC_ADP_CMD_PUSH_MSG));
        mSmsController.pushMessage(telephone, text);
    }

    /**
     * @param service
     * @param commands
     */
    private void handleGetMsg(MainService service, String[] commands) {
        BMessage mBMessageObject = mSmsController.getMessage(Long.valueOf(commands[MapConstants.POSITION_OF_MSG]));
        if (mBMessageObject == null) {
            service.sendMapResult(String.valueOf(-MapConstants.SRV_MAPC_ADP_CMD_GET_MSG));
        } else {
            try {
                byte[] dataofMsg = mBMessageObject.toString().getBytes(MessageObj.CHARSET);
                String cmdOfMsg = String.valueOf(MapConstants.SRV_MAPC_ADP_CMD_GET_MSG) + MapConstants.MAPD_WITH_VCF + String.valueOf(dataofMsg.length) + " ";
                service.sendMapDResult(cmdOfMsg);
                service.sendMapData(dataofMsg);
            } catch (UnsupportedEncodingException e) {
            }
        }

    }

    /**
     * @param service
     * @param commands
     */
    private void handleGetList(MainService service, String[] commands) {

        int listSize = Integer.valueOf(commands[MapConstants.POSITION_OF_LIST_SIZE]);
        int maxSubjectLen = Integer.valueOf(commands[MapConstants.POSITION_OF_SUBJECT_SIZE]);
        MessageList mMsgListRspCache;
        if (mFolder.equals("outbox")) {
            mMsgListRspCache = mSmsController.getMessageList(listSize, maxSubjectLen, "failed");
        } else {
            mMsgListRspCache = mSmsController.getMessageList(listSize, maxSubjectLen, mFolder);
        }
        byte[] dataofList = genXmlBufferOfMsgList(mMsgListRspCache);
        String cmdOfList = String.valueOf(MapConstants.SRV_MAPC_ADP_CMD_GET_LISTING) + MapConstants.MAPD_WITH_XML + String.valueOf(dataofList.length) + " ";
        service.sendMapDResult(cmdOfList);
        service.sendMapData(dataofList);
    }

    /**
     * @param service
     * @param commands
     */
    private void handleSetFolder(MainService service, String[] commands) {
        mFolder = commands[MapConstants.POSITION_OF_FOLDER];
        SmsController.mAddress = null;
        SmsController.mPerson = null;
        mKeys.clear();
        Log.i(TAG, "Set Folder the folder is :" + commands[MapConstants.POSITION_OF_FOLDER]);
        service.sendMapResult(String.valueOf(MapConstants.SRV_MAPC_ADP_CMD_SET_FOLDER));
    }

    private byte[] genXmlBufferOfMsgList(MessageList mMsgListRspCache) {
        XmlSerializer serializer = Xml.newSerializer();
        try {
            StringWriter stringWriter = new StringWriter();
            serializer.setOutput(stringWriter);
            //serializer.setOutput(fileos,MessageObj.CHARSET); 
            serializer.startDocument(MessageObj.CHARSET, false);
            serializer.startTag(null, "MAP-msg-listing");
            serializer.attribute(null, "version", "1.0");
            MessageListItem[] mMessageItems = mMsgListRspCache.generateMessageItemArray();
            for (MessageListItem mMessageItem : mMessageItems) {
                serializer.startTag(null, "msg");
                ArrayList<String> messageItemFields = mMessageItem.getMessageItem();
                for (int i = 0; i < messageItemFields.size(); i++) {
                    String value = messageItemFields.get(i);
                    if (value == null) {
                        value = "";
                    }
                    serializer.attribute(null, MapConstants.messageItemField.get(i), value);
                }
                serializer.endTag(null, "msg");
            }
            serializer.endTag(null, "MAP-msg-listing");
            serializer.endDocument();
            serializer.flush();
            String str = stringWriter.toString();
            return str.getBytes(MessageObj.CHARSET);
        } catch (Exception e) {
            Log.e("Exception", "error occurred while creating xml file");
        }
        return null;
    }

    private String parse(String vcard) {

        String[] elements = vcard.split(BMessage.CRLF);
        for (String element : elements) {
            String[] item = element.split(BMessage.SEPRATOR);
            if (item.length < 2) {
                continue;
            }
            String key = item[0].trim();
            String value = item[1].trim();
            if (key.equals(VCard.TELEPHONE)) {
                return value;
            }
        }
        return null;
    }
}