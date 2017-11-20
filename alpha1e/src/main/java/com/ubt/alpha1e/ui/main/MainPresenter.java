package com.ubt.alpha1e.ui.main;

import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.json.JSONException;
import org.json.JSONObject;


public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter{
    private String TAG="MainPresenter";

    @Override
    public void requestCartoonAction(String json) {
          //  mView.showCartoonAction(1);
    }

    @Override
    public void requestCartoonText(String text) {

    }

    @Override
    public void requestBluetoothStatus(String status) {

    }

    @Override
    public void commandRobotAction(byte cmd, byte[] params) {
        MainUiBtHelper.getInstance(mView.getContext()).sendCommand(cmd,params);
    }

    @Override
    public void dealMessage(String json) {

    }
}
