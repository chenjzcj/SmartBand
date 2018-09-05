/**
 * Copyright 2014  XCL-Charts
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Project XCL-Charts
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */
package com.mtk.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;

import org.xclcharts.chart.BarChart;
import org.xclcharts.chart.BarData;
import org.xclcharts.chart.CustomLineData;
import org.xclcharts.common.IFormatterDoubleCallBack;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.renderer.XEnum;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author XiongChuanLiang<br/>
 *         (xcl_168@aliyun.com) MODIFIED YYYY-MM-DD REASON
 * @ClassName BarChart03View
 * @Description 用于展示定制线与明细刻度线
 */
public class BarChart03View extends DemoView implements Runnable {

    private String TAG = "BarChart03View";
    private BarChart chart = new BarChart();
    // 轴数据源
    private List<String> chartLabels = new LinkedList<>();
    private List<BarData> chartData = new LinkedList<>();
    private List<CustomLineData> mCustomLineDataset = new LinkedList<>();

    public BarChart03View(Context context) {
        super(context);
        initView();
    }

    public BarChart03View(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BarChart03View(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        chartLabels();
        chartDataSet();
        chartCustomLines();
        chartRender();

        // 綁定手势滑动事件
        this.bindTouch(this, chart);

        new Thread(this).start();
    }

    private void chartLabels() {
        chartLabels.add("周一");
        chartLabels.add("周二");
        chartLabels.add("周三");
        chartLabels.add("周四");
        chartLabels.add("周五");
        chartLabels.add("周六");
        chartLabels.add("周日");
    }

    private void chartDataSet() {
        // 标签对应的柱形数据集
        List<Double> dataSeriesA = new LinkedList<>();
        dataSeriesA.add(98d);
        dataSeriesA.add(100d);
        dataSeriesA.add(95d);
        dataSeriesA.add(100d);
        dataSeriesA.add(80d);
        dataSeriesA.add(60d);
        dataSeriesA.add(89d);

        // 依数据值确定对应的柱形颜色.
        List<Integer> dataColorA = new LinkedList<>();
        dataColorA.add(0xFE797C);
        dataColorA.add(0xFE797C);
        dataColorA.add(0xFE797C);
        dataColorA.add(0xFE797C);
        dataColorA.add(0xFE797C);
        dataColorA.add(0xFE797C);
        dataColorA.add(0xFE797C);

        BarData BarDataA = new BarData("", dataSeriesA, dataColorA, Color.rgb(53, 169, 239));

        chartData.clear();
        chartData.add(BarDataA);
    }

    private void chartRender() {
        // 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
        int[] ltrb = getBarLnDefaultSpadding();
        chart.setPadding(ltrb[0], ltrb[0], ltrb[0], ltrb[0]);

        // 标题
        //chart.setTitle("小小熊 - 期末考试成绩");
        //chart.addSubtitle("(XCL-Charts Demo)");
        // 数据源
        // chart.setDataSource(chartData);
        chart.setCategories(chartLabels);
        //chart.setCustomLines(mCustomLineDataset);

        // 图例
        //chart.getAxisTitle().setTitleStyle(XEnum.AxisTitleStyle.ENDPOINT);
        //chart.getAxisTitle().setLeftTitle("分数");
        //chart.getAxisTitle().setLowerTitle("科目");
        //chart.getAxisTitle().setCrossPointTitle("(一班)");

        // 数据轴
        chart.getDataAxis().setAxisMax(100);
        chart.getDataAxis().setAxisMin(0);
        chart.getDataAxis().setAxisSteps(20);

        // 指隔多少个轴刻度(即细刻度)后为主刻度
        //chart.getDataAxis().setDetailModeSteps(40);

        // 背景网格
        chart.getPlotGrid().showHorizontalLines();

        // 定义数据轴标签显示格式
        chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack() {

            @Override
            public String textFormatter(String value) {
                Double tmp = Double.parseDouble(value);
                DecimalFormat df = new DecimalFormat("#0");
                return (df.format(tmp));
            }
        });

        // 在柱形顶部显示值
        chart.getBar().setItemLabelVisible(true);
        chart.getBar().setBarStyle(XEnum.BarStyle.OUTLINE);
        chart.getBar().setBorderWidth(5);//设置柱的宽度
        chart.getBar().setOutlineAlpha(100);//设置条形柱中的透明度

        // chart.getBar().setBarRoundRadius(5);
        // chart.getBar().setBarStyle(XEnum.BarStyle.ROUNDBAR);

        // chart.setPlotPanMode(XEnum.PanMode.FREE);
        chart.disablePanMode();

        // 设定格式
        chart.setItemLabelFormatter(new IFormatterDoubleCallBack() {
            @Override
            public String doubleFormatter(Double value) {
                DecimalFormat df = new DecimalFormat("#0");
                return df.format(value);
            }
        });
        // 隐藏Key
        chart.getPlotLegend().hide();

        chart.getClipExt().setExtTop(150.f);

        // 柱形和标签居中方式
        chart.setBarCenterStyle(XEnum.BarCenterStyle.TICKMARKS);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 图所占范围大小
        chart.setChartRange(w, h);
    }


