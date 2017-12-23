package com.ubt.factorytest;

import android.os.Bundle;

import com.ubt.factorytest.bluetooth.BluetoothFragment;
import com.ubt.factorytest.bluetooth.BluetoothPresenter;
import com.ubt.factorytest.bluetooth.bluetoothLib.BluetoothController;
import com.ubt.factorytest.utils.ContextUtils;

import java.util.UUID;

import me.yokeyword.fragmentation.SupportActivity;

public class MainActivity extends SupportActivity {

    private final String BT_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private BluetoothPresenter mPresenter;
    private BluetoothController mBluetoothController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContextUtils.init(this);
        initBluetooth();
        BluetoothFragment fragment = findFragment(BluetoothFragment.class);
        if (fragment == null) {
            fragment = BluetoothFragment.newInstance();
            loadRootFragment(R.id.frame_content, fragment);
        }
        mPresenter = new BluetoothPresenter(fragment);



    }

    private void initBluetooth() {
        mBluetoothController = BluetoothController.getInstance().build(this);
        mBluetoothController.setAppUuid(UUID.fromString(BT_UUID));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothController != null) {
            mBluetoothController.release();
        }
    }
}
