package com.mtk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtk.bean.SettingsBean;
import com.mtk.data.PreferenceData;
import com.mtk.service.MainService;
import com.mtk.util.DialogHelper;
import com.mtk.util.ToastUtils;
import com.mtk.view.togglebutton.ToggleButton;
import com.ruanan.btnotification.R;

import java.util.ArrayList;

public class SettingsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SettingsBean> settingsDatas;

    public SettingsAdapter(Context context, ArrayList<SettingsBean> settingsDatas) {
        this.context = context;
        this.settingsDatas = settingsDatas;
    }

    @Override
    public int getCount() {
        return settingsDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return settingsDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_settings_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.tv_description);
            holder.tg = (ToggleButton) convertView.findViewById(R.id.tg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SettingsBean settingsData = this.settingsDatas.get(position);
        holder.img.setImageResource(settingsData.getImgResId());
        holder.tvTitle.setText(context.getString(settingsData.getTitleResId()));
        holder.tg.setOnToggleChanged(new ToggleChangeListener(position));

        //判断描述资源id是否为空
        if (settingsData.getDescriptionResId() == 0) {
            holder.tvDescription.setVisibility(View.GONE);
        } else {
            holder.tvDescription.setText(context.getString(settingsData.getDescriptionResId()));
        }

        //判断是否有toggleButton按钮，没有就隐藏
        if (!settingsData.isHasTB()) {
            holder.tg.setVisibility(View.GONE);
        } else {
            if (settingsData.isTBOpen()) {
                holder.tg.toggleOn();
            } else {
                holder.tg.toggleOff();
            }
        }

        return convertView;
    }

    public class ViewHolder {
        public ImageView img;           //图标
        public TextView tvTitle;        //标题
        public TextView tvDescription;  //描述说明
        public ToggleButton tg;         //toggleButton按钮
    }

    /**
     * ToggleButton改变监听器
     */
    private class ToggleChangeListener implements ToggleButton.OnToggleChanged {
        private int position;

        public ToggleChangeListener(int position) {
            this.position = position;
        }

        @Override
        public void onToggle(boolean flag) {
            MainService service = MainService.getInstance();  //主服务
            if (service == null) {
                ToastUtils.showLongToast(context, "The MainSerice is killed,please try again!");
                return;
            }
            switch (position) {
                case 0:   //短信服务
                    PreferenceData.setSmsServiceEnable(flag);
                    if (flag) {
                        service.startSmsService();
                    } else {
                        service.stopSmsService();
                    }
                    break;
                case 1:  //电话服务
                    PreferenceData.setCallServiceEnable(flag);
                    if (flag) {
                        service.startCallService();
                    } else {
                        service.stopCallService();
                    }
                    break;
                case 2: //通知服务
                    PreferenceData.setNotificationServiceEnable(flag);
                    if (flag) {
                        service.startNotificationService();
                        if (!MainService.isNotificationServiceActived()) {
                            DialogHelper.showAccessibilityPrompt(context);
                        }
                    } else {
                        service.stopNotificationService();
                    }
                    break;
            }
        }
    }
}
