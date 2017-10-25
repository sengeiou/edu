package com.ubt.alpha1e.blockly;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class Horizon  extends HorizontalScrollView {
    public Horizon(Context context) {
        super(context);
    }

    public Horizon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//            if (MainActivity.flag == -1) {
//                return super.onInterceptTouchEvent(ev);
//            }else{
        return false;
//            }
    }


}
