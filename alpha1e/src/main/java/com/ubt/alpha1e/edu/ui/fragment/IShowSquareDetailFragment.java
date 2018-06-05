package com.ubt.alpha1e.edu.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.business.thrid_party.IWeiXinListener;
import com.ubt.alpha1e.edu.data.JsonTools;
import com.ubt.alpha1e.edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.net.http.basic.IJsonListener;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.MyMainActivity;
import com.ubt.alpha1e.edu.ui.SharePopupWindow;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.helper.ActionsOnlineHelper;
import com.ubt.alpha1e.edu.ui.helper.MainHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;


/**
 * 重写原有的ActionsLibMainFragment类
 */
public class IShowSquareDetailFragment extends BaseFragment implements IUiListener, IWeiXinListener,BaseDiaUI {
    private static final String TAG = "IShowSquareDetailFragment";
    private View mMainView;
    public static final int UPDATE_ITEMS = 0;
    public static final int SHOW_SHARE_PAGE = 1;
    public MyMainActivity activity;
    private MainHelper mMainHelper;
    private MyMainActivity mActivity;

    public IShowFragment mIShowFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    public int actionSonType = 9;//动作库分类类型 9爱秀 本地存储
    public int actionSortType = 1;//动作库排序类型 1最新 2最热 3动作
    public TabLayout tabLayout;
    public static String shareUrl = "";
    TabLayout.Tab tab3 = null;

    public ActionOnlineInfo actionOnlineInfo = new ActionOnlineInfo();

    public Fragment mCurrentFragment;
    private LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_ITEMS:
                    break;

                case SHOW_SHARE_PAGE:
                    UbtLog.d(TAG,"lihai----------------SHOW_SHARE_PAGE->");
                    new SharePopupWindow((BaseActivity) getActivity(),actionOnlineInfo,shareUrl,IShowSquareDetailFragment.this);

