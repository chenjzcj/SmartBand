package com.mtk.band;

import android.os.Bundle;
import android.view.View;

import com.mtk.base.BaseUIActivity;
import com.ruanan.btnotification.R;

public class JuliActivity extends BaseUIActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mTitleBar.setTitle(getString(R.string.juli));
        backListener();
        View view = this.mInflater.inflate(R.layout.activity_juli, null);
        this.addMainView(view);
    }
}
