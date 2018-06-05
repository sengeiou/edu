package com.ubt.alpha1e.edu.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.ui.helper.RobotInfoHelper;

public class RobotVersionActivity extends BaseActivity {
	private TextView txt_robot;
	private TextView txt_robot_version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelper = new RobotInfoHelper(this);

		setContentView(R.layout.activity_robot_version);
		initUI();
		initControlListener();
	}

	@Override
	protected void initUI() {
		initTitle(((RobotInfoHelper) mHelper).getRobotName());
		txt_robot = (TextView) findViewById(R.id.txt_robot);
		txt_robot_version = (TextView) findViewById(R.id.txt_robot_version);

	}

	@Override
	public void onSkinChanged() {
		super.onSkinChanged();
		txt_robot.setText(((AlphaApplication) this.getApplication())
				.getRobotHardVersion());
		txt_robot_version.setText(((AlphaApplication) this.getApplication())
				.getRobotSoftVersion());
	}

	@Override
	protected void initControlListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initBoardCastListener() {
		// TODO Auto-generated method stub

	}
}
