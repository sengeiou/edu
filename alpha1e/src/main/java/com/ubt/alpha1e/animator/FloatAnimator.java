package com.ubt.alpha1e.animator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/8 11:31
 * @描述: 气泡漂浮动画
 */

public class FloatAnimator {

    private static final String TAG = FloatAnimator.class.getSimpleName();

    private List<View> views = new ArrayList<>();
    private List<AnimatorSet> animatorSets = new ArrayList<>();

    private AnimatorSet mAnimatorSet;

    private static FloatAnimator floatAnimator = null;

    private Random random = new Random();

    private float distance = 30F;

    private FloatAnimator(){

    }

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
     * 开始漂浮动画
     */
    public void startFloatAnimator(View view){

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY",  distance, -1 * distance, distance).setDuration(3800 + random.nextInt(400));
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
        animatorSets.add(mAnimatorSet);
    }

    /**
     * 停止动画
     */
    public void stopFloatAnimator(){
        /*if(mAnimatorSet.isStarted()){
            mAnimatorSet.cancel();
        }*/

        for(AnimatorSet animatorSet : animatorSets){
            if(animatorSet.isStarted()){
                animatorSet.cancel();
            }
        }
    }

    /**
     * 停止动画
     */
    public void startFloatAnimator1(){
        /*if(mAnimatorSet.isStarted()){
            mAnimatorSet.cancel();
        }*/

        for(AnimatorSet animatorSet : animatorSets){
            if(!animatorSet.isStarted()){
                animatorSet.start();
            }
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

    public static FloatAnimator getIntanse(){
        if(floatAnimator == null){
            floatAnimator = new FloatAnimator();
        }
        return floatAnimator;
    }

    public void setDistance(float distance){
        this.distance = distance;
    }


    public FloatAnimator add(View view){
        views.add(view);
        return floatAnimator;
    }

    public void addShow(View view){
        views.add(view);
        startFloatAnimator(view);
    }


    public void remove(View view){

    }

    public void clear(){
        views.clear();
        animatorSets.clear();
    }
}
