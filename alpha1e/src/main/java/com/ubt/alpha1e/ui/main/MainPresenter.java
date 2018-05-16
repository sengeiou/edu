package com.ubt.alpha1e.ui.main;

import android.content.res.TypedArray;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.CheckIsBindRequest;
import com.ubt.alpha1e.base.RequstMode.XGGetAccessIdRequest;
import com.ubt.alpha1e.base.ResponseMode.XGDeviceMode;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.loading.LoadingDialog;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.business.ActionPlayerListener;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.ui.custom.CommonCtrlView;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.userinfo.model.MyRobotModel;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.xingemodule.XGUBTManager;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.sqlite.UBXDataBaseHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.MediaType;

import static android.R.attr.id;
import static com.ubt.alpha1e.base.Constant.ROBOT_SLEEP_EVENT;


public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter,ActionPlayerListener {
    private String TAG = "MainPresenter";
    public final static int CHECK_ROBOT_INFO_HABIT=1;
    public static int APP_CURRENT_STATUS=-1;
    private int ROBOT_UNCHARGE_STATUS=0x0;
    private int ROBOT_CHARGING_STATUS=0x01;
    private int ROBOT_CHARGING_ENOUGH_STATUS=0x03;



    private final int LOW_BATTERY_TWENTY_THRESHOLD=20;
    private final int LOW_BATTERY_FIVE_THRESHOLD=5;
    private  boolean ENTER_LOW_BATTERY_FIVE=false;
    private  boolean ENTER_LOW_BATTERY_TWENTY=false;
    private int powerThreshold[] = {5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    private byte mChargeValue=0;
    private boolean IS_CHARGING=false;
    XGDeviceMode xgDeviceMode;
    private int xgCnt = 0;

    public void registerEventBus() {
        EventBus.getDefault().register(MainPresenter.this);
    }

    public void unregisterEventBus() {
        EventBus.getDefault().unregister(MainPresenter.this);
    }

    @Override
    public  int[] requestCartoonAction(int value ) {
            String actionName="";
            TypedArray typedArray=null;
            int[] resId={0};
            switch (value) {
//                case Constant.cartoon_action_enjoy:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.enjoy);
//                    actionName="enjoy";
//                    break;
//                case Constant.cartoon_action_fall:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.fall);
//                    actionName="fall";
//                    break;
                case Constant.cartoon_action_greeting:
                   if(mView!=null) {
                       if (mView.getContext() != null) {
                           typedArray = mView.getContext().getResources().obtainTypedArray(R.array.greetting);
                       }
                   }
                    actionName="greetting";
                    break;
//                case Constant. cartoon_action_hand_stand:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.hand_stand);
//                    actionName="hand_stand";
//                    break;
//                case Constant.cartoon_action_hand_stand_reverse:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.hand_stand_reverse);
//                    actionName="hand_stand_reverse";
//                    break;
//                case Constant.cartoon_action_smile:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.smile);
//                    actionName="smile";
//                    break;
//                case Constant.cartoon_action_squat:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.squat);
//                    actionName="squat";
//                    break;
//                case Constant.cartoon_aciton_squat_reverse:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.squat_reverse);
//                    actionName="squat_reverse";
//                    break;
//                case Constant.cartoon_action_shiver:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.shiver);
//                    actionName="shiver";
//                    break;
//                case Constant.cartoon_action_swing_left_hand:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.swing_lefthand);
//                    actionName="left_hand";
//                    break;
//                case Constant.cartoon_action_swing_left_leg:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.swing_leftleg);
//                    actionName="left_leg";
//                    break;
//                case Constant.cartoon_action_swing_right_hand:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.swing_righthand);
//                    actionName="right_hand";
//                    break;
//                case Constant.cartoon_action_swing_right_leg:
//                    typedArray = mView.getContext().getResources().obtainTypedArray(R.array.swing_rightleg);
//                    actionName="right_leg";
//                    break;
                default:
                    break;
            }
            UbtLog.d(TAG, "ACTIO NAME IS " + actionName);
            if(typedArray!=null) {
                int len = typedArray.length();
                resId = new int[len];
                for (int i = 0; i < len; i++) {
                    resId[i] = typedArray.getResourceId(i, -1);
                }
                typedArray.recycle();
                return resId;
            }else {
                return resId ;
            }
    }

    @Override
    public String getBuddleText(int type) {
        String text="";
        if(type==Constant.BUDDLE_LOW_BATTERY_TEXT){
            Random random = new Random();
            UbtLog.i(TAG, "LOW_POWER_LESS_TWENTY:   ");
            String[] arrayText = AppManager.getInstance().currentActivity().getResources().getStringArray(R.array.mainUi_buddle_lowpower);
            int select = random.nextInt(arrayText.length);
             text = arrayText[select];
        }else if(type==Constant.BUDDLE_RANDOM_TEXT){
            Random random = new Random();
            // UbtLog.i(TAG, "randomBuddleText :   " + buddleTextTimeout + "main thread " );
            String[] arrayText = AppManager.getInstance().currentActivity().getResources().getStringArray(R.array.mainUi_buddle_text);
            int select = random.nextInt(arrayText.length);
            text = arrayText[select];
        }else if(type==Constant.BUDDLE_INIT_TEXT){
            if(mView!=null) {
                text = mView.getContext().getResources().getString(R.string.buddle_text_init_status);
            }
        }
        return text;
    }

    @Override
    public void commandRobotAction(byte cmd, byte[] params) {
        MainUiBtHelper.getInstance(mView.getContext()).sendCommand(cmd, params);
    }

    @Override
    public void dealMessage(String json) {
        try {
            JSONObject mMessage = new JSONObject(json);
            String mParam=mMessage.getString("param");
            final byte[] mParams = Base64.decode(mParam, Base64.DEFAULT);
            int mCmd = Integer.parseInt(mMessage.getString("cmd"));
            if(mCmd<0){
                mCmd=255+mCmd;
            }
//            UbtLog.d(TAG,"CMD IS  "+mCmd);
            if(mCmd==ConstValue.DV_TAP_HEAD) {
                //looperThread.send(createMessage(ROBOT_HIT_HEAD));
            }else if(mCmd==ConstValue.DV_6D_GESTURE){
                UbtLog.d(TAG,"DV_6D_GESTURE index[0]:"+mParams[0]);
                if(mParams[0]==Constant.ROBOT_HEAD_UP_STAND){
                    mView.dealMessage(Constant.ROBOT_default_gesture);
                } else if(mParams[0]==Constant.ROBOT_HEAD_DOWN) {
                    mView.dealMessage(Constant.ROBOT_hand_stand);
                }else if(mParams[0]==Constant.ROBOT_LEFT_SHOULDER_SLEEP||mParams[0]==Constant.ROBOT_RIGHT_SHOULDER_SLEEP||mParams[0]==Constant.ROBOT_HEAD_UP_SLEEP||mParams[0]==Constant.ROBOT_HEAD_DOWN_SLEEP){
                    mView.dealMessage(Constant.ROBOT_fall);
                }
            }else if (mCmd == ConstValue.DV_SLEEP_EVENT) {
                UbtLog.d(TAG, "ROBOT SLEEP EVENT");
                mView.dealMessage(ROBOT_SLEEP_EVENT);
            } else if (mCmd == ConstValue.DV_LOW_BATTERY) {
                UbtLog.d(TAG, "ROBOT LOW BATTERY");
                UbtLog.d(TAG,"LOW BATTERY +"+mParams[0]);
                // lowBatteryFunction(mParams[0]);
            } else if (mCmd == ConstValue.DV_READ_BATTERY) {
                // UbtLog.d(TAG,"LENGTH :"+mParams.length);
                for (int i = 0; i < mParams.length; i++) {
                    // UbtLog.d(TAG, "index " + i + "value :" + mParams[i]);
                    if (mParams[2] == ROBOT_CHARGING_STATUS) {
                        IS_CHARGING=true;
                       // UbtLog.d(TAG, " IS CHARGING ");
                        mView.dealMessage(Constant.ROBOT_CHARGING);
                        mView.showBatteryCapacity(true,getPowerCapacity(mParams[3]));
                    } else if(mParams[2]==ROBOT_UNCHARGE_STATUS) {
                        IS_CHARGING=false;
                        mView.dealMessage(Constant.ROBOT_UNCHARGING);
                        lowBatteryFunction(mParams[3]);
                        mView.showBatteryCapacity(false,getPowerCapacity(mParams[3]));
                       // UbtLog.d(TAG,"NOT CHARGING");
                    }else if(mParams[2]==ROBOT_CHARGING_ENOUGH_STATUS){
                       // UbtLog.d(TAG,"BATTERY ENOUGH AND PLUG IN CHARGING");
                        mView.dealMessage(Constant.ROBOT_CHARGING_ENOUGH);
                        mView.showBatteryCapacity(true,getPowerCapacity(mParams[3]));
                    }
                    //mChargeValue=mParams[2];
                }
            } else if(mCmd==ConstValue.DV_COMMON_COMMAND) {
                UbtLog.d(TAG,"DV COMMON COMMAND [0]: +"+mParams[0] +"index [1]: "+mParams[1]+"index [2]: " +mParams[2]);
            }else if(mCmd==ConstValue.DV_CURRENT_PLAY_NAME){
                try {
                    String actionName=new String(mParams, 1, mParams.length-1, "utf-8");
                    UbtLog.d(TAG,"ACTION NAME IS "+actionName);
                    if(getRobotStatus()==ROBOT_SLEEP_EVENT) {
                        if (actionName.contains(Constant.WakeUpActionName)) {
                            mView.dealMessage(Constant.ROBOT_WAKEUP_ACTION);
                        }
                    }
                }catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }

            } else if(mCmd==ConstValue.DV_VOICE_WAIT){
                UbtLog.d(TAG,"HIDDEN BUDDLE TEXT");
                mView.hiddenBuddleText();
               // resetGlobalActionPlayer();
            } else {
                //  UbtLog.d(TAG, "ROBOT OTHER SITUATION" + mCmd);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRobotStatus(int status) {
        APP_CURRENT_STATUS=status;
    }

    @Override
    public int getRobotStatus() {
        return APP_CURRENT_STATUS;
    }

    public int getPowerCapacity(byte mParam) {
      //  UbtLog.d(TAG, "POWER VALUE " + mParam);
        int power_index = 0;
        if (mParam < powerThreshold[powerThreshold.length / 2]) {
            for (int j = 0; j < powerThreshold.length / 2; j++) {
                if (mParam < powerThreshold[j + 1] && mParam > powerThreshold[j]) {
                    power_index = j;
                    break;
                }
                if (mParam == powerThreshold[j]) {
                    power_index = j;
                    break;
                }
            }
        } else {
            for (int j = powerThreshold.length / 2; j < powerThreshold.length; j++) {
                if (powerThreshold[j] < mParam && mParam < powerThreshold[j+1]) {
                    power_index = j;
                    break;
                }
                if (mParam == powerThreshold[j]) {
                    power_index = j;
                    break;
                }
            }
        }
        // UbtLog.d(TAG, "Current power is " + power_index);
        //Prodcut manage rCONFIRM AGAIN modification
        if(0<mParam&&mParam<=5){
            power_index=0;
        }
        if(6<=mParam&&mParam<=10){
            power_index=1;
        }
        if(11<=mParam&&mParam<=20){
            power_index=2;
        }
        if(21<=mParam&&mParam<=30){
            power_index=3;
        }
        return power_index;
    }

    private void lowBatteryFunction(int currentValue){
        if(currentValue==(LOW_BATTERY_TWENTY_THRESHOLD)){//ROBOT END BATTERY CAPACITY <=5
            if(!ENTER_LOW_BATTERY_TWENTY) {
                mView.dealMessage(Constant.ROBOT_LOW_POWER_LESS_TWENTY_STATUS);
                ENTER_LOW_BATTERY_TWENTY=true;
            }
        }else if(currentValue==(LOW_BATTERY_FIVE_THRESHOLD)){//ROBOT END BATTERY CAPACITY <=20
            if(!ENTER_LOW_BATTERY_FIVE) {
                mView.dealMessage(Constant.ROBOT_LOW_POWER_LESS_FIVE_STATUS);
                ENTER_LOW_BATTERY_FIVE=true;
            }
        }
    }
    /**
     * 获取信鸽的AccessId 和信鸽的accessToken
     */
    public void getXGInfo() {
        String accessId = SPUtils.getInstance().getString(Constant.SP_XG_ACCESSID);
        String accessKey = SPUtils.getInstance().getString(Constant.SP_XG_ACCESSKEY);
        String userId = SPUtils.getInstance().getString(Constant.SP_XG_USERID);
        UbtLog.d("XGREquest","getXGInfo  old userId"+userId+ "new USERID "+SPUtils.getInstance().getString(Constant.SP_USER_ID));
            String Url = HttpEntity.getXGAppId + "?appName=ALPHA1E";
            UbtLog.d("XGREquest", "url===" + Url);
            OkHttpUtils.get()
                    .addHeader("authorization", SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN))
                    .url(Url)
                    .id(id)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    UbtLog.d("XGREquest", "onError===" + e.getMessage());
                }
                @Override
                public void onResponse(String response, int id) {
                    UbtLog.d("XGREquest", "response===" + response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject object=jsonArray.getJSONObject(0);
                         xgDeviceMode =GsonImpl.get().toObject(object.toString(),XGDeviceMode.class);
                        UbtLog.d("XGREquest", "xgDeviceMode===" + xgDeviceMode.toString());
                        if (xgDeviceMode.getDevice().equals("a")) {
                            SPUtils.getInstance().put(Constant.SP_XG_ACCESSID, xgDeviceMode.getAccessId());
                            SPUtils.getInstance().put(Constant.SP_XG_ACCESSKEY, xgDeviceMode.getAccessKey());
                            AlphaApplication.initXG();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
    }

    private void BindXGServer(final XGDeviceMode xgDeviceMode) {
        String userId = SPUtils.getInstance().getString(Constant.SP_USER_ID);
        if(TextUtils.isEmpty(userId)){
     //   if(!TextUtils.isEmpty(userId)&&userId.equals(SPUtils.getInstance().getString(Constant.SP_USER_ID))){
            UbtLog.d(TAG,"BindXGServer  userId null");
            return;
        }
        XGGetAccessIdRequest request = new XGGetAccessIdRequest();
        request.setAppId(xgDeviceMode.getAppId());
        request.setCreateTime(xgDeviceMode.getCreateTime());
        request.setUserId(SPUtils.getInstance().getString(Constant.SP_USER_ID));
        request.setToken(XGUBTManager.getInstance().getDeviceToken());
        UbtLog.d("XGREquest","TOKEN:  "+XGUBTManager.getInstance().getDeviceToken());
        UbtLog.d("XGREquest", "url===" + HttpEntity.bindXGServer);
        UbtLog.d("XGREquest","BIND REQUEST "+GsonImpl.get().toJson(request));
        OkHttpUtils.postString()
                .addHeader("authorization", SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN))
                .url(HttpEntity.bindXGServer)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(GsonImpl.get().toJson(request))
                .id(id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d("XGREquest", "onError===" + e.getMessage()+"  xgCntxgCnt="+xgCnt);
                mView.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(xgCnt < 2) {
                            BindXGServer(xgDeviceMode);
                            xgCnt++;
                        }else{
                            xgCnt = 0;
                        }
                    }
                },2000);
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d("XGREquest", "response===" + response);
                SPUtils.getInstance().put(Constant.SP_XG_USERID,SPUtils.getInstance().getString(Constant.SP_USER_ID));
            }
        });
    }

    public void UnBindXGServer(String appId,String userId) {
        XGGetAccessIdRequest request = new XGGetAccessIdRequest();
        request.setAppId(appId);
        request.setUserId(userId);
        request.setToken(XGUBTManager.getInstance().getDeviceToken());
        UbtLog.d("XGREquest", "url===" + HttpEntity.unbindXGServer +"unbind UserId: "+userId);
        OkHttpUtils.postString()
                .addHeader("authorization", SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN))
                .url(HttpEntity.unbindXGServer)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(GsonImpl.get().toJson(request))
                .id(id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d("XGREquest", "unbind onError===" + e.getMessage());
            }
            @Override
            public void onResponse(String response, int id) {
                UbtLog.d("XGREquest", "unbind response===" + response);
            }
        });
    }

    private void Test() {
        //Enter the course enter
        byte[] papram = new byte[1];
        papram[0] = 0x01;
        commandRobotAction(ConstValue.DV_ENTER_COURSE, papram);
        //Exit the course enter
        papram[0] = 0x0;
        commandRobotAction(ConstValue.DV_ENTER_COURSE, papram);

    }

    @Override
    public void checkMyRobotState() {
        UbtLog.d(TAG, "click rl_hibits_event 2");
        LoadingDialog.show(AppManager.getInstance().currentActivity());
        CheckIsBindRequest checkRobotInfo = new CheckIsBindRequest();
        checkRobotInfo.setSystemType("3");
        String url = HttpEntity.CHECK_ROBOT_INFO;
        doRequest(url,checkRobotInfo,CHECK_ROBOT_INFO_HABIT);
    }

    @Override
    public void exitGlocalControlCenter() {
        //onlineAudioPlayerView.exitGlocalControlCenter();
        CommonCtrlView.closeCommonCtrlView();
    }

    @Override
    public void requestGlobalButtonControl(boolean status) {
        mView.showGlobalButtonAnmiationEffect(status);
    }

    @Override
    public void setView(MainContract.View view) {
        mView=view;
    }

    @Override
    public void resetGlobalActionPlayer() {
        UbtLog.d(TAG,"resetGlobalActionPlayer");
        MyActionsHelper.setLooping(false);
        requestGlobalButtonControl(false);
        ActionPlayer.getInstance().doStopPlay();
        ActionPlayer.getInstance().clearPlayingInfoList();
    }

    /**
     * 请求网络操作
     */
    public void doRequest(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequest onError:" + e.getMessage());
                switch (id){
                    case CHECK_ROBOT_INFO_HABIT:
                        mView.onGetRobotInfo(0,null);
                        LoadingDialog.dismiss(AppManager.getInstance().currentActivity());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                switch (id) {
                    case CHECK_ROBOT_INFO_HABIT:
                        LoadingDialog.dismiss(AppManager.getInstance().currentActivity());
                        BaseResponseModel<ArrayList<MyRobotModel>> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<ArrayList<MyRobotModel>>>() {
                                }.getType());//加上type转换，避免泛型擦除
                        if(!baseResponseModel.status || baseResponseModel.models == null){
                            mView.onGetRobotInfo(0,null);
                        }else {
                            if(baseResponseModel.models.size() == 0){
                                mView.onGetRobotInfo(2,null);
                                return;
                            }else {
                                UbtLog.d(TAG, "size = "+baseResponseModel.models.size());
                                UbtLog.d(TAG, "autoUpgrade = " + baseResponseModel.models.get(0).getAutoUpgrade());
                                UbtLog.d(TAG, "equipmentSeq = " + baseResponseModel.models.get(0).getEquipmentSeq());
                                UbtLog.d(TAG, "equipmentVersion = " + baseResponseModel.models.get(0).getEquipmentVersion());
                                mView.onGetRobotInfo(1,baseResponseModel.models.get(0));
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG,"notePlayStart");
    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG,"notePlayPause");
    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG,"notePlayContinue");
    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {
        UbtLog.d(TAG,"notePlayFinish");
    }

    @Override
    public void notePlayChargingError() {
        UbtLog.d(TAG,"notePlayChargingError");
    }

    @Override
    public void notePlayCycleNext(String action_name) {
        UbtLog.d(TAG," notePlayChargingError");

    }

    @Subscribe
    public void onXGRegisterSyncEvent(XGPushRegisterResult xgPushRegisterResult) {
        if (!TextUtils.isEmpty(xgPushRegisterResult.getToken())) {
            if(xgDeviceMode!=null) {
                BindXGServer(xgDeviceMode);
            }else {
                UbtLog.d(TAG,"xgDeviceMode==null");
            }
        }
    }
}
