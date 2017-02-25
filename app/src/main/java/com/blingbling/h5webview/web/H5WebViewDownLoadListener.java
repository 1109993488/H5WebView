package com.blingbling.h5webview.web;

import android.webkit.DownloadListener;
import android.webkit.URLUtil;

/**
 * Created by BlingBling on 2017/2/25.
 */
class H5WebViewDownLoadListener implements DownloadListener {

    private H5WebView mH5WebView;

    public H5WebViewDownLoadListener(H5WebView h5WebView) {
        mH5WebView = h5WebView;
    }

    @Override
    public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimeType, final long contentLength) {
        final String suggestedFilename = URLUtil.guessFileName(url, contentDisposition, mimeType);

        if (mH5WebView.mDownloadListener != null) {
            mH5WebView.mDownloadListener.onDownloadRequested(url, suggestedFilename, mimeType, contentLength, contentDisposition, userAgent);
        }
    }
}