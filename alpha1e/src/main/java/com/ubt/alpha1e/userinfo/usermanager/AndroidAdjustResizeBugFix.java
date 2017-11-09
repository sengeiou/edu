package com.ubt.alpha1e.userinfo.usermanager;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**
 * @author：liuhai
 * @date：2017/11/7 15:55
 * @modifier：ubt
 * @modify_date：2017/11/7 15:55
 * [A brief description]
 * version
 */

public class AndroidAdjustResizeBugFix {
    private View mChildOfContent;
    private int usableHeightPrevious;
    private int statusBarHeight;
    private FrameLayout.LayoutParams frameLayoutParams;
    private Activity mActivity;
    OnKeyChangerListeler mOnKeyChangerListeler;

    public AndroidAdjustResizeBugFix(Activity activity) {
        mActivity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        statusBarHeight = getStatusBarHeight();
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }



    public void setOnKeyChangerListeler(OnKeyChangerListeler onKeyChangerListeler) {
        mOnKeyChangerListeler = onKeyChangerListeler;
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                // 如果有高度变化，mChildOfContent.requestLayout()之后界面才会重新测量
                // 这里就随便让原来的高度减去了1
               // ToastUtils.showShort("软件盘开启");
                frameLayoutParams.height = usableHeightSansKeyboard - 1;
                if (null != mOnKeyChangerListeler) {
                    mOnKeyChangerListeler.keyBoardOpen(true);
                }
            } else {
                // keyboard probably just became hidden
                //ToastUtils.showShort("软件盘关闭");
                frameLayoutParams.height = usableHeightSansKeyboard;
                if (null != mOnKeyChangerListeler) {
                    mOnKeyChangerListeler.keyBoardOpen(false);
                }
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return r.bottom - r.top + statusBarHeight;
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            int dimensionPixelSize = mActivity.getResources().getDimensionPixelSize(x);
            return dimensionPixelSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public interface OnKeyChangerListeler {
        void keyBoardOpen(boolean statu);
    }
}
