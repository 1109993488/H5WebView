package com.blingbling.h5webview.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.annotation.Keep;
import android.util.Base64;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.MissingResourceException;


/**
 * Created by BlingBling on 2017/2/25.
 */
class H5WebChromeClient extends WebChromeClient {

    private static final String CHARSET_DEFAULT = "UTF-8";
    private static final String LANGUAGE_DEFAULT_ISO3 = "eng";

    private H5WebView mH5WebView;

    protected String mLanguageIso3;
    /**
     * File upload callback for platform versions prior to Android 5.0
     */
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    /**
     * File upload callback for Android 5.0+
     */
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;

    public H5WebChromeClient(H5WebView h5WebView) {
        mH5WebView = h5WebView;
        mLanguageIso3 = getLanguageIso3();
    }

    protected static String getLanguageIso3() {
        try {
            return Locale.getDefault().getISO3Language().toLowerCase(Locale.US);
        } catch (MissingResourceException e) {
            return LANGUAGE_DEFAULT_ISO3;
        }
    }

    // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
    @SuppressWarnings("unused")
    @Keep
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, null);
    }

    // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
    @Keep
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooser(uploadMsg, acceptType, null);
    }

    // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
    @SuppressWarnings("unused")
    @Keep
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileInput(uploadMsg, null, false);
    }

    // file upload callback (Android 5.0 (API level 21) -- current) (public method)
    @SuppressWarnings("all")
    @Keep
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (Build.VERSION.SDK_INT >= 21) {
            final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;

            openFileInput(null, filePathCallback, allowMultiple);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mH5WebView.onWebPageProgressChanged(newProgress);
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onProgressChanged(view, newProgress);
        } else {
            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onReceivedTitle(view, title);
        } else {
            super.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onReceivedIcon(view, icon);
        } else {
            super.onReceivedIcon(view, icon);
        }
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
        } else {
            super.onReceivedTouchIconUrl(view, url, precomposed);
        }
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onShowCustomView(view, callback);
        } else {
            super.onShowCustomView(view, callback);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        if (Build.VERSION.SDK_INT >= 14) {
            if (mH5WebView.mCustomWebChromeClient != null) {
                mH5WebView.mCustomWebChromeClient.onShowCustomView(view, requestedOrientation, callback);
            } else {
                super.onShowCustomView(view, requestedOrientation, callback);
            }
        }
    }

    @Override
    public void onHideCustomView() {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onHideCustomView();
        } else {
            super.onHideCustomView();
        }
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            return mH5WebView.mCustomWebChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        } else {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onRequestFocus(view);
        } else {
            super.onRequestFocus(view);
        }
    }

    @Override
    public void onCloseWindow(WebView window) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onCloseWindow(window);
        } else {
            super.onCloseWindow(window);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            return mH5WebView.mCustomWebChromeClient.onJsAlert(view, url, message, result);
        } else {
            return super.onJsAlert(view, url, message, result);
        }
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            return mH5WebView.mCustomWebChromeClient.onJsConfirm(view, url, message, result);
        } else {
            return super.onJsConfirm(view, url, message, result);
        }
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            return mH5WebView.mCustomWebChromeClient.onJsPrompt(view, url, message, defaultValue, result);
        } else {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            return mH5WebView.mCustomWebChromeClient.onJsBeforeUnload(view, url, message, result);
        } else {
            return super.onJsBeforeUnload(view, url, message, result);
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (mH5WebView.mGeolocationEnabled) {
            callback.invoke(origin, true, false);
        } else {
            if (mH5WebView.mCustomWebChromeClient != null) {
                mH5WebView.mCustomWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
            } else {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onGeolocationPermissionsHidePrompt();
        } else {
            super.onGeolocationPermissionsHidePrompt();
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onPermissionRequest(PermissionRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (mH5WebView.mCustomWebChromeClient != null) {
                mH5WebView.mCustomWebChromeClient.onPermissionRequest(request);
            } else {
                super.onPermissionRequest(request);
            }
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onPermissionRequestCanceled(PermissionRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (mH5WebView.mCustomWebChromeClient != null) {
                mH5WebView.mCustomWebChromeClient.onPermissionRequestCanceled(request);
            } else {
                super.onPermissionRequestCanceled(request);
            }
        }
    }

    @Override
    public boolean onJsTimeout() {
        if (mH5WebView.mCustomWebChromeClient != null) {
            return mH5WebView.mCustomWebChromeClient.onJsTimeout();
        } else {
            return super.onJsTimeout();
        }
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onConsoleMessage(message, lineNumber, sourceID);
        } else {
            super.onConsoleMessage(message, lineNumber, sourceID);
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            return mH5WebView.mCustomWebChromeClient.onConsoleMessage(consoleMessage);
        } else {
            return super.onConsoleMessage(consoleMessage);
        }
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (mH5WebView.mCustomWebChromeClient != null) {
            return mH5WebView.mCustomWebChromeClient.getDefaultVideoPoster();
        } else {
            return super.getDefaultVideoPoster();
        }
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (mH5WebView.mCustomWebChromeClient != null) {
            return mH5WebView.mCustomWebChromeClient.getVideoLoadingProgressView();
        } else {
            return super.getVideoLoadingProgressView();
        }
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.getVisitedHistory(callback);
        } else {
            super.getVisitedHistory(callback);
        }
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        } else {
            super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        }
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        if (mH5WebView.mCustomWebChromeClient != null) {
            mH5WebView.mCustomWebChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        } else {
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }
    }

    @SuppressLint("NewApi")
    protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);

        if (allowMultiple) {
            if (Build.VERSION.SDK_INT >= 18) {
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }

        i.setType(mH5WebView.mUploadFileTypes);

        if (mH5WebView.mFragment != null && mH5WebView.mFragment.get() != null && Build.VERSION.SDK_INT >= 11) {
            mH5WebView.mFragment.get().startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), mH5WebView.mRequestCodeFilePicker);
        } else if (mH5WebView.mActivity != null && mH5WebView.mActivity.get() != null) {
            mH5WebView.mActivity.get().startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), mH5WebView.mRequestCodeFilePicker);
        }
    }

    /**
     * Provides localizations for the 25 most widely spoken languages that have a ISO 639-2/T code
     *
     * @return the label for the file upload prompts as a string
     */
    protected String getFileUploadPromptLabel() {
        try {
            if (mLanguageIso3.equals("zho")) return decodeBase64("6YCJ5oup5LiA5Liq5paH5Lu2");
            else if (mLanguageIso3.equals("spa")) return decodeBase64("RWxpamEgdW4gYXJjaGl2bw==");
            else if (mLanguageIso3.equals("hin"))
                return decodeBase64("4KSP4KSVIOCkq+CkvOCkvuCkh+CksiDgpJrgpYHgpKjgpYfgpII=");
            else if (mLanguageIso3.equals("ben"))
                return decodeBase64("4KaP4KaV4Kaf4Ka/IOCmq+CmvuCmh+CmsiDgpqjgpr/gprDgp43gpqzgpr7gpprgpqg=");
            else if (mLanguageIso3.equals("ara"))
                return decodeBase64("2KfYrtiq2YrYp9ixINmF2YTZgSDZiNin2K3Yrw==");
            else if (mLanguageIso3.equals("por")) return decodeBase64("RXNjb2xoYSB1bSBhcnF1aXZv");
            else if (mLanguageIso3.equals("rus"))
                return decodeBase64("0JLRi9Cx0LXRgNC40YLQtSDQvtC00LjQvSDRhNCw0LnQuw==");
            else if (mLanguageIso3.equals("jpn"))
                return decodeBase64("MeODleOCoeOCpOODq+OCkumBuOaKnuOBl+OBpuOBj+OBoOOBleOBhA==");
            else if (mLanguageIso3.equals("pan"))
                return decodeBase64("4KiH4Kmx4KiVIOCoq+CovuCoh+CosiDgqJrgqYHgqKPgqYs=");
            else if (mLanguageIso3.equals("deu")) return decodeBase64("V8OkaGxlIGVpbmUgRGF0ZWk=");
            else if (mLanguageIso3.equals("jav")) return decodeBase64("UGlsaWggc2lqaSBiZXJrYXM=");
            else if (mLanguageIso3.equals("msa")) return decodeBase64("UGlsaWggc2F0dSBmYWls");
            else if (mLanguageIso3.equals("tel"))
                return decodeBase64("4LCS4LCVIOCwq+CxhuCxluCwsuCxjeCwqOCxgSDgsI7gsILgsJrgsYHgsJXgsYvgsILgsKHgsL8=");
            else if (mLanguageIso3.equals("vie"))
                return decodeBase64("Q2jhu41uIG3hu5l0IHThuq1wIHRpbg==");
            else if (mLanguageIso3.equals("kor"))
                return decodeBase64("7ZWY64KY7J2YIO2MjOydvOydhCDshKDtg50=");
            else if (mLanguageIso3.equals("fra"))
                return decodeBase64("Q2hvaXNpc3NleiB1biBmaWNoaWVy");
            else if (mLanguageIso3.equals("mar"))
                return decodeBase64("4KSr4KS+4KSH4KSyIOCkqOCkv+CkteCkoeCkvg==");
            else if (mLanguageIso3.equals("tam"))
                return decodeBase64("4K6S4K6w4K+BIOCuleCvh+CuvuCuquCvjeCuquCviCDgrqTgr4fgrrDgr43grrXgr4E=");
            else if (mLanguageIso3.equals("urd"))
                return decodeBase64("2KfbjNqpINmB2KfYptmEINmF24zauiDYs9uSINin2YbYqtiu2KfYqCDaqdix24zaug==");
            else if (mLanguageIso3.equals("fas"))
                return decodeBase64("2LHYpyDYp9mG2KrYrtin2Kgg2qnZhtuM2K8g24zaqSDZgdin24zZhA==");
            else if (mLanguageIso3.equals("tur")) return decodeBase64("QmlyIGRvc3lhIHNlw6dpbg==");
            else if (mLanguageIso3.equals("ita")) return decodeBase64("U2NlZ2xpIHVuIGZpbGU=");
            else if (mLanguageIso3.equals("tha"))
                return decodeBase64("4LmA4Lil4Li34Lit4LiB4LmE4Lif4Lil4LmM4Lir4LiZ4Li24LmI4LiH");
            else if (mLanguageIso3.equals("guj"))
                return decodeBase64("4KqP4KqVIOCqq+CqvuCqh+CqsuCqqOCrhyDgqqrgqrjgqoLgqqY=");
        } catch (Exception ignored) {
        }

        // return English translation by default
        return "Choose a file";
    }

    protected static String decodeBase64(final String base64) throws IllegalArgumentException, UnsupportedEncodingException {
        final byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return new String(bytes, CHARSET_DEFAULT);
    }

    /**
     * Returns whether file uploads can be used on the current device (generally all platform versions except for 4.4)
     *
     * @return whether file uploads can be used
     */
    public static boolean isFileUploadAvailable() {
        return isFileUploadAvailable(false);
    }

    /**
     * Returns whether file uploads can be used on the current device (generally all platform versions except for 4.4)
     * <p>
     * On Android 4.4.3/4.4.4, file uploads may be possible but will come with a wrong MIME type
     *
     * @param needsCorrectMimeType whether a correct MIME type is required for file uploads or `application/octet-stream` is acceptable
     * @return whether file uploads can be used
     */
    public static boolean isFileUploadAvailable(final boolean needsCorrectMimeType) {
        if (Build.VERSION.SDK_INT == 19) {
            final String platformVersion = (Build.VERSION.RELEASE == null) ? "" : Build.VERSION.RELEASE;

            return !needsCorrectMimeType && (platformVersion.startsWith("4.4.3") || platformVersion.startsWith("4.4.4"));
        } else {
            return true;
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    Uri[] dataUris = null;

                    try {
                        if (intent.getDataString() != null) {
                            dataUris = new Uri[]{Uri.parse(intent.getDataString())};
                        } else {
                            if (Build.VERSION.SDK_INT >= 16) {
                                if (intent.getClipData() != null) {
                                    final int numSelectedFiles = intent.getClipData().getItemCount();

                                    dataUris = new Uri[numSelectedFiles];

                                    for (int i = 0; i < numSelectedFiles; i++) {
                                        dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }

                    mFileUploadCallbackSecond.onReceiveValue(dataUris);
                    mFileUploadCallbackSecond = null;
                }
            }
        } else {
            if (mFileUploadCallbackFirst != null) {
                mFileUploadCallbackFirst.onReceiveValue(null);
                mFileUploadCallbackFirst = null;
            } else if (mFileUploadCallbackSecond != null) {
                mFileUploadCallbackSecond.onReceiveValue(null);
                mFileUploadCallbackSecond = null;
            }
        }
    }
}
