package com.petdox.mct.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.muddzdev.styleabletoast.StyleableToast;
import com.petdox.mct.R;
import com.petdox.mct.callback.CallbackActivity;
import com.petdox.mct.utils.ConstantUtils;
import com.petdox.mct.utils.NetworkAvailability;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */

public class BaseActivity extends AppCompatActivity implements CallbackActivity {

    private ProgressDialog mProgressDialog;
    private Vibrator vibrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void showLoading(String message) {

        try {
            hideLoading();
            mProgressDialog = ConstantUtils.showLoadingDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideLoading() {

        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkAvailability.checkNetworkStatus(this);
    }

    @Override
    public void hideKeyboard() {
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void slideTopToBottom() {
        overridePendingTransition(R.anim.enter_top, R.anim.exit_bottom);
    }

    @Override
    public void slideBottomToTop() {
        overridePendingTransition(R.anim.enter_bottom, R.anim.exit_top);
    }

    @Override
    public void slideLeftToRight() {
        overridePendingTransition(R.anim.enter_left, R.anim.exit_right);
    }

    @Override
    public void innToOut() {
        overridePendingTransition(R.anim.dialog_in, R.anim.dialog_out);
    }

    @Override
    public void slideRightToLeft() {
        overridePendingTransition(R.anim.enter_right, R.anim.exit_left);
    }

    @Override
    public void openActivity(Context packageContext, Class<?> cls, boolean isFinish, boolean isFinishAffinity) {
        Intent intent = new Intent(packageContext, cls);
        startActivity(intent);
        if (isFinish) {
            finish();
        } else if (isFinishAffinity) {
            finishAffinity();
        }
    }

    @Override
    public void vibrate() {
        vibrator.vibrate(100);
    }

    @Override
    public void vibrateLong() {
        vibrator.vibrate(300);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showKeyboard(View view) {
        view.requestFocus();
        view.postDelayed(() -> {
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(view, 0);
                }
                , 200);
    }

    public void showToast(String message) {
        new StyleableToast
                .Builder(this)
                .text(message)
                .textColor(Color.WHITE)
                .length(Toast.LENGTH_SHORT)
                .backgroundColor(ContextCompat.getColor(this, R.color.toast_background))
                .show();
    }
}
