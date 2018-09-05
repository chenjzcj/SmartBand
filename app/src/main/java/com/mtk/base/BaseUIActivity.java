package com.mtk.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtk.view.BoundDragView;
import com.mtk.view.TabLoadingProgressView;
import com.mtk.view.TitleBarView;
import com.mtk.view.dialog.DialogControl;
import com.mtk.view.dialog.DialogHelper;
import com.mtk.view.dialog.WaitDialog;
import com.mtk.view.listener.AoListener;
import com.ruanan.btnotification.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 作者 : 527633405@qq.com
 * 时间 : 2015/12/16 0016
 * 最最基础的activity
 */
public class BaseUIActivity extends Activity implements DialogControl, VisibilityControl, Handler.Callback {

    public LayoutInflater mInflater;
    public boolean isPushTodown;
    protected BaseUIActivity mBaseActivity;
    protected TitleBarView mTitleBar;
    protected LinearLayout mainview;
    protected LinearLayout subview;
    protected LinearLayout bar_shadow;
    protected TabLoadingProgressView mprogress;
    protected BoundDragView mBoundDraView;
    protected Handler mHandler;
    private boolean _isVisible;
    private WaitDialog _waitDialog;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //设置Umeng统计相关参数
        initUmengConfig();
        this.isPushTodown = false;
        //this.isPushTodown = true;
        this.mBaseActivity = this;
        setContentView(this.getLayoutId());
        this.mInflater = LayoutInflater.from(this.mBaseActivity);
        this.mHandler = new Handler(this);

        this.mTitleBar = (TitleBarView) this.findViewById(R.id.layout_titlebar);
        this.mainview = (LinearLayout) this.findViewById(R.id.layout_mainview);
        this.subview = (LinearLayout) this.findViewById(R.id.layout_subview);
        this.bar_shadow = (LinearLayout) this.findViewById(R.id.title_bar_shadow);
        this.mBoundDraView = (BoundDragView) this.findViewById(R.id.drag);
        if (this.mBoundDraView != null) {
            this.mBoundDraView.setVisibility(View.VISIBLE);
        }

        this.mprogress = (TabLoadingProgressView) this.findViewById(R.id.loading_bar);

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
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(int title) {
        this.mTitleBar.setTitle(title);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        this.mTitleBar.setTitle(title);
    }

    /**
     * 添加返回按钮监听
     *
     * @param listener
     */
    public void addBackListener(View.OnClickListener listener) {
        TextView textView = new TextView(this.mBaseActivity);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_back);
        textView.setText(getString(R.string.back));
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setCompoundDrawablePadding(10);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(10, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.text_dark));
        this.mTitleBar.setLeftLayoutListener(textView, listener);
    }

    /**
     * 添加右边按钮点击监听
     *
     * @param listener
     */
    public void addListener(View.OnClickListener listener) {
        ImageView imageView = new ImageView(this.mBaseActivity);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(R.drawable.ic_connect_right_normal);
        this.mTitleBar.setRightLayoutListener(imageView, listener);
    }

    /**
     * 添加主View
     *
     * @param view
     */
    public void addMainView(View view) {
        LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.weight = 1.0f;
        this.mainview.addView(view, linearLayout);
    }

    /**
     * 添加子view
     *
     * @param view
     */
    public void addSubView(View view) {
        LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.weight = 1.0f;
        this.subview.addView(view, linearLayout);
    }

    /**
     * 设置是否显示阴影
     *
     * @param bool
     */
    public void setBarshadow(boolean bool) {
        if (bool) {
            this.bar_shadow.setVisibility(View.VISIBLE);
            return;
        }
        this.bar_shadow.setVisibility(View.GONE);
    }

    protected int getLayoutId() {
        return R.layout.layout_base;
    }

    /**
     * 添加返回监听
     */
    public void backListener() {
        this.addBackListener(new AoListener(this));
    }

    public void destoryActivity() {
        finish();
    }

    /**
     * 显示加载提示
     */
    protected void showLoading() {
        mprogress.showLoading(getString(R.string.loading));

    }

    /**
     * 隐藏加载提示
     */
    protected void hideProgress() {
        this.mprogress.hideLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        if (this.isPushTodown) {
            destoryActivity();
            this.overridePendingTransition(0, R.anim.push_down);
            return;
        }
        destoryActivity();
        this.overridePendingTransition(0, R.anim.push_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        _isVisible = false;
        hideWaitDialog();
        super.onPause();
    }

    @Override
    protected void onResume() {
        _isVisible = true;
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // ======================弹出框================================
    @Override
    public WaitDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    @Override
    public WaitDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    @Override
    public WaitDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelper.getWaitDialog(this, message);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(message);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    @Override
    public void hideWaitDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean isVisible() {
        return _isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        _isVisible = visible;
    }

    public void hideSoftInputMethod() {

        final View v = this.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void showSoftInputMethod(View focusView) {
        focusView.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(focusView, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}
