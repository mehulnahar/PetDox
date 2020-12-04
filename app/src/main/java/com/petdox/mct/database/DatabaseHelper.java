package com.petdox.mct.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.petdox.mct.model.AlbumModel;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "PetDox.db";
    private static final int DATABASE_VERSION = 1;

    // database access objects
    private Dao<AlbumModel, Integer> albumDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, AlbumModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, AlbumModel.class, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Dao<AlbumModel, Integer> getAlbumDao() {
        if (albumDao == null) {
            try {
                albumDao = getDao(AlbumModel.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return albumDao;
    }
}
