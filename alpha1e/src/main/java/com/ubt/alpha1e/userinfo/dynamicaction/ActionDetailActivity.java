package com.ubt.alpha1e.userinfo.dynamicaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.GlideRoundTransform;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.loading.LoadingDialog;
import com.ubt.alpha1e.data.TimeTools;
import com.ubt.alpha1e.data.model.DownloadProgressInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubt.alpha1e.AlphaApplication.mContext;

public class ActionDetailActivity extends MVPBaseActivity<DynamicActionContract.View, DynamicActionPresenter> implements DynamicActionContract.View, DownLoadActionManager.DownLoadActionListener {

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

    DynamicActionModel mDynamicActionModel;
    public static String dynamicModel = "dynamicModel";
    @BindView(R.id.rl_play_action)
    RelativeLayout mRlPlayAction;
    @BindView(R.id.view_line)
    View mViewLine;
    @BindView(R.id.view_line1)
    View mViewLine1;
    @BindView(R.id.progress_download)
    ProgressBar mProgressDownload;

    public static int REQUEST_CODE = 1000;
    public static String DELETE_RESULT = "delete_action";
    public static String DELETE_ACTIONID = "delete_action_id";
    private boolean isShowHibitsDialog = false;

    public static void launch(Activity context, DynamicActionModel mDynamicActionModel) {
        Intent intent = new Intent(context, ActionDetailActivity.class);
        intent.putExtra(dynamicModel, mDynamicActionModel);
        context.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDynamicActionModel = (DynamicActionModel) getIntent().getSerializableExtra(dynamicModel);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DownLoadActionManager.getInstance(this).addDownLoadActionListener(this);
    }

    /**
     * 初始化UI
     */
    @Override
    protected void initUI() {
        mTvActionName.setText(mDynamicActionModel.getActionName());
        mTvActionCreateTime.setText(TimeTools.format(mDynamicActionModel.getActionDate()) + "创建");
        mTvActionTime.setText(TimeTools.getMMTime(mDynamicActionModel.getActionTime() * 1000));
        mTvContent.setText(mDynamicActionModel.getActionDesciber());
        Glide.with(mContext).load(mDynamicActionModel.getActionHeadUrl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.action_sport_1b)
                .transform(new GlideRoundTransform(mContext, 10))
                .into(mIvCover);
        if (mDynamicActionModel.getActionStatu() == 1) {
            setPlaBtnAction(2);
        } else if (mDynamicActionModel.getActionStatu() == 2) {
            setPlaBtnAction(3);
        } else if (mDynamicActionModel.getActionStatu() == 0) {
            setPlaBtnAction(1);
        }

        int actionType = mDynamicActionModel.getActionType();
        if (actionType == 1) {//舞蹈
            mTvActionType.setText("舞蹈");
            mIvActionType1.setImageResource(R.drawable.mynew_publish_dance);

        } else if (actionType == 2) {//故事
            mTvActionType.setText("故事");
            mIvActionType1.setImageResource(R.drawable.mynew_publish_story);

        } else if (actionType == 3) {//运动
            mTvActionType.setText("运动");
            mIvActionType1.setImageResource(R.drawable.myniew_publish_sport);

        } else if (actionType == 4) {//儿歌
            mTvActionType.setText("儿歌");
            mIvActionType1.setImageResource(R.drawable.mynew_publish_childsong);

        } else if (actionType == 5) {//科普
            mTvActionType.setText("科普");
            mIvActionType1.setImageResource(R.drawable.mynew_publish_science);
        } else {
            mTvActionType.setText("舞蹈");
            mIvActionType1.setImageResource(R.drawable.mynew_publish_science);
        }

    }

    @OnClick({R.id.iv_back, R.id.rl_play_action, R.id.iv_delete})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_play_action:
                playAction();
                break;
            case R.id.iv_delete:
                showDeleteDialog();

