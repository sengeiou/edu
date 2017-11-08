package com.ubt.alpha1e.userinfo.useredit;

import android.app.Activity;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;
import com.ubt.alpha1e.userinfo.model.UserModel;

import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class UserEditContract {
    public interface View extends BaseView {
        void getAgeDataList(List<String> list);

        void takeImageFromShoot();

        void takeImageFromAblum();

        void ageSelectItem(int type, String age);

        void updateUserModelSuccess(UserModel userModel);
        void updateUserModelFailed();
    }

    interface Presenter extends BasePresenter<View> {
        void showImageHeadDialog(Activity activity);

        void showImageCenterHeadDialog(Activity activity);

        void showAgeDialog(Activity activity, int currentPosition);

        void showGradeDialog(Activity activity, int currentPosition, List<String> list);
    }
}
