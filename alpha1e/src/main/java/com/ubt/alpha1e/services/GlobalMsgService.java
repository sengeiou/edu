package com.ubt.alpha1e.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.android.tpush.XGPushShowedResult;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.ui.dialog.HibitsAlertDialog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.xingepush.XGCmdConstract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/1/8 15:16
 * @描述: 处理全局消息的服务
 */

public class GlobalMsgService extends Service {
    public static final String TAG = "GloabalMsgService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        UbtLog.i(TAG,"onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onDataSynEvent(XGPushShowedResult xgPushShowedResult) {
        UbtLog.i(TAG,"onDataSynEvent event---->" + xgPushShowedResult.getContent());
        try {
            JSONObject mJson = new JSONObject(xgPushShowedResult.getCustomContent());
            if(  mJson.getString("category").equals(XGCmdConstract.BEHAVIOUR_HABIT)) {
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
