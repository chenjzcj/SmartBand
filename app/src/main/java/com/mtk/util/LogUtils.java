package com.mtk.util;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by MZIA(527633405@qq.com) on 2015/1/17 0017 2:40
 * Log统一管理工具类
 */
public class LogUtils {

    private static final String TAG = "MZIA";
    /**
     * 是否需要打印log日志，可以在application的onCreate函数里面初始化
     */
    public static boolean isDebug = true;

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setIsDebug(boolean isDebug) {
        LogUtils.isDebug = isDebug;
    }

    private LogUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug())
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug())
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug())
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug())
            Log.v(TAG, msg);
    }

    // 下面四个是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug())
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug())
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug())
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug())
            Log.i(tag, msg);
    }

    /*************************将日志信息保存在手机本地********************/
    /**
     * 保存日志到手机本地
     */
    public static void saveLog(String logInfo, String fileNamePrefix) {
        if (MyTextUtils.isEmpty(fileNamePrefix)) {
            fileNamePrefix = "";
        }
        File logDir = PathUtils.getInstance().getLogPath();
        //为了不让日志文件太多,当日志文件超过100的时候,就清空一次
        if (logDir.isDirectory()) {
            String[] list = logDir.list();
            if (list != null && list.length > 100) {
                FileUtils.delAllFile(logDir.getAbsolutePath());
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File(logDir, fileNamePrefix + TimeUtils.getCurrentDateString() + ".log"), true);
            fw.write(logInfo);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}