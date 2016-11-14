package com.mathheals.meajuda.presenter;

import android.content.Context;

import com.mathheals.meajuda.dao.JSONHelper;
import com.mathheals.meajuda.model.School;

import org.json.JSONException;

import java.util.List;

public class SchoolPresenter {

    private static SchoolPresenter instance;

    public static SchoolPresenter getInstance() {
        if (instance == null) {
            instance = new SchoolPresenter();
        }
        return instance;
    }

    public School showSchool ( String schooCode) throws JSONException {

        JSONHelper jsonHelper = new JSONHelper();

        School school = jsonHelper.getSchoolByCode(schooCode);

        return school;
    }

    public List<School> getAllSchools() throws JSONException {
        JSONHelper jsonHelper = JSONHelper.getInstance();
        return jsonHelper.getAllSchools();
    }

    
}
