package com.ubt.alpha1e_edu.ui.Introduction;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class IntroductionFragmentPagerAdapter extends FragmentPagerAdapter {

	private SetterFactory mSetterFactory = null;

	public IntroductionFragmentPagerAdapter(FragmentManager fm,
			SetterFactory _setter_factory) {
		super(fm);
		mSetterFactory = _setter_factory;
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment f = new IntroductionFragment(mSetterFactory.getUISetter(arg0));
		Bundle b = new Bundle();
		b.putInt(IntroductionFragment.FRAGMENT_KEY,
				mSetterFactory.getBackgroundRes(arg0));
		f.setArguments(b);
		return f;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mSetterFactory.getLenth();
	}
}
