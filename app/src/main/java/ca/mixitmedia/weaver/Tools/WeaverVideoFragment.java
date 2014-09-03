package ca.mixitmedia.weaver.Tools;
import android.app.Fragment;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import ca.mixitmedia.weaver.R;

public class WeaverVideoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video_player,null);
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
        VideoView videoHolder = (VideoView)v.findViewById(R.id.videoView);
//if you want the controls to appear
        videoHolder.setMediaController(new MediaController(getActivity()));
        Uri video = Uri.parse("android.resource://" + getActivity().getPackageName() + "/"
                + R.raw.welcome); //do not add any extension
        videoHolder.setVideoURI(video);
        videoHolder.start();
        return v;
    }
}
