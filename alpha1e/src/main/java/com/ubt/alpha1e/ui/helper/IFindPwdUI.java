package com.ubt.alpha1e.ui.helper;

import org.json.JSONObject;

import com.ubt.alpha1e.ui.helper.FindPwdHelper.Find_Fail_type;
import com.ubt.alpha1e.ui.helper.FindPwdHelper.Find_Type;

public interface IFindPwdUI {

	public void onStartFindPwd(boolean is_success, Find_Type type,
			JSONObject info);

	public void onNoteVCodeInvalid();

	public void doReSetPwd(String account, Find_Type type);

	public void onNoteResetPwd(Boolean obj);

	public void doReSetPwdFail(Find_Fail_type type);

}
