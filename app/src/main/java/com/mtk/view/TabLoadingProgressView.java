package com.mtk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.ruanan.btnotification.R;


/**
 * tab切换的时候的加载进度条
 *
 * @author admin 2015-9-9
 */

public class TabLoadingProgressView extends LoadingProgressView {

    public TabLoadingProgressView(Context context, AttributeSet set) {
        super(context, set);
    }

    @Override
    protected void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_online_loading_progress, this);
        this.loadingMotion = this.findViewById(R.id.loading_motion);
        this.loadingTips = (TextView) this.findViewById(R.id.loading_tips);
        this.animation = AnimationUtils.loadAnimation(context,
                R.anim.rotate_anim);
    }

}