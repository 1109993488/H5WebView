package com.blingbling.h5webview.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blingbling.h5webview.web.listener.OnDownloadListener;
import com.blingbling.h5webview.web.listener.OnExternalPageRequestListener;
import com.blingbling.h5webview.web.listener.OnWebStateListener;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by BlingBling on 2017/2/25.
 */
public class H5WebView extends WebView {
    protected static final int WEB_STATE_LOADING = 1;
    protected static final int WEB_STATE_STOP = 2;
    protected static final int WEB_STATE_ERROR = 3;

    protected static final String DATABASES_SUB_FOLDER = "/databases";

    protected WeakReference<Activity> mActivity;
    protected WeakReference<Fragment> mFragment;
    protected final List<String> mPermittedHostNames = new LinkedList<String>();

    protected H5WebViewClient mH5WebViewClient;
    protected H5WebChromeClient mH5WebChromeClient;
    protected WebViewClient mCustomWebViewClient;
    protected WebChromeClient mCustomWebChromeClient;
    protected boolean mGeolocationEnabled;
    protected final Map<String, String> mHttpHeaders = new HashMap<String, String>();

    protected OnWebStateListener mWebStateListener;
    protected OnDownloadListener mDownloadListener;
    protected OnExternalPageRequestListener mExternalPageRequestListener;

    protected static final int REQUEST_CODE_FILE_PICKER = 51426;
    protected int mRequestCodeFilePicker = REQUEST_CODE_FILE_PICKER;
    protected String mUploadFileTypes = "*/*";

    private int mState = WEB_STATE_STOP;
    private long mLastError;
    private H5LoadTimeOutHelper mH5LoadTimeOutHelper;
    private H5ProgressHelper mH5ProgressHelper;
    private boolean mEnableProgressHelper = true;

    public H5WebView(Context context) {
        super(context);
        init(context);
    }

    public H5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public H5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnWebStateListener(final Activity activity, final OnWebStateListener listener) {
        if (activity != null) {
            mActivity = new WeakReference<Activity>(activity);
        } else {
            mActivity = null;
        }

        mWebStateListener = listener;
    }

    public void setOnWebStateListener(final Fragment fragment, final OnWebStateListener listener) {
        if (fragment != null) {
            mFragment = new WeakReference<Fragment>(fragment);
        } else {
            mFragment = null;
        }

        mWebStateListener = listener;
    }

    public void setOnDownloadListener(OnDownloadListener listener) {
        mDownloadListener = listener;
    }

    public void setOnExternalPageRequestListener(OnExternalPageRequestListener listener) {
        this.mExternalPageRequestListener = listener;
    }

    @Override
    public void setWebViewClient(final WebViewClient client) {
        mCustomWebViewClient = client;
    }

    @Override
    public void setWebChromeClient(final WebChromeClient client) {
        mCustomWebChromeClient = client;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setGeolocationEnabled(final boolean enabled) {
        if (enabled) {
            getSettings().setJavaScriptEnabled(true);
            getSettings().setGeolocationEnabled(true);
            setGeolocationDatabasePath();
        }

        mGeolocationEnabled = enabled;
    }

    @SuppressLint("NewApi")
    protected void setGeolocationDatabasePath() {
        final Activity activity;

        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11 && mFragment.get().getActivity() != null) {
            activity = mFragment.get().getActivity();
        } else if (mActivity != null && mActivity.get() != null) {
            activity = mActivity.get();
        } else {
            return;
        }

        getSettings().setGeolocationDatabasePath(activity.getFilesDir().getPath());
    }

    /**
     * Loads and displays the provided HTML source text
     *
     * @param html the HTML source text to load
     */
    public void loadHtml(final String html) {
        loadHtml(html, null);
    }

    /**
     * Loads and displays the provided HTML source text
     *
     * @param html    the HTML source text to load
     * @param baseUrl the URL to use as the page's base URL
     */
    public void loadHtml(final String html, final String baseUrl) {
        loadHtml(html, baseUrl, null);
    }

