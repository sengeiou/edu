package com.ubt.alpha1e.edu.ui;

import java.util.Map;

import android.view.View;

public interface IGestureUI {
	public void onScrollLeft(View v);

	public void onScrollRight(View v);

	public void onClick(Map<String, Object> item);

	public void onClick(int index);
}
