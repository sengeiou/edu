package com.ubt.alpha1e.userinfo.notice;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.userinfo.model.NoticeModel;

import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/3 14:05
 * @modifier：ubt
 * @modify_date：2017/11/3 14:05
 * [A brief description]
 * version
 */

public class NoticeAdapter extends BaseQuickAdapter<NoticeModel, BaseViewHolder> {

    public NoticeAdapter(@LayoutRes int layoutResId, @Nullable List<NoticeModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NoticeModel item) {
        helper.setText(R.id.tv_notice_title, item.getNoticeTitle());
        helper.setText(R.id.tv_notice_content, item.getNoticeContent());
    }
}
