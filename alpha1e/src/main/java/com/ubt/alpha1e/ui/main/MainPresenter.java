package com.ubt.alpha1e.ui.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.squareup.haha.perflib.Main;
import com.ubt.alpha1e.blockly.ScanBluetoothActivity;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.ui.MyMainActivity;
import com.ubt.alpha1e.ui.helper.MainHelper;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.json.JSONException;
import org.json.JSONObject;


public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter{
    private String TAG="MainPresenter";

    @Override
    public void requestCartoonAction(String json) {
            mView.showCartoonAction("text");
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

    @Override
    public void dealMessage(String json) {
        try {
            JSONObject mObject = new JSONObject(json);
            mObject.getString("cmd");
            mObject.getInt("len");
            mObject.getString("param").getBytes();
            UbtLog.d(TAG,"CMD  "+ mObject.getString("cmd")+"len "+ mObject.getString("param").getBytes()[0]);
            if(Integer.parseInt(mObject.getString("cmd"))== ConstValue.DV_READ_BATTERY){
                mView.showCartoonAction("leg");
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}
