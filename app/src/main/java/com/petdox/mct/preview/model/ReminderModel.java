package com.petdox.mct.preview.model;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class ReminderModel {

    String reminderName = "";
    boolean selected = false;
    boolean allow = false;

    public ReminderModel(String reminderName, boolean selected, boolean allow) {
        this.reminderName = reminderName;
        this.selected = selected;
        this.allow = allow;
    }

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public String getReminderName() {
        return reminderName;
    }

    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
