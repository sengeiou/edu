package com.ubt.alpha1e.ui.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * Created by Administrator on 2017/11/20.
 */

public class MainUiBtHelper extends BaseHelper {

    private static  MainUiBtHelper mMainUiHelper;

    private static final String TAG = MainUiBtHelper.class.getSimpleName();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

            }
        }
    };

    public MainUiBtHelper(Context context) {
        super(context);
    }
    public  static  MainUiBtHelper getInstance(Context mContext){
        if(mMainUiHelper==null){
            mMainUiHelper=new MainUiBtHelper(mContext);
        }
        return mMainUiHelper;

    }
    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        try {
            UbtLog.d(TAG,"cmd = " + cmd + "  param = " + new String(param,"UTF-8"));
            //Execute on the mainActivity
        }catch (Exception ex){

        }
    }

    public void sendCommand(byte cmd,byte[] params){
        doSendComm(cmd,params);
    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {

    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {

    }

    @Override
    public void DistoryHelper() {

    }
}
