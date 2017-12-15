package com.ubt.factorytest.ActionPlay;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ubt.factorytest.ActionPlay.recycleview.ActionListAdapter;
import com.ubt.factorytest.R;
import com.ubt.factorytest.bluetooth.recycleview.BTAdapter;
import com.ubt.factorytest.utils.ContextUtils;
import com.ubt.factorytest.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/14 19:15
 * @描述:
 */

public class ActionFragment extends SupportFragment implements ActionContract.View {
    private static final String TAG = "ActionFragment";

    private static final int MSG_DATA_CHANGE = 1;

    @BindView(R.id.pg_bar)
    ProgressBar pgBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_test)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private ActionContract.Presenter mPresenter;
    private Context mContext;
    private ActionListAdapter mAdapter;

    private Handler mHandler;


    public static ActionFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ActionFragment fragment = new ActionFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actionlist, container, false);
        mContext = ContextUtils.getContext();
        unbinder = ButterKnife.bind(this, view);
        initToolbarNav(toolbar);
        initRecyclerView();
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_DATA_CHANGE:
                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        return view;
    }

    @Override
    public void setPresenter(ActionContract.Presenter presenter) {
        mPresenter = presenter;
    }

    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1) {
//                    mPresenter.disconnectBT();
                    pop();
                } else {
                    getActivity().finish();
                }
            }
        });
    }


    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        initActionViewAdapter();
    }

    private void initActionViewAdapter() {
        mAdapter = new ActionListAdapter(R.layout.item_action_list,mPresenter.getActionList());
        mAdapter.openLoadAnimation(BTAdapter.SCALEIN);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.d(TAG,"onItemClick position="+position );
                mPresenter.playAction(mAdapter.getData().get(position).getActionName());
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void notifyDataSetChanged() {
        if(mHandler != null) {
            mHandler.sendEmptyMessage(MSG_DATA_CHANGE);
        }
    }

    @Override
    public void btDisconnected() {
        ToastUtils.showShort("");
    }
}
