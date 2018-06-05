package com.ubt.alpha1e.edu.ui.Introduction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplicationValues;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.edu.ui.MyMainActivity;
import com.ubt.alpha1e.edu.ui.main.MainActivity;
import com.umeng.analytics.MobclickAgent;

public class IntroductionActivity extends FragmentActivity implements
		SetterFactory {

	private static final String TAG = "IntroductionActivity";

	private int scale = 0;
	private int densityDpi = 0;

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
 	}

	private static int[] mBackgroundRes = new int[] {
			R.drawable.introduction_1, R.drawable.introduction_2,
			R.drawable.introduction_3, R.drawable.introduction_3  };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AlphaApplicationValues.initInfo(getApplicationContext());
		MobclickAgent.setDebugMode(AlphaApplicationValues.isDebug());
		scale = (int)this.getResources().getDisplayMetrics().density;
		densityDpi = (int)this.getResources().getDisplayMetrics().densityDpi;
		/*UbtLog.d("lihai","lihai-------------scale:"+scale
				+ "	"+(int)this.getResources().getDisplayMetrics().widthPixels
				+ "	="+(int)this.getResources().getDisplayMetrics().heightPixels
				+ "	"+densityDpi);*/

		//测试版本添加
		BasicSharedPreferencesOperator
				.getInstance(IntroductionActivity.this, DataType.USER_USE_RECORD)
				.doWrite(
						BasicSharedPreferencesOperator.IS_FIRST_USE_APP_KEY,
						BasicSharedPreferencesOperator.IS_FIRST_USE_APP_VALUE_TRUE,
						null, -1);

		setContentView(R.layout.activity_introduction);
		IntroductionViewPager start_pager = new IntroductionViewPager(
				R.id.pager, this, this);
		start_pager.setNextActivity(MainActivity.class);

	}

	@Override
	public IPageSetter getUISetter(int index) {
		if (index == 0) {
			return new IPageSetter() {
				@Override
				public void setPage(ViewGroup vg) {

					View add_view = View.inflate(IntroductionActivity.this,R.layout.layout_introduction_index_1, null);

					ImageView imgGuide = (ImageView) add_view.findViewById(R.id.img_introduction_guide);
					imgGuide.setImageResource(R.drawable.introduction_guidecontent_1);

					TextView txt_1 = (TextView) add_view.findViewById(R.id.txt_index_1);
					txt_1.setText(IntroductionActivity.this.getString(R.string.ui_introduction_title1));
					TextView txt_2 = (TextView) add_view.findViewById(R.id.txt_index_2);
					txt_2.setText(IntroductionActivity.this.getString(R.string.ui_introduction_desc1));

					ImageView imgGuideLine_1 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_1);
					imgGuideLine_1.setImageResource(R.drawable.introduction_guideline_sel);

					ImageView imgGuideLine_2 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_2);
					imgGuideLine_2.setImageResource(R.drawable.introduction_guideline);

					ImageView imgGuideLine_3 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_3);
					imgGuideLine_3.setImageResource(R.drawable.introduction_guideline);

					ImageView imgGuideLine_4 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_4);
					imgGuideLine_4.setImageResource(R.drawable.introduction_guideline);

					add_view.findViewById(R.id.lay_instant_experience).setVisibility(View.GONE);

					vg.addView(add_view);
				}
			};
		} else if (index == 1) {
			return new IPageSetter() {

				@Override
				public void setPage(ViewGroup vg) {

					View add_view = View.inflate(IntroductionActivity.this,R.layout.layout_introduction_index_1, null);

					ImageView imgGuide = (ImageView) add_view.findViewById(R.id.img_introduction_guide);
					imgGuide.setImageResource(R.drawable.introduction_guidecontent_2);

					TextView txt_1 = (TextView) add_view.findViewById(R.id.txt_index_1);
					txt_1.setText(IntroductionActivity.this.getString(R.string.ui_introduction_title2));
					TextView txt_2 = (TextView) add_view.findViewById(R.id.txt_index_2);
					txt_2.setText(IntroductionActivity.this.getString(R.string.ui_introduction_desc2));

					ImageView imgGuideLine_1 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_1);
					imgGuideLine_1.setImageResource(R.drawable.introduction_guideline);

					ImageView imgGuideLine_2 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_2);
					imgGuideLine_2.setImageResource(R.drawable.introduction_guideline_sel);

					ImageView imgGuideLine_3 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_3);
					imgGuideLine_3.setImageResource(R.drawable.introduction_guideline);

					ImageView imgGuideLine_4 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_4);
					imgGuideLine_4.setImageResource(R.drawable.introduction_guideline);

					add_view.findViewById(R.id.lay_instant_experience).setVisibility(View.GONE);

					vg.addView(add_view);

				}
			};
		} else if (index == 2) {
			return new IPageSetter() {

				@Override
				public void setPage(ViewGroup vg) {

					View add_view = View.inflate(IntroductionActivity.this,R.layout.layout_introduction_index_1, null);

					ImageView imgGuide = (ImageView) add_view.findViewById(R.id.img_introduction_guide);
					imgGuide.setImageResource(R.drawable.introduction_guidecontent_3);

					TextView txt_1 = (TextView) add_view.findViewById(R.id.txt_index_1);
					txt_1.setText(IntroductionActivity.this.getString(R.string.ui_introduction_title3));
					TextView txt_2 = (TextView) add_view.findViewById(R.id.txt_index_2);
					txt_2.setText(IntroductionActivity.this.getString(R.string.ui_introduction_desc3));

					ImageView imgGuideLine_1 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_1);
					imgGuideLine_1.setImageResource(R.drawable.introduction_guideline);

					ImageView imgGuideLine_2 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_2);
					imgGuideLine_2.setImageResource(R.drawable.introduction_guideline);

					ImageView imgGuideLine_3 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_3);
					imgGuideLine_3.setImageResource(R.drawable.introduction_guideline_sel);

					ImageView imgGuideLine_4 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_4);
					imgGuideLine_4.setImageResource(R.drawable.introduction_guideline);

					add_view.findViewById(R.id.lay_instant_experience).setVisibility(View.GONE);

					vg.addView(add_view);
				}
			};
		} else if(index == 3){
			return new IPageSetter() {

				@Override
				public void setPage(ViewGroup vg) {

					View add_view = View.inflate(IntroductionActivity.this,R.layout.layout_introduction_index_1, null);

					ImageView imgGuide = (ImageView) add_view.findViewById(R.id.img_introduction_guide);
					imgGuide.setImageResource(R.drawable.introduction_guidecontent_4);

					TextView txt_1 = (TextView) add_view.findViewById(R.id.txt_index_1);
					txt_1.setText(IntroductionActivity.this.getString(R.string.ui_introduction_title4));
					TextView txt_2 = (TextView) add_view.findViewById(R.id.txt_index_2);
					txt_2.setText(IntroductionActivity.this.getString(R.string.ui_introduction_desc4));

					ImageView imgGuideLine_1 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_1);
					imgGuideLine_1.setImageResource(R.drawable.introduction_guideline);

					ImageView imgGuideLine_2 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_2);
					imgGuideLine_2.setImageResource(R.drawable.introduction_guideline);

					ImageView imgGuideLine_3 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_3);
					imgGuideLine_3.setImageResource(R.drawable.introduction_guideline);

					ImageView imgGuideLine_4 = (ImageView) add_view.findViewById(R.id.img_introduction_guide_line_4);
					imgGuideLine_4.setImageResource(R.drawable.introduction_guideline_sel);

					View btnView = add_view.findViewById(R.id.lay_instant_experience);
					btnView.setVisibility(View.VISIBLE);
					btnView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setClass(IntroductionActivity.this,MyMainActivity.class);
							IntroductionActivity.this.startActivity(intent);
							IntroductionActivity.this.finish();

							BasicSharedPreferencesOperator
									.getInstance(IntroductionActivity.this, DataType.USER_USE_RECORD)
									.doWrite(
											BasicSharedPreferencesOperator.IS_FIRST_USE_APP_KEY,
											BasicSharedPreferencesOperator.IS_FIRST_USE_APP_VALUE_TRUE,
											null, -1);
						}
					});

					vg.addView(add_view);
				}
			};
		}else{
			return null;
		}
	}

	@Override
	public int getBackgroundRes(int index) {
		return mBackgroundRes[index];
	}

	@Override
	public int getLenth() {
		return mBackgroundRes.length;
	}
}
