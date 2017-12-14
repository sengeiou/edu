package com.ubt.alpha1e.data.model;

import com.baoyz.pg.Parcelable;
import com.ubtechinc.base.ByteHexHelper;

@Parcelable
public class FrameActionInfo {
    public String eng_angles="";
    public int eng_time;
    public int totle_time;

    public byte[] getData() {
        String[] angle = eng_angles.split("#");
        byte[] frameData = new byte[19];
        for (int i = 0; i < angle.length; i++) {
            frameData[i] = (byte) Integer.parseInt(angle[i]);
        }
        frameData[16] = (byte) (eng_time / 20);
        byte[] time = ByteHexHelper.intToTwoHexBytes(totle_time / 20);
        frameData[17] = time[0];
        frameData[18] = time[1];
        return frameData;
    }

    public void setData(int[] datas) {
        eng_time = datas[0];
        totle_time = datas[1];
        for (int i = 2; true; i++) {
            eng_angles += datas[i];
            if (i == 17)
                break;
            eng_angles += "#";
        }
    }

    public int[] getDataInt() {
        String[] angle = eng_angles.split("#");
        int[] frameData = new int[16];
        for (int i = 0; i < angle.length; i++) {
            frameData[i] = (byte) Integer.parseInt(angle[i]);
        }
        return frameData;
    }

    public static FrameActionInfo getDefaultFrame() {
        FrameActionInfo def_frame = new FrameActionInfo();
//        def_frame.eng_angles = "90#90#90#90#90#90#90#60#76#110#90#90#120#104#70#90";
        def_frame.eng_angles = "93#20#66#86#156#127#90#74#95#104#89#89#104#81#76#90";
        def_frame.eng_time = 1000;
        def_frame.totle_time = 1000;
        return def_frame;
    }

    public Object doCopy() {
        FrameActionInfo frame = new FrameActionInfo();
        frame.eng_angles = this.eng_angles;
        frame.totle_time = this.totle_time;
        frame.eng_time = this.eng_time;
        return frame;
    }
}
