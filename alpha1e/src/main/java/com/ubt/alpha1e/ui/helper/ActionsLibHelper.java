package com.ubt.alpha1e.ui.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.ActionsCollocationManager;
import com.ubt.alpha1e.business.ActionsDownLoadManager;
import com.ubt.alpha1e.business.thrid_party.MyWeiXin;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.FileTools.State;
import com.ubt.alpha1e.data.IFileListener;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.TimeTools;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.BannerInfo;
import com.ubt.alpha1e.data.model.CommentInfo;
import com.ubt.alpha1e.data.model.UserInfo;

import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.utils.ResourceUtils;
import com.ubt.alpha1e.utils.cache.ImageCache;
import com.ubt.alpha1e.utils.connect.ActionInfoCallback;
import com.ubt.alpha1e.utils.connect.ActionInfoListCallback;
import com.ubt.alpha1e.utils.connect.BannerInfoListCallback;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.utils.log.MyLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ActionsLibHelper extends BaseHelper implements IJsonListener,
		IFileListener {

	private static final String TAG = "ActionsLibHelper";

	public  enum Action_type {
		Story_type, Dance_type, Base_type, All_type, Top_type, Spring_Type
	}

	/**
	 Show = 0,            //爱秀 自嗨
	 Dance = 1,           //舞蹈
	 Story = 2,           //故事
	 Sport = 3,           //运动
	 Rhymes = 4,          //儿歌
	 Science = 5,         //科普 教育
	 * */
	public enum actionSonType
	{
		Show,Dance,Sport,Story,Rhymes,Science
	}
	/**
	 ActionsSequenceLatest = 1,//1最新
	 ActionsSequenceHot = 2,//2:最热
	 ActionsSequenceFunny = 3,//3:好玩
	 * */
	public enum actionSortType
	{
		Latest,
		Hotest,
		Funnyest
	}

	public  enum Action_download_state {
		not_download, downing, download_finish
	}

	public  enum Start_type {
		message_type, nomal_type
	}

	public int[] imgTypes = {R.drawable.actions_item_music,
			                 R.drawable.actions_item_sport,
			                 R.drawable.actions_item_story,
							 R.drawable.actions_item_science,
							 R.drawable.actions_item_child,
							 R.drawable.actions_item_unkown};

	public static ActionsLibHelper instance = null;
	public static final String Start_type_key = "Start_type_key";
	public static final String Action_type_key = "Action_type_key";
	public static final String Action_title_key = "Action_title_key";
	public static final String Action_key = "Action_key";
	public static final String Action_message_key = "Action_message_key";
	// -------------------------------
	public static final String map_val_action = "map_val_action";
	public static final String map_val_action_logo_res = "map_val_action_logo_res";
	public static final String map_val_action_name = "map_val_action_name";
	public static final String map_val_action_type_logo_res = "map_val_action_type_logo_res";
	public static final String map_val_action_type_logo_res_des = "map_val_action_type_logo_res_des";
	public static final String map_val_action_time = "map_val_action_time";
	public static final String map_val_action_disc = "map_val_action_disc";
	public static final String map_val_action_download_state = "map_val_action_download_state";
	public static final String map_val_action_download_progress = "map_val_action_download_progress";
	public static final String map_val_action_browse_time = "map_val_action_browse_time";
	public static final String map_val_action_resource_type = "map_val_action_resource_type";
	// -------------------------------
	public static final String map_val_comment_user_head = "map_val_comment_user_head";
	public static final String map_val_comment_user_name = "map_val_comment_user_name";
	public static final String map_val_comment_user_index = "map_val_comment_user_index";
	public static final String map_val_comment_user_comment = "map_val_comment_user_comment";
	public static final String map_val_comment_user_comment_time = "map_val_comment_user_comment_time";
	// -------------------------------
	private static final int MSG_DO_GET_ACTIONS_SIMPLE = 1001;
	private static final int MSG_DO_GET_ACTIONS_BY_TYPE = 1002;
	private static final int MSG_DO_GET_ACTION_COMMENTS = 1003;
	private static final int MSG_DO_ACTION_COMMENT = 1004;
	private static final int MSG_DO_ACTION_PRAISE = 1005;
	private static final int MSG_DO_SHARE_WEIXIN = 1006;
	private static final int MSG_DO_POPULAR_ACTIONS_SIMPLE = 1007;
	private static final int MSG_DO_THEME_RECOMMEND_SIMPLE = 1008;
	private static final int MSG_DO_ORIGINAL_LIST_SIMPLE = 1009;
	private static final int MSG_DO_THEME_RECOMMEND_DETAIL = 1010;
	// -------------------------------
	private int do_get_new_actions_simple = 10001;
	private long do_get_actions_by_type = 10002;
	private long do_get_action_comments = 10003;
	private long do_comment_action = 10004;
	private long do_praise_action = 10005;
	private long do_get_banas = 10006;
	private long do_get_share_url = 10007;
	private int do_get_action_detail = 10008;
	private int do_get_popular_actions_simple = 10009;
	private int do_get_theme_recommend_simple = 10010;
	private int do_get_original_list_simple = 10011;
	private int do_get_theme_recommend_detail = 10012;
	// -------------------------------
	private IActionsLibUI mUI;
	private ActionsDownLoadManager mDownLoadManager;
	private ActionsCollocationManager mCollcationManager;
	private List<String> mBanasName = null;
	private List<Bitmap> mBanasImgList = null;

	public List<String> mBannerRecommend = null;
	public List<String> mBannerUrl = new ArrayList<>();


	public int bannerLength = -1;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (msg.what == MSG_DO_GET_ACTIONS_SIMPLE
						|| msg.what == MSG_DO_GET_ACTIONS_BY_TYPE
						|| msg.what == MSG_DO_THEME_RECOMMEND_DETAIL ) {

					mUI.onReadActionsFinish(true, "", (List<ActionInfo>) msg.obj);

				}else if(msg.what == MSG_DO_POPULAR_ACTIONS_SIMPLE){
					mUI.onReadPopularActionsFinish(true, "", (List<ActionInfo>) msg.obj);

				}else if(msg.what == MSG_DO_ORIGINAL_LIST_SIMPLE){
					mUI.onReadOriginalListActionsFinish(true, "", (List<ActionInfo>) msg.obj);

				}else if(msg.what == MSG_DO_THEME_RECOMMEND_SIMPLE){
					mUI.onReadThemeRecommondFinish(true, "", (List<BannerInfo>) msg.obj);

				} else if (msg.what == MSG_DO_GET_ACTION_COMMENTS) {
					// MyLog.writeLog("�յ������б��ص�UI");
					mUI.onReadActionCommentsFinish((List<CommentInfo>) msg.obj);
				} else if (msg.what == MSG_DO_ACTION_COMMENT) {
					// MyLog.writeLog("�������۵���������ص�UI");
					mUI.onActionCommentFinish((Boolean) msg.obj);
				} else if (msg.what == MSG_DO_ACTION_PRAISE) {
					mUI.onActionPraisetFinish((Boolean) msg.obj);
				} else if (msg.what == MSG_DO_SHARE_WEIXIN) {
					mUI.onWeiXinShareFinish((Integer) msg.obj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public ActionsLibHelper(IActionsLibUI _ui, BaseActivity _baseActivity) {
		super(_baseActivity);
		this.mUI = _ui;
		mDownLoadManager = ActionsDownLoadManager.getInstance(mBaseActivity);
		mDownLoadManager.addListener(mUI);
		mCollcationManager = ActionsCollocationManager.getInstance(mBaseActivity);
		mCollcationManager.addListener(mUI);
		mBaseActivity.registerReceiver(mWeiXinShareReceiver, new IntentFilter(MyWeiXin.ACTION_WEIXIN_API_CALLBACK));
		UbtLog.d(TAG,"mBaseActivity.isBulueToothConnected() = " + mBaseActivity.isBulueToothConnected());
		if (mBaseActivity.isBulueToothConnected()
				&& ((AlphaApplication)mBaseActivity.getApplicationContext()).isAlpha1E()) {

			mDownLoadManager.addBluetoothListener();
		}
	}

	public static ActionsLibHelper getInstance(IActionsLibUI _ui, BaseActivity _baseActivity)
	{
		if(instance ==null)
			instance = new ActionsLibHelper(_ui,_baseActivity);

		return instance;
	}

	public void registerLisenters()
	{
		if(mDownLoadManager!=null)
			mDownLoadManager.addListener(mUI);
		if(mCollcationManager!=null)
			mCollcationManager.addListener(mUI);

	}

	public void removeListeners()
	{
		if(mDownLoadManager!=null)
			mDownLoadManager.removeListener(mUI);
		if(mCollcationManager!=null)
			mCollcationManager.removeListener(mUI);
	}

	private void requestBannerListData(String url, String params, int id){

		OkHttpClientUtils
				.getJsonByPostRequest(url,params,id)
				.execute(new BannerInfoListCallback(){

					@Override
					public void onError(Call call, Exception e, int id) {
						mUI.onReadThemeRecommondFinish(false,"ui_common_network_request_failed",new ArrayList<BannerInfo>());
					}

					@Override
					public void onResponse(List<BannerInfo> response, int id) {
						UbtLog.d("requestBannerListData","response = " + response.size() + "		id = " + id);
						mUI.onReadThemeRecommondFinish(true, "", response);
					}
				});
	}

	public void doGetActionsOnLineNewSimple() {

		UserInfo userInfo  = getCurrentUser();
		String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.get_actions_by_type_new);
		String params = HttpAddress.getParamsForPost(new String[]{"1", 6 + "",1 + "",0 + "",userInfo == null?"": userInfo.countryCode,
								userInfo==null?"":userInfo.userId+"",1+""}, HttpAddress.Request_type.getListByPage,mBaseActivity);

		UbtLog.d("doGetActionsOnLineNewSimple","URL == " + url);
		UbtLog.d("doGetActionsOnLineNewSimple","param == " + params);

		requestActionListDatas(url,params,do_get_new_actions_simple);
	}

	private void requestActionListDatas(String url,String params,int id){

		OkHttpClientUtils
				.getJsonByPostRequest(url,params,id)
				.execute(new ActionInfoListCallback(){

					@Override
					public void onError(Call call, Exception e, int id) {
						mUI.onReadActionsFinish(false,"ui_common_network_request_failed",new ArrayList<ActionInfo>());
					}

					@Override
					public void onResponse(List<ActionInfo> response, int id) {
						UbtLog.d("doGetActionsOnLineNewSimple","response = " + response.size() + "		id = " + id);
						if(id == do_get_new_actions_simple
								|| id == do_get_theme_recommend_detail){

							mUI.onReadActionsFinish(true, "", response);
						}else if(id == do_get_popular_actions_simple){
							mUI.onReadPopularActionsFinish(true, "", response);
						}else if(id == do_get_original_list_simple){
							mUI.onReadOriginalListActionsFinish(true, "", response);
						}
					}
				});
	}

	private void requestActionDatas(String url,String params,int id){

		OkHttpClientUtils
				.getJsonByPostRequest(url,params,id)
				.execute(new ActionInfoCallback(){

					@Override
					public void onError(Call call, Exception e, int id) {
						UbtLog.d("requestActionDatas","response = " + e.getMessage());
					}

					@Override
					public void onResponse(ActionInfo response, int id) {
						//UbtLog.d("requestActionDatas","response = " + response + "		id = " + id);
						mUI.onReadActionInfo(response);
					}
				});
	}

	public void doGetActionsOnLinePopularSimple(int page,int pageSize) {

		String country = "";
		if (getCurrentUser() != null) {
			country = getCurrentUser().countryCode;
		}

		String url = HttpAddress.getRequestUrl(Request_type.get_popular_actions_on_line);
		String params = HttpAddress.getParamsForPost(new String[] { page + "",pageSize + "",country,"0","0" },
				HttpAddress.Request_type.get_popular_actions_on_line,
				mBaseActivity);

		requestActionListDatas(url,params,do_get_popular_actions_simple);
	}

	public void doGetActionsOnLineOriginalListSimple(int pageSize) {

		String country = "";
		if (getCurrentUser() != null) {
			country = getCurrentUser().countryCode;
		}

		String url = HttpAddress.getRequestUrl(Request_type.get_popular_actions_on_line);
		String params = HttpAddress.getParamsForPost(new String[] {"1", pageSize + "",country,"1","0" },
				HttpAddress.Request_type.get_popular_actions_on_line,
				mBaseActivity);

		requestActionListDatas(url,params,do_get_original_list_simple);
	}

	/**
	 * 获取主题
	 * @return
     */
	public void doGetThemeRecommendSimple() {

		String url = HttpAddress.getRequestUrl(Request_type.get_theme_recommend);
		String params = HttpAddress.getBasicParamsForPost(mBaseActivity);

		requestBannerListData(url,params,do_get_theme_recommend_simple);
	}

	/**
	 * 获取主题动作
	 * @param recommendId
	 * @return
     */
	public void doGetThemeRecommendDetail(int recommendId) {

		String url = HttpAddress.getRequestUrl(Request_type.get_theme_recommend_detail);
		String params = HttpAddress.getParamsForPost(new String[] { recommendId + ""},
				HttpAddress.Request_type.get_theme_recommend_detail,
				mBaseActivity);

		requestActionListDatas(url,params,do_get_theme_recommend_detail);
	}

	@Override
	public void onGetJson(boolean isSuccess, String json, long request_code) {
		// TODO Auto-generated method stub

		if (do_get_action_comments == request_code) {
			// MyLog.writeLog("�յ������б�");
			List<CommentInfo> comments = new ArrayList<CommentInfo>();
			if (JsonTools.getJsonStatus(json)) {
				// ����ɹ�
				JSONArray j_list = JsonTools.getJsonModels(json);
				// List<Long> ids = new ArrayList<Long>();
				// List<String> urls = new ArrayList<String>();
				for (int i = 0; i < j_list.length(); i++) {
					try {
						CommentInfo info = new CommentInfo().getThiz(j_list
								.get(i).toString());
						comments.add(info);
						// ids.add(info.comment_id);
						// urls.add(info.user_image);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				// GetImagesFromWeb.getInstance().getImages(ids, urls, this.mUI,
				// 50, 50);
			}
			Message msg = new Message();
			msg.what = MSG_DO_GET_ACTION_COMMENTS;
			msg.obj = comments;
			mHandler.sendMessage(msg);
		}

		else if (do_comment_action == request_code) {

			Message msg = new Message();
			msg.what = MSG_DO_ACTION_COMMENT;
			msg.obj = isSuccess;
			mHandler.sendMessage(msg);
		}

		else if (do_praise_action == request_code) {
			Message msg = new Message();
			msg.what = MSG_DO_ACTION_PRAISE;
			msg.obj = isSuccess;
			mHandler.sendMessage(msg);
		} else if (do_get_banas == request_code) {

			if (JsonTools.getJsonStatus(json)) {
				JSONArray list = JsonTools.getJsonModels(json);
				bannerLength = list.length();
				String name[] = new String[list.length()];
				for (int i = 0; i < list.length(); i++) {
					try {
						MyLog.writeLog("Service_test", list.getJSONObject(i)
								.getString("recommendImage"));
						String url = list.getJSONObject(i).getString(
								"recommendImage");
						String recommendUrl = list.getJSONObject(i).getString(
								"recommendUrl");// ��ȡ��Ӧb
												// {"CN":[{"type":6,"":"value":"�������"}]��"EN":[{"type":6,"":"value":"chongxiangdianfeng"}]}
						String file_name = url
								.substring(url.lastIndexOf("/") + 1);
						mBanasName.add(file_name);
						mBannerUrl.add(url);
						mBannerRecommend.add(recommendUrl);
					} catch (Exception e) {
						name[i] = "";
					}
				}

			}

		} else if (do_get_share_url == request_code) {
			if (JsonTools.getJsonStatus(json)) {
				try {
					mUI.onGetShareUrl(new JSONObject(json).getString("models"));
				} catch (JSONException e) {
					mUI.onGetShareUrl("");
					e.printStackTrace();
				}
			} else {
				mUI.onGetShareUrl("");
			}
		}
	}

	public boolean isLogin(){
		UserInfo info = ((AlphaApplication) mBaseActivity
				.getApplicationContext()).getCurrentUserInfo();
		if (info == null) {
			mUI.onNoteNoUser();
			return false;
		}
		return  true;
	}

	public boolean doDownLoad(ActionInfo actionInfo) {

		UserInfo info = ((AlphaApplication) mBaseActivity
				.getApplicationContext()).getCurrentUserInfo();
		if (info == null) {
			mUI.onNoteNoUser();
			mUI.onDownLoadFileFinish(actionInfo, FileDownloadListener.State.fail);
			return false;
		}

		if(((AlphaApplication)mBaseActivity.getApplicationContext()).isAlpha1E()){

			mDownLoadManager.doDownloadOnRobot(actionInfo,false);
		}else {
			mDownLoadManager.DownLoadAction(actionInfo);
		}
		return true;
	}

	public void doReadDownLoadHistory() {
		// TODO Auto-generated method stub
		mDownLoadManager.getDownHistoryList();
	}

	public void UnRegisterHelper() {
		super.UnRegisterHelper();
	}

	private BroadcastReceiver mWeiXinShareReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {

			SendAuth.Resp resp = MyWeiXin.handleIntent(arg1, mBaseActivity);
			int error_code;
			if (resp != null)
				error_code = resp.errCode;
			else
				error_code = -2;

			Message msg = new Message();
			msg.what = MSG_DO_SHARE_WEIXIN;
			msg.obj = new Integer(error_code);
			mHandler.sendMessage(msg);

		}
	};

	public boolean isDownloading(ActionInfo actionInfo) {

		return mDownLoadManager.isDownloading(actionInfo.actionId);
	}

	@Override
	public void onSendData(String mac, byte[] datas, int nLen) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectState(boolean bsucceed, String mac) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReadImageFinish(Bitmap img, long request_code) {
		mUI.onReadImgFromCache(img, request_code - 100000);
	}

	@Override
	public void DistoryHelper() {
		Log.i("yuyong", this.getClass().getName() + "-->DistoryHelper");
		mDownLoadManager.removeListener(mUI);
		try {
			mBaseActivity.unregisterReceiver(mWeiXinShareReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mBanasImgList != null) {

			for (int i = 0; i < mBanasImgList.size(); i++) {
				if (mBanasImgList.get(i) != null
						&& !mBanasImgList.get(i).isRecycled()) {
					mBanasImgList.get(i).recycle();
				}
			}

		}
		mBanasImgList = null;
		ImageCache.getInstances().clearCache();
		System.gc();
	}

	@Override
	public void onWriteDataFinish(long requestCode, State state) {
		// TODO Auto-generated method stub

	}

	public void doReadActionDetail(ActionInfo mAction) {
		// TODO Auto-generated method stub
		UserInfo info = ((AlphaApplication) mBaseActivity
				.getApplicationContext()).getCurrentUserInfo();

		JSONObject jobj = new JSONObject();
		try {
			jobj.put("actionId", mAction.actionId);
			if (info != null) {
				jobj.put("userId", info.userId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		String url = HttpAddress.getRequestUrl(Request_type.get_action_detial);
		String params = HttpAddress.getParamsForPost(jobj.toString(), mBaseActivity);

		requestActionDatas(url,params,do_get_action_detail);
	}

	@Override
	public void onReadCacheSize(int size) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClaerCache() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReadFileStrFinish(String erroe_str, String result,
			boolean result_state, long request_code) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWriteFileStrFinish(String erroe_str, boolean result,
			long request_code) {
		// TODO Auto-generated method stub

	}


	public boolean isDownLoadFinish(ActionInfo info,List<ActionRecordInfo> mMyDownLoadHistory) {
		boolean result = false;
		if (mMyDownLoadHistory != null) {
			for (int i = 0; i < mMyDownLoadHistory.size(); i++) {
				if (info.actionId == mMyDownLoadHistory.get(i).action.actionId) {
					result = true;
					break;
				}
			}
		} else {
			result = false;
		}
		return result;
	}

	public List<Map<String,Object>> loadDatas(List<ActionInfo> datas, List<ActionRecordInfo> mMyDownLoadHistory)
	{
		List<Map<String,Object>> mDatas = new ArrayList<>();
		if (datas == null)
			return mDatas;
		for (int i = 0; i < datas.size(); i++) {
			ActionInfo action = datas.get(i);
			if (datas.get(i).actionType == -1)
				continue;
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(ActionsLibHelper.map_val_action, action);
			item.put(ActionsLibHelper.map_val_action_name,
					action.actionName);
			item.put(ActionsLibHelper.map_val_action_type_logo_res,
					ResourceUtils.getActionTypeImage(action.actionSonType,mBaseActivity));
			item.put(ActionsLibHelper.map_val_action_type_logo_res_des,
					ResourceUtils.getActionType(action.actionSonType,mBaseActivity));
			item.put(ActionsLibHelper.map_val_action_resource_type,action.actionResource);
			item.put(ActionsLibHelper.map_val_action_browse_time,
					action.actionBrowseTime);
			item.put(ActionsLibHelper.map_val_action_time, TimeTools
					.getMMTime((int)action.actionTime * 1000));
			String describer = action.actionDesciber;
				item.put(ActionsLibHelper.map_val_action_disc,
						describer);
			item.put(ActionsLibHelper.map_val_action_download_progress,
					new Double(0));
			if (isDownloading(action)) {
				item.put(
						ActionsLibHelper.map_val_action_download_state,
						ActionsLibHelper.Action_download_state.downing);
			} else {
				if (isDownLoadFinish(action,mMyDownLoadHistory)) {
					item.put(
							ActionsLibHelper.map_val_action_download_state,
							ActionsLibHelper.Action_download_state.download_finish);
				} else {
					item.put(
							ActionsLibHelper.map_val_action_download_state,
							ActionsLibHelper.Action_download_state.not_download);
				}
			}
			mDatas.add(item);

		}
     return  mDatas;

	}

	public void doUpdateSchemeId(String schemeId){
		BasicSharedPreferencesOperator.getInstance(mBaseActivity,
				BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
				BasicSharedPreferencesOperator.SCHEME_RECOED,
				schemeId,
				null, -1);
	}

	public String getPreSchemeId(){
		return BasicSharedPreferencesOperator.getInstance(mBaseActivity,
				BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
				.doReadSync(BasicSharedPreferencesOperator.SCHEME_RECOED);
	}

}
