package com.petdox.mct.editpreview;

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
import com.petdox.mct.R;
import com.petdox.mct.base.BaseActivity;
import com.petdox.mct.callback.CallbackImagePreview;
import com.petdox.mct.callback.CallbackMainCategory;
import com.petdox.mct.callback.CallbackSubCategory;
import com.petdox.mct.database.DatabaseManager;
import com.petdox.mct.databinding.ActivityEditPreviewBinding;
import com.petdox.mct.model.AlbumModel;
import com.petdox.mct.model.CarouselModel;
import com.petdox.mct.preview.AddPictureActivity;
import com.petdox.mct.preview.DisplayImagesActivity;
import com.petdox.mct.preview.adapter.MainCategoryAdapter;
import com.petdox.mct.preview.adapter.PreviewImagesAdapter;
import com.petdox.mct.preview.adapter.ReminderListAdapter;
import com.petdox.mct.preview.adapter.SelectedCategoryAdapter;
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
public class EditPreviewActivity extends BaseActivity implements CallbackMainCategory, CallbackSubCategory, DatePickerDialog.OnDateSetListener, CallbackImagePreview {

    private static final int PERMISSION_CODE = 1002;
    public static ArrayList<String> selectedCategoryModelList = new ArrayList<>();
    private final ArrayList<String> selectedMainCategoryModelList = new ArrayList<>();
    ActivityEditPreviewBinding activityEditPreviewBinding;
    int LAUNCH_ADD_PICTURE_ACTIVITY = 1;
    int LAUNCH_REPLACE_PICTURE_ACTIVITY = 2;
    PreviewImagesAdapter previewImagesAdapter = null;
    MainCategoryAdapter mainCategoryAdapter = null;
    SubEditCategoryAdapter subCategoryAdapter = null;
    SelectedCategoryAdapter selectedCategoryAdapter = null;
    ArrayList<String> imagesList = new ArrayList<>();
    List<MainCategoryModel> mainCategoryModelList = new ArrayList<>();
    String replaceImage = "";
    int currentImagePos = 0;
    CarouselModel carouselModel = null;
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
    AlbumModel albumModelMain = null;
    private AlbumRepo albumRepo;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityEditPreviewBinding = ActivityEditPreviewBinding.inflate(getLayoutInflater());
        setContentView(activityEditPreviewBinding.getRoot());

        activityEditPreviewBinding.imagesRecyclerview.setNestedScrollingEnabled(false);
        activityEditPreviewBinding.mainCategoryList.setNestedScrollingEnabled(false);
        activityEditPreviewBinding.subCategoryList.setNestedScrollingEnabled(false);

        if (selectedCategoryModelList != null && selectedCategoryModelList.size() > 0) {
            selectedCategoryModelList.clear();
        }
        if (selectedMainCategoryModelList != null && selectedMainCategoryModelList.size() > 0) {
            selectedMainCategoryModelList.clear();
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("EDIT_DETAILS")) {
            carouselModel = (CarouselModel) intent.getSerializableExtra("EDIT_DETAILS");
            albumModelMain = (AlbumModel) intent.getSerializableExtra("ALBUM");
        }
        if (carouselModel == null || albumModelMain == null) {
            showToast("No Data Found !!!");
            finish();
        }
        init();

        activityEditPreviewBinding.photoDate.setText(carouselModel.getDate());

        if (carouselModel.getReminderConverted() != null && !carouselModel.getReminderConverted().equalsIgnoreCase("")) {
            activityEditPreviewBinding.addReminderText.setText(carouselModel.getReminderConverted());
        } else {
            activityEditPreviewBinding.addReminderText.setText(getResources().getString(R.string.no_reminder));
        }

        activityEditPreviewBinding.addReminder.setBackgroundResource(R.drawable.rectangle_orange_black_border);

