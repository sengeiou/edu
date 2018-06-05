package com.ubt.alpha1e.edu.data.model;

public class QQUserInfo extends BaseModel {

	public int ret;
	public String msg;
	public int is_lost;
	public String nickname;
	public String gender;
	public String province;
	public String city;
	public String figureurl;
	public String figureurl_1;
	public String figureurl_2;
	public String figureurl_qq_1;
	public String figureurl_qq_2;
	public String is_yellow_vip;
	public String vip;
	public String yellow_vip_level;
	public String level;
	public String is_yellow_year_vip;

	private QQUserInfo thiz;

	@Override
	public QQUserInfo getThiz(String json) {

		try {
			thiz = mMapper.readValue(json, QQUserInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}
}
