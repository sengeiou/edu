package com.ubt.alpha1e.xingepush;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.ubt.xingemodule.IXGListener;

import org.greenrobot.eventbus.EventBus;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/1/5 17:41
 * @描述:
 */

public class XGListener implements IXGListener {
    public static final String TAG = "XGListener";

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        Log.d(TAG,"onRegisterResult");
        EventBus.getDefault().post(xgPushRegisterResult);
    }

    @Override
    public void onUnregisterResult(Context context, int i) {
        Log.d(TAG,"onUnregisterResult");
        EventBus.getDefault().post(i);
    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {
        Log.d(TAG,"onSetTagResult");
        EventBus.getDefault().post(s);
    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {
        Log.d(TAG,"onDeleteTagResult");
        EventBus.getDefault().post(s);
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.d(TAG,"onTextMessage title"+xgPushTextMessage.getTitle() +"content  "+xgPushTextMessage.getContent());
        EventBus.getDefault().post(xgPushTextMessage);
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.d(TAG,"onNotifactionClickedResult");
        EventBus.getDefault().post(xgPushClickedResult);
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        Log.d(TAG,"onNotifactionShowedResult"+xgPushShowedResult.getContent());
        EventBus.getDefault().post(xgPushShowedResult);
    }
}
