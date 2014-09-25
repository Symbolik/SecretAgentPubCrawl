package ca.mixitmedia.weaver.Tools;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.VideoView;

import ca.mixitmedia.weaver.R;

public class WeaverVideoFragment extends Fragment {


    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    Uri videoUri;
	VideoView videoHolder;
	public VideoView getVideoHolder() {
		return videoHolder;
	}
    ImageButton playBtn, resetBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video_player, null);

        playBtn = (ImageButton)v.findViewById(R.id.button_play_pause);
        resetBtn = (ImageButton)v.findViewById(R.id.button_reset);

        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
        videoHolder = (VideoView)v.findViewById(R.id.videoView);
        //videoHolder.setMediaController(new MediaController(getActivity()));

        setupVideo(pendingLocation);
        playBtn.setImageResource(android.R.drawable.ic_media_pause);
		//if you want the controls to appear
        videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playBtn.setImageResource(android.R.drawable.ic_media_play);
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (!playBtn.isEnabled()) return;
                if (videoHolder.isPlaying()) {
                    videoHolder.pause();
                    playBtn.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    videoHolder.start();
                    playBtn.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoHolder.seekTo(0);
                //playBtn.setEnabled(true);
            }
        });
        return v;
    }

    private WeaverLocation pendingLocation;
    private void setupVideo(WeaverLocation location){
        if (location != null) {
            setVideoUri(location.getVideo(getActivity()));
        }else{
            Uri videoUri = Uri.parse("android.resource://"+ getActivity().getPackageName()+"/"+R.raw.weaverguide_intro);
            setVideoUri(videoUri);
        }

        videoHolder.start();
    }
	public void playUri(WeaverLocation location) {
        if(Tools.Current() == this){
            setupVideo(location);
        }else {
            pendingLocation = location;
            Tools.swapTo(this);
        }

	}
}
