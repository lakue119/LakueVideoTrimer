package com.lakue.lakuevideotrim;

import android.app.Application;

import com.lakue.lakuevideotrim.util.BaseUtils;

public class ZApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        BaseUtils.init(this);
//        initFFmpegBinary(this);
    }

//    private void initFFmpegBinary(Context context) {
//        if (!FFmpeg.getInstance(context).isSupported()) {
//            Log.e("ZApplication","Android cup arch not supported!");
//        }
//    }
}
