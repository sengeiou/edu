package com.ubt.alpha1e_edu.blockly;

import org.litepal.crud.DataSupport;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class BlocklyProjectMode extends DataSupport {

    private String pid;
    private String userId;
    private String programName;
    private String programData;
    private boolean delState;
    private boolean serverState;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramData() {
        return programData;
    }

    public void setProgramData(String programData) {
        this.programData = programData;
    }

    public boolean isDelState() {
        return delState;
    }

    public void setDelState(boolean delState) {
        this.delState = delState;
    }

    public boolean isServerState() {
        return serverState;
    }

    public void setServerState(boolean serverState) {
        this.serverState = serverState;
    }

    @Override
    public String toString() {
        return "BlocklyProjectMode{" +
                "pid='" + pid + '\'' +
                ", userId='" + userId + '\'' +
                ", programName='" + programName + '\'' +
                ", programData='" + programData + '\'' +
                ", delState=" + delState +
                ", serverState=" + serverState +
                '}';
    }
}
