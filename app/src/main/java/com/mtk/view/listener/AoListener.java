package com.mtk.view.listener;

import android.view.View;

import com.mtk.base.BaseUIActivity;
import com.ruanan.btnotification.R;

/**
 * Created by YGH on 2016/6/7.
 * 邮箱：839939978
 * Copyright 2016 ruanan. All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of ruanan.
 * You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement
 * you entered into with ruanan.
 */
public class AoListener implements View.OnClickListener {
    BaseUIActivity mBaseActivity;

    public AoListener(BaseUIActivity baseActivitys) {
        this.mBaseActivity = baseActivitys;
    }

    @Override
    public void onClick(View view) {
        if (this.mBaseActivity.isPushTodown) {
            this.mBaseActivity.finish();
            this.mBaseActivity.overridePendingTransition(0, R.anim.push_down);
            return;
        }
        this.mBaseActivity.finish();
        this.mBaseActivity.overridePendingTransition(0, R.anim.push_right);
    }
}
