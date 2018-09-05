package com.mtk.band;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mtk.band.bean.PersonInfo;
import com.mtk.band.utils.SharedPreferencesUtils;
import com.mtk.band.utils.UnitConverstion;
import com.mtk.base.BaseActivity;
import com.mtk.view.dialog.CommonDialog;
import com.ruanan.btnotification.R;

public class PersonActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvSex;
    private TextView tvAge;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvUnit;
    private TextView tvTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.rl_sex).setOnClickListener(this);
        findViewById(R.id.rl_age).setOnClickListener(this);
        findViewById(R.id.rl_height).setOnClickListener(this);
        findViewById(R.id.rl_weight).setOnClickListener(this);
        findViewById(R.id.rl_unit).setOnClickListener(this);
        findViewById(R.id.rl_target).setOnClickListener(this);

        tvSex = (TextView) findViewById(R.id.tv_sex);
        tvAge = (TextView) findViewById(R.id.tv_age);
        tvHeight = (TextView) findViewById(R.id.tv_height);
        tvWeight = (TextView) findViewById(R.id.tv_weight);
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        tvTarget = (TextView) findViewById(R.id.tv_target);
        setData();
    }

    private void setData() {
        PersonInfo personInfo = SharedPreferencesUtils.getPersonInfo(this);
        tvSex.setText(personInfo.getSex() == 1 ? "男" : "女");
        tvAge.setText(personInfo.getAge() + "岁");

        int unit = personInfo.getUnit();

        int height1 = personInfo.getHeight();
        String height = (unit == 1 ? UnitConverstion.getInchByCm(height1) : height1) + (unit == 1 ? "英寸" : "厘米");
        tvHeight.setText(height);

        int weight1 = personInfo.getWeight();
        String weight = (unit == 1 ? UnitConverstion.getPoundByKg(weight1) : weight1) + (unit == 1 ? "磅" : "公斤");
        tvWeight.setText(weight);

        tvUnit.setText(unit == 1 ? "英制" : "公制");
        tvTarget.setText(personInfo.getTarget() + "步");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.rl_sex:
                int sex = SharedPreferencesUtils.getPersonInfo(this).getSex();
                SharedPreferencesUtils.savePersonInfo(context, sex == 1 ? "0" : "1", null, null, null, null, null);
                tvSex.setText(sex == 1 ? "女" : "男");
                break;
            case R.id.rl_age:
                showEditDailog("岁", "age");
                break;
            case R.id.rl_height:
                int unit1 = SharedPreferencesUtils.getPersonInfo(this).getUnit();
                showEditDailog(unit1 == 1 ? "英寸" : "厘米", "height");
                break;
            case R.id.rl_weight:
                int unit2 = SharedPreferencesUtils.getPersonInfo(this).getUnit();
                showEditDailog(unit2 == 1 ? "磅" : "公斤", "weight");
                break;
            case R.id.rl_unit:
                int unit = SharedPreferencesUtils.getPersonInfo(this).getUnit();
                SharedPreferencesUtils.savePersonInfo(context, null, null, null, null, unit == 1 ? "0" : "1", null);
                tvUnit.setText(unit == 1 ? "公制" : "英制");
                setData();
                break;
            case R.id.rl_target:
                showEditDailog("步", "target");
                break;
        }
    }

    private void showEditDailog(String unit, final String tag) {
        final CommonDialog commonDialog = new CommonDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_edit_dialog, null);
        final EditText et_value = (EditText) view.findViewById(R.id.et_value);
        //http://blog.csdn.net/lihenair/article/details/50131767
        if (tag.equals("target"))
            et_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        ((TextView) view.findViewById(R.id.tv_unit)).setText(unit);
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commonDialog != null) {
                    commonDialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = et_value.getText().toString();
                if (!TextUtils.isEmpty(value)) {
                    switch (tag) {
                        case "age":
                            SharedPreferencesUtils.savePersonInfo(context, null, value, null, null, null, null);
                            break;
                        case "height":
                            int unit = SharedPreferencesUtils.getPersonInfo(context).getUnit();
                            if(unit ==1){
                                value = UnitConverstion.getCmByInch(Integer.parseInt(value))+"";
                            }
                            SharedPreferencesUtils.savePersonInfo(context, null, null, value, null, null, null);
                            break;
                        case "weight":
                            int unit1 = SharedPreferencesUtils.getPersonInfo(context).getUnit();
                            if(unit1 ==1){
                                value = UnitConverstion.getKgByPound(Integer.parseInt(value))+"";
                            }
                            SharedPreferencesUtils.savePersonInfo(context, null, null, null, value, null, null);
                            break;
                        case "target":
                            SharedPreferencesUtils.savePersonInfo(context, null, null, null, null, null, value);
                            break;
                    }
                    setData();
                }
                if (commonDialog != null) {
                    commonDialog.dismiss();
                }
            }
        });
        commonDialog.setContent(view);
        commonDialog.setCancelable(true);
        commonDialog.show();
    }
}
