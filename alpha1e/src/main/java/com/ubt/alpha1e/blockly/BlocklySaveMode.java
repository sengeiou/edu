package com.ubt.alpha1e.blockly;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class BlocklySaveMode {

    private String programId;
    private String programName;
    private String programData;

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
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
        return "BlocklySaveMode{" +
                "programId='" + programId + '\'' +
                ", programName='" + programName + '\'' +
                ", programData='" + programData + '\'' +
                '}';
    }
}
