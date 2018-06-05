package com.ubt.alpha1e_edu.data.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class MessageRecordInfo extends BaseModel {
	public long messageId;
	public long userId;
	public boolean isRead;

	public MessageRecordInfo thiz;

	@Override
	public MessageRecordInfo getThiz(String json) {
		try {
			thiz = mMapper.readValue(json, MessageRecordInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}

	public static String getModeslStr(ArrayList<MessageRecordInfo> infos) {

		try {
			return mMapper.writeValueAsString(infos);
		} catch (Exception e) {
			String error = e.getMessage();
			return Convert_fail;
		}
	}

	public static ArrayList<MessageRecordInfo> getModelList(String json) {

		ArrayList<MessageRecordInfo> result = new ArrayList<MessageRecordInfo>();
		try {
			JSONArray j_list = new JSONArray(json);
			for (int i = 0; i < j_list.length(); i++) {
				result.add(new MessageRecordInfo().getThiz(j_list.get(i)
						.toString()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

}
