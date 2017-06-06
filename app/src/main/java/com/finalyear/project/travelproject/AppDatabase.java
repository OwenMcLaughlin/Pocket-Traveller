package com.finalyear.project.travelproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created on 08/02/2017.
 */


public class AppDatabase extends SQLiteAssetHelper {

    private static final String TAG = "test";
    private static final String DATABASE_NAME = "CountryProject_5.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db = getReadableDatabase();
    private SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

    //constructor
    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //methods
    public Cursor getCountries() {

        //set the table to query
        queryBuilder.setTables("country");
        //execute the query and store result in cursor object
        Cursor result = queryBuilder.query(db, new String[]{"country_name"}, null, null, null, null, null);
        //point the cursor to first result
        result.moveToFirst();
        return result;
    }

    public Cursor getCountryDetails(String countryName) {

        queryBuilder.setTables("Country");
        Cursor result = queryBuilder.query(db, null, "country_name = '" + countryName + "'", null, null, null, null);

        result.moveToFirst();
        return result;
    }

    public Cursor getLanguageCategories() {

        queryBuilder.setTables("Category");
        Cursor result = queryBuilder.query(db, new String[]{"cat_name"}, null, null, null, null, null);

        result.moveToFirst();
        return result;
    }

    public Cursor getPhrases(String language, String category) {

        queryBuilder.setTables("Phrase");
        Cursor result = queryBuilder.query(db, null, "cat_name = '" + category + "' AND lang_name = '" + language + "'", null, null, null, null);

        result.moveToFirst();
        return result;
    }

    public Cursor getEmergencyContacts(String countryName) {

        queryBuilder.setTables("Country");
        Cursor result = queryBuilder.query(db, new String[]{"contacts"}, "country_name = '" + countryName + "'", null, null, null, null);

        result.moveToFirst();
        return result;
    }
}

