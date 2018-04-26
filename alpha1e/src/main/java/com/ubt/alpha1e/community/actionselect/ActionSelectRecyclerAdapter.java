package com.ubt.alpha1e.community.actionselect;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.GlideRoundTransform;
import com.ubt.alpha1e.data.TimeTools;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.List;

/**
 * @author：liuhai
 * @date：2018/04/23 14:05
 * @modifier：ubt
 * @modify_date：2018/04/23 14:05
 * [A brief description]
 * version
 */

public class ActionSelectRecyclerAdapter extends BaseQuickAdapter<DynamicActionModel, BaseViewHolder> {

    public ActionSelectRecyclerAdapter(@LayoutRes int layoutResId, @Nullable List<DynamicActionModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DynamicActionModel item) {
        helper.addOnClickListener(R.id.iv_play_status);
        helper.addOnClickListener(R.id.iv_select);

        helper.setText(R.id.tv_dynamic_name, item.getActionName());
        helper.setText(R.id.tv_action_time, "时长:" + TimeTools.getMMTime(item.getActionTime()*1000));
        ImageView ivPlay = helper.getView(R.id.iv_play_action);
        TextView tvDownProgress = helper.getView(R.id.tv_play_action);//进度条
        ProgressBar progressBar = helper.getView(R.id.progress_download);
        ImageView ivHead = helper.getView(R.id.iv_action_background);
        ImageView ivPlayStatus = helper.getView(R.id.iv_play_status);

        Glide.with(mContext).load(item.getActionHeadUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).
                placeholder(R.drawable.action_sport_1b)
                .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext,10))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(ivHead);

        if (item.getActionStatu() == 0) {//停止状态
            ivPlay.setVisibility(View.VISIBLE);
            tvDownProgress.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            ivPlay.setImageResource(R.drawable.ic_btn_play);

            ivPlayStatus.setImageResource(R.drawable.ic_community_play_s);
        } else if (item.getActionStatu() == 1) {//播放状态
            ivPlay.setVisibility(View.VISIBLE);
            tvDownProgress.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            ivPlay.setImageResource(R.drawable.ic_btn_stop);
            ivPlayStatus.setImageResource(R.drawable.ic_community_stop_s);
        } else if (item.getActionStatu() == 2) {//下载状态
            ivPlayStatus.setImageResource(R.drawable.ic_downloading);
            ivPlay.setVisibility(View.GONE);
            int progress = (int) item.getDownloadProgress();
            if (progress > 0) {
                tvDownProgress.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress((int) item.getDownloadProgress());
            } else {
                tvDownProgress.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                tvDownProgress.setText("等待中");
            }
            UbtLog.d("DownloadAction", "convert  progress======" + item.getDownloadProgress());
        }

        ImageView ivSelect = helper.getView(R.id.iv_select);
        if("1".equals(item.getActionStatus())){
            ivSelect.setBackgroundResource(R.drawable.icon_habits_choose_in);
        }else {
            ivSelect.setBackgroundResource(R.drawable.icon_habits_choose_out);
        }
    }
}
