package ca.mixitmedia.sapc.Tools;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;

import ca.mixitmedia.sapc.views.AbstractArchitectCamFragmentV4;
import ca.mixitmedia.sapc.views.WikitudeProvider;
import ca.mixitmedia.sapc.R;

/**
 * Created by Dante on 2014-09-02.
 */
public class WeaverCameraFragment extends Fragment {

    public WeaverCameraFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        return v;
    }

    @Override
    public void onDestroyView() {
        FragmentManager fm = getFragmentManager();
        Fragment xmlFragment = fm.findFragmentById(R.id.camera_preview);

        if (xmlFragment != null && !getActivity().isDestroyed()) {
            fm.beginTransaction().remove(xmlFragment).commit();
        }
        super.onDestroyView();
    }

    public static class SampleCamFragment extends AbstractArchitectCamFragmentV4 {

        private static final java.lang.String ARCHITECT_ACTIVITY_EXTRA_KEY_URL = "url2load";
        /**
         * last time the calibration toast was shown, this avoids too many toast shown when compass needs calibration
         */
        private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
        private int selectedGhost =3;
        private Vibrator v;
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            v = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        }

        @Override
        public String getARchitectWorldPath() {
            try {
                String url = "imager/barry/index.html";//"samples/ss/index.html"; //getResources().getAssets().list("samples")[0];
                Log.d("@@@@@@@@@@@@", url);
                return  url;
                //final String decodedUrl = URLDecoder.decode(getActivity().getIntent().getExtras().getString(ARCHITECT_ACTIVITY_EXTRA_KEY_URL), "UTF-8");
                //return decodedUrl;
            } catch (Exception e) {
                Toast.makeText(this.getActivity(), "Unexpected Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            this.architectView.callJavascript("World.setGhostMarker( " + selectedGhost + " );");
        }

        @Override
        public int getContentViewId() {
            return R.layout.architect_view;
        }

        @Override
        public int getArchitectViewId() {
            return R.id.architectView;
        }

        @Override
        public String getWikitudeSDKLicenseKey() {
            return getString(R.string.Wikitude_Key);
        }


        @Override
        public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener() {
            return new ArchitectView.SensorAccuracyChangeListener() {
                @Override
                public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
                    if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && getActivity() != null && !getActivity().isFinishing()  && System.currentTimeMillis() - SampleCamFragment.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
                        Toast.makeText( getActivity(), R.string.compass_accuracy_low, Toast.LENGTH_LONG ).show();
                    }
                }
            };
        }

        @Override
        public ArchitectView.ArchitectUrlListener getUrlListener() {
            return new ArchitectView.ArchitectUrlListener() {

                @Override
                public boolean urlWasInvoked(String uriString) {
                    Uri invokedUri = Uri.parse(uriString);
                    if ("button".equalsIgnoreCase(invokedUri.getHost())) {
                        if(invokedUri.getBooleanQueryParameter("visible", false)){
                            //((WeaverActivity)getActivity()).experienceManager.ToolSuccess(Tools.imager);
                            //SoundManager.playSound(SoundManager.Sounds.imagerSound);
                            //((WeaverActivity) getActivity()).swapTo(Tools.communicator);                   //Todo:Hack
                        }
                        else Toast.makeText(getActivity(),"~NO GHOST FOUND!~",Toast.LENGTH_LONG).show();
                    }else if("enter".equalsIgnoreCase(invokedUri.getHost())){
                        v.vibrate(new long[]{200,200},1);
                    }
                    else if("enter".equalsIgnoreCase(invokedUri.getHost())){
                        v.cancel();
                    }
                    return true;
                }
            };
        }

        @Override
        public void onPause() {
            super.onPause();
            v.cancel();
        }

        @Override
        public ILocationProvider getLocationProvider(final LocationListener locationListener) {
            return new WikitudeProvider(this.getActivity(), locationListener);
        }

    }
}
