package com.ubt.alpha1e.userinfo.myrobot;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.GotoBindRequest;
import com.ubt.alpha1e.base.RequstMode.SetAutoUpgradeRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.loading.LoadingDialog;
import com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.login.LoginManger;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.RobotBindingDialog;
import com.ubt.alpha1e.ui.dialog.UnbindConfirmDialog;
import com.ubt.alpha1e.ui.dialog.alertview.RobotBindDialog;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * @author: dicy.cheng
 * @description:  连接状态
 * @create: 2017/12/21
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class MyRobotActivity extends MVPBaseActivity<MyRobotContract.View, MyRobotPresenter> implements MyRobotContract.View {

    private String TAG = "MyRobotActivity";

    @BindView(R.id.ib_return)
    ImageButton ib_return;

    @BindView(R.id.btn_goto_unbind)
    Button btn_goto_unbind;

    //去连接
    @BindView(R.id.content_goto_connect_bluetooth)
    RelativeLayout content_goto_connect_bluetooth;

    //一键绑定布局
    @BindView(R.id.content_goto_bind)
    RelativeLayout content_goto_bind;

    //含有解除绑定
    @BindView(R.id.content_goto_unbind)
    RelativeLayout content_goto_unbind;

    //一键绑定按钮
    @BindView(R.id.btn_goto_bind)
    Button btn_goto_bind;

    //去连接蓝牙按钮
    @BindView(R.id.btn_goto_bluetooth_connect)
    Button btn_goto_bluetooth_connect;

    //机器人序列号
    @BindView(R.id.tv_robot_sn_number)
    TextView tv_robot_sn_number;

    //机器人版本
    @BindView(R.id.tv_robot_version)
    TextView tv_robot_version;

    //自动升级状态
    @BindView(R.id.tv_robot_auto_update_state)
    TextView tv_robot_auto_update_state;

    //自动升级状态按钮
    @BindView(R.id.btn_auto_upgrade)
    ImageButton btn_auto_upgrade;

    //自动升级提示
    @BindView(R.id.tv_robot_auto_update_ad)
    TextView tv_robot_auto_update_ad;

    private static final int NO_CONNECTED_AND_NOBINDED = 1;
    private static final int BINDED = 2;
    private static final int CONNECTED_NO_BIND = 3;
    private static final int AUTO_UPGRADE_STATUS = 4;

    private static final int ROBOT_GOTO_UNBIND = 1;
    private static final int ROBOT_GOTO_BIND = 2;
    private static final int ROBOT_SET_AUTO_UPGRADE = 3;

    private boolean isAutoUpgrade = false;

    long lastClickTime = System.currentTimeMillis();


    String openAutoUpgrade = "当前自动升级功能已经开启，机器人每次重新联网后都会去检测新版本，如果有新版本，机器人将自动下载并且升级。";
    String closeAutoUpgrade = "当前自动升级功能已经关闭，当机器人有更新版本时，我们会提示你进行升级。";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NO_CONNECTED_AND_NOBINDED:
                    UbtLog.d(TAG, "-NO_CONNECTED_BINDED-");
                    content_goto_bind.setVisibility(View.GONE);
                    content_goto_unbind.setVisibility(View.GONE);
                    content_goto_connect_bluetooth.setVisibility(View.VISIBLE);
                    break;

                case BINDED:
                    UbtLog.d(TAG, "-BINDED-");
                    content_goto_bind.setVisibility(View.GONE);
                    content_goto_unbind.setVisibility(View.VISIBLE);
                    content_goto_connect_bluetooth.setVisibility(View.GONE);
                    break;
                case CONNECTED_NO_BIND:
                    UbtLog.d(TAG, "-CONNECTED_NO_BIND-");
                    content_goto_bind.setVisibility(View.VISIBLE);
                    content_goto_unbind.setVisibility(View.GONE);
                    content_goto_connect_bluetooth.setVisibility(View.GONE);
                    break;
                case AUTO_UPGRADE_STATUS:
                    if(isAutoUpgrade){
                        isAutoUpgrade = false;
                        tv_robot_auto_update_state.setText("自动升级：关闭");
                        tv_robot_auto_update_ad.setText(closeAutoUpgrade);
                        btn_auto_upgrade.setBackgroundResource(R.drawable.menu_setting_unselect);
                    }else {
                        isAutoUpgrade = true;
                        tv_robot_auto_update_state.setText("自动升级：开启");
                        tv_robot_auto_update_ad.setText(openAutoUpgrade);
                        btn_auto_upgrade.setBackgroundResource(R.drawable.menu_setting_select);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @OnClick({R.id.ib_return,R.id.btn_goto_unbind,R.id.btn_goto_bind,R.id.btn_goto_bluetooth_connect,R.id.btn_auto_upgrade})
    protected void switchActivity(View view) {
        UbtLog.d(TAG, "VIEW +" + view.getTag());
        Intent mLaunch = new Intent();
        switch (view.getId()) {
            case R.id.ib_return:
                MyRobotActivity.this.finish();
                break;
            case R.id.btn_goto_unbind:
                adviceUnBind();
                break;
            case R.id.btn_goto_bind:
                gotoBind();
                break;
            case R.id.btn_goto_bluetooth_connect:
                if(System.currentTimeMillis()-lastClickTime < 1000){
                    UbtLog.d(TAG,"1000ms后才能点击");
                    return;
                }
                lastClickTime = System.currentTimeMillis();
                Intent intent = new Intent(MyRobotActivity.this, BluetoothandnetconnectstateActivity.class);
                startActivity(intent);
                MyRobotActivity.this.finish();
                break;
            case R.id.btn_auto_upgrade:
                if(isAutoUpgrade){
                    setAutoUpgrade("0");
//                    isAutoUpgrade = false;
//                    tv_robot_auto_update_state.setText("自动升级：关闭");
//                    tv_robot_auto_update_ad.setText(closeAutoUpgrade);
//                    btn_auto_upgrade.setBackgroundResource(R.drawable.menu_setting_unselect);
                }else {
                    setAutoUpgrade("1");
//                    isAutoUpgrade = true;
//                    tv_robot_auto_update_state.setText("自动升级：开启");
//                    tv_robot_auto_update_ad.setText(openAutoUpgrade);
//                    btn_auto_upgrade.setBackgroundResource(R.drawable.menu_setting_select);
                }
                break;
            default:
                break;
        }
    }

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, MyRobotActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //设置自动升级
    public void setAutoUpgrade(String upgradeType){
        LoadingDialog.show(MyRobotActivity.this);
        SetAutoUpgradeRequest setAutoUpgradeRequest = new SetAutoUpgradeRequest();
        setAutoUpgradeRequest.setAutoUpdate(upgradeType);
        setAutoUpgradeRequest.setSystemType("3");

        String url = HttpEntity.UPDATE_AUTO_UPGRADE;
        doRequest(url,setAutoUpgradeRequest,ROBOT_SET_AUTO_UPGRADE);

    }

    RobotBindingDialog robotBindingDialog = null ;
    //一键绑定
    public void gotoBind(){

        if(AlphaApplication.currentRobotSN == null || AlphaApplication.currentRobotSN.equals("")){
            ToastUtils.showShort("wei机器人序列号为空");
            return;
        }
        if(robotBindingDialog == null){
            robotBindingDialog = new RobotBindingDialog(AppManager.getInstance().currentActivity())
                    .builder()
                    .setCancelable(true);
        }
        robotBindingDialog.show();
        GotoBindRequest gotoBindRequest = new GotoBindRequest();
        gotoBindRequest.setEquipmentId(AlphaApplication.currentRobotSN);
        gotoBindRequest.setSystemType("3");

        String url = HttpEntity.ROBOT_BIND;
        doRequest(url,gotoBindRequest,ROBOT_GOTO_BIND);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void initUI() {

        Intent i = getIntent();
        if(i.getIntExtra("isBinded",0) == 1){
            content_goto_bind.setVisibility(View.GONE);
            content_goto_unbind.setVisibility(View.VISIBLE);
            content_goto_connect_bluetooth.setVisibility(View.GONE);
            String sn = i.getStringExtra("equipmentSeq");
            tv_robot_sn_number.setText(sn);
            String v = i.getStringExtra("serverVersion");
            tv_robot_version.setText("固件版本："+v);
            String upgrade = i.getStringExtra("autoupdate");
            if(upgrade != null && upgrade.equals("1")){
                tv_robot_auto_update_state.setText("自动升级：开启");
                isAutoUpgrade = true;
                tv_robot_auto_update_ad.setText(openAutoUpgrade);
                btn_auto_upgrade.setBackgroundResource(R.drawable.menu_setting_select);
            }else {
                tv_robot_auto_update_state.setText("自动升级：关闭");
                isAutoUpgrade = false;
                tv_robot_auto_update_ad.setText(closeAutoUpgrade);
                btn_auto_upgrade.setBackgroundResource(R.drawable.menu_setting_unselect);
            }
        }else if(i.getIntExtra("isBinded",0) == 2){
            if(((AlphaApplication) MyRobotActivity.this.getApplicationContext())
                    .getCurrentBluetooth() == null){
                content_goto_bind.setVisibility(View.GONE);
                content_goto_unbind.setVisibility(View.GONE);
                content_goto_connect_bluetooth.setVisibility(View.VISIBLE);
            }else {
                content_goto_bind.setVisibility(View.VISIBLE);
                content_goto_unbind.setVisibility(View.GONE);
                content_goto_connect_bluetooth.setVisibility(View.GONE);
            }
        }
        tv_robot_auto_update_ad.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.myrobot;
    }

    //一键解绑
    public void gotoUnBind(){
        LoadingDialog.show(MyRobotActivity.this);
        GotoBindRequest gotoBindRequest = new GotoBindRequest();
        gotoBindRequest.setEquipmentId(AlphaApplication.currentRobotSN);
        gotoBindRequest.setSystemType("3");

        String url = HttpEntity.ROBOT_UNBIND;
        doRequest(url,gotoBindRequest,ROBOT_GOTO_UNBIND);

    }

    /**
     * 网路请求
     */
    public void doRequest(String url, BaseRequest baseRequest, final int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequestCheckIsBind onError:" + e.getMessage());
                switch (id){
                    case ROBOT_GOTO_UNBIND:
                        UbtLog.d(TAG, "解绑失败" );
                        LoadingDialog.dismiss(MyRobotActivity.this);
                        ToastUtils.showShort("解绑失败");

                        break;
                    case ROBOT_GOTO_BIND:
                        UbtLog.d(TAG, "绑定失败" );
                        if(robotBindingDialog != null && robotBindingDialog.isShowing()){
                            robotBindingDialog.display();
                        }
                        adviceBindFail("");

                        break;
                    case ROBOT_SET_AUTO_UPGRADE:
                        UbtLog.d(TAG, "设置自动升级功能失败" );
                        LoadingDialog.dismiss(MyRobotActivity.this);
                        ToastUtils.showShort("设置失败");
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"doRequestCheckIsBind response = " + response);
//				BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());
                BaseResponseModel<String> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<String>>(){}.getType());
                switch (id){
                    case ROBOT_GOTO_UNBIND:
                        UbtLog.d(TAG, "status:" + baseResponseModel.status);
                        LoadingDialog.dismiss(MyRobotActivity.this);
                        if(baseResponseModel.status){
                            UbtLog.d(TAG, "解绑成功" );
                            ToastUtils.showShort("解绑成功");
                            if (((AlphaApplication) MyRobotActivity.this.getApplicationContext())
                                    .getCurrentBluetooth() == null) {
                                mHandler.sendEmptyMessage(NO_CONNECTED_AND_NOBINDED);
                            }else {
                                mHandler.sendEmptyMessage(CONNECTED_NO_BIND);
                            }
                        }else {
                            UbtLog.d(TAG, "解绑失败" );
                            ToastUtils.showShort("解绑失败");
                        }

                        break;
                    case ROBOT_GOTO_BIND:
                        if(robotBindingDialog != null && robotBindingDialog.isShowing()){
                            robotBindingDialog.display();
                        }
                        UbtLog.d(TAG, "status:" + baseResponseModel.status);
                        UbtLog.d(TAG, "info:" + baseResponseModel.info);
                        if(baseResponseModel.status){
                            UbtLog.d(TAG, "绑定成功" );
                            if(baseResponseModel.models == null || baseResponseModel.models.equals("")){
                                adviceBindSuccess();
                            }else if(baseResponseModel.models != null && baseResponseModel.models.equals("1002")){
                                adviceBindFail("机器人已被他人绑定！");
                            }else if(baseResponseModel.models != null && baseResponseModel.models.equals("1004")){
                                adviceBindFail("机器人不存在！");
                            }
                        }else {
                            adviceBindFail("");
                            UbtLog.d(TAG, "绑定失败" );
                        }
                        break;
                    case ROBOT_SET_AUTO_UPGRADE:
                        UbtLog.d(TAG, "设置自动升级功能成功" );
                        LoadingDialog.dismiss(MyRobotActivity.this);
                        if(baseResponseModel.status){
                            UbtLog.d(TAG, "设置成功" );
                            mHandler.sendEmptyMessage(AUTO_UPGRADE_STATUS);
                        }else {
                            UbtLog.d(TAG, "设置失败" );
                            ToastUtils.showShort("设置失败");
                        }
                        break;
                    default:
                        break;
                }
            }
        });

    }

    //解绑机器人后，将无法使用
    public void adviceUnBind(){
        new UnbindConfirmDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("解绑机器人后，将无法使用：")
                .setUnbindMsg("1、“行为习惯养成”功能\n" +
                        "2、控制机器人版本升级\n"+"  确定要解绑吗？")
                .setCancelable(true)
                .setPositiveButton("解绑", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoUnBind();
                        UbtLog.d(TAG, "解绑 ");
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "取消 ");
                    }
                })
                .show();
    }

    //绑定成功！
    public void adviceBindSuccess(){
        Drawable img_ok;
        Resources res1 = getResources();
        img_ok = res1.getDrawable(R.drawable.ic_bind_success);
        new RobotBindDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("绑定成功！")
                .setMsg("可到“个人中心-设置-我的机器人”查看状态。")
                .setCancelable(false)
                .setPositiveButton("我知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "我知道了 ");
                        MyRobotActivity.this.finish();
                        LoginManger.getInstance().init(AppManager.getInstance().currentActivity(),null);
                        LoginManger.getInstance().toUserCenter(SPUtils.getInstance().getString(Constant.SP_ROBOT_DSN));

                    }
                })
                .setTitlePicture(img_ok)
                .setNoTitleLayout()
                .show();
    }

    //绑定失败！
    public void adviceBindFail(String reason){
        Drawable img_off;
        Resources res2 = getResources();
        img_off = res2.getDrawable(R.drawable.ic_bind_fail);
        new RobotBindDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("绑定失败！")
                .setMsg(reason)
                .setCancelable(true)
                .setPositiveButton("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "重试 ");
                        gotoBind();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "取消 ");
                    }
                })
                .setTitlePicture(img_off)
                .setNoTitleLayout()
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(robotBindingDialog != null && robotBindingDialog.isShowing()){
                robotBindingDialog.display();
                robotBindingDialog = null ;
                return false;
            }else {
                return super.onKeyDown(keyCode, event);
            }
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(NO_CONNECTED_AND_NOBINDED);
        mHandler.removeMessages(BINDED);
        mHandler.removeMessages(CONNECTED_NO_BIND);
    }
}
