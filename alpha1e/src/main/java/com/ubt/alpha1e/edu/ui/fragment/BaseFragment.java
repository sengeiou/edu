package com.ubt.alpha1e.edu.ui.fragment;


import android.app.Fragment;
import android.graphics.drawable.Drawable;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.ui.BaseActivity;

public abstract class BaseFragment extends Fragment {
	protected abstract void initUI();

	protected abstract void initControlListener();

	@Override
	public void onResume() {

		// 如果系统语言和本地语言不一致，重启App
		((BaseActivity) this.getActivity()).doCheckLanguage();
		super.onResume();
	}

	protected abstract void initBoardCastListener();

	public boolean isBulueToothConnected()
	{

		if (((AlphaApplication)this.getActivity().getApplicationContext())
				.getCurrentBluetooth() == null) {
			return false;
		} else {
			return true;
		}
	}

	public String getStringRes(String str)
	{
		return ((BaseActivity)this.getActivity()).getStringResources(str);
	}

	/**
	 * 动态获取图片
	 * @param string 图片Key
	 * @return
	 */
	public Drawable getDrawableRes(String string){
		return ((BaseActivity)this.getActivity()).getDrawableRes(string);
	}

}
