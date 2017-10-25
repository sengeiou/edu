package com.ubt.alpha1e.ui.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.widget.TextView;

public class TextViewPicOpreater {
	public static void addPicToTextView(final Context mContext,
			TextView txtView, int picId, final float pic_size) {

		String str = txtView.getText().toString();
		String[] txt = str.split("#");
		ImageGetter imageGetter = new ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				int id = Integer.parseInt(source);
				Drawable d = mContext.getResources().getDrawable(id);
				d.setBounds(0, 0, (int) pic_size, (int) pic_size);
				return d;
			}
		};
		txtView.setText("");
		txtView.append(Html.fromHtml(txt[0] + "<img src=\"" + picId + "\">"
				+ txt[1], imageGetter, null));

	}
}
