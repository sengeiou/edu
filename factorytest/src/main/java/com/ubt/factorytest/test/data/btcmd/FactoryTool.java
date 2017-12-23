package com.ubt.factorytest.test.data.btcmd;

import android.util.Log;

import com.ubt.factorytest.bluetooth.ubtbtprotocol.ProtocolPacket;
import com.ubt.factorytest.test.data.DataServer;
import com.ubt.factorytest.test.data.IFactoryListener;
import com.ubt.factorytest.test.recycleview.TestClickEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/21 18:29
 * @描述:
 */

public class FactoryTool {
    private static final String TAG = "FactoryTool";
    private static FactoryTool instance;
    private int pressCnt = 0;  //拍头计数
    private int currentVol = 0x7F; //机器人当前音量
    private IFactoryListener listener;

    private FactoryTool(){

    }

    public static FactoryTool getInstance(){
        if(instance == null){
            synchronized (FactoryTool.class){
                if(instance == null){
                    instance = new FactoryTool();
                }
            }
        }

        return instance;
    }

    public void init(){
        pressCnt = 0;
    }

    private void setFactoryListener(IFactoryListener factoryListener){
        listener = factoryListener;
    }
    /**
     * 获取请求命令的命令数组 所有请求命令需要添加测试数据
     * @param item
     * @return
     */
    public byte[] getReqBytes(TestClickEntity item){
        BaseBTReq req = null;

        switch (item.getTestID()){
            case TestClickEntity.TEST_ITEM_START_TIME:
                req = new StartTimeReq();
                break;
            case TestClickEntity.TEST_ITEM_SOFT_VERSION:
                req = new SoftVersionReq();
                break;
            case TestClickEntity.TEST_ITEM_HARDWARE_VER:
                req = new HardwareVerReq();
                break;
            case TestClickEntity.TEST_ITEM_ELECTRICCHARGE:
                req = new ElectCharge();
                break;
            case TestClickEntity.TEST_ITEM_LEDTEST:
                req = new TestLed();
                break;
            case TestClickEntity.TEST_ITEM_BTSENSITIVITY:
                break;
            case TestClickEntity.TEST_ITEM_WAKEUPTEST:
                break;
            case TestClickEntity.TEST_ITEM_INTERRUOTTEST:
                break;
            case TestClickEntity.TEST_ITEM_SPEAKERTEST:
                req = new SpeakerTestReq();
                break;
            case TestClickEntity.TEST_ITEM_RECORD_TEST:
                req = new MicTestReq(MicTestReq.START_RECORDER);
                break;
            case TestClickEntity.TEST_ITEM_PIRTEST:
                req = new PirTest(PirTest.START_PIR);
                break;
            case TestClickEntity.TEST_ITEM_GSENSIRTEST:
                req = new GsensirTest(GsensirTest.START_GSENSIR);
                break;
            case TestClickEntity.TEST_ITEM_WIFITEST:
                break;
            case TestClickEntity.TEST_ITEM_ROBOTRESET:
                req = new ResetRobot();
                break;
            case TestClickEntity.TEST_ITEM_SAVETESTPROFILE:
                break;
            case TestClickEntity.TEST_ITEM_AGEING_TEST:
               // req = new PlayAction("action/course/motion/" + "胜利.hts");
                req = new PlayAction("action/my creation/" + "Action-老化测试动作简版.hts");

                break;

            default:
                break;
        }
        if(req != null){
            return req.toByteArray();
        }else {
            Log.e(TAG,"getReqBytes 无效命令:"+item.getTestID());
            return null;
        }
    }

    /**
     * 解析接收到的蓝牙命令
     * @param cmd
     * @factoryListener 数据包解析回调
     */
    public void  parseBTCmd(final byte[] cmd, IFactoryListener factoryListener){
        formatBTData(cmd, factoryListener);
    }

    /*public void parseBTCmd(final byte[] cmd){
        boolean isOK = false;
        int itemID = 0;
        try {
            ProtocolPacket data = new ProtocolPacket();
            formatBTData(cmd, null);
            byte btCmd = data.getmCmd();
            Log.i(TAG,"btCmd："+ ByteHexHelper.byteToHexString(btCmd));
            switch (btCmd){
                case (byte)0x80: //获取到动作
                    if(listener != null){
                        listener.onActionList(new String(data.getmParam()));
                    }
                    break;
                case BaseBTReq.CONNECT_WIFI:
                    if(listener != null){
                        listener.onWifiConnectState(ByteHexHelper.bytesToHexString(data.getmParam()).trim());
                    }
                    break;
                case BaseBTReq.HEART_CMD:
                    break;
                case BaseBTReq.READ_DEV_STATUS:
                    parseDevStatus(data.getmParam());
                    break;
                case BaseBTReq.DV_ACTION_FINISH:
                    Log.d("DV_ACTION_FINISH","播放动作结束");
                    break;
                default:
                    if(dataServer == null){
                        return -1;
                    }
                    itemID = translateBTCMDID2Item(btCmd);
                    isOK = updateDataResult(dataServer, data, itemID);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        if(isOK){
            return getPosition(dataServer, itemID);
        }else{
            return -1;
        }
    }*/

