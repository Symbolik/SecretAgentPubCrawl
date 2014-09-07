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
 * Created by Dante on 2014-09-01
 */
public class WeaverLocationManager implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    static final int GPS_SLOW_MIN_UPDATE_TIME_MS = 60000; //60 seconds
    static final int GPS_SLOW_MIN_UPDATE_DISTANCE_M = 50; //50 meters
	int GPSMinUpdateTimeMS; //the minimal GPS update interval, in milliseconds
	int GPSMinUpdateDistanceM; // the minimal GPS update interval, in meters.

    LocationManager locationManager;
    Context context;
    Location currentGPSLocation;

	ApproxDistance approxDistance;
	Location destination;
	float proximity;

    boolean GPSStatus;

    public WeaverLocationManager(Context context) {
        this.context = context;
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);


	    destination = new Location("dummyProvider");
	    destination.setLatitude(43.652202);
	    destination.setLongitude(-79.5814);
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

	    proximity = location.distanceTo(destination);

	    ApproxDistance currentDistance;
	    if (proximity >= 1000) {
		    currentDistance = ApproxDistance.FAR_FAR_AWAY;
		    if (approxDistance != currentDistance) setGPSUpdates(60000, 100); //60 seconds, 100 meters
	    } else if (proximity >= 250) {
		    currentDistance = ApproxDistance.FAR;
		    if (approxDistance != currentDistance) setGPSUpdates(30000, 25); //30 seconds, 25 meters
	    } else if (proximity >= 100) {
		    currentDistance = ApproxDistance.MEDIUM;
		    if (approxDistance != currentDistance) setGPSUpdates(10000, 10); //10 seconds, 10 meters
	    } else if (proximity >= 25) {
		    currentDistance = ApproxDistance.CLOSE;
		    if (approxDistance != currentDistance) setGPSUpdates(0, 0); //0 seconds, 0 meters
	    } else {
		    currentDistance = ApproxDistance.THERE;
		    if (approxDistance != currentDistance)  setGPSUpdates(0, 0); //0 seconds, 0 meters
	    }
	    approxDistance = currentDistance;

        if (Tools.Current() == Tools.locatorFragment) {
	        Tools.locatorFragment.onLocationChanged(location);
	        if (approxDistance == ApproxDistance.CLOSE) Tools.locatorFragment.arrivedAtDestination();
        }
	    else {

        }
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

	public Location getDestination() {
		return destination;
	}

	public float getProximity() {
		return proximity;
	}


	public void arrivedAtDestination() {

	}

	/**
	 * An enumeration that stores the frequency with which location updates should be received.
	 * Faster updates are necessary for accuracy at close proximity, but use significantly more
	 * battery energy, and heats up the phone.
	 */
	enum ApproxDistance {
		THERE, CLOSE, MEDIUM, FAR, FAR_FAR_AWAY
	}
}