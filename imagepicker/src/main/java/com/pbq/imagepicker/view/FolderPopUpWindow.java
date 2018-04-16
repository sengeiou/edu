package com.pbq.imagepicker.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.pbq.imagepicker.R;
import com.pbq.imagepicker.Utils;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：16/8/1
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class FolderPopUpWindow extends PopupWindow implements View.OnClickListener {

    private static final String TAG = FolderPopUpWindow.class.getSimpleName();

    private ListView listView;
    private OnItemClickListener onItemClickListener;
    private final View masker;
    private int marginPx;

    public FolderPopUpWindow(Activity context, BaseAdapter adapter) {
        super(context);

        final View view = View.inflate(context, R.layout.imagepick_pop_folder, null);

        masker = view.findViewById(R.id.masker);
        masker.setOnClickListener(this);

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);  //如果不设置，就是 AnchorView 的宽度
        //setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        final DisplayMetrics displayMetrics = Utils.getScreenPix(context);
        Log.d(TAG,"displayMetrics = " + displayMetrics.widthPixels + "/" + displayMetrics.heightPixels + "/" + displayMetrics.density);
        setHeight((int)(displayMetrics.heightPixels - displayMetrics.density*60));

        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));

        setAnimationStyle(0);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int maxHeight = view.getHeight() * 6 / 8;

                int realHeight = listView.getHeight();
                Log.d(TAG,"maxHeight = " + maxHeight + "    realHeight = " + realHeight + " marginPx ==>> " + marginPx);

                ViewGroup.LayoutParams listParams = listView.getLayoutParams();
                listParams.height = realHeight > maxHeight ? maxHeight : realHeight;
                listView.setLayoutParams(listParams);

                enterAnimator();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(adapterView, view, position, l);
                }
            }
        });
    }

    private void enterAnimator() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(masker, "alpha", 0, 1);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(listView, "translationY", -listView.getHeight(), 0);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(400);
        set.playTogether(alpha, translationY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    @Override
    public void dismiss() {
        exitAnimator();
    }

    private void exitAnimator() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(masker, "alpha", 1, 0);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(listView, "translationY", 0, -listView.getHeight());
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.playTogether(alpha, translationY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                FolderPopUpWindow.super.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.start();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setSelection(int selection) {
        listView.setSelection(selection);
    }

    public void setMargin(int marginPx) {
        this.marginPx = marginPx;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"dismiss");
        dismiss();
    }

    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> adapterView, View view, int position, long l);
    }
}
