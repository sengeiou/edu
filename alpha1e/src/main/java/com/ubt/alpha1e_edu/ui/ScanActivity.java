package com.ubt.alpha1e_edu.ui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e_edu.AlphaApplicationValues;
import com.ubt.alpha1e_edu.AlphaApplicationValues.EdtionCode;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e_edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e_edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e_edu.ui.helper.IScanUI;
import com.ubt.alpha1e_edu.ui.helper.ScanHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ScanActivity extends BaseActivity implements IScanUI, BaseDiaUI {

	private boolean isDoingScan;

	private TextView txt_scan_state;
	private ImageView img_scan_main;
	private GifImageView gif_scan_main;
	private ListView lst_robots;
	private SimpleAdapter lst_robots_adapter;
	private List<Map<String, Object>> lst_robots_datas;
	private Button btn_no_robot;
	private int requestCode = -1;
	public static final String REQUEST_TYPE = "REQUEST_TYPE";
	private Map<String, Object> mCurrentRobotInfo = null;

	public static void launchActivity(Activity activity,int requestCode)
	{
		Intent intent = new Intent();
		intent.setClass(activity,ScanActivity.class);
		intent.putExtra(REQUEST_TYPE,requestCode);
		activity.startActivityForResult(intent,requestCode);
		activity.finish();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_scan);
		if(getIntent().getExtras()!=null)
		requestCode = (int)getIntent().getExtras().get(REQUEST_TYPE);
		initUI();
		initControlListener();

	}

	@Override
	protected void initUI() {
		txt_scan_state = (TextView) findViewById(R.id.txt_scan_state);

		img_scan_main = (ImageView) findViewById(R.id.img_scan_main);
		gif_scan_main = (GifImageView) findViewById(R.id.gif_scan_main);

		img_scan_main.setVisibility(View.INVISIBLE);
		gif_scan_main.setVisibility(View.VISIBLE);
		// ((GifDrawable) gif_scan_main.getDrawable()).setSpeed(5.0f);
		((GifDrawable) gif_scan_main.getDrawable()).start();

		initTitle("");
		lst_robots = (ListView) findViewById(R.id.lst_robots);
		lst_robots_datas = new ArrayList<Map<String, Object>>();
		int id = R.layout.layout_robot_item;

		lst_robots_adapter = new SimpleAdapter(this, lst_robots_datas, id,
				new String[] { ScanHelper.map_val_robot_name,
						ScanHelper.map_val_robot_mac, }, new int[] {
						R.id.txt_name, R.id.txt_mac });
		lst_robots.setAdapter(lst_robots_adapter);
		btn_no_robot = (Button) findViewById(R.id.btn_no_robot);
	}

	@Override
	protected void initControlListener() {
		img_scan_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doScan();
			}
		});

		lst_robots.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mCoonLoadingDia == null)
					mCoonLoadingDia = LoadingDialog.getInstance(
							ScanActivity.this, ScanActivity.this);
				((LoadingDialog) mCoonLoadingDia).mCurrentTask = new BaseWebRunnable() {
					@Override
					public void disableTask() {
						((ScanHelper) mHelper).doCancelCoon();
					}
				};
				((LoadingDialog) mCoonLoadingDia).setDoCancelable(true, 7);
				//((LoadingDialog) mCoonLoadingDia).showMessage("连接中...");
				mCoonLoadingDia.show();
				mCurrentRobotInfo = lst_robots_datas.get(arg2);
				((ScanHelper) mHelper).doCoonect(mCurrentRobotInfo);
			}
		});
		btn_no_robot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				String language = getStandardLocale(getAppCurrentLanguage());

				String url = HttpAddress
						.getRequestUrl(Request_type.scan_robot_gest)
						+ HttpAddress.getParamsForGet(
								new String[] { language },
								Request_type.scan_robot_gest);
				Intent inte = new Intent();
				inte.putExtra(WebContentActivity.WEB_TITLE, "");
				inte.putExtra(WebContentActivity.WEB_URL, url);
				inte.setClass(ScanActivity.this, WebContentActivity.class);
				ScanActivity.this.startActivity(inte);

			}
		});
	}

	@Override
	protected void initBoardCastListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		if (mHelper != null && ((ScanHelper) mHelper).thiz_state)
			return;
		mHelper = new ScanHelper(this, this);
		super.onResume();
		doScan();
	}

	public void doScan() {
		if (isDoingScan)
			return;
		if (lst_robots_datas.size() >= 1) {
			lst_robots_datas.clear();
			lst_robots_adapter.notifyDataSetChanged();
		}
		isDoingScan = true;
		img_scan_main.setVisibility(View.INVISIBLE);
		gif_scan_main.setVisibility(View.VISIBLE);
		// ((GifDrawable) gif_scan_main.getDrawable()).setSpeed(5.0f);
		((GifDrawable) gif_scan_main.getDrawable()).start();
		txt_scan_state.setText(getStringResources("ui_scan_doing"));
		if (AlphaApplicationValues.getCurrentEdit() == EdtionCode.for_factory_edit) {
			((ScanHelper) mHelper).doBLEScan();
		} else {
			((ScanHelper) mHelper).doScan();
		}
	}

	@Override
	public void onGetHistoryBindDevices(Set<BluetoothDevice> history_deivces) {
		// TODO Auto-generated method stub

	}

	@Override
	public void noteIsScaning() {
		// TODO Auto-generated method stub

	}

	@Override
	public void noteBtTurnOn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void noteBtIsOff() {
		// TODO Auto-generated method stub

	}

	@Override
	public void noteScanResultInvalid() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNewDevices(List<BluetoothDevice> devices) {
		// TODO Auto-generated method stub
		lst_robots_datas.clear();
		for (int i = 0; i < devices.size(); i++) {
			Map robot = new HashMap<String, Object>();
			robot.put(ScanHelper.map_val_robot_name, AlphaApplicationValues.dealBluetoothName(devices.get(i).getName()));
			robot.put(ScanHelper.map_val_robot_mac, devices.get(i).getAddress());
			robot.put(ScanHelper.map_val_robot_connect_state, false);
			lst_robots_datas.add(robot);
		}
		lst_robots_adapter.notifyDataSetChanged();
	}

	@Override
	public void onScanFinish() {
		// TODO Auto-generated method stub
		img_scan_main.setVisibility(View.VISIBLE);
		gif_scan_main.setVisibility(View.INVISIBLE);
		((GifDrawable) gif_scan_main.getDrawable()).stop();
		txt_scan_state.setText(getStringResources("ui_scan_rescan"));
		isDoingScan = false;

	}

	@Override
	public void onCoonected(boolean state) {
		// TODO Auto-generated method stub
		if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing())
			mCoonLoadingDia.cancel();
		if (state) {

			if(requestCode==-1)
			{
//				((ScanHelper) mHelper).doGoToNextPage();
			}
			Intent mIntent = new Intent();
//			mIntent.setClass(ScanActivity.this,MainHomeActivity.class);
			mIntent.setClass(ScanActivity.this, RobotControlActivity.class);
			this.startActivity(mIntent);
			this.finish();
		} else {
			Toast.makeText(this,getStringResources("ui_home_connect_failed"), Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void updateHistory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void noteUpdateBin() {

		if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing())
			mCoonLoadingDia.cancel();
	}

	@Override
	public void onGetNewDevicesParams(List<BluetoothDevice> mDevicesList,
			Map<BluetoothDevice, Integer> mDevicesRssiMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void noteCheckBin(BaseWebRunnable runnable) {
		if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing()) {
			((LoadingDialog) mCoonLoadingDia).mCurrentTask = runnable;
			((LoadingDialog) mCoonLoadingDia).setDoCancelable(true);

		}
	}

	@Override
	public void onGetUserImg(boolean isSuccess, Bitmap img) {
		// TODO Auto-generated method stub

	}

	@Override
	public void noteWaitWebProcressShutDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateBluetoothFinish(boolean is_success) {
		if (mCoonLoadingDia == null){
			mCoonLoadingDia = LoadingDialog.getInstance(
					ScanActivity.this, ScanActivity.this);
		}

		((LoadingDialog) mCoonLoadingDia).mCurrentTask = new BaseWebRunnable() {
			@Override
			public void disableTask() {
				((ScanHelper) mHelper).doCancelCoon();
			}
		};
		((LoadingDialog) mCoonLoadingDia).setDoCancelable(true, 7);
		((LoadingDialog) mCoonLoadingDia).showMessage(getStringResources("ui_home_connecting"));

		((ScanHelper) mHelper).doCoonect(mCurrentRobotInfo);
	}

	@Override
	public void onUpdateEngineFinish(boolean is_success) {

	}

	@Override
	public void onGotoPCUpdate() {

	}
}
