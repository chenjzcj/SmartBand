package com.mtk.band;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.mtk.band.bean.JiuZuo;
import com.mtk.band.utils.SharedPreferencesUtils;
import com.mtk.band.view.wheel.TosGallery;
import com.mtk.band.view.wheel.WheelTextView;
import com.mtk.band.view.wheel.WheelView;
import com.mtk.base.BaseActivity;
import com.mtk.util.ScreenUtils;
import com.mtk.view.togglebutton.ToggleButton;
import com.ruanan.btnotification.R;

/**
 * 久坐提醒
 */
public class SedentaryReminderActivity extends BaseActivity implements View.OnClickListener {

    private ToggleButton tooggleSedentaryReminder;
    private JiuZuo jiuZuo;
    private WheelView jiuzuoMins;
    String[] minsArray;
    private RelativeLayout rlMins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sedentary_reminder);
        findViewById(R.id.rl_sedentary_reminder).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        (jiuzuoMins = (WheelView) findViewById(R.id.jiuzuo_mins)).setScrollCycle(true);
        tooggleSedentaryReminder = (ToggleButton) findViewById(R.id.tooggle_sedentary_reminder);
        rlMins = (RelativeLayout) findViewById(R.id.rl_mins);
        tooggleSedentaryReminder.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                SharedPreferencesUtils.saveJiuZuoInfo(context, on, -1);
                jiuZuo = SharedPreferencesUtils.getJiuZuoInfo(context);
                if (jiuZuo.isOpen()) {
                    tooggleSedentaryReminder.setToggleOn();
                } else {
                    tooggleSedentaryReminder.setToggleOff();
                }
            }
        });
        initData();
        jiuzuoMins.setAdapter(new NumberAdapter(minsArray));
    }

    @Override
    protected void initData() {
        super.initData();
        jiuZuo = SharedPreferencesUtils.getJiuZuoInfo(this);
        if (jiuZuo.isOpen()) {
            tooggleSedentaryReminder.setToggleOn();
        } else {
            tooggleSedentaryReminder.setToggleOff();
        }
        minsArray = new String[241];
        for (int i = 0; i < minsArray.length; i++) {
            minsArray[i] = i + "";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_sedentary_reminder:
                int visibility = rlMins.getVisibility();
                if (visibility == View.GONE) {
                    showRlMins();
                } else {
                    hideRlMins();
                }
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_cancel:
                hideRlMins();
                break;
            case R.id.btn_ok:
                hideRlMins();
                int selectedItemPosition = jiuzuoMins.getSelectedItemPosition();
                SharedPreferencesUtils.saveJiuZuoInfo(this, jiuZuo.isOpen(), selectedItemPosition);
                initData();
                break;
        }
    }

    private void showRlMins() {
        jiuzuoMins.setSelection(jiuZuo.getMins(), true);
        rlMins.setVisibility(View.VISIBLE);
        rlMins.startAnimation(AnimationUtils.loadAnimation(context, R.anim.in));
    }

    private void hideRlMins() {
        rlMins.setVisibility(View.GONE);
        rlMins.startAnimation(AnimationUtils.loadAnimation(context, R.anim.out));
    }


    private class NumberAdapter extends BaseAdapter {
        int mHeight = 50;
        String[] mData = null;

        public NumberAdapter(String[] data) {
            mHeight = ScreenUtils.getScreenHeight(context) / 10;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return (null != mData) ? mData.length : 0;
        }

        @Override
        public Object getItem(int arg0) {
            return mData[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WheelTextView textView = null;
            if (null == convertView) {
                convertView = new WheelTextView(context);
                convertView.setLayoutParams(new TosGallery.LayoutParams(-1,
                        mHeight));
                textView = (WheelTextView) convertView;
                textView.setTextSize(25);
                textView.setGravity(Gravity.CENTER);
            }
            String text = mData[position];
            if (null == textView) {
                textView = (WheelTextView) convertView;
            }
            textView.setText(text);
            return convertView;
        }
    }
}
