package com.ubt.alpha1e.business;

/**
 * Created by Administrator on 2016/5/9.
 */
public interface IHtsHelperListener {
    void onHtsWriteFinish(boolean isSuccess);

    void onGetNewActionInfoFinish(boolean isSuccess);
}

