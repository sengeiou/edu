package com.ubt.alpha1e.action;

import com.ubt.alpha1e.base.BasePresenter;
import com.ubt.alpha1e.base.BaseView;

/**
 * @author：liuhai
 * @date：2017/10/25 20:57
 * @modifier：ubt
 * @modify_date：2017/10/25 20:57
 * [A brief description]
 * version
 */

public interface ActionContact {

    interface ActionView extends BaseView {
        void showSuccessData(String str);

        void showFailedData(String str);
    }

    interface ActionPresenter extends BasePresenter<ActionView> {
        void getData();
    }
}
