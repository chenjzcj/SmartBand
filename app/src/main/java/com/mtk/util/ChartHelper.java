package com.mtk.util;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.mtk.band.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MZIA(527633405@qq.com) on 2016/11/8 0008 15:45
 */
public class ChartHelper {

    /**
     * 设置柱形图的样式
     *
     * @param chart BarChart
     */
    public static void setBarChart(BarChart chart) {
        chart.setDescription("");
        chart.setDrawGridBackground(false);//设置网格背景
        chart.setScaleEnabled(false);//设置缩放
        chart.setDoubleTapToZoomEnabled(false);//设置双击不进行缩放
        chart.setDrawValueAboveBar(true);//值是否在柱形的上面
        chart.setDrawBarShadow(false);
        chart.setDrawHighlightArrow(false);
        //设置X轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        //xAxis.setTypeface(mTf); // 设置字体
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(true);//是否画出x轴的线
        xAxis.setDrawGridLines(false);//是否显示x轴的网格线

        // 查看setLimitLinesBehindData()方法，true或false的效果图
        //LimitLine xLimitLine = new LimitLine(2f,"is Behind");
        //xLimitLine.setLineColor(Color.BLUE);
        //xLimitLine.setTextColor(Color.BLUE);
        //xAxis.addLimitLine(xLimitLine);
        //xAxis.setDrawLimitLinesBehindData(true);
        //xAxis.setDrawLimitLinesBehindData(false);

        //获得左侧侧坐标轴
        YAxis leftAxis = chart.getAxisLeft();
        //leftAxis.setTypeface(mTf);
        //leftAxis.setAxisLineWidth(1.5f);
        //leftAxis.setSpaceTop(100f);

        leftAxis.setLabelCount(5, false);
        //leftAxis.setShowOnlyMinMax(true);

        //设置右侧坐标轴
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawAxisLine(false); // 右侧坐标轴线
        rightAxis.setDrawLabels(false); // 右侧坐标轴数组Label
        //rightAxis.setTypeface(mTf);
        //rightAxis.setLabelCount(5);
        //rightAxis.setDrawGridLines(false);
    }

