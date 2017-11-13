package com.ubt.alpha1e.xingepush;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/8 15:00
 * @描述: 信鸽推送管理类
 * API 参考网址：http://docs.developer.qq.com/xg/android_access/api.html
 */

public class XGUBTManager {

    private final long ACCESS_ID = 2100270011;
    private final String ACCESS_KEY = "A783M4PIM7JI";

    private static XGUBTManager instance;

    private Context mContext;

    private XGUBTManager(Context context){
        mContext = context;
    }

    public static XGUBTManager getInstance(@NonNull Context context){
        if(instance == null){
            synchronized (XGUBTManager.class){
                if(instance == null){
                    instance = new XGUBTManager(context);
                }
            }
        }

        return instance;
    }

    //初始化信鸽推送
    public void initXG(long accessID, String accessKey){
        //开启信鸽日志输出
        XGPushConfig.enableDebug(mContext, false);
        XGPushConfig.setAccessId(mContext,accessID);
        XGPushConfig.setAccessKey(mContext,accessKey);
        //信鸽注册代码
        XGPushManager.registerPush(mContext, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Log.d("TPush", "注册成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }


    /**
     * 获取当前设备TOKEN
     * @return
     */
    public String getDeviceToken(){
        return XGPushConfig.getToken(mContext);
    }
}
