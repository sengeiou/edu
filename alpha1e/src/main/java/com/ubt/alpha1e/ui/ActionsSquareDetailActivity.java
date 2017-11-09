package com.ubt.alpha1e.ui;

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
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.thrid_party.IWeiXinListener;
import com.ubt.alpha1e.business.thrid_party.MyFaceBook;
import com.ubt.alpha1e.business.thrid_party.MyTencent;
import com.ubt.alpha1e.business.thrid_party.MyTwitter;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.ui.fragment.ActionsSquareDetailFragment;
import com.ubt.alpha1e.ui.helper.ActionsOnlineHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2016/5/11.
 * Actions playground activity
 */
public class ActionsSquareDetailActivity extends BaseActivity implements View.OnClickListener,IUiListener, IWeiXinListener {

    private static final String TAG = "ActionsSquareDetailActivity";
    private static final String DETAIL_TYPE = "DETAIL_TYPE";
    private static final String SORT_TYPE = "SORT_TYPE";
    private String title = "";
    public ActionsSquareDetailFragment mActionsSquareDetailFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    public int actionSonType = -1;//动作库分类类型
    public int actionSortType = 1;//动作库排序类型 1最新 2最热 3好玩
    public TabLayout tabLayout;
    public RelativeLayout rl_share;
    public RelativeLayout rl_type_select;
    public static String shareUrl = "";
    private ImageButton btn_to_qq, btn_to_face, btn_to_twitter, btn_to_wechat, btn_to_friends, btn_qqzone;
    private Button btn_cancel_share;
    private ImageView imgView_type_select;
    private TextView txt_base_title_name;
    private ImageView imgView_show_content_type;
    private boolean isShowActionOnly = true;

    public ActionOnlineInfo actionOnlineInfo = new ActionOnlineInfo();
    private String[] acitonTypes = {"ui_square_all",
                                    "ui_square_dance",
                                    "ui_square_story",
                                    "ui_square_sport",
                                    "ui_square_childrensong",
                                    "ui_square_science"};

    //actionSonType=0 查询全部  1 舞蹈 2 故事 3 基本 4 儿歌 5 科普
    private int[] actionSonTypes = { 0, 1, 3, 2, 5, 4 };

    public Fragment mCurrentFragment;
    private LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();

    private Animation type_select_anim;

    private TextView tv_actions_type_all;
    private TextView tv_actions_type_dance;
    private TextView tv_actions_type_sport;
    private TextView tv_actions_type_story;
    private TextView tv_actions_type_science;
    private TextView tv_actions_type_childrensong;

