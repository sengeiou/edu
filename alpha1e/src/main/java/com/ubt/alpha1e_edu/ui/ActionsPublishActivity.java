package com.ubt.alpha1e_edu.ui;

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
import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.business.NewActionsManager;
import com.ubt.alpha1e_edu.business.NewActionsManagerListener;
import com.ubt.alpha1e_edu.data.DataTools;
import com.ubt.alpha1e_edu.data.FileTools;
import com.ubt.alpha1e_edu.data.model.BaseResponseModel;
import com.ubt.alpha1e_edu.data.model.NewActionInfo;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e_edu.ui.custom.DesContentEditText;
import com.ubt.alpha1e_edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e_edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e_edu.ui.dialog.ProgressDialog;
import com.ubt.alpha1e_edu.ui.dialog.alertview.AlertView;
import com.ubt.alpha1e_edu.ui.dialog.alertview.OnItemClickListener;
import com.ubt.alpha1e_edu.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e_edu.ui.helper.ActionsHelper;
import com.ubt.alpha1e_edu.utils.GsonImpl;
import com.ubt.alpha1e_edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
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

public class ActionsPublishActivity extends BaseActivity implements BaseDiaUI,NewActionsManagerListener {

    private static final String TAG = "ActionsPublishActivity";
    private ImageView img_action_logo;
    private Context mContext;
    private Uri mImageUri;
    private TextView txt_add_media;
    private DesContentEditText edt_disc;
    private LoadingDialog mLoadingDialog;
    private ProgressDialog mProgressDialog;
    public boolean isUploadSuccess = false;
    public String videoUploadPath = "";
    private NewActionInfo newActionInfo = new NewActionInfo();
    private UploadType uploadType = UploadType.unknown;
    public static final int SHOW_PROGRESS_DIALOG = 1002;
    public static final int DISMISS_PROGRESS_DIALOG_SUCCESS = 1003;
    public static final int DISMISS_PROGRESS_DIALOG_FAILED = 1004;

    public static String SCHEME_ID = "SCHEME_ID";
    public static String SCHEME_NAME = "SCHEME_NAME";
    private String mSchemeId = "";
    private String mSchemeName = "";

    public final MyHandler myHandler = new MyHandler(this);

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

    public static void launchActivity(Activity activity, NewActionInfo actionInfo,int requestCode,String schemeId, String schemeName)
    {
        Intent intent = new Intent();
        intent.putExtra(ActionsHelper.TRANSFOR_PARCEBLE, PG.convertParcelable(actionInfo));
        intent.putExtra(SCHEME_ID,schemeId);
        intent.putExtra(SCHEME_NAME,schemeName);
        intent.setClass(activity,ActionsPublishActivity.class);
        activity.startActivityForResult(intent,requestCode);

    }

    private static class MyHandler extends Handler {
        private final WeakReference<ActionsPublishActivity> mActivity;

