package com.petdox.mct.repo;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.petdox.mct.database.DatabaseHelper;
import com.petdox.mct.database.DatabaseManager;
import com.petdox.mct.model.AlbumModel;

import java.util.List;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public class AlbumRepo implements Crud {

    private DatabaseHelper helper;

    public AlbumRepo() {
        this.helper = DatabaseManager.getInstance().getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;
        AlbumModel object = (AlbumModel) item;

        try {
            index = helper.getAlbumDao().create(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;
        AlbumModel object = (AlbumModel) item;

        try {
            index = helper.getAlbumDao().update(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;
        AlbumModel object = (AlbumModel) item;

        try {
            index = helper.getAlbumDao().delete(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return index;
    }

    @Override
    public Object findById(int id) {

        AlbumModel object = null;

        try {
            object = helper.getAlbumDao().queryForId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public List<?> findAll() {

        List<AlbumModel> items = null;

        try {
            items = helper.getAlbumDao().queryForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    @Override
    public List<?> findCategory(String category) {
        try {
            QueryBuilder<AlbumModel, Integer> qb = helper.getAlbumDao().queryBuilder();
            qb.where().like("mainCategories", "%" + category + "%");
            PreparedQuery<AlbumModel> pq = qb.prepare();
            return helper.getAlbumDao().query(pq);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
