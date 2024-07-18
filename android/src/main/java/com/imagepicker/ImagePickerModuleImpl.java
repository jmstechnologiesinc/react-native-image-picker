package com.imagepicker;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.imagepicker.Utils.*;

public class ImagePickerModuleImpl implements ActivityEventListener {
    static final String NAME = "ImagePicker";

    public static final int REQUEST_LAUNCH_LIBRARY = 13003;

    private ReactApplicationContext reactContext;
    private Callback callback;
    private Options options;

    public ImagePickerModuleImpl(ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(this);
    }

    public void launchImageLibrary(final ReadableMap options, final Callback callback) {
        final Activity currentActivity = this.reactContext.getCurrentActivity();
        if (currentActivity == null) {
            callback.invoke(getErrorMap(errOthers, "Activity error"));
            return;
        }

        this.callback = callback;
        this.options = new Options(options);

        int requestCode;
        Intent libraryIntent;
        requestCode = REQUEST_LAUNCH_LIBRARY;

        int selectionLimit = this.options.selectionLimit;
        boolean isSingleSelect = selectionLimit == 1;
        boolean isPhoto = this.options.mediaType.equals(mediaTypePhoto);
        boolean isVideo = this.options.mediaType.equals(mediaTypeVideo);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (isSingleSelect && (isPhoto || isVideo)) {
                libraryIntent = new Intent(Intent.ACTION_PICK);
            } else {
                libraryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                libraryIntent.addCategory(Intent.CATEGORY_OPENABLE);
            }
        } else {
            libraryIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        }

        if (!isSingleSelect) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                libraryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            } else {
                if (selectionLimit != 1) {
                    int maxNum = selectionLimit;
                    if (selectionLimit == 0) maxNum = MediaStore.getPickImagesMaxLimit();
                    libraryIntent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxNum);
                }
            }
        }

        if (isPhoto) {
            libraryIntent.setType("image/*");
        } else if (isVideo) {
            libraryIntent.setType("video/*");
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            libraryIntent.setType("*/*");
            libraryIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        }

        try {
            currentActivity.startActivityForResult(libraryIntent, requestCode);
        } catch (ActivityNotFoundException e) {
            callback.invoke(getErrorMap(errOthers, e.getMessage()));
            this.callback = null;
        }
    }

    void onAssetsObtained(List<Uri> fileUris) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            try {
                callback.invoke(getResponseMap(fileUris, options, reactContext));
            } catch (RuntimeException exception) {
                callback.invoke(getErrorMap(errOthers, exception.getMessage()));
            } finally {
                callback = null;
            }
        });
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

        if (!isValidRequestCode(requestCode) || (this.callback == null)) {
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            try {
                callback.invoke(getCancelMap());
                return;
            } catch (RuntimeException exception) {
                callback.invoke(getErrorMap(errOthers, exception.getMessage()));
            } finally {
                callback = null;
            }
        }

        switch (requestCode) {
            case REQUEST_LAUNCH_LIBRARY:
                onAssetsObtained(collectUrisFromData(data));
                break;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
    }
}
