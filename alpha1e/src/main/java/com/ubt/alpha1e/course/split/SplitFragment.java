package com.ubt.alpha1e.course.split;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.course.CourseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SplitFragment extends MVPBaseFragment<SplitContract.View, SplitPresenter> implements SplitContract.View {

    private static final String TAG = SplitFragment.class.getSimpleName();



    @BindView(R.id.iv_principle_alpha)
    ImageView ivPrincipleAlpha;
    @BindView(R.id.tv_next)
    TextView tvNext;
    Unbinder unbinder;

    private float lastX, lastY;
    private int containerWidth;
    private int containerHeight;
    private int scale = 0;

    @Override
    protected void initUI() {

        scale = (int) this.getResources().getDisplayMetrics().density;
        containerWidth = this.getResources().getDisplayMetrics().widthPixels;
        containerHeight = this.getResources().getDisplayMetrics().heightPixels;

        UbtLog.d(TAG, "scale = " + scale
                + "  width = " + this.getResources().getDisplayMetrics().widthPixels
                + "  height = " + this.getResources().getDisplayMetrics().heightPixels
                + "  densityDpi = " + this.getResources().getDisplayMetrics().densityDpi);

        ivPrincipleAlpha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UbtLog.d(TAG,"event.getActionMasked() = " + event.getActionMasked());
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        {
                            //  不要直接用getX和getY,这两个获取的数据已经是经过处理的,容易出现图片抖动的情况
                            float distanceX = lastX - event.getRawX();
                            float distanceY = lastY - event.getRawY();

                            float nextY = ivPrincipleAlpha.getY() - distanceY;
                            float nextX = ivPrincipleAlpha.getX() - distanceX;

                            // 不能移出屏幕
                            if (nextY < 0) {
                                nextY = 0;
                            } else if (nextY > containerHeight - ivPrincipleAlpha.getHeight()) {
                                nextY = containerHeight - ivPrincipleAlpha.getHeight();
                            }
                            if (nextX < 0){
                                nextX = 0;
                            } else if (nextX > containerWidth - ivPrincipleAlpha.getWidth()){
                                nextX = containerWidth - ivPrincipleAlpha.getWidth();
                            }

                            // 属性动画移动
                            ObjectAnimator y = ObjectAnimator.ofFloat(ivPrincipleAlpha, "y", ivPrincipleAlpha.getY(), nextY);
                            ObjectAnimator x = ObjectAnimator.ofFloat(ivPrincipleAlpha, "x", ivPrincipleAlpha.getX(), nextX);

                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(x, y);
                            animatorSet.setDuration(0);
                            animatorSet.start();

                            lastX = event.getRawX();
                            lastY = event.getRawY();
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                        {
                            // 属性动画移动
                            ObjectAnimator y = ObjectAnimator.ofFloat(ivPrincipleAlpha, "y", ivPrincipleAlpha.getY(), 200);
                            ObjectAnimator x = ObjectAnimator.ofFloat(ivPrincipleAlpha, "x", ivPrincipleAlpha.getX(), 200);

                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(x, y);
                            animatorSet.setDuration(500);
                            animatorSet.start();

                            lastX = event.getRawX();
                            lastY = event.getRawY();
                        }
                        return false;
                }
                return false;
            }
        });
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_split;
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_back, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ((CourseActivity)getContext()).switchFragment(CourseActivity.FRAGMENT_PRINCIPLE);
                break;
            case R.id.tv_next:
                break;
        }
    }
}
