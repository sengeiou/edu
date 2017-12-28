package com.ubt.alpha1e.xingepush;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.ui.dialog.HibitsAlertDialog;
import com.ubt.alpha1e.ui.dialog.LowBatteryDialog;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.utils.Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/8 15:04
 * @描述: 消息接收Receiver
 * API 参考网址：http://docs.developer.qq.com/xg/android_access/api.html
 */

public class  XGMessageRecevicer extends XGPushBaseReceiver {
    private String BEHAVIOUR_HABIT="1";
    private String COMMUNITY="2";
    BehaviorHabitsPresenter mPresenter;
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    //应用内消息推送，默认不会再通知栏显示
    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.d("TPush","msgContent:"+xgPushTextMessage.getContent()+
                "   title:"+xgPushTextMessage.getTitle());
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.d("TPush","onNotifactionClickedResult content="+xgPushClickedResult.getContent());
    }

    //通知栏消息，会自动显示到通知栏里，样式可定制参考类说明网址
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        if(xgPushShowedResult==null){
            return;
        }
        Log.d("TPush","onNotifactionShowedResult result="+xgPushShowedResult.getCustomContent().toString()+" content=="+xgPushShowedResult.getContent());
        //BehaviourHabit event Dialog
                JSONObject mJson = null;
                try {
                    mJson = new JSONObject(xgPushShowedResult.getCustomContent().toString());
                    if(  mJson.getString("category").equals(BEHAVIOUR_HABIT)) {
                        if (mJson.get("eventId") != null) {
                            Log.d("TPush"," contents"+xgPushShowedResult.getContent());
                            new HibitsAlertDialog(AppManager.getInstance().currentActivity()).builder()
                                    .setCancelable(true)
                                    .setEventId(mJson.get("eventId").toString())
                                    .setMsg(xgPushShowedResult.getContent())
                                    .show();

                          //  new LowBatteryDialog(AppManager.getInstance().currentActivity()).setBatteryThresHold(1000000).builder().show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
}
