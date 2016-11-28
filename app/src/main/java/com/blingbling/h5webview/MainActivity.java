package com.blingbling.h5webview;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blingbling.h5webview.web.H5Log;
import com.blingbling.h5webview.web.H5OnProgressListener;
import com.blingbling.h5webview.web.H5WebView;

public class MainActivity extends AppCompatActivity implements H5OnProgressListener {

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
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mWeb.setH5OnProgressListener(this);
    }

    boolean first=true;
    public void click(View view) {
        if(first){
            first=false;
        mWeb.loadUrl(url);

        }else {
            mWeb.loadUrl(url2);
        }
    }

    public void click2(View view) {
        mWeb.loadUrl("javascript:appToH5TagData()");
    }

    @Override
    public void onBackPressed() {
        if (mWeb.canGoBack()) {
            mWeb.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url) {
        H5Log.e("onPageStarted");
        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChanged(int newProgress) {
        H5Log.e("onProgressChanged  %d", newProgress);
        mProgressBar.setProgress(newProgress);
    }

    @Override
    public void onPageFinished(String url) {
        H5Log.e("onPageFinished");
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onReceivedError(WebResourceRequest request, WebResourceError error) {
        Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceivedSslError(SslError error) {
        H5Log.e("onReceivedSslError");
    }
}
