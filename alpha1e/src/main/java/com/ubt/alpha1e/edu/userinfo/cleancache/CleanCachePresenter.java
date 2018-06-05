package com.ubt.alpha1e.edu.userinfo.cleancache;

import android.graphics.Bitmap;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.IFileListener;
import com.ubt.alpha1e.edu.mvp.BasePresenterImpl;

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
