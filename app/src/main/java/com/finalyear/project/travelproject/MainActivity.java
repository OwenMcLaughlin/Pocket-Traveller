package com.finalyear.project.travelproject;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


public class MainActivity extends BaseActivity {

    private Button searchButton;
    private Button recentButton;
    private Button mapButton;
    private Button tripsButton;
    private ButtonListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add the toolbar to the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        instantiateButtons();
    }

    public void instantiateButtons(){
        searchButton = (Button) findViewById(R.id.search_button);
        recentButton = (Button) findViewById(R.id.recent_button);
        mapButton = (Button) findViewById(R.id.map_button);
        tripsButton = (Button) findViewById(R.id.trips_button);

        listener = new ButtonListener();

        searchButton.setOnClickListener(listener);
        recentButton.setOnClickListener(listener);
        mapButton.setOnClickListener(listener);
        tripsButton.setOnClickListener(listener);

    }
    /************************INNER CLASS********************/
    class ButtonListener implements View.OnClickListener{
        public void onClick(View v) {

            Intent intent;

            //if the view clicked matches a button, start the appropriate activity
            if (v.equals(searchButton)) {
                intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            } else if (v.equals(recentButton)) {
                intent = new Intent(MainActivity.this, RecentActivity.class);
                startActivity(intent);
            } else if (v.equals(mapButton)) {
                intent = new Intent(MainActivity.this, MapChoiceActivity.class);
                startActivity(intent);
            } else {
                intent = new Intent(MainActivity.this, TripsActivity.class);
                startActivity(intent);
            }

        }
    }
    /*************************************************************/
}