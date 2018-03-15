package com.ubt.alpha1e.course.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.ubt.alpha1e.course.event.PrincipleEvent;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/11/20.
 */

public class PrincipleHelper extends BaseHelper {

    private static final String TAG = PrincipleHelper.class.getSimpleName();

    public PrincipleHelper(Context context) {
        super(context);
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);

        //UbtLog.d(TAG,"cmd = " + cmd + "    = " + param[0]);
        if(cmd == ConstValue.DV_SET_PLAY_SOUND){
            UbtLog.d(TAG,"DV_SET_PLAY_SOUND cmd = " + cmd + "    = " + param[0]);
            PrincipleEvent playSoundEvent = new PrincipleEvent(PrincipleEvent.Event.PLAY_SOUND);
            playSoundEvent.setStatus(param[0]);
            EventBus.getDefault().post(playSoundEvent);
        }else if(cmd == ConstValue.CTRL_ONE_ENGINE){
            UbtLog.d("PrincipleHelper", ByteHexHelper.bytesToHexString(param));

        }else if(cmd == ConstValue.CTRL_ONE_ENGINE_ON){
            UbtLog.d("PrincipleHelper", ByteHexHelper.bytesToHexString(param));
        }else if(cmd == ConstValue.DV_READ_INFRARED_DISTANCE){
            UbtLog.d("PrincipleHelper", "cmd" + ConstValue.DV_READ_INFRARED_DISTANCE  + " param = " +  ByteHexHelper.bytesToHexString(param));
            PrincipleEvent playSoundEvent = new PrincipleEvent(PrincipleEvent.Event.CALL_GET_INFRARED_DISTANCE);
            playSoundEvent.setInfraredDistance(param[0]);
            EventBus.getDefault().post(playSoundEvent);
        }else if(cmd == ConstValue.DV_CAN_CLICK_HEAD){
            UbtLog.d("PrincipleHelper", "cmd" + ConstValue.DV_CAN_CLICK_HEAD  + " param = " +  ByteHexHelper.bytesToHexString(param));

            PrincipleEvent playSoundEvent = new PrincipleEvent(PrincipleEvent.Event.CALL_CLICK_HEAD);
            playSoundEvent.setStatus(param[0]);
            EventBus.getDefault().post(playSoundEvent);
        }else if(cmd == ConstValue.DV_VOICE_WAIT){
            UbtLog.d(TAG,"DV_VOICE_WAIT = " + cmd);
            PrincipleEvent playSoundEvent = new PrincipleEvent(PrincipleEvent.Event.VOICE_WAIT);
            playSoundEvent.setStatus(param[0]);
            EventBus.getDefault().post(playSoundEvent);
        }else if(cmd == ConstValue.DV_PLAYACTION){
            UbtLog.d(TAG,"--DV_PLAYACTION--" + param[0]);
        }else if (cmd == ConstValue.DV_ACTION_FINISH){
            String finishPlayActionName = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG, "finishPlayActionName = " + finishPlayActionName);

