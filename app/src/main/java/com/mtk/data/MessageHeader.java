package com.mtk.data;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * 消息头封装实体
 */
public class MessageHeader {
    private int mMsgId = 0;
    private String mCategory = null;
    private String mSubType = null;
    private String mAction = null;

    public void setMsgId(int msgId) {
        this.mMsgId = msgId;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public void setSubType(String subType) {
        this.mSubType = subType;
    }

    public void setAction(String action) {
        this.mAction = action;
    }

    public int getMsgId() {
        return mMsgId;
    }

    String getCategory() {
        return mCategory;
    }

    public String getSubType() {
        return mSubType;
    }

    String getAction() {
        return mAction;
    }

    public void genXmlBuff(XmlSerializer serializer) throws IllegalArgumentException,
            IllegalStateException, IOException, NoDataException {
        if (this.getCategory() == null || this.getSubType() == null || this.getMsgId() == 0 || this.getAction() == null) {
            throw new NoDataException();
        }
        serializer.startTag(null, MessageObj.HEADER);
        // event_type
        serializer.startTag(null, MessageObj.CATEGORY);
        serializer.text(this.getCategory());
        serializer.endTag(null, MessageObj.CATEGORY);
        // mime_type
        serializer.startTag(null, MessageObj.SUBTYPE);
        serializer.text(this.getSubType());
        serializer.endTag(null, MessageObj.SUBTYPE);
        // data
        serializer.startTag(null, MessageObj.MSGID);
        serializer.text(String.valueOf(this.getMsgId()));
        serializer.endTag(null, MessageObj.MSGID);

        serializer.startTag(null, MessageObj.ACTION);
        serializer.text(this.getAction());
        serializer.endTag(null, MessageObj.ACTION);
        serializer.endTag(null, MessageObj.HEADER);
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                "mCategory='" + mCategory + '\'' +
                ", mSubType='" + mSubType + '\'' +
                ", mMsgId=" + mMsgId +
                ", mAction='" + mAction + '\'' +
                '}';
    }
}
