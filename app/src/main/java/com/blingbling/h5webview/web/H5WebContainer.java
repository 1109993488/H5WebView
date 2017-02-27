package com.blingbling.h5webview.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.blingbling.h5webview.R;
import com.blingbling.h5webview.web.listener.OnWebStateListener;

/**
 * Created by BlingBling on 2017/2/27.
 */

public class H5WebContainer extends FrameLayout implements OnWebStateListener, View.OnClickListener {

    private H5WebView mH5WebView;
    private ProgressBar mProgressBar;
    private View mNetErrorView;

    public H5WebContainer(@NonNull Context context) {
        this(context, null);
    }

    public H5WebContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.h5_web_container, this);
        initView();
    }

    private void initView() {
        mH5WebView = (H5WebView) findViewById(R.id.h5_web);
        mProgressBar = (ProgressBar) findViewById(R.id.h5_progress);
        mH5WebView.setOnWebStateListener(this);
    }

    private void showNetErrorView() {
        if (mH5WebView.getVisibility() == VISIBLE) {
            mH5WebView.setVisibility(INVISIBLE);
        }
        if (mNetErrorView == null) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.h5_no_net);
            mNetErrorView = viewStub.inflate();
            mNetErrorView.setOnClickListener(this);
        }
        if (mNetErrorView.getVisibility() != VISIBLE) {
            mNetErrorView.setVisibility(VISIBLE);
        }
    }

    private void hideNetErrorView(boolean showWebView) {
        if (showWebView && mH5WebView.getVisibility() != VISIBLE) {
            mH5WebView.setVisibility(VISIBLE);
        }
        if (mNetErrorView != null) {
            if (mNetErrorView.getVisibility() == VISIBLE) {
                mNetErrorView.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        mH5WebView.reload();
        hideNetErrorView(false);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageProgressChanged(int progress) {
        mProgressBar.setProgress(progress);
        if (progress == 1000) {
            mProgressBar.setVisibility(GONE);
        } else {
            if (mProgressBar.getVisibility() != VISIBLE) {
                mProgressBar.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void onPageFinished(String url) {
        hideNetErrorView(true);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        showNetErrorView();
    }

    public H5WebView getWebView() {
        return mH5WebView;
    }
}
