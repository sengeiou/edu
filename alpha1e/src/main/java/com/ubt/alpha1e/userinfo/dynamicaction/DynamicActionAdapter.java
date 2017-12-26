package com.ubt.alpha1e.userinfo.dynamicaction;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.TimeTools;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/3 14:05
 * @modifier：ubt
 * @modify_date：2017/11/3 14:05
 * [A brief description]
 * version
 */

public class DynamicActionAdapter extends BaseQuickAdapter<DynamicActionModel, BaseViewHolder> {

    public DynamicActionAdapter(@LayoutRes int layoutResId, @Nullable List<DynamicActionModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DynamicActionModel item) {
        helper.addOnClickListener(R.id.rl_play_action);
        helper.setText(R.id.tv_dynamic_name, item.getActionName());
        helper.setText(R.id.tv_action_time, "时长:" + TimeTools.getMMTime(item.getActionTime()));
        ImageView ivPlay = helper.getView(R.id.iv_play_action);
        TextView tvDownProgress = helper.getView(R.id.tv_play_action);//进度条
        ProgressBar progressBar = helper.getView(R.id.progress_download);

        if (item.getActionStatu() == 0) {//停止状态
            ivPlay.setVisibility(View.VISIBLE);
            tvDownProgress.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            ivPlay.setImageResource(R.drawable.ic_btn_play);
        } else if (item.getActionStatu() == 1) {//播放状态
            ivPlay.setVisibility(View.VISIBLE);
            tvDownProgress.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            ivPlay.setImageResource(R.drawable.ic_btn_stop);
        } else if (item.getActionStatu() == 2) {//下载状态
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
    }
}
