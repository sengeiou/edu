package com.ubt.alpha1e.edu.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.pg.PG;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.business.NewActionsManager;
import com.ubt.alpha1e.edu.business.NewActionsManagerListener;
import com.ubt.alpha1e.edu.data.Constant;
import com.ubt.alpha1e.edu.data.DataTools;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.ImageTools;
import com.ubt.alpha1e.edu.data.TimeTools;
import com.ubt.alpha1e.edu.data.model.BaseResponseModel;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.ui.custom.DesContentEditText;
import com.ubt.alpha1e.edu.ui.custom.ShapedImageView;
import com.ubt.alpha1e.edu.ui.dialog.AlertDialog;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.dialog.ProgressDialog;
import com.ubt.alpha1e.edu.ui.dialog.alertview.AlertView;
import com.ubt.alpha1e.edu.ui.dialog.alertview.OnItemClickListener;
import com.ubt.alpha1e.edu.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.edu.ui.helper.ActionsHelper;
import com.ubt.alpha1e.edu.utils.GsonImpl;
import com.ubt.alpha1e.edu.utils.ResourceUtils;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.yixia.camera.FFMpegUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;

/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */


public class DynamicActivity extends BaseActivity implements BaseDiaUI,NewActionsManagerListener {

    public static final String TAG = "DynamicActivity";
    public static final String SELECT_ACTION_INFO = "select_action_info";
    public static final String SEND_TYPE = "send_type";
    public static final int REQUEST_SELECT_ACTION_CODE = 0;
    public static final String VIDEO_PATH = "video_path";
    public static final String EMPTY_DATA = "empty_data";

    private Context mContext;
    private Uri mImageUri;
    private String mShareImagePath;
    private DesContentEditText edt_disc;
    private LoadingDialog mLoadingDialog;
    private ProgressDialog mProgressDialog;
    public boolean isUploadSuccess = false;
    public String videoUploadPath = "";
    public String imageUploadPath = "";
    private NewActionInfo newActionInfo ;
    private UploadType uploadType = UploadType.unknown;
    public static final int SHOW_PROGRESS_DIALOG = 1002;
    public static final int DISMISS_PROGRESS_DIALOG_SUCCESS = 1003;
    public static final int DISMISS_PROGRESS_DIALOG_FAILED = 1004;

    public static String SCHEME_ID = "SCHEME_ID";
    public static String SCHEME_NAME = "SCHEME_NAME";
    private String mSchemeId = "";
    private String mSchemeName = "";

    public final MyHandler myHandler = new MyHandler(this);

    private TextView tvTextSum;
    private ImageView ivTakePhotoOrVideo;
    private ImageView ivShowPhotoOrVideo;
    private ImageView ivDeletePhotoOrVideo;
    private RelativeLayout rlShowPhotoOrVideo;
    private ImageView ivAddAction;
    private RelativeLayout rlAction;

    private ShapedImageView shapedImageView;
    private TextView tvActionName;
    private TextView tvActionDes;
    private TextView tvActionType;
    private TextView tvActionTime;
    private ImageView ivActionType;
    private ImageView ivDelAction;
    private ImageView ivTypeVideo;

    private int sendType = -1;
    private String actionResume= "";

    private TextView tvBack;
    private TextView tvTitle;
    private Button btnSend;

    private boolean empty_data = false;
    private boolean isBlocklyChallangeShare = false;

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {

    }

    @Override
    public void onChangeNewActionsFinish() {

        this.setResult(RESULT_OK);
        finish();
    }


    enum UploadType
    {
        Image,Video,unknown
    }

    public static void launchActivity(Activity activity, NewActionInfo actionInfo, int requestCode, String schemeId, String schemeName)
    {
        Intent intent = new Intent();
        intent.putExtra(ActionsHelper.TRANSFOR_PARCEBLE, PG.convertParcelable(actionInfo));
        intent.putExtra(SCHEME_ID,schemeId);
        intent.putExtra(SCHEME_NAME,schemeName);
        intent.setClass(activity,DynamicActivity.class);
        activity.startActivityForResult(intent,requestCode);

    }

    private static class MyHandler extends Handler {
        private final WeakReference<DynamicActivity> mActivity;

