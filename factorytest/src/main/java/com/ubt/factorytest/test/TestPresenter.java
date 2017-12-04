package com.ubt.factorytest.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.ubt.factorytest.bluetooth.bluetoothLib.BluetoothController;
import com.ubt.factorytest.bluetooth.bluetoothLib.base.BluetoothListenerAdapter;
import com.ubt.factorytest.bluetooth.bluetoothLib.base.BluetoothState;
import com.ubt.factorytest.test.data.DataServer;
import com.ubt.factorytest.test.data.btcmd.FactoryTool;
import com.ubt.factorytest.test.data.btcmd.GsensirTest;
import com.ubt.factorytest.test.data.btcmd.HeartBeat;
import com.ubt.factorytest.test.data.btcmd.IntoFactoryTest;
import com.ubt.factorytest.test.data.btcmd.MicTestReq;
import com.ubt.factorytest.test.data.btcmd.PirTest;
import com.ubt.factorytest.test.recycleview.TestClickEntity;
import com.ubt.factorytest.utils.ByteHexHelper;
import com.ubt.factorytest.utils.ContextUtils;
import com.ubt.factorytest.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/16 17:56
 * @描述:
 */

public class TestPresenter implements TestContract.Presenter {
    private static final String TAG = "BTTestPresenter";
    private TestContract.View mView;
    private DataServer mDataServer;
    private BluetoothController mBluetoothController;
    private int btState = BluetoothState.STATE_CONNECTED;
    private String mBTMac;
    private byte[] mHeartBeat;
    private Timer mHeartTime;
    private boolean isPIRTesting = false;
    private boolean isGSensirTesting = false;

    public TestPresenter(TestContract.View view, DataServer dataServer) {
        mView = view;
        mDataServer = dataServer;
        initBT();
        mView.setPresenter(this);
    }

    private void initBT(){
        mBluetoothController = BluetoothController.getInstance();
        initBTListener();
        //与机器人握手
//        byte[] hw = {(byte) 0xFB, (byte) 0xBF, 0x06, 0x01, 0x00, 0x07, (byte) 0xED};
//        mBluetoothController.write(hw);
        mHeartTime = new Timer("heartBeat");
        mHeartBeat = new HeartBeat().toByteArray();
        startHeart();
        mBluetoothController.write(new IntoFactoryTest(IntoFactoryTest.START_TEST).toByteArray());
    }

    private BluetoothListenerAdapter mBTListener = new BluetoothListenerAdapter() {

        @Override
        public void onReadData(BluetoothDevice device, byte[] data) {
            Log.d(TAG, "zz TestPresenter onReadData data:" + ByteHexHelper.bytesToHexString(data));
            int itemID = FactoryTool.getInstance().parseBTCmd(mDataServer, data);
            if(itemID >= 0){
                mView.notifyItemChanged(itemID);
            }
        }

        @Override
        public void onActionDiscoveryStateChanged(String discoveryState) {
            Log.d(TAG, "zz TestPresenter onActionDiscoveryStateChanged:" + discoveryState);
            if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {

            } else if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {

            }
        }

        @Override
        public void onBluetoothServiceStateChanged(int state) {
            Log.i(TAG,"zz TestPresenter onBluetoothServiceStateChanged state="+state);
            switch (state){
                case BluetoothState.STATE_CONNECTED:
                    break;
                case BluetoothState.STATE_CONNECTING:
                    break;
                case BluetoothState.STATE_DISCONNECTED:
                    if(btState == BluetoothState.STATE_CONNECTED) {
                        stopHeart();
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
    public void start() {

    }


    @Override
    public List<TestClickEntity> getTestInitData() {
        return mDataServer.getTestInitData();
    }

    @Override
    public List<TestClickEntity> getDataCache() {
        return mDataServer.getDataCache();
    }

    @Override
    public void setDataResult(int poisition, String result) {
        mDataServer.setDataResult(poisition, result);
    }

    @Override
    public void setDataisPass(int poisition, boolean isPass) {
        mDataServer.setDataisPass(poisition, isPass);
    }

    @Override
    public void initBTListener() {
        mBluetoothController.setBluetoothListener(mBTListener);
    }

    @Override
    public void reInitBT() {
        if (mBluetoothController != null){
            mBluetoothController.unRegisterReciever();
        }
    }

    @Override
    public void disconnectBT() {
        stopHeart();
        mBluetoothController.disconnect();
    }

    @Override
    public void startTest(TestClickEntity item) {
        stopPIRorGsensir(item);
        if(item.getTestID() == TestClickEntity.TEST_ITEM_SAVETESTPROFILE){
            File filePath = FileUtils.getDiskCacheDir(ContextUtils.getContext());
            boolean isSaveOk = FileUtils.writeStringToFile(mDataServer.getDataCache().toString(),
                    filePath.getPath(),mBTMac+".txt",false);
            if(isSaveOk){
                mView.showToast("保存测试文件"+mBTMac+".txt 成功");
            }else{
                mView.showToast("保存测试文件失败!!!!");
            }

        }else {
            byte[] cmd = FactoryTool.getInstance().getReqBytes(item);
            if (cmd != null) {
                mBluetoothController.write(cmd);
            }
        }
    }

    @Override
    public void saveBTMac(String mac) {
        mBTMac = mac;
    }

    @Override
    public void setBTRSSI(String rssi) {
        List<TestClickEntity> data = getDataCache();
        for(TestClickEntity entity:data){
            if(entity.getTestID() == TestClickEntity.TEST_ITEM_BTSENSITIVITY){
                entity.setTestResult(rssi);
            }
        }
    }

    @Override
    public void stopRobotRecord() {
        mBluetoothController.write(new MicTestReq(MicTestReq.STOP_RECORDER).toByteArray());
    }

    private void startHeart(){
        mHeartTime.schedule(new TimerTask() {
            @Override
            public void run() {
                mBluetoothController.write(mHeartBeat);
            }
        }, 8000, 8000);
    }

    private void stopHeart(){
        if(mHeartTime != null){
            mHeartTime.cancel();
        }
    }

    /**
     *  切换命令后停止红外 和陀螺仪测试
     * @param item
     */
    private synchronized void stopPIRorGsensir(TestClickEntity item){
        if(item.getTestID() == TestClickEntity.TEST_ITEM_PIRTEST){
            if(!isPIRTesting){
                isPIRTesting = true;
            }
        }else if(item.getTestID() == TestClickEntity.TEST_ITEM_GSENSIRTEST){
            if(!isGSensirTesting){
                isGSensirTesting = true;
            }
        }else{
            if(isPIRTesting){
                isPIRTesting = false;
                mBluetoothController.write(new PirTest(PirTest.STOP_PIR).toByteArray());
                Log.d(TAG,"退出红外上报");
            }else if(isGSensirTesting){
                isGSensirTesting = false;
                mBluetoothController.write(new GsensirTest(GsensirTest.STOP_GSENSIR).toByteArray());
                Log.d(TAG,"退出陀螺仪上报");
            }
        }

    }
}
