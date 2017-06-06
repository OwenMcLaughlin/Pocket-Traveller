package com.finalyear.project.travelproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MapChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_choice);
        //add the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //local maps button functionality
        Button localButton = (Button)findViewById(R.id.local_button);
        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapChoiceActivity.this, LocalMapActivity.class);
                startActivity(intent);
            }
        });
        //world map button functionality
        Button worldButton = (Button)findViewById(R.id.world_button);
        worldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapChoiceActivity.this, WorldMapActivity.class);
                startActivity(intent);
            }
        });
    }
}
