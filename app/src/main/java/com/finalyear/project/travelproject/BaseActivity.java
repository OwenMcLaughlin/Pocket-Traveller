package com.finalyear.project.travelproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Owen on 11/02/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        //inflates popup menu on toolbar
        inflater.inflate(R.menu.navigation_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Intent dropdownChoice;

        switch (item.getItemId()) {
            case R.id.action_search:
                //search chosen...
                dropdownChoice = new Intent(this, SearchActivity.class);
                //start the search activity
                startActivity(dropdownChoice);
                return true;

            case R.id.action_recent:
                //recent chosen
                dropdownChoice = new Intent(this, RecentActivity.class);
                startActivity(dropdownChoice);
                return true;

            case R.id.action_maps:
                //maps chosen
                dropdownChoice = new Intent(this, MapChoiceActivity.class);
                startActivity(dropdownChoice);
                return true;

            case R.id.action_currency:
                //currency chosen
                dropdownChoice = new Intent(this, CurrencyConverter.class);
                startActivity(dropdownChoice);
                return true;

            case R.id.action_trips:
                //trips chosen
                dropdownChoice = new Intent(this, TripsActivity.class);
                startActivity(dropdownChoice);
                return true;

            case R.id.action_home:
                //home chosen
                dropdownChoice = new Intent(this, MainActivity.class);
                startActivity(dropdownChoice);
                return true;

            default:
                //else let superclass deal with it
                return super.onOptionsItemSelected(item);
        }
    }
}
