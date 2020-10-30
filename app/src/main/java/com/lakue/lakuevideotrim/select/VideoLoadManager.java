package com.lakue.lakuevideotrim.select;

import android.content.Context;

import com.lakue.lakuevideotrim.callback.SimpleCallback;

public class VideoLoadManager {
    private ILoader mLoader;

    public void setLoader(ILoader loader) {
        this.mLoader = loader;
    }

    public void load(final Context context, final SimpleCallback listener) {
        mLoader.load(context, listener);
    }
}
