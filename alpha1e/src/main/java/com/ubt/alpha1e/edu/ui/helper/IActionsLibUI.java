package com.ubt.alpha1e.edu.ui.helper;

import java.util.List;

import android.graphics.Bitmap;

import com.ubt.alpha1e.edu.business.ActionPlayerListener;
import com.ubt.alpha1e.edu.business.ActionsCollocationManagerListener;
import com.ubt.alpha1e.edu.business.ActionsDownLoadManagerListener;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.edu.data.model.BannerInfo;
import com.ubt.alpha1e.edu.data.model.CommentInfo;
import com.ubt.alpha1e.edu.net.http.IGetImagesListener;

public interface IActionsLibUI extends ActionPlayerListener,
		IGetImagesListener, ActionsDownLoadManagerListener,
		ActionsCollocationManagerListener {
	public void onReadActionsFinish(boolean is_success, String error_msg,List<ActionInfo> actions);

	public void onReadActionCommentsFinish(List<CommentInfo> comments);

	public void onActionCommentFinish(boolean is_success);

	public void onActionPraisetFinish(boolean is_success);

	public void onNoteNoUser();

	public void onGetShareUrl(String string);

	public void onWeiXinShareFinish(Integer obj);

	public void onNoteTooMore();

	public void onReadImgFromCache(Bitmap img, long l);

	public void onReadActionInfo(ActionInfo info);

	public void onReadCacheActionsFinish(boolean is_success, List<ActionOnlineInfo> actions);

	public void onReadPopularActionsFinish(boolean is_success, String error_msg,List<ActionInfo> actions);

	public void onReadThemeRecommondFinish(boolean is_success, String error_msg,List<BannerInfo> themes);

	public void onReadOriginalListActionsFinish(boolean is_success, String error_msg,List<ActionInfo> actions);

}
