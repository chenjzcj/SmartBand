package com.mtk.band.bean;

/**
 * Created by MZIA(527633405@qq.com) on 2016/12/1 0001 13:35
 * 久坐提醒
 */
public class JiuZuo {
    private boolean isOpen;//久坐提醒开关,true为开
    private int mins;//久坐提醒分钟数

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public int getMins() {
        return mins;
    }

    public void setMins(int mins) {
        this.mins = mins;
    }

    @Override
    public String toString() {
        return "JiuZuo{" +
                "isOpen=" + isOpen +
                ", mins=" + mins +
                '}';
    }
}
