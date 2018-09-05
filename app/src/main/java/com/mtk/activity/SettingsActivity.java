package com.mtk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mtk.adapter.SettingsAdapter;
import com.mtk.band.PersonActivity;
import com.mtk.band.SedentaryReminderActivity;
import com.mtk.base.BaseUIActivity;
import com.mtk.bean.SettingsBean;
import com.mtk.data.PreferenceData;
import com.mtk.service.MainService;
import com.mtk.updateapp.UpdateUtils;
import com.ruanan.btnotification.R;

import java.util.ArrayList;

/**
 * 设置
 */
public class SettingsActivity extends BaseUIActivity {
    private ListView lv;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mTitleBar.setTitle(getString(R.string.settings));  //设置标题
        backListener();
        View view = this.mInflater.inflate(R.layout.activity_settins_new, null);
        this.addMainView(view);

        this.lv = (ListView) view.findViewById(R.id.settings_lv);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        ArrayList<SettingsBean> list = new ArrayList<>();
        //短信服务
        SettingsBean smsBean = new SettingsBean();
        smsBean.setImgResId(R.drawable.ic_settings_sms);
        smsBean.setTitleResId(R.string.settings_sms);
        smsBean.setDescriptionResId(R.string.settings_sms_describe);
        smsBean.setHasTB(true);
        smsBean.setTBOpen(PreferenceData.isSmsServiceEnable());
        //来电提醒
        SettingsBean callBean = new SettingsBean();
        callBean.setImgResId(R.drawable.ic_settings_call);
        callBean.setTitleResId(R.string.settings_call);
        callBean.setDescriptionResId(R.string.settings_call_describe);
        callBean.setHasTB(true);
        callBean.setTBOpen(PreferenceData.isCallServiceEnable());
        //通知服务
        SettingsBean notifBean = new SettingsBean();
        notifBean.setImgResId(R.drawable.ic_settings_notifi);
        notifBean.setTitleResId(R.string.settings_notification_ctl);
        notifBean.setDescriptionResId(R.string.settings_notification_ctl_describe);
        notifBean.setHasTB(true);
        notifBean.setTBOpen(MainService.isNotificationServiceActived());
        //应用开关
        SettingsBean appBean = new SettingsBean();
        appBean.setImgResId(R.drawable.ic_settings_app);
        appBean.setTitleResId(R.string.settings_app_mannager);
        appBean.setHasTB(false);
        //个人信息
        SettingsBean personInfo = new SettingsBean();
        personInfo.setImgResId(R.drawable.icon_personage);
        personInfo.setTitleResId(R.string.settings_person_info);
        personInfo.setHasTB(false);
        //久坐提醒
        SettingsBean jiuzuo = new SettingsBean();
        jiuzuo.setImgResId(R.drawable.jiuzuo);
        jiuzuo.setTitleResId(R.string.settings_jiuzuo);
        jiuzuo.setHasTB(false);
        //版本更新
        SettingsBean updateBean = new SettingsBean();
        updateBean.setImgResId(R.drawable.ic_settings_update);
        updateBean.setTitleResId(R.string.settings_update_version);
        updateBean.setHasTB(false);

        list.add(smsBean);
        list.add(callBean);
        list.add(notifBean);
        list.add(appBean);
        list.add(personInfo);
        list.add(jiuzuo);
        list.add(updateBean);

        this.lv.setAdapter(new SettingsAdapter(this, list));
        this.lv.setOnItemClickListener(new LvOnItemListener());
    }

    private class LvOnItemListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 3:          //应用开关
                    startActivity(new Intent(mBaseActivity, SelectNotifiActivity.class));
                    mBaseActivity.overridePendingTransition(R.anim.push_left_acc, 0);
                    break;
                case 4:  //个人信息
                    startActivity(new Intent(mBaseActivity, PersonActivity.class));
                    mBaseActivity.overridePendingTransition(R.anim.push_left_acc, 0);
                    break;
                case 5:  //久坐提醒
                    startActivity(new Intent(mBaseActivity, SedentaryReminderActivity.class));
                    mBaseActivity.overridePendingTransition(R.anim.push_left_acc, 0);
                    break;
                case 6:  //检查更新
                    UpdateUtils.checkAppUpdate(false, mBaseActivity);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (UpdateUtils.waitDialog != null) {
            UpdateUtils.waitDialog.hide();
            UpdateUtils.waitDialog = null;
        }
    }
}
