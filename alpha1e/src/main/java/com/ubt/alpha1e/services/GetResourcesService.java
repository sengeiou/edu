package com.ubt.alpha1e.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.ubt.alpha1e.business.ResourcesDownloadManager;
import com.ubt.alpha1e.utils.log.MyLog;

public class GetResourcesService extends Service {

	private AddDownLoadTaskReceiver mAddDownLoadTaskReceiver;

	private ResourcesDownloadManager mResourcesDownloadManager;

	@Override
	public IBinder onBind(Intent arg0) {
		MyLog.writeLog("Service_test", this.hashCode() + "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		MyLog.writeLog("Service_test", this.hashCode() + "onCreate");
		mResourcesDownloadManager = ResourcesDownloadManager.getInstance(this);
		mAddDownLoadTaskReceiver = new AddDownLoadTaskReceiver(
				mResourcesDownloadManager);
		this.registerReceiver(mAddDownLoadTaskReceiver, new IntentFilter(
				AddDownLoadTaskReceiver.ADD_DOWNLOAD_TASK_ACTION));
//		mResourcesDownloadManager.startDownloadBanas();不用下载banner
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(mAddDownLoadTaskReceiver);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(android.content.Intent intent, int flags,
			int startId) {
		MyLog.writeLog("Service_test", this.hashCode() + "onStartCommand");
		// 如果在执行完onStartCommand后，服务才被异常kill掉，则系统不会自动重启该服务。
		return START_REDELIVER_INTENT;
	}

}
