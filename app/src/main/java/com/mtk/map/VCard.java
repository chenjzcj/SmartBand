package com.mtk.map;

import android.util.Log;

public class VCard {
    private static final String BEGIN = "BEGIN:VCARD";
    private static final String END = "END:VCARD";
    private static final String VERSION = "VERSION";
    private static final String NAME = "N";
    private static final String FORMAT_NAME = "FN";
    public static final String TELEPHONE = "TEL";
    private static final String EMAIL = "EMAIL";
    private static final String CRLF = "\r\n";
    private static final String SEPRATOR = ":";
    private static final String VERSION_21 = "2.1";
    private static final String VERSION_30 = "3.0";

    private String mVersion = "2.1"; //defaut 2.1
    private String mName;
    private String mFormatName;
    private String mTelephone;
    private String mEmail;

    public VCard(String version) {
        if (version.equals(VERSION_21) || version.equals(VERSION_30)) {
            mVersion = version;
        } else {
            mVersion = VERSION_21;
        }
    }

    public VCard() {
        mVersion = VERSION_21;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setFormatName(String name) {
        mFormatName = name;
    }

    public void setTelephone(String tel) {
        mTelephone = tel;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void reset() {
        mEmail = null;
        mTelephone = null;
        mFormatName = null;
        mName = null;
    }

    public String getName() {
        return mName;
    }

    public String getFormatName() {
        return mFormatName;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public String getEmail() {
        return mEmail;
    }

    public String toString() {
        StringBuilder vCard = new StringBuilder();
        vCard.append(BEGIN);
        vCard.append(CRLF);
        //version
        vCard.append(VERSION);
        vCard.append(SEPRATOR);
        vCard.append(mVersion);
        vCard.append(CRLF);
        //N (name) is neccessary
        vCard.append(NAME);
        vCard.append(SEPRATOR);
        if (mName != null) {
            vCard.append(mName);
        }
        vCard.append(CRLF);
        if (mVersion.equals(VERSION_30)) {
            //FN (name) is neccessary in vCard 3.0
            vCard.append(FORMAT_NAME);
            vCard.append(SEPRATOR);
            if (mName != null) {
                vCard.append(mFormatName);
            }
            vCard.append(CRLF);
        }

        if (mTelephone != null) {
            vCard.append(TELEPHONE);
            vCard.append(SEPRATOR);
            vCard.append(mTelephone);
            vCard.append(CRLF);
        }
        if (mEmail != null) {
            vCard.append(EMAIL);
            vCard.append(SEPRATOR);
            vCard.append(mEmail);
            vCard.append(CRLF);
        }

        vCard.append(END);
        return vCard.toString();
    }

    public void parse(String vcard) {
        if (vcard == null) {
            return;
        }
        String[] elements = vcard.split(CRLF);
        for (String element : elements) {
            String[] item = element.split(SEPRATOR);
            if (item.length < 2) {
                continue;
            }
            String key = item[0].trim();
            String value = item[1].trim();
            if (key.equals(NAME)) {
                mName = value;
            } else if (key.equals(FORMAT_NAME)) {
                mFormatName = value;
            } else if (key.equals(TELEPHONE)) {
                mTelephone = value;
            } else if (key.equals(EMAIL)) {
                mEmail = value;
            } else {
                log("unrecognized key:" + key);
            }
        }
    }

    private void log(String info) {
        if (null != info) {
            String TAG = "VCard";
            Log.v(TAG, info);
        }
    }
}