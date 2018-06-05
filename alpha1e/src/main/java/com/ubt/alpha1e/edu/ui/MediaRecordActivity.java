package com.ubt.alpha1e.edu.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.business.ActionPlayer;
import com.ubt.alpha1e.edu.business.NewActionPlayer;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.model.ActionColloInfo;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.helper.ActionsHelper;
import com.ubt.alpha1e.edu.ui.helper.IActionsUI;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.edu.ui.helper.SettingHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.yixia.camera.MediaRecorderBase;
import com.yixia.camera.MediaRecorderNative;
import com.yixia.camera.VCamera;
import com.yixia.camera.model.MediaObject;
import com.yixia.camera.util.DeviceUtils;
import com.yixia.camera.util.FileUtils;
import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MediaRecordActivity extends BaseActivity implements MediaRecorderBase.OnErrorListener, View.OnClickListener, MediaRecorderBase.OnPreparedListener,
        MediaRecorderBase.OnEncodeListener,IActionsUI,BaseDiaUI{


    public static final String TAG = "MediaRecordActivity";
    /** 录制最长时间 */
    public final static int RECORD_TIME_MAX = 10 * 1000;
    /** 录制控制按钮 */
    private ImageView mRecordController;
    /** 重新录制控制按钮 */
    private ImageView mReRecordController;
    /** 播放动作按钮*/
    private ImageView mActionPlayController;
    /** 摄像头数据显示画布 */
    private SurfaceView mSurfaceView;
    /** 录制进度 */
    private ProgressBar mProgressView;
    /** 需要重新编译（拍摄新的或者回删） */
    private boolean mRebuild;
    /** SDK视频录制对象 */
    private MediaRecorderBase mMediaRecorder;
    /** 视频信息 */
    private MediaObject mMediaObject;
    /** 刷新进度条 */
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    /** 刷新时间*/
    private static final int HANDLE_TIME_PROGRESS = 2;
    /** 延迟拍摄停止 */
    private static final int HANDLE_STOP_RECORD = 1;
    /** 重新录制 */
    private static final int HANDLE_CLEAR_RECORD = -1;
    /** 弱引用Handler */
    private final MyHandler mHandler = new MyHandler(this);

    private boolean isRecording = false;
    private boolean isCompleteRecording = false;
    private int totalRecordTime = RECORD_TIME_MAX/1000;
    private TextView mTotalTime;
    public static final String REQUEST_CODE = "REQUEST_CODE";
    private TextView txt_reRecord,txt_record_control,txt_action_control;
    private NewActionInfo newActionInfo;

    private MyActionsHelper mHelper;
    private boolean isSendFileToRobo =false;

    private LoadingDialog mLoadingDialog;

//    private int sendType = -1;

    public static void launchActivity(Activity activity, NewActionInfo actionInfo,int requestCode)
    {
        Intent intent = new Intent();
        intent.setClass(activity,MediaRecordActivity.class);
        intent.putExtra(REQUEST_CODE,requestCode);
        intent.putExtra(ActionsHelper.TRANSFOR_PARCEBLE, PG.convertParcelable(actionInfo));
        activity.startActivityForResult(intent,requestCode);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_record);
        newActionInfo = getIntent().getParcelableExtra(ActionsHelper.TRANSFOR_PARCEBLE);
        if(newActionInfo !=null){
            UbtLog.e(TAG, "newActionInfo=" + newActionInfo.toString());
            newActionInfo.hts_file_name = newActionInfo.actionOriginalId+"";
        }


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHelper = MyActionsHelper.getInstance(this);
        /*mHelper.setManagerListeners(this);
        mHelper.RegisterHelper();
        mHelper.registerListeners(this);*/
        UbtLog.d(TAG,"--onCreate--");
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilityAdapter.freeFilterParser();
        UtilityAdapter.initFilterParser();
        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else {
            mMediaRecorder.prepare();
        }

        mHelper.setManagerListeners(this);
        mHelper.RegisterHelper();
        mHelper.registerListeners(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRecord();
        UtilityAdapter.freeFilterParser();
        if(mMediaRecorder!=null){
            mMediaRecorder.release();
        }
        mHelper.UnRegisterHelper();
        mHelper.unRegisterListeners(this);
    }

    @Override
    protected void initUI() {
        initTitle(getStringResources("ui_distribute_short_video"));
        initTitleSave(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaObject.getDuration() >= RECORD_TIME_MAX)
                {
                    showDialog();
                    mMediaRecorder.startEncoding();
                }


            }
        },getStringResources("ui_common_complete"));
        mLoadingDialog = LoadingDialog.getInstance(this,this);
        mLoadingDialog.setDoCancelable(true,5);
        ((Button)findViewById(R.id.btn_base_save)).setTextColor(getResources().getColor(R.color.T7));
        mTotalTime = (TextView)findViewById(R.id.txt_record_time);
        mSurfaceView = (SurfaceView) findViewById(R.id.record_preview);
        mRecordController = (ImageView) findViewById(R.id.img_record_play_or_pause);
        txt_record_control = (TextView) findViewById(R.id.txt_record_play_or_pause);
        mRecordController.setOnClickListener(this);
        mReRecordController = (ImageView) findViewById(R.id.img_re_record);
        txt_reRecord = (TextView) findViewById(R.id.txt_re_record);
        mReRecordController.setOnClickListener(this);
        mActionPlayController = (ImageView) findViewById(R.id.img_action_play_or_pause);
        mActionPlayController.setOnClickListener(this);
        if(newActionInfo != null){
            mActionPlayController.setImageResource(R.drawable.publish_action_play);
            mActionPlayController.setEnabled(true);
        }else{
            mActionPlayController.setImageResource(R.drawable.publish_action_play_unable);
            mActionPlayController.setEnabled(false);
        }

        txt_action_control = (TextView) findViewById(R.id.txt_action_play_or_pause);
        mProgressView = (ProgressBar) findViewById(R.id.record_progress);
        initSurfaceView();


    }

    public void showDialog() {
        if(mLoadingDialog!=null&&!mLoadingDialog.isShowing())
        {
            mLoadingDialog.show();
        }
    }

    public void dismissDialog() {

        if(mLoadingDialog!=null&&mLoadingDialog.isShowing()&&!this.isFinishing())
        {
            mLoadingDialog.cancel();
        }
    }
    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }
    /** 初始化画布 */
    private void initSurfaceView() {
        final int screenWidth = DeviceUtils.getScreenWidth(this);
        int width = screenWidth;
        int height = screenWidth * 3/ 4;
        //
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSurfaceView
                .getLayoutParams();
        lp.width = width;
        lp.height = height;
        mSurfaceView.setLayoutParams(lp);
    }
    /** 初始化拍摄SDK */
    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderNative();
        mRebuild = true;
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnEncodeListener(this);
        File f = new File(VCamera.getVideoCachePath());
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(key,
                VCamera.getVideoCachePath() + key);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();
    }
    /** 开始录制 */
    private void startRecord() {
        if (mMediaRecorder != null) {
            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            if (part == null) {
                return;
            }
        }
        if(isCompleteRecording)
            return;

        mRebuild = true;
        isRecording = true;
//        mPressedStatus = true;
        mRecordController.setImageResource(R.drawable.publish_video_pause);
        txt_record_control.setText(getStringResources("ui_distribute_pause_recording"));

        if (mHandler != null) {
            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);
            mHandler.removeMessages(HANDLE_TIME_PROGRESS);
            mHandler.sendEmptyMessage(HANDLE_TIME_PROGRESS);
            mHandler.removeMessages(HANDLE_STOP_RECORD);
            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD,
                    RECORD_TIME_MAX - mMediaObject.getDuration());
        }
    }
    /** 停止录制 */
    private void stopRecord() {
        isRecording = false;
        mRecordController.setImageResource(R.drawable.publish_video_play);
        txt_record_control.setText(getStringResources("ui_distribute_resume_recording"));
        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
        }
        mHandler.removeMessages(HANDLE_STOP_RECORD);
        mHandler.removeMessages(HANDLE_TIME_PROGRESS);
        mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
