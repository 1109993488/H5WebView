package com.blingbling.h5webview.web;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;

public class H5WebViewDownLoadListener implements DownloadListener {

    private Activity mActivity;

    public H5WebViewDownLoadListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mActivity.startActivity(intent);
    }

}