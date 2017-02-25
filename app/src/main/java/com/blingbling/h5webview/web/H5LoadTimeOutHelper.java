package com.blingbling.h5webview.web;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by BlingBling on 2017/2/25.
 */

class H5LoadTimeOutHelper {

    private static final long DELAY_MILLIS_TIME_OUT = 20000;
    private static final int WHAT_START = 1;

    private WeakReference<H5WebView> mRef;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final H5WebView h5WebView = mRef.get();
            if (h5WebView == null) {
                return;
            }

            h5WebView.onLoadTimeOut();
        }
    };

    public H5LoadTimeOutHelper(H5WebView h5WebView) {
        mRef = new WeakReference<H5WebView>(h5WebView);
    }

    public void start() {
        stop();
        mHandler.sendEmptyMessageDelayed(WHAT_START, DELAY_MILLIS_TIME_OUT);
    }

    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
    }
}
