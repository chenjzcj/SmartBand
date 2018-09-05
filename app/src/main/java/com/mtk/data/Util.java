package com.mtk.data;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.ContactsContract.PhoneLookup;

import com.mtk.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * This class is the static utility class, it provides some useful utility function.
 */
public final class Util {
    // Debugging
    private static final String LOG_TAG = "Util";

    // message id, for both notification and SMS
    private static int sMessageId = 0x0100;

    /**
     * Return message id, it is unique for all notification or SMS
     *
     * @return message id
     */
    public static int genMessageId() {
        Log.i(LOG_TAG, "genMessageId(), messageId=" + sMessageId);
        return sMessageId++;
    }

    /**
     * Get the application info of certain package
     * 获取特定包的应用程序信息
     *
     * @param context     Context
     * @param packageName CharSequence
     * @return ApplicationInfo
     */
    public static ApplicationInfo getAppInfo(Context context, CharSequence packageName) {
        PackageManager packagemanager = context.getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = packagemanager.getApplicationInfo(packageName.toString(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "getAppInfo(), appInfo=" + appInfo);
        return appInfo;
    }

    /**
     * Get the application name of certain application
     *
     * @param context Context
     * @param appInfo ApplicationInfo
     * @return application name
     */
    public static String getAppName(Context context, ApplicationInfo appInfo) {
        String appName;
        if ((context == null) || (appInfo == null)) {
            appName = Constants.NULL_TEXT_NAME;
        } else {
            appName = context.getPackageManager().getApplicationLabel(appInfo).toString();
        }
        Log.i(LOG_TAG, "getAppName(), appName=" + appName);
        return appName;
    }

    /**
     * Get the application icon of certain application
     *
     * @param context Context
     * @param appInfo ApplicationInfo
     * @return application icon
     */
    public static Bitmap getAppIcon(Context context, ApplicationInfo appInfo) {
        Log.i(LOG_TAG, "getAppIcon()");
        return createIcon(context, appInfo, true);
    }

    /**
     * Get the message icon of certain application, will send it to remote device.
     *
     * @param context Context
     * @param appInfo ApplicationInfo
     * @return message icon
     */
    public static Bitmap getMessageIcon(Context context, ApplicationInfo appInfo) {
        Log.i(LOG_TAG, "getMessageIcon()");
        return createIcon(context, appInfo, false);
    }

    /**
     * 创建icon图标
     *
     * @param context   Context
     * @param appInfo   ApplicationInfo
     * @param isAppIcon isAppIcon
     * @return Bitmap
     */
    private static Bitmap createIcon(Context context, ApplicationInfo appInfo, boolean isAppIcon) {
        Bitmap icon;
        if ((context == null) || (appInfo == null)) {
            icon = null;
        } else {
            Drawable drawable = context.getPackageManager().getApplicationIcon(appInfo);
            if (isAppIcon) {
                icon = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            } else {
                icon = createWhiteBitmap();
            }

            Canvas canvas = new Canvas(icon);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            Log.i(LOG_TAG, "createIcon(), icon width=" + icon.getWidth());
        }
        return icon;
    }

    /**
     * 创建空白的位图
     *
     * @return Bitmap
     */
    private static Bitmap createWhiteBitmap() {
        Bitmap whiteBitmap = Bitmap.createBitmap(Constants.APP_ICON_WIDTH, Constants.APP_ICON_HEIGHT, Config.RGB_565);
        int[] pixels = new int[Constants.APP_ICON_WIDTH * Constants.APP_ICON_HEIGHT];

        for (int y = 0; y < Constants.APP_ICON_HEIGHT; y++) {
            for (int x = 0; x < Constants.APP_ICON_WIDTH; x++) {
                int index = y * Constants.APP_ICON_WIDTH + x;
                int r = ((pixels[index] >> 16) & 0xff) | 0xff;
                int g = ((pixels[index] >> 8) & 0xff) | 0xff;
                int b = (pixels[index] & 0xff) | 0xff;
                pixels[index] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
        }

        Log.i(LOG_TAG, "createWhiteBitmap(), pixels num=" + pixels.length);
        whiteBitmap.setPixels(pixels, 0, Constants.APP_ICON_WIDTH, 0, 0,
                Constants.APP_ICON_WIDTH, Constants.APP_ICON_HEIGHT);
        return whiteBitmap;
    }

    /**
     * Returns whether the application is system application.
     *
     * @param appInfo ApplicationInfo
     * @return Return true, if the application is system application, otherwise, return false.
     */
    public static boolean isSystemApp(ApplicationInfo appInfo) {
        boolean isSystemApp = false;
        if (((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                || ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)) {
            isSystemApp = true;
        }
        Log.i(LOG_TAG, "isSystemApp(), packageInfo.packageName=" + appInfo.packageName + ", isSystemApp=" + isSystemApp);
        return isSystemApp;
    }

    /**
     * Returns whether the mobile phone screen is locked.
     *
     * @param context
     * @return Return true, if screen is locked, otherwise, return false.
     */
    public static boolean isScreenLocked(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        Boolean isScreenLocked = km.inKeyguardRestrictedInputMode();
        Log.i(LOG_TAG, "isScreenOn(), isScreenOn=" + isScreenLocked);
        return isScreenLocked;
    }

    /**
     * Returns whether the mobile phone screen is currently on.
     *
     * @param context
     * @return Return true, if screen is on, otherwise, return false.
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        Boolean isScreenOn = pm.isScreenOn();

        Log.i(LOG_TAG, "isScreenOn(), isScreenOn=" + isScreenOn);
        return isScreenOn;
    }

    /**
     * Resize a bitmap according to certain scale
     *
     * @param bitmap
     * @param widthScale
     * @param heightScale
     * @return resized bitmap
     */
    private static Bitmap resizeBitmapByScale(Bitmap bitmap, float widthScale, float heightScale) {
        Log.i(LOG_TAG, "resizeBitmapByScale(), widthScale=" + widthScale + ", heightScale=" + heightScale);

        // Resize bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return resizeBmp;
    }

    public static Bitmap resizeBitmapBySize(Bitmap bitmap, int width, int height) {
        Log.i(LOG_TAG, "resizeBitmapBySize(), width=" + width + ", height=" + height);

        // Get resize scale
        float scaleWidth = ((float) width) / bitmap.getWidth();
        float scaleHeight = ((float) height) / bitmap.getHeight();

        return resizeBitmapByScale(bitmap, scaleWidth, scaleHeight);
    }

    /**
     * Get the current date in "yyyy-MM-dd HH:mm:ss" format.
     *
     * @return the formatted date string
     */
    @SuppressLint("SimpleDateFormat")
    public static String getFormatedDate() {
        // Date format: 2013-05-24 16:00:00
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date(System.currentTimeMillis()));

        // Log.i(LOG_TAG, "getFormatedDate(), date=" + date);
        return date;
    }

    /**
     * Transform the local time to UTC time(zero zone).
     *
     * @param localTime long
     * @return the zero zone time
     */
    public static int getUtcTime(long localTime) {
        Log.i(LOG_TAG, "getUTCTime(), local time=" + localTime);
        // Get UTC time
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(localTime);
        // Only for test
        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // String localDate = dateFormat.format(new Date(cal.getTimeInMillis()));
        // int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // int dstOffset = cal.get(Calendar.DST_OFFSET);
        // cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        // String zeroZoneDate = dateFormat.format(new Date(cal.getTimeInMillis()));
        // Transform to seconds
        int utcTime = (int) (cal.getTimeInMillis() / 1000);
        Log.i(LOG_TAG, "getUTCTime(), UTC time=" + utcTime);
        return utcTime;
    }

    /**
     * Get UTC time zone(zero zone).
     *
     * @param localTime
     * @return the time zone
     */
    public static int getUtcTimeZone(long localTime) {
        // Get UTC time zone
        TimeZone tz = TimeZone.getDefault();

        // Transform to seconds
        int tzs = tz.getRawOffset();
        Date dt = new Date(localTime);
        if (tz.inDaylightTime(dt)) {
            tzs += tz.getDSTSavings();
        }
        Log.i(LOG_TAG, "getUtcTimeZone(), UTC time zone=" + tzs);

        return tzs;
    }

    /**
     * Compress the bitmap to JPG format, and then return its bytes.
     *
     * @param bitmap
     * @return the bytes of bitmap (JPG format)
     */
    public static byte[] getJpgBytes(Bitmap bitmap) {
        Log.i(LOG_TAG, "getJpgBytesFromBitmap()");

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, outStream);
        return outStream.toByteArray();
    }

    /**
     * Lookup contact name from phonebook by phone number.
     *
     * @param context  Context
     * @param phoneNum phoneNum
     * @return the contact name
     */
    public static String getContactName(Context context, String phoneNum) {
        if (phoneNum == null) {
            return null;
        } else if (phoneNum.equals("")) {
            return null;
        } else {
            Cursor cursor = null;
            try {
                String contactName = phoneNum;
                Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactName));
                cursor = context.getContentResolver().query(uri, new String[]{"display_name"}, null, null, null);
                if ((cursor != null) && cursor.moveToFirst()) {
                    contactName = cursor.getString(0);
                }
                Log.i(LOG_TAG, "getContactName(), contactName=" + contactName);
                return contactName;
            } catch (Exception e) {
                Log.i(LOG_TAG, "getContactName Exception");
                return null;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    /**
     * 通过应用包名获取appID
     *
     * @param pkgName 应用包包
     * @return appID
     */
    public static String getKeyFromValue(CharSequence pkgName) {
        Map<Object, Object> appList = AppList.getInstance().getAppList();
        Set<?> set = appList.entrySet();
        Iterator<?> it = set.iterator();
        String key = null;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(pkgName)) {
                key = entry.getKey().toString();
            }
        }
        return key;
    }

