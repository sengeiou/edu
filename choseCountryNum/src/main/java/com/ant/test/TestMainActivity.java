package com.ant.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ant.chosecountrynum.R;
import com.ant.country.CountryActivity;

public class TestMainActivity extends Activity {

	private TextView txt_sel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_main);
		txt_sel = (TextView) findViewById(R.id.txt_sel);
		txt_sel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(TestMainActivity.this, CountryActivity.class);
				startActivityForResult(intent, 12);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case 12:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				String countryName = bundle.getString("countryName");
				String countryNumber = bundle.getString("countryNumber");
				txt_sel.setText(countryNumber + "," + countryName);

			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
