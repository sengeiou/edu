package com.ubt.alpha1e.edu.data;

public interface ISharedPreferensListenet {

	public static final String RETURN_FAIL = "RETURN_FAIL";

	void onSharedPreferenOpreaterFinish(boolean isSuccess, long request_code,
			String value);

}