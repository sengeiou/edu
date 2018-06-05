package com.ubt.alpha1e_edu.bluetoothandnet;

import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;

public class BluetoothHelp extends AppCompatActivity implements View.OnClickListener{

    private WebView web_content;
    String fileUrl = "file:///android_asset/index.html";
    private ImageButton ib_return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_help);
        web_content = (WebView) findViewById(R.id.web_content);
        ib_return = (ImageButton) findViewById(R.id.ib_return);
        ib_return.setOnClickListener(BluetoothHelp.this);
        WebSettings webSettings = web_content.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);
        if(Build.VERSION.SDK_INT >= 19){//4.4 ,小于4.4没有这个方法
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
                if(HttpAddress.WebServiceAdderss.contains(HttpAddress.WebAddressDevelop)){
                    //webview 忽略证书
                    handler.proceed();
                }else {
                    super.onReceivedSslError(view, handler, error);
                }
            }
        };
        web_content.setWebViewClient(webViewClient);
        web_content.loadUrl(fileUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        web_content.removeAllViews();
        web_content.destroy();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id){
            case R.id.ib_return:
                BluetoothHelp.this.finish();
            break;
        }
    }
}
