package com.mtk.band;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mtk.band.utils.Tools;
import com.mtk.base.BaseUIActivity;
import com.mtk.service.MainService;
import com.ruanan.btnotification.R;

/**
 * 睡眠
 */
public class ShuimainActivity extends BaseUIActivity {

    private EditText etAlarm;
    private TextView tvInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mTitleBar.setTitle(getString(R.string.shuim));
        backListener();
        View view = this.mInflater.inflate(R.layout.activity_shuimain, null);
        this.addMainView(view);
        initView(view);
    }

    private void initView(View view) {
        etAlarm = (EditText) view.findViewById(R.id.et_alarm);
        tvInfo = (TextView) view.findViewById(R.id.tv_info);
    }

    public void setAlarm(View view) {
        byte[] data = etAlarm.getText().toString().getBytes();
        MainService.getInstance().sendCAPCData(data);
        tvInfo.setText(etAlarm.getText().toString());
    }

    public void setAlarm1(View view) {
        byte[] data = Tools.PHONE2BAND.getBytes();
        MainService.getInstance().sendCAPCData(data);
        tvInfo.setText(Tools.PHONE2BAND);
    }
}
