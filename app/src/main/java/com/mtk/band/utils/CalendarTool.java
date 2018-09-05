package com.mtk.band.utils;

import com.mtk.util.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by MZIA(527633405@qq.com) on 2016/11/16 0016 17:27
 * http://jingyan.baidu.com/article/59a015e3ac9813f79588657e.html
 */
public class CalendarTool {

    /**
     * 通过指定的年月获取周
     *
     * @param year  如2016
     * @param month 如11
     * @return 格式为:[31-06, 07-13, 14-20, 21-27, 28-03, 04-10]
     */
    public static List<String> getDateStr(int year, int month) {
        List<String> result = new ArrayList<>();
        List<String> date = getDate(year, month);
        for (String s : date) {
            StringBuilder sb = new StringBuilder();
            String[] split = s.split("\\|");
            sb.append((Integer.valueOf(split[0]) < 10) ? "0" + split[0] : split[0])
                    .append("-")
                    .append((Integer.valueOf(split[6]) < 10) ? "0" + split[6] : split[6]);
            result.add(sb.toString());
        }
        return result;
    }

    /**
     * @param year  某年 格式,如2016
     * @param month 某月 格式,如11,代表获取2016年11月份的周
     * @return 返回格式:[30|31|1|2|3|4|5|, 6|7|8|9|10|11|12, 13|14|15|16|17|18|19,
     * 20|21|22|23|24|25|26, 27|28|29|30|1|2|3|, 4|5|6|7|8|9|10]
     */
    private static List<String> getDate(int year, int month) {
        String currentMonth = year + "" + ((month < 10) ? "0" + month : month);
        String preMonth = (month == 1) ? ((year - 1) + "" + 12)
                : (year + "" + (((month - 1) < 10) ? "0" + (month - 1) : month - 1));
        String nextMonth = (month == 12) ? (year + 1) + "0" + 1
                : year + "" + (((month + 1) < 10) ? "0" + (month + 1) : (month + 1));

        LogUtils.i("getDate preMonth=" + preMonth + ",currentMonth=" + currentMonth + ",nextMonth=" + nextMonth);

        List<String> currentMonthWeeks = getWeeksByMonth(currentMonth);
        List<String> preMonthWeeks = getWeeksByMonth(preMonth);
        List<String> nextMonthWeeks = getWeeksByMonth(nextMonth);

        StringBuilder firstWeek = null;//这个是有可能为null的,因为可能刚好填满

        if (currentMonthWeeks.get(0).contains("@")) {
            firstWeek = new StringBuilder();
            if (preMonthWeeks.get(4).contains("@")) {
                //第五周如果都包含0,那第六周全是0了,
                parseStr("", firstWeek, preMonthWeeks.get(4), false);
                parseStr("", firstWeek, currentMonthWeeks.get(0), false);
            } else if (preMonthWeeks.get(5).contains("@")) {
                //因为当前月第一周没满,那前一周肯定也没满
                parseStr("", firstWeek, preMonthWeeks.get(5), false);
                parseStr("", firstWeek, currentMonthWeeks.get(0), false);
            }
        }

        StringBuilder lastPreWeek = null;//倒数第二周,即第五周,这个不一定存在
        StringBuilder lastWeek = new StringBuilder();//最后一周,即第六周,可能全部在其他月份

        if (currentMonthWeeks.get(4).contains("@")) {//这个时候才有lastPreWeek与lastWeek
            lastPreWeek = new StringBuilder();
            parseStr("", lastPreWeek, currentMonthWeeks.get(4), false);
            parseStr("", lastPreWeek, nextMonthWeeks.get(0), false);
            parseStr("", lastWeek, nextMonthWeeks.get(1), false);
        } else if (currentMonthWeeks.get(5).contains("@")) {//这个时候只有lastWeek
            parseStr("", lastWeek, currentMonthWeeks.get(5), false);
            parseStr("", lastWeek, nextMonthWeeks.get(0), false);
        }
        LogUtils.i("preMonthWeeks=" + preMonthWeeks + ",currentMonthWeeks=" + currentMonthWeeks);
        LogUtils.i("nextMonthWeeks=" + nextMonthWeeks + ",firstWeek=" + firstWeek);
        LogUtils.i("lastPreWeek=" + lastPreWeek + ",lastWeek=" + lastWeek);

        List<String> newCurrentMonthWeeks = new ArrayList<>();
        for (int i = 0; i < currentMonthWeeks.size(); i++) {
            if (firstWeek == null) {//那么必定lastPreWeek是存在的
                if (i == 4) {
                    newCurrentMonthWeeks.add(lastPreWeek.toString());
                } else if (i == 5) {
                    newCurrentMonthWeeks.add(lastWeek.toString());
                } else {
                    StringBuilder sb = new StringBuilder();
                    parseStr("", sb, currentMonthWeeks.get(i), false);
                    newCurrentMonthWeeks.add(sb.toString());

                }
            } else {//那么必定lastPreWeek是有可能不存在,也有可能存在
                if (lastPreWeek == null) {
                    if (i == 0) {
                        newCurrentMonthWeeks.add(firstWeek.toString());
                    } else if (i == 5) {
                        newCurrentMonthWeeks.add(lastWeek.toString());
                    } else {
                        StringBuilder sb = new StringBuilder();
                        parseStr("", sb, currentMonthWeeks.get(i), false);
                        newCurrentMonthWeeks.add(sb.toString());
                    }
                } else {
                    if (i == 0) {
                        newCurrentMonthWeeks.add(firstWeek.toString());
                    } else if (i == 4) {
                        newCurrentMonthWeeks.add(lastPreWeek.toString());
                    } else if (i == 5) {
                        newCurrentMonthWeeks.add(lastWeek.toString());
                    } else {
                        StringBuilder sb = new StringBuilder();
                        parseStr("", sb, currentMonthWeeks.get(i), false);
                        newCurrentMonthWeeks.add(sb.toString());
                    }
                }
            }
        }

        LogUtils.i("newCurrentMonthWeeks=" + newCurrentMonthWeeks);
        return newCurrentMonthWeeks;
    }

