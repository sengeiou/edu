package com.ubt.alpha1e_edu.business;

import android.app.Activity;

/**
 * Created by Administrator on 2016/5/16.
 */
public interface  MyActionsRecyclerListener {

    void initRecyclerViews();

    void updateViews(boolean isLogin,int hasData);

    void firstLoadData();

    void initDatas(Activity activity);

    void clearDatas();

}
