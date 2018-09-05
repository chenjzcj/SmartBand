package com.mtk.band.view;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by MZIA(527633405@qq.com) on 2016/11/18 0018 15:02
 * 可以禁止滑动的
 * http://blog.csdn.net/hero_zhouwu/article/details/9071355
 */
public class CustomViewPager extends ViewPager {

    private boolean isCanScroll = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }


    @Override
    public void scrollTo(int x, int y) {
        if (isCanScroll) {
            super.scrollTo(x, y);
        }
    }
}

