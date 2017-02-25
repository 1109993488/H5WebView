package com.blingbling.h5webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.blingbling.h5webview.web.H5WebView;
import com.blingbling.h5webview.web.listener.OnWebStateListener;

import static com.blingbling.h5webview.R.id.progressBar;

public class MainActivity extends AppCompatActivity implements OnWebStateListener {

    //    public static final String url="http://www.jianshu.com/p/569ab68da482";
    public static final String url = "http://www.baidu.com";
    public static final String url2 = "https://www.12306.cn/";

    H5WebView mWeb;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWeb = (H5WebView) findViewById(R.id.web);
        mProgressBar = (ProgressBar) findViewById(progressBar);
        mWeb.setOnWebStateListener(this, this);
        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("TA2", "onPageStarted-->" + view.getProgress());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("TA2", "onPageFinished-->" + view.getProgress());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("TA2", "onReceivedError-->" + view.getProgress());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("TA2", "shouldOverrideUrlLoading-->" + view.getProgress());
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        mWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d("TA2", "onProgressChanged-->" + newProgress);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWeb.onResume();
    }

    @Override
    protected void onPause() {
        mWeb.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWeb.onDestroy();
        super.onDestroy();
    }

    boolean first = true;

    public void click(View view) {
        if (first) {
            first = false;
            mWeb.loadUrl(url);

        } else {
            mWeb.loadUrl(url2);
        }
    }

    public void click2(View view) {
        mWeb.loadUrl("javascript:appToH5TagData()");
    }

    @Override
    public void onBackPressed() {
        if (mWeb.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        Log.e("TAG", "onPageStarted");
//        mProgressBar.setProgress(0);
//        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageProgressChanged(int progress) {
        Log.d("TAG", "onPageProgressChanged---->" + progress);
        mProgressBar.setProgress(progress);
        if(progress==1000){
            mProgressBar.setVisibility(View.GONE);
        }else {
            mProgressBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPageFinished(String url) {
        Log.e("TAG", "onPageFinished");
//        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        mProgressBar.setVisibility(View.GONE);
        Log.e("TAG", "onPageError");

    }
}
