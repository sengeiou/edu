package com.ubt.alpha1e.userinfo.dynamicaction;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class DynamicActionFragment extends MVPBaseFragment<DynamicActionContract.View, DynamicActionPresenter> implements DynamicActionContract.View, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {
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

    private int playingPosition = -1;
    private String mParam1;
    private String mParam2;


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
        mPresenter.getDynamicData(0);
        DownLoadActionManager.getInstance(getActivity()).getRobotAction();

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
        ((TextView) emptyView.findViewById(R.id.tv_no_data)).setText(ResourceManager.getInstance(getActivity()).getStringResources("empty_no_dynamiaction"));
        ((ImageView) emptyView.findViewById(R.id.iv_no_data)).setImageResource(R.drawable.ic_setting_action_deafult);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getDynamicData(1);

            }
        });

        mRefreshLayout.setEnableAutoLoadmore(true);//开启自动加载功能（非必须）
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDynamicActionAdapter.setNewData(getData());
                        mRefreshLayout.finishRefresh();
                        refreshlayout.resetNoMoreData();
                    }
                }, 1000);
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDynamicActionAdapter.addData(getData());
                        if (mDynamicActionAdapter.getItemCount() > 20) {
                            ToastUtils.showShort("数据全部加载完毕");
                            mRefreshLayout.finishLoadmoreWithNoMoreData();//将不会再次触发加载更多事件
                        } else {
                            mRefreshLayout.finishLoadmore();
                        }
                    }
                }, 1000);
            }
        });

        //触发自动刷新
        mRefreshLayout.autoRefresh();
    }

    private List<DynamicActionModel> getData() {
        List<DynamicActionModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DynamicActionModel dynamicActionModel = new DynamicActionModel();
            dynamicActionModel.setActionId(14456);
            dynamicActionModel.setActionName("蚂蚁与鸽子");
            dynamicActionModel.setActionTime(3000);
            list.add(dynamicActionModel);
        }
        return list;
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


    @Override
    public void setDynamicData(List<DynamicActionModel> list) {
        if (null != list) {
            mDynamicActionModels.clear();
            mDynamicActionModels.addAll(list);
            mDynamicActionAdapter.notifyDataSetChanged();
        }
        if (null == mDynamicActionModels || mDynamicActionModels.size() == 0) {
            mDynamicActionAdapter.setEmptyView(emptyView);
        }
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


    private void playAction(int position) {
        DynamicActionModel dynamicActionModel = mDynamicActionAdapter.getItem(position);

        ActionInfo actionInfo = new ActionInfo();
        actionInfo.actionId = dynamicActionModel.getActionId();
        actionInfo.actionName = dynamicActionModel.getActionName();
        actionInfo.actionPath = "https://services.ubtrobot.com/action/16/3/蚂蚁与鸽子.zip";
        DownLoadActionManager.getInstance(getActivity()).getRobotAction();
        //DownLoadActionManager.getInstance(getActivity()).dealAction(actionInfo);
//        for (DynamicActionModel actionModel : mDynamicActionModels) {
//            if (actionModel.getActionId() == dynamicActionModel.getActionId()) {
//                if (actionModel.getActionStatu() == 1) {
//                    actionModel.setActionStatu(0);
//                } else {
//                    actionModel.setActionStatu(1);
//                }
//            } else {
//                actionModel.setActionStatu(0);
//            }
//        }
//        mDynamicActionAdapter.notifyDataSetChanged();
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
}
