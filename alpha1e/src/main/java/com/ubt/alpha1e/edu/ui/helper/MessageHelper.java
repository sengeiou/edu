package com.ubt.alpha1e.edu.ui.helper;

import android.graphics.Bitmap;

import com.ubt.alpha1e.edu.business.MessageRecordManager;
import com.ubt.alpha1e.edu.data.model.MessageRecordInfo;
import com.ubt.alpha1e.edu.ui.BaseActivity;

public class MessageHelper extends BaseHelper {

	private IMessageUI mUI;

	public static final String MAP_KEY_MSG = "MAP_KEY_MSG";;
	public static final String MAP_KEY_MSG_LOGO = "MAP_KEY_MSG_LOGO";;
	public static final String MAP_KEY_MSG_TITLE = "MAP_KEY_MSG_TITLE";
	public static final String MAP_KEY_MSG_DISC = "MAP_KEY_MSG_DISC";
	public static final String MAP_KEY_MSG_DATE = "MAP_KEY_MSG_DATE";
	public static final String MAP_KEY_IS_UNREAD = "MAP_KEY_IS_UNREAD";

	public MessageHelper(BaseActivity _baseActivity, IMessageUI ui) {
		super(_baseActivity);
		mUI = ui;
	}

	public void getNewMessages() {

		String country_code = "";
		if (getCurrentUser() != null){
			country_code = getCurrentUser().countryCode;
		}

		MessageRecordManager.getInstance(mBaseActivity, getCurrentUser(), mUI)
				.doGetMessages();

	}

	public void getNewMessages(int type,int page,int pageSize) {

		MessageRecordManager.getInstance(mBaseActivity, getCurrentUser(), mUI)
				.doGetMessages(type,page,pageSize);

	}

	public void doRecordMessage(long messageId) {
		MessageRecordInfo record = new MessageRecordInfo();
		record.isRead = true;
		record.messageId = messageId;
		record.userId = -1;
		if (getCurrentUser() != null)
			record.userId = getCurrentUser().userId;
		MessageRecordManager.getInstance(mBaseActivity, getCurrentUser(), mUI)
				.addToRecords(record);
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
