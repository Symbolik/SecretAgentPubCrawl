package ca.mixitmedia.weaver;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import ca.mixitmedia.weaver.Tools.Tools;
import ca.mixitmedia.weaver.views.BadgeData;


public class WeaverActivity extends DrawerActivity {

	boolean isDestroyed;
    public WeaverLocationManager locationManager;
    public BadgeData database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        deleteDatabase("weaver_tour"); //Todo: testing!
        database = new BadgeData(this);

	    isDestroyed = false;

        setContentView(R.layout.activity_main);
        Tools.init(this);
        locationManager = new WeaverLocationManager(this);
    }

	@Override
	public void onDestroy() {
		isDestroyed = true;
		super.onDestroy();
	}

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) Tools.refreshSelector(Tools.Current());
    }

    @Override
    protected View getNavigationButton() {
        return findViewById(R.id.Menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    @Override
    protected ListAdapter getDrawerListAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                return null;
            }
        };
    }

	public boolean isDestroyed() {
		return isDestroyed;
	}
}
