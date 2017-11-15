package com.ubt.alpha1e.userinfo.cleancache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.Formatter;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.IFileListener;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CleanCachePresenter extends BasePresenterImpl<CleanCacheContract.View> implements CleanCacheContract.Presenter,IFileListener{

    @Override
    public void doClearCache() {
        //清除短缓存数据
        ((AlphaApplication) mView.getContext().getApplicationContext()).clearCacheData();

        FileTools.clearCacheSize(this);
    }

    @Override
    public void doReadCacheSize() {
        FileTools.readCacheSize(this);
    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result, boolean result_state, long request_code) {

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result, long request_code) {

    }

    @Override
    public void onWriteDataFinish(long requestCode, FileTools.State state) {

    }

    @Override
    public void onReadCacheSize(int size) {
        long totalSize = FileTools.getInternalMemoryLSize(mView.getContext());
        mView.onReadCacheSize(size,totalSize);
    }

    @Override
    public void onClearCache() {
        mView.onClearCache();
    }
}
