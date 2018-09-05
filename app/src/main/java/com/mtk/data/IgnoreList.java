package com.mtk.data;

import android.content.Context;

import com.mtk.MyApp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashSet;

/**
 * This class is used for save ignored and excluded application list.
 * Their notification will not be pushed to remote device.
 * IgnoreList is a single class.
 */
public final class IgnoreList {
    // Debugging
    private static final String LOG_TAG = "IgnoreList";
    // EXCLUSION_LIST, will be processed specially.
    private static final String[] EXCLUSION_LIST = {
            "android", "com.android.mms", "com.android.phone",
            "com.android.providers.downloads", "com.android.bluetooth",
            "com.mediatek.bluetooth", "com.htc.music", "com.lge.music",
            "com.sec.android.app.music", "com.sonyericsson.music",
            "com.ijinshan.mguard"
    };
    // The file to save IgnoreList
    private static final String SAVE_FILE_NAME = "IgnoreList";
    private HashSet<String> mIgnoreList = null;

    private static final IgnoreList INSTANCE = new IgnoreList();
    private Context mContext = null;

    private IgnoreList() {
        Log.i(LOG_TAG, "IgnoreList(), IgnoreList created!");
        mContext = MyApp.getInstance().getApplicationContext();
    }

    /**
     * Return the instance of IgnoreList class.
     *
     * @return the IgnoreList instance
     */
    public static IgnoreList getInstance() {
        return INSTANCE;
    }

    /**
     * Return the ignored application list.
     *
     * @return the ignore list
     */
    public HashSet<String> getIgnoreList() {
        if (mIgnoreList == null) {
            loadIgnoreListFromFile();
        }
        Log.i(LOG_TAG, "getIgnoreList(), mIgnoreList = " + mIgnoreList.toString());
        return mIgnoreList;
    }

    private void loadIgnoreListFromFile() {
        Log.i(LOG_TAG, "loadIgnoreListFromFile(),  file_name= " + SAVE_FILE_NAME);
        if (mIgnoreList == null) {
            try {
                Object obj = (new ObjectInputStream(mContext.openFileInput(SAVE_FILE_NAME))).readObject();
                mIgnoreList = (HashSet<String>) obj;
            } catch (ClassNotFoundException | IOException exception) {
                exception.printStackTrace();
            }
        }
        if (mIgnoreList == null) {
            mIgnoreList = new HashSet<>();
        }
    }

    /**
     * Save ignored applications to file.
     *
     * @param ignoreList ignored applications list
     */
    public void saveIgnoreList(HashSet<String> ignoreList) {
        Log.i(LOG_TAG, "setIgnoreList(),  file_name= " + SAVE_FILE_NAME);
        FileOutputStream fileoutputstream;
        ObjectOutputStream objectoutputstream;
        try {
            fileoutputstream = mContext.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(ignoreList);
            objectoutputstream.close();
            fileoutputstream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }
        mIgnoreList = ignoreList;
        Log.i(LOG_TAG, "setIgnoreList(),  mIgnoreList= " + mIgnoreList);
    }

    /**
     * Return the exclude application list, these applications will not show to user for selection.
     *
     * @return the exclude application list
     */
    public HashSet<String> getExclusionList() {
        HashSet<String> exclusionList = new HashSet<>();
        Collections.addAll(exclusionList, EXCLUSION_LIST);
        Log.i(LOG_TAG, "setIgnoreList(),  exclusionList=" + exclusionList);
        return exclusionList;
    }
}
