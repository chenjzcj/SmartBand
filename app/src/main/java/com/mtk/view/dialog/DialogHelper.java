package com.mtk.view.dialog;

import android.app.Activity;
import android.content.Context;

import com.ruanan.btnotification.R;


/**
 * 对话框工具类
 */
public class DialogHelper {

    public static CommonDialog getPinterestDialog(Context context) {
        return new CommonDialog(context, R.style.dialog_waiting);
    }

    public static CommonDialog getPinterestDialogCancelable(Context context) {
        CommonDialog dialog = new CommonDialog(context, R.style.dialog_waiting);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static WaitDialog getWaitDialog(Activity activity, int message) {
        WaitDialog dialog = null;
        try {
            dialog = new WaitDialog(activity, R.style.dialog_waiting);
            dialog.setMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    public static WaitDialog getWaitDialog(Activity activity, String message) {
        WaitDialog dialog = null;
        try {
            dialog = new WaitDialog(activity, R.style.dialog_waiting);
            dialog.setMessage(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dialog;
    }

    public static WaitDialog getCancelableWaitDialog(Activity activity,
                                                     String message) {
        WaitDialog dialog = null;
        try {
            dialog = new WaitDialog(activity, R.style.dialog_waiting);
            dialog.setMessage(message);
            dialog.setCancelable(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dialog;
    }

}
