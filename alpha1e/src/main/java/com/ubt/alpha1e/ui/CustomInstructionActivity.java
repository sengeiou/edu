package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.CustomInstructionAdapter;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.model.InstructionInfo;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名
 * @author lihai
 * @description 指令列表类。
 * @date 2017.04.11
 *
 */
public class CustomInstructionActivity extends BaseActivity implements BaseDiaUI {

    private static final String TAG = "CustomInstructionActivity";

    public static final int UPDATE_ITEMS = 0; //更新单个对象
    public static final int UPDATE_ALL = 1;   //更新整个列表
    public static final int UPDATE_ADD = 2;   //添加指令
    public static final int UPDATE_PLAY_TEXT = 3;   //更新文本指令
    public static final int UPDATE_PLAY_ACTION = 4;   //更新动作指令

    //定义UI变量
    private ImageView imgRight;
    private RecyclerView recyclerView;
    private LinearLayout llCustomInstructionAdd = null;

    //定义类成员变量
    private LinearLayoutManager mLinearLayoutManager;
    private CustomInstructionAdapter mCustomInstructionAdapter;
    private LoadingDialog mLoadingDialog;

    private List<InstructionInfo> mDatas = new ArrayList<>();
    private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; //初始默认横竖屛默认值 默认为竖屏

    //定义Handler处理对象
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_ITEMS:
                    mCustomInstructionAdapter.notifyItemChanged((int) msg.obj);
                    break;
                case UPDATE_ALL:
                    mCustomInstructionAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_ADD:
                    InstructionInfo instructionInfo = (InstructionInfo) msg.obj;
                    mDatas.add(instructionInfo);
                    mCustomInstructionAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_PLAY_TEXT:
                    int position = msg.arg1;
                    String playContent = (String) msg.obj;
                    UbtLog.d(TAG,"position = " + position  + "  playContent = " + playContent);
                    for(int i = 0;i< mDatas.size(); i++){
                        if(i == position){
                            InstructionInfo info = mDatas.get(position);
                            info.reply = playContent;
                            mCustomInstructionAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                    break;
                case UPDATE_PLAY_ACTION:
                    int pos = msg.arg1;
                    InstructionInfo updateInfo = (InstructionInfo) msg.obj;
                    for(int i = 0;i< mDatas.size(); i++){
                        if(i == pos){
                            InstructionInfo info = mDatas.get(pos);
                            info.reply = updateInfo.reply;
                            info.replyType = updateInfo.replyType;
                            info.replySubType = updateInfo.replySubType;
                            mCustomInstructionAdapter.notifyItemChanged(i);
                            break;
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 启动Activty方法
     * @param activity 启动类
     * @param screenOrientation 横竖屏
     */
    public static void launchActivity(Activity activity,int screenOrientation)
    {
        Intent intent = new Intent();
        intent.setClass(activity,CustomInstructionActivity.class);
        intent.putExtra(Constant.SCREEN_ORIENTATION,screenOrientation);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_instruction);
        mScreenOrientation = getIntent().getIntExtra(Constant.SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initUI();
        initRecyclerview();
        initControlListener();
    }
    public void sendMessage(Object object, int what) {
        Message msg = new Message();
        msg.obj = object;
        msg.what = what;
        if (mHandler != null){
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 初始化Recyclerview
     */
    private void initRecyclerview(){

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_custom_instruction);
        mLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mCustomInstructionAdapter = new CustomInstructionAdapter(mDatas,this,mHandler);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mCustomInstructionAdapter);
        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        recyclerView.getItemAnimator().setSupportsChangeAnimations(false);

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
            imgRight.setVisibility(View.VISIBLE);
            llCustomInstructionAdd.setVisibility(View.GONE);
        }else {

            imgRight.setVisibility(View.GONE);
            llCustomInstructionAdd.setVisibility(View.VISIBLE);
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

        initTitle(getStringResources("ui_instruction"));

        llCustomInstructionAdd = (LinearLayout) findViewById(R.id.ll_custom_instruction_add);

        imgRight = (ImageView) findViewById(R.id.img_right);
        imgRight.setImageResource(R.drawable.instruction_top_add);

        mLoadingDialog = LoadingDialog.getInstance(this,this);
        mLoadingDialog.setDoCancelable(true,6);
    }

    /**
     * 注册按钮监听器
     */
    @Override
    protected void initControlListener() {

        imgRight.setOnClickListener(mNextClickListener);
        llCustomInstructionAdd.setOnClickListener(mNextClickListener);
    }

    /**
     * 定义点击下一步监听器
     */
    private View.OnClickListener mNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            CustomInstructionSelActivity.launchActivity(CustomInstructionActivity.this,
                    getRequestedOrientation(), Constant.INSTRUCTION_SELECT_REQUEST_CODE);

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constant.INSTRUCTION_SELECT_REQUEST_CODE
                && resultCode == Constant.INSTRUCTION_SELECT_RESPONSE_CODE){
            //添加指令
            if(data.getExtras() != null){
                InstructionInfo instructionInfo = data.getExtras().getParcelable(Constant.INSTRUCTION_INFO_KEY);
                Message msg = new Message();
                msg.what = UPDATE_ADD;
                msg.obj = instructionInfo;
                mHandler.sendMessage(msg);
            }
        }else if(requestCode == Constant.INSTRUCTION_PLAY_TEXT_REQUEST_CODE
                && resultCode == Constant.INSTRUCTION_PLAY_TEXT_RESPONSE_CODE){

            //设置指令文本
            if(data.getExtras() != null){
                String playContent = data.getExtras().getString(Constant.INSTRUCTION_PLAY_TEXT);
                int position = data.getExtras().getInt(Constant.INSTRUCTION_SET_POSITION,-1);
                Message msg = new Message();
                msg.what = UPDATE_PLAY_TEXT;
                msg.obj = playContent;
                msg.arg1 = position;
                mHandler.sendMessage(msg);
            }
        }else if(requestCode == Constant.INSTRUCTION_PLAY_ACTION_REQUEST_CODE
                && resultCode == Constant.INSTRUCTION_PLAY_ACTION_RESPONSE_CODE){

            //设置指令动作
            if(data.getExtras() != null){
                InstructionInfo instructionInfo = data.getExtras().getParcelable(Constant.INSTRUCTION_INFO_KEY);
                int position = data.getExtras().getInt(Constant.INSTRUCTION_SET_POSITION,-1);

                Message msg = new Message();
                msg.what = UPDATE_PLAY_TEXT;
                msg.obj = instructionInfo;
                msg.arg1 = position;
                mHandler.sendMessage(msg);
            }
        }

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }
}
