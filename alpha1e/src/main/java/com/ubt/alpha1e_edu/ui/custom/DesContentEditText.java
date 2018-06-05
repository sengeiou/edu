package com.ubt.alpha1e_edu.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 用来定位EditText 光标位置
 * Created by Administrator on 2017/3/20.
 */
public class DesContentEditText extends EditText {
    private String mSchemeName = "";

    public DesContentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        // TODO Auto-generated method stub
        if(!TextUtils.isEmpty(mSchemeName) && selStart < mSchemeName.length()){
            setSelection(mSchemeName.length());
            return;
        }
        super.onSelectionChanged(selStart, selEnd);
    }

    public void setSchemeName(String schemeName){
        mSchemeName = schemeName;
    }
}
