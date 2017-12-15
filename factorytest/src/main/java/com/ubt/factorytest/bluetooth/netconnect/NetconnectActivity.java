package com.ubt.factorytest.bluetooth.netconnect;


import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ubt.factorytest.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * @author: dicy.cheng
 * @description:  网络连接
 * @create: 2017/11/4
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class NetconnectActivity extends Activity implements View.OnClickListener{


    String TAG = "NetconnectActivity";

//    @BindView(R.id.ib_close)
    ImageButton ib_close;
//
//    @BindView(R.id.ib_return)
    ImageButton ib_return;
//
//    @BindView(R.id.ig_wifi)
//    ImageView ig_wifi;
//
//    @BindView(R.id.ed_wifi_name)
    EditText ed_wifi_name;
//
//    @BindView(R.id.ig_get_wifi_name)
    ImageView ig_get_wifi_name;
//
//    @BindView(R.id.ig_wifi_pwd)
//    ImageView ig_wifi_pwd;
//
//    @BindView(R.id.ed_wifi_pwd)
    EditText ed_wifi_pwd;
//
//    @BindView(R.id.ig_see_wifi_pwd)
    ImageView ig_see_wifi_pwd;
//
//    @BindView(R.id.btn_send_wifi_pwd)
    Button btn_send_wifi_pwd;
//
//    @BindView(R.id.rl_net_list)
    RelativeLayout rl_net_list;
//
//    @BindView(R.id.net_device_list)
//    RecyclerView net_device_list;

    //定义类常量
    private static final int NETWORK_CONNECT_SUCCESS = 1; //连接成功
    private static final int NETWORK_CONNECT_FAIL = 2;    //连接失败
    private static final int NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY = 3;    //连接对话框消失
    private static final int NETWORK_CONNECT_FAIL_DIALOG_DISPLAY = 4;    //连接对话框消失
    private static final int UPDATE_WIFI_NAME = 5;    //更新网络连接名称

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_connect);

        initUI();
    }

    boolean isFirst = false ;

    protected void initUI() {
        ib_close = (ImageButton)this.findViewById(R.id.ib_close );
        ib_return= (ImageButton)this.findViewById(R.id.ib_return );
        btn_send_wifi_pwd = (Button)this.findViewById(R.id.btn_send_wifi_pwd );
        ig_see_wifi_pwd = (ImageView)this.findViewById(R.id.ig_see_wifi_pwd );
        ig_get_wifi_name = (ImageView)this.findViewById(R.id.ig_get_wifi_name );
        rl_net_list= (RelativeLayout)this.findViewById(R.id.rl_net_list);
        ed_wifi_name = (EditText)this.findViewById(R.id.ed_wifi_name );
        ed_wifi_pwd= (EditText)this.findViewById(R.id.ed_wifi_pwd );
        ib_close.setOnClickListener(this);
        btn_send_wifi_pwd.setOnClickListener(this);
        ig_see_wifi_pwd.setOnClickListener(this);
        ig_get_wifi_name.setOnClickListener(this);

        rl_net_list.setVisibility(View.INVISIBLE);
//
//        Intent i = getIntent();
//        String name = i.getStringExtra("wifiName");
//        if(name == null){
//            isFirst = true ;
            ib_return.setVisibility(View.INVISIBLE);
////            initData();
//        }else {
//            isFirst = false ;
//            wifiName = name ;
//            ed_wifi_name.setText(wifiName);
//            ed_wifi_pwd.requestFocus();
//        }
        ed_wifi_name.addTextChangedListener(mTextWatcher);
        ed_wifi_pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                ed_wifi_pwd.setSelection(ed_wifi_pwd.getText().length());
            }
        });
        EventBus.getDefault().register(this);
    }


    int seePWD = 0; //1 看密码  0 隐藏
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
//                Intent intent = new Intent();
//                intent.setClass(NetconnectActivity.this,MainActivity.class);
//                startActivity(intent);
                NetconnectActivity.this.finish();
                break;
            case R.id.ib_return:
                NetconnectActivity.this.finish();
                break;
            case R.id.ig_get_wifi_name:
                Log.d(TAG," ccy ig_get_wifi_name CLICK" );
                if(!isOPen(this)){
                    new ConfirmDialog(this).builder()
                            .setTitle("提示")
                            .setMsg("请在手机的设置中把“位置服务”打开")
                            .setCancelable(true)
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            }).show();
                    return;
                }
                gotoSelectWifi();
                break;
            case R.id.ig_see_wifi_pwd:
                if(TextUtils.isEmpty(ed_wifi_pwd.getText().toString())){
                    return;
                }
                if(ig_see_wifi_pwd == null){
                    return;
                }
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


    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    // 去联网
    void doNetConnect(){
        if(TextUtils.isEmpty(ed_wifi_name.getText().toString())){
            new AlertDialog(NetconnectActivity.this)
                    .builder()
                    .setTitle("联网提示")
                    .setMsg("机器人需联网才能执行该操作哦")
                    .setCancelable(false)
                    .setPositiveButton("去联网", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gotoSelectWifi();
                        }
                    }).setNegativeButton("取消", new View.OnClickListener() {
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
                    .setTitle("无密码提示")
                    .setMsg("您未输入Wi-Fi密码，确定继续连接么？")
                    .setCancelable(false)
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startNetwork();
                        }
                    }).setNegativeButton("取消", new View.OnClickListener() {
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
        Log.d(TAG," ccy startNetwork " );
//        if (((AlphaApplication) this.getApplicationContext())
//                .getCurrentBluetooth() == null) {
////            UbtLog.d(TAG,"蓝牙已经断开");
////            ToastUtils.showShort("蓝牙已经断开，请重新连接蓝牙");
//            NetconnectActivity.this.finish();
//            return;
//        }
        mHandler.postDelayed(overTimeDo,60000);
        LoadingDialog.show(this,"联网中...");
//        ((NetworkHelper)mHelper).doConnectNetwork(ed_wifi_name.getText().toString(), ed_wifi_pwd.getText().toString());
    }

    Runnable overTimeDo = new Runnable() {
        @Override
        public void run() {
            LoadingDialog.dismiss(NetconnectActivity.this);
//            ToastUtils.showShort("机器人wifi连接失败");
            Toast.makeText(NetconnectActivity.this,"机器人wifi连接失败",Toast.LENGTH_SHORT);
        }
    };

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
//            UbtLog.d(TAG,"afterTextChanged 改变");
//
//            setNetworkButtonState();
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
//    private void setNetworkButtonState(){
//        if(ed_wifi_name == null){
//            return;
//        }
//        if(ed_wifi_name.getText().toString().trim().length() > 0){
//            UbtLog.d(TAG,"afterTextChanged ENBLE");
//            btn_send_wifi_pwd.setEnabled(true);
//        }else{
//            UbtLog.d(TAG,"afterTextChanged DISABLE");
//            btn_send_wifi_pwd.setEnabled(false);
//        }
//    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NETWORK_CONNECT_SUCCESS:
                    mHandler.sendEmptyMessageDelayed(NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY,200);

                    break;
//                case NETWORK_CONNECT_FAIL:
//                    UbtLog.d(TAG,"网络连接失败！  1 " );
//                    mHandler.sendEmptyMessageDelayed(NETWORK_CONNECT_FAIL_DIALOG_DISPLAY,200);
//                    break;
//                case NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY:
//                    displayDialog();
//                    UbtLog.d(TAG,"网络连接成功 wifiName: " +wifiName  );
//
//                    Intent intent = new Intent();
//                    //把返回数据存入Intent
//                    intent.putExtra("wifiName", ed_wifi_name.getText().toString());
//                    intent.putExtra("isConnectSucces","yes");
//                    //设置返回数据
//                    NetconnectActivity.this.setResult(RESULT_OK, intent);
//                    //关闭Activity
//                    NetconnectActivity.this.finish();
//                    break;
//                case NETWORK_CONNECT_FAIL_DIALOG_DISPLAY:
//                    displayDialog();
//                    ToastUtils.showCustomShortWithGravity(R.layout.bluetooth_wifi_connect_fail,
//                            Gravity.CENTER, 0 , 0);
//                    break;
//                case UPDATE_WIFI_NAME:
//                    String remoteConnectName = (String) msg.obj;
//                    if(!TextUtils.isEmpty(remoteConnectName)){
//                        if(ed_wifi_name == null){
//                            return;
//                        }
//                        ed_wifi_name.setText(remoteConnectName);
//                        ed_wifi_pwd.requestFocus();
//                    }else {
//                        NetworkInfo mWifiInfo = getCurrentLocalConnectNetworkInfo();
//                        if (mWifiInfo.isConnected()) {
//                            UbtLog.d(TAG,"mWifiInfo.getExtraInfo() == " + mWifiInfo.getExtraInfo());
//                            if(ed_wifi_name == null){
//                                return;
//                            }
//                            ed_wifi_name.setText(mWifiInfo.getExtraInfo().replaceAll("\"",""));
//                            ed_wifi_pwd.requestFocus();
//                        }else {
//                            if(ed_wifi_name == null){
//                                return;
//                            }
//                            ed_wifi_name.setText("");
//                        }
//                    }
//                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 消失对话框
     */
//    private void displayDialog(){
//        mHandler.removeCallbacks(overTimeDo);
//        LoadingDialog.dismiss(NetconnectActivity.this);
//    }

    /**
     * 获取当前手机连接WIFI名称
     * @return
     */
//    private NetworkInfo getCurrentLocalConnectNetworkInfo(){
//
//        //当前连接WIFI对象
//        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        return mWifi;
//    }

    /**
     * 更新网络连接名称
     * @param event
     */
    private void updateNetworkConnectName(NetworkEvent event){
       // UbtLog.d(TAG,"event.getConnectWifiName() = " + event.getConnectWifiName());
        Message msg = new Message();
        msg.what = UPDATE_WIFI_NAME;
        msg.obj = event.getConnectWifiName();
        mHandler.sendMessage(msg);
    }
//
//    /**
//     * 初始化数据
//     */
//    private void initData(){
//        Message msg = new Message();
//        msg.what = UPDATE_WIFI_NAME;
//        msg.obj = "";
//        mHandler.sendMessage(msg);
//    }
//
    String wifiName = "";
    /**
     * 监听Eventbus消息方法
     * @param event
     */
    @Subscribe
    public void onEventNetwork(NetworkEvent event) {
        if(event.getEvent() == NetworkEvent.Event.CHANGE_SELECT_WIFI){

            wifiName = event.getSelectWifiName();
            Log.d(TAG,"ccy 选择的wifi ："+wifiName);
            if(ed_wifi_name == null){
                return;
            }
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
        //UbtLog.d(TAG,"connectStatus === " + connectStatus);
        if(connectStatus == 1){
            //联网中
        }else if(connectStatus == 2){
//            NetworkInfo networkInfo = new NetworkInfo();
//            com.ubt.alpha1e.data.model.NetworkInfo networkInfo = new com.ubt.alpha1e.data.model.NetworkInfo();
//            networkInfo.status = true;
//            networkInfo.name = event.getConnectWifiName();

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
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
//        LoadingDialog.dismiss(this);
        super.onDestroy();
//        if(mHelper != null){
//            //读取更新当前机器人网络状态
////            ((NetworkHelper)mHelper).readNetworkStatus();
//        }
//        mHandler.removeMessages(NETWORK_CONNECT_SUCCESS);
//        mHandler.removeMessages(NETWORK_CONNECT_FAIL);
//        mHandler.removeMessages(NETWORK_CONNECT_SUCCESS_DIALOG_DISPLAY);
//        mHandler.removeMessages(UPDATE_WIFI_NAME);
//        mHandler.removeCallbacks(overTimeDo);
    }

    @Override
    public void finish() {
        super.finish();

//        if(isFirst){
//            //关闭窗体动画显示
//            this.overridePendingTransition(0,R.anim.activity_close_down_up);
//        }
    }
}