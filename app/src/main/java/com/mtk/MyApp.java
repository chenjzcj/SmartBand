package com.mtk;

import android.app.Activity;
import android.app.Application;

import com.mtk.data.MyExceptionHandel;
import com.mtk.util.LogUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.xutils.x;

import java.util.LinkedList;
import java.util.List;


/**
 * This class is the application enter, when it created, begin record logs.
 */
public class MyApp extends Application {
    private static MyApp sInstance = null;
    private final List<Activity> activityList = new LinkedList<>();

    /**
     * Return the instance of our application.
     *
     * @return the application instance
     */
    public static MyApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("onCreate(), BTNoticationApplication create!");
        //初始化未捕获异常
        MyExceptionHandel crashHandler = MyExceptionHandel.getInstance();
        crashHandler.init(getApplicationContext());
        sInstance = this;
        //buggly
        CrashReport.initCrashReport(getApplicationContext(), "900037363", false);
        initXuitls();
    }

    /**
     * xutils初始化
     */
    private void initXuitls() {
        x.Ext.init(this);
        // 是否输出debug日志
        x.Ext.setDebug(false);
    }

    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
