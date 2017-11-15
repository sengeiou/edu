package com.ubt.alpha1e.action.actioncreate;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author：liuhai
 * @date：2017/11/15 15:30
 * @modifier：ubt
 * @modify_date：2017/11/15 15:30
 * [A brief description]
 * version
 */

public abstract class BaseActionEditLayout extends LinearLayout{




    public BaseActionEditLayout(Context context) {
        super(context);
        init(context);
    }



    public BaseActionEditLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseActionEditLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public abstract int getLayoutId();
    public void init(Context context) {
        View.inflate(context, getLayoutId(), this);
    }

}
