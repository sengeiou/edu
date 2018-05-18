package com.ubt.alpha1e.userinfo.notice;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.TimeTools;
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
        TextView tvTitle = helper.getView(R.id.tv_notice_title);
        String title = "系统消息";
        if (item.getType().equals("1")) {
            title = "系统消息";
            ((ImageView) helper.getView(R.id.iv_right_icon)).setImageResource(R.drawable.ic_setting_system_push);
        } else if (item.getType().equals("2")) {
            title = "活动消息";
            ((ImageView) helper.getView(R.id.iv_right_icon)).setImageResource(R.drawable.ic_notice_activity);
        }else if (item.getType().equals("3")) {
            title = "帖子消息";
            ((ImageView) helper.getView(R.id.iv_right_icon)).setImageResource(R.drawable.ic_notice_newaction);
        }
        tvTitle.setText(title);

        helper.setText(R.id.tv_notice_content, item.getContent());
        helper.getView(R.id.rl_root).setBackgroundColor(mContext.getResources().getColor(R.color.white));
        helper.setText(R.id.tv_notice_time, TimeTools.format(item.getCreateTime()));
        TextView tvBar = helper.getView(R.id.bar_num);
        tvBar.setVisibility(item.getStatus().equals("0") ? View.VISIBLE : View.GONE);
    }
}
