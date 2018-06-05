package com.ubt.alpha1e.edu.ui.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.data.model.NetworkInfo;
import com.ubt.alpha1e.edu.event.RobotEvent;
import com.ubt.alpha1e.edu.utils.BluetoothParamUtil;
import com.ubt.alpha1e.edu.utils.GsonImpl;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/11/20.
 */

public class BluetoothStateHelper extends BaseHelper {

    private static BluetoothStateHelper mMainUiHelper;

    private static final String TAG = BluetoothStateHelper.class.getSimpleName();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

            }
        }
    };

    public BluetoothStateHelper(Context context) {
        super(context);
    }
    public  static BluetoothStateHelper getInstance(Context mContext){
        if(mMainUiHelper==null){
            mMainUiHelper=new BluetoothStateHelper(mContext);
        }
        return mMainUiHelper;

    }

    public void doCancelCoon() {
        ((AlphaApplication) mContext.getApplicationContext()).doLostConnect();
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
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
