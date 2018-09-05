package com.mtk.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;

import com.mtk.Constants;
import com.mtk.data.AppList;
import com.mtk.data.BlockList;
import com.mtk.data.IgnoreList;
import com.mtk.data.Log;
import com.mtk.data.MessageHeader;
import com.mtk.data.MessageObj;
import com.mtk.data.NotificationMessageBody;
import com.mtk.data.PreferenceData;
import com.mtk.data.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class will receive and process all notifications.
 */
public class NotificationService extends AccessibilityService {
    // Debugging
    private static final String LOG_TAG = "NotificationService";
    // For get tile and content of notification
    private static final int NOTIFICATION_TITLE_TYPE = 9;
    private static final int NOTIFICATION_CONTENT_TYPE = 10;
    // Avoid propagating events to the client too frequently
    private static final long EVENT_NOTIFICATION_TIMEOUT_MILLIS = 0L;
    // Received event
    private AccessibilityEvent mAccessibilityEvent = null;
    // Received notification
    private Notification mNotification = null;

    public NotificationService() {
        Log.i(LOG_TAG, "NotifiService(), NotifiService created!");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(LOG_TAG, "onAccessibilityEvent(), eventType=" + event.getEventType());
        // Only concern TYPE_NOTIFICATION_STATE_CHANGED
        if (event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            return;
        }
        mAccessibilityEvent = event;
        mNotification = (Notification) mAccessibilityEvent.getParcelableData();
        Log.i(LOG_TAG, "onAccessibilityEvent(),toString=" + mAccessibilityEvent.toString());
        // If notification is null, will not forward it
        if (mNotification == null) {
            return;
        }

        // No need forward the notification of system ongoing widget
        ApplicationInfo appinfo = Util.getAppInfo(getBaseContext(), event.getPackageName());
        if (Util.isSystemApp(appinfo) && isOngoingNotifi()) {
            return;
        }

        boolean isServiceEnabled = PreferenceData.isNotificationServiceEnable();
        boolean needForward = PreferenceData.isNeedPush();
        if (isServiceEnabled && needForward) {
            // Filter notification according to ignore list and exclusion list
            HashSet<String> blockList = BlockList.getInstance().getBlockList();
            HashSet<String> ignoreList = IgnoreList.getInstance().getIgnoreList();
            HashSet<String> exclusionList = IgnoreList.getInstance().getExclusionList();

            String packageName = event.getPackageName().toString();
            if (!blockList.contains(packageName) && !ignoreList.contains(packageName)
                    && !exclusionList.contains(packageName)) {
                sendNotifiMessage();
            } else {
                Log.i(LOG_TAG, "Notice: This notification received!, package name=" + packageName);
            }
        }
    }

    /**
     * 不间断
     *
     * @return true为不间断
     */
    private boolean isOngoingNotifi() {
        boolean isOngoing = (mNotification.flags & Notification.FLAG_ONGOING_EVENT) != 0;
        Log.i(LOG_TAG, "isOnGoingNotifi(), isOngoing=" + isOngoing);
        return isOngoing;
    }


    /**
     * 将通知栏消息通过蓝牙连接推送给远端设备
     */
    private void sendNotifiMessage() {
        Log.i(LOG_TAG, "sendNotifiMessage()");
        // Create notification message
        MessageObj notificationMessage = new MessageObj();
        notificationMessage.setDataHeader(createNotificationHeader());
        notificationMessage.setDataBody(createNotificationBody());

        // Test whether the message is valid
        String msgContent = notificationMessage.getDataBody().getContent();
        String msgTitile = ((NotificationMessageBody) notificationMessage.getDataBody()).getTitle();
        String msgtickText = ((NotificationMessageBody) notificationMessage.getDataBody()).getTickerText();
        if (msgContent.length() == 0 && msgTitile.length() == 0 && msgtickText.length() == 0) {
            // Without content, no need to forward.
            return;
        }

        // Call main service to send data
        MainService service = MainService.getInstance();
        if (service != null) {
            service.sendNotiMessage(notificationMessage);
        }
    }

    /**
     * 创建消息头实体
     *
     * @return MessageHeader
     */
    private MessageHeader createNotificationHeader() {
        // Fill message header
        MessageHeader header = new MessageHeader();
        header.setMsgId(Util.genMessageId());
        header.setCategory(MessageObj.CATEGORY_NOTI);
        header.setSubType(MessageObj.SUBTYPE_NOTI);
        header.setAction(MessageObj.ACTION_ADD);
        Log.i(LOG_TAG, "createNotificationHeader(), header=" + header);
        return header;
    }

