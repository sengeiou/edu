package com.ubt.alpha1e_edu.utils.test;

import android.os.Bundle;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.model.MessageInfo;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.ui.helper.IMessageUI;
import com.ubt.alpha1e_edu.ui.helper.MessageHelper;

import java.util.List;

public class MessageTestActivity extends BaseActivity implements IMessageUI {

	TextView txt_out_put;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_test);
		initUI();
	}

	@Override
	protected void initUI() {
		txt_out_put = (TextView) findViewById(R.id.txt_out_put);
		mHelper = new MessageHelper(this, this);
		((MessageHelper) mHelper).getNewMessages();
	}

	@Override
	protected void initControlListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initBoardCastListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNewMessages(boolean isSuccess, String errorInfo,
			List<MessageInfo> messages,int type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddNoReadMessage() {

	}

	@Override
	public void onReadUnReadRecords(List<Long> ids) {
		// TODO Auto-generated method stub

	}

}
