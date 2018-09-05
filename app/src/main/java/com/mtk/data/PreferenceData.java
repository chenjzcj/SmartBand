package com.mtk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mtk.MyApp;

/**
 * This class is the static utility class to get preference data.
 */
public final class PreferenceData {
    // Debugging
    private static final String LOG_TAG = "PreferenceData";
    // Preference Keys
    public static final String PREFERENCE_KEY_APP_INFO = "app_info";
    public static final String PREFERENCE_KEY_SMS = "enable_sms_service_preference";
    public static final String PREFERENCE_KEY_NOTIFI = "enable_notifi_service_preference";
    public static final String PREFERENCE_KEY_CALL = "enable_call_service_preference";
    public static final String PREFERENCE_KEY_ACCESSIBILITY = "show_accessibility_menu_preference";
    public static final String PREFERENCE_KEY_SELECT_NOTIFICATIONS = "select_notifi_preference";
    public static final String PREFERENCE_KEY_SELECT_BLOCKS = "select_blocks_preference";
    public static final String PREFERENCE_KEY_SHOW_CONNECTION_STATUS = "show_connection_status_preference";
    public static final String PREFERENCE_KEY_ALWAYS_FORWARD = "always_forward_preference";
    public static final String PREFERENCE_KEY_CURRENT_VERSION = "current_version_preference";

    private static final Context mContext = MyApp.getInstance().getApplicationContext();
    //获取默认的SharedPreferences对象
    private static final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

    /**
     * Return whether the enable_sms_service_preference is checked.
     */
    public static boolean isSmsServiceEnable() {
        boolean isEnable = mSharedPreferences.getBoolean(PREFERENCE_KEY_SMS, true);
        Log.i(LOG_TAG, "isSmsServiceEnable(), isEnable=" + isEnable);
        return isEnable;
    }

    /**
     * 设置短信服务推送可用性
     */
    public static void setSmsServiceEnable(boolean flag) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_KEY_SMS, flag).apply();
    }

    /**
     * Return whether the enable_notifi_service_preference is checked.
     */
    public static boolean isNotificationServiceEnable() {
        boolean isEnable = mSharedPreferences.getBoolean(PREFERENCE_KEY_NOTIFI, false);
        Log.i(LOG_TAG, "isNotificationServiceEnable(), isEnable=" + isEnable);
        return isEnable;
    }

    /**
     * 设置通知栏开关
     */
    public static void setNotificationServiceEnable(boolean flag) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_KEY_NOTIFI, flag).apply();
    }

    /**
     * Return whether the enable_call_service_preference is checked.
     */
    public static boolean isCallServiceEnable() {
        boolean isEnable = mSharedPreferences.getBoolean(PREFERENCE_KEY_CALL, true);
        Log.i(LOG_TAG, "isCallServiceEnable(), isEnable=" + isEnable);
        return isEnable;
    }

    /**
     * 设置电话服务可用性
     */
    public static void setCallServiceEnable(boolean flag) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_KEY_CALL, flag).apply();
    }

    /**
     * Return whether the show_accessibility_menu_preference is checked.
     */
    public static boolean isShowConnectionStatus() {
        boolean isShow = mSharedPreferences.getBoolean(PREFERENCE_KEY_SHOW_CONNECTION_STATUS, true);
        Log.i(LOG_TAG, "isShowConnectionStatus(), isShow=" + isShow);
        return isShow;
    }

    /**
     * 设置是否显示连接状态
     */
    public static void setShowConnectionStatus(boolean flag) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_KEY_SHOW_CONNECTION_STATUS, flag).apply();
    }

    /**
     * Return whether the always_forward_preference is checked.
     */
    private static boolean isAlwaysForward() {
        boolean isAlways = mSharedPreferences.getBoolean(PREFERENCE_KEY_ALWAYS_FORWARD, true);
        Log.i(LOG_TAG, "isAlwaysForward(), isAlways=" + isAlways);
        return isAlways;
    }

    /**
     * ruanan
     */
    public static void setAlwaysForward(boolean flag) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_KEY_ALWAYS_FORWARD, flag).apply();
    }

    /**
     * Return whether need to push message to remote device.
     * Push message if always forward preference is enable
     * or phone screen is locked.
     */
    public static boolean isNeedPush() {
        boolean needPush = (PreferenceData.isAlwaysForward() || Util.isScreenLocked(mContext));
        Log.i(LOG_TAG, "isNeedForward(), needPush=" + needPush);
        return needPush;
    }
}
