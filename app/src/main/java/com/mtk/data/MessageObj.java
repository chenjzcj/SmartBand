package com.mtk.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * 对消息进行封装的实体类,包含消息头,消息体
 */
public class MessageObj {
    private MessageHeader mDataHeader;
    private MessageBody mDataBody;

    private static final String LOG_TAG = "NcenterDataObj";

    private static final String XML_HEADER = "event_report";
    public static final String HEADER = "header";
    public static final String CATEGORY = "category";
    public static final String SUBTYPE = "subType";
    public static final String MSGID = "msgId";
    public static final String ACTION = "action";

    public static final String BODY = "body";
    public static final String SENDER = "sender";
    public static final String APPID = "appId";
    public static final String TIEMSTAMP = "timestamp";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TICKER_TEXT = "ticker_text";
    public static final String ICON = "icon";
    public static final String NUMBER = "number";
    public static final String MISSED_CALL_COUNT = "missed_call_count";

    public static final String CATEGORY_NOTI = "notification";
    public static final String CATEGORY_CALL = "call";

    public static final String SUBTYPE_NOTI = "text";
    public static final String SUBTYPE_BLOCK = "block_sender";
    public static final String SUBTYPE_SMS = "sms";
    public static final String SUBTYPE_MISSED_CALL = "missed_call";

    public static final String ACTION_ADD = "add";
    public static final String ACTION_DEL = "delete";
    public static final String ACTION_DELALL = "deleteAll";
    public static final String ACTION_UPDATE = "update";

    public static final String CHARSET = "UTF-8";

    public MessageHeader getDataHeader() {
        return mDataHeader;
    }

    public void setDataHeader(MessageHeader dataHeader) {
        this.mDataHeader = dataHeader;
    }

    public MessageBody getDataBody() {
        return mDataBody;
    }

    public void setDataBody(MessageBody dataBody) {
        this.mDataBody = dataBody;
    }

    public byte[] genXmlBuff() throws IllegalArgumentException, IllegalStateException,
            IOException, XmlPullParserException, NoDataException {
        StringWriter stringWriter = new StringWriter();
        MessageHeader header = this.getDataHeader();
        MessageBody body = this.getDataBody();
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(stringWriter);
        serializer.startDocument(CHARSET, true);
        serializer.startTag(null, XML_HEADER);
        if (header != null) {
            header.genXmlBuff(serializer);
        }
        if (body != null) {
            body.genXmlBuff(serializer);
        }
        if (header == null || body == null) {
            Log.i(LOG_TAG, "genXmlBuff() header or body is null");
        }
        serializer.endTag(null, XML_HEADER);
        serializer.endDocument();

        String str = stringWriter.toString();
        //remove str to reduce log lenth.
        Log.i(LOG_TAG, "genXmlBuff()");
        return str.getBytes(CHARSET);
    }

    public MessageObj parseXml(byte[] bytes) throws XmlPullParserException, IOException {
        String str = new String(bytes);
        Charset cs = Charset.forName(CHARSET);
        cs.encode(str);
        Log.i(LOG_TAG, "parseXml()");
        StringReader stringReader = new StringReader(str);
        MessageObj currObj = new MessageObj();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(stringReader);

        int eventType = parser.getEventType();
        MessageHeader header = null;
        while (XmlPullParser.END_DOCUMENT != eventType) {
            String nodeName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (nodeName.equals(HEADER)) {
                        header = new MessageHeader();
                        currObj.setDataHeader(header);
                    } else if (header != null) {
                        parseHeader(parser, nodeName, currObj);
                    } else {
                        Log.i(LOG_TAG, "parseXml()");
                        break;
                    }
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return currObj;
    }

    private void parseHeader(XmlPullParser parser, String nodeName, MessageObj currObj) throws XmlPullParserException,
            IOException {
        MessageHeader header = currObj.getDataHeader();
        MessageBody body = currObj.getDataBody();
        if (nodeName.equals(CATEGORY)) {
            header.setCategory(parser.nextText());
        } else if (nodeName.equals(SUBTYPE)) {
            header.setSubType(parser.nextText());
        } else if (nodeName.equals(MSGID)) {
            header.setMsgId(Integer.parseInt(parser.nextText()));
        } else if (nodeName.equals(ACTION)) {
            header.setAction(parser.nextText());
        } else {
            if (nodeName.equals(BODY)) {
                if (header.getSubType().equals(SUBTYPE_NOTI)) {
                    body = new NotificationMessageBody();
                } else if (header.getSubType().equals(SUBTYPE_SMS)) {
                    body = new SmsMessageBody();
                } else if (header.getSubType().equals(SUBTYPE_BLOCK)) {
                    body = new NotificationMessageBody();
                } else if (header.getSubType().equals(SUBTYPE_MISSED_CALL)) {
                    body = new CallMessageBody();
                }
                currObj.setDataBody(body);
            } else if (body != null) {
                parseBody(parser, nodeName, currObj);
            } else {
                Log.i(LOG_TAG, "parseHeader()");
                body = null;
            }
        }

    }

    private void parseBody(XmlPullParser parser, String nodeName, MessageObj currObj) throws XmlPullParserException,
            IOException {
        MessageBody body = currObj.getDataBody();
        if (nodeName.equals(CONTENT)) {
            body.setContent(parser.nextText());
        } else if (nodeName.equals(TIEMSTAMP)) {
            body.setTimestamp(Integer.parseInt(parser.nextText()));
        } else if (nodeName.equals(SENDER)) {
            body.setSender(parser.nextText());
        } else if (nodeName.equals(APPID)) {
            NotificationMessageBody notiBody = (NotificationMessageBody) body;
            notiBody.setAppID(parser.nextText());
        } else if (nodeName.equals(ICON)) {
            NotificationMessageBody notiBody = (NotificationMessageBody) body;
            byte[] bytes = Base64.decode(parser.nextText(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            notiBody.setIcon(bitmap);
        } else if (nodeName.equals(NUMBER)) {
            SmsMessageBody smsBody = (SmsMessageBody) body;
            smsBody.setNumber(parser.nextText());
        } else {
            Log.i(LOG_TAG, "parseBody()");
        }
    }
}
