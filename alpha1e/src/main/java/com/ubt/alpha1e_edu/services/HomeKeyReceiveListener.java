package com.ubt.alpha1e_edu.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ubt.alpha1e_edu.utils.log.UbtLog;

/**
 * 类名
 *
 * @author wmma
 * @description 实现对Home按键的监听
 * @date  2016/09/05
 * @update
 */


public class HomeKeyReceiveListener {

    private static final String TAG = "HomeKeyReceiveListener";
    private  Context mContext;
    private IntentFilter mFilter;
    private OnHomePressedListener mListener;
    private InnerRecevier mRecevier;

    public interface OnHomePressedListener{

         void onHomePressed();
         void onHomeLongPressed();
         void onScreenOff();

    }

    public HomeKeyReceiveListener(Context context) {
        mContext = context;
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
    }

    public void setOnHomePressedListener(OnHomePressedListener listener) {
        mListener = listener;
        mRecevier = new InnerRecevier();
    }

    public void startListener(){

        if (mRecevier != null) {
            mContext.registerReceiver(mRecevier, mFilter);
        }

    }

    public void stopListiener(){
        if (mRecevier != null) {
            mContext.unregisterReceiver(mRecevier);
        }
    }



    class InnerRecevier extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    UbtLog.e(TAG, "--wmma--action:" + action + ",reason:" + reason);
                    if (mListener != null) {
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            // 短按home键
                            mListener.onHomePressed();
                        } else if (reason
                                .equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                            // 长按home键
                            mListener.onHomeLongPressed();
                        }
                    }
                }
            }

            if(action.equals(Intent.ACTION_SCREEN_OFF)){
                mListener.onScreenOff();
            }
        }
    }





}
