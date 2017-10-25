package com.ubt.alpha1e.net.http.basic;

public interface FileUploadListener {

	public enum State {
		busy, success, fail, connect_fail
	}

	public void onGetFileLenth(long request_code, int file_lenth);

	public void onReportProgress(long request_code, double progess);

	public void onUpLoadFileFinish(long request_code,String json, State state);
}
