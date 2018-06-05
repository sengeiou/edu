package com.ubt.alpha1e.edu.ui.Introduction;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class IntroductionViewPager {
	private FragmentActivity mContext;
	private ViewPager mPager;
	private IntroductionFragmentPagerAdapter mAdapter;
	private IntroductionPageChangeListener mListener;
	private Class mTheNextActivity;
	private SetterFactory mSetterFactory = null;

	public IntroductionViewPager(int _pager_source, FragmentActivity _context,
			SetterFactory _setter_factory) {

		mContext = _context;
		mPager = (ViewPager) mContext.findViewById(_pager_source);
		mSetterFactory = _setter_factory;
		mAdapter = new IntroductionFragmentPagerAdapter(
				mContext.getSupportFragmentManager(), mSetterFactory);
		mPager.setAdapter(mAdapter);
		mListener = new IntroductionPageChangeListener(mPager);
		mPager.setOnPageChangeListener(mListener);
	}

	public void setNextActivity(Class activity) {
		mTheNextActivity = activity;
		mListener.setTheNext(mSetterFactory.getLenth() - 1, mContext,
				mTheNextActivity);
	}

}
