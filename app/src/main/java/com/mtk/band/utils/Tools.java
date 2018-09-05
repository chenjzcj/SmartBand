package com.mtk.band.utils;

import android.content.Context;

import com.mtk.service.MainService;
import com.mtk.util.LogUtils;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by MZIA(527633405@qq.com) on 2016/9/28 0028 11:47
 */
public class Tools {

    public static Random random = new Random();

    /**
     * 模拟手环通过蓝牙传输给app的数据
     */
    public static final String BAND2PHONE = "GET,1,0|2016-11-03 17:23:38|5|0,1|2016-11-04 00:00:00|0|0";
    /**
     * 模拟app通过蓝牙传输给手环的数据(设置闹钟等)
     */
    public static final String PHONE2BAND = "SET,0,17:33|1111111|1,17:34|1111111|1,17:35|1111111|1";

    public static byte[] str2Byte(String str) {
        return str.getBytes();
    }

    public static String genSimulatedData() {
        StringBuilder sb = new StringBuilder("GET,1,");
        sb.append("0|").append(com.mtk.util.TimeUtils.getCurrentTimeString()).append("|");
        sb.append(bushu()).append("|").append(bushu()).append(",1|").append(com.mtk.util.TimeUtils.getCurrentTimeString());
        sb.append("|").append(bushu()).append("|").append(bushu());
        return sb.toString();
    }

    private static int bushu() {
        return random.nextInt(5000);
    }

    /**
     * 根据运动的距离与时间计算消耗的卡路里,默认体重55kg
     *
     * @param context 上下文
     * @param buShu   运动步数:步
     * @return 消耗的卡路里:大卡
     */
    public static String getCaloreis(Context context, int buShu) {
        int weight = SharedPreferencesUtils.getPersonInfo(context).getWeight();
        weight = (weight == 0 ? 55 : weight);
        return doblue2Str(4.5 * (0.6 * buShu * 0.001) * ((weight * 1.0) / 3600));
    }

    /**
     * 根据步数获取里程数.里程=步幅*步数*0.001,此处默认步幅为60cm
     *
     * @param buShu 步数
     * @return 距离:公里
     */
    public static String getDistance(int buShu) {
        return doblue2Str(0.6 * buShu * 0.001);
    }

    /**
     * 根据睡眠开始时间计算睡眠时间.时间=时+分/60+秒/3600,单位:小时
     *
     * @param sleepTime 睡眠持续时间,格式为:564,单位:秒
     * @return 睡眠时间:小时
     */
    public static String getSleepTime(String sleepTime) {
        return doblue2Str((Integer.valueOf(sleepTime) / 60.0) / 60.0);
    }

    /**
     * 将double类型的数据转成保留两位小数点的字符串
     *
     * @param d double
     * @return String
     */
    private static String doblue2Str(double d) {
        DecimalFormat df = new DecimalFormat("0.00"); //设置double类型小数点后位数格式
        return df.format(d);
    }


    /**
     * 5、计步器省电优化方案
     * 1.智能机端屏亮，且当前智能手环APP处于active状态时，发送指令12345，手环端解析指令启动计步器并传输数据；
     * 2.智能机端屏灭，或智能机端屏亮但手环APP处于非active状态时，发送指令12121，手环端解析指令停止传输数据(此时计步器仍在计数)；
     * 3.智能机端退出APP时，发送指令54321，手环端解析指令并停止计步器功能运行(此时计步器不再计数)；
     */
    private static final String STEP_START = "12345";
    private static final String STEP_PAUSE = "12121";
    private static final String STEP_STOP = "54321";

    /**
     * 启动计步器并传输数据
     */
    public static void stepStart() {
        LogUtils.i("stepStart");
        if (MainService.getInstance() != null) {
            MainService.getInstance().sendCAPCData(Tools.STEP_START.getBytes());
        }
    }

    /**
     * 停止传输数据,但计步器还是开启状态
     */
    public static void stepPause() {
        LogUtils.i("stepPause");
        if (MainService.getInstance() != null) {
            MainService.getInstance().sendCAPCData(Tools.STEP_PAUSE.getBytes());
        }
    }

    /**
     * 停止计步器
     */
    public static void stepStop() {
        LogUtils.i("stepStop");
        if (MainService.getInstance() != null) {
            MainService.getInstance().sendCAPCData(Tools.STEP_STOP.getBytes());
        }
    }
}
