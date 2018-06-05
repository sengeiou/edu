package com.ubt.alpha1e_edu.ui.helper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.google.gson.reflect.TypeToken;
//import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.AlphaApplicationValues.Thrid_login_type;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.business.thrid_party.IFaceBookLoginListener;
import com.ubt.alpha1e_edu.business.thrid_party.ITwitterLoginListener;
import com.ubt.alpha1e_edu.business.thrid_party.MyFaceBook;
import com.ubt.alpha1e_edu.business.thrid_party.MyTencent;
import com.ubt.alpha1e_edu.business.thrid_party.MyTwitter;
import com.ubt.alpha1e_edu.business.thrid_party.MyWeiXin;
import com.ubt.alpha1e_edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e_edu.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e_edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e_edu.data.JsonTools;
import com.ubt.alpha1e_edu.data.Md5;
import com.ubt.alpha1e_edu.data.model.BaseResponseModel;
import com.ubt.alpha1e_edu.data.model.QQLoginInfo;
import com.ubt.alpha1e_edu.data.model.UserInfo;
import com.ubt.alpha1e_edu.data.model.WeiXinLoginInfo;
import com.ubt.alpha1e_edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e_edu.net.http.basic.IJsonListener;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.utils.GsonImpl;
import com.ubt.alpha1e_edu.utils.log.MyLog;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import twitter4j.auth.AccessToken;

