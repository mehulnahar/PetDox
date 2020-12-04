package com.petdox.mct.preview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.petdox.mct.BuildConfig;
import com.petdox.mct.MainActivity;
import com.petdox.mct.R;
import com.petdox.mct.base.BaseActivity;
import com.petdox.mct.callback.CallbackImagePreview;
import com.petdox.mct.callback.CallbackMainCategory;
import com.petdox.mct.callback.CallbackSubCategory;
import com.petdox.mct.camera.CameraActivity;
import com.petdox.mct.database.DatabaseManager;
import com.petdox.mct.databinding.ActivityPreviewBinding;
import com.petdox.mct.model.AlbumModel;
import com.petdox.mct.preview.adapter.MainCategoryAdapter;
import com.petdox.mct.preview.adapter.PreviewImagesAdapter;
import com.petdox.mct.preview.adapter.ReminderListAdapter;
import com.petdox.mct.preview.adapter.SelectedCategoryAdapter;
import com.petdox.mct.preview.adapter.SubCategoryAdapter;
import com.petdox.mct.preview.model.MainCategoryModel;
import com.petdox.mct.preview.model.ReminderModel;
import com.petdox.mct.preview.model.SubCategoryModel;
import com.petdox.mct.repo.AlbumRepo;
import com.petdox.mct.utils.PermissionUtils;
import com.takusemba.multisnaprecyclerview.MultiSnapHelper;
import com.takusemba.multisnaprecyclerview.SnapGravity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class PreviewActivity extends BaseActivity implements CallbackMainCategory, CallbackSubCategory, DatePickerDialog.OnDateSetListener, CallbackImagePreview {

    private static final int PERMISSION_CODE = 1002;
    public static ArrayList<String> selectedCategoryModelList = new ArrayList<>();
    int LAUNCH_ADD_PICTURE_ACTIVITY = 1;
    int LAUNCH_REPLACE_PICTURE_ACTIVITY = 2;
    ActivityPreviewBinding activityPreviewBinding;
    String imagePath = "";
    PreviewImagesAdapter previewImagesAdapter = null;
    MainCategoryAdapter mainCategoryAdapter = null;
    SubCategoryAdapter subCategoryAdapter = null;
    SelectedCategoryAdapter selectedCategoryAdapter = null;
    ArrayList<String> imagesList = new ArrayList<>();
    List<MainCategoryModel> mainCategoryModelList = new ArrayList<>();
    String replaceImage = "";
    int currentImagePos = 0;
    String[] mCategoryArray;
    String[] catArray;
    String[] dogArray;
    String[] elephantArray;
    String[] fishArray;
    String[] frogArray;
    String[] monkeyArray;
    String[] gorillaArray;
    String[] cowArray;
    String[] zebraArray;
    boolean enableSaved = false;
    String reminderActual = "";
    String reminderConverted = "";
    String reminderTime = "";
    boolean isTimeAllow = false;
    private ArrayList<String> selectedMainCategoryModelList = new ArrayList<>();
    private AlbumRepo albumRepo;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityPreviewBinding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(activityPreviewBinding.getRoot());

        activityPreviewBinding.imagesRecyclerview.setNestedScrollingEnabled(false);
        activityPreviewBinding.mainCategoryList.setNestedScrollingEnabled(false);
        activityPreviewBinding.subCategoryList.setNestedScrollingEnabled(false);

        if (selectedCategoryModelList != null && selectedCategoryModelList.size() > 0) {
            selectedCategoryModelList.clear();
        }
        if (selectedMainCategoryModelList != null && selectedMainCategoryModelList.size() > 0) {
            selectedMainCategoryModelList.clear();
        }
        init();

        Intent intent = getIntent();
        imagePath = intent.getStringExtra("IMAGE_PATH");

        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String today = formatter.format(date);
        activityPreviewBinding.photoDate.setText(today);

        mCategoryArray = getResources().getStringArray(R.array.mainCategoryArray);
        catArray = getResources().getStringArray(R.array.catArray);
        dogArray = getResources().getStringArray(R.array.dogArray);
        elephantArray = getResources().getStringArray(R.array.elephantArray);
        fishArray = getResources().getStringArray(R.array.fishArray);
        frogArray = getResources().getStringArray(R.array.frogArray);
        monkeyArray = getResources().getStringArray(R.array.monkeyArray);
        gorillaArray = getResources().getStringArray(R.array.gorillaArray);
        cowArray = getResources().getStringArray(R.array.cowArray);
        zebraArray = getResources().getStringArray(R.array.zebraArray);

        for (String value : mCategoryArray) {
            List<SubCategoryModel> subCategoryModelList = new ArrayList<>();
            if (value.toLowerCase().contains("cat")) {
                for (String s : catArray) {
                    subCategoryModelList.add(new SubCategoryModel(s, false, false));
                }
                mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
            } else if (value.toLowerCase().contains("dog")) {
                for (String s : dogArray) {
                    subCategoryModelList.add(new SubCategoryModel(s, false, false));
                }
                mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
            } else if (value.toLowerCase().contains("elephant")) {
                for (String s : elephantArray) {
                    subCategoryModelList.add(new SubCategoryModel(s, false, false));
                }
                mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
            } else if (value.toLowerCase().contains("fish")) {
                for (String s : fishArray) {
                    subCategoryModelList.add(new SubCategoryModel(s, false, false));
                }
                mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
            } else if (value.toLowerCase().contains("frog")) {
                for (String s : frogArray) {
                    subCategoryModelList.add(new SubCategoryModel(s, false, false));
                }
                mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
            } else if (value.toLowerCase().contains("monkey")) {
                for (String s : monkeyArray) {
                    subCategoryModelList.add(new SubCategoryModel(s, false, false));
                }
                mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
            } else if (value.toLowerCase().contains("gorilla")) {
                for (String s : gorillaArray) {
                    subCategoryModelList.add(new SubCategoryModel(s, false, false));
                }
                mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
            } else if (value.toLowerCase().contains("cow")) {
                for (String s : cowArray) {
                    subCategoryModelList.add(new SubCategoryModel(s, false, false));
                }
                mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
            } else if (value.toLowerCase().contains("zebra")) {
                for (String s : zebraArray) {
                    subCategoryModelList.add(new SubCategoryModel(s, false, false));
                }
                mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
            } else {
                mainCategoryModelList.add(new MainCategoryModel(value, null, false, false));
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        if (previewImagesAdapter == null) {
            previewImagesAdapter = new PreviewImagesAdapter(this);
        }
        previewImagesAdapter.setCallbackImagePreview(this);
        activityPreviewBinding.imagesRecyclerview.setLayoutManager(linearLayoutManager);
        activityPreviewBinding.imagesRecyclerview.setAdapter(previewImagesAdapter);

        imagesList.add(imagePath);
        previewImagesAdapter.addImage(imagePath);

        MultiSnapHelper multiSnapHelper = new MultiSnapHelper(SnapGravity.START, 1, 100);
        multiSnapHelper.attachToRecyclerView(activityPreviewBinding.imagesRecyclerview);

        activityPreviewBinding.imagesRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {

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

        activityPreviewBinding.addCategory.setOnClickListener(v -> {
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(PreviewActivity.this, LinearLayoutManager.VERTICAL, false);
            mainCategoryAdapter = new MainCategoryAdapter(PreviewActivity.this);
            mainCategoryAdapter.setCallbackMainCategory(this);
            activityPreviewBinding.mainCategoryList.setLayoutManager(linearLayoutManager1);
            activityPreviewBinding.mainCategoryList.setAdapter(mainCategoryAdapter);
            mainCategoryAdapter.refreshList(mainCategoryModelList);

            activityPreviewBinding.categoryLayout.setVisibility(View.VISIBLE);
            activityPreviewBinding.captureView.setVisibility(View.VISIBLE);
        });

        activityPreviewBinding.cancel.setOnClickListener(v -> {
            if (activityPreviewBinding.categoryLayout.getVisibility() == View.VISIBLE) {
                activityPreviewBinding.categoryLayout.setVisibility(View.GONE);
                activityPreviewBinding.captureView.setVisibility(View.GONE);
            } else {
                cancelDialog();
            }
        });

        activityPreviewBinding.photoDate.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    PreviewActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getSupportFragmentManager(), "PhotoDate");

        });

        activityPreviewBinding.mainCategoryList.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of  child view
            activityPreviewBinding.categoryLayout.requestDisallowInterceptTouchEvent(true);
            return false;
        });

        activityPreviewBinding.subCategoryList.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of  child view
            activityPreviewBinding.categoryLayout.requestDisallowInterceptTouchEvent(true);
            return false;
        });

        activityPreviewBinding.addPicture.setOnClickListener(v -> addPictureDialog());

        activityPreviewBinding.replace.setOnClickListener(v -> replacePictureDialog());

        activityPreviewBinding.remove.setOnClickListener(v -> {
            if (imagesList != null && imagesList.size() == 1) {
                removePictureDialog();
            } else {
                removeSinglePictureDialog();
            }
        });

        activityPreviewBinding.addNoteView.setOnClickListener(v -> {
            String desText = getResources().getString(R.string.description_text);
            String actualDesText = activityPreviewBinding.descriptionText.getText().toString().trim();
            if (desText.equalsIgnoreCase(actualDesText)) {
                addNoteDialog("");
            } else {
                addNoteDialog(actualDesText);
            }
        });

        activityPreviewBinding.addReminder.setOnClickListener(v -> addReminderDialog());

        activityPreviewBinding.saveDetails.setOnClickListener(v -> {
            if (enableSaved) {
                saveData();
            } else {
                showToast("Please select any category/sub-category");
            }
        });

        activityPreviewBinding.selectedCategoryList.setOnClickListener(v -> {

        });

        activityPreviewBinding.mainScrollview.setOnClickListener(v -> {

        });

        activityPreviewBinding.captureView.setOnClickListener(v -> {

        });

        activityPreviewBinding.categoryLayout.setOnClickListener(v -> {

        });
    }

    @Override
    public void onBackPressed() {
        if (activityPreviewBinding.categoryLayout.getVisibility() == View.VISIBLE) {
            activityPreviewBinding.categoryLayout.setVisibility(View.GONE);
            activityPreviewBinding.captureView.setVisibility(View.GONE);
        } else {
            cancelDialog();
        }
    }

    @Override
    public void selectMainCategory(MainCategoryModel mainCategoryModel) {

        mainCategoryAdapter.notifyDataSetChanged();
        if (mainCategoryModel.getSubCategoryModelList() != null && mainCategoryModel.getSubCategoryModelList().size() > 0) {
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(PreviewActivity.this, LinearLayoutManager.VERTICAL, false);
            subCategoryAdapter = new SubCategoryAdapter(PreviewActivity.this);
            subCategoryAdapter.setCallbackSubCategory(this);
            activityPreviewBinding.subCategoryList.setLayoutManager(linearLayoutManager1);
            activityPreviewBinding.subCategoryList.setAdapter(subCategoryAdapter);
            subCategoryAdapter.refreshList(mainCategoryModel);
        } else {
            if (selectedCategoryModelList != null && selectedCategoryModelList.size() == 10) {
                showToast("You cannot add more than 10 sub-categories");
            } else {
                if (selectedCategoryAdapter == null) {
                    LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(PreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    selectedCategoryAdapter = new SelectedCategoryAdapter(PreviewActivity.this);
                    activityPreviewBinding.selectedCategoryList.setLayoutManager(linearLayoutManager1);
                    activityPreviewBinding.selectedCategoryList.setAdapter(selectedCategoryAdapter);
                }

                if (selectedCategoryModelList != null && selectedCategoryModelList.size() > 0) {
                    if (selectedCategoryModelList.contains(mainCategoryModel.getCategoryName())) {
                        return;
                    }
                }
                if (selectedCategoryModelList != null) {
                    selectedCategoryModelList.add(mainCategoryModel.getCategoryName());
                }
                selectedMainCategoryModelList.add(mainCategoryModel.getCategoryName());
                selectedCategoryAdapter.addCategory(mainCategoryModel.getCategoryName());

                activityPreviewBinding.selectedCategoryList.setVisibility(View.VISIBLE);
                activityPreviewBinding.selectedCategoryList.smoothScrollToPosition(Objects.requireNonNull(activityPreviewBinding.selectedCategoryList.getAdapter()).getItemCount() - 1);
                activityPreviewBinding.saveDetails.setBackgroundResource(R.drawable.rectangle_dark_green_border);
                enableSaved = true;
            }

        }
    }

    @Override
    public void selectSubCategory(SubCategoryModel subCategoryModel, MainCategoryModel mainCategoryModel, boolean anySaved) {

        mainCategoryAdapter.notifyDataSetChanged();
        if (selectedCategoryAdapter == null) {
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(PreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
            selectedCategoryAdapter = new SelectedCategoryAdapter(PreviewActivity.this);
            activityPreviewBinding.selectedCategoryList.setLayoutManager(linearLayoutManager1);
            activityPreviewBinding.selectedCategoryList.setAdapter(selectedCategoryAdapter);
        }

        subCategoryAdapter.notifyDataSetChanged();
        assert selectedCategoryModelList != null;
        if (subCategoryModel.isSaved()) {
            selectedCategoryModelList.add(subCategoryModel.getCategoryName());
            selectedCategoryAdapter.addCategory(subCategoryModel.getCategoryName());
            if (selectedMainCategoryModelList != null && selectedMainCategoryModelList.size() > 0) {
                if (selectedMainCategoryModelList.contains(mainCategoryModel.getCategoryName())) {
                    return;
                }
            }
            selectedMainCategoryModelList.add(mainCategoryModel.getCategoryName());
        } else {
            selectedCategoryModelList.remove(subCategoryModel.getCategoryName());
            selectedCategoryAdapter.removeCategory(subCategoryModel.getCategoryName());
            if (!anySaved) {
                selectedMainCategoryModelList.remove(mainCategoryModel.getCategoryName());
            }
        }
        if (selectedCategoryModelList != null && selectedCategoryModelList.size() > 0) {
            activityPreviewBinding.selectedCategoryList.setVisibility(View.VISIBLE);
            activityPreviewBinding.selectedCategoryList.smoothScrollToPosition(Objects.requireNonNull(activityPreviewBinding.selectedCategoryList.getAdapter()).getItemCount() - 1);
            activityPreviewBinding.addCategoryText.setText(getResources().getString(R.string.list_category));
            activityPreviewBinding.addCategory.setBackgroundResource(R.drawable.rectangle_dark_green_border);
            activityPreviewBinding.saveDetails.setBackgroundResource(R.drawable.rectangle_dark_green_border);
            enableSaved = true;
        } else {
            selectedCategoryModelList = new ArrayList<>();
            activityPreviewBinding.selectedCategoryList.setVisibility(View.GONE);
            activityPreviewBinding.addCategoryText.setText(getResources().getString(R.string.add_category));
            activityPreviewBinding.addCategory.setBackgroundResource(R.drawable.rectangle_white_black_border);
            activityPreviewBinding.saveDetails.setBackgroundResource(R.drawable.rectangle_grey);
            enableSaved = false;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = monthOfYear + 1;
        if (dayOfMonth < 10) {
            if (monthOfYear < 10) {
                activityPreviewBinding.photoDate.setText(String.format(Locale.ENGLISH, "0%d.0%d.%d", dayOfMonth, monthOfYear, year));
            } else {
                activityPreviewBinding.photoDate.setText(String.format(Locale.ENGLISH, "0%d.%d.%d", dayOfMonth, monthOfYear, year));
            }
        } else if (monthOfYear < 10) {
            activityPreviewBinding.photoDate.setText(String.format(Locale.ENGLISH, "%d.0%d.%d", dayOfMonth, monthOfYear, year));
        } else {
            activityPreviewBinding.photoDate.setText(String.format(Locale.ENGLISH, "%d.%d.%d", dayOfMonth, monthOfYear, year));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0) {

                if (PermissionUtils.isStorageGranted(PreviewActivity.this) && PermissionUtils.isCameraGranted(PreviewActivity.this)) {

                    Intent i = new Intent(PreviewActivity.this, AddPictureActivity.class);
                    if (replaceImage != null && !replaceImage.equalsIgnoreCase("")) {
                        startActivityForResult(i, LAUNCH_REPLACE_PICTURE_ACTIVITY);
                    } else {
                        startActivityForResult(i, LAUNCH_ADD_PICTURE_ACTIVITY);
                    }
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
                    PermissionUtils.checkBothPermission(PreviewActivity.this, PERMISSION_CODE);
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.cancel),
                (dialog12, id) -> {
                    dialog12.cancel();
                    PermissionUtils.checkBothPermission(PreviewActivity.this, PERMISSION_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_ADD_PICTURE_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String imagePathNew = data.getStringExtra("IMAGE_PATH");
                imagesList.add(imagePathNew);
                previewImagesAdapter.addImage(imagePathNew);
                activityPreviewBinding.imagesRecyclerview.smoothScrollToPosition(Objects.requireNonNull(activityPreviewBinding.imagesRecyclerview.getAdapter()).getItemCount() - 1);

            }
        } else if (requestCode == LAUNCH_REPLACE_PICTURE_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String imagePathNew = data.getStringExtra("IMAGE_PATH");
                imagesList.remove(replaceImage);
                previewImagesAdapter.removeImage(replaceImage);
                imagesList.add(imagePathNew);
                previewImagesAdapter.addImage(imagePathNew);
                replaceImage = "";
            }
        }
    }

    private void addPictureDialog() {

        replaceImage = "";

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Add Picture");
        dialog.setMessage("Do you want to add another picture ?");
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.Yes),
                (dialog1, id) -> {
                    dialog1.cancel();
                    if (imagesList != null && imagesList.size() == 10) {
                        showToast("You cannot add more than 10 pictures");
                    } else {

                        if (PermissionUtils.isStorageGranted(PreviewActivity.this) && PermissionUtils.isCameraGranted(PreviewActivity.this)) {
                            Intent i = new Intent(PreviewActivity.this, AddPictureActivity.class);
                            startActivityForResult(i, LAUNCH_ADD_PICTURE_ACTIVITY);
                            slideRightToLeft();
                        } else {
                            PermissionUtils.checkBothPermission(PreviewActivity.this, PERMISSION_CODE);
                        }

                    }
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> {
                    dialog12.cancel();

                });

        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private void replacePictureDialog() {

        replaceImage = "";

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Replace Picture");
        dialog.setMessage("Do you want to replace this picture with another picture ?");
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.Yes),
                (dialog1, id) -> {
                    dialog1.cancel();
                    try {
                        replaceImage = imagesList.get(currentImagePos);
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) e.printStackTrace();
                        replaceImage = imagesList.get(currentImagePos - 1);
                    }
                    if (PermissionUtils.isStorageGranted(PreviewActivity.this) && PermissionUtils.isCameraGranted(PreviewActivity.this)) {
                        Intent i = new Intent(PreviewActivity.this, AddPictureActivity.class);
                        startActivityForResult(i, LAUNCH_REPLACE_PICTURE_ACTIVITY);
                        slideRightToLeft();
                    } else {
                        PermissionUtils.checkBothPermission(PreviewActivity.this, PERMISSION_CODE);
                    }
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> {
                    dialog12.cancel();

                });

        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private void removePictureDialog() {

        replaceImage = "";

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Remove Details");
        dialog.setMessage("Do you want to remove this picture ? This will also discard your all details.");
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.Yes),
                (dialog1, id) -> {
                    dialog1.cancel();
                    openActivity(this, CameraActivity.class, true, false);
                    slideLeftToRight();
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> {
                    dialog12.cancel();

                });

        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private void cancelDialog() {

        replaceImage = "";

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Remove Details");
        dialog.setMessage("Do you want to discard your details ?");
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.Yes),
                (dialog1, id) -> {
                    dialog1.cancel();
                    openActivity(this, CameraActivity.class, true, false);
                    slideLeftToRight();
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> {
                    dialog12.cancel();

                });

        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private void removeSinglePictureDialog() {

        replaceImage = "";

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Remove Picture");
        dialog.setMessage("Do you want to remove this picture ?");
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.Yes),
                (dialog1, id) -> {
                    dialog1.cancel();
                    if (imagesList != null && imagesList.size() >= 0) {
                        previewImagesAdapter.removeImage(imagesList.get(currentImagePos));
                        imagesList.remove(currentImagePos);
                    }
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> {
                    dialog12.cancel();

                });

        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private void addNoteDialog(String text) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.dialog_note, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Add a note");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setCancelable(false);
        alertDialog.setView(view);
        final AppCompatEditText etComments = view.findViewById(R.id.etComments);
        etComments.setText(text);
        if (text != null && !text.equalsIgnoreCase("")) {
            etComments.setSelection(text.length());
        }
        showKeyboard(etComments);
        alertDialog.setPositiveButton(
                getResources().getString(R.string.submit),
                (dialog1, id) -> {
                    dialog1.cancel();
                    String textNew = etComments.getText().toString().trim();
                    if (textNew != null && !textNew.equalsIgnoreCase("")) {
                        activityPreviewBinding.descriptionText.setText(textNew);
                    }
                });

        alertDialog.setNegativeButton(
                getResources().getString(R.string.cancel),
                (dialog12, id) -> {
                    dialog12.cancel();
                });


        AlertDialog alert = alertDialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private void addReminderDialog() {

        List<ReminderModel> reminderList = new ArrayList<>();
        reminderList.add(new ReminderModel("no Reminder", false, false));
        reminderList.add(new ReminderModel("End of day (7pm)", false, false));
        reminderList.add(new ReminderModel("Tomorrow (9pm)", false, false));
        reminderList.add(new ReminderModel("End of week (Friday)", false, true));
        reminderList.add(new ReminderModel("End of month (27th)", false, true));
        reminderList.add(new ReminderModel("End of next month (27th)", false, true));
        reminderList.add(new ReminderModel("End of year ( Dec. 8th)", false, true));

        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.dialog_reminder, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setView(view);

        final LinearLayout cancel_reminder = view.findViewById(R.id.cancel_reminder);
        final LinearLayout ok_reminder = view.findViewById(R.id.ok_reminder);
        final LinearLayout date_click = view.findViewById(R.id.date_click);
        final LinearLayout time_click = view.findViewById(R.id.time_click);

        final AppCompatTextView reminder_date = view.findViewById(R.id.reminder_date);
        final AppCompatTextView reminder_time = view.findViewById(R.id.reminder_time);

        final RecyclerView reminders_list = view.findViewById(R.id.reminders_list);

        AlertDialog alert = alertDialog.create();
        reminders_list.setNestedScrollingEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ReminderListAdapter reminderListAdapter = new ReminderListAdapter(reminderList, this);
        reminderListAdapter.setCallbackClickReminder(reminderModel -> {

            isTimeAllow = reminderModel.isAllow();

            date_click.setBackgroundResource(R.drawable.rectangle_white_black_border);
            time_click.setBackgroundResource(R.drawable.rectangle_white_black_border);
            reminder_date.setText("dd.mm.yyyy");
            reminder_time.setText("hh:mm");

            reminderActual = reminderModel.getReminderName();

            Calendar calendar = Calendar.getInstance();
            LocalDate nowJoda = LocalDate.now();

            Date todayDate = calendar.getTime();

            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            @SuppressLint("SimpleDateFormat") DateFormat formatterMMYYYY = new SimpleDateFormat("MM.yyyy");
            @SuppressLint("SimpleDateFormat") DateFormat formatterYYYY = new SimpleDateFormat("yyyy");
            DateTimeFormatter formatterJoda = DateTimeFormat.forPattern("dd.MM.yyyy");

            if (reminderActual.equalsIgnoreCase("End of day (7pm)")) {
                reminderConverted = formatter.format(todayDate);
                reminderTime = "07:00 pm";
            } else if (reminderActual.equalsIgnoreCase("Tomorrow (9pm)")) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrowDate = calendar.getTime();
                reminderConverted = formatter.format(tomorrowDate);
                reminderTime = "09:00 pm";
            } else if (reminderActual.equalsIgnoreCase("End of week (Friday)")) {
                reminderConverted = calcNextFriday(nowJoda).toString(formatterJoda);
            } else if (reminderActual.equalsIgnoreCase("End of month (27th)")) {
                reminderConverted = "27." + formatterMMYYYY.format(todayDate);
            } else if (reminderActual.equalsIgnoreCase("End of next month (27th)")) {
                calendar.add(Calendar.MONTH, 1);
                Date nextMonth = calendar.getTime();
                reminderConverted = "27." + formatterMMYYYY.format(nextMonth);
            } else if (reminderActual.equalsIgnoreCase("End of year ( Dec. 8th)")) {
                reminderConverted = "08.12." + formatterYYYY.format(todayDate);
            } else {
                reminderConverted = "no Reminder";
            }

        });
        reminders_list.setLayoutManager(linearLayoutManager);
        reminders_list.setAdapter(reminderListAdapter);
        cancel_reminder.setOnClickListener(v -> alert.cancel());
        ok_reminder.setOnClickListener(v -> {

            if (reminderConverted != null && !reminderConverted.equalsIgnoreCase("")) {
                alert.cancel();
                activityPreviewBinding.addReminderText.setText(reminderConverted);
                activityPreviewBinding.addReminder.setBackgroundResource(R.drawable.rectangle_orange_black_border);
            } else {
                showToast("Please select any reminder");
            }
        });
        date_click.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    (view1, year, monthOfYear, dayOfMonth) -> {

                        isTimeAllow = true;

                        monthOfYear = monthOfYear + 1;
                        if (dayOfMonth < 10) {
                            if (monthOfYear < 10) {
                                reminder_date.setText(String.format(Locale.ENGLISH, "0%d.0%d.%d", dayOfMonth, monthOfYear, year));
                            } else {
                                reminder_date.setText(String.format(Locale.ENGLISH, "0%d.%d.%d", dayOfMonth, monthOfYear, year));
                            }
                        } else if (monthOfYear < 10) {
                            reminder_date.setText(String.format(Locale.ENGLISH, "%d.0%d.%d", dayOfMonth, monthOfYear, year));
                        } else {
                            reminder_date.setText(String.format(Locale.ENGLISH, "%d.%d.%d", dayOfMonth, monthOfYear, year));
                        }

                        date_click.setBackgroundResource(R.drawable.rectangle_dark_green_border);

                        reminderConverted = reminder_date.getText().toString().trim();
                        List<ReminderModel> reminderListNew = new ArrayList<>();

                        reminderListNew.add(new ReminderModel("no Reminder", false, false));
                        reminderListNew.add(new ReminderModel("End of day (7pm)", false, false));
                        reminderListNew.add(new ReminderModel("Tomorrow (9pm)", false, false));
                        reminderListNew.add(new ReminderModel("End of week (Friday)", false, true));
                        reminderListNew.add(new ReminderModel("End of month (27th)", false, true));
                        reminderListNew.add(new ReminderModel("End of next month (27th)", false, true));
                        reminderListNew.add(new ReminderModel("End of year ( Dec. 8th)", false, true));

                        ReminderListAdapter reminderListAdapterNew = new ReminderListAdapter(reminderListNew, this);
                        reminderListAdapterNew.setCallbackClickReminder(reminderModel -> {

                            isTimeAllow = reminderModel.isAllow();

                            date_click.setBackgroundResource(R.drawable.rectangle_white_black_border);
                            time_click.setBackgroundResource(R.drawable.rectangle_white_black_border);
                            reminder_date.setText("dd.mm.yyyy");
                            reminder_time.setText("hh:mm");

                            reminderActual = reminderModel.getReminderName();

                            Calendar calendar = Calendar.getInstance();
                            LocalDate nowJoda = LocalDate.now();

                            Date todayDate = calendar.getTime();

                            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                            @SuppressLint("SimpleDateFormat") DateFormat formatterMMYYYY = new SimpleDateFormat("MM.yyyy");
                            @SuppressLint("SimpleDateFormat") DateFormat formatterYYYY = new SimpleDateFormat("yyyy");
                            DateTimeFormatter formatterJoda = DateTimeFormat.forPattern("dd.MM.yyyy");

                            if (reminderActual.equalsIgnoreCase("End of day (7pm)")) {
                                reminderConverted = formatter.format(todayDate);
                                reminderTime = "07:00 pm";
                            } else if (reminderActual.equalsIgnoreCase("Tomorrow (9pm)")) {
                                calendar.add(Calendar.DAY_OF_YEAR, 1);
                                Date tomorrowDate = calendar.getTime();
                                reminderConverted = formatter.format(tomorrowDate);
                                reminderTime = "09:00 pm";
                            } else if (reminderActual.equalsIgnoreCase("End of week (Friday)")) {
                                reminderConverted = calcNextFriday(nowJoda).toString(formatterJoda);
                            } else if (reminderActual.equalsIgnoreCase("End of month (27th)")) {
                                reminderConverted = "27." + formatterMMYYYY.format(todayDate);
                            } else if (reminderActual.equalsIgnoreCase("End of next month (27th)")) {
                                calendar.add(Calendar.MONTH, 1);
                                Date nextMonth = calendar.getTime();
                                reminderConverted = "27." + formatterMMYYYY.format(nextMonth);
                            } else if (reminderActual.equalsIgnoreCase("End of year ( Dec. 8th)")) {
                                reminderConverted = "08.12." + formatterYYYY.format(todayDate);
                            } else {
                                reminderConverted = "no Reminder";
                            }

                        });
                        reminders_list.setLayoutManager(linearLayoutManager);
                        reminders_list.setAdapter(reminderListAdapterNew);
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getSupportFragmentManager(), "ReminderDate");
        });

        time_click.setOnClickListener((View.OnClickListener) v -> {

            if (isTimeAllow) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance((TimePickerDialog.OnTimeSetListener) (view12, hourOfDay, minute, second) -> {

                            boolean isPm = false;
                            if (hourOfDay > 12) {
                                isPm = true;
                                hourOfDay = hourOfDay - 12;
                            } else if (hourOfDay == 12) {
                                isPm = true;
                            }
                            String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                            String minuteString = minute < 10 ? "0" + minute : "" + minute;

                            if (isPm) {
                                reminder_time.setText(String.format("%s.%s pm", hourString, minuteString));
                            } else {
                                reminder_time.setText(String.format("%s.%s am", hourString, minuteString));
                            }

                            time_click.setBackgroundResource(R.drawable.rectangle_dark_green_border);

                            reminderTime = reminder_time.getText().toString().trim();

                            if (!isTimeAllow) {
                                List<ReminderModel> reminderListNew = new ArrayList<>();

                                reminderListNew.add(new ReminderModel("no Reminder", false, false));
                                reminderListNew.add(new ReminderModel("End of day (7pm)", false, false));
                                reminderListNew.add(new ReminderModel("Tomorrow (9pm)", false, false));
                                reminderListNew.add(new ReminderModel("End of week (Friday)", false, true));
                                reminderListNew.add(new ReminderModel("End of month (27th)", false, true));
                                reminderListNew.add(new ReminderModel("End of next month (27th)", false, true));
                                reminderListNew.add(new ReminderModel("End of year ( Dec. 8th)", false, true));

                                ReminderListAdapter reminderListAdapterNew = new ReminderListAdapter(reminderListNew, this);
                                reminderListAdapterNew.setCallbackClickReminder(reminderModel -> {

                                    isTimeAllow = reminderModel.isAllow();

                                    date_click.setBackgroundResource(R.drawable.rectangle_white_black_border);
                                    time_click.setBackgroundResource(R.drawable.rectangle_white_black_border);
                                    reminder_date.setText("dd.mm.yyyy");
                                    reminder_time.setText("hh:mm");

                                    reminderActual = reminderModel.getReminderName();

                                    Calendar calendar = Calendar.getInstance();
                                    LocalDate nowJoda = LocalDate.now();

                                    Date todayDate = calendar.getTime();

                                    @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                                    @SuppressLint("SimpleDateFormat") DateFormat formatterMMYYYY = new SimpleDateFormat("MM.yyyy");
                                    @SuppressLint("SimpleDateFormat") DateFormat formatterYYYY = new SimpleDateFormat("yyyy");
                                    DateTimeFormatter formatterJoda = DateTimeFormat.forPattern("dd.MM.yyyy");

                                    if (reminderActual.equalsIgnoreCase("End of day (7pm)")) {
                                        reminderConverted = formatter.format(todayDate);
                                        reminderTime = "07:00 pm";
                                    } else if (reminderActual.equalsIgnoreCase("Tomorrow (9pm)")) {
                                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                                        Date tomorrowDate = calendar.getTime();
                                        reminderConverted = formatter.format(tomorrowDate);
                                        reminderTime = "09:00 pm";
                                    } else if (reminderActual.equalsIgnoreCase("End of week (Friday)")) {
                                        reminderConverted = calcNextFriday(nowJoda).toString(formatterJoda);
                                    } else if (reminderActual.equalsIgnoreCase("End of month (27th)")) {
                                        reminderConverted = "27." + formatterMMYYYY.format(todayDate);
                                    } else if (reminderActual.equalsIgnoreCase("End of next month (27th)")) {
                                        calendar.add(Calendar.MONTH, 1);
                                        Date nextMonth = calendar.getTime();
                                        reminderConverted = "27." + formatterMMYYYY.format(nextMonth);
                                    } else if (reminderActual.equalsIgnoreCase("End of year ( Dec. 8th)")) {
                                        reminderConverted = "08.12." + formatterYYYY.format(todayDate);
                                    } else {
                                        reminderConverted = "no Reminder";
                                    }

                                });
                                reminders_list.setLayoutManager(linearLayoutManager);
                                reminders_list.setAdapter(reminderListAdapterNew);
                            }

                        }, now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE), false);
                tpd.show(getSupportFragmentManager(), "ReminderTime");
            }
        });

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private LocalDate calcNextFriday(LocalDate d) {
        return d.plusWeeks(d.getDayOfWeek() < DateTimeConstants.FRIDAY ? 0 : 1).withDayOfWeek(DateTimeConstants.FRIDAY);
    }


    @Override
    public void openImagePreview() {
        Intent intent = new Intent(this, DisplayImagesActivity.class);
        intent.putExtra("IMAGE_LIST", imagesList);
        startActivity(intent);
        slideRightToLeft();
    }

    private void init() {
        DatabaseManager.init(PreviewActivity.this);
        albumRepo = new AlbumRepo();
    }

    private void saveData() {

        showLoading("");

        new Handler().postDelayed(() -> {

            StringBuilder image = new StringBuilder();
            StringBuilder mainCategory = new StringBuilder();
            StringBuilder subCategory = new StringBuilder();
            AlbumModel albumModel = new AlbumModel();
            albumModel.setDate(activityPreviewBinding.photoDate.getText().toString().trim());
            if (imagesList != null && imagesList.size() > 0) {
                for (int i = 0; i < imagesList.size(); i++) {
                    if (i == 0) {
                        image = new StringBuilder(imagesList.get(i));
                    } else {
                        image.append("#").append(imagesList.get(i));
                    }
                }
            }
            if (selectedMainCategoryModelList != null && selectedMainCategoryModelList.size() > 0) {
                for (int i = 0; i < selectedMainCategoryModelList.size(); i++) {
                    if (i == 0) {
                        mainCategory = new StringBuilder(selectedMainCategoryModelList.get(i));
                    } else {
                        mainCategory.append("#").append(selectedMainCategoryModelList.get(i));
                    }
                }
            }
            if (selectedCategoryModelList != null && selectedCategoryModelList.size() > 0) {
                for (int i = 0; i < selectedCategoryModelList.size(); i++) {
                    if (i == 0) {
                        subCategory = new StringBuilder(selectedCategoryModelList.get(i));
                    } else {
                        subCategory.append("#").append(selectedCategoryModelList.get(i));
                    }
                }
            }
            albumModel.setImages(image.toString());
            albumModel.setMainCategories(mainCategory.toString());
            albumModel.setSubCategories(subCategory.toString());
            albumModel.setNote(activityPreviewBinding.descriptionText.getText().toString().trim());
            albumModel.setReminderConverted(reminderConverted);
            albumModel.setReminderActual(reminderActual);
            albumModel.setReminderTime(reminderTime);

            albumRepo.create(albumModel);

            hideLoading();

            openActivity(this, MainActivity.class, false, true);
            slideTopToBottom();
        }, 1000);
    }
}
