package com.ubt.factorytest.test.recycleview;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.factorytest.R;

import java.util.List;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/16 18:23
 * @描述:
 */

public class TestItemClickAdapter extends BaseMultiItemQuickAdapter<TestClickEntity, BaseViewHolder>{

    private BaseViewHolder mViewHolder;

    public TestItemClickAdapter(List<TestClickEntity> data) {
        super(data);
        addItemType(TestClickEntity.CLICK_ITEM_VIEW, R.layout.item_click_view);
    }

    @Override
    protected void convert(BaseViewHolder helper, TestClickEntity item) {
        mViewHolder  = helper;
//        Log.d(TAG,"convert ");
        switch (helper.getItemViewType()) {
            case TestClickEntity.CLICK_ITEM_VIEW:
                helper.addOnClickListener(R.id.btn_ok)
                .setImageResource(R.id.imageView,item.getImgID())
                .setText(R.id.tv_item, item.getTestItem())
                .setText(R.id.tv_result, item.getTestResult())
                .setText(R.id.btn_ok, (item.isPass()?"通过":"失败"))
                .setTextColor(R.id.btn_ok, (item.isPass()? Color.GREEN:Color.RED));
                break;
        }
    }

}
