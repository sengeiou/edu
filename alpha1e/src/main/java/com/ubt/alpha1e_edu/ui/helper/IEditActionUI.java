package com.ubt.alpha1e_edu.ui.helper;

import com.ubt.alpha1e_edu.business.NewActionPlayerListener;
import com.ubt.alpha1e_edu.data.IFileListener;

public interface IEditActionUI extends NewActionPlayerListener, IFileListener {
	public void onReadEng(byte[] eng_angle);

	public void onChangeActionFinish();
}
