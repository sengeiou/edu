package com.ubt.alpha1e.blockly.sensor;

import android.graphics.Bitmap;

import com.ubt.alpha1e.event.BlocklyEvent;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;

import static com.ubt.alpha1e.utils.BluetoothParamUtil.stringToBytes;

/**
 * @author wmma
 * @className SensorHelper
 * @description  处理传感器数据相关的蓝牙通信协议
 * @date 2017/4/27
 * @update
 */


public class SensorHelper extends BaseHelper {

    private final String TAG = SensorHelper.class.getSimpleName();

    public SensorHelper(BaseActivity baseActivity){
        super(baseActivity);
    }


    @Override
    public void onReceiveData(String mac, byte cmd,  byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        //UbtLog.d(TAG, "onReceiveData cmd:" + cmd + "--param:" + ByteHexHelper.bytesToHexString(param));
        if(cmd == ConstValue.DV_READ_INFRARED_DISTANCE){
            if(param != null){
                UbtLog.d(TAG, "param[0]=" + param[0]); //param[0] 表示机器人与障碍物的距离
                if(param[0] != -1){
                    EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_GET_INFRARED_DISTANCE, param[0]));
                }

            }
        }else if(cmd == ConstValue.DV_READ_ROBOT_FALL_DOWN){
            if(param != null){
                //跌倒状态：00-没有跌倒,01-向前跌倒趋势，02-向后跌倒趋势，03-已经跌倒
                UbtLog.d(TAG, " fall param:" + param[0]);
                EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_ROBOT_FALL_DOWN_STATE, param[0]));
            }
        }else if(cmd == ConstValue.DV_READ_ROBOT_GYROSCOPE_DATA) {
            if(param != null){
                //TODO 返回机器人的姿态
                UbtLog.d(TAG, "params:" + BluetoothParamUtil.bytesToString(param));
                UbtLog.d(TAG, "ang:" + ByteHexHelper.bytesToHexString(param) +"param0:"+ param[0]);
                EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_ROBOT_GET_GYROSCOPE_DATA, param));
            }
        }else if(cmd == ConstValue.DV_SET_LED_LIGHT) {
            UbtLog.d(TAG, "led light set success!");
        }else if(cmd == ConstValue.DV_SET_PLAY_SOUND){
            if(param != null){
               UbtLog.d(TAG, "sound:" + ByteHexHelper.bytesToHexString(param) + "param[0]:" + param[0]);
                if(param[0] == 1){
                    EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_ROBOT_PLAY_SOUND_FINISH));
                }
            }
        }else if(cmd == ConstValue.DV_SET_PLAY_EMOJI){
            if(param != null){
                UbtLog.d(TAG, "emoji:" + ByteHexHelper.bytesToHexString(param) + "param[0]:" + param[0]);
                if(param[0] == 1){
                    //表情播放完成后通知js，js端音效和表情完成是同一个方法
                    EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_ROBOT_PLAY_SOUND_FINISH));
                }
            }
        }else if(cmd == ConstValue.DV_SET_LED_LIGHT){
            UbtLog.d(TAG, "robot received stop led light cmd");
        }else if(cmd == ConstValue.DV_SET_STOP_EMOJI) {
            UbtLog.d(TAG, "stop emo");
        }else if(cmd == ConstValue.REQUEST_WIFI_PORT) {
            String port = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG, "port:" +port );
            EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_CONNECT_SOCKET, port));
        }else if(cmd == ConstValue.DV_READ_ROBOT_ACCELERATION) {
            String value = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG, "acceleration:" + value);
        }else if(cmd == ConstValue.DV_FALL_DOWN) {
            UbtLog.d(TAG, "robot fall down: " + BluetoothParamUtil.bytesToString(param));
            EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_ROBOT_FALL_DOWN));
        }else if(cmd == ConstValue.DV_TAP_HEAD) {
            UbtLog.d(TAG, "robot tapped head: " + BluetoothParamUtil.bytesToString(param));
            EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_TAPPED_ROBOT_HEAD));
        }else if(cmd == ConstValue.DV_WALK){
            UbtLog.d(TAG, "机器人回复步态行走");
            if(param != null){
                UbtLog.d(TAG, "emoji:" + ByteHexHelper.bytesToHexString(param) + "param[0]:" + param[0]);
                if(param[0] == 0 || param[0] == 3){
                    //表情播放完成后通知js，js端音效和表情完成是同一个方法
                    EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_ROBOT_WALK_STOP));
                }
            }
        }else if(cmd == ConstValue.DV_INTO_EDIT){
            if(param != null){
                UbtLog.d(TAG, "DV_INTO_EDIT:" + ByteHexHelper.bytesToHexString(param) );
            }
        }else if(cmd == ConstValue.DV_6D_GESTURE){
            if(param != null){
                UbtLog.d(TAG, "DV_6D_GESTURE:" + ByteHexHelper.bytesToHexString(param) );
                if(param[0] != -1){
                    EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_6D_GESTURE, param[0]));
                }

            }
        }
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
     * 机器人跌倒
     */
    public void doReadRobotFallState() {
        doSendComm(ConstValue.DV_READ_ROBOT_FALL_DOWN, null);
    }

    /**
     * 读取机器人角度
     */
    public void doReadGyroData(byte status) {
        UbtLog.d(TAG, "doReadGyroData status:" + status);
        byte[] params = new byte[1];
        params[0] = status;
        doSendComm(ConstValue.DV_READ_ROBOT_GYROSCOPE_DATA, params);
    }

    /**
     * 机器人上报加速度
     * @param status 0x01开启 0x00 关闭
     */

    public void doReadAcceleration(byte status){
        byte[] params = new byte[1];
        params[0] = status;
        doSendComm(ConstValue.DV_READ_ROBOT_ACCELERATION, params);
    }

    /**
     * 设置灯光
     * @param params
     */
    public void setLedLight(byte[] params){
        doSendComm(ConstValue.DV_SET_LED_LIGHT, params);
    }

    /**
     * 播放音效
     * @param params
     */

    public void playSoundAudio(String params){
        doSendComm(ConstValue.DV_SET_PLAY_SOUND, stringToBytes(params));
    }

    /**
     * 展示表情
     * @param params
     */

    public void showEmoji(String params) {
        doSendComm(ConstValue.DV_SET_PLAY_EMOJI, stringToBytes(params));
    }

    /**
     * 停止音效
     */
    public void stopPlayAudio() {
        doSendComm(ConstValue.DV_SET_STOP_VOICE, null);
    }

    /**
     * 停止播放表情
     */
    public void stopPlayEmoji() {
        doSendComm(ConstValue.DV_SET_STOP_EMOJI, null);
    }


    /**
     * 停止眼睛灯
     */
    public void stopLedLight() {
        doSendComm(ConstValue.DV_SET_STOP_LED_LIGHT, null);
    }

    /**
     * 请求wifi传输port
     *
     */

    public void requestPort(String params) {
        UbtLog.d(TAG, "params:" + params);
        doSendComm(ConstValue.REQUEST_WIFI_PORT, BluetoothParamUtil.stringToBytes(params));
    }

    /**
     *
     * @param direct 方向：前后左右
     * @param speed  速度：快，中，慢
     * @param step   步数0-100,占4个参数
     */

    public void doWalk(byte direct, byte speed, byte[] step){
        UbtLog.d(TAG, "doWalk params:" + direct + "--" + speed + "--" + step);
        byte[] params = new byte[6];
        params[0] = direct;
        params[1] = speed;
        params[2] = step[3];
        params[3] = step[2];
        params[4] = step[1];
        params[5] = step[0];
        doSendComm(ConstValue.DV_WALK, params);
    }

    public void doStopWalk(){
        doSendComm(ConstValue.DV_STOP_WALK, null);
    }

    public void doChangeEditState(byte state){
        byte[] params = new byte[2];
        params[0] = state;
        params[1] = 0;
        UbtLog.d(TAG, "doChangeEditState params:" + ByteHexHelper.bytesToHexString(params));
        doSendComm(ConstValue.DV_INTO_EDIT, params);
    }

    /**
     * 主动读取一次6D状态
     */
    public void doRead6DState(){
        doSendComm(ConstValue.DV_6D_GESTURE, null);
    }




    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {

    }


    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {

    }

    @Override
    public void DistoryHelper() {

    }
}
