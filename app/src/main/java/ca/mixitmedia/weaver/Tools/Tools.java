package ca.mixitmedia.weaver.Tools;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
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

import ca.mixitmedia.weaver.R;
import ca.mixitmedia.weaver.WeaverActivity;


/**
 * Created by Dante on 2014-07-27
 */
public class Tools {

    public static WeaverVideoFragment WeaverVideoFragment;
    public static WeaverMapFragment WeaverMapFragment;

    public static Fragment Compass;
    public static Fragment Camera;
    public static Fragment Badges;

    private static HashMap<Fragment, ImageView> toolButtons;
    private static WeaverActivity Main;

    private static View selector1;
    private static View selector2;

    public static void init(WeaverActivity Main) {
        Tools.Main  = Main;

        WeaverVideoFragment = new WeaverVideoFragment();
        WeaverMapFragment   = new WeaverMapFragment();
        Compass             = new TestFragment().setColor(Color.GREEN);
        Camera              = new TestFragment().setColor(Color.BLUE);
        Badges              = new TestFragment().setColor(Color.CYAN);

        toolButtons = new HashMap<>();
        toolButtons.put(WeaverVideoFragment,   (ImageView) Main.findViewById(R.id.Video  ));
        toolButtons.put(WeaverMapFragment, (ImageView) Main.findViewById(R.id.LocMap ));
        toolButtons.put(Compass, (ImageView) Main.findViewById(R.id.Compass));
        toolButtons.put(Camera,  (ImageView) Main.findViewById(R.id.Camera ));
        toolButtons.put(Badges,  (ImageView) Main.findViewById(R.id.Badges ));

        selector1 = Main.findViewById(R.id.selector1);
        selector2 = Main.findViewById(R.id.selector2);

        for(final Fragment s : toolButtons.keySet()){
	        /* Candidates:
	        ADD
			DARKEN
			DST_ATOP
			DST_OVER
			LIGHTEN
			MULTIPLY
			OVERLAY
			SCREEN
			SRC_ATOP
			SRC_IN
	         */
//            toolButtons.get(s).setColorFilter(Main.getResources().getColor(R.color.RyeYellow), PorterDuff.Mode.SRC_IN);
	        colorImageView(Main, toolButtons.get(s), R.color.RyeBlue);
            toolButtons.get(s).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swapTo(s);
                }
            });
        }
        swapTo(WeaverVideoFragment);
    }

	public static void colorImageView(Context context, ImageView imageView, @ColorRes int colorResId) {
		Drawable drawable = imageView.getDrawable();
		int color = context.getResources().getColor(colorResId);

		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);

		for (int x = 0; x < bitmap.getWidth(); x++) {
			for (int y = 0; y < bitmap.getHeight(); y++) {
				int pixel = bitmap.getPixel(x, y);
				int r = Color.red(pixel), g = Color.green(pixel), b = Color.blue(pixel);
				if (Color.alpha(pixel) > 140) bitmap.setPixel(x, y, Color.rgb(r, g, b));
			}
		}

		drawable = new BitmapDrawable(context.getResources(), bitmap);
		drawable.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[] {
				0, 0, 0, 0, Color.red(color),
				0, 0, 0, 0, Color.green(color),
				0, 0, 0, 0, Color.blue(color),
				0, 0, 0, 1, 255 - Color.alpha(color),
		})));

		imageView.setImageDrawable(drawable);
	}

    public static Iterable<Fragment> All() {
        return Arrays.asList(
		        WeaverVideoFragment,
		        WeaverMapFragment,
                Compass,
                Camera,
                Badges);
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
		if (toolButtons.keySet().contains(Tools.Current())) colorImageView(Main, toolButtons.get(Current()), R.color.RyeBlue);
	    colorImageView(Main, toolButtons.get(tool), R.color.RyeYellow);

        Main.getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, tool)
                .commit();
        refreshSelector(tool);
    }

    public static void refreshSelector(Fragment tool){
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
            case "video":   return WeaverVideoFragment;
            case "maptool": return WeaverMapFragment;
            case "compass": return Compass;
            case "camera":  return Camera;
            case "badges":  return Badges;

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
}