package com.mtk.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者 : 527633405@qq.com
 * 时间 : 2015/12/15 0015
 * 引导页面适配器
 */
public class GuidePageAdapter extends PagerAdapter {
    private List<View> mList;

    public GuidePageAdapter(List<View> list) {
        if (list == null) {
            mList = new ArrayList<>();
        }
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(mList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        view.addView(mList.get(position));
        return mList.get(position);
    }

}
