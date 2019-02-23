package com.ericsson.locationservice;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class Listener implements LocationListener {

    public static final int PAUSE_BETWEEN_REQUEST = 1000;
    long lastRequest = 0;

    Thread connectThread = null;

    //public static final String DEVICE_URL = "https://api.thinger.io/v2/users/TLocTeam/devices/prova/coordinates_in";

    //public static final String DEVICE_URL = "http://192.168.43.239:80/putCoordinates";

    //public static final String COORDINATE_SERVICE= DEVICE_URL + "putCoordinates";

    public  Handler handler;

    public Listener(Handler handler) {

        this.handler = handler;
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat,lng;
        float velocity;

        Log.v(this.getClass().getName(),"Location changed");

        if(location != null){

            lat = location.getLatitude();
            lng = location.getLongitude();
            velocity = location.getSpeed();

            Log.v(this.getClass().getName(),"Latitude: "+lat);
            Log.v(this.getClass().getName(),"Longitude: "+lng);
            Log.v(this.getClass().getName(),"Velocity: "+velocity);

            Log.v(this.getClass().getName(),"Sending latitude: "+lat);
            Log.v(this.getClass().getName(),"Sending longitude: "+lng);
            Log.v(this.getClass().getName(),"Sending velocity: "+velocity);

            if (System.currentTimeMillis() - lastRequest > PAUSE_BETWEEN_REQUEST) {

                 lastRequest = System.currentTimeMillis();
            }

            Message message = new Message();
            Bundle data = new Bundle();
            data.putDouble("latitude",lat);
            data.putDouble("longitude",lng);
            data.putDouble("velocity",velocity);
            message.setData(data);
            handler.sendMessage(message);

        }
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


    public void putCoordinatesFromUrl(final String url, final double lat, final double lng)  {


        if (connectThread != null && connectThread.isAlive()) {
            Log.v(this.getClass().getName(),"Already pushing coordinate");
            return;
        }


        Thread connectThread = new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream in = null;
                HttpURLConnection httpURLConnection = null;
                // Making HTTP request
                try {

                    executeJSONRequest();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.v(this.getClass().getName(), "Error converting result " + e.toString());
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (httpURLConnection != null) {
                    Log.v(this.getClass().getName(),"Disconnecting");
                    httpURLConnection.disconnect();
                }



            }

            private void executeJSONRequest() throws IOException, JSONException {
                Log.v(this.getClass().getName(), "Sending " + url + " to web server ");
                URL urlIndicator = new URL(url);

                HttpURLConnection conn = (HttpURLConnection) urlIndicator.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXYiOiJwcm92YSIsImlhdCI6MTU0NDEwMzIwMCwianRpIjoiNWMwOTI1MjAzODUzYjE3ZWZjN2U3NzE0IiwidXNyIjoiVExvY1RlYW0ifQ.XLvfQYQEMgR0vkqKIZuAkNRlkVT_Cety6BJeYUe2odE");
                conn.setRequestProperty("Accept", "application/json, text/plain, */*");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonIn = new JSONObject();
                jsonIn.put("longitude", lng);
                jsonIn.put("latitude", lat);

                JSONObject jsonCoordinates = new JSONObject();
                jsonCoordinates.put("in", jsonIn);

                Log.i("JSON", jsonCoordinates.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonCoordinates.toString());

                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());

                conn.disconnect();
            }

        });

        connectThread.start();

    }

}
