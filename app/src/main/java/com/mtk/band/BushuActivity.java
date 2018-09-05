package com.mtk.band;

import android.os.Bundle;
import android.view.View;

import com.mtk.base.BaseUIActivity;
import com.ruanan.btnotification.R;

/**
 * 步数
 */
public class BushuActivity extends BaseUIActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mTitleBar.setTitle(getString(R.string.step));
        backListener();
        View view = this.mInflater.inflate(R.layout.activity_bushu, null);
        this.addMainView(view);
    }
}
