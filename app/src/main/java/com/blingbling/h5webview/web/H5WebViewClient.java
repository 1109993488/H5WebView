package com.blingbling.h5webview.web;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.v7.app.AlertDialog;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by BlingBling on 2016/11/28.
 */
class H5WebViewClient extends WebViewClient {

    private H5WebView mH5WebView;

    public void setH5WebView(H5WebView h5WebView) {
        mH5WebView = h5WebView;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        H5Log.d("onPageStarted  %s", url);
        mH5WebView.onPageStarted(url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        H5Log.d("shouldOverrideUrlLoading  %s", url);
        mH5WebView.shouldOverrideUrlLoading(url);
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        H5Log.d("onPageFinished  %s", url);
        mH5WebView.onPageFinished(url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        H5Log.d("onReceivedError  %d", view.getProgress());
        mH5WebView.onReceivedError(request, error);
    }

    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, final SslError error) {
        String message;
        switch (error.getPrimaryError()) {
            case SslError.SSL_UNTRUSTED:
                message = "当前证书不受信任，";
                break;
            case SslError.SSL_EXPIRED:
                message = "当前证书已经过期，";
                break;
            case SslError.SSL_IDMISMATCH:
                message = "证书ID不匹配，";
                break;
            case SslError.SSL_NOTYETVALID:
                message = "证书不可用，";
                break;
            default:
                message = "证书错误，";
                break;
        }
        message += "您依旧要信任访问么？";
        new AlertDialog.Builder(view.getContext())
                .setTitle("SSL证书错误")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                        mH5WebView.onReceivedSslError(error);
                    }
                }).show();
    }
}