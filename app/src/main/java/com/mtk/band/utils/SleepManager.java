package com.mtk.band.utils;

/**
 * Created by MZIA(527633405@qq.com) on 2016/11/25 0025 09:59
 * 睡眠监测管理类
 */
public class SleepManager {
    public static int deepSleepLowerLimit = 0;//深睡下限
    public static int deepSleepUpperLimit = 5;//深睡上限

    public static int lightSleepLowerLimit = 5;//浅睡下限
    public static int lightSleepUpperLimit = 50;//浅睡上限

    public static int SLEEPSTATE_ALIVE = 0;//清醒状态
    public static int SLEEPSTATE_LIGHT_ONE = 1;//浅睡一期状态
    public static int SLEEPSTATE_DEEP = 2;//深睡状态
    public static int SLEEPSTATE_DEEP_TWO = 3;//浅睡二期状态
    public static int SLEEPSTATE_NOT_WEARING = 4;//未佩戴状态
}
