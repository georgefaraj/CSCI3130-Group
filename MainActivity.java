package com.example.group3.csci3130_group3_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    FirebaseAuth firebaseAuth;
    public DatabaseReference firebaseReference;
    public FirebaseDatabase firebaseDBInstance;
    //Button logout_bt;
   // TextView userName;
    String uid;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    public Location mCurrentLocation;
    private LatLngBounds.Builder mBounds = new LatLngBounds.Builder();

    //below is used for callbacks in permission checking
    private static final int REQUEST_FINE_LOCATION_ACCESS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SET UP


        super.onCreate(savedInstanceState);
        addNavBar();
        setActivityLayout(R.layout.activity_main);


        //x getWindow().getDecorView().setBackgroundColor(Color.parseColor("000000"));
        //CHECK LOGIN
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, CredentialActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
       // userName = findViewById(R.id.userEmail);
       // userName.setText(user.getEmail());
        uid = user.getUid();
        firebaseDBInstance = FirebaseDatabase.getInstance();

        //Connect to Google API client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();

        // Code that checks for enabled Location Services
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER,true);
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        updateLocation();



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//
//        UserProfile thisUser = dataSnapshot.getValue(UserProfile.class);
//        profileId = thisUser.getUid();
//        username.setText(thisUser.getUsername());
//        favoriteColor.setText(thisUser.getFavoriteColor());

        //SETTING CUSTOM BackGround color CODE


