package com.mtk.map;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.mtk.MyApp;
import com.mtk.service.SmsReceiver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SmsContentObserver extends ContentObserver {
    private static final String TAG = "MessageObserver";
    // message id(long) <--> message type(int)
    private static final Context sContext = MyApp.getInstance().getApplicationContext();
    private HashMap<Long, Integer> previousMessage;
    private SmsController mSmsController = null;
    private final String HEADER = "telecom/msg/";

    public SmsContentObserver(SmsController smsController) {
        super(new Handler());
        mSmsController = smsController;
        previousMessage = new HashMap<Long, Integer>();
        new DatabaseMonitor(DatabaseMonitor.MONITER_TYPE_ONLY_QUERY).start();
    }

    @Override
    public void onChange(boolean onSelf) {
        super.onChange(onSelf);
        Log.i(TAG, "DataBase State Changed");
        new DatabaseMonitor(DatabaseMonitor.MONITER_TYPE_QUERY_AND_NOTIFY).start();
    }

    public class DatabaseMonitor extends Thread {
        public final static int MONITER_TYPE_ONLY_QUERY = 0;
        public final static int MONITER_TYPE_QUERY_AND_NOTIFY = 1;

        private int mQueryType = 0;

        public DatabaseMonitor(int type) {
            mQueryType = type;
        }

        public void run() {
            if (MONITER_TYPE_ONLY_QUERY == mQueryType) {
                try {
                    query();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (MONITER_TYPE_QUERY_AND_NOTIFY == mQueryType) {
                try {
                    queryAndNotify();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                // do nothing
                Log.i(TAG, "invalid monitor type:" + mQueryType);
            }
        }

        private synchronized void query() {
            queryMessage(previousMessage);
            Log.i(TAG, "query: size->" + previousMessage.size());
        }

        // @SuppressWarnings("unused")
        private synchronized void queryAndNotify() {
            HashMap<Long, Integer> currentMessage = new HashMap<Long, Integer>();
            @SuppressWarnings("rawtypes")
            Iterator iterator;
            String newFolder;
            String oldFolder;
            queryMessage(currentMessage);

            Log.i(TAG, "database has been changed, mType is " + " previous size is " + previousMessage.size()
                    + "current size is " + currentMessage.size());

            // if previous message is smaller than current, new message is received
            if (previousMessage.size() < currentMessage.size()) {
                // find the new message
                iterator = currentMessage.entrySet().iterator();
                while (iterator.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Long key = (Long) entry.getKey();
                    // messasge is not in previous messages and the type is

                    String folder = revertMailboxType(currentMessage.get(key));
                    if (!previousMessage.containsKey(key) && folder != null && folder.equals(MapConstants.Mailbox.INBOX)) {
                        mSmsController.onMessageEvent(key, HEADER + folder, MapConstants.EVENT_NEW);
                        Intent newSMSIntent = new Intent();
                        newSMSIntent.setAction(SmsReceiver.SMS_RECEIVED);
                        sContext.sendBroadcast(newSMSIntent);

                    }
                }
            } else {
                iterator = previousMessage.entrySet().iterator();
                while (iterator.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Long key = (Long) entry.getKey();
                    // messasge is not in previous messages and the type is
                    if (!currentMessage.containsKey(key)) {
                        oldFolder = revertMailboxType((previousMessage.get(key)));
                        mSmsController.onMessageEvent(key, HEADER + oldFolder, MapConstants.EVENT_DELETE);
                    } else {
                        oldFolder = revertMailboxType((Integer) entry.getValue());
                        newFolder = revertMailboxType((currentMessage.get(key)));

                        // log("id " + key +"oldFolder is " + oldFolder + "new folder is " + newFolder);

                        if (newFolder == null || oldFolder == null || oldFolder.equals(newFolder)) {
                            continue;
                        }
                        // check to determine message to be deleted or shifted

                        if (newFolder.equals(MapConstants.Mailbox.DELETED)) {
                            // mSmsController.onMessageEvent(key, HEADER + oldFolder,MapConstants.EVENT_SHIFT);
                        } else {
                            mSmsController.onMessageEvent(key, HEADER + oldFolder, MapConstants.EVENT_SHIFT);
                        }
                    }
                }
            }
            previousMessage = currentMessage;
        }

        private void queryMessage(HashMap<Long, Integer> info) {

            Cursor messageCursor = null;
            try {
                messageCursor = sContext.getContentResolver().query(Uri.parse(MapConstants.SMS_CONTENT_URI),
                        new String[]{MapConstants._ID, MapConstants.TYPE}, null, null, null);
                if (messageCursor != null) {

                    while (messageCursor.moveToNext()) {
                        info.put(messageCursor.getLong(0), messageCursor.getInt(1));
                    }
                }
            } catch (Exception e) {
                if (messageCursor != null) {
                    messageCursor.close();
                }
            } finally {
                if (messageCursor != null) {
                    messageCursor.close();
                }

            }

        }

        private String revertMailboxType(int smsMailboxType) {
            switch (smsMailboxType) {
                case MapConstants.MESSAGE_TYPE_INBOX:
                    return MapConstants.Mailbox.INBOX;
                case MapConstants.MESSAGE_TYPE_OUTBOX:
                    return MapConstants.Mailbox.OUTBOX;
                case MapConstants.MESSAGE_TYPE_SENT:
                    return MapConstants.Mailbox.SENT;
                case MapConstants.MESSAGE_TYPE_DRAFT:
                    return MapConstants.Mailbox.DRAFT;
                default:
                    return MapConstants.Mailbox.DELETED;
            }
        }
    }
}
