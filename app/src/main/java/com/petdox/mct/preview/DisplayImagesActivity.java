package com.petdox.mct.preview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.petdox.mct.BuildConfig;
import com.petdox.mct.base.BaseActivity;
import com.petdox.mct.databinding.ActivityImagesPreviewBinding;
import com.petdox.mct.preview.adapter.DisplayImagesAdapter;
import com.takusemba.multisnaprecyclerview.MultiSnapHelper;
import com.takusemba.multisnaprecyclerview.SnapGravity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class DisplayImagesActivity extends BaseActivity {

    ActivityImagesPreviewBinding activityImagesPreviewBinding;
    ArrayList<String> imagesList = new ArrayList<>();
    DisplayImagesAdapter displayImagesAdapter = null;
    int currentImagePos = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityImagesPreviewBinding = ActivityImagesPreviewBinding.inflate(getLayoutInflater());
        setContentView(activityImagesPreviewBinding.getRoot());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("IMAGE_LIST")) {
            imagesList = (ArrayList<String>) intent.getSerializableExtra("IMAGE_LIST");
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        if (displayImagesAdapter == null) {
            displayImagesAdapter = new DisplayImagesAdapter(this);
        }
        activityImagesPreviewBinding.imagesRecyclerview.setLayoutManager(linearLayoutManager);
        activityImagesPreviewBinding.imagesRecyclerview.setAdapter(displayImagesAdapter);

        for (int i = 0; i < imagesList.size(); i++) {
            displayImagesAdapter.addImage(imagesList.get(i));
        }


        MultiSnapHelper multiSnapHelper = new MultiSnapHelper(SnapGravity.START, 1, 100);
        multiSnapHelper.attachToRecyclerView(activityImagesPreviewBinding.imagesRecyclerview);

        activityImagesPreviewBinding.imagesRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    try {
                        View centerView = multiSnapHelper.findSnapView(linearLayoutManager);
                        currentImagePos = linearLayoutManager.getPosition(Objects.requireNonNull(centerView));
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) e.printStackTrace();
                    }

                }
            }
        });

        activityImagesPreviewBinding.back.setOnClickListener(v -> {
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
