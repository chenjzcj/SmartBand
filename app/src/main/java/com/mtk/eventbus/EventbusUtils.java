package com.mtk.eventbus;

import de.greenrobot.event.EventBus;

/**
 * Created by MZIA(527633405@qq.com) on 2016/9/28 0028 15:12
 * eventbus事件总线工具类
 */
public class EventbusUtils {
    /**
     * 注册
     *
     * @param obj Object
     */
    public static void register(Object obj) {
        EventBus.getDefault().register(obj);
    }

    /**
     * 注销
     *
     * @param obj Object
     */
    public static void unregister(Object obj) {
        EventBus.getDefault().unregister(obj);
    }

    /**
     * 发送事件
     *
     * @param obj Object
     */
    public static void postEvent(Object obj) {
        EventBus.getDefault().post(obj);
    }
}
