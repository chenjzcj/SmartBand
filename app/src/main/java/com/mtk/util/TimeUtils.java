package com.mtk.util;

import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MZIA(527633405@qq.com) on 2015/6/12 0012 2:40
 * 时间操作工具类
 */
public class TimeUtils {

    static long secondsOfHour = 60 * 60;
    static long secondsOfDay = secondsOfHour * 24;
    static long secondsOfTwoDay = secondsOfDay * 2;
    static long secondsOfThreeDay = secondsOfDay * 3;
    // 用于格式化日期,作为日志文件名的一部分
    static SimpleDateFormat mStandardFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    static SimpleDateFormat mFullDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getSimpleTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date date;
        try {
            date = mFullDateFormat.parse(time);
            return format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getMinutesBefore(String time, String now) {
        try {
            Date t = mFullDateFormat.parse(time);
            Date n = mFullDateFormat.parse(now);
            long ms = n.getTime() - t.getTime();
            return (int) (ms / (1000 * 60 * 60));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * 获取标准的中国时间显示格式
     *
     * @return String
     */
    public static String getStandardTimeFormat() {
        return mStandardFormat.format(new Date());
    }

    /**
     * time 是否大于 now
     *
     * @param time
     * @param now
     * @return
     */
    public static boolean minutesBefore(String time, String now) {
        try {
            Date t = mFullDateFormat.parse(time);
            Date n = mFullDateFormat.parse(now);
            long ms = t.getTime() - n.getTime();
            return ms >= 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static String formartTaskDate(String time) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formater = new SimpleDateFormat("MM月dd日");
        Date date = null;
        try {
            date = f.parse(time);
            return formater.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Calendar formatTime(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar instance = Calendar.getInstance();
        try {
            instance.setTime(simpleDateFormat.parse(s));
            return instance;
        } catch (ParseException ex) {
            return null;
        }
    }

    public static String formartTaskTime(String time) {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formater = new SimpleDateFormat("HH:mm");
        Date date;
        try {
            date = f.parse(time);
            return formater.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取当前时间的标准格式
     *
     * @return 当前时间的标准格式
     */
    public static String getCurrentDateString() {
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        return f.format(new Date(System.currentTimeMillis()));
    }

    public static String getAfterDateString(int millis) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        return f.format(new Date(System.currentTimeMillis() + millis));
    }

    public static String getAfterTimeString(int millis) {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
        return f.format(new Date(System.currentTimeMillis() + millis));
    }

    public static String getCurrentTimeString() {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        return f.format(new Date(System.currentTimeMillis()));
    }

    public static String getCurrentTimeString(int afterMinutes) {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        return f.format(new Date(System.currentTimeMillis() + (afterMinutes * 60 * 1000)));
    }

    public static String getSimpleTimeString(String time) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return f.format(new Date(Long.valueOf(time)));
    }

    public static String getSimpleTimeString2(String time) {
        SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm");
        return f.format(new Date(Long.valueOf(time)));
    }

    public static String formatChatTime(String time) {
        try {
            Date d = mFullDateFormat.parse(time);
            SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm:ss");
            return f.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formatTime(Date date) {
        // SimpleDateFormat f = new
        // SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        return mFullDateFormat.format(date);

    }

    public static String formatTime(long ss) {
        return mFullDateFormat.format(new Date(ss));
    }

    // @SuppressLint("SimpleDateFormat")
    public static String formatDate(long ss) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(new Date(ss));
    }

    public static String format(long ss) {
        SimpleDateFormat f = new SimpleDateFormat("MM月dd日 HH:mm");
        return f.format(new Date(ss));
    }

    /**
     * 获取当前时间的yyyy-MM-dd HH:mm:ss格式
     *
     * @return 返回当前时间的yyyy-MM-dd HH:mm:ss格式
     */
    public static String getCurrentTime() {
        return mFullDateFormat.format(new Date(System.currentTimeMillis()));
    }

    public static Date formartToDate(String time) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = f.parse(time);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateSelectorConvert(String text) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date = f.parse(text);
            return mSimpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    // 得到简单的时间描述
    public static String getSimpleTimeDesc(String now, String target) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            Date nowDate = f.parse(now);
            Date targetDate = f.parse(target);
            String result;
            long date = (nowDate.getTime() - targetDate.getTime()) / (24 * 60 * 60 * 1000);
            if (date < 7) {
                if (date == 0) {
                    long h = (nowDate.getTime() - targetDate.getTime()) / (60 * 60 * 1000);
                    if (h < 1) {
                        long m = (nowDate.getTime() - targetDate.getTime()) / (60 * 1000);
                        if (m < 1) {
                            result = "刚刚";
                        } else {
                            result = m + "分钟前";
                        }
                    } else {
                        result = h + "小时前";
                    }
                } else {
                    result = date + "天前";
                }
            } else {
                return f2.format(targetDate);
            }
            return result;

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getYear(String dateString) {
        SimpleDateFormat formart = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date d = formart.parse(dateString);
            return d.getYear();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getMonth(String dateString) {
        SimpleDateFormat formart = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date d = formart.parse(dateString);
            return d.getMonth();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getDayOfMonth(String dateString) {
        SimpleDateFormat formart = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date d = formart.parse(dateString);
            return d.getDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getHours(String timeString) {
        SimpleDateFormat formart = new SimpleDateFormat("HH:mm");
        try {
            Date date = formart.parse(timeString);
            return date.getHours();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getMinutes(String timeString) {
        SimpleDateFormat formart = new SimpleDateFormat("HH:mm");
        try {
            Date date = formart.parse(timeString);
            return date.getMinutes();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public static Date parseTime(String time) {
        try {
            return mFullDateFormat.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseTaskTime(String time) {
        SimpleDateFormat formart = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try {
            return formart.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPublishDateString(String date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date d = f.parse(date);
            return mSimpleDateFormat.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Date parseBirthday(String birthday) {
        try {
            Date d = mSimpleDateFormat.parse(birthday);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getLastUpdateTimeDesc(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        String desc = time;
        try {
            Date d = mFullDateFormat.parse(time);
            desc = formatLastUpdateTime(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return desc;
    }

    public static String getLastUpdateTimeDesc(long time) {
        String desc = "";
        try {
            Date d = new Date(time);
            desc = formatLastUpdateTime(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desc;
    }

    public static String getTodayFormat() {
        return mFullDateFormat.format(new Date());
    }

    public static String getDayFormat(long l) {
        String dates;
        SimpleDateFormat ydf = new SimpleDateFormat("yyyyMM");
        String yd = ydf.format(new Date(l));
        String nyd = ydf.format(new Date());
        if (nyd.equals(yd)) {
            SimpleDateFormat ddf = new SimpleDateFormat("dd");
            int dd = Integer.parseInt(ddf.format(new Date(l)));
            Log.i("d", "day:" + dd);
            int nd = Integer.parseInt(ddf.format(new Date()));
            if (dd == nd) {
                dates = "今天";
            } else if ((dd + 1) == nd) {
                dates = "昨天";
            } else {
                dates = "custom";
            }
        } else {
            dates = "custom";
        }
        return dates;
    }

    private static String formatLastUpdateTime(Date d) {
        String desc;
        Date n = new Date();
        long delay = n.getTime() - d.getTime();
        // 相差的秒数
        long delaySeconds = delay / 1000;
        if (delaySeconds < 10) {
            desc = "刚刚";
        } else if (delaySeconds <= 60) {
            desc = delaySeconds + "秒钟前";
        } else if (delaySeconds < secondsOfHour) {
            desc = (delaySeconds / 60) + "分钟前";
        } else if (delaySeconds < secondsOfDay) {
            desc = (delaySeconds / 3600) + "小时前";
        } else if (delaySeconds < secondsOfTwoDay) {
            desc = "一天前";
        } else if (delaySeconds < secondsOfThreeDay) {
            desc = "两天前";
        } else if (n.getYear() == d.getYear()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日");
            desc = dateFormat.format(d);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
            desc = format.format(d);
        }
        return desc;
    }

    public static String compareDate(Calendar calender, long serviceTime, long createTime) {
        calender.setTimeInMillis(serviceTime);
        int year1 = calender.get(Calendar.YEAR);
        int month1 = calender.get(Calendar.MONTH);
        int day1 = calender.get(Calendar.DATE);
        calender.setTimeInMillis(createTime);
        int year2 = calender.get(Calendar.YEAR);
        int month2 = calender.get(Calendar.MONTH);
        int day2 = calender.get(Calendar.DATE);
        if (year1 == year2 && month1 == month2) {
            if (day1 == day2) {
                return "今天";
            } else if (day2 + 1 == day1) {
                return "昨天";
            }
        }
        return null;
    }

    public static String getDate(String unixDate) {
        SimpleDateFormat fm1 = new SimpleDateFormat("yyyyMMddhhmmss");
        SimpleDateFormat fm2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long unixLong = 0;
        String date = "";
        try {
            unixLong = Long.parseLong(unixDate);
        } catch (Exception ex) {
            System.out.println("String转换Long错误，请确认数据可以转换！");
        }
        try {
            long iii = System.currentTimeMillis();
            date = fm1.format(unixLong);
            date = fm2.format(new Date(date));
        } catch (Exception ex) {
            System.out.println("String转换Date错误，请确认数据可以转换！");
        }

        return date;
    }

    public static boolean isBiger(String arg1, String now) {
        int s = arg1.length();
        long old = 0;
        // if(s<13){
        // old=Long.parseLong(arg1)*1000;
        // }
        old = Long.parseLong(arg1);
        long nows = Long.parseLong(now);
        return old > nows ? true : false;
    }

    /**
     * 获取当前时间【yyyyMMddHHmmss】
     *
     * @return String
     * @create 2015年12月2日 下午8:51:59
     */
    public static long getCurrentTimeToLong() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) * 10000000000l + (calendar.get(Calendar.MONTH) + 1) * 100000000l
                + calendar.get(Calendar.DAY_OF_MONTH) * 1000000l + calendar.get(Calendar.HOUR_OF_DAY) * 10000
                + calendar.get(Calendar.MINUTE) * 100 + calendar.get(Calendar.SECOND);
    }

    /*********************************************************************/

    private static SoftReference<Date> date_sof;
    private static SoftReference<SimpleDateFormat> dateFotmate_sof;

    /**
     * 格式化时间
     * 使用示例: String yyyyMMddhhmmss = TimeUtils.transforTime(System.currentTimeMillis(), "yyyy-MM-dd:hh-mm-ss");
     *
     * @param time    时间戳
     * @param formate 需要格式化的样式 eg: yyyy-MM-dd HH:mm:ss
     * @return 转换后的字符串
     */
    public static String transforTime(long time, String formate) {
        Date date = null;
        if (date_sof != null) {
            date = date_sof.get();
        }
        if (date == null) {
            date = new Date();
            date_sof = null;
            date_sof = new SoftReference<>(date);
        }
        date.setTime(time);
        SimpleDateFormat formatter = getFormat();
        formatter.applyPattern(formate);
        return formatter.format(date);
    }

    /**
     * 得到一个时间格式化实例
     */
    public static SimpleDateFormat getFormat() {
        SimpleDateFormat formatter = null;
        if (dateFotmate_sof != null) {
            formatter = dateFotmate_sof.get();
        }
        if (formatter == null) {
            formatter = new SimpleDateFormat();
            dateFotmate_sof = null;
            dateFotmate_sof = new SoftReference<>(formatter);
        }
        return formatter;
    }

    /*********************************************************************/
}
