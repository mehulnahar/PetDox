package com.petdox.mct.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
@DatabaseTable(tableName = "album")
public class AlbumModel implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "note")
    private String note;

    @DatabaseField(columnName = "date")
    private String date;

    @DatabaseField(columnName = "images")
    private String images;

    @DatabaseField(columnName = "mainCategories")
    private String mainCategories;
    @DatabaseField(columnName = "subCategories")
    private String subCategories;
    @DatabaseField(columnName = "reminderConverted")
    private String reminderConverted;
    @DatabaseField(columnName = "reminderActual")
    private String reminderActual;
    @DatabaseField(columnName = "reminderTime")
    private String reminderTime;

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getMainCategories() {
        return mainCategories;
    }

    public void setMainCategories(String mainCategories) {
        this.mainCategories = mainCategories;
    }

    public String getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(String subCategories) {
        this.subCategories = subCategories;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getReminderConverted() {
        return reminderConverted;
    }

    public void setReminderConverted(String reminderConverted) {
        this.reminderConverted = reminderConverted;
    }

    public String getReminderActual() {
        return reminderActual;
    }

    public void setReminderActual(String reminderActual) {
        this.reminderActual = reminderActual;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
