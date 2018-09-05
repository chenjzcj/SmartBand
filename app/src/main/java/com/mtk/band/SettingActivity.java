package com.mtk.band;

import android.os.Bundle;
import android.view.View;

import com.mtk.base.BaseActivity;
import com.ruanan.btnotification.R;

/**
 * 手环相关数据设置
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.rl_personinfo).setOnClickListener(this);
        findViewById(R.id.rl_alarm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.rl_personinfo:
                enterActivity(PersonActivity.class);
                break;
            case R.id.rl_alarm:
                enterActivity(AlarmActivity.class);
                break;
        }
    }
}
