package com.ubt.alpha1e.data.model;

import com.ubt.alpha1e.utils.log.UbtLog;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public abstract class BaseModel {

	public String token;
	public String accessToken;
	public String appType;
	public String serviceVersion;
	public String requestKey;
	public String requestTime;
	public String systemLanguage;
	public static final String Convert_fail = "Convert_fail";

	protected static ObjectMapper mMapper;

	static {
		mMapper = new ObjectMapper();
		mMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT,
				Boolean.TRUE);
		mMapper.getSerializationConfig().setSerializationInclusion(
				Inclusion.ALWAYS);
		mMapper.getDeserializationConfig()
				.set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);
	}

	public static void initMapper(){
		if(mMapper == null){
			mMapper = new ObjectMapper();
			mMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT,
					Boolean.TRUE);
			mMapper.getSerializationConfig().setSerializationInclusion(
					Inclusion.ALWAYS);
			mMapper.getDeserializationConfig()
					.set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
							false);
		}
	}

	public abstract BaseModel getThiz(String json);

	public static String getModelStr(BaseModel info) {

		try {
			//app 报错重启后，有时候为null
			if(mMapper == null){
				UbtLog.e("BaseModel","---mMapper:"+mMapper + "	---initMapper--");
				initMapper();
			}
			return mMapper.writeValueAsString(info);
		} catch (Exception e) {
			String error = e.getMessage();
			return Convert_fail;
		}
	}

}
