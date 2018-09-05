package com.mtk.map;

import java.util.ArrayList;

/**
 * 地图相关常量值
 */
public class MapConstants {

    // Command Position
    public static final int POSITION_OF_COMMAND = 0;
    public static final int POSITION_OF_STATUS = 1;
    public static final int POSITION_OF_LIST_SIZE = 2;
    public static final int POSITION_OF_MSG_ID = 2;
    public static final int POSITION_OF_MSG = 2;
    public static final int POSITION_OF_FOLDER = 3;
    public static final int POSITION_OF_SUBJECT_SIZE = 4;

    // MAPD COMMAND
    public static final String MAPD_WITH_XML = " 2 0 ";
    public static final String MAPD_WITH_VCF = " 2 1 ";
    // MAP Function
    public static final int SRV_MAPC_ADP_CMD_SET_FOLDER = 1;
    public static final int SRV_MAPC_ADP_CMD_GET_LIST_SIZE = 2;
    public static final int SRV_MAPC_ADP_CMD_GET_LISTING = 3;
    public static final int SRV_MAPC_ADP_CMD_GET_MSG = 4;
    public static final int SRV_MAPC_ADP_CMD_SET_STATUS = 5;
    public static final int SRV_MAPC_ADP_CMD_PUSH_MSG = 6;
    public static final int SRV_MAPC_ADP_EVENT_REPORT = 7;
    public static final int SRV_MAPC_ADP_CONNECT_REQUEST = 8;

    public static final String BT_MAP_BROADCAST_ACTION = "com.mtk.map.BT_MAP_COMMAND_ARRIVE";
    public static final String BT_MAP_REQUEST_ACTION = "com.mtk.map.BT_MAP_REQUEST";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String DISCONNECT = "DISCONNECT";

    public static final long SMS_GSM_HANDLE_BASE = 0x1000000000000000L;
    public static final long MESSAGE_HANDLE_MASK = 0x0FFFFFFFFFFFFFFFL;

    // SMS DataBase CL
    public static final String _ID = "_id";
    public static final String SUBJECT = "subject";
    public static final String DATE = "date";
    public static final String ADDRESS = "address";
    public static final String STATUS = "status";
    public static final String READ = "read";
    public static final String PERSON = "person";
    public static final String BODY = "body";
    public static final String THREAD_ID = "thread_id";
    public static final String TYPE = "type";
    public static final String MESSAGE_SIZE = "m_size";
    public static final String REPLY_PATH_PRESENT = "reply_path_present";
    public static final String SERVICE_CENTER = "service_center";
    public static final String SEEN = "seen";
    public static final String PROTOCOL = "protocol";
    public static final String ERROR_CODE = "error_code";

    public static final int MESSAGE_TYPE_ALL = 0;
    public static final int MESSAGE_TYPE_DRAFT = 3;
    public static final int MESSAGE_TYPE_FAILED = 5;
    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_OUTBOX = 4;
    public static final int MESSAGE_TYPE_QUEUED = 6;
    public static final int MESSAGE_TYPE_SENT = 2;

    public static final String SMS_CONTENT_URI = "content://sms/";
    public static final String INBOX = "content://sms/inbox";
    public static final String OUTBOX = "content://sms/outbox";
    public static final String SENT = "content://sms/sent";
    public static final String DRAFT = "content://sms/draft";
    public static final String DEFAULT_SORT_ORDER = "date DESC";
    public static final String FAILED = "content://sms/failed";
    public static final String CONVERSATION = "content://mms-sms/conversataions";

    //read status
    public static final int UNREAD_STATUS = 0;
    public static final int READ_STATUS = 1;
    public static final int DELETE_STATUS = 2;
    public static final int STATUS_PENDING = 64;

    //recepient status
    public static final int RECEPIENT_STATUS_COMPLETE = 0;
    public static final int RECEPIENT_STATUS_FRACTIONED = 1;
    public static final int RECEPIENT_STATUS_NOTIFICATION = 2;

    //messageitemfield
    class MessageItemField {
        public static final int MsgHandle = 0;
        public static final int Subject = 1;     // [M] Title, the first words of the message, or "" (This length shall be used according to the requested value in GetMessagesListing)
        public static final int DateTime = 2;            // [M] The sending time or the reception time in format "YYYYMMDDTHHMMSS"
        public static final int SenderName = 3;  // [C]
        public static final int SenderAddr = 4;  // [C] The senders email address or phone number
        //private String ReplyToAddr; // [C] This shall be used only for emails to deliver the sender's reply-to email address.
        public static final int RecipientName = 5;   // [C] The recipient's email address, a list of email addresses, or phone number
        public static final int RecipientAddr = 6;   // [M] If the recipient is not known this may be left empty.
        public static final int MsgType = 7;        // [M]
        public static final int OrignalMsgSize = 8; // [M] [MAP_CHECK] The overall size in bytes of the original message as received from network (using UINT16 in BRCM)
        public static final int bText = 9;          // (default 'no') (The message includes textual content or not)
        public static final int RecipientStatus = 10;    // [M]
        public static final int AttachSize = 11;     // [M] [MAP_CHECK] (using UINT16 in BRCM)
        public static final int bPriority = 12;      // (default 'no') The message is of high priority or not.
        public static final int read = 13;           // (default 'no') The message has already been read on the MSE or not.
        public static final int bSent = 14;           // (default 'no') The message has already been sent to the recipient or not.
        public static final int bProtected = 15;
    }

    public static final int MAX_SUBJECT_LEN = 254;
    @SuppressWarnings("serial")
    public static final ArrayList<String> messageItemField = new ArrayList<String>() {
        {
            add("handle");
            add("subject");
            add("datetime");
            add("sender_name");
            add("sender_addressing");
            add("recipient_name");
            add("recipient_addressing");
            add("type");
            add("size");
            add("text");
            add("reception_status");
            add("attachment_size");
            add("priority");
            add("read");
            add("sent");
            add("protected");
        }
    };

    //event report result   
    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = -1;
    public static final int EVENT_NEW = 1;
    public static final int EVENT_DELETE = 2;
    public static final int EVENT_SHIFT = 3;
    public static final String EVENT_NEW_S = "NewMessage";
    public static final String EVENT_DELETE_S = "MessageDeleted";
    public static final String EVENT_SHIFT_S = "MessageShift";

    class Mailbox {
        public static final String TELECOM = "telecom";
        public static final String MSG = "msg";
        public static final String INBOX = "inbox";
        public static final String OUTBOX = "outbox";
        public static final String SENT = "sent";
        public static final String DELETED = "deleted";
        public static final String DRAFT = "draft";
        public static final String FAILED = "failed";
    }

    //Message Report Tag
    public static final int MSG_TYPE_SMS_GSM = 0x01;
    public static final String MESSAGE_TYPE_SMS_GSM = "SMS_GSM";
}