            if(!TextUtils.isEmpty(finishPlayActionName) && finishPlayActionName.contains("初始化")){
                //return;
            }else {
                PrincipleEvent playSoundEvent = new PrincipleEvent(PrincipleEvent.Event.PLAY_ACTION_FINISH);
                playSoundEvent.setStatus(param[0]);
                EventBus.getDefault().post(playSoundEvent);
            }
        }else if(cmd == ConstValue.DV_TAP_HEAD){
            UbtLog.d(TAG,"TAP_HEAD = " + cmd);
            PrincipleEvent tapHeadEvent = new PrincipleEvent(PrincipleEvent.Event.TAP_HEAD);
            EventBus.getDefault().post(tapHeadEvent);
        }else if(cmd == ConstValue.DV_CONTROL_ENGINE_COMMAND){
            UbtLog.d(TAG,"cmd = " + cmd + "    = " + param[0]);
        }

    }

    /**
     * 播放音效
     * @param params
     */

    public void playSoundAudio(String params){
        UbtLog.d(TAG,"params = " + params);
        doSendComm(ConstValue.DV_SET_PLAY_SOUND, BluetoothParamUtil.stringToBytes(params));
    }

    /**
     * 停止音效
     */
    public void stopPlayAudio() {
        doSendComm(ConstValue.DV_SET_STOP_VOICE, null);
    }

    private void doOnOnePower(int id){
        byte[] params = new byte[5];
        //DefaultServosInitAngle=1==93,2==20,3==66,4==86,5==156,6==127,7==90,8==74,9==95,10==104,11==89,12==89,13==104,14==81,15==76,16==89
        params[0] = ByteHexHelper.intToHexByte(id);
        if(id == 1){
            params[1] = ByteHexHelper.intToHexByte(93);
        }else if(id == 2){
            params[1] = ByteHexHelper.intToHexByte(20);
        }else if(id == 3){
            params[1] = ByteHexHelper.intToHexByte(66);
        }else if(id == 4){
            params[1] = ByteHexHelper.intToHexByte(86);
        }else if(id == 5){
            params[1] = ByteHexHelper.intToHexByte(156);
        }else if(id == 6){
            params[1] = ByteHexHelper.intToHexByte(127);
        }else if(id == 7){
            params[1] = ByteHexHelper.intToHexByte(90);
        }else if(id == 8){
            params[1] = ByteHexHelper.intToHexByte(74);
        }else if(id == 9){
            params[1] = ByteHexHelper.intToHexByte(95);
        }else if(id == 10){
            params[1] = ByteHexHelper.intToHexByte(104);
        }else if(id == 11){
            params[1] = ByteHexHelper.intToHexByte(89);
        }else if(id == 12){
            params[1] = ByteHexHelper.intToHexByte(89);
        }else if(id == 13){
            params[1] = ByteHexHelper.intToHexByte(104);
        }else if(id == 14){
            params[1] = ByteHexHelper.intToHexByte(81);
        }else if(id == 15){
            params[1] = ByteHexHelper.intToHexByte(76);
        }else if(id == 16){
            params[1] = ByteHexHelper.intToHexByte(89);
        }else {
            params[1] = ByteHexHelper.intToHexByte(89);
        }
        params[2] = ByteHexHelper.intToHexByte(100);
        params[3] = ByteHexHelper.intToHexByte(0);
        params[4] = ByteHexHelper.intToHexByte(100);
        doSendComm(ConstValue.CTRL_ONE_ENGINE_ON, params);
    }

    private void doLostOnePower(int id) {
        byte[] params = new byte[1];
        params[0] = ByteHexHelper.intToHexByte(id);
        doSendComm(ConstValue.CTRL_ONE_ENGINE, params);
    }

    public void doLostLeftHand(){
        doLostOnePower(1);
        doLostOnePower(2);
        doLostOnePower(3);
    }

    public void doLostRightHand() {
        doLostOnePower(4);
        doLostOnePower(5);
        doLostOnePower(6);
    }

    public void doLostLeftFoot() {
        doLostOnePower(7);
        doLostOnePower(8);
        doLostOnePower(9);
        doLostOnePower(10);
        doLostOnePower(11);

    }

    public void doLostRightFoot() {
        doLostOnePower(12);
        doLostOnePower(13);
        doLostOnePower(14);
        doLostOnePower(15);
        doLostOnePower(16);
    }

    public void doOnLeftHand(){
        doOnOnePower(1);
        doOnOnePower(2);
        doOnOnePower(3);
    }

    public void doOnRightHand() {
        doOnOnePower(4);
        doOnOnePower(5);
        doOnOnePower(6);
    }

    public void doOnLeftFoot() {
        doOnOnePower(7);
        doOnOnePower(8);
        doOnOnePower(9);
        doOnOnePower(10);
        doOnOnePower(11);

    }

    public void doOnRightFoot() {
        doOnOnePower(12);
        doOnOnePower(13);
        doOnOnePower(14);
        doOnOnePower(15);
        doOnOnePower(16);
    }

    public void doOnFoot(){
        doOnOnePower(11);
        doOnOnePower(16);
        doOnOnePower(10);
        doOnOnePower(15);
        doOnOnePower(9);
        doOnOnePower(14);
        doOnOnePower(8);
        doOnOnePower(13);
        doOnOnePower(7);
        doOnOnePower(12);
    }

    /**
     * 机器人控制舵机命令（目前只有[1,3],[1,4]）
     * @param controlType 1.掉电；2.上电
     * @param position 1.左手；2.右手 3.双脚 4.除8号、13号舵机外，其他全部掉电
     */
    public void doControlEngine(int controlType, int position){
        byte[] params = new byte[2];
        params[0] = ByteHexHelper.intToHexByte(controlType);
        params[1] = ByteHexHelper.intToHexByte(position);
        doSendComm(ConstValue.DV_CONTROL_ENGINE_COMMAND, params);
    }

    /**
     * 读取红外传感器距离
     * @param status 01表示进入 ，00表示离开
     */
    public void doEnterCourse(byte status){
        UbtLog.d(TAG, "doReadInfraredSensor status:" + status);
        byte[] params = new byte[1];
        params[0] = status;
        doSendComm(ConstValue.DV_ENTER_COURSE, params);
    }

    /**
     * 读取红外传感器距离
     * @param status 01表示开启 ，00表示关闭
     */
    public void doReadInfraredSensor(byte status){
        UbtLog.d(TAG, "doReadInfraredSensor status:" + status);
        byte[] params = new byte[1];
        params[0] = status;
        doSendComm(ConstValue.DV_READ_INFRARED_DISTANCE, params);
    }

    /**
     * 启用禁用拍头课程
     * @param status 01表示开启 ，00表示关闭
     */
    public void doReadHeadClick(byte status){
        UbtLog.d(TAG, "doReadInfraredSensor status:" + status);
        byte[] params = new byte[1];
        params[0] = status;
        doSendComm(ConstValue.DV_CAN_CLICK_HEAD, params);
    }

    public void doLostPower() {
        doSendComm(ConstValue.DV_DIAODIAN, null);
    }

    public void doInit(){
        String initFile = "action/初始化.hts";
        doSendComm(ConstValue.DV_PLAYACTION,BluetoothParamUtil.stringToBytes(initFile));
    }

    public void playFile(String fileName){
        String filePath = "action/course/principle/" + fileName;
        UbtLog.d(TAG,"filePath = " + filePath);
        doSendComm(ConstValue.DV_PLAYACTION, BluetoothParamUtil.stringToBytes(filePath));
    }

    @Override
    public void onDeviceDisConnected(String mac) {
        UbtLog.d(TAG,"--onDeviceDisConnected--" + mac );
        /*PrincipleEvent event = new PrincipleEvent(PrincipleEvent.Event.DISCONNECTED);
        EventBus.getDefault().post(event);*/
        super.onDeviceDisConnected(mac);
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
