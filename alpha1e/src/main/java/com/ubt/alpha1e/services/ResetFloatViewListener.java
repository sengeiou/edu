package com.ubt.alpha1e.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */


public class ResetFloatViewListener  {

    public static String TAG = "ResetFloatViewListener";
    public static String INTENT_FILTER = "com.ubt.alpha1e.resetFloatView";
    private onResetFloatViewListener mListener;
    private Context mContext;
    private ResetFloatViewBroadcast receiver;
    private IntentFilter mFilter;

    public ResetFloatViewListener(Context context){
        mContext = context;
        mFilter = new IntentFilter(INTENT_FILTER);
    }

    public interface onResetFloatViewListener{
         void resetFloatView(boolean reset);
    }

    public void setResetFloatViewListener(onResetFloatViewListener listener){
        mListener = listener;
        receiver = new ResetFloatViewBroadcast();

    }

    public void startListener(){

        if (receiver != null) {
            mContext.registerReceiver(receiver, mFilter);
        }

    }

    public void stopListiener(){
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    class ResetFloatViewBroadcast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            UbtLog.d(TAG, "----onReceive!");
            if(mListener !=null){
                mListener.resetFloatView(true);
            }
        }
    }



}
