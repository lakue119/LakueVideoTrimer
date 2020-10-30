package com.lakue.lakuevideotrim.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.lakue.lakuevideotrim.R;
import com.lakue.lakuevideotrim.trim.OnExoPlayerStateListener;
import com.lakue.lakuevideotrim.util.DeviceUtil;
import com.lakue.lakuevideotrim.util.UnitConverter;

public class CustomExoPlayerView extends PlayerView {

    SimpleExoPlayer player;
    DataSource.Factory mediaDataSourceFactory;
    DefaultTrackSelector trackSelector;
    TrackGroupArray lastSeenTrackGroupArray;
    AdaptiveTrackSelection.Factory videoTrackSelectionFactory;

    OnExoPlayerStateListener onExoPlayerStateListener;

    private int maxHeight;

    public CustomExoPlayerView(Context context) {
        super(context);
    }

    public CustomExoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
    }

    public CustomExoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = View.MeasureSpec.makeMeasureSpec((int) ((maxHeight*3) / Resources.getSystem().getDisplayMetrics().density), View.MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomExoPlayerView);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomExoPlayerView, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {
        maxHeight = typedArray.getDimensionPixelSize(R.styleable.CustomExoPlayerView_video_max_height, UnitConverter.dpToPx(300));

        typedArray.recycle();
    }

    public void initializePlayer(String url) {
        if (trackSelector != null) {
            Log.i("DATADATA", "trankSelector : not null");
        } else {
            Log.i("DATADATA", "trankSelector : null");
        }

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mediaDataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "mediaPlayerSample"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(mediaDataSourceFactory)
                .createMediaSource(Uri.parse(url));

//        DefaultExtractorsFactory extractorsFactory =
//                new DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true);
//        ProgressiveMediaSource progressiveMediaSource =
//                new ProgressiveMediaSource.Factory(mediaDataSourceFactory, extractorsFactory)
//                        .createMediaSource(Uri.parse(url));


        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

        player.prepare(mediaSource, false, false);
        player.setPlayWhenReady(true);

        setShutterBackgroundColor(Color.TRANSPARENT);
        setPlayer(player);
        requestFocus();


        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {

                    case Player.STATE_IDLE: // 1
                        //재생 실패
                        onExoPlayerStateListener.onFail(player);
                        break;
                    case Player.STATE_BUFFERING: // 2
                        // 재생 준비
                        onExoPlayerStateListener.onBuffering(player);
                        break;
                    case Player.STATE_READY: // 3
                        // 재생 준비 완료
                        onExoPlayerStateListener.onReady(player);
                        break;
                    case Player.STATE_ENDED: // 4
                        // 재생 마침
                        onExoPlayerStateListener.onFinish(player);
                        break;
                    default:
                        break;
                }
            }
        });
        lastSeenTrackGroupArray = null;
    }

    public int getCurrentPosition(){
        return (int) player.getCurrentPosition();
    }

    public boolean isPlaying(){
        return player.getPlayWhenReady();
    }

    public void pause(){
        if(player != null){
            player.setPlayWhenReady(false);
        }
    }

    public int getDuration(){
        return (int) player.getDuration();
    }

    public void start(){
        if(player != null){
            player.setPlayWhenReady(true);
        }
    }

    public SimpleExoPlayer getPlayer(){
        return player;
    }

    public void seekTo(int position){
        player.seekTo(position);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i("DATADATA","onResume");
        player.setPlayWhenReady(true);
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.i("DATADATA","onPause");
        player.setPlayWhenReady(false);
    }


    public void releasePlayer() {
        if(player != null){
            player.release();
            trackSelector = null;
        }
    }

    public void setOnExoPlayerStateListener(OnExoPlayerStateListener onExoPlayerStateListener) {
        this.onExoPlayerStateListener = onExoPlayerStateListener;
    }
}
