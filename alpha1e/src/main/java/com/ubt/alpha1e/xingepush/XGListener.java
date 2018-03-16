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
        Log.d(TAG, "onRegisterResult");
        EventBus.getDefault().post(xgPushRegisterResult);
    }

    @Override
    public void onUnregisterResult(Context context, int i) {
        Log.d(TAG, "onUnregisterResult");
        EventBus.getDefault().post(i);
    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {
        Log.d(TAG, "onSetTagResult");
        EventBus.getDefault().post(s);
    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {
        Log.d(TAG, "onDeleteTagResult");
        EventBus.getDefault().post(s);
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.d(TAG, "onTextMessage title" + xgPushTextMessage.getTitle() + "content  " + xgPushTextMessage.getContent());
        EventBus.getDefault().post(xgPushTextMessage);
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.d(TAG, "onNotifactionClickedResult"+xgPushClickedResult.toString());
        String text = "";
        if (xgPushClickedResult.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :";
        } else if (xgPushClickedResult.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :";
        }
        Log.d(TAG, "onNotifactionClickedResult==" + text);
        EventBus.getDefault().post(xgPushClickedResult);
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        Log.d(TAG, "onNotifactionShowedResult" + xgPushShowedResult.getContent());
        EventBus.getDefault().post(xgPushShowedResult);


    }
}
