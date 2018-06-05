package com.ubt.alpha1e_edu.community.actionselect;


import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.base.AppManager;
import com.ubt.alpha1e_edu.base.ResourceManager;
import com.ubt.alpha1e_edu.base.ToastUtils;
import com.ubt.alpha1e_edu.base.loading.LoadingDialog;
import com.ubt.alpha1e_edu.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e_edu.bluetoothandnet.netconnect.NetconnectActivity;
import com.ubt.alpha1e_edu.data.Constant;
import com.ubt.alpha1e_edu.data.model.DownloadProgressInfo;
import com.ubt.alpha1e_edu.event.RobotEvent;
import com.ubt.alpha1e_edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e_edu.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e_edu.ui.helper.BaseHelper;
import com.ubt.alpha1e_edu.userinfo.dynamicaction.DownLoadActionManager;
import com.ubt.alpha1e_edu.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ActionSelectActivity extends MVPBaseActivity<ActionSelectContract.View, ActionSelectPresenter> implements ActionSelectContract.View, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, DownLoadActionManager.DownLoadActionListener {

    private static final String TAG = ActionSelectActivity.class.getSimpleName();

    /**
     * 获取机器人动作列表超时
     */
    private final static int GET_ROBOT_ACTION_TIMEOUT = 1001;
    private final static int ROBOT_NOT_NETWORK = 1002;
    private final static int REFRESH_DATA = 1003;

    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rv_action_list)
    RecyclerView mRvActionList;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private ActionSelectRecyclerAdapter mAdapter;
    private List<DynamicActionModel> mDynamicActionModels = new ArrayList<>();//原创列表
    private List<String> robotActionList = new ArrayList<>();//机器人下载列表

    private View emptyView;
    private TextView tvEmpty;
    private TextView tvRetry;
    private LinearLayout llError;
    private ImageView ivStatu;

    private int currentType = 0;//上拉下拉类型
    private boolean isNoneFinishLoadMore = false;//是否可以上拉加载 true不能上拉 false 可以上拉

    private int page = 1;
    private int offset = 8;

    private boolean isShowHibitsDialog = false;
    private boolean isShowLowBarry = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_ROBOT_ACTION_TIMEOUT:
                    finishRefresh();
                    break;
                case ROBOT_NOT_NETWORK:
                    for (int i = 0; i < mDynamicActionModels.size(); i++) {
                        if (mDynamicActionModels.get(i).getActionStatu() == 2) {
                            UbtLog.d(TAG, "actionName==" + mDynamicActionModels.get(i));
                            mDynamicActionModels.get(i).setActionStatu(0);
                        }
                    }
                    showNetWorkConnectDialog();
                    break;
                case REFRESH_DATA:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void initUI() {
        tvBaseTitleName.setText("原创动作");
        ivTitleRight.setVisibility(View.VISIBLE);

        mAdapter = new ActionSelectRecyclerAdapter(R.layout.layout_action_select_item, mDynamicActionModels);
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setHasStableIds(true);
        mRvActionList.setLayoutManager(new GridLayoutManager(this, 3));
        mRvActionList.setAdapter(mAdapter);

        emptyView = LayoutInflater.from(this).inflate(R.layout.layout_empty, null);
        tvEmpty = (TextView) emptyView.findViewById(R.id.tv_no_data);
        tvRetry = emptyView.findViewById(R.id.tv_retry);
        llError = emptyView.findViewById(R.id.ll_error_layout);
        ivStatu = emptyView.findViewById(R.id.iv_no_data);
        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                mPresenter.getDynamicData(0, page, offset);
                UbtLog.d(TAG, "重试一次");
                LoadingDialog.show(ActionSelectActivity.this);

            }
        });

        //触发自动刷新
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setEnableAutoLoadmore(true);//开启自动加载功能（非必须）
        mRefreshLayout.setEnableScrollContentWhenLoaded(true);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                page = 1;
                mPresenter.getDynamicData(0, page, offset);

            }
        });

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                ++page;
                mPresenter.getDynamicData(1, page, offset);
            }
        });
    }


    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_action_select;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        DownLoadActionManager.getInstance(this).addDownLoadActionListener(this);
        initUI();
    }

    /**
     * 刷新获取数据
     *
     * @param status 是否刷新成功
     * @param type
     * @param list
     */
    @Override
    public void setDynamicData(boolean status, int type, List<DynamicActionModel> list) {
        currentType = type;
        if (status) {
            refreshData(type, list);
        } else {//请求失败
            if (mDynamicActionModels.size() == 0) {//如果请求失败切列表数据为0，则显示错误页面
                showStatuLayout(2);
            } else {
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadmore();
            }
        }
        LoadingDialog.dismiss(this);
    }

    @Override
    public void deleteActionResult(boolean isSuccess) {

    }

    /**
     * 刷新数据后，如果机器人联网则从机器人拿取已下载的动作列表
     *
     * @param type 0下拉刷新 1上拉加载
     * @param list
     */
    private void refreshData(int type, List<DynamicActionModel> list) {
        if (null != list) {
            if (type == 0) {
                mDynamicActionModels.clear();
                mDynamicActionModels.addAll(list);
            } else if (type == 1) {
                mDynamicActionModels.addAll(list);
            }
            if (list.size() < 8) {
                isNoneFinishLoadMore = true;
            } else {
                isNoneFinishLoadMore = false;
            }
            if (isBulueToothConnected() && type == 0) {//蓝牙连接成功则从机器人获取列表动作
                DownLoadActionManager.getInstance(this).getRobotAction();
                mHandler.sendEmptyMessageDelayed(GET_ROBOT_ACTION_TIMEOUT, 3000);
            } else {
                mPresenter.praseGetRobotData(this, robotActionList, mDynamicActionModels);
                finishRefresh();
            }

        }
        UbtLog.d(TAG, "size====" + mDynamicActionModels.size());
    }


    /**
     * 显示空View还是Error View 1表示Empty 2表示Error
     *
     * @param status
     */
    private void showStatuLayout(int status) {
        if (status == 1) {
            tvEmpty.setVisibility(View.VISIBLE);
            llError.setVisibility(View.GONE);
            tvEmpty.setText(ResourceManager.getInstance(this).getStringResources("empty_no_dynamiaction"));
            ivStatu.setImageResource(R.drawable.ic_setting_action_deafult);
        } else if (status == 2) {
            tvRetry.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            tvRetry.getPaint().setAntiAlias(true);//抗锯齿
            tvEmpty.setVisibility(View.GONE);
            llError.setVisibility(View.VISIBLE);
            ivStatu.setImageResource(R.drawable.ic_loading_failed);
            mRefreshLayout.setEnableRefresh(false);
            mRefreshLayout.setEnableLoadmore(false);
            mRefreshLayout.finishRefresh();
            mRefreshLayout.finishLoadmore();
        }
        mAdapter.setEmptyView(emptyView);
    }

    /**
     * 按钮点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.iv_play_status:
                playAction(position);
                break;
            case R.id.iv_select:
                DynamicActionModel model = null;
                for(int i = 0;i < mDynamicActionModels.size(); i++){
                    model = mDynamicActionModels.get(i);
                    if(i == position){
                        if("1".equals(model.getActionStatus())){
                            model.setActionStatus("0");
                        }else {
                            model.setActionStatus("1");
                        }
                    }else {
                        model.setActionStatus("0");
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    /**
     * Item点击播放事件
     *
     * @param position
     */
    private void playAction(int position) {
        if (!isBulueToothConnected()) {
            showBluetoothConnectDialog();
            return;
        }

        if (BaseHelper.isStartHibitsProcess) {
            showStartHibitsProcess();
            return;
        }

        if (BaseHelper.isLowBatteryNotExecuteAction) {
            showLowBatteryDialog();
            return;
        }

        mPresenter.playAction(this, position, mDynamicActionModels);
        mAdapter.notifyDataSetChanged();

    }

    private void showLowBatteryDialog() {
        isShowLowBarry = true;
        new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("提示")
                .setMsg("机器人电量低动作不能执行，请充电！")
                .setCancelable(true)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isShowLowBarry = false;
                        //调到主界面
                        UbtLog.d(TAG, "确定 ");
                    }
                }).show();
    }

    /**
     * Item点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    /**
     * 结束刷新事件
     */
    private void finishRefresh() {
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadmore(true);
        if (currentType == 0) {
            mRefreshLayout.finishRefresh();
            if (isNoneFinishLoadMore) {
                mRefreshLayout.setLoadmoreFinished(true);//将不会再次触发加载更多事件
            } else {
                mRefreshLayout.resetNoMoreData();
            }
        } else if (currentType == 1) {
            if (isNoneFinishLoadMore) {
                mRefreshLayout.finishLoadmoreWithNoMoreData();//将不会再次触发加载更多事件
            } else {
                mRefreshLayout.finishLoadmore();
            }
        }
        if (null == mDynamicActionModels || mDynamicActionModels.size() == 0) {//数据为空
            mRefreshLayout.setEnableLoadmore(false);
            showStatuLayout(1);
        }
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 获取机器人动作列表
     * @param list
     */
    @Override
    public void getRobotActionLists(List<String> list) {
        UbtLog.d(TAG, "获取机器人列表==" + list.toString());
        this.robotActionList = list;
        mHandler.removeMessages(GET_ROBOT_ACTION_TIMEOUT);
        mPresenter.praseGetRobotData(this, list, mDynamicActionModels);
        finishRefresh();
    }

    /**
     * 下载进度
     * @param info
     * @param progressInfo
     */
    @Override
    public void getDownLoadProgress(DynamicActionModel info, DownloadProgressInfo progressInfo) {
        mPresenter.praseDownloadData(this, progressInfo, mDynamicActionModels);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 动作播放结束
     * @param actionName
     */
    @Override
    public void playActionFinish(String actionName) {
        UbtLog.d(TAG,"playActionFinish = " + actionName);
        for (int i = 0; i < mDynamicActionModels.size(); i++) {
            if (actionName.contains(mDynamicActionModels.get(i).getActionOriginalId())) {
                mDynamicActionModels.get(i).setActionStatu(0);
                break;
            }
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    /**
     * 蓝牙掉线
     */
    @Override
    public void onBlutheDisconnected() {//机器人掉线
        UbtLog.d(TAG, "机器人掉线");
        for (int i = 0; i < mDynamicActionModels.size(); i++) {
            mDynamicActionModels.get(i).setActionStatu(0);
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    /**
     * 在详情页播放或者暂停，可以同步到列表页面
     *
     * @param actionId
     * @param status    0结束动作 1播放动作
     */
    @Override
    public void doActionPlay(long actionId, int status) {
        for (int i = 0; i < mDynamicActionModels.size(); i++) {
            if (mDynamicActionModels.get(i).getActionId() == actionId) {
                UbtLog.d(TAG, "actionName==" + mDynamicActionModels.get(i));
                mDynamicActionModels.get(i).setActionStatu(status);
                if (status == 1) {
                    DynamicActionModel model = DownLoadActionManager.getInstance(this).getPlayingInfo();
                    if (null != model && model.getActionId() != actionId) {
                        int position = mPresenter.getPositionById(model.getActionId(), mDynamicActionModels);
                        mDynamicActionModels.get(position).setActionStatu(0);
                    }
                }
                break;
            }
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    /**
     * 拍头打断
     */
    @Override
    public void doTapHead() {
        for (int i = 0; i < mDynamicActionModels.size(); i++) {
            if (mDynamicActionModels.get(i).getActionStatu() == 1) {
                UbtLog.d(TAG, "actionName==" + mDynamicActionModels.get(i));
                mDynamicActionModels.get(i).setActionStatu(0);
            }
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void isAlpha1EConnectNet(boolean status) {
        if (!status) {
            UbtLog.d(TAG,"isAlpha1EConnectNet = " + AppManager.getInstance().currentActivity() );
            mHandler.sendEmptyMessage(ROBOT_NOT_NETWORK);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DownLoadActionManager.getInstance(this).removeDownLoadActionListener(this);
    }

    //显示蓝牙连接对话框
    private void showBluetoothConnectDialog() {
        new ConfirmDialog(this).builder()
                .setTitle("提示")
                .setMsg("请先连接蓝牙和Wi-Fi")
                .setCancelable(true)
                .setPositiveButton("去连接", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "去连接蓝牙 ");
                        Intent intent = new Intent();
                        intent.putExtra(com.ubt.alpha1e_edu.base.Constant.BLUETOOTH_REQUEST, true);
                        intent.setClass(ActionSelectActivity.this, BluetoothconnectActivity.class);
                        startActivityForResult(intent, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                    }
                }).show();
    }


    //显示网络连接对话框
    private void showNetWorkConnectDialog() {
        new ConfirmDialog(this).builder()
                .setTitle("提示")
                .setMsg("请先连接机器人Wi-Fi")
                .setCancelable(true)
                .setPositiveButton("去连接", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "去连接Wifi ");
                        Intent intent = new Intent();
                        intent.setClass(ActionSelectActivity.this, NetconnectActivity.class);
                        startActivity(intent);
                    }
                }).show();
    }

    @Subscribe
    public void onEventRobot(RobotEvent event) {
        UbtLog.d(TAG, "onEventRobot = obj == 1");
        if (event.getEvent() == RobotEvent.Event.HIBITS_PROCESS_STATUS) {
            //流程开始，收到行为提醒状态改变，开始则退出流程，并Toast提示
            UbtLog.d(TAG, "onEventRobot = obj == 2" + event.isHibitsProcessStatus());
            if (event.isHibitsProcessStatus()) {
                UbtLog.d(TAG, "onEventRobot = obj == 3");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onBlutheDisconnected();
                        if (!isShowHibitsDialog) {
                            showStartHibitsProcess();
                        }
                    }
                });
                //行为习惯流程未结束，退出当前流程
            }
        } else if (event.getEvent() == RobotEvent.Event.LOW_BATTERY_LESS_FIVE_PERCENT) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onBlutheDisconnected();
                    if (!isShowLowBarry) {
                        showLowBatteryDialog();
                    }
                }
            });

        }
    }

    //显示行为提醒弹出框
    public void showStartHibitsProcess() {
        isShowHibitsDialog = true;
        String msg = ResourceManager.getInstance(this).getStringResources("ui_habits_process_starting");
        String position = ResourceManager.getInstance(this).getStringResources("ui_common_ok");
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


    @OnClick({R.id.ll_base_back, R.id.iv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                finish();
                break;
            case R.id.iv_title_right:
                DynamicActionModel model = null;
                for(int i = 0;i < mDynamicActionModels.size(); i++){
                    if("1".equals(mDynamicActionModels.get(i).getActionStatus())){
                        model = mDynamicActionModels.get(i);
                        break;
                    }
                }
                if(model == null){
                    ToastUtils.showShort("您还没有选择动作");
                    return;
                }
                UbtLog.d(TAG,"select model = " + model);
                Intent mIntent = new Intent();
                mIntent.putExtra(Constant.DYNAMIC_ACTION_MODEL,model);
                setResult(Constant.ACTION_SELECT_RESPONSE_CODE, mIntent);
                finish();
                break;
        }
    }
}
