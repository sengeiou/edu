package com.ubt.alpha1e.edu.ui.custom;

import android.view.View;
import android.widget.ViewSwitcher;

public interface OnSlideListener {
	public void getNext(ViewSwitcher _switcher, int[] _img_ids);

	public void getPrev(ViewSwitcher _switcher, int[] _img_ids);

	public void onClient(View view);
}
