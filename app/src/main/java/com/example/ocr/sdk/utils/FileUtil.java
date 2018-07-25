/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.example.ocr.sdk.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    private static final String DEFAULT_DISK_CACHE_DIR = "ocr";

    public static File getSaveFile(Context context) {
////        地址内容手机目录找不到
//        File file = new File(context.getFilesDir(), "pic.jpg");
        File imageCacheDir = getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR);
        File file = null;
        try {
            // /storage/emulated/0/Android/data/com.example.ocr/cache/ocr/ocr_985486902.jpg
            file = File.createTempFile("ocr_", ".jpg", imageCacheDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     *
     * @param context   A context.
     * @param cacheName The name of the subdirectory in which to store the cache.
     */
    @Nullable
    public static File getImageCacheDir(Context context, String cacheName) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        return null;
    }

}
