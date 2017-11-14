package com.ubt.alpha1e.Course.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.Course.model.ActionCourseModel;
import com.ubt.alpha1e.R;

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
        ((ImageView) helper.getView(R.id.iv_cources)).setImageResource(item.getDrawableId());
        helper.setText(R.id.tv_iction_cources_name, item.getActionCourcesName());
    }
}
