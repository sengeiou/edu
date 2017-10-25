package com.ubt.alpha1e.business;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ActionsCollocationManager implements IJsonListener {

	private static ActionsCollocationManager thiz;
	private Context mContext;
	private int do_read_history_recoeds_from_web = 1001;
	private int do_collocatate_action = 1002;
	private int do_remove_collocatate_action = 1003;
	private List<ActionsCollocationManagerListener> mCollocationListenerLists;

	private ActionsCollocationManager() {
	};

	public static ActionsCollocationManager getInstance(Context _context) {
		if (thiz == null) {
			thiz = new ActionsCollocationManager();
			thiz.mCollocationListenerLists = new ArrayList<ActionsCollocationManagerListener>();
		}
		thiz.mContext = _context.getApplicationContext();
		return thiz;
	}


	// 添加监听者
	public void addListener(ActionsCollocationManagerListener listener) {
		if (!mCollocationListenerLists.contains(listener))
			mCollocationListenerLists.add(listener);
	}

	// 移除监听者
	public void removeListener(ActionsCollocationManagerListener listener) {
		if (mCollocationListenerLists.contains(listener))
			mCollocationListenerLists.remove(listener);
	}

	public BaseWebRunnable getCollcationRecord() {

		// 网络收藏
		if (((AlphaApplication) mContext)
				.getCurrentUserInfo() == null) {
			// 没有登录
			return null;
		}
		String uid = ((AlphaApplication) mContext)
				.getCurrentUserInfo().userId + "";
		return GetDataFromWeb
				.getJsonByPost(
						do_read_history_recoeds_from_web,
						HttpAddress
								.getRequestUrl(HttpAddress.Request_type.do_get_my_collocations),
						HttpAddress
								.getParamsForPost(
										new String[] { uid },
										HttpAddress.Request_type.do_get_my_collocations,
										mContext), this);

	}

	@Override
	public void onGetJson(boolean isSuccess, String json, long request_code) {

		if (request_code == do_read_history_recoeds_from_web) {
			ArrayList<ActionColloInfo> history = new ArrayList<ActionColloInfo>();
			String error_info = "";
			if (!isSuccess) {
				error_info = mContext.getResources().getString(
						R.string.ui_common_network_request_failed);
			}
			try{
				BaseResponseModel<List<ActionColloInfo>> baseResponseModel = GsonImpl.get().toObject(json,
						new TypeToken<BaseResponseModel<List<ActionColloInfo>>>(){}.getType());
				history.addAll(baseResponseModel.models);

			}catch (Exception e)
			{
				e.printStackTrace();
			}

//			if (JsonTools.getJsonStatus(json)) {
//				// 请求成功
//				JSONArray j_list = JsonTools.getJsonModels(json);
//				for (int i = 0; i < j_list.length(); i++) {
//					try {
//						ActionColloInfo info = new ActionColloInfo()
//								.getThiz(j_list.get(i).toString());
//						history.add(info);
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			}
			for (int i = 0; i < mCollocationListenerLists.size(); i++) {
				mCollocationListenerLists.get(i).onReadCollocationRecordFinish(
						isSuccess, error_info, history);
			}
			return;
		}
		if (request_code == do_collocatate_action) {
			// 收藏成功

			if (!isSuccess) {
				for (int i = 0; i < mCollocationListenerLists.size(); i++) {
					mCollocationListenerLists.get(i).onCollocateFinish(
							-1,
							false,
							mContext.getResources().getString(
									R.string.ui_action_collocate_fail_net));
				}
				return;
			}

			if (!JsonTools.getJsonStatus(json)) {

				if (JsonTools.getJsonInfo(json) == 4200) {
					// 如果已经收藏
					for (int i = 0; i < mCollocationListenerLists.size(); i++) {
						mCollocationListenerLists.get(i).onCollocateFinish(
								-1,
								false,
								mContext.getResources().getString(
										R.string.ui_action_collocated));
					}
					return;
				}
				for (int i = 0; i < mCollocationListenerLists.size(); i++) {
					mCollocationListenerLists.get(i).onCollocateFinish(
							-1,
							false,
							mContext.getResources().getString(
									R.string.ui_action_collocate_fail));
				}
				return;

			}
			try {
				int action_id = JsonTools.getJsonModel(json).getInt(
						"collectRelationId");

				for (int i = 0; i < mCollocationListenerLists.size(); i++) {
					mCollocationListenerLists.get(i).onCollocateFinish(
							action_id, true, null);
				}

			} catch (JSONException e) {
				for (int i = 0; i < mCollocationListenerLists.size(); i++) {
					mCollocationListenerLists.get(i).onCollocateFinish(
							-1,
							false,
							mContext.getResources().getString(
									R.string.ui_action_collocate_fail));
				}
				return;
			}

		}

		if (request_code == do_remove_collocatate_action) {
			// 删除成功

			if (!isSuccess) {
				for (int i = 0; i < mCollocationListenerLists.size(); i++) {
					mCollocationListenerLists.get(i).onCollocateRmoveFinish(
							false);
				}
				return;
			}

			if (!JsonTools.getJsonStatus(json)) {

				for (int i = 0; i < mCollocationListenerLists.size(); i++) {
					mCollocationListenerLists.get(i).onCollocateRmoveFinish(
							false);
				}
				return;

			}

			for (int i = 0; i < mCollocationListenerLists.size(); i++) {
				mCollocationListenerLists.get(i).onCollocateRmoveFinish(true);
			}

		}
	}

	public void addToWebRecord(long action_id) {
		String uid = ((AlphaApplication) mContext)
				.getCurrentUserInfo().userId + "";
		GetDataFromWeb.getJsonByPost(do_collocatate_action, HttpAddress
				.getRequestUrl(HttpAddress.Request_type.do_collocatate_action),
				HttpAddress.getParamsForPost(new String[] { action_id + "",
						uid, 1 + "" },
						HttpAddress.Request_type.do_collocatate_action,
						mContext), this);

	}

	public void removeWebRecord(long collo_id) {
		String uid = ((AlphaApplication) mContext)
				.getCurrentUserInfo().userId + "";
		GetDataFromWeb
				.getJsonByPost(
						do_remove_collocatate_action,
						HttpAddress
								.getRequestUrl(HttpAddress.Request_type.do_remove_collocatate_action),
						HttpAddress
								.getParamsForPost(
										new String[] { collo_id + "", uid,
												1 + "" },
										HttpAddress.Request_type.do_remove_collocatate_action,
										mContext), this);
	}

	public void notifyListeners()
	{
		if(mCollocationListenerLists == null)
			return;
		for (int i = 0; i < mCollocationListenerLists.size(); i++) {
			mCollocationListenerLists.get(i).onCollocateRmoveFinish(true);
		}
	}
}
