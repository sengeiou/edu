package com.ubt.alpha1e.mvp;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ubt.alpha1e.AlphaApplication;

import java.lang.reflect.ParameterizedType;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public abstract class  MVPBaseFragment<V extends BaseView,T extends BasePresenterImpl<V>> extends Fragment implements BaseView{
    public T mPresenter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter= getInstance(this,1);
        mPresenter.attachView((V) this);
    }

    protected abstract T createPresenter();

    protected abstract void initUI();

    protected abstract void initControlListener();

    @Override
    public void onResume() {

        // 如果系统语言和本地语言不一致，重启App
        ((MVPBaseActivity) this.getActivity()).doCheckLanguage();
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
        return ((MVPBaseActivity)this.getActivity()).getStringResources(str);
    }

    /**
     * 动态获取图片
     * @param string 图片Key
     * @return
     */
    public Drawable getDrawableRes(String string){
        return ((MVPBaseActivity)this.getActivity()).getDrawableRes(string);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null)
            mPresenter.detachView();
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    public  <T> T getInstance(Object o, int i) {
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
