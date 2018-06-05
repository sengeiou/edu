package com.ubt.alpha1e_edu.userinfo.notice;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.base.loading.LoadingDialog;
import com.ubt.alpha1e_edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e_edu.userinfo.model.NoticeModel;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.List;

import butterknife.BindView;

public class WebActivity extends MVPBaseActivity<NoticeContract.View, NoticePresenter> implements NoticeContract.View {

    private static final String TAG = "WebActivity";
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.ib_return)
    ImageButton mIbReturn;
    @BindView(R.id.rl_tile)
    RelativeLayout mRlTile;
    @BindView(R.id.web_content)
    WebView mWebContent;
    public static final String WEB_TITLE = "WEB_TITLE";
    public static final String WEB_URL = "WEB_URL";
    String mTitle;
    String mUrl;
    @BindView(R.id.web_parant)
    RelativeLayout webParant;
    @BindView(R.id.img_net_error)
    ImageView imgNetError;
    @BindView(R.id.load_error_layout)
    RelativeLayout loadErrorLayout;
    @BindView(R.id.webview_main)
    RelativeLayout webviewMain;

    //    private RelativeLayout webParentView, mErrorViewParent;
//    private View mErrorView; //加载错误的视图
//    private ImageView imageViewError;
//    private RotateAnimation rotate;
    private boolean isWebError = false;
    private boolean isRefreshing = false;

    private Handler mHandler = new Handler();

    public static void launchActivity(Activity activity, String url, String mTitle) {
        Intent intent = new Intent();
        intent.setClass(activity, WebActivity.class);
        intent.putExtra(WEB_URL, url);
        intent.putExtra(WEB_TITLE, mTitle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadingDialog.show(this);
        mTitle = (String) getIntent().getStringExtra(
                WEB_TITLE);
        mUrl = (String) getIntent().getStringExtra(WEB_URL);
        mTvTitle.setText(mTitle);
        initWebView();

        loadErrorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbtLog.d(TAG,"onClick");
                if(rotate()) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWebContent.loadUrl(mUrl);
                        }
                    },1000);

                }
            }
        });
        mIbReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initWebView() {
        WebSettings webSettings = mWebContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= 19) {//4.4 ,小于4.4没有这个方法
            webSettings.setMediaPlaybackRequiresUserGesture(true);
        }
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                UbtLog.d(TAG, "shouldOverrideUrlLoading url:" + url);
                Uri uri = Uri.parse(url);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.getScheme().equals("js")) {
                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {

                        //  步骤3：
                        // 执行JS所需要调用的逻辑
                        UbtLog.d(TAG, "js调用了Android的方法");
                        // 可以在协议上带有参数并传递到Android上
                        String arg = uri.getQueryParameter("arg1");
                        UbtLog.d(TAG, "arg:" + arg);
                        mWebContent.loadUrl(mUrl);
                    }
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
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //6.0以下执行
                UbtLog.i(TAG, "onReceivedError: ------->errorCode" + errorCode + ":" + description);
                if (!isWebError) {
                    showErrorPage();
                    isWebError = true;
                }
            }

            //处理网页加载失败时
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //6.0以上执行
                UbtLog.i(TAG, "onReceivedError: ");
                if (!isWebError) {
                    showErrorPage();
                    isWebError = true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                UbtLog.i(TAG, "onPageFinished: ");
                super.onPageFinished(view, url);
                if(!isWebError){
                    hideErrorPage();
                }
                isWebError = false;
                if(isRefreshing) {
                    isRefreshing = false;
                    imgNetError.clearAnimation();
                }
                LoadingDialog.dismiss(WebActivity.this);
            }
        };

        mWebContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                UbtLog.i(TAG, "onReceivedTitle:title ------>" + title);
                if (title.contains("404")){
                    if (!isWebError) {
                        showErrorPage();
                        isWebError = true;
                    }
                }
            }
        });


        mWebContent.setWebViewClient(webViewClient);
        mWebContent.loadUrl(mUrl);
    }

    private void showErrorPage() {
//        mWebContent.loadUrl("file:///android_asset/netError.html");
        UbtLog.d(TAG, "showErrorPage");
        loadErrorLayout.setVisibility(View.VISIBLE);
        webviewMain.postInvalidate();
    }

    private void hideErrorPage(){
        UbtLog.d(TAG, "hideErrorPage");
        loadErrorLayout.setVisibility(View.GONE);
        webviewMain.postInvalidate();
    }


    public boolean rotate() {
        if(isRefreshing){
            return false;
        }
        isRefreshing = true;
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        imgNetError.setAnimation(rotateAnimation);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnimation.setInterpolator(lin);
        rotateAnimation.setDuration(500);
        rotateAnimation.setRepeatCount(-1);//设置重复次数
        rotateAnimation.setFillAfter(false);//动画执行完后是否停留在执行完的状态
        imgNetError.startAnimation(rotateAnimation);
        return true;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_web;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
    public void showLoading() {

    }

    @Override
    public void dissLoding() {

    }

    @Override
    public void setNoticeData(boolean isSuccess, int type, List<NoticeModel> list) {

    }

    @Override
    public void updateStatu(boolean isSuccess, int messageId) {

    }

    @Override
    public void deleteNotice(boolean isSuccess, int messageId) {

    }
}