        activityEditPreviewBinding.addCategoryText.setText(getResources().getString(R.string.list_category));
        activityEditPreviewBinding.addCategory.setBackgroundResource(R.drawable.rectangle_dark_green_border);
        activityEditPreviewBinding.saveDetails.setBackgroundResource(R.drawable.rectangle_dark_green_border);
        activityEditPreviewBinding.descriptionText.setText(carouselModel.getNote());
        enableSaved = true;

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

        if (selectedCategoryAdapter == null) {
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(EditPreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
            selectedCategoryAdapter = new SelectedCategoryAdapter(EditPreviewActivity.this);
            activityEditPreviewBinding.selectedCategoryList.setLayoutManager(linearLayoutManager1);
            activityEditPreviewBinding.selectedCategoryList.setAdapter(selectedCategoryAdapter);
        }

        for (int k = 0; k < carouselModel.getSubCategories().size(); k++) {
            selectedCategoryModelList.add(carouselModel.getSubCategories().get(k));
            selectedCategoryAdapter.addCategory(carouselModel.getSubCategories().get(k));
        }


        for (String value : mCategoryArray) {
            List<SubCategoryModel> subCategoryModelList = new ArrayList<>();
            if (value.toLowerCase().contains("cat")) {
                for (String s : catArray) {
                    if (carouselModel.getSubCategories().contains(s)) {
                        subCategoryModelList.add(new SubCategoryModel(s, false, true));
                    } else {
                        subCategoryModelList.add(new SubCategoryModel(s, false, false));
                    }
                }
                if (carouselModel.getMainCategories().contains(value)) {
                    selectedMainCategoryModelList.add(value);
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, true));
                } else {
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
                }
            } else if (value.toLowerCase().contains("dog")) {
                for (String s : dogArray) {
                    if (carouselModel.getSubCategories().contains(s)) {
                        subCategoryModelList.add(new SubCategoryModel(s, false, true));
                    } else {
                        subCategoryModelList.add(new SubCategoryModel(s, false, false));
                    }
                }
                if (carouselModel.getMainCategories().contains(value)) {
                    selectedMainCategoryModelList.add(value);
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, true));
                } else {
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
                }
            } else if (value.toLowerCase().contains("elephant")) {
                for (String s : elephantArray) {
                    if (carouselModel.getSubCategories().contains(s)) {
                        subCategoryModelList.add(new SubCategoryModel(s, false, true));
                    } else {
                        subCategoryModelList.add(new SubCategoryModel(s, false, false));
                    }
                }
                if (carouselModel.getMainCategories().contains(value)) {
                    selectedMainCategoryModelList.add(value);
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, true));
                } else {
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
                }
            } else if (value.toLowerCase().contains("fish")) {
                for (String s : fishArray) {
                    if (carouselModel.getSubCategories().contains(s)) {
                        subCategoryModelList.add(new SubCategoryModel(s, false, true));
                    } else {
                        subCategoryModelList.add(new SubCategoryModel(s, false, false));
                    }
                }
                if (carouselModel.getMainCategories().contains(value)) {
                    selectedMainCategoryModelList.add(value);
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, true));
                } else {
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
                }
            } else if (value.toLowerCase().contains("frog")) {
                for (String s : frogArray) {
                    if (carouselModel.getSubCategories().contains(s)) {
                        subCategoryModelList.add(new SubCategoryModel(s, false, true));
                    } else {
                        subCategoryModelList.add(new SubCategoryModel(s, false, false));
                    }
                }
                if (carouselModel.getMainCategories().contains(value)) {
                    selectedMainCategoryModelList.add(value);
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, true));
                } else {
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
                }
            } else if (value.toLowerCase().contains("monkey")) {
                for (String s : monkeyArray) {
                    if (carouselModel.getSubCategories().contains(s)) {
                        subCategoryModelList.add(new SubCategoryModel(s, false, true));
                    } else {
                        subCategoryModelList.add(new SubCategoryModel(s, false, false));
                    }
                }
                if (carouselModel.getMainCategories().contains(value)) {
                    selectedMainCategoryModelList.add(value);
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, true));
                } else {
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
                }
            } else if (value.toLowerCase().contains("gorilla")) {
                for (String s : gorillaArray) {
                    if (carouselModel.getSubCategories().contains(s)) {
                        subCategoryModelList.add(new SubCategoryModel(s, false, true));
                    } else {
                        subCategoryModelList.add(new SubCategoryModel(s, false, false));
                    }
                }
                if (carouselModel.getMainCategories().contains(value)) {
                    selectedMainCategoryModelList.add(value);
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, true));
                } else {
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
                }
            } else if (value.toLowerCase().contains("cow")) {
                for (String s : cowArray) {
                    if (carouselModel.getSubCategories().contains(s)) {
                        subCategoryModelList.add(new SubCategoryModel(s, false, true));
                    } else {
                        subCategoryModelList.add(new SubCategoryModel(s, false, false));
                    }
                }
                if (carouselModel.getMainCategories().contains(value)) {
                    selectedMainCategoryModelList.add(value);
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, true));
                } else {
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
                }
            } else if (value.toLowerCase().contains("zebra")) {
                for (String s : zebraArray) {
                    if (carouselModel.getSubCategories().contains(s)) {
                        subCategoryModelList.add(new SubCategoryModel(s, false, true));
                    } else {
                        subCategoryModelList.add(new SubCategoryModel(s, false, false));
                    }
                }
                if (carouselModel.getMainCategories().contains(value)) {
                    selectedMainCategoryModelList.add(value);
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, true));
                } else {
                    mainCategoryModelList.add(new MainCategoryModel(value, subCategoryModelList, false, false));
                }
            } else {
                mainCategoryModelList.add(new MainCategoryModel(value, null, false, false));
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        if (previewImagesAdapter == null) {
            previewImagesAdapter = new PreviewImagesAdapter(this);
        }
        previewImagesAdapter.setCallbackImagePreview(this);
        activityEditPreviewBinding.imagesRecyclerview.setLayoutManager(linearLayoutManager);
        activityEditPreviewBinding.imagesRecyclerview.setAdapter(previewImagesAdapter);
        for (int i = 0; i < carouselModel.getImages().size(); i++) {
            imagesList.add(carouselModel.getImages().get(i));
            previewImagesAdapter.addImage(carouselModel.getImages().get(i));
        }

        activityEditPreviewBinding.selectedCategoryList.setVisibility(View.VISIBLE);
        activityEditPreviewBinding.selectedCategoryList.smoothScrollToPosition(Objects.requireNonNull(activityEditPreviewBinding.selectedCategoryList.getAdapter()).getItemCount() - 1);

        MultiSnapHelper multiSnapHelper = new MultiSnapHelper(SnapGravity.START, 1, 100);
        multiSnapHelper.attachToRecyclerView(activityEditPreviewBinding.imagesRecyclerview);

        activityEditPreviewBinding.imagesRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {

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

        activityEditPreviewBinding.addCategory.setOnClickListener(v -> {
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(EditPreviewActivity.this, LinearLayoutManager.VERTICAL, false);
            mainCategoryAdapter = new MainCategoryAdapter(EditPreviewActivity.this);
            mainCategoryAdapter.setCallbackMainCategory(this);
            activityEditPreviewBinding.mainCategoryList.setLayoutManager(linearLayoutManager1);
            activityEditPreviewBinding.mainCategoryList.setAdapter(mainCategoryAdapter);
            mainCategoryAdapter.refreshList(mainCategoryModelList);

            activityEditPreviewBinding.categoryLayout.setVisibility(View.VISIBLE);
            activityEditPreviewBinding.captureView.setVisibility(View.VISIBLE);
        });

        activityEditPreviewBinding.cancel.setOnClickListener(v -> {
            if (activityEditPreviewBinding.categoryLayout.getVisibility() == View.VISIBLE) {
                activityEditPreviewBinding.categoryLayout.setVisibility(View.GONE);
                activityEditPreviewBinding.captureView.setVisibility(View.GONE);
            } else {
                cancelDialog();
            }
        });

        activityEditPreviewBinding.photoDate.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    EditPreviewActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getSupportFragmentManager(), "PhotoDate");

        });

        activityEditPreviewBinding.mainCategoryList.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of  child view
            activityEditPreviewBinding.categoryLayout.requestDisallowInterceptTouchEvent(true);
            return false;
        });

        activityEditPreviewBinding.subCategoryList.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of  child view
            activityEditPreviewBinding.categoryLayout.requestDisallowInterceptTouchEvent(true);
            return false;
        });

        activityEditPreviewBinding.addPicture.setOnClickListener(v -> addPictureDialog());

        activityEditPreviewBinding.replace.setOnClickListener(v -> replacePictureDialog());

        activityEditPreviewBinding.remove.setOnClickListener(v -> {
            if (imagesList != null && imagesList.size() == 1) {
                removePictureDialog();
            } else {
                removeSinglePictureDialog();
            }
        });

        activityEditPreviewBinding.addNoteView.setOnClickListener(v -> {
            String desText = getResources().getString(R.string.description_text);
            String actualDesText = activityEditPreviewBinding.descriptionText.getText().toString().trim();
            if (desText.equalsIgnoreCase(actualDesText)) {
                addNoteDialog("");
            } else {
                addNoteDialog(actualDesText);
            }
        });

        activityEditPreviewBinding.addReminder.setOnClickListener(v -> addReminderDialog());

        activityEditPreviewBinding.saveDetails.setOnClickListener(v -> {
            if (enableSaved) {
                updateData();
            } else {
                showToast("Please select any category/sub-category");
            }
        });

        activityEditPreviewBinding.selectedCategoryList.setOnClickListener(v -> {

        });

        activityEditPreviewBinding.mainScrollview.setOnClickListener(v -> {

        });

        activityEditPreviewBinding.captureView.setOnClickListener(v -> {

        });

        activityEditPreviewBinding.categoryLayout.setOnClickListener(v -> {

        });

        activityEditPreviewBinding.deleteDetails.setOnClickListener(v -> deleteDataPopup());
    }

    private void init() {
        DatabaseManager.init(EditPreviewActivity.this);
        albumRepo = new AlbumRepo();
    }

    @Override
    public void onBackPressed() {
        if (activityEditPreviewBinding.categoryLayout.getVisibility() == View.VISIBLE) {
            activityEditPreviewBinding.categoryLayout.setVisibility(View.GONE);
            activityEditPreviewBinding.captureView.setVisibility(View.GONE);
        } else {
            cancelDialog();
        }
    }

    @Override
    public void selectMainCategory(MainCategoryModel mainCategoryModel) {

        mainCategoryAdapter.notifyDataSetChanged();
        if (mainCategoryModel.getSubCategoryModelList() != null && mainCategoryModel.getSubCategoryModelList().size() > 0) {
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(EditPreviewActivity.this, LinearLayoutManager.VERTICAL, false);
            subCategoryAdapter = new SubEditCategoryAdapter(EditPreviewActivity.this);
            subCategoryAdapter.setCallbackSubCategory(this);
            activityEditPreviewBinding.subCategoryList.setLayoutManager(linearLayoutManager1);
            activityEditPreviewBinding.subCategoryList.setAdapter(subCategoryAdapter);
            subCategoryAdapter.refreshList(mainCategoryModel);
        } else {
            if (selectedCategoryModelList != null && selectedCategoryModelList.size() == 10) {
                showToast("You cannot add more than 10 sub-categories");
            } else {
                if (selectedCategoryAdapter == null) {
                    LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(EditPreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    selectedCategoryAdapter = new SelectedCategoryAdapter(EditPreviewActivity.this);
                    activityEditPreviewBinding.selectedCategoryList.setLayoutManager(linearLayoutManager1);
                    activityEditPreviewBinding.selectedCategoryList.setAdapter(selectedCategoryAdapter);
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

                activityEditPreviewBinding.selectedCategoryList.setVisibility(View.VISIBLE);
                activityEditPreviewBinding.selectedCategoryList.smoothScrollToPosition(Objects.requireNonNull(activityEditPreviewBinding.selectedCategoryList.getAdapter()).getItemCount() - 1);
                activityEditPreviewBinding.saveDetails.setBackgroundResource(R.drawable.rectangle_dark_green_border);
                enableSaved = true;
            }

        }
    }

    @Override
    public void selectSubCategory(SubCategoryModel subCategoryModel, MainCategoryModel mainCategoryModel, boolean anySaved) {

        mainCategoryAdapter.notifyDataSetChanged();
        if (selectedCategoryAdapter == null) {
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(EditPreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
            selectedCategoryAdapter = new SelectedCategoryAdapter(EditPreviewActivity.this);
            activityEditPreviewBinding.selectedCategoryList.setLayoutManager(linearLayoutManager1);
            activityEditPreviewBinding.selectedCategoryList.setAdapter(selectedCategoryAdapter);
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
            assert selectedMainCategoryModelList != null;
            selectedMainCategoryModelList.add(mainCategoryModel.getCategoryName());
        } else {
            selectedCategoryModelList.remove(subCategoryModel.getCategoryName());
            selectedCategoryAdapter.removeCategory(subCategoryModel.getCategoryName());
            if (!anySaved) {
                selectedMainCategoryModelList.remove(mainCategoryModel.getCategoryName());
            }
        }
        if (selectedCategoryModelList != null && selectedCategoryModelList.size() > 0) {
            activityEditPreviewBinding.selectedCategoryList.setVisibility(View.VISIBLE);
            activityEditPreviewBinding.selectedCategoryList.smoothScrollToPosition(Objects.requireNonNull(activityEditPreviewBinding.selectedCategoryList.getAdapter()).getItemCount() - 1);
            activityEditPreviewBinding.addCategoryText.setText(getResources().getString(R.string.list_category));
            activityEditPreviewBinding.addCategory.setBackgroundResource(R.drawable.rectangle_dark_green_border);
            activityEditPreviewBinding.saveDetails.setBackgroundResource(R.drawable.rectangle_dark_green_border);
            enableSaved = true;
        } else {
            selectedCategoryModelList = new ArrayList<>();
            activityEditPreviewBinding.selectedCategoryList.setVisibility(View.GONE);
            activityEditPreviewBinding.addCategoryText.setText(getResources().getString(R.string.add_category));
            activityEditPreviewBinding.addCategory.setBackgroundResource(R.drawable.rectangle_white_black_border);
            activityEditPreviewBinding.saveDetails.setBackgroundResource(R.drawable.rectangle_grey);
            enableSaved = false;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = monthOfYear + 1;
        if (dayOfMonth < 10) {
            if (monthOfYear < 10) {
                activityEditPreviewBinding.photoDate.setText(String.format(Locale.ENGLISH, "0%d.0%d.%d", dayOfMonth, monthOfYear, year));
            } else {
                activityEditPreviewBinding.photoDate.setText(String.format(Locale.ENGLISH, "0%d.%d.%d", dayOfMonth, monthOfYear, year));
            }
        } else if (monthOfYear < 10) {
            activityEditPreviewBinding.photoDate.setText(String.format(Locale.ENGLISH, "%d.0%d.%d", dayOfMonth, monthOfYear, year));
        } else {
            activityEditPreviewBinding.photoDate.setText(String.format(Locale.ENGLISH, "%d.%d.%d", dayOfMonth, monthOfYear, year));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0) {

                if (PermissionUtils.isStorageGranted(EditPreviewActivity.this) && PermissionUtils.isCameraGranted(EditPreviewActivity.this)) {

                    Intent i = new Intent(EditPreviewActivity.this, AddPictureActivity.class);
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
                    PermissionUtils.checkBothPermission(EditPreviewActivity.this, PERMISSION_CODE);
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.cancel),
                (dialog12, id) -> {
                    dialog12.cancel();
                    PermissionUtils.checkBothPermission(EditPreviewActivity.this, PERMISSION_CODE);
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
                activityEditPreviewBinding.imagesRecyclerview.smoothScrollToPosition(Objects.requireNonNull(activityEditPreviewBinding.imagesRecyclerview.getAdapter()).getItemCount() - 1);

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

                        if (PermissionUtils.isStorageGranted(EditPreviewActivity.this) && PermissionUtils.isCameraGranted(EditPreviewActivity.this)) {
                            Intent i = new Intent(EditPreviewActivity.this, AddPictureActivity.class);
                            startActivityForResult(i, LAUNCH_ADD_PICTURE_ACTIVITY);
                            slideRightToLeft();
                        } else {
                            PermissionUtils.checkBothPermission(EditPreviewActivity.this, PERMISSION_CODE);
                        }

                    }
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> dialog12.cancel());

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
                    if (PermissionUtils.isStorageGranted(EditPreviewActivity.this) && PermissionUtils.isCameraGranted(EditPreviewActivity.this)) {
                        Intent i = new Intent(EditPreviewActivity.this, AddPictureActivity.class);
                        startActivityForResult(i, LAUNCH_REPLACE_PICTURE_ACTIVITY);
                        slideRightToLeft();
                    } else {
                        PermissionUtils.checkBothPermission(EditPreviewActivity.this, PERMISSION_CODE);
                    }
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> dialog12.cancel());

        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private void removePictureDialog() {

        replaceImage = "";

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Delete Details");
        dialog.setMessage("Do you want to remove this picture ? This will also delete your album details.");
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.Yes),
                (dialog1, id) -> {
                    dialog1.cancel();
                    deleteData();
                    finish();
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> dialog12.cancel());

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
        dialog.setMessage("Do you want to discard your edit details ?");
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.Yes),
                (dialog1, id) -> {
                    dialog1.cancel();
                    finish();
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> dialog12.cancel());

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
                    if (imagesList != null) {
                        imagesList.size();
                        previewImagesAdapter.removeImage(imagesList.get(currentImagePos));
                        imagesList.remove(currentImagePos);
                    }
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> dialog12.cancel());

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
                    String textNew = Objects.requireNonNull(etComments.getText()).toString().trim();
                    if (!textNew.equalsIgnoreCase("")) {
                        activityEditPreviewBinding.descriptionText.setText(textNew);
                    }
                });

        alertDialog.setNegativeButton(
                getResources().getString(R.string.cancel),
                (dialog12, id) -> dialog12.cancel());


        AlertDialog alert = alertDialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    @SuppressLint("SetTextI18n")
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
                activityEditPreviewBinding.addReminderText.setText(reminderConverted);
                activityEditPreviewBinding.addReminder.setBackgroundResource(R.drawable.rectangle_orange_black_border);
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

    private void deleteDataPopup() {

        replaceImage = "";

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Delete Details");
        dialog.setMessage("Do you want to delete your album details.");
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.Yes),
                (dialog1, id) -> {
                    dialog1.cancel();
                    deleteData();
                    finish();
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog12, id) -> dialog12.cancel());

        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }
    }

    private void deleteData() {
        showLoading("");

        new Handler().postDelayed(() -> {

            albumRepo.delete(albumModelMain);

            hideLoading();

            finish();
        }, 1000);
    }

    private void updateData() {

        showLoading("");

        new Handler().postDelayed(() -> {

            StringBuilder image = new StringBuilder();
            StringBuilder mainCategory = new StringBuilder();
            StringBuilder subCategory = new StringBuilder();

            albumModelMain.setDate(activityEditPreviewBinding.photoDate.getText().toString().trim());
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
            albumModelMain.setImages(image.toString());
            albumModelMain.setMainCategories(mainCategory.toString());
            albumModelMain.setSubCategories(subCategory.toString());
            albumModelMain.setNote(activityEditPreviewBinding.descriptionText.getText().toString().trim());
            albumModelMain.setReminderConverted(reminderConverted);
            albumModelMain.setReminderActual(reminderActual);
            albumModelMain.setReminderTime(reminderTime);

            albumRepo.update(albumModelMain);

            hideLoading();

            finish();
        }, 1000);
    }
}
