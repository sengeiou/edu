package com.ubt.alpha1e.community;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pbq.imagepicker.ImagePicker;
import com.pbq.imagepicker.bean.ImageItem;
import com.pbq.imagepicker.ui.media.MediaGridActivity;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e.BuildConfig;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.loading.LoadingDialog;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.bluetoothandnet.netconnect.NetconnectActivity;
import com.ubt.alpha1e.business.thrid_party.IWeiXinListener;
import com.ubt.alpha1e.business.thrid_party.MyTencent;
import com.ubt.alpha1e.business.thrid_party.MyWeiBoNew;
import com.ubt.alpha1e.business.thrid_party.MyWeiXin;
import com.ubt.alpha1e.community.actionselect.ActionSelectActivity;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.model.DownloadProgressInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.userinfo.dynamicaction.DownLoadActionManager;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;
import com.ubt.alpha1e.utils.ImageUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class CommunityActivity extends MVPBaseActivity<CommunityContract.View, CommunityPresenter>
        implements CommunityContract.View,IUiListener,IWeiXinListener,WbShareCallback,DownLoadActionManager.DownLoadActionListener {

    private static final String TAG = CommunityActivity.class.getSimpleName();

    private static final int REQUEST_IMAGE_SELECT = 100;

    private static final int SEND_IMAGE_TO_H5 = 1;
    private static final int DEAL_IMAGE_TO_H2 = 2;
    private static final int SEND_ACTION_TO_H5 = 3;
    private static final int UPLOAD_VIDEO_TO_QINIU_FINISH = 4;
    private static final int ROBOT_NOT_NETWORK = 5;



    @BindView(R.id.web_content)
    WebView webContent;

    String communityUrl = null;
    //String mainUrl = "http://10.10.32.22:8080/community/index.html?source=0";
    //String postReplyUrl = "http://10.10.32.22:8080/community/postReply.html?source=1&class=active";
    //String postDetailUrl = "http://10.10.32.22:8080/community/postDetails.html?source=2";

    String mainUrl = BuildConfig.WebServiceCommunity + "index.html?source=0";
    String postReplyUrl = BuildConfig.WebServiceCommunity + "postReply.html?source=1&class=active";
    String postDetailUrl = BuildConfig.WebServiceCommunity + "postDetails.html?source=2";
    //String communityUrl = "https://test79.ubtrobot.com/community/alphaEbot/index.html?source=0";

    private int mCommunitySource = 0;//0 首页 1 发贴 2 贴子详情
    private int mCommunityPostId = -1; //贴子ID
    private DynamicActionModel mReplyActionModel = null;

    private List<String> mRobotDownActionList = new ArrayList<>();//机器人下载列表

    private CommunityJsInterface mCommunityJsInterface;
    private String mVideoFilePath = "";//发布视频的路径

    private boolean hasReadRobotDownAction = false;
    private DynamicActionModel mPlayActionModel = null;
    private boolean isClickPlayAction = false;

    private boolean needReSendAction = false;//动作详情读动作，会有延迟，读完再重新发一次

    private boolean isFirst = true;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DEAL_IMAGE_TO_H2:
                    ArrayList<ImageItem> imageData = (ArrayList<ImageItem>)msg.obj;
                    if(imageData != null && imageData.size() > 0){
                        if(imageData.get(0).isVideo()){//视频
                            mVideoFilePath = imageData.get(0).path;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Bitmap bitmap = ImageUtils.loadLocalFileBitmap(mVideoFilePath, CommunityActivity.this);
                                    UbtLog.d(TAG,"bitmap == " + bitmap);
                                    String image64 = "";
                                    if(bitmap != null){
                                        image64 = ImageUtils.bitmapToString(bitmap);
                                    }

                                    String params = "{\"type\":2,\"data\":\""+ image64 +"\"}";

                                    Message imageMsg = new Message();
                                    imageMsg.what = SEND_IMAGE_TO_H5;
                                    imageMsg.obj = params;
                                    mHandler.sendMessage(imageMsg);

                                }
                            }).start();

                        }else {//图片
                            String image64 = "";
                            ImageItem imageItem = null;
                            String tmpImage64 = "";

                            for(int i = 0; i<imageData.size(); i++){
                                imageItem = imageData.get(i);
                                tmpImage64 = ImageUtils.bitmapToString(imageItem.path);
                                tmpImage64 = tmpImage64.replaceAll(" ","").replaceAll("\\n","");
                                UbtLog.d(TAG,"imageItem.path => " + imageItem.path + "   tmpImage64 = " + tmpImage64);
                                if(i < imageData.size()-1){
                                    image64 += tmpImage64 + ",";
                                }else {
                                    image64 += tmpImage64;
                                }
                            }

                            String params = "{\"type\":1,\"data\":\""+ image64 +"\"}";

                            Message imageMsg = new Message();
                            imageMsg.what = SEND_IMAGE_TO_H5;
                            imageMsg.obj = params;
                            mHandler.sendMessage(imageMsg);
                        }
                    }
                    break;
                case SEND_IMAGE_TO_H5:
                    if(webContent != null){
                        String js = "javascript:androidPicData('" + msg.obj + "')";
                        webContent.loadUrl(js);
                    }
                    break;
                case SEND_ACTION_TO_H5:
                    if(webContent != null){
                        String js = "javascript:onActionSelect('" + msg.obj + "')";
                        webContent.loadUrl(js);
                    }
                    break;
                case UPLOAD_VIDEO_TO_QINIU_FINISH:
                    String videoUrl = "";
                    if(msg.arg1 == 1){//上传成功
                        videoUrl = (String) msg.obj;
                    }

                    if(webContent != null){
                        String js = "javascript:getQiniuAppVideoUrl('" + videoUrl + "')";
                        UbtLog.d(TAG,"getQiniuAppVideoUrl = " + videoUrl);
                        webContent.loadUrl(js);
                    }
                    break;
                case ROBOT_NOT_NETWORK:
                    //通知联网，并告诉前端状态
                    if(mPlayActionModel != null){
                        sendActionStatus(mPlayActionModel.getActionId(), 0, 0, "");
                    }
                    showNetWorkConnectDialog();
                    break;
            }
        }
    };

    public static void launchActivity(Activity activity,int type) {
        Intent intent = new Intent();
        intent.setClass(activity, CommunityActivity.class);
        intent.putExtra(Constant.COMMUNITY_SOURCE, type);
        activity.startActivity(intent);
    }

    public static void launchToReplyAction(Activity activity,DynamicActionModel dynamicActionModel){
        Intent intent = new Intent();
        intent.setClass(activity, CommunityActivity.class);
        intent.putExtra(Constant.COMMUNITY_SOURCE, 1);
        intent.putExtra(Constant.DYNAMIC_ACTION_MODEL, dynamicActionModel);
        activity.startActivity(intent);
    }

    @Override
    protected void initUI() {
        UbtLog.d(TAG,"initUI-->>");

        WebSettings webSettings = webContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBlockNetworkImage(false);//解决图片加载不出来的问题

        if (Build.VERSION.SDK_INT >= 19) {//4.4 ,小于4.4没有这个方法
            webSettings.setMediaPlaybackRequiresUserGesture(true);
        }

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                UbtLog.d(TAG, "url = " + url);
                if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O) {
                    doGotoPage(url);
                }else{
                    return false;
                }

                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (HttpAddress.WebServiceAdderss.contains(HttpAddress.WebAddressDevelop)) {
                    //webview 忽略证书
                    handler.proceed();
                } else {
                    super.onReceivedSslError(view, handler, error);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                UbtLog.d(TAG,"onPageFinished url = " + url);
                if(!TextUtils.isEmpty(url) && url.contains(communityUrl) && isFirst){
                    isFirst = false;
                    LoadingDialog.dismiss(CommunityActivity.this);
                }

                super.onPageFinished(view, url);
            }
        };

        mCommunityJsInterface = new CommunityJsInterface(CommunityActivity.this);
        webContent.addJavascriptInterface(mCommunityJsInterface, "communityObject");

        if(mCommunitySource == 1){//1 发帖
            communityUrl = postReplyUrl;
        }else if(mCommunitySource == 2){// 2 详情
            communityUrl = postDetailUrl + "&postId=" + mCommunityPostId;
        }else {//0 首页
            communityUrl = mainUrl;
        }

        communityUrl = communityUrl
                + "&userid=" + SPUtils.getInstance().getString(com.ubt.alpha1e.base.Constant.SP_USER_ID)
                + "&token=" + SPUtils.getInstance().getString(com.ubt.alpha1e.base.Constant.SP_LOGIN_TOKEN);
        UbtLog.d(TAG, "communityUrl = " + communityUrl);


        isFirst = true;
        LoadingDialog.show(this);

        webContent.setWebViewClient(webViewClient);
        webContent.loadUrl(communityUrl);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isBulueToothConnected()){
            DownLoadActionManager.getInstance(this).registerBluetoothListener();
            if(!hasReadRobotDownAction){
                hasReadRobotDownAction = true;
                DownLoadActionManager.getInstance(this).getRobotAction();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        if(event.getEvent() == RobotEvent.Event.CONNECT_SUCCESS){
            hasReadRobotDownAction = true;
            DownLoadActionManager.getInstance(this).registerBluetoothListener();
            DownLoadActionManager.getInstance(this).getRobotAction();
        }
    }

    private void doGotoPage(String url) {
        UbtLog.d(TAG, "url:" + url );
        webContent.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == event.ACTION_DOWN && event.getKeyCode() == event.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return false;
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_community;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        UbtLog.d(TAG,"getIntent() => " + getIntent());
        if(getIntent() != null){
            mCommunitySource = getIntent().getExtras().getInt(Constant.COMMUNITY_SOURCE, 0);
            mCommunityPostId = getIntent().getExtras().getInt(Constant.COMMUNITY_POST_ID, -1);
            mReplyActionModel = (DynamicActionModel) getIntent().getExtras().getSerializable(Constant.DYNAMIC_ACTION_MODEL);
        }

        UbtLog.d(TAG,"mCommunitySource = " + mCommunitySource + " mCommunityPostId = " + mCommunityPostId + " mReplyActionModel = " + mReplyActionModel);
        DownLoadActionManager.getInstance(this).addDownLoadActionListener(this);

        MyWeiBoNew.initMyWeiBoShare(this);
        initUI();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //设置weibo回调
        MyWeiBoNew.doResultIntent(intent, this);
    }

    @Override
    protected void onDestroy() {
        DownLoadActionManager.getInstance(this).removeDownLoadActionListener(this);
        super.onDestroy();
    }

    //显示蓝牙连接对话框
    private void showBluetoothConnectDialog() {
        new ConfirmDialog(this).builder()
                .setTitle("提示")
                .setMsg("请先连接蓝牙和Wi-Fi")
                .setCancelable(true)
                .setPositiveButton("去连接", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "去连接蓝牙 ");
                        Intent intent = new Intent();
                        intent.putExtra(com.ubt.alpha1e.base.Constant.BLUETOOTH_REQUEST, true);
                        intent.setClass(CommunityActivity.this, BluetoothconnectActivity.class);
                        startActivityForResult(intent, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                    }
                }).show();
    }


    //显示网络连接对话框
    private void showNetWorkConnectDialog() {
        new ConfirmDialog(this).builder()
                .setTitle("提示")
                .setMsg("请先连接机器人Wi-Fi")
                .setCancelable(true)
                .setPositiveButton("去连接", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "去连接Wifi ");
                        Intent intent = new Intent();
                        intent.setClass(CommunityActivity.this, NetconnectActivity.class);
                        startActivity(intent);
                    }
                }).show();
    }


    public void getActionStatus(DynamicActionModel actionModel){
        if (isBulueToothConnected()) {
            mPlayActionModel = actionModel;
            UbtLog.d(TAG,"mRobotDownActionList = " + mRobotDownActionList.size() + "     actionModel = " + actionModel.getActionOriginalId());
            for(String name : mRobotDownActionList){
                UbtLog.d(TAG, "name = " + name);
            }

            mPresenter.getActionStatus(getContext(), actionModel, mRobotDownActionList);
        }else {
            sendActionStatus(actionModel.getActionId(), 0, 0, "0");
        }
    }

    public void playAction(DynamicActionModel actionModel){
        if (!isBulueToothConnected()) {
            showBluetoothConnectDialog();
            return;
        }

        isClickPlayAction = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //用来显示查询网络提示
                isClickPlayAction = false;
            }
        },5*1000);

        mPlayActionModel = actionModel;
        mPresenter.playAction(this, actionModel);
    }

    /**
     * 上传动作状态
     *  {"actionId":0,"isDownload":0,"actionStatus":0,"downloadPercent":""}
     * @param actionId
     * @param isDownload
     * @param actionStatus
     * @param downloadPercent
     */
    public void sendActionStatus(final int actionId, final int isDownload, final int actionStatus, final String downloadPercent){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mCommunitySource == 2){
                    if(isDownload == 0){
                        needReSendAction = true;
                    }else {
                        needReSendAction = false;
                    }
                }

                String params = "{\"actionId\":"+ actionId +",\"isDownload\":" + isDownload + ",\"actionStatus\":" + actionStatus + ",\"downloadPercent\":\"" + downloadPercent + "\"}";
                UbtLog.d(TAG,"sendActionStatus = " + params);
                if(webContent != null){
                    String js = "javascript:sendActionStatus('" + params + "')";
                    webContent.loadUrl(js);
                }
            }
        });
    }

    /**
     * 上传视频到七牛
     */
    public void loadVideoFileToQiNiu(){
        if(!TextUtils.isEmpty(mVideoFilePath)){
            mPresenter.loadFileToQiNiu(mVideoFilePath);
        }
    }

    public void getReplyAction(){
        UbtLog.d(TAG, "getReplyAction mReplyActionModel = " + mReplyActionModel);
        if(mReplyActionModel != null){
            sendActionToH5(mReplyActionModel);
        }
    }

    private void sendActionToH5(DynamicActionModel actionModel){
        String params = "{\"actionDesciber\":\""+ actionModel .getActionDesciber()
                + "\",\"actionHeadUrl\":\"" + actionModel.getActionHeadUrl()
                + "\",\"actionId\":\"" + actionModel.getActionId()
                + "\",\"actionUrl\":\"" + actionModel.getActionUrl()
                + "\",\"actionName\":\"" + actionModel.getActionName()
                + "\",\"actionOriginalId\":\"" + actionModel.getActionOriginalId()
                + "\",\"actionType\":\"" + actionModel.getActionType()
                +"\"}";

        UbtLog.d(TAG,"selectAction = " + params);
        Message msg = new Message();
        msg.what = SEND_ACTION_TO_H5;
        msg.obj = params;
        mHandler.sendMessage(msg);
    }

    public void showActionSelect(){
        Intent intent = new Intent(this, ActionSelectActivity.class);
        startActivityForResult(intent, Constant.ACTION_SELECT_REQUEST_CODE);
    }

    public void showImagePicker(final int type,final int num){
        if(num > 0){
            PermissionUtils.getInstance(this).request(new PermissionUtils.PermissionLocationCallback() {
                @Override
                public void onSuccessful() {
                    UbtLog.d(TAG,"onSuccessful");
                    ImagePicker.getInstance().setSelectLimit(num);
                    ImagePicker.getInstance().setSelectType(type);
                    Intent intent = new Intent(CommunityActivity.this, MediaGridActivity.class);
                    startActivityForResult(intent, REQUEST_IMAGE_SELECT);
                }

                @Override
                public void onFailure() {
                    UbtLog.d(TAG,"onFailure");
                }

                @Override
                public void onRationSetting() {
                    UbtLog.d(TAG,"onRationSetting");
                }

                @Override
                public void onCancelRationSetting() {
                    UbtLog.d(TAG,"onCancelRationSetting");
                }

            }, PermissionUtils.PermissionEnum.STORAGE, this);
        }
    }

    public void shareToThirdApp(String type,String url,String title,String des){
        if("0".equals(type)){
            doShareWechat(url,title,des);
        }else if("1".equals(type)){
            doShareFriends(url,title,des);
        }else if("2".equals(type)){
            doShareQQ(url,title,des);
        }else if("3".equals(type)){
            doShareWeibo(url,title,des);
        }
    }


    /**
     * 分享QQ
     */
    private void doShareQQ(String url,String title,String des){
        MyTencent.doShareQQ(this, url,title,des, this);
    }

    /**
     * 分享微信
     */
    private void doShareWechat(String url,String title,String des){
        UbtLog.d(TAG,"doShareWechat");
        MyWeiXin.doShareToWeiXin(url, title, des, this, this, 0);
    }

    /**
     * 分享朋友圈
     */
    private void doShareFriends(String url,String title,String des){
        UbtLog.d(TAG,"doShareFriends");
        MyWeiXin.doShareToWeiXin(url, title,des, this, this, 1);
    }

    /**
     * 分享微信
     */
    private void doShareWeibo(String url,String title,String des){
        UbtLog.d(TAG,"doShareWeibo" + " isWbInstall = " + WbSdk.isWbInstall(this));
        if(!WbSdk.isWbInstall(this)){
            ToastUtils.showShort("请先安装新浪微博客户端");
            return;
        }

        MyWeiBoNew.doShareWeiBo(this, title, url);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UbtLog.d(TAG,"onActivityResult requestCode = " + requestCode + "    resultCode = " + resultCode + " data = " + data);
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == ImagePicker.RESULT_IMAGE_ITEMS) {
            //添加图片返回
            if (data != null ) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                UbtLog.d(TAG,"images = " + images);
                Message msg = new Message();
                msg.what = DEAL_IMAGE_TO_H2;
                msg.obj = images;
                mHandler.sendMessage(msg);
            }
        }else if(requestCode == Constant.ACTION_SELECT_REQUEST_CODE && resultCode == Constant.ACTION_SELECT_RESPONSE_CODE){
            if(resultCode == Constant.ACTION_SELECT_RESPONSE_CODE && data != null){
                DynamicActionModel actionModel = (DynamicActionModel)data.getSerializableExtra(Constant.DYNAMIC_ACTION_MODEL);
                sendActionToH5(actionModel);
            }
            if(isBulueToothConnected()){
                UbtLog.d(TAG,"getRobotAction");
                DownLoadActionManager.getInstance(this).getRobotAction();
            }
        }
    }

    @Override
    public void onComplete(Object o) {
        UbtLog.d(TAG,"-onComplete-" + o);
    }

    @Override
    public void onError(UiError uiError) {
        UbtLog.d(TAG,"-onError-" + uiError);
    }

    @Override
    public void onCancel() {
        UbtLog.d(TAG,"-onCancel-" );
    }

    @Override
    public void noteWeixinNotInstalled() {
        UbtLog.d(TAG,"-noteWeixinNotInstalled-" );
        ToastUtils.showShort("请先安装微信客户端");
    }

    @Override
    public void onWbShareSuccess() {
        UbtLog.d(TAG,"onWbShareSuccess");
        ToastUtils.showShort(getStringResources("ui_share_success"));
    }

    @Override
    public void onWbShareCancel() {
        UbtLog.d(TAG,"onWbShareCancel");
        ToastUtils.showShort(getStringResources("ui_share_canceled"));
    }

    @Override
    public void onWbShareFail() {
        UbtLog.d(TAG,"onWbShareFail");
        ToastUtils.showShort(getStringResources("ui_share_failed"));
    }

    @Override
    public void onQiniuTokenFromServer(boolean status, String token) {
        if(!status){
            UbtLog.d(TAG,"获取七牛token失败");
            Message msg = new Message();
            msg.what = UPLOAD_VIDEO_TO_QINIU_FINISH;
            msg.obj = "";
            msg.arg1 = 0;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onLoadFileToQiNiu(boolean status, String url) {
        if(status){
            UbtLog.d(TAG,"上传视频成功：" + url);

            Message msg = new Message();
            msg.what = UPLOAD_VIDEO_TO_QINIU_FINISH;
            msg.obj = url;
            msg.arg1 = 1;
            mHandler.sendMessage(msg);
        }else {
            UbtLog.d(TAG,"上传视频失败");

            Message msg = new Message();
            msg.what = UPLOAD_VIDEO_TO_QINIU_FINISH;
            msg.obj = url;
            msg.arg1 = 0;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onActionStatus(int actionId, int isDownload, int actionStatus, String downloadPercent) {
        sendActionStatus(actionId,isDownload,actionStatus,downloadPercent);
    }

    @Override
    public void getRobotActionLists(List<String> list) {
        UbtLog.d(TAG,"getRobotActionLists = " + list);
        if(list != null && list.size() > 0){
            UbtLog.d(TAG,"getRobotActionLists = " + list.size());

            mRobotDownActionList.clear();
            mRobotDownActionList.addAll(list);

            UbtLog.d(TAG,"mCommunitySource = " + mCommunitySource + "   needReSendAction = " + needReSendAction + " mPlayActionModel = " + mPlayActionModel);
            if(mCommunitySource == 2 && needReSendAction && mPlayActionModel != null){
                for(String actionName : mRobotDownActionList){
                    if(actionName.equals(mPlayActionModel.getActionOriginalId())){
                        sendActionStatus(mPlayActionModel.getActionId(),1, 0,"0");
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void getDownLoadProgress(DynamicActionModel info, DownloadProgressInfo progressInfo) {
        UbtLog.d(TAG,"progressInfo = " + progressInfo.status + "/" + progressInfo.progress + "/" + progressInfo.actionId);
        if(progressInfo.status == 0){//下载失败
            sendActionStatus(info.getActionId(), 0, 0, "0");
        }else if(progressInfo.status == 1){//下载中

        }else if(progressInfo.status == 2){//下载成功
            sendActionStatus(info.getActionId(), 1, 0, "0");
            UbtLog.d(TAG,"mRobotDownActionList.contains(info.getActionOriginalId()) == " + mRobotDownActionList.contains(info.getActionOriginalId()) + "/" + info.getActionOriginalId()+"/" + mRobotDownActionList.size());
            if(!mRobotDownActionList.contains(info.getActionOriginalId())){
                mRobotDownActionList.add(info.getActionOriginalId());
            }

            if(mPlayActionModel != null && mPlayActionModel.getActionId() == info.getActionId()){
                //下载完成后自动播放
                mPlayActionModel.setDownload(true);
                UbtLog.d(TAG,"download finish and play == " + mPlayActionModel);
                mPresenter.playAction(this, mPlayActionModel);
            }
        }else if(progressInfo.status == 3){//未联网
            sendActionStatus(info.getActionId(), 0, 0, "0");
        }
    }

    @Override
    public void playActionFinish(String actionName) {
        UbtLog.d(TAG,"playActionFinish = " + actionName);
        if(mPlayActionModel != null && !TextUtils.isEmpty(actionName)){
            if(actionName.contains(mPlayActionModel.getActionOriginalId())){
                sendActionStatus(mPlayActionModel.getActionId(), 1, 0, "0");
            }
        }
    }

    @Override
    public void onBlutheDisconnected() {
        UbtLog.d(TAG,"-onBlutheDisconnected-");
        hasReadRobotDownAction = false;
        mRobotDownActionList.clear();
    }

    @Override
    public void doActionPlay(long actionId, int status) {

    }

    @Override
    public void doTapHead() {

    }

    @Override
    public void isAlpha1EConnectNet(boolean status) {
        if (!status) {
            UbtLog.d(TAG,"isAlpha1EConnectNet = " + AppManager.getInstance().currentActivity() + " isClickPlayAction = " + isClickPlayAction);
            if(AppManager.getInstance().currentActivity() != null
                    && AppManager.getInstance().currentActivity() instanceof CommunityActivity
                    && isClickPlayAction){
                mHandler.sendEmptyMessage(ROBOT_NOT_NETWORK);
            }
        }
    }
}
