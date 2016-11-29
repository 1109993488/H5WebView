package com.blingbling.h5webview.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.File;


/**
 * Created by BlingBling on 2016/11/28.
 */
public class H5WebChromeClient extends WebChromeClient {

    private H5WebView mH5WebView;
    private ValueCallback<Uri> mUploadMessage;
    private File mCameraFile;

    public void setH5WebView(H5WebView h5WebView) {
        mH5WebView = h5WebView;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        H5Log.d("onProgressChanged-->%d", newProgress);
        mH5WebView.onProgressChanged(newProgress);
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooserLocal(uploadMsg, acceptType, "");
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "", "");
    }

    // For Android > 4.1.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooserLocal(uploadMsg, acceptType, capture);
    }

    private void openFileChooserLocal(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        final String imageMimeType = "image/*";
        final String videoMimeType = "video/*";
        final String audioMimeType = "audio/*";
        final String mediaSourceKey = "capture";
        final String mediaSourceValueCamera = "camera";
        final String mediaSourceValueFileSystem = "filesystem";
        final String mediaSourceValueCamcorder = "camcorder";
        final String mediaSourceValueMicrophone = "microphone";

        // According to the spec, media source can be 'filesystem' or 'camera'
        // or 'camcorder'
        // or 'microphone' and the default value should be 'filesystem'.
        String mediaSource = mediaSourceValueFileSystem;

        if (mUploadMessage != null) {
            // Already a file picker operation in progress.
            return;
        }

        mUploadMessage = uploadMsg;

        // Parse the accept type.
        String params[] = acceptType.split(";");
        String mimeType = params[0];

        if (capture.length() > 0) {
            mediaSource = capture;
        }

        if (capture.equals(mediaSourceValueFileSystem)) {
            // To maintain backwards compatibility with the previous
            // implementation
            // of the media capture API, if the value of the 'capture' attribute
            // is
            // "filesystem", we should examine the accept-type for a MIME type
            // that
            // may specify a different capture value.
            for (String p : params) {
                String[] keyValue = p.split("=");
                if (keyValue.length == 2) {
                    // Process key=value parameters.
                    if (mediaSourceKey.equals(keyValue[0])) {
                        mediaSource = keyValue[1];
                    }
                }
            }
        }

        // Ensure it is not still set from a previous upload.
        mCameraFile = null;

        if (mimeType.equals(imageMimeType)) {
            if (mediaSource.equals(mediaSourceValueCamera)) {
                // Specified 'image/*' and requested the camera, so go ahead and
                // launch the
                // camera directly.
                startActivityForResult(createCamcorderIntent());
                return;
            } else {
                // Specified just 'image/*', capture=filesystem, or an invalid
                // capture parameter.
                // In all these cases we show a traditional picker filetered on
                // accept type
                // so launch an intent for both the Camera and image/* OPENABLE.
                Intent chooser = createChooserIntent(createCameraIntent());
                chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent(imageMimeType));
                startActivityForResult(chooser);
                return;
            }
        } else if (mimeType.equals(videoMimeType)) {
            if (mediaSource.equals(mediaSourceValueCamcorder)) {
                // Specified 'video/*' and requested the camcorder, so go ahead
                // and launch the
                // camcorder directly.
                startActivityForResult(createCamcorderIntent());
                return;
            } else {
                // Specified just 'video/*', capture=filesystem or an invalid
                // capture parameter.
                // In all these cases we show an intent for the traditional file
                // picker, filtered
                // on accept type so launch an intent for both camcorder and
                // video/* OPENABLE.
                Intent chooser = createChooserIntent(createCamcorderIntent());
                chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent(videoMimeType));
                startActivityForResult(chooser);
                return;
            }
        } else if (mimeType.equals(audioMimeType)) {
            if (mediaSource.equals(mediaSourceValueMicrophone)) {
                // Specified 'audio/*' and requested microphone, so go ahead and
                // launch the sound
                // recorder.
                startActivityForResult(createSoundRecorderIntent());
                return;
            } else {
                // Specified just 'audio/*', capture=filesystem of an invalid
                // capture parameter.
                // In all these cases so go ahead and launch an intent for both
                // the sound
                // recorder and audio/* OPENABLE.
                Intent chooser = createChooserIntent(createSoundRecorderIntent());
                chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent(audioMimeType));
                startActivityForResult(chooser);
                return;
            }
        }
        // No special handling based on the accept type was necessary, so
        // trigger the default
        // file upload chooser.
        startActivityForResult(createDefaultOpenableIntent());
    }

    private void startActivityForResult(Intent intent) {
        final H5OpenFileChooserListener listener = mH5WebView.mH5OpenFileChooserListener;
        if (listener != null) {
            listener.onStartActivityForResult(intent);
        }
    }

    private Intent createDefaultOpenableIntent() {
        // Create and return a chooser with the default OPENABLE
        // actions including the camera, camcorder and sound
        // recorder where available.
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");

        Intent chooser = createChooserIntent(createCameraIntent(),
                createCamcorderIntent(), createSoundRecorderIntent());
        chooser.putExtra(Intent.EXTRA_INTENT, i);
        return chooser;
    }

    private Intent createChooserIntent(Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_TITLE, "选择应用");
        return chooser;
    }

    private Intent createOpenableIntent(String type) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType(type);
        return i;
    }

    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final H5OpenFileChooserListener listener = mH5WebView.mH5OpenFileChooserListener;
        if (listener != null) {
            mCameraFile = listener.getCameraFile();
        }
        if (mCameraFile == null) {
            final String datetime = DateFormat.format("yyyyMMdd_HHmmss", System.currentTimeMillis()).toString();
            final File externalDataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            mCameraFile = new File(externalDataDir, "Camera" + File.separator + "IMG_" + datetime + ".jpg");
        }
        final File parentFile = mCameraFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
        return cameraIntent;
    }

    private Intent createCamcorderIntent() {
        return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    }

    private Intent createSoundRecorderIntent() {
        return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    }

    final void onActivityResult(Context context, final int resultCode, final Intent data) {
        File file = null;
        if (resultCode == Activity.RESULT_OK) {// WebView打开图片返回
            if (null != data) {
                Uri selectedImage = data.getData();
                String picturePath = PathFromUriUtil.getPath(context, selectedImage);
                if (!TextUtils.isEmpty(picturePath)) {
                    file = new File(picturePath);
                }
            } else if (mCameraFile != null && mCameraFile.exists()) {
                file = mCameraFile;
            }
        }
        if (mUploadMessage != null) {
            if (file != null && file.exists()) {
                mUploadMessage.onReceiveValue(Uri.fromFile(file));
            } else {
                mUploadMessage.onReceiveValue(null);
            }
        }
        mUploadMessage = null;
        mCameraFile = null;
    }
}
