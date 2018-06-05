package com.ubt.alpha1e_edu.ui.helper;

import android.graphics.Bitmap;

import com.ubt.alpha1e_edu.business.ActionsCollocationManagerListener;
import com.ubt.alpha1e_edu.business.ActionsDownLoadManagerListener;
import com.ubt.alpha1e_edu.net.http.IGetImagesListener;

public interface IActionsColloUI extends IGetImagesListener,
		ActionsDownLoadManagerListener, ActionsCollocationManagerListener {
	public void onNoteNoUser();

	public void onNoteTooMore();

	public void onReadImgFromCache(Bitmap img, long l);
}
