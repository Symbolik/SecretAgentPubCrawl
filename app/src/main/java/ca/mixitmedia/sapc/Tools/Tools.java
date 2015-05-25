package ca.mixitmedia.sapc.Tools;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.HashMap;

import ca.mixitmedia.sapc.R;
import ca.mixitmedia.sapc.WeaverActivity;

/**
 * Created by Dante on 2014-07-27
 */
public class Tools {

    public static WeaverVideoFragment   videoFragment   ;
    public static WeaverMapFragment     mapFragment     ;
    public static WeaverLocatorFragment locatorFragment ;
    public static WeaverCameraFragment  cameraFragment  ;
    public static WeaverBadgeFragment   badgeFragment   ;
    public static SecretAgentMiniGame minigameFragment  ;
    public static MinigameConnectFragment minigameConnectFragment;


    private static HashMap<Fragment, ImageView> toolButtons;
    private static WeaverActivity Main;

    private static View selector1;
    private static View selector2;

    public static void init(WeaverActivity Main) {
        Tools.Main  = Main;

        videoFragment   = new WeaverVideoFragment();
        mapFragment     = new WeaverMapFragment();
        locatorFragment = new WeaverLocatorFragment();
        cameraFragment  = new WeaverCameraFragment();
        badgeFragment   = new WeaverBadgeFragment();
        minigameFragment = new SecretAgentMiniGame();
        minigameConnectFragment = new MinigameConnectFragment();




        toolButtons = new HashMap<>();
        toolButtons.put(videoFragment       ,(ImageView) Main.findViewById(R.id.Video  ));
        toolButtons.put(mapFragment         ,(ImageView) Main.findViewById(R.id.LocMap ));
        toolButtons.put(locatorFragment     ,(ImageView) Main.findViewById(R.id.Compass));

        toolButtons.put(minigameConnectFragment, (ImageView)Main.findViewById(R.id.Camera));
        //toolButtons.put(cameraFragment      ,(ImageView) Main.findViewById(R.id.Camera ));
        //toolButtons.put(minigameFragment      ,(ImageView) Main.findViewById(R.id.Camera ));

        toolButtons.put(badgeFragment       ,(ImageView) Main.findViewById(R.id.Badges ));

        selector1 = Main.findViewById(R.id.selector1);
        selector2 = Main.findViewById(R.id.selector2);

        for(final Fragment s : toolButtons.keySet()){
	        colorImageView(toolButtons.get(s), R.color.RyeBlue);
            toolButtons.get(s).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swapTo(s);
                }
            });
        }
        Tools.swapTo(Tools.videoFragment);
    }

    public static Iterable<Fragment> All() {
        return Arrays.asList(
                videoFragment,
                mapFragment,
                locatorFragment,
                minigameConnectFragment,
                //minigameFragment,
                //cameraFragment,
                badgeFragment   );
    }

    public static Fragment Current() {
        return Main.getFragmentManager().findFragmentById(R.id.fragment_container);
    }




    public static void swapTo(Fragment tool){
        if (Tools.Current() == tool) return;

//        if (toolButtons.keySet().contains(Tools.Current()))
//            toolButtons.get(Current()).setColorFilter(Main.getResources().getColor(R.color.RyeYellow));
//
//        toolButtons.get(tool).setColorFilter(Main.getResources().getColor(R.color.RyeBlue));
		if (toolButtons.keySet().contains(Tools.Current())) colorImageView(toolButtons.get(Current()), R.color.RyeBlue);
	    colorImageView(toolButtons.get(tool), R.color.RyeYellow);

        Main.getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, tool)
                .commit();
        refreshSelector(tool);
    }

    //swap fragments without toolbuttons stuff, used for minigame screens
    public static void directlySwapTo(Fragment tool){
        if (Tools.Current() == tool) return;

        //Main.getFragmentManager().beginTransaction().remove(Tools.Current()).add(R.id.fragment_container,tool).commit();

        Main.getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_container, tool)
                .commit();



    }

    public static void refreshSelector(Fragment tool){

        if(tool == minigameFragment)
            return;

        ImageView v = toolButtons.get(tool);
        int width = v.getWidth();
        float x = v.getX();
        if (width == 0 || x < 0.0001) return;
        selector1.animate().scaleX(width);
        selector2.animate().scaleX(width);
        selector1.animate().x(x + width/2);
        selector2.animate().x(x + width/2);
    }

    public static Fragment byName(String ToolName) {
        switch (ToolName.toLowerCase()) {
            case "videoFragment"    :return videoFragment   ;
            case "mapFragment"      :return mapFragment     ;
            case "locatorFragment"  :return locatorFragment ;
            case "cameraFragment"   :return minigameFragment  ;
            //case "cameraFragment"   :return minigameFragment  ;
            case "minigameConnectFragment" : return minigameConnectFragment;
            case "minigameFragment" : return minigameFragment;
           // case "cameraFragment"   :return cameraFragment  ;
            case "badgeFragment"    :return badgeFragment   ;

            default:
                Log.e("Tools", "Tried to get non-Existent Tool" + ToolName);
                return null;
        }
    }

    public static class TestFragment extends Fragment {
        int Color;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = new View(getActivity());
            v.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return v;
        }

        public TestFragment setColor(int Color){
            this.Color = Color;
            return this;
        }
    }

    public static void colorImageView(ImageView imageView, @ColorRes int colorResId) {
        Drawable drawable = imageView.getDrawable();
        int color = imageView.getContext().getResources().getColor(colorResId);

		/*You know those really nasty, gross, finicky pieces of code... The ones you've waged war with f
		for hours. Hours and hours of just tedious, fucking bullshit? Yeah, this is one of those. It's
		decent enough, and the time is over. It's done. Don't touch it. Just... don't. Hide it here,
		collapse the method... and never look at it ever... ever again. */
        //http://youtu.be/euI3v2jpTlI
        drawable.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                1.8f, 0,    0,    0, Color.red(color),
                0,    1.8f, 0,    0, Color.green(color),
                0,    0,    1.8f, 0, Color.blue(color),
                0,    0,    0,    2, 0,
        })));
    }
}
