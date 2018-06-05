package com.ubt.alpha1e.edu.ui.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.data.DataTools;
import com.ubt.alpha1e.edu.data.ImageTools;
import com.ubt.alpha1e.edu.ui.dialog.IntroductionUIListener;

public class IntroductionUIOpreater {

	private RelativeLayout mRootView;
	private Context mContext;
	IntroductionUIListener mListener;
	private RelativeLayout mBaseView;
	private ImageView mBaseBg;
	private Bitmap mBaseBgImg;
	private int[] mBaseBgs;
	private int[][] mBaseTxts;
	private int mCurrentIndex = 0;

	public IntroductionUIOpreater(RelativeLayout root, Context context,
			IntroductionUIListener listener) {
		mRootView = root;
		mContext = context;
		mListener = listener;
	}

	public void setIntroductionViews(int baseViewId, int baseBgId, int[] bgs,
			int[][] txts) {
		mBaseView = (RelativeLayout) View.inflate(mContext, baseViewId, null);
		mBaseBg = (ImageView) mBaseView.findViewById(baseBgId);
		mBaseBgs = bgs;
		mBaseTxts = txts;

	}

	private void showIndex() {

		if (mBaseTxts == null)
			return;

		for (int i = 0; i < mBaseTxts.length; i++) {
			if (mCurrentIndex == i) {

				if (mBaseBgImg != null && (!mBaseBgImg.isRecycled())) {
					mBaseBgImg.recycle();
				}

				mBaseBgImg = ImageTools.compressImage(mContext.getResources(),
						mBaseBgs[i], DataTools.dip2px(mContext,
								mBaseBg.getLayoutParams().height), DataTools
								.dip2px(mContext,
										mBaseBg.getLayoutParams().width));
				mBaseBg.setImageBitmap(mBaseBgImg);
				for (int j = 0; j < mBaseTxts[i].length; j++)
					((TextView) mRootView.findViewById(mBaseTxts[i][j]))
							.setVisibility(View.VISIBLE);
			} else {
				for (int j = 0; j < mBaseTxts[i].length; j++)
					((TextView) mRootView.findViewById(mBaseTxts[i][j]))
							.setVisibility(View.GONE);
			}
		}
	}

	public void showIntroductions() {
		mRootView.addView(mBaseView);
		mCurrentIndex = 0;
		showIndex();
		mBaseBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mCurrentIndex++;
				if (mCurrentIndex >= mBaseBgs.length) {
					mListener.onIntroduceFinish();
					mBaseView.setVisibility(View.GONE);
					if (mBaseBgImg != null && (!mBaseBgImg.isRecycled())) {
						mBaseBgImg.recycle();
					}
				} else {
					showIndex();
				}
			}
		});
	}
}
