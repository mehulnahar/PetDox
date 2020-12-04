package com.petdox.mct.repo;

import java.util.List;

/**
 * Created by Maroof Ahmed Siddique
 * maroofahmedsiddique@gmail.com
 */
public interface Crud {

    int create(Object item);

    int update(Object item);

    int delete(Object item);

    Object findById(int id);

    List<?> findAll();

    List<?> findCategory(String category);
}