    /**
     * 定制线/分界线
     */
    private void chartCustomLines() {
        CustomLineData line1 = new CustomLineData("特优", 25d, Color.RED, 0);
        line1.setCustomLineCap(XEnum.DotStyle.PRISMATIC);
        line1.setLabelHorizontalPostion(Align.LEFT);
        line1.setLabelOffset(15);
        //line1.hideLine();
        line1.getLineLabelPaint().setColor(Color.BLACK);
        mCustomLineDataset.add(line1);

        CustomLineData line2 = new CustomLineData("良好", 75d, Color.RED, 0);
        line2.setLabelHorizontalPostion(Align.LEFT);
        line2.setLabelOffset(15);
        //line2.hideLine();
        mCustomLineDataset.add(line2);

        CustomLineData line3 = new CustomLineData("较差", 80d, Color.rgb(35, 172,
                57), 5);
        line3.setCustomLineCap(XEnum.DotStyle.RECT);
        line3.setLabelHorizontalPostion(Align.LEFT);
        line3.setLineStyle(XEnum.LineStyle.DOT);
        mCustomLineDataset.add(line3);

        CustomLineData line4 = new CustomLineData("失眠", 90d, Color.rgb(53, 169,
                239), 5);
        line4.setCustomLineCap(XEnum.DotStyle.TRIANGLE);
        line4.setLabelOffset(15);
        line4.getLineLabelPaint().setColor(Color.rgb(216, 44, 41));
        line4.setLineStyle(XEnum.LineStyle.DASH);
        mCustomLineDataset.add(line4);

        int average = calcAvg();
        CustomLineData line6 = new CustomLineData("本次考试平均得分:"
                + Integer.toString(average), (double) average, Color.BLUE, 5);
        line6.setLabelHorizontalPostion(Align.CENTER);
        line6.setLineStyle(XEnum.LineStyle.DASH);
        line6.getLineLabelPaint().setColor(Color.RED);
        mCustomLineDataset.add(line6);
    }

    private int calcAvg() {
        return (98 + 100 + 95 + 100) / 4;
    }

    @Override
    public void render(Canvas canvas) {
        try {
            chart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void run() {
        try {
            chartAnimation();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private void chartAnimation() {
        try {
            for (int i = 0; i < chartData.size(); i++) {
                BarData barData = chartData.get(i);
                for (int j = 0; j < barData.getDataSet().size(); j++) {
                    Thread.sleep(100);
                    List<BarData> animationData = new LinkedList<BarData>();
                    List<Double> dataSeries = new LinkedList<Double>();
                    List<Integer> dataColorA = new LinkedList<Integer>();
                    for (int k = 0; k <= j; k++) {
                        dataSeries.add(barData.getDataSet().get(k));
                        dataColorA.add(barData.getDataColor().get(k));
                    }

                    BarData animationBarData = new BarData("", dataSeries,
                            dataColorA, Color.rgb(53, 169, 239));
                    animationData.add(animationBarData);
                    chart.setDataSource(animationData);
                    postInvalidate();
                }
            }

        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}
