package com.ubt.alpha1e_edu.blockly.sensor;


import android.text.TextUtils;

import com.ubt.alpha1e_edu.blockly.DeviceDirectionEnum;
import com.ubt.alpha1e_edu.blockly.RobotStateEnum;
import com.ubt.alpha1e_edu.blockly.bean.QueryResult;
import com.ubt.alpha1e_edu.blockly.bean.RobotSensor;
import com.ubt.alpha1e_edu.event.BlocklyEvent;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @className SensorObserver
 *
 * @author wmma
 * @description 传感器事件观察者
 * @date 2017/4/19
 * @update
 */


public class SensorObserver implements Observer {

//    [{"sensorType":"phone","operator":"EQ","value":"left","sensorId":"0","branchId":"5","callback":"sensorCallback"}]

    private final String TAG = SensorObserver.class.getSimpleName();

    private String sensorType;  //传感器类型
    private String operator;    //操作符：> < = ≠
    private String value;       //传感器值
    private String sensorId;    //传感器ID
    private String branchId;    //分支ID
    private String callback;    //js回调函数名

    private String result; //传感器返回的结果

    @Override
    public void update(Observable observable, Object data) {
        UbtLog.d(TAG, "update");
        QueryResult result = (QueryResult) data;
        check(observable, result);
    }

    //检查机器人返回的数据是否符合当前监听的条件
    private void check(Observable observable, QueryResult result){
        if(TextUtils.isEmpty(sensorId)){
            return ;
        }
        UbtLog.d(TAG, "check" + sensorType);
        if(RobotSensor.SensorType.INFRARED.sameSensor(sensorType)){
            checkInfrared(observable, result);
        }else if(RobotSensor.SensorType.GYROSCOPE.sameSensor(sensorType)){
//            checkGyroscope(observable, result);
        }else if(RobotSensor.SensorType.TOUCH.sameSensor(sensorType)){
//            checkTouch(observable, result);
        }else if(RobotSensor.SensorType.PHONE.sameSensor(sensorType)){
            checkPhone(observable, result);
        }else if(RobotSensor.SensorType.ROBOT.sameSensor(sensorType)){
            checkRobot(observable, result);
        }else if(RobotSensor.SensorType.ACCELERATION.sameSensor(sensorType)){
            checkAcceleration(observable, result);
        }

    }


    private void checkPhone(Observable observable, QueryResult sensorResult){
        UbtLog.d(TAG, "checkPhone");
        List<RobotSensor> sensors = sensorResult.getPhone();
        if (sensors == null || sensors.size() < 1) {
            return;
        }
        RobotSensor sensor = sensors.get(0);
        result = sensor.getSensorEvent() + "";
        UbtLog.d(TAG, "result" + result);
        int compareValue = -1;
        for(DeviceDirectionEnum dirction : DeviceDirectionEnum.values()){
            if(dirction.getValue().equals(value)){
                compareValue = dirction.getType();
                break;
            }
        }
        if (Comparation.compare(result, operator, compareValue + "")) {
            setJSResult(callback, branchId, result);
            observable.deleteObserver(this);
        }
    }

    private void checkInfrared(Observable observable, QueryResult sensorResult) {
        List<RobotSensor> sensors = sensorResult.getInfrared();
        if (sensors == null || sensors.size() < 1) {
            return;
        }
        for (RobotSensor sensor : sensors) {
            if (sensorId.equals(sensor.getSensorId() + "")) {
                result = sensor.getSensorEvent() + "";
                if (Comparation.compare(result, this.operator, this.value)) {
                    setJSResult(callback, branchId, result);
                    observable.deleteObserver(this);
                }
                break;
            }
        }
    }

    //check 机器人事件
    private void checkRobot(Observable observable, QueryResult sensorResult) {
        List<RobotSensor> sensors = sensorResult.getRobot();
        if (sensors == null || sensors.size() < 1) {
            return;
        }

        RobotSensor sensor = sensors.get(0);
        result = sensor.getSensorEvent() + "";
        UbtLog.d(TAG, "result" + result);
        int compareValue = -1;
        for(RobotStateEnum state : RobotStateEnum.values()){
            if(state.getValue().equals(value)){
                compareValue = state.getType();
                break;
            }
        }
        if (Comparation.compare(result, operator, compareValue + "")) {
            setJSResult(callback, branchId, result);
            observable.deleteObserver(this);
        }

    }


    private void checkAcceleration(Observable observable, QueryResult sensorResult){
       //TODO
    }

    private void setJSResult(String func, String... result) {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:").append(func).append("(");
        if (result != null && result.length > 0) {
            for (int i = 0; i < result.length; i++) {
                sb.append("\'").append(result[i]).append("\'");
                if (i < result.length - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append(")");
        final String js = sb.toString();
        UbtLog.d(TAG, js);
        EventBus.getDefault().post(new BlocklyEvent(
                BlocklyEvent.CALL_JAVASCRIPT, js));
    }


    public static class Comparation {

        public static final String OP_GREATER_THAN = "GT";
        public static final String OP_EQUALS = "EQ";
        public static final String OP_LESS_THAN = "LT";
        public static final String OP_NOT_EQUALS = "NEQ";

        public static boolean compare(String param1, String operator, String param2) {
            boolean result = false;
            try {
                int par1 = Integer.parseInt(param1);
                int par2 = Integer.parseInt(param2);
                switch (operator) {
                    case OP_GREATER_THAN:
                        result = par1 > par2;
                        break;
                    case OP_EQUALS:
                        result = par1 == par2;
                        break;
                    case OP_LESS_THAN:
                        result = par1 < par2;
                        break;
                    case OP_NOT_EQUALS:
                        result = par1 != par2;
                        break;
                    default:
                        break;
                }
            }catch (NumberFormatException e){
                e.printStackTrace();
                UbtLog.e("Comparation", e.getMessage());
            }
            return result;
        }

    }










}
