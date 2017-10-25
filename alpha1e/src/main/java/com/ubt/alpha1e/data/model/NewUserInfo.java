package com.ubt.alpha1e.data.model;

import com.ubt.alpha1e.utils.log.UbtLog;

public class NewUserInfo extends BaseModel {

	//新接口
	public String activeDate = "";
	public String countryCode = "";
	public String countryName = "";
	public int emailVerify;
	public String modifyDate = "";
	public String nickName = "";
	public int registerType;
	public String userBirthday = "";
	public String userEmail = "";
	public String userGender = "";
	public long userId;
	public String userImage = "";
	public String userName = "";
	public String userOnlyId = "";
	public String userOtherName = "";
	public String userPassword = "";
	public String userPhone = "";
	public String userRelationId  = "";
	public String userRelationType  = "";
	public String userRoleType   = "";
	public String userStatus = "";
	public String webappRoleId = "";

	private NewUserInfo thiz;

	@Override
	public synchronized NewUserInfo getThiz(String json) {

		try {
			//app 报错重启后，有时候为null
			if(mMapper == null){
				UbtLog.e("NewUserInfo","---mMapper:"+mMapper + "	---initMapper--");
				initMapper();
			}
			thiz = mMapper.readValue(json, NewUserInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}

	public static NewUserInfo doClone(NewUserInfo _thiz) {
		String info = NewUserInfo.getModelStr(_thiz);
		return new NewUserInfo().getThiz(info);
	}


	@Override
	public String toString() {
		//新接口
		return "NewUserInfo{" +
				"userId=" + userId +
				", userName='" + userName + '\'' +
				", nickName='" + nickName + '\'' +
				", userOtherName='" + userOtherName + '\'' +
				", userBirthday='" + userBirthday + '\'' +
				", userPassword='" + userPassword + '\'' +
				", userEmail='" + userEmail + '\'' +
				", emailVerify='" + emailVerify + '\'' +
				", userPhone='" + userPhone + '\'' +
				", userStatus='" + userStatus + '\'' +
				", userRoleType='" + userRoleType + '\'' +
				", userGender='" + userGender + '\'' +
				", userOnlyId='" + userOnlyId + '\'' +
				", userImage='" + userImage + '\'' +
				", activeDate='" + activeDate + '\'' +
				", modifyDate='" + modifyDate + '\'' +
				", registerType='" + registerType + '\'' +
				", userRelationId='" + userRelationId + '\'' +
				", webappRoleId='" + webappRoleId + '\'' +
				", countryCode='" + countryCode + '\'' +
				", countryName='" + countryName + '\'' +
				", userRelationType=" + userRelationType +
				'}';
	}
}
