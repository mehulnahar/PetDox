package com.petdox.mct.database;

import android.content.Context;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class DatabaseManager {

    static private DatabaseManager instance;
    private final DatabaseHelper helper;

    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    static public void init(Context ctx) {
        if (null == instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    public DatabaseHelper getHelper() {
        return helper;
    }
}
