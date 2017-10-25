package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.business.thrid_party.IWeiXinListener;
import com.ubt.alpha1e.business.thrid_party.MyFaceBook;
import com.ubt.alpha1e.business.thrid_party.MyTencent;
import com.ubt.alpha1e.business.thrid_party.MyTwitter;
import com.ubt.alpha1e.business.thrid_party.MyWeiXin;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.BannerInfo;
import com.ubt.alpha1e.data.model.CommentInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.ui.helper.IActionsLibUI;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class WebContentActivity extends BaseActivity implements IActionsLibUI,IUiListener,
        IWeiXinListener {
    private static final String TAG = "WebContentActivity";
    public static final String WEB_TITLE = "WEB_TITLE";
    public static final String WEB_URL = "WEB_URL";
    public static final String WEB_IS_SHARE = "WEB_IS_SHARE";
    public static final String WEB_SCHEME_ID = "WEB_SCHEME_ID";
    public static final String FROM_NOTICE = "FROM_NOTICE";

    public static final String SCREEN_ORIENTATION = "SCREEN_ORIENTATION";
    private int mScreenOrientation = 0;

    private WebView web_content;

    private String mTitle = "";
    private String mUrl;
    private Stack<String> mUrls;
    private String mSchemeId = "";

    private Button btn_share;
    private RelativeLayout lay_share_to;
    private Button btn_cancel_share;
    private boolean mIsShare = false;
    private ImageButton btn_to_qq;
    private ImageButton btn_to_face;
    private ImageButton btn_to_twitter;
    //private ImageButton btn_broser;
    private ImageButton btn_to_qq_weixin;
    private ImageButton btn_to_qq_weixin_pengyouquan;
    private ImageButton btn_to_qq_zone;

    public static final int REQUSET_CODE = 1001;
    public static final int LOGIN_REQUSET_CODE = 12306;

    private List<ActionRecordInfo> myDownloadHistoty = null;

    public static void launchActivity(Activity activity,String url,String mTitle,boolean mIsShare)
    {
        Intent intent = new Intent();
        intent.setClass(activity,WebContentActivity.class);
        intent.putExtra(WebContentActivity.WEB_URL,url);
        intent.putExtra(WebContentActivity.WEB_TITLE,mTitle);
        intent.putExtra(WebContentActivity.WEB_IS_SHARE,mIsShare);
        activity.startActivity(intent);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_content);
        mTitle = (String) getIntent().getExtras().get(
                WebContentActivity.WEB_TITLE);
        mUrl = (String) getIntent().getExtras().get(WebContentActivity.WEB_URL);
        mSchemeId = (String) getIntent().getExtras().get(WebContentActivity.WEB_SCHEME_ID);
        mScreenOrientation = getIntent().getIntExtra(SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mHelper = new ActionsLibHelper(this, this);

        mUrls = new Stack<String>();
        if (!mUrl.toLowerCase().contains("http")) {
            mUrl = "http://" + mUrl;
        }
        try {
            mIsShare = (Boolean) getIntent().getExtras().get(
                    WebContentActivity.WEB_IS_SHARE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initUI();
        initControlListener();

    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(WebContentActivity.class.getSimpleName());
        initBoardCastListener();
        super.onResume();

        ((ActionsLibHelper) mHelper).registerLisenters();
        ((ActionsLibHelper) mHelper).doReadDownLoadHistory();
        lay_share_to.setVisibility(View.GONE);

        if(mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        initTitle(mTitle);
    }

    @Override
    protected void initUI() {

        web_content = (WebView) findViewById(R.id.web_content);
        // ----------------------------------------------------------
        initTitle(mTitle);
        // ----------------------------------------------------------
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
                doGotoPage(url);
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
        doGotoPage(mUrl);

        btn_share = (Button) findViewById(R.id.btn_share);
        if (mIsShare) {
            btn_share.setVisibility(View.VISIBLE);
        }
        lay_share_to = (RelativeLayout) findViewById(R.id.lay_share_to);
        btn_cancel_share = (Button) findViewById(R.id.btn_cancel_share);
        btn_to_qq = (ImageButton) findViewById(R.id.btn_to_qq);
        //btn_broser = (ImageButton) findViewById(R.id.btn_broser);
        btn_to_qq_zone = (ImageButton) findViewById(R.id.btn_to_qq_zone);
        btn_to_qq_weixin = (ImageButton) findViewById(R.id.btn_to_qq_weixin);
        btn_to_qq_weixin_pengyouquan = (ImageButton) findViewById(R.id.btn_to_qq_weixin_pengyouquan);
        btn_to_face = (ImageButton) findViewById(R.id.btn_to_face);
        btn_to_twitter = (ImageButton) findViewById(R.id.btn_to_twitter);
    }

    private void doGotoPage(String url) {
        UbtLog.d(TAG,"url:"+url + "    scheme:"+mSchemeId + "  mTitle:"+mTitle);
        if(url.startsWith("alpha1s:schemeId")){//活动主题

            if(TextUtils.isEmpty(mSchemeId)){
                mSchemeId = url.replace("alpha1s:schemeId=","");
            }

            UserInfo info = ((AlphaApplication) this.getApplication()).getCurrentUserInfo();
            if(info == null){
                LoginActivity.launchActivity(WebContentActivity.this,true,LOGIN_REQUSET_CODE);
            }else{
                MyActionsActivity.launchActivity(WebContentActivity.this, 3, mSchemeId, mTitle);
                this.finish();
            }
        }else if(url.startsWith("alpha1s:actionId=")){
            ActionInfo actionInfo = new ActionInfo();
            String actId = url.replace("alpha1s:actionId=","");
            if(isStringNumber(actId)){
                actionInfo.actionId = Integer.parseInt(actId);

                boolean isDownloading = ((ActionsLibHelper) mHelper).isDownloading(actionInfo);
                boolean isDownloadfinish = ((ActionsLibHelper) mHelper).isDownLoadFinish(actionInfo,myDownloadHistoty);
                int state = 0;
                int progress = -1;
                if(isDownloading){
                    state = 2;
                    progress = 0;
                }else if(isDownloadfinish){
                    state = 1;
                }
                ActionsLibPreviewWebActivity.launchActivity(WebContentActivity.this, actionInfo, state, progress);
            }
        }else{
            mUrls.push(url);
            web_content.loadUrl(url);
        }

    }

    /***
     * 判断字符串是否都是数字
     */
    public  boolean isStringNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    private boolean doBackPage() {
        if (mUrls.isEmpty()) {
            this.finish();
            return false;
        } else {
            web_content.loadUrl(mUrls.pop());
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mWeiXinShareReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ((ActionsLibHelper) mHelper).removeListeners();
        ((ActionsLibHelper) mHelper).DistoryHelper();
        super.onDestroy();
    }

    @Override
    protected void initControlListener() {
        // TODO Auto-generated method stub


        btn_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                lay_share_to.setVisibility(View.VISIBLE);
            }
        });

        btn_cancel_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (lay_share_to.getVisibility() == View.VISIBLE) {
                    lay_share_to.setVisibility(View.GONE);
                }
            }
        });

        android.view.View.OnClickListener doShareListener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(mUrl.contains("fromWebview=true")){
                    mUrl = mUrl.replace("fromWebview=true","fromWebview=false");
                }

                if (arg0.getId() == btn_to_qq.getId()) {
                    MyTencent.doShareQQ(WebContentActivity.this, mUrl,
                            WebContentActivity.this);
                } else if (arg0.getId() == btn_to_qq_weixin.getId()) {
                    MyWeiXin.doShareToWeiXin(mUrl, mTitle,WebContentActivity.this,
                            WebContentActivity.this, 0);
                } else if (arg0.getId() == btn_to_qq_weixin_pengyouquan.getId()) {
                    MyWeiXin.doShareToWeiXin(mUrl,mTitle, WebContentActivity.this,
                            WebContentActivity.this, 1);
                } else if (arg0.getId() == btn_to_face.getId()) {
                    MyFaceBook.doShareFaceBook(WebContentActivity.this, mUrl);

                } else if (arg0.getId() == btn_to_twitter.getId()) {
                    MyTwitter.doShareTwitter(WebContentActivity.this, mUrl);
                /*} else if (arg0.getId() == btn_broser.getId()) {

                    if(mUrl.contains("fromWebview=true")){
                        mUrl = mUrl.replace("fromWebview=true","fromWebview=false");
                    }
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(mUrl);
                    intent.setData(content_url);
                    startActivity(intent);*/
                }else if(arg0.getId() == btn_to_qq_zone.getId()){
                    MyTencent.doShareQQKongjian(WebContentActivity.this,mUrl,mTitle,WebContentActivity.this);
                }
            }
        };

        btn_to_qq.setOnClickListener(doShareListener);
        btn_to_qq_weixin.setOnClickListener(doShareListener);
        btn_to_qq_weixin_pengyouquan.setOnClickListener(doShareListener);
        btn_to_face.setOnClickListener(doShareListener);
        btn_to_twitter.setOnClickListener(doShareListener);
        //btn_broser.setOnClickListener(doShareListener);
        btn_to_qq_zone.setOnClickListener(doShareListener);
    }

    @Override
    protected void initBoardCastListener() {
        registerReceiver(mWeiXinShareReceiver, new IntentFilter(
                MyWeiXin.ACTION_WEIXIN_API_CALLBACK));
    }

    private BroadcastReceiver mWeiXinShareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {

            SendAuth.Resp resp = MyWeiXin.handleIntent(arg1,WebContentActivity.this);
            int error_code;
            if (resp != null){
                error_code = resp.errCode;
            }else{
                error_code = -2;
            }

            final int e = error_code;

            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    switch (e) {
                        case BaseResp.ErrCode.ERR_OK:
                            lay_share_to.setVisibility(View.GONE);
                            WebContentActivity.this.showToast("ui_share_success");
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            lay_share_to.setVisibility(View.GONE);
                            WebContentActivity.this.showToast("ui_share_cancel");
                            break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            lay_share_to.setVisibility(View.GONE);
                            WebContentActivity.this.showToast("ui_share_fail");
                            break;
                        default:
                            lay_share_to.setVisibility(View.GONE);
                            WebContentActivity.this.showToast("ui_share_fail");
                            break;
                    }

                }
            });

        }
    };

    @Override
    public void onCancel() {
        lay_share_to.setVisibility(View.GONE);
    }

    @Override
    public void onComplete(Object arg0) {
        lay_share_to.setVisibility(View.GONE);
        WebContentActivity.this.showToast("ui_share_success");
    }

    @Override
    public void onError(UiError arg0) {
        lay_share_to.setVisibility(View.GONE);
        WebContentActivity.this.showToast("ui_share_fail");
    }

    @Override
    public void noteWeixinNotInstalled() {
        lay_share_to.setVisibility(View.GONE);
        WebContentActivity.this.showToast("ui_weixin_not_install");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == event.ACTION_DOWN
                && event.getKeyCode() == event.KEYCODE_BACK) {
            return doBackPage();
        }
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUSET_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                finish();
            }
        }else if(requestCode == LOGIN_REQUSET_CODE){
            UserInfo info = ((AlphaApplication) this.getApplication()).getCurrentUserInfo();
            if(info != null){
                MyActionsActivity.launchActivity(WebContentActivity.this, 3, mSchemeId, mTitle);
                this.finish();
            }
        }
    }

    @Override
    public void onReadActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {

    }

    @Override
    public void onReadActionCommentsFinish(List<CommentInfo> comments) {

    }

    @Override
    public void onActionCommentFinish(boolean is_success) {

    }

    @Override
    public void onActionPraisetFinish(boolean is_success) {

    }

    @Override
    public void onNoteNoUser() {

    }

    @Override
    public void onGetShareUrl(String string) {

    }

    @Override
    public void onWeiXinShareFinish(Integer obj) {

    }

    @Override
    public void onNoteTooMore() {

    }

    @Override
    public void onReadImgFromCache(Bitmap img, long l) {

    }

    @Override
    public void onReadActionInfo(ActionInfo info) {

    }

    @Override
    public void onReadCacheActionsFinish(boolean is_success, List<ActionOnlineInfo> actions) {

    }

    @Override
    public void onReadPopularActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {

    }

    @Override
    public void onReadThemeRecommondFinish(boolean is_success, String error_msg, List<BannerInfo> themes) {

    }

    @Override
    public void onReadOriginalListActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {

    }

    @Override
    public void notePlayChargingError() {

    }

    @Override
    public void notePlayCycleNext(String action_name) {

    }

    @Override
    public void onReadCollocationRecordFinish(boolean isSuccess, String errorInfo, List<ActionColloInfo> history) {

    }

    @Override
    public void onDelRecordFinish() {

    }

    @Override
    public void onRecordFinish(long action_id) {

    }

    @Override
    public void onCollocateFinish(long action_id, boolean isSuccess, String error) {

    }

    @Override
    public void onCollocateRmoveFinish(boolean b) {

    }

    @Override
    public void onGetFileLenth(ActionInfo action, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(ActionInfo action, FileDownloadListener.State state) {

    }

    @Override
    public void onReportProgress(ActionInfo action, double progess) {

    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, FileDownloadListener.State state) {

    }

    @Override
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {
        myDownloadHistoty = history;
        UbtLog.d(TAG,"lihai-------------history>" + history.size());

    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo) {

    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }
}
