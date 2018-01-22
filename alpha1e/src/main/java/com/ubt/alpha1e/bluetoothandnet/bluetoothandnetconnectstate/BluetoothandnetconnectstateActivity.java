package com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.bluetoothandnet.netsearchresult.NetSearchResultActivity;
import com.ubt.alpha1e.course.feature.FeatureActivity;
import com.ubt.alpha1e.course.merge.MergeActivity;
import com.ubt.alpha1e.course.principle.PrincipleActivity;
import com.ubt.alpha1e.course.split.SplitActivity;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.BluetoothStateHelper;
import com.ubt.alpha1e.ui.main.MainUiBtHelper;
import com.ubt.alpha1e.utils.AviodTwoClicksUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;


/**
 * @author: dicy.cheng
 * @description:  连接状态
 * @create: 2017/11/6
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class BluetoothandnetconnectstateActivity extends MVPBaseActivity<BluetoothandnetconnectstateContract.View, BluetoothandnetconnectstatePresenter> implements BluetoothandnetconnectstateContract.View , View.OnClickListener {

    String TAG = "BluetoothandnetconnectstateActivity";

    @BindView(R.id.ib_return)
    ImageButton ib_return;

    @BindView(R.id.ib_close)
    ImageButton ib_close;

    //上面是大布局
    @BindView(R.id.rl_state1_up_big)
    RelativeLayout rl_state1_up_big;

    @BindView(R.id.ig_rl_state1_robot)
    ImageView ig_rl_state1_robot;

    @BindView(R.id.ed_state1_bluetooth_name)
    EditText ed_state1_bluetooth_name;

    @BindView(R.id.ig_state1_get_bluetooth_list)
    Button ig_state1_get_bluetooth_list;

    @BindView(R.id.ig_state1_wifi)
    ImageView ig_state1_wifi;

    @BindView(R.id.ed_state1_wifi_name)
    EditText ed_state1_wifi_name;

    @BindView(R.id.ig_state1_goto_connect_net)
    TextView ig_state1_goto_connect_net;

    //上面是小布局
    @BindView(R.id.rl_state2_up_small)
    RelativeLayout rl_state2_up_small;

    @BindView(R.id.ig_state2_wifi)
    ImageView ig_state2_wifi;

    @BindView(R.id.ed_state2_wifi_name)
    EditText ed_state2_wifi_name;

    @BindView(R.id.ig_state2_goto_connect_net)
    TextView ig_state2_goto_connect_net;

    @BindView(R.id.ed_state2_bluetooth_name)
    EditText ed_state2_bluetooth_name;

    @BindView(R.id.ig_state2_get_bluetooth_list)
    Button ig_state2_get_bluetooth_list;

    @BindView(R.id.tv_advice1)
    TextView tv_advice1;

    private static final int UPDATE_WIFI_STATUS = 1; //更新WIFI状态
    public static final int MSG_DO_BLUETOOTH_DISCONNECT = 8; //蓝牙断开
    public static final int CLOSED_ACTIVITY = 9; //关闭页面


    public static final int REQUEST_CODE = 500;
    public static final int REQUEST_CODE_ENTER_NETSEARCHRESULT = 501;

    private List<Map<String, Object>> lst_robots_result_datas;

    public BaseHelper mBluetoothStateHelper;

    //定义Handler处理对象
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_WIFI_STATUS:
                    NetworkInfo networkInfo = (NetworkInfo) msg.obj;
                    UbtLog.d(TAG,"networkInfo == " + networkInfo);
                    if(networkInfo.status){
                        if(!MainUiBtHelper.getInstance(getContext()).isLostCoon()){
                            UbtLog.d(TAG,"bluetoothandnetstate CONNECT_SUCCESS 2");
                            BluetoothDevice b = (BluetoothDevice)((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).getCurrentBluetooth();
                            String name = b.getName();
                            bluetoothAndNetConnect(name,networkInfo.name);
                            ((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).setmCurrentNetworkInfo(networkInfo);
                        }else {
                            bluetoothDisconnect();
                            ((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).setmCurrentNetworkInfo(null);
                        }
                    }else {
                        BluetoothDevice b = (BluetoothDevice)((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).getCurrentBluetooth();
                        String name = b.getName();
                        onlyBluetoothConnect(name);
                        ((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).setmCurrentNetworkInfo(null);
                    }
                    break;
                case MSG_DO_BLUETOOTH_DISCONNECT:
                    UbtLog.d(TAG,"MSG_DO_BLUETOOTH_DISCONNECT ..... " );
                    ((AlphaApplication) getApplicationContext()).doLostConnect();
                    bluetoothDisconnect();
                    ((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).setmCurrentNetworkInfo(null);
                    break;
                case CLOSED_ACTIVITY:
                    BluetoothandnetconnectstateActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lst_robots_result_datas = new ArrayList<>();
        mBluetoothStateHelper = BluetoothStateHelper.getInstance(getContext());
        initUI();
    }

    @Override
    protected void initUI() {
        ib_return.setOnClickListener(this);
        ib_close.setOnClickListener(this);
        ig_state1_get_bluetooth_list.setOnClickListener(this);
        ig_state1_goto_connect_net.setOnClickListener(this);
        ig_state2_goto_connect_net.setOnClickListener(this);
        ig_state2_get_bluetooth_list.setOnClickListener(this);
        bluetoothDisconnect();

    }

    //蓝牙断开状态
    void bluetoothDisconnect(){
        UbtLog.d(TAG, "蓝牙断开状态!");
        if(rl_state1_up_big == null){
            return;
        }
        rl_state1_up_big.setVisibility(View.VISIBLE);
        rl_state2_up_small.setVisibility(View.INVISIBLE);
        ig_rl_state1_robot.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.img_robot_fail));
        ed_state1_bluetooth_name.setText("");
        ig_state1_get_bluetooth_list.setText("连接");
        ig_state1_wifi.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.bluetooth_wifi_abnomal));
        ed_state1_wifi_name.setText("");
        ig_state1_goto_connect_net.setText("联网");
        ig_state1_goto_connect_net.setTextColor(Color.parseColor("#C1C1C1"));
        tv_advice1.setVisibility(View.INVISIBLE);
    }

    //蓝牙和网络同时连接
    void bluetoothAndNetConnect(String bluetoothName,String wifiName){
        UbtLog.d(TAG, "蓝牙和网络同时连接!");
        if(rl_state1_up_big == null){
            return;
        }
        rl_state1_up_big.setVisibility(View.VISIBLE);
        rl_state2_up_small.setVisibility(View.INVISIBLE);
        ig_rl_state1_robot.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.img_alpha_connected_s));
        ed_state1_bluetooth_name.setText(bluetoothName);
        ig_state1_get_bluetooth_list.setText("断开连接");
        ig_state1_wifi.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.img_wifi_connected));
        ed_state1_wifi_name.setText(wifiName);
        ig_state1_goto_connect_net.setText("切换Wi-Fi");
        ig_state1_goto_connect_net.setTextColor(Color.parseColor("#02AAE8"));
        tv_advice1.setVisibility(View.INVISIBLE);
    }

    //只有蓝牙连接
    void onlyBluetoothConnect(String bluetoothName){
        UbtLog.d(TAG, "只有蓝牙连接!");
        if(rl_state1_up_big == null){
            return;
        }
        rl_state1_up_big.setVisibility(View.INVISIBLE);
        rl_state2_up_small.setVisibility(View.VISIBLE);
        tv_advice1.setVisibility(View.VISIBLE);
        ig_state2_wifi.setBackground(ContextCompat.getDrawable(BluetoothandnetconnectstateActivity.this,R.drawable.img_alpha_connected_b));
        ed_state2_wifi_name.setText(bluetoothName);
        ig_state2_goto_connect_net.setText("断开连接");
    }


    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.bluetooth_and_net_connect_state;
    }

    @Subscribe
    public void onEventRobot(RobotEvent event) {
        super.onEventRobot(event);
        UbtLog.d(TAG,"bluetoothandnetstate onEventRobot state:"+event.getEvent());
        if(event.getEvent() == RobotEvent.Event.NETWORK_STATUS){
            updateNetworkStatus(event);
        }else if(event.getEvent() == RobotEvent.Event.DISCONNECT){
            onBluetoothDisconnect(event);
        }else if(event.getEvent()== RobotEvent.Event.CONNECT_SUCCESS){
            UbtLog.d(TAG,"bluetoothandnetstate CONNECT_SUCCESS 1");
            if(!MainUiBtHelper.getInstance(getContext()).isLostCoon()){
                UbtLog.d(TAG,"bluetoothandnetstate CONNECT_SUCCESS 2");
                BluetoothDevice b = (BluetoothDevice)((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).getCurrentBluetooth();
                String name = b.getName();
                String macAddr = b.getAddress();
                UbtLog.d(TAG,"当前连接设备："+name +" mac地址："+macAddr);
                onlyBluetoothConnect(name);

                BluetoothStateHelper.getInstance(getContext()).readNetworkStatus();
            }
        }
    }

    /**
     * 蓝牙断开
     * @param event
     */
    private void onBluetoothDisconnect(RobotEvent event){
        mHandler.sendEmptyMessage(MSG_DO_BLUETOOTH_DISCONNECT);
    }

    /**
     * 更新网络状态
     * @param event
     */
    private void updateNetworkStatus(RobotEvent event){
        Message msg = new Message();
        msg.what = UPDATE_WIFI_STATUS;
        msg.obj = event.getNetworkInfo();
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (BluetoothStateHelper.getInstance(getContext()) != null) {
            BluetoothStateHelper.getInstance(getContext()).UnRegisterHelper();
        }
    }

    @Override
    public void onDestroy() {
        UbtLog.d(TAG,"---onDestroy----");
        try {
            this.mBluetoothStateHelper.DistoryHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mBluetoothStateHelper != null){
            mBluetoothStateHelper.RegisterHelper();
        }
        if(!BluetoothStateHelper.getInstance(getContext()).isLostCoon()){
            BluetoothDevice b = (BluetoothDevice)((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).getCurrentBluetooth();
            String name = b.getName();
            String macAddr = b.getAddress();
            UbtLog.d(TAG,"当前连接设备："+name +" mac地址："+macAddr);
            onlyBluetoothConnect(name);

            BluetoothStateHelper.getInstance(getContext()).readNetworkStatus();
            if(BluetoothandnetconnectstateActivity.this != null && ((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).getmCurrentNetworkInfo() != null){
                NetworkInfo networkInfo = ((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).getmCurrentNetworkInfo();
                UbtLog.d(TAG,"网络和蓝牙都连接了  "+ networkInfo.name);
                bluetoothAndNetConnect(name,networkInfo.name);
            }
        }else {
            bluetoothDisconnect();
            ((AlphaApplication) BluetoothandnetconnectstateActivity.this.getApplication()).setmCurrentNetworkInfo(null);
        }
    }

    @Override
    public void finish() {
        super.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0,R.anim.activity_close_down_up);
    }


    void startBluetoothConnect(){
        UbtLog.d(TAG,"获取蓝牙列表");
        Intent intent = new Intent();
        intent.putExtra("isFirst","no");
        intent.setClass(BluetoothandnetconnectstateActivity.this,BluetoothconnectActivity.class);
        this.startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            UbtLog.d(TAG, "onActivityResult RESULT_OK!");
            if (requestCode == REQUEST_CODE){
                dealUI();
                BluetoothandnetconnectstateActivity.this.finish();
            }else if(requestCode == REQUEST_CODE_ENTER_NETSEARCHRESULT){
                BluetoothandnetconnectstateActivity.this.finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_return:
//                BluetoothandnetconnectstateActivity.this.finish();
                break;
            case R.id.ib_close:
                dealUI();
                BluetoothandnetconnectstateActivity.this.finish();
                break;
            case R.id.ig_state1_get_bluetooth_list:
                if(AviodTwoClicksUtils.isFastClick()){
                    return;
                }
                if(!ig_state1_get_bluetooth_list.getText().toString().equals("连接")){
                    UbtLog.d(TAG,"点击断开连接1");
                    disconnectBluetoothDialog();
//                    BluetoothStateHelper.getInstance(getContext()).doCancelCoon();
//                    bluetoothDisconnect();
                    return;
                }
                BluetoothAdapter mBtAdapter;
                mBtAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBtAdapter.isEnabled()) {
                    UbtLog.d(TAG, "bluetoothEnable false ");
                    boolean bluetoothEnable = mBtAdapter.enable();
                    if(bluetoothEnable){
                        UbtLog.d(TAG, "bluetooth Enable true 判断是否授权");
                        if(PermissionUtils.getInstance(this).hasPermission(Permission.LOCATION)){
                            UbtLog.d(TAG, "bluetoothEnable true 有授权");//ok
                            startBluetoothConnect();
                        }else {
                            UbtLog.d(TAG, "bluetoothEnable true 没有授权");//ok
                            PermissionUtils.getInstance(this).showRationSettingDialog(PermissionUtils.PermissionEnum.LOACTION,this, null);
                        }
                    }else {
                        UbtLog.d(TAG, "bluetoothEnable false 提醒去打开蓝牙");//ok
                        new ConfirmDialog(this).builder()
                                .setTitle("提示")
                                .setMsg("请在手机的“设置->蓝牙”中打开蓝牙")
                                .setCancelable(true)
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UbtLog.d(TAG, "bluetoothEnable false onClick 1 ");
                                    }
                                }).show();
                    }
                }else {
                    UbtLog.d(TAG, "bluetooth enable  判断是否授权");
                    if(PermissionUtils.getInstance(this).hasPermission(Permission.LOCATION)){
                        UbtLog.d(TAG, "bluetoothEnable true 有授权");//ok
                        startBluetoothConnect();
                    }else {
                        UbtLog.d(TAG, "bluetoothEnable true 没有授权"); //ok
                        PermissionUtils.getInstance(this).showRationSettingDialog(PermissionUtils.PermissionEnum.LOACTION,this, null);
                    }
                }

                break;
            case R.id.ig_state2_get_bluetooth_list:
                if(AviodTwoClicksUtils.isFastClick()){
                    return;
                }
                searchNet(false);
                break;
            case R.id.ig_state1_goto_connect_net:
                if(AviodTwoClicksUtils.isFastClick()){
                    return;
                }
                searchNet(true);
                break;
            case R.id.ig_state2_goto_connect_net:
                UbtLog.d(TAG,"点击断开连接2");
                if(AviodTwoClicksUtils.isFastClick()){
                    return;
                }
                disconnectBluetoothDialog();
