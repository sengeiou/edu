package com.ubt.alpha1e.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourControlRequest;
import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.ui.dialog.HibitsAlertDialog;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.xingepush.XGCmdConstract;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/1/8 15:16
 * @描述: 处理全局消息的服务
 */

public class GlobalMsgService extends Service {
    public static final String TAG = "GloabalMsgService";
    private int GET_HABITEVENT=999;

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
        UbtLog.i(TAG,"onDestroy");
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onDataSynEvent(XGPushShowedResult xgPushShowedResult) {
        UbtLog.i(TAG,"onDataSynEvent event---->" + xgPushShowedResult.getContent());
        try {
            JSONObject mJson = new JSONObject(xgPushShowedResult.getCustomContent());
            if( mJson.getString("category").equals(XGCmdConstract.BEHAVIOUR_HABIT)) {
                if (mJson.get("eventId") != null) {
                    Log.d("TPush"," contents"+xgPushShowedResult.getContent());
                    new HibitsAlertDialog(AppManager.getInstance().currentActivity()).builder()
                            .setCancelable(true)
                            .setEventId(mJson.get("eventId").toString())
                            .setMsg(xgPushShowedResult.getContent())
                            .show();
                    //requireBehaviourNextEvent();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
