package com.ubt.alpha1e.blockly;

import com.ubt.alpha1e.data.model.BaseResponseModel;

import java.io.Serializable;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class BlocklyRespondMode extends BaseResponseModel implements Serializable{

    private String id;
    private String userId;
    private String programName;
    private String programData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    @Override
    public String toString() {
        return "BlocklyRespondMode{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", programName='" + programName + '\'' +
                ", programData='" + programData + '\'' +
                '}';
    }
}
