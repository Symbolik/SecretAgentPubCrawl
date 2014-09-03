package ca.mixitmedia.weaver.Tools;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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
import ca.mixitmedia.weaver.views.BadgeData;

public class WeaverMapFragment extends Fragment implements OnMarkerClickListener {

	MapFragment mapFragment;
	GoogleMap map;
	HashMap<String, Marker> markers = new HashMap<>();
    Cursor cursor;

	@Override
	public void onResume() {
		super.onResume();
		getView().post(new Runnable() {
            @Override
            public void run() {
                setUpMapIfNeeded();
            }
        });
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.fragment_map, container, false);

		 if (mapFragment == null) {
			 mapFragment = MapFragment.newInstance(new GoogleMapOptions()
					 .compassEnabled(false)
					 .rotateGesturesEnabled(false)
					 .zoomControlsEnabled(false)
					 .camera(CameraPosition.fromLatLngZoom(new LatLng(43.65863, -79.37928), 15.5f)));
		 }
        cursor = ((WeaverActivity)getActivity()).database.getReadableDatabase().rawQuery("SELECT * FROM "+ BadgeData.TABLE_BADGE, null);

		getFragmentManager()
				 .beginTransaction()
				 .add(R.id.googleMapHolder, mapFragment)
				 .commit();
        MapsInitializer.initialize(getActivity().getApplicationContext());
        return view;
    }

	public void onDestroy() {
		if (mapFragment != null && !getActivity().isDestroyed()) {
			getFragmentManager()
					.beginTransaction()
					.remove(mapFragment)
					.commit();
		}
		super.onPause();
	}

	private void setUpMapIfNeeded() {
		map = mapFragment.getMap();
			addMarkers();
			map.setOnMarkerClickListener(this);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.65863, -79.37928), 15.5f));
			map.getUiSettings().setCompassEnabled(false);
			map.getUiSettings().setRotateGesturesEnabled(false);
			map.getUiSettings().setZoomControlsEnabled(false);
			//map.setMyLocationEnabled(true);
	}

	private void addMarkers() {

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            addMarker(cursor.getDouble(cursor.getColumnIndex(BadgeData.COLUMN_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(BadgeData.COLUMN_LONGITUDE)),
                    cursor.getLong(cursor.getColumnIndex(BadgeData.COLUMN_COLLECTED))==0,
                    cursor.getString(cursor.getColumnIndex(BadgeData.COLUMN_NAME))
                    );
        }
		//addMarker(43.66184, -79.37991, true, "Mattamy Centre (formerly Maple Leaf Gardens)");
		//addMarker(43.65782, -79.37928, true, "Image Arts Building (IMA)");
		//addMarker(43.65777, -79.38011, true, "Library Building (LIB)");
		//addMarker(43.65770, -79.37980, true, "Devonian Pond (Lake Devo)");
		//addMarker(43.65836, -79.37738, true, "Rogers Communication Centre (RCC)");
		//addMarker(43.65589, -79.38241, true, "Ted Rogers School of Management (TRSM)");
		//addMarker(43.65811, -79.37772, true, "Engineering and Architectural Sciences Building (ENG)");
		//addMarker(43.65689, -79.37976, true, "Chang School of Continuing Education");
		//addMarker(43.65863, -79.37928, true, "The Quad");
		//addMarker(43.65646, -79.38047, true, "Digital Media Zone (DMZ)");
		//addMarker(43.65806, -79.37819, true, "Student Campus Centre");
	}

	private void addMarker(double lat, double lang, boolean active, String title) {
		markers.put(title, map.addMarker(new MarkerOptions()
				.title(title)
				.position(new LatLng(lat, lang))
				.icon(BitmapDescriptorFactory.fromResource(active ?
						R.drawable.pin_blue_yellow_center : //if active
						R.drawable.pin_blue)))); //if !active
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
        for(Marker m : markers.values()){
            m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue_yellow_center));
        }
		marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue)); //for testing
		return false;
	}

}
