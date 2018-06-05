package com.ubt.alpha1e.edu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;


public class PcUpdateActivity extends BaseActivity implements BaseDiaUI {

    private static final String TAG = "PcUpdateActivity";
    private ImageView img_pc_update_next;
    private TextView tv_pc_url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc_update);

        initUI();
        initControlListener();
    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub
        img_pc_update_next = (ImageView)findViewById(R.id.img_pc_update_next);
        tv_pc_url = (TextView)findViewById(R.id.tv_pc_url);
    }

    @Override
    protected void initControlListener() {

        img_pc_update_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent();
                mIntent.setClass(PcUpdateActivity.this, PcUpdateFinishActivity.class);
                startActivity(mIntent);
                PcUpdateActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_pc_url.setText(getStringResources("ui_upgrade_guide1_open") + " " + getStringResources("ui_pc_update_url"));
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