    /**
     * 获取指定月份内的周数
     *
     * @param yearMonth 格式如:201611
     * @return 格式为:[@|@|@|@|1|2|3, 4|5|6|7|8|9|10, 11|12|13|14|15|16|17,
     * 18|19|20|21|22|23|24, 25|26|27|28|29|30|31, @|@|@|@|@|@|@]
     */
    private static List<String> getWeeksByMonth(String yearMonth) {
        List<String> weeks = new ArrayList<>();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
        Date date = null;
        try {
            date = sf.parse(yearMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); //获取当月天数
        int[][] array = new int[6][7];
        for (int i = 0; i <= day_month - 1; i++) {//循环遍历每天
            calendar.set(Calendar.DAY_OF_MONTH, i + 1);
            int week_month = calendar.get(Calendar.WEEK_OF_MONTH);//获取该天在本月的第几个星期，也就是第几行
            int now_day_month = calendar.get(Calendar.DAY_OF_WEEK);//获取该天在本星期的第几天  ，也就是第几列
            array[week_month - 1][now_day_month - 1] = i + 1;//将改天存放到二位数组中
        }

        for (int i = 0; i <= array.length - 1; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j <= array[i].length - 1; j++) {
                String day = (array[i][j] == 0 ? "@" : array[i][j] + "");
                sb.append(day);
                if ((j + 1) % 7 != 0) {
                    sb.append("|");
                }
            }
            weeks.add(sb.toString());
        }
        LogUtils.i("getWeeksByMonth month = " + yearMonth + ",weeks" + weeks);
        return weeks;
    }

    /**
     * 获取的数据格式为:
     * List<String[]> weeks = new ArrayList<>();
     * weeks.add(new String[]{"20161030", "20161031", "20161101", "20161102", "20161103", "20161104", "20161105"});
     * weeks.add(new String[]{"20161106", "20161107", "20161108", "20161109", "20161110", "20161111", "20161112"});
     * weeks.add(new String[]{"20161113", "20161114", "20161115", "20161116", "20161117", "20161118", "20161119"});
     * weeks.add(new String[]{"20161120", "20161121", "20161122", "20161123", "20161124", "20161125", "20161126"});
     * weeks.add(new String[]{"20161127", "20161128", "20161129", "20161130", "20161201", "20161202", "20161203"});
     * weeks.add(new String[]{"20161204", "20161205", "20161206", "20161207", "20161208", "20161209", "20161210"});
     *
     * @param year  年,如2016
     * @param month 月,如11
     * @return 数据格式如上所示
     */
    public static List<String[]> getWeeksByMonth(int year, int month) {
        return getDateFull(year, month);
    }

