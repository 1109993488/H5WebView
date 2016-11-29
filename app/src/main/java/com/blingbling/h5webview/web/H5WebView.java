package com.blingbling.h5webview.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by BlingBling on 2016/11/28.
 */
public class H5WebView extends WebView {

    private H5WebViewClient mH5WebViewClient;
    private H5WebChromeClient mH5WebChromeClient;

    H5OnProgressListener mH5OnProgressListener;
    H5OpenFileChooserListener mH5OpenFileChooserListener;
    private boolean mPageStated = false;
    private boolean mRedirect = false;
    private boolean mReceivedError = false;

    public H5WebView(Context context){
        this(context,null);
    }

    public H5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(false);
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true); // 开启 Application Caches
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLoadWithOverviewMode(true);

        setWebViewClient(new H5WebViewClient());
        setWebChromeClient(new H5WebChromeClient());
        setDownloadListener(new H5WebViewDownLoadListener((Activity)getContext()));
    }

    void onPageStarted(String url) {
        mRedirect = false;
        if (!mPageStated) {
            mPageStated = true;
            if (mH5OnProgressListener != null) {
                mH5OnProgressListener.onPageStarted(url);
            }
        }
    }

    void onProgressChanged(int newProgress) {
        if (mPageStated && !mRedirect) {
            if (mH5OnProgressListener != null) {
                mH5OnProgressListener.onProgressChanged(newProgress);
            }
        }
    }

    void onPageFinished(String url) {
        if (mPageStated) {
            if (mReceivedError
                    || (!mRedirect && getProgress() == 100)) {
                mPageStated = false;
                mReceivedError = false;
                if (mH5OnProgressListener != null) {
                    mH5OnProgressListener.onPageFinished(url);
                }
            }
        }
    }

    void onReceivedError(int errorCode, String description, String failingUrl) {
        if (!mReceivedError) {
            mReceivedError = true;
            if (mH5OnProgressListener != null) {
                mH5OnProgressListener.onReceivedError(errorCode, description, failingUrl);
            }
        }
    }

    void onReceivedSslError(SslError error) {
        if (!mReceivedError) {
            mReceivedError = true;
            if (mH5OnProgressListener != null) {
                mH5OnProgressListener.onReceivedSslError(error);
            }
        }
    }

    void shouldOverrideUrlLoading(String url) {
        mRedirect = true;
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
        if (client instanceof H5WebViewClient) {
            mH5WebViewClient = (H5WebViewClient) client;
            mH5WebViewClient.setH5WebView(this);
        } else {
            throw new IllegalArgumentException("you should use H5WebViewClient");
        }
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
        if (client instanceof WebChromeClient) {
            mH5WebChromeClient = (H5WebChromeClient) client;
            mH5WebChromeClient.setH5WebView(this);
        } else {
            throw new IllegalArgumentException("you should use H5WebChromeClient");
        }
    }

    public void setH5OnProgressListener(H5OnProgressListener listener) {
        this.mH5OnProgressListener = listener;
    }

    public void setH5OpenFileChooserListener(H5OpenFileChooserListener listener) {
        this.mH5OpenFileChooserListener = listener;
    }

    public void onActivityResult(Context context, final int resultCode, final Intent data) {
        mH5WebChromeClient.onActivityResult(context, resultCode, data);
    }
}
