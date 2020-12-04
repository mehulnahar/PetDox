package com.petdox.mct.preview.model;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class SubCategoryModel {

    String categoryName = "";
    boolean selected = false;
    boolean saved = false;

    public SubCategoryModel(String categoryName, boolean selected, boolean saved) {
        this.categoryName = categoryName;
        this.selected = selected;
        this.saved = saved;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
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
