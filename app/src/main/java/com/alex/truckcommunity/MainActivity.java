
package com.alex.truckcommunity;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private TextView speedText;
    private TextView gpsText;
    private TextView alertText;

    private LocationManager locationManager;

    private static final int GPS_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);

        createInterface();
    }

    private void createInterface() {

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setGravity(Gravity.CENTER);
        main.setPadding(25, 25, 25, 25);
        main.setBackgroundColor(Color.rgb(15, 20, 27));

        TextView title = new TextView(this);
        title.setText("TRUCK ALEX COMMUNITY");
        title.setTextSize(23);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("TRUCK RADAR GPS");
        subtitle.setTextSize(15);
        subtitle.setTextColor(Color.CYAN);
        subtitle.setGravity(Gravity.CENTER);

        speedText = new TextView(this);
        speedText.setText("0");
        speedText.setTextSize(85);
        speedText.setTextColor(Color.WHITE);
        speedText.setGravity(Gravity.CENTER);

        TextView kmh = new TextView(this);
        kmh.setText("km/h");
        kmh.setTextSize(20);
        kmh.setTextColor(Color.LTGRAY);
        kmh.setGravity(Gravity.CENTER);

        gpsText = new TextView(this);
        gpsText.setText("GPS: NEACTIV");
        gpsText.setTextColor(Color.YELLOW);
        gpsText.setTextSize(17);
        gpsText.setGravity(Gravity.CENTER);
        gpsText.setPadding(10, 25, 10, 25);

        alertText = new TextView(this);
        alertText.setText("RADAR: IN ASTEPTARE");
        alertText.setTextColor(Color.GREEN);
        alertText.setTextSize(18);
        alertText.setGravity(Gravity.CENTER);
        alertText.setPadding(10, 20, 10, 20);

        Button gpsButton = new Button(this);
        gpsButton.setText("ACTIVEAZA GPS");

        gpsButton.setOnClickListener(v -> requestGPS());

        TextView footer = new TextView(this);
        footer.setText("NAVIGATIE PENTRU CAMIOANE");
        footer.setTextColor(Color.GRAY);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(10, 35, 10, 10);

        main.addView(title);
        main.addView(subtitle);
        main.addView(speedText);
        main.addView(kmh);
        main.addView(gpsText);
        main.addView(alertText);
        main.addView(gpsButton);
        main.addView(footer);

        setContentView(main);
    }

    private void requestGPS() {

        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    GPS_PERMISSION
            );

        } else {
            startGPS();
        }
    }

    private void startGPS() {

        if (!locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER)) {

            gpsText.setText("ACTIVEAZA GPS DIN TELEFON");
            return;
        }

        try {

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    locationListener
            );

            gpsText.setText("GPS: CAUTARE SATELITI");

        } catch (SecurityException e) {
            gpsText.setText("GPS: ACCES REFUZAT");
        }
    }

    private final LocationListener locationListener =
            new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            double speed = location.hasSpeed()
                    ? location.getSpeed() * 3.6
                    : 0;

            speedText.setText(
                    String.valueOf(Math.round(speed))
            );

            gpsText.setText(
                    "GPS ACTIV\n" +
                    "Lat: " + location.getLatitude() +
                    "\nLon: " + location.getLongitude()
            );

            alertText.setText("RADAR: DATE IN CURAND");
        }
    };

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == GPS_PERMISSION) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startGPS();

            } else {

                gpsText.setText("GPS: PERMISIUNE REFUZATA");
                Toast.makeText(this,
                        "Permisiunea GPS este necesara",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
