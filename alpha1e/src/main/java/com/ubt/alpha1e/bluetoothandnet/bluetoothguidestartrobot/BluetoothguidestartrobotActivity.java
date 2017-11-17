package com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.yanzhenjie.permission.Permission;

import butterknife.BindView;



/**
 * @author: dicy.cheng
 * @description:  蓝牙连接引导页
 * @create: 2017/11/2
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class BluetoothguidestartrobotActivity extends MVPBaseActivity<BluetoothguidestartrobotContract.View, BluetoothguidestartrobotPresenter> implements BluetoothguidestartrobotContract.View,View.OnClickListener {

    String TAG =  "BluetoothguidestartrobotActivity";

    @BindView(R.id.bluetooth_guide_start_robot)
    ImageButton btn_finish;

    @BindView(R.id.button_next)
    Button btn_next;

    @BindView(R.id.select)
    CheckBox select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void initUI() {
        btn_finish.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        select.setOnClickListener(this);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_bluetooth_guide_startrobot;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluetooth_guide_start_robot:
                BluetoothguidestartrobotActivity.this.finish();
                break;
            case R.id.select:

                if(select.isChecked()){
                    select.setTextColor(Color.parseColor("#2AADEA"));
                    btn_next.setBackground(ContextCompat.getDrawable(BluetoothguidestartrobotActivity.this,R.drawable.action_button_enable));
                }else {
                    select.setTextColor(Color.parseColor("#000000"));
                    btn_next.setBackground(ContextCompat.getDrawable(BluetoothguidestartrobotActivity.this,R.drawable.action_button_disable));
                }
                break;
            case R.id.button_next:
                if(select.isChecked()){
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
                                PermissionUtils.getInstance(this).showRationSettingDialog(PermissionUtils.PermissionEnum.LOACTION);
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
                            PermissionUtils.getInstance(this).showRationSettingDialog(PermissionUtils.PermissionEnum.LOACTION);
                        }
                    }

                }else {
                    ToastUtils.showShort("请确认站立，并选择");
                }
                break;
            default:

        }
    }

    void startBluetoothConnect(){
        Intent intent = new Intent();
        intent.putExtra("isFirst","yes");
        intent.setClass(BluetoothguidestartrobotActivity.this,BluetoothconnectActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.activity_open_up_down,0);
        BluetoothguidestartrobotActivity.this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0,R.anim.activity_close_down_up);
    }
}
