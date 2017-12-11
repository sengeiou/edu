package com.ubt.alpha1e.blockly;

import android.content.pm.ActivityInfo;
import android.webkit.JavascriptInterface;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.blockly.bean.RobotSensor;
import com.ubt.alpha1e.blockly.sensor.SensorObservable;
import com.ubt.alpha1e.blockly.sensor.SensorObserver;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.event.BlocklyEvent;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.RobotInfoActivity;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @className BlocklyJsInterface
 *
 * @author wmma
 * @description android端和js交互接口
 * @date  2017/03/06
 * @update
 */


public class BlocklyJsInterface {

    private String TAG = "BlocklyJsInterface";

    private BaseActivity mBaseActivity;
    private String mActions = "";

    public static final String FORWARD = "forward";
    public static final String BACK = "back";
    public static final String LEFT = "left";
    public static final String RIGHT= "right";
    public static final String FAST = "fast";
    public static final String MID = "mid";
    public static final String SLOW = "slow";



    public BlocklyJsInterface(BaseActivity baseActivity) {
        mBaseActivity = baseActivity;
    }

    /**
     * 向js传递内置动作列表
     *
     * @return 返回动作列表，每个动作之间以&来拼接，若未连接机器人则传递"";
     */
    @JavascriptInterface
    public String setActionParams() {
        UbtLog.d(TAG, "setActionParams");
        mActions = "";
        return parseActionList();
    }

    private String parseActionList() {

        List<String> names = ((BlocklyActivity) mBaseActivity).getActionList();
        UbtLog.d(TAG, "parseActionList=" + names);
        if (names != null && names.size() > 0) {
            for (int i = 0; i < names.size(); i++) {
                String actionName = names.get(i);
                if (actionName.startsWith("@") || actionName.startsWith("#") || actionName.startsWith("%")) {
                    actionName = actionName.substring(1);
                }
                mActions += actionName + "&";
            }
            UbtLog.d(TAG, "mActions=" + mActions);
        }
        return mActions;
    }

    /**
     * jimu项目遗留的接口，初始化时需要，后续需要web端屏蔽此方法。
     *
     * @return
     */
    @JavascriptInterface
    public String getServoID() {
        return "&servos=1|2|3";
    }

    /**
     * 沿用jimu的机器人执行命令接口
     *
     * @param params 动作名称
     */
    @JavascriptInterface
    public void executeByCmd(String params) {
        String action = params;
        UbtLog.d(TAG, "params:" + params);
        ((BlocklyActivity) mBaseActivity).playRobotAction(action, false, "", false);

    }


    /**
     * 用于回调给js动作是否执行完成， 该接口已废弃, 目前用于和js做调试使用。
     *
     * @return true 执行结束 false 未结束
     */
    @Deprecated
    @JavascriptInterface
    public boolean actionState() {
        boolean state = ((BlocklyActivity) mBaseActivity).getPlayFinishState();
        return true;
    }

    /**
     * 用户停止执行blockly程序时停止机器人播放。
     */
    @JavascriptInterface
    public void stopRobot() {
        UbtLog.d(TAG, "stopRobot");
        ((BlocklyActivity) mBaseActivity).stopPlay();
    }



    /**
     * 关闭blockly逻辑编程页面
     */
    @JavascriptInterface
    public void closeBlocklyWindow() {
        UbtLog.d(TAG, "closeBlocklyWindow");
        ((BlocklyActivity) mBaseActivity).finish();
//        ((BlocklyActivity) mBaseActivity).startOrStopRun((byte)0x02);

    }

    /**
     * js请求连接蓝牙
     */
    @JavascriptInterface
    public void bluetoothConnect() {
        UbtLog.d(TAG, "js call bluetoothConnect");
        ((BlocklyActivity) mBaseActivity).connectBluetooth();

    }


