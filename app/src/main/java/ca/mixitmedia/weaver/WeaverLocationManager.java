package ca.mixitmedia.weaver;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ca.mixitmedia.weaver.Tools.Tools;
import ca.mixitmedia.weaver.Tools.WeaverLocation;

/**
 * Created by Dante on 2014-09-01
 */
public class WeaverLocationManager implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    static final int GPS_SLOW_MIN_UPDATE_TIME_MS = 60000; //60 seconds
    static final int GPS_SLOW_MIN_UPDATE_DISTANCE_M = 50; //50 meters
	int GPSMinUpdateTimeMS; //the minimal GPS update interval, in milliseconds
	int GPSMinUpdateDistanceM; // the minimal GPS update interval, in meters.

	static final ArrayList<String> destinations = new ArrayList<>(Arrays.asList(
			"mac",
			"ima",
            "trs"
	));

    LocationManager locationManager;
	WeaverActivity Main;

    Location currentGPSLocation;

	public HashMap<String, WeaverLocation> locations = new HashMap<>();
	int destinationIndex;
	WeaverLocation destination;
	ApproxDistance approxDistance;
	float proximity;

    boolean GPSStatus;

    public WeaverLocationManager(WeaverActivity Main) {
        this.Main = Main;
        locationManager = (LocationManager) this.Main.getSystemService(Context.LOCATION_SERVICE);


//	    destination = new WeaverLocation(43.652202, -79.5814, "dummy destination");

	    addLocation(43.66184, -79.37991, "mac", "Mattamy Centre (formerly Maple Leaf Gardens)", R.raw.weaverguide_loc_mac);
	    addLocation(43.65782, -79.37928, "ima", "Image Arts Building (IMA)", R.raw.weaverguide_loc_img);
	    addLocation(43.65777, -79.38011, "lib", "Library Building (LIB)", 0);
	    addLocation(43.65770, -79.37980, "dev", "Devonian Pond (Lake Devo)", 0);
	    addLocation(43.65836, -79.37738, "rcc", "Rogers Communication Centre (RCC)", 0);
	    addLocation(43.65589, -79.38241, "trs", "Ted Rogers School of Management (TRSM)", R.raw.weaverguide_outro);
	    addLocation(43.65811, -79.37772, "eng", "Engineering and Architectural Sciences Building (ENG)", 0);
	    addLocation(43.65689, -79.37976, "ced", "Chang School of Continuing Education", 0);
	    addLocation(43.65863, -79.37928, "qua", "The Quad", 0);
	    addLocation(43.65646, -79.38047, "dmz", "Digital Media Zone (DMZ)", 0);
	    addLocation(43.65806, -79.37819, "scc", "Student Campus Centre", 0);

	    setDestination("mac");
    }

	public void addLocation(double lat, double lng, String id, String title, int videoRes) {
		if (videoRes == 0) videoRes = R.raw.weaverguide_intro;
		Uri videoUri = Uri.parse("android.resource://"+ Main.getPackageName()+"/"+videoRes);

		locations.put(id, new WeaverLocation(lat, lng, title, id));
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
        //Main.experienceManager.UpdateLocation(location);

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


	    if (approxDistance == ApproxDistance.THERE) {
		    arrivedAtDestination();
	    }

        if (Tools.Current() == Tools.locatorFragment) {
	        Tools.locatorFragment.onLocationChanged(location);
	        if (approxDistance == ApproxDistance.THERE) Tools.locatorFragment.arrivedAtDestination();
        }
	    if (Tools.Current() == Tools.mapFragment) {
		    Tools.mapFragment.arrivedAtDestination();
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

	public WeaverLocation getDestination() {
		return locations.get(destinations.get(destinationIndex));
	}

	public void setDestination(String title) {
		destinationIndex = destinations.indexOf(title);
		if (destinationIndex == -1) {
			Log.e("setDestination", "invalid destination");
			destinationIndex = 0;
		}
		destination = getDestination();
	}

	public float getProximity() {
		return proximity;
	}


	public void arrivedAtDestination() {
		Tools.videoFragment.playUri(getDestination().getVideo());

		if (destinationIndex + 1 >= destinations.size()) destinationIndex = 0;
		else destinationIndex++;
		destination = getDestination();

	}

	public void UpdateLocation(Uri uri) {
		if (uri != null && uri.getScheme().equals("weaver") && uri.getHost().equals("ghostcatcher.mixitmedia.ca")) {
			String[] tokens = uri.getLastPathSegment().split("\\.");
			String type = tokens[1];
			String id = tokens[0];
			if (type.equals("location")) onLocationChanged(id);
			else
				Toast.makeText(Main, "Location: " + id + " was not found", Toast.LENGTH_LONG).show();
		} else Toast.makeText(Main, "Invalid Location URL", Toast.LENGTH_LONG).show();
	}

	public void onLocationChanged(String title) {
		onLocationChanged(locations.get(title));
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