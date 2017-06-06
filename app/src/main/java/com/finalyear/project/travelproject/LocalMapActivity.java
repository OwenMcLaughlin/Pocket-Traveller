package com.finalyear.project.travelproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//adapted from tutorial @: https://developers.google.com/places/android-api/current-place-tutorial

public class LocalMapActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
                                                                GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private static final String TAG = "Local Map";
    private final LatLng mDefaultLocation = new LatLng(54.9966, -7.3086);
    //letterkenny: 54.9524, -7.7209
    //derry: 54.9966 and -7.3086
    private final int DEFAULT_ZOOM = 17;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private int PLACE_PICKER_REQUEST;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if there is a recently found location, go there
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_local_map);
        //add the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //find button functionality
        Button findButton = (Button) findViewById(R.id.find_button);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findStuff();
            }
        });
        //create the GoogleApiClient to access google API services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                //accesses location services and Places APIs
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        //connect to google services
        mGoogleApiClient.connect();
    }

    //called when a map is prepared for display
    public void onMapReady(GoogleMap map) {
        //instantiate the map
        mMap = map;
        // Turn on the phone location button
        updateLocationUI();
        // Get the current location of the device and show map.
        getDeviceLocation();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //not implemented
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //on successful connection, get the map fragment to embed a map in the app interface
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //notifies the system when a map is ready (e.g. onMapReady() above)
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //not implemented
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            //if the map has been instantiated, save the camera position and location
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void getDeviceLocation() {
        //check for permissions, if true, continue
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else { //otherwise request permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //if permissions are granted use the fusedLocation API to retrieve the last known
        //location of the phone
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            //if the last known location has been successfully retrieved, create a LatLng object
            LatLng currentLL = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            //add a marker
            mMap.addMarker(new MarkerOptions().position(currentLL).title("YOU!"));
            //move the camera to device location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLL, DEFAULT_ZOOM));

        } else {
            //current location has not been found, go to default location
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    //same method as in emergency numbers activity
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
            mLocationPermissionGranted = false;
            switch (requestCode) {
                case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = true;
                    }
                }
        }
        //updates user location button on phone
        updateLocationUI();
    }

    public void updateLocationUI(){
        if (mMap == null) {
            //if there is no map yet initialized, break out of method
            return;
        }
        //checks for granted permissions
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }//otherwise requests permissions
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //if permissions are granted, enable location, otherwise disable it
        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //last known location cannot be found without permissions
            mLastKnownLocation = null;
        }

    }


    public void findStuff() {
        //this method shows the Google Places PlacePicker interface
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            //the placepicker activity returns a result
            startActivityForResult(builder.build(this), 1);
        }//error handling
        catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //when a result is returned from the placepicker
            if (resultCode == RESULT_OK) {
                System.out.println("result is ok");
                //if the result is ok, get the place and call showPlace() method
                Place place = PlacePicker.getPlace(data, this);
                showPlace(place);
            }
    }

    public void showPlace(Place p){
        if(p != null) {
        //if the place returned is not null, inflate the popup
            LayoutInflater layoutInflater = (LayoutInflater) LocalMapActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupLayout = layoutInflater.inflate(R.layout.place_popup, null);
            //set the popup parameters
            final PopupWindow popup = new PopupWindow(popupLayout, Toolbar.LayoutParams.MATCH_PARENT,
                    Toolbar.LayoutParams.WRAP_CONTENT, true);

            //get the stuff from the place
            final Uri website = p.getWebsiteUri();
            String name = (String) p.getName();
            String address = (String) p.getAddress();
            String tel = (String) p.getPhoneNumber();
            double rating = p.getRating();

            Log.d(TAG, String.valueOf(rating));
            Log.d(TAG, tel);
            Log.d(TAG,address );
            Log.d(TAG, name);
            //show the place name
            TextView placeName = (TextView) popupLayout.findViewById(R.id.place_name);
            placeName.setText(R.string.place_name + " " + name);
        //show the address
            TextView placeAddress = (TextView) popupLayout.findViewById(R.id.place_address);
            placeAddress.setText(R.string.place_address + " " + address);
            //show the telephone no.
            TextView placeTel = (TextView) popupLayout.findViewById(R.id.place_tel);
            placeTel.setText(R.string.place_telephone + " " + tel);

            TextView placeRating = (TextView) popupLayout.findViewById(R.id.place_rating);
            //if the rating is less than 0, no rating has been recorder.  set to "unknown"
            if (rating < 0){
                placeRating.setText(R.string.place_rating + " " + R.string.unknown_value);
            }
            //otherwise show the rating
            placeRating.setText(R.string.place_rating + " " + String.valueOf(rating));

            //direction button functionality (not implemented)
            Button directionsButton = (Button) popupLayout.findViewById(R.id.directions_button);
            directionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.dismiss();
                    //getDirections();
                }
            });

        //website button functionality
            Button websiteButton = (Button) popupLayout.findViewById(R.id.website_button);
            websiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.dismiss();
                    //opens the associated website in the phone browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, website);
                    startActivity(intent);
                }
            });

            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_language_choice);
            //show the popup
            popup.setBackgroundDrawable(new ColorDrawable(Color.CYAN));
            popup.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
        }
        else{
            //otherwise display an error message
            Toast.makeText(this,"data not available",Toast.LENGTH_SHORT).show();
        }
    }

}

