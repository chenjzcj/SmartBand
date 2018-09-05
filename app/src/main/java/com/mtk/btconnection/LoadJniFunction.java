package com.mtk.btconnection;

import com.mtk.util.LogUtils;
import com.mtk.util.ToastUtils;

/**
 * This class is used to load JNI function.
 */
public class LoadJniFunction {
    private static final String LIB_NAME = "Command";
    public static final int CMD_1 = 1;
    public static final int CMD_2 = 2;//将手表时间与手机时间同步的命令
    public static final int CMD_3 = 3;
    public static final int CMD_4 = 4;
    public static final int CMD_5 = 5;
    public static final int CMD_6 = 6;
    public static final int CMD_7 = 7;
    public static final int CMD_8 = 8;

    // Load native library
    static {
        try {
            System.loadLibrary(LIB_NAME);
        } catch (Throwable throwable) {
            //报错的解决方法http://blog.csdn.net/liyx2018/article/details/51516513
            throwable.printStackTrace();
            ToastUtils.showLong("throwable = " + throwable);
        }
    }

    public native byte[] getDataCmdFromJni(int len, String arg);

    public native int getCmdTypeFromJni(byte[] command, int commandlenth);

    public native int getDataLenthFromJni(byte[] command, int commandlenth);

    /**
     * Call JNI function to get data command.
     *
     * @param len data length
     * @return the command data
     */
    public byte[] getDataCmd(int len, String arg) {
        return getDataCmdFromJni(len, arg);
    }

    /**
     * Call JNI function to get operation command.
     *
     * @param commandlenth data length
     * @return the command data
     */
    public int getCmdType(byte[] command, int commandlenth) {
        return getCmdTypeFromJni(command, commandlenth);
    }

    /**
     * 获取数据长度
     *
     * @param command      命令字节码
     * @param commandlenth 命令长度
     * @return 数据长度
     */
    public int getDataLenth(byte[] command, int commandlenth) {
        return getDataLenthFromJni(command, commandlenth);
    }

}
