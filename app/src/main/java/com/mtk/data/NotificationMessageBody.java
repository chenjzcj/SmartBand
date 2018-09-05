package com.mtk.data;

import android.graphics.Bitmap;
import android.util.Base64;

import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 通知消息体封装实体
 */
public class NotificationMessageBody extends MessageBody {
    private String mIcon = null;
    private String mTitle = null;
    private String mTickerText = null;
    private String mAppId = null;

    String getIcon() {
        return this.mIcon;
    }

    public void setIcon(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        this.mIcon = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTickerText() {
        return mTickerText;
    }

    public void setTickerText(String tickerText) {
        this.mTickerText = tickerText;
    }

    public String getAppID() {
        return mAppId;
    }

    public void setAppID(String appID) {
        this.mAppId = appID;
    }

    @Override
    public void genXmlBuff(XmlSerializer serializer) throws IllegalArgumentException,
            IllegalStateException, IOException {
        serializer.startTag(null, MessageObj.BODY);
        // event_type
        if (this.getSender() != null) {
            serializer.startTag(null, MessageObj.SENDER);
            // serializer.text(new String(this.getSender().getBytes(), NcenterDataObj.CHARSET));
            serializer.text(this.getSender());
            serializer.endTag(null, MessageObj.SENDER);
        }

        if (this.getAppID() != null) {
            serializer.startTag(null, MessageObj.APPID);
            // serializer.text(new String(this.getSender().getBytes(), NcenterDataObj.CHARSET));
            serializer.text(this.getAppID());
            serializer.endTag(null, MessageObj.APPID);
        }

        // mime_type
        if (this.getIcon() != null) {
            serializer.startTag(null, MessageObj.ICON);
            serializer.cdsect(this.getIcon());
            // serializer.text(this.getIcon());
            serializer.endTag(null, MessageObj.ICON);
        }
        // title
        if (this.getTitle() != null) {
            serializer.startTag(null, MessageObj.TITLE);
            serializer.cdsect(this.getTitle());
            serializer.endTag(null, MessageObj.TITLE);
        }
        // content
        if (this.getContent() != null) {
            serializer.startTag(null, MessageObj.CONTENT);
            serializer.cdsect(this.getContent());
            // serializer.cdsect(new String(this.getContent().getBytes(), NcenterDataObj.CHARSET));
            // serializer.text(this.getContent());
            serializer.endTag(null, MessageObj.CONTENT);
        }
        // ticker text
        if (this.getTickerText() != null) {
            serializer.startTag(null, MessageObj.TICKER_TEXT);
            serializer.cdsect(this.getTickerText());
            serializer.endTag(null, MessageObj.TICKER_TEXT);
        }
        if (this.getTimestamp() != 0) {
            serializer.startTag(null, MessageObj.TIEMSTAMP);
            serializer.text(String.valueOf(this.getTimestamp()));
            serializer.endTag(null, MessageObj.TIEMSTAMP);
        }
        serializer.endTag(null, MessageObj.BODY);
    }

    @Override
    public String toString() {
        final String separator = ", ";

        StringBuilder str = new StringBuilder();
        str.append("[");

        if (this.getSender() != null) {
            str.append(this.getSender());
        }

        str.append(separator);
        if (this.getIcon() != null) {
            str.append(this.getIcon());
        }

        str.append(separator);
        if (this.getIcon() != null) {
            str.append(this.getAppID());
        }

        str.append(separator);
        if (this.getTitle() != null) {
            str.append(this.getTitle());
        }

        str.append(separator);
        if (this.getContent() != null) {
            str.append(this.getContent());
        }

        str.append(separator);
        if (this.getTickerText() != null) {
            str.append(this.getTickerText());
        }

        str.append(separator);
        if (this.getTimestamp() != 0) {
            str.append(String.valueOf(this.getTimestamp()));
        }

        str.append("]");
        return str.toString();
    }
}
