package com.Shankk.amd.garbageapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements LocationListener {

    Button getLocationBtn;
    TextView addressText;
    WebView locationMap;

    double latiM, longiM;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        getLocationBtn = (Button)findViewById(R.id.button);
        //locationText = (TextView)findViewById(R.id.longi);
        addressText = (TextView)findViewById(R.id.lati);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
            }
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {
        //locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

        String mapUrl = "https://www.google.com/maps/search/?api=1&query="+location.getLatitude()+","+location.getLongitude();
        getMap(mapUrl);

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            addressText.setText(addresses.get(0).getAddressLine(0) +"\n"+
                    addresses.get(0).getLocality() +"\n"+
                    addresses.get(0).getAdminArea() +"\n"+
                    addresses.get(0).getCountryName() +"\n"+
                    addresses.get(0).getPostalCode() +"\n"+
                    addresses.get(0).getFeatureName());
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(LocationActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    public void getMap(String murl) {
        locationMap = (WebView)findViewById(R.id.webView);
        locationMap.getSettings().setJavaScriptEnabled(true);
        locationMap.getSettings().setDomStorageEnabled(true);
        locationMap.setWebViewClient(new WebViewClient());
        locationMap.getSettings().setLoadWithOverviewMode(true);
        locationMap.getSettings().setUseWideViewPort(true);
        locationMap.loadUrl(murl);
    }
}
