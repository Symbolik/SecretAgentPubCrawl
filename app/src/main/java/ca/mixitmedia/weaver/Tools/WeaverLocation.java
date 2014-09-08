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

	Uri video;
	public Uri getVideo() {
		return video;
	}
	public void setVideo(Uri video) {
		this.video = video;
	}

	public WeaverLocation(double latitude, double longitude, String title, Uri video) {
		super("Weaver Provider");
		setLatitude(latitude);
		setLongitude(longitude);

		this.title = title;
		this.video = video;
	}

	public LatLng asLatLng() {
		return new LatLng(getLatitude(), getLongitude());
	}

}
