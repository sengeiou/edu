package com.ubt.alpha1e.mvp;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubt.alpha1e.AlphaApplication;

import java.lang.reflect.ParameterizedType;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public abstract class MVPBaseFragment<V extends BaseView, T extends BasePresenterImpl<V>> extends Fragment implements BaseView {
    public T mPresenter;
    protected View mRootView;
    private Unbinder mUnbinder;
    public Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = getInstance(this, 1);
        mPresenter.attachView((V) this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getContentViewId(), container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);//绑定framgent
        initUI();
        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    protected abstract void initUI();

    protected abstract void initControlListener();

    public abstract int getContentViewId();

    @Override
    public void onResume() {

        // 如果系统语言和本地语言不一致，重启App
        ((MVPBaseActivity) this.getActivity()).doCheckLanguage();
        super.onResume();
    }

    protected abstract void initBoardCastListener();

    public boolean isBulueToothConnected() {

        if (((AlphaApplication) this.getActivity().getApplicationContext())
                .getCurrentBluetooth() == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getStringRes(String str) {
        return ((MVPBaseActivity) this.getActivity()).getStringResources(str);
    }

    /**
     * 动态获取图片
     *
     * @param string 图片Key
     * @return
     */
    public Drawable getDrawableRes(String string) {
        return ((MVPBaseActivity) this.getActivity()).getDrawableRes(string);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detachView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public Context getContext() {
        //Android4.4 没有getContext方法，换成getActivity 11.13 lihai
        //return super.getContext();
        return super.getActivity();
    }

    public <T> T getInstance(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
