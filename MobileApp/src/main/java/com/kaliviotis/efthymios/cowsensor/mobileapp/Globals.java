package com.kaliviotis.efthymios.cowsensor.mobileapp;

import android.content.Context;

/**
 * Created by Efthymios on 10/20/2017.
 */

public class Globals {
    private static Globals mGlobals = null;

    public static Globals getInstance() {
        if (mGlobals == null)
            mGlobals = new Globals();
        return mGlobals;
    }

    private Context mContext;

    public void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public Context getContext() {
        return mContext;
    }
}
