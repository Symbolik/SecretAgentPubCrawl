package ca.mixitmedia.weaver.Tools;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import ca.mixitmedia.weaver.R;
import ca.mixitmedia.weaver.WeaverActivity;

public class WeaverMapFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

	WeaverActivity Main;
	MapFragment mapFragment;
	GoogleMap map;
	HashMap<WeaverLocation, Marker> markers = new HashMap<>();

	@Override
	public void onResume() {
		super.onResume();
        if (getView()!=null)
		getView().post(new Runnable() {
            @Override
            public void run() {
                setUpMapIfNeeded();
                WeaverLocation loc = Main.weaverLocationManager.getDestination();
                simulateMarkerClick(loc);
            }
        });
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.fragment_map, container, false);

	    Main = (WeaverActivity) getActivity();
		if (mapFragment == null) {
			mapFragment = MapFragment.newInstance(new GoogleMapOptions()
					.compassEnabled(false)
					.rotateGesturesEnabled(false)
					.zoomControlsEnabled(false)
					.camera(CameraPosition.fromLatLngZoom(new LatLng(43.65863, -79.37928), 15.5f)));
		}
		getFragmentManager()
				 .beginTransaction()
				 .add(R.id.googleMapHolder, mapFragment)
				 .commit();
        MapsInitializer.initialize(Main.getApplicationContext());
        return view;
    }

	public void onDestroy() {
		if (mapFragment != null && !Main.isDestroyed()) {
			getFragmentManager()
					.beginTransaction()
					.remove(mapFragment)
					.commit();
		}
		super.onPause();
	}

	private void setUpMapIfNeeded() {
		map = mapFragment.getMap();

		refreshMapColors();
        map.setOnMarkerClickListener(this);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.65863, -79.37928), 15.5f));
		map.getUiSettings().setCompassEnabled(false);
		map.getUiSettings().setRotateGesturesEnabled(false);
		map.getUiSettings().setZoomControlsEnabled(false);
		map.setMyLocationEnabled(true);
	}

	public void refreshMapColors() {
		for (WeaverLocation l : Main.weaverLocationManager.locations.values()) {
			markers.put(l, map.addMarker(new MarkerOptions()
					.title(l.getTitle())
					.position(l.asLatLng())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue))));
		}
		WeaverLocation destination = Main.weaverLocationManager.getDestination();
        if (destination!= null)
            markers.get(destination).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue_yellow_center));
	}

	public void arrivedAtDestination() {
        refreshMapColors();
    }

	@Override
	public boolean onMarkerClick(Marker marker) {
        for(final WeaverLocation m : markers.keySet()){
            boolean collected = m.isCollected();
            markers.get(m).setIcon(
                    BitmapDescriptorFactory.fromResource(
                            collected ? R.drawable.pin_gray : R.drawable.pin_blue));

            if(markers.get(m).getId().equals(marker.getId())){
                View v = getView();
                if (v!=null){

                    String alias = m.alias;
                    String name =  m.title;

                    int imageResource = getResources().getIdentifier("drawable/badge_" + alias + ((collected)?"":"_shadow"), null, getActivity().getPackageName());
                    if (imageResource == 0) imageResource = (collected)?R.drawable.badge_default:R.drawable.badge_shadow;
                    ((ImageView)v.findViewById(R.id.map_selected_badge)).setImageResource(imageResource);

                    ((TextView)v.findViewById(R.id.map_selected_location)).setText(name);
                    CheckBox chx = ((CheckBox)v.findViewById(R.id.map_destination_checkbox));
                    chx.setChecked(Main.weaverLocationManager.getDestination() == m);
                    chx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(((CheckBox)view).isChecked()){
                                Main.weaverLocationManager.setDestination(m);
                            }else{
                                Main.weaverLocationManager.setDestination(null);// Murder occurred here.
                            }

                        }
                    });

                }
            }

        }
		marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue_yellow_center)); //for testing


		return false;
	}

    public void simulateMarkerClick(WeaverLocation loc) {
        if (loc!=null){
            Marker m = markers.get(loc);
            onMarkerClick(m);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(),
                    15.5f));
        }
    }
}