    /**
     * js获取当前蓝牙的连接状态
     *
     * @return true 连接， false 未连接
     */
    @JavascriptInterface
    public boolean getBluetoothState() {
        UbtLog.d(TAG, "getBluetoothState=" + ((BlocklyActivity) mBaseActivity).isBulueToothConnected());
        return ((BlocklyActivity) mBaseActivity).isBulueToothConnected();
    }

    /**
     * 连接蓝牙的情况下跳转到机器人信息页面
     */

    @JavascriptInterface
    public void showRobotInfo() {
        UbtLog.d(TAG, "showRobotInfo");
        RobotInfoActivity.launchActivity(mBaseActivity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }


    /**
     * js获取手机/平板状态接口
     */
    @JavascriptInterface
    public int matchPhoneDirection(String direction) {
        UbtLog.d(TAG, "direction=" + direction);

        if (((BlocklyActivity) mBaseActivity).getmDirection().equalsIgnoreCase(direction)) {
            return 1;
        } else {
            return 0;
        }

    }


    /**
     * js获取机器人是否低电量接口
     *
     * @return
     */
    @JavascriptInterface
    public boolean lowBatteryState() {

        return ((BlocklyActivity) mBaseActivity).getBatteryState();
    }

    /**
     * 显示表情接口
     *
     * @param params 表情ID
     */

    @JavascriptInterface
    public void showEmoji(String params) {
        //TODO 让机器人执行相应的表情动作
        UbtLog.d(TAG, "showEmoji params=" + params);
        ((BlocklyActivity) mBaseActivity).showEmoji(params);
    }

    /**
     * 播放音效
     */

    @JavascriptInterface
    public void playAudio(String params) {
        UbtLog.d(TAG, "playAudio params=" + params);
        ((BlocklyActivity) mBaseActivity).playSoundAudio(params);
    }


    /**
     * displayWalk, 行走模块
     *
     * @param params 参数以 " , " 分割方向、速度、时间
     */

    @JavascriptInterface
    public void displayWalk(String params) {
//        forward,fast,10
        UbtLog.d(TAG, "params:" + params);
        if (params != "" && params != null) {
            String direction = params.split(",")[0];
            String speed = params.split(",")[1];
            String time = params.split(",")[2];


            ((BlocklyActivity) mBaseActivity).doWalk(parseDirection(direction),parseSpeed(speed), ByteHexHelper.intToFourHexBytes(Integer.valueOf(time)));
//            String actionName = direction + "_" + speed;
//            ((BlocklyActivity) mBaseActivity).playRobotAction(actionName, true, time, false);
        }

    }

    private byte parseDirection(String direct) {
        if(direct.equals(FORWARD)){
            return WalkDirectionEnum.FORWARD.getValue();
        }else if(direct.equals(BACK)){
            return WalkDirectionEnum.BACK.getValue();
        }else if(direct.equals(LEFT)){
            return  WalkDirectionEnum.LEFT.getValue();
        }else if(direct.equals(RIGHT)) {
            return WalkDirectionEnum.RIGHT.getValue();
        }
        return 0;
    }

    private byte parseSpeed(String speed) {
        if(speed.equals(FAST)){
            return WalkSpeedEnum.FAST.getValue();
        }else if(speed.equals(MID)) {
            return WalkSpeedEnum.MID.getValue();
        }else if(speed.equals(SLOW)){
            return  WalkSpeedEnum.SLOW.getValue();
        }
        return 1;
    }


    /**
     * 保存block程序
     *
     * @param xml js传递的参数
     */

    @JavascriptInterface
    public void saveXmlProject(String xml) {
        UbtLog.d(TAG, "xml=" + xml.toString());
    }


    /**
     * js获取用户信息
     *
     * @return 当前登录用户信息ID
     */

    @JavascriptInterface
    public long getUserID() {
        UbtLog.d(TAG, "getUserID");
        return ((BlocklyActivity) mBaseActivity).getCurrentUserID();
    }


    /**
     * 传感器相关接口
     *
     * @param params js传递给android端的数据
     */

    @JavascriptInterface
    public void registerSensorObservers(String params) {
//        [{"sensorType":"phone","operator":"EQ","value":"left","sensorId":"0","branchId":"5","callback":"sensorCallback"}]
        UbtLog.d(TAG, "params=" + params.toString());
        JSONObject jsonObject = null;
        try {
            JSONArray jsonArray = new JSONArray(params);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject != null) {
                        SensorObserver sensorObserver = GsonImpl.get().toObject(jsonObject.toString(), SensorObserver.class);
                        SensorObservable.getInstance().addObserver(sensorObserver);
                        if (jsonObject.get("sensorType").equals(RobotSensor.SensorType.INFRARED)) {
                            EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_START_INFRARED));
                        }
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 移除所有的传感器观察者
     */
    @JavascriptInterface
    public void unRegisterAllSensorObserver() {
        UbtLog.d(TAG, "unRegisterAllSensorObserver");
        SensorObservable.getInstance().deleteObservers();
        SensorObservable.getInstance().clearActiveObserver();
    }


    /**
     * registerEventObservers js调用的所有事件的方法
     *
     * @param params
     * @return true or false
     * <p>
     * 机器人跌倒传var ifreamParam = {"sensorType":"fallDown", "value":""};
     * 电量查询var ifreamParam = {"sensorType":"lowBatter", "value":""};
     * 红外数据格式 {"sensorType":"infraredDistance","value":"10","symbol":"greater"}
     * {"sensorType":"robotState", "value":"pick_up"}
     * var robotStrArr = ["pick_up","gravity_forward","gravity_after","strike_front","strike_after"];
     * 机器人加速度{"sensorType":"robotAddSpeed", "value":"6",direction":"up,"sysmbol":"LT"}
     */

    @JavascriptInterface
    public void registerEventObservers(String params) {
        UbtLog.d(TAG, "registerEventObservers params=" + params);
        try {
            JSONObject jsonObject = new JSONObject(params);
            String sensorType = jsonObject.getString("sensorType");
            if (sensorType.equalsIgnoreCase((EventType.lowBatter).toString())) {
                ((BlocklyActivity) mBaseActivity).checkBattery();
            } else if (sensorType.equalsIgnoreCase((EventType.fallDown).toString())) {
                ((BlocklyActivity) mBaseActivity).checkRobotFall();
            } else if (sensorType.equalsIgnoreCase((EventType.infraredDistance).toString())) {
                ((BlocklyActivity) mBaseActivity).startInfrared(params);
            } else if (sensorType.equalsIgnoreCase("robotState")) {
                String value = jsonObject.getString("value");
            } else if (sensorType.equalsIgnoreCase("robotAddSpeed")) {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 给js传递语音数据列表
     *
     * @return 获取的语音数据，以json的格式传递
     */

    @JavascriptInterface
    public String setOfflineParams() {
        UbtLog.d(TAG, "setOfflineParams");
        // 以下模拟数据供调试，待机器人本地完善后获取真实数据处理。
        return "{\"action\":[{\"param\":\"你好\"},{\"param\":\"天天向上\"},{\"param\":\"回眸一笑百媚生\"}],\"emotion\":[{\"param\":\"开心\"},{\"param\":\"操蛋\"}],\"answer\":[{\"param\":\"搞事啊！\"},{\"param\":\"操蛋\"}]}";
    }

    @JavascriptInterface
    public void path() {

    }


    // jimu遗留
    @JavascriptInterface
    public void customSoundList() {

    }

    @JavascriptInterface
    public void stopPlayAudio() {
        UbtLog.d(TAG, "stopPlayAudio");
        ((BlocklyActivity) mBaseActivity).stopPlayAudio();
    }


    //眼镜灯光
    // ['red','orange','yellow','green','cyan','blue','purple','white'];
    @JavascriptInterface
    public void setEyeStatusParameter(String params) {
        UbtLog.d(TAG, "setEyeStatusParameter params=" + params);
        ((BlocklyActivity) mBaseActivity).setLedLight(params);

    }


    /**
     * TTS 语音播报
     *
     * @param tts
     */

    @JavascriptInterface
    public void playTTS(String tts) {
        UbtLog.d(TAG, "playTTS:" + tts);
        ((BlocklyActivity) mBaseActivity).playTTSFinish();
    }


    //掉电回读
    @JavascriptInterface
    public void playBackReadPowerDownEvent() {
        UbtLog.d(TAG, "playBackReadPowerDownEvent");
        EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_ROBOT_POWER_DOWN));
    }

    //掉电回读完成
    @JavascriptInterface
    public void playBackReadFinish() {
        UbtLog.d(TAG, "playBackReadFinish");
        EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_ROBOT_READ_ANGLE));
    }


