package com.ubt.alpha1e.ui.main;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
        if(bounceAnimiaton == null){
            bounceAnimiaton= AnimationUtils.loadAnimation(mContext, R.anim.hyperspace_jump);
            bounceAnimiaton.setRepeatMode(Animation.RESTART);

            /*bounceAnimiaton.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                @Override
                public void onAnimationEnd(Animation animation) {

                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });*/
        }
        return bounceAnimiaton ;
    }
    public Animation getCourceCenterBounceAnimation(final RelativeLayout mCourseCenter) {
        if(bounceCCAnimation == null){
            bounceCCAnimation= AnimationUtils.loadAnimation(mContext, R.anim.hyperspace_jump_infinite);
            bounceCCAnimation.setRepeatMode(Animation.RESTART);
        }
        return bounceCCAnimation ;
    }

}
