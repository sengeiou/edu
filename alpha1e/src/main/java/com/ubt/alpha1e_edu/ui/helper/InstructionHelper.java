package com.ubt.alpha1e_edu.ui.helper;


import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.utils.BluetoothParamUtil;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;


public class InstructionHelper extends BaseHelper {

    private static final String TAG = "InstructionHelper";

    private static final int MSG_DO_SEND_HANDSHAKE = 1007;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == MSG_DO_SEND_HANDSHAKE){
                String mac = (String) msg.obj;
                doSendComm(mac, ConstValue.DV_HANDSHAKE, null);
            }
        }
    };

    public InstructionHelper(BaseActivity baseActivity) {
        super(baseActivity);
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        UbtLog.d(TAG, "mac = " + mac + "    cmd = " + cmd);
        if(cmd == ConstValue.DV_HANDSHAKE_B_SEVEN){

            Message msg = new Message();
            msg.what = MSG_DO_SEND_HANDSHAKE;
            msg.obj = mac;
            mHandler.sendMessage(msg);

        }
    }

    /**
     * 机器人联网
     * @param netName 连接网络名称
     * @param netPassword 连接网络密码
     */
    public void doConnectNetwork(String netName,String netPassword){

        String params = BluetoothParamUtil.paramsToJsonString(new String[]{ netName,netPassword }, ConstValue.DV_DO_NETWORK_CONNECT);
        UbtLog.d(TAG,"params = " + params);
        //doSendComm(ConstValue.DV_DO_NETWORK_CONNECT,BluetoothParamUtil.stringToBytes(params));
    }


    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {

    }


    @Override
    public void onDeviceDisConnected(String mac) {

    }

    @Override
    public void DistoryHelper() {
        super.UnRegisterHelper();
    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {

    }
}
