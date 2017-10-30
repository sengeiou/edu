package com.ubt.alpha1e.ui.main;


import android.os.Bundle;
import android.widget.Button;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View {
    @BindView(R.id.button2)
    Button mButton;
    MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainPresenter=new MainPresenter();
        mMainPresenter.attachView(this);
        setContentView(R.layout.activity_main);

    }
    @OnClick(R.id.button2)
    public void switchActivity(){
          mMainPresenter.launchActivity("test");
    }

    @Override
    public void showCartoonAction(String json) {

    }

    @Override
    public void showBluetoothStatus(String status) {

    }

    @Override
    public void showCartoonText(String text) {

    }

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
        return 0;
    }
}
