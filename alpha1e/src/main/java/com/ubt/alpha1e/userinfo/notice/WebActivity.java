package com.ubt.alpha1e.userinfo.notice;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.userinfo.model.NoticeModel;

import java.util.List;

import butterknife.BindView;

public class WebActivity extends MVPBaseActivity<NoticeContract.View, NoticePresenter> implements NoticeContract.View {

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
        mTitle = (String) getIntent().getStringExtra(
                WEB_TITLE);
        mUrl = (String) getIntent().getStringExtra(WEB_URL);
        mTvTitle.setText(mTitle);
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
        mWebContent.setWebViewClient(webViewClient);
        mWebContent.loadUrl(mUrl);

        mIbReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_web;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

//    @Override
//    public void onClick(View view) {
//
//        int id = view.getId();
//        switch (id) {
//            case R.id.ib_return:
//                BluetoothHelp.this.finish();
//                break;
//        }
//    }

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
