package com.mtk.util;

import android.text.TextUtils;

/**
 * 作者 : 527633405@qq.com
 * 时间 : 2015/12/22 0022
 */
public class NumUtil {
    /**
     * @param num 1到99的数字
     * @return 如果num < 10,返回0+n,否则返回n
     */
    public static String get2StrLenNum(int num) {
        if (!(num >= 0 && num < 100)) {
            throw new IllegalArgumentException("num < 0 || num>100");
        }
        return num < 10 ? "0" + num : "" + num;
    }

    /**
     * 这个方法只能用于判断是否是正整数
     * http://blog.csdn.net/lengyuhong/article/details/5938392
     *
     * @param likeNum 需要鉴定的字符串
     * @return true即为纯数字
     */
    public static boolean isNumber(String likeNum) {
        return !TextUtils.isEmpty(likeNum) && likeNum.matches("[0-9]+");
    }

    /**
     * 获取当前时间戳的hashcode值
     *
     * @return 当前时间戳的hashcode值
     */
    public static String getCurrentTimeHashcode() {
        return String.valueOf(System.currentTimeMillis()).hashCode() + "";
    }

    /**
     * 将版本信息转化成整数,如4.0.3转换后为4+0+3=7
     *
     * @param version 版本
     * @return 版本的整数
     */
    public static int parseVerstion(String version) {
        int result = 0;
        String[] split = version.split("\\.");
        for (String s : split) {
            result += Integer.parseInt(s);
            LogUtils.i("s = " + s + ",result = " + result);
        }
        return result;
    }
}
