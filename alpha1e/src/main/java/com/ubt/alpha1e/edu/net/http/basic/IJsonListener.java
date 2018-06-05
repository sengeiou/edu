package com.ubt.alpha1e.edu.net.http.basic;

public interface IJsonListener {
	public static final String RETURN_FAIL = "RETURN_FAIL";

	void onGetJson(boolean isSuccess, String json, long request_code);
}
