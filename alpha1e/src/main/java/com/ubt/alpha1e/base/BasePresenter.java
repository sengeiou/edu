package com.ubt.alpha1e.base;

/**
 * @author：liuhai
 * @date：2017/10/25 20:20
 * @modifier：ubt
 * @modify_date：2017/10/25 20:20
 * [A brief description]
 * version
 */

public interface BasePresenter<V extends BaseView> {

    /**
     * @param view 绑定
     */
    void attachView(V view);


    /**
     * 防止内存的泄漏,清楚presenter与activity之间的绑定
     */
    void detachView();


    /**
     * 当前MView是否为空
     * @return
     */
    boolean isAttchView();

}
