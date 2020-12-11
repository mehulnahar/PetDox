package com.petdox.mct.splash;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.petdox.mct.MainActivity;
import com.petdox.mct.base.BaseActivity;
import com.petdox.mct.databinding.ActivitySplashBinding;

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
public class SplashActivity extends BaseActivity {

    ActivitySplashBinding activitySplashBinding;
    private Handler mSplashHandler;
    private Runnable mSplashRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        activitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(activitySplashBinding.getRoot());

        initSplash();
        int mSplashTime = 3000;
        mSplashHandler.postDelayed(mSplashRunnable, mSplashTime);
    }

    private void initSplash() {
        try {

            mSplashHandler = new Handler();
            mSplashRunnable = () -> {
                try {
                    openActivity(this, MainActivity.class, false, true);
                    slideBottomToTop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mSplashRunnable != null && mSplashHandler != null)
            mSplashHandler.removeCallbacks(mSplashRunnable);
        finish();
        slideBottomToTop();
    }
}
