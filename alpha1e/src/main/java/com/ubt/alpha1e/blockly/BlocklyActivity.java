package com.ubt.alpha1e.blockly;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.czt.mp3recorder.MP3Recorder;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.blockly.bean.QueryResult;
import com.ubt.alpha1e.blockly.bean.RobotSensor;
import com.ubt.alpha1e.blockly.sensor.SensorHelper;
import com.ubt.alpha1e.blockly.sensor.SensorObservable;
import com.ubt.alpha1e.blockly.sensor.SensorObserver;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.business.NewActionPlayer;
import com.ubt.alpha1e.business.thrid_party.IWeiXinListener;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.RemoteItem;
import com.ubt.alpha1e.data.ZipTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.FrameActionInfo;
import com.ubt.alpha1e.data.model.LessonInfo;
import com.ubt.alpha1e.data.model.LessonTaskInfo;
import com.ubt.alpha1e.data.model.LessonTaskResultInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.data.model.RemoteRoleInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.event.ActionEvent;
import com.ubt.alpha1e.event.BlocklyEvent;
import com.ubt.alpha1e.event.LessonEvent;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.LoginActivity;
import com.ubt.alpha1e.ui.RobotNetConnectActivity;
import com.ubt.alpha1e.ui.custom.IOnClickListener;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LessonTaskFinishDialog;
import com.ubt.alpha1e.ui.dialog.LessonTaskHelpDialog;
import com.ubt.alpha1e.ui.dialog.LessonTaskSuccessDialog;
import com.ubt.alpha1e.ui.dialog.ShareDialog;
import com.ubt.alpha1e.ui.dialog.SyncDownloadAlertDialog;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.CourseHelper;
import com.ubt.alpha1e.ui.helper.IActionsUI;
import com.ubt.alpha1e.ui.helper.IEditActionUI;
import com.ubt.alpha1e.ui.helper.IRemoteUI;
import com.ubt.alpha1e.ui.helper.MainHelper;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.ui.helper.RemoteHelper;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * @className BlocklyActivity
 *
 * @author wmma
 * @description Google Block逻辑编程页面
 * @date 2017/2/22
 * @update
 */


public class BlocklyActivity extends BaseActivity implements IEditActionUI, IActionsUI, DirectionSensorEventListener.ActionListener, BaseDiaUI, IRemoteUI,IUiListener,IWeiXinListener {

    private static final String TAG = "BlocklyActivity";
    public static String URL = "https://ubt.codemao.cn/";
    public static int REQUEST_CODE = 1000;
    public static int LOGIN_REQUEST_CODE = 1111;
    public static final int READ_ACTION_TIME_OUT_CODE = 2222;
    public static final int STOP_WALK_CONTINUE_CODE = 3333; //停止walk动作
    public static final int NET_REQUEST_CODE = 5555;

    public static final int DO_GOTO_NEXT_TASK = 6001;
    public static final int DO_TASK_FINISH = 6002;
    public static final int DO_LESSION_FINISH = 6003;
    public static final int DO_DEAL_LESSON_TASK_UI = 6004;
    public static final int DO_DOWNLOAD_UPDATE_PROGRESS = 6005;
    public static final int DO_DOWNLOAD_FAIL = 6006;
    public static final int DO_DOWNLOAD_SUCCESS = 6007;
    public static final int DO_NO_LAST_VERSION = 6008;
    public static final int DO_SHOW_LESSON_TASK = 6009;
    public static final int DO_DOWNLOAD_LESSON_TASK = 6010;
    public static final int DO_DOWNLOAD_BLOCKLY = 6011;
    public static final int DO_PLAY_AGAIN = 6012;
    public static final int DO_PLAY_SOUND_EFFECT_FINISH = 6013;


    private WebView mWebView;
    private BlocklyJsInterface mBlocklyJsInterface;

    private MyActionsHelper mMyActionsHelper;
    private List<String>  mActionList = new ArrayList<String>();
    private boolean mPlayFinish = false;

    private MainHelper mainHelper;
    private boolean mLowBatteryState = false;

    private DirectionSensorEventListener mListener;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private String mDirection = DeviceDirectionEnum.NONE.getValue();

    private ActionsEditHelper mActionsEditHelper;

    private SensorHelper mSensorHelper;  //传感器相关helper

    //机器人跌倒状态flag， 00-没有跌倒,01-向前跌倒趋势，02-向后跌倒趋势，03-已经跌倒
    private int mRobotFalldownState = -1;
    private int mInfraredDistance = 0;

    List<String> unSyncFileNames = new ArrayList<String>();

    private boolean checkWalk = false;
    private boolean isLoadFinish = false; //js加载完成
    private boolean isLoadActionFinish = false; //加载完action数据
    private boolean needContinue = false;  //循环执行行走动作flag
    private String  walkActionName = "";

    private SyncDownloadAlertDialog mSyncAlertDialog = null;

    private String mDir = FileTools.file_path+ "/voice";
    private MP3Recorder mRecorder;
    private MediaPlayer mediaPlayer;

    private boolean charging = false;

    //course
    private boolean isFromCourse = false;
    private LessonInfo mLessonInfo = null;
    private LessonTaskInfo mCurrentTaskInfo = null;
    private CourseHelper mCourseHelper = null;
    private List<LessonTaskInfo> mLessonTaskInfoList = null;

    private LessonTaskHelpDialog mLessonTaskHelpDialog = null;
    private LessonTaskFinishDialog mLessonTaskFinishDialog = null;
    private LessonTaskSuccessDialog mLessonTaskSuccessDialog = null;
    private ShareDialog mShareDialog = null;
    private RelativeLayout rlBlank = null;

    private int mChallengeFailCount = 0;//挑战失败次数
    private long mChallengeStartTime = 0; //挑战开始时间
    private int mSendCourseActionCount = 0;
    private int mPlayAudioCount = 0;   //播放音效总次数
    private int mPlayAudioNum = 0;     //播放音效次数
    private String mPlayAudioName = "" ;//播放音效名称
    private boolean isPlayLocalAudio = false; //是否播放本地音效
    private boolean hasShowLessonTask = false;
    private int playCount = 0;  //动作播放次数（目前在播放左转右转45用到）

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case READ_ACTION_TIME_OUT_CODE:
                    if(isBulueToothConnected()){
//                    ((RemoteHelper)mHelper).sendWalkFiles(unSyncFileNames);
                    }
                    dismissLoading();
                    break;
                case STOP_WALK_CONTINUE_CODE :
                    stopPlay();
                    break;
                case DO_PLAY_AGAIN:
                    if(LessonTaskView.getSimpleInstace() != null){
                        LessonTaskView.getSimpleInstace().playAgain();
                    }
                    break;
                case DO_GOTO_NEXT_TASK:
                    if(LessonTaskView.getSimpleInstace() != null){
                        LessonTaskView.getSimpleInstace().gotoNext();
                    }
                    break;
                case DO_TASK_FINISH:
                    unLockNext();
                    playTaskFinish(true);
                    showTaskFinish(mCurrentTaskInfo,msg.arg1,mChallengeFailCount);
                    break;
                case DO_LESSION_FINISH:
                    showLessonFinish();
                    break;
                case DO_DEAL_LESSON_TASK_UI:
                    dealLessonTaskUI((List<LessonTaskInfo>) msg.obj);
                    break;
                case DO_DOWNLOAD_UPDATE_PROGRESS:
                    if(mSyncAlertDialog != null && msg.arg1 < 100){
                        mSyncAlertDialog.setProgress(msg.arg1);
                    }
                    break;
                case DO_DOWNLOAD_FAIL:
                    dismissLoading();
                    break;
                case DO_DOWNLOAD_SUCCESS:
                    init();
                    break;
                case DO_NO_LAST_VERSION:
                    if(blockFilesExists()){
                        init();
                    }else {
                        dismissLoading();
                    }
                    break;
                case DO_SHOW_LESSON_TASK:
                    if(mLessonTaskInfoList != null && !hasShowLessonTask){
                        hasShowLessonTask = true;
                        LessonTaskView.getInstace(BlocklyActivity.this).setTaskData(mLessonInfo,mLessonTaskInfoList).show();
                    }
                    break;
                case DO_DOWNLOAD_LESSON_TASK:
                case DO_DOWNLOAD_BLOCKLY:
                    if(mSyncAlertDialog != null && mSyncAlertDialog.isShowing()){
                        mSyncAlertDialog.setCancelable(false);
                    }
                    break;
                case DO_PLAY_SOUND_EFFECT_FINISH:
                    if(mWebView != null){
                        UbtLog.d(TAG, "play sound or emoji finish!");
                        mWebView.loadUrl("javascript:playSoundEffectFinish()");
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockly);
        mWebView = (WebView) findViewById(R.id.blockly_webView);
        rlBlank = (RelativeLayout) findViewById(R.id.rl_blank);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER );
        isFromCourse = getIntent().getBooleanExtra(Constant.IS_FROM_COURSE,false);

        if(isFromCourse){
            hasShowLessonTask = false;
            mSendCourseActionCount = 0;
            rlBlank.setVisibility(View.VISIBLE);
            mLessonInfo = getIntent().getParcelableExtra(Constant.LESSON_INFO);
        }else {
            rlBlank.setVisibility(View.GONE);
        }

        UbtLog.d(TAG, "UserID => " + getCurrentUserID() + "    isFromCourse = " + isFromCourse);
