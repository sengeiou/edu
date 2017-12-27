package com.ubt.alpha1e.userinfo.notice;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.popup.EasyPopup;
import com.ubt.alpha1e.base.popup.HorizontalGravity;
import com.ubt.alpha1e.base.popup.VerticalGravity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.userinfo.model.NoticeModel;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class NoticeFragment extends MVPBaseFragment<NoticeContract.View, NoticePresenter> implements NoticeContract.View, BaseQuickAdapter.OnItemLongClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recyclerview_notice)
    RecyclerView mRecyclerviewNotice;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;

    private NoticeAdapter mNoticeAdapter;
    /**
     * 消息数据列表
     */
    private List<NoticeModel> mNoticeModels = new ArrayList<>();

    private View emptyView;

    private String mParam1;
    private String mParam2;


    public NoticeFragment() {
    }


    public static NoticeFragment newInstance(String param1, String param2) {
        NoticeFragment fragment = new NoticeFragment();
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
        mPresenter.getNoticeData(0);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    protected void initUI() {
        mNoticeAdapter = new NoticeAdapter(R.layout.layout_notice_item, mNoticeModels);
        mRecyclerviewNotice.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerviewNotice.setAdapter(mNoticeAdapter);
        mNoticeAdapter.bindToRecyclerView(mRecyclerviewNotice);
        mNoticeAdapter.setOnItemLongClickListener(this);
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_empty, null);
        String emptyMsg = "";
        if (mParam1.equals("1")) {
            emptyMsg = "你目前没有任何成就";
        } else if (mParam1.equals("2")) {
            emptyMsg = "你目前没有任何消息";
        } else if (mParam1.equals("3")) {
            emptyMsg = "你目前没有任何动态";
        } else if (mParam1.equals("5")) {
            emptyMsg = "你目前没有任何下载";
        }
        ((TextView) emptyView.findViewById(R.id.tv_no_data)).setText(emptyMsg);

        ((ImageView) emptyView.findViewById(R.id.iv_no_data)).setImageResource(R.drawable.ic_setting_push_deafult);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getNoticeData(1);

            }
        });

        mRefreshLayout.setEnableAutoLoadmore(true);//开启自动加载功能（非必须）
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mNoticeAdapter.setNewData(getData());
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
                        mNoticeAdapter.addData(getData());
                        if (mNoticeAdapter.getItemCount() > 20) {
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

    private List<NoticeModel> getData() {
        List<NoticeModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            NoticeModel noticeModel = new NoticeModel();
            noticeModel.setNoticeTitle("系统消息" + i);
            noticeModel.setNoticeContent("测试数据测试数据测试数据测试数据测试数据");
            list.add(noticeModel);
        }
        return list;
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_user_notice;
    }

    @Override
    protected void initBoardCastListener() {

    }


    /**
     * 设置返回的消息列表
     *
     * @param list
     */
    @Override
    public void setNoticeData(List<NoticeModel> list) {
        if (null != list) {
            mNoticeModels.clear();
            mNoticeModels.addAll(list);
            mNoticeAdapter.notifyDataSetChanged();
        }
        if (null == mNoticeModels || mNoticeModels.size() == 0) {
            mNoticeAdapter.setEmptyView(emptyView);
        }
    }

    EasyPopup mCirclePop = null;
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;


    @Override
    public boolean onItemLongClick(final BaseQuickAdapter adapter, final View view, final int position) {

        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            UbtLog.d("onItemLongClick", "执行长按事件");
            adapter.getViewByPosition(position, R.id.rl_root).setBackgroundColor(getActivity().getResources().getColor(R.color.background_delete_coor));
            UbtLog.d("onItemLongClick", "position==========" + position);
            if (null != mCirclePop) {
                mCirclePop.dismiss();
                UbtLog.d("onItemLongClick", "position====dismiss======" + position);
            } else {
                mCirclePop = new EasyPopup(getActivity())
                        .setContentView(R.layout.dialog_item_delete)
                        .setWidth(420)
                        .setHeight(200)
                        //是否允许点击PopupWindow之外的地方消失
                        .setFocusAndOutsideEnable(true)
                        .createPopup()
                        .setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                mNoticeAdapter.notifyDataSetChanged();
                            }
                        });
            }

            mCirclePop.showAtAnchorView(view, VerticalGravity.CENTER, HorizontalGravity.ALIGN_RIGHT, -80, 0);
            TextView tvDelete = mCirclePop.getView(R.id.tv_delete);
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNoticeModels.remove(position);
                    mCirclePop.dismiss();
                }
            });
        } else {
            UbtLog.d("onItemLongClick", "没有执行长按事件");
        }
        return false;
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
}
