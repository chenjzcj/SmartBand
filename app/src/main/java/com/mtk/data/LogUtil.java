package com.mtk.data;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * This class is used for saving logs to file.
 */
public class LogUtil {
    // Debugging
    private static final String LOG_TAG = "LogUtil";
    //private static final int MAX_LOG_COUNT = 5;
    //private static final String LOG_FILE_PREFIX = "MTK_LOG_";
    //private static final String LOG_FILE_POSTFIX = ".log";    
    private static LogUtil sINSTANCE = null;
    private LogDumper mLogDumper = null;
    private final int mPId;

    private String mLogPath;

    public static LogUtil getInstance(Context context) {
        if (sINSTANCE == null) {
            sINSTANCE = new LogUtil(context);
        }
        return sINSTANCE;
    }

    private LogUtil(Context context) {
        init(context);  
        mPId = android.os.Process.myPid();
    }
    // Initialize log path
    void init(Context context) {
        // Prefer to save log in external storage
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mLogPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mLogPath = mLogPath + File.separator + context.getPackageName();
        } else {
            mLogPath = context.getFilesDir().getAbsolutePath();
        }
        File file = new File(mLogPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        /*
         * // Only save MAX_LOG_COUNT logs File[] allLogs = file.listFiles(); if (allLogs.length > MAX_LOG_COUNT) {
         * Arrays.sort(allLogs, new FileComparator()); int deleteLogCout = allLogs.length - MAX_LOG_COUNT + 1; for (int index
         * = 0; index < deleteLogCout; index++) { allLogs[index].delete(); } }
         */
        Log.i(LOG_TAG, "init(), Log file path=" + mLogPath);
    }
    
    
    // Sort file by file name
    /*
    private class FileComparator implements Comparator<File> {
        @Override
        public int compare(File leftFile, File rightFile) {
            return leftFile.getName().compareTo(rightFile.getName());
        } 
         
      } 
    */
    public void start() {
        Log.i(LOG_TAG, "Log is running");
        if (mLogDumper == null) {
            mLogDumper = new LogDumper(String.valueOf(mPId), mLogPath);
        }
        mLogDumper.start();
    }
    public void stop() {
        Log.i(LOG_TAG, "Log is stopped");
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }
    public boolean isStarted() {
        return mLogDumper != null;
    }

    private class LogDumper extends Thread {

        private Process mLogcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String mCmds = null;
        private final String mPID;
        private FileWriter  mLogWriter = null;
        //private String mLogFile = null;
        final String filename = "BTNotification_Log.txt";
        

        public LogDumper(String pid, String dir) {
            mPID = pid;
            File logFile = new File(dir,filename);
            if (!logFile.exists()) {
                try
                {
                   logFile.createNewFile();
                } 
                catch (IOException e)
                {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
                }
            }
            
            try {
                mLogWriter = new FileWriter(logFile,true);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            //}
            /**
             * 
             * Log level*:v , *:d , *:w , *:e , *:f , *:s
             * 
             */
            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";  // Save all logs
            // cmds = "logcat -s way";
            mCmds = "logcat *:e *:w *:i | grep \"(" + mPID + ")\"";
        }

        public void stopLogs() {
            mRunning = false;
        }
        @Override
        public void run() {
            try {
                mLogcatProc = Runtime.getRuntime().exec(mCmds);
                mReader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning) {
                    line = mReader.readLine();
                    if (line == null) {
                        break;
                    }

                    if (line.length() == 0) {
                        continue;
                    }
                    if (mLogWriter != null && line.contains(mPID)) {
                        mLogWriter.write((Util.getFormatedDate() + "  " + line + "\n"));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (mLogcatProc != null) {
                    mLogcatProc.destroy();
                    mLogcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (mLogWriter != null) {
                    try {
                        mLogWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mLogWriter = null;
                }
            }
        }
    }
}
