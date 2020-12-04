package com.petdox.mct;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.petdox.mct.base.BaseActivity;
import com.petdox.mct.callback.CallbackImageClick;
import com.petdox.mct.camera.CameraActivity;
import com.petdox.mct.carousel.CarouselLayoutManager;
import com.petdox.mct.carousel.CarouselZoomPostLayoutListener;
import com.petdox.mct.carousel.CenterScrollListener;
import com.petdox.mct.carousel.DefaultChildSelectionListener;
import com.petdox.mct.comingsoon.ComingSoonActivity;
import com.petdox.mct.database.DatabaseManager;
import com.petdox.mct.databinding.ActivityMainBinding;
import com.petdox.mct.model.AlbumModel;
import com.petdox.mct.model.CarouselModel;
import com.petdox.mct.preview.DisplayImagesActivity;
import com.petdox.mct.repo.AlbumRepo;
import com.petdox.mct.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
public class MainActivity extends BaseActivity implements CallbackImageClick {

    private static final int PERMISSION_CODE = 1002;
    ActivityMainBinding activityMainBinding;
    List<CarouselModel> carouselModels = new ArrayList<>();
    MainSelectedCategoryAdapter selectedCategoryAdapter = null;
    ArrayList<String> imagesList = new ArrayList<>();
    private AlbumRepo albumRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        init();
        retrieveData();

