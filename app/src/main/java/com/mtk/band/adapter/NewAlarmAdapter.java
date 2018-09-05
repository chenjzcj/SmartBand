package com.mtk.band.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mtk.band.AlarmActivity;
import com.mtk.band.bean.Alarm;
import com.mtk.util.NumUtil;
import com.mtk.view.togglebutton.ToggleButton;
import com.ruanan.btnotification.R;

import java.util.List;

/**
 * 作者 : 527633405@qq.com
 * 时间 : 2015/12/23 0023
 * 新的闹铃设置适配器
 */
public class NewAlarmAdapter extends BaseAdapter {
    private List<Alarm> alarms;
    private Context context;
    private AlarmActivity alarmActivity;

    public NewAlarmAdapter(Context context, List<Alarm> alarms, AlarmActivity alarmActivity) {
        this.context = context;
        this.alarms = alarms;
        this.alarmActivity = alarmActivity;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_alarm_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Alarm alarm = alarms.get(position);
        if (alarm.isEnable()) {
            holder.tooggleAlarmState.setToggleOn();
        } else {
            holder.tooggleAlarmState.setToggleOff();
        }
        holder.tooggleAlarmState.setOnToggleChanged(new MysOnToggleChanged(alarm, position));
        int time = alarm.getTime();
        String timeStr = NumUtil.get2StrLenNum(time / 100 % 100) + ":" + NumUtil.get2StrLenNum(time % 100);
        holder.tvalarmtime.setText(timeStr);

        //设置隐身星期
        int weekDay = alarm.getWeekDay();
        String weekDayStr = Integer.toBinaryString(weekDay);
        StringBuilder sb = new StringBuilder();
        //补全前面
        for (int i = 0; i < 8 - weekDayStr.length(); i++) {
            sb.append("0");
        }
        sb.append(weekDayStr);
        //二进制：第1位=星期日、第2位=星期一、第3位=星期二、第4位=星期三、第5位=星期四、第6位=星期五、第7位=星期六
        holder.ibsun.setSelected(sb.substring(1, 2).equals("1"));
        holder.ibmon.setSelected(sb.substring(2, 3).equals("1"));
        holder.ibtue.setSelected(sb.substring(3, 4).equals("1"));
        holder.ibwed.setSelected(sb.substring(4, 5).equals("1"));
        holder.ibthur.setSelected(sb.substring(5, 6).equals("1"));
        holder.ibfri.setSelected(sb.substring(6, 7).equals("1"));
        holder.ibsat.setSelected(sb.substring(7, 8).equals("1"));
        return convertView;
    }


    public class ViewHolder {
        public final ToggleButton tooggleAlarmState;
        public final TextView tvalarmtime;
        public final Button ibmon;
        public final Button ibtue;
        public final Button ibwed;
        public final Button ibthur;
        public final Button ibfri;
        public final Button ibsat;
        public final Button ibsun;
        public final View root;

        public ViewHolder(View root) {
            tooggleAlarmState = (ToggleButton) root.findViewById(R.id.tooggle_alarm_state);
            tvalarmtime = (TextView) root.findViewById(R.id.tv_alarmtime);
            ibmon = (Button) root.findViewById(R.id.ib_mon);
            ibtue = (Button) root.findViewById(R.id.ib_tue);
            ibwed = (Button) root.findViewById(R.id.ib_wed);
            ibthur = (Button) root.findViewById(R.id.ib_thur);
            ibfri = (Button) root.findViewById(R.id.ib_fri);
            ibsat = (Button) root.findViewById(R.id.ib_sat);
            ibsun = (Button) root.findViewById(R.id.ib_sun);
            this.root = root;
        }
    }

    class MysOnToggleChanged implements ToggleButton.OnToggleChanged {
        private Alarm alarm;
        private int id;

        public MysOnToggleChanged(Alarm alarm, int id) {
            this.alarm = alarm;
            this.id = id;
        }

        @Override
        public void onToggle(boolean on) {
            alarmActivity.modify(alarm, id, on);
        }
    }
}
