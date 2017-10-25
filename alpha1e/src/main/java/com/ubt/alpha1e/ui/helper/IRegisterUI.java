package com.ubt.alpha1e.ui.helper;

import org.json.JSONObject;

public interface IRegisterUI {

	public void onNoteVCodeInvalid();

	public void onRegisterFinish(boolean is_success, JSONObject info,
			String error_info);

}