        public MyHandler(DynamicActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DynamicActivity activity = mActivity.get();
            if(activity!=null)
            {
                switch (msg.what)
                {
                    case SHOW_PROGRESS_DIALOG:
                        activity.mProgressDialog = ProgressDialog.getInstance(activity);
//                        if(activity.uploadType.equals(UploadType.Video)){
//                            activity.mProgressDialog.showMsg(activity.getStringResources("ui_distribute_uploading_video"));
//                        }else if(activity.uploadType.equals(UploadType.Image)){
//                            activity.mProgressDialog.showMsg(activity.getStringResources("ui_distribute_uploading_picture"));
//                        }

                        activity.mProgressDialog.showMsg(activity.getStringResources("ui_dynamic_uploading"));
                        break;
                    case DISMISS_PROGRESS_DIALOG_FAILED:
                        if(activity.mProgressDialog!=null&&!activity.isFinishing())
                        {
                            if(activity.mProgressDialog.isShowing()){
                                activity.mProgressDialog.dismiss();
                            }
                            activity.mProgressDialog = null;
                        }
                        activity.dismissProgress();
                        if(activity.uploadType.equals(UploadType.Video)){
                            Toast.makeText(activity,activity.getStringResources("ui_distribute_upload_video_failed"),Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(activity,activity.getStringResources("ui_distribute_upload_picture_failed"),Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case DISMISS_PROGRESS_DIALOG_SUCCESS:
                        if(activity.mProgressDialog!=null&&!activity.isFinishing())
                        {
                            if(activity.mProgressDialog.isShowing()){
                                activity.mProgressDialog.dismiss();
                            }
                            activity.mProgressDialog = null;
                        }
                        activity.dismissProgress();
                        if(activity.newActionInfo != null){
                            UbtLog.d(TAG, "spring newActionInfo is not null update action info!");
                            NewActionsManager.getInstance(activity).addListener(activity);
                            NewActionsManager.getInstance(activity).doUpdate(activity.newActionInfo);
                        }else{
                            if(activity.empty_data == true){
                                UbtLog.d(TAG, "spring empty data finish activity");
                                activity.setResult(RESULT_OK);
                                activity.finish();
                            }else{
                                UbtLog.d(TAG, "spring finish activity");
                                activity.finish();
                            }

                        }

//                        Toast.makeText(activity,activity.getStringResources("ui_distribute_reviewing"),Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        }
    }

    public void showProgress()
    {
        if(mLoadingDialog == null)
            mLoadingDialog = LoadingDialog.getInstance(this,this);
        if(!mLoadingDialog.isShowing())
            mLoadingDialog.show();
    }

    public void dismissProgress()
    {
        if(mLoadingDialog!=null)
        {
            if(mLoadingDialog.isShowing()&&!isFinishing())
            {
                mLoadingDialog.cancel();
                mLoadingDialog = null;
            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_publish);
        mContext = this;
        newActionInfo = getIntent().getParcelableExtra(ActionsHelper.TRANSFOR_PARCEBLE);
        mImageUri = getIntent().getData();
        mShareImagePath = getIntent().getStringExtra(Constant.SHARE_IMAGE_PATH);
        videoUploadPath = getIntent().getStringExtra(VIDEO_PATH);
        isBlocklyChallangeShare = getIntent().getBooleanExtra(Constant.SHARE_BLOCKLY_CHAllANGE,false);
        sendType = getIntent().getIntExtra(SEND_TYPE, -1);
        UbtLog.d(TAG,"sendType = " + sendType + "   mImageUri = " + mImageUri);
        mSchemeId = getIntent().getStringExtra(SCHEME_ID);
        mSchemeName = getIntent().getStringExtra(SCHEME_NAME);
        empty_data = getIntent().getBooleanExtra(EMPTY_DATA, false);
        initUI();
        initControlListener();
    }
    @Override
    protected void initUI() {
        tvTextSum = (TextView) findViewById(R.id.txt_max_length);
        edt_disc = (DesContentEditText) findViewById(R.id.edt_content);
        edt_disc.addTextChangedListener(textWatcher);

        UbtLog.d(TAG,"mSchemeId:"+mSchemeId + "    mSchemeName:"+mSchemeName);
        if(!TextUtils.isEmpty(mSchemeId) && !TextUtils.isEmpty(mSchemeName)){

            //文本内容
            final String schemeName = "#"+mSchemeName+"# ";
            SpannableString schemeNameSpan = new SpannableString(schemeName);

            //设置字符颜色
            schemeNameSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.T7)),
                    0, schemeName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            edt_disc.setSchemeName(schemeName);
            edt_disc.setText(schemeNameSpan);

            edt_disc.setSelection(edt_disc.getText().length());
            edt_disc.setOnKeyListener(new View.OnKeyListener() {

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL
                            && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if(edt_disc.length() <= schemeName.length()){
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        if(isBlocklyChallangeShare){
            edt_disc.setText(getStringResources("ui_share_tips"));
            edt_disc.setSelection(edt_disc.getText().toString().length());
        }

        ivTakePhotoOrVideo = (ImageView) findViewById(R.id.take_photo);
        ivShowPhotoOrVideo = (ImageView) findViewById(R.id.iv_show_photo_or_video);
        ivDeletePhotoOrVideo = (ImageView) findViewById(R.id.iv_delete_photo_or_video);
        rlShowPhotoOrVideo = (RelativeLayout) findViewById(R.id.rl_show_photo_or_video);
        ivAddAction = (ImageView) findViewById(R.id.iv_add_action);
        rlAction = (RelativeLayout) findViewById(R.id.rl_action);

        tvBack = (TextView) findViewById(R.id.tv_base_back);
        tvTitle = (TextView) findViewById(R.id.txt_base_title_name);
        btnSend = (Button) findViewById(R.id.btn_base_save);
        btnSend.setText(getStringResources("ui_distribute_publish"));
        btnSend.setVisibility(View.VISIBLE);
        tvTitle.setText(getStringResources("ui_dynamic_release_title"));


        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_disc.getText().toString().length()>0 || newActionInfo != null || imageUploadPath != ""
                         || videoUploadPath != null){
                    UbtLog.d(TAG, "length=" + edt_disc.getText().toString().length() + "---newActionInfo="
                     +newActionInfo + "--imageUploadPath="+ imageUploadPath + "---videoUploadPath=" + videoUploadPath);
                    new AlertDialog(DynamicActivity.this).builder().setMsg(getStringResources("ui_dynamic_release_warning")).setCancelable(true).
                            setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            }).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }else{
                    finish();
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClosedKeyboard();
                actionResume = edt_disc.getText().toString();
                if(newActionInfo != null){
                    newActionInfo.actionResume = edt_disc.getText().toString();
                }

                if(uploadType.equals(UploadType.Video))
                {
                    getQiniuTokenFromServer();
                }else if(uploadType.equals(UploadType.Image))
                {
                    getQiniuTokenFromServer();
                }else
                {
                    postUploadParamsToServer();

                }
            }
        });



        if(newActionInfo == null) {
            setAddActionEnable(true);
        }else{
            setAddActionEnable(false);
        }

        shapedImageView = (ShapedImageView) findViewById(R.id.action_logo);
        tvActionName = (TextView) findViewById(R.id.txt_action_name);
        tvActionDes = (TextView) findViewById(R.id.txt_disc);
        ivActionType = (ImageView) findViewById(R.id.img_type_logo);
        tvActionType = (TextView) findViewById(R.id.txt_type_des);
        tvActionTime = (TextView) findViewById(R.id.txt_time);
        ivDelAction = (ImageView) findViewById(R.id.iv_delete_action);
        ivTypeVideo = (ImageView) findViewById(R.id.iv_type_video);

        if(mImageUri != null) {
            getCameraImage(mImageUri);
        }

        if(mShareImagePath != null){
            getImageByPath(mShareImagePath);
        }

        if(videoUploadPath != null && videoUploadPath != ""){
            getVideoThumbnail(videoUploadPath);
        }


        updateActionInfo();

    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        btnSend.setText(getStringResources("ui_distribute_publish"));
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            tvTextSum.setText("" + s.length() + "/140");
        }

        @Override
        public void afterTextChanged(Editable editable) {
            setSubmitButtonState();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }


    };

    private void updateActionInfo() {
        if(newActionInfo != null){
            UbtLog.d(TAG, "newActionInfo=" + newActionInfo.toString());


            Glide.with(mContext).load(newActionInfo.actionHeadUrl).fitCenter().placeholder(R.drawable.sec_action_logo).
                    into(shapedImageView);
            tvActionName.setText(newActionInfo.actionName);
            tvActionDes.setText(newActionInfo.actionDesciber);
            ivActionType.setImageResource(ResourceUtils.getActionTypeImage(newActionInfo.actionSonType,this));
            tvActionType.setText( ResourceUtils.getActionType(newActionInfo.actionSonType,this));
            tvActionTime.setText(TimeTools.getMMTimeForPublish((int)newActionInfo.actionTime)+""+ "");
        }
    }

    private void setSubmitButtonState(){
        Button btnSubmit = (Button)this.findViewById(R.id.btn_base_save);
        if(edt_disc.getText().toString().trim().length() > 0 || !uploadType.equals(UploadType.unknown) || newActionInfo != null){
            btnSubmit.setTextColor(getResources().getColor(R.color.T5));
            btnSubmit.setClickable(true);
        }else{
            btnSubmit.setTextColor(getResources().getColor(R.color.T7));
            btnSubmit.setClickable(false);
        }

    }

    private void doClosedKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }
    @Override
    protected void initControlListener() {


        View.OnClickListener listener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClosedKeyboard();
                if(sendType == 1){
                    new AlertView(null, null, getStringResources("ui_common_cancel"), new String[]{getStringResources("ui_distribute_short_video"),
                            getStringResources("ui_distribute_take_photo"),
                            getStringResources("ui_distribute_phone_library")},
                            null,
                            mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                        public void onItemClick(Object o,int position){
                            switch (position)
                            {
                                case 0:
                                    if(newActionInfo != null){
                                        MediaRecordActivity.launchActivity((DynamicActivity)mContext,newActionInfo, ActionsEditHelper.GetThumbnailRequestCodeByVideo);
                                    }else{
                                        Intent intent = new Intent();
                                        intent.setClass(mContext, MediaRecordActivity.class);
                                        intent.putExtra(ActionsHelper.TRANSFOR_PARCEBLE, "");
                                        startActivityForResult(intent, ActionsEditHelper.GetThumbnailRequestCodeByVideo);
                                    }

                                    break;
                                case 1:
                                    fromTakingPhoto();
                                    break;
                                case 2:
                                    fromFileSelect();
                                    break;
                            }

                        }
                    }).show();
                }else if(sendType == 2){
                    new AlertView(null, null, getStringResources("ui_common_cancel"), new String[]{
                            getStringResources("ui_distribute_take_photo"),
                            getStringResources("ui_distribute_phone_library")},
                            null,
                            mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                        public void onItemClick(Object o,int position){
                            switch (position)
                            {
                                case 0:
                                    fromTakingPhoto();
                                    break;
                                case 1:
                                    fromFileSelect();
                                    break;
                            }

                        }
                    }).show();
                }/*else if(sendType == 3){
                    UbtLog.d(TAG, "1111");
                    new AlertView(null, null, getStringResources("ui_common_cancel"), new String[]{getStringResources("ui_distribute_short_video"),
                            getStringResources("ui_distribute_take_photo"),
                            getStringResources("ui_distribute_phone_library")},
                            null,
                            mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                        public void onItemClick(Object o,int position){
                            switch (position)
                            {
                                case 0:

                                    if(newActionInfo != null){
                                        MediaRecordActivity.launchActivity((DynamicActivity)mContext,newActionInfo, ActionsEditHelper.GetThumbnailRequestCodeByVideo);
                                    }else{
                                        Intent intent = new Intent();
                                        intent.setClass(mContext, MediaRecordActivity.class);
                                        intent.putExtra(ActionsHelper.TRANSFOR_PARCEBLE, "");
//                                        intent.putExtra(SEND_TYPE, sendType);
                                        startActivityForResult(intent, ActionsEditHelper.GetThumbnailRequestCodeByVideo);
                                    }

                                    break;
                                case 1:
                                    fromFileSelect();
                                    break;
                            }

                        }
                    }).show();
                }*/else{
                    new AlertView(null, null, getStringResources("ui_common_cancel"), new String[]{getStringResources("ui_distribute_short_video"),
                            getStringResources("ui_distribute_take_photo"),
                            getStringResources("ui_distribute_phone_library")},
                            null,
                            mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                        public void onItemClick(Object o,int position){
                            switch (position)
                            {
                                case 0:
                                    if(newActionInfo != null){
                                        MediaRecordActivity.launchActivity((DynamicActivity)mContext,newActionInfo, ActionsEditHelper.GetThumbnailRequestCodeByVideo);
                                    }else{
                                        Intent intent = new Intent();
                                        intent.setClass(mContext, MediaRecordActivity.class);
                                        intent.putExtra(ActionsHelper.TRANSFOR_PARCEBLE, "");
                                        startActivityForResult(intent, ActionsEditHelper.GetThumbnailRequestCodeByVideo);
                                    }

                                    break;
                                case 1:
                                    fromTakingPhoto();
                                    break;
                                case 2:
                                    fromFileSelect();
                                    break;
                            }

                        }
                    }).show();
                }

            }
        };
        ivTakePhotoOrVideo.setOnClickListener(listener);

        ivDeletePhotoOrVideo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                rlShowPhotoOrVideo.setVisibility(View.GONE);
                uploadType = UploadType.unknown;
                imageUploadPath = "";
                videoUploadPath = null;
                setSubmitButtonState();
            }
        });

        ivDelAction.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                rlAction.setVisibility(View.GONE);
                uploadType = UploadType.unknown;
                newActionInfo = null;
                setAddActionEnable(true);
                setSubmitButtonState();
            }
        });

        ivAddAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(SEND_TYPE,sendType);
                intent.setClass(DynamicActivity.this, MyActionsSelectActivity.class);
                startActivityForResult(intent,REQUEST_SELECT_ACTION_CODE );

            }
        });

    }

    private void setAddActionEnable(boolean enable) {
        ivAddAction.setEnabled(enable);
        //TODO set enable drawable
        if(enable){
            rlAction.setVisibility(View.GONE);
            ivAddAction.setImageResource(R.drawable.icon_add_action);
        }else{
            rlAction.setVisibility(View.VISIBLE);
            ivAddAction.setImageResource(R.drawable.icon_add_action_unable);
        }
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setSubmitButtonState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
    }

    /***
     *从服务器获取七牛上传token
     */
    private void getQiniuTokenFromServer()
    {

        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.getQiniuToken);
        String params = HttpAddress.getBasicParamsForPost(this);

        OkHttpClientUtils
                .getJsonByPostRequest(url,params)
                .execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int i) {
                UbtLog.d(TAG,"onResponse:"+e.getMessage());
                isUploadSuccess = false;
            }
            @Override
            public void onResponse(String s,int i) {
                UbtLog.d(TAG,"onResponse:"+s);
                isUploadSuccess = true;
                BaseResponseModel<String> responseModel =  new Gson().fromJson(s, BaseResponseModel.class);
                if(uploadType.equals(UploadType.Image)){
                    uploadImageToQiNiuServer(responseModel.models);
                }else if(uploadType.equals(UploadType.Video)){
                    uploadVideoToQiNiuServer(responseModel.models);
                }

            }
        });
    }

    /***
     * 上传视频到七牛服务器
     */
    private String actionVideoPath = "";
    private void uploadVideoToQiNiuServer(String token)
    {

        myHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
        UploadManager uploadManager = new UploadManager();
        String key = System.currentTimeMillis()+".mp4";
        uploadManager.put(videoUploadPath, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        UbtLog.d(TAG,"onResponse:"+ key+","+info.toString());
                        if(info.isOK())
                        {
                            if(newActionInfo!=null){
                                newActionInfo.actionVideoPath = key;
                            }
                            actionVideoPath = key;
                            postUploadParamsToServer();
//                            postUploadVideoParamsToServer();
                        }else
                            myHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG_FAILED);

                    }
                }, new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String s, double v) {
                        UbtLog.d(TAG,"onResponse:"+ s+"--progress:"+v);
                        int progress = (int)(v*100);
                        if(mProgressDialog!=null)
                            mProgressDialog.updateProgress(progress+"");

                    }
                },null));
    }

    //上传图片到七牛服务器
    private String actionImagePath = "";
    private void uploadImageToQiNiuServer(String token)
    {

        myHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
        UploadManager uploadManager = new UploadManager();
        String key = System.currentTimeMillis()+".jpg";
        uploadManager.put(imageUploadPath, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        UbtLog.d(TAG,"imageUploadPath="+ imageUploadPath + "---onResponse:"+ key+","+"info="+info.toString() + "---res=" + res);
                        if(info.isOK())
                        {
                            actionImagePath = key;
                            postUploadParamsToServer();
//                            postUploadImageParamsToServer();
                        }else
                            myHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG_FAILED);

                    }
                }, new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String s, double v) {
                        UbtLog.d(TAG,"onResponse:"+ s+"--progress:"+v);
                        int progress = (int)(v*100);
                        if(mProgressDialog!=null)
                            mProgressDialog.updateProgress(progress+"");

                    }
                },null));
    }




    private void postUploadTextParamsToServer(){
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.uploadAction);
        long actionUserId = ((AlphaApplication) mContext.getApplicationContext()
        ).getCurrentUserInfo().userId;
        UbtLog.d(TAG, "actionUser=" + actionUserId + "text=" + edt_disc.getText().toString());
        Map<String,String> params = HttpAddress.getBasicParamsMap(this);
        params.put("actionUser", actionUserId+ "");
        params.put("actionResume", edt_disc.getText().toString());

        uploadVideoRequest(url, params);
    }

    private void postUploadImageParamsToServer(){
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.uploadAction);
        long actionUserId = ((AlphaApplication) mContext.getApplicationContext()
        ).getCurrentUserInfo().userId;
        UbtLog.d(TAG, "actionUser=" + actionUserId + "text=" + edt_disc.getText().toString() + "--actionImagePath=" + actionImagePath);
        Map<String,String> params = HttpAddress.getBasicParamsMap(this);
        params.put("actionUser", actionUserId+ "");
        params.put("actionResume", edt_disc.getText().toString());
        params.put("actionImagePath", actionImagePath);

        uploadVideoRequest(url, params);
    }

    private void postUploadVideoParamsToServer(){
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.uploadAction);
        long actionUserId = ((AlphaApplication) mContext.getApplicationContext()
        ).getCurrentUserInfo().userId;
        UbtLog.d(TAG, "actionUser=" + actionUserId + "text=" + edt_disc.getText().toString() + "--actionImagePath=" + actionImagePath);
        Map<String,String> params = HttpAddress.getBasicParamsMap(this);
        params.put("actionUser", actionUserId+ "");
        params.put("actionResume", edt_disc.getText().toString());
        params.put("actionVideoPath", actionVideoPath);

        uploadVideoRequest(url, params);
    }

    private void postUploadParamsToServer()
    {
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.uploadAction);
        long actionUserId = ((AlphaApplication) mContext.getApplicationContext()
        ).getCurrentUserInfo().userId;

        //    actionUser  动作上传者
        //    actionDesciber  动作描述
        //    actionType 动作类型
        //    actionVideoPath 视频地址
        //    actionOriginalId 动作ID
        //    actionPath 服务器hts路径
        //    actionName 动作名称
        //    actionHeadUrl 服务器动作缩略图路径
        Map<String,String> params = HttpAddress.getBasicParamsMap(this);

        if(newActionInfo != null){
            newActionInfo.actionUser = actionUserId+"";
            params.put("actionUser",newActionInfo.actionUser);
            params.put("actionDesciber",newActionInfo.actionDesciber);
            params.put("actionResume",newActionInfo.actionResume);
            params.put("actionVideoPath",newActionInfo.actionVideoPath+"");
            params.put("actionType",newActionInfo.actionType+"");
            params.put("actionOriginalId",newActionInfo.actionOriginalId+"");
            if(!TextUtils.isEmpty(newActionInfo.actionUrl)){
                params.put("actionPath",newActionInfo.actionUrl);
            }else {
                params.put("actionPath","");
            }
            params.put("actionName",newActionInfo.actionName);
            params.put("actionTime",newActionInfo.actionTime+"");
            params.put("actionHeadUrl",
                    newActionInfo.actionHeadUrl== null? FileTools.actions_default_head_url:newActionInfo.actionHeadUrl+"");
            params.put("actionImagePath", actionImagePath);

        }else{
            params.put("actionUser", actionUserId + "");
            params.put("actionDesciber","");
            params.put("actionResume",actionResume);
            params.put("actionVideoPath",actionVideoPath);
            params.put("actionType","");
            params.put("actionOriginalId","");
            params.put("actionPath","");
            params.put("actionName","");
            params.put("actionTime","");
            params.put("actionHeadUrl", "");
            params.put("actionImagePath", actionImagePath);


        }


        if(mSchemeId == null){
            mSchemeId = "";
        }
        params.put("schemeId",mSchemeId);

        uploadRequest(url, params);

