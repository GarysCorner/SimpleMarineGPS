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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.security.Permission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    //declare vars
    private LocationManager locationmanager;
    private LocationListener locationlistener;
    private long minUpdateTime = 1000 * 60;  //min between location updates
    private long minDistance = 10;  //minimum distance between updates in meters
    private final static int requestGPScode = 1;  //return code for GPS permissions gran/deny

    private boolean locationOn =false;


    //widgets
    TextView text_lat, text_long, text_acc, text_last;

    //code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MainActivity", "Creating main activity!");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set textview widget variables
        text_lat = (TextView) findViewById(R.id.text_lat);
        text_long = (TextView) findViewById(R.id.text_long);
        text_acc = (TextView) findViewById(R.id.text_acc);
        text_last = (TextView) findViewById(R.id.text_last);

        //setup location manager and listener
        locationsetup();

        //request permissions or start manager depending
        if(requestLocationPermissions()) {
            startLocationManager();
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

    @Override
    public void onStop() {
        super.onPause();

        Log.w("MainActivity", "Main activity paused");

        stopLocationServices();


    }

    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
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
    public boolean checkLocationPermission() {
        return(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION));
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
                Log.d("LocationManager","Location updated" + location.toString());

                text_lat.setText(doubleToLat(location.getLatitude()));
                text_long.setText(doubleToLong(location.getLongitude()));
                text_acc.setText(doubleToAcc(location.getAccuracy()));
                text_last.setText(longToTime(location.getTime()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("LocationManager","LocationManager status change");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("LocationManager","LocationManager enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("LocationManager","LocationManager disabled");
            }
        };

    }

    private String doubleToLat(double lat) {

        return Double.toString(lat);
    }

    private String doubleToLong(double longitude) {
        return Double.toString(longitude);
    }

    private String doubleToAcc(double acc) {
        return Double.toString(acc);
    }

    private String longToTime(long timestamp) {
        Date date = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);

    }

    private boolean stopLocationServices() { //stop locaiton services

        Log.d("LocationManager", "Attempting to remove locaiton updates");
        try{
            locationmanager.removeUpdates(locationlistener);
        } catch (SecurityException e) {
            Log.w("LocationManager", "Removing location update failed!?!");
            return false;
        }

        locationOn = false;

        return true;
    }

    //star location services
    private void startLocationManager() {
        Log.d("LocationManager", "Attempting to start location manager");

        try{
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minUpdateTime, minDistance, locationlistener);
            locationOn = true;
        } catch (SecurityException e) {  //We should already havechecked permissions at this point but if something happens handle
            Log.d("LocationManager", "No permissions to start locationmanager unexpectidly");
            locationOn = false;
        }

    }
}

