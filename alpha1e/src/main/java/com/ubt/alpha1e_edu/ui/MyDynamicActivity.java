package com.ubt.alpha1e_edu.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.business.thrid_party.IWeiXinListener;
import com.ubt.alpha1e_edu.business.thrid_party.MyFaceBook;
import com.ubt.alpha1e_edu.business.thrid_party.MyTencent;
import com.ubt.alpha1e_edu.business.thrid_party.MyTwitter;
import com.ubt.alpha1e_edu.data.JsonTools;
import com.ubt.alpha1e_edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e_edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e_edu.net.http.basic.IJsonListener;
import com.ubt.alpha1e_edu.ui.fragment.IShowFragment;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2016/5/11.
 * Actions playground activity
 */
public class MyDynamicActivity extends BaseActivity implements View.OnClickListener,IUiListener, IWeiXinListener {

    private static final String TAG = "ActionsSquareDetailActivity";
    private static final String DETAIL_TYPE = "DETAIL_TYPE";
    private String title = "";
    public IShowFragment mActionsSquareDetailFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    public TabLayout tabLayout;
    public RelativeLayout rl_share;
    public RelativeLayout rl_type_select;
    public static String shareUrl = "";
    private ImageButton btn_to_qq, btn_to_face, btn_to_twitter, btn_to_wechat, btn_to_friends, btn_qqzone;
    private Button btn_cancel_share;
    public ActionOnlineInfo actionOnlineInfo = new ActionOnlineInfo();


    public Fragment mCurrentFragment;
    private LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();

    private Animation type_select_anim;

    public static final int SEND_DYNAMIC_CODE = 110;



    public static void launchActivity(Activity activity/*, int type*/) {
        Intent intent = new Intent();
        intent.setClass(activity,MyDynamicActivity.class);
//        intent.putExtra(DETAIL_TYPE,type);
        activity.startActivity(intent);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dynamic);
//        actionSonType = actionSonTypes[(int)getIntent().getExtras().get(DETAIL_TYPE)];


        initUI();
        initControlListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void initUI() {
        initTitle(getStringResources("ui_mine_dymamic"));
        rl_share = (RelativeLayout) findViewById(R.id.layout_share);
        rl_type_select = (RelativeLayout) findViewById(R.id.lay_type_select);
        type_select_anim = new TranslateAnimation(0, 0, 100, 0);
        type_select_anim.setDuration(600); //设置持续时间1秒

        mActionsSquareDetailFragment = IShowFragment.newInstance(0,8);
        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.rl_fragment_content, mActionsSquareDetailFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = mActionsSquareDetailFragment;
//        mFragmentCache.put(actionSortType, mCurrentFragment);

    }

