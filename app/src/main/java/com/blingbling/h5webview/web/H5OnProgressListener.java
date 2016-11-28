package com.blingbling.h5webview.web;

import android.net.http.SslError;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;

/**
 * Created by BlingBling on 2016/11/28.
 */
public interface H5OnProgressListener {
    void onPageStarted(String url);

    void onProgressChanged(int newProgress);

    void onPageFinished(String url);

    void onReceivedError(WebResourceRequest request, WebResourceError error);

    void onReceivedSslError(SslError error);
}
