package com.mtk.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ruanan.btnotification.R;

import java.lang.reflect.Field;
import java.util.List;

public class SystemUtil {

    public static final int NETTYPE_WIFI = 0x01;

    public static final int NETTYPE_CMWAP = 0x02;

    public static final int NETTYPE_CMNET = 0x03;

    /**
     * 判断是否手机插入Sd卡
     */
    public static boolean hasSdcard() {

        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断是否联网或者漫游
     *
     * @param context
     * @return boolean
     */
    public static boolean hasNet(Context context) {
        //获得ConnectivityManager的管理器
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.isRoaming()) { //漫游判断
            return true;
        }
        return true;
    }

    /**
     * 获得The data stream for the file
     */
    public static String getUrlPath(Activity context, Uri uri) {

        if (uri.toString().contains("file:/")) {
            return uri.getPath();
        }
        String[] proj = {MediaStore.Images.Media.DATA};


        //Cursor cursor = context.managedQuery(uri, proj, null, null, null);
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
//		int column_index = cursor
//				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            return cursor.getString(column_index);
        }
        return null;
    }

    /**
     * 兼容4.4以上系统选择系统图片
     */
    @SuppressLint("NewApi")
    public static String getPath(Activity context, Uri contentUri) {
        String filePath = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String wholeID = DocumentsContract.getDocumentId(contentUri);
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
                return filePath;
            }
            cursor.close();
        } else {
            if (contentUri.toString().contains("file:/")) {
                return contentUri.getPath();
            }
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(column_index);
            return filePath;
        }
        return null;
    }

    /**
     * 从传入Uri获得真实的path
     */
    public String getRealPathFromURI(Activity context, Uri contentUri) {

        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            return cursor.getString(column_index);
        }
        return null;
    }

    /**
     * 获得屏幕的宽度
     */
    public static int getScreenWidth(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度
     */
    public static int getScreenHeight(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getSystemVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获得网络的类型
     */
    public static int getNetworkType(Context context) {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {  //判断是否联网
            return netType;
        }
        int nType = networkInfo.getType(); //获得
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 隐藏软件盘
     */
    public static void hideSoftKeyborad(Activity context) {
        final View v = context.getWindow().peekDecorView(); //Retrieve the current decor view
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) context     //获得输入方法的Manager
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static int getAnimId(String name) {
        try {
            Field field = R.anim.class.getField(name);
            Object o = field.get(null);
            if (o != null && o instanceof Integer) {
                return (Integer) (o);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 复制
     *
     * @param context
     * @param text
     */
    @SuppressLint({"NewApi", "NewApi"})
    public static void copyText(Context context, String text) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("label", text);
            clipboardManager.setPrimaryClip(clipData);
        } else {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(text);
        }

    }


    /**
     * 获取android当前可用内存大小
     */
    public static String getAvailMemory(Context context) {// 获取android当前可用内存大小

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存

        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }

    /**
     * 获取android当前可用内存大小
     */
    public static long getmem_UNUSED(Context mContext) {
        long MEM_UNUSED;
        ActivityManager am = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        MEM_UNUSED = mi.availMem / 1024;
        return MEM_UNUSED;
    }

    /**
     * 消耗的内存大小
     */
    @SuppressLint("NewApi")
    public static long getmem_used(Context mContext) {
        long MEM_USED;
        ActivityManager am = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        MEM_USED = (mi.totalMem - mi.availMem) / 1024;
        return MEM_USED;
    }

    /**
     * 服务是否在运行
     *
     * @param mContext
     * @param className
     * @return 服务运行状态
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
