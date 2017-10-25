package com.ubt.alpha1e.ui.custom;

import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.utils.log.MyLog;

import java.util.Date;

public class ViewSwitcherOpreater {

	private Context mContext;
	private ViewSwitcher mSwicher;
	private int[] mImgs;
	private OnSlideListener mListener;

	public ViewSwitcherOpreater(ViewSwitcher _swicher,
			OnSlideListener _listener, Context _context) {
		this.mSwicher = _swicher;
		this.mContext = _context;
		mListener = _listener;
	}

	public void setImgs(int[] ress) {
		mImgs = ress;
		MyOnGestureListener myOnGestureListener = new MyOnGestureListener(
				mListener, mSwicher, mImgs);
		GestureDetector myDesDet = new GestureDetector(myOnGestureListener);
		ViewFactory viewFactory = new myViewFactory(mContext, myDesDet);

		mSwicher.setFactory(viewFactory);
		mListener.getNext(mSwicher, mImgs);
	}

	class myViewFactory implements ViewFactory {
		private GestureDetector desDet;
		private Context mContext;

		public myViewFactory(Context _mContext, GestureDetector _desDet) {
			mContext = _mContext;
			desDet = _desDet;
		}

		@Override
		public View makeView() {

			ImageView img = (ImageView) ((Activity) mContext)
					.getLayoutInflater().inflate(R.layout.layout_image, null);

			img.setOnTouchListener(new MyOnTouchListener(desDet));
			return img;
		}
	}

	class MyOnTouchListener implements OnTouchListener {
		private GestureDetector desDet;

		public MyOnTouchListener(GestureDetector _desDet) {
			desDet = _desDet;
		}

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			return desDet.onTouchEvent(arg1);
		}
	}
}

class MyOnGestureListener implements OnGestureListener {
	private OnSlideListener listener;
	private static Date lastTime = null;
	private ViewSwitcher mSwitcher;
	private int[] mImgs;

	public MyOnGestureListener(OnSlideListener a, ViewSwitcher _Switcher,
			int[] imgs) {
		listener = a;
		mSwitcher = _Switcher;
		mImgs = imgs;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
//System.out.println("-----onscoll-----"+distanceX);
//		Date curDate = new Date(System.currentTimeMillis());
//		float time_difference = 1000;
//		if (lastTime != null) {
//			time_difference = curDate.getTime() - lastTime.getTime();
//		}
//
//		if (time_difference > 100) {
//			if (distanceX > 5) {
//				listener.getNext(mSwitcher, mImgs);
//				MyLog.writeLog("遥控器测试", "getNext");
//			}
//			if (distanceX < -5) {
//				listener.getPrev(mSwitcher, mImgs);
//				MyLog.writeLog("遥控器测试", "getPrev");
//			}
//		}
//		lastTime = curDate;
		return false;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		MyLog.writeLog("遥控器测试", "onDown");
		return true;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float x,
			float y) {
		MyLog.writeLog("遥控器测试", "onFling");
		if((arg0.getX()-arg1.getX())>50)
		{
			listener.getNext(mSwitcher, mImgs);
		}else if((arg0.getX()-arg1.getX())<-50)
		{
			listener.getPrev(mSwitcher, mImgs);
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		MyLog.writeLog("遥控器测试", "onLongPress");
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		MyLog.writeLog("遥控器测试", "onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		MyLog.writeLog("遥控器测试", "onSingleTapUp");
		listener.onClient(mSwitcher);
		return true;
	}
}