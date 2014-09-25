package ca.mixitmedia.weaver;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class DrawerActivity extends Activity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        getNavigationButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationDrawerFragment.Toggle();
            }
        });
    }


    public static class NavigationDrawerFragment extends Fragment {

        private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
        private DrawerActivity activity;
        private DrawerLayout mDrawerLayout;
        private ListView mDrawerListView;
        private View mFragmentContainerView;
        private int mCurrentSelectedPosition = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            activity = (DrawerActivity)getActivity();
            if (savedInstanceState != null) {
                mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            }
            //selectItem(mCurrentSelectedPosition);
        }

        @Override
        public void onActivityCreated (Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View top = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
            top.findViewById(R.id.drawer_header).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {Toggle();
                }
            });
            mDrawerListView = (ListView)top.findViewById(R.id.drawer_list);
            mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {selectItem(position);}
            });
            mDrawerListView.setAdapter(activity.getDrawerListAdapter());
            mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
            return top;
        }

        public void setUp(int fragmentId, DrawerLayout drawerLayout) {
            mFragmentContainerView = getActivity().findViewById(fragmentId);
            mDrawerLayout = drawerLayout;
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            //mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            //    @Override public void onDrawerSlide(View view, float v) {/*No Op*/ }
            //    @Override public void onDrawerOpened(View view) {/*Todo: Add shadow to main View.*/}
            //    @Override public void onDrawerClosed(View view) {/*Todo: Remove shadow from main View.*/}
            //    @Override public void onDrawerStateChanged(int i) {/*No Op*/}
            //});
        }

        private void selectItem(int position) {
            mCurrentSelectedPosition = position;
            if (mDrawerListView != null) {
                mDrawerListView.setItemChecked(position, true);
            }
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
            }
            if (getActivity() != null) {
                ((DrawerActivity)getActivity()).onNavigationDrawerItemSelected(position);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        }

        public void Toggle() {
            if (mDrawerLayout.isDrawerOpen(getView())) mDrawerLayout.closeDrawer(getView());
            else mDrawerLayout.openDrawer(getView());
        }
    }

    protected abstract View getNavigationButton();

    protected abstract void onNavigationDrawerItemSelected(int position);

    protected abstract ListAdapter getDrawerListAdapter();

}
