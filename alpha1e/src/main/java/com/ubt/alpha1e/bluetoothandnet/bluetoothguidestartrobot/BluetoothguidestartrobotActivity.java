package com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.bluetoothandnet.bluetoothconnect.BluetoothconnectActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;

import butterknife.BindView;



/**
 * @author: dicy.cheng
 * @description:  蓝牙连接引导页
 * @create: 2017/11/2
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class BluetoothguidestartrobotActivity extends MVPBaseActivity<BluetoothguidestartrobotContract.View, BluetoothguidestartrobotPresenter> implements BluetoothguidestartrobotContract.View,View.OnClickListener {

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
                    Intent intent = new Intent();
                    intent.setClass(BluetoothguidestartrobotActivity.this,BluetoothconnectActivity.class);
                    this.startActivity(intent);
                }else {
                    ToastUtils.showShort("请确认站立，并选择");
                }
                break;
            default:

        }
    }
}
