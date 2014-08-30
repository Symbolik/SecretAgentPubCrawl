package ca.mixitmedia.weaver.Tools;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.HashMap;

import ca.mixitmedia.weaver.MainActivity;
import ca.mixitmedia.weaver.R;


/**
 * Created by Dante on 2014-07-27
 */
public class Tools {


    public static VideoPlayer Video   ;
    public static LocMap LocMap  ;
    public static Fragment Compass ;
    public static Fragment Camera  ;
    public static Fragment Badges  ;

    private static HashMap<Fragment, ImageView> toolButtons;
    private static MainActivity Main;

    private static View selector1;
    private static View selector2;

    public static void init(MainActivity Main) {
        Tools.Main = Main;
        Video      = new VideoPlayer();
        LocMap     = new LocMap();
        Compass    = new TestFragment().setColor(Color.GREEN);
        Camera     = new TestFragment().setColor(Color.BLUE);
        Badges     = new TestFragment().setColor(Color.CYAN);

        toolButtons = new HashMap<>();
        toolButtons.put(Video   ,(ImageView)Main.findViewById(R.id.Video    ));
        toolButtons.put(LocMap  ,(ImageView)Main.findViewById(R.id.LocMap   ));
        toolButtons.put(Compass ,(ImageView)Main.findViewById(R.id.Compass  ));
        toolButtons.put(Camera  ,(ImageView)Main.findViewById(R.id.Camera   ));
        toolButtons.put(Badges  ,(ImageView)Main.findViewById(R.id.Badges   ));

        selector1 = Main.findViewById(R.id.selector1);
        selector2 = Main.findViewById(R.id.selector2);

        for(final Fragment s : toolButtons.keySet()){
            toolButtons.get(s).setColorFilter(Main.getResources().getColor(R.color.RyeYellow), PorterDuff.Mode.MULTIPLY);
            toolButtons.get(s).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swapTo(s);
                }
            });
        }
        swapTo(Video);
    }

    public static Iterable<Fragment> All() {
        return Arrays.asList(
                Video       ,
                LocMap      ,
                Compass     ,
                Camera      ,
                Badges      );
    }

    public static Fragment Current() {
        return Main.getFragmentManager().findFragmentById(R.id.fragment_container);
    }

    public static void swapTo(Fragment tool){
        if (Tools.Current() == tool) return;

        if (toolButtons.keySet().contains(Tools.Current()))
            toolButtons.get(Current()).setColorFilter(Main.getResources().getColor(R.color.RyeYellow));

        ImageView v = toolButtons.get(tool);
        v.setColorFilter(Main.getResources().getColor(R.color.RyeBlue));

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
            case "video"    :return Video      ;
            case "locmap"   :return LocMap     ;
            case "compass"  :return Compass    ;
            case "camera"   :return Camera     ;
            case "badges"   :return Badges     ;

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