    //运行回读动作
    @JavascriptInterface
    public void playBackReadAction(String params) {
        UbtLog.d(TAG, "playBackReadAction---" + params);
        EventBus.getDefault().post(new BlocklyEvent(BlocklyEvent.CALL_ROBOT_PLAY_READ_ACTION));

    }

    @JavascriptInterface
    public void login() {
        UbtLog.d(TAG, "login");
        ((BlocklyActivity) mBaseActivity).login();
    }

    //读取机器人硬件版本
    @JavascriptInterface
    public String getRobotHardVersion() {
        UbtLog.d(TAG, "getRobotHardVersion");
        return ((AlphaApplication) mBaseActivity.getApplicationContext()).getRobotHardVersion();
    }

    //左转右转
    //{"direction":"left","angle":"270"}

    @JavascriptInterface
    public void movementTurnParams(String params) {
        UbtLog.d(TAG, "turn params:" + params);
        try {
            JSONObject jsonObject = new JSONObject(params);
            String direction = jsonObject.getString("direction");
            String angle = jsonObject.getString("angle");
            UbtLog.d(TAG, "direction:" + direction + "angle:" + angle);

            int count = 1;
            if (isStringNumber(angle)) {
                count = Integer.parseInt(angle) / 45;
            }
            UbtLog.d(TAG, "turn params count : " + count);
            ((BlocklyActivity) mBaseActivity).playRobotAction(direction, true, count + "", true);
            //TODO 通知机器人执行左转右转
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //播放音乐
    @JavascriptInterface
    public void playMusic(String params) {
//        {"filename":"啊啊啊.mp3","playStatus":1,"uploadStatus":0}
        UbtLog.d(TAG, "playMusic params=" + params);
        try {
            JSONObject jsonObject = new JSONObject(params);
            String filename = jsonObject.getString("filename");
            int playStatus = jsonObject.getInt("playStatus");
            int uploadStatus = jsonObject.getInt("uploadStatus");
            if (uploadStatus == 0) {
                //TODO 手机播放
                if (playStatus == 1) {
                    ((BlocklyActivity) mBaseActivity).playMP3Recorder(filename);
                } else {
                    ((BlocklyActivity) mBaseActivity).stopMP3Play();
                }

            } else {
                //TODO 机器人播放
                if (playStatus == 1) {
                    //播放
                } else {
                    //停止
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ((BlocklyActivity) mBaseActivity).playSoundAudio(params);
    }


    //开始录音
    @JavascriptInterface
    public boolean startRecordAudio() {
        UbtLog.d(TAG, "startRecordAudio");
        ((BlocklyActivity) mBaseActivity).startMP3Recorder();
        return true;

    }

    //结束录音
    @JavascriptInterface
    public void stopRecordAudio() {
        UbtLog.d(TAG, "stopRecordAudio");
        ((BlocklyActivity) mBaseActivity).stopMP3Recorder();
    }

    @JavascriptInterface
    public void playMP3Record(String recordName) {
        UbtLog.d(TAG, "playMP3Record:" + recordName);
        //TODO 预览临时录音文件
        ((BlocklyActivity) mBaseActivity).playMP3Recorder(recordName);
    }

    @JavascriptInterface
    public boolean cancelRecordAudio(String name) {
        //TODO 不保存录音文件，删除录音临时文件
        UbtLog.d(TAG, "cancelRecordAudio:" + name);
        ((BlocklyActivity) mBaseActivity).deleteMP3RecordFile(name);
        return true;
    }

    public boolean deleteRecordAudio(String name) {
        UbtLog.d(TAG, "deleteRecordAudio:" + name);
        ((BlocklyActivity) mBaseActivity).deleteMP3RecordFile(name);
        return true;
    }

    @JavascriptInterface
    public void saveRecordAudio(String params) {
        UbtLog.d(TAG, "saveRecordAudio:" + params);
        //TODO 用户保存录音
        ((BlocklyActivity) mBaseActivity).saveMP3RecordFile(params + ".mp3");
    }

    //读取用户当前的录音文件
    @JavascriptInterface
    public String readRecordAudio() {
        UbtLog.d(TAG, "readRecordAudio");
//        String  recordAudios = "";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        try {
            List<String> names = ((BlocklyActivity) mBaseActivity).readMP3RecordFiles();
            UbtLog.d(TAG, "names:" + names.toString());
            if (names.size() > 0) {


                for (int i = 0; i < names.size(); i++) {
//                    recordAudios += names.get(i) + "&";
                    jsonObject = new JSONObject();
                    jsonObject.put("name", names.get(i));
                    jsonObject.put("uploadStatus", 0);
                    jsonArray.put(jsonObject);
                    jsonObject = null;
                }
//                jsonArray.put(jsonObject);
            }

        } catch (Exception e) {
            UbtLog.d(TAG, "Exception:" + e.getMessage());
        }

        return jsonArray.toString();
    }

    /**
     * 判断边充边玩是否打开
     *
     * @return true or false
     */
    @JavascriptInterface
    public boolean checkCharge() {
        UbtLog.d(TAG, "checkCharge:" + ((BlocklyActivity) mBaseActivity).checkCharge());
        return ((BlocklyActivity) mBaseActivity).checkCharge();
    }

    /**
     * 显示课程帮助
     */
    @JavascriptInterface
    public void showLessonTaskHelpDes() {
        UbtLog.d(TAG, "-showLessonTaskHelpDes-");
        ((BlocklyActivity) mBaseActivity).showLessonTaskHelpDes();
    }

    /**
     * 课程任务结果，对与错
     *
     * @param isSuccess
     */
    @JavascriptInterface
    public void uploadCourseResult(boolean isSuccess) {
        UbtLog.d(TAG, "-uploadCourseResult-" + isSuccess);
        ((BlocklyActivity) mBaseActivity).receiveTaskRunResult(isSuccess);
    }

    @JavascriptInterface
    public void endInit() {
        UbtLog.d(TAG, "-endInit-");
    }

    @JavascriptInterface
    public String courseInitDefaultJs() {
        UbtLog.d(TAG, "-courseInitDefaultJs-");
        return "";
    }

    /**
     * 显示隐藏课程任务
     *
     * @param isShow
     * @return
     */
    @JavascriptInterface
    public void appBouncedEffects(boolean isShow) {
        UbtLog.d(TAG, "-appBouncedEffects-" + isShow);
        ((BlocklyActivity) mBaseActivity).setHeadTaskShow(isShow);
    }

    /**
     * 获取任务导航
     *
     * @return
     */
    @JavascriptInterface
    public String getTaskGuide() {
        UbtLog.d(TAG, "-getTaskGuide-");
        return ((BlocklyActivity) mBaseActivity).getTaskGuideToBlockly();
    }

    /**
     * 获取app当前语言代码
     *
     * @return
     */
    @JavascriptInterface
    public String getAppLanguageCode() {
        String localLanguage = mBaseActivity.getAppCurrentLanguage();
        if (localLanguage.contains("zh")) {
            String country = AlphaApplication.getBaseActivity().getAppCurrentCountry();
            if ("tw".equalsIgnoreCase(country) || "hk".equalsIgnoreCase(country)) {
                localLanguage = "zh_tw";
            } else {
                localLanguage = "zh_cn";
            }
        }

        localLanguage = mBaseActivity.getStandardLocale(localLanguage);
        UbtLog.d(TAG, "localLanguage == " + localLanguage);
        return localLanguage;
    }

    /**
     * 获取当前是否第一次进入blockly程序
     *
     * @return
     */
    @JavascriptInterface
    public boolean isFirstTimeBlockly() {
        UbtLog.d(TAG, "-isFirstTimeBlockly-");
        if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(BasicSharedPreferencesOperator.IS_FIRST_USE_BLOCKLY)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_BLOCKLY_FALSE)) {
            return false;
        }

        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_FIRST_USE_BLOCKLY,
                BasicSharedPreferencesOperator.IS_FIRST_USE_BLOCKLY_FALSE,
                null, -1);
        return true;
    }

    /**
     * 获取当前是否第一次进入任务
     *
     * @return
     */
    @JavascriptInterface
    public boolean isFirstTimeTask() {
        UbtLog.d(TAG, "-isFirstTimeTask-");
        return ((BlocklyActivity) mBaseActivity).isFirstTimeTask();
    }

    /**
     *开始执行动作
     */
    @JavascriptInterface
    public void startRunProgram(){
        UbtLog.d(TAG, "-startRunProgram-");
        ((BlocklyActivity) mBaseActivity).startOrStopRun((byte)0x01);
    }

    /**
     * 执行完程序
     */
    @JavascriptInterface
    public void finishProgramRun() {
        UbtLog.d(TAG, "-finishProgramRun-");
        ((BlocklyActivity) mBaseActivity).startOrStopRun((byte)0x02);

    }

    @JavascriptInterface
    public void debugLog(String log) {

    }


    @JavascriptInterface
    public void stopAllAction() {
        //TODO 停止所有动作
        UbtLog.d(TAG, "stopAllAction");
        ((BlocklyActivity) mBaseActivity).stopPlay();
    }

    /**
     * 保存项目
     *
     * @param saveString
     */
    @JavascriptInterface
    public void saveProject(String saveString) {
        UbtLog.d(TAG, "saveProject:" + saveString);
        try {
            JSONObject jsonObject = new JSONObject(saveString);
            String name = jsonObject.getString("name");
            String xml = jsonObject.getString("xml");
            UbtLog.d(TAG, "xml:" + xml);

            BlocklyProjectMode projectMode = new BlocklyProjectMode();
            projectMode.setName(name);
            projectMode.setXml(xml);

            projectMode.saveOrUpdate("name = ?", name);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除保存项目
     * @param xml
     */
    @JavascriptInterface
    public void deleteProjectXml(String xml) {
        UbtLog.d(TAG, "deleteProjectXml:" + xml);
        DataSupport.deleteAll(BlocklyProjectMode.class, "name = ?", xml);
    }

    /**
     * 获取保存的项目列表
     *
     * @return
     */
    @JavascriptInterface
    public String getProjectLists() {
        UbtLog.d(TAG, "getProjectLists:");
        return getProjectList();
    }

    private String getProjectList() {
        String userId = SPUtils.getInstance().getString(Constant.SP_USER_ID);
        List<BlocklyProjectMode> projectModeList = DataSupport.findAll(BlocklyProjectMode.class);

        UbtLog.d(TAG, "json:" + projectModeList.toString());

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < projectModeList.size(); i++) {
            try {
                BlocklyProjectMode projectMode = projectModeList.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", projectMode.getName());
                jsonObject.put("xml", projectMode.getXml());
                UbtLog.d(TAG, "projectMode:" + projectMode.getXml());
                jsonArray.put(i, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        String json = jsonArray.toString().replace("\\/", "/");
        UbtLog.d(TAG, "json:" + json);
        return json;


    }








    /***
     * 判断字符串是否都是数字
     */
    public  boolean isStringNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 事件类型枚举； 低电量、机器人跌倒、红外
     */

    public enum EventType {
        lowBatter,
        fallDown,
        infraredDistance
    }





}
