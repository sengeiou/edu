package com.ubt.alpha1e_edu.maincourse.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.maincourse.model.CourseModel;

import java.util.List;


/**
 * @author：liuhai
 * @date：2017/11/13 18:35
 * @modifier：ubt
 * @modify_date：2017/11/13 18:35
 * [A brief description]
 * version
 */

public class MainCoursedapter extends BaseQuickAdapter<CourseModel, BaseViewHolder> {

    public MainCoursedapter(@LayoutRes int layoutResId, @Nullable List<CourseModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CourseModel item) {
        ((ImageView) helper.getView(R.id.iv_cources)).setImageResource(item.getDrawableId());
        TextView tvName = helper.getView(com.ubt.alpha1e_edu.R.id.tv_cources_name);
        tvName.setTextColor(item.getLockType() == 0 ? mContext.getResources().getColorStateList(R.color.tv_black_color) : mContext.getResources().getColorStateList(R.color.login_line_color));
        helper.setText(R.id.tv_cources_name, item.getMainCourcesName());
        ImageView ivScore = helper.getView(R.id.iv_complete);
        ImageView ivLock = helper.getView(R.id.iv_lock);
        ivLock.setVisibility(item.getLockType() == 0 ? View.GONE : View.VISIBLE);
        View ivBackground = helper.getView(R.id.view_background);
        ivBackground.setVisibility(item.getLockType() == 0 ? View.GONE : View.VISIBLE);

        ((ImageView) helper.getView(R.id.iv_cources)).setAlpha(item.getLockType() == 0 ? 1.0f : 0.5f);
    }
}
