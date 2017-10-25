package com.ubt.alpha1e.ui.helper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;

import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;

public interface IScanUI {
	public void onReadHeadImgFinish(boolean is_success, Bitmap img);

	public void onGetHistoryBindDevices(Set<BluetoothDevice> history_deivces);

	public void noteIsScaning();

	public void noteBtTurnOn();

	public void noteBtIsOff();

	public void noteScanResultInvalid();

	public void onGetNewDevices(List<BluetoothDevice> devices);

	public void onScanFinish();

	public void onCoonected(boolean state);

	public void updateHistory();

	public void noteUpdateBin();

	public void onGetNewDevicesParams(List<BluetoothDevice> mDevicesList,
			Map<BluetoothDevice, Integer> mDevicesRssiMap);

	public void noteCheckBin(BaseWebRunnable runnable);

	public void onGetUserImg(boolean isSuccess, Bitmap img);

	public void onUpdateBluetoothFinish(boolean is_success);

	public void onUpdateEngineFinish(boolean is_success);

	public void onGotoPCUpdate();
}
