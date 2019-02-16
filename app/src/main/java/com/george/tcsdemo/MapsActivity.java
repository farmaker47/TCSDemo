package com.george.tcsdemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 111;
    Marker marker;
    LocationListener locationListener;
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    private EditText mMessageEditText;
    private String latitudeToPass, longtitudeToPass, addressToPass;
    public static final String LATITUDE_FROM_MAPS = "latitude_from_maps";
    public static final String LONGTITUDE_FROM_MAPS = "longtitude_from_maps";
    public static final String ADDRESS_TO_PASS = "address_to_pass";
    private Button prevButton;
    private ProgressBar progressBar;
    private static final String PROGRESSBAR_STATE = "progressbar_state";
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        progressBar = findViewById(R.id.progressBarMaps);
        if (savedInstanceState != null) {
            switch (savedInstanceState.getInt(PROGRESSBAR_STATE)) {
                case 1:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    progressBar.setVisibility(View.INVISIBLE);
                    break;
            }
        }
        view = findViewById(R.id.map);

        //set click listener to button
        prevButton = findViewById(R.id.buttonMaps);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if there is internet connection
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                // If there is a network connection ask for location
                if (networkInfo != null && networkInfo.isConnected()) {
                    showDialogForAutomaticLocation();
                    //set progress bar to invisible
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(MapsActivity.this, getString(R.string.connectToInternet), Toast.LENGTH_LONG).show();
                }

            }
        });

        //stop reloading map at rotation
        mapFragment.setRetainInstance(true);

    }

    //Method to ask the user if he wants to find his location automatically or manually
    private void showDialogForAutomaticLocation() {
        android.app.AlertDialog.Builder downloadDialog = new android.app.AlertDialog.Builder(MapsActivity.this);
        downloadDialog.setTitle(R.string.automaticLocation);
        downloadDialog.setMessage(R.string.automaticLocationMessage);
        downloadDialog.setPositiveButton(R.string.epilogiYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                //Check for permissions
                marshmallowGPSPremissionCheck();

            }
        });
        downloadDialog.setNegativeButton(R.string.epilogiNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do some actions if user wants to manually insert addres
                manualInputOfAddress();
            }
        });
        downloadDialog.show();
    }

    private void locationManagerInit() {

        //create listener for changes in location
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //get the location name from latitude and longitude
                latitudeToPass = String.valueOf(latitude);
                longtitudeToPass = String.valueOf(longitude);
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addresses =
                            geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        String result = addresses.get(0).getLocality() + ":";
                        result += addresses.get(0).getAdminArea();
                        addressToPass = result;
                        LatLng latLng = new LatLng(latitude, longitude);
                        if (marker != null) {
                            marker.remove();
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                            mMap.setMaxZoomPreference(20);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));

                            //Remove updates so you can click the marker
                            locationManager.removeUpdates(locationListener);
                            //Set listener to marker
                            clickMarker();
                            //Set Progressbar invisible
                            progressBar.setVisibility(View.INVISIBLE);
                            //Show Snackbar to info to user that he/she has to click on its balloon
                            Snackbar.make(view, R.string.editTextMessageInfo, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        } else {
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                            mMap.setMaxZoomPreference(20);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21.0f));
                            //Remove updates so you can click the marker
                            locationManager.removeUpdates(locationListener);
                            //Set listener to marker
                            clickMarker();
                            //Set Progressbar invisible
                            progressBar.setVisibility(View.INVISIBLE);
                            //Show Snackbar to info to user that he/she has to click on its balloon
                            Snackbar.make(view, R.string.editTextMessageInfo, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //enable updates
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (progressBar.getVisibility() == View.VISIBLE) {
            outState.putInt(PROGRESSBAR_STATE, 1);
        } else {
            outState.putInt(PROGRESSBAR_STATE, 2);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void promptToEnableGps() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Start action to find location
            locationManagerInit();
            //Set Progressbar visible
            progressBar.setVisibility(View.VISIBLE);

        }

        if (!hasGPSDevice(this)) {
            Toast.makeText(this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            //Do some action to enable GPS
            enableLoc();
        }
    }

    //Method to check if there is GPS
    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    //method to enable GPS and start activity for result
    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MapsActivity.this, REQUEST_LOCATION);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                //When GPS is enabled do some action to find location
                locationManagerInit();
                //Set Progressbar visible
                progressBar.setVisibility(View.VISIBLE);

                break;

        }

    }

    //Method to manually input location
    private void manualInputOfAddress() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.titleInsertAddress);
        builder.setMessage(R.string.editTextMessageInfo);
        mMessageEditText = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mMessageEditText.setLayoutParams(lp);
        //set id to edittext to use itin tests
        mMessageEditText.setId(444);
        builder.setView(mMessageEditText);

        builder.setPositiveButton(getResources().getString(R.string.userOK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Set ProgressBar Visible
                progressBar.setVisibility(View.VISIBLE);
                //Checking first if user has inserted an address
                if (!TextUtils.isEmpty(mMessageEditText.getText().toString())) {

                    ///////////////////////////////////////////////////////////////////////////////////////////////////
                    //Because geocode.getLocationFromName didn't work I decided to use reverse geocode api
                    //I used Volley library to fetch location from address and then populated map the usual way as with automatic location
                    ///////////////////////////////////////////////////////////////////////////////////////////////////
                    final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
                    final String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                            mMessageEditText.getText().toString() + "&key=AIzaSyBLhPvq05FY85mbfARoeh6id2FV8IILCxs";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {

                            JSONObject jsonObject = (JSONObject) response;
                            //used this values if user types something unusual
                            Double lat = 0.0, lng = 0.0;
                            try {

                                lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                        .getJSONObject("geometry").getJSONObject("location")
                                        .getDouble("lng");

                                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                        .getJSONObject("geometry").getJSONObject("location")
                                        .getDouble("lat");

                                latitudeToPass = String.valueOf(lat);
                                longtitudeToPass = String.valueOf(lng);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            try {
                                //Check if user has inserted wrong address
                                if (lat == 0.0 && lng == 0.0) {
                                    Toast.makeText(MapsActivity.this, getString(R.string.noLocationFound), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return;
                                }
                                List<Address> addresses =
                                        geocoder.getFromLocation(lat, lng, 1);
                                if (addresses != null && addresses.size() > 0) {
                                    String result = addresses.get(0).getLocality() + ":";
                                    result += addresses.get(0).getAdminArea();
                                    addressToPass = result;
                                    LatLng latLng = new LatLng(lat, lng);
                                    if (marker != null) {
                                        marker.remove();
                                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                                        mMap.setMaxZoomPreference(10);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));

                                        //Set listener to marker
                                        clickMarker();
                                        //Set Progressbar invisible
                                        progressBar.setVisibility(View.INVISIBLE);
                                        //Show Snackbar to info to user that he/she has to click on its balloon
                                        Snackbar.make(view, R.string.editTextMessageInfo, Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    } else {
                                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                                        mMap.setMaxZoomPreference(10);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));

                                        //Set listener to marker
                                        clickMarker();
                                        //Set Progressbar invisible
                                        progressBar.setVisibility(View.INVISIBLE);
                                        //Show Snackbar to info to user that he/she has to click on its balloon
                                        Snackbar.make(view, R.string.editTextMessageInfo, Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MapsActivity.this, getString(R.string.tryAgain), Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);

                } else {
                    //Prompt user to insert address
                    Toast.makeText(MapsActivity.this, getString(R.string.insertAddress), Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void clickMarker() {
        //Add marker click listener
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, WeatherActivity.class);
                intent.putExtra(LATITUDE_FROM_MAPS, latitudeToPass);
                intent.putExtra(LONGTITUDE_FROM_MAPS, longtitudeToPass);
                intent.putExtra(ADDRESS_TO_PASS, addressToPass);
                Log.d("Values", latitudeToPass + " " + longtitudeToPass);
                startActivity(intent);


            }
        });
    }

    private void marshmallowGPSPremissionCheck() {
        //from Marshmallow we have to check for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && this.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            //below M we prompt to enable GPS
            promptToEnableGps();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
            promptToEnableGps();

        }
    }

}
