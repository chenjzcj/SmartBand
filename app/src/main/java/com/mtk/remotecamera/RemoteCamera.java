/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mtk.remotecamera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mtk.MyApp;
import com.mtk.data.Util;
import com.mtk.service.MainService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 远程相机页面(手表点击拍照,手机将会打开拍照预览并可以实现远程控制拍照)
 */
public class RemoteCamera extends Activity {
    private final String TAG = "RemoteCamera";
    private Preview mPreview;
    private Camera mCamera;
    private int numberOfCameras;
    static int ratation = 0;

    // private int cameraCurrentlyLocked;
    private boolean isNotifiedToCloseByBTDialer = false;
    private final MainService service = MainService.getInstance();
    // The first rear facing camera
    // private int defaultCameraId;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 123) {
                int orientation = msg.arg1;
                if (orientation > 45 && orientation < 135) {
                    // SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    ratation = 270;
                } else if (orientation > 135 && orientation < 225) {
                    // SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    ratation = 180;

                } else if (orientation > 225 && orientation < 315) {
                    // SCREEN_ORIENTATION_LANDSCAPE
                    ratation = 90;

                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                    // SCREEN_ORIENTATION_PORTRAIT
                    ratation = 0;
                }
            }
        }
    };
    private final SubSensorListener mSubSensorListener = new SubSensorListener(handler);

    class SubSensorListener implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        private final Handler handler;

        public SubSensorListener(Handler handler) {
            this.handler = handler;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y value
            if (magnitude * 4 >= Z * Z) {
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }

            if (handler != null) {
                handler.obtainMessage(123, orientation, 0).sendToTarget();
            }
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (RemoteCameraService.BT_REMOTECAMERA_EXIT_ACTION.equals(action)) {
                isNotifiedToCloseByBTDialer = true;
                RemoteCameraService.isIntheProgressOfExit = true;
                finish();
            } else if (RemoteCameraService.BT_REMOTECAMERA_CAPTURE_ACTION.equals(action)) {
                if (mPreview != null) {
                    mPreview.takePicture(ratation);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // regist the broadcast Receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(RemoteCameraService.BT_REMOTECAMERA_EXIT_ACTION);
        filter.addAction(RemoteCameraService.BT_REMOTECAMERA_CAPTURE_ACTION);
        this.registerReceiver(mReceiver, filter);

        SensorManager sm = (SensorManager) MyApp.getInstance().getApplicationContext()
                .getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(mSubSensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI, handler);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new Preview(this);
        setContentView(mPreview);

        // Find the total number of cameras available
        numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                // defaultCameraId = i;
            }
        }
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        try {
            mCamera = Camera.open();
            if (mCamera == null) {
                Log.i(TAG, "Can't open the camera");
                isNotifiedToCloseByBTDialer = false;
                finish();
            }
        } catch (Exception e) {
            Log.i(TAG, "onResume and isNotifiedToCloseByBTDialer = true");
            isNotifiedToCloseByBTDialer = false;
            finish();
        }
        // cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
        String cmdOfList = String.valueOf(RemoteCameraService.Commands.POSITION_OF_START_ACTIVITY)
                + RemoteCameraService.Commands.NUM_OF_START_ACTIFITY_ARGS;
        service.sendCAPCResult(cmdOfList);

        RemoteCameraService.isLaunched = true;
        RemoteCameraService.isIntheProgressOfExit = false;
    }

    @Override
    public void finish() {
        Log.i(TAG, "finish");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        SensorManager sm = (SensorManager) MyApp.getInstance().getApplicationContext()
                .getSystemService(Context.SENSOR_SERVICE);
        sm.unregisterListener(mSubSensorListener);
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
        Log.i(TAG, "onPause");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: isNotifiedToCloseByBTDialer is:" + isNotifiedToCloseByBTDialer);

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        this.unregisterReceiver(mReceiver);
        if (isNotifiedToCloseByBTDialer) {
            isNotifiedToCloseByBTDialer = false;
        } else {
            String cmdOfList = String.valueOf(RemoteCameraService.Commands.POSITION_OF_EXIT_ACTIVITY)
                    + RemoteCameraService.Commands.NUM_OF_EXIT_ACTIFITY_ARGS;
            service.sendCAPCResult(cmdOfList);
        }
        RemoteCameraService.isLaunched = false;
        RemoteCameraService.isIntheProgressOfExit = false;

    }

}

// ----------------------------------------------------------------------

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the Camera to the surface. We need
 * to center the SurfaceView because not all devices have cameras that support preview sizes at the same aspect ratio as the
 * device's display.
 */
