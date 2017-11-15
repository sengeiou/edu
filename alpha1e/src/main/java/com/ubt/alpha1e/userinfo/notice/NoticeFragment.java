package com.ubt.alpha1e.userinfo.notice;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.userinfo.model.NoticeModel;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class NoticeFragment extends MVPBaseFragment<NoticeContract.View, NoticePresenter> implements NoticeContract.View, BaseQuickAdapter.OnItemLongClickListener {


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
        mRecyclerviewNotice.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerviewNotice.setAdapter(mNoticeAdapter);
        mNoticeAdapter.bindToRecyclerView(mRecyclerviewNotice);
        mNoticeAdapter.setOnItemLongClickListener(this);
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_empty, null);
        ((TextView) emptyView.findViewById(R.id.tv_no_data)).setText(getActivity().getResources().getString(R.string.empty_no_noticedata));
        ((ImageView) emptyView.findViewById(R.id.iv_no_data)).setImageResource(R.drawable.ic_setting_push_deafult);
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

    EasyPopup mCirclePop = null;

    @Override
    public boolean onItemLongClick(final BaseQuickAdapter adapter, final View view, final int position) {
        adapter.getViewByPosition(position, R.id.rl_root).setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.background_delete_coor));
        if (null != mCirclePop) {
            mCirclePop.dismiss();
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

        mCirclePop.showAtAnchorView(view, VerticalGravity.BELOW, HorizontalGravity.ALIGN_RIGHT, -80, 0);
        TextView tvDelete = mCirclePop.getView(R.id.tv_delete);
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoticeModels.remove(position);
                mCirclePop.dismiss();
            }
        });
        return false;
    }
}
