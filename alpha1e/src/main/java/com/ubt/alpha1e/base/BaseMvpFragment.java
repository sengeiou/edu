package com.ubt.alpha1e.base;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ubt.alpha1e.AlphaApplication;

/**
 * @author：liuhai
 * @date：2017/10/26 15:04
 * @modifier：ubt
 * @modify_date：2017/10/26 15:04
 * [A brief description]
 * version
 */

public abstract class BaseMvpFragment<V extends BaseView, P extends BasePresenterImpl<V>> extends Fragment implements BaseView {
    public P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPresenter();
        mPresenter.attachView((V) this);
    }

    protected abstract P createPresenter();

    protected abstract void initUI();

    protected abstract void initControlListener();

    @Override
    public void onResume() {

        // 如果系统语言和本地语言不一致，重启App
        ((BaseMvpActivity) this.getActivity()).doCheckLanguage();
        super.onResume();
    }

    protected abstract void initBoardCastListener();

    public boolean isBulueToothConnected()
    {

        if (((AlphaApplication)this.getActivity().getApplicationContext())
                .getCurrentBluetooth() == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getStringRes(String str)
    {
        return ((BaseMvpActivity)this.getActivity()).getStringResources(str);
    }

    /**
     * 动态获取图片
     * @param string 图片Key
     * @return
     */
    public Drawable getDrawableRes(String string){
        return ((BaseMvpActivity)this.getActivity()).getDrawableRes(string);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detachView();
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }


}

