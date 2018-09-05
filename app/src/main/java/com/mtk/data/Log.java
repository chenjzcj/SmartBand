package com.mtk.data;

public class Log {

    private static final int LEVEL = android.util.Log.VERBOSE;

    static public void d(String tag, String msgFormat, Object... args) {
        if (LEVEL <= android.util.Log.DEBUG) {
            try {
                android.util.Log.d(tag, String.format(msgFormat, args));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    static public void d(String tag, Throwable t, String msgFormat, Object... args) {
        if (LEVEL <= android.util.Log.DEBUG) {
            try {
                android.util.Log.d(tag, String.format(msgFormat, args), t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static public void i(String tag, String msgFormat, Object... args) {
        if (LEVEL <= android.util.Log.INFO) {
            try {
                android.util.Log.i(tag, String.format(msgFormat, args));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static public void i(String tag, Throwable t, String msgFormat, Object... args) {
        if (LEVEL <= android.util.Log.INFO) {
            try {
                android.util.Log.i(tag, String.format(msgFormat, args), t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static public void w(String msgFormat, Object... args) {
        if (LEVEL <= android.util.Log.WARN) {
            try {
                android.util.Log.w(com.mtk.btconnection.BluetoothConnection.LOG_TAG, String.format(msgFormat, args));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static public void w(String tag, Throwable t, String msgFormat, Object... args) {
        if (LEVEL <= android.util.Log.WARN) {
            try {
                android.util.Log.w(tag, String.format(msgFormat, args), t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static public void e(String tag, String msgFormat, Object... args) {
        if (LEVEL <= android.util.Log.ERROR) {
            try {
                android.util.Log.e(tag, String.format(msgFormat, args));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static public void e(String tag, Throwable t, String msgFormat, Object... args) {
        if (LEVEL <= android.util.Log.ERROR) {
            try {
                android.util.Log.e(tag, String.format(msgFormat, args), t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


