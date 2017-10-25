package com.ubt.alpha1e.blockly;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.ScanActivity;
import com.ubt.alpha1e.ui.WebContentActivity;

/**
 * @className ScanBluetoothActivity
 *
 * @author wmma
 * @description Block逻辑编程出发蓝牙连接 Activity,
 * @date 2017/3/6
 * @update 2017/4/5
 */


public class ScanBluetoothActivity extends BaseActivity {

    private String TAG = ScanBluetoothActivity.class.getSimpleName();

    public static final String REQUEST_TYPE = "REQUEST_TYPE";

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private ScanDeviceFragment mScanDeviceFragment;

    private LinearLayout llBack;
    private ImageView ivReScan;
    private ImageView ivHelp;

    public static String REUEST_CODE = "request_code";
    public int requestCode = 0;


    public static void launchActivity(Activity activity, int requestCode)
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
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_blockly_scan);
        AutoScanConnectService.doEntryManalConnect(true);

        if(getIntent().getExtras()!=null){
            requestCode = (int)getIntent().getExtras().get(REQUEST_TYPE);
        }

        initUI();

        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();

        mScanDeviceFragment = new ScanDeviceFragment();
        mFragmentTransaction.add(R.id.scan_container, mScanDeviceFragment);
        mFragmentTransaction.commit();

        initControlListener();


    }

    @Override
    protected void initUI() {
        llBack = (LinearLayout) findViewById(R.id.lay_base_back);
        ivReScan = (ImageView) findViewById(R.id.iv_re_scan);
        ivHelp = (ImageView) findViewById(R.id.iv_help);

    }

    @Override
    protected void initControlListener() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mScanDeviceFragment != null) {
                    mScanDeviceFragment.onBack();
                }
            }
        });

        ivReScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScanDeviceFragment.doReScan();
            }
        });

        ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language = getStandardLocale(getAppCurrentLanguage());

                String url = HttpAddress
                        .getRequestUrl(HttpAddress.Request_type.scan_robot_gest)
                        + HttpAddress.getParamsForGet(
                        new String[] { language },
                        HttpAddress.Request_type.scan_robot_gest);
                Intent intent = new Intent();
                intent.putExtra(WebContentActivity.WEB_TITLE, "");
                intent.putExtra(WebContentActivity.WEB_URL, url);
                intent.setClass(ScanBluetoothActivity.this, WebContentActivity.class);
                startActivity(intent);
            }
        });
    }

    public int getRequestCode() {
        return requestCode;
    }


    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(mScanDeviceFragment != null) {
            mScanDeviceFragment.onBack();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        AutoScanConnectService.doEntryManalConnect(false);
        super.onDestroy();
    }

}
