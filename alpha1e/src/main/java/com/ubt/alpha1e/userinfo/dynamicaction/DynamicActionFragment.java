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
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.DownloadProgressInfo;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class DynamicActionFragment extends MVPBaseFragment<DynamicActionContract.View, DynamicActionPresenter> implements DynamicActionContract.View, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, DownLoadActionManager.GetRobotActionListener {
    private static String TAG = DynamicActionFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recyclerview_dynamic)
    RecyclerView mRecyclerviewDynamic;
    DynamicActionAdapter mDynamicActionAdapter;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;
    private List<DynamicActionModel> mDynamicActionModels = new ArrayList<>();
    private View emptyView;
    private TextView tvEmpty;
    private TextView tvRetry;
    private LinearLayout llError;
    private ImageView ivStatu;
    private int playingPosition = -1;
    private String mParam1;
    private String mParam2;

    private int currentType = 0;


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
                mPresenter.getDynamicData(0);
            }
        });

        mRefreshLayout.setEnableAutoLoadmore(true);//开启自动加载功能（非必须）
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                mPresenter.getDynamicData(0);

            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                mPresenter.getDynamicData(1);
            }
        });

        //触发自动刷新
        mRefreshLayout.autoRefresh();
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
    }

    /**
     * 刷新数据
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
            if (isBulueToothConnected()) {//蓝牙连接成功则从机器人获取列表动作
                DownLoadActionManager.getInstance(getActivity()).getRobotAction(this);
            } else {
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
                ToastUtils.showShort("播放" + position);
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
        DynamicActionModel dynamicActionModel = mDynamicActionModels.get(position);

        ActionInfo actionInfo = new ActionInfo();
        actionInfo.actionId = dynamicActionModel.getActionId();
        actionInfo.actionName = dynamicActionModel.getActionName();
        actionInfo.actionPath = "https://services.ubtrobot.com/action/16/3/蚂蚁与鸽子.zip";
        boolean isDownloading = DownLoadActionManager.getInstance(getActivity()).isRobotDownloading(dynamicActionModel.getActionId());
        if (!isDownloading) {
            int statu = DownLoadActionManager.getInstance(getActivity()).dealAction(dynamicActionModel.isDownload(), actionInfo);
            if (statu == DownLoadActionManager.STATU_DOWNLOADING) {//正在下载
                mDynamicActionModels.get(position).setActionStatu(2);
            } else if (statu == DownLoadActionManager.STATU_PLAYING) {//正在播放
                mDynamicActionModels.get(position).setActionStatu(1);
            }
            mDynamicActionAdapter.notifyItemChanged(position);
        }


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

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        startActivity(new Intent(getActivity(), ActionDetailActivity.class));
    }

    /**
     * 结束刷新事件
     */
    private void finishRefresh() {
        if (currentType == 0) {
            mRefreshLayout.finishRefresh();
            mRefreshLayout.resetNoMoreData();
        } else if (currentType == 1) {
            if (mDynamicActionAdapter.getItemCount() > 20) {
                mRefreshLayout.finishLoadmoreWithNoMoreData();//将不会再次触发加载更多事件
            } else {
                mRefreshLayout.finishLoadmore();
            }
        }
        if (null == mDynamicActionModels || mDynamicActionModels.size() == 0) {
            showStatuLayout(1);
        }
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadmore(true);

        mDynamicActionAdapter.notifyDataSetChanged();
    }

    /**
     * 获取机器人动作列表
     *
     * @param list
     */
    @Override
    public void getRobotActionLists(List<String> list) {
        Message message = new Message();
        message.what = 1111;
        message.obj = list;
        mHandler.sendMessage(message);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<String> list = (List<String>) msg.obj;
            if (msg.what == 1111) {
                for (int i = 0; i < mDynamicActionModels.size(); i++) {
                    for (String str : list) {
                        if (mDynamicActionModels.get(i).getActionName().equals(str)) {
                            mDynamicActionModels.get(i).setDownload(true);
                            break;
                        }
                    }
                }
                finishRefresh();
            } else if (msg.what == 1112) {//动作播放结束
                String actionName = (String) msg.obj;
                for (int i = 0; i < mDynamicActionModels.size(); i++) {
                    if (mDynamicActionModels.get(i).getActionName().equals(actionName)) {
                        mDynamicActionModels.get(i).setActionStatu(0);
                        break;
                    }
                }
                mDynamicActionAdapter.notifyDataSetChanged();
            }else if (msg.what==1113){
                DownloadProgressInfo downloadProgressInfo = (DownloadProgressInfo) msg.obj;
                long actionId = downloadProgressInfo.actionId;
                int position = mPresenter.getPositionById(actionId,mDynamicActionModels);
                if (downloadProgressInfo.status==1){

                }


            }
        }
    };


    /**
     * 下载进度
     *
     * @param info
     * @param progressInfo
     */
    @Override
    public void getDownLoadProgress(ActionInfo info, DownloadProgressInfo progressInfo) {
        Message message = new Message();
        message.what = 1113;
        message.obj = progressInfo;
        mHandler.sendMessage(message);
    }

    @Override
    public void playActionFinish(String actionName) {
        Message message = new Message();
        message.what = 1112;
        message.obj = actionName;
        mHandler.sendMessage(message);
    }



}
