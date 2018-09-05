package com.mtk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.mtk.base.BaseActivity;
import com.ruanan.btnotification.R;

/**
 * 应用启动页面
 */
public class SplashActivity extends BaseActivity {
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_splash);
        ViewGroup container = (ViewGroup) findViewById(R.id.splash_container);
        // 渐变展示启动屏
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        alphaAnimation.setDuration(2000);
        container.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                //startActivity(new Intent(context, com.mtk.band.MainActivity.class));
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });
    }

    @Override
    public void onBackPressed() {
    }
}
