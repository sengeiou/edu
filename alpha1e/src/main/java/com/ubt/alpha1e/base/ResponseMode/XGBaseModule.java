package com.ubt.alpha1e.base.ResponseMode;

import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/13 13:51
 * @modifier：ubt
 * @modify_date：2017/11/13 13:51
 * [A brief description]
 * version
 */

public class XGBaseModule {
//    private boolean success;
//    private int errCode;
//    private String msg;
    private List<XGDeviceMode> data;

//    public boolean isSuccess() {
//        return success;
//    }
//
//    public void setSuccess(boolean success) {
//        this.success = success;
//    }
//
//    public int getErrCode() {
//        return errCode;
//    }
//
//    public void setErrCode(int errCode) {
//        this.errCode = errCode;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }

    public List<XGDeviceMode> getData() {
        return data;
    }

    public void setData(List<XGDeviceMode> data) {
        this.data = data;
    }

//    @Override
//    public String toString() {
//        return "XGBaseModule{" +
//                "success=" + success +
//                ", errCode=" + errCode +
//                ", msg='" + msg + '\'' +
//                ", data=" + data +
//                '}';
//    }


    @Override
    public String toString() {
        return "XGBaseModule{" +
                "data=" + data +
                '}';
    }
}
