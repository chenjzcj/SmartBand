package com.mtk.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.ruanan.btnotification.R;

public class NotifiUtils {
    private NotifiUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    public static int notifictionId = 400;
    @SuppressLint("NewApi")
    public static void showNotification(Context context, Bitmap bitmap, String easetitle, String title, Intent intent,
            String content) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);

        Notification notification = builder.build();
        // 必须要设置这个值,否则在通知栏不会显示
        notification.icon = R.mipmap.ic_launcher;
        notification.tickerText = easetitle;
        notification.when = System.currentTimeMillis();

        // 自定义界面
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        rv.setImageViewBitmap(R.id.iv_notifi_icon, bitmap);
        rv.setTextViewText(R.id.tv_notifi_title, title);
        rv.setTextViewText(R.id.tv_notifi_content, content);
        // The color of the led. The hardware will do its best approximation.
        notification.ledARGB = 001100;
        notification.contentView = rv;

        PendingIntent pi = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_ONE_SHOT);
        notification.contentIntent = pi;
        // notification.setLatestEventInfo(context, title, content, pi);

        // 点击后消失
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;
        //不同的id,防止 覆盖
        nm.notify(notifictionId++, notification);
    }

}
