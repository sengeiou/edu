package com.ubt.alpha1e.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;


public class PcUpdateFinishActivity extends BaseActivity implements BaseDiaUI {

    private static final String TAG = "PcUpdateFinishActivity";
    private ImageView img_pc_update_back;
    private ImageView img_pc_update_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc_update2);

        initUI();
        initControlListener();
    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub
        img_pc_update_ok = (ImageView)findViewById(R.id.img_pc_update_ok);
        img_pc_update_back = (ImageView)findViewById(R.id.img_pc_update_back);
    }

    @Override
    protected void initControlListener() {

        img_pc_update_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent();
                mIntent.setClass(PcUpdateFinishActivity.this, RobotConnectedActivity.class);
                startActivity(mIntent);
                PcUpdateFinishActivity.this.finish();
            }
        });

        img_pc_update_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent();
                mIntent.setClass(PcUpdateFinishActivity.this, PcUpdateActivity.class);
                startActivity(mIntent);
                PcUpdateFinishActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }


    @Override
    public void noteWaitWebProcressShutDown() {

    }

}
