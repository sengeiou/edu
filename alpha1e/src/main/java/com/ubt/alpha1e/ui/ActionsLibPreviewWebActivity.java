package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baoyz.pg.PG;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.business.ActionsDownLoadManager;
import com.ubt.alpha1e.business.ActionsDownLoadManagerListener;
import com.ubt.alpha1e.business.NewActionsManager;
import com.ubt.alpha1e.business.NewActionsManagerListener;
import com.ubt.alpha1e.business.thrid_party.IWeiXinListener;
import com.ubt.alpha1e.business.thrid_party.MyFaceBook;
import com.ubt.alpha1e.business.thrid_party.MyTencent;
import com.ubt.alpha1e.business.thrid_party.MyTwitter;
import com.ubt.alpha1e.business.thrid_party.MyWeiXin;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.BannerInfo;
import com.ubt.alpha1e.data.model.CommentInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.MyAlertDialog;
import com.ubt.alpha1e.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.ui.helper.IActionsLibUI;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.ActionInfoCallback;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.utils.log.MyLog;

import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import okhttp3.Call;

public class ActionsLibPreviewWebActivity extends BaseActivity implements
        IActionsLibUI, ActionsDownLoadManagerListener, IJsonListener, View.OnClickListener, IUiListener, IWeiXinListener, BaseDiaUI,NewActionsManagerListener {

    private static final String TAG = "ActionsLibPreviewWebActivity";

    private static final String mUrl = HttpAddress.WebServiceAdderss + "/actiondetail/loading.html";

    private ActionInfo mAction;
    private WebView web_content;
    private ActionsDownLoadManager downloadManager;
    private RelativeLayout rl_share;
    private ImageButton btn_to_qq, btn_to_face, btn_to_twitter, btn_to_wechat, btn_to_friends, btn_qqzone;
    private Button btn_cancel_share,btn_cancel_publish;
    private MyAlertDialog mMyAlertDialog;
    private Context thiz;
    private View viewStub = null;

    private Button btn_share;


    public static final String WEB_URL = "WEB_URL";
    public static final String DOWNLOAD_STATE = "DOWNLOAD_STATE";
    public static final String DOWNLOAD_PERCENT = "DOWNLOAD_PERCENT";
    public static final String FROM_NOTICE = "FROM_NOTICE";
    public static final String HEADER_DOWNLOAD = "action_download:";
    public static final String HEADER_SHARE = "action_share:";
    public static final String HEADER_PLAY = "action_play:";
    public static final String HEADER_LOGIN = "login";
    public static final String HEADER_KEYBOARD_ON = "openKeyBord";
    public static final String HEADER_KEYBOARD_OFF = "closeKeyBord";
    public static final String HEADER_SYSTEM_ERROR = "systemTips#";
    public static final String HEADER = "alpha://";

    public static final int SHOW_TOAST = 0x0010;
    public static final int SHOW_SHARE = 0x0011;
    private long do_get_share_url = 1001;
    private String shareUrl = "";
    private String shareId = "";
    private String postUrlParams = "";
    private String country = "EN";



    private int state = -1;
    private int percent = 0;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SHOW_TOAST:
                    String str = msg.obj.toString();
                    showToast(str);
                    break;
                case SHOW_SHARE:
                    if(msg.arg1 == 1){
                        btn_share.setVisibility(View.VISIBLE);
                    }else {
                        btn_share.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    public static void launchActivity(Context context, ActionInfo actionInfo, int state, double percent) {
        Intent intent = new Intent();
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ActionsLibHelper.Action_key, PG.convertParcelable(actionInfo));
        intent.putExtra(ActionsLibPreviewWebActivity.DOWNLOAD_STATE, state);
        intent.putExtra(ActionsLibPreviewWebActivity.DOWNLOAD_PERCENT, percent);
        intent.setClass(context,
                ActionsLibPreviewWebActivity.class);
        context.startActivity(intent);
    }

    public static void launchActivity(Context context, ActionInfo actionInfo, int state, double percent,boolean fromNotice) {
        Intent intent = new Intent();
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ActionsLibHelper.Action_key, PG.convertParcelable(actionInfo));
        intent.putExtra(ActionsLibPreviewWebActivity.DOWNLOAD_STATE, state);
        intent.putExtra(ActionsLibPreviewWebActivity.DOWNLOAD_PERCENT, percent);
        intent.putExtra(ActionsLibPreviewWebActivity.FROM_NOTICE, fromNotice);
        intent.setClass(context,ActionsLibPreviewWebActivity.class);
        context.startActivity(intent);
    }

    public static void launchActivity(Activity activity, ActionInfo actionInfo, int state, double percent,int requestCode) {
        Intent intent = new Intent();
        intent.putExtra(ActionsLibHelper.Action_key, PG.convertParcelable(actionInfo));
        intent.putExtra(ActionsLibPreviewWebActivity.DOWNLOAD_STATE, state);
        intent.putExtra(ActionsLibPreviewWebActivity.DOWNLOAD_PERCENT, percent);
        intent.setClass(activity,
                ActionsLibPreviewWebActivity.class);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_lib_preview_web);
        thiz =  this.getApplicationContext();
        if(getIntent().getExtras()!=null)
        mAction = getIntent().getExtras().getParcelable(ActionsLibHelper.Action_key);
        initUI();
        initControlListener();
        initParams(this.getApplicationContext());
    }


    private void initParams(Context context) {

        mHelper = new ActionsLibHelper(this, this);
        ((ActionsLibHelper) mHelper).doReadDownLoadHistory();
        state = (int) getIntent().getExtras().get(ActionsLibPreviewWebActivity.DOWNLOAD_STATE);
        percent = (int) ((double) getIntent().getExtras().get(ActionsLibPreviewWebActivity.DOWNLOAD_PERCENT) + 0);
        downloadManager = ActionsDownLoadManager.getInstance(context);
        downloadManager.addListener(this);
        WebSettings webSettings = web_content.getSettings();
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);
        if(Build.VERSION.SDK_INT >= 19){//4.4 ,小于4.4没有这个方法
            webSettings.setMediaPlaybackRequiresUserGesture(true);
        }
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        web_content.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                UbtLog.d(TAG,"onProgressChanged::"+newProgress);
                super.onProgressChanged(view, newProgress);
            }


        });
        WebViewClient webViewClient = new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!web_content.getSettings().getLoadsImagesAutomatically()) {
                    web_content.getSettings().setLoadsImagesAutomatically(true);
                }
                loadJsMethods(postDetailParams(state));

                if(!TextUtils.isEmpty(url) && url.contains("?")){
                    web_content.setVisibility(View.VISIBLE);
                    viewStub.setVisibility(View.GONE);
                }
            }

            //5.0以下版本
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                if (url.startsWith(HEADER)) {
                    if (Build.VERSION.SDK_INT <= 20)
                        doActionsByJs(url.replace(HEADER, ""));
                    return null;
                } else{
                    return super.shouldInterceptRequest(view, url);
                }
            }

            //5.0以上版本
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString();
                //UbtLog.d(TAG, "request::" + url);
                if (url.startsWith(HEADER)) {
                    doActionsByJs(url.replace(HEADER, ""));
                    return null;
                } else{
                    return super.shouldInterceptRequest(view, request);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                mLoadingBar.setVisibility(View.GONE);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(HEADER)) {
                    doActionsByJs(url.replace(HEADER, ""));
                    return true;
                }
                return false;
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
        web_content.loadUrl(mUrl);

        if(mAction != null){
            UbtLog.d(TAG,"mAction = " + mAction);
            ((ActionsLibHelper) mHelper).doReadActionDetail(mAction);
        }
    }

    @Override
    protected void initUI() {

        initTitle(getStringResources("ui_dynamic_detail"));

        ViewStub view = (ViewStub)findViewById(R.id.loading_viewstub);
        if(view != null){
            viewStub = view.inflate();
            viewStub.setVisibility(View.VISIBLE);
        }
        web_content = (WebView) findViewById(R.id.web_content);
        rl_share = (RelativeLayout) findViewById(R.id.layout_share);
        btn_cancel_publish = (Button)findViewById(R.id.btn_base_save);
        btn_cancel_share = (Button) findViewById(R.id.btn_cancel_share);
        btn_to_qq = (ImageButton) findViewById(R.id.btn_to_qq);
        btn_to_wechat = (ImageButton) findViewById(R.id.btn_to_qq_weixin);
        btn_to_friends = (ImageButton) findViewById(R.id.btn_to_qq_weixin_pengyouquan);
        btn_to_face = (ImageButton) findViewById(R.id.btn_to_face);
        btn_to_twitter = (ImageButton) findViewById(R.id.btn_to_twitter);
        btn_qqzone = (ImageButton) findViewById(R.id.btn_to_qq_zone);
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setVisibility(View.GONE);

        btn_to_qq.setOnClickListener(this);
        btn_to_wechat.setOnClickListener(this);
        btn_to_friends.setOnClickListener(this);
        btn_to_face.setOnClickListener(this);
        btn_to_twitter.setOnClickListener(this);
        btn_qqzone.setOnClickListener(this);
        btn_cancel_share.setOnClickListener(this);
        btn_cancel_publish.setOnClickListener(this);
        btn_share.setOnClickListener(this);


    }

    public  String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    public String urlDecode(String s)
    {
        try {

            return URLDecoder.decode(s,"UTF-8");
        }catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    private String postDetailParams(int state) {
        UserInfo info = ((AlphaApplication) this.getApplication()).getCurrentUserInfo();
        String token = info == null ? null : info.token;
        int userId = info == null ? 0 : (int) info.userId;
        String username = info == null ? "" : info.userName;
        String[] req = HttpAddress.getRequestInfo();
        country = getStandardLocale(getAppSetLanguage());
        String requestKey = req[1];
        String requestTime = req[0];
        postUrlParams = "userId=" + userId +
                "&actionId=" + mAction.actionId +
                "&lange=" + country +
                "&isWify=" + getWiFiState() +
                "&token=" + token +
                "&requestKey=" + requestKey +
                "&requestTime=" + requestTime +
                "&isDownload=" + state +
                "&downloadPesent=" + percent +
                "&userName=" + urlEncode(username);
        UbtLog.d(TAG, "$$$:" + postUrlParams);
        return "javascript:callActionDetail(\"" + postUrlParams + "\")";
    }

    private String postUserId() {
        UserInfo info = ((AlphaApplication) this.getApplication()).getCurrentUserInfo();
        int userId = info == null ? 0 : (int) info.userId;
        return "javascript:callActionDetail(\"" + userId + "\")";
    }

    private String doDownLoadAction(int percent) {
        return "javascript:changeDownloadState(\"" + percent + "\")";

    }


    private void loadJsMethods(String method) {
        web_content.evaluateJavascript(method, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {

                if(mAction.isFromCreate || mAction.actionPath == null){//只有我的创建才显示取消发布的button / 消息中心进来的时候 actionPath为null
                    doReadActionDetails(mAction);
                }
            }
        });

    }

    private void doReadActionDetails(ActionInfo actionInfo)
    {
        UserInfo info = ((AlphaApplication) this
                .getApplicationContext()).getCurrentUserInfo();
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.getDetailIos);
        final long userID = info==null?-1:info.userId;
        String params = HttpAddress
                .getParamsForPost(
                        new String[]{actionInfo.actionId + "",info==null?0+"":info.userId+""},
                        HttpAddress.Request_type.getDetailIos,
                        this);
        OkHttpClientUtils
                .getJsonByPostRequest(url,params)
                 .execute(new ActionInfoCallback() {
                     @Override
                     public void onError(Call call, Exception e,int i) {

                     }

                     @Override
                     public void onResponse(final ActionInfo actionInfo,int i) {
                         UbtLog.d(TAG, "onResponse:" + actionInfo.toString());
//                         if(mAction.actionPath != null){
//                             mAction.actionPath = actionInfo.actionPath;
//                         }
                         //在消息中心分享和下载需要用到以下参数
                         mAction.actionPath = actionInfo.actionPath;
                         mAction.actionImagePath = actionInfo.actionImagePath;
                         mAction.actionVideoPath = actionInfo.actionVideoPath;
                         mAction.actionName =actionInfo.actionName;
                         mAction.actionDesciber = actionInfo.actionDesciber;

                         mAction.actionTitle = actionInfo.actionTitle;
                         mAction.actionType = actionInfo.actionType;
                         mAction.actionSonType = actionInfo.actionSonType;
                         mAction.actionDownloadTime = actionInfo.actionDownloadTime;
                         mAction.actionPraiseTime = actionInfo.actionPraiseTime;
                         mAction.actionTime = actionInfo.actionTime;
                         mAction.hts_file_name = actionInfo.hts_file_name;
                         mAction.isCollect = actionInfo.isCollect;
                         mAction.isPraise = actionInfo.isPraise;
                         mAction.actionBrowseTime = actionInfo.actionBrowseTime;

                         if(web_content==null) return;
                         web_content.post(new Runnable() {
                             @Override
                             public void run() {

                                 //V2.9.1.3这里不需要 取消发布按钮
                                 /*if (actionInfo.actionStatus == 9||actionInfo.actionStatus ==1) {
                                     if(actionInfo.actionUser.equalsIgnoreCase(userID+""))
                                     {
                                         btn_cancel_publish.setText(getStringResources("ui_distribute_publish_cancel"));
                                         btn_cancel_publish.setMaxEms(6);
                                         btn_cancel_publish.setSingleLine(true);
                                         btn_cancel_publish.setEllipsize(TextUtils.TruncateAt.END);
                                         btn_cancel_publish.setVisibility(View.VISIBLE);
                                     }else{
                                         btn_cancel_publish.setVisibility(View.GONE);
                                     }
                                 }*/

                                 btn_cancel_publish.setVisibility(View.GONE);
                             }
                         });

                     }
                 });


    }

    private void doSetKeyboard(boolean isOpen) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isOpen) {
            imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    private void doActionsByJs(String url) {
        if (url.startsWith(HEADER_DOWNLOAD))//下载
        {
            String download = url.replace(HEADER_DOWNLOAD, "").split("=")[1];

            if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                downloadManager.doDownloadOnRobot(mAction,false);
            }else {
                downloadManager.DownLoadAction(mAction);
            }

        } else if (url.startsWith(HEADER_SHARE))//分享
        {
            shareId = url.replace(HEADER_SHARE, "").split("=")[1];
            getSharedUrl();
        } else if (url.startsWith(HEADER_PLAY))//播放
        {
            if (mHelper.isLostCoon()) {
                ActionsLibPreviewWebActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RobotConnectedActivity.launchActivity(ActionsLibPreviewWebActivity.this,true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                    }
                });

            } else {
                if(mAction.isFromCreate)
                {
                    Intent backIntent = new Intent();
                    backIntent.putExtra("actionId", mAction.actionId);
                    setResult(Constant.FROM_DETAIL_PLAY_BACK_TO_CREATE,backIntent);
                    ActionsLibPreviewWebActivity.this.finish();
                }else{

                    UserInfo info = ((AlphaApplication) ActionsLibPreviewWebActivity.this.getApplicationContext()).getCurrentUserInfo();
                    if (info == null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                noteNoUser();
                            }
                        });
                    }else{
                        //检测是否在充电状态和边充边玩状态是否打开
                        if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(ActionsLibPreviewWebActivity.this)){
                            Toast.makeText(ActionsLibPreviewWebActivity.this, ActionsLibPreviewWebActivity.this.getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        MyActionsActivity.launchActivity(this,1,mAction.actionId);
                    }
                }
            }
        } else if (url.startsWith(HEADER_LOGIN)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    noteNoUser();
                }
            });
        } else if (url.startsWith(HEADER_KEYBOARD_ON)) {
            doSetKeyboard(true);
        } else if (url.startsWith(HEADER_KEYBOARD_OFF)) {
            doSetKeyboard(false);

        }else if(url.startsWith(HEADER_SYSTEM_ERROR))
        {
            notifyUserError(urlDecode(url.replace(HEADER_SYSTEM_ERROR,"")));
        }


    }

    private void notifyUserError(final String string)
    {
        ActionsLibPreviewWebActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ActionsLibPreviewWebActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void noteNoUser() {
        LoginActivity.launchActivity(ActionsLibPreviewWebActivity.this,true,12306);
    }

    private void getSharedUrl() {
        GetDataFromWeb.getJsonByPost(do_get_share_url, HttpAddress
                .getRequestUrl(HttpAddress.Request_type.get_share_url), HttpAddress
                .getParamsForPost(new String[]{"share", "url"},
                        HttpAddress.Request_type.get_share_url,
                        ((AlphaApplication) this.getApplicationContext()).getCurrentUserInfo(),
                        this), this);
    }


    @Override
    protected void initControlListener() {

    }

    private String pactStringUrl(String url,String shareId)
    {
        return url+"actionId="+shareId+"&lange="+country;
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_to_face:
                MyFaceBook.doShareFaceBook(ActionsLibPreviewWebActivity.this, mAction, pactStringUrl(shareUrl,shareId));
                break;
            case R.id.btn_to_twitter:
                MyTwitter.doShareTwitter(ActionsLibPreviewWebActivity.this, mAction, pactStringUrl(shareUrl,shareId));
                break;
            case R.id.btn_to_qq:
                MyTencent.doShareQQ(ActionsLibPreviewWebActivity.this, mAction, pactStringUrl(shareUrl,shareId), ActionsLibPreviewWebActivity.this);
                break;
            case R.id.btn_to_qq_weixin:
                MyWeiXin.doShareToWeiXin(pactStringUrl(shareUrl,shareId), mAction, ActionsLibPreviewWebActivity.this, ActionsLibPreviewWebActivity.this, 0);
                break;
            case R.id.btn_to_qq_weixin_pengyouquan:
                MyWeiXin.doShareToWeiXin(pactStringUrl(shareUrl,shareId), mAction, ActionsLibPreviewWebActivity.this, ActionsLibPreviewWebActivity.this, 1);
                break;
            case R.id.btn_to_qq_zone:
                MyTencent.doShareQQKongjian(ActionsLibPreviewWebActivity.this,
                        mAction, pactStringUrl(shareUrl,shareId),
                        ActionsLibPreviewWebActivity.this);
                break;
            case R.id.btn_cancel_share:
                if (rl_share.getVisibility() == View.VISIBLE) {
                    rl_share.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_base_save:
                new AlertDialog(this).builder().setMsg(getStringResources("ui_distribute_cancel_tip")).setCancelable(true).
                        setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doCancelPublishActions();
                            }
                        }).setNegativeButton(getStringResources("ui_common_back"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                break;
            case R.id.btn_share:
                if(mAction != null){
                    shareId = mAction.actionId+"";
                    getSharedUrl();
                }else {

                }
                break;
            default:
                break;
        }

    }

    private void doCancelPublishActions()
    {
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.cancelPublish);
        String params = HttpAddress
                .getParamsForPost(
                        new String[]{mAction.actionOriginalId +""},
                        HttpAddress.Request_type.cancelPublish,
                        this);

        OkHttpClientUtils
                .getJsonByPostRequest(url,params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int i) {
                        UbtLog.d(TAG, "onResponse:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String s,int i) {
                        UbtLog.d(TAG, "onResponse:" + s);
                        mAction.actionStatus = 0;
                        String string = GsonImpl.get().toJson(mAction);
                        NewActionInfo newActionInfo = GsonImpl.get().toObject(string,NewActionInfo.class);
                        NewActionsManager.getInstance(thiz).addListener(ActionsLibPreviewWebActivity.this);
                        NewActionsManager.getInstance(thiz).doUpdate(newActionInfo);
                    }
                });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(web_content!=null)
        {
            web_content.destroyDrawingCache();
            web_content.destroy();
        }
    }

    @Override
    protected void initBoardCastListener() {

    }

    public int getWiFiState() {
        ConnectivityManager connManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null
                && wifi.getState() == android.net.NetworkInfo.State.CONNECTED) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(ActionsLibPreviewWebActivity.class.getSimpleName());
        super.onResume();
        web_content.onResume();
        rl_share.setVisibility(View.GONE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        web_content.onPause();//防止退出时继续播视频
    }

    @Override
    public void onGetFileLenth(ActionInfo action, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(ActionInfo action, FileDownloadListener.State state) {

    }

    @Override
    public void onReportProgress(ActionInfo action, double progess) {
        if (action.actionId == mAction.actionId) {

            final int progress = (int) progess;
            web_content.post(new Runnable() {
                @Override
                public void run() {
                    loadJsMethods(doDownLoadAction(progress<=0?0:progress));
                }
            });
        }


    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, final FileDownloadListener.State state) {

        web_content.post(new Runnable() {
            @Override
            public void run() {
                loadJsMethods(doDownLoadAction(state == FileDownloadListener.State.success ? -1 : -2));
            }
        });
    }


    @Override
    public void onGetJson(boolean isSuccess, String json, long request_code) {

        if (do_get_share_url == request_code) {
            if (JsonTools.getJsonStatus(json)) {
                try {
                    shareUrl = new JSONObject(json).getString("models");
                    rl_share.post(new Runnable() {
                        @Override
                        public void run() {
                            rl_share.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @Override
    public void onComplete(Object arg0) {
        rl_share.setVisibility(View.GONE);
//        Toast.makeText(this,
//                this.getResources().getString(R.string.ui_action_share_success), Toast.LENGTH_SHORT)
//                .show();
    }

    @Override
    public void onError(UiError arg0) {
        rl_share.setVisibility(View.GONE);
        MyLog.writeLog("第三方分享", "分享失败:" + arg0.errorCode + ","
                + arg0.errorDetail + "," + arg0.errorMessage);
        Toast.makeText(this,getStringResources("ui_action_share_fail"), Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onCancel() {
        rl_share.setVisibility(View.GONE);
    }

    @Override
    public void noteWeixinNotInstalled() {
        rl_share.setVisibility(View.GONE);
        Toast.makeText(this,getStringResources("ui_action_share_no_wechat"),
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onGetShareUrl(String string) {

    }

    @Override
    public void onWeiXinShareFinish(Integer obj) {
        switch (obj) {
            case BaseResp.ErrCode.ERR_OK:
                rl_share.setVisibility(View.GONE);
                System.out.println("onComplete-----onWeiXinShareFinish");
                Toast.makeText(this,getStringResources("ui_action_share_success"),
                        Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                rl_share.setVisibility(View.GONE);
                Toast.makeText(this,getStringResources("ui_action_share_cancel"),
                        Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                rl_share.setVisibility(View.GONE);
                Toast.makeText(this,getStringResources("ui_action_share_fail"), Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                rl_share.setVisibility(View.GONE);
                Toast.makeText(this,getStringResources("ui_action_share_fail"), Toast.LENGTH_SHORT)
                        .show();
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12306) {
            web_content.post(new Runnable() {
                @Override
                public void run() {
//                    loadJsMethods(postUserId());
                    web_content.loadUrl(mUrl);
                }
            });

        } else {
            Tencent.onActivityResultData(requestCode, resultCode, data,
                    ActionsLibPreviewWebActivity.this);
//            MyFaceBook.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onNoteTooMore() {

    }

    @Override
    public void onReadImgFromCache(Bitmap img, long l) {

    }

    @Override
    public void onReadActionInfo(ActionInfo info) {
        String actionId = "";
       if(info != null && info.actionId > 0){
           actionId = info.actionId + "";
       }

        if(TextUtils.isEmpty(actionId)){
            Message msg = new Message();
            msg.what = SHOW_SHARE;
            msg.arg1 = 0;
            mHandler.sendMessage(msg);
        }else {
            Message msg = new Message();
            msg.what = SHOW_SHARE;
            msg.arg1 = 1;
            mHandler.sendMessage(msg);
        }

        //UbtLog.d(TAG,"info == actionId = " + actionId);
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
    public void onNoteDataChaged(Bitmap img, long id) {

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
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {
    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo){

    }


    @Override
    public void noteWaitWebProcressShutDown() {

    }

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {

    }

    @Override
    public void onChangeNewActionsFinish() {

          this.finish();
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
}
