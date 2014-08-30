package ca.mixitmedia.weaver;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.util.LinkedHashMap;

import ca.mixitmedia.weaver.Tools.Tools;


public class MainActivity extends DrawerActivity {



    private LinkedHashMap<String, ImageView> toolButtons = new LinkedHashMap<String, ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tools.init(this);
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        Tools.refreshSelector(Tools.Current());
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
}
