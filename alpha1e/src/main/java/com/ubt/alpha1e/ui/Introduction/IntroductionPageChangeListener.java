package com.ubt.alpha1e.ui.Introduction;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;

import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator.DataType;

public class IntroductionPageChangeListener extends SimpleOnPageChangeListener {
	private ViewPager mPager;
	private boolean mHasTheNext = false;
	private boolean gotoNext = false;
	private boolean isTurnNext = false;
	private int mTheLastIndex;
	private Context mContext;
	private Class mTheNext;

	public void setTheNext(int theLastIndex, Context context, Class theNext) {
		mHasTheNext = true;
		mTheLastIndex = theLastIndex;
		mContext = context;
		mTheNext = theNext;
	}

	public IntroductionPageChangeListener(ViewPager _pager) {
		mPager = _pager;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		super.onPageScrolled(position, positionOffset, positionOffsetPixels);
		if (!mHasTheNext) {
			if (gotoNext && !isTurnNext && position == mTheLastIndex) {
				isTurnNext = true;
				Intent inte = new Intent();
				inte.setClass(mContext, mTheNext);
				mContext.startActivity(inte);
				((FragmentActivity) mContext).finish();
				BasicSharedPreferencesOperator
						.getInstance(mContext, DataType.USER_USE_RECORD)
						.doWrite(
								BasicSharedPreferencesOperator.IS_FIRST_USE_APP_KEY,
								BasicSharedPreferencesOperator.IS_FIRST_USE_APP_VALUE_TRUE,
								null, -1);
				return;
			}
			if (position == mTheLastIndex) {
				gotoNext = true;
				return;
			} else {
				mHasTheNext = true;
				gotoNext = false;
			}

		}
		if (position == mTheLastIndex - 1 && positionOffset != 0) {
			mHasTheNext = false;
		}
	}

}