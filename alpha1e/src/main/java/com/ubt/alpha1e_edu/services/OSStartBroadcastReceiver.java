package com.ubt.alpha1e_edu.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OSStartBroadcastReceiver extends BroadcastReceiver {
	public static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	public void onReceive(Context context, Intent intent) {
		if (1 == 1) {
			if (intent.getAction().equals(ACTION)) {
				Intent i = new Intent(Intent.ACTION_RUN);
				i.setClass(context, GetResourcesService.class);
				context.startService(i);
			}
		}
	}
}
