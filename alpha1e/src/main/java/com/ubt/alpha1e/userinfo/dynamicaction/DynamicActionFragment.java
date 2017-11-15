package com.ubt.alpha1e.userinfo.dynamicaction;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class DynamicActionFragment extends MVPBaseFragment<DynamicActionContract.View, DynamicActionPresenter> implements DynamicActionContract.View, BaseQuickAdapter.OnItemChildClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recyclerview_dynamic)
    RecyclerView mRecyclerviewDynamic;
    DynamicActionAdapter mDynamicActionAdapter;
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void initUI() {
        mDynamicActionAdapter = new DynamicActionAdapter(R.layout.layout_dynamic_action_item, mDynamicActionModels);
        mDynamicActionAdapter.setOnItemChildClickListener(this);
        mRecyclerviewDynamic.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerviewDynamic.setAdapter(mDynamicActionAdapter);
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_empty, null);
        ((TextView) emptyView.findViewById(R.id.tv_no_data)).setText(getActivity().getResources().getString(R.string.empty_no_dynamiaction));
        ((ImageView)emptyView.findViewById(R.id.iv_no_data)).setImageResource(R.drawable.ic_setting_action_deafult);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getDynamicData(1);

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
            case R.id.iv_delete_action:
                ToastUtils.showShort("删除" + position);
                break;

            case R.id.iv_play_action:
                ToastUtils.showShort("播放" + position);
                DynamicActionModel dynamicActionModel = mDynamicActionAdapter.getItem(position);
                for (DynamicActionModel actionModel : mDynamicActionModels) {
                    if (actionModel.getActionId() == dynamicActionModel.getActionId()) {
                        if (actionModel.getActionStatu() == 1) {
                            actionModel.setActionStatu(0);
                        } else {
                            actionModel.setActionStatu(1);
                        }
                    } else {
                        actionModel.setActionStatu(0);
                    }
                }
                mDynamicActionAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}
