package com.mtk.band;

import android.os.Bundle;
import android.view.View;

import com.mtk.base.BaseUIActivity;
import com.ruanan.btnotification.R;

/**
 * 卡路里页面
 */
public class CaloriesActivity extends BaseUIActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mTitleBar.setTitle(getString(R.string.calorie));
        backListener();
        View view = this.mInflater.inflate(R.layout.activity_calories, null);
        this.addMainView(view);
    }
}
