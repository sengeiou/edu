package com.ubt.alpha1e_edu.data.model;

public class WeiXinUserInfo extends BaseModel {

	public String openid;
	public String nickname;
	public String sex;
	public String language;
	public String city;
	public String province;
	public String country;
	public String headimgurl;
	public String[] privilege;
	public String unionid;

	private WeiXinUserInfo thiz;

	@Override
	public WeiXinUserInfo getThiz(String json) {

		try {
			thiz = mMapper.readValue(json, WeiXinUserInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}
}
