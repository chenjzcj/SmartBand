package com.mtk.map;

import com.mtk.data.MessageObj;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BMessage {

    public static final String CRLF = "\r\n";
    public static final String SEPRATOR = ":";
    //BMessage Tag
    private static final String BEGINBMSG = "BEGIN:BMSG";
    private static final String ENDBMSG = "END:BMSG";
    private static final String VERSION = "VERSION";
    private static final String VERSION_10 = "1.0";
    private static final String STATUS = "STATUS";
    private static final String STATUSREAD = "READ";
    private static final String STATUSUNREAD = "UNREAD";
    private static final String TYPE = "TYPE:SMS_GSM";
    private static final String FOLDER = "FOLDER";
    private static final String BEGINBENV = "BEGIN:BENV";
    private static final String ENDBENV = "END:BENV";
    private static final String BEGINBBODY = "BEGIN:BBODY";
    private static final String ENDBBODY = "END:BBODY";
    private static final String BEGINMSG = "BEGIN:MSG";
    private static final String ENDMSG = "END:MSG";
    private static final String CHARSET = "CHARSET:UTF-8";
    private static final String LENGTH = "LENGTH";
    //private final String TAG		= "BMessageObject";
    private int mReadStatus;
    private String mOrignator;
    //envelope
    private ArrayList<Integer> mRecipientSize;
    private ArrayList<String> mRecipient;
    private ArrayList<Integer> mContentSize;
    private long mWholeSize;
    private String mBody;

    public BMessage() {
        initCache();
    }

    private void initCache() {
        mRecipient = new ArrayList<>();
        mRecipientSize = new ArrayList<>();
        mContentSize = new ArrayList<>();
    }

    public void reset() {
        mReadStatus = -1;
        mOrignator = null;

        if (mRecipientSize != null) {
            mRecipientSize.clear();
        }
        if (mRecipient != null) {
            mRecipient.clear();
        }
        if (mContentSize != null) {
            mContentSize.clear();
        }
        mWholeSize = 0;
    }

    public boolean setOrignator(String orignator) {
        mOrignator = orignator;

        return true;
    }

    //note: the input is nest vcards
    public boolean addRecipient(String recipient) {
        if (recipient == null) {
            return true;
        }
        mRecipientSize.add(recipient.length());
        mRecipient.add(recipient);
        return true;
    }

    public boolean setContentSize(int size) {
        mWholeSize = size;
        mContentSize.add(size);
        return true;
    }

    public boolean setContentSize(File file) {
        if (file == null) {
            return false;
        }
        try {
            FileInputStream stream = new FileInputStream(file);
            int size = stream.available();
            mWholeSize = size;
            mContentSize.add(size);
            stream.close();
            return true;
        } catch (IOException e) {

        }
        return true;
    }

    public boolean setContent(String text) {
        mBody = text;
        return true;
    }

    public long getContentSize() {
        return mWholeSize;
    }

    public int getContentSize(int i) {
        if (i < mContentSize.size()) {
            return mContentSize.get(i).intValue();
        } else {
            return 0;
        }
    }

    String getOrignator() {
        return mOrignator;
    }

    public String getFinalRecipient() {
        if (mRecipient.size() > 0) {
            return mRecipient.get(mRecipient.size() - 1);
        } else {
            return null;
        }
    }

    ArrayList<String> getRecipient() {
        return mRecipient;
    }

    int getReadStatus() {
        return mReadStatus;
    }

    public void setReadStatus(int state) {
        switch (state) {
            case MapConstants.READ_STATUS:
            case MapConstants.UNREAD_STATUS:
                mReadStatus = state;
                break;
            default:

                mReadStatus = MapConstants.READ_STATUS;
        }
    }

    public String toString() {
        StringBuilder bMessageObject = new StringBuilder();

        //Head
        bMessageObject.append(BEGINBMSG);
        bMessageObject.append(CRLF);
        //Version
        bMessageObject.append(VERSION);
        bMessageObject.append(SEPRATOR);
        bMessageObject.append(VERSION_10);
        bMessageObject.append(CRLF);
        //Status
        bMessageObject.append(STATUS);
        bMessageObject.append(SEPRATOR);
        if (getReadStatus() == MapConstants.READ_STATUS) {
            bMessageObject.append(STATUSREAD);
        } else {
            bMessageObject.append(STATUSUNREAD);
        }
        bMessageObject.append(CRLF);
        //Type
        bMessageObject.append(TYPE);
        bMessageObject.append(CRLF);
        //Folder
        bMessageObject.append(FOLDER);
        bMessageObject.append(SEPRATOR);
        bMessageObject.append(CRLF);
        //Orignator
        bMessageObject.append(getOrignator());
        bMessageObject.append(CRLF);
        //Begin BENV
        bMessageObject.append(BEGINBENV);
        bMessageObject.append(CRLF);
        bMessageObject.append(getRecipient());
        bMessageObject.append(CRLF);
        //Begin BBody
        bMessageObject.append(BEGINBBODY);
        bMessageObject.append(CRLF);
        bMessageObject.append(CHARSET);
        bMessageObject.append(CRLF);
        bMessageObject.append(LENGTH);
        bMessageObject.append(SEPRATOR);
        try {
            if (mBody == null) {
                mBody = "\n";
                bMessageObject.append("0");
            } else {
                bMessageObject.append(String.valueOf(mBody.getBytes(MessageObj.CHARSET).length));
            }
        } catch (Exception e) {

        }

        bMessageObject.append(CRLF);
        //MSG BODY
        bMessageObject.append(BEGINMSG);
        bMessageObject.append(CRLF);
        bMessageObject.append(mBody);
        bMessageObject.append(CRLF);
        bMessageObject.append(ENDMSG);
        bMessageObject.append(CRLF);
        bMessageObject.append(ENDBBODY);
        bMessageObject.append(CRLF);
        bMessageObject.append(ENDBENV);
        bMessageObject.append(CRLF);
        bMessageObject.append(ENDBMSG);
        return bMessageObject.toString();
    }
}
