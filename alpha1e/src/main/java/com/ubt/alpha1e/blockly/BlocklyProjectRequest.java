package com.ubt.alpha1e.blockly;

import com.ubt.alpha1e.base.RequstMode.BaseRequest;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class BlocklyProjectRequest extends BaseRequest {

    private String programName;
    private String programData;

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
        return "BlocklyProjectRequest{" +
                "programName='" + programName + '\'' +
                ", programData='" + programData + '\'' +
                '}';
    }
}
