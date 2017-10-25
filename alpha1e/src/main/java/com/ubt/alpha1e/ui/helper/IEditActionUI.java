package com.ubt.alpha1e.ui.helper;

import com.ubt.alpha1e.business.NewActionPlayerListener;
import com.ubt.alpha1e.data.IFileListener;

public interface IEditActionUI extends NewActionPlayerListener, IFileListener {
	public void onReadEng(byte[] eng_angle);

	public void onChangeActionFinish();
}
