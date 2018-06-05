package com.ubt.alpha1e_edu.data.model;

import com.ubt.alpha1e_edu.utils.log.UbtLog;

public class UserInfo extends BaseModel {

	public long userId;
	public String userName = "";
	public String userPassword = "";
	public String userEmail = "";
	public String userPhone = "";
	public String userStatus = "";
	public String userRoleType = "";
	public String userGender = "";
	public String userOnlyId = "";
	public String tokenExpires = "";
	public String userImage = "";
	public long activeDate;
	public long modifyDate;
	public String type = "";
	public String userRelationId = "";
	public String code = "";
	public String countryCode = "";
	public int userRelationType;
	public int loginUserId;
	private String openfireIp;

	/*新接口
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
	public String webappRoleId = "";*/

	private UserInfo thiz;



	@Override
	public synchronized UserInfo getThiz(String json) {

		try {
			//app 报错重启后，有时候为null
			if(mMapper == null){
				UbtLog.e("UserInfo","---mMapper:"+mMapper + "	---initMapper--");
				initMapper();
			}
			thiz = mMapper.readValue(json, UserInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}

	public static UserInfo doClone(UserInfo _thiz) {
		String info = UserInfo.getModelStr(_thiz);
		return new UserInfo().getThiz(info);
	}


	@Override
	public String toString() {
		//旧接口
		return "UserInfo{" +
				"userId=" + userId +
				", userName='" + userName + '\'' +
				", userPassword='" + userPassword + '\'' +
				", userEmail='" + userEmail + '\'' +
				", userPhone='" + userPhone + '\'' +
				", userStatus='" + userStatus + '\'' +
				", userRoleType='" + userRoleType + '\'' +
				", userGender='" + userGender + '\'' +
				", userOnlyId='" + userOnlyId + '\'' +
				", tokenExpires='" + tokenExpires + '\'' +
				", userImage='" + userImage + '\'' +
				", activeDate=" + activeDate +
				", modifyDate=" + modifyDate +
				", type='" + type + '\'' +
				", userRelationId='" + userRelationId + '\'' +
				", code='" + code + '\'' +
				", countryCode='" + countryCode + '\'' +
				", userRelationType=" + userRelationType +
				", thiz=" + thiz +
				'}';

		//新接口
		/*return "UserInfo{" +
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
				'}';*/
	}
}
