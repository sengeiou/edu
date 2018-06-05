package com.ubt.alpha1e.edu.ui.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ubt.alpha1e.edu.R;

 /**
   * User: wilson <br>
   * Description: 自定义密码是否可见editText <br>
   * Time: 2016/7/12 9:24 <br>
   */

public class VisiableEditText extends AppCompatEditText implements View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {

    private Drawable mPassWordIcon;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnTouchListener mOnTouchListener;
    private Drawable openDrawable;//密码可见
    private Drawable closedDrawable;//密码不可见
    private Drawable wrappedOpenDrawable;
    private Drawable wrappedClosedDrawable;


    public VisiableEditText(final Context context) {
        super(context);
        init(context);
    }

    public VisiableEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VisiableEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener onFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(final OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    private void init(final Context context) {
        openDrawable = ContextCompat.getDrawable(context, R.drawable.register_password_show);
        closedDrawable = ContextCompat.getDrawable(context, R.drawable.register_password_hide);
        wrappedOpenDrawable = DrawableCompat.wrap(openDrawable); //Wrap the drawable so that it can be tinted pre Lollipop
        wrappedClosedDrawable = DrawableCompat.wrap(closedDrawable); //Wrap the drawable so that it can be tinted pre Lollipop
        mPassWordIcon = openDrawable;
        mPassWordIcon.setBounds(0, 0, mPassWordIcon.getIntrinsicHeight(), mPassWordIcon.getIntrinsicHeight());
        changePassWordIcon(true);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    @Override
    public void onFocusChange(final View view, final boolean hasFocus) {
//        if (hasFocus) {
//            setIconVisible(getText().length()>0);
//        } else {
//            setIconVisible(false);
//        }
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            boolean touchable = motionEvent.getX() > (getWidth() - getTotalPaddingRight())
                    && (motionEvent.getX() < ((getWidth() - getPaddingRight())));
            if(touchable)
            {
                if (mPassWordIcon == closedDrawable)
                {
                    setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
                    Editable etable = this.getText();
                    Selection.setSelection(etable, etable.length()); // 隐藏
                    changePassWordIcon(true);

                }else
                {
                    this.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Editable etable = this.getText();
                    Selection.setSelection(etable, etable.length()); // 显示
                    changePassWordIcon(false);
                    this.invalidate();
                }

            }

        }

        return super.onTouchEvent(motionEvent);
    }

    @Override
    public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
//        if (isFocused()) {
//            setIconVisible(s.length() > 0);
//        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * 设置密码图标
     */
    private void changePassWordIcon(final boolean visible) {
//        mPassWordIcon.setVisible(visible, false);
        mPassWordIcon = visible ? wrappedOpenDrawable : wrappedClosedDrawable;
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                 mPassWordIcon
                ,compoundDrawables[3]);
    }

     protected void setIconVisible(boolean visible) {
         setCompoundDrawables(getCompoundDrawables()[0],
                 getCompoundDrawables()[1], visible ? mPassWordIcon : null,
                 getCompoundDrawables()[3]);
     }
}
