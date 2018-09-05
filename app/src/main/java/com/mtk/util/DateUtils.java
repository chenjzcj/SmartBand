package com.mtk.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.mtk.util.DateTimeUtil.getFormatDate;


/**
 * Created by MZIA(527633405@qq.com) on 2016/10/28 0028 17:14
 * 安卓中获取日期的工具类：
 * 今天项目中用到自己获取日期，自己写了一个获取时间的工具类，分享出来：
 * 包括获取 1 当前年月日 2 当前是周几  3、根据日期获取是周几 4、获取7天的日期 5、获取当天往后的一周
 */
public class DateUtils {


    private static String mYear; // 当前年
    private static String mMonth; // 月
    private static String mDay;
    private static String mWay;
    public static long oneDayMillis = 24 * 60 * 60 * 1000;//一天的毫秒数


    /**
     * 获取当前日期几月几号
     */
    public static String getDateString() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        if (Integer.parseInt(mDay) > MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), (Integer.parseInt(mMonth)))) {
            mDay = String.valueOf(MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), (Integer.parseInt(mMonth))));
        }
        return mMonth + "月" + mDay + "日";
    }

    /**
     * 获取包含当前日期的从周日到周六的七天日期,假如今天是10月28日,星期五,返回的样式为
     * [2016/10/23,2016/10/24,2016/10/25,2016/10/26,2016/10/27,2016/10/28,2016/10/29]
     *
     * @param week week=0的时候代表当前周,1为上一周,以此类推
     * @return 包含当前日期的从周日到周六的七天日期
     */
    public static List<String> getWeekDate(int week) {
        List<String> result = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis() - week * 7 * oneDayMillis;
        int i = (-1 * getWeekInt(new Date().toString())) + 1;//根据当前周,获取本周周日的数学表示
        for (int j = 0; j < 7; j++) {
            result.add(timeStamp2Date(currentTimeMillis + (i++) * oneDayMillis));
        }
        return result;
    }

    /**
     * 获取包含当前月的从1号到31号日期,假如今天是10月28日,星期五,返回的样式为
     * [25-01,02-08,09-15,16-22,23-29,30-05]
     *
     * @param month month=0的时候代表当前月,1为上一月,以此类推
     * @return 包含当前月日期的所有周
     */
    public static List<String> getMonthDate(int month) {
        List<String> result = new ArrayList<>();
        StringBuilder sb;
        int frontWeek = -2;//相对于当天,前面有几周
        int backWeek = 4;//相对于当前,后面有几周,backWeek-frontWeek=6
        for (int k = frontWeek; k < backWeek; k++) {
            long currentTimeMillis = System.currentTimeMillis() + k * 7 * oneDayMillis;
            int i = (-1 * getWeekInt(new Date().toString())) + 1;//根据当前周,获取本周周日的数学表示
            sb = new StringBuilder();
            for (int j = 0; j < 7; j++) {
                if (j == 0 || j == 6) {
                    sb.append(timeStamp2DateDay(currentTimeMillis + i * oneDayMillis));
                    if (j == 0) {
                        sb.append("-");
                    }
                }
                i++;
            }
            result.add(sb.toString());
        }
        return result;
    }

    /**
     * 将时间戳转化成日期格式
     *
     * @param timeStamp 时间戳
     * @return 日期格式
     */
    public static String timeStamp2Date(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(timeStamp);
    }

    /**
     * 将时间戳转化成日期格式
     *
     * @param timeStamp 时间戳
     * @return 日期格式
     */
    public static String timeStamp2Date1(long timeStamp) {
        //http://blog.csdn.net/thelostxxx/article/details/52584141
        //SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss");//12小时制
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");//24小时制
        return format.format(timeStamp);
    }

    /**
     * 获取给定时间戳的小时
     */
    public static String getHour(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");//24小时制
        return format.format(timeStamp);
    }

    /**
     * 获取给定时间戳的日期
     */
    public static String getDay(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(timeStamp);
    }

    /**
     * 将时间戳转化成日期格式,仅返回日
     *
     * @param timeStamp 时间戳
     * @return 日期格式
     */
    private static String timeStamp2DateDay(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return format.format(timeStamp);
    }

    /**
     * 获取当前月份或者上一月份
     *
     * @param month 当month=0的时候代表当月,为1的时候代表上一个月,以此类推
     * @return String
     */
    public static String getMonth(int month) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -month);
        return getFormatDate(cal.getTime(), "yyyy/MM");
    }

    public static int getCurYear() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        return Integer.valueOf(getFormatDate(cal.getTime(), "yyyy"));
    }

    public static int getCurMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        return Integer.valueOf(getFormatDate(cal.getTime(), "MM"));
    }


    /**
     * 根据当前日期获得是星期几的数字
     *
     * @return 1为周日
     */
    public static int getWeekInt(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.get(Calendar.DAY_OF_WEEK);
    }


    /**
     * 获取今天往后一周的日期（几月几号）
     */
    public static List<String> getSevendate() {
        List<String> dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));


        for (int i = 0; i < 7; i++) {
            mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
            mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
            mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + i);// 获取当前日份的日期号码
            if (Integer.parseInt(mDay) > MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), (i + 1))) {
                mDay = String.valueOf(MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), (i + 1)));
            }
            String date = mMonth + "月" + mDay + "日";
            dates.add(date);
        }
        return dates;
    }


    /**
     * 获取今天往后一周的集合
     */
    public static List<String> get7week() {
        String week;
        List<String> weeksList = new ArrayList<>();
        List<String> dateList = get7date();
        for (String s : dateList) {
            if (s.equals(StringData())) {
                week = "今天";
            } else {
                week = getWeek(s);
            }
            weeksList.add(week);
        }
        return weeksList;
    }

    /**
     * 根据当前日期获得是星期几
     *
     * @return
     */
    public static String getWeek(String time) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "周日";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "周一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "周二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "周三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "周四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "周五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "周六";
        }
        return Week;
    }


    /**
     * 获取从今天开始往后一周的日期
     *
     * @return 从今天开始往后一周的日期列表
     */
    public static List<String> get7date() {
        List<String> dates = new ArrayList<>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        java.text.SimpleDateFormat sim = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String date = sim.format(c.getTime());
        dates.add(date);
        for (int i = 0; i < 6; i++) {
            c.add(java.util.Calendar.DAY_OF_MONTH, 1);
            date = sim.format(c.getTime());
            dates.add(date);
        }
        return dates;
    }

    /**
     * 获取当前年月日
     *
     * @return
     */
    public static String StringData() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        if (Integer.parseInt(mDay) > MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), (Integer.parseInt(mMonth)))) {
            mDay = String.valueOf(MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), (Integer.parseInt(mMonth))));
        }
        return mYear + "-" + (mMonth.length() == 1 ? "0" + mMonth : mMonth) + "-" + (mDay.length() == 1 ? "0" + mDay : mDay);
    }

    /**
     * 得到当年当月的最大日期
     **/
    public static int MaxDayFromDay_OF_MONTH(int year, int month) {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
        time.set(Calendar.MONTH, month - 1);//注意,Calendar对象默认一月为0
        int day = time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
        return day;
    }

    public static void printWeeks() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        int month = calendar.get(Calendar.MONTH);
        int count = 0;
        while (calendar.get(Calendar.MONTH) == month) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                StringBuilder builder = new StringBuilder();
                builder.append("week:");
                builder.append(++count);
                builder.append(" (");
                builder.append(format.format(calendar.getTime()));
                builder.append(" - ");
                calendar.add(Calendar.DATE, 6);
                builder.append(format.format(calendar.getTime()));
                builder.append(")");
                LogUtils.i(builder.toString());
            }
            calendar.add(Calendar.DATE, 1);
        }
    }

    /**
     * 判断当时时间是否在某范围之内
     *
     * @param startTime 范围开始时间 ,格式0730,范围为0000--2359
     * @param endTime   范围结束时间,格式:2310,范围为0000--2359
     * @return true即代表当前时间在指定范围内
     */
    public static boolean isCurTimeInRange() {
        int startTime = 2300;
        int endTime = 700;
        //以下是跨天的情况
        //int startTime = 2300;
        //int endTime = 0700;
        String day = getDateStr(System.currentTimeMillis(), "HHmm");//本天
        int curHour = Integer.parseInt(day);
        LogUtils.i("isCurTimeInRange startTime = " + startTime + ",endTime = " + endTime + ",curHour = " + curHour);
        if (endTime > startTime) {
            //周期范围代表当天
            if (curHour > startTime && curHour < endTime) {
                return true;
            }
        } else {
            //周期范围代表跨天
            if (curHour > startTime || curHour < endTime) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取给定时间戳的日期
     */
    public static String getDateStr(long timeStamp, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(timeStamp);
    }

    /**
     * 通过指定日期获取时间戳
     *
     * @param dateStr 指定日期,格式为yyyyMMddHHmm
     * @return 返回指定日期的时候戳
     */
    public static long getTimestampByDate(String dateStr) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis();
    }
}
