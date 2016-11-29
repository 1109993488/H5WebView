package com.blingbling.h5webview.web;

import android.net.http.SslError;

/**
 * Created by BlingBling on 2016/11/28.
 */
public interface H5OnProgressListener {
    void onPageStarted(String url);

    void onProgressChanged(int newProgress);

    void onPageFinished(String url);

    void onReceivedError(int errorCode, String description, String failingUrl);

    void onReceivedSslError(SslError error);
}
