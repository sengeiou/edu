package com.ubt.alpha1e_edu.ui.helper;

import android.graphics.Bitmap;

public interface IBindUI {

	public void onReadHeadImgFinish(boolean is_success, Bitmap img);

	public void onNoteNumNotFull();

	public void onBindFinish(int result);
}
