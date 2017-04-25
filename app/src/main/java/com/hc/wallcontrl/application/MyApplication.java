package com.hc.wallcontrl.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by alex on 2017/4/8.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        mContext = context;
    }
}
