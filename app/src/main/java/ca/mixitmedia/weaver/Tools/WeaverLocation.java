package ca.mixitmedia.weaver.Tools;

import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Alexander on 14-09-07
 */
public class WeaverLocation extends Location {

	String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    String alias;

    boolean collected;
    public boolean isCollected() {return collected;}
    public void setCollected(boolean collected) {this.collected = collected;}

	public WeaverLocation(double latitude, double longitude, String title, String alias) {
		super("Weaver Provider");
		setLatitude(latitude);
		setLongitude(longitude);

		this.title = title;
		this.alias = alias;
	}

	public LatLng asLatLng() {
		return new LatLng(getLatitude(), getLongitude());
	}

}
