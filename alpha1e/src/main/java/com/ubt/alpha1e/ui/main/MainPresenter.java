package com.ubt.alpha1e.ui.main;

import android.content.Context;
import android.util.Log;

import com.ubt.alpha1e.mvp.BasePresenterImpl;



public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter{
    private String TAG="MainPresenter";

    @Override
    public void launchActivity(String packageName) {
        Log.d(TAG,"launch the activity"+packageName);
    }

    @Override
    public void requestCartoonAction(String json) {

    }

    @Override
    public void requestCartoonText(String text) {

    }

    @Override
    public void requestBluetoothStatus(String status) {

    }

    @Override
    public void commandRobotAction(String json) {

    }
}
