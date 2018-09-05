package com.mtk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtk.adapter.HomeAdapter;
import com.mtk.band.AlarmActivity;
import com.mtk.band.SportActivity;
import com.mtk.band.utils.SharedPreferencesUtils;
import com.mtk.band.utils.Tools;
import com.mtk.base.BaseActivity;
import com.mtk.data.PreferenceData;
import com.mtk.eventbus.DataUpdateEvent;
import com.mtk.receiver.ScreenBroadcastReceiver;
import com.mtk.remotecamera.RemoteCameraService;
import com.mtk.service.MainService;
import com.mtk.util.BluetoothUtils;
import com.mtk.util.DateUtils;
import com.mtk.util.FileUtils;
import com.mtk.util.LogUtils;
import com.ruanan.btnotification.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 首页
 */
public class MainActivity extends BaseActivity {
    //指向右边的箭头
    private ImageView arrowRight0, arrowRight1, arrowRight2, arrowRight3, arrowRight4;
    //指向左边的箭头
    private ImageView arrowLeft0, arrowLeft1, arrowLeft2, arrowLeft3, arrowLeft4;
    //蓝牙连接状态
    private TextView tvConnectStatus;
    private GridView gridview;
    //存放箭头图片ImageView
    private ArrayList<ImageView> imageViews;
    private Timer timer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //LogUtils.i("msg.arg1=" + msg.arg1);
            setBTLinkState();  //实时获取蓝牙状态并刷新显示
            for (ImageView imageView : imageViews) {
                imageView.setSelected(imageViews.get(msg.arg1) == imageView);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setBTLinkState();
        //如果蓝牙通知服务不可用并且通知服务对象为null,则弹出对话框提示用户启用辅助功能
        if (!PreferenceData.isNotificationServiceEnable() && !MainService.isNotificationServiceActived()) {
            //智能手环暂时不需要这个,先注释
            //DialogHelper.showAccessibilityPrompt(context);
        }

        //短信服务和通知服务是否在运行
        if (PreferenceData.isSmsServiceEnable() || PreferenceData.isNotificationServiceEnable()) {
            startMainService();
        }
        //检查更新
        //UpdateUtils.checkAppUpdate(true, context);
        //开始箭头动画
        this.startArrowAnim();
        registerReceiver(new ScreenBroadcastReceiver(), ScreenBroadcastReceiver.genIntentFilter());
    }

