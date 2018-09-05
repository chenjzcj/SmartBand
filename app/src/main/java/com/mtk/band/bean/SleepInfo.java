package com.mtk.band.bean;

import java.util.List;

/**
 * Created by MZIA(527633405@qq.com) on 2016/11/22 0022 15:01
 * 睡眠监测信息
 */
public class SleepInfo {
    private String startTime;//睡眠开始时间,如23:00
    private String endTime;//睡眠结束时间,如6:15
    private float totalTime;//睡眠总时间,如7.5,代表7时30分
    private float deepSleepTime;//深睡时间,如2.5,代表2时30分
    private float lightSleepTime;//浅睡时间,如2.5,代表2时30分
    private int sleepLatencyCount;//清醒次数
    private String sleepQuality;//睡眠质量,标准未定,暂时深睡时间与总时间的比值来决定,如果>0.3 优 >0.2 良 <0.2 差
    private List<Integer> sleepState;//睡眠状态,保存32个区间的步数,如果步数<10:深睡 步数<50:浅睡 步数>50:清醒

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(float totalTime) {
        this.totalTime = totalTime;
    }

    public float getDeepSleepTime() {
        return deepSleepTime;
    }

    public void setDeepSleepTime(float deepSleepTime) {
        this.deepSleepTime = deepSleepTime;
    }

    public float getLightSleepTime() {
        return lightSleepTime;
    }

    public void setLightSleepTime(float lightSleepTime) {
        this.lightSleepTime = lightSleepTime;
    }

    public int getSleepLatencyCount() {
        return sleepLatencyCount;
    }

    public void setSleepLatencyCount(int sleepLatencyCount) {
        this.sleepLatencyCount = sleepLatencyCount;
    }

    public String getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(String sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public List<Integer> getSleepState() {
        return sleepState;
    }

    public void setSleepState(List<Integer> sleepState) {
        this.sleepState = sleepState;
    }

    @Override
    public String toString() {
        return "SleepInfo{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", totalTime=" + totalTime +
                ", deepSleepTime=" + deepSleepTime +
                ", lightSleepTime=" + lightSleepTime +
                ", sleepLatencyCount=" + sleepLatencyCount +
                ", sleepQuality='" + sleepQuality + '\'' +
                ", sleepState=" + sleepState +
                '}';
    }
}
