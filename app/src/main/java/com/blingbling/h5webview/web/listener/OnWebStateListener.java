package com.blingbling.h5webview.web.listener;

import android.graphics.Bitmap;

/**
 * Created by BlingBling on 2017/2/25.
 */
public interface OnWebStateListener {
    void onPageStarted(String url, Bitmap favicon);

    /**
     * @param progress 0-1000
     */
    void onPageProgressChanged(int progress);

    void onPageFinished(String url);

    void onPageError(int errorCode, String description, String failingUrl);

}
