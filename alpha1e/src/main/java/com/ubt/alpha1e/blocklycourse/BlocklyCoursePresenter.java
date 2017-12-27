package com.ubt.alpha1e.blocklycourse;

import com.ubt.alpha1e.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class BlocklyCoursePresenter extends BasePresenterImpl<BlocklyCourseContract.View> implements BlocklyCourseContract.Presenter{

    @Override
    public void getData() {
        //从后台获取课程数据
    }

    @Override
    public void updateCourseData() {

    }
}
