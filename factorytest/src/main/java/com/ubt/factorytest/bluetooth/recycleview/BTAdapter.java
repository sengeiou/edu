package com.ubt.factorytest.bluetooth.recycleview;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.factorytest.R;

import java.util.List;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/21 11:38
 * @描述:
 */

public class BTAdapter extends BaseMultiItemQuickAdapter<BTBean, BaseViewHolder> {

    public BTAdapter(List<BTBean> data) {
        super(data);
        addItemType(BTBean.BT_ITEM_VIEW, R.layout.item_bt_view);
    }

    @Override
    protected void convert(BaseViewHolder helper, BTBean item) {
        switch (helper.getItemViewType()) {
            case BTBean.BT_ITEM_VIEW:
                helper.setText(R.id.tv_bt_name, item.getBtName())
                        .setText(R.id.tv_mac, item.getBtMac())
                        .setText(R.id.tv_rssi, item.getBtRSSI());
                break;
        }
    }

}
