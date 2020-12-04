package com.petdox.mct.callback;

import com.petdox.mct.preview.model.MainCategoryModel;
import com.petdox.mct.preview.model.SubCategoryModel;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public interface CallbackSubCategory {

    void selectSubCategory(SubCategoryModel subCategoryModel, MainCategoryModel mainCategoryModel, boolean anySaved);
}
