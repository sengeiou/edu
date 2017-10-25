package com.ubt.alpha1e.data;

import android.graphics.Bitmap;

public interface IPrivateFileListener {
	public void onPrivateBitMapOpreaterFinish(boolean isSuccess,
			long request_code, Bitmap value);
}
