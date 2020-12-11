package com.petdox.mct.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class CarouselModel implements Serializable {

    String date = "";
    String reminderActual = "";
    String reminderConverted = "";
    String reminderTime = "";
    String note = "";
    List<String> images;
    List<String> mainCategories;
    List<String> subCategories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReminderActual() {
        return reminderActual;
    }

    public void setReminderActual(String reminderActual) {
        this.reminderActual = reminderActual;
    }

    public String getReminderConverted() {
        return reminderConverted;
    }

    public void setReminderConverted(String reminderConverted) {
        this.reminderConverted = reminderConverted;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getMainCategories() {
        return mainCategories;
    }

    public void setMainCategories(List<String> mainCategories) {
        this.mainCategories = mainCategories;
    }

    public List<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<String> subCategories) {
        this.subCategories = subCategories;
    }
}
