package com.blingbling.h5webview.web;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by BlingBling on 2017/2/25.
 */

public class H5ProgressHelper {

    private static final int WHAT_UPDATE = 1;

    private boolean mFinish = false;
    private int mProgress = 0;
    private WeakReference<H5WebView> mRef;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final H5WebView h5WebView = mRef.get();
            if (h5WebView == null) {
                return;
            }
            mProgress = (int) msg.obj;
            h5WebView.onWebPageProgressChangedByHelper(mProgress);
            if (!mFinish) {
                switch (mProgress) {
                    case 600:
                        startProgress60To80();
                        break;
                    case 800:
                        startProgress80To90();
                        break;
                }
            }
        }
    };

    public H5ProgressHelper(H5WebView h5WebView) {
        mRef = new WeakReference<H5WebView>(h5WebView);
    }

    public void start() {
        mFinish = false;
        mHandler.removeCallbacksAndMessages(null);
        startProgressTo60();
    }

    public void stop() {
        cancel();
        startProgressTo100();
    }

    public void cancel() {
        mFinish = true;
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 进度条 假装加载到60%
     */
    private void startProgressTo60() {
        for (int i = 0; i <= 600; i++) {
            updateProgressDelayed(i, i * 6);
        }
    }

    /**
     * 进度条 假装加载60%-80%
     */
    private void startProgress60To80() {
        for (int i = 601; i <= 800; i++) {
            updateProgressDelayed(i, (i - 600) * 20);
        }
    }

    /**
     * 进度条 假装加载80%-90%
     */
    private void startProgress80To90() {
        for (int i = 801; i <= 900; i++) {
            updateProgressDelayed(i, (i - 800) * 50);
        }
    }

    /**
     * 进度条 加载到100%
     */
    private void startProgressTo100() {
        final int start = mProgress;
        for (int i = mProgress + 1; i <= 1000; i++) {
            updateProgressDelayed(i, i - start);
        }
    }

    private void updateProgressDelayed(int progress, int delayMillis) {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(WHAT_UPDATE, progress), delayMillis);
    }

}
