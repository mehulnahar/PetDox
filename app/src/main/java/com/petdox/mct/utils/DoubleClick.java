package com.petdox.mct.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class DoubleClick implements View.OnClickListener {

    /*
     * Handler to process click event.
     */
    private final Handler mHandler = new Handler(Looper.myLooper());
    /*
     * Click callback.
     */
    private final DoubleClickListener doubleClickListener;
    /*
     * Duration of click interval.
     * 200 milliseconds is a best fit to double click interval.
     */
    private long DOUBLE_CLICK_INTERVAL;  // Time to wait the second click.
    /*
     * Number of clicks in @DOUBLE_CLICK_INTERVAL interval.
     */
    private int clicks;

    /*
     * Flag to check if click handler is busy.
     */
    private boolean isBusy = false;

    /**
     * Builds a DoubleClick.
     *
     * @param doubleClickListener the click listener to notify clicks.
     */
    public DoubleClick(final DoubleClickListener doubleClickListener) {
        this(doubleClickListener, 200L);
        DOUBLE_CLICK_INTERVAL = 200L; // default time to wait the second click.
    }

    public DoubleClick(final DoubleClickListener doubleClickListener, final long DOUBLE_CLICK_INTERVAL) {
        this.doubleClickListener = doubleClickListener;
        this.DOUBLE_CLICK_INTERVAL = DOUBLE_CLICK_INTERVAL; // developer specified time to wait the second click.
    }

    @Override
    public void onClick(final View view) {

        if (!isBusy) {
            //  Prevent multiple click in this short time
            isBusy = true;

            // Increase clicks count
            clicks++;

            mHandler.postDelayed(() -> {

                if (clicks >= 2) {  // Double tap.
                    doubleClickListener.onDoubleClick(view);
                }

                if (clicks == 1) {  // Single tap
                    doubleClickListener.onSingleClick(view);
                }

                // we need to  restore clicks count
                clicks = 0;
            }, DOUBLE_CLICK_INTERVAL);
            isBusy = false;
        }

    }
}
