package com.ubt.alpha1e.userinfo.dynamicaction;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;

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
        ImageView ivPlay = helper.getView(R.id.iv_play_action);
        TextView downProgress = helper.getView(R.id.tv_play_action);//进度条
        if (item.getActionStatu() == 0) {//停止状态
            ivPlay.setVisibility(View.VISIBLE);
            downProgress.setVisibility(View.GONE);
            ivPlay.setImageResource(R.drawable.ic_btn_play);
        } else if (item.getActionStatu() == 1) {//播放状态
            ivPlay.setVisibility(View.VISIBLE);
            downProgress.setVisibility(View.GONE);
            ivPlay.setImageResource(R.drawable.ic_btn_stop);
        } else if (item.getActionStatu() == 2) {//下载状态
            ivPlay.setVisibility(View.GONE);
            downProgress.setVisibility(View.VISIBLE);
            downProgress.setText((int) item.getDownloadProgress() + " % ");
        }
    }
}