//        mSyncAlertDialog = new SyncDownloadAlertDialog(BlocklyActivity.this)
//                .builder()
//                .setMsg(getStringResources("ui_init_blockly"))
//                .setImageResoure(R.drawable.data_loading)
//                .setCancelable(true,20);
//
//        requestUpdate();
        init();
    }

    /**
     * 初始化需要同步到行走目录的动作
     */
    private void initListData(){
        unSyncFileNames.add("forward_fast.hts");
        unSyncFileNames.add("forward_mid.hts");
        unSyncFileNames.add("forward_slow.hts");
        unSyncFileNames.add("back_fast.hts");
        unSyncFileNames.add("back_mid.hts");
        unSyncFileNames.add("back_slow.hts");
        unSyncFileNames.add("left_fast.hts");
        unSyncFileNames.add("left_mid.hts");
        unSyncFileNames.add("left_slow.hts");
        unSyncFileNames.add("right_fast.hts");
        unSyncFileNames.add("right_mid.hts");
        unSyncFileNames.add("right_slow.hts");
        unSyncFileNames.add("turn_left.hts");
        unSyncFileNames.add("turn_right.hts");
        unSyncFileNames.add("course_failer.hts");
        unSyncFileNames.add("course_success.hts");
    }

    /**
     * 初始化录音功能
     */
    private void initMP3Recorder(){
        if (mRecorder == null) {
            File file = new File(mDir);
            if(!file.exists()){
                file.mkdirs();
            }
            File mp3 = new File(mDir + File.separator + "tmp.mp3");
            mRecorder = new MP3Recorder(mp3);
        }
    }

    /**
     * 初始化音效文件
     */
    private void initVoiceVoice(){
        mCourseHelper.initCourseVoiceFile(BlocklyActivity.this);
    }

    /**
     * 开始录音
     */
    public void startMP3Recorder(){
        if(mRecorder != null){
            try {
                mRecorder.start();
            } catch (IOException e) {
                UbtLog.d(TAG, "" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    //停止录音
    public void stopMP3Recorder(){
        if(mRecorder != null) {
            mRecorder.stop();
        }
    }

    private void initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    if(isFromCourse && LessonTaskView.getSimpleInstace() != null){
                        LessonTaskView.getSimpleInstace().setMp3PlayFinish();
                    }

                    if(isPlayLocalAudio){
                        if(mPlayAudioNum < mPlayAudioCount){
                            mPlayAudioNum++;
                            playMP3(mPlayAudioName);
                        }else {
                            isPlayLocalAudio = false;
                            mPlayAudioCount = 0;
                            mPlayAudioNum = 0;
                            mPlayAudioName = "";

                            mHandler.sendEmptyMessage(DO_PLAY_SOUND_EFFECT_FINISH);
                        }
                    }
                }
            });
        }
    }

    public void playMP3Recorder(String mp3){
        String path = null;
        if(mp3.equals("")){
            path = mDir + File.separator+"tmp.mp3";
            final File file = new File(path);
        }else{
            path = mDir + File.separator + mp3;
        }
        playMP3(path);

    }

    public void playMP3(String mp3Path){
        //mp3Path = getBlocklyDir(BlocklyActivity.this) + "/voice/id_yellowPeople.wav";
        File file = new File(mp3Path);
        if (file.exists()) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(mp3Path);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "file is not exit", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopMP3Play(){
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                UbtLog.e("MP3播放", e.getMessage());
            }
        }
    }

    //删除用户录音的临时文件
    public void deleteMP3RecordFile(String name) {
        if(name.equals("") || name.equals(null)){
            FileTools.DeleteFile(new File(mDir + File.separator+"tmp.mp3"));
        }else{
            UbtLog.d(TAG, "deleteMP3RecordFile:" + name);
            FileTools.DeleteFile(new File(mDir + File.separator+name));
        }

    }

    //根据用户输入的名称保存录音文件
    public void saveMP3RecordFile(String fileName){
        boolean success = FileTools.renameFile(mDir + File.separator+"tmp.mp3", mDir + File.separator+fileName);
        if(isBulueToothConnected()){
            if(((AlphaApplication) getApplicationContext()).isAlpha1E()){
                if(((BaseHelper)mSensorHelper).hasConnectNetwork){
                    sendMusicToRobot();
                }else{
                    Intent intent = new Intent();
                    intent.setClass(this, RobotNetConnectActivity.class);
                    intent.putExtra(Constant.SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    startActivityForResult(intent,NET_REQUEST_CODE);

                }
            }
        }else{
            connectBluetooth();
        }
    }

    public List<String> readMP3RecordFiles(){
        return FileTools.readFiles(mDir);
    }

    @Override
    protected void onResume() {
        UbtLog.d(TAG, "onResume isFromCourse = " + isFromCourse );
        setCurrentActivityLable(BlocklyActivity.class.getSimpleName());
        super.onResume();

        if(mListener == null){
            mListener = new DirectionSensorEventListener(this);
            mSensorManager.registerListener(mListener,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void showLoading() {

        if(mSyncAlertDialog != null && !mSyncAlertDialog.isShowing())
        {
            mSyncAlertDialog.show();
        }

    }

    public void dismissLoading() {

        if(mSyncAlertDialog != null && mSyncAlertDialog.isShowing() && !this.isFinishing())
        {
            mSyncAlertDialog.display();
        }
    }

    private void initActionData(){

        if(isBulueToothConnected()){

            UbtLog.d(TAG, "hard =" + ((AlphaApplication) getApplicationContext()).getRobotHardVersion());

            isLoadActionFinish = false;
            checkWalk = true;
            UbtLog.d(TAG, "start doReadWalkAction");
            ((RemoteHelper)mHelper).doReadWalkAction();
            mHandler.sendEmptyMessageDelayed(READ_ACTION_TIME_OUT_CODE, 5000);
        }else {
            isLoadActionFinish = true;

            if(isLoadFinish && isLoadActionFinish){
                if(isFromCourse){
                    mHandler.sendEmptyMessage(DO_SHOW_LESSON_TASK);
                }
                dismissLoading();
            }
        }
    }

    private void init(){
        initWebView();
        initListData();
        initHelper();
        //initActionData();
        initVoiceVoice();
        initMP3Recorder();  //初始化MP3 recorder
        initMediaPlayer();

        if(isFromCourse){
            loadLessonTask();
        }else {
            UbtLog.d(TAG,"--initActionData--1");
            initActionData();
        }
    }

    private void initHelper(){
        if(mainHelper == null)
        {
            mainHelper = new MainHelper(this);
            mainHelper.RegisterHelper();
            mainHelper.doReadState();
        }

        if(mCourseHelper == null){
            mCourseHelper = new CourseHelper(this);
            mCourseHelper.RegisterHelper();
        }

        if(isBulueToothConnected()){
            if(mHelper == null){
                mHelper = new RemoteHelper(this, this);
                mHelper.RegisterHelper();
            }
            mMyActionsHelper = MyActionsHelper.getInstance(this);
            mMyActionsHelper.RegisterHelper();
            mMyActionsHelper.registerListeners(this);

            if(mActionsEditHelper == null){
                mActionsEditHelper = new ActionsEditHelper(this, this);
                mActionsEditHelper.RegisterHelper();
            }

            if(mSensorHelper == null) {
                mSensorHelper = new SensorHelper(this);
                mSensorHelper.RegisterHelper();
            }
        }
    }

    private void initWebView(){
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        //开发稳定后需去掉该行代码
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setUseWideViewPort(true);  //将图片调整到适合webview的大小
        mWebView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if(HttpAddress.WebServiceAdderss.contains(HttpAddress.WebAddressDevelop)){
                    //webview 忽略证书
                    handler.proceed();
                }else {
                    super.onReceivedSslError(view, handler, error);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isLoadFinish = true;
                if(!checkWalk){
                    dismissLoading();
                }

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isLoadFinish = true;
                //dismissLoading();
                mWebView.loadUrl("javascript:loadWebViewDisplay()");
                if(((AlphaApplication) getApplicationContext()).isAlpha1E() && isBulueToothConnected()){
                    UbtLog.d(TAG, "do start infrared!");
                    mSensorHelper.doReadInfraredSensor((byte)0x01);  //进入block如果连接蓝牙且是1E立马开始读取红外传感器数据
                    mSensorHelper.doReadGyroData((byte)0x01);
                    mSensorHelper.doReadAcceleration((byte)0x01);
                }

                if(isBulueToothConnected()){
                    mainHelper.doReadState();
                }

                UbtLog.d(TAG,"onPageFinished isLoadActionFinish = " + isLoadActionFinish);
                if(isLoadActionFinish){
                    if(isFromCourse){
                        mHandler.sendEmptyMessage(DO_SHOW_LESSON_TASK);
                    }
                    dismissLoading();
                }
            }

        };
        mWebView.setWebViewClient(webViewClient);

        mBlocklyJsInterface = new BlocklyJsInterface(BlocklyActivity.this);
        mWebView.addJavascriptInterface(mBlocklyJsInterface, "blocklyObj");
//        mWebView.loadUrl(URL);
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                Class<?> clazz = mWebView.getSettings().getClass();
                Method method = clazz.getMethod(
                        "setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(mWebView.getSettings(), true);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

//        mWebView.loadUrl("file:///"+getBlocklyPath(this));
        mWebView.loadUrl(URL);

        rlBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UbtLog.d(TAG,"setHeadTaskShow = doHiddenTaskDetail ---");
                if(LessonTaskView.getSimpleInstace() != null){
                    LessonTaskView.getSimpleInstace().doHiddenTaskDetail();
                }
                rlBlank.setVisibility(View.GONE);
            }
        });
    }

    private void loadLessonTask(){
        if(isFromCourse && mLessonInfo != null){
            mCourseHelper.loadLessonTask(mLessonInfo);
        }
    }

    /**
     * app让机器人执行js传递过来的工作，在连接蓝牙的状态下执行，未连接的状态下告知js播放完成继续下一个块的执行
     * @param name 机器人内置动作的名称
     * @param isWalk 是否行走动作
     * @param time 行走时长
     * @param isTurn 是否左转又转
     * @return
     */
    public void playRobotAction(String name, boolean isWalk, String time ,boolean isTurn){
        if(isBulueToothConnected()){
            if(isWalk){
                if(isTurn){
                    playCount = Integer.parseInt(time);
                    playCount--;
                }else {
                    playCount = 0;
                    needContinue = true;
                    if(mHandler.hasMessages(STOP_WALK_CONTINUE_CODE)){
                        mHandler.removeMessages(STOP_WALK_CONTINUE_CODE);
                    }
                    mHandler.sendEmptyMessageDelayed(STOP_WALK_CONTINUE_CODE, Integer.valueOf(time)*1000);
                }
                walkActionName = name;
            }else {
                playCount = 0;
                //当前播放动作的下标
                int pos = 0;
                for(int i = 0; i<mActionList.size(); i++){
                    String actionName = mActionList.get(i);
                    //blockly 已经把@#%去掉了，只能包含判断，不能做equals判断
                    if(actionName.contains(name)/*name.equals(actionName)*/){
                        name = actionName;//包含@#%的时候，把它还原
                        pos = i;
                        break;
                    }
                }
                UbtLog.d(TAG,"pos = " + pos + "     localSize = " + MyActionsHelper.localSize
                        + "   " + MyActionsHelper.myDownloadSize + "  " +  mActionList.size());

                /*for(int i = 0 ; i<mActionList.size();i++){
                    String actionName = mActionList.get(i);
                    UbtLog.d(TAG,"actionNameIndex = " + i + "   actionName = " + actionName);
                }*/

                if(pos < MyActionsHelper.localSize){
                    MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.Unkown;
                }else if(pos >= MyActionsHelper.localSize  &&  pos < (MyActionsHelper.myDownloadSize + MyActionsHelper.localSize) ){
                    MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_download_local;
                }else if(pos >= (MyActionsHelper.myDownloadSize + MyActionsHelper.localSize) ){
                    MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_new_local;
                }
            }
            UbtLog.d(TAG,"name = " + name + "   isWalk = " + isWalk + "     time = " + time);
            mMyActionsHelper.doPlayForBlockly(name, isWalk);
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:continueSteps()");
                }
            });

        }

    }


    public void doWalk(byte direct, byte speed, byte step) {
        if(mSensorHelper != null){
            mSensorHelper.doWalk(direct, speed, step);
        }
    }


    // Block停止执行，通知机器人停止动作

    public void stopPlay(){
        needContinue = false;
        playCount = 0;
        if(isBulueToothConnected() && mMyActionsHelper != null){
            mMyActionsHelper.stopPlayAction();
        }

        if(mSensorHelper != null){
            mSensorHelper.doStopWalk();
        }

        if(isPlayLocalAudio){
            stopMP3Play();
            isPlayLocalAudio = false;
            mPlayAudioCount = 0;
            mPlayAudioNum = 0;
            mPlayAudioName = "";
        }
    }

    public List<String> getActionList(){
        UbtLog.d(TAG, "getActionList:" + mActionList);
        return mActionList;
    }


    public boolean getPlayFinishState(){
        return mPlayFinish;
    }

    public boolean getBatteryState() {
        return mLowBatteryState;
    }

    public void checkBattery(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callJavascript(mLowBatteryState);
            }
        }, 7*1000);

    }

    public void callJavascript( boolean status){

        final String js = "javascript:eventJudgmentForApp(" + status + ")" ;
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                UbtLog.d(TAG, "callJavascript:" + js);
                mWebView.loadUrl(js);
            }
        });
    }

    public static final String LED_NORMAL = "normal";
    public static final String LED_FAST = "fast";
    public static final String LED_SLOW = "slow";
    public static final String LED_OFF = "off";
    public static final String RED = "red";
    public static final String ORANGE = "orange";
    public static final String YELLOW = "yellow";
    public static final String GREEN = "green";
    public static final String CYAN = "cyan";
    public static final String BLUE = "blue";
    public static final String PURPLE = "purple";
    public static final String WHITE = "white";

    LedColorEnum colorEnum = LedColorEnum.WHITE;
    public void setLedLight(String status){
        try {
            if(status != null){
                JSONObject jsonObject = new JSONObject(status);
                String state = jsonObject.getString("state");
                String color = jsonObject.getString("EyeColor");
                String time = jsonObject.getString("time");
                UbtLog.d(TAG, "state:" + state + "-color:" + color + "-time:" + time);
                byte [] params = new byte[5];
                if(state.equalsIgnoreCase(LED_NORMAL)){
                    params[0] = (byte)0x01;
                }else if(state.equalsIgnoreCase(LED_FAST)){
                    params[0] = (byte)0x02;
                }else if(state.equalsIgnoreCase(LED_SLOW)){
                    params[0] = (byte)0x03;
                }else if(state.equalsIgnoreCase(LED_OFF)){
                    params[0] = (byte)0x04;
                }

                if(color.equalsIgnoreCase(RED)) {
                    colorEnum = LedColorEnum.RED;
                }else if(color.equalsIgnoreCase(ORANGE)) {
                    colorEnum = LedColorEnum.ORANGE;
                }else if(color.equalsIgnoreCase(YELLOW)) {
                    colorEnum = LedColorEnum.YELLOW;
                }else if(color.equalsIgnoreCase(GREEN)) {
                    colorEnum = LedColorEnum.GREEN;
                }else if(color.equalsIgnoreCase(CYAN)) {
                    colorEnum = LedColorEnum.CYAN;
                }else if(color.equalsIgnoreCase(BLUE)) {
                    colorEnum = LedColorEnum.BLUE;
                }else if(color.equalsIgnoreCase(PURPLE)) {
                    colorEnum = LedColorEnum.PURPLE;
                }else if(color.equalsIgnoreCase(WHITE)) {
                    colorEnum = LedColorEnum.WHITE;
                }

                params[1] = ByteHexHelper.intToHexByte(colorEnum.getR());
                params[2] = ByteHexHelper.intToHexByte(colorEnum.getG());
                params[3] = ByteHexHelper.intToHexByte(colorEnum.getB());

                params[4] = ByteHexHelper.intToHexByte(Integer.valueOf(time));

                if(mSensorHelper != null && isBulueToothConnected()){
                    mSensorHelper.setLedLight(params);
                }

            }
        }catch (JSONException e){
            UbtLog.d(TAG, e.getMessage());
        }



    }


    @Override
    public void onLostBtCoon() {
        UbtLog.d(TAG, "onLostBtCoon");
        if(mWebView != null){
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    UbtLog.d(TAG, "Bluetooth disconnect checkBlueConnectState 3！");
                    mActionList.clear();
                    mWebView.loadUrl("javascript:checkBlueConnectState()");
                }
            });
        }

        super.onLostBtCoon();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMyActionsHelper != null){
            mMyActionsHelper.UnRegisterHelper();
            mMyActionsHelper.unRegisterListeners(this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if(mHandler.hasMessages(STOP_WALK_CONTINUE_CODE)){
            mHandler.removeMessages(STOP_WALK_CONTINUE_CODE);
        }
        mHandler.sendEmptyMessage(STOP_WALK_CONTINUE_CODE);
        stopMP3Play();

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "onDestroy");
        if(mMyActionsHelper != null){
            mMyActionsHelper.getDataType = MyActionsHelper.Action_type.Unkown;
        }
        if(mHandler.hasMessages(STOP_WALK_CONTINUE_CODE)){
            mHandler.removeMessages(STOP_WALK_CONTINUE_CODE);
        }

        destroyWebView();
        mSensorManager.unregisterListener(mListener);
        if(mActionsEditHelper != null){
            mActionsEditHelper.UnRegisterHelper();
        }
        UbtLog.d(TAG,"dismissLoading-1");
        dismissLoading();
        if(mSensorHelper != null){
            //退出界面停止红外数据上报
            if(isBulueToothConnected()){
                mSensorHelper.doReadInfraredSensor((byte)0x00);
                mSensorHelper.doReadGyroData((byte)0x00);
                mSensorHelper.doReadAcceleration((byte)0x00);
            }
            mSensorHelper.UnRegisterHelper();
        }

        if(mCourseHelper != null){
            mCourseHelper.UnRegisterHelper();
        }
        LessonTaskView.closeLessonTaskView();
    }

    public void destroyWebView() {

        if(mWebView != null) {
            mWebView.removeAllViews();
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.freeMemory();
            mWebView.destroy();
            mWebView = null; // Note that mWebView.destroy() and mWebView = null do the exact same thing
        }

    }

    @Override
    public void onReadActionsFinish(List<String> names) {
        UbtLog.d(TAG, "onReadActionsFinish checkWalk = " + checkWalk);
        //UbtLog.d(TAG, "onReadActionsFinish=" + names.toString());
        if(checkWalk){
            if(names.size() == 0){
                UbtLog.d(TAG, "开始同步所有行走动作");
                ((RemoteHelper)mHelper).sendWalkFiles(unSyncFileNames);
            }else{
                List<String> needSyncFileNames = new ArrayList<String>();
                for(int i =0; i <unSyncFileNames.size(); i++){
                    UbtLog.d(TAG, "unSyncFileNames:" + unSyncFileNames.get(i).substring(0, unSyncFileNames.get(i).length()-4));
                    if(names.contains(unSyncFileNames.get(i).substring(0, unSyncFileNames.get(i).length()-4))){

                    }else{
                        needSyncFileNames.add(unSyncFileNames.get(i));
                    }
                }
                if(needSyncFileNames.size() >0){
                    UbtLog.d(TAG, "开始同步行走动作:" + needSyncFileNames.size());
                    ((RemoteHelper)mHelper).sendWalkFiles(needSyncFileNames);
                }else{
                    UbtLog.d(TAG, "no need send");
                    mMyActionsHelper.getDataType = MyActionsHelper.Action_type.Unkown;
                    checkWalk = false;
                    UbtLog.d(TAG, "mMyActionsHelper doReadActions start 1");
                    mMyActionsHelper.doReadActions();
                }
            }

        }else{

            /*if(isFromCourse && mLessonInfo != null && mSendCourseActionCount < 2){
                List<String> needSyncFile = mCourseHelper.getNeedSyncAction(mLessonInfo, names, mLessonTaskInfoList);
                if(needSyncFile.size() > 0){
                    String fileDir = FileTools.course_task_cache + File.separator + mLessonInfo.courseId + "_" + mLessonInfo.lessonId;
                    UbtLog.d(TAG,"isFromCourse = " + isFromCourse + " mSendCourseActionCount = " + mSendCourseActionCount + "   needSyncFile = " + needSyncFile);
                    mSendCourseActionCount++;
                    mMyActionsHelper.getDataType = MyActionsHelper.Action_type.MY_COURSE;
                    ((RemoteHelper)mHelper).sendCourseFiles(fileDir, needSyncFile);
                    return;
                }
            }

            //获取机器人内置动作后，主动调用js方法通知蓝牙状态变化，让js重新请求获取内置动作。
            UbtLog.d(TAG, "names.size():" + names.size());
            if (names.size() > 0) {
//                mActionList.clear();
                mActionList = names;
                UbtLog.d(TAG, "read finish:" + mActionList.toString() + "read actions:" + names.size());
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        UbtLog.d(TAG, "checkBlueConnectState 1 ");
                        mWebView.loadUrl("javascript:checkBlueConnectState()");
                    }
                });
            }

            if(isLoadFinish){
                if(isFromCourse){
                    mHandler.sendEmptyMessage(DO_SHOW_LESSON_TASK);
                }
                UbtLog.d(TAG,"dismissLoading-2");
                dismissLoading();
            }
            isLoadActionFinish = true;*/
        }



    }



    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {
        Log.d(TAG, "notePlayStart!");
    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {
        UbtLog.d(TAG, "notePlayFinish!" + mSourceActionNameList.toString());

        if(needContinue || playCount > 0){
            UbtLog.d(TAG, "needContinue walk");
            playCount--;
            mMyActionsHelper.doPlayForBlockly(walkActionName, true);
        }else{
            //动作播放完成后，调用js方法通知执行下一个block块
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "notePlayFinish mWebView loadUrl!");
                    mWebView.loadUrl("javascript:continueSteps()");
                }
            });
        }



    }

    //跳转到连接蓝牙页面
    public void connectBluetooth() {
        UbtLog.d(TAG, "connectBluetooth");
        Intent intent = new Intent();
        intent.putExtra(com.ubt.alpha1e.base.Constant.BLUETOOTH_REQUEST, true);
        intent.setClass(BlocklyActivity.this, BluetoothconnectActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UbtLog.d(TAG, "onActivityResult BlockActivity");
        if(resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE){
//               showLoading();
                initHelper();
                initActionData();

                if(mSensorHelper != null && ((AlphaApplication) getApplicationContext()).isAlpha1E()){
                    UbtLog.d(TAG, "do start infrared!");
                    mSensorHelper.doReadInfraredSensor((byte)0x01);
                    mSensorHelper.doReadGyroData((byte)0x01);
                    mSensorHelper.doReadAcceleration((byte)0x01);
                }

      /*          mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:checkBlueConnectState()");
                    }
                });*/

                //上报机器人硬件版本号
                UbtLog.d(TAG, "hard =" + ((AlphaApplication) getApplicationContext()).getRobotHardVersion());
                if(mWebView != null) {
                    final String js = "javascript:getRobotModel(" + ((AlphaApplication) getApplicationContext()).getRobotHardVersion() + ")";
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.loadUrl(js);
                        }
                    });
                }

            }else if(requestCode == LOGIN_REQUEST_CODE){
                UbtLog.d(TAG, "LOGIN_REQUEST_CODE");
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:storeUserLoginInfo()");
                    }
                });

            }else if(requestCode == NET_REQUEST_CODE) {
                UbtLog.d(TAG, "联网成功");
                sendMusicToRobot();
            }
        }

    }

    /**
     * 以下实现加速度传感器的监听状态的改变
     * onShake 左右摇晃, onLeftUp 向左倾斜， onRightUp 向右倾斜
     * onTopUp 向上倾斜，onBottomUp 向下倾斜，onDirectionNone None
     */

    @Override
    public void onShake(long timeStamp, float[] value) {
        mDirection = DeviceDirectionEnum.SWING.getValue();
    }

    @Override
    public void onLeftUp() {
        mDirection = DeviceDirectionEnum.LEFT.getValue();
//        callSensorBack();
    }

    @Override
    public void onRightUp() {
        mDirection = DeviceDirectionEnum.RIGHT.getValue();
    }

    @Override
    public void onTopUp() {
        mDirection = DeviceDirectionEnum.UP.getValue();
    }

    @Override
    public void onBottomUp() {
        mDirection = DeviceDirectionEnum.DOWN.getValue();
    }

    @Override
    public void onDirectionNone() {
        mDirection = DeviceDirectionEnum.NONE.getValue();
    }

    public String getmDirection(){
        return mDirection;
    }

    /****************end***********************/

    @Override
    public void onEventRobot(RobotEvent event){
        super.onEventRobot(event);

        if(event.getEvent() == RobotEvent.Event.UPDATING_POWER){
            //UbtLog.d(TAG, "Event.UPDATING_POWER");
            charging = false;
            int power = event.getPower(); //获取电池电量
            if(mWebView != null && isLoadFinish){
                mWebView.loadUrl("javascript:uploadLowPowerData(" + power + ")");
            }
            if(power < 10){
                mLowBatteryState = true;
            }else{
                mLowBatteryState = false;
            }
        }else if(event.getEvent() == RobotEvent.Event.CHARGING){
            UbtLog.d(TAG, "Event.CHARGING");
            charging = true;
            if(mWebView != null && isLoadFinish){
                //mWebView.loadUrl("javascript:uploadLowPowerData(-1)");
                mWebView.loadUrl("javascript:uploadLowPowerData(100)");
            }

        }else if(event.getEvent() == RobotEvent.Event.DISCONNECT){
            //处理机器人断开事件
            if(mWebView != null){
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mActionList.clear();
                        mWebView.loadUrl("javascript:checkBlueConnectState()");
                    }
                });
            }

        }else if(event.getEvent() == RobotEvent.Event.CONNECT_SUCCESS){
            //自动连接成功后初始化蓝牙和block相关数据
            initHelper();
            initActionData();

            if(mSensorHelper != null && ((AlphaApplication) getApplicationContext()).isAlpha1E()){
                UbtLog.d(TAG, "do start infrared!");
                mSensorHelper.doReadInfraredSensor((byte)0x01);
                mSensorHelper.doReadGyroData((byte)0x01);
                mSensorHelper.doReadAcceleration((byte)0x01);
            }


            //上报机器人硬件版本号
            UbtLog.d(TAG, "hard =" + ((AlphaApplication) getApplicationContext()).getRobotHardVersion());
            if(mWebView != null) {
                final String js = "javascript:getRobotModel(" + ((AlphaApplication) getApplicationContext()).getRobotHardVersion() + ")";
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl(js);
                    }
                });
            }
        }
    }

    @Subscribe
    public void onEventBlock(BlocklyEvent event){
        if(event.getType() == BlocklyEvent.CALL_JAVASCRIPT){
            final String js = (String)event.getMessage();
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(js);
                }
            });
        }else if(event.getType() == BlocklyEvent.CALL_ROBOT_POWER_DOWN){
            //执行机器人掉电操作
            mActionsEditHelper.doLostPower();
        }else if(event.getType() == BlocklyEvent.CALL_ROBOT_READ_ANGLE){
            UbtLog.d(TAG, "doReadAllEng");
            mActionsEditHelper.doReadAllEng();
        }else if(event.getType() == BlocklyEvent.CALL_ROBOT_PLAY_READ_ACTION) {
            doPlayCurrentFrames();
        }else if(event.getType() == BlocklyEvent.CALL_START_INFRARED){
            mSensorHelper.doReadInfraredSensor((byte)0x01);
        }else if(event.getType() == BlocklyEvent.CALL_GET_INFRARED_DISTANCE) {
            mInfraredDistance = ByteHexHelper.byteToInt((byte)event.getMessage());
            if(mWebView != null && isLoadFinish){
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:uploadInfraredSensorDistance(" + mInfraredDistance + ")");
                    }
                });
            }
            updateInfraredObserver(mInfraredDistance);
            if(infraredParams != ""){
                callInfraredEvent(infraredParams, mInfraredDistance);
            }
        }else if(event.getType() == BlocklyEvent.CALL_ROBOT_FALL_DOWN_STATE){
            mRobotFalldownState = ByteHexHelper.byteToInt((byte)event.getMessage());
            if(mRobotFalldownState == 3){
                callJavascript(true);
            }else{
                callJavascript(false);
            }
        }else if(event.getType() == BlocklyEvent.CALL_ROBOT_PLAY_SOUND_FINISH){

            mHandler.sendEmptyMessage(DO_PLAY_SOUND_EFFECT_FINISH);
        }else if(event.getType() == BlocklyEvent.CALL_ROBOT_GET_GYROSCOPE_DATA){
            //给js上报陀螺仪数据
            final String gyroData = new String((byte[])(event.getMessage()));
            if(mWebView != null && isLoadFinish) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:uploadGyroscopeData(" + gyroData + ")");
                    }
                });
            }
        }else if(event.getType() == BlocklyEvent.CALL_READ_WALK_FILE_FINISH){
            UbtLog.d(TAG, "action event:" + event.getMessage().toString());

        }else if(event.getType() == BlocklyEvent.CALL_ROBOT_FALL_DOWN) {
            UbtLog.d(TAG, "robot fall down and stop block run!");
            if(isLoadFinish && mWebView != null){
                mWebView.loadUrl("javascript:listenRobotStopEvent()");
            }
        }else if(event.getType() == BlocklyEvent.CALL_TAPPED_ROBOT_HEAD) {
            UbtLog.d(TAG, "tapped robot head and stop block run!");
            if(isLoadFinish && mWebView != null){
                mWebView.loadUrl("javascript:listenRobotStopEvent()");
            }
        }
    }

    @Subscribe
    public void onActionEvent(ActionEvent event){
        if(event.getEvent() == ActionEvent.Event.READ_ACTIONS_FINISH){

            List<String> names = event.getActionsNames();
            UbtLog.d(TAG,"READ_ACTIONS_FINISH = " + names.size());
            if(isFromCourse && mLessonInfo != null && mSendCourseActionCount < 2){
                List<String> needSyncFile = mCourseHelper.getNeedSyncAction(mLessonInfo, names, mLessonTaskInfoList);
                if(needSyncFile.size() > 0){
                    String fileDir = FileTools.course_task_cache + File.separator + mLessonInfo.courseId + "_" + mLessonInfo.lessonId;
                    UbtLog.d(TAG,"isFromCourse = " + isFromCourse + " mSendCourseActionCount = " + mSendCourseActionCount + "   needSyncFile = " + needSyncFile);
                    mSendCourseActionCount++;
                    mMyActionsHelper.getDataType = MyActionsHelper.Action_type.MY_COURSE;
                    ((RemoteHelper)mHelper).sendCourseFiles(fileDir, needSyncFile);
                    return;
                }
            }

            //获取机器人内置动作后，主动调用js方法通知蓝牙状态变化，让js重新请求获取内置动作。
            UbtLog.d(TAG, "names.size():" + names.size());
            if (names.size() > 0) {
                mActionList = names;
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        UbtLog.d(TAG, "checkBlueConnectState 1 ");
                        mWebView.loadUrl("javascript:checkBlueConnectState()");
                    }
                });
            }

            if(isLoadFinish){
                if(isFromCourse){
                    mHandler.sendEmptyMessage(DO_SHOW_LESSON_TASK);
                }
                UbtLog.d(TAG,"dismissLoading-2");
                dismissLoading();
            }
            isLoadActionFinish = true;

            UbtLog.d(TAG, "READ_ACTIONS_FINISH:" + mActionList.toString()) ;
        }
    }

    @Subscribe
    public void onEventLesson(LessonEvent event){
        if(event.getEvent() == LessonEvent.Event.DO_GET_LESSON_TASK){

            Message msg = new Message();
            msg.what = DO_DEAL_LESSON_TASK_UI;
            msg.obj = event.getLessonTaskInfoList();
            mHandler.sendMessage(msg);

        }else if(event.getEvent() == LessonEvent.Event.DO_DOWNLOAD_LESSON_TASK){
            mHandler.sendEmptyMessage(DO_DOWNLOAD_LESSON_TASK);
        }
    }

    /**
     * 根据数据处理UI
     * @param lessonTaskInfoList 任务列表
     */
    private void dealLessonTaskUI(List<LessonTaskInfo> lessonTaskInfoList){
        if(lessonTaskInfoList != null && !lessonTaskInfoList.isEmpty()){

            //本地数据遭删除，同步服务器数据
            for (int i = 0; i< mLessonInfo.taskDown; i++){
                LessonTaskInfo taskInfo = lessonTaskInfoList.get(i);
                if(taskInfo.is_unlock == 0 || taskInfo.status == 0){
                    taskInfo.is_unlock = 1;
                    taskInfo.status = 1;
                    mCourseHelper.updateLessonTask(taskInfo);
                }
            }

            //自动解锁下一个
            if(lessonTaskInfoList.size() > mLessonInfo.taskDown){
                LessonTaskInfo taskInfo = lessonTaskInfoList.get(mLessonInfo.taskDown);
                if(taskInfo.is_unlock == 0){
                    taskInfo.is_unlock = 1;
                    mCourseHelper.updateLessonTask(taskInfo);
                }
            }

            mLessonTaskInfoList = lessonTaskInfoList;

            //LessonTaskView.getInstace(this).setTaskData(mLessonInfo,lessonTaskInfoList).show();
        }
        initActionData();
    }

    /**
     * 任务切换时刷新blockly程序
     * @param taskInfo 任务对象
     */
    public void refreshBlocklyTaskData(LessonTaskInfo taskInfo){
        mChallengeFailCount = 0;
        mChallengeStartTime = System.currentTimeMillis();

        mCurrentTaskInfo = taskInfo;
        String taskPath = getBlocklyDir(BlocklyActivity.this) + File.separator + "Blockly/engine/courses/" + mLessonInfo.lessonId + File.separator + taskInfo.task_result;

        File file = new File(taskPath);
        UbtLog.d(TAG,"taskPath == " + taskPath + "  file = " + file.exists());
        if(file.exists()){
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:setTaskPath('"+ mCurrentTaskInfo.lesson_id + File.separator + mCurrentTaskInfo.task_result +"')");
                }
            });

        }else {

            //再拷贝一遍
            String filePath = FileTools.course_task_cache + File.separator + mLessonInfo.courseId + "_" + mLessonInfo.lessonId;
            mCourseHelper.copyTaskFileToBlocklyProject(mLessonInfo,filePath);
            UbtLog.d(TAG,"taskPath => " + taskPath + "  file = " + file.exists());
            if(file.exists()){
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:setTaskPath('"+ mCurrentTaskInfo.lesson_id + File.separator + mCurrentTaskInfo.task_result +"')");
                    }
                });
            }
        }

    }

    private void callInfraredEvent(String params, int distance){
        try {
            JSONObject jsonObject = new JSONObject(params);
            String value = jsonObject.getString("value");
            String operate = jsonObject.getString("symbol");
            boolean result = SensorObserver.Comparation.compare(String.valueOf(distance), operate, value);
            callJavascript(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void updateInfraredObserver(int distance){
        QueryResult rb = new QueryResult();
        List<RobotSensor> infrared = new ArrayList<RobotSensor>();
        RobotSensor sensor = new RobotSensor(RobotSensor.SensorType.INFRARED, 1, distance);
        infrared.add(sensor);
        rb.setInfrared(infrared);
        SensorObservable.getInstance().setData(rb);
    }

    //机器人上报事件后notify Observable,该方法需要在机器人上报事件的eventbus事件中调用
    private void updateRobotObserver(int event_code){
        QueryResult rb = new QueryResult();
        List<RobotSensor> robot = new ArrayList<RobotSensor>();
        RobotSensor sensor = new RobotSensor(RobotSensor.SensorType.ROBOT,1, event_code);
        robot.add(sensor);
        rb.setRobot(robot);
        SensorObservable.getInstance().setData(rb);
    }

    //机器人加速度全局事件notify Observable
    private void updateAccelerationObserver(int value) {
        //TODO 待完成
        QueryResult rb = new QueryResult();
        List<RobotSensor> acceleration = new ArrayList<RobotSensor>();
        RobotSensor sensor = new RobotSensor(RobotSensor.SensorType.ACCELERATION,1, value);
        acceleration.add(sensor);
        rb.setRobot(acceleration);
        SensorObservable.getInstance().setData(rb);
    }

    private String infraredParams = "";
    public void startInfrared(String params){
        infraredParams = params;
    }


    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

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
    public void onReadSettingItem(List<RemoteItem> items) {

    }

    @Override
    public void onReadRemoteRoleFinish(List<RemoteRoleInfo> mRemoteRoles) {

    }

    @Override
    public void onAddRemoteRole(boolean isSuccess, int roleId) {

    }

    @Override
    public void onUpdateRemoteRole(boolean isSuccess, RemoteRoleInfo roleInfo) {

    }

    @Override
    public void onDelRemoteRole(boolean isSuccess, RemoteRoleInfo roleInfo) {

    }

    @Override
    public void onAddRemoteRoleActions(boolean isSuccess, int roleId) {

    }

    @Override
    public void onDelRemoteHeadPrompt(boolean isSuccess) {

    }

    @Override
    public void onSendFileBusy() {

    }

    @Override
    public void onSendFileError() {

    }

    @Override
    public void onSendFileFinish(int pos) {

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
    public void onSendFileFinish(boolean isSuccess) {
        UbtLog.d(TAG, "onSendFileFinish:" + isSuccess);
        if(isLoadFinish && isLoadActionFinish){
            dismissLoading();
        }
        mMyActionsHelper.getDataType = MyActionsHelper.Action_type.Unkown;
        checkWalk = false;
        UbtLog.d(TAG, "mMyActionsHelper doReadActions start 2");
        mMyActionsHelper.doReadActions();
    }

    @Override
    public void onPlayActionFileNotExist() {

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
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {

    }

    @Override
    public void onChangeNewActionsFinish() {

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


    /**
     * get user info
     */

    public long getCurrentUserID(){
        UserInfo userInfo = ((AlphaApplication)(this.getApplicationContext())).getCurrentUserInfo();
        if(userInfo != null){
            return userInfo.userId;
        }else{
            return 0;
        }
    }

    public void callSensorBack(final String branchId){
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                String js = "javascript:sensorCallback(" + branchId + ")";
                mWebView.loadUrl(js);
            }
        });

    }

    //机器人是否处于跌倒状态,该方法待机器人本体完成后完善。
    public void checkRobotFall() {
        if(mSensorHelper != null) {
            mSensorHelper.doReadRobotFallState();
        }
    }

    private boolean fallDown = false;
    public boolean robotFallDown() {
        if(mRobotFalldownState == 3){
            fallDown = true;
        }else{
            fallDown = false;
        }

        return fallDown;
    }

    /**
     * 播放音效
     * @param params  js传递来的json数据
     */

    public void playSoundAudio(String params) {
        //{"type":"animal", "key":"id_elephant.wav", "description":"大象"，"playcount":3}
//        if(((AlphaApplication) getApplicationContext()).isAlpha1E()){
            if(mSensorHelper != null){
                mSensorHelper.playSoundAudio(params);
            }
//        }else {
//            try {
//                UbtLog.d(TAG,"params = " + params);
//                JSONObject jsonObject = new JSONObject(params);
//                String audioName = jsonObject.getString("filename");
//                mPlayAudioCount = jsonObject.getInt("playcount");
//
//                if(mPlayAudioCount > 0){
//                    isPlayLocalAudio = true;
//                    mPlayAudioNum = 1;
//                    mPlayAudioName = getBlocklyDir(BlocklyActivity.this) + "/voice/" + audioName;
//                    playMP3(mPlayAudioName);
//                }else {
//                    isPlayLocalAudio = false;
//                    mPlayAudioCount = 0;
//                    mPlayAudioNum = 0;
//                    mPlayAudioName = "";
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

    }

    /**
     * 显示表情
     * @param params
     */
    public void showEmoji(String params) {
        if(mSensorHelper != null){
            mSensorHelper.showEmoji(params);
        }
    }

    /**
     * 停止音效和眼睛灯光
     */
    public void stopPlayAudio() {
        if(mSensorHelper != null){
            mSensorHelper.stopPlayAudio();
            mSensorHelper.stopLedLight();
            mSensorHelper.stopPlayEmoji();
        }

        if(isPlayLocalAudio){
            stopMP3Play();
            isPlayLocalAudio = false;
            mPlayAudioCount = 0;
            mPlayAudioNum = 0;
            mPlayAudioName = "";
        }
    }

    /**
     * 播放TTS语音完成
     */
    public void playTTSFinish(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:playTTSFinish()");
                    }
                }) ;
            }
        }, 3000);
    }


    private List<Map<String, Object>> lst_actions_adapter_data =  new ArrayList<Map<String, Object>>();
    @Override
    public void onReadEng(byte[] eng_angle) {
        String angles = "";
        for (int i = 0; i < eng_angle.length; i++) {
            angles += eng_angle[i] + "#";
        }
        FrameActionInfo info = new FrameActionInfo();
        info.eng_angles = angles;
        info.eng_time = 1000;
        info.totle_time = 1000;

        Map map = new HashMap<String, Object>();
        map.put(ActionsEditHelper.MAP_FRAME, info);
        map.put(ActionsEditHelper.MAP_FRAME_NAME,  "block");
        map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time + "ms");
        lst_actions_adapter_data.add(map);


    }

    @Override
    public void onChangeActionFinish() {

    }

    /**
     * 播放当前新建动作的帧
     */
    private void doPlayCurrentFrames() {
        if (mActionsEditHelper.getNewPlayerState() == NewActionPlayer.PlayerState.PLAYING) {
            mActionsEditHelper.doActionCommand(ActionsEditHelper.Command_type.Do_Stop,
                    getEditingActions());


        } else {
            mActionsEditHelper.doActionCommand(ActionsEditHelper.Command_type.Do_play,
                    getEditingActions());


        }

    }

    //定义当前新建的动作
    public NewActionInfo mCurrentNewAction = new NewActionInfo();

    /**
     * 获取当前编辑的动作
     * @return
     */
    private NewActionInfo getEditingActions() {

        List<FrameActionInfo> frames = new ArrayList<FrameActionInfo>();
        frames.add(FrameActionInfo.getDefaultFrame());
        for (int i = 0; i < lst_actions_adapter_data.size(); i++) {
            frames.add(((FrameActionInfo) lst_actions_adapter_data.get(i).get(
                    ActionsEditHelper.MAP_FRAME)));
        }
        mCurrentNewAction.frameActions = frames;
        return mCurrentNewAction;
    }

    /**
     * 判断blockly版本号文件是否存在
     * @return
     */
    public boolean blockFilesExists() {
        try {
            File f = new File(getBlocklyDir(this) + "/Blockly/version.txt");
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    /**
     * 获取blockly的存储路径
     * @param context
     * @return
     */
    public static String getBlocklyDir(Context context){
        String cacheDir = Environment.getExternalStorageDirectory().getPath()
                + File.separator + "Android"
                + File.separator + "data"
                + File.separator + context.getPackageName()
                + File.separator + "files" + File.separator
                + "data/blockly";

        return cacheDir;
    }


    @Override
    public void onPlaying() {

    }

    @Override
    public void onPausePlay() {

    }

    @Override
    public void onFinishPlay() {
        UbtLog.d(TAG, "onFinishPlay");
    }

    @Override
    public void onFrameDo(int index) {

    }

    @Override
    public void notePlayChargingError() {

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    /**
     * 跳转登录页面
     */
    public void login() {
        LoginActivity.launchActivity(BlocklyActivity.this,true, LOGIN_REQUEST_CODE);
    }

    /**
     * 获取当前blockly的版本号
     * @param context
     * @return
     */
    private String readBlocklyLocalVersion(Context context){
        File versionFile = new File(getBlocklyDir(context) + File.separator + "Blockly/version.txt");
        String vervion = "";
        if(versionFile.exists()){
            vervion = FileTools.readFileOneLine(versionFile.getPath());
        }
        return vervion;
    }

    /**
     * 获取加载页面路径 课程（course.html） 普通编程（index.html）
     * @param context
     * @return
     */
    public String getBlocklyPath(Context context){
        String cacheDir = getBlocklyDir(context) + File.separator + "Blockly/project/";

        if(isFromCourse){
            cacheDir += "course.html";
        }else {
            cacheDir += "index.html";
        }

        UbtLog.d(TAG,"cacheDir = " + cacheDir);
        return cacheDir;
    }

    /**
     * 发送音乐到机器人
     */
    private void sendMusicToRobot() {
        UbtLog.d(TAG, "sendMusicToRobot");
    }

    /**
     * 判断是否边冲边玩打开
     * @return
     */
    public boolean checkCharge() {
        if(isBulueToothConnected()){
            UbtLog.d(TAG, "charge:" + mMyActionsHelper.getChargingState() + "isPlayCharging:" + SettingHelper.isPlayCharging(this));
            if(charging && !SettingHelper.isPlayCharging(this)){
                Toast.makeText(this, getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * 是否第一次进入课时任务（blockly调用）
     * @return
     */
    public boolean isFirstTimeTask(){

        if(isFromCourse && mCurrentTaskInfo != null){
            if (BasicSharedPreferencesOperator
                    .getInstance(BlocklyActivity.this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                    .doReadSync(BasicSharedPreferencesOperator.IS_FIRST_USE_TASK + mCurrentTaskInfo.lesson_id + "_" + mCurrentTaskInfo.task_id)
                    .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_TASK_FALSE)) {
                return false;
            }

            BasicSharedPreferencesOperator.getInstance(BlocklyActivity.this,
                    BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                    BasicSharedPreferencesOperator.IS_FIRST_USE_TASK + mCurrentTaskInfo.lesson_id + "_" + mCurrentTaskInfo.task_id,
                    BasicSharedPreferencesOperator.IS_FIRST_USE_TASK_FALSE,
                    null, -1);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 显示课程帮助按键(blockly程序调用)
     */
    public void showLessonTaskHelpDes(){
        if(mLessonTaskHelpDialog == null){
            mLessonTaskHelpDialog = new LessonTaskHelpDialog(BlocklyActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setPositiveButton(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
        }

        if(mCurrentTaskInfo != null){
            mLessonTaskHelpDialog.setMsg(mCurrentTaskInfo.task_help);
            mLessonTaskHelpDialog.show();
        }
    }

    /**
     * 显示单个任务完成
     * @param taskInfo 完成任务对象
     * @param challengeTime 完成任务所需的时间
     * @param challengeFailCount 完成任务失败的次数
     */
    private void showTaskFinish(LessonTaskInfo taskInfo,int challengeTime,int challengeFailCount ){
        if(mLessonTaskFinishDialog == null){
            mLessonTaskFinishDialog = new LessonTaskFinishDialog(BlocklyActivity.this)
                    .builder()
                    .setCancelable(false)
                    .setNegativeButton(getStringResources("ui_task_play_again"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mChallengeFailCount = 0;
                            mHandler.sendEmptyMessage(DO_PLAY_AGAIN);
                        }
                    }).setPositiveButton(getStringResources("ui_task_play_continue"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mChallengeFailCount = 0;
                            mHandler.sendEmptyMessage(DO_GOTO_NEXT_TASK);

                        }
                    });
        }

        mLessonTaskFinishDialog.setData(taskInfo,challengeTime,challengeFailCount);
        mLessonTaskFinishDialog.show();
    }

    /**
     * 显示课时完成页面
     */
    private void showLessonFinish(){

        List<LessonTaskResultInfo> resultInfoList = mCourseHelper.getLessonTaskResult(mLessonInfo.courseId, mLessonInfo.lessonId);

        mLessonTaskSuccessDialog = new LessonTaskSuccessDialog(BlocklyActivity.this)
                .builder()
                .setCancelable(true)
                .setData(mLessonInfo,resultInfoList)
                .setShowOffButton(new IOnClickListener() {

                    @Override
                    public void onClick(Object... objects) {
                        String screenShotPath = (String) objects[0];
                        UbtLog.d(TAG, "screenShotPath = " + screenShotPath);
                        showShareDialog(screenShotPath);
                    }

                }).setContinueChallengeButton( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BlocklyActivity.this.finish();
                    }
                });
        mLessonTaskSuccessDialog.show();
    }

    /**
     * 显示分享对话框
     * @param shareImagePath 分享图片路径
     */
    private void showShareDialog(String shareImagePath){
        mShareDialog = new ShareDialog(BlocklyActivity.this)
                .builder()
                .setCancelable(false)
                .setShareImage(shareImagePath)
                .setNegativeButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

        mShareDialog.show();
    }

    /**
     * 播放任务完成动作以及MP3
     * @param isSuccess 答案对与不对
     */
    private void playTaskFinish(boolean isSuccess){
        String voicePath;
        String actionName;
        if(isSuccess){
            voicePath = getBlocklyDir(BlocklyActivity.this) + "/voice/course_success.mp3";
            actionName = "course_success";
        }else {
            voicePath = getBlocklyDir(BlocklyActivity.this) + "/voice/course_failer.mp3";
            actionName = "course_failer";
        }

        UbtLog.d(TAG,"playTaskFinish = " + actionName );
        mMyActionsHelper.doPlayForBlockly(actionName, true);
        playMP3(voicePath);

    }

    /**
     * 显示课程空白页，用来点击收下课程详情
     */
    public void showOrHiddenBlankPage(boolean isShow){
        if(isShow){
            rlBlank.setVisibility(View.VISIBLE);
        }else {
            rlBlank.setVisibility(View.GONE);
        }

    }

    /**
     * 接受任务允许结果
     * @param isSuccess true 闯关成功，否则失败
     */
    public void receiveTaskRunResult(boolean isSuccess){
        if(isSuccess){

            int challengeTime = (int)(System.currentTimeMillis() - mChallengeStartTime)/1000;
            mCourseHelper.updateLessonTaskResult(mLessonInfo.courseId,mCurrentTaskInfo,challengeTime,mChallengeFailCount);

            mCurrentTaskInfo.status = 1;
            mCourseHelper.updateLessonTask(mCurrentTaskInfo);
            updateLessonTown();

            UbtLog.d(TAG,"challengeSuccessTime = " + challengeTime + "   mChallengeFailCount = " + mChallengeFailCount);

            if(isLastTask()){
                mHandler.sendEmptyMessage(DO_LESSION_FINISH);
            }else {
                Message msg = new Message();
                msg.what = DO_TASK_FINISH;
                msg.arg1 = challengeTime;
                mHandler.sendMessage(msg);

            }
        }else {
            mChallengeFailCount++;
            playTaskFinish(false);
            Toast.makeText(this, getStringResources("ui_task_chanllege_fail"), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取当前课程指引给blockly
     * @return
     */
    public String getTaskGuideToBlockly(){
        if(mLessonInfo != null && mCurrentTaskInfo != null && mLessonTaskInfoList != null){

            int index = 0;
            LessonTaskInfo taskInfo = null;
            for(int i = 0;i<mLessonTaskInfoList.size(); i++){
                taskInfo = mLessonTaskInfoList.get(i);
                if(taskInfo.task_id == mCurrentTaskInfo.task_id){
                    index = i;
                    break;
                }
            }
            String lessonGuide = mLessonInfo.lessonGuide;
            lessonGuide = lessonGuide.replaceAll("'","&&&").replaceAll("\"","&&&&");
            return "{\"taskIndex\":" + index +",\"taskIntroduce\":\"" + lessonGuide + "\"}";
        }
        return "";
    }

    /**
     * 解锁下一个任务
     */
    public void unLockNext(){

        LessonTaskInfo taskInfo = null;
        for(int i = 0;i<mLessonTaskInfoList.size(); i++){
            taskInfo = mLessonTaskInfoList.get(i);
            if(taskInfo.task_id == mCurrentTaskInfo.task_id){
                UbtLog.d(TAG,"unLockNext = " +((i + 1) == mLessonTaskInfoList.size()) + "   i = " + i + "  size = " + mLessonTaskInfoList.size());
                if((i + 1) != mLessonTaskInfoList.size() ){
                    LessonTaskInfo nextTaskInfo = mLessonTaskInfoList.get(i + 1);
                    nextTaskInfo.is_unlock = 1;
                    mCourseHelper.updateLessonTask(nextTaskInfo);

                    if(LessonTaskView.getSimpleInstace() != null){
                        LessonTaskView.getSimpleInstace().unLockNext(nextTaskInfo);
                    }
                }
            }
        }
    }

    /**
     * 显示课程任务导航栏
     * @param isShow true为显示，false为不显示
     */
    public void setHeadTaskShow(final boolean isShow){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(isFromCourse){
                    UbtLog.d(TAG,"setHeadTaskShow = " + isShow);
                    if(LessonTaskView.getSimpleInstace() != null){
                        LessonTaskView.getSimpleInstace().setShowOrHidden(isShow);
                        if(isShow){
                            if(LessonTaskView.getSimpleInstace().isShowLessonTaskDetail()){
                                rlBlank.setVisibility(View.VISIBLE);
                            }else {
                                rlBlank.setVisibility(View.GONE);
                            }
                        }else {
                            rlBlank.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    /**
     * 更新课时当前闯关数
     */
    private void updateLessonTown(){
        LessonTaskInfo taskInfo = null;
        for(int i = 0;i<mLessonTaskInfoList.size(); i++){
            taskInfo = mLessonTaskInfoList.get(i);
            if(taskInfo.task_id == mCurrentTaskInfo.task_id){
                if(mLessonInfo.taskDown < ( i+1 )){
                    mLessonInfo.taskDown = i+1;
                    mCourseHelper.updateLesson(mLessonInfo);
                }
            }
        }
    }

    /**
     * 判断是否当前课时的最后一个任务
     * @return
     */
    private boolean isLastTask(){
        LessonTaskInfo taskInfo = null;
        boolean isLast = false;
        for(int i = 0;i<mLessonTaskInfoList.size(); i++){
            taskInfo = mLessonTaskInfoList.get(i);
            if(taskInfo.task_id == mCurrentTaskInfo.task_id){
                UbtLog.d(TAG,"unLockNext = " +((i + 1) == mLessonTaskInfoList.size()));
                if((i + 1) == mLessonTaskInfoList.size() ){//最后一个
                    isLast = true;
                    break;
                }
            }
        }
        return isLast;
    }

    /**
     * 请求判断是否有blockly程序更新
     *  传当前版本过去，如果有返回URL，则表示有更新，否则当前为最版本，无更新版本
     */
    public void requestUpdate() {

        showLoading();

        String updateUrl = HttpAddress.WebBlocklyUpdateAdderss + readBlocklyLocalVersion(BlocklyActivity.this);
        UbtLog.d(TAG,"updateUrl = " + updateUrl);

        /*if(blockFilesExists()){
            mHandler.sendEmptyMessage(DO_NO_LAST_VERSION);
            return;
        }*/

        OkHttpClientUtils.getJsonByGetRequest(updateUrl, -1).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "onError:" + e.getMessage());
                mHandler.sendEmptyMessage(DO_NO_LAST_VERSION);
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "response:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    String url = "";
                    if(code == 1){
                        url = jsonObject.getString("msg");
                    }

                    UbtLog.d(TAG,"url =>> " + url);
                    if(!TextUtils.isEmpty(url)){
                        downloadBlockly(url);
                    }else {
                        mHandler.sendEmptyMessage(DO_NO_LAST_VERSION);
                    }

                } catch (JSONException e) {
                    UbtLog.e(TAG,e.getMessage());
                    mHandler.sendEmptyMessage(DO_NO_LAST_VERSION);
                }
            }
        });
    }

    /**
     * 下载blockly程序
     * @param url 下载的URL
     */
    private void downloadBlockly(String url){

        mHandler.sendEmptyMessage(DO_DOWNLOAD_BLOCKLY);
        GetDataFromWeb.getFileFromHttp(8888, url, getBlocklyDir(this)+".zip", new FileDownloadListener() {

            @Override
            public void onGetFileLenth(long request_code, double file_lenth) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopDownloadFile(long request_code, State state) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onReportProgress(long request_code, double progess) {
                Message msg = new Message();
                msg.what = DO_DOWNLOAD_UPDATE_PROGRESS;
                msg.arg1 = (int)progess;
                mHandler.sendMessage(msg);

                UbtLog.d(TAG, "progress:" + progess);
            }

            @Override
            public void onDownLoadFileFinish(long request_code, State state) {
                // TODO Auto-generated method stub
                if (state == State.fail) {
                    UbtLog.d(TAG, "下载失败");
                    mHandler.sendEmptyMessage(DO_DOWNLOAD_FAIL);

                } else {

                    UbtLog.d(TAG, "下载成功");
                    String success = ZipTools.unZip(getBlocklyDir(BlocklyActivity.this)+".zip", getBlocklyDir(BlocklyActivity.this));
                    if(success.equals(ZipTools.MSG_SUCCESS)){
                        //TODO 解压完成后删除zip包
                        FileTools.DeleteFile(new File(getBlocklyDir(BlocklyActivity.this)+".zip"));
                        mHandler.sendEmptyMessage(DO_DOWNLOAD_SUCCESS);
                    }else {
                        mHandler.sendEmptyMessage(DO_DOWNLOAD_FAIL);
                    }
                    UbtLog.d(TAG, "解压完成 success = " + success);
                }
            }
        });
    }

    @Override
    public void onComplete(Object o) {
        UbtLog.d(TAG,"onComplete = " + o);
    }

    @Override
    public void onError(UiError uiError) {
        UbtLog.d(TAG,"UiError = " + uiError);
    }

    @Override
    public void onCancel() {
        UbtLog.d(TAG,"onCancel = " );
    }

    @Override
    public void noteWeixinNotInstalled() {
        Toast.makeText(this,getStringResources("ui_action_share_no_wechat"),Toast.LENGTH_SHORT).show();
    }
}
