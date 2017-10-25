package com.ubt.alpha1e.blockly;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.BaseActivity;

/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class TestBluetooth extends BaseActivity   {


    public static final String TAG = "TestBluetooth";

    private SeekBar sbTime;
    private View timeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blockly_test);
//        setContentView(R.layout.layout_new_edit_ation_item);

//        sbTime = (SeekBar) findViewById(R.id.skb_time);
//        timeView = (View) findViewById(R.id.vew_play_frame_time_long);
//        changViewLength(timeView, 100);
//
//
//        sbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                UbtLog.d(TAG, "progress:" + progress);
//                    if(progress<100){
//                        progress = 100;
//                        sbTime.setProgress(progress);
//                    }
//                changViewLength(timeView, progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

    }


    private void changViewLength(View view, int progress){

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(progress, 100);
        view.setLayoutParams(params);

    }



    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }



}
