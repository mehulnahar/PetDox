package com.petdox.mct.preview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.renderscript.RenderScript;

import com.google.android.cameraview.CameraView;
import com.petdox.mct.BuildConfig;
import com.petdox.mct.R;
import com.petdox.mct.base.BaseActivity;
import com.petdox.mct.databinding.ActivityCameraBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.github.silvaren.easyrs.tools.Nv21Image;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class AddPictureActivity extends BaseActivity {

    ActivityCameraBinding activityCameraBinding;
    double height = 0.0;
    double width = 0.0;
    private RenderScript renderScript;
    private boolean frameIsProcessing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }


        activityCameraBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(activityCameraBinding.getRoot());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = width * 1.33;
        int value = (int) Math.round(height);

        activityCameraBinding.cameraView.start();
        try {
            renderScript = RenderScript.create(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
        setupCameraCallbacks();
        activityCameraBinding.switchBtn.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                activityCameraBinding.cameraView.setFlash(CameraView.FLASH_ON);
            } else {
                activityCameraBinding.cameraView.setFlash(CameraView.FLASH_OFF);
            }
        });

        activityCameraBinding.clickPhoto.setOnClickListener(v -> activityCameraBinding.cameraView.takePicture());
        activityCameraBinding.cancel.setOnClickListener(v -> {
            finish();
            slideLeftToRight();
        });

        activityCameraBinding.hiddenView.postDelayed(() -> {

            activityCameraBinding.frame.getLayoutParams().height = value;
            activityCameraBinding.cameraView.getLayoutParams().height = value;
            activityCameraBinding.frame.requestLayout();
            activityCameraBinding.cameraView.requestLayout();
        }, 200);
    }

    @Override
    protected void onPause() {
        activityCameraBinding.cameraView.stop();
        super.onPause();
    }

    private void setupCameraCallbacks() {
        activityCameraBinding.cameraView.setOnPictureTakenListener(this::startSavingPhoto);
        activityCameraBinding.cameraView.setOnFocusLockedListener(() -> {

        });
        activityCameraBinding.cameraView.setOnTurnCameraFailListener(e -> showToast("Switch Camera Failed. Does you device has a front camera?"));
        activityCameraBinding.cameraView.setOnCameraErrorListener(e -> showToast(e.getMessage()));
        activityCameraBinding.cameraView.setOnFrameListener((data, width, height, rotationDegrees) -> {
            if (frameIsProcessing) return;
            frameIsProcessing = true;
            Observable.fromCallable(() -> Nv21Image.nv21ToBitmap(renderScript, data, width, height)).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Bitmap frameBitmap) {

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            frameIsProcessing = false;
                        }
                    });
        });
    }

    private String bitmapToFile(Bitmap bitmap) {
        //create a file to write bitmap data
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = getString(R.string.app_name) + sdf.format(currentTime) + ".jpg";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), fileName);
        try {
            file.createNewFile();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return "";
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, bos);
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return "";
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    @SuppressLint("CheckResult")
    private void startSavingPhoto(final Bitmap bitmap, final int rotationDegrees) {

        Observable.fromCallable(() -> {
            Matrix matrix = new Matrix();
            matrix.postRotate(-rotationDegrees);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }).map(this::bitmapToFile).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(filePath -> {

                    if (filePath.isEmpty()) {
                        showToast("Save image file failed :(");
                    } else {
                        notifyGallery(filePath);
                    }
                });
    }

    private void notifyGallery(String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("IMAGE_PATH", filePath);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        slideLeftToRight();
    }

    @Override
    public void onBackPressed() {
        finish();
        slideLeftToRight();
    }
}