    /**
     * 创建通知消息体实体
     *
     * @return NotificationMessageBody
     */
    private NotificationMessageBody createNotificationBody() {
        // Get message body content
        ApplicationInfo appinfo = Util.getAppInfo(getBaseContext(), mAccessibilityEvent.getPackageName());
        String appName = Util.getAppName(getBaseContext(), appinfo);
        Bitmap sendIcon = Util.getMessageIcon(getBaseContext(), appinfo);
        int timestamp;
        //如果当前时间与通知的时间相差1小时以上,则设置为当前时间,否则设置通知的实际时间
        if ((System.currentTimeMillis() - mNotification.when) > 1000 * 60 * 60) {
            timestamp = Util.getUtcTime(System.currentTimeMillis());
        } else {
            timestamp = Util.getUtcTime(mNotification.when);
        }

        // Use the Content to Add Applist
        Map<Object, Object> applist = AppList.getInstance().getAppList();
        if (!applist.containsValue(mAccessibilityEvent.getPackageName())) {
            int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
            applist.remove(AppList.MAX_APP);
            max = max + 1;
            applist.put(AppList.MAX_APP, max);
            applist.put(max, mAccessibilityEvent.getPackageName());
            AppList.getInstance().saveAppList(applist);
        }
        // Get notification title and content.
        String title = "";
        String content = "";
        String[] textList = getNotificationText();
        if (textList != null) {
            if ((textList.length > 0) && (textList[0] != null)) {
                title = textList[0];
            } else {
                // Get notification ticker text.
                if (mNotification.tickerText != null) {
                    title = mNotification.tickerText.toString();
                }
                if (title.length() > Constants.TICKER_TEXT_MAX_LENGH) {
                    title = title.substring(0, Constants.TICKER_TEXT_MAX_LENGH) + Constants.TEXT_POSTFIX;
                }
            }

            if ((textList.length > 1) && (textList[1] != null)) {
                content = textList[1];
            }
            // Adjust text length, no longer than TEXT_MAX_LENGH
            if (title.length() > Constants.TITLE_TEXT_MAX_LENGH) {
                title = title.substring(0, Constants.TITLE_TEXT_MAX_LENGH) + Constants.TEXT_POSTFIX;
            }
        }

        if (title.length() > 0) {
            String leftBracket = "[";
            String rightBracket = "]";
            title = leftBracket.concat(title).concat(rightBracket);
        }

        // Fill message body
        String appID = Util.getKeyFromValue(mAccessibilityEvent.getPackageName());

        NotificationMessageBody body = new NotificationMessageBody();
        body.setSender(appName);
        body.setAppID(appID);
        body.setTitle(title);
        body.setContent(content);
        // 有的通知会把通知标题和通知内容并在一起。所以不用 mNotification.tickerText。用title取代
        body.setTickerText(title);
        body.setTimestamp(timestamp);
        body.setIcon(sendIcon);

        Log.i(LOG_TAG, "createNotificationBody(), body=" + body.toString().substring(0, 20));
        return body;
    }

    /**
     * 获取通知文本
     *
     * @return String[]
     */
    private String[] getNotificationText() {
        RemoteViews remoteViews = mNotification.contentView;
        if (remoteViews == null) {
            return new String[]{};
        }
        Class<? extends RemoteViews> remoteViewsClass = remoteViews.getClass();
        HashMap<Integer, String> text = new HashMap<>();
        int notificationKey = 0x1;
        try {
            Field[] outerFields = remoteViewsClass.getDeclaredFields();
            for (Field outerField : outerFields) {
                if (!outerField.getName().equals("mActions")) {
                    continue;
                }
                outerField.setAccessible(true);
                ArrayList<Object> actions = (ArrayList<Object>) outerField.get(remoteViews);
                for (Object action : actions) {
                    //Get notification tile and content
                    Field innerFields[] = action.getClass().getDeclaredFields();
                    Object value = null;
                    Integer type = null;
                    for (Field field : innerFields) {
                        field.setAccessible(true);
                        if (field.getName().equals("value")) {
                            value = field.get(action);
                        } else if (field.getName().equals("type")) {
                            type = field.getInt(action);
                        }
                    }

                    // If this notification filed is title or content, save it to text list
                    if ((type != null) && ((type == NOTIFICATION_TITLE_TYPE) || (type == NOTIFICATION_CONTENT_TYPE))) {
                        if (value != null) {
                            text.put(notificationKey, value.toString());
                        }
                        notificationKey++;
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Collection<String> values = text.values();
        String[] textArray = values.toArray(new String[values.size()]);
        Log.i(LOG_TAG, "getNotificationText(), text list = " + Arrays.toString(textArray));
        Log.i(LOG_TAG, "getNotificationText Exception");
        return textArray;
    }

    /*
     * // Content format: // Title // Content // [Ticker Text] private String getMessageContent() { Log.i(LOG_TAG,
     * "getContent(), mAccessibilityEvent.getText()=" + mAccessibilityEvent.getText()); StringBuilder content = new
     * StringBuilder(); final String textDecollator = "\r\n";
     * 
     * // Fill tile and content Collection<String> list = getNotificationText(); for (String str : list) { if
     * (content.length() > 0) { content.append(textDecollator); } content.append(str); }
     * 
     * // Fill ticker text if ((mNotification.tickerText != null) && (mNotification.tickerText.length() > 0)) { if
     * (content.length() > 0) { content.append(textDecollator); } // Format: [Ticker Text] content.append("[");
     * content.append(mNotification.tickerText); content.append("]"); }
     * 
     * // Adjust content length if (content.length() > Constants.CONTENT_MAX_LENGH) {
     * content.replace(Constants.CONTENT_MAX_LENGH, content.length(), "..."); }
     * 
     * Log.i(LOG_TAG, "getContent(), content=" + content); return content.toString(); }
     */

    /**
     * 当辅助功能服务连接成功时回调
     */
    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.i(LOG_TAG, "onServiceConnected()");
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 14) {
            setAccessibilityServiceInfo();
        }
        MainService.setNotificationService(this);
    }

    /**
     * 设置辅助服务信息
     */
    private void setAccessibilityServiceInfo() {
        Log.i(LOG_TAG, "setAccessibilityServiceInfo()");
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        accessibilityServiceInfo.notificationTimeout = EVENT_NOTIFICATION_TIMEOUT_MILLIS;
        setServiceInfo(accessibilityServiceInfo);
    }

    @Override
    public void onInterrupt() {
        Log.i(LOG_TAG, "onInterrupt()");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(LOG_TAG, "onUnbind()");
        MainService.clearNotificationService();
        return false;
    }
}