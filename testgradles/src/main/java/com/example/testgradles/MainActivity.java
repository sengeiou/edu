package com.example.testgradles;

import android.app.Activity;
import android.os.Bundle;

import com.ubtechinc.base.PublicInterface;


public class MainActivity extends Activity implements PublicInterface.BlueToothInteracter{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onReceiveData(String s, byte b, byte[] bytes, int i) {

    }

    @Override
    public void onSendData(String s, byte[] bytes, int i) {

    }

    @Override
    public void onConnectState(boolean b, String s) {

    }

    @Override
    public void onDeviceDisConnected(String s) {

    }
}
