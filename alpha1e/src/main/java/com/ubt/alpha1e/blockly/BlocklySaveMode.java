package com.ubt.alpha1e.blockly;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class BlocklySaveMode {

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
        return "BlocklySaveMode{" +
                "programName='" + programName + '\'' +
                ", programData='" + programData + '\'' +
                '}';
    }
}
