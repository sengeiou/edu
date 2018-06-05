package com.ubt.alpha1e_edu.business;

import com.ubt.alpha1e_edu.data.model.ActionInfo;
import com.ubt.alpha1e_edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e_edu.net.http.basic.FileDownloadListener.State;

import java.util.List;

public interface ActionsDownLoadManagerListener {

    void onGetFileLenth(ActionInfo action, double file_lenth);

    void onStopDownloadFile(ActionInfo action, State state);

    void onReportProgress(ActionInfo action, double progess);

    void onDownLoadFileFinish(ActionInfo action, State state);

    void onSyncHistoryFinish();

    void onReadHistoryFinish(List<ActionRecordInfo> history);

    void onChangeFinish(ActionInfo actionInfo);
}
