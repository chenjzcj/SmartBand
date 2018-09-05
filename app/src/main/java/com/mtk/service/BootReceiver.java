/**
 * 
 */
package com.mtk.service;

import com.mtk.data.Log;
import com.mtk.data.PreferenceData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class receives BOOT_COMPLETED action to start NotificationWatcher main service.
 */
public class BootReceiver extends BroadcastReceiver {
    // Debugging
    private static final String LOG_TAG = "BootReceiver";

    public BootReceiver() {
        Log.i(LOG_TAG, "BootReceiver(), BootReceiver created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "onReceive(), action=" + intent.getAction());
        // If notification service or SMS service is enabled, need to start main service after BOOT_COMPLETED;
        boolean isServiceEnabled = (PreferenceData.isNotificationServiceEnable() || PreferenceData.isSmsServiceEnable());
        Log.i(LOG_TAG, "BootReceiver(), isServiceEnabled=" + isServiceEnabled);
        
        if (isServiceEnabled) {
            // Start main service
            Log.i(LOG_TAG, "BootReceiver(), Start MainService!");
            context.startService(new Intent(context, MainService.class));
        }
    }
}