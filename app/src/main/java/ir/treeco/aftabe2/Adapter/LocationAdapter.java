package ir.treeco.aftabe2.Adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Rest.Utils.LocationHolder;

/**
 * Created by root on 5/5/16.
 */
public class LocationAdapter {

    public LocationAdapter(Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);

        if (location != null) {
            LocationHolder locationHolder = new LocationHolder(location.getLatitude(), location.getLongitude(), location.getAccuracy());

            AftabeAPIAdapter.updateLocation(locationHolder);

        }
    }
}
