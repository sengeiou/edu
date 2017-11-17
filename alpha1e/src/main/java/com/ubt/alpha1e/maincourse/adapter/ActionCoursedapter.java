package com.ubt.alpha1e.maincourse.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.maincourse.model.ActionCourseModel;

import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/13 18:35
 * @modifier：ubt
 * @modify_date：2017/11/13 18:35
 * [A brief description]
 * version
 */

public class ActionCoursedapter extends BaseQuickAdapter<ActionCourseModel, BaseViewHolder> {

    public ActionCoursedapter(@LayoutRes int layoutResId, @Nullable List<ActionCourseModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ActionCourseModel item) {
        ImageView ivCourse = helper.getView(R.id.iv_cources);
        ((ImageView) helper.getView(R.id.iv_cources)).setAlpha(item.getActionLockType() == 0 ? 1.0f : 0.5f);
        ((ImageView) helper.getView(R.id.iv_cources)).setImageResource(item.getDrawableId());
        TextView tvActionName = helper.getView(R.id.tv_action_cources_name);
        helper.setText(R.id.tv_action_cources_name, item.getActionCourcesName());
        tvActionName.setTextColor(item.getActionLockType() == 0 ? mContext.getResources().getColorStateList(R.color.tv_black_color) : mContext.getResources().getColorStateList(R.color.login_line_color));
        ImageView ivLock = helper.getView(R.id.iv_action_lock);
        ivLock.setVisibility(item.getActionLockType() == 0 ? View.GONE : View.VISIBLE);
    }
}
