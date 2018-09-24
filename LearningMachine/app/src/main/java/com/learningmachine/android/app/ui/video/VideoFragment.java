package com.learningmachine.android.app.ui.video;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentVideoBinding;
import com.learningmachine.android.app.ui.LMFragment;


public class VideoFragment extends LMFragment {

    public static final String RESUME_POSITION_KEY = "com.learningmachine.android.app.ui.video.resumePosition";
    public static final String IS_PAUSED_KEY = "com.learningmachine.android.app.ui.video.isPaused";
    private boolean mIsPaused;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    private FragmentVideoBinding mBinding;
    private SimpleExoPlayer player;
    private static long pauseSavedPosition = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (args != null) {
            mIsPaused = args.getBoolean(IS_PAUSED_KEY);
            initPlayer(args.getLong(RESUME_POSITION_KEY));
        } else {
            initPlayer(pauseSavedPosition);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(RESUME_POSITION_KEY, player.getCurrentPosition());
            bundle.putBoolean(IS_PAUSED_KEY, mIsPaused);
            setArguments(bundle);
            pauseSavedPosition = player.getCurrentPosition();
        }
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(RESUME_POSITION_KEY, player.getCurrentPosition());
            bundle.putBoolean(IS_PAUSED_KEY, mIsPaused);
            setArguments(bundle);
            pauseSavedPosition = player.getCurrentPosition();
        }
        releasePlayer();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (player != null) {
            savedInstanceState.putLong(RESUME_POSITION_KEY, player.getCurrentPosition());
        }
        releasePlayer();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_video,
                container,
                false);

        if (savedInstanceState != null) {
            pauseSavedPosition = savedInstanceState.getLong("resumePosition", 0);
        }

        return mBinding.getRoot();
    }


    private void initPlayer(long resumePosition) {
        if(player == null) {
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(null);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);


            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            mBinding.VideoView.setPlayer(player);
            //The exoplayer progress bar already contains the current position information
            //We can make the position view not important for accessibility.
            View videoPosView = mBinding.VideoView.findViewById(R.id.exo_position);
            if (videoPosView != null) {
                videoPosView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);

            }


            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                    Util.getUserAgent(getContext(), "Blockcerts Wallet"), null);

            String filename = "rawresource:///" + R.raw.video;
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(filename));

            player.prepare(videoSource);

            player.setPlayWhenReady(!mIsPaused);

            player.seekTo(resumePosition);


            player.addListener(new Player.EventListener() {

                public void onPlayerStateChanged(boolean playWhenReady, int state) {
                    if (state == Player.STATE_ENDED) {
                        player.seekTo(0);
                        getActivity().finish();
                    }
                    if (state == Player.STATE_READY) {
                        mIsPaused = !playWhenReady;
                    }
                }

                public void onTimelineChanged(Timeline timeline, Object manifest, @Player.TimelineChangeReason int reason) {
                }

                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                }

                public void onLoadingChanged(boolean isLoading) {
                }

                public void onRepeatModeChanged(@Player.RepeatMode int repeatMode) {
                }

                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                }

                public void onPlayerError(ExoPlaybackException error) {
                }

                public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
                }

                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                }

                public void onSeekProcessed() {
                }

            });
        }
    }

    private void releasePlayer() {
        if(player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

}


