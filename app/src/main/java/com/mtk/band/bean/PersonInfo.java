package com.mtk.band.bean;

/**
 * Created by MZIA(527633405@qq.com) on 2016/11/18 0018 10:35
 * 个人信息
 */
public class PersonInfo {
    private int sex;//性别,1代表男,0代表女
    private int age;//年龄
    private int height;//身高,保存单位为厘米
    private int weight;//体重,保存单位为公斤
    private int unit;//单位,1代表英制,0代表公制
    private int target;//运动目标,单位:步

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
