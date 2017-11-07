package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.services.AutoScanConnectService;
import com.ubt.alpha1e.ui.custom.CommonCtrlView;
import com.ubt.alpha1e.ui.custom.IShowPublishView;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.fragment.ActionsLibMainFragment3;
import com.ubt.alpha1e.ui.fragment.IShowSquareDetailFragment;
import com.ubt.alpha1e.ui.fragment.MyFragment;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.IMainUI;
import com.ubt.alpha1e.ui.helper.LoginHelper;
import com.ubt.alpha1e.ui.helper.MainHelper;
import com.ubt.alpha1e.utils.LoginUtil;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.changeskin.SkinManager;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;

public class MyMainActivity extends BaseActivity implements
        BaseDiaUI,IMainUI {

    public static final String TAG = "MyMainActivity";

    public static final String Start_Tpye = "Start_Tpye";
    private static final int ACTIONS_LIB_FRAGMENT = 1;
    private static final int MY_FRAGMENT = 2;
    private static final int ISHOW_FRAGMENT = 3;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private ActionsLibMainFragment3 mActionsLibMainFragment;
    private LoadingDialog mLoadingDialog;
    private ImageButton imgBtnRobotState,imgBtnIshowAdd;
    private MainHelper mMainHelper = null;

    private Date lastTime;

    private static final int BLUETOOTH_DISCONNECTED = 100;
    private static final int INIT_COURSE_ACCESS_TOKEN = 101;
    private static final int START_AUTO_SCAN_CONNECT_SERVICE = 102;

    private TextView tvSquare, tvMy;
    private RelativeLayout rlMyTab, rlRobotTab, rlSquareTab;
    private ImageView ivSquareTab, ivTabPoint,ivMyTab;

    private TextView baseTitleName;
    private TextView ishowText;

    private static final int TITLE_SELECT_SIZE = 20;//sp
    private static final int TITLE_NORMAL_SIZE = 18;//sp

    public Fragment mCurrentFragment;
    private LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BLUETOOTH_DISCONNECTED:
                    imgBtnRobotState.setBackground(getDrawableRes("robot_disconnect_ft"));
                    break;
                case INIT_COURSE_ACCESS_TOKEN:
                    if(mMainHelper != null){
                        mMainHelper.initCourseAccessToken();
                    }
                    break;
                case START_AUTO_SCAN_CONNECT_SERVICE:
                    startAutoScanConnectService();
                    break;
                default:
                    break;
            }
        }
    };

    public static void launchActivity(Activity activity,int requestCode)
    {
        Intent intent = new Intent();
        intent.setClass(activity,MyMainActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UbtLog.d(TAG,"savedInstanceState = " + savedInstanceState);
        if(savedInstanceState != null){
            //当activity恢复的时候，清除fragment的状态，处理在fragment中getActivity为null的问题。
            String FRAGMENTS_TAG =  "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        super.onCreate(savedInstanceState);
        UbtLog.d(TAG,"---onCreate---");
        setContentView(R.layout.activity_my_main);
        mFragmentManager = this.getFragmentManager();
        mMainHelper = new MainHelper(this);

        initUI();
        initControlListener();
        //startAutoScanConnectService();
        mHandler.sendEmptyMessage(START_AUTO_SCAN_CONNECT_SERVICE);
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(MyMainActivity.class.getSimpleName());
        super.onResume();
        mHandler.sendEmptyMessage(INIT_COURSE_ACCESS_TOKEN);

        if(mCurrentFragment instanceof MyFragment){
            //切换语言的时候，Fragment要重新设一下，暂时这样处理，后面优化
            initTitle(getStringResources("ui_action_mine"));
        }

        if(mMainHelper!=null)
        {
            mMainHelper.RegisterHelper();
            mMainHelper.doRegisterListenerUI(this);
            mMainHelper.doReadState();
        }

        if(isBulueToothConnected()){
            imgBtnRobotState.setBackground(getDrawableRes("robot_connect_ft"));
        }else{
            imgBtnRobotState.setBackground(getDrawableRes("robot_disconnect_ft"));
        }

        if(checkLoginState()){
            if(!mMainHelper.clickedMessage() ||  mMainHelper.isFirstUseSetting()){
                ivTabPoint.setVisibility(View.VISIBLE);
            }else{
                ivTabPoint.setVisibility(View.GONE);
            }
        }else{
            if(mMainHelper.isFirstUseSetting()){
                ivTabPoint.setVisibility(View.VISIBLE);
            }else{
                ivTabPoint.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        if(event.getEvent() == RobotEvent.Event.CONNECT_SUCCESS){
            if(mMainHelper!=null)
            {
                UbtLog.d(TAG,"--CONNECT_SUCCESS--");
                mMainHelper.RegisterHelper();
                mMainHelper.doReadState();
            }
        }else if(event.getEvent() == RobotEvent.Event.DISCONNECT){
            mHandler.sendEmptyMessage(BLUETOOTH_DISCONNECTED);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMainHelper!=null) {
            mMainHelper.UnRegisterHelper();
            mMainHelper.doUnRegisterListenerUI(this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == event.ACTION_DOWN
                && event.getKeyCode() == event.KEYCODE_BACK) {
            Date curDate = new Date(System.currentTimeMillis());
            float time_difference = 1000;
            if (lastTime != null) {
                time_difference = curDate.getTime() - lastTime.getTime();
            }

            if (time_difference < 1000) {
                CommonCtrlView.closeCommonCtrlView();
                SkinManager.getInstance().cleanInstance();
                ((AlphaApplication) this.getApplication()).doExitApp(false);
            } else {
                showToast("ui_home_exit");
            }
            lastTime = curDate;
        }
        return true;
    }

    @Override
    public void initTitle(String titleName) {

        UbtLog.d(TAG,"--initTitle--");
        baseTitleName.setText(titleName);

        if(titleName.equals(getStringResources("ui_home_square"))){
            ishowText.setVisibility(View.VISIBLE);
            baseTitleName.setClickable(true);

            baseTitleName.setTextColor(getColorRes("main_title_text_select_ft"));
            ishowText.setTextColor(getColorRes("main_title_text_default_ft"));

            baseTitleName.setTextSize(TITLE_SELECT_SIZE);
            ishowText.setTextSize(TITLE_NORMAL_SIZE);

        }else{
            baseTitleName.setTextColor(getColorRes("main_title_text_default_ft"));
            baseTitleName.setTextSize(TITLE_NORMAL_SIZE);
            ishowText.setVisibility(View.GONE);
            baseTitleName.setClickable(false);
            showIshowAddLayout(false);
        }
    }

    private void showIshowAddLayout(boolean isShow){
        if(isShow){
            imgBtnIshowAdd.setVisibility(View.VISIBLE);
        }else {
            imgBtnIshowAdd.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initUI() {

        mLoadingDialog = LoadingDialog.getInstance(this,this);
        mLoadingDialog.setDoCancelable(true,6);

        baseTitleName = ((TextView) this.findViewById(R.id.txt_base_title_name));
        ishowText  = ((TextView) this.findViewById(R.id.txt_base_ishow_title_name));

        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        mActionsLibMainFragment = new ActionsLibMainFragment3(this);
        mFragmentTransaction.add(R.id.lay_page, mActionsLibMainFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = mActionsLibMainFragment;
        mFragmentCache.put(ACTIONS_LIB_FRAGMENT,mActionsLibMainFragment);

        initTitle(getStringResources("ui_home_square"));

        imgBtnRobotState = (ImageButton) findViewById(R.id.imgbtn_robot_state);
        imgBtnIshowAdd = (ImageButton) findViewById(R.id.imgbtn_ishow_add);

        ivSquareTab = (ImageView) findViewById(R.id.iv_square);
        rlMyTab = (RelativeLayout) findViewById(R.id.rl_my_tab);
        tvSquare = (TextView) findViewById(R.id.tab_square);
        tvMy = (TextView)findViewById(R.id.tab_my);
        ivMyTab = (ImageView)findViewById(R.id.iv_my);
        ivTabPoint = (ImageView) findViewById(R.id.iv_message_state);
        rlRobotTab = (RelativeLayout) findViewById(R.id.rl_robot_tab);
        rlSquareTab = (RelativeLayout) findViewById(R.id.rl_square_tab);

        ivSquareTab.setSelected(true);
        tvSquare.setTextColor(getColorRes("main_bottom_tab_text_select_ft"));
        tvMy.setTextColor(getColorRes("main_bottom_tab_text_default_ft"));

        rlMyTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = mFragmentCache.containsKey(MY_FRAGMENT) ? mFragmentCache.get(MY_FRAGMENT)
                        : new MyFragment(MyMainActivity.this,mMainHelper);
                if (!mFragmentCache.containsKey(MY_FRAGMENT)) {
                    mFragmentCache.put(MY_FRAGMENT, f);
                }
                loadFragment(f);
                initTitle(getStringResources("ui_action_mine"));

                ivMyTab.setSelected(true);
                tvMy.setTextColor(getColorRes("main_bottom_tab_text_select_ft"));

                ivSquareTab.setSelected(false);
                tvSquare.setTextColor(getColorRes("main_bottom_tab_text_default_ft"));

            }
        });

        rlSquareTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = mFragmentCache.containsKey(ACTIONS_LIB_FRAGMENT) ? mFragmentCache.get(ACTIONS_LIB_FRAGMENT)
                        : new ActionsLibMainFragment3(MyMainActivity.this);
                if (!mFragmentCache.containsKey(ACTIONS_LIB_FRAGMENT)) {
                    mFragmentCache.put(ACTIONS_LIB_FRAGMENT, f);
                }
                loadFragment(f);
                initTitle(getStringResources("ui_home_square"));

                ivMyTab.setSelected(false);
                tvMy.setTextColor(getColorRes("main_bottom_tab_text_default_ft"));

                ivSquareTab.setSelected(true);
                tvSquare.setTextColor(getColorRes("main_bottom_tab_text_select_ft"));
            }
        });

        rlRobotTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//          blockly功能不连接蓝牙也可进入该页面
//                if(isBulueToothConnected()){
                    Intent intent = new Intent();
                    intent.setClass(MyMainActivity.this, RobotControlActivity.class);
                    startActivity(intent);

//                }else{
//                    RobotConnectedActivity.launchActivity(MyMainActivity.this,true,12306);
//                }
            }
        });

        imgBtnIshowAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbtLog.d(TAG,"--imgBtnIshowAdd--");
                if(checkLoginState()){
                    IShowPublishView iShowPublishView = new IShowPublishView(MyMainActivity.this);
                }else{
                    Intent intent = new Intent();
                    intent.putExtra(LoginHelper.IS_LOGIN_SIGLE, true);
                    intent.setClass(MyMainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }


            }
        });

        baseTitleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                baseTitleName.setTextColor(getColorRes("main_title_text_select_ft"));
                ishowText.setTextColor(getColorRes("main_title_text_default_ft"));
                baseTitleName.setTextSize(TITLE_SELECT_SIZE);
                ishowText.setTextSize(TITLE_NORMAL_SIZE);
                showIshowAddLayout(false);

                Fragment f = mFragmentCache.containsKey(ACTIONS_LIB_FRAGMENT) ? mFragmentCache.get(ACTIONS_LIB_FRAGMENT)
                        : new ActionsLibMainFragment3(MyMainActivity.this);
                if (!mFragmentCache.containsKey(ACTIONS_LIB_FRAGMENT)) {
                    mFragmentCache.put(ACTIONS_LIB_FRAGMENT, f);
                }
                loadFragment(f);
            }
        });

        ishowText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchIshow(1);
            }
        });
    }

    public void switchIshow(int actionSortType){

        baseTitleName.setTextColor(getColorRes("main_title_text_default_ft"));
        ishowText.setTextColor(getColorRes("main_title_text_select_ft"));
        baseTitleName.setTextSize(TITLE_NORMAL_SIZE);
        ishowText.setTextSize(TITLE_SELECT_SIZE);
        showIshowAddLayout(true);

        Fragment f = mFragmentCache.containsKey(ISHOW_FRAGMENT) ? mFragmentCache.get(ISHOW_FRAGMENT)
                : new IShowSquareDetailFragment(MyMainActivity.this,mMainHelper,actionSortType);

        if (!mFragmentCache.containsKey(ISHOW_FRAGMENT)) {
            mFragmentCache.put(ISHOW_FRAGMENT, f);
        }
        loadFragment(f);

        if(actionSortType == 9){
            //如果是精品原创切入，则强制跳转到动作页面
            ((IShowSquareDetailFragment)mCurrentFragment).switchToActionTab();
        }

    }

    public void setDisableTabClickListener(boolean flag){
        UbtLog.d(TAG,"lihai----setDisableTabClickListener-- " + flag);
        if(mCurrentFragment instanceof IShowSquareDetailFragment){
            ((IShowSquareDetailFragment)mCurrentFragment).setDisableTabClickListener(flag);
        }
    }

    private void startAutoScanConnectService(){
        AutoScanConnectService.startService(MyMainActivity.this);
    }

    @Override
    protected void onDestroy() {
        UbtLog.d(TAG, "-onDestroy-");
        if(mLoadingDialog!=null)
        {
            if(mLoadingDialog.isShowing()&&!isFinishing()){
                mLoadingDialog.cancel();
            }
            mLoadingDialog = null;
        }
        mMainHelper.DistoryHelper();
        super.onDestroy();
    }

    @Override
    protected void initControlListener() {
        imgBtnRobotState.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                UbtLog.d(TAG, "-imgBtnRobotState is onclick");
                if(isBulueToothConnected()){
                    UbtLog.d(TAG, "-isBulueToothConnected yes");
                  RobotInfoActivity.launchActivity(MyMainActivity.this, getRequestedOrientation());
                }else{
                    UbtLog.d(TAG, "-isBulueToothConnected no");
                    Intent mIntent = new Intent();
                    mIntent.setClass(MyMainActivity.this, RobotConnectedActivity.class);
                    startActivity(mIntent);
                }

            }
        });

    }

    private void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        UbtLog.d(TAG,"targetFragment.isAdded()->>>"+(!targetFragment.isAdded()));
        if (!targetFragment.isAdded()) {
            mCurrentFragment.onStop();

            transaction
                    .hide(mCurrentFragment)
                    .add(R.id.lay_page, targetFragment)
                    .commit();
        } else {
            mCurrentFragment.onStop();
            targetFragment.onResume();

            transaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;
    }


    @Override
    protected void initBoardCastListener() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12306){
            if (((AlphaApplication) this.getApplicationContext()).getCurrentBluetooth() != null){
                Intent mIntent = new Intent();
                mIntent.setClass(MyMainActivity.this, RobotControlActivity.class);
                startActivity(mIntent);
                return;
            }

        }else if (requestCode == ActionsEditHelper.GetUserHeadRequestCodeByShoot
                || requestCode == ActionsEditHelper.GetUserHeadRequestCodeByFile) {
            if (resultCode == RESULT_OK) {
                ContentResolver cr = this.getContentResolver();
                if (requestCode == ActionsEditHelper.GetUserHeadRequestCodeByFile) {
                    if (data == null){
                        return;
                    }

                    //android gao ban ben
                    String h_type = cr.getType(data.getData());
                    //android di ban ben
                    String l_type = data.getType();
                    UbtLog.d(TAG,"h_type:"+h_type  + "   l_type:"+l_type);
                    if (h_type == null && l_type == null){
                        return;
                    }

                    mImageUri = data.getData();
                }
                Intent intent = new Intent();
                intent.setData(mImageUri);
                intent.putExtra(DynamicActivity.SEND_TYPE, 2); //type 2:图片
                intent.setClass(this, DynamicActivity.class);
                startActivity(intent);
            }

        }else if(requestCode == ActionsEditHelper.GetThumbnailRequestCodeByVideo){
            if(resultCode == RESULT_OK){
                String videoUploadPath = (String)data.getExtras().get("output");
                UbtLog.d(TAG, "output=" +videoUploadPath);
                Intent intent = new Intent();
                intent.putExtra(DynamicActivity.SEND_TYPE, 3);
                intent.putExtra(DynamicActivity.VIDEO_PATH, videoUploadPath);
                intent.setClass(MyMainActivity.this, DynamicActivity.class);
                startActivity(intent);
            }
        }
        else{
            LoginUtil.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    @Override
    public void noteCharging() {
        imgBtnRobotState.setBackground(getDrawableRes("robot_connect_ft"));
    }

    @Override
    public void updateBattery(int power) {
        if(power < 10){
            imgBtnRobotState.setBackground(getDrawableRes("robot_lowbattery_ft"));
        }else{
            imgBtnRobotState.setBackground(getDrawableRes("robot_connect_ft"));
        }
    }

    @Override
    public void noteDiscoonected() {
        mHandler.sendEmptyMessage(BLUETOOTH_DISCONNECTED);
    }

    @Override
    public void onLostBtCoon() {
        mHandler.sendEmptyMessage(BLUETOOTH_DISCONNECTED);
        super.onLostBtCoon();
    }

    @Override
    public void noteLightOn() {

    }

    @Override
    public void noteLightOff() {

    }

    @Override
    public void onNoteVol(int mCurrentVol) {

    }

    @Override
    public void onNoteVolState(boolean mCurrentVolState) {

    }


    /**
     * photo taking
     */
    private Uri mImageUri;

    public void fromTakingPhoto()
    {
        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File path = new File(FileTools.image_cache);
        if (!path.exists()) {
            path.mkdirs();
        }
        mImageUri = Uri.fromFile(new File(path, System.currentTimeMillis() + ""));

        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                mImageUri);
        cameraIntent.putExtra("return-data", true);
        startActivityForResult(cameraIntent,
                ActionsEditHelper.GetUserHeadRequestCodeByShoot);
    }


    /**
     * from file
     */
    public void fromFileSelect()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,
                ActionsEditHelper.GetUserHeadRequestCodeByFile);
    }

    public void takeVideo() {
        Intent intent = new Intent();
        intent.setClass(MyMainActivity.this, MediaRecordActivity.class);
        intent.putExtra(DynamicActivity.SEND_TYPE, 3);
        startActivityForResult(intent, ActionsEditHelper.GetThumbnailRequestCodeByVideo);
    }

}
