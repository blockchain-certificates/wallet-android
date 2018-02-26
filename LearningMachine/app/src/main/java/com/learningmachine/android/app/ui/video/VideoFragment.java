package com.learningmachine.android.app.ui.video;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentVideoBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.trello.rxlifecycle.android.FragmentEvent;


public class VideoFragment extends LMFragment {

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    private SimpleExoPlayer player;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        player.setPlayWhenReady(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        player.setPlayWhenReady(false);
    }

    @Override
    public void onStop() {
        super.onStop();

        player.release();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);


        /*
        savedInstanceState.putString("p", mPassphrase);
        savedInstanceState.putBoolean("onboardingSaveCheckmark", mBinding.onboardingSaveCheckmark.getVisibility() == View.VISIBLE);
        savedInstanceState.putBoolean("onboardingWriteCheckmark", mBinding.onboardingWriteCheckmark.getVisibility() == View.VISIBLE);
        savedInstanceState.putBoolean("onboardingEmailCheckmark", mBinding.onboardingEmailCheckmark.getVisibility() == View.VISIBLE);
        */

        player.release();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentVideoBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_video,
                container,
                false);


        Handler mainHandler = new Handler();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(null);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);


        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        binding.VideoView.setPlayer(player);


        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), "BlockCerts Wallet"), null);

        String filename = "rawresource:///" + R.raw.video;
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(filename));
        player.prepare(videoSource);

        player.setPlayWhenReady(true);


        //String fileName = "android.resource://" + getActivity().getPackageName() + "/raw/video";
        //binding.VideoView.setVideoURI(Uri.parse(fileName));
        //binding.VideoView.start();

        return binding.getRoot();
    }

}


