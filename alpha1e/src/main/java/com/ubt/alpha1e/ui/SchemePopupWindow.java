package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.services.HomeKeyReceiveListener;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */


public class SchemePopupWindow {

    private static final String TAG = "SchemePopupWindow";

    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private RelativeLayout schemeLayout;

    private ImageView schemeImageView = null;
    private ImageView closeImageView = null;
    private String mSchemeName = null;
    private String mSchemeId = null;
    private String mSchemeUrl = null;
    private Bitmap mBitmap = null;

    private HomeKeyReceiveListener homeKeyReceiveListener;

    public SchemePopupWindow(Activity activity, String schemeName,String schemeId,String schemeUrl,Bitmap bitmap) {
        UbtLog.d(TAG, "--lihai-----create SchemePopupWindow!");
        mActivity = activity;
        mSchemeName = schemeName;
        mSchemeId = schemeId;
        mSchemeUrl = schemeUrl;
        mBitmap = bitmap;
        createView();

        homeKeyReceiveListener = new HomeKeyReceiveListener(mActivity);
        homeKeyReceiveListener.setOnHomePressedListener(new HomeKeyReceiveListener.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                 removeSchemePopWindow();
            }

            @Override
            public void onHomeLongPressed() {
                 removeSchemePopWindow();
            }

            @Override
            public void onScreenOff() {
                 removeSchemePopWindow();
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
        schemeLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.popuwindow_scheme, null);

        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(120, 0, 0, 0));
        schemeLayout.setBackground(colorDrawable);
        initView();

        mWindowManager.addView(schemeLayout, wmParams);
    }

    private void initView(){

        schemeImageView = (ImageView) schemeLayout.findViewById(R.id.img_dialog_scheme);
        closeImageView = (ImageView) schemeLayout.findViewById(R.id.img_dialog_close);
        schemeImageView.setImageBitmap(mBitmap);

        View.OnClickListener onClickListener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch (v.getId()){
                    case R.id.img_dialog_scheme:
                        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.getSchemeInfo);

                        if(!mSchemeUrl.contains("fromWebview")){
                            mSchemeUrl = mSchemeUrl + "?fromWebview=true";
                        }

                        UbtLog.d(TAG,"mSchemeName:"+mSchemeName + "    mSchemeUrl:"+mSchemeUrl + "  url::"+url);

                        Intent mIntent = new Intent(mActivity, WebContentActivity.class);
                        mIntent.putExtra(WebContentActivity.WEB_TITLE, mSchemeName);
                        mIntent.putExtra(WebContentActivity.WEB_SCHEME_ID,mSchemeId);
                        mIntent.putExtra(WebContentActivity.WEB_URL, mSchemeUrl);
                        mIntent.putExtra(WebContentActivity.WEB_IS_SHARE,true);

                        mActivity.startActivity(mIntent);

                         removeSchemePopWindow();
                        break;
                    case R.id.img_dialog_close:
                         removeSchemePopWindow();
                        break;

                }

                ((BaseActivity)mActivity).recordSchemeShowState("1");

            }
        };
        schemeImageView.setOnClickListener(onClickListener);
        closeImageView.setOnClickListener(onClickListener);

    }


    public  void removeSchemePopWindow() {
        if(schemeLayout != null) {
            mWindowManager.removeView(schemeLayout);
            homeKeyReceiveListener.stopListiener();
            schemeLayout = null;
        }
    }

}
