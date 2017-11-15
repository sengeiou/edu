package com.ubt.alpha1e.ui.helper;

import android.content.Context;
import android.graphics.Bitmap;

import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.event.NetworkEvent;
import com.ubt.alpha1e.ui.BaseActivity;

import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;

import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;

public class NetworkHelper extends BaseHelper {

    private static final String TAG = "NetworkHelper";

    public NetworkHelper(Context context) {
        super(context);
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);

        if(cmd == ConstValue.DV_DO_NETWORK_CONNECT){

            UbtLog.d(TAG,"status == " + param[0]);

            NetworkEvent event = new NetworkEvent(NetworkEvent.Event.DO_CONNECT_WIFI);
            event.setConnectStatus(param[0]);
            EventBus.getDefault().post(event);

        }else if(cmd == ConstValue.DV_READ_NETWORK_STATUS){
            String networkInfoJson = BluetoothParamUtil.bytesToString(param);

            UbtLog.d(TAG,"cmd = " + cmd + "    networkInfoJson = " + networkInfoJson);
            NetworkInfo networkInfo = GsonImpl.get().toObject(networkInfoJson,NetworkInfo.class);

            NetworkEvent event = new NetworkEvent(NetworkEvent.Event.NETWORK_STATUS);
            event.setConnectWifiName(networkInfo.name);
            EventBus.getDefault().post(event);
        }
    }

    /**
     * 读取 1E 联网状态
     */
    public void readNetworkStatus(){
        UbtLog.d(TAG,"--readNetworkStatus-- " );
        doSendComm(ConstValue.DV_READ_NETWORK_STATUS, null);
    }

    /**
     * 机器人联网
     * @param netName 连接网络名称
     * @param netPassword 连接网络密码
     */
    public void doConnectNetwork(String netName,String netPassword){

        String params = BluetoothParamUtil.paramsToJsonString(new String[]{ netName,netPassword }, ConstValue.DV_DO_NETWORK_CONNECT);
        UbtLog.d(TAG,"params = " + params);
        doSendComm(ConstValue.DV_DO_NETWORK_CONNECT, BluetoothParamUtil.stringToBytes(params));
    }


    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {

    }

    public void DistoryHelper() {
        super.UnRegisterHelper();
    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {

    }
}
