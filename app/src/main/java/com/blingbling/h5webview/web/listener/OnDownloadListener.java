package com.blingbling.h5webview.web.listener;

/**
 * Created by BlingBling on 2017/2/25.
 */

public interface OnDownloadListener {
    void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent);
}
