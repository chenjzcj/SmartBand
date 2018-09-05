package com.mtk.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mtk.eventbus.EventbusUtils;
import com.ruanan.btnotification.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 应用基类
 */
public class BaseActivity extends Activity {
    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        initUmengConfig();
        EventbusUtils.register(this);
    }

    //初始化View
    protected void initView() {
    }

    //初始化数据
    protected void initData() {
    }

    /**
     * 初始化Umeng统计
     */
    private void initUmengConfig() {
        //是否输出日志;
        MobclickAgent.setDebugMode(true);
        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.openActivityDurationTrack(false);
        //设置统计类型
        MobclickAgent.setScenarioType(this.context, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        this.overridePendingTransition(0, R.anim.push_right);
    }

    /**
     * 必须要有这个方法,子类可以选择是否重写,否则需要在每个activity里面注册与注销eventbus
     *
     * @param obj Object
     */
    public void onEventMainThread(Object obj) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventbusUtils.unregister(this);
    }

    public void enterActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_acc, 0);
    }
}
