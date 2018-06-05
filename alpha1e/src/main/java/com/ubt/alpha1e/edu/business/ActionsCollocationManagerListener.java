package com.ubt.alpha1e.edu.business;

import java.util.List;

import com.ubt.alpha1e.edu.data.model.ActionColloInfo;

public interface ActionsCollocationManagerListener {

	public void onReadCollocationRecordFinish(boolean isSuccess,
			String errorInfo, List<ActionColloInfo> history);

	public void onDelRecordFinish();

	public void onRecordFinish(long action_id);

	public void onCollocateFinish(long action_id, boolean isSuccess,
			String error);

	public void onCollocateRmoveFinish(boolean b);
}
