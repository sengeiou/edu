package com.ubt.alpha1e.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.BehaviourDelayAlertRequest;
import com.ubt.alpha1e.base.RequstMode.GetCourseProgressRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * 先读uid，如果读不到有效的uid则认为机器是没升级bin的旧机器直接跳过。 如果可以读到uid，读不到有效的sn，则分配sn并写入。
 * 如果可以读到uid，也可以读到sn，直接激活
 */

public class HibitsAlertService extends Service {

	private static final String TAG = HibitsAlertService.class.getSimpleName();

	private static final int SAVE_HIBITS_EVENT_ALERT  = 1;

	String url = "http://10.10.1.14:8080/alpha1e/event/remindReply";

	private Handler mHandler = new Handler();

	public static String mEventId = "";
	public static String mDelayTime = "";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		UbtLog.d(TAG, "onDestroy");
		super.onDestroy();
	}

	private void doTryActivation() {
		if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constant.SP_USER_ID))) {
			UbtLog.d(TAG, "未登录返回");
			this.stopSelf();
			// 如果在执行完onStartCommand后，服务才被异常kill掉，则系统不会自动重启该服务。
			return;
		}

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				doSaveHibitsAlert();
			}
		});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null){
			mEventId = intent.getStringExtra("EventId");
			mDelayTime = intent.getStringExtra("DelayTime");
		}
		UbtLog.d(TAG,"mEventId = " + mEventId + "	mDelayTime = " + mDelayTime);

		doTryActivation();
		// 如果在执行完onStartCommand后，服务才被异常kill掉，则系统不会自动重启该服务。
		return START_REDELIVER_INTENT;
	}

	public void doSaveHibitsAlert() {

		BehaviourDelayAlertRequest mBehaviourDelayAlertRequest = new BehaviourDelayAlertRequest();
		mBehaviourDelayAlertRequest.setEventId(mEventId);
		mBehaviourDelayAlertRequest.setDelayTime(mDelayTime);
		doRequestFromWeb(url, mBehaviourDelayAlertRequest, SAVE_HIBITS_EVENT_ALERT);
	}

	/**
	 * 请求网络操作
	 */
	public void doRequestFromWeb(String url, BaseRequest baseRequest, int requestId) {

		OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				UbtLog.d(TAG, "doRequestFromWeb onError:" + e.getMessage());
				switch (id){
					case SAVE_HIBITS_EVENT_ALERT:
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								UbtLog.d(TAG,"--stopSelf--1");
								ToastUtils.showShort(getResources().getString(R.string.ui_habits_alert_set_fail));
								HibitsAlertService.this.stopSelf();
							}
						});

						break;
				}
			}

			@Override
			public void onResponse(String response, int id) {
				UbtLog.d(TAG,"response = " + response);
				switch (id){
					case SAVE_HIBITS_EVENT_ALERT:
						try {
							BaseResponseModel mbaseResponseModel = GsonImpl.get().toObject(response,
									new TypeToken<BaseResponseModel>() {
									}.getType());
							if(mbaseResponseModel.status){
								ToastUtils.showShort(getResources().getString(R.string.ui_habits_alert_set_success));
							}else {
								ToastUtils.showShort(getResources().getString(R.string.ui_habits_alert_set_fail));
							}
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									UbtLog.d(TAG,"--stopSelf--2");
									HibitsAlertService.this.stopSelf();
								}
							});

						}catch (Exception ex){
							UbtLog.e(TAG,ex.getMessage());
						}
						break;
				}

			}
		});

	}

	/***
	 * 判断字符串是否都是数字
	 */
	public  boolean isStringNumber(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

}
