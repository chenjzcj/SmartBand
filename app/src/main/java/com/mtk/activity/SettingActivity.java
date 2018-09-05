/**
 * 
 */
package com.mtk.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.mtk.Constants;
import com.mtk.data.Log;
import com.mtk.data.PreferenceData;
import com.mtk.service.MainService;
import com.ruanan.btnotification.R;

/**
 * This activity is used for application settings. User can select whether enable Notification Service and SMS Service
 */
public class SettingActivity extends PreferenceActivity {
    // Debugging
    private static final String LOG_TAG = "SettingActivity";
    // Intent to ACCESSIBILITY_SETTINGS
    private static final Intent ACCESSIBILITY_INTENT = new Intent("android.settings.ACCESSIBILITY_SETTINGS");

    public SettingActivity() {
        Log.i(LOG_TAG, "SettingActivity(), Create SettingActivity!");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle bundle) {
        Log.i(LOG_TAG, "onCreate(), Create setting activity UI");
        super.onCreate(bundle);
        addPreferencesFromResource(R.layout.setting_activity_layout);
        // Create sms service preference
        CheckBoxPreference smsPreference = (CheckBoxPreference) findPreference(PreferenceData.PREFERENCE_KEY_SMS);
        OnPreferenceClickListener onSmsPreferenceClickListener = new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainService service = MainService.getInstance();
                if (service == null) {
                    return false;
                }
                if (((CheckBoxPreference) preference).isChecked()) {
                    service.startSmsService();
                } else {
                    service.stopSmsService();
                }
                return true;
            }
        };
        smsPreference.setOnPreferenceClickListener(onSmsPreferenceClickListener);
        // Create call service preference
        CheckBoxPreference callPreference = (CheckBoxPreference) findPreference(PreferenceData.PREFERENCE_KEY_CALL);
        OnPreferenceClickListener onCallPreferenceClickListener = new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainService service = MainService.getInstance();
                if (service == null) {
                    return false;
                }
                if (((CheckBoxPreference) preference).isChecked()) {
                    service.startCallService();
                } else {
                    service.stopCallService();
                }
                return true;
            }
        };
        callPreference.setOnPreferenceClickListener(onCallPreferenceClickListener);
        // Create notification service preference
        CheckBoxPreference notifiPreference = (CheckBoxPreference) findPreference(PreferenceData.PREFERENCE_KEY_NOTIFI);
        OnPreferenceClickListener onNotifiPreferenceClickListener = new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainService service = MainService.getInstance();
                if (service == null) {
                    return false;
                }
                if (((CheckBoxPreference) preference).isChecked()) {
                    service.startNotificationService();
                    if (!MainService.isNotificationServiceActived()) {
                        // 显示弹出框
                        showAccessibilityPrompt();
                    }
                } else {
                    service.stopNotificationService();
                }
                return true;
            }
        };

        notifiPreference.setOnPreferenceClickListener(onNotifiPreferenceClickListener);
        // Prompt user to enable accessibility
        if (notifiPreference.isChecked() && !MainService.isNotificationServiceActived()) {
            showAccessibilityPrompt();
        }
        // Create accessibility setting service preference
        Preference accessibilityPreference = findPreference(PreferenceData.PREFERENCE_KEY_ACCESSIBILITY);
        OnPreferenceClickListener onAccessibilityPreferenceClickListener = new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Enter accessibility setting page
                startActivity(SettingActivity.ACCESSIBILITY_INTENT);
                return true;
            }
        };
        accessibilityPreference.setOnPreferenceClickListener(onAccessibilityPreferenceClickListener);
        // Create selection notifications preference
        Preference selectNotifiPreference = findPreference(PreferenceData.PREFERENCE_KEY_SELECT_NOTIFICATIONS);
        OnPreferenceClickListener onSelectNotifiPreferenceClickListener = new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Enter select app notification page
                // startActivity(new Intent(SettingActivity.this, SelectNotifiActivity.class));
                startActivity(new Intent(SettingActivity.this, SelectNotifiActivity.class));
                return true;
            }
        };
        selectNotifiPreference.setOnPreferenceClickListener(onSelectNotifiPreferenceClickListener);
        Preference selectBlocksPreference = findPreference(PreferenceData.PREFERENCE_KEY_SELECT_BLOCKS);
        OnPreferenceClickListener onselectBlocksPreferenceClickListener = new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Enter select app notification page
                startActivity(new Intent(SettingActivity.this, SelectBlocksAppActivity.class));
                return true;
            }
        };
        selectBlocksPreference.setOnPreferenceClickListener(onselectBlocksPreferenceClickListener);
        // Create show connection status preference
        CheckBoxPreference showConnectNotifiPreference = (CheckBoxPreference) findPreference(PreferenceData.PREFERENCE_KEY_SHOW_CONNECTION_STATUS);
        OnPreferenceClickListener onShowConnectNotifiPreferenceClickListener = new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainService service = MainService.getInstance();
                if (service != null) {
                    service.updateConnectionStatus(false);
                }
                return true;
            }
        };
        showConnectNotifiPreference.setOnPreferenceClickListener(onShowConnectNotifiPreferenceClickListener);

        // Create always forward preference
        CheckBoxPreference pushCasePreference = (CheckBoxPreference) findPreference(PreferenceData.PREFERENCE_KEY_ALWAYS_FORWARD);
        OnPreferenceClickListener onPushCasePreferenceClickListener = new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Nothing to do now
                return true;
            }
        };
        pushCasePreference.setOnPreferenceClickListener(onPushCasePreferenceClickListener);
        // Create current version preference
        Preference currentVersionPreference = findPreference(PreferenceData.PREFERENCE_KEY_CURRENT_VERSION);
        currentVersionPreference.setSummary(getCurrentVersion());
        // 开启服务
        if (smsPreference.isChecked() || notifiPreference.isChecked()) {
            startMainService();
        }

    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy()");
        super.onDestroy();

    }

    public void onResume() {
        super.onResume();
    }

    private String getCurrentVersion() {
        String versionName;
        int versionCode;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // Set them by default value
            versionName = Constants.NULL_TEXT_NAME;
            versionCode = 0;
            e.printStackTrace();
        }

        return (versionName + " (" + versionCode + ")");
    }

    private void startMainService() {
        Intent startServiceIntent = new Intent(this, MainService.class);
        startService(startServiceIntent);
    }

    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "onStart(), SettingActivity starts!");
        super.onStart();

    }

    private void showAccessibilityPrompt() {

        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(R.string.accessibility_prompt_title);
        builder.setMessage(R.string.accessibility_prompt_content);
        // Cancel, do nothing
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Go to accessibility settings
        builder.setPositiveButton(R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(SettingActivity.ACCESSIBILITY_INTENT);
            }
        });
        builder.create().show();
    }

}
