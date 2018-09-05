package com.mtk.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.mtk.view.dialog.CommonDialog;
import com.ruanan.btnotification.R;

/**
 * Created by MZIA(527633405@qq.com) on 2016/9/5 0005 10:47
 * 对话框帮助类
 */
public class DialogHelper {

    /**
     * 显示启动辅助功能对话框
     *
     * @param context Context
     */
    public static void showAccessibilityPrompt(final Context context) {
        CommonDialog commonDialog = new CommonDialog(context);
        commonDialog.setTitle(R.string.accessibility_prompt_title);
        commonDialog.setMessage(R.string.accessibility_prompt_content);
        commonDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        commonDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
            }
        });
        commonDialog.show();
    }
}
