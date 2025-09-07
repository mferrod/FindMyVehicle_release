package dev.marianof.getlocated.Controller;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {
        Log.d("MyLocationListener", "Recopilando coordenadas");
        double longitude = loc.getLongitude();
        double latitude = loc.getLatitude();
        Log.d("MyLocationListener", "Coordenadas recogidas: \n LAT: " + latitude + "  - LON: " + longitude);
        MainController.getSingleton().setCoordinates(latitude, longitude);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
