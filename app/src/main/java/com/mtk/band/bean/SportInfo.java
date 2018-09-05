package com.mtk.band.bean;

/**
 * Created by MZIA(527633405@qq.com) on 2016/9/20 0020 20:29
 * 戴手环的用户的运动信息
 */
public class SportInfo {
    private int steps;//步数,单位:步
    private String distance;//跑步距离,单位:公里
    private String calories;//消耗卡路里,单位:大卡

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }


    @Override
    public String toString() {
        return "SportInfo{" +
                "steps=" + steps +
                ", distance='" + distance + '\'' +
                ", calories='" + calories + '\'' +
                '}';
    }
}
