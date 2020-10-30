package com.lakue.lakuevideotrim.util;

import android.content.Context;

import java.lang.ref.WeakReference;

public class BaseUtils {

    private static final String ERROR_INIT = "Initialize BaseUtils with invoke init()";

    private static WeakReference<Context> mWeakReferenceContext;

    /**
     * init in Application
     */
    public static void init(Context context){
        mWeakReferenceContext = new WeakReference<>(context);
        //something to do...
    }

    public static Context getContext() {
        if (mWeakReferenceContext == null) {
            throw new IllegalArgumentException(ERROR_INIT);
        }
        return mWeakReferenceContext.get().getApplicationContext();
    }
}
