package com.ubt.alpha1e.userinfo.dynamicaction;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.loading.LoadingDialog;
import com.ubt.alpha1e.data.model.DownloadProgressInfo;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ubt.alpha1e.userinfo.dynamicaction.ActionDetailActivity.dynamicModel;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class DynamicActionFragment extends MVPBaseFragment<DynamicActionContract.View, DynamicActionPresenter> implements DynamicActionContract.View, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, DownLoadActionManager.DownLoadActionListener {
    private static String TAG = DynamicActionFragment.class.getSimpleName();

    /**
     * 获取机器人动作列表
     */
    private static int HANDLE_GET_ROBOTACTIONLIST = 1111;
    /**
     * 动作播放结束
     */
    private static int HANDLE_PLAY_ACTION_FINISH = 1112;
    /**
     * 动作下载进度
     */
    private static int HANDLE_DOWNLOAD_PROGRESS = 1113;
    /**
     * 蓝牙掉线
     */
    private static int HANDLE_ON_DISCONNECT = 1114;
    /**
     * 获取机器人动作列表超时
     */
    private static int HANDLE_GET_ROBOTACTIONLIST_TIMEOUT = 1115;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recyclerview_dynamic)
    RecyclerView mRecyclerviewDynamic;
    DynamicActionAdapter mDynamicActionAdapter;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;
    private List<DynamicActionModel> mDynamicActionModels = new ArrayList<>();//原创列表
    private List<String> robotActionList = new ArrayList<>();//机器人下载列表

    private View emptyView;
    private TextView tvEmpty;
    private TextView tvRetry;
    private LinearLayout llError;
    private ImageView ivStatu;
    private int playingPosition = -1;
    private String mParam1;
    private String mParam2;

    private int currentType = 0;//上拉下拉类型
    private boolean isNoneFinishLoadMore = false;//是否可以上拉加载 true不能上拉 false 可以上拉

    private int page = 0;
    private int offset = 8;

    public DynamicActionFragment() {
    }


    public static DynamicActionFragment newInstance(String param1, String param2) {
        DynamicActionFragment fragment = new DynamicActionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DownLoadActionManager.getInstance(getActivity()).addDownLoadActionListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void initUI() {
        mDynamicActionAdapter = new DynamicActionAdapter(R.layout.layout_dynamic_action_item, mDynamicActionModels);
        mDynamicActionAdapter.setOnItemChildClickListener(this);
        mDynamicActionAdapter.setOnItemClickListener(this);
        mDynamicActionAdapter.setHasStableIds(true);
        mRecyclerviewDynamic.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerviewDynamic.setAdapter(mDynamicActionAdapter);
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_empty, null);
        tvEmpty = (TextView) emptyView.findViewById(R.id.tv_no_data);
        tvRetry = emptyView.findViewById(R.id.tv_retry);
        llError = emptyView.findViewById(R.id.ll_error_layout);
        ivStatu = emptyView.findViewById(R.id.iv_no_data);
        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 0;
                mPresenter.getDynamicData(0, page, offset);
                UbtLog.d("tvRetry", "重试一次");
                LoadingDialog.show(getActivity());

            }
        });
        //触发自动刷新
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setEnableAutoLoadmore(true);//开启自动加载功能（非必须）
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                page = 0;
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
        return R.layout.fragment_dynamic_action;
    }


    /**
     * 刷新获取数据
     *
     * @param statu 是否刷新成功
     * @param type
     * @param list
     */
    @Override
    public void setDynamicData(boolean statu, int type, List<DynamicActionModel> list) {
        currentType = type;
        if (statu) {
            refreshData(type, list);
        } else {//请求失败
            if (mDynamicActionModels.size() == 0) {//如果请求失败切列表数据为0，则显示错误页面
                showStatuLayout(2);
                ToastUtils.showShort("加载失败");
            }
        }
        LoadingDialog.dismiss(getActivity());
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
            if (list.size() < 5) {
                isNoneFinishLoadMore = true;
            } else {
                isNoneFinishLoadMore = false;
            }
            if (isBulueToothConnected() && type == 0) {//蓝牙连接成功则从机器人获取列表动作
                DownLoadActionManager.getInstance(getActivity()).getRobotAction();
                mHandler.sendEmptyMessageDelayed(HANDLE_GET_ROBOTACTIONLIST_TIMEOUT, 3000);
            } else {
                mPresenter.praseGetRobotData(getActivity(), robotActionList, mDynamicActionModels);
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
            tvEmpty.setText(ResourceManager.getInstance(getActivity()).getStringResources("empty_no_dynamiaction"));
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
        mDynamicActionAdapter.setEmptyView(emptyView);
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
            case R.id.rl_play_action:
                playAction(position);
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
            ToastUtils.showShort("请连接蓝牙");
            return;
        }
        mPresenter.playAction(getActivity(), position, mDynamicActionModels);
        mDynamicActionAdapter.notifyDataSetChanged();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
        // ActionDetailActivity.launch(getActivity(), mDynamicActionModels.get(position));
        Intent intent = new Intent(getActivity(), ActionDetailActivity.class);
        intent.putExtra(dynamicModel, mDynamicActionModels.get(position));
        startActivityForResult(intent, 1);
    }

    /**
     * 详情页删除actionId回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UbtLog.d(TAG, "onActivityResult=======================");
        if (requestCode == 1) {
            if (resultCode == ActionDetailActivity.REQUEST_CODE) {
                boolean isDelete = data.getBooleanExtra(ActionDetailActivity.DELETE_RESULT, false);
                int actionId = data.getIntExtra(ActionDetailActivity.DELETE_ACTIONID, 0);
                if (isDelete && actionId != 0) {
                    for (int i = 0; i < mDynamicActionModels.size(); i++) {
                        if (mDynamicActionModels.get(i).getActionId() == actionId) {
                            mDynamicActionModels.remove(i);
                            break;
                        }
                    }
                    mDynamicActionAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 结束刷新事件
     */
    private void finishRefresh() {
        if (currentType == 0) {
            mRefreshLayout.finishRefresh();
            if (isNoneFinishLoadMore) {
                mRefreshLayout.finishLoadmoreWithNoMoreData();//将不会再次触发加载更多事件
            }
            mRefreshLayout.resetNoMoreData();
        } else if (currentType == 1) {
            if (isNoneFinishLoadMore) {
                mRefreshLayout.finishLoadmoreWithNoMoreData();//将不会再次触发加载更多事件
            } else {
                mRefreshLayout.finishLoadmore();
            }
        }
        if (null == mDynamicActionModels || mDynamicActionModels.size() == 0) {//数据为空
            showStatuLayout(1);
        }
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadmore(true);
        mDynamicActionAdapter.notifyDataSetChanged();
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == HANDLE_GET_ROBOTACTIONLIST_TIMEOUT) {
                finishRefresh();
            }
        }
    };


    /**
     * 获取机器人动作列表
     *
     * @param list
     */
    @Override
    public void getRobotActionLists(List<String> list) {
        UbtLog.d(TAG, "获取机器人列表==" + list.toString());
        this.robotActionList = list;
        mHandler.removeMessages(HANDLE_GET_ROBOTACTIONLIST_TIMEOUT);
        mPresenter.praseGetRobotData(getActivity(), list, mDynamicActionModels);
        finishRefresh();
    }

    /**
     * 下载进度
     *
     * @param info
     * @param progressInfo
     */
    @Override
    public void getDownLoadProgress(DynamicActionModel info, DownloadProgressInfo progressInfo) {
        mPresenter.praseDownloadData(getActivity(), progressInfo, mDynamicActionModels);
        mDynamicActionAdapter.notifyDataSetChanged();
    }

    /**
     * 动作播放结束
     *
     * @param actionName
     */
    @Override
    public void playActionFinish(String actionName) {
        for (int i = 0; i < mDynamicActionModels.size(); i++) {
            if (actionName.contains(mDynamicActionModels.get(i).getActionName())) {
                mDynamicActionModels.get(i).setActionStatu(0);
                break;
            }
        }
        mDynamicActionAdapter.notifyDataSetChanged();
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
        mDynamicActionAdapter.notifyDataSetChanged();
    }

    /**
     * 在详情页播放或者暂停，可以同步到列表页面
     *
     * @param actionid
     * @param statu    0结束动作 1播放动作
     */
    @Override
    public void doActionPlay(long actionid, int statu) {
        for (int i = 0; i < mDynamicActionModels.size(); i++) {
            if (mDynamicActionModels.get(i).getActionId() == actionid) {
                UbtLog.d(TAG, "actionName==" + mDynamicActionModels.get(i));
                mDynamicActionModels.get(i).setActionStatu(statu);
                if (statu == 1) {
                    DynamicActionModel model = DownLoadActionManager.getInstance(getActivity()).getPlayingInfo();
                    if (null != model) {
                        int postion = mPresenter.getPositionById(model.getActionId(), mDynamicActionModels);
                        mDynamicActionModels.get(postion).setActionStatu(0);
                    }
                }
                break;
            }

        }
        mDynamicActionAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "--------------onDestory-----------");
        DownLoadActionManager.getInstance(getActivity()).removeDownLoadActionListener(this);
    }
}
