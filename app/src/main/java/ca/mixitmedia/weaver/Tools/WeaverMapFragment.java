package ca.mixitmedia.weaver.Tools;
import android.app.Fragment;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

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
	HashMap<Integer, Marker> markers = new HashMap<>();
    WeaverActivity mainActivity;

	@Override
	public void onResume() {
		super.onResume();
        if (getView()!=null)
		getView().post(new Runnable() {
            @Override
            public void run() {
                setUpMapIfNeeded();
                Marker m = markers.get(mainActivity.destination <=0?1:mainActivity.destination);

                onMarkerClick(m);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(),
                        15.5f));
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
        mainActivity = ((WeaverActivity)getActivity());

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
        Cursor cursor = mainActivity.readBadges();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            addMarker(cursor.getInt(cursor.getColumnIndex(BadgeData.COLUMN_ID)),
                    cursor.getDouble(cursor.getColumnIndex(BadgeData.COLUMN_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(BadgeData.COLUMN_LONGITUDE)),
                    cursor.getLong(cursor.getColumnIndex(BadgeData.COLUMN_COLLECTED))!=0,
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

	private void addMarker(int id, double lat, double lang, boolean collected, String title) {
		markers.put(id, map.addMarker(new MarkerOptions()
				.title(title)
				.position(new LatLng(lat, lang))
				.icon(BitmapDescriptorFactory.fromResource(collected ?
						R.drawable.pin_gray : //if active
						R.drawable.pin_blue)))); //if !active
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
        Cursor cursor = mainActivity.readBadges();
        for(final Integer m : markers.keySet()){
            cursor.moveToPosition(m-1);
            boolean collected = cursor.getLong(cursor.getColumnIndex(BadgeData.COLUMN_COLLECTED))!=0;
            markers.get(m).setIcon(
                    BitmapDescriptorFactory.fromResource(
                            collected ? R.drawable.pin_gray : R.drawable.pin_blue));

            if(markers.get(m).getId().equals(marker.getId())){
                View v = getView();
                if (v!=null){
                    cursor.moveToPosition(m-1);
                    String alias = cursor.getString(cursor.getColumnIndex(BadgeData.COLUMN_ALIAS));
                    String name =  cursor.getString(cursor.getColumnIndex(BadgeData.COLUMN_NAME));

                    int imageResource = getResources().getIdentifier("drawable/badge_" + alias + ((collected)?"":"_shadow"), null, getActivity().getPackageName());
                    if (imageResource == 0) imageResource = (collected)?R.drawable.badge_default:R.drawable.badge_shadow;
                    ((ImageView)v.findViewById(R.id.map_selected_badge)).setImageResource(imageResource);

                    ((TextView)v.findViewById(R.id.map_selected_location)).setText(name);
                    CheckBox chx = ((CheckBox)v.findViewById(R.id.map_destination_checkbox));
                    chx.setChecked(mainActivity.destination == m);
                    chx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(((CheckBox)view).isChecked()){
                                mainActivity.destination = m;
                            }else{
                                mainActivity.destination = -1;
                            }

                        }
                    });

                }
            }

        }
		marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue_yellow_center)); //for testing


		return false;
	}

}
