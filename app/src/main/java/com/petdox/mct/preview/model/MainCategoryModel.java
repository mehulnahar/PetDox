package com.petdox.mct.preview.model;

import java.util.List;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class MainCategoryModel {

    String categoryName = "";
    boolean selected = false;
    boolean saved = false;
    List<SubCategoryModel> subCategoryModelList;

    public MainCategoryModel(String categoryName, List<SubCategoryModel> subCategoryModelList, boolean selected, boolean saved) {
        this.categoryName = categoryName;
        this.subCategoryModelList = subCategoryModelList;
        this.selected = selected;
        this.saved = saved;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public List<SubCategoryModel> getSubCategoryModelList() {
        return subCategoryModelList;
    }

    public void setSubCategoryModelList(List<SubCategoryModel> subCategoryModelList) {
        this.subCategoryModelList = subCategoryModelList;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
