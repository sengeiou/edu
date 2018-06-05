package com.ubt.alpha1e.edu.behaviorhabits;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.Constant;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.base.loading.LoadingDialog;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";

    private WebView mWebView;

    private String mUrl;

    public static void launchActivity(Context context) {
        Intent intent = new Intent(context, StatisticsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initWebView();

    }

    private void initWebView() {
        LoadingDialog.show(this);
        mWebView = (WebView) findViewById(R.id.statistic_webview);
        String userId = SPUtils.getInstance().getString(Constant.SP_USER_ID);
        String token = SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN);
        //  mWebView.loadUrl("http://10.10.1.14:8080/alpha1e/index.html" + "?" + "userid=" + userId + "&" + "token=" + token);
        mUrl = HttpEntity.HABIT_STATIS_URL + "?" + "userid=" + userId + "&" + "token=" + token;
       // mUrl = "http://10.10.32.149:8080/behavioralHabits/index.html" + "?" + "userid=" + userId + "&" + "token=" + token;
        //mUrl= "http://10.10.32.149:8080/behavioralHabits/index.html";
        UbtLog.d(TAG, "mUrl=====" + mUrl);
//		webView.loadUrl("http://10.10.1.14:8080/Alexa/desktop.html");
        mWebView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mWebView.getSettings().setLoadsImagesAutomatically(false);
        }
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsInteration(), "android");
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                UbtLog.d(TAG, "shouldOverrideUrlLoading url:" + url);
                if (!TextUtils.isEmpty(url) && url.contains("goBack")) {
                    finish();
                }
//                Uri uri = Uri.parse(url);
//                // 如果url的协议 = 预先约定的 js 协议
//                // 就解析往下解析参数
//                if (uri.getScheme().equals("js")) {
//                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
//                    // 所以拦截url,下面JS开始调用Android需要的方法
//                    if (uri.getAuthority().equals("webview")) {
//
//                        //  步骤3：
//                        // 执行JS所需要调用的逻辑
//                        UbtLog.d(TAG, "js调用了Android的方法");
//                        // 可以在协议上带有参数并传递到Android上
//                        String arg = uri.getQueryParameter("arg1");
//                        UbtLog.d(TAG, "arg:" + arg);
//                        mWebView.loadUrl(mUrl);
//                    }else if(uri.getScheme().equals("alpha1e")){
//                        finish();
//                    }
//                }
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

            }

            //处理网页加载失败时
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //6.0以上执行
                UbtLog.i(TAG, "onReceivedError: ");

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                UbtLog.i(TAG, "onPageFinished: ");
                super.onPageFinished(view, url);

                LoadingDialog.dismiss(StatisticsActivity.this);
            }
        };

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                UbtLog.i(TAG, "onReceivedTitle:title ------>" + title);
                if (title.contains("404")) {

                }
            }
        });

        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(mUrl);
    }


    public class JsInteration {

        @JavascriptInterface
        public void jumpAlexaBtnClick() {
            //进入Alexa 设置页面
        }

        @JavascriptInterface
        public void jumphomeBtn() {

        } // 进入主页面
    }

}
