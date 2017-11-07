package com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate;


import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;


/**
 * @author: dicy.cheng
 * @description:  连接状态
 * @create: 2017/11/6
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class BluetoothandnetconnectstateActivity extends MVPBaseActivity<BluetoothandnetconnectstateContract.View, BluetoothandnetconnectstatePresenter> implements BluetoothandnetconnectstateContract.View {

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.bluetooth_and_net_connect_state;

    }
}
