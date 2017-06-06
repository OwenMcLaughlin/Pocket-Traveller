package com.finalyear.project.travelproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhraseActivity extends BaseActivity {

    private RelativeLayout relativeLayout;
    private String languageSelected;
    private String categorySelected;
    private String countryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase);
        //add the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //get the layout for use with the popup
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_phrase);
        //get the language and category from the intent
        languageSelected = getIntent().getStringExtra("LanguageSelected");
        categorySelected = getIntent().getStringExtra("categorySelected");
        countryName = getIntent().getStringExtra("countryName");
        //create the list of phrases
        createPhraseList();
        //back to profile page functionality
        Button backToProfileButton = (Button)findViewById(R.id.back_to_profile);
        backToProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhraseActivity.this, CountryProfile.class);
                intent.putExtra("countryName", countryName);
                startActivity(intent);
            }
        });
    }

    public void createPhraseList(){
        //create an array of phrases
        String[] phraseArray = getPhraseResults();
        //create a list adapter
        ListAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, phraseArray);
        ListView phrasesLV = (ListView)findViewById(R.id.phrases);
        //set the adapter
        phrasesLV.setAdapter(listAdapter);
        //when the user selects a phrase call showPhrasePopup()
        phrasesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPhrasePopup(String.valueOf(parent.getItemAtPosition(position)));
            }
        });
    }

    public String[] getPhraseResults(){
        AppDatabase db = new AppDatabase(this);
        //get the phrases using the language and category selected
        Cursor c = db.getPhrases(languageSelected, categorySelected);

        String[] results = new String[c.getCount()];
        int i = 0;
        //store the results in an array for use with the adapter
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("initial"))!= null) {
                results[i] = c.getString(c.getColumnIndex("initial"));
                c.moveToNext();
                i++;
            }
        }
        db.close();
        return results;
    }

    //pop up!
    public void showPhrasePopup(final String initialPhrase){
        //inflate the popup (references the popup layout XML)
        LayoutInflater layoutInflater = (LayoutInflater)PhraseActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupLayout = layoutInflater.inflate(R.layout.popup_layout, null);
        //final because accessed from inner class
        final PopupWindow popup = new PopupWindow(popupLayout, Toolbar.LayoutParams.MATCH_PARENT,
                Toolbar.LayoutParams.WRAP_CONTENT, true);
        //set the text and translation
        final TextView init = (TextView) popupLayout.findViewById(R.id.initial);
        TextView trans = (TextView) popupLayout.findViewById(R.id.translation);

        init.setText(initialPhrase);
        trans.setText(getTranslation(initialPhrase));

        Button audioButton = (Button)popupLayout.findViewById(R.id.listen_button);
        //get the audiofile associated with the phrase
        int resId = getResources().getIdentifier(getAudio(initialPhrase), "raw", getPackageName());
        //create media player
        final MediaPlayer player = MediaPlayer.create(PhraseActivity.this, resId);

        audioButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play audio
                player.start();
            }
        });

        Button closeButton = (Button) popupLayout.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss popup
                //remove media player from system resources
                player.release();
                popup.dismiss();
            }
        });

        //show the popup
        popup.setBackgroundDrawable(new ColorDrawable(Color.CYAN));
        popup.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
    }

    //get translations and audiofile to be used in popup window
    public String getTranslation(String english){
        AppDatabase db = new AppDatabase(this);
        Cursor c = db.getPhrases(languageSelected, categorySelected);

        String translation = "";

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("initial"))!= null) {
                if(!english.equals(c.getString(c.getColumnIndex("initial")))){
                    c.moveToNext();
                }
                else{
                    translation = c.getString(c.getColumnIndex("translation"));
                    break;
                }
            }
        }
        db.close();
        return translation;
    }



    public String getAudio(String english){
        AppDatabase db = new AppDatabase(this);
        Cursor c = db.getPhrases(languageSelected, categorySelected);

        String audioFile = "";

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("initial"))!= null) {
                if(!english.equals(c.getString(c.getColumnIndex("initial")))){
                    c.moveToNext();
                }
                else{
                    audioFile = c.getString(c.getColumnIndex("audio_file"));
                    break;
                }
            }
        }
        db.close();
        return audioFile;
    }
}
