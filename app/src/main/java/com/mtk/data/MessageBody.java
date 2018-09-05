package com.mtk.data;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * 消息体封装实体
 */
public abstract class MessageBody {
    private int mTimestamp = 0;
    private String mSender = null;
    private String mContent = null;

    int getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(int timestamp) {
        this.mTimestamp = timestamp;
    }

    String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        this.mSender = sender;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public abstract void genXmlBuff(XmlSerializer serializer) throws IllegalArgumentException,
            IllegalStateException, IOException;
}
