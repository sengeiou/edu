package com.ubt.alpha1e_edu.ui.helper;

import com.ubt.alpha1e_edu.business.ActionPlayerListener;
import com.ubt.alpha1e_edu.data.model._RemoteInfo;

import java.util.List;

public interface _IRemoteUI extends ActionPlayerListener {

	public void onReadStateFinish(boolean isUsed);

	public void onReadSetInfoFinish(_RemoteInfo info, boolean is_with_default);

	public void onRecordSetInfoFinish();

	public void onReadActionsFinish(List<String> mActionsNames);

	public void noteTFPulled();

}
