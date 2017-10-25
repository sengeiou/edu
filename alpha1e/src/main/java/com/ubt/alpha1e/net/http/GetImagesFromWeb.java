package com.ubt.alpha1e.net.http;

import android.graphics.Bitmap;

import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.IImageListener;

import java.util.List;

public class GetImagesFromWeb implements IImageListener {

	private static GetImagesFromWeb thiz;
	private IGetImagesListener listener;

	private GetImagesFromWeb() {

	}

	public static GetImagesFromWeb getInstance() {

		if (thiz == null)
			thiz = new GetImagesFromWeb();
		return thiz;
	}

	public void getImages(List<Long> ids, List<String> urls,
			IGetImagesListener _listener, float h, float w, int corners) {
		listener = _listener;
		if (ids == null || urls == null)
			return;
		if (ids.size() == 0 || urls.size() == 0)
			return;
		if (ids.size() != urls.size())
			return;
		for (int i = 0; i < ids.size(); i++) {
			// GetDataFromWeb.getImageFromHttp(urls.get(i), ids.get(i), this, h,
			// w, 2, corners);
			GetDataFromWeb.getImageFromHttp(urls.get(i), ids.get(i), this, h,
					w, -1, corners);
		}
	}

	public void getImages(List<Long> ids, List<String> urls,
			IGetImagesListener _listener, int corners) {
		getImages(ids, urls, _listener, -1, -1, corners);

	}

	@Override
	public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
		// TODO Auto-generated method stub
		MyLog.writeLog("新图片缓存策略", request_code + "获取完毕");
		if (bitmap == null)
			return;
		if (listener != null)
			listener.onNoteDataChaged(bitmap, request_code);
	}

}