package ca.mixitmedia.weaver;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

import ca.mixitmedia.weaver.Tools.Tools;

/**
 * Created by Dante on 2014-09-01.
 */
public class WeaverLocationManager implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    static final int GPS_SLOW_MIN_UPDATE_TIME_MS = 60000; //60 seconds
    static final int GPS_SLOW_MIN_UPDATE_DISTANCE_M = 50; //50 meters

    LocationManager locationManager;
    Context context;
    Location currentGPSLocation;
    //the minimal GPS update interval, in milliseconds
    int GPSMinUpdateTimeMS;
    // the minimal GPS update interval, in meters.
    int GPSMinUpdateDistanceM;

    boolean GPSStatus;

    public WeaverLocationManager(Context context) {
        this.context = context;
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
    }


    //Todo:Implement
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        setGPSStatus();
    }

    @Override
    public void onProviderEnabled(String provider) {
        setGPSStatus();
    }

    @Override
    public void onProviderDisabled(String provider) {
        setGPSStatus();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentGPSLocation = location;
        //context.experienceManager.UpdateLocation(location);
        if (Tools.Current() == Tools.locatorFragment) Tools.locatorFragment.onLocationChanged(location);
    }

    public void setGPSStatus() {
        if (Tools.Current() == Tools.locatorFragment) {
            if (GPSStatus && currentGPSLocation != null) {
                System.out.println("Stored location loaded");
                onLocationChanged(currentGPSLocation);
            }
        }
    }

    /**
     * reconfigures GPS updates to occur at the requested minimum time and distance intervals
     *
     * @param GPSMinUpdateTimeMS    the minimal GPS update interval, in milliseconds
     * @param GPSMinUpdateDistanceM the minimal GPS update interval, in meters.
     */
    public void setGPSUpdates(int GPSMinUpdateTimeMS, int GPSMinUpdateDistanceM) {
        if (GPSMinUpdateTimeMS < 0 || GPSMinUpdateDistanceM < 0) {
            throw new IllegalArgumentException("GPSMinUpdateTimeMS and GPSMinUpdateDistanceM  cannot be negative");
        }

        this.GPSMinUpdateTimeMS = GPSMinUpdateTimeMS;
        this.GPSMinUpdateDistanceM = GPSMinUpdateDistanceM;

        locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER,
                GPSMinUpdateTimeMS,
                GPSMinUpdateDistanceM, this);
    }

    /**
     * reconfigures GPS updates to occur at the minimum every 6s and 50 meters
     */
    public void requestSlowGPSUpdates() {
        setGPSUpdates(GPS_SLOW_MIN_UPDATE_TIME_MS, GPS_SLOW_MIN_UPDATE_DISTANCE_M);
    }

    /**
     * returns the most recent known location of the user.
     * @return the most recent known location of the user.
     */
    public Location getCurrentGPSLocation() {
        return currentGPSLocation;
    }

    public void removeUpdates() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        GPSStatus = true;
    }

    @Override
    public void onDisconnected() {
        GPSStatus = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        GPSStatus = false;
    }
}