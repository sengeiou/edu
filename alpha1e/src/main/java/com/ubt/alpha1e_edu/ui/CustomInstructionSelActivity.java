package com.ubt.alpha1e_edu.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.baoyz.pg.PG;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.Constant;
import com.ubt.alpha1e_edu.data.model.InstructionInfo;
import com.ubt.alpha1e_edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e_edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e_edu.ui.fragment.CustomInstructionSelFragment;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.LinkedHashMap;

/**
 * 类名
 * @author lihai
 * @description 指令选择列表类。
 * @date 2017.04.11
 *
 */
public class CustomInstructionSelActivity extends BaseActivity implements BaseDiaUI {

    private static final String TAG = "CustomInstructionSelActivity";

    //定义UI变量
    private TabLayout tabLayout;
    private LinearLayout llBottomNext = null;
    private Button btnTopNext;

    //定义类成员变量
    private CustomInstructionSelFragment mInstructionSelFragment;
    private LoadingDialog mLoadingDialog;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment mCurrentFragment;
    private LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();

    private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; //初始默认横竖屛默认值 默认为竖屏
    private int mInstructionType = 0; // 指令类型 0为语音  1 为传感器

    /**
     * 启动Activty方法
     * @param activity 启动类
     * @param screenOrientation 横竖屏
     * @param requestCode 请求码
     */
    public static void launchActivity(Activity activity,int screenOrientation,int requestCode)
    {
        Intent intent = new Intent();
        intent.setClass(activity,CustomInstructionSelActivity.class);
        intent.putExtra(Constant.SCREEN_ORIENTATION,screenOrientation);
        activity.startActivityForResult(intent,requestCode);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_instruction_sel);
        mScreenOrientation = getIntent().getIntExtra(Constant.SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        UbtLog.d(TAG,"mScreenOrientation => " + mScreenOrientation);
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
            btnTopNext.setVisibility(View.VISIBLE);
            llBottomNext.setVisibility(View.GONE);
        }else {

            btnTopNext.setVisibility(View.GONE);
            llBottomNext.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示转动对话框
     */
    public void showDialog() {
        if(mLoadingDialog!=null&&!mLoadingDialog.isShowing())
        {
            mLoadingDialog.show();
        }
    }

    /**
     * 消失对话框
     */
    public void dismissDialog() {

        if(mLoadingDialog!=null&&mLoadingDialog.isShowing()&&!this.isFinishing())
        {
            mLoadingDialog.cancel();
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

        initTitle(getStringResources("ui_instruction_select"));

        llBottomNext = (LinearLayout) findViewById(R.id.ll_instruction_select_next);
        btnTopNext = (Button) findViewById(R.id.btn_base_save);

        //给TabLayout增加Tab, 并关联ViewPager
        tabLayout = (TabLayout) findViewById(R.id.tab_instruction_type);
        TabLayout.Tab tab1 = tabLayout.newTab().setText(getStringResources("ui_instruction_voice"));
        TabLayout.Tab tab2 = tabLayout.newTab().setText(getStringResources("ui_instruction_sensor"));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.addTab(tab1,true);
        tabLayout.addTab(tab2,false);

        mInstructionSelFragment = CustomInstructionSelFragment.newInstance(mInstructionType);
        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.rl_fragment_content, mInstructionSelFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = mInstructionSelFragment;
        mFragmentCache.put(mInstructionType, mCurrentFragment);


        mLoadingDialog = LoadingDialog.getInstance(this,this);
        mLoadingDialog.setDoCancelable(true,6);
    }

    /**
     * 注册按钮监听器
     */
    @Override
    protected void initControlListener() {

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                UbtLog.d(TAG,"onTabSelected:"+tab.getPosition());

                mInstructionType = tab.getPosition();
                Fragment f = mFragmentCache.containsKey(mInstructionType) ? mFragmentCache.get(mInstructionType)
                        : CustomInstructionSelFragment.newInstance(mInstructionType);
                if (!mFragmentCache.containsKey(mInstructionType)) {
                    mFragmentCache.put(mInstructionType, f);
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

        llBottomNext.setOnClickListener(mNextClickListener);
        btnTopNext.setOnClickListener(mNextClickListener);
    }

    /**
     * 定义点击下一步监听器
     */
    private View.OnClickListener mNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InstructionInfo instructionInfo = ((CustomInstructionSelFragment)mCurrentFragment).getCurrentSelectInstructionInfo();
            if(instructionInfo != null){
                Intent intent = new Intent();
                intent.putExtra(Constant.INSTRUCTION_INFO_KEY, PG.convertParcelable(instructionInfo));
                setResult(Constant.INSTRUCTION_SELECT_RESPONSE_CODE,intent);
                CustomInstructionSelActivity.this.finish();
            }else {
                Toast.makeText(CustomInstructionSelActivity.this,getStringResources("ui_robot_select_instruction_empty"),Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 更新UI字符串
     */
    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        btnTopNext.setText(getStringResources("ui_perfect_next"));
    }

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
        if (!targetFragment.isAdded()) {
            ((CustomInstructionSelFragment)mCurrentFragment).removeListeners();
            transaction
                    .hide(mCurrentFragment)
                    .add(R.id.rl_fragment_content, targetFragment)
                    .commit();
        } else {
            ((CustomInstructionSelFragment)mCurrentFragment).removeListeners();
            ((CustomInstructionSelFragment)targetFragment).registerListeners();
            transaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;

    }
}
