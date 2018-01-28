package com.ubt.alpha1e.blocklycourse.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.blocklycourse.model.CourseData;

import java.util.List;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class BlocklyCourseAdapter extends BaseQuickAdapter<CourseData, BaseViewHolder> {

    private Context context;

    public BlocklyCourseAdapter(@LayoutRes int layoutResId, @Nullable List<CourseData> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, CourseData item) {
        ImageView ivCourse = helper.getView(R.id.iv_cources);
        if(item.getCurrGraphProgramId() >= item.getCid()){
            ((ImageView) helper.getView(R.id.iv_cources)).setAlpha(1.0f);
        }else{
            ((ImageView) helper.getView(R.id.iv_cources)).setAlpha(0.5f);
        }
        ImageView ivPlayVideo = helper.getView(R.id.iv_play_video);
        if(item.getCurrGraphProgramId() >= item.getCid()){
            ivPlayVideo.setVisibility(View.VISIBLE);
        }else{
            ivPlayVideo.setVisibility(View.GONE);
        }

        Glide.with(context).load(item.getThumbnailUrl()).placeholder(R.drawable.ic_course).override(360, 255).fitCenter().into(ivCourse);

//        ((ImageView) helper.getView(R.id.iv_cources)).setImageResource(item.getDrawableId());
        TextView tvActionName = helper.getView(R.id.tv_action_cources_name);
        helper.setText(R.id.tv_action_cources_name, item.getName());
        tvActionName.setTextColor(Integer.valueOf(item.getStatus()) ==  1 ? mContext.getResources().getColorStateList(R.color.tv_black_color) : mContext.getResources().getColorStateList(R.color.login_line_color));
        ImageView ivLock = helper.getView(R.id.iv_action_lock);
        ivLock.setVisibility(Integer.valueOf(item.getStatus()) == 1 ? View.GONE : View.VISIBLE);

        ImageView ivStar = helper.getView(R.id.iv_action_complete);
        if(item.getCurrGraphProgramId() > item.getCid()){
            ivStar.setImageResource(R.drawable.img_action_completed);
        }else{
            ivStar.setImageResource(R.drawable.img_action_incomplete);
        }
//        ivStar.setImageResource(item.getActionCourcesScore() == 0 ? R.drawable.img_action_incomplete : R.drawable.img_action_completed);

//        if (TextUtils.isEmpty(item.getName())) {
//            helper.getView(R.id.rl_content).setVisibility(View.GONE);
//            helper.getView(R.id.iv_next).setVisibility(View.VISIBLE);
//        } else {
//            helper.getView(R.id.rl_content).setVisibility(View.VISIBLE);
//            helper.getView(R.id.iv_next).setVisibility(View.GONE);
//        }
    }

}
