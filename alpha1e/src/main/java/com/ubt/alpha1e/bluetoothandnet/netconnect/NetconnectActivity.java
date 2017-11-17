package com.ubt.alpha1e.bluetoothandnet.netconnect;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.loading.LoadingDialog;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothDeviceModel;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.event.NetworkEvent;
import com.ubt.alpha1e.login.LoginActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.RobotNetConnectActivity;
import com.ubt.alpha1e.ui.custom.VisiblePswEditText;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.dialog.NetConnectAlertDialog;
import com.ubt.alpha1e.ui.dialog.WifiSelectAlertDialog;
import com.ubt.alpha1e.ui.helper.NetworkHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;


/**
 * @author: dicy.cheng
 * @description:  网络连接
 * @create: 2017/11/4
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class NetconnectActivity extends MVPBaseActivity<NetconnectContract.View, NetconnectPresenter> implements NetconnectContract.View , View.OnClickListener{


    String TAG = "NetconnectActivity";

    @BindView(R.id.ib_close)
    ImageButton ib_close;

    @BindView(R.id.ib_return)
    ImageButton ib_return;

    @BindView(R.id.ig_wifi)
    ImageView ig_wifi;

    @BindView(R.id.ed_wifi_name)
    EditText ed_wifi_name;

    @BindView(R.id.ig_get_wifi_name)
    ImageView ig_get_wifi_name;

    @BindView(R.id.ig_wifi_pwd)
    ImageView ig_wifi_pwd;

    @BindView(R.id.ed_wifi_pwd)
    EditText ed_wifi_pwd;

    @BindView(R.id.ig_see_wifi_pwd)
    ImageView ig_see_wifi_pwd;

    @BindView(R.id.btn_send_wifi_pwd)
    Button btn_send_wifi_pwd;

    @BindView(R.id.rl_net_list)
    RelativeLayout rl_net_list;

    @BindView(R.id.net_device_list)
    RecyclerView net_device_list;

    //定义类常量
    private static final int NETWORK_CONNECT_SUCCESS = 1; //连接成功
    private static final int NETWORK_CONNECT_FAIL = 2;    //连接失败
    private static final int NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY = 3;    //连接对话框消失
    private static final int NETWORK_CONNECT_FAIL_DIALOG_DISPLAY = 4;    //连接对话框消失
    private static final int UPDATE_WIFI_NAME = 5;    //更新网络连接名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        mHelper = new NetworkHelper(NetconnectActivity.this);
    }

    boolean isFirst = false ;
    @Override
    protected void initUI() {
        ib_close.setOnClickListener(this);
        ib_return.setOnClickListener(this);
        btn_send_wifi_pwd.setOnClickListener(this);
        ig_see_wifi_pwd.setOnClickListener(this);
        ig_get_wifi_name.setOnClickListener(this);

        rl_net_list.setVisibility(View.INVISIBLE);

        Intent i = getIntent();
        String name = i.getStringExtra("wifiName");
        UbtLog.d(TAG,"name ==="+wifiName);
        if(name == null){
            isFirst = true ;
            ib_return.setVisibility(View.INVISIBLE);
            initData();
        }else {
            isFirst = false ;
            wifiName = name ;
            ed_wifi_name.setText(wifiName);
            ed_wifi_pwd.requestFocus();
        }
        ed_wifi_name.addTextChangedListener(mTextWatcher);
        ed_wifi_pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                ed_wifi_pwd.setSelection(ed_wifi_pwd.getText().length());
            }
        });
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_netconnect;
    }

    int seePWD = 0; //1 看密码  0 隐藏
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                NetconnectActivity.this.finish();
                break;
            case R.id.ib_return:
                NetconnectActivity.this.finish();
                break;
            case R.id.ig_get_wifi_name:
                UbtLog.d(TAG,"ig_get_wifi_name click");
                gotoSelectWifi();
                break;
            case R.id.ig_see_wifi_pwd:
                if(seePWD == 0){
                    seePWD = 1 ;
                    ig_see_wifi_pwd.setBackground(ContextCompat.getDrawable(NetconnectActivity.this,R.drawable.net_see_pwd));
                    ed_wifi_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ed_wifi_pwd.setSelection(ed_wifi_pwd.getText().length());
                 }else {
                    seePWD = 0 ;
                    ig_see_wifi_pwd.setBackground(ContextCompat.getDrawable(NetconnectActivity.this,R.drawable.net_see_no_pwd));
                    ed_wifi_pwd.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ed_wifi_pwd.setSelection(ed_wifi_pwd.getText().length());
                }

                break;
            case R.id.btn_send_wifi_pwd:
                doNetConnect();
                break;
            default:

        }
    }

    // 去联网
    void doNetConnect(){
        if(TextUtils.isEmpty(ed_wifi_name.getText().toString())){
            new AlertDialog(NetconnectActivity.this)
                    .builder()
                    .setTitle(getStringResources("ui_network_prompt"))
                    .setMsg(getStringResources("ui_netwok_robot_need_network"))
                    .setCancelable(false)
                    .setPositiveButton(getStringResources("ui_network_go_connect_net"), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gotoSelectWifi();
                        }
                    }).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }).show();
            return;
        }

        //密码为null,弹出提示
        if(TextUtils.isEmpty(ed_wifi_pwd.getText().toString())){
            new AlertDialog(NetconnectActivity.this)
                    .builder()
                    .setTitle(getStringResources("ui_network_no_password_tips"))
                    .setMsg(getStringResources("ui_network_un_pass_tip"))
                    .setCancelable(false)
                    .setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startNetwork();
                        }
                    }).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }).show();
        }else {
            startNetwork();
        }
    }

    /**
     * 开始联网
     */
    private void startNetwork(){
        LoadingDialog.show(this,"联网中...");
        ((NetworkHelper)mHelper).doConnectNetwork(ed_wifi_name.getText().toString(), ed_wifi_pwd.getText().toString());
    }

    /**
     * 文本内容改变监听器
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable editable) {
            UbtLog.d(TAG,"afterTextChanged 改变");
            setNetworkButtonState();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }
    };

    /**
     * 文本内容改变，按钮处理逻辑
     * 内容为空时，不可点击，非空时，可以点击
     */
    private void setNetworkButtonState(){

        if(ed_wifi_name.getText().toString().trim().length() > 0){
            UbtLog.d(TAG,"afterTextChanged ENBLE");
            btn_send_wifi_pwd.setEnabled(true);
        }else{
            UbtLog.d(TAG,"afterTextChanged DISABLE");
            btn_send_wifi_pwd.setEnabled(false);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NETWORK_CONNECT_SUCCESS:
                    mHandler.sendEmptyMessageDelayed(NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY,200);

                    break;
                case NETWORK_CONNECT_FAIL:
                    UbtLog.d(TAG,"网络连接失败！  1 " );
                    mHandler.sendEmptyMessageDelayed(NETWORK_CONNECT_FAIL_DIALOG_DISPLAY,200);
                    break;
                case NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY:
                    displayDialog();
                    UbtLog.d(TAG,"网络连接成功 wifiName: " +wifiName  );

                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putExtra("wifiName", ed_wifi_name.getText().toString());
                    intent.putExtra("isConnectSucces","yes");
                    //设置返回数据
                    NetconnectActivity.this.setResult(RESULT_OK, intent);
                    //关闭Activity
                    NetconnectActivity.this.finish();
                    break;
                case NETWORK_CONNECT_FAIL_DIALOG_DISPLAY:
                    displayDialog();
                    ToastUtils.showCustomShort(R.layout.bluetooth_wifi_connect_fail);
                    break;
                case UPDATE_WIFI_NAME:
                    String remoteConnectName = (String) msg.obj;
                    if(!TextUtils.isEmpty(remoteConnectName)){
                        ed_wifi_name.setText(remoteConnectName);
                        ed_wifi_pwd.requestFocus();
                    }else {
                        NetworkInfo mWifiInfo = getCurrentLocalConnectNetworkInfo();
                        if (mWifiInfo.isConnected()) {
                            UbtLog.d(TAG,"mWifiInfo.getExtraInfo() == " + mWifiInfo.getExtraInfo());
                            ed_wifi_name.setText(mWifiInfo.getExtraInfo().replaceAll("\"",""));
                            ed_wifi_pwd.requestFocus();
                        }else {
                            ed_wifi_name.setText("");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 消失对话框
     */
    private void displayDialog(){
        LoadingDialog.dismiss(NetconnectActivity.this);
    }

    /**
     * 获取当前手机连接WIFI名称
     * @return
     */
    private NetworkInfo getCurrentLocalConnectNetworkInfo(){

        //当前连接WIFI对象
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi;
    }

    /**
     * 更新网络连接名称
     * @param event
     */
    private void updateNetworkConnectName(NetworkEvent event){
        UbtLog.d(TAG,"event.getConnectWifiName() = " + event.getConnectWifiName());
        Message msg = new Message();
        msg.what = UPDATE_WIFI_NAME;
        msg.obj = event.getConnectWifiName();
        mHandler.sendMessage(msg);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        Message msg = new Message();
        msg.what = UPDATE_WIFI_NAME;
        msg.obj = "";
        mHandler.sendMessage(msg);
    }

    String wifiName = "";
    /**
     * 监听Eventbus消息方法
     * @param event
     */
    @Subscribe
    public void onEventNetwork(NetworkEvent event) {
        if(event.getEvent() == NetworkEvent.Event.CHANGE_SELECT_WIFI){

            wifiName = event.getSelectWifiName();
            UbtLog.d(TAG,"选择的wifi ："+wifiName);
            ed_wifi_name.setText(event.getSelectWifiName());
            ed_wifi_pwd.requestFocus();
        }else if(event.getEvent() == NetworkEvent.Event.DO_CONNECT_WIFI){
            updateNetworkConnectStatus(event);
        }else if(event.getEvent() == NetworkEvent.Event.NETWORK_STATUS){
            updateNetworkConnectName(event);
        }
    }

    /**
     * 更新网络连接逻辑
     * @param event
     */
    private void updateNetworkConnectStatus(NetworkEvent event){
        int connectStatus = event.getConnectStatus();
        UbtLog.d(TAG,"connectStatus === " + connectStatus);
        if(connectStatus == 1){
            //联网中
        }else if(connectStatus == 2){
            mHandler.sendEmptyMessage(NETWORK_CONNECT_SUCCESS);
        }else if(connectStatus == 3){
            mHandler.sendEmptyMessage(NETWORK_CONNECT_FAIL);
        }
    }

    /**
     * 选择WIFI,进行联网
     */
    private void gotoSelectWifi(){
        new WifiSelectAlertDialog(NetconnectActivity.this)
                .setmCurrentSelectWifiName(ed_wifi_name.getText().toString())
                .builder()
                .setTitle("可用网络列表")
                .setCancelable(true)
                .setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHelper != null){
            //读取更新当前机器人网络状态
//            ((NetworkHelper)mHelper).readNetworkStatus();
        }
        mHandler.removeMessages(NETWORK_CONNECT_SUCCESS);
        mHandler.removeMessages(NETWORK_CONNECT_FAIL);
        mHandler.removeMessages(NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY);
        mHandler.removeMessages(UPDATE_WIFI_NAME);
    }

    @Override
    public void finish() {
        super.finish();

        if(isFirst){
            //关闭窗体动画显示
            this.overridePendingTransition(0,R.anim.activity_close_down_up);
        }
    }
}
