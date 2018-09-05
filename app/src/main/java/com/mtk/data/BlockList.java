package com.mtk.data;

import android.content.Context;

import com.mtk.MyApp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

/**
 * This class is used for save ignored and excluded application list.
 * Their notification will not be pushed to remote device.
 * IgnoreList is a single class.
 */
public final class BlockList {
    // Debugging
    private static final String LOG_TAG = "BlockList";
    private static final String SAVE_FILE_NAME = "BlockList";
    private HashSet<String> mBlockList = null;

    private static final BlockList INSTANCE = new BlockList();
    private Context mContext = null;

    private BlockList() {
        Log.i(LOG_TAG, "BlockList(), BlockList created!");
        mContext = MyApp.getInstance().getApplicationContext();
    }

    /**
     * Return the instance of BlockList class.
     *
     * @return the BlockList instance
     */
    public static BlockList getInstance() {
        return INSTANCE;
    }

    /**
     * Return the ignored and excluded application list.
     *
     * @return the ignored and excluded list
     */
    public HashSet<String> getBlockList() {
        if (mBlockList == null) {
            loadBlockListFromFile();
        }
        Log.i(LOG_TAG, "getBlockList(), mBlockList = " + mBlockList.toString());
        return mBlockList;
    }

    /**
     * 从文件中加载黑名单列表
     */
    private void loadBlockListFromFile() {
        Log.i(LOG_TAG, "loadBlockListFromFile(),  file_name= " + SAVE_FILE_NAME);
        if (mBlockList == null) {
            try {
                Object obj = (new ObjectInputStream(mContext.openFileInput(SAVE_FILE_NAME))).readObject();
                mBlockList = (HashSet<String>) obj;
            } catch (ClassNotFoundException | IOException exception) {
                exception.printStackTrace();
            }
        }
        if (mBlockList == null) {
            mBlockList = new HashSet<>();
        }
    }

    /**
     * Save ignored and excluded applications to file.
     *
     * @param blockList ignored and excluded applications list
     */
    public void saveBlockList(HashSet<String> blockList) {
        Log.i(LOG_TAG, "setIgnoreList(),  file_name= " + SAVE_FILE_NAME);
        FileOutputStream fileoutputstream;
        ObjectOutputStream objectoutputstream;
        try {
            fileoutputstream = mContext.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(blockList);
            objectoutputstream.close();
            fileoutputstream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }
        mBlockList = blockList;
        Log.i(LOG_TAG, "setIgnoreList(),  mIgnoreList= " + mBlockList);
    }
}
