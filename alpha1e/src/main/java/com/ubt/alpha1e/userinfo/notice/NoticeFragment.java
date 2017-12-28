package com.ubt.alpha1e.userinfo.notice;


import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.loading.LoadingDialog;
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

public class NoticeFragment extends MVPBaseFragment<NoticeContract.View, NoticePresenter> implements NoticeContract.View, BaseQuickAdapter.OnItemLongClickListener, BaseQuickAdapter.OnItemClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recyclerview_notice)
    RecyclerView mRecyclerviewNotice;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;
    private TextView tvEmpty;
    private TextView tvRetry;
    private LinearLayout llError;
    private ImageView ivStatu;
    private NoticeAdapter mNoticeAdapter;
    /**
     * 消息数据列表
     */
    private List<NoticeModel> mNoticeModels = new ArrayList<>();

    private View emptyView;

    private String mParam1;
    private String mParam2;

    private int page = 0;
    private int offset = 8;
    private int currentType;
    private boolean isNoneFinishLoadMore;

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
        mNoticeAdapter.setOnItemClickListener(this);
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_empty, null);
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_empty, null);
        tvEmpty = (TextView) emptyView.findViewById(R.id.tv_no_data);
        tvRetry = emptyView.findViewById(R.id.tv_retry);
        llError = emptyView.findViewById(R.id.ll_error_layout);
        ivStatu = emptyView.findViewById(R.id.iv_no_data);
        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 0;
                mPresenter.getNoticeData(0, page, offset);
                showLoading();
            }
        });
        mRefreshLayout.setEnableAutoLoadmore(true);//开启自动加载功能（非必须）
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                page = 0;
                mPresenter.getNoticeData(0, page, offset);
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                ++page;
                mPresenter.getNoticeData(1, page, offset);
            }
        });

        //触发自动刷新
        mRefreshLayout.autoRefresh();
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
                    mPresenter.deleteNotice(mNoticeModels.get(position).getId());
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

    @Override
    public void showLoading() {
        LoadingDialog.show(getActivity());
    }

    @Override
    public void dissLoding() {
        LoadingDialog.dismiss(getActivity());
    }

    /**
     * 获取消息列表数据
     *
     * @param isSuccess
     * @param type
     * @param list
     */
    @Override
    public void setNoticeData(boolean isSuccess, int type, List<NoticeModel> list) {
        if (isSuccess) {
            mRefreshLayout.setEnableRefresh(true);
            mRefreshLayout.setEnableLoadmore(true);
            if (type == 0) {//下拉刷新
                mNoticeModels.clear();
                mNoticeModels.addAll(list);
                mRefreshLayout.finishRefresh();
                if (list.size() < 8) {
                    mRefreshLayout.setLoadmoreFinished(true);//将不会再次触发加载更多事件
                } else {
                    mRefreshLayout.resetNoMoreData();
                }
            } else if (type == 1) {//上拉加载
                mNoticeModels.addAll(list);
                if (list.size() < 8) {
                    mRefreshLayout.finishLoadmoreWithNoMoreData();//将不会再次触发加载更多事件
                } else {
                    mRefreshLayout.finishLoadmore();
                }
            }

            if (null == mNoticeModels || mNoticeModels.size() == 0) {//数据为空
                showStatuLayout(1);
            }
        } else {
            if (mNoticeModels.size() == 0) {//如果请求失败切列表数据为0，则显示错误页面
                showStatuLayout(2);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadmore();
                mRefreshLayout.setEnableRefresh(false);
                mRefreshLayout.setEnableLoadmore(false);
            }
        }
        dissLoding();
    }

    /**
     * 更新消息状态
     *
     * @param isSuccess
     * @param noticeId
     */
    @Override
    public void updateStatu(boolean isSuccess, int noticeId) {
        if (isSuccess) {
            for (int i = 0; i < mNoticeModels.size(); i++) {
                if (mNoticeModels.get(i).getId() == noticeId) {
                    mNoticeModels.get(i).setStatus("1");
                    break;
                }
            }
            mNoticeAdapter.notifyDataSetChanged();
            if (null != mCallBackListener) {
                mCallBackListener.onChangeUnReadMessage();
                UbtLog.d("Notice", "updateStatu==" + isSuccess);
            }
        }
    }

    /**
     * 删除消息返回结果
     *
     * @param isSuccess
     * @param noticeId
     */
    @Override
    public void deleteNotice(boolean isSuccess, int noticeId) {
        dissLoding();
        if (isSuccess) {
            for (int i = 0; i < mNoticeModels.size(); i++) {
                if (mNoticeModels.get(i).getId() == noticeId) {
                    if (mNoticeModels.get(i).getStatus().equals("0")) {
                        if (null != mCallBackListener) {
                            mCallBackListener.onChangeUnReadMessage();
                            UbtLog.d("Notice", "updateStatu==" + isSuccess);
                        }
                    }
                    mNoticeModels.remove(i);
                    break;
                }

            }
            mNoticeAdapter.notifyDataSetChanged();
            if (mNoticeModels.size() == 0) {
                showStatuLayout(1);
            }

        }
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
            ivStatu.setImageResource(R.drawable.ic_setting_action_deafult);
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
            tvEmpty.setText(emptyMsg);
        } else if (status == 2) {
            tvRetry.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            tvRetry.getPaint().setAntiAlias(true);//抗锯齿
            tvEmpty.setVisibility(View.GONE);
            llError.setVisibility(View.VISIBLE);
            ivStatu.setImageResource(R.drawable.ic_loading_failed);
            mRefreshLayout.setEnableRefresh(false);
            mRefreshLayout.setEnableLoadmore(false);
        }
        mNoticeAdapter.setEmptyView(emptyView);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        mPresenter.updateNoticeStatu(mNoticeModels.get(position).getId());
    }

    CallBackListener mCallBackListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallBackListener = (CallBackListener) activity;
    }

    //设置用于修改文本的回调接口
    public static interface CallBackListener {
        public void onChangeUnReadMessage();
    }
}
