package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.MessageRecordManagerListener;
import com.ubt.alpha1e.data.model.MessageInfo;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.ui.dialog.IntroductionUIListener;
import com.ubt.alpha1e.ui.fragment.IMainUI;
import com.ubt.alpha1e.ui.fragment.ScanFragment;
import com.ubt.alpha1e.ui.helper.MainHelper;
import com.ubt.alpha1e.utils.LoginUtil;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.List;

public class RobotConnectedActivity extends BaseActivity implements
        IntroductionUIListener, MessageRecordManagerListener, IMainUI {
    private static final String TAG = "RobotConnectedActivity";

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    /**
     * 连接机器人扫描界面
     */

    private ScanFragment mScanFragment;

    private Fragment mCurrentFragment;
    private Button btnHelp;
    private boolean mComeConnectForBack = false;

    private Button btnReScan;

    public static void launchActivity(Activity activity, boolean comeConnectForBack, int requestCode)
    {
        Intent intent = new Intent();
        intent.setClass(activity,RobotConnectedActivity.class);
        intent.putExtra("mComeConnectForBack",comeConnectForBack);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_connected);

        AutoScanConnectService.doEntryManalConnect(true);
        if(getIntent().getExtras() != null){
            mComeConnectForBack = getIntent().getExtras().getBoolean("mComeConnectForBack");
        }
        mHelper = new MainHelper(this);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initTitle(getStringResources("ui_home_connect_robot"));

        initControlListener();

        loadFragment(mScanFragment);

    }

    @Override
    protected void initUI() {

        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();

        mScanFragment = new ScanFragment((MainHelper) mHelper);
        mScanFragment.setComeConnectForBack(mComeConnectForBack);
        mScanFragment.setMainUI(this);
        mFragmentTransaction.add(R.id.lay_page, mScanFragment).commit();
        mCurrentFragment = mScanFragment;

        btnHelp = (Button) findViewById(R.id.btn_help);
        btnReScan = (Button) findViewById(R.id.btn_re_scan);
    }

    private void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction
                    .hide(mCurrentFragment)
                    .add(R.id.lay_page, targetFragment)
                    .commit();
        } else {
            transaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;
    }
    @Override
    protected void initControlListener() {

        setTitleBack(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mScanFragment != null) {
                    mScanFragment.onBack();
                }
            }
        });

        btnHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String language = getStandardLocale(getAppCurrentLanguage());

                String url = HttpAddress
                        .getRequestUrl(HttpAddress.Request_type.scan_robot_gest)
                        + HttpAddress.getParamsForGet(
                        new String[] { language },
                        HttpAddress.Request_type.scan_robot_gest);
                Intent inte = new Intent();
                inte.putExtra(WebContentActivity.WEB_TITLE, "");
                inte.putExtra(WebContentActivity.WEB_URL, url);
                inte.setClass(RobotConnectedActivity.this, WebContentActivity.class);
                RobotConnectedActivity.this.startActivity(inte);
            }
        });

        btnReScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mScanFragment != null) {
                    mScanFragment.doReScan();
                }
            }
        });

    }

    @Override
    public void onPause() {
        try {
            this.mHelper.UnRegisterHelper();

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onPause();
    }

    @Override
    protected void initBoardCastListener() {
    }

    @Override
    public void onBackPressed() {
        if(mScanFragment != null) {
            mScanFragment.onBack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoginUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        UbtLog.d(TAG,"-onDestroy-");
        AutoScanConnectService.doEntryManalConnect(false);
        super.onDestroy();
    }

    @Override
    public void onIntroduceFinish() {

    }

    @Override
    public void onAddNoReadMessage() {

    }

    @Override
    public void onReadUnReadRecords(List<Long> ids) {

    }

    @Override
    public void onGetNewMessages(boolean isSuccess, String errorInfo,
                                 List<MessageInfo> messages,int type) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getOtherPage() {

    }

    @Override
    public void toOtherPageStart() {

    }

    @Override
    public void toOtherPageEnd() {

    }

}