//        if(uploadType.equals(UploadType.Video))
//        {
//            uploadVideoRequest(url,params);
//
//        }else
//        {
//            showProgress();
//            File file = new File(newActionInfo.actionPublishCover);
//            uploadImageRequest(url,params,file);
//        }

    }

    private void uploadRequest(String url,Map<String,String> params)
    {
        UbtLog.d(TAG, "url=" + url + "params=" + params.toString());
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int i) {
                        UbtLog.d(TAG, "onResponse:" + e.getMessage());
                        myHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG_FAILED);

                    }

                    @Override
                    public void onResponse(String s,int i) {
                        UbtLog.d(TAG, "onResponse:" + s);
                        if(newActionInfo != null){
                            BaseResponseModel<NewActionInfo> base = GsonImpl.get().toObject(s,
                                    new TypeToken<BaseResponseModel<NewActionInfo>>(){}.getType());
                            newActionInfo.actionId = base.models.actionId;
                            newActionInfo.actionStatus = 9;
                        }
                        myHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG_SUCCESS);
                    }
                });

    }

    //小视频
    private void uploadVideoRequest(String url,Map<String,String> params)
    {
        UbtLog.d(TAG, "url=" + url + "---params=" + params.toString());
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int i) {
                        UbtLog.d(TAG, "onResponse:" + e.getMessage());
                        myHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG_FAILED);

                    }

                    @Override
                    public void onResponse(String s,int i) {
                        UbtLog.d(TAG, "onResponse:" + s);
                        BaseResponseModel<NewActionInfo> base = GsonImpl.get().toObject(s,
                                new TypeToken<BaseResponseModel<NewActionInfo>>(){}.getType());
                        newActionInfo.actionId = base.models.actionId;
                        newActionInfo.actionStatus = 9;
                        myHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG_SUCCESS);
                    }
                });

    }
    //图片/拍照
    private void uploadImageRequest(String url,Map<String,String> params,File file)
    {
        OkHttpUtils.post()//
                .addFile("mFile", file.getName(), file)//
                .url(url)//
                .params(params)
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int i) {
                        UbtLog.d(TAG, "onResponse:" + e.getMessage());
                        myHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG_FAILED);
                    }

                    @Override
                    public void onResponse(String s,int i) {
                        BaseResponseModel<NewActionInfo> base = GsonImpl.get().toObject(s,
                                new TypeToken<BaseResponseModel<NewActionInfo>>(){}.getType());
                        newActionInfo.actionId = base.models.actionId;
                        newActionInfo.actionStatus = 9;
                        UbtLog.d(TAG, "onResponse:" + s);
                        myHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG_SUCCESS);
                    }
                });

    }

    /**
     * photo taking
     */
    private void fromTakingPhoto()
    {
        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File path = new File(FileTools.image_cache);
        if (!path.exists()) {
            path.mkdirs();
        }
        mImageUri = Uri.fromFile(new File(path, System.currentTimeMillis() + ""));

        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                mImageUri);
        cameraIntent.putExtra("return-data", true);
        startActivityForResult(cameraIntent,
                ActionsEditHelper.GetUserHeadRequestCodeByShoot);
    }

    /**
     * from file
     */
    private void fromFileSelect()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,
                ActionsEditHelper.GetUserHeadRequestCodeByFile);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActionsEditHelper.GetUserHeadRequestCodeByShoot
                || requestCode == ActionsEditHelper.GetUserHeadRequestCodeByFile) {
            if (resultCode == RESULT_OK) {
                ContentResolver cr = this.getContentResolver();
                if (requestCode == ActionsEditHelper.GetUserHeadRequestCodeByFile) {
                    if (data == null){
                        return;
                    }

                    //android gao ban ben
                    String h_type = cr.getType(data.getData());
                    //android di ban ben
                    String l_type = data.getType();
                    UbtLog.d(TAG,"h_type:"+h_type  + "   l_type:"+l_type);
                    if (h_type == null && l_type == null){
                        return;
                    }

                    mImageUri = data.getData();
                    UbtLog.d(TAG, "mImageUri=" + mImageUri);
                }
                getCameraImage(mImageUri);
                uploadType = UploadType.Image;
                setSubmitButtonState();
            }
        }else if(requestCode == ActionsEditHelper.GetThumbnailRequestCodeByVideo)
        {
            if(resultCode == RESULT_OK)
            {

                videoUploadPath = (String)data.getExtras().get("output");
                getVideoThumbnail(videoUploadPath);
                uploadType = UploadType.Video;
                setSubmitButtonState();
            }

        }else if(requestCode == REQUEST_SELECT_ACTION_CODE){
            if(resultCode == RESULT_OK){
                newActionInfo = (NewActionInfo) data.getExtras().get(ActionsHelper.TRANSFOR_PARCEBLE);
                setAddActionEnable(false);
                updateActionInfo();
            }
        }
    }


    /***
     *获取图片通过路径
     */
    private void getImageByPath(String imagePath)
    {
        File imageFile = new File(imagePath);
        if(imageFile.exists()){
            rlShowPhotoOrVideo.setVisibility(View.VISIBLE);
            ivTypeVideo.setVisibility(View.GONE);
            resetLayoutParams(ivShowPhotoOrVideo,70,70);

            Bitmap bitmap = ImageTools.compressImage(imageFile, 0, 0, false);
            ivShowPhotoOrVideo.setImageBitmap(bitmap);
            uploadType = UploadType.Image;
            imageUploadPath = imagePath;
        }
    }

    /***
     *获取相机或本地图片
     */
    private void getCameraImage(final Uri uri)
    {

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                rlShowPhotoOrVideo.setVisibility(View.VISIBLE);
                ivTypeVideo.setVisibility(View.GONE);
                resetLayoutParams(ivShowPhotoOrVideo,70,70);
                Glide.with(mContext).load(uri).centerCrop().into(ivShowPhotoOrVideo);
                uploadType = UploadType.Image;
            }

            @Override
            protected Void doInBackground(Void... params) {
                Bitmap bit = null;
                try {
//                    if(newActionInfo != null){
                        bit = Glide.with(mContext).load(uri).asBitmap().into((int) DataTools.dip2px(mContext, 60),
                                (int)DataTools.dip2px(mContext,60)).get();

                        String path = FileTools.actions_new_cache+ File.separator+"Images/"+System.currentTimeMillis()+".jpg";
//                        newActionInfo.actionPublishCover = path;
                        FileTools.writeImage(bit,
                                path, true);
                        imageUploadPath = path;
//                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                txt_add_media.setText(getStringResources("ui_distribute_readd"));
//                txt_add_media.setTextColor(getResources().getColor(R.color.T5));
//                findViewById(R.id.img_add_video).setVisibility(View.GONE);
            }
        }.execute();


    }

    /**
     * 获取本地视频缩略图
     * */
    private void getVideoThumbnail(final String filepath)
    {

        new AsyncTask<Void,Void,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgress();
                rlShowPhotoOrVideo.setVisibility(View.VISIBLE);
                ivTypeVideo.setVisibility(View.VISIBLE);
                resetLayoutParams(ivShowPhotoOrVideo,70,70);
                uploadType = UploadType.Video;
            }
            @Override
            protected String doInBackground(Void... params) {
                String outpath = filepath.replace(".mp4",".jpg");
                boolean finish = FFMpegUtils.captureThumbnails(filepath,outpath,"252x142","1");

                return finish?outpath:"";
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                dismissProgress();
                Glide.with(mContext).load(s).centerCrop().into(ivShowPhotoOrVideo);
//                txt_add_media.setText(getStringResources("ui_distribute_readd"));
//                txt_add_media.setTextColor(getResources().getColor(R.color.T5));
//                findViewById(R.id.img_add_video).setVisibility(View.VISIBLE);
            }


        }.execute();

    }

    /**
     * 重置layout参数
     * 单位 dp
     * */
    private void resetLayoutParams(View view,int width,int height)
    {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view
                .getLayoutParams();
        lp.width = (int)DataTools.dip2px(mContext, width);
        lp.height = (int)DataTools.dip2px(mContext, height);
        view.setLayoutParams(lp);

    }
    @Override
    public void noteWaitWebProcressShutDown() {

    }
}
