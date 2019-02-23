package com.ericsson.locationservice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class LocationActivity extends AppCompatActivity {

    public LocationManager locationManager;
    public Listener locationListener;
    public Handler handler;

    private TextView txtView;
    private TextView txtViewLat;
    private TextView txtViewLong;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final  TextView txtLat = (TextView) findViewById(R.id.LatText);
        final  TextView txtLon = (TextView) findViewById(R.id.LongText);
        final  TextView txtVel = (TextView) findViewById(R.id.VelText);


        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Double lat = msg.getData().getDouble("latitude");
                Double lon = msg.getData().getDouble("longitude");
                Double vel = msg.getData().getDouble("velocity");

                txtLat.setText(getResources().getString(R.string.lat) +  String.format("%.6f", lat));
                txtLon.setText(getResources().getString(R.string.lon) +  String.format("%.6f", lon));
                txtVel.setText(getResources().getString(R.string.vel) +  String.format("%.6f", vel));

            }
        };

        Log.v(this.getClass().getName(),"Requesting permission");

        requestPermissions(INITIAL_PERMS,1111);

        Log.v(this.getClass().getName(),"Permission requested");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new Listener(handler);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
