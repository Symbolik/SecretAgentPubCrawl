package ca.mixitmedia.weaver.Tools;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import ca.mixitmedia.weaver.R;
import ca.mixitmedia.weaver.WeaverActivity;
import ca.mixitmedia.weaver.views.BadgeData;

/**
 * Created by Dante on 2014-09-02.
 */
public class WeaverBadgeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WeaverActivity weaverActivity = (WeaverActivity)getActivity();
        View v = inflater.inflate(R.layout.fragment_badges,null);
        GridView g = (GridView)v.findViewById(R.id.gridView1);
        g.setAdapter(new Page1ListAdapter(weaverActivity));
        return v;
    }

    public class Page1ListAdapter extends BaseAdapter {

        private LayoutInflater adapterInflater;

        ImageView badge;

        public Page1ListAdapter(Context context) {
            super();
            adapterInflater=LayoutInflater.from(context);
        }
        private Cursor getCursor(){
            return ((WeaverActivity)getActivity()).readBadges();
        }
        @Override
        public int getCount() {
            int i = getCursor().getCount();
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            getCursor().moveToPosition(position);
            if (convertView == null) {
                badge = new ImageView(getActivity());
            } else {
                badge = (ImageView) convertView;
            }
            getCursor().moveToPosition(position);
            badge.setAdjustViewBounds(true);
            String alias = getCursor().getString(getCursor().getColumnIndex(BadgeData.COLUMN_ALIAS));
            final Long collected = getCursor().getLong(getCursor().getColumnIndex(BadgeData.COLUMN_ALIAS));
            int imageResource = getResources().getIdentifier("drawable/badge_" + alias + ((collected == 0)?"_shadow":""), null, getActivity().getPackageName());
            if (imageResource == 0) imageResource = (collected == 0)?R.drawable.badge_shadow:R.drawable.badge_default;
            Drawable image = getResources().getDrawable(imageResource);
            badge.setImageDrawable(image);
            //badge.setImageResource(R.drawable.badge_shadow);

            badge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getCursor().moveToPosition(position);
                    String name = getCursor().getString(getCursor().getColumnIndex(BadgeData.COLUMN_NAME));

                    if(collected ==0){
                        Toast.makeText(getActivity(),name,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    LayoutInflater inflater = (LayoutInflater)
                            getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    PopupWindow pw = new PopupWindow(
                            inflater.inflate(R.layout.widget_badge_popup, null, false),
                            100,
                            100,
                            true);
                    // The code below assumes that the root container has an id called 'main'
                    pw.showAtLocation(getActivity().findViewById(R.id.fragment_container), Gravity.CENTER, 0, 0);
                }
            });
            return badge;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            Log.i("mCursor.getCount() --->", String.valueOf(getCursor().getCount()));
            getCursor().moveToPosition(position);
            long id = getCursor().getLong(0); // <--- error here
            return id;
        }

    }
}
