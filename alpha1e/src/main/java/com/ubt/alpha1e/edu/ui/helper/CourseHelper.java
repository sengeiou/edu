package com.ubt.alpha1e.edu.ui.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.DB.LessonRecordOperater;
import com.ubt.alpha1e.edu.data.DB.LessonTaskRecordOperater;
import com.ubt.alpha1e.edu.data.DB.LessonTaskResultOperater;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.IFileListener;
import com.ubt.alpha1e.edu.data.Md5;
import com.ubt.alpha1e.edu.data.NewJsonTools;
import com.ubt.alpha1e.edu.data.ZipTools;
import com.ubt.alpha1e.edu.data.model.BaseNewResponseModel;
import com.ubt.alpha1e.edu.data.model.LessonTaskInfo;
import com.ubt.alpha1e.edu.data.model.LessonTaskResultInfo;
import com.ubt.alpha1e.edu.data.model.PageInfo;
import com.ubt.alpha1e.edu.data.model.LessonInfo;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.event.LessonEvent;
import com.ubt.alpha1e.edu.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.utils.GsonImpl;
import com.ubt.alpha1e.edu.utils.ImageUtils;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.zhy.changeskin.SkinManager;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class CourseHelper extends BaseHelper {

	private static final String TAG = "CourseHelper";

	private final int DO_GET_LESSON_FAIL = 200;//获取课时失败
	private final int DO_GET_LESSON_SUCCESS = 201;//获取课时成功
	private final int DO_DOWNLOAD_TASK_FAIL = 202;//下载任务失败
	private final int DO_DOWNLOAD_TASK_SUCCESS = 203;//下载任务成功
	private final int DO_DOWNLOAD_TASK_NOT_NEED = 204;//不需要下载任务
	private final int DO_READ_TASK_FAIL = 205;//读取任务失败
	private final int DO_READ_TASK_SUCCESS = 206;//读取任务成功
	private final int DO_SYNC_REMOTE_TASK_RESULT_FAIL = 207;//读取任务结果失败
	private final int DO_SYNC_REMOTE_TASK_RESULT_SUCCESS = 208;//读取任务结果成功
	private final int DO_DOWNLOAD_LESSON_TASK = 209;//下载课程任务
	private final int DO_TOKEN_ERROR = 210;//token验证错误

	private final int do_get_all_lesson = 101; //获取课时
	private final int do_save_lesson_task_result = 102; //保存课时任务结果
	private final int do_get_lesson_task_result_list = 103; //获取课时任务结果列表
	private final int do_get_course_task_result_list = 104; //获取课程任务结果列表
	private final int do_save_batch_lesson_task_result = 105; //批量保存课时任务结果

	private final String taskDataFileName = "data.txt";

	private List<LessonInfo> mLocalLessonInfoList = null;
	private List<LessonTaskInfo> mLocalLessonTaskInfoList = null;
	private List<LessonTaskResultInfo> mLocalLessonTaskResultInfoList = null;
	private LessonInfo mCurrentLessonInfo = null;
	private long mCurrentUserId = -1;
	public static int mCurrentCourseId = 91;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case DO_TOKEN_ERROR:
					LessonEvent tokenEvent = new LessonEvent(LessonEvent.Event.DO_TOKEN_ERROR);
					EventBus.getDefault().post(tokenEvent);
					break;
				case DO_GET_LESSON_FAIL:
				case DO_GET_LESSON_SUCCESS:
					LessonEvent lessonEvent = new LessonEvent(LessonEvent.Event.DO_GET_LESSON);
					lessonEvent.setLessonInfoList(mLocalLessonInfoList);
					EventBus.getDefault().post(lessonEvent);
					break;
				case DO_DOWNLOAD_LESSON_TASK:
					LessonEvent downloadLessonEvent = new LessonEvent(LessonEvent.Event.DO_DOWNLOAD_LESSON_TASK);
					EventBus.getDefault().post(downloadLessonEvent);
					break;
				case DO_READ_TASK_SUCCESS:
					{
						LessonEvent lessonTaskEvent = new LessonEvent(LessonEvent.Event.DO_GET_LESSON_TASK);
						lessonTaskEvent.setLessonTaskInfoList((List<LessonTaskInfo>) msg.obj);
						EventBus.getDefault().post(lessonTaskEvent);
					}
					break;
				case DO_READ_TASK_FAIL:
				case DO_DOWNLOAD_TASK_FAIL:
					{
						LessonEvent lessonTaskEvent = new LessonEvent(LessonEvent.Event.DO_GET_LESSON_TASK);
						lessonTaskEvent.setLessonTaskInfoList(new ArrayList<LessonTaskInfo>());
						EventBus.getDefault().post(lessonTaskEvent);
					}
					break;
				case DO_DOWNLOAD_TASK_NOT_NEED:
					if(mLocalLessonTaskInfoList != null && !mLocalLessonTaskInfoList.isEmpty()){
						LessonEvent taskEvent = new LessonEvent(LessonEvent.Event.DO_GET_LESSON_TASK);
						taskEvent.setLessonTaskInfoList(mLocalLessonTaskInfoList);
						EventBus.getDefault().post(taskEvent);
					}else {
						LessonInfo info = (LessonInfo) msg.obj;
						String filePath = FileTools.course_task_cache + File.separator + info.courseId + "_" + info.lessonId;
						readTaskContent(filePath,info.lessonId);
					}
					break;
				case DO_DOWNLOAD_TASK_SUCCESS:
					LessonInfo info = (LessonInfo) msg.obj;
					String filePath = FileTools.course_task_cache + File.separator + info.courseId + "_" + info.lessonId;
					readTaskContent(filePath,info.lessonId);
					break;

				case DO_SYNC_REMOTE_TASK_RESULT_FAIL:
					LessonEvent syncTaskFailEvent = new LessonEvent(LessonEvent.Event.DO_SYNC_TASK_DATA_FAIL);
					EventBus.getDefault().post(syncTaskFailEvent);
					break;
				case DO_SYNC_REMOTE_TASK_RESULT_SUCCESS:
					LessonEvent syncTaskSuccessEvent = new LessonEvent(LessonEvent.Event.DO_SYNC_TASK_DATA_SUCCESS);
					EventBus.getDefault().post(syncTaskSuccessEvent);
					break;
				default:
					break;
			}
		}
	};

	public CourseHelper(Context context) {
		super(context);
		UserInfo userInfo = ((AlphaApplication)context.getApplicationContext()).getCurrentUserInfo();
		if(userInfo != null){
			mCurrentUserId = userInfo.userId;
		}
	}

	public void getLessons(int courseId) {

		getLessonsFromLocal(courseId,mCurrentUserId);
		String localLanguage = getLanguage();
		String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.query_user_lessons);
		String params = HttpAddress.getParamsForGet(new String[]{"1", "50", courseId + "", localLanguage, "0"}, HttpAddress.Request_type.query_user_lessons);
		requestDataFromWebByGet(url + params,do_get_all_lesson);
	}

	/**
	 * 保存用户当前任务运行结果
	 * @param courseId
	 * @param lessonId
	 * @param taskId
	 * @param efficiencyStar
     * @param qualityStar
     */
	public void saveUserTaskResultToRemote(int courseId,int lessonId,int taskId,int efficiencyStar,int qualityStar){

		String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.save_user_task);

		String params = HttpAddress.getParamsForPost(new String[]{courseId + "",lessonId + "", taskId + "",efficiencyStar+"",qualityStar+ ""},
				HttpAddress.Request_type.save_user_task,mContext);

		requestDataFromWebByPost(url,params,do_save_lesson_task_result);
	}

	/**
	 * 批量保存用户当前任务运行结果
	 * @param taskResultInfoList 运行结果列表
	 */
	public void saveBatchUserTaskResultToRemote(List<LessonTaskResultInfo> taskResultInfoList){
		UbtLog.d(TAG,"taskResultInfoList = " + taskResultInfoList);
		if(taskResultInfoList != null && !taskResultInfoList.isEmpty()){
			LessonTaskResultInfo taskResultInfo = null;
			String jsonValue = "[";
			for(int i = 0; i<taskResultInfoList.size(); i++ ){
				taskResultInfo = taskResultInfoList.get(i);
				jsonValue += "{";
				jsonValue += "\"courseId\":" + taskResultInfo.courseId + ",";
				jsonValue += "\"lessonId\":" + taskResultInfo.lessonId + ",";
				jsonValue += "\"taskId\":"   + taskResultInfo.taskId   + ",";
				jsonValue += "\"efficiencyStar\":" + taskResultInfo.efficiencyStar + ",";
				jsonValue += "\"qualityStar\":" + taskResultInfo.qualityStar ;
				jsonValue += "}";
				if(i+1 != taskResultInfoList.size()){
					jsonValue += ",";
				}
			}
			jsonValue += "]";
			UbtLog.d(TAG,"jsonValue = " + jsonValue);

			String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.save_batch_user_task);
			String params = HttpAddress.getParamsForPost(new String[]{jsonValue + ""},
						 HttpAddress.Request_type.save_batch_user_task, mContext);

			requestDataFromWebByPost(url, params, do_save_batch_lesson_task_result);
		}else {
			mHandler.sendEmptyMessage(DO_SYNC_REMOTE_TASK_RESULT_SUCCESS);
		}
	}

	/**
	 * 通过课时ID 获取任务结果
	 * @param courseId
	 * @param lessonId
     */
	public void getUserTaskResultFromRemote(int courseId, int lessonId){
		String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.query_user_tasks);
		String params = HttpAddress.getParamsForGet(new String[]{courseId + "",lessonId + ""},HttpAddress.Request_type.query_user_tasks);

		requestDataFromWebByGet(url+params,do_get_lesson_task_result_list);
	}

	/**
	 * 通过课程ID 获取任务结果
	 * @param courseId
	 */
	public void getUserTaskResultFromRemote(int courseId){
		String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.query_user_tasks);
		String params = HttpAddress.getParamsForGet(new String[]{courseId + ""},HttpAddress.Request_type.query_user_tasks);

		requestDataFromWebByGet(url+params,do_get_course_task_result_list);
	}

	public List<LessonTaskResultInfo> getLessonTaskResult(int courseId, int lessonId){

		mLocalLessonTaskResultInfoList = LessonTaskResultOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.getRecoedByLessonId(courseId,lessonId,mCurrentUserId);

		return mLocalLessonTaskResultInfoList;
	}

	public List<LessonTaskResultInfo> getLessonTaskResult(int courseId){

		mLocalLessonTaskResultInfoList = LessonTaskResultOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.getRecoedByCourseId(courseId,mCurrentUserId);

		return mLocalLessonTaskResultInfoList;
	}

	public void updateLessonTaskResult(int courseId,LessonTaskInfo info,int challengeTime,int challengeFailCount){

		List<LessonTaskResultInfo> resultInfoList = LessonTaskResultOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.getRecordByTaskId(info.lesson_id,info.task_id,mCurrentUserId);

		int efficiencyStar = challengeTime > info.task_duration ? 0 : 1;
		int qualityStar = challengeFailCount > 0 ? 0 : 1;

		UbtLog.d(TAG,"updateLessonTaskResult = " + resultInfoList + " efficiencyStar = " + efficiencyStar + "	qualityStar = " + qualityStar);

		if(resultInfoList != null && resultInfoList.size() > 0){
			//update
			LessonTaskResultInfo resultInfo = resultInfoList.get(0);
			boolean needUpdate = false;
			if(resultInfo.efficiencyStar != efficiencyStar || resultInfo.qualityStar != qualityStar ){
				needUpdate = true;
			}

			if(needUpdate){
				resultInfo.efficiencyStar = efficiencyStar;
				resultInfo.qualityStar = qualityStar;
				resultInfo.updateTime = System.currentTimeMillis();
				LessonTaskResultOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name).updateRecord(resultInfo);
			}
		}else {
			addLessonTaskResult(courseId, info.lesson_id, info.task_id, efficiencyStar,qualityStar);
		}

		saveUserTaskResultToRemote(courseId,info.lesson_id,info.task_id,efficiencyStar,qualityStar);
	}

	public void addLessonTaskResult(int courseId, int lessonId,int taskId,int efficiencyStar,int qualityStar){

		LessonTaskResultInfo resultInfo = new LessonTaskResultInfo();
		resultInfo.userId = mCurrentUserId;
		resultInfo.courseId = courseId;
		resultInfo.lessonId = lessonId;
		resultInfo.taskId = taskId;
		resultInfo.efficiencyStar = efficiencyStar;
		resultInfo.qualityStar = qualityStar;
		resultInfo.addTime = System.currentTimeMillis();
		resultInfo.updateTime = resultInfo.addTime;
		LessonTaskResultOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name).addRecord(resultInfo);

	}

	private List<LessonTaskInfo> getLessonTasksFromLocal(int lessonId,String language){
		mLocalLessonTaskInfoList = LessonTaskRecordOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.getRecoedByLessonId(lessonId, mCurrentUserId,language);
		UbtLog.d(TAG,"mLocalLessonTaskInfoList = " + mLocalLessonTaskInfoList.size());
		return mLocalLessonTaskInfoList;
	}

	private void addLessonTaskToLocal(LessonTaskInfo info){
		LessonTaskRecordOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.addRecord(info);
	}

	public void updateLessonTask(LessonTaskInfo info){
		LessonTaskRecordOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.updateRecord(info);
	}

	private void deleteLessonTask(int taskId){
		LessonTaskRecordOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.deleteRecord(taskId);

		LessonTaskResultOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.deleteRecord(taskId);
	}

	private List<LessonInfo> getLessonsFromLocal(int courseId,long userId){
		mLocalLessonInfoList = LessonRecordOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.getRecoedByCourseId(courseId, userId);
		UbtLog.d(TAG,"mLocalLessonInfoList = " + mLocalLessonInfoList.size());
		return mLocalLessonInfoList;
	}

	private void addLessonToLocal(LessonInfo info){
		LessonRecordOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.addRecord(info);
	}

	public void updateLesson(LessonInfo info){
		LessonRecordOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
				.updateRecord(info);
	}

	public void deleteLesson(int lessonId){
		LessonRecordOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name).deleteRecord(lessonId);
	}

	/**
	 * 获取网络数据
	 * @param url 访问url
	 * @param id 访问id
     */
	private void requestDataFromWebByGet(String url,int id){

		UbtLog.d(TAG,"requestDataFromWebByGet params : " + url + "   id = " + id + " token = " + mCourseAccessToken);
		OkHttpClientUtils
				.getJsonByGetRequest(url,mCourseAccessToken,id)
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						UbtLog.e(TAG,"requestDataFromWebByGet exception : " + e.getMessage() + "   id = " + id );
						switch (id){
							case do_get_all_lesson:
								//mHandler.sendEmptyMessage(MSG_DO_REQUEST_FAIL);
								mHandler.sendEmptyMessage(DO_GET_LESSON_FAIL);
								break;
							case do_get_lesson_task_result_list:
								UbtLog.e(TAG,"do_get_lesson_task_result_list : " + e.getMessage());
								mHandler.sendEmptyMessage(DO_SYNC_REMOTE_TASK_RESULT_FAIL);

								break;
							case do_get_course_task_result_list:
								mHandler.sendEmptyMessage(DO_SYNC_REMOTE_TASK_RESULT_FAIL);
								break;
							default:
								break;
						}
					}

					@Override
					public void onResponse(String response, int id) {

						//UbtLog.d(TAG,"Course response id = " + id );

						if(id == do_get_course_task_result_list){

							try {
								if(NewJsonTools.getJsonStatus(response)){
									BaseNewResponseModel<List<LessonTaskResultInfo>> baseResponseModel = GsonImpl.get().toObject(response,
											new TypeToken<BaseNewResponseModel<List<LessonTaskResultInfo>>>(){}.getType());

									List<LessonTaskResultInfo> remoteTaskResultInfoList = baseResponseModel.data;
									//UbtLog.d(TAG,"remoteTaskResultInfoList : " + remoteTaskResultInfoList );

									//update to local
									if(remoteTaskResultInfoList != null){
										UbtLog.d(TAG,"remoteTaskResultInfoList = " + remoteTaskResultInfoList.size() + "	mLocalLessonTaskResultInfoList = " + mLocalLessonTaskResultInfoList.size());

										for(LessonTaskResultInfo remoteInfo : remoteTaskResultInfoList){

											boolean isNeedAdd = true;
											for(LessonTaskResultInfo localInfo : mLocalLessonTaskResultInfoList){
												//判断有没有数据，没有则添加，不作更新，已本地数据为准
												if(remoteInfo.courseId == localInfo.courseId
														&& remoteInfo.lessonId == localInfo.lessonId
														&& remoteInfo.taskId == localInfo.taskId){
													isNeedAdd = false;
													break;
												}
											}
											if(isNeedAdd){
												addLessonTaskResult(remoteInfo.courseId, remoteInfo.lessonId, remoteInfo.taskId, remoteInfo.efficiencyStar, remoteInfo.qualityStar);
											}
										}
									}

									//update to remote
									List<LessonTaskResultInfo> needAddList = new ArrayList<>();
									if(remoteTaskResultInfoList != null){
										for(LessonTaskResultInfo localInfo : mLocalLessonTaskResultInfoList){
											boolean isNeedAdd = true;
											for(LessonTaskResultInfo remoteInfo : remoteTaskResultInfoList){
												//判断有没有数据，以及数据是否一样
												String localValue  = "" + localInfo.courseId  + localInfo.lessonId  + localInfo.taskId  + localInfo.efficiencyStar  + localInfo.qualityStar;
												String remoteValue = "" + remoteInfo.courseId + remoteInfo.lessonId + remoteInfo.taskId + remoteInfo.efficiencyStar + remoteInfo.qualityStar;
												if(localValue.equals(remoteValue)){
													isNeedAdd = false;
													break;
												}
											}
											if(isNeedAdd){
												needAddList.add(localInfo);
											}
										}
									}else {
										//如果为空，把本地的全部更新到远程
										needAddList.addAll(mLocalLessonTaskResultInfoList);
									}

									saveBatchUserTaskResultToRemote(needAddList);

								}else {
									mHandler.sendEmptyMessage(DO_SYNC_REMOTE_TASK_RESULT_FAIL);
								}

							}catch (Exception ex) {
								UbtLog.e(TAG,ex.getMessage());
								mHandler.sendEmptyMessage(DO_SYNC_REMOTE_TASK_RESULT_FAIL);
							}

						}else if(id == do_get_all_lesson){
							//UbtLog.d(TAG,"do_get_all_lesson response = " + response);
							if(NewJsonTools.getJsonStatus(response)){
								try{
									BaseNewResponseModel<List<PageInfo<List<LessonInfo>>>> baseResponseModel = GsonImpl.get().toObject(response,
											new TypeToken<BaseNewResponseModel<List<PageInfo<List<LessonInfo>>>>>(){}.getType());

									UbtLog.d(TAG,"baseResponseModel = " + baseResponseModel.data + "	" + baseResponseModel.responseTime);
									if(baseResponseModel.data != null && baseResponseModel.data.size() != 0){
										PageInfo pageInfo = baseResponseModel.data.get(0);

										List<LessonInfo> lessonInfos = (List<LessonInfo>) pageInfo.records;
										if(lessonInfos != null && lessonInfos.size() != 0){
											LessonInfo remoteLessonInfo = null;
											for(int i = 0; i< lessonInfos.size(); i++){
												remoteLessonInfo = lessonInfos.get(i);
												remoteLessonInfo.userId = mCurrentUserId;
												if(remoteLessonInfo.taskDown > 0 && remoteLessonInfo.status == 0){
													remoteLessonInfo.status = 1;
												}

												UbtLog.d(TAG,"remoteLessonInfo = " + remoteLessonInfo.lessonId + "		lessonName = "
														+ remoteLessonInfo.lessonName + "	" + remoteLessonInfo.taskDown+"_"+remoteLessonInfo.taskTotal + "	taskUrl = " + remoteLessonInfo.taskUrl);
												boolean isNeedAddToLocal = true;
												for(LessonInfo localLessonInfo : mLocalLessonInfoList){
													if(localLessonInfo.lessonId == remoteLessonInfo.lessonId){
														isNeedAddToLocal = false;
														updateLessonToLocal(localLessonInfo,remoteLessonInfo);
														break;
													}
												}

												if(isNeedAddToLocal){
													mLocalLessonInfoList.add(remoteLessonInfo);
													addLessonToLocal(remoteLessonInfo);
												}
											}

											//判断是否需要删除本地
											for(LessonInfo localLessonInfo : mLocalLessonInfoList){
												boolean isNeedDelete = true;
												for(LessonInfo remoteInfo : lessonInfos){
													if(localLessonInfo.lessonId == remoteInfo.lessonId){
														isNeedDelete = false;
														break;
													}
												}

												if(isNeedDelete){
													deleteLesson(localLessonInfo.lessonId);
													mLocalLessonInfoList.remove(localLessonInfo);
												}
											}
										}
									}
								}catch (Exception e)
								{
									e.printStackTrace();
								}

								mHandler.sendEmptyMessage(DO_GET_LESSON_SUCCESS);
							}else {

								if(NewJsonTools.getErrorCode(response) == -1){
									UbtLog.d(TAG,"token验证错误,请重新登录");
									mHandler.sendEmptyMessage(DO_TOKEN_ERROR);
								}else {
									mHandler.sendEmptyMessage(DO_GET_LESSON_FAIL);
								}
							}
						}
					}
				});
	}

	/**
	 * 获取网络数据
	 * @param url 访问url
	 * @param param 访问参数
	 * @param id 访问id
	 */
	private void requestDataFromWebByPost(String url,String param,int id){

		UbtLog.d(TAG,"requestDataFromWebByPost params : " + url + "   id = " + id + " token = " + mCourseAccessToken);
		OkHttpClientUtils
				.getJsonByPostRequest(url,param,mCourseAccessToken,id)
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						UbtLog.d(TAG,"requestDataFromWebByPost exception : " + e.getMessage() + "   id = " + id );
						switch (id){
							case do_save_lesson_task_result:
								UbtLog.e(TAG,"do_save_lesson_task_result : " + e.getMessage());
								break;
							case do_save_batch_lesson_task_result:
								UbtLog.e(TAG,"do_save_batch_lesson_task_result : " + e.getMessage());
								mHandler.sendEmptyMessage(DO_SYNC_REMOTE_TASK_RESULT_FAIL);
								break;
							default:
								break;
						}
					}

					@Override
					public void onResponse(String response, int id) {
						if(id == do_save_lesson_task_result){
							UbtLog.d(TAG,"do_save_lesson_task_result response : " + response + "  id = " + id);
						}else if(id == do_save_batch_lesson_task_result){
							UbtLog.d(TAG, "save_batch = " + response);
							if(NewJsonTools.getJsonStatus(response)){
								mHandler.sendEmptyMessage(DO_SYNC_REMOTE_TASK_RESULT_SUCCESS);
							}else {
								mHandler.sendEmptyMessage(DO_SYNC_REMOTE_TASK_RESULT_FAIL);
							}
						}
					}
				});
	}

	private LessonInfo updateLessonToLocal(LessonInfo localInfo,LessonInfo remoteInfo){

		//本地需要更新的字段的值
		String localUpdateValue = localInfo.lessonGuide
								+ localInfo.lessonName
								+ localInfo.isDeleted
								+ localInfo.lessonDifficulty
								+ localInfo.lessonIcon
								+ localInfo.lessonOrder
								+ localInfo.lessonPic
								+ localInfo.lessonText
								+ localInfo.taskUrl
								+ localInfo.taskMd5
								+ localInfo.taskTotal
								;

		String romoteUpdateValue = remoteInfo.lessonGuide
								+ remoteInfo.lessonName
								+ remoteInfo.isDeleted
								+ remoteInfo.lessonDifficulty
								+ remoteInfo.lessonIcon
								+ remoteInfo.lessonOrder
								+ remoteInfo.lessonPic
								+ remoteInfo.lessonText
								+ remoteInfo.taskUrl
								+ remoteInfo.taskMd5
								+ remoteInfo.taskTotal
								;
		if(!localUpdateValue.equals(romoteUpdateValue)
				|| localInfo.taskDown > remoteInfo.taskTotal
				|| localInfo.taskDown < remoteInfo.taskDown){
			//need update
			localInfo.lessonGuide = remoteInfo.lessonGuide;
			localInfo.lessonName  = remoteInfo.lessonName;
			localInfo.isDeleted	  = remoteInfo.isDeleted;
			localInfo.lessonIcon  = remoteInfo.lessonIcon;
			localInfo.lessonOrder = remoteInfo.lessonOrder;
			localInfo.lessonPic   = remoteInfo.lessonPic;
			localInfo.lessonText  = remoteInfo.lessonText;
			localInfo.taskUrl     = remoteInfo.taskUrl;
			localInfo.taskMd5     = remoteInfo.taskMd5;
			localInfo.taskTotal   = remoteInfo.taskTotal;
			localInfo.lessonDifficulty = remoteInfo.lessonDifficulty;

			if(localInfo.taskDown > 0 && localInfo.status == 0){
				//已闯关数大于0，但是没状态没解锁
				localInfo.status = 1;
			}

			if(localInfo.taskDown > localInfo.taskTotal){
				//后台删除任务
				localInfo.taskDown = localInfo.taskTotal;
			}

			if(localInfo.taskDown < remoteInfo.taskDown){
				localInfo.taskDown = remoteInfo.taskDown;
			}

			//更新数据库
			updateLesson(localInfo);

			//替换缓存
			for(int index = 0 ;index < mLocalLessonInfoList.size(); index++){
				LessonInfo localLessonInfo = mLocalLessonInfoList.get(index);
				if(localLessonInfo.lessonId == localInfo.lessonId){
					mLocalLessonInfoList.remove(index);
					mLocalLessonInfoList.add(index,localInfo);
					break;
				}
			}
		}
		return localInfo;
	}

	private void updateLessonTaskToLocal(LessonTaskInfo localInfo,LessonTaskInfo remoteInfo){

		//本地需要更新的字段的值
		String localUpdateValue = localInfo.task_name
				+ localInfo.task_pic
				+ localInfo.have_debris
				+ localInfo.task_duration
				+ localInfo.debris_name
				+ localInfo.task_text
				+ localInfo.task_order
				+ localInfo.debris_pic
				+ localInfo.task_guide
				+ localInfo.task_help
				+ localInfo.task_result
				+ localInfo.main_text
				+ localInfo.voice_file
				+ localInfo.motion_file
				+ localInfo.task_voice_en
				+ localInfo.task_voice
				+ localInfo.motion_name
				;

		String romoteUpdateValue = remoteInfo.task_name
				+ remoteInfo.task_pic
				+ remoteInfo.have_debris
				+ remoteInfo.task_duration
				+ remoteInfo.debris_name
				+ remoteInfo.task_text
				+ remoteInfo.task_order
				+ remoteInfo.debris_pic
				+ remoteInfo.task_guide
				+ remoteInfo.task_help
				+ remoteInfo.task_result
				+ remoteInfo.main_text
				+ remoteInfo.voice_file
				+ remoteInfo.motion_file
				+ remoteInfo.task_voice_en
				+ remoteInfo.task_voice
				+ remoteInfo.motion_name
				;
		if(!localUpdateValue.equals(romoteUpdateValue)){
			//need update
			localInfo.task_name = remoteInfo.task_name;
			localInfo.task_pic  = remoteInfo.task_pic;
			localInfo.have_debris = remoteInfo.have_debris;
			localInfo.task_duration  = remoteInfo.task_duration;
			localInfo.debris_name = remoteInfo.debris_name;
			localInfo.task_text   = remoteInfo.task_text;
			localInfo.task_order  = remoteInfo.task_order;
			localInfo.debris_pic  = remoteInfo.debris_pic;
			localInfo.task_guide  = remoteInfo.task_guide;
			localInfo.task_help   = remoteInfo.task_help;
			localInfo.task_result = remoteInfo.task_result;
			localInfo.main_text   = remoteInfo.main_text;
			localInfo.voice_file  = remoteInfo.voice_file;
			localInfo.motion_file = remoteInfo.motion_file;
			localInfo.task_voice_en = remoteInfo.task_voice_en;
			localInfo.task_voice = remoteInfo.task_voice;
			localInfo.motion_name = remoteInfo.motion_name;

			//更新数据库
			updateLessonTask(localInfo);

			/*//替换缓存
			for(int index = 0 ;index < mLocalLessonTaskInfoList.size(); index++){
				LessonTaskInfo localLessonTaskInfo = mLocalLessonTaskInfoList.get(index);
				if(localLessonTaskInfo.task_id == localInfo.task_id){
					mLocalLessonTaskInfoList.remove(index);
					mLocalLessonTaskInfoList.add(index,localInfo);
					break;
				}
			}*/
		}
	}

	/**
	 *
	 * @param info
     */
	public void syncData(LessonInfo info){
		mCurrentLessonInfo = info;
		getLessonTaskResult(info.courseId, info.lessonId);
		getUserTaskResultFromRemote(info.courseId, info.lessonId);
	}

	public void loadLessonTask(LessonInfo info){

		String localLanguage = getLanguage();

		getLessonTasksFromLocal(info.lessonId,localLanguage);

		boolean isNeed = isNeedDownload(info);
		UbtLog.d(TAG,"isNeedDownload = " + isNeed + "  mContext = " + mContext);
		if(isNeed){
			mHandler.sendEmptyMessage(DO_DOWNLOAD_LESSON_TASK);
			downloadLessonTask(info);
		}else {
			Message msg = new Message();
			msg.what = DO_DOWNLOAD_TASK_NOT_NEED;
			msg.obj = info;
			mHandler.sendMessage(msg);
		}
	}

	private boolean isNeedDownload(LessonInfo info){
		boolean isNeed = true;
		final String taskPath = FileTools.course_task_cache + File.separator + info.courseId + "_" + info.lessonId + ".zip";
		File taskFile = new File(taskPath);
		if(taskFile.exists()){
			String taskFileMd5 = Md5.getFileMD5String(taskFile);
			UbtLog.d(TAG,"info.taskMd5 = " + info.taskMd5 + "	taskFileMd5 = " + taskFileMd5);
			if(info.taskMd5 != null && info.taskMd5.equalsIgnoreCase(taskFileMd5)){
				isNeed = false;
			}
		}
		return isNeed;
	}

	public void downloadLessonTask(final LessonInfo info){
		final String taskPath = FileTools.course_task_cache + File.separator + info.courseId + "_" + info.lessonId + ".zip";

		final long requestTime = System.currentTimeMillis();
		GetDataFromWeb.getFileFromHttp(requestTime, info.taskUrl, taskPath, new FileDownloadListener() {
			@Override
			public void onGetFileLenth(long request_code, double file_lenth) {

			}

			@Override
			public void onStopDownloadFile(long request_code, State state) {

			}

			@Override
			public void onReportProgress(long request_code, double progess) {
				UbtLog.d(TAG,"onReportProgress progess = " + progess);
			}

			@Override
			public void onDownLoadFileFinish(long request_code, State state) {
				UbtLog.d(TAG,"onDownLoadFileFinish = " + state);
				if(state == State.success){
					try {

						String filePath = FileTools.course_task_cache + File.separator + info.courseId + "_" + info.lessonId;
						//删除原来的
						FileTools.DeleteFile(new File(filePath));

						String result = ZipTools.unZip(taskPath, filePath);

						copyTaskFileToBlocklyProject(info,filePath);

						UbtLog.d(TAG,"onDownLoadFileFinish =>> " + result);

						Message msg = new Message();
						msg.what = DO_DOWNLOAD_TASK_SUCCESS;
						msg.obj = info;
						mHandler.sendMessage(msg);
					}catch (Exception ex){
						UbtLog.e(TAG,ex.getMessage());
						mHandler.sendEmptyMessage(DO_DOWNLOAD_TASK_FAIL);
					}
				}else {
					mHandler.sendEmptyMessage(DO_DOWNLOAD_TASK_FAIL);
				}
			}
		});
	}

	private void readTaskContent(final String filePath,final int lessonId){
		FileTools.readFileString(filePath, taskDataFileName, -1, new IFileListener() {
			@Override
			public void onReadImageFinish(Bitmap img, long request_code) {

			}

			@Override
			public void onReadFileStrFinish(String erroe_str, String result, boolean result_state, long request_code) {

				syncLocalTaskData(filePath, lessonId, result);

				List<LessonTaskInfo> taskInfoList = getLessonTasksFromLocal(lessonId,getLanguage());
				if(taskInfoList != null && !taskInfoList.isEmpty()){
					Message msg = new Message();
					msg.what = DO_READ_TASK_SUCCESS;
					msg.obj = taskInfoList;
					mHandler.sendMessage(msg);
				}else {
					mHandler.sendEmptyMessage(DO_READ_TASK_FAIL);
				}
			}

			@Override
			public void onWriteFileStrFinish(String erroe_str, boolean result, long request_code) {

			}

			@Override
			public void onWriteDataFinish(long requestCode, FileTools.State state) {

			}

			@Override
			public void onReadCacheSize(int size) {

			}

			@Override
			public void onClearCache() {

			}
		});
	}

	private void syncLocalTaskData(String filePath, int lessonId, String result){
		String[] languages = getCourseLanguages();
		for(String language : languages){
			syncLocalTaskData(filePath, lessonId, result, language);
		}

	}

	private boolean syncLocalTaskData(String filePath, int lessonId, String result, String language){
		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray(language);
			List<LessonTaskInfo> taskInfoList = LessonTaskInfo.getModelList(jsonArray.toString());
			//UbtLog.d(TAG,"syncLocalTaskData language == " + language + "	taskInfoList = " + taskInfoList.size());
			if(taskInfoList != null && !taskInfoList.isEmpty()){

				List<LessonTaskInfo> mLocalLessonTaskInfoList = LessonTaskRecordOperater.getInstance(mContext, FileTools.db_log_cache, FileTools.db_log_name)
						.getRecoedByLessonId(lessonId, mCurrentUserId,language);

				for(int i = 0; i<taskInfoList.size(); i++){

					LessonTaskInfo lessonTaskInfo = taskInfoList.get(i);
					lessonTaskInfo.user_id = mCurrentUserId;

					//重命名动作文件
					if(!TextUtils.isEmpty(lessonTaskInfo.motion_file) && !TextUtils.isEmpty(lessonTaskInfo.motion_name)){
						File destMotionFile = new File(filePath + File.separator + lessonTaskInfo.motion_name + ".hts");

						if(!destMotionFile.exists()){
							File sourseMotionFile = new File(filePath + File.separator + lessonTaskInfo.motion_file);
							if(sourseMotionFile.exists()){
								FileTools.copyFile(sourseMotionFile.getPath(), destMotionFile.getPath(), true);
							}
						}
					}

					boolean isNeedAddToLocal = true;
					for(LessonTaskInfo taskInfo : mLocalLessonTaskInfoList){
						if(lessonTaskInfo.task_id == taskInfo.task_id){
							isNeedAddToLocal = false;
							updateLessonTaskToLocal(taskInfo,lessonTaskInfo);
							break;
						}
					}

					if(isNeedAddToLocal){
						addLessonTaskToLocal(lessonTaskInfo);
					}
				}

				//判断是否需要删除本地任务
				for(LessonTaskInfo localLessonTaskInfo : mLocalLessonTaskInfoList){
					boolean isNeedDelete = true;
					for(LessonTaskInfo remoteLessonTaskInfo : taskInfoList){
						if(localLessonTaskInfo.task_id == remoteLessonTaskInfo.task_id){
							isNeedDelete = false;
							break;
						}
					}

					if(isNeedDelete){
						deleteLessonTask(localLessonTaskInfo.task_id);
					}
				}

				return true;
			}else {
				return false;
			}
		} catch (JSONException e) {
			UbtLog.e(TAG,e.getMessage());
			return false;
		}
	}

	public void copyTaskFileToBlocklyProject(LessonInfo lessonInfo,String taskFilePath){
		File taskFileDir = new File(taskFilePath);
		if(!taskFileDir.exists()){
			return;
		}

		String[] action_files = taskFileDir.list(new FilenameFilter() {
			public boolean accept(File f, String name) {
				return name.endsWith(".js");
			}
		});

		UbtLog.d(TAG,"action_files = " + action_files);
		if(action_files != null){

			String destFilePath = getBlocklyLessonTaskPath()+ lessonInfo.lessonId ;

			for (String fileName : action_files){
				boolean isCopySuccess = FileTools.copyFile(taskFilePath + File.separator + fileName, destFilePath + File.separator + fileName,true);
				UbtLog.d(TAG,"isCopySuccess = " + isCopySuccess);
			}
		}
	}

	private String getBlocklyLessonTaskPath(){
		return Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "Android"
				+ File.separator + "data"
				+ File.separator + mContext.getPackageName()
				+ File.separator + "files" + File.separator + "data/blockly/Blockly/engine/courses/";
	}

	public static File getLocalTaskFile(LessonInfo info,String fileName){
		String filePath = FileTools.course_task_cache + File.separator + info.courseId + "_" + info.lessonId + File.separator + fileName;
		File file = new File(filePath);
		return file;
	}

	public static File getLocalFile(String url){
		if(TextUtils.isEmpty(url)){
			//返回一个不存在的文件
			return new File(FileTools.file_path + File.separator + System.currentTimeMillis());
		}else {
			String localFileName = Md5.getMD5(url) + "." + url.substring(url.lastIndexOf(".") + 1);
			String filePath = FileTools.course_image_cache + File.separator + localFileName;
			File file = new File(filePath);
			return file;
		}
	}

	public File loadLocalImage(String imageUrl){
		File imageFile = CourseHelper.getLocalFile(imageUrl);
		if(!imageFile.exists()){
			loadImageToSave(imageUrl,imageFile.getPath());
		}
		return imageFile;
	}

	private void loadImageToSave(final String url,final String filePath){
		if(!TextUtils.isEmpty(url)){
			FileTools.pool.execute(new Runnable() {
				@Override
				public void run() {
					Bitmap bitmap = ImageUtils.loadBitmap(url, mContext);
					UbtLog.d(TAG,"bitmap => " + bitmap + "  filePath = " + filePath);
					FileTools.writeImage(bitmap,filePath,false);
				}
			});
		}
	}

	public void initCourseVoiceFile(Context mContext){
		List<String> voiceList = getAllVoiceList();
		File blockVoicePath = new File(getBlocklyDir(mContext) + "/voice");
		if(blockVoicePath.exists()){
			String[] fileList = blockVoicePath.list();
			UbtLog.d(TAG,"fileList = " +fileList.length );
			for(String voiceFileName : voiceList){
				boolean isNeedCopy = true;
				for(String existFile : fileList){
					if(voiceFileName.equals(existFile)){
						isNeedCopy = false;
						break;
					}
				}
				if(isNeedCopy){
					boolean isFileCreateSuccess = FileTools.writeAssetsToSd("music/blockly/" + voiceFileName, mContext, blockVoicePath.getPath() + File.separator + voiceFileName);
					UbtLog.d(TAG,"voiceFileName = " + voiceFileName + "		isFileCreateSuccess = " + isFileCreateSuccess);
				}
			}
		}else {
			for(String voiceFileName : voiceList){
				boolean isFileCreateSuccess = FileTools.writeAssetsToSd("music/blockly/" + voiceFileName, mContext, blockVoicePath.getPath() + File.separator + voiceFileName);
				UbtLog.d(TAG,"voiceFileName = " + voiceFileName + "		isFileCreateSuccess = " + isFileCreateSuccess);
			}
		}
	}

	private List<String> getAllVoiceList(){
		List<String> voiceList = new ArrayList<>();

		//动物
		voiceList.add("id_elephant.wav");//大象
		voiceList.add("id_dog.wav");    //狗
		voiceList.add("id_chicken.wav");//鸡
		voiceList.add("id_tiger.wav"); //老虎
		voiceList.add("id_donkey.wav");//驴
		voiceList.add("id_horse.wav"); //马
		voiceList.add("id_cat.wav");   //猫
		voiceList.add("id_cow.wav");   //牛
		voiceList.add("id_duck.wav");  //鸭
		voiceList.add("id_sheep.wav"); //羊

		//特效
		voiceList.add("id_kungFu.wav");		//功夫
		voiceList.add("id_guru.wav");    	//咕噜
		voiceList.add("id_monster.wav"); 	//怪兽
		voiceList.add("id_robot.wav"); 		//机器人
		voiceList.add("id_giantMonster.wav");//巨人怪
		voiceList.add("id_aliens.wav"); 	//外星人
		voiceList.add("id_yellowPeople.wav");   //小黄人
		voiceList.add("id_Indian.wav");   	//印第安
		voiceList.add("id_ghost.wav");  	//幽灵
		voiceList.add("id_apeMan.wav"); 	//猿人

		//日常
		voiceList.add("id_shutUp.wav");		//别说话
		voiceList.add("id_whistling.wav");  //吹口哨
		voiceList.add("id_yawning.wav"); 	//打哈欠
		voiceList.add("id_snore.wav"); 		//打呼噜
		voiceList.add("id_fart.wav");		//放屁
		voiceList.add("id_cough.wav"); 		//咳嗽
		voiceList.add("id_kiss.wav");   	//喷嚏
		voiceList.add("id_blowNose.wav");   //亲吻
		voiceList.add("id_sneezing.wav");  	//擤鼻涕
		voiceList.add("id_alarm_song.mp3"); //报警

		//情绪
		voiceList.add("id_laugh.wav");		//大笑
		voiceList.add("id_sorry.mp3");  	//对不起
		voiceList.add("id_relax.mp3"); 		//放松
		voiceList.add("id_nervous.wav"); 	//紧张
		voiceList.add("id_happy.wav");		//开心
		voiceList.add("id_angry.mp3"); 		//生气
		voiceList.add("id_like.mp3");   	//喜欢
		voiceList.add("id_abandon.wav");   	//嫌弃
		voiceList.add("id_boos.wav");  		//嘘声
		voiceList.add("id_doubt.mp3");  	//疑问

		voiceList.add("course_success.mp3"); //成功
		voiceList.add("course_failer.mp3");  //失败

		return voiceList;
	}

	public List<String> getNeedSyncAction(LessonInfo info,List<String> existActionList,List<LessonTaskInfo> taskInfoList){
		List<String> syncAtionList = new ArrayList<>();
		String filePath = FileTools.course_task_cache + File.separator + info.courseId + "_" + info.lessonId;
		File lessonDir = new File(filePath);
		if(lessonDir.exists() && taskInfoList != null && taskInfoList.size() > 0){
			String[] actionFiles = lessonDir.list(new FilenameFilter() {
				public boolean accept(File f, String name) {
					return name.endsWith(".hts");
				}
			});
			UbtLog.d(TAG,"actionFiles == " + actionFiles);

			List<String> needCompareFile = new ArrayList<>();
			for(String actionFile : actionFiles){
				boolean isNeedCompare = false;
				for(LessonTaskInfo taskInfo : taskInfoList){
					if(!TextUtils.isEmpty(taskInfo.motion_name) && (taskInfo.motion_name + ".hts").equals(actionFile)){
						isNeedCompare = true;
						break;
					}
				}
				if(isNeedCompare){
					needCompareFile.add(actionFile);
				}
			}
			UbtLog.d(TAG,"needCompareFile == " + needCompareFile);


			for(String actionFile : needCompareFile){
				boolean isNeedSync = true;
				for(String existAction : existActionList){
					if(actionFile.equals(existAction+".hts")){
						isNeedSync = false;
						break;
					}
				}

				if(isNeedSync){
					UbtLog.d(TAG,"SYNC actionFile = " + actionFile);
					syncAtionList.add(actionFile);
				}
			}
		}

		return syncAtionList;
	}

	public static String getBlocklyDir(Context context){
		String cacheDir = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "Android"
				+ File.separator + "data"
				+ File.separator + context.getPackageName()
				+ File.separator + "files" + File.separator
				+ "data/blockly";

		return cacheDir;
	}

	private String getLanguage(){
		String localLanguage = AlphaApplication.getBaseActivity().getAppCurrentLanguage();

		if(localLanguage.contains("zh")){
			String country = AlphaApplication.getBaseActivity().getAppCurrentCountry();
			if("tw".equalsIgnoreCase(country) || "hk".equalsIgnoreCase(country)){
				localLanguage = "zh_tw";
			}else {
				localLanguage = "zh_cn";
			}
		}
		localLanguage = AlphaApplication.getBaseActivity().getStandardLocale(localLanguage);

		return getCourseLanguage(localLanguage);
	}


	private String getCourseLanguage(String currentAppLanguage){
		String courseLanguage = "en";
		String[] languages = getCourseLanguages();
		for(String language : languages){
			//UbtLog.d(TAG,"language = " + language + "	currentAppLanguage = " + currentAppLanguage);
			if("zh_cn".equals(language) ){
				String cnLanguage = AlphaApplication.getBaseActivity().getStandardLocale(language);
				if(cnLanguage.equalsIgnoreCase(currentAppLanguage)){
					courseLanguage = language;
					break;
				}
			}else {
				if(language.equalsIgnoreCase(currentAppLanguage)){
					courseLanguage = language;
					break;
				}
			}
		}
		UbtLog.d(TAG,"courseLanguage =>> " + courseLanguage);
		return courseLanguage;
	}

	private String[] getCourseLanguages(){
		String[] languages = null;
		Resources res = mContext.getResources();
		if (SkinManager.getInstance().needChangeSkin()) {
			res = SkinManager.getInstance().getmResources();
			try {
				languages = res.getStringArray(res.getIdentifier("ui_course_language_up", "array", FileTools.package_name));
			} catch (Exception e) {
				UbtLog.e(TAG, "exception: " + e.toString());
				languages = mContext.getResources().getStringArray(R.array.ui_course_language_up);
			}
		} else {
			languages = res.getStringArray(R.array.ui_course_language_up);
		}
		return languages;
	}

	/**
	 *
	 * @param courseId
	 */
	public void syncBatchData(int courseId){

		getLessonTaskResult(courseId);
		getUserTaskResultFromRemote(courseId);
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
	public void DistoryHelper() {
		// TODO Auto-generated method stub
		super.UnRegisterHelper();
	}

	@Override
	public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
		// TODO Auto-generated method stub

	}

}
