package com.ubt.alpha1e.edu.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.Constant;
import com.ubt.alpha1e.edu.ui.custom.DesContentEditText;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

/**
 * 类名
 * @author lihai
 * @description 定义播放文本处理类。
 * @date 2017.04.11
 *
 */
public class CustomInstructionPlayTextActivity extends BaseActivity implements BaseDiaUI {

    private static final String TAG = "CustomInstructionPlayTextActivity";

    //定义UI变量
    private DesContentEditText edtPlayContent;
    private TextView tvTextSum;
    private Button btnFinish;

    //定义类成员变量
    private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; //初始默认横竖屛默认值 默认为竖屏
    private LoadingDialog mLoadingDialog;

    /**
     * 启动Activty方法
     * @param activity 启动类
     * @param screenOrientation 横竖屏
     * @param position 设置数据的原始下标
     * @param playContent 设置数据的原始内容
     * @param requestCode 请求码
     */
    public static void launchActivity(Activity activity,int screenOrientation,int position,String playContent,int requestCode)
    {
        Intent intent = new Intent();
        intent.setClass(activity,CustomInstructionPlayTextActivity.class);
        intent.putExtra(Constant.SCREEN_ORIENTATION,screenOrientation);
        intent.putExtra(Constant.INSTRUCTION_SET_POSITION,position);
        intent.putExtra(Constant.INSTRUCTION_PLAY_TEXT,playContent);
        activity.startActivityForResult(intent,requestCode);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_instruction_playtext);
        mScreenOrientation = getIntent().getIntExtra(Constant.SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initUI();
        initControlListener();
        initData();
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(BannerDetailActivity.class.getSimpleName());
        super.onResume();

        switchScreemOrientation();
        setSubmitButtonState();
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

        initTitle(getStringResources("ui_instruction_play_text"));

        tvTextSum = (TextView) findViewById(R.id.tv_max_length);
        edtPlayContent = (DesContentEditText) findViewById(R.id.edt_play_connect);
        edtPlayContent.addTextChangedListener(mTextWatcher);

        btnFinish = (Button) findViewById(R.id.btn_base_save);
        btnFinish.setText(getStringResources("ui_distribute_publish"));
        btnFinish.setVisibility(View.VISIBLE);

        mLoadingDialog = LoadingDialog.getInstance(this,this);
        mLoadingDialog.setDoCancelable(true,6);

    }

    /**
     * 注册按钮监听器
     */
    @Override
    protected void initControlListener() {
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playContent = edtPlayContent.getText().toString();
                if(!TextUtils.isEmpty(playContent)){
                    Intent intent = new Intent();
                    intent.putExtra(Constant.INSTRUCTION_PLAY_TEXT, playContent.toString());
                    intent.putExtra(Constant.INSTRUCTION_SET_POSITION,getIntent().getIntExtra(Constant.INSTRUCTION_SET_POSITION,-1));
                    setResult(Constant.INSTRUCTION_PLAY_TEXT_RESPONSE_CODE,intent);
                    CustomInstructionPlayTextActivity.this.finish();
                }else {
                    Toast.makeText(CustomInstructionPlayTextActivity.this,
                            getStringResources("ui_robot_instruction_play_text_empty"),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    public void initData(){

        String initPlayContent = getIntent().getStringExtra(Constant.INSTRUCTION_PLAY_TEXT);
        if(!TextUtils.isEmpty(initPlayContent)){
            edtPlayContent.setText(initPlayContent);
            edtPlayContent.setSelection(initPlayContent.length());
        }
    }

    /**
     * 文本内容改变监听器
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            tvTextSum.setText("" + s.length() + "/140");
        }

        @Override
        public void afterTextChanged(Editable editable) {
            setSubmitButtonState();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }
    };

    /**
     * 文本内容改变，按钮处理逻辑
     * 内容为空时，不可点击，非空时，可以点击
     */
    private void setSubmitButtonState(){

        if(edtPlayContent.getText().toString().trim().length() > 0){
            btnFinish.setTextColor(getResources().getColor(R.color.T5));
            btnFinish.setClickable(true);
        }else{
            btnFinish.setTextColor(getResources().getColor(R.color.T7));
            btnFinish.setClickable(false);
        }

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }
}
