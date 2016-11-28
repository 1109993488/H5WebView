package com.blingbling.h5webview.web;

import android.util.Log;

import com.blingbling.h5webview.BuildConfig;

/**
 * Created by BlingBling on 2016/11/28.
 */

public class H5Log {

    public static final String TAG = "H5WebView";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static void d(String format, Object... args) {
        if (DEBUG) {
            Log.d(TAG, String.format(format, args));
        }
    }

    public static void e(String format, Object... args) {
        if (DEBUG) {
            Log.e(TAG, String.format(format, args));
        }
    }
}