//        userListener
//        View thisView = findViewById(R.id.mainViewForBG);
//        View rootOfThisView = thisView.getRootView();
//        UserProfile thisUser = dataSnapshot.getValue(UserProfile.class);
//        if (thisUser != null) {
//            String colorString = thisUser.getFavoriteColor();
//            Log.d("Favorite COLOR:", colorString);
//            int color = Color.parseColor(String.valueOf(colorString));
//            rootOfThisView.setBackgroundColor(color);
//        }
///*****get the user's color and color the view*/
//        final String[] colorString = {"FFFFFF"}; //default
//        ChildEventListener userListener = new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                UserProfile thisUser = dataSnapshot.getValue(UserProfile.class);
//                if (thisUser != null) {
//                    colorString[0] = thisUser.getFavoriteColor();
//                    Log.d("Favorite COLOR:", colorString[0]);
//                }
//                View thisView = findViewById(R.id.mainViewForBG);
//                View rootOfThisView = thisView.getRootView();
//                int color = Color.parseColor(String.valueOf(colorString[0]));
//                rootOfThisView.setBackgroundColor(color);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////                View thisView = findViewById(R.id.mainViewForBG);
////                View rootOfThisView = thisView.getRootView();
////                UserProfile thisUser = dataSnapshot.getValue(UserProfile.class);
////                if (thisUser != null) {
////                    String colorString = thisUser.getFavoriteColor();
////                    Log.d("Favorite COLOR:", colorString);
////                    int color = Color.parseColor(String.valueOf(colorString));
////                    rootOfThisView.setBackgroundColor(color);
////                }
//                UserProfile thisUser = dataSnapshot.getValue(UserProfile.class);
//                if (thisUser != null) {
//                    colorString[0] = thisUser.getFavoriteColor();
//                    Log.d("Favorite COLOR:", colorString[0]);
//                }
//                View thisView = findViewById(R.id.mainViewForBG);
//                View rootOfThisView = thisView.getRootView();
//                int color = Color.parseColor(String.valueOf(colorString[0]));
//                rootOfThisView.setBackgroundColor(color);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        firebaseReference.child("users").child(user.getUid()).child("userprofile").addChildEventListener(userListener);
//        DataSnapshot dataSnapshot=null;
//        UserProfile thisUser = dataSnapshot.getValue(UserProfile.class);

       // String colorString = thisUser.getFavoriteColor();

    }

    @Override
    public void onStart() {
        super.onStart();
        updateLocation();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        //Need to explicitly check for permission before accessing location
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION_ACCESS);
        }
        mMap.setMyLocationEnabled(true);

        /* Add a marker in Sydney and move the camera
        This code was for learning purposes only.
        LatLng dal = new LatLng(44.6366, -63.5917);
        mMap.addMarker(new MarkerOptions().position(dal).title("Dalhousie!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dal));
        */
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                addPointToViewPort(ll);
                // we only want to grab the location once, to allow the user to pan and zoom freely.
                mMap.setOnMyLocationChangeListener(null);
            }
        });
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if(getIntent().hasExtra("Favorite")) {
                    Favorite sentFavorite = (Favorite) getIntent().getSerializableExtra("Favorite");
                    if (sentFavorite != null) {
                        String message = String.format("%f, %f; %s", sentFavorite.getmLatitude(), sentFavorite.getmLongitude(), sentFavorite.getName());
                        LatLng sentLocation = new LatLng(sentFavorite.getmLatitude(), sentFavorite.getmLongitude());
                        Log.d("Favorite coordinates:", message);
                        mMap.addMarker(new MarkerOptions().position(sentLocation));
                        addPointToViewPort(sentLocation);
                    }
                }
                if(getIntent().hasExtra("Address")){
                    String location = (String) getIntent().getSerializableExtra("Address");
                    performSearch(location);
                }
            }
        });



    }

    private void addPointToViewPort(LatLng newPoint) {
        mBounds.include(newPoint);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds.build(),
                findViewById(R.id.searchBar).getHeight()));
    }

    public void mapSearch(View view) {
        performSearch();
    }
    public void performSearch(){
        EditText locationSearch = (EditText) findViewById(R.id.searchBar);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null) {
            if(!location.isEmpty()) {

                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(addressList != null) {
                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("User Search"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } else {
                        LatLng sydney = new LatLng(-34, 151);
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                    }
                }
            }
        }
    }
    public void performSearch(String input){
        List<Address> addressList = null;
        String location = input;
        if (location != null) {
            if(!location.isEmpty()) {

                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(addressList != null) {
                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("User Search"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } else {
                        LatLng sydney = new LatLng(-34, 151);
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                    }
                }
            }
        }
    }


    //Sets the location on map to the one described in the global mCurrentLocation
  /*Commented out this method as GoogleMaps provides a UI element which does the same thing
    public void FIND(View view) {
        if (mCurrentLocation == null) {
            Display("Current Location NULL");
            return;
        }
        LatLng HERE = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(HERE).title("I AM HERE"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(HERE));
    }
    *****************************************************************************/

    /*
    * Update location will use the location manager to get the most recent GPS coordinates set in the device/emulator
    * The response is processed in MyLocationListenerGPS
    * */
    public void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          //  Display("Permission Error");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION_ACCESS);
            return;
        }
        mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new MyLocationListenerGPS(), null);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        AddFavoriteDialog addFavoriteDialog = new AddFavoriteDialog();
        addFavoriteDialog.setNewLocation(latLng);
        addFavoriteDialog.show(getSupportFragmentManager(), "Favorites");
    }

    /*
     * After updateLocation() has been called MyLocationListenerGPS handles the responses to the update
     * Once the location is received it updates the global mCurrentLocation via the location manager
      */

    public class MyLocationListenerGPS implements LocationListener {
        @SuppressLint("MissingPermission")
        @Override
        public void onLocationChanged(Location location) {
           // Display("Location Changed!");
            mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
         //   Display("Status change");
            mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        @Override
        public void onProviderEnabled(String provider) {
           // Display("provider change");
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Display("provider died");
        }
    }

    /*
    * If the user has not granted the app the required permissions this code handles the request and response
    * TODO: Perform some sort of error handling in case they say no and improve current error handling
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION_ACCESS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

  /*  public void Display(String e) {
        TextView st = (TextView) findViewById(R.id.statusText);
        st.setText(e);
    } */
}