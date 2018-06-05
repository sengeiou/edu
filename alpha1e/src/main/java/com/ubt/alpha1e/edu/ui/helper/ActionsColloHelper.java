package com.ubt.alpha1e.edu.ui.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.business.ActionsCollocationManager;
import com.ubt.alpha1e.edu.business.ActionsDownLoadManager;
import com.ubt.alpha1e.edu.data.TimeTools;
import com.ubt.alpha1e.edu.data.model.ActionColloInfo;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.net.http.GetImagesFromWeb;
import com.ubt.alpha1e.edu.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.edu.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.edu.ui.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionsColloHelper extends BaseHelper {

	// -------------------------------
	public static final String map_val_action = "map_val_action";
	public static final String map_val_action_logo_res = "map_val_action_logo_res";
	public static final String map_val_action_name = "map_val_action_name";
	public static final String map_val_action_type_logo_res = "map_val_action_type_logo_res";
	public static final String map_val_action_time = "map_val_action_time";
	public static final String map_val_action_disc = "map_val_action_disc";
	public static final String map_val_action_type_logo_res_des = "map_val_action_type_logo_res_des";
	public static final String map_val_action_download_state = "map_val_action_download_state";
	public static final String map_val_action_download_progress = "map_val_action_download_progress";
	public static final String map_val_action_browse_time = "map_val_action_browse_time";
	// -------------------------------
	private IActionsColloUI mUI;
	private ActionsDownLoadManager mDownLoadManager;
	private ActionsCollocationManager mCollcationManager;
	private Activity mActivity;

	// -------------------------------

	public ActionsColloHelper(IActionsColloUI _ui, BaseActivity _baseActivity) {
		super(_baseActivity);
		this.mUI = _ui;
		mActivity = _baseActivity;
		mDownLoadManager = ActionsDownLoadManager.getInstance(mBaseActivity);
		mDownLoadManager.addListener(mUI);
		mCollcationManager = ActionsCollocationManager
				.getInstance(mBaseActivity);
		mCollcationManager.addListener(mUI);

	}

	public boolean doDownLoad(ActionInfo actionInfo) {

		UserInfo info = ((AlphaApplication) mBaseActivity
				.getApplicationContext()).getCurrentUserInfo();
		if (info == null) {
			mUI.onNoteNoUser();
			mUI.onDownLoadFileFinish(actionInfo, FileDownloadListener.State.fail);
			return false;
		}

		if (mDownLoadManager.isTooMore()) {
			mUI.onNoteTooMore();
			return false;
		}

		mDownLoadManager.DownLoadAction(actionInfo);

		return true;
	}

	public void doReadDownLoadHistory() {
		// TODO Auto-generated method stub
		mDownLoadManager.getDownHistoryList();
	}

	public BaseWebRunnable doReadCollcationRecord() {
		return mCollcationManager.getCollcationRecord();
	}

	public void doCollocatWeb(ActionInfo mAction) {
		UserInfo info = ((AlphaApplication) mBaseActivity
				.getApplicationContext()).getCurrentUserInfo();
		if (info == null) {
			mUI.onNoteNoUser();
			return;
		}
		mCollcationManager.addToWebRecord(mAction.actionId);

	}

	public void UnRegisterHelper() {
		super.UnRegisterHelper();
	}

	public boolean isDownloading(long action_id) {

		return mDownLoadManager.isDownloading(action_id);
	}

	@Override
	public void onSendData(String mac, byte[] datas, int nLen) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectState(boolean bsucceed, String mac) {
		// TODO Auto-generated method stub

	}

	public void doGetImages(List<Long> ids, List<String> urls, float h, float w) {
		GetImagesFromWeb.getInstance().getImages(ids, urls, this.mUI, h, w, -1);
	}

	@Override
	public void DistoryHelper() {
		Log.i("yuyong", this.getClass().getName() + "-->DistoryHelper");
		mDownLoadManager.removeListener(mUI);
		mCollcationManager.removeListener(mUI);
	}

	public void doDelCollocation(ActionColloInfo mInfo) {
		// TODO Auto-generated method stub
		mCollcationManager.removeWebRecord(mInfo.collectRelationId);
	}

	@Override
	public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
		// TODO Auto-generated method stub

	}

	public  List<Map<String,Object>> loadDatas(List<ActionColloInfo> mActions)
	{

		List<Map<String, Object>> mDatas = new ArrayList<>();
		int[] type_logos = new int[]{R.drawable.gamepad_actions_basic_s_icon,
				R.drawable.sec_dance_icon_s, R.drawable.sec_story_icon_s};

		String[] type_names = new String[]{
				((BaseActivity)mActivity).getStringResources("ui_action_type_basic"),
				((BaseActivity)mActivity).getStringResources("ui_action_type_dance"),
				((BaseActivity)mActivity).getStringResources("ui_action_type_story")};
		if (mActions == null)
			return mDatas;

		for (int i = 0; i < mActions.size(); i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(ActionsColloHelper.map_val_action, mActions.get(i));

			item.put(ActionsColloHelper.map_val_action_logo_res,
					R.drawable.sec_action_logo);

			item.put(ActionsColloHelper.map_val_action_name,
					mActions.get(i).collectName);
			String[] str = mActions.get(i).extendInfo.split("#");
			String browseTime = str[str.length - 1];
			item.put(ActionsColloHelper.map_val_action_browse_time, browseTime);

			try {
				int type_num = Integer.parseInt(mActions.get(i).extendInfo
						.split("#")[0]);
				item.put(ActionsColloHelper.map_val_action_type_logo_res,
						type_logos[type_num - 1]);
				item.put(ActionsHelper.map_val_action_type_name,
						type_names[type_num - 1]);

			} catch (Exception e) {
				item.put(ActionsColloHelper.map_val_action_type_logo_res,
						type_logos[0]);
			}

			try {
				int time = Integer.parseInt(mActions.get(i).extendInfo
						.split("#")[2]);
				item.put(ActionsColloHelper.map_val_action_time,
						TimeTools.getMMTime(time * 1000));
			} catch (Exception e) {
				item.put(ActionsColloHelper.map_val_action_time,
						TimeTools.getMMTime(0));
			}

			if (mActions.get(i).collectDescriber != null
					&& !mActions.get(i).collectDescriber.equals("")) {
				item.put(ActionsColloHelper.map_val_action_disc,
						mActions.get(i).collectDescriber);
			} else {
				item.put(
						ActionsColloHelper.map_val_action_disc,
						((BaseActivity)mActivity).getStringResources("ui_action_no_description"));
			}

			item.put(ActionsColloHelper.map_val_action_download_progress,
					new Double(0));

			mDatas.add(item);
		}

//		for (int i = 0; i < mDatas.size(); i++) {
//
//			ActionColloInfo action = (ActionColloInfo) mDatas.get(i)
//					.get(ActionsColloHelper.map_val_action);
//
//			if (isDownloading(action.collectRelationId)) {
//				mDatas.get(i).put(
//						ActionsColloHelper.map_val_action_download_state,
//						ActionsHelper.Action_download_state.downing);
//			} else {
//				if (isDownLoadFinish(mDownLoadHistory,action.collectRelationId)) {
//					mDatas.get(i).put(
//							ActionsColloHelper.map_val_action_download_state,
//							ActionsHelper.Action_download_state.download_finish);
//				} else {
//					mDatas.get(i).put(
//							ActionsColloHelper.map_val_action_download_state,
//							ActionsHelper.Action_download_state.not_download);
//				}
//			}
//		}
            return mDatas;
	}

	private boolean isDownLoadFinish(List<ActionRecordInfo> mDownLoadHistory,long action_id) {
			for(ActionRecordInfo info:mDownLoadHistory)
			{
				if(action_id==info.action.actionId)
				{
					return  true;
				}
			}
			return false;
			}
}
