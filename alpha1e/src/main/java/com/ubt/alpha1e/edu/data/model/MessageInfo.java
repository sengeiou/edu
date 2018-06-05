package com.ubt.alpha1e.edu.data.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MessageInfo extends BaseModel {

	public int countryCode;
	public long messageId;
	public String messageContent;
	public long createdTime;
	public String messageUsers;
	public int creator;
	public String messageDescription;
	public String title;
	public String pushTime;
	public int messageType;
	public int pushType;
	public String extra;
	public int type;

	public MessageSubInfo subInfo;


	public MessageInfo thiz;

	@Override
	public MessageInfo getThiz(String json) {
		try {
			thiz = mMapper.readValue(json, MessageInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}

	public static ArrayList<MessageInfo> getModelList(String json) {
		ArrayList<MessageInfo> result = new ArrayList<MessageInfo>();
		try {
			JSONArray j_list = new JSONArray(json);
			for (int i = 0; i < j_list.length(); i++) {
				result.add(new MessageInfo().getThiz(j_list.get(i).toString()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getModeslStr(ArrayList<MessageInfo> infos) {

		try {
			return mMapper.writeValueAsString(infos);
		} catch (Exception e) {
			String error = e.getMessage();
			return Convert_fail;
		}
	}

}
