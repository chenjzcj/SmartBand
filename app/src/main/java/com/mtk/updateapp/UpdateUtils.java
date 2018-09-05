package com.mtk.updateapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.mtk.Constants;
import com.mtk.MyApp;
import com.mtk.util.LogUtils;
import com.mtk.util.NetUtils;
import com.mtk.util.NumUtil;
import com.mtk.util.ToastUtils;
import com.mtk.view.dialog.CommonDialog;
import com.mtk.view.dialog.DialogHelper;
import com.mtk.view.dialog.WaitDialog;
import com.ruanan.btnotification.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * @author YGH 2016-4-30下午2:42:55 TODO 应用工具更新类
 */
public class UpdateUtils {

    //app版本更新
    public static final int UPDATA_NONEED = 0;        //app不需要更新
    public static final int UPDATA_CLIENT = 1;        //更新app
    public static final int GET_UNDATAINFO_ERROR = 2; //获取数据失败
    public static final int GET_UNDATAINFO_SUCCESS = 3;//获取数据成功
    public static final int DOWN_ERROR = 4;        //下载失败

    public static WaitDialog waitDialog;
    @SuppressLint("HandlerLeak")
    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Context context = MyApp.getInstance();
            hideWaitDialog();
            switch (msg.what) {
                case UpdateUtils.DOWN_ERROR:
                    ToastUtils.showShortToast(context, context.getResources().getString(R.string.download_failed));
                    break;
                case UpdateUtils.GET_UNDATAINFO_SUCCESS: //更新数据成功
                    hideWaitDialog();
                    break;
                case UpdateUtils.GET_UNDATAINFO_ERROR:   //更新数据错误
                    hideWaitDialog();
                    ToastUtils.showShortToast(context, context.getString(R.string.failed_to_get_server_updates));
                    break;
            }
        }
    };
    private static Context context;

    /**
     * 检测版本更新
     *
     * @param isClient 自动更新还是手动更新
     * @param context  上下文
     */
    public static void checkAppUpdate(final Boolean isClient, final Context context) {
        UpdateUtils.context = context;
        try {
            if (!NetUtils.isNetConnected(context)) {
                ToastUtils.showShortToast(context, context.getString(R.string.failed_to_get_server_updates));
                return;
            }
            //如果是手动请求更新，就显示等待进度框
            if (!isClient) {
                if (waitDialog == null) {
                    waitDialog = DialogHelper.getWaitDialog((Activity) context, R.string.is_download_the_update);
                }
                waitDialog.setCancelable(false);
                waitDialog.show();
            }
            String able = context.getResources().getConfiguration().locale.getCountry(); //通过系统语言获取国家，用来区分国内用户和国外用户
            LogUtils.i("able =" + able);
            if (able.equals("CN")) {
                //如果用户是国内用户，就更新国内版的蓝牙通知
                getUpdateInfoFromServer(isClient, context, Constants.UPDATE_APP_URL_CHINA);
                LogUtils.i("更新国内版本的蓝牙通知");
            } else {
                //如果用户不是国内用户，就更新国外版的蓝牙通知
                getUpdateInfoFromServer(isClient, context, Constants.UPDATE_APP_URL_FOREIGN);
                LogUtils.i("更新国外版本的蓝牙通知");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从服务器获取更新信息
     *
     * @param isClient 手动请求还是自动请求
     * @param context  上下文
     * @param url      更新链接
     */
    private static void getUpdateInfoFromServer(final Boolean isClient, final Context context, String url) {
        x.http().get(new RequestParams(url), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.i("onSuccess");
                Message message = new Message();
                message.what = UpdateUtils.GET_UNDATAINFO_SUCCESS;
                handler.sendMessage(message);
                try {
                    ByteArrayInputStream is = new ByteArrayInputStream(result.getBytes());
                    String localVersion = getVersionName(context);            //获取当前版本名称
                    UpdataInfo info = UpdataInfoParser.getUpdataInfo(is); //获取更新信息
                    if (NumUtil.parseVerstion(localVersion) >= NumUtil.parseVerstion(info.getVersion())) {
                        if (!isClient) {
                            ToastUtils.showShortToast(context, context.getResources().getString(R.string.is_the_latest_version_do_not_need_to_update));
                        }
                    } else {
                        ////此处手环不让用户更新,所以一直为显示为最新版本,不弹出更新对话框
                        //showUpdataDialog(UpdateUtils.context, info);
                        ToastUtils.showShortToast(context, context.getResources().getString(R.string.is_the_latest_version_do_not_need_to_update));
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtils.i("ex = " + ex);
                Message message = new Message();
                message.what = UpdateUtils.GET_UNDATAINFO_ERROR;
                handler.sendMessage(message);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取app版本名称
     *
     * @param context
     * @return
     * @throws Exception
     */
    private static String getVersionName(Context context) throws Exception {
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
    }

    /**
     * 隐藏dialog
     */
    public static void hideWaitDialog() {
        if (waitDialog != null) {
            waitDialog.hide();
        }
    }

    /**
     * 弹出对话框通知用户更新程序
     *
     * @param context 上下文
     * @param info    版本更新信息
     */
    protected static void showUpdataDialog(final Context context, final UpdataInfo info) {
        //info.setForce("false");//仅供测试阶段使用
        CommonDialog commonDialog = new CommonDialog(context);
        commonDialog.setCancelable(info.getForce().equals("false"));
        commonDialog.setTitle(context.getResources().getString(R.string.version_upgrade));
        commonDialog.setMessage(context.getResources().getString(R.string.monitoring_to_the_latest_version));
        commonDialog.setPositiveButton(context.getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk(context, info);
            }
        });
        commonDialog.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (info.getForce().equals("false"))
                    dialog.cancel();
            }
        });
        commonDialog.show();
    }

    /**
     * 从服务器中下载APK
     *
     * @param context 上下文
     * @param info    更新信息
     */
    protected static void downLoadApk(final Context context, final UpdataInfo info) {
        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage(context.getResources().getString(R.string.is_download_the_update));
        pd.setCancelable(false);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = DownLoadManager.getFileFromServer(info.getUrl(), pd);
                    sleep(3000);
                    installApk(context, file);
                    pd.dismiss();
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = UpdateUtils.DOWN_ERROR;
                    handler.sendMessage(msg);
                    pd.dismiss();
                }
            }
        }.start();
    }

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param file    安装文件
     */
    protected static void installApk(Context context, File file) {
        Intent intents = new Intent();
        intents.setAction("android.intent.action.VIEW");
        intents.addCategory("android.intent.category.DEFAULT");
        intents.setType("application/vnd.android.package-archive");
        intents.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intents);
    }
}
