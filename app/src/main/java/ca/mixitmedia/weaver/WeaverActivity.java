package ca.mixitmedia.weaver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import ca.mixitmedia.weaver.Tools.Tools;
import ca.mixitmedia.weaver.views.BadgeData;


public class WeaverActivity extends DrawerActivity {

	boolean isDestroyed;
    public WeaverLocationManager weaverLocationManager;
    public BadgeData database;
//	public Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
//	    cursor = database.getReadableDatabase().rawQuery("SELECT * FROM "+ BadgeData.TABLE_BADGE, null);

	    isDestroyed = false;

        Tools.init(this);
        weaverLocationManager = new WeaverLocationManager(this);

	    deleteDatabase("weaver_tour"); //Todo: testing!
	    database = new BadgeData(this);

    }

	@Override
	protected void onNewIntent(Intent intent) {
//		if (intent.getAction() != null && intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
//			for (Uri uri : Utils.getNdefIntentURIs(intent)) {
//				experienceManager.UpdateLocation(uri);
//			}
//		}
	}

	@Override
	public void onResume() {
		super.onResume();
		weaverLocationManager.requestSlowGPSUpdates();
	}

	@Override
	public void onPause() {
		weaverLocationManager.removeUpdates();
		super.onDestroy();
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
