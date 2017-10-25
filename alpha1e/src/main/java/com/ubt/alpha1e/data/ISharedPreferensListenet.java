package com.ubt.alpha1e.data;

public interface ISharedPreferensListenet {

	public static final String RETURN_FAIL = "RETURN_FAIL";

	void onSharedPreferenOpreaterFinish(boolean isSuccess, long request_code,
			String value);

}