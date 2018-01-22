package com.ubt.alpha1e.ui.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

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
        JSONObject mData=new JSONObject();
        try {
            mData.put("mac", mac);
            mData.put("cmd", cmd);
            mData.put("param", Base64.encodeToString(param,Base64.DEFAULT));
            mData.put("len", len);
            EventBus.getDefault().post(new MainActivity.MessageEvent(mData.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if(cmd == ConstValue.DV_READ_NETWORK_STATUS){
                String networkInfoJson = BluetoothParamUtil.bytesToString(param);
                UbtLog.d(TAG,"cmd = " + cmd + "    networkInfoJson = " + networkInfoJson);
                NetworkInfo networkInfo = GsonImpl.get().toObject(networkInfoJson,NetworkInfo.class);

                RobotEvent event = new RobotEvent(RobotEvent.Event.NETWORK_STATUS);
                event.setNetworkInfo(networkInfo);
                EventBus.getDefault().post(event);

            }
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
       UbtLog.d(TAG,"MAIN UI "+bsucceed +"mac address" +mac);

    }

    @Override
    public void DistoryHelper() {

    }

    /**
     * 读取 1E 联网状态
     */
    public void readNetworkStatus(){
        UbtLog.d(TAG,"--readNetworkStatus-- " );
        doSendComm(ConstValue.DV_READ_NETWORK_STATUS, null);
    }
}
