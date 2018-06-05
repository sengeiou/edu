package com.ubt.alpha1e.edu.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.helper.BaseHelper;

/**
   * User: wilson
   * Description: 注册流程抽象类
   * Time: 2016/7/14 15:57
   */

public abstract  class BaseRegisterFragment extends Fragment {

    protected abstract int getLayoutResourceId();

    public abstract void gotoNextStep();

    protected abstract void initViews();

    protected abstract void initListeners();

    protected Activity mActivity;

    protected View mView;

    public BaseHelper mHelper;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHelper = ((BaseActivity)getActivity()).mHelper;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(getLayoutResourceId(),container,false);
        initViews();
        initListeners();
        return mView;
    }
}
