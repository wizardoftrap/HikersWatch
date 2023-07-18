package com.shivprakash.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    private CompassSensorManager compassSensorManager;
    LocationListener locationListener;
    ImageView compassImageView;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening();

        }

    }

    public void startListening() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        }

    }

    public void updateLocationInfo(Location location) {
        Log.i("LocationInfo", location.toString());
        TextView latTextView = (TextView) findViewById(R.id.latTextView);
        TextView lonTextView = (TextView) findViewById(R.id.lonTextView);
        TextView altTextView = (TextView) findViewById(R.id.altTextView);
        TextView accTextView = (TextView) findViewById(R.id.accTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(currentDate);
        String formattedTime = timeFormat.format(currentDate);
        dateTextView.setText("Date: "+formattedDate);
        timeTextView.setText("Time: "+formattedTime);
        latTextView.setText("Latitude: " + location.getLatitude());
        lonTextView.setText("Longitude: " + location.getLongitude());
        altTextView.setText("Altitude: " + location.getAltitude());
        accTextView.setText("Accuracy: " + location.getAccuracy());
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            String address = "Could not find address";
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listAddresses != null && listAddresses.size() > 0 ) {
                Log.i("PlaceInfo", listAddresses.get(0).toString());
                address = "Address: "+listAddresses.get(0).getAddressLine(0);
            }
            TextView addressTextView = (TextView) findViewById(R.id.addressTextView);
            addressTextView.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassImageView = findViewById(R.id.compassImageView);
        compassSensorManager = new CompassSensorManager(MainActivity.this, compassImageView);
        compassSensorManager.setCompassImage(R.drawable.compass);
        compassSensorManager.start();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
            }
        };
        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    updateLocationInfo(location);
                }
            }
        }
    }
}