//                BluetoothStateHelper.getInstance(getContext()).doCancelCoon();
//                bluetoothDisconnect();
                break;

            default:
        }
    }

    //确认是否要断开蓝牙
    public void disconnectBluetoothDialog(){
        new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("提示")
                .setMsg("确定断开与机器人的蓝牙连接?")
                .setCancelable(true)
                .setPositiveButton("断开", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "断开 ");
                        BluetoothStateHelper.getInstance(getContext()).doCancelCoon();
                        bluetoothDisconnect();
                    }
                })
                .setNegativeButton("暂不", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "暂不 ");
                    }
                }).show();
    }


    void searchNet(boolean isConnectWifi){
        if(!BluetoothStateHelper.getInstance(getContext()).isLostCoon()){
            if(!isOPen(this)){
                UbtLog.d(TAG,"请先打开位置信息");
                new ConfirmDialog(this).builder()
                        .setTitle("提示")
                        .setMsg("请在手机的设置中把“位置服务”打开")
                        .setCancelable(true)
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UbtLog.d(TAG, "位置信息确定 ");
                            }
                        }).show();
                return;
            }

            Intent i = new Intent();
            if(isConnectWifi){
                i.putExtra("wifiName",ed_state1_wifi_name.getText().toString());
            }else {
                i.putExtra("wifiName","");
            }
            UbtLog.d(TAG,"ed_wifi_name===="+ed_state1_wifi_name.getText().toString());
            i.setClass(BluetoothandnetconnectstateActivity.this,NetSearchResultActivity.class);
            this.startActivityForResult(i,REQUEST_CODE_ENTER_NETSEARCHRESULT);

        }else {
            UbtLog.d(TAG,"请先连接蓝牙");
            ToastUtils.showShort("请先连接机器人");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() ==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBack() {
        dealUI();
    }

    //处理相应界面
    void dealUI(){
        if(BluetoothStateHelper.getInstance(getContext()).isLostCoon()){
            AppManager.getInstance().finishUseBluetoothActivity();
        }else {
            if(AlphaApplication.getmNeedOpenActivity() != null){
                if(AlphaApplication.getmNeedOpenActivity().equals(PrincipleActivity.class.getSimpleName())){
                    PrincipleActivity.launchActivity(this,false);
                }else if(AlphaApplication.getmNeedOpenActivity().equals(SplitActivity.class.getSimpleName())){
                    SplitActivity.launchActivity(this,false);
                }else if(AlphaApplication.getmNeedOpenActivity().equals(MergeActivity.class.getSimpleName())){
                    MergeActivity.launchActivity(this,false);
                }else if(AlphaApplication.getmNeedOpenActivity().equals(FeatureActivity.class.getSimpleName())){
                    FeatureActivity.launchActivity(this,false);
                }
            }
        }
        AlphaApplication.setmNeedOpenActivity(null);
    }
}
