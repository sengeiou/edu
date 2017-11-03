package com.ubt.alpha1e.userinfo.notice;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.userinfo.model.NoticeModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class NoticeFragment extends MVPBaseFragment<NoticeContract.View, NoticePresenter> implements NoticeContract.View {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recyclerview_notice)
    RecyclerView mRecyclerviewNotice;

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
        mRecyclerviewNotice.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerviewNotice.setAdapter(mNoticeAdapter);
        emptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty, null);
        ((TextView) emptyView.findViewById(R.id.tv_no_data)).setText(mContext.getResources().getString(R.string.empty_no_noticedata));
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getNoticeData(1);

            }
        });
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
}
