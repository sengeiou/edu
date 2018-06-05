package com.ubt.alpha1e_edu.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baoyz.pg.PG;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.Constant;
import com.ubt.alpha1e_edu.data.model.InstructionInfo;
import com.ubt.alpha1e_edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e_edu.ui.fragment.CustomInstructionPlayActionFragment;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.LinkedHashMap;

/**
 * 类名
 * @author lihai
 * @description 指令动作选择类。
 * @date 2017.04.11
 *
 */

public class CustomInstructionPlayActionActivity extends BaseActivity implements BaseDiaUI {

    private static final String TAG = "CustomInstructionPlayActionActivity";

    //定义UI变量
    public TabLayout tabActionType ;
    public LinearLayout llBottomFinish;
    private Button btnTopFinish;

    //定义类成员变量
    private CustomInstructionPlayActionFragment mPlayActionFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment mCurrentFragment;
    private LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();

    private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; //初始默认横竖屛默认值 默认为竖屏
    private int mRequestPosition = 0; // 请求下标索引 0为内置动作  1 为我的下载  2 我的创建
    private InstructionInfo mInstructionInfo = null;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                default:
                    break;
            }
        }
    };

    /**
     * 启动Activty方法
     * @param activity 启动类
     * @param screenOrientation 横竖屏
     * @param position 设置数据的原始下标
     * @param instructionInfo 设置数据的指令
     * @param requestCode 请求码
     */
    public static void launchActivity(Activity activity, int screenOrientation, int position, InstructionInfo instructionInfo, int requestCode)
    {
        Intent intent = new Intent();
        intent.setClass(activity,CustomInstructionPlayActionActivity.class);
        intent.putExtra(Constant.SCREEN_ORIENTATION,screenOrientation);
        intent.putExtra(Constant.INSTRUCTION_SET_POSITION,position);
        intent.putExtra(Constant.INSTRUCTION_INFO_KEY, PG.convertParcelable(instructionInfo));
        activity.startActivityForResult(intent,requestCode);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_instruction_playaction);

        mScreenOrientation = getIntent().getIntExtra(Constant.SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mInstructionInfo = getIntent().getExtras().getParcelable(Constant.INSTRUCTION_INFO_KEY);

        initUI();
        initControlListener();
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(BannerDetailActivity.class.getSimpleName());
        super.onResume();

        switchScreemOrientation();
    }

    /**
     * 切换横竖屛
     */
    private void switchScreemOrientation(){

        UbtLog.d(TAG,"mScreenOrientation == " + mScreenOrientation);
        if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                && mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            //设置为横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                && mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){

            //设置为竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //横竖屏 UI选择性显示
        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            btnTopFinish.setVisibility(View.VISIBLE);
            llBottomFinish.setVisibility(View.GONE);
        }else {

            btnTopFinish.setVisibility(View.GONE);
            llBottomFinish.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * 初始化UI
     */
    @Override
    protected void initUI() {

        initTitle(getStringResources("ui_cycle_title"));
        initTitleSave(finishListener,getStringResources("ui_common_complete"));

        llBottomFinish = (LinearLayout) findViewById(R.id.ll_instruction_action_select_finish);
        btnTopFinish = (Button) findViewById(R.id.btn_base_save);
        btnTopFinish.setText(getStringResources("ui_common_complete"));

        //给TabLayout增加Tab, 并关联ViewPager
        tabActionType = (TabLayout) findViewById(R.id.tab_action_type);
        TabLayout.Tab tab1 = tabActionType.newTab().setText(getStringResources("ui_myaction_built_in"));
        TabLayout.Tab tab2 = tabActionType.newTab().setText(getStringResources("ui_action_main_download"));
        TabLayout.Tab tab3 = tabActionType.newTab().setText(getStringResources("ui_myaction_creation"));
        tabActionType.setTabMode(TabLayout.MODE_SCROLLABLE);

        mRequestPosition = mInstructionInfo.replySubType;
        UbtLog.d(TAG,"mRequestPosition = " + mRequestPosition);
        if(mRequestPosition == 1){
            tabActionType.addTab(tab1,false);
            tabActionType.addTab(tab2,true);
            tabActionType.addTab(tab3,false);
        }else if(mRequestPosition == 2){
            tabActionType.addTab(tab1,false);
            tabActionType.addTab(tab2,false);
            tabActionType.addTab(tab3,true);
        }else {
            tabActionType.addTab(tab1,true);
            tabActionType.addTab(tab2,false);
            tabActionType.addTab(tab3,false);
        }

        mPlayActionFragment = CustomInstructionPlayActionFragment.newInstance(mRequestPosition);
        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.rl_fragment_content, mPlayActionFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = mPlayActionFragment;
        mFragmentCache.put(mRequestPosition, mCurrentFragment);

    }

    /**
     * 注册按钮监听器
     */
    @Override
    protected void initControlListener() {

        tabActionType.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                UbtLog.d(TAG,"onTabSelected : "+tab.getPosition());

                mRequestPosition = tab.getPosition();
                Fragment f = mFragmentCache.containsKey(mRequestPosition) ? mFragmentCache.get(mRequestPosition)
                        : CustomInstructionPlayActionFragment.newInstance(mRequestPosition);
                if (!mFragmentCache.containsKey(mRequestPosition)) {
                    mFragmentCache.put(mRequestPosition, f);
                }

                loadFragment(f);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        llBottomFinish.setOnClickListener(finishListener);
        btnTopFinish.setOnClickListener(finishListener);
    }

    /**
     * 点击完成监听器
     */
    private View.OnClickListener finishListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String selectActionName = ((CustomInstructionPlayActionFragment)mCurrentFragment).getSelectAction();
            if(!TextUtils.isEmpty(selectActionName)){
                mInstructionInfo.replySubType = mRequestPosition;
                mInstructionInfo.replyType = 1;
                mInstructionInfo.reply = selectActionName;

                Intent intent = new Intent();
                intent.putExtra(Constant.INSTRUCTION_SET_POSITION,getIntent().getIntExtra(Constant.INSTRUCTION_SET_POSITION,-1));
                intent.putExtra(Constant.INSTRUCTION_INFO_KEY,PG.convertParcelable(mInstructionInfo));
                setResult(Constant.INSTRUCTION_PLAY_ACTION_RESPONSE_CODE,intent);
                CustomInstructionPlayActionActivity.this.finish();
            }else {
                Toast.makeText(CustomInstructionPlayActionActivity.this,
                        getStringResources("ui_robot_instruction_play_action_empty"),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    /**
     * Fragment 切换方法
     * @param targetFragment 目标Fragment
     */
    private void loadFragment(Fragment targetFragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        UbtLog.d(TAG,"targetFragment.isAdded()->" + (targetFragment.isAdded()));

        ((CustomInstructionPlayActionFragment)mCurrentFragment).removeListeners();
        if (!targetFragment.isAdded()) {
            transaction
                    .hide(mCurrentFragment)
                    .add(R.id.rl_fragment_content, targetFragment)
                    .commit();
        } else {
            transaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
            ((CustomInstructionPlayActionFragment)targetFragment).registerListeners();
        }
        mCurrentFragment = targetFragment;

    }
}
