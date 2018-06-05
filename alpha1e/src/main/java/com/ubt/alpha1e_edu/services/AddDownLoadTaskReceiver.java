package com.ubt.alpha1e_edu.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ubt.alpha1e_edu.business.ResourcesDownloadManager;

public class AddDownLoadTaskReceiver extends BroadcastReceiver {

	private ResourcesDownloadManager mResourcesDownloadManager;
	public final static String ADD_DOWNLOAD_TASK_ACTION = "ADD_DOWNLOAD_TASK_ACTION";
	public static final String FILE_URL = "FILE_URL";
	public static final String FILE_PATH = "FILE_PATH";

	public AddDownLoadTaskReceiver(ResourcesDownloadManager manager) {
		mResourcesDownloadManager = manager;
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		mResourcesDownloadManager.addDownLoadTask(
				arg1.getExtras().getString(FILE_URL), arg1.getExtras()
						.getString(FILE_PATH));
	}
}
