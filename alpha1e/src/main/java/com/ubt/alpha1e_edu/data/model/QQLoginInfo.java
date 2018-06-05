package com.ubt.alpha1e_edu.data.model;

public class QQLoginInfo extends BaseModel {

	public int ret;
	public String openid;
	public String access_token;
	public String pay_token;
	public int expires_in;
	public String pf;
	public String pfkey;
	public String msg;
	public int login_cost;
	public String query_authority_cost;
	public int authority_cost;

	private QQLoginInfo thiz;

	@Override
	public QQLoginInfo getThiz(String json) {

		try {
			thiz = mMapper.readValue(json, QQLoginInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}
}
