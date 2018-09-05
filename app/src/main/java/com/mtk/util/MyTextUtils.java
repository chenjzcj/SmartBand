package com.mtk.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MZIA(527633405@qq.com) on 2016/6/12 0012 2:40
 * 文本相差操作工具类
 */
public class MyTextUtils {
    /**
     * 实现文本复制功能
     * add by wangqianzhou
     *
     * @param content 文本内容
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(ClipData.newPlainText(null, content.trim()));
    }

    /**
     * 实现粘贴功能
     * add by wangqianzhou
     *
     * @param context Context
     * @return 从粘贴板获取的字符串
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    /**
     * http://blog.csdn.net/lovexjyong/article/details/17021235
     */
    public static SpannableStringBuilder spanBuilder(Context context, String text, boolean setTextSize, int colorId, boolean setUrl, String urlText) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorId)), 0,
                text.length(), Spanned.SPAN_PARAGRAPH);
        if (setTextSize) {
            ssb.setSpan(new RelativeSizeSpan(1.5f), 0, text.length(), Spanned.SPAN_PARAGRAPH);
        }
        if (setUrl) {
            ssb.setSpan(new URLSpan(urlText), 0, text.length(), Spanned.SPAN_PARAGRAPH);
        }
        return ssb;
    }

    /**
     * 判断给定字符串是否空白串。
     * 空白串是指由空格、制表符、回车符、换行符组成的字符串
     * 若输入字符串为null或空字符串，返回true
     *
     * @param str 给定需要判断的字符串
     * @return true为空白字符串或者包含隐式符号的字符串
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str)) return true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email 预期的电子邮箱地址
     * @return true即为合法的电子邮箱地址
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email)) return false;
        Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        return emailer.matcher(email).matches();
    }

    /**
     * 从字符串中获取数字
     *
     * @param target 目标字符串
     * @return 数字
     */
    public static String getNumFromStr(String target) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(target);
        return m.replaceAll("").trim();
    }
}
