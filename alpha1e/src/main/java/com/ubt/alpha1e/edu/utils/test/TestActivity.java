package com.ubt.alpha1e.edu.utils.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class TestActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    TextView tv = new TextView(this);
        addContentView(tv, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        String id ="push_object_id:"+ getIntent().getStringExtra("push_object_id");
        tv.setText(id);
	}

}
