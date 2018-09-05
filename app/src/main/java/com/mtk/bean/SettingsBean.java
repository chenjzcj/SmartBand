package com.mtk.bean;

public class SettingsBean {
    private int imgResId;           //图片资源id
    private int titleResId;         //标题文字资源id
    private int descriptionResId;   //文字描述资源id
    private boolean isTBOpen;       //ToggleButton打开状态
    private boolean hasTB;          //是否有toggleButton

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public void setTitleResId(int titleResId) {
        this.titleResId = titleResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    public void setDescriptionResId(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }

    public boolean isTBOpen() {
        return isTBOpen;
    }

    public void setTBOpen(boolean TBOpen) {
        isTBOpen = TBOpen;
    }

    public boolean isHasTB() {
        return hasTB;
    }

    public void setHasTB(boolean hasTB) {
        this.hasTB = hasTB;
    }
}
