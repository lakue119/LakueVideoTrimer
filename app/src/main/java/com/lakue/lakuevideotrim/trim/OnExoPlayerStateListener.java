package com.lakue.lakuevideotrim.trim;

import com.google.android.exoplayer2.SimpleExoPlayer;

public interface OnExoPlayerStateListener {
    void onFail(SimpleExoPlayer player);
    void onBuffering(SimpleExoPlayer player);
    void onReady(SimpleExoPlayer player);
    void onFinish(SimpleExoPlayer player);
}
