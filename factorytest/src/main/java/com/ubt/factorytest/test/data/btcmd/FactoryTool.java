package com.ubt.factorytest.test.data.btcmd;

import android.util.Log;

import com.ubt.factorytest.bluetooth.ubtbtprotocol.InvalidPacketException;
import com.ubt.factorytest.bluetooth.ubtbtprotocol.UbtBTProtocol;
import com.ubt.factorytest.test.data.DataServer;
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
     * @param dataServer
     * @param cmd
     * @return 返回RecyclerView中需要更新参数的ITEMID
     */
    public int parseBTCmd(DataServer dataServer, final byte[] cmd){
        boolean isOK = false;
        int cmdID = 0;

        try {
            UbtBTProtocol data = new UbtBTProtocol(cmd);
            if(data.getCmd() == BaseBTReq.HEART_CMD){//心跳命令 不处理
                return -1;
            }
            cmdID = translateBTCMDID2Item(data.getCmd());
            isOK = updateDataResult(dataServer, data, cmdID);
        } catch (InvalidPacketException e) {
            e.printStackTrace();
            return -1;
        }
        if(isOK){
            return getPosition(dataServer, cmdID);
        }else{
            return -1;
        }

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
    private boolean updateDataResult(DataServer dataServer, UbtBTProtocol data, int itemID){
        int position;
        position = getPosition(dataServer, itemID);
        if(position >= 0) {
            String result = "";
            switch (itemID){
                case TestClickEntity.TEST_ITEM_START_TIME:
                    result = new String(data.getParam())+"秒";
                    break;
                case TestClickEntity.TEST_ITEM_ELECTRICCHARGE:
                    byte[] power = data.getParam();
                    result = power[3]+"";   //电量只需要第三位，电量值
                    break;
                case TestClickEntity.TEST_ITEM_PIRTEST:
                    //红外距离是INT类型
                    result = String.valueOf(data.getParam()[0]&0xff);
                    break;
                case TestClickEntity.TEST_ITEM_INTERRUOTTEST:
                    result = "触发了拍头事件";
                    dataServer.setDataisPass(position, true);
                    break;
                case TestClickEntity.TEST_ITEM_WAKEUPTEST:
                    result = "触发了唤醒事件";
                    dataServer.setDataisPass(position, true);
                    break;
                case TestClickEntity.TEST_ITEM_GSENSIRTEST:
                    try {
                        JSONObject jsonObject = new JSONObject(new String(data.getParam()));
                        JSONObject jsonResult = new JSONObject();
                        jsonResult.put("front", jsonObject.getString("front"));
                        jsonResult.put("left", jsonObject.getString("left"));
                        result = jsonResult.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    result = new String(data.getParam());
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
}
