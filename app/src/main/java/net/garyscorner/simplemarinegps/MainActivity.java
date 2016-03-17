package net.garyscorner.simplemarinegps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.Manifest;



public class MainActivity extends AppCompatActivity {

    //declare vars
    private LocationManager locationmanager;
    private LocationListener locationlistener;
    private long minUpdateTime = 0;  //min between location updates
    private long maxUpdateTime = 20;  //maximum time between location updates
    private final static int requestGPScode = 1;


    //code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w("MainActivity", "Creating main activity!");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setup location manager and listener
        locationsetup();

        //request permissions or start manager depending
        if(checkLocationPermission()) {
            startLocationManager();
        } else {
            requestLocationPermissions();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsintent = new Intent(this, Settings.class);
            startActivity(settingsintent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();  //call super first
        Log.w("MainActivity", "Resuming main activity");

        //if we have permissions go ahead and start GPS otherwise request
        if(checkLocationPermission()) {
            startLocationManager();
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case requestGPScode: {

                    if(grantResults.length >0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startLocationManager();
                    }
                    return;
            }


        }



    }


    //check for location manager permissions
    public boolean checkLocationPermission()
    {
        return(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION));
    }

    //start location manager
    private void startLocationManager() {
        Log.w("LocationManager", "Attempting to start location manager");

        try{
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minUpdateTime, maxUpdateTime, locationlistener);
        } catch (SecurityException e) {  //We should already havechecked permissions at this point but if something happens handle
            Log.d("LocationManager", "No permissions to start locationmanager unexpectidly");
        }

    }

    //request location permission if we dont have it
    private boolean requestLocationPermissions() {
        if(!checkLocationPermission()) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, requestGPScode);

            return false;  //return false if we dont have permissions but have requested
        } else {
            return true;  //return true if we already have permissions
        }
    }

    //setup locaiton manager and listener
    private void locationsetup() {

        locationmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationlistener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.w("LocationManager","Location updated" + location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.w("LocationManager","LocationManager status change");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.w("LocationManager","LocationManager enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.w("LocationManager","LocationManager disabled");
            }
        };

    }



}
