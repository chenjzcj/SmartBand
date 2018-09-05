package com.mtk.band;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mtk.band.adapter.NewAlarmAdapter;
import com.mtk.band.bean.Alarm;
import com.mtk.band.utils.SharedPreferencesUtils;
import com.mtk.band.view.wheel.TosGallery;
import com.mtk.band.view.wheel.WheelTextView;
import com.mtk.band.view.wheel.WheelView;
import com.mtk.base.BaseActivity;
import com.mtk.util.ScreenUtils;
import com.ruanan.btnotification.R;

import java.util.List;


public class AlarmActivity extends BaseActivity implements View.OnClickListener {

    private ListView lvAlarmlist;
    private NewAlarmAdapter alarmAdapter;
    private AdapterView.OnItemClickListener alarmOnItemClickListener;
    private Button ib_mon, ib_tue, ib_wed, ib_thur, ib_fri, ib_sat, ib_sun;
    WheelView mHours, mMins;
    NumberAdapter hourAdapter, minAdapter;
    private LinearLayout llEditAlarmView;
    private List<Alarm> alarms;

    private int clickIndex;
    private String[] hoursArray = {"00", "01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
            "18", "19", "20", "21", "22", "23"};
    private String[] minsArray = {"00", "01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
            "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
            "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        initView();
    }

    protected void initView() {
        lvAlarmlist = (ListView) findViewById(R.id.lv_alarmlist);
        alarms = SharedPreferencesUtils.getAlarms(this);
        alarmAdapter = new NewAlarmAdapter(context, alarms, this);
        lvAlarmlist.setAdapter(alarmAdapter);
        alarmOnItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showllEditAlarmView(alarms.get(position));
                clickIndex = position;
            }
        };
        lvAlarmlist.setOnItemClickListener(alarmOnItemClickListener);

        llEditAlarmView = (LinearLayout) findViewById(R.id.ll_edit_alarm_view);
        findViewById(R.id.btn_back).setOnClickListener(this);
        (ib_mon = (Button) findViewById(R.id.ib_mon)).setOnClickListener(this);
        (ib_tue = (Button) findViewById(R.id.ib_tue)).setOnClickListener(this);
        (ib_wed = (Button) findViewById(R.id.ib_wed)).setOnClickListener(this);
        (ib_thur = (Button) findViewById(R.id.ib_thur)).setOnClickListener(this);
        (ib_fri = (Button) findViewById(R.id.ib_fri)).setOnClickListener(this);
        (ib_sat = (Button) findViewById(R.id.ib_sat)).setOnClickListener(this);
        (ib_sun = (Button) findViewById(R.id.ib_sun)).setOnClickListener(this);

        (mHours = (WheelView) findViewById(R.id.alarm_hour)).setScrollCycle(true);
        (mMins = (WheelView) findViewById(R.id.alarm_min)).setScrollCycle(true);

        hourAdapter = new NumberAdapter(hoursArray);
        minAdapter = new NumberAdapter(minsArray);

        mHours.setAdapter(hourAdapter);
        mMins.setAdapter(minAdapter);

        findViewById(R.id.iv_cancel).setOnClickListener(this);
        findViewById(R.id.iv_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.ib_mon:
            case R.id.ib_tue:
            case R.id.ib_wed:
            case R.id.ib_thur:
            case R.id.ib_fri:
            case R.id.ib_sat:
            case R.id.ib_sun:
                v.setSelected(!v.isSelected());
                break;
            case R.id.iv_cancel:
                hidellEditAlarmView(true);
                break;
            case R.id.iv_ok:
                //提交数据
                int hour = mHours.getSelectedItemPosition();
                int min = mMins.getSelectedItemPosition();
                int time = hour * 100 + min;
                int sun = ib_sun.isSelected() ? 0x1 << 6 : 0x0;
                int mon = ib_mon.isSelected() ? 0x1 << 5 : 0x0;
                int tue = ib_tue.isSelected() ? 0x1 << 4 : 0x0;
                int wed = ib_wed.isSelected() ? 0x1 << 3 : 0x0;
                int thur = ib_thur.isSelected() ? 0x1 << 2 : 0x0;
                int fri = ib_fri.isSelected() ? 0x1 << 1 : 0x0;
                int sat = ib_sat.isSelected() ? 0x1 : 0x0;
                int weekDay = sun + mon + tue + wed + thur + fri + sat;
                int bell = 0;
                Alarm alarm = new Alarm();
                alarm.setEnable(true);
                alarm.setWeekDay(weekDay);
                alarm.setTime(time);
                alarm.setBell(bell);
                SharedPreferencesUtils.saveAlarms(this, clickIndex == 0 ? alarm : null,
                        clickIndex == 1 ? alarm : null, clickIndex == 2 ? alarm : null);
                hidellEditAlarmView(true);
                refresh();
                break;
        }
    }

    private void refresh() {
        alarms = SharedPreferencesUtils.getAlarms(this);
        alarmAdapter.setAlarms(alarms);
        alarmAdapter.notifyDataSetChanged();
    }

    private void showllEditAlarmView(Alarm alarm) {
        if (alarm == null) {
            alarm = new Alarm();
            alarm.setBell(0);
            alarm.setEnable(true);
            alarm.setTime(0);
            alarm.setWeekDay(0);
        }

        //设置闹钟星期
        int weekDay = alarm.getWeekDay();
        String weekDayStr = Integer.toBinaryString(weekDay);
        StringBuilder sb = new StringBuilder();
        //补全前面
        for (int i = 0; i < 8 - weekDayStr.length(); i++) {
            sb.append("0");
        }
        sb.append(weekDayStr);
        //二进制：第1位=星期日、第2位=星期一、第3位=星期二、第4位=星期三、第5位=星期四、第6位=星期五、第7位=星期六
        ib_sun.setSelected(sb.substring(1, 2).equals("1"));
        ib_mon.setSelected(sb.substring(2, 3).equals("1"));
        ib_tue.setSelected(sb.substring(3, 4).equals("1"));
        ib_wed.setSelected(sb.substring(4, 5).equals("1"));
        ib_thur.setSelected(sb.substring(5, 6).equals("1"));
        ib_fri.setSelected(sb.substring(6, 7).equals("1"));
        ib_sat.setSelected(sb.substring(7, 8).equals("1"));

        int time = alarm.getTime();
        int hour = time / 100 % 100;
        int min = time % 100;
        mHours.setSelection(hour, true);
        mMins.setSelection(min, true);

        lvAlarmlist.setOnItemClickListener(null);
        llEditAlarmView.setVisibility(View.VISIBLE);
        llEditAlarmView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.in));
    }

    private void hidellEditAlarmView(boolean needAnim) {
        lvAlarmlist.setOnItemClickListener(alarmOnItemClickListener);
        llEditAlarmView.setVisibility(View.GONE);
        if (needAnim)
            llEditAlarmView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.out));
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

    public void modify(Alarm alarm, int id, boolean on) {
        alarm.setEnable(on);
        SharedPreferencesUtils.saveAlarms(this, id == 0 ? alarm : null,
                id == 1 ? alarm : null, id == 2 ? alarm : null);
        refresh();
    }
}
