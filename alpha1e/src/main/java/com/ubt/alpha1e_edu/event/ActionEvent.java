package com.ubt.alpha1e_edu.event;

import com.ubt.alpha1e_edu.data.model.ActionInfo;
import com.ubt.alpha1e_edu.data.model.DownloadProgressInfo;

import java.util.List;

/**
 * @className ActionEvent
 *
 * @author lihai
 * @description 动作相关事件
 * @date 2017/4/12
 * @update
 */


public class ActionEvent {

    private Event event;

    private String mActionName;

    private int status;

    private List<String> mActionsNames;

    private ActionInfo mActionInfo = null;

    private DownloadProgressInfo mDownloadProgressInfo = null;

    public ActionEvent(Event event) {
        this.event = event;
    }

    public enum Event{
        READ_ACTIONS_FINISH,
        ROBOT_ACTION_DOWNLOAD_START,
        ROBOT_ACTION_DOWNLOAD,
        ACTION_PLAY_START,
        ACTION_PLAY_PAUSE,
        ACTION_PLAY_FINISH
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<String> getActionsNames() {
        return mActionsNames;
    }

    public void setActionsNames(List<String> actionsNames) {
        this.mActionsNames = actionsNames;
    }

    public DownloadProgressInfo getDownloadProgressInfo() {
        return mDownloadProgressInfo;
    }

    public void setDownloadProgressInfo(DownloadProgressInfo downloadProgressInfo) {
        this.mDownloadProgressInfo = downloadProgressInfo;
    }

    public ActionInfo getActionInfo() {
        return mActionInfo;
    }

    public void setActionInfo(ActionInfo actionInfo) {
        this.mActionInfo = actionInfo;
    }

    public String getActionName() {
        return mActionName;
    }

    public void setActionName(String mActionName) {
        this.mActionName = mActionName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
