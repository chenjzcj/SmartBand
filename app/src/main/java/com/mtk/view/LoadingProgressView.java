package com.mtk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ruanan.btnotification.R;


/**
 * 加载进度控件
 *
 * @author admin 2015-9-9
 */
public class LoadingProgressView extends FrameLayout {
    protected View loadingMotion;
    protected TextView loadingTips;
    protected Animation animation;

    public LoadingProgressView(Context context) {
        this(context, null);
    }

    public LoadingProgressView(Context context, AttributeSet set) {
        super(context, set);
        initView(context);
    }

    protected void initView(Context context) {
        LayoutInflater.from(context).inflate(
                R.layout.layout_online_loading_progress, (ViewGroup) this);
        loadingMotion = this.findViewById(R.id.loading_motion);
        loadingTips = (TextView) this.findViewById(R.id.loading_tips);
        animation = AnimationUtils.loadAnimation(context, R.anim.rotate_anim);
    }

    public void showLoading(String text) {
        if (text != null) {
            loadingTips.setText((CharSequence) text);
        }
        animation.reset();
        loadingMotion.clearAnimation();
        loadingMotion.startAnimation(animation);
        this.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        animation.reset();
        loadingMotion.clearAnimation();
        this.setVisibility(View.GONE);
    }
}