    public static void launchActivity(Activity activity, int type,int sort) {
        Intent intent = new Intent();
        intent.setClass(activity,ActionsSquareDetailActivity.class);
        intent.putExtra(DETAIL_TYPE,type);
        intent.putExtra(SORT_TYPE,sort);
        activity.startActivity(intent);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_square_detail);
        actionSonType = actionSonTypes[(int)getIntent().getExtras().get(DETAIL_TYPE)];
        actionSortType = (int)getIntent().getExtras().get(SORT_TYPE);
        isShowActionOnly = true;
        UbtLog.d(TAG,"actionSortType = " + actionSortType);
        initUI();
        initControlListener();
    }

    @Override
    protected void onPause() {
        hideActionsTypeSelectLayout();
        super.onPause();
    }


    @Override
    protected void initUI() {
        initTitle(getStringResources(acitonTypes[actionSonType]));
        rl_share = (RelativeLayout) findViewById(R.id.layout_share);
        rl_type_select = (RelativeLayout) findViewById(R.id.lay_type_select);
        imgView_type_select = (ImageView) findViewById(R.id.imgView_type_select);
        txt_base_title_name = (TextView) findViewById(R.id.txt_base_title_name);
        imgView_show_content_type = (ImageView) findViewById(R.id.imgView_show_content_type);
        type_select_anim = new TranslateAnimation(0, 0, 100, 0);
        type_select_anim.setDuration(600); //设置持续时间1秒

        //给TabLayout增加Tab, 并关联ViewPager
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        TabLayout.Tab tab1 = tabLayout.newTab().setText(getStringResources("ui_square_new"));
        TabLayout.Tab tab2 = tabLayout.newTab().setText(getStringResources("ui_official_hot"));
        TabLayout.Tab tab3 = tabLayout.newTab().setText(getStringResources("ui_official_recommend"));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        if(actionSortType == 2){
            tabLayout.addTab(tab1,false);
            tabLayout.addTab(tab2,true);
            tabLayout.addTab(tab3,false);
        }else {
            tabLayout.addTab(tab1,true);
            tabLayout.addTab(tab2,false);
            tabLayout.addTab(tab3,false);
        }

        mActionsSquareDetailFragment = ActionsSquareDetailFragment.newInstance(actionSortType,actionSonType);
        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.rl_fragment_content, mActionsSquareDetailFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = mActionsSquareDetailFragment;
        mFragmentCache.put(actionSortType, mCurrentFragment);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                UbtLog.d(TAG,"onTabSelected:"+tab.getPosition());
                actionSortType = tab.getPosition()+1;
                Fragment f = mFragmentCache.containsKey(actionSortType) ? mFragmentCache.get(actionSortType)
                        : ActionsSquareDetailFragment.newInstance(actionSortType, actionSonType);
                if (!mFragmentCache.containsKey(actionSortType)) {
                    mFragmentCache.put(actionSortType, f);
                }
                ActionsOnlineHelper.actionLocalSortType = actionSortType;
                ActionsOnlineHelper.actionLocalSonType = actionSonType;
                loadFragment(f);

//                if(mActionsSquareDetailFragment!=null)
//                    mActionsSquareDetailFragment.refreshData(actionSortType);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
                                    Toast.makeText(ActionsSquareDetailActivity.this,getStringResources("ui_common_operate_fail"),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        rl_share.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActionsSquareDetailActivity.this,getStringResources("ui_common_network_request_failed"),Toast.LENGTH_SHORT).show();
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

        tv_actions_type_all = (TextView) findViewById(R.id.tv_actions_type_all);
        tv_actions_type_dance = (TextView) findViewById(R.id.tv_actions_type_dance);
        tv_actions_type_sport = (TextView) findViewById(R.id.tv_actions_type_sport);
        tv_actions_type_story = (TextView) findViewById(R.id.tv_actions_type_story);
        tv_actions_type_science = (TextView) findViewById(R.id.tv_actions_type_science);
        tv_actions_type_childrensong = (TextView) findViewById(R.id.tv_actions_type_childrensong);


        btn_to_qq.setOnClickListener(this);
        btn_to_wechat.setOnClickListener(this);
        btn_to_friends.setOnClickListener(this);
        btn_to_face.setOnClickListener(this);
        btn_to_twitter.setOnClickListener(this);
        btn_qqzone.setOnClickListener(this);
        btn_cancel_share.setOnClickListener(this);

        imgView_type_select.setOnClickListener(this);
        txt_base_title_name.setOnClickListener(this);
        imgView_show_content_type.setOnClickListener(this);

        tv_actions_type_all.setOnClickListener(this);
        tv_actions_type_dance.setOnClickListener(this);
        tv_actions_type_sport.setOnClickListener(this);
        tv_actions_type_story.setOnClickListener(this);
        tv_actions_type_science.setOnClickListener(this);
        tv_actions_type_childrensong.setOnClickListener(this);

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
                MyTwitter.doShareTwitter(ActionsSquareDetailActivity.this, actionOnlineInfo, pactStringUrl(shareUrl,actionOnlineInfo.actionId+""));
                break;
            case R.id.btn_to_qq:
                MyTencent.doShareQQ(ActionsSquareDetailActivity.this, actionOnlineInfo, pactStringUrl(shareUrl,actionOnlineInfo.actionId+""), ActionsSquareDetailActivity.this);
                break;
            case R.id.btn_to_qq_weixin:
//                MyWeiXin.doShareToWeiXin(pactStringUrl(shareUrl,actionOnlineInfo.actionId+""), actionOnlineInfo, ActionsSquareDetailActivity.this, ActionsSquareDetailActivity.this, 0);
                break;
            case R.id.btn_to_qq_weixin_pengyouquan:
//                MyWeiXin.doShareToWeiXin(pactStringUrl(shareUrl,actionOnlineInfo.actionId+""), actionOnlineInfo, ActionsSquareDetailActivity.this, ActionsSquareDetailActivity.this, 1);
                break;
            case R.id.btn_to_qq_zone:
                MyTencent.doShareQQKongjian(ActionsSquareDetailActivity.this,
                        actionOnlineInfo, pactStringUrl(shareUrl,actionOnlineInfo.actionId+""),
                        ActionsSquareDetailActivity.this);
                break;
            case R.id.btn_cancel_share:
                if (rl_share.getVisibility() == View.VISIBLE) {
                    rl_share.setVisibility(View.GONE);
                }
                break;
            case R.id.imgView_type_select:
            case R.id.txt_base_title_name:
                if(rl_type_select.getVisibility() == View.VISIBLE){
                    hideActionsTypeSelectLayout();
                }else{
                    showActionsTypeSelectLayout();
                }
                break;

            case R.id.tv_actions_type_all:
                actionSonType = actionSonTypes[0];
                initTitle(getStringResources(acitonTypes[actionSonType]));
                changAtionsTypeRereshData();
                break;
            case R.id.tv_actions_type_dance:
                actionSonType = actionSonTypes[1];
                initTitle(getStringResources(acitonTypes[actionSonType]));
                changAtionsTypeRereshData();
                break;
            case R.id.tv_actions_type_sport:
                actionSonType = actionSonTypes[2];
                initTitle(getStringResources(acitonTypes[actionSonType]));
                changAtionsTypeRereshData();
                break;
            case R.id.tv_actions_type_story:
                actionSonType = actionSonTypes[3];
                initTitle(getStringResources(acitonTypes[actionSonType]));
                changAtionsTypeRereshData();
                break;
            case R.id.tv_actions_type_science:
                actionSonType = actionSonTypes[4];
                initTitle(getStringResources(acitonTypes[actionSonType]));
                changAtionsTypeRereshData();
                break;
            case R.id.tv_actions_type_childrensong:
                actionSonType = actionSonTypes[5];
                initTitle(getStringResources(acitonTypes[actionSonType]));
                changAtionsTypeRereshData();
                break;
            case R.id.imgView_show_content_type:
                if(isShowActionOnly){
                    isShowActionOnly = false;
                    imgView_show_content_type.setImageDrawable(getDrawableRes("actions_square_show_action"));
                }else{
                    isShowActionOnly = true;
                    imgView_show_content_type.setImageDrawable(getDrawableRes("actions_square_show_all"));
                }
                changeContentType();
                break;
            default:
                break;
        }
    }

    /**
     * 改变显示内容类型
     */
    private void changeContentType(){
        ((ActionsSquareDetailFragment)mCurrentFragment).changeContentType();
    }

    /**
     * 获取显示内容类型
     * @return
     */
    public boolean getShowContentType(){
        return isShowActionOnly;
    }

    private void showActionsTypeSelectLayout(){
        UbtLog.d(TAG,"lihai-----showActionsTypeSelectLayout-->>>");
        imgView_type_select.setImageResource(R.drawable.actions_square_type_stuffe_sel);
        rl_type_select.setVisibility(View.VISIBLE);
        rl_type_select.setAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_uptodown));

    }

    public void hideActionsTypeSelectLayout(){
        UbtLog.d(TAG,"lihai-----showActionsTypeSelectLayout--: " + rl_type_select.getVisibility());
        if(rl_type_select.getVisibility() != View.GONE){
            imgView_type_select.setImageResource(R.drawable.actions_square_type_stuffe);
            rl_type_select.setVisibility(View.GONE);
            rl_type_select.setAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_downtoup));
        }
    }

    private void changAtionsTypeRereshData(){
        UbtLog.d(TAG,"lihai--------changAtionsTypeRereshData--" + actionSonType);

        hideActionsTypeSelectLayout();

        mActionsSquareDetailFragment = null;
        mFragmentCache.clear();

        mActionsSquareDetailFragment = ActionsSquareDetailFragment.newInstance(actionSortType,actionSonType);
        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.rl_fragment_content, mActionsSquareDetailFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = mActionsSquareDetailFragment;
        mFragmentCache.put(actionSortType, mCurrentFragment);
    }

    @Override
    public void onComplete(Object arg0) {
        rl_share.setVisibility(View.GONE);
    }

    @Override
    public void onError(UiError arg0) {
        rl_share.setVisibility(View.GONE);
        Toast.makeText(this,getStringResources("ui_action_share_fail"), Toast.LENGTH_SHORT)
                .show();
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
                    ActionsSquareDetailActivity.this);
        }

        if(requestCode == 10086)
        {
            if(mCurrentFragment!=null)
                ((ActionsSquareDetailFragment)mCurrentFragment).setAutoRefresh();
        }
    }

    private void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        //UbtLog.d(TAG,"targetFragment.isAdded()->"+(!targetFragment.isAdded()));
        if (!targetFragment.isAdded()) {
           ((ActionsSquareDetailFragment)mCurrentFragment).removeListeners();
            transaction
                    .hide(mCurrentFragment)
                    .add(R.id.rl_fragment_content, targetFragment)
                    .commit();
        } else {
            ((ActionsSquareDetailFragment)mCurrentFragment).removeListeners();
            ((ActionsSquareDetailFragment)targetFragment).registerListeners();
            transaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;

        //fragment 切换数据刷新,第一次创建不用请求，mHelper = null
        if(((ActionsSquareDetailFragment) mCurrentFragment).mHelper != null){
            ((ActionsSquareDetailFragment) mCurrentFragment).requestData();
            ((ActionsSquareDetailFragment) mCurrentFragment).changeContentType();
        }
    }
}