public class LoginHelper extends BaseHelper implements IJsonListener,
        ISharedPreferensListenet, IUiListener, /*WeiboAuthListener,*/
        IFaceBookLoginListener, ITwitterLoginListener {

    private static final String TAG = "LoginHelper";

    private Context thiz;
    // -------------------------------
    private ILoginUI mUI;
    // -------------------------------
    private long do_login_request = 10001;
    private long do_thread_login_request = 10002;
    private long do_weixin_get_login_info = 10003;
    // -------------------------------
    private int do_record_user_request = 11001;
    private int do_read_user_request = 11002;
    // -------------------------------
    private static final int MSG_DO_LOGIN = 1001;
    private static final int MSG_DO_RECOED_USER = 1002;
    private static final int MSG_DO_READ_USER = 1003;
    private static final int MSG_DO_THRID_LOGIN = 1004;
    private static final int MSG_DO_GET_WEIXIN_LOGIN_INFO = 1005;
    // �������ͱ���
    private static final int MSG_SET_ALIAS = 1006;
    // -------------------------------
    public static final String IS_LOGIN_SIGLE = "IS_LOGIN_SIGLE";
    public static final String ACCOUNT = "ACCOUNT";
    public static final String PHONE_REGISTER = "PHONE_REGISTER";
    private String loginPasswd = "";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_DO_LOGIN) {
                if (msg.obj != null) {
                    String json = (String) msg.obj;//返回的json里面 models是以[]形式的数组对象（但是只有一个）,需要做类型转换 坑爹
                    BaseResponseModel<List<UserInfo>> baseResponseModel = GsonImpl.get().toObject(json,new TypeToken<BaseResponseModel<List<UserInfo>>>(){}.getType());
                    UbtLog.d(TAG,"json ====>> " + json);
                    if(baseResponseModel.status)
                    {
                        UserInfo current_uer = baseResponseModel.models.get(0);

                        //用MD5 加密保存本地
                        current_uer.userPassword = Md5.convertMD5(loginPasswd);

                        ((AlphaApplication) mBaseActivity
                                .getApplicationContext())
                                .setCurrentUserInfo(current_uer);

                        if (current_uer.countryCode != null && !current_uer.countryCode.equals("")) {
                            JSONObject jsonObject = JsonTools.getJsonModel(json);
                            if(jsonObject != null){
                                try {jsonObject.put("userPassword",current_uer.userPassword); }catch (Exception ex){}
                            }
                            mCourseAccessToken = current_uer.accessToken;
                            mUI.onLoginFinish(true, jsonObject, "");
                        } else {
                            mUI.onCompleteCountry(current_uer);
                        }
                    }else
                    {
                        mUI.onLoginFinish(false, null,baseResponseModel.info);
                    }

                    /*//新登录接口
                    BaseNewResponseModel<List<UserInfo>> baseResponseModel = GsonImpl.get().toObject(json,new TypeToken<BaseNewResponseModel<List<UserInfo>>>(){}.getType());
                    if(baseResponseModel.success)
                    {
                        UserInfo current_uer = baseResponseModel.data.get(0);
                        ((AlphaApplication) mBaseActivity
                                .getApplicationContext())
                                .setCurrentUserInfo(current_uer);
                        UbtLog.d(TAG,"current_uer = " + current_uer);
                        if (current_uer.countryCode != null && !current_uer.countryCode.equals("")) {
                            mUI.onLoginFinish(true, NewJsonTools.getJsonData(json), "");
                        } else {
                            mUI.onCompleteCountry(current_uer);
                        }
                    }else
                    {
                        mUI.onLoginFinish(false,null,baseResponseModel.errCode+"");
                    }*/

                } else {
                    mUI.onLoginFinish(false, null, mBaseActivity.getResources()
                            .getString(R.string.ui_common_network_request_failed));
                }
            }
            if (msg.what == MSG_DO_RECOED_USER) {

                LoginHelper.this.doReadUser();

            }
            if (msg.what == MSG_DO_READ_USER) {
                UbtLog.d(TAG, "MSG_DO_READ_USER=" + msg.obj);
            }
            if (msg.what == MSG_DO_THRID_LOGIN) {
                String json = (String) msg.obj;
                if (JsonTools.getJsonStatus(json)) {
                    JSONObject jsonObject = JsonTools.getJsonModel(json);

                    UserInfo info = new UserInfo().getThiz(jsonObject.toString());
                    UbtLog.d(TAG,info.toString());

                    // 在内存中记录用户状态
                    ((AlphaApplication) mBaseActivity.getApplicationContext()).setCurrentUserInfo(info);

                    BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                            DataType.USER_USE_RECORD).doWrite(
                            BasicSharedPreferencesOperator.LOGIN_USER_INFO,
                            jsonObject.toString(),
                            LoginHelper.this, -1);

                    if (info != null) {
                        mCourseAccessToken = info.accessToken;

                        if (info.userName == null || info.userName.equals("")) {
                            // û��ע��
                            mUI.onThridLoginFinish(true, false, info);
                        } else {
                            // ��ע��
                            mUI.onThridLoginFinish(true, true, info);
                        }
                    } else {
                        mUI.onThridLoginFinish(false, true, null);
                    }

                } else {
                    mUI.onThridLoginFinish(false, true, null);
                }
            }
            if (msg.what == MSG_DO_GET_WEIXIN_LOGIN_INFO) {

                WeiXinLoginInfo weiXinLoginInfo = ((WeiXinLoginInfo) ((AlphaApplication) mBaseActivity
                        .getApplicationContext())
                        .getCurrentThridLoginInfo());

                if(weiXinLoginInfo == null){
                    mUI.onLoginFinish(false, null, mBaseActivity.getResources()
                            .getString(R.string.ui_login_information_error));
                }else {
                    GetDataFromWeb.getJsonByPost(do_thread_login_request,
                                    HttpAddress.getRequestUrl(Request_type.thrid_login),
                                    HttpAddress.getParamsForPost(
                                            new String[]{weiXinLoginInfo.openid,
                                                    getThridType(Thrid_login_type.WECHAT)+ ""},
                                            Request_type.thrid_login,
                                            mBaseActivity),
                                    LoginHelper.this);
                    mUI.onThridLogin();
                }
            }
            if (MSG_SET_ALIAS == msg.what) {

            }
        }
    };

    public LoginHelper(BaseActivity _baseActivity) {
        super(_baseActivity);
        thiz = _baseActivity.getApplicationContext();
    }

    public LoginHelper(ILoginUI _ui, BaseActivity _baseActivity) {
        super(_baseActivity);
        this.mUI = _ui;
        thiz = _baseActivity.getApplicationContext();
        mBaseActivity.registerReceiver(mWeiXinLoginReceiver, new IntentFilter(
                MyWeiXin.ACTION_WEIXIN_API_CALLBACK));
    }

    public void UnRegisterHelper() {
        super.UnRegisterHelper();

    }

    public void doLogin(Thrid_login_type type) {
        if (type == Thrid_login_type.QQ) {
            MyLog.writeLog("��������¼",
                    "com.ubt.alpha1e.ui.helper.LoginHelper.doLogin");
            MyTencent.doLogin((Activity) mUI, this);
            MyTencent.isNeedOnResualt = true;
        } else if (type == Thrid_login_type.WECHAT) {
            MyLog.writeLog("΢�ŵ�¼",
                    "com.ubt.alpha1e.ui.helper.LoginHelper.doLogin");
//            MyWeiXin.doLogin(mBaseActivity, mUI);
        } else if (type == Thrid_login_type.SINABLOG) {
            MyLog.writeLog("΢����¼",
                    "com.ubt.alpha1e.ui.helper.LoginHelper.doLogin");
//            MyWeiBo.doLogin((Activity) this.mUI, this);
        } else if (type == Thrid_login_type.FACEBOOK) {
            MyFaceBook.doLogin(mBaseActivity, this);
            MyFaceBook.isNeedOnResualt = true;
        } else if (type == Thrid_login_type.TWITTER) {
            MyTwitter.doLogin(mBaseActivity, this);
            MyTwitter.isNeedOnResult = true;
        }
    }

    public void doLogin(String name, String passwd) {

        if (name == null || name.trim().equals("")) {
            mUI.onNoteNameEmpty();
            return;
        }

        if (passwd == null || passwd.trim().equals("")) {
            mUI.onNotePassEmpty();
            return;
        }

        loginPasswd = passwd;

        //旧登录接口
        String url = HttpAddress.getRequestUrl(Request_type.login);
        String param = HttpAddress.getParamsForPost(new String[]{name, passwd},
                       Request_type.login, this.mBaseActivity);

        //新登录接口，暂时不替换
        /*String url;
        String param;
        if(name.contains("@")){
            url = HttpAddress.getRequestUrl(Request_type.new_email_login);
            param = HttpAddress.getParamsForPost(new String[]{name, passwd},
                    Request_type.new_email_login, this.mBaseActivity);
        }else {
            url = HttpAddress.getRequestUrl(HttpAddress.Request_type.new_phone_login);
            param = HttpAddress.getParamsForPost(new String[]{name, passwd},
                    HttpAddress.Request_type.new_phone_login, this.mBaseActivity);
        }*/

        UbtLog.d(TAG,"url = " + url);
        UbtLog.d(TAG,"param = " + param);

        GetDataFromWeb.getJsonByPost(do_login_request, url, param, this);

    }

    public void doRecordUser(JSONObject jobj) {
        // ��¼�������

        doRecordUser(jobj.toString());

    }

    public void doRecordUser(String jstr) {

        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.LOGIN_USER_INFO, jstr, this,
                do_record_user_request);
        setJPushAlias(jstr);

    }

    public void doReadUser() {
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doReadAsync(
                BasicSharedPreferencesOperator.LOGIN_USER_INFO, this,
                do_read_user_request);

    }

    @Override
    public void onGetJson(boolean isSuccess, String json, long request_code) {
        // TODO Auto-generated method stub
        if (do_login_request == request_code) {
            UbtLog.d(TAG,"isSuccess = " + isSuccess + " json = " + json);
            if (isSuccess) {
                Message msg = new Message();
                msg.obj = json;
                msg.what = MSG_DO_LOGIN;
                mHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.obj = null;
                msg.what = MSG_DO_LOGIN;
                mHandler.sendMessage(msg);
            }

        } else if (do_thread_login_request == request_code) {
            UbtLog.d(TAG,"第三方登录:" + json );
            Message msg = new Message();
            msg.obj = json;
            msg.what = MSG_DO_THRID_LOGIN;
            mHandler.sendMessage(msg);
        } else if (do_weixin_get_login_info == request_code) {
            if (isSuccess) {
//                SendAuth.Resp weixin_return_info = (SendAuth.Resp) ((AlphaApplication) mBaseActivity
//                        .getApplicationContext()).getCurrentThridLoginInfo();

                WeiXinLoginInfo loginInfo = new WeiXinLoginInfo().getThiz(json);

                ((AlphaApplication) mBaseActivity.getApplicationContext())
                        .setCurrentThridUserInfo(Thrid_login_type.WECHAT,loginInfo);

                Message msg = new Message();
                msg.what = MSG_DO_GET_WEIXIN_LOGIN_INFO;
                mHandler.sendMessage(msg);

            }
        }
    }

    @Override
    public void onSharedPreferenOpreaterFinish(boolean isSuccess,
                                               long request_code, String value) {
        // TODO Auto-generated method stub
        if (do_record_user_request == request_code) {

            if (isSuccess) {
                Message msg = new Message();
                msg.obj = value;
                msg.what = MSG_DO_RECOED_USER;
                mHandler.sendMessage(msg);
            }

        }
        if (do_read_user_request == request_code) {
            Message msg = new Message();
            msg.obj = value;
            msg.what = MSG_DO_READ_USER;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onCancel() {
        // MyLog.writeLog("onCancel");
    }

    private int getThridType(Thrid_login_type type) {
        if (type == Thrid_login_type.QQ)
            return 1;
        else if (type == Thrid_login_type.WECHAT)
            return 2;
        else if (type == Thrid_login_type.SINABLOG)
            return 3;
        else if (type == Thrid_login_type.FACEBOOK)
            return 4;
        else if (type == Thrid_login_type.TWITTER)
            return 5;
        return -1;
    }

    @Override
    public void onError(UiError arg0) {
        // MyLog.writeLog("errot:" + arg0.toString());
    }

    private BroadcastReceiver mWeiXinLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {

/*            SendAuth.Resp weixin_return_info = MyWeiXin.handleIntent(arg1,
                    mBaseActivity);
            if (weixin_return_info == null) {
                mUI.onLoginFinish(false, null, mBaseActivity.getResources()
                        .getString(R.string.ui_login_information_error));
            } else {
                // д���ڴ�
                ((AlphaApplication) mBaseActivity.getApplicationContext())
                        .setCurrentThridUserInfo(Thrid_login_type.WECHAT,
                                weixin_return_info);
                // �����������󣬻�ȡopenId
                do_weixin_get_login_info = GetDataFromWeb
                        .getJsonByGet(
                                HttpAddress.getRequestUrl(Request_type.get_weixin_login_info),
                                HttpAddress.getParamsForGet(new String[]{
                                                MyWeiXin.WEIXIN_APP_ID,
                                                MyWeiXin.WEIXIN_APP_SECRET,
                                                weixin_return_info.code,
                                                MyWeiXin.GRANTTYPE},
                                        Request_type.get_weixin_login_info),
                                LoginHelper.this);
                mUI.onThridLogin();
            }*/
        }
    };

    @Override
    public void onComplete(Object arg0) {

        String result = ((JSONObject) arg0).toString();
        MyLog.writeLog(TAG, "LoginHelper.onComplete--QQ->" + result);
        QQLoginInfo qq_return_info = new QQLoginInfo().getThiz(result);
        //qq_return_info sometime is null
        if(qq_return_info != null){
            ((AlphaApplication) mBaseActivity.getApplicationContext())
                    .setCurrentThridUserInfo(Thrid_login_type.QQ, qq_return_info);

            //qq_return_info.access_token
            String url = HttpAddress.getRequestUrl(Request_type.thrid_login);
            String param = HttpAddress.getParamsForPost(new String[]{qq_return_info.openid,
                    getThridType(Thrid_login_type.QQ) + ""},Request_type.thrid_login, this.mBaseActivity);

            GetDataFromWeb.getJsonByPost(do_thread_login_request, url, param, this);
            UbtLog.d(TAG, "QQ登录请求 url = " + url);
            UbtLog.d(TAG, "QQ登录请求 param = " + param);
            mUI.onThridLogin();
        }else {
            Message msg = new Message();
            msg.obj = "";
            msg.what = MSG_DO_THRID_LOGIN;
            mHandler.sendMessage(msg);
        }

    }

//    @Override
//    public void onComplete(Bundle values) {
//        MyLog.writeLog("΢����¼", "onComplete-->΢����¼�յ�");
//        Oauth2AccessToken mAccessToken = Oauth2AccessToken
//                .parseAccessToken(values); // �� Bundle �н��� Token
//        if (mAccessToken.isSessionValid()) {
//            MyLog.writeLog("΢����¼", "token��Ч��" + mAccessToken.toString());
//
//            ((AlphaApplication) mBaseActivity.getApplicationContext())
//                    .setCurrentThridUserInfo(Thrid_login_type.SINABLOG,
//                            mAccessToken);
//
//            // ���󱾵ص�¼����ñ���token��
//            MyLog.writeLog("΢����¼", "���𱾵ص�¼��������");
//            GetDataFromWeb
//                    .getJsonByPost(do_thread_login_request, HttpAddress
//                                    .getRequestUrl(Request_type.thrid_login),
//                            HttpAddress.getParamsForPost(new String[]{
//                                            mAccessToken.getUid(),
//                                            getThridType(Thrid_login_type.SINABLOG)
//                                                    + ""}, Request_type.thrid_login,
//                                    this.mBaseActivity), this);
//            mUI.onThridLogin();
//
//        } else {
//            String code = values.getString("code", "");
//            MyLog.writeLog("΢����¼", "token��Ч��" + code);
//        }
//
//    }
//
//    @Override
//    public void onWeiboException(WeiboException arg0) {
//        // TODO Auto-generated method stub
//
//    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {
        // TODO Auto-generated method stub

    }

    @Override
    public void DistoryHelper() {
        try {
            mBaseActivity.unregisterReceiver(mWeiXinLoginReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setJPushAlias(String str) {
        try {
            JSONObject json = new JSONObject(str);
            String alias = "JP" + json.get("userId");
            setAlias(alias);
            System.out.println("record alias:" + alias);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // �������� JPush Example �����ñ����� Activity ��Ĵ��롣һ�� App �����õĵ�����ڣ����κη���ĵط����ö����ԡ�
    public void setAlias(String alias) {
        // ���� Handler ���첽���ñ���
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }



    /**
     * FACEBOOK
     */
    @Override
    public void onLoginComplete(JSONObject object) {
        try {
            System.out.println("onLoginComplete");
            // д���ڴ�
            ((AlphaApplication) mBaseActivity.getApplicationContext())
                    .setCurrentThridUserInfo(Thrid_login_type.FACEBOOK, object);

            // ���󱾵ص�¼����ñ���token��
            MyLog.writeLog("��������¼", "Facebook���𱾵ص�¼��������");
            GetDataFromWeb
                    .getJsonByPost(
                            do_thread_login_request,
                            HttpAddress.getRequestUrl(Request_type.thrid_login),
                            HttpAddress.getParamsForPost(
                                    new String[]{
                                            object.getString("id"),
                                            getThridType(Thrid_login_type.FACEBOOK)
                                                    + ""},
                                    Request_type.thrid_login,
                                    this.mBaseActivity), this);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mUI.onThridLogin();
    }

    /**
     * TWITTER
     */
    @Override
    public void OnLoginComplete(AccessToken token) {
        mUI.onThridLogin();
        ((AlphaApplication) mBaseActivity.getApplicationContext())
                .setCurrentThridUserInfo(Thrid_login_type.TWITTER, token);

        MyLog.writeLog("��������¼", "TWITTER���𱾵ص�¼��������");
        GetDataFromWeb.getJsonByPost(do_thread_login_request, HttpAddress
                .getRequestUrl(Request_type.thrid_login), HttpAddress
                .getParamsForPost(
                        new String[]{String.valueOf(token.getUserId()),
                                getThridType(Thrid_login_type.TWITTER) + ""},
                        Request_type.thrid_login, this.mBaseActivity), this);
    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
        // TODO Auto-generated method stub

    }
}
