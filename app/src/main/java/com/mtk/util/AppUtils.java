package com.mtk.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.ruanan.btnotification.R;

import java.util.Iterator;
import java.util.List;


/**
 * Created by MZIA(527633405@qq.com) on 2015/1/14 0014 11:09
 * 跟App相关的工具类
 */
public class AppUtils {

    /**
     * 获取应用程序名称
     *
     * @param context Context
     * @return 返回当前应用的名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序版本号
     *
     * @param context Context
     * @return 返回当前应用程序版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取应用程序版本名称
     *
     * @param context Context
     * @return 返回当前应用程序版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前正在运行的Activity名称
     * 需要权限:<uses-permission android:name="android.permission.GET_TASKS"/>
     *
     * @return ActivityName
     */
    public String getActivityName(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        return info.topActivity.getShortClassName();
    }

    /**
     * 创建桌面快捷方式
     * 需要权限 : <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
     *
     * @param resId 应用图标资源文件
     */
    public static void createShortcut(Context context, int resId) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        shortcut.putExtra("duplicate", false);
        ComponentName comp = new ComponentName(context.getPackageName(), "."
                + ((Activity) context).getLocalClassName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
                Intent.ACTION_MAIN).setComponent(comp));
        ShortcutIconResource iconRes = ShortcutIconResource.fromContext(
                context, resId);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        context.sendBroadcast(shortcut);
    }

    /**
     * 判断当前应用是否在前台运行
     *
     * @param context Context
     * @return true, 当前应用在前台运行
     */
    public static boolean isPagkageName(Context context) {
        String packageName = context.getPackageName();
        Iterator<RunningTaskInfo> iterator = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningTasks(1).iterator();
        while (iterator.hasNext()) {
            if (iterator.next().topActivity.getPackageName().equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断app是否在前台运行
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        return !TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName());
    }

    /**
     * 判断某个界面是否在前台
     * http://blog.csdn.net/lsqwdx91805605/article/details/42834257
     *
     * @param context   Context
     * @param className 某个界面名称
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断程序是否在后台运行(锁屏状态也算是后台运行)
     */
    public static boolean isBackgroundRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo process : processList) {
                if (process.processName.startsWith(context.getPackageName())) {
                    boolean isBackground = process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                            && process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
                    boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                    return isBackground || isLockedState;
                }
            }
        }
        return false;
    }

    /**
     * 获取App安装包信息
     *
     * @param context 上下文
     * @return PackageInfo
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }

}
