package com.ubt.alpha1e.userinfo.dynamicaction;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;

import java.util.List;

import butterknife.BindView;

public class ActionDetailActivity extends MVPBaseActivity<DynamicActionContract.View, DynamicActionPresenter> implements DynamicActionContract.View {


    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_delete)
    ImageView mIvDelete;
    @BindView(R.id.iv_cover)
    ImageView mIvCover;
    @BindView(R.id.tv_action_name)
    TextView mTvActionName;
    @BindView(R.id.iv_action_type)
    ImageView mIvActionType;
    @BindView(R.id.ll_name)
    LinearLayout mLlName;
    @BindView(R.id.tv_action_create_time)
    TextView mTvActionCreateTime;
    @BindView(R.id.tv_action_time)
    TextView mTvActionTime;
    @BindView(R.id.iv_action_type1)
    ImageView mIvActionType1;
    @BindView(R.id.tv_action_type)
    TextView mTvActionType;
    @BindView(R.id.ll_type)
    LinearLayout mLlType;
    @BindView(R.id.tv_flag)
    TextView mTvFlag;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.btn_publish)
    Button mBtnPublish;
    @BindView(R.id.tv_play)
    TextView mTvPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }


    @Override
    public int getContentViewId() {
        return R.layout.activity_action_detail;
    }

    @Override
    public void setDynamicData(boolean statu,int type,List<DynamicActionModel> list) {

    }
}
