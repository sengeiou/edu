package com.ubt.alpha1e.ui.helper;

import com.ubt.alpha1e.business.ActionPlayerListener;
import com.ubt.alpha1e.business.ActionsDownLoadManagerListener;
import com.ubt.alpha1e.business.NewActionPlayerListener;
import com.ubt.alpha1e.business.NewActionsManagerListener;
import com.ubt.alpha1e.data.IFileListener;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;

import java.util.List;
import java.util.Map;

public interface IActionsUI extends ActionPlayerListener, FileDownloadListener,
        NewActionPlayerListener, ActionsDownLoadManagerListener,
        NewActionsManagerListener, IFileListener,IActionsColloUI {
    void onNoteNoUser();

    void onReadActionsFinish(List<String> names);

    void onNoteVol(int vol_pow);

    void onNoteVolState(boolean vol_state);

    void onReadMyDownLoadHistory(String hashCode,
                                 List<ActionRecordInfo> history);

    void onSendFileStart();

    void onSendFileBusy();

    void onSendFileError();

    void onSendFileFinish(int pos);

    void onSendFileCancel();

    void onSendFileUpdateProgress(String progress);

    void noteLightOn();

    void noteLightOff();

    void noteChangeFinish(boolean b, String string);

    void noteTFPulled();

    void syncServerDataEnd(List<Map<String, Object>> data);

    void noteDeleteActionStart(int pos);

    void noteDeleteActionFinish(boolean isOk,String str);

}