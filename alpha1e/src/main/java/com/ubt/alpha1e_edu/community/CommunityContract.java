package com.ubt.alpha1e_edu.community;

import android.content.Context;

import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;
import com.ubt.alpha1e_edu.userinfo.model.DynamicActionModel;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CommunityContract {
    interface View extends BaseView {
        void onQiniuTokenFromServer(boolean status, String token);
        void onLoadFileToQiNiu(boolean status, String url);
        void onActionStatus(int actionId, int isDownload, int actionStatus, String downloadPercent);

    }

    interface  Presenter extends BasePresenter<View> {
        void getQiniuTokenFromServer();

        void loadFileToQiNiu(String path);

        void playAction(Context context, DynamicActionModel actionModel);

        void getActionStatus(Context context, DynamicActionModel actionModel,List<String> mRobotDownActionList);
    }
}
