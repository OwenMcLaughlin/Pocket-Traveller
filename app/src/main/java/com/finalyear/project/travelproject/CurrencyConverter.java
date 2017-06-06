package com.finalyear.project.travelproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import cz.msebera.android.httpclient.Header;

public class CurrencyConverter extends BaseActivity {

    private static final String TAG = "CurrencyCon";
    private String url;
    private String currencyCodes;
    private String currIn;
    private String currOut;
    private double rate;
    private Spinner currencyInSpinner;
    private Spinner currencyOutSpinner;
    private TextView inputView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);
        //add the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //initializing dropdowns
        currencyInSpinner = (Spinner) findViewById(R.id.spinner_currency_in);
        currencyOutSpinner = (Spinner) findViewById(R.id.spinner_currency_out);
        //create an array adapter for the spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the adapter
        currencyInSpinner.setAdapter(adapter);
        currencyOutSpinner.setAdapter(adapter);

        //preset to country selected
        Intent intent = getIntent();
        //if the intent passed has extra information, get it and preset the spinner
        if (intent.hasExtra("currencyInfo")){
            String currencyOption = intent.getStringExtra("currencyInfo");
            currencyOutSpinner.setSelection(adapter.getPosition(currencyOption), true);
        }

        //convert button functionality
        final Button convertButton = (Button) findViewById(R.id.convert_button);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencyCodes = makeCurrCodes();
                url = buildYahooQuery(currencyCodes);
                getConversionRate(url);
                calcAndDisplayConversion();
            }
        });

        //reset button functionality
        Button resetButton = (Button) findViewById(R.id.reset_button);
        inputView = (TextView)findViewById(R.id.money_input);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sets the input to 1 and executes conversion function
                inputView.setText("1");
                convertButton.performClick();
            }
        });
        convertButton.performClick(); //display default conversion values
    }


    public String makeCurrCodes(){
        //get the first value and extract the currency code
        currIn = currencyInSpinner.getSelectedItem().toString();
        currIn = currIn.split(" ")[0]; //[0] discards the rest of the string
        //get the second value and extract the currency code
        currOut = currencyOutSpinner.getSelectedItem().toString();
        currOut = currOut.split(" ")[0];

        //join them together and return
        String currCode = currIn+currOut;
        System.out.println(currCode);
        return currCode;
    }

    public String buildYahooQuery(String currCodes){
        //url executes a conversion query to the yahoo finance API
        url = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20" +
                "pair%20in%20(%22"+currCodes+"%22)&env=store://datatables.org/alltableswithkeys";
        return url;
    }


    public double getConversionRate(String yahooQuery){
        //new AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        //send the query and create a AsyncHttpResponseHandler to handle the response
        client.get(yahooQuery, new AsyncHttpResponseHandler() {
            @Override
            //if successful.....
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Success!");
                //coerce bytestream to a string
                String response = new String(responseBody);
                //new XmlPullParser object
                XmlPullParser parser = Xml.newPullParser();
                try {
                    //set the input to the query response
                    parser.setInput(new StringReader(response));
                    int eventType = parser.getEventType();
                    //while not at the end of XML document
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        //if the event is a start tag and is labelled "Rate"
                        if (eventType == XmlPullParser.START_TAG && parser.getName().equals("Rate")) {
                            //move to the next event and get the text (i.e. the conversion rate)
                            eventType = parser.next();
                            Log.d(TAG, parser.getText());
                            //parse string and store as type double
                            rate = Double.parseDouble(parser.getText());
                        } else {
                            //otherwise move to the next event
                            eventType = parser.next();
                        }
                    }
                    //exception handling
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //in the case of failure, show an error message
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Error, no data retrieved");
                Toast.makeText(CurrencyConverter.this, "Error, no data retrieved", Toast.LENGTH_LONG).show();
            }
        });
        Log.d(TAG, String.valueOf(rate));
        return rate;
    }


    public void calcAndDisplayConversion(){
        //do the calcs
        if (rate == 0.0){
            //if the rate is 0.0, prompt user to try again
            Toast.makeText(CurrencyConverter.this, "Rates delayed, try again!", Toast.LENGTH_LONG).show();
        }else{
            //parse user input as type DOuble and do conversion calc
            double userInput = Double.parseDouble(inputView .getText().toString());
            double conversionResult = userInput * rate;
            conversionResult = Math.round(conversionResult * 100.0)/100.0; //rounding for 2 decimal places
            //set text of output to result
            TextView resultView = (TextView)findViewById(R.id.conversion_output);
            resultView.setText(currIn+" "+userInput+" = "+currOut+" "+conversionResult);
        }
    }
}
