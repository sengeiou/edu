package com.ubt.alpha1e.animator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/8 11:31
 * @描述: 气泡漂浮动画
 */

public class FloatAnimator {

    private static final String TAG = "FloatAnimator";

    private List<View> views = new ArrayList<>();

    private AnimatorSet mAnimatorSet;


    private FloatAnimator(Builder builder){
        this.views = builder.animatorViews;
    }

    /**
     * 开始漂浮动画
     */
    public void startFloatAnimator(){
        if(views.size() <= 0){
            Log.e(TAG,"animatorViews is 0!!!!");
            return;
        }
        ObjectAnimator[] animators = new ObjectAnimator[views.size()];
        for (int i = 0; i < views.size(); i++) {
            animators[i] = ObjectAnimator
                    .ofFloat(views.get(i), "translationY",  30.0F, -30.0f, 30.0f)
                    .setDuration(4000+i*200);
            animators[i].setRepeatCount(ObjectAnimator.INFINITE);
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animators[0]);
        for (int j = 1; j < animators.length; j++) {
            mAnimatorSet.play(animators[j]).with(animators[j-1]);
        }
        mAnimatorSet.start();
    }

    /**
     * 停止动画
     */
    public void stopFloatAnimator(){
        if(mAnimatorSet.isStarted()){
            mAnimatorSet.cancel();
        }
    }

    public static class Builder{
        private List<View> animatorViews = new ArrayList<>();

        public Builder  view(View view) {
            animatorViews.add(view);
            return this;
        }

        public FloatAnimator build(){
            return new FloatAnimator(this);
        }
    }
}
