package com.ubt.alpha1e.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.GetCourseProgressRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.DataTools;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * 先读uid，如果读不到有效的uid则认为机器是没升级bin的旧机器直接跳过。 如果可以读到uid，读不到有效的sn，则分配sn并写入。
 * 如果可以读到uid，也可以读到sn，直接激活
 */

public class SyncDataService extends Service {

	private static final String TAG = SyncDataService.class.getSimpleName();

	private static final int GET_COURSE_PROGRESS  = 1;

	private Handler mHandler = new Handler();

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
				doGetCourseProgress(1);
			}
		});
	}

	@Override
	public int onStartCommand(Intent intent, int flags,
			int startId) {

		doTryActivation();

		// 如果在执行完onStartCommand后，服务才被异常kill掉，则系统不会自动重启该服务。
		return START_REDELIVER_INTENT;
	}

	public void doGetCourseProgress(int type) {
		GetCourseProgressRequest getCourseProgressRequest = new GetCourseProgressRequest();
		getCourseProgressRequest.setType(type);

		String url = HttpEntity.GET_COURSE_PROGRESS;
		doRequestFromWeb(url,getCourseProgressRequest,GET_COURSE_PROGRESS);
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
					case GET_COURSE_PROGRESS:
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								UbtLog.d(TAG,"--stopSelf--1");
								SyncDataService.this.stopSelf();
							}
						});

						break;
				}
			}

			@Override
			public void onResponse(String response, int id) {
				UbtLog.d(TAG,"response = " + response);
				//BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());

				switch (id){
					case GET_COURSE_PROGRESS:
						try {
							if (JsonTools.getJsonStatus(response) && JsonTools.getJsonModel(response) != null) {
								JSONObject jsonObject = JsonTools.getJsonModel(response);
								String progressOne = jsonObject.getString("progressOne");
								UbtLog.d(TAG,"jsonObject = " + jsonObject);
								if(TextUtils.isEmpty(progressOne) && isStringNumber(progressOne)){

									SPUtils.getInstance().put(Constant.PRINCIPLE_PROGRESS + SPUtils.getInstance().getString(Constant.SP_USER_ID),progressOne);
									UbtLog.d(TAG,"progressOne == " + progressOne);
								}else {
									UbtLog.d(TAG,"progressOne => " + progressOne);
								}
							}

							mHandler.post(new Runnable() {
								@Override
								public void run() {
									UbtLog.d(TAG,"--stopSelf--2");
									SyncDataService.this.stopSelf();
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
