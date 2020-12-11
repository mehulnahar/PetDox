package com.petdox.mct.utils;

import android.view.View;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public interface DoubleClickListener {

    /**
     * Called when the user make a single click.
     */
    void onSingleClick(final View view);

    /**
     * Called when the user make a double click.
     */
    void onDoubleClick(final View view);
}