    /**
     * @param year  某年 格式,如2016
     * @param month 某月 格式,如11,代表获取2016年11月份的周
     * @return 返回格式:[30|31|1|2|3|4|5|, 6|7|8|9|10|11|12, 13|14|15|16|17|18|19,
     * 20|21|22|23|24|25|26, 27|28|29|30|1|2|3|, 4|5|6|7|8|9|10]
     */
    private static List<String[]> getDateFull(int year, int month) {
        String currentYearMonth = year + "" + ((month < 10) ? "0" + month : month);
        String preYearMonth = (month == 1) ? ((year - 1) + "" + 12)
                : (year + "" + (((month - 1) < 10) ? "0" + (month - 1) : month - 1));
        String nextYearMonth = (month == 12) ? (year + 1) + "0" + 1
                : year + "" + (((month + 1) < 10) ? "0" + (month + 1) : (month + 1));

        LogUtils.i("getDateFull preMonth=" + preYearMonth + ",currentMonth=" + currentYearMonth + ",nextMonth=" + nextYearMonth);

        List<String> currentMonthWeeks = getWeeksByMonth(currentYearMonth);
        List<String> preMonthWeeks = getWeeksByMonth(preYearMonth);
        List<String> nextMonthWeeks = getWeeksByMonth(nextYearMonth);

        StringBuilder firstWeek = null;//这个是有可能为null的,因为可能刚好填满

        if (currentMonthWeeks.get(0).contains("@")) {
            firstWeek = new StringBuilder();
            if (preMonthWeeks.get(4).contains("@")) {
                //第五周如果都包含0,那第六周全是0了,
                parseStr(preYearMonth, firstWeek, preMonthWeeks.get(4), true);
            } else if (preMonthWeeks.get(5).contains("@")) {
                //因为当前月第一周没满,那前一周肯定也没满
                parseStr(preYearMonth, firstWeek, preMonthWeeks.get(5), true);
            }
            parseStr(currentYearMonth, firstWeek, currentMonthWeeks.get(0), true);
        }

        StringBuilder lastPreWeek = null;//倒数第二周,即第五周,这个不一定存在
        StringBuilder lastWeek = new StringBuilder();//最后一周,即第六周,可能全部在其他月份

        if (currentMonthWeeks.get(4).contains("@")) {//这个时候才有lastPreWeek与lastWeek,大部分是这种情况
            lastPreWeek = new StringBuilder();
            parseStr(currentYearMonth, lastPreWeek, currentMonthWeeks.get(4), true);
            parseStr(nextYearMonth, lastPreWeek, nextMonthWeeks.get(0), true);
            parseStr(nextYearMonth, lastWeek, nextMonthWeeks.get(1), true);
        } else if (currentMonthWeeks.get(5).contains("@")) {//这个时候只有lastWeek
            parseStr(currentYearMonth, lastWeek, currentMonthWeeks.get(5), true);
            parseStr(nextYearMonth, lastWeek, nextMonthWeeks.get(0), true);
        }

        List<String> newCurrentMonthWeeks = new ArrayList<>();
        for (int i = 0; i < currentMonthWeeks.size(); i++) {
            if (firstWeek == null) {//那么必定lastPreWeek是存在的
                if (i == 4) {
                    newCurrentMonthWeeks.add(lastPreWeek.toString());
                } else if (i == 5) {
                    newCurrentMonthWeeks.add(lastWeek.toString());
                } else {
                    StringBuilder sb = new StringBuilder();
                    parseStr(currentYearMonth, sb, currentMonthWeeks.get(i), true);
                    newCurrentMonthWeeks.add(sb.toString());
                }
            } else {//那么必定lastPreWeek是有可能不存在,也有可能存在
                if (lastPreWeek == null) {
                    if (i == 0) {
                        newCurrentMonthWeeks.add(firstWeek.toString());
                    } else if (i == 5) {
                        newCurrentMonthWeeks.add(lastWeek.toString());
                    } else {
                        StringBuilder sb = new StringBuilder();
                        parseStr(currentYearMonth, sb, currentMonthWeeks.get(i), true);
                        newCurrentMonthWeeks.add(sb.toString());
                    }
                } else {
                    if (i == 0) {
                        newCurrentMonthWeeks.add(firstWeek.toString());
                    } else if (i == 4) {
                        newCurrentMonthWeeks.add(lastPreWeek.toString());
                    } else if (i == 5) {
                        newCurrentMonthWeeks.add(lastWeek.toString());
                    } else {
                        StringBuilder sb = new StringBuilder();
                        parseStr(currentYearMonth, sb, currentMonthWeeks.get(i), true);
                        newCurrentMonthWeeks.add(sb.toString());
                    }
                }
            }
        }
        List<String[]> result = new ArrayList<>();
        for (String s : newCurrentMonthWeeks) {
            String[] strArray = new String[7];
            String[] split = s.split("\\|");
            for (int i = 0; i < 7; i++) {
                strArray[i] = split[i];
            }
            result.add(strArray);
        }
        return result;
    }

    /**
     * 解析字符串数据,格式为25|26|27|28|29|30|1
     *
     * @param prefix 前缀,格式为:201611
     * @param sb     StringBuilder
     * @param s      格式 : 25|26|27|28|29|30|1
     * @param isAdd0 如果为true,则在不满10的数字前补0
     */
    private static void parseStr(String prefix, StringBuilder sb, String s, boolean isAdd0) {
        String[] split = s.split("\\|");
        for (String ss : split) {
            if (!ss.equals("@")) {
                int i = Integer.parseInt(ss);
                sb.append(isAdd0 ? prefix + (i < 10 ? "0" + i : i) : ss).append("|");
            }
        }
    }
}
