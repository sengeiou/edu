package com.ubt.alpha1e.action.actioncreate;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.ubt.alpha1e.R;

/**
 * @author：liuhai
 * @date：2017/11/15 15:36
 * @modifier：ubt
 * @modify_date：2017/11/15 15:36
 * [A brief description]
 * version
 */

public class ActionEditImpleLayout extends ActionEditLayout {
    public ActionEditImpleLayout(Context context) {
        super(context);
    }

    public ActionEditImpleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionEditImpleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_edit;
    }

    @Override
    public void init(Context context) {
        super.init(context);
    }
}
