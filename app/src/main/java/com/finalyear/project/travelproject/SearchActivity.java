package com.finalyear.project.travelproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //add the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //array of countries returned from database
        String[] countries = getDropdownResults();

        //create a new array adapter to bridge data with corresponding View
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, countries);

        //reference autocomplete text view
        final AutoCompleteTextView textView =
                (AutoCompleteTextView) findViewById(R.id.countries_dropdown);

        //threshold at 0 causes autocomplete function after 1 letter is entered
        textView.setThreshold(0);
        textView.setAdapter(adapter);

        //add a click listener to start a new intent
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent viewCountryIntent = new Intent(SearchActivity.this, CountryProfile.class);
                //get the users country choice
                String choice = textView.getText().toString();
                //add to an intent and start the country profile activity
                viewCountryIntent.putExtra("countryName", choice);
                startActivity(viewCountryIntent);
            }
        });
    }

    public String[] getDropdownResults(){
        //new database instance
        AppDatabase db = new AppDatabase(this);
        //call getCountries method and store result in cursor
        Cursor c = db.getCountries();
        //instantiate results array of same size as cursor
        String[] results = new String[c.getCount()];
        int i = 0;
        //adds all country names to results array
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("country_name"))!= null) {
                results[i] = c.getString(c.getColumnIndex("country_name"));
                c.moveToNext();
                i++;
            }
        }
        //close the database
        db.close();
        //return the array for use in autocomplete text view
        return results;
    }
}
