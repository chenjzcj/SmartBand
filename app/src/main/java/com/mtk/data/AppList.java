package com.mtk.data;

import android.content.Context;

import com.mtk.MyApp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * app列表
 */
public final class AppList {
    // Debugging
    private static final String LOG_TAG = "AppList";

    private static final String SAVE_FILE_NAME = "AppList";
    public static final String MAX_APP = "MaxApp";
    public static final CharSequence BETTRYLOW_APPID = "com.mtk.btnotification.batterylow";
    public static final CharSequence SMSRESULT_APPID = "com.mtk.btnotification.smsresult";
    public static final int CREATE_LENTH = 3;
    private Map<Object, Object> mAppList = null;

    private static final AppList INSTANCE = new AppList();
    private Context mContext = null;

    private AppList() {
        Log.i(LOG_TAG, "AppList(), AppList created!");
        mContext = MyApp.getInstance().getApplicationContext();
    }

    /**
     * Return the instance of AppList class.
     *
     * @return the AppList instance
     */
    public static AppList getInstance() {
        return INSTANCE;
    }

    /**
     * Return the application list.
     *
     * @return the application list
     */
    public Map<Object, Object> getAppList() {
        if (mAppList == null) {
            loadAppListFromFile();
        }
        Log.i(LOG_TAG, "getAppList(), mAppList = " + mAppList.toString());
        return mAppList;
    }

    /**
     * 从文件中加载app列表
     */
    private void loadAppListFromFile() {
        Log.i(LOG_TAG, "loadIgnoreListFromFile(),  file_name= " + SAVE_FILE_NAME);
        if (mAppList == null) {
            try {
                Object obj = (new ObjectInputStream(mContext.openFileInput(SAVE_FILE_NAME))).readObject();
                mAppList = (Map<Object, Object>) obj;
            } catch (ClassNotFoundException | IOException exception) {
                exception.printStackTrace();
            }
        }
        if (mAppList == null) {
            mAppList = new HashMap<>();
        }
    }

    /**
     * Save applications to file.
     *
     * @param appList applications list
     */
    public void saveAppList(Map<Object, Object> appList) {
        Log.i(LOG_TAG, "setIgnoreList(),  file_name= " + SAVE_FILE_NAME);
        FileOutputStream fileoutputstream;
        ObjectOutputStream objectoutputstream;
        try {
            fileoutputstream = mContext.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(mAppList);
            objectoutputstream.close();
            fileoutputstream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }
        mAppList = appList;
        Log.i(LOG_TAG, "setIgnoreList(),  mIgnoreList= " + mAppList);
    }
}
