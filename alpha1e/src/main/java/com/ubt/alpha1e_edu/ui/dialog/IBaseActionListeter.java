package com.ubt.alpha1e_edu.ui.dialog;

import com.ubt.alpha1e_edu.data.model._BaseActionInfo;

public interface IBaseActionListeter {
	public void onAddAction(_BaseActionInfo info);

	public void onSetActionRepetTime(int position, int time);

	public void onNoteDelAction(int position);
}
