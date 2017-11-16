package com.ubt.alpha1e.maincourse.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.maincourse.model.CourseModel;
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

public class MainCoursedapter extends BaseQuickAdapter<CourseModel, BaseViewHolder> {

    public MainCoursedapter(@LayoutRes int layoutResId, @Nullable List<CourseModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CourseModel item) {
        ((ImageView) helper.getView(R.id.iv_cources)).setImageResource(item.getDrawableId());
        helper.setText(R.id.tv_cources_name, item.getMainCourcesName());
        ImageView  ivScore = helper.getView(R.id.iv_complete);
     }
}
