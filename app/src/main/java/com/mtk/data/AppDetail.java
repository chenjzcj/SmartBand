package com.mtk.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppDetail {

    // 应用游戏ID
    public int id;
    // 标题
    private String title;
    // 图标
    private String icon;
    // 包名
    private String name;
    // 简介
    private String content;
    // 下载次数
    private long downcount;
    // 安装包大小(单位:KB)
    private long size;
    // 安装包地址
    private String apkfile;
    // 截图地图, key = url
    private List<HashMap<String, String>> images; 
    // 送话费时间（单位：分）不送则为0
    private int integral;
    // 广告轮播图
    private String advertimage;
    //  当前下载size
    private long currloadsize;

    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDowncount() {
        return downcount;
    }

    public void setDowncount(long downcount) {
        this.downcount = downcount;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getApkfile() {
        return apkfile;
    }

    public void setApkfile(String apkfile) {
        this.apkfile = apkfile;
    }

    public List<HashMap<String, String>> getImages() {
        return images;
    }

    public void setImages(ArrayList<HashMap<String, String>> images) {
        this.images = images;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getAdvertimage() {
        return advertimage;
    }

    public void setAdvertimage(String advertimage) {
        this.advertimage = advertimage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCurrloadsize() {
        return currloadsize;
    }

    public void setCurrloadsize(long currloadsize) {
        this.currloadsize = currloadsize;
    }

    public void setImages(List<HashMap<String, String>> images) {
        this.images = images;
    }
    
}
