package com.ubt.alpha1e.ui.helper;

import com.ubt.alpha1e.business.ActionPlayerListener;
import com.ubt.alpha1e.data.RemoteItem;
import com.ubt.alpha1e.data.model.RemoteRoleInfo;

import java.util.List;

public interface IRemoteUI extends ActionPlayerListener {

    void onReadActionsFinish(List<String> mActionsNames);

    void noteTFPulled();

    void onSendFileFinish(boolean isSuccess);

    void onPlayActionFileNotExist();

    void onSendFileStart();

    void onReadSettingItem(List<RemoteItem> items);

    void onReadRemoteRoleFinish(List<RemoteRoleInfo> mRemoteRoles);

    void onAddRemoteRole(boolean isSuccess,int roleId);

    void onUpdateRemoteRole(boolean isSuccess,RemoteRoleInfo roleInfo);

    void onDelRemoteRole(boolean isSuccess,RemoteRoleInfo roleInfo);

    void onAddRemoteRoleActions(boolean isSuccess,int roleId);

    void onDelRemoteHeadPrompt(boolean isSuccess);

}
