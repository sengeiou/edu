package com.ubt.alpha1e.edu.ui.helper;

import android.graphics.Bitmap;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubtechinc.base.ConstValue;

public class RobotInfoHelper extends BaseHelper {

	public RobotInfoHelper(BaseActivity baseActivity) {
		super(baseActivity);

	}

	public String getRobotName() {
		return ((AlphaApplication) mBaseActivity.getApplicationContext())
				.getCurrentDeviceName();
	}

	public void doRenameRobot(String name) {
		// TODO Auto-generated method stub

		if (!mBaseActivity.checkCoon()) {
			return;
		}

		 // �������ݿ�
		((AlphaApplication) mBaseActivity.getApplicationContext())
				.getDBAlphaInfoManager()
				.updateDeviceName(
						((AlphaApplication) mBaseActivity
								.getApplicationContext()).getCurrentBluetooth()
								.getAddress(),
						((AlphaApplication) mBaseActivity
								.getApplicationContext()).getCurrentBluetooth()
								.getName(), name);
		// �����ڴ�
		((AlphaApplication) mBaseActivity.getApplicationContext())
				.setCurrentDeviceName(name);
		// ���»�����
		byte[] param;
		try {
			param = name.getBytes("GBK");
			doSendComm(ConstValue.DV_MODIFYNAME, param);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSendData(String mac, byte[] datas, int nLen) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectState(boolean bsucceed, String mac) {
		// TODO Auto-generated method stub

	}

	@Override
	public void DistoryHelper() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
		// TODO Auto-generated method stub

	}

}
