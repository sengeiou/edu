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
        bounceAnimiaton.setRepeatMode(Animation.REVERSE);
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




}
