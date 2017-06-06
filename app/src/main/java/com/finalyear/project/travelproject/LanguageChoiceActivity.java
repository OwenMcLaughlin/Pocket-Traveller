package com.finalyear.project.travelproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class LanguageChoiceActivity extends BaseActivity {

    private static final String TAG = "languageTest";
    private String countryName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_choice);
        //add the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //get the languages from the intent
        String languages = getIntent().getStringExtra("languageInfo");
        countryName = getIntent().getStringExtra("countryName");
        createLanguageOptions(languages);
    }

    public void createLanguageOptions(String languagesIn){
        //split languages up into an array by comma
        String[] languageArray = languagesIn.split(",");
        //create list adapter to bridge listview and data
        ListAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, languageArray);
        ListView languagesLV = (ListView)findViewById(R.id.languages);
        //set adapter
        languagesLV.setAdapter(listAdapter);
        //user language selection functionality
        languagesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LanguageChoiceActivity.this, LanguageCategoryActivity.class);
                intent.putExtra("languageSelected", String.valueOf(parent.getItemAtPosition(position)));
                intent.putExtra("countryName", countryName);
                Log.d(TAG, String.valueOf(parent.getItemAtPosition(position)));
                startActivity(intent);
            }
        });

    }
}
