package com.hc.wallcontrl.util;


import com.orhanobut.logger.Logger;

import static android.R.attr.tag;

/**
 * Created by alex on 2017/4/8.
 */

public class LogUtil {

    public static void d( String msg) {
        Logger.d(msg);
    }

    public static void e( String msg) {
        Logger.e(msg);
    }

    public static void i( String msg) {
        Logger.i(msg);
    }

    public static void v( String msg) {
        Logger.v(msg);
    }

    public static void w(String msg) {
        Logger.w(msg);
    }

    public static void c(String msg) {
        Logger.wtf(msg);
    }
}
