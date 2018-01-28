package com.ubt.alpha1e.action.actioncreate;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * @author：liuhai
 * @date：2017/11/15 15:36
 * @modifier：ubt
 * @modify_date：2017/11/15 15:36
 * [A brief description]
 * version
 */

public class ActionEditsStandard extends BaseActionEditLayout {

    public ActionEditsStandard(Context context) {
        super(context);
    }

    public ActionEditsStandard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionEditsStandard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutId() {
        if(AlphaApplication.isPad()){
            return R.layout.activity_create_action_for_pad;
        }else{
            return R.layout.activity_create_action;
        }

    }

    @Override
    public void init(Context context) {
        super.init(context);
        UbtLog.d("ActionEditsStandard","执行init方法 111");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ((ActionsEditHelper) mHelper).doEnterOrExitActionEdit((byte) 0x03);
                doReset();

            }
        }, 1000);
    }
}
