package com.ubt.alpha1e.ui.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.ubt.alpha1e.R;

public class VisiblePswEditText extends EditText implements TextWatcher,View.OnFocusChangeListener {

	/**
	 * 位于控件内右侧密码是否可见
	 */
	private Drawable mCloseDrawable;
	private Drawable mOpenDrawable;
	private boolean  isPwdVisible = false;
	private Drawable mVisibleIcon;
	private OnFocusChangeListener mOnFocusChangeListener;
	/**
	 * 位于控件内左边的图片
	 */

	public VisiblePswEditText(Context context) {
		super(context);
		init(context);
	}

	public VisiblePswEditText(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.editTextStyle);
		init(context);
	}

	public VisiblePswEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@Override
	public void setOnFocusChangeListener(final OnFocusChangeListener onFocusChangeListener) {
		mOnFocusChangeListener = onFocusChangeListener;
	}

	private void init(Context context) {
		// 设置背景

		mCloseDrawable = getCompoundDrawables()[2];
		mCloseDrawable =  ContextCompat.getDrawable(context,R.drawable.register_password_hide);
		mCloseDrawable.setBounds(0, 0,
				mCloseDrawable.getIntrinsicHeight(), mCloseDrawable.getIntrinsicHeight());
		
		mOpenDrawable = getCompoundDrawables()[2];
		mOpenDrawable =  ContextCompat.getDrawable(context,R.drawable.register_password_show);
		mOpenDrawable.setBounds(0, 0,
				mOpenDrawable.getIntrinsicHeight(), mOpenDrawable.getIntrinsicHeight());
		
		setPwdDrawable(false);
		setIconVisible(false);
		setInputType(InputType.TYPE_CLASS_TEXT);
		this.setTransformationMethod(PasswordTransformationMethod.getInstance());
	}

	/**
	 * 设置密码是否可见图标
	 */
	private void setPwdDrawable(boolean visiable) {
		isPwdVisible = visiable;
		Drawable right = visiable ? mOpenDrawable : mCloseDrawable;
		mVisibleIcon = right;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	private void setIconVisible(boolean visible)
	{
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], visible ? mVisibleIcon : null, getCompoundDrawables()[3]);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {
				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));

				if (touchable) {
					if (isPwdVisible) {
						setPwdDrawable(false);
						this.setTransformationMethod(PasswordTransformationMethod.getInstance());//设置密码不可见
					} else {
						setPwdDrawable(true);
						this.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//设置密码可见
					}
				}
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        if (isFocused()) {
            setIconVisible(s.length() > 0);
        }
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
            setIconVisible(getText().length()>0);
        } else {
            setIconVisible(false);
        }
		if (mOnFocusChangeListener != null) {
			mOnFocusChangeListener.onFocusChange(v, hasFocus);
		}
	}
}
