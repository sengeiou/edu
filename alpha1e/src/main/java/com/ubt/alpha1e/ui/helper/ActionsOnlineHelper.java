package com.ubt.alpha1e.ui.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.business.ActionsCollocationManager;
import com.ubt.alpha1e.business.ActionsDownLoadManager;
import com.ubt.alpha1e.business.NewActionsManager;
import com.ubt.alpha1e.business.thrid_party.MyWeiXin;
import com.ubt.alpha1e.data.DB.ActionsOnlineCacheOperater;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.FileTools.State;
import com.ubt.alpha1e.data.IFileListener;
import com.ubt.alpha1e.data.ImageTools;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.TimeTools;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.CommentInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.library.AutoVideo.visibility.items.VideoItem;
import com.ubt.alpha1e.net.http.GetImagesFromWeb;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.ui.ActionsSquareDetailActivity;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.MyDynamicActivity;
import com.ubt.alpha1e.ui.MyMainActivity;
import com.ubt.alpha1e.ui.fragment.ActionsSquareDetailFragment;
import com.ubt.alpha1e.ui.fragment.IShowFragment;
import com.ubt.alpha1e.ui.fragment.IShowSquareDetailFragment;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.cache.ImageCache;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.utils.log.MyLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ActionsOnlineHelper extends BaseHelper implements IJsonListener,
		IFileListener {

	private static final String TAG = "ActionsOnlineHelper";

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

	public static ActionsOnlineHelper instance = null;
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
	// -------------------------------
	private long do_get_new_actions_simple = 10001;
	private long do_get_actions_by_type = 10002;
	private long do_get_action_comments = 10003;
	private long do_comment_action = 10004;
	private long do_praise_action = 10005;
	private long do_get_banas = 10006;
	private long do_get_share_url = 10007;
	private long do_get_action_detail = 10008;
	// --------------------------------
	public static final int more_info_count = 15;
	public static final int page_size = 6;
	// -------------------------------
	private IActionsLibUI mUI;
	private ActionsDownLoadManager mDownLoadManager;
	private ActionsCollocationManager mCollcationManager;
	private List<String> mBanasName = null;
	private Map<Integer, Bitmap> mBanasImg = null;
	private List<Bitmap> mBanasImgList = null;
	// -------------------------------
	private List<ActionInfo> mActions;
	public  List<ActionRecordInfo> mDownloadRecord;

	public List<String> mBannerRecommend = null;
	public List<String> mBannerUrl = new ArrayList<>();

	public static int actionLocalSonType = 0;
	public static int actionLocalSortType = 0;

	public int bannerLength = -1;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (msg.what == MSG_DO_GET_ACTIONS_SIMPLE
						|| msg.what == MSG_DO_GET_ACTIONS_BY_TYPE) {

					mUI.onReadActionsFinish(true, "",
							(List<ActionInfo>) msg.obj);

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

	public ActionsOnlineHelper(IActionsLibUI _ui, BaseActivity _baseActivity) {
		super(_baseActivity);
		this.mUI = _ui;
		mDownLoadManager = ActionsDownLoadManager.getInstance(mBaseActivity);
		mDownLoadManager.addListener(mUI);
		mCollcationManager = ActionsCollocationManager
				.getInstance(mBaseActivity);
		mCollcationManager.addListener(mUI);
		mBaseActivity.registerReceiver(mWeiXinShareReceiver, new IntentFilter(
				MyWeiXin.ACTION_WEIXIN_API_CALLBACK));
	}

	public static ActionsOnlineHelper getInstance(IActionsLibUI _ui, BaseActivity _baseActivity)
	{
		if(instance ==null)
			instance = new ActionsOnlineHelper(_ui,_baseActivity);

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

	public boolean isWifiCoon() {
		ConnectivityManager connManager = (ConnectivityManager) mBaseActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifi != null
				&& wifi.getState() == NetworkInfo.State.CONNECTED) {
			return true;
		} else {
			return false;
		}
	}

	public BaseWebRunnable doGetActionsOnLineNewSimple() {

		String country = "";
		if (getCurrentUser() != null) {
			country = getCurrentUser().countryCode;
		}

		return GetDataFromWeb
				.getJsonByPost(
						do_get_new_actions_simple,
						HttpAddress
								.getRequestUrl(Request_type.get_actions_on_line),
						HttpAddress.getParamsForPost(new String[] { "5",
								country },
								Request_type.get_actions_on_line,
								mBaseActivity), this);
	}

	public void doCommentAction(long action_id, String comment_content) {

		UserInfo info = ((AlphaApplication) mBaseActivity
				.getApplicationContext()).getCurrentUserInfo();

		if (info == null) {
			mUI.onNoteNoUser();
			return;
		}

		String[] params = new String[] { comment_content, action_id + "",
				(info.userId) + "" };

		// MyLog.writeLog("�������۵�����������");

		GetDataFromWeb.getJsonByPost(do_comment_action, HttpAddress
				.getRequestUrl(Request_type.do_action_comment),
				HttpAddress.getParamsForPost(params,
						Request_type.do_action_comment, info,
						mBaseActivity), this);
	}

	public void doGetActionComments(String id) {
		GetDataFromWeb.getJsonByPost(do_get_action_comments, HttpAddress
				.getRequestUrl(Request_type.get_action_comments),
				HttpAddress.getParamsForPost(new String[] { id },
						Request_type.get_action_comments,
						mBaseActivity), this);
	}

	public BaseWebRunnable doGetActionsByType(Action_type type, int index) {

		if (type == Action_type.All_type) {

			String country = "";
			if (getCurrentUser() != null) {
				country = getCurrentUser().countryCode;
			}

			return GetDataFromWeb
					.getJsonByPost(
							do_get_actions_by_type,
							HttpAddress
									.getRequestUrl(Request_type.get_actions_on_line),
							HttpAddress
									.getParamsForPost(
											new String[] {
													more_info_count + "",
													country },
											Request_type.get_actions_on_line,
											mBaseActivity), this);

		}

		int type_value = -1;
		if (type == Action_type.Base_type)
			type_value = 1;
		else if (type == Action_type.Dance_type)
			type_value = 2;
		else if (type == Action_type.Story_type)
			type_value = 3;
		else if (type == Action_type.Top_type)
			type_value = 5;
		else if (type == Action_type.Spring_Type)
			type_value = 6;

		String country = "";
		if (getCurrentUser() != null) {
			country = getCurrentUser().countryCode;
		}

		return GetDataFromWeb.getJsonByPost(do_get_actions_by_type, HttpAddress
				.getRequestUrl(Request_type.get_actions_by_type),
				HttpAddress.getParamsForPost(new String[] { type_value + "",
						page_size + "", index + "", country },
						Request_type.get_actions_by_type,
						mBaseActivity), this);

	}

	public boolean doPraise(ActionOnlineInfo actionOnlineInfo) {
		UserInfo current_user = ((AlphaApplication) mBaseActivity
				.getApplicationContext()).getCurrentUserInfo();
		if (current_user == null) {
			mUI.onNoteNoUser();
			return false;
		}
		actionOnlineInfo.isPraise = 1;
		actionOnlineInfo.actionPraiseTime++;
		JSONObject jobj = new JSONObject();
		try {
			jobj.put("praiseUserId", current_user.userId);
			jobj.put("praiseObjectId", actionOnlineInfo.actionId);
			jobj.put("praiseType", 1 + "");
			jobj.put("token", current_user.token);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		GetDataFromWeb.getJsonByPost(do_praise_action, HttpAddress
				.getRequestUrl(Request_type.do_action_praise),
				HttpAddress.getParamsForPost(jobj.toString(), mBaseActivity),
				this);

		updateActionOnlineCache(actionOnlineInfo);
		return  true;
	}

	@Override
	public void onGetJson(boolean isSuccess, String json,long request_code) {
		// TODO Auto-generated method stub

		if (do_get_new_actions_simple == request_code
				|| do_get_actions_by_type == request_code) {

			if (!isSuccess) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						mUI.onReadActionsFinish(
								false,
								"ui_common_network_request_failed",
								new ArrayList<ActionInfo>());
					}
				});
				return;
			}

			List<ActionInfo> actions = new ArrayList<ActionInfo>();

			if (JsonTools.getJsonStatus(json)) {
				// ����ɹ�
				JSONArray j_list = JsonTools.getJsonModels(json);
				for (int i = 0; i < j_list.length(); i++) {
					try {
						ActionInfo info = new ActionInfo().getThiz(j_list
								.get(i).toString());
						actions.add(info);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			if (do_get_new_actions_simple == request_code) {
				Message msg = new Message();
				msg.what = MSG_DO_GET_ACTIONS_SIMPLE;
				msg.arg1 = (int)request_code;
				msg.obj = actions;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.what = MSG_DO_GET_ACTIONS_BY_TYPE;
				msg.arg1 = (int)request_code;
				msg.obj = actions;
				mHandler.sendMessage(msg);
			}

		} else if (do_get_action_comments == request_code) {
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
			// MyLog.writeLog("�������۵����������յ�");
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

//		if (mDownLoadManager.isTooMore()) {
//			mUI.onNoteTooMore();
//			return false;
//		}

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

	public void doReadBanaImgName() {
		if (mBanasName == null) {
			mBanasName = new ArrayList<String>();
			mBannerRecommend = new ArrayList<String>();
			GetDataFromWeb.getJsonByPost(do_get_banas, HttpAddress
					.getRequestUrl(Request_type.get_bana_imgs), HttpAddress
					.getParamsForPost(new String[] { "3" },
							Request_type.get_bana_imgs, mBaseActivity), this);
		}
	}

	public Bitmap getBana(int index, int hight, int width) {
		if (mBanasName.size() - 1 < index) {
			return null;
		}
		File img_bana = new File(FileTools.image_cache + "/"
				+ mBanasName.get(index));
		if (mBanasImg == null)
			mBanasImg = new HashMap<Integer, Bitmap>();
		if (mBanasImgList == null) {
			mBanasImgList = new ArrayList<Bitmap>();
		}
		if (mBanasImg.get(index) == null) {

			if (!img_bana.exists()) {
				return null;
			}

			Bitmap img = null;
			try {
				img = ImageTools.compressImage(
						BitmapFactory.decodeFile(img_bana.getPath()), width,
						hight, false);
			} catch (Throwable e) {
				img = null;
			}

			mBanasImg.put(index, img);
			mBanasImgList.add(mBanasImg.get(index));

		}
		return mBanasImg.get(index);
	}

	public void getShareUrl(ActionInfo mAction) {
		GetDataFromWeb.getJsonByPost(do_get_share_url, HttpAddress
				.getRequestUrl(Request_type.get_share_url), HttpAddress
				.getParamsForPost(new String[] { "share", "url" },
						Request_type.get_share_url,
						((AlphaApplication) mBaseActivity
								.getApplicationContext()).getCurrentUserInfo(),
						mBaseActivity), this);
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
	public boolean isDownloading(ActionOnlineInfo actionInfo) {

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

	public void doGetImages(List<Long> ids, List<String> urls, float h,
							float w, int corners) {
		GetImagesFromWeb.getInstance().getImages(ids, urls, this.mUI, h, w,
				corners);
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

	public boolean doCollocatWeb(ActionOnlineInfo actionInfo) {
		UserInfo info = ((AlphaApplication) mBaseActivity
				.getApplicationContext()).getCurrentUserInfo();
		if (info == null) {
			mUI.onNoteNoUser();
			return false;
		}
		actionInfo.isCollect = 1;
		actionInfo.actionCollectTime++;
		mCollcationManager.addToWebRecord(actionInfo.actionId);

		updateActionOnlineCache(actionInfo,actionLocalSonType,actionLocalSortType);
		return  true;


	}

	public boolean  doRemoveCollectWeb(ActionOnlineInfo actionOnlineInfo) {
		UserInfo info = ((AlphaApplication) mBaseActivity
				.getApplicationContext()).getCurrentUserInfo();
		if (info == null) {
			mUI.onNoteNoUser();
			return false;
		}
		actionOnlineInfo.isCollect = 0;
		actionOnlineInfo.actionCollectTime--;
		mCollcationManager.removeWebRecord(actionOnlineInfo.actionId);
 		return  true;
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


	private boolean isDownLoadFinish(ActionOnlineInfo info) {
		boolean result = false;
		if (mDownloadRecord != null) {

			for (int i = 0; i < mDownloadRecord.size(); i++) {
				if (info.actionId == mDownloadRecord.get(i).action.actionId) {
					result = true;
					break;
				}
			}
		} else {
			result = false;
		}
		return result;
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


	public List<Map<String,Object>> loadDatas(List<ActionOnlineInfo> datas,List<ActionRecordInfo> mMyDownLoadHistory,boolean needSave,int localSonType,int localSortType)
	{

		List<Map<String,Object>> mDatas = new ArrayList<>();
		if (datas == null)
			return mDatas;
		for (int i = 0; i < datas.size(); i++) {
			ActionOnlineInfo action = datas.get(i);
			if (datas.get(i).actionType == -1)
				continue;
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(ActionsOnlineHelper.map_val_action, action);
			item.put(ActionsOnlineHelper.map_val_action_name,
					action.actionName);
			item.put(ActionsOnlineHelper.map_val_action_browse_time,
					action.actionBrowseTime);
			item.put(ActionsOnlineHelper.map_val_action_time, TimeTools
					.getMMTime((int)action.actionTime * 1000));
			String describer = action.actionDesciber;
				item.put(ActionsOnlineHelper.map_val_action_disc,
						describer);

			item.put(ActionsOnlineHelper.map_val_action_download_progress,
					new Double(0));
			if (isDownloading(action)) {
				item.put(
						ActionsOnlineHelper.map_val_action_download_state,
						ActionsOnlineHelper.Action_download_state.downing);
			} else {
				if (isDownLoadFinish(action,mMyDownLoadHistory)) {
					item.put(
							ActionsOnlineHelper.map_val_action_download_state,
							ActionsOnlineHelper.Action_download_state.download_finish);
				} else {
					item.put(
							ActionsOnlineHelper.map_val_action_download_state,
							ActionsOnlineHelper.Action_download_state.not_download);
				}
			}
			mDatas.add(item);
			if(needSave){
				UbtLog.d(TAG,"lihai-----------localSonType->"+localSonType+"---localSortType->"+localSortType);
				addActionOnlineCache(action,localSonType,localSortType);
			}
		}
     return  mDatas;

	}
	public List<VideoItem> getVideoListItems(List<Map<String,Object>> mDatas)
	{
		List<VideoItem> list = new ArrayList<>();
		for(Map<String,Object> map:mDatas)
		{
			ActionOnlineInfo actionOnlineInfo =(ActionOnlineInfo)map.get(ActionsOnlineHelper.map_val_action);
			if(actionOnlineInfo.actionVideoPath!=null)
			{
				VideoItem videoListItem = new VideoItem(actionOnlineInfo.actionVideoPath);
				list.add(videoListItem);
			}
		}
		return list;
	}

	public void doUpdateItem(int position)
	{
		UbtLog.d(TAG,"lihai---------doUpdateItem---" + position);
		if(mBaseActivity instanceof ActionsSquareDetailActivity){
			ActionsSquareDetailActivity instance = (ActionsSquareDetailActivity)mBaseActivity;
			ActionsSquareDetailFragment mCurrentFragment = (ActionsSquareDetailFragment)instance.mCurrentFragment;
			mCurrentFragment.updateItemView(position);
		}else if(mBaseActivity instanceof MyMainActivity){
			MyMainActivity instance = (MyMainActivity)mBaseActivity;
			if(instance.mCurrentFragment instanceof IShowSquareDetailFragment){
				IShowSquareDetailFragment mCurrentFragment = (IShowSquareDetailFragment)instance.mCurrentFragment;
				mCurrentFragment.updateItemView(position);
			}
		}

		/*ActionsSquareDetailActivity instance = (ActionsSquareDetailActivity)mBaseActivity;
		ActionsSquareDetailFragment mCurrentFragment = (ActionsSquareDetailFragment)instance.mCurrentFragment;
		mCurrentFragment.updateItemView(position);*/
	}

	public void doShareToOthers(ActionOnlineInfo actionOnlineInfo)
	{
		if(mBaseActivity instanceof ActionsSquareDetailActivity){
			ActionsSquareDetailActivity instance = (ActionsSquareDetailActivity)mBaseActivity;
			if(instance!=null){
				instance.doShareActions(actionOnlineInfo);
			}
		}else if(mBaseActivity instanceof MyMainActivity){
			MyMainActivity instance = (MyMainActivity)mBaseActivity;
			if(instance!=null && instance.mCurrentFragment instanceof IShowSquareDetailFragment){
				((IShowSquareDetailFragment)instance.mCurrentFragment).doShareActions(actionOnlineInfo);
			}
		}else if(mBaseActivity instanceof MyDynamicActivity){
			MyDynamicActivity instance = (MyDynamicActivity)mBaseActivity;
			if(instance!=null ){
				instance.doShareActions(actionOnlineInfo);
			}
		}
	}

	public void addActionOnlineCache(final ActionOnlineInfo onlineInfo,final int localSonType,final int localSortType){

		FileTools.pool.execute(new Runnable() {
			@Override
			public void run() {
				//写入数据库
				ActionsOnlineCacheOperater.getInstance(mBaseActivity, FileTools.db_log_cache, FileTools.db_log_name).addOnlineCache(onlineInfo,localSonType,localSortType);
			}
		});

	}

	public void updateActionOnlineCache(ActionOnlineInfo actionOnlineInfo){
		updateActionOnlineCache(actionOnlineInfo,actionLocalSonType,actionLocalSortType);
	}

	public void updateActionOnlineCache(final ActionOnlineInfo onlineInfo,final int localSonType,final int localSortType){

		FileTools.pool.execute(new Runnable() {
			@Override
			public void run() {
				//更新数据库
				ActionsOnlineCacheOperater.getInstance(mBaseActivity, FileTools.db_log_cache, FileTools.db_log_name).updateOnlineCache(onlineInfo,localSonType,localSortType);
			}
		});

	}

	public void getAllOnlineCache(final int actionSonType,final int actionSortType ){
		FileTools.pool.execute(new Runnable() {
			@Override
			public void run() {
				try{
					//获取本地缓存数据
					List<ActionOnlineInfo> cacheData = ActionsOnlineCacheOperater.getInstance(mBaseActivity, FileTools.db_log_cache, FileTools.db_log_name)
							.getAllOnlineCache(actionSonType,actionSortType);
					UbtLog.d(TAG,"lihai---------getAllOnlineCache-stop>"+cacheData.size());
					if(mUI != null){
						mUI.onReadCacheActionsFinish(true,cacheData);
					}

				}catch (Exception ex){
					ex.printStackTrace();
					if(mUI != null){
						mUI.onReadCacheActionsFinish(false,null);
					}
				}


			}
		});
	}


	public void deleteMyDynamic(final ActionOnlineInfo info){
		final long actionId = info.actionId;

		String url =  HttpAddress.getRequestUrl(HttpAddress.Request_type.deleteMyShow);
		String params = HttpAddress.
				getParamsForPost(new String[]{actionId+""},
						HttpAddress.Request_type.deleteMyShow,mBaseActivity);
		UbtLog.d(TAG, "url & params:" + url + "---" + params);
		UbtLog.d(TAG,"onResponse:params::"+params);
		OkHttpClientUtils
				.getJsonByPostRequest(url,params)
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e,int i) {
						UbtLog.d(TAG,"onResponse:"+e.getMessage());
					}
					@Override
					public void onResponse(String result,int i) {
						//UbtLog.d(TAG,"lihai------requsetDataSize->"+actionOnlineInfo.size());
						UbtLog.d(TAG, "onResponse data:" + result.toString());
						if(JsonTools.getJsonStatus(result)){
							//删除数据
							ActionsOnlineCacheOperater.deleteMyDynamic(actionId);
							if(mBaseActivity instanceof MyDynamicActivity){
								MyDynamicActivity instance = (MyDynamicActivity)mBaseActivity;
								if(instance!=null && instance.mCurrentFragment instanceof IShowFragment){
									((IShowFragment)instance.mCurrentFragment).requestData();

								}
							}

							if(!TextUtils.isEmpty(info.actionName)){
								info.actionStatus = 0;
								ActionInfo actionInfo = info;
								String string = GsonImpl.get().toJson(actionInfo);
								//
								UbtLog.d(TAG, "string=" + string);
								NewActionInfo newActionInfo = GsonImpl.get().toObject(string,NewActionInfo.class);
								newActionInfo.actionUrl = actionInfo.actionPath;
								newActionInfo.actionSonType = actionInfo.actionSonType;
								newActionInfo.actionType = actionInfo.actionSonType;
								UbtLog.d(TAG, "1023 2 newActionInfo:" + newActionInfo.actionUrl + "-info:" + info);

								NewActionsManager.getInstance(mBaseActivity).doUpdate(newActionInfo);

							}

						}
					}
				});
	}

}
