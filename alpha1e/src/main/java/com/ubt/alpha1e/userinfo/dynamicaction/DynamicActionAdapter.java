package com.ubt.alpha1e.userinfo.dynamicaction;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

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
        helper.addOnClickListener(R.id.iv_delete_action);
        helper.addOnClickListener(R.id.iv_play_action);
        helper.setText(R.id.tv_dynamic_name, item.getActionName());
        ImageView ivDelete = helper.getView(R.id.iv_play_action);
        ivDelete.setImageResource(item.getActionStatu() == 0 ? R.drawable.play_playing : R.drawable.play_pause);

    }
}
