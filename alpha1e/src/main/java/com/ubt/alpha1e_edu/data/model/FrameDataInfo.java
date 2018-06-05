package com.ubt.alpha1e_edu.data.model;

import com.ubtechinc.base.ByteHexHelper;

public class FrameDataInfo {
	public String xmldata;
	public int xmltime;

	public byte[] getData() {
		String[] angle = xmldata.split("#");
		byte[] frameData = new byte[19];
		for (int i = 0; i < angle.length; i++) {
			frameData[i] = (byte) Integer.parseInt(angle[i]);
		}
		frameData[16] = (byte) (xmltime / 20);
		byte[] time = ByteHexHelper.intToTwoHexBytes(xmltime / 20);
		frameData[17] = time[0];
		frameData[18] = time[1];
		return frameData;
	}
}
