package com.ubt.alpha1e_edu.data;

import android.graphics.Bitmap;

import com.ubt.alpha1e_edu.data.FileTools.State;

public interface IFileListener {
	public void onReadImageFinish(Bitmap img, long request_code);

	public void onReadFileStrFinish(String erroe_str, String result,
			boolean result_state, long request_code);

	public void onWriteFileStrFinish(String erroe_str, boolean result,
			long request_code);

	public void onWriteDataFinish(long requestCode, State state);

	public void onReadCacheSize(int size);

	public void onClearCache();

}
