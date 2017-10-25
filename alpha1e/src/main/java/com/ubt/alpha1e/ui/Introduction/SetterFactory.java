package com.ubt.alpha1e.ui.Introduction;

public interface SetterFactory {

	IPageSetter getUISetter(int index);

	int getBackgroundRes(int index);

	int getLenth();
}
