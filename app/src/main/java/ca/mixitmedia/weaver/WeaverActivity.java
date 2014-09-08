package ca.mixitmedia.weaver;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Arrays;

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

//	    Uri video = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.weaverguide_intro); //do not add any extension
//	    Tools.videoFragment.playUri(video); TODO: fix intro video
    }

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getAction() == null) return;
		if (!intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))  return;

		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		ArrayList<Uri> ret = new ArrayList<>();
		if (rawMsgs == null) return;
		NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
		for (int i = 0; i < rawMsgs.length; i++) msgs[i] = (NdefMessage) rawMsgs[i];

		weaverLocationManager.UpdateLocation(msgs[0].getRecords()[0].toUri());
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




	//Test
	boolean debugging = true;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (debugging && ev.getPointerCount() >= 4) {
			debugging = false;
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_debug);
			dialog.setTitle("Settings");

			ArrayList<Button> LocationButtons = new ArrayList<>(Arrays.asList((Button) dialog.findViewById(R.id.location1),
					(Button) dialog.findViewById(R.id.location2),
					(Button) dialog.findViewById(R.id.location3),
					(Button) dialog.findViewById(R.id.location4)));
			LocationButtons.get(0).setText("Mattamy Centre");
			LocationButtons.get(1).setText("Image Arts Building");
			LocationButtons.get(2).setText("TRSM");
			LocationButtons.get(3).setText("Eng");
			// if button is clicked, close the custom dialog
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					debugging = true;
				}
			});
			dialog.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					debugging = true;
				}
			});

			View.OnClickListener clickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
						case R.id.location1:
							weaverLocationManager.onLocationChanged("mac");
							break;
						case R.id.location2:
							weaverLocationManager.onLocationChanged("ima");
							break;
						case R.id.location3:
							weaverLocationManager.onLocationChanged("trs");
							break;
						case R.id.location4:
							weaverLocationManager.onLocationChanged("eng");
							break;
					}
				}
			};
			for (Button b : LocationButtons) b.setOnClickListener(clickListener);

			dialog.show();
		}
		return super.dispatchTouchEvent(ev);
	}
}
