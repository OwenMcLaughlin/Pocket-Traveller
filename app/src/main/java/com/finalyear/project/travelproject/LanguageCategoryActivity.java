package com.finalyear.project.travelproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class LanguageCategoryActivity extends BaseActivity {

    private static final String TAG = "language";
    private String countryName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_category);
        //add toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //get the country name from the intent
        countryName = getIntent().getStringExtra("countryName");

        createCategoryOptions();
    }

    public void createCategoryOptions(){
        //get the categories from the db and put them in an array
        String[] categoryArray = getCategoryResults();

        //create a list adapter
        ListAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryArray);
        ListView categoriesLV = (ListView)findViewById(R.id.categories);
        //set the adapter
        categoriesLV.setAdapter(listAdapter);

        //on user selection functionality
        categoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LanguageCategoryActivity.this, PhraseActivity.class);
                //add the category
                intent.putExtra("categorySelected", String.valueOf(parent.getItemAtPosition(position)));
                //get the language and add
                String languageSelected = getIntent().getStringExtra("languageSelected");
                intent.putExtra("LanguageSelected", languageSelected);
                intent.putExtra("countryName", countryName);
                Log.d(TAG, languageSelected);
                startActivity(intent);
            }
        });
    }


    public String[] getCategoryResults(){
        AppDatabase db = new AppDatabase(this);
        //query the database for the language categories
        Cursor c = db.getLanguageCategories();

        String[] results = new String[c.getCount()];
        int i = 0;
        //add results to an array for use with the adapter
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cat_name"))!= null) {
                results[i] = c.getString(c.getColumnIndex("cat_name"));
                c.moveToNext();
                i++;
            }
        }
        //close the db
        db.close();

        return results;
    }
}
