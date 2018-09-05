package com.mtk;

/**
 * This class is used for recording some useful constants,
 * and will collect these info from remote device later.
 */
public final class Constants {
    // Null app name
    public static final String NULL_TEXT_NAME = "(unknown)";
    // App icon size
    public static final int APP_ICON_WIDTH = 40;
    public static final int APP_ICON_HEIGHT = 40;
    public static final int NOTIFYMINIHEADERLENTH = 8;
    public static final int NOTIFYSYNCLENTH = 4;
    // Message content size
    public static final int TEXT_MAX_LENGH = 256;
    public static final int TICKER_TEXT_MAX_LENGH = 128;
    public static final int TITLE_TEXT_MAX_LENGH = 128;
    public static final String TEXT_POSTFIX = "...";

    public static final String UPDATE_APP_URL_CHINA = "http://api.ruanan.com/public/native_watch/version.xml";        //蓝牙通知更新国内app链接
    public static final String UPDATE_APP_URL_FOREIGN = "http://api.ruanan.com/public/foreign_watch/version.xml";//蓝牙通知更新国外app链接
}
