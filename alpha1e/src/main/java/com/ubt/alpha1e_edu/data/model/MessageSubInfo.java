package com.ubt.alpha1e_edu.data.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MessageSubInfo extends BaseModel {

	public String userName;
	public String userImage;
	public long actionId;
	public String actionName;
	public String actionImagePath;
	public String actionHeadUrl;
	public String actionResume;
	public String actionPath;
	public String actionVideoPath;
	public String actionUserName;

	public String collectTime;
	public String commentTime;
	public String reCommentContext;
	public String reCommentUser;
	public String commentContext;

	public MessageSubInfo thiz;

	@Override
	public MessageSubInfo getThiz(String json) {
		try {
			thiz = mMapper.readValue(json, MessageSubInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}

	public static ArrayList<MessageSubInfo> getModelList(String json) {
		ArrayList<MessageSubInfo> result = new ArrayList<MessageSubInfo>();
		try {
			JSONArray j_list = new JSONArray(json);
			for (int i = 0; i < j_list.length(); i++) {
				result.add(new MessageSubInfo().getThiz(j_list.get(i).toString()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getModeslStr(ArrayList<MessageSubInfo> infos) {

		try {
			return mMapper.writeValueAsString(infos);
		} catch (Exception e) {
			String error = e.getMessage();
			return Convert_fail;
		}
	}

}
