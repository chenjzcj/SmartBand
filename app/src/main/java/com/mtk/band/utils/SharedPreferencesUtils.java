package com.mtk.band.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mtk.band.bean.Alarm;
import com.mtk.band.bean.JiuZuo;
import com.mtk.band.bean.PersonInfo;
import com.mtk.band.bean.SleepInfo;
import com.mtk.service.MainService;
import com.mtk.util.DateUtils;
import com.mtk.util.LogUtils;
import com.mtk.util.NumUtil;
import com.mtk.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MZIA(527633405@qq.com) on 2016/11/3 0003 17:20
 */
public class SharedPreferencesUtils {

    private static String NAME = "BandData";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存每一天每一个小时的步数
     *
     * @param context Context
     * @param key     key,格式为2016110511,代表2016年11月05日上午11点走的步数
     * @param step    步数
     */
    private static void saveStep(Context context, String key, float step) {
        getSharedPreferences(context).edit().putFloat(key, step).apply();
    }

    /**
     * 返回指定日期,指定时间的步数
     *
     * @param context Context
     * @param key     key,格式为2016-11-03-01,代表2016年11月03日凌晨1点走的步数
     * @return 指定时间的步数
     */
    private static float getStep(Context context, String key) {
        return getSharedPreferences(context).getFloat(key, 0.00f);
    }

    /**
     * 保存每一天每一个小时的步数
     * 通过计步开始时间与当前时间进行比较,如果当前时间
     *
     * @param context       Context
     * @param stepStartTime 计步开始时间,格式为20161103010000,代表2016年11月03日凌晨1点走的步数
     * @param step          步数
     */
    public static void saveStepForEachHour(Context context, String stepStartTime, float step) {
        String startHour = DateUtils.getHour(Long.valueOf(stepStartTime) * 1000);//开始的时间
        String curHour = DateUtils.getHour(System.currentTimeMillis());//当前小时
        int k = 0;//当前小时与开始小时相比,相差小时数
        for (int i = 0; i < 24; i++) {
            //前面i个小时
            if (startHour.equals(DateUtils.getHour(System.currentTimeMillis() - i * 60 * 60 * 1000))) {
                k = i;
                LogUtils.i("k = " + k);
                break;
            }
        }
        float countStep = step;
        for (int i = k; i > 0; i--) {
            String hour = DateUtils.getHour(System.currentTimeMillis() - i * 60 * 60 * 1000);
            float step1 = getStep(context, hour);
            step -= step1;
            LogUtils.i("hour = " + hour + ",step1 = " + step1);
        }
        LogUtils.i("startHour = " + startHour + ",curHour = " + curHour
                + ",countStep = " + countStep + ",step = " + step);
        saveStep(context, curHour, step);
    }