        public MyHandler(ActionsPublishActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ActionsPublishActivity activity = mActivity.get();
            if(activity!=null)
            {
                switch (msg.what)
                {
                    case SHOW_PROGRESS_DIALOG:
                        activity.mProgressDialog = ProgressDialog.getInstance(activity);
                        activity.mProgressDialog.showMsg(activity.getStringResources("ui_distribute_uploading_video"));
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
                        NewActionsManager.getInstance(activity).addListener(activity);
                        NewActionsManager.getInstance(activity).doUpdate(activity.newActionInfo);
                        Toast.makeText(activity,activity.getStringResources("ui_distribute_reviewing"),Toast.LENGTH_SHORT).show();

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
        setContentView(R.layout.activity_actions_publish);
        mContext = this;
        newActionInfo = getIntent().getParcelableExtra(ActionsHelper.TRANSFOR_PARCEBLE);
        mSchemeId = getIntent().getStringExtra(SCHEME_ID);
        mSchemeName = getIntent().getStringExtra(SCHEME_NAME);
        initUI();
        initControlListener();
    }
    @Override
    protected void initUI() {
        initTitle(getStringResources("ui_readback_save_publish"));
        initTitleSave(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int length =  edt_disc.getText().toString().length();
                if(length<=0){
                    return;
                }
                if (length > 140) {
                    showToast("ui_about_feedback_input_too_long");
                    return;
                }
                newActionInfo.actionResume = edt_disc.getText().toString();
                if(uploadType.equals(UploadType.Video))
                {
                    getQiniuTokenFromServer();
                }else if(uploadType.equals(UploadType.Image))
                {
                    postUploadParamsToServer();
                }else
                {
                    showToast("ui_distribute_add_single_media");
                }

            }
        }, getStringResources("ui_readback_submit"));
        img_action_logo = (ImageView) findViewById(R.id.img_add_media);
        txt_add_media = (TextView)findViewById(R.id.txt_add_media);
        edt_disc = (DesContentEditText)findViewById(R.id.edt_content);
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
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
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

    private void setSubmitButtonState(){
        Button btnSubmit = (Button)ActionsPublishActivity.this.findViewById(R.id.btn_base_save);
        if(edt_disc.getText().toString().trim().length() > 0 && !uploadType.equals(UploadType.unknown)){
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
                new AlertView(null, null, getStringResources("ui_common_cancel"), new String[]{getStringResources("ui_distribute_short_video"),
                        getStringResources("ui_distribute_take_photo"),
                        getStringResources("ui_distribute_phone_library")},
                        null,
                        mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                    public void onItemClick(Object o,int position){
                        switch (position)
                        {
                            case 0:
                                MediaRecordActivity.launchActivity((ActionsPublishActivity)mContext,newActionInfo,ActionsEditHelper.GetThumbnailRequestCodeByVideo);
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
        };
        img_action_logo.setOnClickListener(listener);
        txt_add_media.setOnClickListener(listener);

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
                uploadVideoToQiNiuServer(responseModel.models);
            }
        });
    }

    /***
     * 上传视频到七牛服务器
     */
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
                            if(newActionInfo!=null)
                                newActionInfo.actionVideoPath = key;
                            postUploadParamsToServer();
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

    private void postUploadParamsToServer()
    {
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.uploadAction);
        long actionUserId = ((AlphaApplication) mContext.getApplicationContext()
        ).getCurrentUserInfo().userId;
        newActionInfo.actionUser = actionUserId+"";
        //    actionUser  动作上传者
        //    actionDesciber  动作描述
        //    actionType 动作类型
        //    actionVideoPath 视频地址
        //    actionOriginalId 动作ID
        //    actionPath 服务器hts路径
        //    actionName 动作名称
        //    actionHeadUrl 服务器动作缩略图路径
        Map<String,String> params = HttpAddress.getBasicParamsMap(this);

        params.put("actionUser",newActionInfo.actionUser);
        params.put("actionDesciber",newActionInfo.actionDesciber);
        params.put("actionResume",newActionInfo.actionResume);
        params.put("actionVideoPath",newActionInfo.actionVideoPath+"");
        params.put("actionType",newActionInfo.actionType+"");
        params.put("actionOriginalId",newActionInfo.actionOriginalId+"");
        params.put("actionPath",newActionInfo.actionUrl);
        params.put("actionName",newActionInfo.actionName);
        params.put("actionTime",newActionInfo.actionTime+"");
        params.put("actionHeadUrl",
                newActionInfo.actionHeadUrl== null?FileTools.actions_default_head_url:newActionInfo.actionHeadUrl+"");

        if(mSchemeId == null){
            mSchemeId = "";
        }
        params.put("schemeId",mSchemeId);

        if(uploadType.equals(UploadType.Video))
        {
            uploadVideoRequest(url,params);

        }else
        {
            showProgress();
            File file = new File(newActionInfo.actionPublishCover);
            uploadImageRequest(url,params,file);
        }

    }

    //小视频
    private void uploadVideoRequest(String url,Map<String,String> params)
    {
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
                resetLayoutParams(img_action_logo,72,72);
                Glide.with(mContext).load(uri).centerCrop().into(img_action_logo);
            }

            @Override
            protected Void doInBackground(Void... params) {
                Bitmap bit = null;
                try {
                    /*bit = Glide.with(mContext).load(uri).asBitmap().into((int) DataTools.dip2px(mContext, 80),
                            (int)DataTools.dip2px(mContext,80)).get();*/
                    bit = Glide.with(mContext).load(uri).asBitmap().into((int) DataTools.dip2px(mContext, 60),
                            (int)DataTools.dip2px(mContext,60)).get();

                    String path = FileTools.actions_new_cache+ File.separator+"Images/"+System.currentTimeMillis()+".jpg";
                    newActionInfo.actionPublishCover = path;
                    FileTools.writeImage(bit,
                            path, true);
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
                txt_add_media.setText(getStringResources("ui_distribute_readd"));
                txt_add_media.setTextColor(getResources().getColor(R.color.T5));
                findViewById(R.id.img_add_video).setVisibility(View.GONE);
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
               resetLayoutParams(img_action_logo,126,72);
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
               Glide.with(mContext).load(s).centerCrop().into(img_action_logo);
               txt_add_media.setText(getStringResources("ui_distribute_readd"));
               txt_add_media.setTextColor(getResources().getColor(R.color.T5));
               findViewById(R.id.img_add_video).setVisibility(View.VISIBLE);
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
