package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.location.LocationManager.*;

public class MainActivity extends Activity {

    phpDown task;

    private final static String appKey = "9c8c143b1bb0efd33f5f41282b0b5075";


    TextView tv1;
    Button button1;
    TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        button1 = (Button) findViewById(R.id.button1);
        txtResult = (TextView) findViewById(R.id.txtResult);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                } else {
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String provider = location.getProvider();
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    double altitude = location.getAltitude();
                    txtResult.setText("???????????? : " + provider + "\n" + "?????? : " + longitude + "\n" + "?????? : " + latitude + "\n" + "?????? : " + altitude);
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
                    task = new phpDown();

                    task.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + appKey);
                }
            }
        });


        tv1 = findViewById(R.id.tem);




    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
            txtResult.setText("???????????? : " + provider + "\n" + "?????? : " + longitude + "\n" + "?????? : " + latitude + "\n" + "?????? : " + altitude);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    private class phpDown extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // ?????? url ??????
                URL url = new URL(urls[0]);
                // ????????? ?????? ??????
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // ??????????????????.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // ??????????????? ????????? ????????????.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // ????????? ???????????? ???????????? ??????????????? ?????? ??????.
                            String line = br.readLine();
                            if (line == null) break;
                            // ????????? ????????? ????????? jsonHtml??? ????????????
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }


        protected void onPostExecute(String str) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                //????????????
                int s = jsonObject.getJSONObject("main").getInt("temp") - 273;
                //????????????
                String a = jsonObject.getJSONObject("main").getString("humidity");
                //????????????
                String b = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                //??????
                String c = jsonObject.getString("name");
                tv1.setText(String.valueOf(s) + "  " + a + "   " + b + "   " + c);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}