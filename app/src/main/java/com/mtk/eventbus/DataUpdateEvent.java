package com.mtk.eventbus;

/**
 * Created by MZIA(527633405@qq.com) on 2016/9/28 0028 15:20
 * 手环通过蓝牙发过来的数据,通知数据更新
 */
public class DataUpdateEvent {

    private String data;
    private int length;

    public DataUpdateEvent(String data, int length) {
        this.data = data;
        this.length = length;
    }

    public String getData() {
        return data;
    }

    public int getLength() {
        return length;
    }
}
