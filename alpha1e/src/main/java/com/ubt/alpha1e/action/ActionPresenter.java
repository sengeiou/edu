package com.ubt.alpha1e.action;

import android.content.Context;
import android.os.Handler;

import com.ubt.alpha1e.base.BasePresenterImpl;

/**
 * @author：liuhai
 * @date：2017/10/25 20:56
 * @modifier：ubt
 * @modify_date：2017/10/25 20:56
 * [A brief description]
 * version
 */

public class ActionPresenter extends BasePresenterImpl<ActionContact.ActionView> implements ActionContact.ActionPresenter {
    private ActionContact.ActionView mView;
    private Context mContext;

    public ActionPresenter(Context context) {
        mContext = context;
    }


    @Override
    public void getData() {
        new Handler();
        mView.showSuccessData("test成功");
    }
}
