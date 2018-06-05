package com.ubt.alpha1e_edu.base.loading;

import android.animation.ValueAnimator;
import android.graphics.Canvas;

/**
 * Created by Tuyen Nguyen on 2/10/17.
 */

public class ClassicSpinner extends LoaderView {
  private Circle[] circles;
  private int circlesSize;
  int circleRadius1;
  public ClassicSpinner() {
    circlesSize = 8;
  }

  @Override
  public void initializeObjects() {
    final float size = Math.min(width, height);
    final float circleRadius = size / 10.0f;
    circleRadius1 = (int) (size / 10.0f);
    circles = new Circle[circlesSize];

    for (int i = 0; i < circlesSize; i++) {
      circles[i] = new Circle();
      circles[i].setCenter(center.x, circleRadius);
      circles[i].setColor(color);
     // circles[i].setAlpha(126);
      circles[i].setRadius(circleRadius - circleRadius * i / 9);
    }
  }

  @Override
  public void setUpAnimation() {
    for (int i = 0; i < circlesSize; i++) {
      final int index = i;

      ValueAnimator fadeAnimator = ValueAnimator.ofFloat(circleRadius1 / 2, circleRadius1);
      fadeAnimator.setRepeatCount(ValueAnimator.INFINITE);
      fadeAnimator.setDuration(1000);
      fadeAnimator.setStartDelay(index * 80);
      fadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          // circles[index].setAlpha((int)animation.getAnimatedValue());
          //Log.d("ClassicSpinner", "index==="+index+"value===" + (float) animation.getAnimatedValue());
          circles[index].setRadius((float) animation.getAnimatedValue());
          if (invalidateListener != null) {
            invalidateListener.reDraw();
          }
        }
      });

      fadeAnimator.start();
    }
  }

  @Override
  public void draw(Canvas canvas) {
    for (int i = 0; i < circlesSize; i++) {
      canvas.save();
      canvas.rotate(45 * i, center.x, center.y);
      circles[i].draw(canvas);
      canvas.restore();
    }
  }
}
