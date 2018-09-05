package com.mtk.util;

import android.content.Context;
import android.os.Environment;

import com.mtk.MyApp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 通用路径管理工具
 */
public class PathUtils {
    public static String pathPrefix;
    private static File storageDir = null;
    private static PathUtils instance = null;
    public static final String chatPathName = "/chat/";
    public static final String imagePathName = "/image/";
    public static final String voicePathName = "/voice/";
    public static final String videoPathName = "/video/";
    public static final String filePathName = "/file/";
    public static final String logPathName = "/log/";
    public static final String mapPathName = "/map/";
    public static final String commPathName = "/comm/";
    private File chatPath = null;
    private File imagePath = null;
    private File voicePath = null;
    private File videoPath = null;
    private File filePath = null;
    private File logPath = null;
    private File mapPath = null;
    private File commPath = null;

    private PathUtils() {
        initDirs(MyApp.getInstance());
    }

    public static PathUtils getInstance() {
        if (instance == null)
            instance = new PathUtils();
        return instance;
    }

    public void initDirs(Context context) {
        pathPrefix = "/Android/data/" + context.getPackageName() + "/";

        this.voicePath = generateVoicePath(context);
        if (!this.voicePath.exists()) {
            boolean mkdirs = this.voicePath.mkdirs();
            if (!mkdirs) {
                this.voicePath = null;
            }
        }

        this.imagePath = generateImagePath(context);
        if (!this.imagePath.exists()) {
            boolean mkdirs = this.imagePath.mkdirs();
            if (!mkdirs) {
                this.imagePath = null;
            }
        }

        this.chatPath = generateChatPath(context);
        if (!this.chatPath.exists()) {
            boolean mkdirs = this.chatPath.mkdirs();
            if (!mkdirs) {
                this.chatPath = null;
            }
        }

        this.videoPath = generateVideoPath(context);
        if (!this.videoPath.exists()) {
            boolean mkdirs = this.videoPath.mkdirs();
            if (!mkdirs) {
                this.videoPath = null;
            }
        }

        this.filePath = generateFilePath(context);
        if (!this.filePath.exists()) {
            boolean mkdirs = this.filePath.mkdirs();
            if (!mkdirs) {
                this.filePath = null;
            }
        }

        this.logPath = generateLogPath(context);
        if (!this.logPath.exists()) {
            boolean mkdirs = this.logPath.mkdirs();
            if (!mkdirs) {
                this.logPath = null;
            }
        }

        this.mapPath = generateMapPath(context);
        if (!this.mapPath.exists()) {
            boolean mkdirs = this.mapPath.mkdirs();
            if (!mkdirs) {
                this.mapPath = null;
            }
        }

        this.commPath = generateCommPath(context);
        if (!this.commPath.exists()) {
            boolean mkdirs = this.commPath.mkdirs();
            if (!mkdirs) {
                this.commPath = null;
            }
        }
    }

    /**
     * 获取根目录,如果不存在SD卡,则存放在应用私有的文件目录中
     *
     * @param context 上下文
     * @return 返回根目录文件
     */
    private static File getStorageDir(Context context) {
        if (storageDir == null) {
            File localFile = Environment.getExternalStorageDirectory();
            if (localFile.exists())
                return localFile;
            storageDir = context.getFilesDir();
        }
        return storageDir;
    }

    /**
     * 生成地图文件存放根目录
     *
     * @param context Context
     * @return 地图文件根目录
     */
    private static File generateMapPath(Context context) {
        String str = pathPrefix + mapPathName;
        return new File(getStorageDir(context), str);
    }

    public File getMapPath() {
        if (this.mapPath == null) {
            initDirs(MyApp.getInstance());
        }
        return this.mapPath;
    }

    /**
     * 生成通用文件存放根目录
     *
     * @param context Context
     * @return 地图文件根目录
     */
    private static File generateCommPath(Context context) {
        String str = pathPrefix + commPathName;
        return new File(getStorageDir(context), str);
    }

    public File getCommPath() {
        if (this.commPath == null) {
            initDirs(MyApp.getInstance());
        }
        return this.commPath;
    }

    /**
     * 生成日志文件存放根目录
     *
     * @param context Context
     * @return 日志文件根目录
     */
    private static File generateLogPath(Context context) {
        String str = pathPrefix + logPathName;
        return new File(getStorageDir(context), str);
    }

    public File getLogPath() {
        if (this.logPath == null) {
            initDirs(MyApp.getInstance());
        }
        return this.logPath;
    }

    /**
     * 生成普通文件存放根目录
     *
     * @param context Context
     * @return 普通文件根目录
     */
    private static File generateFilePath(Context context) {
        String str = pathPrefix + filePathName;
        return new File(getStorageDir(context), str);
    }

    public File getFilePath() {
        if (this.filePath == null) {
            initDirs(MyApp.getInstance());
        }
        return this.filePath;
    }

