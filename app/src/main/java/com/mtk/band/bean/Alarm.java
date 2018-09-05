package com.mtk.band.bean;

/**
 * 作者 : 527633405@qq.com
 * 时间 : 2015/12/9 0009
 * 闹铃
 */
public class Alarm {
    private boolean enable; // 是否启用[default = false]
    private int weekDay; // 提醒日期，二进制：第1位=星期一、第2位=星期二、第3位=星期三、第4位=星期四、第5位=星期五、第6位=星期六、第7位=星期日、第8位=每天、0=一次
    private int time; // 提醒时间 HHmm
    private int bell; // 铃声

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getBell() {
        return bell;
    }

    public void setBell(int bell) {
        this.bell = bell;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "enable=" + enable +
                ", weekDay=" + weekDay +
                ", time=" + time +
                ", bell=" + bell +
                '}';
    }
}
