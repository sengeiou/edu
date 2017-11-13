package com.ubt.alpha1e.userinfo.helpfeedback.feelback;

import android.content.Context;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;

import org.json.JSONObject;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class FeelbackPresenter extends BasePresenterImpl<FeelbackContract.View> implements FeelbackContract.Presenter{

    @Override
    public void doFeedBack(String content, String email, String phone) {
        String verLocal = "";
        try {
            verLocal = mView.getContext().getPackageManager().getPackageInfo(
                    mView.getContext().getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        String robot_hard_version = ((AlphaApplication) mView.getContext().getApplicationContext()).getRobotHardVersion();
        String robot_soft_version = ((AlphaApplication) mView.getContext().getApplicationContext()).getRobotSoftVersion();

        JSONObject jobj = new JSONObject();
        try {
            jobj.put("appVersion", verLocal);
            jobj.put("robotHardVersion", robot_hard_version);
            jobj.put("robotSoftVersion", robot_soft_version);
            //jobj.put("feedbackUser", account);
            jobj.put("systemType", "android");
            jobj.put("feedbackInfo", content);

        } catch (Exception e) {
            mView.onFeedbackFinish(false,mView.getContext().getResources().getString(
                            R.string.ui_about_feedback_fail));
        }

        /*GetDataFromWeb.getJsonByPost(do_feedback,
                HttpAddress.getRequestUrl(Request_type.do_feed_back),
                HttpAddress.getParamsForPost(jobj.toString(), mBaseActivity),
                this);*/
    }
}