//        checkStatus();
    }
    /** 重新录制 */
    private void doRecordAgain()
    {
        if(mMediaObject == null)
            return;
        isRecording = false;
        isCompleteRecording = false;
        doDeleteRecord();
        txt_record_control.setText(getStringResources("ui_distribute_start_recording"));
        mHandler.sendEmptyMessage(HANDLE_CLEAR_RECORD);
        mHandler.removeMessages(HANDLE_TIME_PROGRESS);
        mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
        mHandler.removeMessages(HANDLE_STOP_RECORD);

    }
    /** 删除录制数据 */
    private void doDeleteRecord()
    {

        if (mMediaObject == null)
            return;
        List<MediaObject.MediaPart> mDeleteList = new LinkedList<>();
        mDeleteList.addAll(mMediaObject.getMedaParts());
        for (MediaObject.MediaPart part : mDeleteList) {
            if (part != null) {
                mRebuild = true;
                part.remove = false;
                mMediaObject.removePart(part, true);
            }
        }
        mDeleteList.clear();

//            mMediaObject.delete();
        }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.lay_base_back:
                break;
            case R.id.img_record_play_or_pause:
                if(isRecording)
                {
                    stopRecord();
                }else
                    startRecord();
                break;
            case R.id.img_re_record:
                doRecordAgain();
                break;
            case R.id.img_action_play_or_pause:
                if (!isBulueToothConnected()){
                    RobotConnectedActivity.launchActivity(MediaRecordActivity.this,true,12306);
                    return;
                }

                //检测是否在充电状态和边充边玩状态是否打开
                if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(MediaRecordActivity.this)){
                    Toast.makeText(MediaRecordActivity.this, getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!isSendFileToRobo)
                {
                    mHelper.sendActionFileToRobot(newActionInfo);
                    showDialog();
                }else
                {
                    if(mHelper.getPlayerState() == ActionPlayer.Play_state.action_finish && mHelper.getNewPlayerState() == NewActionPlayer.PlayerState.STOPING)
                    {
                        mHelper.doActionCommand(MyActionsHelper.Command_type.Do_play,
                                newActionInfo, MyActionsHelper.Action_type.My_new);
                    }else{
                        mHelper.doActionCommand(
                                MyActionsHelper.Command_type.Do_pause_or_continue, "",
                                MyActionsHelper.Action_type.My_new);
                    }

                }
                break;


        }

    }

    @Override
    public void onLostBtCoon() {
        MediaRecordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActionPlayController.setImageResource(R.drawable.publish_action_play);
            }
        });
        super.onLostBtCoon();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG,"--onDestroy--");
        if(isBulueToothConnected()){
            mHelper.doDefaultActions();
        }
        /*mHelper.UnRegisterHelper();
        mHelper.unRegisterListeners(this);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mMediaObject != null && mMediaObject.getDuration() > 1) {
            // 未转码
//            new AlertView()
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.hint)
//                    .setMessage(R.string.record_camera_exit_dialog_message)
//                    .setNegativeButton(
//                            R.string.record_camera_cancel_dialog_yes,
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    mMediaObject.delete();
//                                    finish();
//                                    overridePendingTransition(
//                                            R.anim.push_bottom_in,
//                                            R.anim.push_bottom_out);
//                                }
//
//                            })
//                    .setPositiveButton(R.string.record_camera_cancel_dialog_no,
//                            null).setCancelable(false).show();
            return;
        }

        if (mMediaObject != null)
            mMediaObject.delete();
        finish();
    }

    @Override
    public void onEncodeStart() {
        UbtLog.d("MediaRecordActivity","onEncodeStart---");
    }

    @Override
    public void onEncodeProgress(int progress) {
        UbtLog.d("MediaRecordActivity","onEncodeProgress---"+progress);

    }

    @Override
    public void onEncodeComplete() {
        UbtLog.d("MediaRecordActivity","onEncodeComplete---");
        mTotalTime.post(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
            }
        });

        Intent intent = new Intent();
        intent.putExtra("output",mMediaObject.getOutputTempVideoPath());
        MediaRecordActivity.this.setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onEncodeError() {
        UbtLog.d("MediaRecordActivity","onEncodeError---");
    }

    @Override
    public void onVideoError(int what, int extra) {

    }

    @Override
    public void onAudioError(int what, String message) {

    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onNoteNoUser() {

    }

    @Override
    public void onNoteTooMore() {

    }

    @Override
    public void onReadImgFromCache(Bitmap img, long l) {

    }

    @Override
    public void onReadActionsFinish(List<String> names) {

    }

    @Override
    public void onNoteVol(int vol_pow) {

    }

    @Override
    public void onNoteVolState(boolean vol_state) {

    }

    @Override
    public void onReadMyDownLoadHistory(String hashCode, List<ActionRecordInfo> history) {

    }

    @Override
    public void onSendFileStart() {

    }

    @Override
    public void onSendFileBusy() {

    }

    @Override
    public void onSendFileError() {

    }

    @Override
    public void onSendFileFinish(int pos) {
        UbtLog.d(TAG,"----onSendFileFinish----");
        isSendFileToRobo = true;
        dismissDialog();
        /*mHelper.doActionCommand(MyActionsHelper.Command_type.Do_play,
                newActionInfo, MyActionsHelper.Action_type.My_download);*/
        mHelper.doActionCommand(MyActionsHelper.Command_type.Do_play,
                newActionInfo, MyActionsHelper.Action_type.My_new);
    }

    @Override
    public void onSendFileCancel() {

    }

    @Override
    public void onSendFileUpdateProgress(String progress) {

    }

    @Override
    public void noteLightOn() {

    }

    @Override
    public void noteLightOff() {

    }

    @Override
    public void noteChangeFinish(boolean b, String string) {

    }

    @Override
    public void noteTFPulled() {

    }

    @Override
    public void syncServerDataEnd(List<Map<String, Object>> data) {

    }

    @Override
    public void noteDeleteActionStart(int pos) {

    }

    @Override
    public void noteDeleteActionFinish(boolean isOk, String str) {

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG,"----notePlayStart----");
        mActionPlayController.setImageResource(R.drawable.publish_action_pause);
        mHelper.doPlayMp3ForMyCreate(action);

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG,"----notePlayPause----");
        mActionPlayController.setImageResource(R.drawable.publish_action_play);

    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG,"----notePlayContinue----");
        mActionPlayController.setImageResource(R.drawable.publish_action_pause);

    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {
        UbtLog.d(TAG,"----notePlayFinish----");
        MediaRecordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActionPlayController.setImageResource(R.drawable.publish_action_play);
                mHelper.doStopMp3ForMyDownload();
            }
        });

    }

    @Override
    public void onPlaying() {
        UbtLog.d(TAG,"----onPlaying----");
        MediaRecordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActionPlayController.setImageResource(R.drawable.publish_action_pause);
            }
        });
    }

    @Override
    public void onPausePlay() {
        MediaRecordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActionPlayController.setImageResource(R.drawable.publish_action_play);
            }
        });
    }

    @Override
    public void onFinishPlay() {
        MediaRecordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActionPlayController.setImageResource(R.drawable.publish_action_play);
            }
        });
    }

    @Override
    public void onFrameDo(int index) {

    }

    @Override
    public void notePlayChargingError() {

    }

    @Override
    public void notePlayCycleNext(String action_name) {

    }

    @Override
    public void onReadCollocationRecordFinish(boolean isSuccess, String errorInfo, List<ActionColloInfo> history) {

    }

    @Override
    public void onDelRecordFinish() {

    }

    @Override
    public void onRecordFinish(long action_id) {

    }

    @Override
    public void onCollocateFinish(long action_id, boolean isSuccess, String error) {

    }

    @Override
    public void onCollocateRmoveFinish(boolean b) {

    }

    @Override
    public void onGetFileLenth(ActionInfo action, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(ActionInfo action, State state) {

    }

    @Override
    public void onReportProgress(ActionInfo action, double progess) {

    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, State state) {

    }

    @Override
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {

    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo) {

    }

    @Override
    public void onGetFileLenth(long request_code, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(long request_code, State state) {

    }

    @Override
    public void onReportProgress(long request_code, double progess) {

    }

    @Override
    public void onDownLoadFileFinish(long request_code, State state) {

    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result, boolean result_state, long request_code) {

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

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {

    }

    @Override
    public void onChangeNewActionsFinish() {

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    private static class MyHandler extends Handler {
        private final WeakReference<MediaRecordActivity> mActivity;

        public MyHandler(MediaRecordActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaRecordActivity activity = mActivity.get();
            if (activity != null) {
              switch (msg.what)
              {
                  case HANDLE_INVALIDATE_PROGRESS:
                      if (activity.mMediaRecorder != null && !activity.isFinishing()) {
                          if (activity.mProgressView != null)
                          {
                              int pos = activity.mMediaObject.getDuration()/10;
                              activity.totalRecordTime = (10-pos/100);
                              UbtLog.d("MediaRecordActivity","progress:"+pos);
                              activity.mProgressView.setProgress(pos);
                          }

                          sendEmptyMessageDelayed(HANDLE_INVALIDATE_PROGRESS, 50);

                      }
                      break;
                  case HANDLE_STOP_RECORD:
                      if(activity.mMediaObject.getDuration()<activity.RECORD_TIME_MAX)
                      {
                          int duration = activity.RECORD_TIME_MAX-activity.mMediaObject.getDuration();
                          sendEmptyMessageDelayed(HANDLE_STOP_RECORD,duration);
                          removeMessages(HANDLE_TIME_PROGRESS);
                      }else
                      {
                          activity.stopRecord();
                          activity.isCompleteRecording = true;
                          ((Button)activity.findViewById(R.id.btn_base_save)).setTextColor(activity.getResources().getColor(R.color.T5));
                          activity.mTotalTime.setText(0+"s");
                      }
                      break;
                  case HANDLE_TIME_PROGRESS:
                      activity.mTotalTime.setText(activity.totalRecordTime+"s");
                      UbtLog.d("MediaRecordActivity","remain:"+activity.totalRecordTime);
                      if(activity.totalRecordTime<=0) {
                          sendEmptyMessageDelayed(HANDLE_STOP_RECORD,1000);
                      }
                      sendEmptyMessageDelayed(HANDLE_TIME_PROGRESS, 1000);
                      break;
                  case HANDLE_CLEAR_RECORD:
                      activity.totalRecordTime = RECORD_TIME_MAX/1000;
                      activity.mRecordController.setImageResource(R.drawable.publish_video_play);
                      activity.mTotalTime.setText(activity.RECORD_TIME_MAX/1000+"s");
                      ((Button)activity.findViewById(R.id.btn_base_save)).setTextColor(activity.getResources().getColor(R.color.T7));
                      activity.mProgressView.setProgress(0);
                      break;
              }


            }
        }
    }

}
