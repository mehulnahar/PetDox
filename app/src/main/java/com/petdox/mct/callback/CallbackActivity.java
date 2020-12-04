package com.petdox.mct.callback;

import android.content.Context;


/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
public interface CallbackActivity {

    void showLoading(String message);

    void hideLoading();

    boolean isNetworkConnected();

    void hideKeyboard();

    void slideTopToBottom();

    void slideBottomToTop();

    void slideLeftToRight();

    void innToOut();

    void slideRightToLeft();

    void openActivity(Context packageContext, Class<?> cls, boolean isFinish, boolean isFinishAffinity);

    void vibrate();

    void vibrateLong();

}
