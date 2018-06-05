package com.ubt.alpha1e_edu.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

public class HScrollView extends HorizontalScrollView {

	private IHScrollListener mListener;

	public HScrollView(Context context) {
		super(context);
	}

	public HScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public HScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setScrollListener(IHScrollListener _listener) {
		mListener = _listener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		View view = (View) getChildAt(getChildCount() - 1);

		if (view.getLeft() - getScrollX() == 0) {
			if (mListener != null)
				mListener.onLeft();

		} else if ((view.getRight() - (getWidth() + getScrollX())) == 0) {
			if (mListener != null)
				mListener.onRight();

		} else {
			if (mListener != null)
				mListener.onScroll();
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

}
