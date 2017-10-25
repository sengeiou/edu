package com.ubt.alpha1e.ui.helper;

import android.graphics.Bitmap;

import com.ubt.alpha1e.business.ActionsCollocationManagerListener;
import com.ubt.alpha1e.business.ActionsDownLoadManagerListener;
import com.ubt.alpha1e.net.http.IGetImagesListener;

public interface IActionsColloUI extends IGetImagesListener,
		ActionsDownLoadManagerListener, ActionsCollocationManagerListener {
	public void onNoteNoUser();

	public void onNoteTooMore();

	public void onReadImgFromCache(Bitmap img, long l);
}