        activityMainBinding.spinner.setItems("All", "Cat", "Dog", "Elephant", "Fish", "Frog", "Monkey", "Gorilla", "Cow", "Zebra", "other/unknown");
        activityMainBinding.spinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> showToast("In Development"));

        activityMainBinding.addNew.setOnClickListener(v -> {
            if (PermissionUtils.isStorageGranted(MainActivity.this) && PermissionUtils.isCameraGranted(MainActivity.this)) {
                openActivity(MainActivity.this, CameraActivity.class, false, false);
                slideRightToLeft();
            } else {
                PermissionUtils.checkBothPermission(MainActivity.this, PERMISSION_CODE);
            }
        });

        activityMainBinding.findPet.setOnClickListener(v -> {
            openActivity(MainActivity.this, ComingSoonActivity.class, false, false);
            slideRightToLeft();
        });

        activityMainBinding.mostRecentFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("In Development");
            }
        });

        activityMainBinding.nextReminderFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("In Development");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0) {

                if (PermissionUtils.isStorageGranted(MainActivity.this) && PermissionUtils.isCameraGranted(MainActivity.this)) {
                    openActivity(MainActivity.this, CameraActivity.class, false, false);
                    slideRightToLeft();
                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showDialogOK(getResources().getString(R.string.some_req_permissions));
                    } else {
                        explain(getResources().getString(R.string.open_settings));
                    }
                }
            }
        }
        if (requestCode != PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showDialogOK(String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.ok),
                (dialog1, id) -> {
                    dialog1.cancel();
                    PermissionUtils.checkBothPermission(MainActivity.this, PERMISSION_CODE);
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.cancel),
                (dialog12, id) -> {
                    dialog12.cancel();
                    PermissionUtils.checkBothPermission(MainActivity.this, PERMISSION_CODE);
                });

        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private void explain(String msg) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.yes),
                (dialog1, id) -> {

                    dialog1.cancel();

                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(uri);
                    startActivity(intent);
                    finish();
                });
        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private void init() {
        DatabaseManager.init(MainActivity.this);
        albumRepo = new AlbumRepo();
    }

    private void retrieveData() {

        showLoading("");

        if (carouselModels != null && carouselModels.size() > 0) {
            carouselModels.clear();
        }

        new Handler().postDelayed(() -> {
            List<AlbumModel> items = (List<AlbumModel>) albumRepo.findAll();

            if (items != null && items.size() > 0) {

                for (int i = 0; i < items.size(); i++) {
                    CarouselModel carouselModel = new CarouselModel();
                    carouselModel.setDate(items.get(i).getDate());
                    carouselModel.setReminderTime(items.get(i).getReminderTime());
                    carouselModel.setReminderActual(items.get(i).getReminderActual());
                    carouselModel.setReminderConverted(items.get(i).getReminderConverted());
                    carouselModel.setNote(items.get(i).getNote());
                    List<String> images = new ArrayList<>();
                    List<String> mainCategories = new ArrayList<>();
                    List<String> subCategories = new ArrayList<>();

                    StringTokenizer imageST = new StringTokenizer(items.get(i).getImages(), "#");
                    while (imageST.hasMoreTokens()) {
                        images.add(imageST.nextToken());
                    }
                    carouselModel.setImages(images);

                    StringTokenizer mainCategoriesST = new StringTokenizer(items.get(i).getMainCategories(), "#");
                    while (mainCategoriesST.hasMoreTokens()) {
                        mainCategories.add(mainCategoriesST.nextToken());
                    }
                    carouselModel.setMainCategories(mainCategories);

                    StringTokenizer subCategoriesST = new StringTokenizer(items.get(i).getSubCategories(), "#");
                    while (subCategoriesST.hasMoreTokens()) {
                        subCategories.add(subCategoriesST.nextToken());
                    }
                    carouselModel.setSubCategories(subCategories);
                    carouselModels.add(carouselModel);
                }

                if (carouselModels != null && carouselModels.size() > 0) {
                    updateData();
                    activityMainBinding.allContent.setVisibility(View.VISIBLE);
                    activityMainBinding.noRecordAdded.setVisibility(View.GONE);
                } else {
                    activityMainBinding.allContent.setVisibility(View.GONE);
                    activityMainBinding.noRecordAdded.setVisibility(View.VISIBLE);
                }
            } else {
                activityMainBinding.allContent.setVisibility(View.GONE);
                activityMainBinding.noRecordAdded.setVisibility(View.VISIBLE);
            }

            hideLoading();

        }, 1000);

    }

    private void updateData() {

        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());

        activityMainBinding.carouselList.setLayoutManager(layoutManager);
        activityMainBinding.carouselList.setHasFixedSize(true);
        CarouselAdapter carouselAdapter = new CarouselAdapter(this);
        carouselAdapter.setCallbackImageClick(this);
        activityMainBinding.carouselList.setAdapter(carouselAdapter);
        activityMainBinding.carouselList.addOnScrollListener(new CenterScrollListener());
        carouselAdapter.updateList(carouselModels);
        DefaultChildSelectionListener.initCenterItemListener((recyclerView, carouselLayoutManager, v) -> {
            final int position = recyclerView.getChildLayoutPosition(v);
            final String msg = String.format(Locale.ENGLISH, "Item %1$d was clicked", position);
            // showToast(msg);
            showToast("In Development");
        }, activityMainBinding.carouselList, layoutManager);
        layoutManager.addOnItemSelectionListener(this::detailsDataView);
    }

    private void detailsDataView(int position) {

        if (carouselModels.get(position).getReminderConverted() != null && !carouselModels.get(position).getReminderConverted().equalsIgnoreCase("")) {
            activityMainBinding.addReminderText.setText(carouselModels.get(position).getReminderConverted());
        } else {
            activityMainBinding.addReminderText.setText("No Reminder");
        }

        activityMainBinding.photoDate.setText(carouselModels.get(position).getDate());
        activityMainBinding.descriptionText.setText(carouselModels.get(position).getNote());
        if (selectedCategoryAdapter == null) {
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
            selectedCategoryAdapter = new MainSelectedCategoryAdapter(MainActivity.this);
            activityMainBinding.selectedCategoryList.setLayoutManager(linearLayoutManager1);
            activityMainBinding.selectedCategoryList.setAdapter(selectedCategoryAdapter);
        }

        selectedCategoryAdapter.addCategory(carouselModels.get(position).getSubCategories());

        if (carouselModels != null && carouselModels.size() > 0) {
            if (imagesList != null && imagesList.size() > 0) {
                imagesList.clear();
            }

            assert imagesList != null;
            imagesList.addAll(carouselModels.get(position).getImages());
        }

    }

    @Override
    public void singleClick() {
        Intent intent = new Intent(this, DisplayImagesActivity.class);
        intent.putExtra("IMAGE_LIST", imagesList);
        startActivity(intent);
        slideRightToLeft();
    }

    @Override
    public void doubleClick() {
        showToast("onItemDoubleClicked");
    }
}
