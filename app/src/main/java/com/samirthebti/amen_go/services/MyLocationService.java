package com.samirthebti.amen_go.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.samirthebti.amen_go.utils.Preferences;


public class MyLocationService extends Service {
    public static final String TAG = MyLocationService.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private LocationManager locManager;
    private LocationListener locListener = new myLocationListener();
    static final Double EARTH_RADIUS = 6371.00;

    private boolean gps_enabled = false;
    private boolean network_enabled = false;

    private Handler handler = new Handler();
    Thread t;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_LONG).show();
        final Runnable r = new Runnable() {
            public void run() {
                Log.v("Debug", "Hello");
                location();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(r, 5000);
        return START_STICKY;
    }

    public void location() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        Log.v("Debug", "in on create.. 2");

        if (gps_enabled) {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
            Log.v("Debug", "Enabled..");
        }
        if (network_enabled) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
            Log.v("Debug", "Disabled..");
        }
        Log.v("Debug", "in on create..3");
    }

    private class myLocationListener implements LocationListener {
        double lat_old = 0.0;
        double lon_old = 0.0;
        double lat_new;
        double lon_new;
        Location newLocation;
        Location oldLocation;
        double time = 10;
        double speed = 0.0;

        @Override
        public void onLocationChanged(Location location) {
            Log.v("Debug", "in onLocation changed..");
            if (location != null) {
               // locManager.removeUpdates(locListener);
                //String Speed = "Device Speed: " +location.getSpeed();
                lat_new = location.getLongitude();
                lon_new = location.getLatitude();
                newLocation = new Location(location);

                String longitude = "Longitude: " + location.getLongitude();
                String latitude = "Latitude: " + location.getLatitude();
                if (oldLocation != null) {
                    double distance = oldLocation.distanceTo(newLocation);
                    Log.e("tag", "onLocationChanged: " + distance);
                    speed = distance / time;
                    Log.d("TAG ====>> : ", "onLocationChanged: " + speed + " M/S");
                    speed = (speed * 18) / 5;
                    try {
                        Preferences.writeSharedPreference(getBaseContext(), "speed", (float) speed);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getBaseContext(), longitude + "\n" + latitude + "\nDistance is: "
                            + distance + "\nSpeed is: " + speed, Toast.LENGTH_SHORT).show();
                    Log.e("speed", "onLocationChanged: " + longitude + "\n" + latitude + "\nDistance is: "
                            + distance + "\nSpeed is: " + speed);
                }
                lat_old = lat_new;
                lon_old = lon_new;
                oldLocation = new Location(location);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


}
