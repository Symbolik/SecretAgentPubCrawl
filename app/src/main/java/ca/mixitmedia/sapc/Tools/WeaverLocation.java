package ca.mixitmedia.sapc.Tools;

import android.content.Context;
import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import ca.mixitmedia.sapc.R;

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
    public Uri getVideo(Context context){
        int imageResource = context.getResources().getIdentifier("raw/weaverguide_" + alias, null, context.getPackageName());
        if (imageResource == 0) imageResource = R.raw.weaverguide_encourage;
        return Uri.parse("android.resource://"+context.getPackageName()+"/"+imageResource);
    }
    boolean collected;
    public boolean isCollected() {return collected;}
    public void setCollected(boolean collected) {this.collected = collected;}

	public WeaverLocation(double latitude, double longitude, String title, String alias, boolean collected) {
		super("Weaver Provider");
		setLatitude(latitude);
		setLongitude(longitude);
        this.collected = collected;
		this.title = title;
		this.alias = alias;
	}

	public LatLng asLatLng() {
		return new LatLng(getLatitude(), getLongitude());
	}

}
