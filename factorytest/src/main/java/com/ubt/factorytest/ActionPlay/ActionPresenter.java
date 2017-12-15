package com.ubt.factorytest.ActionPlay;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.ubt.factorytest.ActionPlay.recycleview.ActionBean;
import com.ubt.factorytest.bluetooth.bluetoothLib.BluetoothController;
import com.ubt.factorytest.bluetooth.bluetoothLib.base.BluetoothListenerAdapter;
import com.ubt.factorytest.bluetooth.bluetoothLib.base.BluetoothState;
import com.ubt.factorytest.bluetooth.ubtbtprotocol.ProtocolPacket;
import com.ubt.factorytest.test.data.DataServer;
import com.ubt.factorytest.test.data.IFactoryListener;
import com.ubt.factorytest.test.data.btcmd.FactoryTool;
import com.ubt.factorytest.test.data.btcmd.GetActionList;
import com.ubt.factorytest.test.data.btcmd.PlayAction;
import com.ubt.factorytest.utils.ByteHexHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/14 19:16
 * @描述:
 */

public class ActionPresenter implements ActionContract.Presenter, IFactoryListener{
    private static final String TAG = "ActionPresenter";
    private ActionContract.View mView;
    private DataServer mDataServer;
    private BluetoothController mBluetoothController;
    private int btState = BluetoothState.STATE_CONNECTED;

    private List<ActionBean> mActionList = new ArrayList() ;

    public ActionPresenter(ActionContract.View view) {
        FactoryTool.getInstance().init();
        mView = view;
//        mDataServer = dataServer;
        initBT();
        mView.setPresenter(this);
        mBluetoothController.write(new GetActionList("action").toByteArray());
    }

    private void initBT(){
        mBluetoothController = BluetoothController.getInstance();
        mBluetoothController.setBluetoothListener(mBTListener);
    }

    @Override
    public void start() {

    }

    private BluetoothListenerAdapter mBTListener = new BluetoothListenerAdapter() {

        @Override
        public void onReadData(BluetoothDevice device, byte[] data) {
            Log.d(TAG, "zz ActionPresenter onReadData data:" + ByteHexHelper.bytesToHexString(data));
            FactoryTool.getInstance().parseBTCmd(data,ActionPresenter.this);
        }


        @Override
        public void onBluetoothServiceStateChanged(int state) {
            Log.i(TAG,"zz ActionPresenter onBluetoothServiceStateChanged state="+state);
            switch (state){
                case BluetoothState.STATE_CONNECTED:
                    break;
                case BluetoothState.STATE_CONNECTING:
                    break;
                case BluetoothState.STATE_DISCONNECTED:
                    if(btState == BluetoothState.STATE_CONNECTED) {
                        mView.btDisconnected();
                    }
                    break;
                default:
                    break;
            }

            btState = state;
        }

    };

    @Override
    public void onProtocolPacket(ProtocolPacket packet) {
        switch (packet.getmCmd()){
            case (byte)0x80:
                mActionList.add(new ActionBean(ActionBean.ACTION_ITEM_VIEW, new String(packet.getmParam())));
                mView.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public List<ActionBean> getActionList() {
        return mActionList;
    }

    @Override
    public void playAction(String actionName) {
        mBluetoothController.write(new PlayAction(actionName).toByteArray());
    }
}
