package com.mtk.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mtk.base.BaseUIActivity;
import com.mtk.util.AppUtils;
import com.ruanan.btnotification.R;

/**
 * 关于
 */
public class AboutActivity extends BaseUIActivity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mTitleBar.setTitle(getString(R.string.about));
        backListener();
        View view = this.mInflater.inflate(R.layout.activity_aboutus, null);
        this.addMainView(view);
        initView(view);
    }

    private void initView(View view) {
        ((TextView) view.findViewById(R.id.tv_version)).
                setText(getString(R.string.version_info, AppUtils.getVersionName(mBaseActivity), AppUtils.getVersionCode(mBaseActivity)));
    }
}
