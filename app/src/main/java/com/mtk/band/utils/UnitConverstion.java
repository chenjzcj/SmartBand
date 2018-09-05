package com.mtk.band.utils;

/**
 * Created by MZIA(527633405@qq.com) on 2016/11/18 0018 11:52
 * 单位转换工具
 */
public class UnitConverstion {
    /**
     * 将千克转换成英磅
     *
     * @param kgValue 千克值
     * @return 英磅
     */
    public static int getPoundByKg(int kgValue) {
        return (int) (2.205f * kgValue + 0.5);
    }

    /**
     * 将英磅转换成千克
     *
     * @param poundValue 英磅值
     * @return 千克
     */
    public static int getKgByPound(int poundValue) {
        return (int) (poundValue / 2.205f + 0.5);
    }

    /**
     * 将厘米转换成英寸
     *
     * @param cmValue 厘米值
     * @return 英寸
     */
    public static int getInchByCm(int cmValue) {
        return (int) (cmValue / 2.54f + 0.5);
    }

    /**
     * 将英寸转换成厘米
     *
     * @param inchValue 英寸值
     * @return 厘米
     */
    public static int getCmByInch(int inchValue) {
        return (int) (inchValue * 2.54f + 0.5);
    }
}
