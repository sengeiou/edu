package com.ubt.alpha1e.edu.utils.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.ubt.alpha1e.edu.utils.log.MyLog;

public class ImageCache {

    private static ImageCache thiz;
    private LruCache<String, Bitmap> mMemoryCache;

    private ImageCache() {
    }

    public static ImageCache getInstances() {

        if (thiz == null) {
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int cacheSize = maxMemory / 6;
            thiz = new ImageCache();
            thiz.mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }

            };

        }
        return thiz;
    }

    public void clearCache() {

        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                mMemoryCache.evictAll();
            }
        }
    }

    public synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap) {

        MyLog.writeLog("图片缓存", "缓存：" + key + "|" + mMemoryCache.size() + "-->"
                + (bitmap == null ? 0 : bitmap.getByteCount()));

        if (bitmap == null) {
            try {
                removeImageCache(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (bitmap.getByteCount() / 1024 + mMemoryCache.size() + 5 > mMemoryCache
                .maxSize()) {
            clearCache();
        }

        if (mMemoryCache.get(key) == null) {
            if (key != null && bitmap != null)
                mMemoryCache.put(key, bitmap);
        }
        MyLog.writeLog("图片缓存", "缓存：" + key + "|" + mMemoryCache.size() + "-->"
                + mMemoryCache.maxSize());
    }

    public synchronized Bitmap getBitmapFromMemCache(String key) {
        MyLog.writeLog("图片缓存", "读取：" + key);
        Bitmap bm = mMemoryCache.get(key);
        if (key != null) {
            if (bm == null) {
                removeImageCache(key);
            } else {
                return bm;
            }
        }
        return null;
    }

    public synchronized void removeImageCache(String key) {
        if (key != null) {
            if (mMemoryCache != null) {
                Bitmap bm = mMemoryCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }

}
