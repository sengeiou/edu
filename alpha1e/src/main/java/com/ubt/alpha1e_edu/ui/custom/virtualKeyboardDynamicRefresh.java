package com.ubt.alpha1e_edu.ui.custom;

/**
 * @作者：ubt
 * @日期: 2018/1/20 11:13
 * @描述:
 */
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.ubt.alpha1e_edu.utils.log.UbtLog;

/**
 * Created by win7 on 2016/12/14.
 */


public class virtualKeyboardDynamicRefresh {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
    static CommonCtrlView mCommonCtrlView;
    /**
     * 关联要监听的视图
     *
     * @param viewObserving
     */
    public static void assistActivity(View viewObserving, CommonCtrlView commonCtrlView) {
        new virtualKeyboardDynamicRefresh(viewObserving);
        mCommonCtrlView=commonCtrlView;
    }

    private View mViewObserved;//被监听的视图
//    private int usableHeightPrevious;//视图变化前的可用高度
private int usableWidthPrevious=0;//视图变化前的可用高度
    private ViewGroup.LayoutParams frameLayoutParams;

    private virtualKeyboardDynamicRefresh(View viewObserving) {
        mViewObserved = viewObserving;
        //给View添加全局的布局监听器
        mViewObserved.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
               // resetLayoutByUsableHeight(computeUsableHeight());
                resetLayoutByUsableHeight(computeUsableWidth());
            }
        });
        frameLayoutParams = mViewObserved.getLayoutParams();
    }

//    private void resetLayoutByUsableHeight(int usableHeightNow) {
//        //比较布局变化前后的View的可用高度
//        if (usableHeightNow != usableHeightPrevious) {
//            UbtLog.d("virtualKeyboardDynamicRefreshs","HEIGHT IS NOT EQUAL");
//            //如果两次高度不一致
//            //将当前的View的可用高度设置成View的实际高度
//            frameLayoutParams.height = usableHeightNow;
//            mViewObserved.requestLayout();//请求重新布局
//            usableHeightPrevious = usableHeightNow;
//        }
//    }
    private void resetLayoutByUsableHeight(int usableWidthNow) {
        //比较布局变化前后的View的可用高度
        UbtLog.d("virtualKeyboardDynamicRefreshs","width "+usableWidthNow+"width before"+usableWidthPrevious);
        if (usableWidthNow != usableWidthPrevious) {
            UbtLog.d("virtualKeyboardDynamicRefreshs","WIDTH IS NOT EQUAL");
            //如果两次高度不一致
            //将当前的View的可用高度设置成View的实际高度
            frameLayoutParams.width =usableWidthNow;
            mViewObserved.requestLayout();//请求重新布局
            usableWidthPrevious = usableWidthNow;
//            if(usableWidthPrevious!=0) {
//                if (mCommonCtrlView != null) {
//                    UbtLog.d("virtualKeyboardDynamicRefreshs", "ADJUST THE SIZE");
//                    mCommonCtrlView.dynamicAdjustWindowSize();
//                }
//            }

        }
    }

    /**
     * 计算视图可视高度
     *
     * @return
     */
    private int computeUsableHeight() {
        Rect r = new Rect();
        mViewObserved.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }
    private int computeUsableWidth(){
        Rect r=new Rect();
        mViewObserved.getWindowVisibleDisplayFrame(r);
        return (r.right-r.left);
    }
}

