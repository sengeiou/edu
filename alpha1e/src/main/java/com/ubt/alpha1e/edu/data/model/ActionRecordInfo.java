package com.ubt.alpha1e.edu.data.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class ActionRecordInfo extends BaseModel {

	public ActionInfo action;
	public boolean isDownLoadSuccess;

	public ActionRecordInfo thiz;

	public ActionRecordInfo() {
		super();
	}

	public ActionRecordInfo(ActionInfo _info, boolean _isDownLoadSuccess) {
		super();
		action = _info;
		isDownLoadSuccess = _isDownLoadSuccess;
	}

	@Override
	public ActionRecordInfo getThiz(String json) {

		try {
			thiz = mMapper.readValue(json, ActionRecordInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}

	public static String getModeslStr(ArrayList<ActionRecordInfo> infos) {

		try {
			return mMapper.writeValueAsString(infos);
		} catch (Exception e) {
			String error = e.getMessage();
			return Convert_fail;
		}
	}

	public static ArrayList<ActionRecordInfo> getModelList(String json) {

		ArrayList<ActionRecordInfo> result = new ArrayList<ActionRecordInfo>();
		try {
			JSONArray j_list = new JSONArray(json);
			for (int i = 0; i < j_list.length(); i++) {
				result.add(new ActionRecordInfo().getThiz(j_list.get(i)
						.toString()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
}
