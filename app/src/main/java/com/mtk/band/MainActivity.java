package com.mtk.band;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtk.activity.SettingsActivity;
import com.mtk.band.bean.SportInfo;
import com.mtk.band.utils.Tools;
import com.mtk.base.BaseActivity;
import com.mtk.data.PreferenceData;
import com.mtk.eventbus.DataUpdateEvent;
import com.mtk.receiver.ScreenBroadcastReceiver;
import com.mtk.remotecamera.RemoteCameraService;
import com.mtk.service.MainService;
import com.mtk.util.BluetoothUtils;
import com.mtk.util.DialogHelper;
import com.mtk.util.LogUtils;
import com.ruanan.btnotification.R;

import static java.lang.Integer.parseInt;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvSport1;
    private TextView tvSport2;
    private TextView tvSport3;
    //指示当前切换的主要运动数据,默认居中显示步数
    private static final int SPORTINDICATE_BUSHU = 0;
    private static final int SPORTINDICATE_DAKA = 1;
    private static final int SPORTINDICATE_GONGLI = 2;
    private int SPORT_INDICATE = SPORTINDICATE_BUSHU;
    private LinearLayout llSport;
    private SportInfo sportInfo;
    private ImageView ivRing;
    private TextView tvUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_band);
        initData();
        initView();
        startRing();

        //如果蓝牙通知服务不可用并且通知服务对象为null,则弹出对话框提示用户启用辅助功能
        if (!PreferenceData.isNotificationServiceEnable() && !MainService.isNotificationServiceActived()) {
            DialogHelper.showAccessibilityPrompt(context);
        }

        //短信服务和通知服务是否在运行
        if (PreferenceData.isSmsServiceEnable() || PreferenceData.isNotificationServiceEnable()) {
            startMainService();
        }

        LogUtils.d("registerReceiver");
        registerReceiver(new ScreenBroadcastReceiver(), ScreenBroadcastReceiver.genIntentFilter());
    }

    /**
     * 圈圈开始旋转
     */
    private void startRing() {
        //https://my.oschina.net/janson2013/blog/118558
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.tip);
        animation.setInterpolator(new LinearInterpolator());
        ivRing.startAnimation(animation);
    }

    protected void initData() {
        sportInfo = new SportInfo();
        sportInfo.setSteps(0);
        sportInfo.setDistance("0");
        sportInfo.setCalories("0");
    }

    protected void initView() {
        findViewById(R.id.tv_find).setOnClickListener(this);
        findViewById(R.id.tv_jiuzuo).setOnClickListener(this);
        findViewById(R.id.tv_shuimian).setOnClickListener(this);
        findViewById(R.id.tv_set).setOnClickListener(this);

        (tvSport1 = (TextView) findViewById(R.id.tv_sport1)).setOnClickListener(this);
        (tvSport2 = (TextView) findViewById(R.id.tv_sport2)).setOnClickListener(this);
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        (tvSport3 = (TextView) findViewById(R.id.tv_sport3)).setOnClickListener(this);
        ivRing = (ImageView) findViewById(R.id.iv_ring);
        tvSport1.setText(getString(R.string.distance, sportInfo.getDistance()));
        tvSport2.setText(sportInfo.getSteps() + "");
        tvSport3.setText(getString(R.string.calories, sportInfo.getCalories()));
        tvUnit.setText(R.string.steps_unit);
        llSport = (LinearLayout) findViewById(R.id.ll_sport);
        llSport.setOnTouchListener(new View.OnTouchListener() {

            private float startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (event.getX() - startX >= 100) {
                            switchSportInfoShow(false);
                        } else if (startX - event.getX() >= 100) {
                            switchSportInfoShow(true);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_find:
                findBand();
                break;
            case R.id.tv_jiuzuo:
                enterActivity(SportActivity.class);
                break;
            case R.id.tv_shuimian:
                enterActivity(ShuimainActivity.class);
                break;
            case R.id.tv_set:
                enterActivity(SettingsActivity.class);
                break;
            default:
                enterSportPage(v.getId());
                break;
        }
    }

    public void enterSportPage(int id) {
        switch (id) {
            case R.id.tv_sport1:
                switch (SPORT_INDICATE) {
                    case SPORTINDICATE_BUSHU:
                        enterActivity(JuliActivity.class);
                        break;
                    case SPORTINDICATE_GONGLI:
                        enterActivity(CaloriesActivity.class);
                        break;
                    case SPORTINDICATE_DAKA:
                        enterActivity(BushuActivity.class);
                        break;
                }
                break;
            case R.id.tv_sport2:
                switch (SPORT_INDICATE) {
                    case SPORTINDICATE_BUSHU:
                        enterActivity(BushuActivity.class);
                        break;
                    case SPORTINDICATE_GONGLI:
                        enterActivity(JuliActivity.class);
                        break;
                    case SPORTINDICATE_DAKA:
                        enterActivity(CaloriesActivity.class);
                        break;
                }
                break;
            case R.id.tv_sport3:
                switch (SPORT_INDICATE) {
                    case SPORTINDICATE_BUSHU:
                        enterActivity(CaloriesActivity.class);
                        break;
                    case SPORTINDICATE_GONGLI:
                        enterActivity(BushuActivity.class);
                        break;
                    case SPORTINDICATE_DAKA:
                        enterActivity(JuliActivity.class);
                        break;
                }
                break;
        }
    }

    /**
     * 刷新数据
     */
    private void refresh() {
        switch (SPORT_INDICATE) {
            case SPORTINDICATE_BUSHU:
                centerBushu();
                break;
            case SPORTINDICATE_GONGLI:
                centerGongli();
                break;
            case SPORTINDICATE_DAKA:
                centerCalories();
                break;
        }
    }

    /**
     * 切换运动信息显示
     */
    private void switchSportInfoShow(boolean isLeft) {
        switch (SPORT_INDICATE) {
            case SPORTINDICATE_BUSHU:
                if (isLeft) {
                    SPORT_INDICATE = SPORTINDICATE_DAKA;
                    centerCalories();
                } else {
                    SPORT_INDICATE = SPORTINDICATE_GONGLI;
                    centerGongli();
                }
                break;
            case SPORTINDICATE_DAKA:
                if (isLeft) {
                    SPORT_INDICATE = SPORTINDICATE_GONGLI;
                    centerGongli();
                } else {
                    SPORT_INDICATE = SPORTINDICATE_BUSHU;
                    centerBushu();
                }
                break;
            case SPORTINDICATE_GONGLI:
                if (isLeft) {
                    SPORT_INDICATE = SPORTINDICATE_BUSHU;
                    centerBushu();
                } else {
                    SPORT_INDICATE = SPORTINDICATE_DAKA;
                    centerCalories();
                }
                break;
        }
    }

    /**
     * 卡路里居中
     */
    private void centerCalories() {
        tvSport1.setText(getString(R.string.steps, sportInfo.getSteps()));
        tvSport2.setText(sportInfo.getCalories() + "");
        tvSport3.setText(getString(R.string.distance, sportInfo.getDistance()));
        setCompoundDrawables(tvSport1, R.mipmap.bushu);
        setCompoundDrawables(tvSport2, R.mipmap.daka_large);
        setCompoundDrawables(tvSport3, R.mipmap.gongli);
        tvUnit.setText(R.string.calories_unit);
    }

    /**
     * 公里居中
     */
    private void centerGongli() {
        tvSport3.setText(getString(R.string.steps, sportInfo.getSteps()));
        tvSport1.setText(getString(R.string.calories, sportInfo.getCalories()));
        tvSport2.setText(sportInfo.getDistance() + "");
        setCompoundDrawables(tvSport1, R.mipmap.daka);
        setCompoundDrawables(tvSport2, R.mipmap.gongli_large);
        setCompoundDrawables(tvSport3, R.mipmap.bushu);
        tvUnit.setText(R.string.distance_unit);
    }

    /**
     * 步数居中
     */
    private void centerBushu() {
        tvSport2.setText(sportInfo.getSteps() + "");
        tvSport3.setText(getString(R.string.calories, sportInfo.getCalories()));
        tvSport1.setText(getString(R.string.distance, sportInfo.getDistance()));
        setCompoundDrawables(tvSport1, R.mipmap.gongli);
        setCompoundDrawables(tvSport2, R.mipmap.bushu_large);
        setCompoundDrawables(tvSport3, R.mipmap.daka);
        tvUnit.setText(R.string.steps_unit);
    }

    /**
     * 设置textivew的drawble
     * http://jingyan.baidu.com/article/eb9f7b6d8844a7869364e8e1.html
     *
     * @param tv    TextView
     * @param resId resId
     */
    private void setCompoundDrawables(TextView tv, int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        tv.setCompoundDrawables(null, drawable, null, null);//画在上边
    }

    /**
     * 开启主服务
     */
    private void startMainService() {
        startService(new Intent(context, MainService.class));
    }

    /**
     * 找手环
     */
    private void findBand() {
        String command = String.valueOf(6) + RemoteCameraService.Commands.NUM_OF_START_ACTIFITY_ARGS;
        if (BluetoothUtils.getBlutootnLinkState()) {
            MainService.getInstance().sendCAPCResult(command);
        } else {
            openBluetoothSettings();
        }
    }

    /**
     * 打开蓝牙设置页面
     */
    private void openBluetoothSettings() {
        startActivity(BluetoothUtils.repairBluetooth());
    }

    @Override
    public void onEventMainThread(Object obj) {
        if (obj instanceof DataUpdateEvent) {
            DataUpdateEvent dataUpdateEvent = (DataUpdateEvent) obj;
            String data = dataUpdateEvent.getData();
            if (!data.contains("GET,")) {
                return;
            }
            String[] split = data.split(",");
            String jibu = split[2];
            String shuimain = split[3];

            String bu = jibu.split("\\|")[3];
            String time = jibu.split("\\|")[2];
            int steps = parseInt(bu);
            sportInfo.setSteps(steps);
            sportInfo.setDistance(Tools.getDistance(parseInt(bu)));
            sportInfo.setCalories(Tools.getCaloreis(context, parseInt(bu)));
            LogUtils.i("bbbbbbbbbbbbbbbbbbbbb data = " + data + ",sportInfo = " + sportInfo);
            refresh();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.stepStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Tools.stepStop();
    }
}
