package com.ubt.alpha1e.bluetoothandnet.netsearchresult;


import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.WifiInfoAdapter;
import com.ubt.alpha1e.adapter.WifiInfoAdapter_list;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.bluetoothandnet.netconnect.NetconnectActivity;
import com.ubt.alpha1e.event.NetworkEvent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.WifiSelectAlertDialog;
import com.ubt.alpha1e.ui.helper.WifiHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * @author: dicy.cheng
 * @description:  蓝牙连接引导页
 * @create: 2017/11/13
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class NetSearchResultActivity extends MVPBaseActivity<NetSearchResultContract.View, NetSearchResultPresenter> implements NetSearchResultContract.View  , View.OnClickListener{

    String TAG = "NetSearchResultActivity";

    @BindView(R.id.ib_return)
    ImageButton ib_return;

    @BindView(R.id.ib_close)
    ImageButton ib_close;

    @BindView(R.id.btn_goto_connect)
    Button btn_goto_connect;

    @BindView(R.id.rl_content_bluetooth_no_net)
    RelativeLayout rl_content_bluetooth_no_net;

    @BindView(R.id.rl_content_wifi_list)
    RelativeLayout rl_content_wifi_list;

    @BindView(R.id.recyclerview_wifi)
    RecyclerView mRecyclerview;


    public WifiHelper mWifiHelper = null;
    public WifiInfoAdapter_list mAdapter;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiListItem = new ArrayList<>();
    private String mCurrentSelectWifiName = null;
    private ScanResult mScanResult = null;

    public static final int UPDATE_DATA = 1;
    public static final int SELECT_POSITION = 2;

    public LinearLayoutManager mLayoutManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_DATA:
                    mAdapter.notifyDataSetChanged();
                    break;
                case SELECT_POSITION:
                    mScanResult = (ScanResult)msg.obj;
                    UbtLog.d(TAG,"mScanResult = " + mScanResult.SSID);

                    Intent i = new Intent();
                    i.putExtra("wifiName",mScanResult.SSID);
//                    i.putExtra("wifiName",event.getSelectWifiName());
                    i.setClass(NetSearchResultActivity.this,NetconnectActivity.class);
                    NetSearchResultActivity.this.startActivity(i);
                    NetSearchResultActivity.this.finish();
                    break;
                case WifiHelper.REFRESH_WIFI_DATA:
                    mWifiListItem.clear();
                    mWifiListItem.addAll((List<ScanResult>) msg.obj);
                    UbtLog.d(TAG,"mWifiListItem size :"+mWifiListItem.size() );
                    mAdapter.notifyDataSetChanged();

                    if(mWifiListItem.isEmpty()){
                        UbtLog.d(TAG,"mWifiListItem isempty" );
                        if(rl_content_wifi_list != null && rl_content_bluetooth_no_net != null){
                            rl_content_wifi_list.setVisibility(View.INVISIBLE);
                            rl_content_bluetooth_no_net.setVisibility(View.VISIBLE);
                        }else {
                            UbtLog.d(TAG,"mWifiListItem isempty    null" );
                        }
                    }else {
                        UbtLog.d(TAG,"mWifiListItem hava data " );
                        if(rl_content_wifi_list != null && rl_content_bluetooth_no_net != null){
                            rl_content_wifi_list.setVisibility(View.VISIBLE);
                            rl_content_bluetooth_no_net.setVisibility(View.INVISIBLE);
                        }else {
                            UbtLog.d(TAG,"mWifiListItem hava data     null" );
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        mWifiHelper = new WifiHelper(getApplicationContext(),mHandler);

        mWifiHelper.doStartScan();
    }

    @Override
    protected void initUI() {
        btn_goto_connect.setOnClickListener(this);
        ib_close.setOnClickListener(this);
        rl_content_bluetooth_no_net.setVisibility(View.INVISIBLE);
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(mLayoutManager);
        String wifi =getIntent().getStringExtra("wifiName");
        if( wifi != null && !wifi.equals("") ){
            mCurrentSelectWifiName = wifi ;
        }
        UbtLog.d(TAG, "mCurrentSelectWifiName=="+mCurrentSelectWifiName);
        mAdapter = new WifiInfoAdapter_list(getApplicationContext(),mWifiListItem,mHandler,mCurrentSelectWifiName);
        mRecyclerview.setAdapter(mAdapter);

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.bluetooth_net_search_result;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto_connect:
                Intent intent = new Intent();
                intent.setClass(NetSearchResultActivity.this,NetconnectActivity.class);
                this.startActivity(intent);
                NetSearchResultActivity.this.finish();
                break;
            case R.id.ib_close:
                NetSearchResultActivity.this.finish();
                break;
            case R.id.ib_return:
                NetSearchResultActivity.this.finish();
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWifiHelper != null){
            mWifiHelper.onDestroy();
        }
    }
}