    /**
     * 生成视频文件存放根目录
     *
     * @param context Context
     * @return 视频文件根目录
     */
    private static File generateVideoPath(Context context) {
        String str = pathPrefix + videoPathName;
        return new File(getStorageDir(context), str);
    }

    public File getVideoPath() {
        if (this.videoPath == null) {
            initDirs(MyApp.getInstance());
        }
        return this.videoPath;
    }


    /**
     * 生成聊天文件存放根目录
     *
     * @param context Context
     * @return 聊天文件根目录
     */
    private static File generateChatPath(Context context) {
        String str = pathPrefix + chatPathName;
        return new File(getStorageDir(context), str);
    }

    public File getChatPath() {
        if (this.chatPath == null) {
            initDirs(MyApp.getInstance());
        }
        return this.chatPath;
    }

    /**
     * 生成图片文件存放根目录
     *
     * @param context Context
     * @return 图片文件根目录
     */
    private static File generateImagePath(Context context) {
        String str = pathPrefix + imagePathName;
        return new File(getStorageDir(context), str);
    }

    public File getImagePath() {
        if (this.imagePath == null) {
            initDirs(MyApp.getInstance());
        }
        return this.imagePath;
    }

    /**
     * 生成语音文件存放根目录
     *
     * @param context Context
     * @return 语音文件根目录
     */
    private static File generateVoicePath(Context context) {
        String str = pathPrefix + voicePathName;
        return new File(getStorageDir(context), str);
    }

    public File getVoicePath() {
        if (this.voicePath == null) {
            initDirs(MyApp.getInstance());
        }
        return this.voicePath;
    }

    /***************************************************/

    public static String generateVoiceFilePath(String voiceFileName) {
        return PathUtils.getInstance().getVoicePath() + File.separator + voiceFileName + ".amr";
    }

    public static String getVoiceFileName(String direction, String name) {
        return direction + name;
    }

    /***************************************************/
    /**
     * 生成照片文件存放父路径
     */
    public static String generatePhotoDir() {
        File photoDir = new File(PathUtils.getInstance().getImagePath() + File.separator + "photo");
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
        return new File(photoDir.getPath()).getPath();
    }

    /**
     * 生成缓存照片文件存放父路径
     */
    public static String generatePhotoCacheDir() {
        File photoCacheDir = new File(PathUtils.getInstance().getImagePath() + File.separator + "photo_cache");
        if (!photoCacheDir.exists()) {
            photoCacheDir.mkdirs();
        }
        return new File(photoCacheDir.getPath()).getPath();
    }

    /**
     * 生成二维码图片文件路径
     *
     * @param qrcodeFileName 二维码图片文件名
     * @return 二维码图片文件路径
     */
    public static File generateQrcodeFilePath(String qrcodeFileName) {
        File qrcodeDir = new File(PathUtils.getInstance().getImagePath() + File.separator + "qrcode");
        if (!qrcodeDir.exists()) {
            qrcodeDir.mkdirs();
        }
        return new File(qrcodeDir.getPath(), qrcodeFileName);
    }

    /**
     * 生成头像图片文件父路径
     *
     * @return 头像图片文件父路径
     */
    public static File generateAvatarDir() {
        File avatarDir = new File(PathUtils.getInstance().getImagePath() + File.separator + "avatar");
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }
        return new File(avatarDir.getPath());
    }

    /**
     * 生成头像文件路径
     */
    public static File generateAvatarFilePath() {
        return new File(generateAvatarDir(), getPhotoFileName());
    }

    /**
     * 使用系统当前日期加以调整作为照片的名称
     *
     * @return 照片的文件名
     */
    private static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.CHINESE);
        return dateFormat.format(date) + ".jpg";
    }

    /***************************************************/

    /**
     * SDCard/Android/data/你的应用的包名/files/ 目录
     * http://blog.csdn.net/jaycee110905/article/details/21130557
     *
     * @param context Context
     */
    public File getExternalFilesDir(Context context) {
        return getExternalFilesDir(context, null);

    }

    /**
     * SDCard/Android/data/你的应用的包名/cache/ 目录
     * 会随着应用的卸载而被清空
     */
    public File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * 返回SDCard/Music或者SDCard/ALARMS等目录路径,如果type!=null,目录里面的文件不会随着应用的卸载而清空
     * http://my.oschina.net/handsomeban/blog/207025
     *
     * @param context Context
     * @param type    {@link android.os.Environment#DIRECTORY_MUSIC},
     *                {@link android.os.Environment#DIRECTORY_PODCASTS},
     *                {@link android.os.Environment#DIRECTORY_RINGTONES},
     *                {@link android.os.Environment#DIRECTORY_ALARMS},
     *                {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     *                {@link android.os.Environment#DIRECTORY_PICTURES}, or
     *                {@link android.os.Environment#DIRECTORY_MOVIES}.
     */
    public File getExternalFilesDir(Context context, String type) {
        return context.getExternalFilesDir(type);
    }

}