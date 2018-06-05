package com.ubt.alpha1e_edu.ui.helper;

import org.json.JSONObject;

import com.ubt.alpha1e_edu.business.thrid_party.IWeiXinListener;
import com.ubt.alpha1e_edu.data.model.UserInfo;

public interface ILoginUI extends IWeiXinListener {
	public void onLoginFinish(boolean is_success, JSONObject info,
			String error_str);

	public void onNoteNameEmpty();

	public void onNotePassEmpty();

	public void onThridLogin();

	public void onThridLoginFinish(boolean is_success, boolean is_registed,
			UserInfo info);

	public void onCompleteCountry(UserInfo current_uer);

}
