package com.ubt.alpha1e_edu.business;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e_edu.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e_edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e_edu.data.JsonTools;
import com.ubt.alpha1e_edu.data.model.BaseResponseModel;
import com.ubt.alpha1e_edu.data.model.MessageInfo;
import com.ubt.alpha1e_edu.data.model.MessageRecordInfo;
import com.ubt.alpha1e_edu.data.model.UserInfo;
import com.ubt.alpha1e_edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e_edu.net.http.basic.IJsonListener;
import com.ubt.alpha1e_edu.utils.GsonImpl;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageRecordManager implements IJsonListener {

	private static final String TAG = "MessageRecordManager";

	private MessageRecordManager() {
	};

	private final static int doGetMessageHistory = 1001;
	private final static int doGetNoticeHistory = 1002;

	public final static int TYPE_NOTICE  = 1;
	public final static int TYPE_MESSAGE = 2;

	private static MessageRecordManager thiz;
	private Context mContext;
	private MessageRecordManagerListener mListener;
	private List<MessageInfo> mMessageList;
	private UserInfo mUser;

	public static MessageRecordManager getInstance(Context _context,
			UserInfo _user, MessageRecordManagerListener _listener) {
		if (thiz == null) {
			thiz = new MessageRecordManager();
		}
		thiz.mContext = _context;
		thiz.mListener = _listener;
		thiz.mUser = _user;
		return thiz;
	}

	public void doGetMessages() {

		JSONObject jobj = new JSONObject();
		String country_code = "";
		long user_id = -1;
		if (mUser != null){
			country_code = mUser.countryCode;
			user_id = mUser.userId ;
		}

		try {
			jobj.put("countryCode", country_code);
			//jobj.put("userId",284342);
			jobj.put("userId",user_id);
		} catch (Exception e) {
			mListener.onGetNewMessages(false, mContext.getResources()
					.getString(R.string.ui_remote_synchoronize_unknown_error), null,-1);
		}

		UbtLog.d(TAG,"url = " + HttpAddress.getRequestUrl(Request_type.get_messages_new));
		UbtLog.d(TAG,"param = " + HttpAddress.getParamsForPost(jobj.toString(), mContext));
		GetDataFromWeb.getJsonByPost(doGetMessageHistory,
				HttpAddress.getRequestUrl(Request_type.get_messages_new),
				HttpAddress.getParamsForPost(jobj.toString(), mContext), this);

	}

	/**
	 *
	 * @param type 区分是消息还是通知 1 是通知 2 是消息
	 * @param page
	 * @param pageSize
     */
	public void doGetMessages(int type,int page,int pageSize) {

		JSONObject jobj = new JSONObject();
		String country_code = "";
		long user_id = -1;
		if (mUser != null){
			country_code = mUser.countryCode;
			user_id = mUser.userId ;
		}

		try {
			jobj.put("type",type);
			jobj.put("page",page);
			jobj.put("pageSize",pageSize);

			jobj.put("countryCode", country_code);
			jobj.put("userId",user_id);
		} catch (Exception e) {
			mListener.onGetNewMessages(false, mContext.getResources()
					.getString(R.string.ui_remote_synchoronize_unknown_error), null,type);
		}

		//UbtLog.d(TAG,"url == " + HttpAddress.getRequestUrl(Request_type.get_messages_new));
		//UbtLog.d(TAG,"param = " + HttpAddress.getParamsForPost(jobj.toString(), mContext));

		if(type == TYPE_NOTICE){
			GetDataFromWeb.getJsonByPost(doGetNoticeHistory,
					HttpAddress.getRequestUrl(Request_type.get_messages_new),
					HttpAddress.getParamsForPost(jobj.toString(), mContext), this);

		}else {
			GetDataFromWeb.getJsonByPost(doGetMessageHistory,
					HttpAddress.getRequestUrl(Request_type.get_messages_new),
					HttpAddress.getParamsForPost(jobj.toString(), mContext), this);
		}

	}

	public void addToRecords(final MessageRecordInfo info) {
		BasicSharedPreferencesOperator.getInstance(mContext,
				DataType.MESSAGE_RECORD).doReadAsync(
				BasicSharedPreferencesOperator.MESSAGE_RECOED,
				new ISharedPreferensListenet() {

					@Override
					public void onSharedPreferenOpreaterFinish(
							boolean isSuccess, long request_code, String value) {

						if (isSuccess) {

							List<MessageRecordInfo> mRecords = new ArrayList<MessageRecordInfo>();
							if( !TextUtils.isEmpty(value)&&!value.equalsIgnoreCase("NO_VALUE"))
							{

								List<MessageRecordInfo> baseResponseModel = GsonImpl.get().toObject(value,
										new TypeToken<List<MessageRecordInfo>>(){}.getType() );
								mRecords = baseResponseModel;
							}

							if (mRecords != null&&mRecords.size()>0)
							{
								for (int i = 0; i < mRecords.size(); i++) {
									if (mRecords.get(i).messageId == info.messageId
											&& mRecords.get(i).userId == info.userId) {
										return;
									}
								}
							}

							if(!mRecords.contains(info)){
								mRecords.add(info);
							}

							String records = GsonImpl.get().toJson(mRecords);
							BasicSharedPreferencesOperator.getInstance(mContext,DataType.MESSAGE_RECORD)
									.doWrite(BasicSharedPreferencesOperator.MESSAGE_RECOED, records, null, -1);

							mListener.onAddNoReadMessage();
						}

					}
				}, -1);
	}

	private void readUnReadRecord() {
		BasicSharedPreferencesOperator.getInstance(mContext,
				DataType.MESSAGE_RECORD).doReadAsync(
				BasicSharedPreferencesOperator.MESSAGE_RECOED,
				new ISharedPreferensListenet() {

					@Override
					public void onSharedPreferenOpreaterFinish(
							boolean isSuccess, long request_code, String value) {
						UbtLog.e("MessageRecordManager", "value = " + value);
						if (isSuccess && !TextUtils.isEmpty(value)&&!value.equalsIgnoreCase("NO_VALUE")) {

							UbtLog.e("MessageRecordManager", "--wmma--onSharedPreferenOpreaterFinish");

							try{
								List<MessageRecordInfo> baseResponseModel = GsonImpl.get().toObject(value,
										new TypeToken<List<MessageRecordInfo>>(){}.getType() );
						    	List<MessageRecordInfo> mRecords = baseResponseModel;
						    	List<Long> messageIds = new ArrayList<>();

								for (int i = 0; i < mMessageList.size(); i++) {
									long id = mMessageList.get(i).messageId;
									for (int j = 0; j < mRecords.size(); j++) {
										if (id == mRecords.get(j).messageId && mUser.userId == mRecords.get(j).userId) {
											//id = -1;
											messageIds.add(id);
										}
									}
									//messageIds.add(id);
								}

								mListener.onReadUnReadRecords(messageIds);
								}catch (Exception exception){
									exception.printStackTrace();
								}

						}else{
							mListener.onReadUnReadRecords(new ArrayList<Long>());
						}
					}
				}, -1);
	}

	@Override
	public void onGetJson(boolean isSuccess, String json, long request_code) {
		UbtLog.d(TAG,"isSuccess = " + isSuccess + "	request_code = " + request_code + "	mListener = " + mListener);
		if (doGetMessageHistory == request_code || doGetNoticeHistory == request_code) {
			int type = TYPE_NOTICE;//Notice
			if(doGetMessageHistory == request_code){
				type = TYPE_MESSAGE;
			}

			if (isSuccess) {
				if (JsonTools.getJsonStatus(json)) {

					BaseResponseModel<List<MessageInfo>> baseResponseModel = GsonImpl.get().toObject(json,
							new TypeToken<BaseResponseModel<List<MessageInfo>>>(){}.getType());
//					mMessageList = MessageInfo.getModelList(JsonTools
//							.getJsonModels(json).toString());
					mMessageList = baseResponseModel.models;

					mListener.onGetNewMessages(true, "", mMessageList,type);
					UbtLog.d(TAG, "--wmma--readUnReadRecord start");
					if(doGetNoticeHistory == request_code){
						readUnReadRecord();
					}
				}
			} else {
				mListener.onGetNewMessages(false, mContext.getResources()
						.getString(R.string.ui_remote_synchoronize_unknown_error), null,type);
			}

		} else {
			mListener.onGetNewMessages(false, mContext.getResources()
					.getString(R.string.ui_common_network_request_failed), null,TYPE_NOTICE);
		}
	}

}
