package com.blingbling.h5webview.web;

import android.content.Intent;

import java.io.File;

/**
 * Created by BlingBling on 2016/11/28.
 */
public interface H5OpenFileChooserListener {
    File getCameraFile();
    void onStartActivityForResult(Intent intent);
}
