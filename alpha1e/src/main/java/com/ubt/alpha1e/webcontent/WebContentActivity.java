package com.ubt.alpha1e.webcontent;


import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
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

    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.web_content)
    WebView webContent;

    private String mTitle = "";
    private String mUrl;
    private Stack<String> mUrls;

    public static void launchActivity(Activity activity, String url, String mTitle) {
        Intent intent = new Intent();
        intent.setClass(activity, WebContentActivity.class);
        intent.putExtra(WebContentActivity.WEB_URL, url);
        intent.putExtra(WebContentActivity.WEB_TITLE, mTitle);
        activity.startActivity(intent);

    }

    @Override
    protected void initUI() {

        tvBaseTitleName.setText(mTitle);

        WebSettings webSettings = webContent.getSettings();
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
                UbtLog.d(TAG,"url = " + url);
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
        };
        webContent.setWebViewClient(webViewClient);
        webContent.loadUrl(mUrl);
    }

    private void doGotoPage(String url) {
        UbtLog.d(TAG, "url:" + url + "  mTitle:" + mTitle);

        mUrls.push(url);
        webContent.loadUrl(url);
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

        mUrls = new Stack<String>();
        if (!mUrl.toLowerCase().contains("http")) {
            mUrl = "http://" + mUrl;
        }

        initUI();
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
