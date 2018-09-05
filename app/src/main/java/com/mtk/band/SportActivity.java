package com.mtk.band;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtk.adapter.GuidePageAdapter;
import com.mtk.band.bean.PersonInfo;
import com.mtk.band.bean.SleepInfo;
import com.mtk.band.bean.SportInfo;
import com.mtk.band.utils.SharedPreferencesUtils;
import com.mtk.band.utils.Tools;
import com.mtk.band.view.CustomViewPager;
import com.mtk.base.BaseActivity;
import com.mtk.eventbus.DataUpdateEvent;
import com.mtk.util.DateUtils;
import com.mtk.util.FileUtils;
import com.mtk.util.LogUtils;
import com.ruanan.btnotification.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * 运动页面
 */
public class SportActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivStep, ivSleep;
    private CustomViewPager viewPager;
    List<View> mList;
    private SportInfo sportInfo;
    private TextView steps, stepTarget, distance, energy, sportQuality;
    private boolean isSleep;
    private TextView sleepTime, tvDeepSleep, tvLightSleep, tvSleepLatency;

    public static Intent enterSportActivity(Context context, boolean isSleep) {
        Intent intent = new Intent(context, SportActivity.class);
        intent.putExtra("isSleep", isSleep);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportctivity);

        isSleep = getIntent().getBooleanExtra("isSleep", false);
        ((TextView) findViewById(R.id.main_title)).setText(!isSleep ? "运动计步" : "睡眠监测");
        (ivStep = (ImageView) findViewById(R.id.iv_step)).setSelected(true);
        ivSleep = (ImageView) findViewById(R.id.iv_sleep);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_setting).setOnClickListener(this);
        initData();
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new GuidePageAdapter(mList));
        viewPager.setCurrentItem(isSleep ? 1 : 0, true);
        viewPager.setScanScroll(false);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //mainTitle.setText(position == 0 ? "计步" : "睡眠");
                //ivStep.setSelected(position == 0);
                //ivSleep.setSelected(position == 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {
        mList = new ArrayList<>();
        View stepView = getLayoutInflater().inflate(R.layout.layout_sport_step, null);
        View sleepView = getLayoutInflater().inflate(R.layout.layout_sport_sleep, null);
        mList.add(stepView);
        mList.add(sleepView);
        initStepView(stepView);
        initSleepView(sleepView);
        setStepAndSleepData();
    }

    private void initStepView(View stepView) {
        LinearLayout llStep = (LinearLayout) stepView.findViewById(R.id.ll_step);
        llStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity(StepActivity.class);
            }
        });
        steps = (TextView) stepView.findViewById(R.id.steps);
        Typeface fontFace = Typeface.createFromAsset(getAssets(), "fonts/shishangzhonghei.ttf");
        steps.setTypeface(fontFace);

        stepTarget = (TextView) stepView.findViewById(R.id.step_target);
        distance = (TextView) stepView.findViewById(R.id.distance);
        energy = (TextView) stepView.findViewById(R.id.energy);
        sportQuality = (TextView) stepView.findViewById(R.id.sport_quality);
    }

    private void initSleepView(View sleepView) {
        LinearLayout llStep = (LinearLayout) sleepView.findViewById(R.id.ll_step);
        llStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity(SleepActivity.class);
            }
        });
        sleepTime = (TextView) sleepView.findViewById(R.id.sleep_time);
        Typeface fontFace = Typeface.createFromAsset(getAssets(), "fonts/shishangzhonghei.ttf");
        sleepTime.setTypeface(fontFace);

        tvDeepSleep = (TextView) sleepView.findViewById(R.id.tv_deep_sleep);
        tvLightSleep = (TextView) sleepView.findViewById(R.id.tv_light_sleep);
        tvSleepLatency = (TextView) sleepView.findViewById(R.id.tv_sleep_latency);
    }

    private void refresh() {
        setStepAndSleepData();
    }

    /**
     * 设置计步和睡眠数据
     */
    private void setStepAndSleepData() {
        //-------------------设置运动计步数据-----------------------
        if (sportInfo == null) {
            sportInfo = new SportInfo();
            sportInfo.setSteps(0);
            sportInfo.setDistance("0");
            sportInfo.setCalories("0");
        }
        steps.setText(sportInfo.getSteps() + "");
        distance.setText(sportInfo.getDistance() + "千米");
        energy.setText(sportInfo.getCalories() + "千卡");
        PersonInfo personInfo = SharedPreferencesUtils.getPersonInfo(context);
        stepTarget.setText("目标 " + personInfo.getTarget() + "步");
        //-------------------设置睡眠监测数据-----------------------
        SleepInfo sleepInfo = SharedPreferencesUtils.getSleepInfoInDay(context, 0);
        if (sleepInfo == null) {
            return;
        }
        sleepTime.setText(String.valueOf(sleepInfo.getTotalTime()));
        float deepSleepTime = sleepInfo.getDeepSleepTime();
        tvDeepSleep.setText((int) deepSleepTime + "时" + (int) ((deepSleepTime - (int) deepSleepTime) * 60 + 0.5) + "分");
        float lightSleepTime = sleepInfo.getLightSleepTime();
        tvLightSleep.setText((int) lightSleepTime + "时" + (int) ((lightSleepTime - (int) lightSleepTime) * 60 + 0.5) + "分");
        tvSleepLatency.setText(sleepInfo.getSleepLatencyCount() + "次");
    }

    @Override
    public void onEventMainThread(Object obj) {
        if (obj instanceof DataUpdateEvent) {
            int length = ((DataUpdateEvent) obj).getLength();
            String data = ((DataUpdateEvent) obj).getData().substring(0, length);
            if (!data.contains("GET,")) {
                return;
            }
            parseData(data);
        }
    }

    /**
     * 解析运动计步数据,当在晚上23:00--07:00时间段内,不解析运动,只解析睡眠部分
     *
     * @param data 格式:GET,1,0|1479719440|899|423,1|1479720600|0|5
     */
    private void parseData(String data) {
        LogUtils.i("SportActivity data = " + data);
        String[] split = data.split(",");
        String stepData = split[2];//计步数据
        //如果当前时间在头一天的晚上11点与第二天早上7点之间,则启动睡眠
        if (DateUtils.isCurTimeInRange()) {
            return;
        }
        parseStepData(stepData);
        refresh();
    }

    /**
     * 解析运动计步数据
     *
     * @param stepData 格式:0|1479719440|899|423
     */
    private void parseStepData(String stepData) {
        //运动数据设置
        FileUtils.writeFile(stepData, "step", DateUtils.getDateStr(System.currentTimeMillis(), "yyyyMMddHHmmss") + ".txt");
        String stepStartTime = stepData.split("\\|")[1];//计步开始时间,时间戳,单位是秒
        String buShu = stepData.split("\\|")[3];//步数,这个是累积的步数
        SharedPreferencesUtils.saveStepForEachHour(context, stepStartTime, Integer.parseInt(buShu));
        sportInfo.setSteps(Integer.parseInt(buShu));
        sportInfo.setDistance(Tools.getDistance(parseInt(buShu)));
        sportInfo.setCalories(Tools.getCaloreis(context, parseInt(buShu)));
        LogUtils.i("parseStepData sportInfo = " + sportInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.stepStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_setting:
                enterActivity(SettingActivity.class);
                break;
        }
    }
}
