package com.ubt.alpha1e.maincourse.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
        ((ImageView) helper.getView(R.id.iv_cources)).setAlpha(item.getActionLockType() == 1 ? 1.0f : 0.5f);
 
        ImageView imageView = (ImageView) helper.getView(R.id.iv_cources);
        Glide.with(mContext).load(item.getDrawableId()).placeholder(R.drawable.ic_action_level1).into(imageView);
        TextView tvActionName = helper.getView(R.id.tv_action_cources_name);
        helper.setText(R.id.tv_action_cources_name, item.getActionCourcesName());
        tvActionName.setTextColor(item.getActionLockType() == 1 ? mContext.getResources().getColorStateList(R.color.tv_black_color) : mContext.getResources().getColorStateList(R.color.login_line_color));
        ImageView ivLock = helper.getView(R.id.iv_action_lock);
        ivLock.setVisibility(item.getActionLockType() == 1 ? View.GONE : View.VISIBLE);

        ImageView ivStar = helper.getView(R.id.iv_action_complete);
        ivStar.setImageResource(item.getActionCourcesScore() == 0 ? R.drawable.img_action_incomplete : R.drawable.img_action_completed);

        if (TextUtils.isEmpty(item.getActionCourcesName())) {
            helper.getView(R.id.rl_content).setVisibility(View.GONE);
            helper.getView(R.id.iv_next).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.rl_content).setVisibility(View.VISIBLE);
            helper.getView(R.id.iv_next).setVisibility(View.GONE);
        }
    }
}
