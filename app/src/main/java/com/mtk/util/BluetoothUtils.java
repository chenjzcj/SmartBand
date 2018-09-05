package com.mtk.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.ruanan.btnotification.R;

/**
 * 蓝牙工具类
 */
public class BluetoothUtils {

    private static final BluetoothAdapter mBluetoothAdapter;

    static {
        //获取蓝牙adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 打开或者关闭蓝牙
     */
    public static void openOrCloseBT(Context context) {
        if (mBluetoothAdapter == null) {
            ToastUtils.showShortToast(context, context.getResources().getString(R.string.no_found_drivers));
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                closeBluetooth();
                ToastUtils.showShortToast(context, context.getResources().getString(R.string.bluetooth_has_been_closed));
            } else {
                openBluetooth(context);
            }
        }
    }

    /**
     * 开启蓝牙
     */
    private static void openBluetooth(Context context) {
        // 如果本地蓝牙没有开启，则开启
        if (!mBluetoothAdapter.isEnabled()) {
            context.startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }
    }

    /**
     * 关闭蓝牙
     */
    private static void closeBluetooth() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }


    /**
     * 配对蓝牙Intent
     */
    public static Intent repairBluetooth() {
        return new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
    }

    /**
     * 获取蓝牙打开关闭状态(连接与否并不知道)
     *
     * @return true表示蓝牙已经打开
     */
    public static boolean getBlutootnLinkState() {
        return (mBluetoothAdapter != null) && (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_OFF);
    }
}
