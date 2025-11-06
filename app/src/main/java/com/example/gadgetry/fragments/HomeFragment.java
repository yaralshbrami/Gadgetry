package com.example.gadgetry.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.gadgetry.R;
import com.example.gadgetry.activities.LoginActivity;
import com.example.gadgetry.databinding.FragmentHomeBinding;
import com.example.gadgetry.utils.UtilKeys;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    VideoView videoView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        videoView=binding.video;

        String videoPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.video_file;
        videoView.setVideoPath(videoPath);

        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        binding.Logout.setOnClickListener(v -> {
            //Store Data in Shared Preferences
            SharedPreferences.Editor editor = UtilKeys.PREFS.edit();
            editor.putString(UtilKeys.TOKEN, "");
            editor.putString(UtilKeys.USER_KEY, "");
            editor.commit();

            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });
        return binding.getRoot();
    }
}