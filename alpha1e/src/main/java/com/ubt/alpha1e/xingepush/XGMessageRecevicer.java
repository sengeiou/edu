package com.ubt.alpha1e.xingepush;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/8 15:04
 * @描述: 消息接收Receiver
 * API 参考网址：http://docs.developer.qq.com/xg/android_access/api.html
 */

public class XGMessageRecevicer extends XGPushBaseReceiver {
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
        Log.d("TPush","onNotifactionShowedResult result="+xgPushShowedResult.getContent());
    }
}
