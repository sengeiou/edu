package com.ubt.alpha1e.data.model;

public class WeiXinLoginInfo extends BaseModel {

	public String access_token;
	public String expires_in;
	public String refresh_token;
	public String openid;
	public String scope;
	public String unionid;

	private WeiXinLoginInfo thiz;

	@Override
	public WeiXinLoginInfo getThiz(String json) {

		try {
			thiz = mMapper.readValue(json, WeiXinLoginInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}
}