                    break;
                default:
                    break;
            }
        }
    };


    public IShowSquareDetailFragment() {

    }

    @SuppressLint("ValidFragment")
    public IShowSquareDetailFragment(BaseActivity mBaseActivity, MainHelper mainHelper,int actionSortType) {
        UbtLog.d(TAG, "new IShowSquareDetailFragment--");
        this.mActivity = (MyMainActivity) mBaseActivity;
        this.mMainHelper = mainHelper;
        this.actionSortType = actionSortType;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MyMainActivity) getActivity();
        mMainView = inflater.inflate(R.layout.fragment_actions_ishow_square_detail, null);

        initUI();
        initControlListener();
        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //切换语言的时候，Fragment要重新设一下，暂时这样处理，后面优化
        tabLayout.getTabAt(0).setText(mActivity.getStringResources("ui_square_new"));
        tabLayout.getTabAt(1).setText(mActivity.getStringResources("ui_square_hot"));
        tabLayout.getTabAt(2).setText(mActivity.getStringResources("ui_home_actions"));

        //读取机器人电量
        mMainHelper.RegisterHelper();
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub

        //给TabLayout增加Tab, 并关联ViewPager
        tabLayout = (TabLayout) mMainView.findViewById(R.id.tabs);

        tabLayout.setTabTextColors(mActivity.getColorId("tab_layout_default_text_color_ft"),mActivity.getColorId("tab_layout_select_text_color_ft"));
        
        //tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tab_layout_select_text_color_ft));

        TabLayout.Tab tab1 = tabLayout.newTab().setText(mActivity.getStringResources("ui_square_new"));
        TabLayout.Tab tab2 = tabLayout.newTab().setText(mActivity.getStringResources("ui_square_hot"));
        tab3 = tabLayout.newTab().setText(mActivity.getStringResources("ui_home_actions"));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        if(actionSortType == 9){
            tabLayout.addTab(tab1,false);
            tabLayout.addTab(tab2,false);
            tabLayout.addTab(tab3,true);
        }else {
            tabLayout.addTab(tab1,true);
            tabLayout.addTab(tab2,false);
            tabLayout.addTab(tab3,false);
        }

        mIShowFragment = IShowFragment.newInstance(actionSortType,actionSonType);
        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.rl_fragment_content, mIShowFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = mIShowFragment;
        mFragmentCache.put(actionSortType, mCurrentFragment);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                UbtLog.d(TAG,"onTabSelected::"+tab.getPosition());
                actionSortType = tab.getPosition()+1;
                if(actionSortType == 3){
                    //action
                    actionSortType = 9;
                }

                Fragment f = mFragmentCache.containsKey(actionSortType) ? mFragmentCache.get(actionSortType)
                        : IShowFragment.newInstance(actionSortType, actionSonType);
                if (!mFragmentCache.containsKey(actionSortType)) {
                    mFragmentCache.put(actionSortType, f);
                }
                ActionsOnlineHelper.actionLocalSortType = actionSortType;
                ActionsOnlineHelper.actionLocalSonType = actionSonType;
                loadFragment(f);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 强制切换到动作TAB
     */
    public void switchToActionTab(){
        //UbtLog.d(TAG,"switchToActionTab = " + actionSortType);
        if(actionSortType != 9){
            tab3.select();
        }
    }

    public void updateItemView(int position){
        if(mCurrentFragment != null){
            ((IShowFragment)mCurrentFragment).updateItemView(position);
        }
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
                            ((AlphaApplication) mActivity.getApplicationContext()).getCurrentUserInfo(),
                            mActivity), new IJsonListener() {
                @Override
                public void onGetJson(boolean isSuccess, String json, long request_code) {
                    //UbtLog.d(TAG,"isSuccess:"+isSuccess + "   json:"+json );
                    if(isSuccess)
                    {
                        if (JsonTools.getJsonStatus(json)) {
                            try {
                                shareUrl = new JSONObject(json).getString("models");

                                mHandler.sendEmptyMessage(SHOW_SHARE_PAGE);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            tabLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity,mActivity.getStringResources("ui_common_operate_fail"),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        tabLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mActivity,mActivity.getStringResources("ui_common_network_request_failed"),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }else {
            mHandler.sendEmptyMessage(SHOW_SHARE_PAGE);
        }

    }

    public void sendMessage(Object object, int what) {
        Message msg = new Message();
        msg.obj = object;
        msg.what = what;
        if (mHandler != null){
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void initControlListener() {
        // TODO Auto-generated method stub


    }

    public void showToast(String str) {
        Toast.makeText(mActivity.getApplicationContext(), mActivity.getStringResources(str), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub
    }

    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub
    }

    private void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        //UbtLog.d(TAG,"targetFragment.isAdded()->"+(!targetFragment.isAdded()));
        if (!targetFragment.isAdded()) {
            ((IShowFragment)mCurrentFragment).removeListeners();
            transaction
                    .hide(mCurrentFragment)
                    .add(R.id.rl_fragment_content, targetFragment)
                    .commit();
        } else {
            ((IShowFragment)mCurrentFragment).removeListeners();
            ((IShowFragment)targetFragment).registerListeners();
            transaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;

        //fragment 切换数据刷新,第一次创建不用请求，mHelper = null
        if(((IShowFragment) mCurrentFragment).mHelper != null){
            ((IShowFragment) mCurrentFragment).requestData();
        }
    }

    @Override
    public void onComplete(Object o) {
        //rl_share.setVisibility(View.GONE);
    }

    @Override
    public void onError(UiError uiError) {
        //rl_share.setVisibility(View.GONE);
        Toast.makeText(mActivity,mActivity.getStringResources("ui_action_share_fail"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        //rl_share.setVisibility(View.GONE);
    }

    @Override
    public void noteWeixinNotInstalled() {
        //rl_share.setVisibility(View.GONE);
        Toast.makeText(mActivity,mActivity.getStringResources("ui_action_share_no_wechat"),Toast.LENGTH_SHORT).show();
    }
}
