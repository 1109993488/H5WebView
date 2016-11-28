package com.blingbling.h5webview.web;

import android.content.Context;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by BlingBling on 2016/11/28.
 */
public class H5WebView extends WebView {

    H5OnProgressListener mH5OnProgressListener;
    H5OpenFileChooserListener mH5OpenFileChooserListener;
    private boolean mPageStated = false;
    private boolean mRedirect = false;
    private boolean mReceivedError = false;


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

    void onReceivedError(WebResourceRequest request, WebResourceError error) {
        if (!mReceivedError) {
            mReceivedError = true;
            if (mH5OnProgressListener != null) {
                mH5OnProgressListener.onReceivedError(request, error);
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
            final H5WebViewClient h5WebViewClient = (H5WebViewClient) client;
            h5WebViewClient.setH5WebView(this);
        } else {
            throw new IllegalArgumentException("you should use H5WebViewClient");
        }
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
        if (client instanceof WebChromeClient) {
            final H5WebChromeClient h5WebChromeClient = (H5WebChromeClient) client;
            h5WebChromeClient.setH5WebView(this);
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
}
