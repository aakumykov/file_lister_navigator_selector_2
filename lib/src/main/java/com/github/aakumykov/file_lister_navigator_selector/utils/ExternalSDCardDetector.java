package com.github.aakumykov.file_lister_navigator_selector.utils;

import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.lang.reflect.Method;

public class ExternalSDCardDetector {

    public static final String PATH_UNDETECTED_VALUE = null;

    private static final String TAG = ExternalSDCardDetector.class.getSimpleName();
    private static final String STORAGE_VOLUME_CLASS_NAME = "android.os.storage.StorageVolume";
    private static final String GET_VOLUME_LIST_METHOD_NAME = "getVolumeList";
    private static final String GET_DIRECTORY_METHOD_NAME = "getDirectory";
    private static final String GET_PATH_METHOD_NAME = "getPath";
    private static final String IS_REMOVABLE_METHOD_NAME = "isRemovable";


    @Nullable
    public static String getExternalCardDirectory(Context context) {

        final StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        try {
            final Class<?> storageVolumeClass = Class.forName(STORAGE_VOLUME_CLASS_NAME);

            final Method getVolumeListMethod = storageManager.getClass().getMethod(GET_VOLUME_LIST_METHOD_NAME);

            final Method getPathMethod = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ?
                    storageVolumeClass.getMethod(GET_DIRECTORY_METHOD_NAME)
                    : storageVolumeClass.getMethod(GET_PATH_METHOD_NAME);

            final Method isRemovableMethod = storageVolumeClass.getMethod(IS_REMOVABLE_METHOD_NAME);


            final StorageVolume[] result = (StorageVolume[]) getVolumeListMethod.invoke(storageManager);

            if (null == result)
                return PATH_UNDETECTED_VALUE;

            for (StorageVolume storageVolume : result) {

                final Object isRemovable = isRemovableMethod.invoke(storageVolume);

                if (isRemovable instanceof Boolean && (Boolean) isRemovable) {

                    final Object getPathResult = getPathMethod.invoke(storageVolume);

                    if (getPathResult instanceof File)
                        return ((File) getPathResult).getAbsolutePath();
                    else if (getPathResult instanceof String)
                        return (String) getPathResult;
                    else {
                        Log.e(TAG, "Unsupported result type of 'get path' method: "+getPathResult);
                        return PATH_UNDETECTED_VALUE;
                    }
                }
            }

            return PATH_UNDETECTED_VALUE;
        }
        catch (Throwable throwable) {
            Log.e(TAG, "ERROR: "+throwable.getMessage());
            return PATH_UNDETECTED_VALUE;
        }
    }
}