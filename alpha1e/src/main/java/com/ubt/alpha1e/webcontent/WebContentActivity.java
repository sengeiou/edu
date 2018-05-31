package com.ubt.alpha1e.webcontent;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class WebContentActivity extends MVPBaseActivity<WebContentContract.View, WebContentPresenter> implements WebContentContract.View {


    private static final String TAG = WebContentActivity.class.getSimpleName();
    public static final String WEB_TITLE = "WEB_TITLE";
    public static final String WEB_URL = "WEB_URL";
    public static final String SHOW_BACK = "SHOW_BACK";

    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.web_content)
    WebView webContent;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;

    private String mTitle = "";
    private String mUrl;
    private boolean isShowBack = false;
    private Stack<String> mUrls;

    private WebContentJsInterface mWebContentJsInterface;

    public static void launchActivity(Context activity, String url, String mTitle) {
        launchActivity(activity, url, mTitle, false);
    }

    public static void launchActivity(Context activity, String url, String mTitle, boolean isShowBack) {
        Intent intent = new Intent();
        intent.setClass(activity, WebContentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(WebContentActivity.WEB_URL, url);
        intent.putExtra(WebContentActivity.WEB_TITLE, mTitle);
        intent.putExtra(WebContentActivity.SHOW_BACK, isShowBack);
        activity.startActivity(intent);
    }

    @Override
    protected void initUI() {
        UbtLog.d(TAG, "initUI-->");
        if (isShowBack) {
            rlTitle.setVisibility(View.VISIBLE);
        }

        tvBaseTitleName.setText(mTitle);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                UbtLog.d(TAG, "onPageFinished url = " + url);
                super.onPageFinished(view, url);
            }

        };

        WebChromeClient webChromeClient = new WebChromeClient() {
            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            // For Android > 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }
        };

        mWebContentJsInterface = new WebContentJsInterface(WebContentActivity.this);
        webContent.addJavascriptInterface(mWebContentJsInterface, "communityObject");

        UbtLog.d(TAG, "mUrl = " + mUrl);
        webContent.setWebViewClient(webViewClient);
        webContent.setWebChromeClient(webChromeClient);
        webContent.loadUrl(mUrl);
    }


    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    private void doGotoPage(String url) {
        UbtLog.d(TAG, "url:" + url + "  mTitle:" + mTitle);
        if (url.startsWith("alpha1e:goBack")) {//
            this.finish();
        } else {
            if(webContent != null){
                mUrls.push(url);
                webContent.loadUrl(url);
            }else {
                //华为平板出现过一次webContent为null, 故作此判断
                UbtLog.e(TAG,"webContent is null");
            }
        }
    }

    private boolean doBackPage() {
        if (mUrls.isEmpty()) {
            this.finish();
            return false;
        } else {
            webContent.loadUrl(mUrls.pop());
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == event.ACTION_DOWN && event.getKeyCode() == event.KEYCODE_BACK) {
            this.finish();
            //return doBackPage();
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
        return R.layout.activity_web_content_mvp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

        mTitle = (String) getIntent().getExtras().get(WebContentActivity.WEB_TITLE);
        mUrl = (String) getIntent().getExtras().get(WebContentActivity.WEB_URL);
        isShowBack = getIntent().getExtras().getBoolean(WebContentActivity.SHOW_BACK, false);

        mUrls = new Stack<String>();
        if (!mUrl.toLowerCase().contains("http")) {
            mUrl = "http://" + mUrl;
        }

        initUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }

    @OnClick({R.id.ll_base_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                WebContentActivity.this.finish();
                break;

        }
    }
}
