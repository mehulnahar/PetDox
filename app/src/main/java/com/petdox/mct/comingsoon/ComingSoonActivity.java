package com.petdox.mct.comingsoon;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.petdox.mct.base.BaseActivity;
import com.petdox.mct.databinding.ActivityComingsoonBinding;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class ComingSoonActivity extends BaseActivity {

    ActivityComingsoonBinding activityComingsoonBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityComingsoonBinding = ActivityComingsoonBinding.inflate(getLayoutInflater());
        setContentView(activityComingsoonBinding.getRoot());

        activityComingsoonBinding.cancel.setOnClickListener(v -> {
            finish();
            slideLeftToRight();
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        slideLeftToRight();
    }
}
