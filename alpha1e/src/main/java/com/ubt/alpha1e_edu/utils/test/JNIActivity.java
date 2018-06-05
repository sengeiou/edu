package com.ubt.alpha1e_edu.utils.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.business.HtsHelper;

public class JNIActivity extends Activity {

    private Button write_hts;
    private Button read_hts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_jni);
        write_hts = (Button) findViewById(R.id.write_hts);
        write_hts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HtsHelper.test_write();
            }
        });
        read_hts = (Button) findViewById(R.id.read_hts);
        read_hts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HtsHelper.test_read();
            }
        });
    }

}
