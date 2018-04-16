package com.ubt.alpha1e.ui.main;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.R;

/**
 * @作者：ubt
 * @日期: 2018/4/16 10:31
 * @描述:
 */


public class MainAnimationEffect {
    Animation bounceAnimiaton;
    Animation bounceCCAnimation;
    Animation shrinkAnimaiton;
    Animation planetAnimation;
    Context   mContext;
    public MainAnimationEffect(Context context) {
        mContext=context;

    }

    /**
     * bluetooth icon, programming icon, etc.
     * @return animation effect.
     */
    public Animation getBounceAnimation() {
        bounceAnimiaton= AnimationUtils.loadAnimation(mContext, R.anim.hyperspace_jump);
        bounceAnimiaton.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return bounceAnimiaton ;
    }
    public Animation getCourceCenterBounceAnimation(final RelativeLayout mCourseCenter) {
        bounceCCAnimation= AnimationUtils.loadAnimation(mContext, R.anim.hyperspace_jump);
        bounceCCAnimation.setRepeatCount(Animation.INFINITE);
        bounceCCAnimation.setRepeatMode(Animation.REVERSE);

        bounceCCAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                bounceCCAnimation= AnimationUtils.loadAnimation(mContext, R.anim.hyperspace_jump);
                bounceCCAnimation.setAnimationListener(this);
                mCourseCenter.startAnimation(bounceCCAnimation);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return bounceCCAnimation ;
    }
    public Animation getShrinkAnimation(){
        shrinkAnimaiton= AnimationUtils.loadAnimation(mContext, R.anim.shrink_animation);
        shrinkAnimaiton.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return shrinkAnimaiton ;
    }

    public Animation getPlanetAnimation(final ImageView mPlanet){
        planetAnimation = new TranslateAnimation(0, 10,0, 10);
        planetAnimation.setDuration(1000);
        planetAnimation.setRepeatCount(Animation.INFINITE);
        planetAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                planetAnimation = new TranslateAnimation(0, 50,0, 50);
                planetAnimation.setAnimationListener(this);
                mPlanet.startAnimation(planetAnimation);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return planetAnimation;

    }



}
