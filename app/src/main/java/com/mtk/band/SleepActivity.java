package com.mtk.band;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.mtk.adapter.GuidePageAdapter;
import com.mtk.band.bean.SleepInfo;
import com.mtk.band.utils.CalendarTool;
import com.mtk.band.utils.SharedPreferencesUtils;
import com.mtk.band.view.SleepStateView;
import com.mtk.base.BaseActivity;
import com.mtk.util.ChartHelper;
import com.mtk.util.DateUtils;
import com.mtk.util.LogUtils;
import com.ruanan.btnotification.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mtk.band.utils.SharedPreferencesUtils.getTotalSleepsInDay;
import static com.mtk.util.DateUtils.getWeekInt;

public class SleepActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivDayTask;
    private ImageView ivWeekTask;
    private ImageView ivMonthTask;
    private ViewPager viewPagerSportChart;
    private List<View> mList = new ArrayList<>();
    private GuidePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.ll_day).setOnClickListener(this);
        findViewById(R.id.ll_week).setOnClickListener(this);
        findViewById(R.id.ll_month).setOnClickListener(this);

        viewPagerSportChart = (ViewPager) findViewById(R.id.viewPager_sport_chart);

        ivDayTask = (ImageView) findViewById(R.id.iv_day_task);
        ivWeekTask = (ImageView) findViewById(R.id.iv_week_task);
        ivMonthTask = (ImageView) findViewById(R.id.iv_month_task);

        setStepChart("day");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_day:
                ivDayTask.setVisibility(View.VISIBLE);
                ivWeekTask.setVisibility(View.INVISIBLE);
                ivMonthTask.setVisibility(View.INVISIBLE);
                setStepChart("day");
                break;
            case R.id.ll_week:
                ivDayTask.setVisibility(View.INVISIBLE);
                ivWeekTask.setVisibility(View.VISIBLE);
                ivMonthTask.setVisibility(View.INVISIBLE);
                setStepChart("week");
                break;
            case R.id.ll_month:
                ivDayTask.setVisibility(View.INVISIBLE);
                ivWeekTask.setVisibility(View.INVISIBLE);
                ivMonthTask.setVisibility(View.VISIBLE);
                setStepChart("month");
                break;
        }
    }

    private void setStepChart(String tag) {
        initChartData(tag);
        adapter = new GuidePageAdapter(mList);
        viewPagerSportChart.setAdapter(adapter);
        viewPagerSportChart.setCurrentItem(mList.size(), false);
        LogUtils.i("mList.size() = " + mList.size() + ",tag = " + tag);
    }

    protected void initChartData(String tag) {
        mList.clear();
        int pageCount = 3;
        switch (tag) {
            case "day":
                for (int i = pageCount; i >= 0; i--) {
                    View view = getLayoutInflater().inflate(R.layout.layout_day_sleep, null);
                    setDayStepData(view, i);
                    mList.add(view);
                }
                break;
            case "week":
                for (int i = pageCount; i >= 0; i--) {
                    View view = getLayoutInflater().inflate(R.layout.layout_week_sleep, null);
                    setWeekStepData(view, i);
                    mList.add(view);
                }
                break;
            case "month":
                for (int i = pageCount; i >= 0; i--) {
                    View view = getLayoutInflater().inflate(R.layout.layout_month_sleep, null);
                    setMonthStepData(view, i);
                    mList.add(view);
                }
                break;
        }
    }

    /**
     * 设置每日时时计步数据
     */
    private void setDayStepData(View view, int day) {
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
        TextView tv_sub_date = (TextView) view.findViewById(R.id.tv_sub_date);
        TextView tv_sleep_total = (TextView) view.findViewById(R.id.tv_sleep_total);
        TextView tv_deep_sleep = (TextView) view.findViewById(R.id.tv_deep_sleep);
        TextView tv_light_sleep = (TextView) view.findViewById(R.id.tv_light_sleep);
        TextView tv_sleep_latency = (TextView) view.findViewById(R.id.tv_sleep_latency);
        TextView tv_sleep_quality = (TextView) view.findViewById(R.id.tv_sleep_quality);
        SleepStateView sleepStateView = (SleepStateView) view.findViewById(R.id.sleepStateView);
        if (day != 1 && day != 0) {
            tv_date.setText(DateUtils.timeStamp2Date(System.currentTimeMillis() - day * DateUtils.oneDayMillis));
            tv_sub_date.setVisibility(View.GONE);
        } else {
            tv_sub_date.setText(DateUtils.timeStamp2Date(System.currentTimeMillis() - day * DateUtils.oneDayMillis));
            if (day == 0) {
                tv_date.setText("今天");
            }
            if (day == 1) {
                tv_date.setText("昨天");
            }
        }
        SleepInfo sleepInfo = SharedPreferencesUtils.getSleepInfoInDay(context, day);
        LogUtils.i("sleepInfo = " + sleepInfo);
        sleepStateView.setSleepInfo(sleepInfo);
        if (sleepInfo == null) {
            return;
        }
        float count = sleepInfo.getTotalTime();
        float deepSleepTime = sleepInfo.getDeepSleepTime();
        float lightSleepTime = sleepInfo.getLightSleepTime();
        tv_sleep_total.setText("共 " + (int) count + " 时 " + (int) ((count - (int) count) * 60 + 0.5) + " 分");
        tv_deep_sleep.setText("深睡\n" + (int) deepSleepTime + " 时 " + (int) ((deepSleepTime - (int) deepSleepTime) * 60 + 0.5) + " 分");
        tv_light_sleep.setText("浅睡\n" + (int) lightSleepTime + " 时 " + (int) ((lightSleepTime - (int) lightSleepTime) * 60 + 0.5) + " 分");
        tv_sleep_latency.setText("清醒\n" + sleepInfo.getSleepLatencyCount() + " 次");
        tv_sleep_quality.setText("睡眠质量\n" + sleepInfo.getSleepQuality());
    }

    /**
     * 设置一周计步数据
     *
     * @param view View
     * @param week int
     */
    private void setWeekStepData(View view, int week) {
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
        TextView tv_step_count = (TextView) view.findViewById(R.id.tv_step_count);
        TextView date1 = (TextView) view.findViewById(R.id.date1);
        TextView date2 = (TextView) view.findViewById(R.id.date2);
        TextView date3 = (TextView) view.findViewById(R.id.date3);
        TextView date4 = (TextView) view.findViewById(R.id.date4);
        TextView date5 = (TextView) view.findViewById(R.id.date5);
        TextView date6 = (TextView) view.findViewById(R.id.date6);
        TextView date7 = (TextView) view.findViewById(R.id.date7);

        List<String> weekDate = DateUtils.getWeekDate(week);
        tv_date.setText(weekDate.get(6).substring(0, 7));
        date1.setText(weekDate.get(0).split("/")[2]);
        date2.setText(weekDate.get(1).split("/")[2]);
        date3.setText(weekDate.get(2).split("/")[2]);
        date4.setText(weekDate.get(3).split("/")[2]);
        date5.setText(weekDate.get(4).split("/")[2]);
        date6.setText(weekDate.get(5).split("/")[2]);
        date7.setText(weekDate.get(6).split("/")[2]);

        float[] mDataYs = new float[7];
        int k = getWeekInt(new Date().toString());
        LogUtils.i("getTotalStepsInDay k = " + k);
        for (int i = 0; i < mDataYs.length; i++) {
            mDataYs[i] = getTotalSleepsInDay(context, --k, week);
        }
        float count = 0.0f;
        for (Float mDataY : mDataYs) {
            count += mDataY;
        }
        tv_step_count.setText("共 " + (int) count + " 时 " + (int) ((count - (int) count) * 60) + " 分");

        BarChart barchart = (BarChart) view.findViewById(R.id.barchart);
        ChartHelper.setBarChart(barchart);
        ArrayList<String> xAxisLables = new ArrayList<>();
        xAxisLables.add("日");
        xAxisLables.add("一");
        xAxisLables.add("二");
        xAxisLables.add("三");
        xAxisLables.add("四");
        xAxisLables.add("五");
        xAxisLables.add("六");
        ChartHelper.loadBarChartData(barchart, mDataYs, xAxisLables);
    }

    /**
     * 设置一周计步数据
     *
     * @param view  View
     * @param month int
     */
    private void setMonthStepData(View view, int month) {
        TextView tv_year_month = (TextView) view.findViewById(R.id.tv_year_month);
        TextView tv_step_count = (TextView) view.findViewById(R.id.tv_step_count);
        TextView date1 = (TextView) view.findViewById(R.id.date1);
        TextView date2 = (TextView) view.findViewById(R.id.date2);
        TextView date3 = (TextView) view.findViewById(R.id.date3);
        TextView date4 = (TextView) view.findViewById(R.id.date4);
        TextView date5 = (TextView) view.findViewById(R.id.date5);
        TextView date6 = (TextView) view.findViewById(R.id.date6);

        tv_year_month.setText(DateUtils.getMonth(month));
        int curYear = DateUtils.getCurYear();//当前的年份
        int curMonth = DateUtils.getCurMonth();//当前的月份
        int factYear = curYear;//根据month(代表前面多少月)值推算出来的实际年份
        int factMonth;//根据month(代表前面多少月)值推算出来的实际月份
        if (curMonth - month > 0) {
            factMonth = curMonth - month;
        } else {
            factYear = curYear - 1;
            factMonth = 12 + curMonth - month;
        }
        List<String> weekDate = CalendarTool.getDateStr(factYear, factMonth);
        LogUtils.i("month = " + month + ",factYear = " + factYear + ",factMonth = " + factMonth + ",weekDate = " + weekDate);
        date1.setText(weekDate.get(0));
        date2.setText(weekDate.get(1));
        date3.setText(weekDate.get(2));
        date4.setText(weekDate.get(3));
        date5.setText(weekDate.get(4));
        date6.setText(weekDate.get(5));

        List<String[]> weeks = CalendarTool.getWeeksByMonth(factYear, factMonth);

        float[] mDataYs = new float[6];
        for (int i = 0; i < mDataYs.length; i++) {
            mDataYs[i] = SharedPreferencesUtils.getTotalSleepTimeByWeek(context, weeks.get(i));
        }
        float count = 0;//单位是小时
        for (Float mDataY : mDataYs) {
            count += mDataY;
        }
        tv_step_count.setText("共 " + (int) count + " 时 " + (int) ((count - (int) count) * 60 + 0.5) + " 分");

        BarChart barchart = (BarChart) view.findViewById(R.id.barchart);
        ChartHelper.setBarChart(barchart);
        ArrayList<String> xAxisLables = new ArrayList<>();
        xAxisLables.add("一");
        xAxisLables.add("二");
        xAxisLables.add("三");
        xAxisLables.add("四");
        xAxisLables.add("五");
        xAxisLables.add("六");
        ChartHelper.loadBarChartData(barchart, mDataYs, xAxisLables);
    }

}
