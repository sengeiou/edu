package com.ubt.alpha1e_edu.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.business.thrid_party.MyFaceBook;
import com.ubt.alpha1e_edu.business.thrid_party.MyTencent;
import com.ubt.alpha1e_edu.business.thrid_party.MyTwitter;
import com.ubt.alpha1e_edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e_edu.services.HomeKeyReceiveListener;
import com.ubt.alpha1e_edu.ui.fragment.IShowSquareDetailFragment;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */


public class SharePopupWindow {

    private static final String TAG = "SharePopupWindow";

    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private BaseActivity mActivity;
    private LayoutInflater mLayoutInflater;
    private RelativeLayout shareLayout;

    private ImageButton btn_to_qq, btn_to_face, btn_to_twitter, btn_to_wechat, btn_to_friends, btn_qqzone;
    private Button btn_cancel_share;

    public String shareUrl = "";
    public ActionOnlineInfo actionOnlineInfo = null;
    public IShowSquareDetailFragment showSquareDetailFragment = null;

    private HomeKeyReceiveListener homeKeyReceiveListener;

    public SharePopupWindow(BaseActivity activity, ActionOnlineInfo mActionOnlineInfo, String mShareUrl,IShowSquareDetailFragment mIShowSquareDetailFragment) {
        UbtLog.d(TAG, "--lihai-----create SharePopupWindow!");
        mActivity = activity;
        actionOnlineInfo = mActionOnlineInfo;
        shareUrl = mShareUrl;
        showSquareDetailFragment = mIShowSquareDetailFragment;

        createView();

        homeKeyReceiveListener = new HomeKeyReceiveListener(mActivity);
        homeKeyReceiveListener.setOnHomePressedListener(new HomeKeyReceiveListener.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                removeSharePopWindow();
            }

            @Override
            public void onHomeLongPressed() {
                removeSharePopWindow();
            }

            @Override
            public void onScreenOff() {
                removeSharePopWindow();
            }
        });

        homeKeyReceiveListener.startListener();
    }

    private void createView(){

        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager)mActivity.getSystemService(Context.WINDOW_SERVICE);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
//        }else{
//            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        }

        wmParams.alpha = 10;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wmParams.x = 0;
        wmParams.y = 0;

        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        shareLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.popuwindow_share, null);

        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(120, 0, 0, 0));
        shareLayout.setBackground(colorDrawable);
        initView();

        mWindowManager.addView(shareLayout, wmParams);
    }

    private void initView(){

        btn_cancel_share = (Button) shareLayout.findViewById(R.id.btn_cancel_share);
        btn_to_qq = (ImageButton) shareLayout.findViewById(R.id.btn_to_qq);
        btn_to_wechat = (ImageButton) shareLayout.findViewById(R.id.btn_to_qq_weixin);
        btn_to_friends = (ImageButton) shareLayout.findViewById(R.id.btn_to_qq_weixin_pengyouquan);
        btn_to_face = (ImageButton) shareLayout.findViewById(R.id.btn_to_face);
        btn_to_twitter = (ImageButton) shareLayout.findViewById(R.id.btn_to_twitter);
        btn_qqzone = (ImageButton) shareLayout.findViewById(R.id.btn_to_qq_zone);


        View.OnClickListener onClickListener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_to_face:
                        MyFaceBook.doShareFaceBook(mActivity, actionOnlineInfo,pactStringUrl(shareUrl,actionOnlineInfo.actionId+""));
                        removeSharePopWindow();
                        break;
                    case R.id.btn_to_twitter:
                        MyTwitter.doShareTwitter(mActivity, actionOnlineInfo, pactStringUrl(shareUrl,actionOnlineInfo.actionId+""));
                        removeSharePopWindow();
                        break;
                    case R.id.btn_to_qq:
                        MyTencent.doShareQQ(mActivity, actionOnlineInfo, pactStringUrl(shareUrl,actionOnlineInfo.actionId+""), showSquareDetailFragment);
                        removeSharePopWindow();
                        break;
                    case R.id.btn_to_qq_weixin:
//                        MyWeiXin.doShareToWeiXin(pactStringUrl(shareUrl,actionOnlineInfo.actionId+""), actionOnlineInfo, mActivity, showSquareDetailFragment, 0);
//                        removeSharePopWindow();
                        break;
                    case R.id.btn_to_qq_weixin_pengyouquan:
//                        MyWeiXin.doShareToWeiXin(pactStringUrl(shareUrl,actionOnlineInfo.actionId+""), actionOnlineInfo, mActivity, showSquareDetailFragment, 1);
//                        removeSharePopWindow();
                        break;
                    case R.id.btn_to_qq_zone:
                        MyTencent.doShareQQKongjian(mActivity,actionOnlineInfo, pactStringUrl(shareUrl,actionOnlineInfo.actionId+""),showSquareDetailFragment);
                        removeSharePopWindow();
                        break;
                    case R.id.btn_cancel_share:
                        removeSharePopWindow();
                        break;

                }
            }
        };
        btn_cancel_share.setOnClickListener(onClickListener);
        btn_to_qq.setOnClickListener(onClickListener);
        btn_to_wechat.setOnClickListener(onClickListener);
        btn_to_friends.setOnClickListener(onClickListener);
        btn_to_face.setOnClickListener(onClickListener);
        btn_to_twitter.setOnClickListener(onClickListener);
        btn_qqzone.setOnClickListener(onClickListener);

    }

    private String pactStringUrl(String url,String shareId)
    {
        return url+"actionId="+shareId+"&lange=" + mActivity.getStandardLocale(mActivity.getAppSetLanguage());
    }

    public  void removeSharePopWindow() {
        if(shareLayout != null) {
            mWindowManager.removeView(shareLayout);
            homeKeyReceiveListener.stopListiener();
            shareLayout = null;
        }
    }

}
