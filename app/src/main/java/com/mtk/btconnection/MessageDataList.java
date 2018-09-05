package com.mtk.btconnection;

import android.content.Context;

import com.mtk.data.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used for saving message data that failed to send to remote device.
 * When connection is resumed, these data will be sent again.
 */
public class MessageDataList {
    // Debugging
    private static final String LOG_TAG = "MessageDataList";
    // The file to save message list
    private static final String SAVE_FILE_NAME = "MessageDataList";
    // The maximum count of messages need to save.
    private static final int MAX_MSG_COUNT = 5;
    private LinkedList<byte[]> mMsgList = null;
    private Context mContext = null;

    public MessageDataList(Context context) {
        Log.i(LOG_TAG, "MessageList(), MessageList created!");
        mContext = context;
        loadMessageDataList();
    }

    /**
     * 保存发送失败的数据
     *
     * @param msgData 消息数据
     */
    public void saveMessageData(byte[] msgData) {
        Log.i(LOG_TAG, "saveMessageData(), msgData=" + Arrays.toString(msgData));
        // remove redundant message
        if (mMsgList.size() >= MAX_MSG_COUNT) {
            mMsgList.remove(0);
        }
        mMsgList.add(msgData);
    }

    /**
     * 获取消息数据列表
     *
     * @return List<byte>
     */
    public List<byte[]> getMessageDataList() {
        Log.i(LOG_TAG, "getMessageDataList(), msgData=" + mMsgList);
        if (mMsgList == null) {
            loadMessageDataList();
        }
        return mMsgList;
    }

    /**
     * 加载消息数据列表
     */
    private void loadMessageDataList() {
        Log.i(LOG_TAG, "loadMessageDataList(),  file_name= " + SAVE_FILE_NAME);
        try {
            Object obj = (new ObjectInputStream(mContext.openFileInput(SAVE_FILE_NAME))).readObject();
            if (obj instanceof LinkedList<?>) {
                mMsgList = (LinkedList<byte[]>) obj;
            }
        } catch (ClassNotFoundException | IOException exception) {
            exception.printStackTrace();
        }
        // If loads failed from file, create a new msg List
        if (mMsgList == null) {
            mMsgList = new LinkedList<>();
        }
    }

    /**
     * 保存消息数据列表到文件中
     */
    public void saveMessageDataList() {
        Log.i(LOG_TAG, "saveMessageDataList(),  file_name= " + SAVE_FILE_NAME);
        if (mMsgList == null) {
            return;
        }
        try {
            FileOutputStream fileoutputstream;
            ObjectOutputStream objectoutputstream;
            fileoutputstream = mContext.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            objectoutputstream = new ObjectOutputStream(fileoutputstream);
            // Save IgnoreList to file
            objectoutputstream.writeObject(mMsgList);
            objectoutputstream.close();
            fileoutputstream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        Log.i(LOG_TAG, "saveMessageDataList(),  mMsgList= " + mMsgList);
    }
}
