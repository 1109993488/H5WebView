package com.blingbling.h5webview.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for methods related to alternative browsers that have their own rendering engines
 */
public class Browsers {
    /**
     * Alternative browsers that have their own rendering engine and *may* be installed on this device
     */
    protected static final String[] ALTERNATIVE_BROWSERS = new String[]{"org.mozilla.firefox", "com.android.chrome", "com.opera.browser", "org.mozilla.firefox_beta", "com.chrome.beta", "com.opera.browser.beta"};

    /**
     * Package name of an alternative browser that is installed on this device
     */
    private static String mAlternativePackage;

    /**
     * Returns whether there is an alternative browser with its own rendering engine currently installed
     *
     * @param context a valid `Context` reference
     * @return whether there is an alternative browser or not
     */
    public static boolean hasAlternative(final Context context) {
        return getAlternative(context) != null;
    }

    /**
     * Returns the package name of an alternative browser with its own rendering engine or `null`
     *
     * @param context a valid `Context` reference
     * @return the package name or `null`
     */
    public static String getAlternative(final Context context) {
        if (mAlternativePackage != null) {
            return mAlternativePackage;
        }

        final List<String> alternativeBrowsers = Arrays.asList(ALTERNATIVE_BROWSERS);
        final List<ApplicationInfo> apps = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo app : apps) {
            if (!app.enabled) {
                continue;
            }

            if (alternativeBrowsers.contains(app.packageName)) {
                mAlternativePackage = app.packageName;

                return app.packageName;
            }
        }

        return null;
    }

    /**
     * Opens the given URL in an alternative browser
     *
     * @param context a valid `Activity` reference
     * @param url     the URL to open
     */
    public static void openUrl(final Activity context, final String url) {
        openUrl(context, url, false);
    }

    /**
     * Opens the given URL in an alternative browser
     *
     * @param context           a valid `Activity` reference
     * @param url               the URL to open
     * @param withoutTransition whether to switch to the browser `Activity` without a transition
     */
    public static void openUrl(final Activity context, final String url, final boolean withoutTransition) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage(getAlternative(context));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);

        if (withoutTransition) {
            context.overridePendingTransition(0, 0);
        }
    }

}