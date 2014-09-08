package ca.mixitmedia.weaver.Tools;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import ca.mixitmedia.weaver.R;
import ca.mixitmedia.weaver.WeaverActivity;

/**
 * Created by Dante on 2014-08-31
 */
public class WeaverLocatorFragment extends Fragment implements SensorEventListener {

    TextView destinationProximityTextView;
    ImageView backgroundImageView;
    ImageView letterImageView;
    ImageView highlightImageView;
    ImageView centerImageView;
    ImageView arrowImageView;

    SensorManager sensorManager;
    Handler handler = new Handler();
    Runnable destinationReachedRunnable;
	boolean flashing;

    /**
     * heading: The angle between magnetic north and the front of the device
     * bearing: The angle between magnetic north and the destination.
     * relativeBearing: the angle between the heading and the bearing
     * proximity: Distance to the destination in meters
     */
    float heading;
    float bearing;
    float relativeBearing;

    private WeaverActivity Main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_locator, container, false);

        Main = (WeaverActivity) getActivity();

        sensorManager = (SensorManager) Main.getSystemService(Context.SENSOR_SERVICE);

        backgroundImageView = (ImageView) view.findViewById(R.id.locator_background);
	    letterImageView= (ImageView) view.findViewById(R.id.locator_letters);

	    destinationProximityTextView = (TextView) view.findViewById(R.id.locator_textview);
	    destinationProximityTextView.setTextColor(Main.getResources().getColor(R.color.RyeBlue));

	    centerImageView= (ImageView) view.findViewById(R.id.locator_center);
	    centerImageView.setColorFilter(Main.getResources().getColor(R.color.RyeYellow));

        highlightImageView= (ImageView) view.findViewById(R.id.locator_highlight);
        highlightImageView.setColorFilter(Main.getResources().getColor(R.color.RyeYellow));

        arrowImageView = (ImageView) view.findViewById(R.id.locator_arrow);
        arrowImageView.setColorFilter(Main.getResources().getColor(R.color.RyeBlue));


        destinationReachedRunnable = new Runnable()  {
	        boolean backgroundFlashingState;
	        int flashes;
            @Override
            public void run() {
                if (backgroundFlashingState) {
	                centerImageView.setColorFilter(Main.getResources().getColor(R.color.RyeYellow));
	                destinationProximityTextView.setTextColor(Main.getResources().getColor(R.color.RyeBlue));
	                backgroundFlashingState = false;
                }
                else {
	                centerImageView.setColorFilter(R.color.RyeBlue);
	                destinationProximityTextView.setTextColor(Main.getResources().getColor(R.color.RyeYellow));
	                backgroundFlashingState = true;
                }
	            if (flashes++ < 11) handler.postDelayed(destinationReachedRunnable, 250);
	            else if (flashes == 11) {
		            flashing = false;
	            }
            }
        };

        return view;
    }

    //Registers this fragment to resume receiving sensor data
    @Override
    public void onResume() {
        super.onResume();
        //register listener for the sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        Main.weaverLocationManager.setGPSUpdates(0, 0);
        Main.weaverLocationManager.setGPSStatus();
    }

    //Unregisters this fragment to pause receiving sensor data
    @Override
    public void onPause() {
        sensorManager.unregisterListener(this);    //unregister listener for sensors
        Main.weaverLocationManager.requestSlowGPSUpdates(); //slow down gps updates
        super.onPause();
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {} //stub

    //Reads the deprecated (argh!) orientation pseudo-sensor to get device heading
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // get the angle around the z-axis rotated
            updateHeading(event.values[0]);
        }
    }

    //updates heading of the device and rotates the arrow
    private void updateHeading(float newHeading) {
        newHeading = Math.round(newHeading);
        float newRelativeBearing = Math.round((newHeading - bearing + 360) % 360);

        // create a rotation animation (reverse turn newHeading degrees)
        rotateImage(arrowImageView,-relativeBearing,newRelativeBearing);
        rotateImage(highlightImageView,-relativeBearing,newRelativeBearing);
        rotateImage(backgroundImageView,heading,newHeading);
        rotateImage(letterImageView,heading,newHeading);
        heading = -newHeading;
        relativeBearing = newRelativeBearing;
    }

    private void rotateImage(ImageView image, float oldRelativeBearing, float newRelativeBearing) {
        RotateAnimation ra = new RotateAnimation(
		        oldRelativeBearing, newRelativeBearing,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);// set the animation after the end of the reservation status
        image.startAnimation(ra);
    }

    //called by the onLocationChanged of the parent MainActivity
    public void onLocationChanged(Location location) {
	    System.out.println("onLocationChanged");
        if (location == null || getView() == null) {
            Log.d("RF", "Locations shouldn't be null, you dun fucked up.");
            destinationProximityTextView.setText("GPS Unavailable");
            return;
        }

        bearing = (location.bearingTo(Main.weaverLocationManager.getDestination()) + 360) % 360;

        destinationProximityTextView.setText("Proximity: "+Math.round(Main.weaverLocationManager.getProximity())+" m");
    }

	public void arrivedAtDestination() {
		if (!flashing) {
			handler.postDelayed(destinationReachedRunnable, 250);
			flashing = true;
		}

//		Main.cursor.;
	}
}