class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "REMOTECAMERAService";

    private final SurfaceView mSurfaceView;
    private final SurfaceHolder mHolder;
    private Size mPreviewSize;
    private Size mPictureSize;
    private List<Size> mSupportedPreviewSizes;
    private List<Size> mSupportedPictureSizes;
    private Camera mCamera;
    byte[] mPreviewJpegData;
    byte[] mCaptureJpegData;
    private int ratation;
    private final Context mContext;
    private long mCurrentTime;
    Activity mAcitivity;

    private final TakePictureCallback mTakePictureCallback = new TakePictureCallback();
    private final AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();

    Preview(Context context) {
        super(context);
        mContext = context;
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);
        mCurrentTime = 0;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        // mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;

        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();

            requestLayout();
        }
    }

    public void takePicture(int rote) {
        if (mCamera != null) {
            ratation = rote;
            // mCamera.enableShutterSound(true);
            mCamera.autoFocus(mAutoFocusCallback);

        }
    }

    private final class AutoFocusCallback implements Camera.AutoFocusCallback {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.i(TAG, "onAutoFocus Callback");
            camera.cancelAutoFocus();
            camera.takePicture(null, null, mTakePictureCallback);
        }
    }

    private final class TakePictureCallback implements PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Log.i(TAG, "onPictureTaken");
            String state = Environment.getExternalStorageState();
            if ((!(Environment.MEDIA_MOUNTED.equals(state)))
                    || (Util.getAvailableStore(Environment.getExternalStorageDirectory().getPath()) < 2000)) {

                sendCaptureFail();
            } else {
                sendCaptureData(data);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                if (ratation == 0 || ratation == 180) {
                    matrix.postRotate(ratation + 90);
                }
                if (ratation == 90 || ratation == 270) {
                    matrix.postRotate(ratation - 90);
                }
                File pictureFile = getOutputMediaFile(1);
                if (pictureFile == null) {
                    Log.d(TAG, "Error creating media file, check storage permissions: ");
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                            + pictureFile.getAbsolutePath())));
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                // MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "IMG_" + curTime.substring(0,
                // 8)
                // + curTime.substring(9).replace(":", "_") + curTime.substring(10, 16) + ".jpg",
                // "Image of RemoteCapture");

                /*
                 * //create parameters for Intent with filename
                 * 
                 * File sdCard = Environment.getExternalStorageDirectory(); File directory = new File
                 * (sdCard.getAbsolutePath() + "/RemoteCaptures"); directory.mkdirs();
                 * 
                 * String filename = String.format("%d.jpg", System.currentTimeMillis()); File file = new File(directory,
                 * filename);
                 * 
                 * //File file = new File(Environment.getExternalStorageState(), System.currentTimeMillis() + ".jpg"); try {
                 * FileOutputStream fout = new FileOutputStream(file); BufferedOutputStream bos = new
                 * BufferedOutputStream(fout); bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                 * 
                 * //sendNotification(Bitmap.createScaledBitmap(bitmap, 40, 40, false)); bos.flush(); bos.close();
                 * MediaStore.Images.Media.insertImage(mContext.getContentResolver(), file.getAbsolutePath(), filename,);
                 * //imageUri is the current activity attribute, define and save it for later usage (also in
                 * onSaveInstanceState)
                 * 
                 * 
                 * 
                 * 
                 * } catch (FileNotFoundException e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
                 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
                 */
            }

            Log.i("Remote Capture", "Capture success");
            camera.setDisplayOrientation(0);
            camera.startPreview();
        }

        private void sendCaptureFail() {
            MainService service = MainService.getInstance();
            String cmdOfList = String.valueOf(-RemoteCameraService.Commands.POSITION_OF_START_ACTIVITY)
                    + RemoteCameraService.Commands.NUM_OF_START_ACTIFITY_ARGS;
            service.sendCAPCResult(cmdOfList);
        }

        private Bitmap sendCaptureData(byte[] data) {
            MainService service = MainService.getInstance();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            double scaleRation;
            int thumbnailWidth = bitmap.getWidth();
            int thumbnailHeight = bitmap.getHeight();

            if (thumbnailWidth > thumbnailHeight) {
                scaleRation = thumbnailWidth / 170;
            } else {
                scaleRation = thumbnailHeight / 170;
            }

            Matrix matrix = new Matrix();
            if (ratation == 0 || ratation == 180) {
                matrix.postRotate(ratation + 90);
            }
            if (ratation == 90 || ratation == 270) {
                matrix.postRotate(ratation - 90);
            }

            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (thumbnailWidth / scaleRation),
                    (int) (thumbnailHeight / scaleRation), false);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) (thumbnailWidth / scaleRation),
                    (int) (thumbnailHeight / scaleRation), matrix, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);

            mCaptureJpegData = baos.toByteArray();
            String cmdOfList = String.valueOf(RemoteCameraService.Commands.POSITION_OF_CAP)
                    + RemoteCameraService.Commands.NUM_OF_CAP_ACTIFITY_ARGS + String.valueOf(mCaptureJpegData.length) + " ";
            service.sendCAPCResult(cmdOfList);
            service.sendCAPCData(mCaptureJpegData);
            return bitmap;
        }
    }

    /*
     * private void sendNotification(Bitmap captureImage) { String appName = "RemoteCapture"; String title = "Photo"; String
     * content = "Content: Captured Photo"; String tickerText = "[Captured Photo]"; Bitmap sendIcon = captureImage;
     * 
     * // Log.i(LOG_TAG, "createNotificationBody(), content=" + content); int timestamp =
     * Util.getUtcTime(System.currentTimeMillis());
     * 
     * // body NotificationMessageBody body = new NotificationMessageBody(); body.setSender(appName); body.setAppID("5");
     * body.setTitle(title); body.setContent(content); body.setTickerText(tickerText); body.setTimestamp(timestamp);
     * body.setIcon(sendIcon);
     * 
     * // header MessageHeader header = new MessageHeader(); header.setCategory(MessageObj.CATEGORY_NOTI);
     * header.setSubType(MessageObj.SUBTYPE_NOTI); header.setMsgId(Util.genMessageId());
     * header.setAction(MessageObj.ACTION_ADD);
     * 
     * MessageObj notifiMessage = new MessageObj(); notifiMessage.setDataHeader(header); notifiMessage.setDataBody(body);
     * 
     * MainService.getInstance().sendNotiMessage(notifiMessage); }
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
        if (mSupportedPictureSizes != null) {
            mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        Log.i(TAG, "surfaceCreated");
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;
        int length = sizes.size();
        // Try to find an size match aspect ratio and size
        for (int i = length - 1; i > 0; --i) {
            Size size = sizes.get(i);
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        if (mCamera == null) {
            return;
        }
        Log.i(TAG, "surfaceChanged");
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        requestLayout();

        mCamera.setParameters(parameters);

        mCamera.setPreviewCallback(new PreviewCallback() {
            public synchronized void onPreviewFrame(byte[] data, Camera camera) {

                if (RemoteCameraService.needPreview) {
                    if (mCurrentTime == 0) {
                        mCurrentTime = System.currentTimeMillis();
                    }
                    long deltaTime = System.currentTimeMillis() - mCurrentTime;
                    if (deltaTime > 333) {
                        Log.i("CameraPreview", "vedio data come ...");
                        mCurrentTime = System.currentTimeMillis();
                        Camera.Parameters parameters = camera.getParameters();
                        int imageFormat = parameters.getPreviewFormat();
                        int previewWidth = parameters.getPreviewSize().width;
                        int previewHight = parameters.getPreviewSize().height;
                        Rect rect = new Rect(0, 0, previewWidth, previewHight);
                        YuvImage yuvImg = new YuvImage(data, imageFormat, previewWidth, previewHight, null);
                        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
                        yuvImg.compressToJpeg(rect, 70, outputstream);

                        // int a = outputstream.toByteArray().length;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(outputstream.toByteArray(), 0, outputstream.size());

                        double scaleRation;
                        if (previewWidth > previewHight) {
                            scaleRation = previewWidth / 170;
                        } else {
                            scaleRation = previewHight / 170;
                        }

                        Matrix matrix = new Matrix();
                        ratation = RemoteCamera.ratation;
                        if (ratation == 0 || ratation == 180) {
                            matrix.postRotate(ratation + 90);
                        }
                        if (ratation == 90 || ratation == 270) {
                            matrix.postRotate(ratation - 90);
                        }

                        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (previewWidth / scaleRation),
                                (int) (previewHight / scaleRation), false);

                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

                        mPreviewJpegData = baos.toByteArray();

                        String cmdOfList = String.valueOf(RemoteCameraService.Commands.POSITION_OF_PREVIEW)
                                + RemoteCameraService.Commands.NUM_OF_CAP_ACTIFITY_ARGS
                                + String.valueOf(mPreviewJpegData.length) + " ";

                        MainService service = MainService.getInstance();
                        service.sendCAPCResult(cmdOfList);
                        service.sendCAPCData(mPreviewJpegData);
                        Log.i("CameraPreview", "vedio data has sent ...");

                    } else {
                        Log.i("CameraPreview", "vedio data did not need to send ...");
                    }
                }

            }
        });

        mCamera.startPreview();

    }

    /**
     * 为保存图片或视频创建File
     */
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Notification");
        // 如果期望图片在应用程序卸载后还存在、且能被其它应用程序共享，
        // 则此保存位置最合适
        // 如果不存在的话，则创建存储目录
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // 创建媒体文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }
}