                break;
            default:
        }
    }

    /**
     * 播放按钮
     */
    private void playAction() {
        if (BaseHelper.isStartHibitsProcess) {
            showStartHibitsProcess();
            return;
        }

        int actionStatu = mDynamicActionModel.getActionStatu();
        if (actionStatu == 0) {
            if (mDynamicActionModel.isDownload()) {//已经下载
                setPlaBtnAction(2);
                DownLoadActionManager.getInstance(this).playAction(true, mDynamicActionModel);
                mDynamicActionModel.setActionStatu(1);
            } else {//没有下载，需要下载
                DownLoadActionManager.getInstance(this).downRobotAction(mDynamicActionModel);
            }
        } else if (actionStatu == 1) {//正在播放
            DownLoadActionManager.getInstance(this).stopAction(true);
            mDynamicActionModel.setActionStatu(0);
            setPlaBtnAction(1);
        } else if (actionStatu == 2) {//正在下载
            setPlaBtnAction(3);
        }
    }


    /**
     * 设置播放按钮状态 1播放状态 2暂停状态 3下载状态
     *
     * @param type
     */
    public void setPlaBtnAction(int type) {
        if (type == 1) {
            mProgressDownload.setVisibility(View.GONE);
            mTvPlay.setVisibility(View.VISIBLE);
            mTvPlay.setText("播放");
            Drawable drawable = getDrawable(R.drawable.ic_btn_play1);
            mTvPlay.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else if (type == 2) {
            mProgressDownload.setVisibility(View.GONE);
            mTvPlay.setVisibility(View.VISIBLE);
            mTvPlay.setText("暂停");
            Drawable drawable = getResources().getDrawable(R.drawable.ic_btn_stop1);
            mTvPlay.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else if (type == 3) {
            mProgressDownload.setVisibility(View.VISIBLE);
            mTvPlay.setVisibility(View.GONE);
            if ((int) mDynamicActionModel.getDownloadProgress() == 0) {
                mTvPlay.setVisibility(View.VISIBLE);
                mTvPlay.setText("等待中");
                mTvPlay.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            } else {
                mTvPlay.setVisibility(View.GONE);
                mProgressDownload.setProgress((int) mDynamicActionModel.getDownloadProgress());
            }

        }
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
    public void setDynamicData(boolean statu, int type, List<DynamicActionModel> list) {

    }

    /**
     * 删除结果
     *
     * @param isSuccess
     */
    @Override
    public void deleteActionResult(boolean isSuccess) {
        LoadingDialog.dismiss(this);
        if (isSuccess) {
            Intent intent = new Intent();
            intent.putExtra(DELETE_RESULT, true);
            intent.putExtra(DELETE_ACTIONID, mDynamicActionModel.getActionId());
            setResult(REQUEST_CODE, intent);
            finish();
        }
    }

    @Override
    public void getRobotActionLists(List<String> list) {

    }

    @Override
    public void getDownLoadProgress(DynamicActionModel info, DownloadProgressInfo downloadProgressInfo) {
        if (info.getActionId() == mDynamicActionModel.getActionId()) {
            if (downloadProgressInfo.status == 1) {//正在下载
                String progress = downloadProgressInfo.progress;
                mDynamicActionModel.setDownloadProgress(Double.parseDouble(downloadProgressInfo.progress));
                setPlaBtnAction(3);
                UbtLog.d("praseDownloadData", "progress=====" + progress);
            } else if (downloadProgressInfo.status == 2) {//下载成功后立即播放
                mDynamicActionModel.setActionStatu(1);
                DownLoadActionManager.getInstance(this).playAction(true, mDynamicActionModel);
                setPlaBtnAction(2);
            } else if (downloadProgressInfo.status == 3) {//机器人未联网
                ToastUtils.showShort("机器人未联网");
                mDynamicActionModel.setActionStatu(0);
                setPlaBtnAction(1);
            } else {//下载失败
                ToastUtils.showShort("下载失败");
                mDynamicActionModel.setActionStatu(0);
                setPlaBtnAction(1);
            }
        }
    }

    @Override
    public void playActionFinish(String actionName) {
        if (actionName.contains(mDynamicActionModel.getActionOriginalId())) {
            mDynamicActionModel.setActionStatu(0);
            setPlaBtnAction(1);
        }
    }

    @Override
    public void onBlutheDisconnected() {

    }

    @Override
    public void doActionPlay(long actionId, int statu) {

    }

    @Override
    public void doTapHead() {
        mDynamicActionModel.setActionStatu(0);
        setPlaBtnAction(1);
    }

    @Override
    public void isAlpha1EConnectNet(boolean statu) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownLoadActionManager.getInstance(this).removeDownLoadActionListener(this);
    }


    @Override
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        UbtLog.d("ActionDetailActivity", "onEventRobot===========" + event.isHibitsProcessStatus());
        if (event.getEvent() == RobotEvent.Event.HIBITS_PROCESS_STATUS) {
            //流程开始，收到行为提醒状态改变，开始则退出流程，并Toast提示
            if (event.isHibitsProcessStatus()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDynamicActionModel.setActionStatu(0);
                        setPlaBtnAction(1);
                        if (!isShowHibitsDialog) {
                            showStartHibitsProcess();
                        }
                    }
                });

            }
        }
    }

    //显示蓝牙连接对话框
    private void showDeleteDialog() {
        new ConfirmDialog(this).builder()
                .setMsg("确定要删除这个内容吗？")
                .setCancelable(true)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.deleteActionById(mDynamicActionModel.getActionId());
                        LoadingDialog.show(ActionDetailActivity.this);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }


    //显示行为提醒弹出框
    public void showStartHibitsProcess() {
        isShowHibitsDialog = true;
        String msg = "行为习惯正在进行中，请先完成";
        String position = "好的";

        msg = ResourceManager.getInstance(this).getStringResources("ui_habits_process_starting");
        position = ResourceManager.getInstance(this).getStringResources("ui_common_ok");

        new ConfirmDialog(this)
                .builder()
                .setMsg(msg)
                .setCancelable(false)
                .setPositiveButton(position, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isShowHibitsDialog = false;
                    }
                }).show();
    }

}
