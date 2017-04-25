package com.hc.wallcontrl.util;

import android.util.Log;

/**
 * Created by alex on 2017/4/8.
 */

public class LogUtil {

    public static void d(String tag, String msg) {
        Log.d(tag,msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag,msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag,msg);
    }

    public static void v(String tag, String msg) {
        Log.v(tag,msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag,msg);
    }

    public static void c(String tag, String msg) {
        Log.wtf(tag,msg);
    }
}
