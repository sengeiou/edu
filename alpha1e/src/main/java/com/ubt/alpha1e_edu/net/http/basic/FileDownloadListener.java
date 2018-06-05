package com.ubt.alpha1e_edu.net.http.basic;

public interface FileDownloadListener {

	public enum State {
		busy, success, fail, connect_fail
	}

	public void onGetFileLenth(long request_code, double file_lenth);

	public void onStopDownloadFile(long request_code, State state);

	public void onReportProgress(long request_code, double progess);

	public void onDownLoadFileFinish(long request_code, State state);
}
