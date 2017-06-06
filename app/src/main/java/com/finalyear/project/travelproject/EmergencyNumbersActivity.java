package com.finalyear.project.travelproject;

import android.*;
import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class EmergencyNumbersActivity extends BaseActivity {

    private static final String TAG = "EmergencyNos";
    private boolean contactPermissionGranted;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private String[] contactList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_numbers);
        //add toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //get the country name from an intent
        String countryName = getIntent().getStringExtra("countryName");
        //create the contacts list using the countryname
        contactList = createContactsList(countryName);

        //check for permissions
        //if permissions  already granted....
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            //set this to true
            contactPermissionGranted = true;
        } else {
            //else ask for permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        if (contactPermissionGranted) {
            //show confirmation dialogue
            showConfirmation();
        }
    }


    public String[] createContactsList(String countryName) {
        AppDatabase db = new AppDatabase(this);
        //get the contacts value associated with the country name
        Cursor c = db.getEmergencyContacts(countryName);
        String result = c.getString(c.getColumnIndex("contacts"));
        //split the result at each comma and store in an array
        String[] contacts = result.split(",");

        db.close();
        return contacts;
    }


    private void showConfirmation(){
        //new AlertDialogue builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title and message
        builder.setTitle("Confirm");
        builder.setMessage("Add emergency contacts?");
        //positive response functionality
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                addContacts(contactList);
                finish();
            }
        });
        //negative response functionality
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        //create the dialogue and show it
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void addContacts(String[] contactsList) {
//new arraylist of content provider operations
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactId = 1;
        //split each contact into name and number delimited by ":" and put into a 2 block array
        for (int i = 0; i < contactsList.length; i++) {
            String[] delimitedContact = contactsList[i].split(":");
            //first item is name, second is number
            String name = delimitedContact[0];
            String number = delimitedContact[1];

            //add a new contact
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    //set contact id
                    .withValue(Data.RAW_CONTACT_ID, rawContactId)
                    //set the content type
                    .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    //set phone number
                    .withValue(Phone.NUMBER, number)
                    //set the icon
                    .withValue(Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_PAGER)
                    //set the contact name
                    .withValue(Phone.LABEL, name)
                    //build the insert operation
                    .build());

            rawContactId++;

                try {
                    //try to get the appropriate content resolver and pass the information
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }

//for when user responds to a permissions request
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        //set contactPermissionGranted flag to false (just in case)
        contactPermissionGranted = false;
        switch (requestCode) {
            //if the request code is for reading contacts
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission is granted set contactPermissionGranted to true
                    contactPermissionGranted = true;
                }
            }
        }
    }
}
