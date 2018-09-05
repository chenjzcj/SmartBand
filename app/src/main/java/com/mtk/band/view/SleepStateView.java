package com.mtk.band.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mtk.band.bean.SleepInfo;
import com.mtk.band.utils.SleepManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 睡眠状态图表
 */
public class SleepStateView extends View {
    private Context context;
    private Paint mPaint;
    private int deepSleepColor = 0xff732D78;
    private int lightSleepColor = 0xffC896C8;
    private int noSleepColor = 0xffA0AAE6;
    private SleepInfo sleepInfo;
    private boolean isShowDetail = false;//是否显示详情
    private float eventX;
    private List<Map<String, Integer>> positionColor = new ArrayList<>();

    public SleepStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    public void setSleepInfo(SleepInfo sleepInfo) {
        this.sleepInfo = sleepInfo;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        if (sleepInfo == null) {
            return;
        }
        List<Integer> sleepState = sleepInfo.getSleepState();
        mPaint.setColor(noSleepColor);
        for (int index = 0; index < sleepState.size(); index++) {
            Integer step = sleepState.get(index);
            int color = (step == SleepManager.SLEEPSTATE_DEEP) ? deepSleepColor :
                    (step == SleepManager.SLEEPSTATE_LIGHT_ONE ||
                            step == SleepManager.SLEEPSTATE_DEEP_TWO) ? lightSleepColor : noSleepColor;
            mPaint.setColor(color);
            drawRect(canvas, contentWidth, contentHeight, index, color, sleepState);
        }
        drawText(canvas, contentWidth, contentHeight);
        //if (isShowDetail)
        //drawDetailText(canvas, contentWidth);//暂时关闭
    }


    private void drawRect(Canvas canvas, int contentWidth, int contentHeight,
                          int index, int color, List<Integer> sleepState) {
        float singleWidth = (contentWidth - 40) / (sleepState.size() * 1.0f);//分成sleepState.size()份后的宽度,float类型防止精度损失
        float left = singleWidth * index + 20;//x坐标左
        float top = 20.0f;
        float right = singleWidth * (index + 1) + 20;//x坐标右
        float bottom = contentHeight - 20;
        canvas.drawRect(left, top, right, bottom, mPaint);

        Map<String, Integer> map = new HashMap<>();
        map.put("left", (int) left);
        map.put("right", (int) right);
        map.put("color", color);
        positionColor.add(map);
    }

    /**
     * 绘制y轴的文字
     *
     * @param canvas        Canvas
     * @param contentWidth  内容宽度
     * @param contentHeight 内容高度
     */
    private void drawText(Canvas canvas, int contentWidth, int contentHeight) {
        mPaint.setTextSize(22);
        mPaint.setColor(Color.WHITE);
        float textWidth = mPaint.measureText("00:00");//测量这段文字的宽度
        float interval = (contentWidth - 5 * textWidth) / 4.0f;//文字之间的间隔
        List<String> pointOfTimes = getPointOfTimes(sleepInfo.getStartTime(), sleepInfo.getEndTime());
        for (int i = 0; i < pointOfTimes.size(); i++) {
            canvas.drawText(pointOfTimes.get(i), i * (textWidth + interval), contentHeight, mPaint);
        }
    }

    /**
     * 获取时间节点
     *
     * @param startTime 开始时间,格式02:00
     * @param endTime   结束时间,格式02:00
     * @return 时间节点列表
     */
    private List<String> getPointOfTimes(String startTime, String endTime) {
        List<String> pointOfTimes = new ArrayList<>();
        pointOfTimes.add(startTime);
        pointOfTimes.add("02:00");
        pointOfTimes.add("04:00");
        pointOfTimes.add("06:00");
        pointOfTimes.add(endTime);
        return pointOfTimes;
    }

    private void drawDetailText(Canvas canvas, int contentWidth) {
        String state = "深睡";
        for (Map<String, Integer> map : positionColor) {
            Integer left = map.get("left");
            Integer right = map.get("right");
            Integer color = map.get("color");
            if (eventX >= left && eventX < right) {
                state = (color == deepSleepColor) ? "深睡" : (color == lightSleepColor) ? "浅睡" : "清醒";
                break;
            }
        }
        float textWidth = mPaint.measureText("00:12开始");
        canvas.drawText(state, 20, 18, mPaint);
        canvas.drawText("00:12开始", contentWidth / 2 - textWidth / 2, 18, mPaint);
        canvas.drawText("07:50结束", contentWidth - 20 - textWidth, 18, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isShowDetail = true;
                eventX = event.getX();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isShowDetail = false;
                break;
        }
        invalidate();
        //此处一定要设置成true,否则文字不会消失
        return true;
    }
}
