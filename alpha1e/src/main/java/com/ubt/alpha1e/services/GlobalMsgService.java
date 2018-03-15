package com.ubt.alpha1e.services;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.android.tpush.XGPushShowedResult;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.ui.StartInitSkinActivity;
import com.ubt.alpha1e.ui.dialog.HibitsAlertDialog;
import com.ubt.alpha1e.utils.NotifyUtil;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.xingepush.XGCmdConstract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/1/8 15:16
 * @描述: 处理全局消息的服务
 */

public class GlobalMsgService extends Service {
    public static final String TAG = "GloabalMsgService";
    private int GET_HABITEVENT = 999;

    private int requestCode = (int) SystemClock.uptimeMillis();
    /**
     * 通知id生成器
     */
    private static AtomicInteger sIdGenerator = new AtomicInteger(0);
    private static final int MAX_NOTIFICATION_SIZE = 5;
    private int id = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        UbtLog.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UbtLog.i(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onDataSynEvent(XGPushShowedResult xgPushShowedResult) {
        UbtLog.i(TAG, "onDataSynEvent event---->" +xgPushShowedResult.toString());
        try {
            JSONObject mJson = new JSONObject(xgPushShowedResult.getCustomContent());
            if (mJson.getString("category").equals(XGCmdConstract.BEHAVIOUR_HABIT)) {
                if (mJson.get("eventId") != null) {
                    Log.d("TPush", " contents" + xgPushShowedResult.getContent());
                    new HibitsAlertDialog(AppManager.getInstance().currentActivity()).builder()
                            .setCancelable(true)
                            .setEventId(mJson.get("eventId").toString())
                            .setMsg(xgPushShowedResult.getContent())
                            .show();
                    //requireBehaviourNextEvent();

                   // showNotify(NOTIFICATION_HABIT_TOFORNT, xgPushShowedResult);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 高仿网易新闻
     */
    private void showNotify(String Action, XGPushShowedResult xgPushShowedResult) {
        IntentFilter filter_click = new IntentFilter();
        filter_click.addAction(Action);
        //注册广播
        registerReceiver(receiver_onclick, filter_click);

        Intent Intent_pre = new Intent(Action);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, Intent_pre, 0);

        int smallIcon = R.drawable.ic_launcher;
        String ticker = "您有一条新通知";
        String title = xgPushShowedResult.getTitle();
        String content = xgPushShowedResult.getContent();
        //实例化工具类，并且调用接口
        NotifyUtil notify2 = new NotifyUtil(GlobalMsgService.this, id);
        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, content, true, true, false);

    }


    public static final String NOTIFICATION_HABIT_TOFORNT = "com.ubt.alpha1e.habit.front";
    public static final String NOTIFICATION_USER_CENTER = "com.ubt.alpha1e.usercenter";


    private BroadcastReceiver receiver_onclick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NOTIFICATION_HABIT_TOFORNT)) {
                Log.d("MyService", "点击通知栏");
                if (MyLifecycleHandler.isIsForeground()) {

                } else {
                    ActivityManager am = (ActivityManager) GlobalMsgService.this.getSystemService(Context.ACTIVITY_SERVICE);
                    Activity activity = AppManager.getInstance().currentActivity();
                    if (null != activity && !activity.isFinishing()) {
                        am.moveTaskToFront(activity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
                    } else {
                        Intent intent1 = new Intent(GlobalMsgService.this, StartInitSkinActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                    }
                }
            } else if (intent.getAction().equals(NOTIFICATION_USER_CENTER)) {//跳转个人中心

            }
        }
    };

}
