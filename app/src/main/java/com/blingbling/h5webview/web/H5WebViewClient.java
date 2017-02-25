package com.blingbling.h5webview.web;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by BlingBling on 2017/2/25.
 */
class H5WebViewClient extends WebViewClient {

    private H5WebView mH5WebView;

    public H5WebViewClient(H5WebView h5WebView) {
        mH5WebView = h5WebView;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        mH5WebView.onWebPageStarted(url,favicon);

        if (mH5WebView.mCustomWebViewClient != null) {
            mH5WebView.mCustomWebViewClient.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        mH5WebView.onWebPageFinished(url);

        if (mH5WebView.mCustomWebViewClient != null) {
            mH5WebView.mCustomWebViewClient.onPageFinished(view, url);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        mH5WebView.onWebPageError(errorCode,description,failingUrl);

        if (mH5WebView.mCustomWebViewClient != null) {
            mH5WebView.mCustomWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
        // if the hostname may not be accessed
        if (!mH5WebView.isHostnameAllowed(url)) {
            // if a listener is available
            if (mH5WebView.mExternalPageRequestListener != null) {
                // inform the listener about the request
                mH5WebView.mExternalPageRequestListener.onExternalPageRequest(url);
            }

            // cancel the original request
            return true;
        }

        // if there is a user-specified handler available
        if (mH5WebView.mCustomWebViewClient != null) {
            // if the user-specified handler asks to override the request
            if (mH5WebView.mCustomWebViewClient.shouldOverrideUrlLoading(view, url)) {
                // cancel the original request
                return true;
            }
        }

        // route the request through the custom URL loading method
        view.loadUrl(url);

        // cancel the original request
        return true;
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (mH5WebView.mCustomWebViewClient != null) {
            mH5WebView.mCustomWebViewClient.onLoadResource(view, url);
        } else {
            super.onLoadResource(view, url);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (Build.VERSION.SDK_INT >= 11) {
            if (mH5WebView.mCustomWebViewClient != null) {
                return mH5WebView.mCustomWebViewClient.shouldInterceptRequest(view, url);
            } else {
                return super.shouldInterceptRequest(view, url);
            }
        } else {
            return null;
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (mH5WebView.mCustomWebViewClient != null) {
                return mH5WebView.mCustomWebViewClient.shouldInterceptRequest(view, request);
            } else {
                return super.shouldInterceptRequest(view, request);
            }
        } else {
            return null;
        }
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        if (mH5WebView.mCustomWebViewClient != null) {
            mH5WebView.mCustomWebViewClient.onFormResubmission(view, dontResend, resend);
        } else {
            super.onFormResubmission(view, dontResend, resend);
        }
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (mH5WebView.mCustomWebViewClient != null) {
            mH5WebView.mCustomWebViewClient.doUpdateVisitedHistory(view, url, isReload);
        } else {
            super.doUpdateVisitedHistory(view, url, isReload);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//        if (mH5WebView.mCustomWebViewClient != null) {
//            mH5WebView.mCustomWebViewClient.onReceivedSslError(view, handler, error);
//        } else {
//            super.onReceivedSslError(view, handler, error);
//        }
        handler.proceed();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (mH5WebView.mCustomWebViewClient != null) {
                mH5WebView.mCustomWebViewClient.onReceivedClientCertRequest(view, request);
            } else {
                super.onReceivedClientCertRequest(view, request);
            }
        }
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if (mH5WebView.mCustomWebViewClient != null) {
            mH5WebView.mCustomWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
        } else {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (mH5WebView.mCustomWebViewClient != null) {
            return mH5WebView.mCustomWebViewClient.shouldOverrideKeyEvent(view, event);
        } else {
            return super.shouldOverrideKeyEvent(view, event);
        }
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (mH5WebView.mCustomWebViewClient != null) {
            mH5WebView.mCustomWebViewClient.onUnhandledKeyEvent(view, event);
        } else {
            super.onUnhandledKeyEvent(view, event);
        }
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (mH5WebView.mCustomWebViewClient != null) {
            mH5WebView.mCustomWebViewClient.onScaleChanged(view, oldScale, newScale);
        } else {
            super.onScaleChanged(view, oldScale, newScale);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        if (Build.VERSION.SDK_INT >= 12) {
            if (mH5WebView.mCustomWebViewClient != null) {
                mH5WebView.mCustomWebViewClient.onReceivedLoginRequest(view, realm, account, args);
            } else {
                super.onReceivedLoginRequest(view, realm, account, args);
            }
        }
    }

}