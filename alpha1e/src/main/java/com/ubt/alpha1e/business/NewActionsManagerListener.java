package com.ubt.alpha1e.business;

import java.util.List;

import com.ubt.alpha1e.data.model.NewActionInfo;

public interface NewActionsManagerListener {
	public void onReadNewActionsFinish(List<NewActionInfo> actions);

	public void onChangeNewActionsFinish();
}
