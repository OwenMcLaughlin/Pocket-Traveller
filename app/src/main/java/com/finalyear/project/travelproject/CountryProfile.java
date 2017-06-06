package com.finalyear.project.travelproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class CountryProfile extends BaseActivity {

    private static final String TAG = "Test:";
    private ImageButton flagButton;
    private Button profileButton;
    private Button currencyButton;
    private Button phrasesButton;
    private Button emergencyButton;
    private ButtonListener listener;
    private String countryName;
    private TextView languages;
    private String languageInfo;
    private String currencyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_profile);
        //add the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //instantiate the 4 buttons (+flag) and create a mini profile
        instantiateButtons();
        createMiniProfile();
    }

    public void createMiniProfile(){
        //new database instance
        AppDatabase db = new AppDatabase(this);
        //get the countryname passed with the intent from the previous activity
        countryName = getIntent().getStringExtra("countryName");
        //calls getCountryDetails and stores result
        Cursor c = db.getCountryDetails(countryName);

            //set the page title to the country name
        TextView pageTitle = (TextView) findViewById(R.id.country_name);
        if(getResources().getDisplayMetrics().widthPixels>getResources().getDisplayMetrics().heightPixels) {

        }else{
            pageTitle.setText(c.getString(c.getColumnIndex("country_name")));
        }
        //set the flag (png file)
        String flagFile = c.getString(c.getColumnIndex("flag_file"));
        //adds flag image to the imagebutton
        ImageButton imageButton = (ImageButton) findViewById(R.id.countryImageButton);
        int resId = getResources().getIdentifier(flagFile, "drawable", getPackageName());
        imageButton.setImageResource(resId);

        //builds the infoList

        //get the capital
        TextView capital = (TextView) findViewById(R.id.capital);
        capital.setText("Capital: "+c.getString(c.getColumnIndex("capital")));
        //get the currency
        TextView currency = (TextView) findViewById(R.id.currency);
        currency.setText("Currency: "+c.getString(c.getColumnIndex("currency")));
       // get the languages
        languages = (TextView) findViewById(R.id.languages);
        languages.setText("Languages: "+c.getString(c.getColumnIndex("languages")));
        //get the weather status
        TextView weather = (TextView) findViewById(R.id.weather);
        weather.setText("Weather: "+c.getString(c.getColumnIndex("weather_status")));
        //get the state status
        TextView state = (TextView) findViewById(R.id.state);
        state.setText("State: "+c.getString(c.getColumnIndex("state_status")));
        //get the conflict status
        TextView conflict = (TextView) findViewById(R.id.conflict);
        conflict.setText("Conflict: "+c.getString(c.getColumnIndex("conflict_status")));

        //info to be passed to other activities
        languageInfo = c.getString(c.getColumnIndex("languages"));
        currencyInfo = c.getString(c.getColumnIndex("currency"));
        //close the database
        db.close();

    }

    public void instantiateButtons(){
        flagButton = (ImageButton) findViewById(R.id.countryImageButton);
        profileButton = (Button) findViewById(R.id.profile_button);
        currencyButton = (Button) findViewById(R.id.currency_button);
        phrasesButton = (Button) findViewById(R.id.phrases_button);
        emergencyButton = (Button) findViewById(R.id.emergency_button);

        //add listener
        listener = new ButtonListener();

        flagButton.setOnClickListener(listener);
        profileButton.setOnClickListener(listener);
        currencyButton.setOnClickListener(listener);
        phrasesButton.setOnClickListener(listener);
        emergencyButton.setOnClickListener(listener);
    }

    /**********INNER CLASS****************/
    class ButtonListener implements View.OnClickListener{
        public void onClick(View v){

            Intent intent;
    //if the view clicked matches a button, start the appropriate activity
            if(v.equals(flagButton)||v.equals(profileButton)){
                intent = new Intent(CountryProfile.this, CountryTabbedActivity.class);
                //add information to be used in next activity
                intent.putExtra("countryName", countryName);
                //start next activity
                startActivity(intent);
            }
            else if(v.equals(currencyButton)){
                intent = new Intent(CountryProfile.this, CurrencyConverter.class);
                intent.putExtra("countryName", countryName);
                intent.putExtra("currencyInfo", currencyInfo);
                startActivity(intent);
            }
            else if(v.equals(phrasesButton)){
                intent = new Intent(CountryProfile.this, LanguageChoiceActivity.class);
                intent.putExtra("languageInfo", languageInfo);
                intent.putExtra("countryName", countryName);
                Log.d(TAG, languageInfo);
                startActivity(intent);
            }
            //else is emergency button
            else{
                intent = new Intent(CountryProfile.this, EmergencyNumbersActivity.class);
                intent.putExtra("countryName", countryName);
                startActivity(intent);
            }
        }
    }
}
