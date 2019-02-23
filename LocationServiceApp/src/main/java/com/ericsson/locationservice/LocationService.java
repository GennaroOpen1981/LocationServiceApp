package com.ericsson.locationservice;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class LocationService extends Service {

    public LocationManager locationManager;
    public Listener locationListener;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(this.getClass().getName(),"Location Service Started");


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //locationListener = new Listener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(this.getClass().getName(),"Permission not granted");
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, (float) 0.001, locationListener);


        return super.onStartCommand(intent, flags, startId);
    }

}
