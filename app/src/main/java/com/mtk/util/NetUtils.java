package com.mtk.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 跟网络相关的工具类
 *
 * @author zhongcj
 * @time 2015-1-15 下午10:56:17
 */
public class NetUtils {
    private NetUtils() {
  /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return true为连接 false为未连接
     * @author zhongcj
     */
    public static boolean isNetConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     *
     * @param context
     * @return true为wifi连接 false为非wifi连接
     * @author zhongcj
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return false;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        //先要进行网络连接判断,只有在连接网络的情况才能判断是否是wifi连接
        if (networkInfo == null) {
            ToastUtils.showShortToast(context, "网络没有连接");
            return false;
        } else {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
    }

    /**
     * 打开设置或网络设置界面
     *
     * @param context
     * @param isNetSetting true 打开网络设置界面 false打开设置界面
     * @author zhongcj
     */
    public static void openSetting(Context context, boolean isNetSetting) {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT > 10) {
            //3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
            if (isNetSetting) {
                intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            } else {
                intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            }
        } else {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        }
        context.startActivity(intent);
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        if (isNetConnected(context)) {
            if (isWifiConnected(context)) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int i = wifiInfo.getIpAddress();
                return int2ip(i);
            } else {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()) {
                                return inetAddress.getHostAddress().toString();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
