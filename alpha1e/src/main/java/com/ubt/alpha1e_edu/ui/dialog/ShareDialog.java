package com.ubt.alpha1e_edu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.blockly.BlocklyActivity;
import com.ubt.alpha1e_edu.business.thrid_party.MyFaceBook;
import com.ubt.alpha1e_edu.business.thrid_party.MyTencent;
import com.ubt.alpha1e_edu.business.thrid_party.MyTwitter;
import com.ubt.alpha1e_edu.data.Constant;
import com.ubt.alpha1e_edu.data.model.BaseResponseModel;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e_edu.ui.DynamicActivity;
import com.ubt.alpha1e_edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by liuqiang on 10/23/15.
 */
public class ShareDialog {

    private static final String TAG = "ShareDialog";

    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private RelativeLayout rlShareCancel = null;
    private LinearLayout llShareIshow,llShareQQ, llShareFacebook, llShareTwitter, llShareWechat, llShareFriends, llShareQzone;


    private Display display;
    private String mShareImagePath = null;

    private String shareUrl = "https://video.ubtrobot.com/";
    private int shareViewId = 0;

    private LoadingDialog mLoadingDialog;

    public ShareDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public ShareDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_share_dialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        rlShareCancel = (RelativeLayout) view.findViewById(R.id.rl_share_cancel);
        llShareIshow = (LinearLayout) view.findViewById(R.id.ll_share_to_ishow);
        llShareQQ = (LinearLayout) view.findViewById(R.id.ll_share_to_qq);
        llShareFacebook = (LinearLayout) view.findViewById(R.id.ll_share_to_facebook);
        llShareTwitter = (LinearLayout) view.findViewById(R.id.ll_share_to_twitter);
        llShareWechat = (LinearLayout) view.findViewById(R.id.ll_share_to_wechat);
        llShareFriends = (LinearLayout) view.findViewById(R.id.ll_share_to_friends);
        llShareQzone = (LinearLayout) view.findViewById(R.id.ll_share_to_qq_zone);

        initControlListener();

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        UbtLog.d("LessonTaskSuccessDialog","-----------");
        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth()), (int) (display.getHeight())));

        return this;
    }

    private void initControlListener(){

        View.OnClickListener onClickListener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareViewId = v.getId();

                switch (v.getId()){
                    case R.id.ll_share_to_ishow:
                        Intent intent = new Intent();
                        intent.putExtra(Constant.SHARE_IMAGE_PATH,mShareImagePath);
                        intent.putExtra(Constant.SHARE_BLOCKLY_CHAllANGE,true);
                        intent.putExtra(DynamicActivity.SEND_TYPE, 2); //type 2:图片
                        intent.setClass(context, DynamicActivity.class);
                        context.startActivity(intent);
                        dialog.dismiss();

                        break;
                    case R.id.ll_share_to_qq:
                    case R.id.ll_share_to_facebook:
                    case R.id.ll_share_to_twitter:
                    case R.id.ll_share_to_wechat:
                    case R.id.ll_share_to_friends:
                    case R.id.ll_share_to_qq_zone:
                        getQiniuTokenFromServer();
                        break;
                    case R.id.rl_share_cancel:
                        dialog.dismiss();
                        break;

                }
            }
        };

        llShareIshow.setOnClickListener(onClickListener);
        llShareQQ.setOnClickListener(onClickListener);
        llShareFacebook.setOnClickListener(onClickListener);
        llShareTwitter.setOnClickListener(onClickListener);
        llShareWechat.setOnClickListener(onClickListener);
        llShareFriends.setOnClickListener(onClickListener);
        llShareQzone.setOnClickListener(onClickListener);
        rlShareCancel.setOnClickListener(onClickListener);
    }

    public ShareDialog setMsg(String msg) {

        return this;
    }

    public ShareDialog setShareImage(String shareImagePath){
        mShareImagePath = shareImagePath;
        return this;
    }

    public ShareDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public ShareDialog setNegativeButton(final View.OnClickListener listener) {

        rlShareCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {

    }

    public void show() {
        setLayout();
        dialog.show();
    }

    public void display(){
        dialog.dismiss();
    }

    public boolean isShowing(){
        return dialog.isShowing();
    }

    /***
     *从服务器获取七牛上传token
     */
    private void getQiniuTokenFromServer()
    {
        showLoading();

        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.getQiniuToken);
        String params = HttpAddress.getBasicParamsForPost(context);

        OkHttpClientUtils
                .getJsonByPostRequest(url,params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        UbtLog.e(TAG,"onResponse:"+e.getMessage());
                        //isUploadSuccess = false;
                        dismissLoading();
                    }
                    @Override
                    public void onResponse(String s,int i) {
                        UbtLog.d(TAG,"onResponse:"+s);
                        //isUploadSuccess = true;
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

        //myHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
        UploadManager uploadManager = new UploadManager();
        String key = System.currentTimeMillis()+".png";

        uploadManager.put(mShareImagePath, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        UbtLog.d(TAG,"onResponse:"+ key+","+info.toString());

                        if(info.isOK()){
                            doShare(shareUrl + key);
                        }

                        dismissLoading();
                    }
                }, new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String s, double v) {
                        UbtLog.d(TAG,"onResponse:"+ s+"--progress:"+v);
                        int progress = (int)(v*100);

                    }
                },null));
    }


    private void showLoading() {

        if (mLoadingDialog == null){
            mLoadingDialog = LoadingDialog.getInstance((BlocklyActivity)context, (BlocklyActivity)context);
        }
        mLoadingDialog.setDoCancelable(true, 6);
        mLoadingDialog.show();
    }

    public void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.cancel();
        }
    }

    private void doShare(String shareUrl){

        switch (shareViewId){
            case R.id.ll_share_to_qq:
                MyTencent.doShareQQ((BlocklyActivity)context, shareUrl, (BlocklyActivity)context);
                dialog.dismiss();
                break;
            case R.id.ll_share_to_facebook:
                MyFaceBook.doShareFaceBook((BlocklyActivity)context, shareUrl);
                dialog.dismiss();
                break;
            case R.id.ll_share_to_twitter:
                MyTwitter.doShareTwitter((BlocklyActivity)context, shareUrl);
                dialog.dismiss();
                break;
            case R.id.ll_share_to_wechat:
//                MyWeiXin.doShareToWeiXin(shareUrl, "", (BlocklyActivity)context, (BlocklyActivity)context, 0);
//                dialog.dismiss();
                break;
            case R.id.ll_share_to_friends:
//                MyWeiXin.doShareToWeiXin(shareUrl, "", (BlocklyActivity)context, (BlocklyActivity)context, 1);
//                dialog.dismiss();
                break;
            case R.id.ll_share_to_qq_zone:
                MyTencent.doShareQQKongjian((BlocklyActivity)context, shareUrl, "", (BlocklyActivity)context);
                dialog.dismiss();
                break;
            default:
                break;

        }


    }

}
