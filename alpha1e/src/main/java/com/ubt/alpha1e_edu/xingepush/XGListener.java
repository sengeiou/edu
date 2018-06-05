package com.ubt.alpha1e_edu.xingepush;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.ubt.alpha1e_edu.base.Constant;
import com.ubt.alpha1e_edu.base.SPUtils;
import com.ubt.alpha1e_edu.login.HttpEntity;
import com.ubt.alpha1e_edu.userinfo.mainuser.UserCenterActivity;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.ubt.alpha1e_edu.webcontent.WebContentActivity;
import com.ubt.xingemodule.IXGListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

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
        Log.d(TAG, "onNotifactionClickedResult" + xgPushClickedResult.toString());
        String text = "";
        if (xgPushClickedResult.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :";
            try {
                JSONObject mJson = new JSONObject(xgPushClickedResult.getCustomContent());
                if (mJson.getString("category").equals(XGCmdConstract.USER_CENTER)) {
                    Intent intent = new Intent(context, UserCenterActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(UserCenterActivity.USER_CURRENT_POSITION, 2);
                    context.startActivity(intent);
                } else if (mJson.getString("category").equals(XGCmdConstract.HABIT_TOTAL)) {
                    // StatisticsActivity.launchActivity(context);
                    String userId = SPUtils.getInstance().getString(Constant.SP_USER_ID);
                    String token = SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN);
                    String statisticsUrl = HttpEntity.HABIT_STATIS_URL + "?" + "userid=" + userId + "&" + "token=" + token;

                    UbtLog.d(TAG, "statisticsUrl = " + statisticsUrl);

                    WebContentActivity.launchActivity(context, statisticsUrl, "");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (xgPushClickedResult.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            //
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