    @Override
    protected void initView() {
        super.initView();
        arrowRight0 = (ImageView) findViewById(R.id.iv_connect_right_0);
        arrowRight1 = (ImageView) findViewById(R.id.iv_connect_right_1);
        arrowRight2 = (ImageView) findViewById(R.id.iv_connect_right_2);
        arrowRight3 = (ImageView) findViewById(R.id.iv_connect_right_3);
        arrowRight4 = (ImageView) findViewById(R.id.iv_connect_right_4);

        arrowLeft0 = (ImageView) findViewById(R.id.iv_connect_left_0);
        arrowLeft1 = (ImageView) findViewById(R.id.iv_connect_left_1);
        arrowLeft2 = (ImageView) findViewById(R.id.iv_connect_left_2);
        arrowLeft3 = (ImageView) findViewById(R.id.iv_connect_left_3);
        arrowLeft4 = (ImageView) findViewById(R.id.iv_connect_left_4);

        tvConnectStatus = (TextView) findViewById(R.id.tv_connect_status);
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0://找手表
                        MobclickAgent.onEvent(context, "click_findwatch", "find_watch");             //统计找手表点击次数
                        findWatch();
                        break;
                    case 1://应用开关
                        MobclickAgent.onEvent(context, "click_notif_mannager", "notif_mannager");   //统计应用开关点击次数
                        openAccessibilitySettings();
                        break;
                    case 2://设置
                        MobclickAgent.onEvent(context, "click_settings", "settings");               //统计设置点击次数
                        intent = new Intent(context, SettingsActivity.class);
                        break;
                    case 3://打开蓝牙配对
                        MobclickAgent.onEvent(context, "click_bluetooth_settings", "blue_settings"); //统计设置点击次数
                        openBluetoothSettings();
                        break;
                    case 4://运动计步
                        intent = SportActivity.enterSportActivity(context, false);
                        break;
                    case 5://睡眠监测
                        intent = SportActivity.enterSportActivity(context, true);
                        break;
                    case 6://智能闹钟
                        intent = new Intent(context, AlarmActivity.class);
                        break;
                    case 7://关于
                        intent = new Intent(context, AboutActivity.class);
                        break;
                }

                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_acc, 0);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        //将箭头放入集合中
        imageViews = new ArrayList<>();
        this.imageViews.add(arrowRight0);
        this.imageViews.add(arrowRight1);
        this.imageViews.add(arrowRight2);
        this.imageViews.add(arrowRight3);
        this.imageViews.add(arrowRight4);
        this.imageViews.add(arrowLeft4);
        this.imageViews.add(arrowLeft3);
        this.imageViews.add(arrowLeft2);
        this.imageViews.add(arrowLeft1);
        this.imageViews.add(arrowLeft0);
        /************初始化griView相关数据************/
        String[] itemTexts = getResources().getStringArray(R.array.home_items);
        ArrayList<Integer> imgResIds = new ArrayList<>();
        imgResIds.add(R.drawable.ic_findwatch);
        imgResIds.add(R.drawable.ic_app_license);
        imgResIds.add(R.drawable.ic_settings);
        imgResIds.add(R.drawable.ic_blutooth_repair);
        imgResIds.add(R.drawable.ic_sport);
        imgResIds.add(R.drawable.ic_sleep);
        imgResIds.add(R.drawable.icon_alarm);
        imgResIds.add(R.drawable.ic_about);
        this.gridview.setAdapter(new HomeAdapter(this, itemTexts, imgResIds));
    }

    /**
     * 设置蓝牙连接状态
     */
    private void setBTLinkState() {
        boolean btConnected = MainService.mBluetoothManager.isBTConnected();
        tvConnectStatus.setText(btConnected ? getString(R.string.connected) : getString(R.string.disconnected));
    }

    private int arrowIndex = 0;//箭头角标

    /**
     * 运行箭头动画效果
     */
    private void startArrowAnim() {
        this.timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mHandler.obtainMessage(0, arrowIndex++ % imageViews.size(), 0).sendToTarget();
            }
        };
        this.timer.schedule(task, 1000, 1000);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        MobclickAgent.onResume(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        MobclickAgent.onPause(context);
    }

    /**
     * 开启主服务
     */
    private void startMainService() {
        startService(new Intent(context, MainService.class));
    }

    /**
     * 打开蓝牙设置页面
     */
    private void openBluetoothSettings() {
        startActivity(BluetoothUtils.repairBluetooth());
    }

    /**
     * 打开辅助功能设置页面
     */
    private void openAccessibilitySettings() {
        startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
    }

    /**
     * 找手表
     */
    private void findWatch() {
        String command = String.valueOf(6) + RemoteCameraService.Commands.NUM_OF_START_ACTIFITY_ARGS;
        if (BluetoothUtils.getBlutootnLinkState()) {
            MainService.getInstance().sendCAPCResult(command);
        } else {
            openBluetoothSettings();
        }
    }

    @Override
    public void onBackPressed() {
        //统计程序退出
        MobclickAgent.onProfileSignOff();
        Tools.stepStop();
        //super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        if (this.imageViews != null) {
            this.imageViews.clear();
            this.imageViews = null;
        }
        if (timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onEventMainThread(Object obj) {
        if (obj instanceof DataUpdateEvent) {
            int length = ((DataUpdateEvent) obj).getLength();
            String data = ((DataUpdateEvent) obj).getData().substring(0, length);
            if (!data.contains("GET,")) {
                return;
            }
            LogUtils.i("MainActivity data = " + data);
            String[] split = data.split(",");
            String sleepData = split[3];//睡眠数据
            //如果当前时间在头一天的晚上11点与第二天早上7点之间,则启动睡眠
            if (DateUtils.isCurTimeInRange()) {
                parseSleepData(sleepData);
            }
        }
    }

    /**
     * 解析睡眠监测数据
     *
     * @param sleepData 格式:1|1479720600|0|5
     */
    private void parseSleepData(String sleepData) {
        LogUtils.i("parseSleepData sleepData " + sleepData);
        FileUtils.writeFile(sleepData, "sleep", DateUtils.getDateStr(System.currentTimeMillis(), "yyyyMMddHHmmss") + ".txt");
        //在睡眠状态走的步数,根据这个值来判断睡眠质量
        String sleepStep = sleepData.split("\\|")[3];
        SharedPreferencesUtils.saveSleepStep(context, Integer.parseInt(sleepStep));
    }
}