    public int getItemPositon(DataServer dataServer, ProtocolPacket packet){
        boolean isOK;
        int itemID;
        int pos;

        itemID = translateBTCMDID2Item(packet.getmCmd());
        isOK = updateDataResult(dataServer, packet, itemID);
        if(isOK){
            pos = getPosition(dataServer, itemID);
        }else{
            pos = -1;
        }
        return pos;
    }
    /**
     * 转换蓝牙命令ID到RecyclerView itemID 需要检测返回值的项，才需要转换ID
     * @param cmdID
     * @return
     */
    private int translateBTCMDID2Item(byte cmdID){
        int itemID = 0;

        switch (cmdID){
            case BaseBTReq.START_TIME:
                itemID = TestClickEntity.TEST_ITEM_START_TIME;
                break;
            case BaseBTReq.SOFT_VERSION:
                itemID = TestClickEntity.TEST_ITEM_SOFT_VERSION;
                break;
            case BaseBTReq.HARDWARE_VER:
                itemID = TestClickEntity.TEST_ITEM_HARDWARE_VER;
                break;
            case BaseBTReq.ELECTRICCHARGE:
                itemID = TestClickEntity.TEST_ITEM_ELECTRICCHARGE;
                break;
            case BaseBTReq.TEST_LED:
                itemID = TestClickEntity.TEST_ITEM_LEDTEST;
                break;
            case BaseBTReq.RESET_ROBOT:
                itemID = TestClickEntity.TEST_ITEM_ROBOTRESET;
                break;
            case BaseBTReq.PIR_TEST:
                itemID = TestClickEntity.TEST_ITEM_PIRTEST;
                break;
            case BaseBTReq.GSENSIR_TEST:
                itemID = TestClickEntity.TEST_ITEM_GSENSIRTEST;
                break;
            case BaseBTReq.PRESSHEAD_UP:
                itemID = TestClickEntity.TEST_ITEM_INTERRUOTTEST;
                break;
            case BaseBTReq.WAKEUP_UP:
                itemID = TestClickEntity.TEST_ITEM_WAKEUPTEST;
                break;
            case BaseBTReq.DV_ACTION_FINISH:
                itemID = TestClickEntity.TEST_ITEM_AGEING_TEST;
                break;
            default:
                Log.e(TAG,"translateBTCMDID2Item 未找到itemID  cmdID="+cmdID);
                break;
        }

        return itemID;
    }


    /**
     * 更新Recycler对应itemID位置的 结果值
     * @param dataServer
     * @param data
     * @param itemID
     * @return
     */
    private boolean updateDataResult(DataServer dataServer, ProtocolPacket data, int itemID){
        int position;
        position = getPosition(dataServer, itemID);
        if(position >= 0) {
            String result = "";
            switch (itemID){
                case TestClickEntity.TEST_ITEM_START_TIME:
                    String time = new String(data.getmParam());
                    time = filterTime(time);
                    result = time+"秒";
                    if(Integer.valueOf(time) > 30){
                        dataServer.setDataisPass(position, false);
                    }
                    break;
                case TestClickEntity.TEST_ITEM_ELECTRICCHARGE:
                    byte[] power = data.getmParam();
                    result = power[3]+"";   //电量只需要第三位，电量值
                    break;
                case TestClickEntity.TEST_ITEM_PIRTEST:
                    //红外距离是INT类型
                    result = String.valueOf(data.getmParam()[0]&0xff);
                    break;
                case TestClickEntity.TEST_ITEM_INTERRUOTTEST:
                    pressCnt += 1;
                    result = "拍头次数:"+pressCnt;
                    if(pressCnt < 3) {
                        dataServer.setDataisPass(position, false);
                    }else if(pressCnt == 3){
                        dataServer.setDataisPass(position, true);
                    }
                    break;
                case TestClickEntity.TEST_ITEM_WAKEUPTEST:
                    result = "触发了唤醒事件";
                    dataServer.setDataisPass(position, true);
                    break;
                case TestClickEntity.TEST_ITEM_GSENSIRTEST:
                    try {
                        JSONObject jsonObject = new JSONObject(new String(data.getmParam()));
                        JSONObject jsonResult = new JSONObject();
                        jsonResult.put("front", jsonObject.getString("front"));
                        jsonResult.put("left", jsonObject.getString("left"));
                        result = jsonResult.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    result = new String(data.getmParam());
                    break;
            }
            dataServer.setDataResult(position, result);
            return true;
        }else{
            Log.e(TAG,"updateDataResult 错误position="+position+"  itemID="+itemID);
            return false;
        }
    }

    /**
     *  获取指定 itemID在recylerView 缓存数据中的位置
     * @param dataServer
     * @param itemID
     * @return
     */
    private int getPosition(DataServer dataServer, int itemID){
        int position;
        List<TestClickEntity> dataCache =  dataServer.getDataCache();
        int dataSize = dataCache.size();

        for(position = 0; position < dataCache.size(); position++){
            if(dataCache.get(position).getTestID() == itemID){
                break;
            }
        }
        if(dataSize == position){
            position = -1;
        }
        return position;
    }

    /**
     *解析机器人状态
     * @param status
     */
    public void parseDevStatus(byte[] status){
        if(status[0] == 0x02){
            currentVol = status[1];
            Log.i(TAG,"当前电量:"+currentVol);
        }
    }

    public int getCurrentVol() {
        return currentVol;
    }

    public void setCurrentVol(int vol) {
         currentVol = vol;
    }

    private String filterTime(String time){
        StringBuffer newStr = new StringBuffer();
        for(int i = 0; i < time.length(); i++){
            char c = time.charAt(i);
            if(c >= 0x30  && c <= 0x39){
                newStr.append(c);
            }
        }

        return newStr.toString();
    }

    private void formatBTData(byte[] data, IFactoryListener factoryListener){
        ProtocolPacket pack = new ProtocolPacket();
        for (int i = 0; i < data.length; i++) {
            if (pack.setData_(data[i])) {
                // 一帧数据接收完成
                pack.setmParamLen(pack.getmParam().length);
                if(factoryListener != null){ //package返回本地接口
                    factoryListener.onProtocolPacket(pack);
                }
            }
        }
    }
}