    @SuppressWarnings("deprecation")
    public static long getAvailableStore(String filePath) {
        // get sdcard path
        StatFs statFs = new StatFs(filePath);
        // get block SIZE
        long blockSize = statFs.getBlockSize();
        // getBLOCK numbers
        // long totalBlocks = statFs.getBlockCount();
        // get available Blocks
        long availaBlock = statFs.getAvailableBlocks();
        // long total = totalBlocks * blocSize;
        long availableSpace = availaBlock * blockSize;
        return availableSpace / 1024;
    }

    public static byte[] getAlphaJpegImage(Bitmap bitmap) {
        Log.i(LOG_TAG, "getAlphaJpegImage()");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, bos);
        byte[] out_stream = bos.toByteArray();

        if (!bitmap.hasAlpha()) {
            return out_stream;
        }

        // header: alpha size: wxh 8 byte
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int size = w * h + 2;
        int total_size = bos.size() + 2 + size;
        int index = 5;

        if (total_size > 65535) {
            return out_stream;
        }

        try {
            out_stream = new byte[total_size];
            System.arraycopy(bos.toByteArray(), 0, out_stream, 0, 2);

            out_stream[2] = (byte) 0xFF;
            out_stream[3] = (byte) 0xEE;
            out_stream[4] = (byte) (size >> 8);
            out_stream[5] = (byte) (size & 0xFF);
            for (int i = 0; i < h; ++i) {
                for (int j = 0; j < w; ++j) {
                    out_stream[++index] = (byte) Color.alpha(bitmap.getPixel(j, i));
                }
            }

            System.arraycopy(bos.toByteArray(), 2, out_stream, index + 1, bos.size() - 2);
            bos.close();
        } catch (IOException e) {

        }
        return out_stream;
    }

}