    /**
     * 获取一天中每一个小时的数据
     *
     * @param context Context
     * @param day     day=0代表当天,1为前一天,2为上前天,以此类推
     */
    public static List<Float> getStepsInDay(Context context, int day) {
        String dayKey = DateUtils.getDay(System.currentTimeMillis() - day * DateUtils.oneDayMillis);
        List<Float> steps = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String key = dayKey + (i < 10 ? "0" + i : i);
            float step = getStep(context, key);
            steps.add(step);
        }
        LogUtils.i("dayKey = " + dayKey + ",steps = " + steps);
        return steps;
    }

    /**
     * 获取一天中总的计步数据
     *
     * @param context Context
     * @param day     day=0代表当天,1为前一天,2为上前天,以此类推,-1代表后一天
     * @param week    week=0代表当周,1为前一周,2为上周天,以此类推,-1代表后一周
     */
    public static float getTotalStepsInDay(Context context, int day, int week) {
        String day1 = DateUtils.getDay((System.currentTimeMillis() - week * 7 * DateUtils.oneDayMillis) - day * DateUtils.oneDayMillis);
        LogUtils.i("getTotalStepsInDay day1 = " + day1 + ",day = " + day);
        return getStepsInDay(context, day1);
    }

    public static float getDataByWeek(Context context, String[] week) {
        float totalSteps = 0;
        for (String key : week) {
            totalSteps += getStepsInDay(context, key);
            LogUtils.i("getDataByWeek week=" + key);
        }
        return totalSteps;
    }

    /**
     * 获取一天的步数,通过日期,如20161106
     *
     * @param context Context
     * @param day     格式为:20161106
     * @return 一天的步数
     */
    private static float getStepsInDay(Context context, String day) {
        float totalSteps = 0;
        for (int i = 0; i < 24; i++) {
            String key = day + (i < 10 ? "0" + i : i);
            totalSteps += getStep(context, key);
        }
        return totalSteps;
    }


    //------------------------------------------个人信息本地存储---------------------------------------------

    /**
     * 获取保存在本地的信息
     *
     * @param context 上下文
     * @return 返回PersonInfo实体
     */
    public static PersonInfo getPersonInfo(Context context) {
        String personInfo = getSharedPreferences(context).getString("PersonInfo", "0|0|0|0|0|0");
        PersonInfo person = new PersonInfo();
        String[] split = personInfo.split("\\|");
        person.setSex(Integer.parseInt(split[0]));
        person.setAge(Integer.parseInt(split[1]));
        person.setHeight(Integer.parseInt(split[2]));
        person.setWeight(Integer.parseInt(split[3]));
        person.setUnit(Integer.parseInt(split[4]));
        person.setTarget(Integer.parseInt(split[5]));
        return person;
    }

    /**
     * 保存个人信息在本地
     * 如果只想设置或者修改其中某一项,可以将其他数据设置为null
     *
     * @param context 上下文
     * @return 保存的格式为:0|0|0|0|0|0,分别表示sex,age,height,weight,unit,target
     */
    public static void savePersonInfo(Context context, String sex, String age, String height, String weight, String unit, String target) {
        PersonInfo personInfo = getPersonInfo(context);
        if (sex == null) {
            sex = personInfo.getSex() + "";
        }
        if (age == null) {
            age = personInfo.getAge() + "";
        }
        if (height == null) {
            height = personInfo.getHeight() + "";
        }
        if (weight == null) {
            weight = personInfo.getWeight() + "";
        }
        if (unit == null) {
            unit = personInfo.getUnit() + "";
        }
        if (target == null) {
            target = personInfo.getTarget() + "";
        }
        getSharedPreferences(context).edit().putString("PersonInfo", sex + "|" + age + "|" +
                height + "|" + weight + "|" + unit + "|" + target).apply();
    }

    //------------------------------------------智能闹钟本地存储---------------------------------------------

    /**
     * 保存的数据格式为0|0|0000|0,第一个代表开关,1为开,0为关,第二个代表提醒日期,十进制数,第三个代表提醒时间,第四个代表铃声
     *
     * @param context Context
     * @return 三个闹钟
     */
    public static List<Alarm> getAlarms(Context context) {
        List<Alarm> result = new ArrayList<>();
        String alarms = getSharedPreferences(context).getString("Alarms", "0|0|0000|0,0|0000|0|0,0|0|0000|0");
        LogUtils.i("aaaaaaaaaaaaa alarms =" + alarms);
        String[] split = alarms.split(",");
        for (String s : split) {
            String[] split1 = s.split("\\|");
            Alarm alarm = new Alarm();
            alarm.setEnable(split1[0].equals("1"));
            alarm.setWeekDay(Integer.parseInt(split1[1]));
            alarm.setTime(Integer.parseInt(split1[2]));
            alarm.setBell(Integer.parseInt(split1[3]));
            result.add(alarm);
        }
        return result;
    }

    /**
     * 保存闹钟数据
     *
     * @param context 上下文
     * @param alarm1  第一个闹钟
     * @param alarm2  第二个闹钟
     * @param alarm3  第三个闹钟
     */
    public static void saveAlarms(Context context, Alarm alarm1, Alarm alarm2, Alarm alarm3) {
        boolean btConnected = MainService.mBluetoothManager.isBTConnected();
        if (btConnected) {
            ToastUtils.showLong("设置成功!");
        } else {
            ToastUtils.showLong("设置失败,没有连接手环!");
            return;
        }

        List<Alarm> alarms = getAlarms(context);
        List<Alarm> newAlarms = new ArrayList<>();
        alarm1 = (alarm1 == null) ? alarms.get(0) : alarm1;
        alarm2 = (alarm2 == null) ? alarms.get(1) : alarm2;
        alarm3 = (alarm3 == null) ? alarms.get(2) : alarm3;
        newAlarms.add(alarm1);
        newAlarms.add(alarm2);
        newAlarms.add(alarm3);
        StringBuilder sb = new StringBuilder();
        for (Alarm alarm : newAlarms) {
            boolean enable = alarm.isEnable();
            int weekDay = alarm.getWeekDay();
            int time = alarm.getTime();
            int bell = alarm.getBell();
            sb.append(enable ? "1" : "0").append("|").append(weekDay)
                    .append("|").append(time).append("|").append(bell).append(",");
        }

        StringBuilder bandAlarmData = new StringBuilder("SET,0,");
        for (Alarm alarm : newAlarms) {
            int time = alarm.getTime();
            String timeStr = NumUtil.get2StrLenNum(time / 100 % 100) + ":" + NumUtil.get2StrLenNum(time % 100);

            int weekDay = alarm.getWeekDay();
            String weekDayStr = Integer.toBinaryString(weekDay);
            StringBuilder weekDaySB = new StringBuilder();
            //补全前面
            for (int i = 0; i < 8 - weekDayStr.length(); i++) {
                weekDaySB.append("0");
            }
            weekDaySB.append(weekDayStr);

            boolean enable = alarm.isEnable();
            bandAlarmData.append(timeStr).append("|")
                    .append(weekDaySB.substring(1)).append("|")
                    .append(enable ? "1" : "0").append(",");
        }
        bandAlarmData.deleteCharAt(bandAlarmData.length() - 1);//删除最后一个逗号
        setBandAlarm(bandAlarmData.toString());
        getSharedPreferences(context).edit().putString("Alarms", sb.toString()).apply();
    }

    /**
     * 设置手环闹钟,要确定蓝牙是连接上的,否则设置无效
     *
     * @param alarmStr 格式为 "SET,0,17:33|1111111|1,17:34|1111111|1,17:35|1111111|1"
     */
    private static void setBandAlarm(String alarmStr) {
        LogUtils.i("alarmStr = " + alarmStr);
        byte[] data = alarmStr.getBytes();
        MainService.getInstance().sendCAPCData(data);
    }

    //--------------------------------------睡眠监测数据本地存储----------------------------------------------
    public static void saveSleepStep(Context context, int step) {
        String dayKey = DateUtils.getDay(System.currentTimeMillis());
        String hh = DateUtils.getDateStr(System.currentTimeMillis(), "HH");//当前小时
        String hourMin = DateUtils.getDateStr(System.currentTimeMillis(), "HHmm");
        if (Integer.parseInt(hh) >= 23) {//必须得加上=号
            //将前一天的保存成当天的
            dayKey = DateUtils.getDay(System.currentTimeMillis() + DateUtils.oneDayMillis);
        }
        String key = "sleepStep" + dayKey;//格式 sleepStep2016112201
        int hM = Integer.parseInt(hourMin);
        if (hM >= 2300 && hM < 2315) {
            key = key + "01";
        } else if (hM >= 2315 && hM < 2330) {
            key = key + "02";
        } else if (hM >= 2330 && hM < 2345) {
            key = key + "03";
        } else if (hM >= 2345 && hM < 2359) {
            key = key + "04";
        } else if (hM >= 0 && hM < 15) {
            key = key + "05";
        } else if (hM >= 15 && hM < 30) {
            key = key + "06";
        } else if (hM >= 30 && hM < 45) {
            key = key + "07";
        } else if (hM >= 45 && hM < 100) {
            key = key + "08";
        } else if (hM >= 100 && hM < 115) {
            key = key + "09";
        } else if (hM >= 115 && hM < 130) {
            key = key + "10";
        } else if (hM >= 130 && hM < 145) {
            key = key + "11";
        } else if (hM >= 145 && hM < 200) {
            key = key + "12";
        } else if (hM >= 200 && hM < 215) {
            key = key + "13";
        } else if (hM >= 215 && hM < 230) {
            key = key + "14";
        } else if (hM >= 230 && hM < 245) {
            key = key + "15";
        } else if (hM >= 245 && hM < 300) {
            key = key + "16";
        } else if (hM >= 300 && hM < 315) {
            key = key + "17";
        } else if (hM >= 315 && hM < 330) {
            key = key + "18";
        } else if (hM >= 330 && hM < 345) {
            key = key + "19";
        } else if (hM >= 345 && hM < 400) {
            key = key + "20";
        } else if (hM >= 400 && hM < 415) {
            key = key + "21";
        } else if (hM >= 415 && hM < 430) {
            key = key + "22";
        } else if (hM >= 430 && hM < 445) {
            key = key + "23";
        } else if (hM >= 445 && hM < 500) {
            key = key + "24";
        } else if (hM >= 500 && hM < 515) {
            key = key + "25";
        } else if (hM >= 515 && hM < 530) {
            key = key + "26";
        } else if (hM >= 530 && hM < 545) {
            key = key + "27";
        } else if (hM >= 545 && hM < 600) {
            key = key + "28";
        } else if (hM >= 600 && hM < 615) {
            key = key + "29";
        } else if (hM >= 615 && hM < 630) {
            key = key + "30";
        } else if (hM >= 630 && hM < 645) {
            key = key + "31";
        } else if (hM >= 645 && hM < 700) {
            key = key + "32";
        }
        getSharedPreferences(context).edit().putInt(key, step).apply();
    }

    /**
     * 获取一天中总的睡眠数据
     *
     * @param context Context
     * @param day     day=0代表当天,1为前一天,2为上前天,以此类推,-1代表后一天
     * @param week    week=0代表当周,1为前一周,2为上周天,以此类推,-1代表后一周
     */
    public static float getTotalSleepsInDay(Context context, int day, int week) {
        String dayKey = DateUtils.getDay((System.currentTimeMillis() - week * 7 * DateUtils.oneDayMillis) - day * DateUtils.oneDayMillis);
        LogUtils.i("getTotalSleepsInDay dayKey = " + dayKey + ",day = " + day);
        SleepInfo sleepInfoByDayKey = getSleepInfoByDayKey(context, dayKey);
        if (sleepInfoByDayKey == null) {
            return 0f;
        } else {
            return sleepInfoByDayKey.getTotalTime();
        }
    }

    public static float getTotalSleepTimeByWeek(Context context, String[] week) {
        float totalSteps = 0.0f;
        for (String key : week) {
            SleepInfo sleepInfoByDayKey = getSleepInfoByDayKey(context, key);
            totalSteps += (sleepInfoByDayKey == null ? 0.0f : sleepInfoByDayKey.getTotalTime());
            LogUtils.i("getDataByWeek week=" + key);
        }
        return totalSteps;
    }

    /**
     * 获取某一天的睡眠状态
     *
     * @param context Context
     * @param day     day=0代表当天,1为前一天,2为上前天,以此类推
     * @return SleepInfo
     */
    public static SleepInfo getSleepInfoInDay(Context context, int day) {
        String dayKey = DateUtils.getDay(System.currentTimeMillis() - day * DateUtils.oneDayMillis);
        return getSleepInfoByDayKey(context, dayKey);
    }

    /**
     * 获取某一天的睡眠状态
     *
     * @param context Context
     * @param dayKey  格式:yyyyMMdd
     * @return SleepInfo
     */
    private static SleepInfo getSleepInfoByDayKey(Context context, String dayKey) {
        List<Integer> sleepStates = new ArrayList<>();
        int noDataCount = 0;//记录出现-1的次数,即没有数据的次数,如果全为-1,则不存在数据
        for (int i = 1; i < 33; i++) {
            String key = "sleepStep" + dayKey + (i < 10 ? "0" + i : i);//格式:sleepStep2016112201
            int step = getSharedPreferences(context).getInt(key, -1);
            //step = new Random().nextInt(3);//模拟测试数据
            LogUtils.i("getSleepInfoByDayKey key = " + key + ",step = " + step);
            sleepStates.add(step);
            if (step == -1) noDataCount++;
        }
        //如果32个段全为-1,则不存在睡眠监测数据
        if (noDataCount == 32) {
            return null;
        }
        String startTime = getStartTime(sleepStates);//睡眠开始时间
        String endTime = getEndTime(sleepStates);//睡眠结束时间
        float totalTime = getTotalTime(sleepStates);//睡眠总时间
        float deepSleepTime = getSleepTime(sleepStates, SleepManager.SLEEPSTATE_DEEP,
                SleepManager.SLEEPSTATE_DEEP);
        float lightSleepTime = getSleepTime(sleepStates, SleepManager.SLEEPSTATE_LIGHT_ONE,
                SleepManager.SLEEPSTATE_DEEP_TWO);
        float qualityRatio = deepSleepTime / totalTime;
        //睡眠质量,如果>0.3 优 >0.2 良 <0.2 差
        String sleepQuality = (qualityRatio > 0.3f) ? "优" : (qualityRatio > 0.2 ? "良" : "差");

        SleepInfo sleepInfo = new SleepInfo();
        sleepInfo.setStartTime(startTime);
        sleepInfo.setEndTime(endTime);
        sleepInfo.setTotalTime(totalTime);
        sleepInfo.setDeepSleepTime(deepSleepTime);
        sleepInfo.setLightSleepTime(lightSleepTime);
        sleepInfo.setSleepQuality(sleepQuality);
        sleepInfo.setSleepState(sleepStates);
        setNewSleepState(sleepInfo);
        sleepInfo.setSleepLatencyCount(getLatencyCount(sleepInfo.getSleepState()));
        return sleepInfo;
    }

    /**
     * 去掉sleepInfo.getSleepState()前后的无效值,包括清醒状态与无数据-1状态
     */
    private static void setNewSleepState(SleepInfo sleepInfo) {
        if (sleepInfo == null) {
            return;
        }
        List<Integer> sleepState = sleepInfo.getSleepState();
        int startIndex = 0;
        int endIndex = 31;
        if (sleepState != null) {
            for (int i = 0; i < sleepState.size(); i++) {
                Integer state = sleepState.get(i);
                if (state == SleepManager.SLEEPSTATE_LIGHT_ONE ||
                        state == SleepManager.SLEEPSTATE_DEEP ||
                        state == SleepManager.SLEEPSTATE_DEEP_TWO) {
                    startIndex = i;
                    break;
                }

            }
            for (int i = sleepState.size() - 1; i >= 0; i--) {
                Integer state = sleepState.get(i);
                if (state == SleepManager.SLEEPSTATE_LIGHT_ONE ||
                        state == SleepManager.SLEEPSTATE_DEEP ||
                        state == SleepManager.SLEEPSTATE_DEEP_TWO) {
                    endIndex = i;
                    break;
                }
            }
            //public List<E> subList(int start, int end)方法包含头,不包含尾
            sleepInfo.setSleepState(sleepState.subList(startIndex, endIndex + 1));
        }
    }

    /**
     * 获取睡眠开始时间
     *
     * @param sleepStates 32个时间节点的步数
     * @return 23:15
     */
    private static String getStartTime(List<Integer> sleepStates) {
        String startTime = "23:00";
        int startIndex = 0;
        if (sleepStates != null) {
            for (int i = 0; i < sleepStates.size(); i++) {
                Integer state = sleepStates.get(i);
                if (state == SleepManager.SLEEPSTATE_LIGHT_ONE ||
                        state == SleepManager.SLEEPSTATE_DEEP ||
                        state == SleepManager.SLEEPSTATE_DEEP_TWO) {
                    startIndex = i;
                    break;
                }
            }
            if (startIndex < 4 && startIndex > 0) {
                startTime = "23:" + startIndex * 15;
            } else if (startIndex == 4) {
                startTime = "00:00";
            } else if (startIndex > 4) {
                startTime = (startIndex - 4) * 15 / 60 + ":" + (startIndex - 4) * 15 % 60;
            }
        }
        return startTime;
    }

    /**
     * 获取睡眠结束时间
     *
     * @param sleepStates 32个时间节点的步数
     * @return 23:15
     */
    private static String getEndTime(List<Integer> sleepStates) {
        String endTime = "07:00";
        int endIndex = 0;
        if (sleepStates != null) {
            for (int i = sleepStates.size() - 1; i >= 0; i--) {
                Integer state = sleepStates.get(i);
                if (state == SleepManager.SLEEPSTATE_LIGHT_ONE ||
                        state == SleepManager.SLEEPSTATE_DEEP ||
                        state == SleepManager.SLEEPSTATE_DEEP_TWO) {
                    endIndex = i;
                    break;
                }
            }
            int i = 420 - (sleepStates.size() - 1 - endIndex) * 15;
            int hour = i / 60;
            int min = i % 60;
            endTime = (hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min);
        }
        return endTime;
    }

    /**
     * 获取睡眠总时间,去掉前面与后面的清醒状态
     *
     * @param sleepStates 32个时间节点的步数
     * @return 如:6.5
     */
    private static float getTotalTime(List<Integer> sleepStates) {
        float totalTime = 0f;
        int startIndex = 0;
        int endIndex = 31;
        if (sleepStates != null) {
            for (int i = 0; i < sleepStates.size(); i++) {
                Integer state = sleepStates.get(i);
                if (state == SleepManager.SLEEPSTATE_LIGHT_ONE ||
                        state == SleepManager.SLEEPSTATE_DEEP ||
                        state == SleepManager.SLEEPSTATE_DEEP_TWO) {
                    startIndex = i;
                    break;
                }
            }
            for (int i = sleepStates.size() - 1; i >= 0; i--) {
                Integer state = sleepStates.get(i);
                if (state == SleepManager.SLEEPSTATE_LIGHT_ONE ||
                        state == SleepManager.SLEEPSTATE_DEEP ||
                        state == SleepManager.SLEEPSTATE_DEEP_TWO) {
                    endIndex = i;
                    break;
                }
            }
            totalTime = ((endIndex - startIndex + 1) * 15.0f) / 60.0f;
        }
        return totalTime;
    }

    /**
     * 根据特定时间段监测数据,获取深/浅睡时间
     *
     * @param sleepStates 32个时间节点的步数
     * @param sleepState1 睡眠状态值1
     * @param sleepState2 睡眠状态值2
     * @return 返回, 如2.3, 代表2小时18分钟
     */
    private static float getSleepTime(List<Integer> sleepStates, int sleepState1, int sleepState2) {
        float sleepTime = 0f;
        int sleepCount = 0;
        if (sleepStates != null) {
            for (int sleepState : sleepStates) {
                if (sleepState == sleepState1 || sleepState == sleepState2) {
                    sleepCount++;
                }
            }
            sleepTime = sleepCount * 15.0f / 60.0f;
        }
        return sleepTime;
    }

    /**
     * 获取清醒次数
     *
     * @param sleepStates 32个时间节点的步数
     * @return 次数
     */
    private static int getLatencyCount(List<Integer> sleepStates) {
        int latencyCount = 0;
        if (sleepStates != null) {
            for (int i = 0; i < sleepStates.size(); i++) {
                if (sleepStates.get(i) == SleepManager.SLEEPSTATE_ALIVE) {
                    //过滤掉连在一起的清醒状态
                    if (((i + 1) < sleepStates.size()) && sleepStates.get(i + 1) == SleepManager.SLEEPSTATE_ALIVE) {
                        continue;
                    }
                    latencyCount++;
                }
            }
        }
        return latencyCount;
    }
    //--------------------------------------久坐提醒数据本地存储----------------------------------------------

    /**
     * 保存久坐提醒数据
     *
     * @param context 上下文
     * @param isOpen  是否开启
     * @param mins    多少分钟提醒一次,-1的时候不改变
     */
    public static void saveJiuZuoInfo(Context context, boolean isOpen, int mins) {
        boolean btConnected = MainService.mBluetoothManager.isBTConnected();
        if (btConnected) {
            ToastUtils.showLong("设置成功!");
        } else {
            ToastUtils.showLong("设置失败,没有连接手环!");
            return;
        }
        JiuZuo jiuZuoInfo = getJiuZuoInfo(context);
        if (mins == -1) {
            mins = jiuZuoInfo.getMins();
        }
        String jiuzuo = (isOpen ? "1" : "0") + "|" + mins;
        LogUtils.i("getJiuZuoInfo save = " + jiuzuo);
        getSharedPreferences(context).edit().putString("JIUZUO", jiuzuo).apply();

        String jiuZuoData = "SET,2," + mins + "|" + (isOpen ? "1" : "0");
        LogUtils.i("jiuZuoData = " + jiuZuoData);
        MainService.getInstance().sendCAPCData(jiuZuoData.getBytes());
    }

    /**
     * 获取久坐提醒信息
     *
     * @param context 上下文
     * @return 久坐提醒信息, 格式为1|300,代表开,300分钟,默认180分钟
     */
    public static JiuZuo getJiuZuoInfo(Context context) {
        String[] jiuzuo = getSharedPreferences(context).getString("JIUZUO", "0|180").split("\\|");
        JiuZuo jiuZuo = new JiuZuo();
        jiuZuo.setOpen(jiuzuo[0].equals("1"));
        jiuZuo.setMins(Integer.parseInt(jiuzuo[1]));
        LogUtils.i("getJiuZuoInfo get = " + jiuZuo);
        return jiuZuo;
    }

}