    public void setDisableTabClickListener(final boolean isDisable)
    {
        if(tabLayout == null){
            return;
        }
        UbtLog.d(TAG,"setDisableTabClickListener:"+isDisable);
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return isDisable;
                }
            });
        }
    }

    public void doShareActions(ActionOnlineInfo info)
    {
        actionOnlineInfo = info;
        UbtLog.d(TAG,"lihai-----------shareUrl::"+shareUrl);
        if(TextUtils.isEmpty(shareUrl)){
            GetDataFromWeb.getJsonByPost(1001, HttpAddress
                    .getRequestUrl(HttpAddress.Request_type.get_share_url), HttpAddress
                    .getParamsForPost(new String[]{"share", "url"},
                            HttpAddress.Request_type.get_share_url,
                            ((AlphaApplication) this.getApplicationContext()).getCurrentUserInfo(),
                            this), new IJsonListener() {
                @Override
                public void onGetJson(boolean isSuccess, String json, long request_code) {
                    //UbtLog.d(TAG,"isSuccess:"+isSuccess + "   json:"+json );
                    if(isSuccess)
                    {
                        if (JsonTools.getJsonStatus(json)) {
                            try {
                                shareUrl = new JSONObject(json).getString("models");
                                rl_share.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        rl_share.setVisibility(View.VISIBLE);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            rl_share.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MyDynamicActivity.this,getStringResources("ui_common_operate_fail"),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        rl_share.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyDynamicActivity.this,getStringResources("ui_common_network_request_failed"),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }else {
            rl_share.post(new Runnable() {
                @Override
                public void run() {
                    rl_share.setVisibility(View.VISIBLE);
                }
            });
        }

    }
    @Override
    protected void initControlListener() {
        btn_cancel_share = (Button) findViewById(R.id.btn_cancel_share);
        btn_to_qq = (ImageButton) findViewById(R.id.btn_to_qq);
        btn_to_wechat = (ImageButton) findViewById(R.id.btn_to_qq_weixin);
        btn_to_friends = (ImageButton) findViewById(R.id.btn_to_qq_weixin_pengyouquan);
        btn_to_face = (ImageButton) findViewById(R.id.btn_to_face);
        btn_to_twitter = (ImageButton) findViewById(R.id.btn_to_twitter);
        btn_qqzone = (ImageButton) findViewById(R.id.btn_to_qq_zone);



        btn_to_qq.setOnClickListener(this);
        btn_to_wechat.setOnClickListener(this);
        btn_to_friends.setOnClickListener(this);
        btn_to_face.setOnClickListener(this);
        btn_to_twitter.setOnClickListener(this);
        btn_qqzone.setOnClickListener(this);
        btn_cancel_share.setOnClickListener(this);



        rl_type_select.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //直接返回true，被覆盖层不响应
                return true;
            }
        });

        rl_share.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //直接返回true，被覆盖层不响应
                return true;
            }
        });
    }

    @Override
    protected void initBoardCastListener() {

    }


    private String pactStringUrl(String url,String shareId)
    {
        return url+"actionId="+shareId+"&lange="+getStandardLocale(getAppSetLanguage());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_to_face:
                MyFaceBook.doShareFaceBook(this, actionOnlineInfo,pactStringUrl(shareUrl,actionOnlineInfo.actionId+""));
                break;
            case R.id.btn_to_twitter:
                MyTwitter.doShareTwitter(MyDynamicActivity.this, actionOnlineInfo, pactStringUrl(shareUrl,actionOnlineInfo.actionId+""));
                break;
            case R.id.btn_to_qq:
                MyTencent.doShareQQ(MyDynamicActivity.this, actionOnlineInfo, pactStringUrl(shareUrl,actionOnlineInfo.actionId+""), MyDynamicActivity.this);
                break;
            case R.id.btn_to_qq_weixin:
//                MyWeiXin.doShareToWeiXin(pactStringUrl(shareUrl,actionOnlineInfo.actionId+""), actionOnlineInfo, MyDynamicActivity.this, MyDynamicActivity.this, 0);
                break;
            case R.id.btn_to_qq_weixin_pengyouquan:
//                MyWeiXin.doShareToWeiXin(pactStringUrl(shareUrl,actionOnlineInfo.actionId+""), actionOnlineInfo, MyDynamicActivity.this, MyDynamicActivity.this, 1);
                break;
            case R.id.btn_to_qq_zone:
                MyTencent.doShareQQKongjian(MyDynamicActivity.this,
                        actionOnlineInfo, pactStringUrl(shareUrl,actionOnlineInfo.actionId+""),
                        MyDynamicActivity.this);
                break;
            case R.id.btn_cancel_share:
                if (rl_share.getVisibility() == View.VISIBLE) {
                    rl_share.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }




    @Override
    public void onComplete(Object arg0) {
        rl_share.setVisibility(View.GONE);
    }

    @Override
    public void onError(UiError arg0) {
        rl_share.setVisibility(View.GONE);
//        Toast.makeText(this,
//                this.getResources().getString(R.string.ui_action_share_fail), Toast.LENGTH_SHORT)
//                .show();
    }

    @Override
    public void onCancel() {
        rl_share.setVisibility(View.GONE);
    }

    @Override
    public void noteWeixinNotInstalled() {
        rl_share.setVisibility(View.GONE);
        Toast.makeText(this,getStringResources("ui_action_share_no_wechat"),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rl_share.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != 12306){
            Tencent.onActivityResultData(requestCode, resultCode, data,
                    MyDynamicActivity.this);
        }

        if(requestCode == 10086)
        {
            if(mCurrentFragment!=null)
                ((IShowFragment)mCurrentFragment).setAutoRefresh();
        }

        if(requestCode == SEND_DYNAMIC_CODE){
            if(resultCode == RESULT_OK){
                UbtLog.d(TAG, "request data ---111");
                mCurrentFragment.onResume();
                ((IShowFragment)mCurrentFragment).requestData();
            }
        }
    }

}
