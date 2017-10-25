package com.ubt.alpha1e.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.ui.ActionsLibPreviewWebActivity;
import com.ubt.alpha1e.ui.WebContentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class MyJPushReceiver extends BroadcastReceiver {

	private static final String J_MESSAGE_TYPE = "message_type";
	private static final String J_MESSAGE_ID = "push_object_id";
	private static final String J_MESSAGE_CONTEXT = "message_context";
	public String message_type = null;// 消息类型
	public String push_object_id = null;// 动作id
	public String message_content = null;// 消息内容
	public String intentString;

	@Override
	public void onReceive(final Context context, Intent intent) {

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			intentString = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
			message_type = getValueFromString(intentString, J_MESSAGE_TYPE);
			push_object_id = getValueFromString(intentString, J_MESSAGE_ID);
			message_content = getValueFromString(intentString,
					J_MESSAGE_CONTEXT);
			Intent i = null;
			if (message_type != null && "1".equals(message_type))
			// 如果是1则跳到对应的动作页面
			{
				ActionInfo info = new ActionInfo();
				info.actionId = Long.valueOf(push_object_id);
				info.actionDesciber = "";
				info.actionName = "";
//				i = new Intent(context, ActionsLibPreViewActivity.class);
//				i.putExtra(ActionsLibHelper.Action_key,
//						ActionInfo.getModelStr(info));
//				i.putExtra(ActionsLibHelper.Start_type_key,
//						ActionsLibHelper.Start_type.message_type);
//				i.putExtra(ActionsLibHelper.Action_message_key, message_content);
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				ActionsLibPreviewWebActivity.launchActivity(context,info,0,0,true);
				return;

			} else if (message_type != null && "2".equals(message_type))//
			// 如果是2则是url，直接打开
			{
				Uri uri = Uri.parse(push_object_id);// 该字段存放url
				i = new Intent(context, WebContentActivity.class);
				i.putExtra(WebContentActivity.WEB_TITLE, "");
				i.putExtra(WebContentActivity.WEB_URL, uri.toString());
				i.putExtra(WebContentActivity.FROM_NOTICE, true);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			} /*else {
				System.out.println("ACTION_NOTIFICATION_OPENED:"
						+ intent.getStringExtra(JPushInterface.EXTRA_EXTRA)
						+ " " + message_content + " "
						+ intent.getStringExtra(JPushInterface.EXTRA_PUSH_ID));
				i = new Intent(context, TestActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}*/

		}
	}

	private String getValueFromString(String str, String key) {
		try {
			return new JSONObject(str).getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
