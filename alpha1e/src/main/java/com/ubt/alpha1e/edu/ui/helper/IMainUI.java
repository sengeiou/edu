package com.ubt.alpha1e.edu.ui.helper;

import android.graphics.Bitmap;

public interface IMainUI {

	public void noteCharging();

	public void updateBattery(int power);

	public void noteDiscoonected();

	public void noteLightOn();

	public void noteLightOff();

	public void onNoteVol(int mCurrentVol);

	public void onNoteVolState(boolean mCurrentVolState);

	public void onReadHeadImgFinish(boolean b, Bitmap obj);

}