    /**
     * 加载并设置柱形图的数据
     *
     * @param chart       BarChart
     * @param mDataYs     int[] x轴对应的y轴数值
     * @param xAxisLables ArrayList<String> x轴的标签集合
     */
    public static void loadBarChartData(BarChart chart, float[] mDataYs, ArrayList<String> xAxisLables) {
        //所有数据点的集合
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < xAxisLables.size(); i++) {
            entries.add(new BarEntry(mDataYs[i], i));
        }
        //柱形数据的集合
        BarDataSet mBarDataSet = new BarDataSet(entries, "Color");
        mBarDataSet.setBarSpacePercent(20f);
        mBarDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);//设置每条柱子的颜色
        mBarDataSet.setHighLightAlpha(255);//设置点击后高亮颜色透明度
        mBarDataSet.setHighLightColor(Color.BLUE);

        //BarData表示整个柱形图的数据
        BarData mBarData = new BarData(xAxisLables, mBarDataSet);
        chart.setData(mBarData);

        // 设置动画
        //chart.animateX(2000);
        //chart.animateY(2000);
        chart.animateXY(1000, 1000);
        //chart.animateY(8000, Easing.EasingOption.EaseInSine);
    }

    // 设置chart显示的样式
    public static void setChartStyle(LineChart mLineChart, LineData lineData, int color) {
        // 是否在折线图上添加边框
        mLineChart.setDrawBorders(false);
        // 数据描述
        mLineChart.setDescription("");
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        mLineChart.setNoDataTextDescription("如果传给MPAndroidChart的数据为空，那么你将看到这段文字。@Zhang Phil");
        // 是否绘制背景颜色。
        // 如果mLineChart.setDrawGridBackground(false)，
        // 那么mLineChart.setGridBackgroundColor(Color.CYAN)将失效;
        mLineChart.setDrawGridBackground(false);
        mLineChart.setGridBackgroundColor(Color.CYAN);
        // 触摸
        mLineChart.setTouchEnabled(true);
        // 拖拽
        mLineChart.setDragEnabled(true);
        // 缩放
        mLineChart.setScaleEnabled(true);
        mLineChart.setPinchZoom(false);
        // 隐藏右边 的坐标轴
        mLineChart.getAxisRight().setEnabled(false);
        // 让x轴在下面
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // // 隐藏左边坐标轴横网格线
        // mLineChart.getAxisLeft().setDrawGridLines(false);
        // // 隐藏右边坐标轴横网格线
        // mLineChart.getAxisRight().setDrawGridLines(false);
        // // 隐藏X轴竖网格线
        // mLineChart.getXAxis().setDrawGridLines(false);
        // 设置背景
        mLineChart.setBackgroundColor(color);
        // 设置x,y轴的数据
        mLineChart.setData(lineData);
        // 设置比例图标示，就是那个一组y的value的
        Legend mLegend = mLineChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(0.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        // 沿x轴动画，时间2000毫秒。
        mLineChart.animateX(2000);
    }


    /**
     * @param count 数据点的数量。
     * @param day   day=0代表当天,1为前一天,2为上前天,以此类推
     * @return LineData
     */
    public static LineData makeLineData(Context context, int count, int day) {
        ArrayList<String> x = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (i == 0)
                x.add("00:00");
            else if (i == 6)
                x.add("06:00");
            else if (i == 12)
                x.add("12:00");
            else if (i == 18)
                x.add("18:00");
            else if (i == 23)
                x.add("23:00");
            else x.add("");
        }

        // y轴的数据
        ArrayList<Entry> y = new ArrayList<>();
        List<Float> stepsInDay = SharedPreferencesUtils.getStepsInDay(context, day);
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * 100);
            //Entry entry = new Entry(val, i);
            Entry entry = new Entry(stepsInDay.get(i), i);
            y.add(entry);
        }

        // y轴数据集
        LineDataSet mLineDataSet = new LineDataSet(y, "");
        // 用y轴的集合来设置参数
        // 线宽
        mLineDataSet.setLineWidth(1.0f);
        // 显示的圆形大小
        mLineDataSet.setCircleSize(3.0f);
        // 折线的颜色
        mLineDataSet.setColor(Color.DKGRAY);
        // 圆球的颜色
        mLineDataSet.setCircleColor(Color.GREEN);

        // 设置mLineDataSet.setDrawHighlightIndicators(false)后，
        // Highlight的十字交叉的纵横线将不会显示，
        // 同时，mLineDataSet.setHighLightColor(Color.CYAN)失效。
        mLineDataSet.setDrawHighlightIndicators(true);

        // 按击后，十字交叉线的颜色
        mLineDataSet.setHighLightColor(Color.CYAN);
        // 设置这项上显示的数据点的字体大小。
        mLineDataSet.setValueTextSize(10.0f);
        // mLineDataSet.setDrawCircleHole(true);

        // 改变折线样式，用曲线。
        mLineDataSet.setDrawCubic(true);
        // 默认是直线
        // 曲线的平滑度，值越大越平滑。
        //mLineDataSet.setCubicIntensity(0.2f);
        // 填充曲线下方的区域，红色，半透明。
        //mLineDataSet.setDrawFilled(true);
        //mLineDataSet.setFillAlpha(128);
        //mLineDataSet.setFillColor(Color.RED);
        // 填充折线上数据点、圆球里面包裹的中心空白处的颜色。
        mLineDataSet.setCircleColorHole(Color.YELLOW);
        // 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。
        mLineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry,
                                            int dataSetIndex, ViewPortHandler viewPortHandler) {
                int n = (int) value;
                return "" + n;
            }
        });
        ArrayList<LineDataSet> mLineDataSets = new ArrayList<>();
        mLineDataSets.add(mLineDataSet);
        return new LineData(x, mLineDataSets);
    }
}
