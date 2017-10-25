package com.ubt.alpha1e.ui.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ubt.alpha1e.R;

public class NumInputsView extends LinearLayout {

	private EditIndexView[] edt_nums;

	public NumInputsView(Context context) {
		super(context);
	}

	public NumInputsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.layout_num_input, this,
				true);

		edt_nums = new EditIndexView[] {
				(EditIndexView) findViewById(R.id.edt_1),
				(EditIndexView) findViewById(R.id.edt_2),
				(EditIndexView) findViewById(R.id.edt_3),
				(EditIndexView) findViewById(R.id.edt_4),
				(EditIndexView) findViewById(R.id.edt_5),
				(EditIndexView) findViewById(R.id.edt_6),
				(EditIndexView) findViewById(R.id.edt_7),
				(EditIndexView) findViewById(R.id.edt_8),
				(EditIndexView) findViewById(R.id.edt_9) };

		for (int i = 0; i < edt_nums.length - 1; i++)
			edt_nums[i].the_next = edt_nums[i + 1];

		for (int i = 0; i < edt_nums.length; i++) {

			edt_nums[i]
					.addTextChangedListener(new TextIndexWatcher(edt_nums[i]));

		}
	}

	public String getText() {
		String txt = "";
		for (int i = 0; i < edt_nums.length; i++) {
			txt += edt_nums[i].getText().toString();
		}
		return txt;
	}

	class TextIndexWatcher implements TextWatcher {

		private EditIndexView mView;

		public TextIndexWatcher(EditIndexView view) {
			mView = view;
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			if (arg0.length() >= 1) {
				if (mView.the_next != null)
					mView.the_next.requestFocus();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

	}

}
