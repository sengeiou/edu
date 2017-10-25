package com.ubt.alpha1e.data.model;

import java.util.List;
import java.util.Locale;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class _BaseActionInfo {

	public List<FrameDataInfo> frame;
	public String actionname;
	public String versionname;
	public String actionVideoPath;
	public String actionImagePath;

	public _BaseActionInfo thiz;

	public _BaseActionInfo getThiz(JSONObject json) {

		try {
			JSONArray frame_list = json.getJSONArray("frame");
		} catch (JSONException e1) {
			try {
				JSONObject frame = json.getJSONObject("frame");
				JSONArray array = new JSONArray();
				array.put(frame);
				json.put("frame", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT,
				Boolean.TRUE);
		objectMapper.getSerializationConfig().setSerializationInclusion(
				Inclusion.ALWAYS);
		objectMapper.getDeserializationConfig()
				.set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);
		try {
			thiz = objectMapper
					.readValue(json.toString(), _BaseActionInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}

	public String getRealName(Context mContext) {
		String realName = "";
		Locale l = Locale.getDefault();
		String language = l.getLanguage();
		String[] names = this.actionname.split("@");
		for (int i = 0; i < names.length; i++) {
			String[] cn = names[i].split("#");
			if (language == "zh") {
				String ct = mContext.getResources().getConfiguration().locale
						.getCountry();
				if (cn[0].toLowerCase().equals(ct)) {
					realName = cn[1];
				}
			} else {
				if (cn[0].toLowerCase().equals(language)) {
					realName = cn[1];
				}
			}
		}
		String[] cn = names[0].split("#");
		realName = cn[1];
		return realName;
	}

}
