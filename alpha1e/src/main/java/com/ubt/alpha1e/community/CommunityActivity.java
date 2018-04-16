package com.ubt.alpha1e.community;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pbq.imagepicker.ImagePicker;
import com.pbq.imagepicker.bean.ImageItem;
import com.pbq.imagepicker.ui.media.MediaGridActivity;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.blockly.BlocklyActivity;
import com.ubt.alpha1e.business.thrid_party.IWeiXinListener;
import com.ubt.alpha1e.business.thrid_party.MyTencent;
import com.ubt.alpha1e.business.thrid_party.MyWeiBo;
import com.ubt.alpha1e.business.thrid_party.MyWeiBoNew;
import com.ubt.alpha1e.business.thrid_party.MyWeiXin;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.utils.ImageUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class CommunityActivity extends MVPBaseActivity<CommunityContract.View, CommunityPresenter> implements CommunityContract.View,IUiListener,IWeiXinListener,WbShareCallback {

    private static final String TAG = CommunityActivity.class.getSimpleName();

    private static final int REQUEST_IMAGE_SELECT = 100;
    private static final int SEND_IMAGE_TO_H5 = 1;

    String communityUrl = "http://10.10.32.149:8080/community/index.html";

    private CommunityJsInterface mCommunityJsInterface;

    @BindView(R.id.web_content)
    WebView webContent;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEND_IMAGE_TO_H5:
                    if(webContent != null){
                        ArrayList<ImageItem> imageData = (ArrayList<ImageItem>)msg.obj;
                        if(imageData != null && imageData.size() > 0){
                            String paths = "";
                            ImageItem imageItem = null;
                            String path64 = "";

                            if(imageData.get(0).isVideo()){
                                ToastUtils.showShort("前端暂时不支持上传视频");
                                return;
                            }

                            for(int i = 0; i<imageData.size(); i++){
                                imageItem = imageData.get(i);
                                path64 = ImageUtils.bitmapToString(imageItem.path);
                                UbtLog.d(TAG,"imageItem.path = " + imageItem.path + "   path64 = " + path64);

                                if(i < imageData.size()-1){
                                    paths += path64 + ",";
                                }else {
                                    paths += path64;
                                }
                            }

                            String js = "javascript:androidPicData('" + paths + "')";
                            UbtLog.d(TAG,"SEND_IMAGE_TO_H5 androidPicData-> ");
                            webContent.loadUrl(js);
                        }
                    }
                    break;
            }
        }
    };

    public static void launchActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, CommunityActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void initUI() {
        UbtLog.d(TAG,"initUI-->");

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
                doGotoPage(url);
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
                super.onPageFinished(view, url);
            }

        };

        mCommunityJsInterface = new CommunityJsInterface(CommunityActivity.this);
        webContent.addJavascriptInterface(mCommunityJsInterface, "communityObject");

        UbtLog.d(TAG, "communityUrl = " + communityUrl);
        webContent.setWebViewClient(webViewClient);
        webContent.loadUrl(communityUrl);
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
        WbSdk.install(this,new AuthInfo(this, MyWeiBoNew.APP_KEY, MyWeiBoNew.REDIRECT_URL, MyWeiBoNew.SCOPE));

        MyWeiBoNew.initMyWeiBoShare(this);
        initUI();
    }

    public void showImagePicker(int num){
        if(num > 0){
            ImagePicker.getInstance().setSelectLimit(num);
            Intent intent = new Intent(this, MediaGridActivity.class);
            startActivityForResult(intent, REQUEST_IMAGE_SELECT);
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
        MyWeiBoNew.doShareWeiBo(this, title, url);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == ImagePicker.RESULT_IMAGE_ITEMS) {
            //添加图片返回
            if (data != null ) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                UbtLog.d(TAG,"images = " + images);
                Message msg = new Message();
                msg.what = SEND_IMAGE_TO_H5;
                msg.obj = images;
                mHandler.sendMessage(msg);
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
}