    /**
     * Loads and displays the provided HTML source text
     *
     * @param html       the HTML source text to load
     * @param baseUrl    the URL to use as the page's base URL
     * @param historyUrl the URL to use for the page's history entry
     */
    public void loadHtml(final String html, final String baseUrl, final String historyUrl) {
        loadHtml(html, baseUrl, historyUrl, "utf-8");
    }

    /**
     * Loads and displays the provided HTML source text
     *
     * @param html       the HTML source text to load
     * @param baseUrl    the URL to use as the page's base URL
     * @param historyUrl the URL to use for the page's history entry
     * @param encoding   the encoding or charset of the HTML source text
     */
    public void loadHtml(final String html, final String baseUrl, final String historyUrl, final String encoding) {
        loadDataWithBaseURL(baseUrl, html, "text/html", encoding, historyUrl);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onResume() {
        if (Build.VERSION.SDK_INT >= 11) {
            super.onResume();
        }
        resumeTimers();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onPause() {
        pauseTimers();
        if (Build.VERSION.SDK_INT >= 11) {
            super.onPause();
        }
    }

    public void onDestroy() {
        mH5LoadTimeOutHelper.stop();
        mH5ProgressHelper.cancel();
        // try to remove this view from its parent first
        try {
            ((ViewGroup) getParent()).removeView(this);
        } catch (Exception ignored) {
        }

        // then try to remove all child views from this view
        try {
            removeAllViews();
        } catch (Exception ignored) {
        }

        // and finally destroy this view
        destroy();
    }


    public void setUploadableFileTypes(final int requestCodeFilePicker) {
        mRequestCodeFilePicker = requestCodeFilePicker;
    }

    public void setUploadableFileTypes(final String mimeType) {
        mUploadFileTypes = mimeType;
    }

    public void setUploadableFileTypes(final int requestCodeFilePicker, final String mimeType) {
        mRequestCodeFilePicker = requestCodeFilePicker;
        mUploadFileTypes = mimeType;
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == mRequestCodeFilePicker) {
            mH5WebChromeClient.onActivityResult(requestCode, resultCode, intent);
        }
    }

    /**
     * Adds an additional HTTP header that will be sent along with every HTTP `GET` request
     * <p>
     * This does only affect the main requests, not the requests to included resources (e.g. images)
     * <p>
     * If you later want to delete an HTTP header that was previously added this way, call `removeHttpHeader()`
     * <p>
     * The `WebView` implementation may in some cases overwrite headers that you set or unset
     *
     * @param name  the name of the HTTP header to add
     * @param value the value of the HTTP header to send
     */
    public void addHttpHeader(final String name, final String value) {
        mHttpHeaders.put(name, value);
    }

    /**
     * Removes one of the HTTP headers that have previously been added via `addHttpHeader()`
     * <p>
     * If you want to unset a pre-defined header, set it to an empty string with `addHttpHeader()` instead
     * <p>
     * The `WebView` implementation may in some cases overwrite headers that you set or unset
     *
     * @param name the name of the HTTP header to remove
     */
    public void removeHttpHeader(final String name) {
        mHttpHeaders.remove(name);
    }

    public void addPermittedHostName(String hostname) {
        mPermittedHostNames.add(hostname);
    }

    public void addPermittedHostNames(Collection<? extends String> collection) {
        mPermittedHostNames.addAll(collection);
    }

    public List<String> getPermittedHostNames() {
        return mPermittedHostNames;
    }

    public void removePermittedHostName(String hostname) {
        mPermittedHostNames.remove(hostname);
    }

    public void clearPermittedHostNames() {
        mPermittedHostNames.clear();
    }

    public boolean onBackPressed() {
        if (canGoBack()) {
            goBack();
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("NewApi")
    protected static void setAllowAccessFromFileUrls(final WebSettings webSettings, final boolean allowed) {
        if (Build.VERSION.SDK_INT >= 16) {
            webSettings.setAllowFileAccessFromFileURLs(allowed);
            webSettings.setAllowUniversalAccessFromFileURLs(allowed);
        }
    }

    @SuppressWarnings("static-method")
    public void setCookiesEnabled(final boolean enabled) {
        CookieManager.getInstance().setAcceptCookie(enabled);
    }

    @SuppressLint("NewApi")
    public void setThirdPartyCookiesEnabled(final boolean enabled) {
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, enabled);
        }
    }

    public void setMixedContentAllowed(final boolean allowed) {
        setMixedContentAllowed(getSettings(), allowed);
    }

    @SuppressWarnings("static-method")
    @SuppressLint("NewApi")
    protected void setMixedContentAllowed(final WebSettings webSettings, final boolean allowed) {
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(allowed ? WebSettings.MIXED_CONTENT_ALWAYS_ALLOW : WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }
    }

    public void setDesktopMode(final boolean enabled) {
        final WebSettings webSettings = getSettings();

        final String newUserAgent;
        if (enabled) {
            newUserAgent = webSettings.getUserAgentString().replace("Mobile", "eliboM").replace("Android", "diordnA");
        } else {
            newUserAgent = webSettings.getUserAgentString().replace("eliboM", "Mobile").replace("diordnA", "Android");
        }

        webSettings.setUserAgentString(newUserAgent);
        webSettings.setUseWideViewPort(enabled);
        webSettings.setLoadWithOverviewMode(enabled);
        webSettings.setSupportZoom(enabled);
//        webSettings.setBuiltInZoomControls(enabled);//缩放按钮
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    protected void init(Context context) {
        // in IDE's preview mode
        if (isInEditMode()) {
            // do not run the code from this method
            return;
        }

        mH5LoadTimeOutHelper = new H5LoadTimeOutHelper(this);
        mH5ProgressHelper = new H5ProgressHelper(this);

        if (context instanceof Activity) {
            mActivity = new WeakReference<Activity>((Activity) context);
        }

        setFocusable(true);
        setFocusableInTouchMode(true);

        setSaveEnabled(true);

        final String filesDir = context.getFilesDir().getPath();
        final String databaseDir = filesDir.substring(0, filesDir.lastIndexOf("/")) + DATABASES_SUB_FOLDER;

        final WebSettings webSettings = getSettings();
        webSettings.setAllowFileAccess(false);
        setAllowAccessFromFileUrls(webSettings, false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT < 18) {
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }
        webSettings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < 19) {
            webSettings.setDatabasePath(databaseDir);
        }
        setMixedContentAllowed(webSettings, true);

        setThirdPartyCookiesEnabled(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);

        mH5WebViewClient = new H5WebViewClient(this);
        mH5WebChromeClient = new H5WebChromeClient(this);
        super.setWebViewClient(mH5WebViewClient);
        super.setWebChromeClient(mH5WebChromeClient);

        setDownloadListener(new H5WebViewDownLoadListener(this));
    }

    private void setLastError() {
        mLastError = System.currentTimeMillis();
    }

    private boolean hasError() {
        return (mLastError + 500) >= System.currentTimeMillis();
    }

    public void enableProgressHelper(boolean enable) {
        mEnableProgressHelper = enable;
    }

    final void onWebPageStarted(String url, Bitmap favicon) {
        if (mState == WEB_STATE_STOP
                || (!hasError() && mState == WEB_STATE_ERROR)) {
            mState = WEB_STATE_LOADING;
            mH5LoadTimeOutHelper.start();
            if (mEnableProgressHelper) {
                mH5ProgressHelper.start();
            }
            if (mWebStateListener != null) {
                mWebStateListener.onPageStarted(url, favicon);
            }
        }
    }

    /**
     * WebView更新进度条方法
     *
     * @param progress
     */
    final void onWebPageProgressChanged(int progress) {
        if (mState != WEB_STATE_LOADING) {
            return;
        }
        if (!mEnableProgressHelper) {
            if (mWebStateListener != null) {
                mWebStateListener.onPageProgressChanged(progress * 10);
            }
        }
    }

    /**
     * {@link H5ProgressHelper}调用更新进度条
     *
     * @param progress
     */
    final void onWebPageProgressChangedByHelper(int progress) {
        if (mEnableProgressHelper) {
            if (mWebStateListener != null) {
                mWebStateListener.onPageProgressChanged(progress);
            }
        }
    }

    final void onWebPageFinished(String url) {
        if (getProgress() == 100 && mState == WEB_STATE_LOADING) {
            mState = WEB_STATE_STOP;
            mH5LoadTimeOutHelper.stop();
            if (mEnableProgressHelper) {
                mH5ProgressHelper.stop();
            }
            if (mWebStateListener != null) {
                mWebStateListener.onPageFinished(url);
            }
        }
    }

    final void onWebPageError(int errorCode, String description, String failingUrl) {
        setLastError();
        mState = WEB_STATE_ERROR;
        mH5LoadTimeOutHelper.stop();
        if (mEnableProgressHelper) {
            mH5ProgressHelper.stop();
        }
        if (mWebStateListener != null) {
            mWebStateListener.onPageError(errorCode, description, failingUrl);
        }
    }

    final void onLoadTimeOut() {
        stopLoading();
        mState = WEB_STATE_ERROR;
        if (mEnableProgressHelper) {
            mH5ProgressHelper.stop();
        }
        if (mWebStateListener != null) {
            mWebStateListener.onPageError(504, "网络请求超时", getOriginalUrl());
        }
    }

    @Override
    public void loadUrl(final String url, Map<String, String> additionalHttpHeaders) {
        if (additionalHttpHeaders == null) {
            additionalHttpHeaders = mHttpHeaders;
        } else if (mHttpHeaders.size() > 0) {
            additionalHttpHeaders.putAll(mHttpHeaders);
        }

        super.loadUrl(url, additionalHttpHeaders);
    }

    @Override
    public void loadUrl(final String url) {
        if (mHttpHeaders.size() > 0) {
            super.loadUrl(url, mHttpHeaders);
        } else {
            super.loadUrl(url);
        }
    }

    public void loadUrl(String url, final boolean preventCaching) {
        if (preventCaching) {
            url = makeUrlUnique(url);
        }

        loadUrl(url);
    }

    public void loadUrl(String url, final boolean preventCaching, final Map<String, String> additionalHttpHeaders) {
        if (preventCaching) {
            url = makeUrlUnique(url);
        }

        loadUrl(url, additionalHttpHeaders);
    }

    protected static String makeUrlUnique(final String url) {
        StringBuilder unique = new StringBuilder();
        unique.append(url);

        if (url.contains("?")) {
            unique.append('&');
        } else {
            if (url.lastIndexOf('/') <= 7) {
                unique.append('/');
            }
            unique.append('?');
        }

        unique.append(System.currentTimeMillis());
        unique.append('=');
        unique.append(1);

        return unique.toString();
    }

    protected boolean isHostnameAllowed(final String url) {
        // if the permitted hostnames have not been restricted to a specific set
        if (mPermittedHostNames.size() == 0) {
            // all hostnames are allowed
            return true;
        }

        // get the actual hostname of the URL that is to be checked
        final String actualHost = Uri.parse(url).getHost();

        // for every hostname in the set of permitted hosts
        for (String expectedHost : mPermittedHostNames) {
            // if the two hostnames match or if the actual host is a subdomain of the expected host
            if (actualHost.equals(expectedHost) || actualHost.endsWith("." + expectedHost)) {
                // the actual hostname of the URL to be checked is allowed
                return true;
            }
        }

        // the actual hostname of the URL to be checked is not allowed since there were no matches
        return false;
    }

}
