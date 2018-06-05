package com.ubt.alpha1e_edu.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.pg.PG;
import com.bumptech.glide.Glide;
import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.adapter.ActionsSimpleAdapter;
import com.ubt.alpha1e_edu.adapter.ActionsThemeSimpleAdapter;
import com.ubt.alpha1e_edu.adapter.OriginalActionsAdapter;
import com.ubt.alpha1e_edu.business.ActionPlayer;
import com.ubt.alpha1e_edu.data.JsonTools;
import com.ubt.alpha1e_edu.data.model.ActionColloInfo;
import com.ubt.alpha1e_edu.data.model.ActionInfo;
import com.ubt.alpha1e_edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e_edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e_edu.data.model.BannerInfo;
import com.ubt.alpha1e_edu.data.model.CommentInfo;
import com.ubt.alpha1e_edu.data.model.UserInfo;
import com.ubt.alpha1e_edu.library.UbtBanner.ConvenientBanner;
import com.ubt.alpha1e_edu.library.UbtBanner.holder.CBViewHolderCreator;
import com.ubt.alpha1e_edu.library.UbtBanner.holder.Holder;
import com.ubt.alpha1e_edu.library.ptr.PtrClassicFrameLayout;
import com.ubt.alpha1e_edu.library.ptr.PtrDefaultHandler;
import com.ubt.alpha1e_edu.library.ptr.PtrFrameLayout;
import com.ubt.alpha1e_edu.library.ptr.PtrHandler;
import com.ubt.alpha1e_edu.net.http.basic.FileDownloadListener.State;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e_edu.ui.ActionsSquareDetailActivity;
import com.ubt.alpha1e_edu.ui.BannerDetailActivity;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.ui.LoginActivity;
import com.ubt.alpha1e_edu.ui.MyMainActivity;
import com.ubt.alpha1e_edu.ui.OriginalListActivity;
import com.ubt.alpha1e_edu.ui.SchemePopupWindow;
import com.ubt.alpha1e_edu.ui.ThemeActivity;
import com.ubt.alpha1e_edu.ui.WebContentActivity;
import com.ubt.alpha1e_edu.ui.custom.AppGuideView;
import com.ubt.alpha1e_edu.ui.custom.MyLinearLayoutManager;
import com.ubt.alpha1e_edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e_edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e_edu.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e_edu.ui.helper.ActionsLibHelper.Action_download_state;
import com.ubt.alpha1e_edu.ui.helper.IActionsLibUI;
import com.ubt.alpha1e_edu.ui.helper.LoginHelper;
import com.ubt.alpha1e_edu.utils.ImageUtils;
import com.ubt.alpha1e_edu.utils.SizeUtils;
import com.ubt.alpha1e_edu.utils.connect.BannerInfoListCallback;
import com.ubt.alpha1e_edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * 重写原有的ActionsLibMainFragment类
 */
public class ActionsLibMainFragment3 extends BaseFragment implements
        IActionsLibUI,BaseDiaUI {
    private static final String TAG = "ActionsLibMainFragment3";
    private View mMainView;

    private List<Map<String, Object>> latestUpdateActionsDatas = new ArrayList<>();
    private List<Map<String, Object>> popularRecommendActionsDatas = new ArrayList<>();
    private List<Map<String, Object>> originalListActionsDatas = new ArrayList<>();
    private List<BannerInfo> themeRecommendDatas = new ArrayList<>();

    private static List<BannerInfo> mBannerInfosCache = new ArrayList<>();
    private static List<ActionInfo> mNewActionsLatestCache = new ArrayList<>();
    private static List<BannerInfo> mThemeRecommendDatasCache = new ArrayList<>();
    private static List<ActionInfo> mPopularRecommendActionsDatasCache = new ArrayList<>();
    private static List<ActionInfo> mOriginalListActionsDatasCache = new ArrayList<>();

    private ActionsLibHelper mHelper = null;
    private List<ActionRecordInfo> mDownLoadHistory = null;
    private LoadingDialog mLoadingDialog;

    public static final int UPDATE_BANNERS = 1;
    public static final int GET_SHOW_SCHEME_PICTURE = 2;
    public static final int SHOW_SCHEME_WINDOW = 3;
    public static final int REFRECH_DATA_CHANGE = 4;
    private static final int SHOW_UI = 100;

    public static final int UPDATE_LATEST_ITEMS = 5;
    public static final int UPDATE_POPULAR_ITEMS = 6;
    public static final int UPDATE_ORIGINAL_ITEMS = 7;
    public static final int UPDATE_ORIGINAL_ALL = 8;
    public static final int UPDATE_THEME_RECOMMEND_ALL = 9;
    public static final int UPDATE_POPULAR_ALL = 10;
    public static final int UPDATE_LATEST_ALL = 11;

    private ConvenientBanner mUbtBanner;
    private GridView mLatestUpdateGridView;
    private GridView mPopularRecommendationGridView;
    private GridView mThemeRecommendationGridView;

    private TextView tvThemeRecommendationmMore;
    private ImageView imgThemeRecommendationmMore;
    private TextView tvUpdateRecentlyMore;
    private ImageView imgUpdateRecentlyMore;
    private TextView tvPopularRecommendMore;
    private ImageView imgPopularRecommendMore;
    private TextView tvOriginalListMore;
    private ImageView imgOriginalListMore;

    private RelativeLayout rlThemeRecommendation = null;
    private RelativeLayout rlActionsSquareOfficial = null;
    private RelativeLayout rlActionsSquareOriginal = null;

    private ActionsSimpleAdapter mActionsLatestAdapter;
    private ActionsSimpleAdapter mActionsPopularAdapter;
    private ActionsThemeSimpleAdapter mActionsThemeAdapter;

    private RecyclerView mOriginalRecyclerview;
    private MyLinearLayoutManager mLayoutManager;
    private OriginalActionsAdapter mOriginalAdapter;
    public MyMainActivity activity;
    private List<String> mBannerUrl = new ArrayList<>();
    private List<BannerInfo> mBannerInfos = new ArrayList<>();

    private AppGuideView guideView;
    private boolean isResume;
    boolean isQuestRefresh = false;
    private PtrClassicFrameLayout ptrClassicFrameLayout;

    private Toast errorToast = null;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UI:
                    UbtLog.d(TAG,"--SHOW_UI--->>>" );
                    ptrClassicFrameLayout.setVisibility(View.VISIBLE);
                    break;
                case UPDATE_LATEST_ITEMS:
                case UPDATE_LATEST_ALL:
                    mActionsLatestAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_POPULAR_ITEMS:
                    mActionsPopularAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_ORIGINAL_ITEMS:
                    mOriginalAdapter.notifyItemChanged((int) msg.obj);
                    break;
                case UPDATE_ORIGINAL_ALL:
                    mOriginalAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_THEME_RECOMMEND_ALL:
                    mActionsThemeAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_POPULAR_ALL:
                    mActionsPopularAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_BANNERS:
                    List<String> bannelUrl = (ArrayList<String>) msg.obj;
                    updateBanners(bannelUrl);

                    UbtLog.d(TAG,"mHelper=" + mHelper);
                    UbtLog.d(TAG,"hasGetScheme:"+mHelper.hasGetScheme);

                    if(!mHelper.hasGetScheme && ((BaseActivity)getActivity()).isLaunchered()){
                        getScheme();
                    }
                    break;
                case GET_SHOW_SCHEME_PICTURE:

                    if(mHelper == null){
                        return;
                    }
                    Bundle data = msg.getData();
                    final String schemeId = data.getString("schemeId");
                    UbtLog.d(TAG,"schemeId:"+schemeId + "  pre:"+mHelper.getPreSchemeId());
                    if(!schemeId.equals(mHelper.getPreSchemeId()) || !((BaseActivity)getActivity()).readSchemeShowState().equals("1")){
                        final String schemeName = data.getString("schemeName");
                        final String schemeIcon = data.getString("schemeIcon");
                        final String schemeUrl = data.getString("schemeUrl");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap mBitmap = ImageUtils.loadBitmap(schemeIcon,getActivity());
                                //UbtLog.d(TAG,"lihai----------mBitmap::"+mBitmap + "     schemeIcon:"+schemeIcon + " isResume:" +isResume);

                                if(mBitmap != null && isResume){
                                    Message msg = new Message();
                                    msg.what = SHOW_SCHEME_WINDOW;
                                    Bundle data = new Bundle();
                                    data.putString("schemeName",schemeName);
                                    data.putString("schemeIcon",schemeIcon);
                                    data.putString("schemeId",schemeId);
                                    data.putString("schemeUrl",schemeUrl);
                                    msg.obj = mBitmap;
                                    msg.setData(data);
                                    mHandler.sendMessage(msg);
                                }
                            }
                        }).start();
                    }
                    break;
                case SHOW_SCHEME_WINDOW:

                    Bundle mData = msg.getData();
                    new SchemePopupWindow(getActivity(),
                            mData.getString("schemeName"),
                            mData.getString("schemeId"),
                            mData.getString("schemeUrl"),
                            (Bitmap) msg.obj);
                    mHelper.doUpdateSchemeId(mData.getString("schemeId"));
                    mHelper.hasGetScheme = true;
                    break;
                case REFRECH_DATA_CHANGE:
                    mOriginalAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };


    public ActionsLibMainFragment3() {

    }

    public static void clearCacheDatas(){
        mBannerInfosCache.clear();
        mNewActionsLatestCache.clear();
        mThemeRecommendDatasCache.clear();
        mPopularRecommendActionsDatasCache.clear();
        mOriginalListActionsDatasCache.clear();
    }

    @SuppressLint("ValidFragment")
    public ActionsLibMainFragment3(BaseActivity mBaseActivity) {
        UbtLog.d(TAG, "new ActionsLibMainFragment3--");
        mHelper = new ActionsLibHelper(this,mBaseActivity);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MyMainActivity) getActivity();
        mMainView = inflater.inflate(R.layout.fragment_actions_lib_main_3, null);

        initUI();
        initControlListener();
        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBannerData();

        isResume = true;

        if(getActivity() != null && !((BaseActivity)getActivity()).isLaunchered()){
            if(guideView == null){
                UbtLog.d(TAG, "--->create AppGuideView!");
//                guideView = new AppGuideView(getActivity(), listener);
            }
        }

        if(!((BaseActivity)getActivity()).readSchemeShowState().equals("1") && ((BaseActivity)getActivity()).isLaunchered()){
            getScheme();
        }
        registerHelper();

        UbtLog.d(TAG,"mUbtBanner = " + mUbtBanner + "   mHelper = " + mHelper);
        if(mHelper != null){
//            showDialog();
            mHelper.doReadDownLoadHistory();
        }
    }

    public void registerHelper(){
        if (mHelper != null){
            mHelper.RegisterHelper();
            mHelper.registerLisenters();
        }
    }

    public void unRegisterHelper(){
        if (mHelper != null) {
            mHelper.UnRegisterHelper();
            mHelper.removeListeners();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        UbtLog.d(TAG,"---onPause---");
        if(mUbtBanner!=null){
            mUbtBanner.stopTurning();
        }

        unRegisterHelper();
    }

    @Override
    public void onStop() {
        super.onStop();
        isResume = false;
        UbtLog.d(TAG, "--->onStop!");
        if(mHandler != null){
            mHandler.removeMessages(GET_SHOW_SCHEME_PICTURE);
            mHandler.removeMessages(SHOW_SCHEME_WINDOW);
            mHandler.removeMessages(UPDATE_BANNERS);
            mHandler.removeMessages(UPDATE_ORIGINAL_ITEMS);
        }
        if(guideView != null){
            guideView.closeAppGuideView();
            guideView = null;
        }
        unRegisterHelper();
    }

    @Override
    public void onDestroy() {
        if(mLoadingDialog!=null)
        {
            if(mLoadingDialog.isShowing()&&!getActivity().isFinishing()){
                mLoadingDialog.cancel();
            }
            mLoadingDialog = null;
        }

        if (mHelper != null){
            mHelper.DistoryHelper();
        }
        super.onDestroy();
    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub


        ptrClassicFrameLayout = (PtrClassicFrameLayout) mMainView.findViewById(R.id.pull_to_refresh);
        ptrClassicFrameLayout.setKeepHeaderWhenRefresh(true);
        ptrClassicFrameLayout.disableWhenHorizontalMove(true);
        ptrClassicFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();//5000ms超时
                    }
                },5000);

                requestData();

            }
        });
        ptrClassicFrameLayout.setVisibility(View.GONE);

        mLoadingDialog = LoadingDialog.getInstance(getActivity(),this);
        mLoadingDialog.setDoCancelable(true,6);

        initRecyclerViews();
        initMoreOperation();
        initBanners();
        initGrids();

        if(errorToast == null){
            errorToast = Toast.makeText(getActivity(),"",Toast.LENGTH_SHORT);
        }
    }

    /**
     * 初始化更多操作
     */
    private void initMoreOperation(){

        tvThemeRecommendationmMore = (TextView) mMainView.findViewById(R.id.txt_theme_recommendation_more);
        imgThemeRecommendationmMore = (ImageView) mMainView.findViewById(R.id.img_theme_recommendation_more);
        tvThemeRecommendationmMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoThemePage();
            }
        });
        imgThemeRecommendationmMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoThemePage();
            }
        });

        tvUpdateRecentlyMore = (TextView) mMainView.findViewById(R.id.txt_update_recently_more);
        imgUpdateRecentlyMore = (ImageView) mMainView.findViewById(R.id.img_update_recently_more);
        tvUpdateRecentlyMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLatestMore();
            }
        });
        imgUpdateRecentlyMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLatestMore();
            }
        });

        tvPopularRecommendMore = (TextView) mMainView.findViewById(R.id.txt_popular_recommend_more);
        imgPopularRecommendMore = (ImageView) mMainView.findViewById(R.id.img_popular_recommend_more);
        tvPopularRecommendMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPopularMore();
            }
        });
        imgPopularRecommendMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPopularMore();
            }
        });

        tvOriginalListMore = (TextView)mMainView.findViewById(R.id.txt_original_list_more);
        imgOriginalListMore = (ImageView)mMainView.findViewById(R.id.img_original_list_more);
        tvOriginalListMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoOriginalListMore();
            }
        });
        imgOriginalListMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoOriginalListMore();
            }
        });

        rlActionsSquareOfficial = (RelativeLayout)mMainView.findViewById(R.id.rl_actions_square_official);
        rlActionsSquareOriginal = (RelativeLayout)mMainView.findViewById(R.id.rl_actions_square_original);
        rlActionsSquareOfficial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionsSquareDetailActivity.launchActivity(getActivity(), 0,1);
            }
        });

        rlActionsSquareOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MyMainActivity)getActivity()).switchIshow(9);
            }
        });

        rlThemeRecommendation = (RelativeLayout)mMainView.findViewById(R.id.rl_theme_recommendation);
        LinearLayout.LayoutParams themeParams = (LinearLayout.LayoutParams) rlThemeRecommendation.getLayoutParams();
        if(AlphaApplication.isPad()){
            themeParams.height = SizeUtils.dip2px(activity,380);
            rlThemeRecommendation.setLayoutParams(themeParams);
        }
    }

    /**
     * 跳转到更多主题页面
     */
    private void gotoThemePage(){
        ThemeActivity.launchActivity(getActivity());
    }

    /**
     * 跳转到最新上线更多页面
     */
    private void gotoLatestMore(){
        ActionsSquareDetailActivity.launchActivity(getActivity(), 0, 1);
    }

    /**
     * 跳转到最热门更多页面
     */
    private void gotoPopularMore(){
        ActionsSquareDetailActivity.launchActivity(getActivity(), 0, 2);
    }

    /**
     * 跳转到原创榜单更多页面
     */
    private void gotoOriginalListMore(){
        OriginalListActivity.launchActivity(getActivity());
    }

    private void loadBannerData(){
        if(mBannerInfosCache.isEmpty()){
            getBanners();
        }else{
            if(mBannerUrl.isEmpty()){

                mBannerInfos.clear();
                mBannerInfos.addAll(mBannerInfosCache);

                List<String> bannerUrl = new ArrayList<>();
                for(BannerInfo info:mBannerInfos)
                {
                    bannerUrl.add(info.recommendImage);
                }

                Message bannerMsg = new Message();
                bannerMsg.what = UPDATE_BANNERS;
                bannerMsg.obj = bannerUrl;
                mHandler.sendMessage(bannerMsg);
            }else {
                mHandler.sendEmptyMessage(UPDATE_BANNERS);
            }
        }
    }

    public  void onRefreshComplete()
    {
        if(ptrClassicFrameLayout==null) {
            return;
        }
        ptrClassicFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                ptrClassicFrameLayout.refreshComplete();
            }
        });
    }

    public void showUI(){
        if(ptrClassicFrameLayout==null) {
            return;
        }
        mHandler.sendEmptyMessage(SHOW_UI);
    }

    /**
     * 获取活动主题
     */
    private void getScheme(){
        UserInfo userInfo  = ((AlphaApplication) activity
                .getApplicationContext()).getCurrentUserInfo();
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.getScheme);
        String params = HttpAddress.getParamsForPost(new String[]{(userInfo == null ? "" : userInfo.countryCode)}, HttpAddress.Request_type.getScheme, getActivity());

        OkHttpClientUtils.getJsonByPostRequest(url,params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        try {
                            UbtLog.d(TAG,"json = " + json);
                            if (JsonTools.getJsonStatus(json)) {
                                JSONObject jsonObject = new JSONObject(json);
                                JSONArray schemeInfoArray = jsonObject.getJSONObject("models").getJSONArray("schemeInfo");

                                if (schemeInfoArray.length() == 0) {
                                    mHelper.hasGetScheme = true;
                                } else {
                                    for (int i = 0; i < schemeInfoArray.length(); i++) {
                                        JSONObject schemeInfoObject = schemeInfoArray.getJSONObject(i);
                                        //UbtLog.d(TAG,"schemeInfoObject:"+schemeInfoObject);
                                        String schemeName = schemeInfoObject.getString("schemeName");
                                        String schemeIcon = schemeInfoObject.getString("schemeIcon");
                                        String schemeId = schemeInfoObject.getString("schemeId");
                                        String schemeUrl = schemeInfoObject.getString("schemeUrl");

                                        Message msg = new Message();
                                        msg.what = GET_SHOW_SCHEME_PICTURE;
                                        Bundle data = new Bundle();
                                        data.putString("schemeName", schemeName);
                                        data.putString("schemeIcon", schemeIcon);
                                        data.putString("schemeId", schemeId);
                                        data.putString("schemeUrl", schemeUrl);
                                        msg.setData(data);
                                        mHandler.sendMessage(msg);
                                        UbtLog.d(TAG, "schemeId::" + schemeId + "   schemeName:" + schemeName + " schemeIcon:" + schemeIcon + " schemeUrl:" + schemeUrl);
                                        break;
                                    }
                                }
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获取banners数据
     */
    private void getBanners() {

        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.get_bana_imgs);
        String params = HttpAddress.getParamsForPost(new String[]{"3"},
                HttpAddress.Request_type.get_bana_imgs, getActivity());
        OkHttpClientUtils
                .getJsonByPostRequest(url,params)
                .execute(new BannerInfoListCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mHandler.sendEmptyMessage(UPDATE_BANNERS);
                    }

                    @Override
                    public void onResponse(List<BannerInfo> response, int id) {
                        UbtLog.d(TAG,"response : " +response.size());
                        mBannerInfos = response;
                        List<String> bannerUrl = new ArrayList<>();
                        for(BannerInfo info:mBannerInfos)
                        {
                            String recommendUrl = info.recommendUrl;
                            String actionTitle = "";
                            if(!TextUtils.isEmpty(recommendUrl)){
                                try {
                                    JSONObject recommendUrlObj = new JSONObject(recommendUrl);
                                    JSONArray jsonArr = null;
                                    String systemLanguage = AlphaApplication.getBaseActivity().getStandardLocale(AlphaApplication.getBaseActivity().getAppCurrentLanguage());
                                    if (systemLanguage.equals("CN")) {
                                        jsonArr = recommendUrlObj.getJSONArray("CN");
                                    } else {
                                        jsonArr = recommendUrlObj.getJSONArray("EN");
                                    }

                                    actionTitle = jsonArr.getJSONObject(0).getString("actionTitle");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    actionTitle = "";
                                }
                            }
                            info.actionTitle = actionTitle;
                            bannerUrl.add(info.recommendImage);
                        }
                        if(mBannerInfos.size() != 0){
                            if(!mBannerInfosCache.isEmpty()){
                                mBannerInfosCache.clear();
                            }
                            mBannerInfosCache.addAll(mBannerInfos);
                        }

                        Message bannerMsg = new Message();
                        bannerMsg.what = UPDATE_BANNERS;
                        bannerMsg.obj = bannerUrl;
                        mHandler.sendMessage(bannerMsg);
                    }
                });
    }

    private CBViewHolderCreator holderCreator = new CBViewHolderCreator<LocalImageHolderView>() {
        @Override
        public LocalImageHolderView createHolder() {
            return new LocalImageHolderView();
        }
    };

    /**
     * 初始化banner
     */
    private void initBanners() {
        mUbtBanner = (ConvenientBanner) mMainView.findViewById(R.id.convenientBanner);

        mUbtBanner.setPages(holderCreator, mBannerUrl);

        mUbtBanner.setPageIndicator(new int[]{R.drawable.sel_point_close, R.drawable.sel_point}).
                setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        mUbtBanner.startTurning(3000);

        mUbtBanner.setOnItemClickListener(new com.ubt.alpha1e_edu.library.UbtBanner.listener.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BannerInfo info = mBannerInfos.get(position);
                UbtLog.d(TAG, "----mUbtBanner---onItemClick----" + position+", "+info.recommendForwardType);
                switch (info.recommendForwardType)
                {
                    //url
                    case 1:
                    case 4:
                        String url = "www.ubtrobot.com";
                        if(info.clickForward!=null&&info.clickForward.size()!=0)
                        {
                            url = info.clickForward.get(0).actionUrl;
                            //广告
                            if(info.recommendForwardType == 4){
                                if(url.contains("?")){
                                    url = url + "&fromWebview=true";
                                }else {
                                    url = url + "?fromWebview=true";
                                }
                            }
                        }
                        WebContentActivity.launchActivity(getActivity(),url,info.actionTitle,true);
                        break;
                    //单图
                    case 2:
                        //main_action
                    case 3:

                        BannerDetailActivity.launchActivity(activity, PG.convertParcelable(info));
                        break;
                    default:
                        break;

                }
            }
        });

        if(AlphaApplication.isPad()){
            LinearLayout.LayoutParams bannerParams = (LinearLayout.LayoutParams) mUbtBanner.getLayoutParams();
            bannerParams.height = SizeUtils.dip2px(activity,360);
            mUbtBanner.setLayoutParams(bannerParams);
        }
    }

    /**
     * 初始化banner
     */
    private void updateBanners(List<String> bannelUrl) {
        if(mUbtBanner != null){
            if(bannelUrl != null && !bannelUrl.isEmpty()){
                mBannerUrl.clear();
                mBannerUrl.addAll(bannelUrl);
            }
            mUbtBanner.notifyDataSetChanged();

            if(!mUbtBanner.isTurning()){
                mUbtBanner.startTurning(3000);
            }
        }
    }

    /**
     * 初始化原创榜单列表
     */
    private void initRecyclerViews() {
        mOriginalRecyclerview = (RecyclerView) mMainView.findViewById(R.id.recyclerview_original_list_actions);
        mLayoutManager = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mOriginalAdapter = new OriginalActionsAdapter(originalListActionsDatas, getActivity(), mHelper);
        mOriginalRecyclerview.setLayoutManager(mLayoutManager);
        mOriginalRecyclerview.setAdapter(mOriginalAdapter);
        mOriginalRecyclerview.setHasFixedSize(true);
        RecyclerView.ItemAnimator animator = mOriginalRecyclerview.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    private void initGrids() {
        mLatestUpdateGridView = (GridView) mMainView.findViewById(R.id.lay_latest_update);
        mPopularRecommendationGridView = (GridView) mMainView.findViewById(R.id.lay_popular_recommendation);
        mThemeRecommendationGridView = (GridView) mMainView.findViewById(R.id.lay_theme_recommendation);

        initThemeRecommodGrid();
        initLatestGrid();
        initPopularGrid();
    }

    /**
     * 初始化主题推荐页面，固定4个
     */
    private void initThemeRecommodGrid(){
        for(int i = 0; i < 4 ; i ++){
            themeRecommendDatas.add(new BannerInfo());
        }
        mActionsThemeAdapter = new ActionsThemeSimpleAdapter((BaseActivity) getActivity(),themeRecommendDatas,mHelper);
        mThemeRecommendationGridView.setAdapter(mActionsThemeAdapter);
    }

    /**
     * 初始化最新动作页面，固定6个
     */
    private void initLatestGrid(){
        for(int i = 0; i < 6 ; i ++){
            latestUpdateActionsDatas.add(new HashMap<String, Object>());
        }
        mActionsLatestAdapter = new ActionsSimpleAdapter((BaseActivity) getActivity(),latestUpdateActionsDatas,mHelper,ActionsSimpleAdapter.ACTION_TYPE_LATEST);
        mLatestUpdateGridView.setAdapter(mActionsLatestAdapter);
    }

    /**
     * //初始化热门推荐页面，固定6个
     */
    private void initPopularGrid(){
        for(int i = 0; i < 6 ; i ++){
            popularRecommendActionsDatas.add(new HashMap<String, Object>());
        }
        mActionsPopularAdapter = new ActionsSimpleAdapter((BaseActivity) getActivity(),popularRecommendActionsDatas,mHelper,ActionsSimpleAdapter.ACTION_TYPE_POPULAR);
        mPopularRecommendationGridView.setAdapter(mActionsPopularAdapter);
    }

    public void showDialog() {
        if(mLoadingDialog!=null&&!mLoadingDialog.isShowing())
        {
         //   mLoadingDialog.show();
        }
    }

    public void dismissDialog() {
        if(mLoadingDialog!=null&&mLoadingDialog.isShowing()&&!getActivity().isFinishing())
        {
            mLoadingDialog.cancel();
        }
    }

    public void requestData()
    {
        if(mBannerInfosCache.isEmpty()){
            getBanners();
        }

        isQuestRefresh = true;
        if(mHelper!=null){
            mHelper.doReadDownLoadHistory();
        }
    }

    /**
     * 更新最新上线数据
     * @param actions
     */
    public void setActionsNew(List<ActionInfo> actions) {

        latestUpdateActionsDatas = mHelper.loadDatas(actions, mDownLoadHistory);
        UbtLog.d(TAG,"---mActionsLatestAdapter: "+ mActionsLatestAdapter + "     mActionsLatestAdapter :" + latestUpdateActionsDatas.size());
        if (mActionsLatestAdapter != null) {

            if(latestUpdateActionsDatas.size() < 6){
                for(int i = 0; i < 6 - latestUpdateActionsDatas.size(); i++ ){
                    latestUpdateActionsDatas.add(new HashMap<String, Object>());
                }
            }

            if(latestUpdateActionsDatas.size() > 6 ){
                for(int i = latestUpdateActionsDatas.size() - 1; i >= 6; i--){
                    latestUpdateActionsDatas.remove(i);
                }
            }

            mActionsLatestAdapter.setDatas(latestUpdateActionsDatas);
            mHandler.sendEmptyMessage(UPDATE_LATEST_ALL);
        }
        if(getActivity()!=null){
            //((MyMainActivity)getActivity()).onRefreshComplete();
            onRefreshComplete();
        }
    }

    /**
     * 更新热门推荐数据
     * @param actions
     */
    public void setActionsPopular(List<ActionInfo> actions) {

        popularRecommendActionsDatas = mHelper.loadDatas(actions, mDownLoadHistory);
        if (mActionsPopularAdapter != null) {

            if(popularRecommendActionsDatas.size() < 6){
                for(int i = 0; i < 6 - popularRecommendActionsDatas.size(); i++ ){
                    popularRecommendActionsDatas.add(new HashMap<String, Object>());
                }
            }

            if(popularRecommendActionsDatas.size() > 6 ){
                for(int i = popularRecommendActionsDatas.size() - 1; i >= 6; i--){
                    popularRecommendActionsDatas.remove(i);
                }
            }

            mActionsPopularAdapter.setDatas(popularRecommendActionsDatas);
            mHandler.sendEmptyMessage(UPDATE_POPULAR_ALL);
        }
    }

    /**
     * 更新原创榜单数据
     * @param actions
     */
    public void setActionsOriginalList(List<ActionInfo> actions) {

        originalListActionsDatas = mHelper.loadDatas(actions, mDownLoadHistory);
        if (mOriginalAdapter != null) {

            if(originalListActionsDatas.size() > 5 ){
                for(int i = originalListActionsDatas.size() - 1; i >= 5; i--){
                    originalListActionsDatas.remove(i);
                }
            }

            mOriginalAdapter.setDatas(originalListActionsDatas);
            mHandler.sendEmptyMessage(UPDATE_ORIGINAL_ALL);
        }
    }

    /**
     * 设置推荐主题数据
     * @param themes
     */
    public void setActionsThemeRecommend(List<BannerInfo> themes) {

        themeRecommendDatas.clear();
        for (int i=0; i<themes.size() && i<4; i++){//最多显示4个
            BannerInfo theme = themes.get(i);
            if(!TextUtils.isEmpty(theme.recommendUrl)){
                try {
                    JSONObject recommendUrlObj = new JSONObject(theme.recommendUrl);
                    JSONArray jsonArr = null;
                    String systemLanguage = AlphaApplication.getBaseActivity().getStandardLocale(AlphaApplication.getBaseActivity().getAppCurrentLanguage());
                    if (systemLanguage.equals("CN")) {
                        jsonArr = recommendUrlObj.getJSONArray("CN");
                    } else {
                        jsonArr = recommendUrlObj.getJSONArray("EN");
                    }
                    theme.actionTitle = jsonArr.getJSONObject(0).getString("actionTitle");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //UbtLog.d(TAG,"themeRecommendDatas => " + themeRecommendDatas.size() + "  themes = " + themes.size());

            themeRecommendDatas.add(theme);
        }

        if(themeRecommendDatas.size() < 4){
            for(int i = 0; i < 4 - themeRecommendDatas.size(); i++ ){
                themeRecommendDatas.add(new BannerInfo());
            }
        }

        mActionsThemeAdapter.setDatas(themeRecommendDatas);
        mHandler.sendEmptyMessage(UPDATE_THEME_RECOMMEND_ALL);


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

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }


    public void noteOnActionDownLoadFinish(ActionInfo action, State state) {
        // TODO Auto-generated method stub
        //UbtLog.d(TAG,"action = " + action.actionName);
        //update original list
        for (int i = 0; i < originalListActionsDatas.size(); i++) {

            ActionInfo actionInfo = ((ActionInfo) originalListActionsDatas.get(i).get(ActionsLibHelper.map_val_action));
            //加null 占位符与脏数据 continue
            if(actionInfo == null){
                continue;
            }
            if (actionInfo.actionId == action.actionId) {
                if (state == State.success) {
                    originalListActionsDatas.get(i).put(ActionsLibHelper.map_val_action_download_state,
                            Action_download_state.download_finish);
                    actionInfo.actionDownloadTime++;
                } else {
                    originalListActionsDatas.get(i).put(
                            ActionsLibHelper.map_val_action_download_state,
                            Action_download_state.not_download);
                }
                mOriginalAdapter.setDatas(originalListActionsDatas);
                sendMessage(i, UPDATE_ORIGINAL_ITEMS);
            }
        }

        //update latest action
        for (int i = 0; i < latestUpdateActionsDatas.size(); i++) {
            ActionInfo actionInfo = ((ActionInfo) latestUpdateActionsDatas.get(i).get(ActionsLibHelper.map_val_action));
            //加null 占位符与脏数据continue
            if(actionInfo == null){
                continue;
            }
            if (actionInfo.actionId == action.actionId) {
                if (state == State.success) {
                    latestUpdateActionsDatas.get(i).put(ActionsLibHelper.map_val_action_download_state,Action_download_state.download_finish);
                    actionInfo.actionDownloadTime++;
                } else {
                    latestUpdateActionsDatas.get(i).put(
                            ActionsLibHelper.map_val_action_download_state,
                            Action_download_state.not_download);
                }
                mActionsLatestAdapter.setDatas(latestUpdateActionsDatas);
                sendMessage(i, UPDATE_LATEST_ITEMS);
            }
        }

        //update popular action
        for (int i = 0; i < popularRecommendActionsDatas.size(); i++) {
            ActionInfo actionInfo = ((ActionInfo) popularRecommendActionsDatas.get(i).get(ActionsLibHelper.map_val_action));
            //加null 占位符与脏数据 continue
            if(actionInfo == null){
                continue;
            }
            if (actionInfo.actionId == action.actionId) {
                if (state == State.success) {
                    popularRecommendActionsDatas.get(i).put(ActionsLibHelper.map_val_action_download_state,
                            Action_download_state.download_finish);
                    actionInfo.actionDownloadTime++;
                } else {
                    popularRecommendActionsDatas.get(i).put(
                            ActionsLibHelper.map_val_action_download_state,
                            Action_download_state.not_download);
                }
                mActionsPopularAdapter.setDatas(popularRecommendActionsDatas);
                sendMessage(i, UPDATE_POPULAR_ITEMS);
            }
        }
    }

    public void noteOnActionDownLoadProgress(ActionInfo action, double progess) {

        //update original list
        for (int i = 0; i < originalListActionsDatas.size(); i++) {
            ActionInfo actionInfo = ((ActionInfo) originalListActionsDatas.get(i).get(ActionsLibHelper.map_val_action));
            //加null 占位符与脏数据 continue
            if(actionInfo == null){
                continue;
            }

            if (actionInfo.actionId == action.actionId) {
                originalListActionsDatas.get(i).put(ActionsLibHelper.map_val_action_download_progress,progess);
                mOriginalAdapter.setDatas(originalListActionsDatas);
                sendMessage(i, UPDATE_ORIGINAL_ITEMS);
                break;
            }
        }

        //update latest action
        for (int i = 0; i < latestUpdateActionsDatas.size(); i++) {
            ActionInfo actionInfo = ((ActionInfo) latestUpdateActionsDatas.get(i).get(ActionsLibHelper.map_val_action));
            //加null 占位符与脏数据 continue
            if(actionInfo == null){
                continue;
            }
            if (actionInfo.actionId == action.actionId) {
                latestUpdateActionsDatas.get(i).put(ActionsLibHelper.map_val_action_download_progress,progess);
                mActionsLatestAdapter.setDatas(latestUpdateActionsDatas);
                sendMessage(i, UPDATE_LATEST_ITEMS);
                break;
            }
        }

        //update popular action
        for (int i = 0; i < popularRecommendActionsDatas.size(); i++) {
            ActionInfo actionInfo = ((ActionInfo) popularRecommendActionsDatas.get(i).get(ActionsLibHelper.map_val_action));
            //加null 占位符与脏数据 continue
            if(actionInfo == null){
                continue;
            }

            if (actionInfo.actionId == action.actionId) {
                popularRecommendActionsDatas.get(i).put(ActionsLibHelper.map_val_action_download_progress,progess);
                mActionsPopularAdapter.setDatas(popularRecommendActionsDatas);
                sendMessage(i, UPDATE_POPULAR_ITEMS);
                break;
            }
        }
    }

    public void setActionsDownLoadHistory(List<ActionRecordInfo> history) {
        mDownLoadHistory = history;
    }

    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onReadActionsFinish(boolean isSuccess, String errorInfo, List<ActionInfo> actions) {
        //UbtLog.d(TAG,"lihai------actions = "+actions.size());
        isQuestRefresh = false;
        dismissDialog();

        if (!isSuccess) {
            if(errorToast != null){
                errorToast.setText(((MyMainActivity)getActivity()).getStringResources(errorInfo));
                errorToast.setDuration(Toast.LENGTH_SHORT);
                errorToast.show();
            }
        }else if(actions.size() > 0){
            //add to cache
            mNewActionsLatestCache.clear();
            mNewActionsLatestCache.addAll(actions);
            setActionsNew(actions);
        }

        if(getActivity() != null){
            showUI();
        }
    }

    @Override
    public void onReadActionCommentsFinish(List<CommentInfo> comments) {

    }

    @Override
    public void onActionCommentFinish(boolean is_success) {

    }

    @Override
    public void onActionPraisetFinish(boolean is_success) {

    }

    @Override
    public void onNoteNoUser() {
        Intent inte = new Intent();
        inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, true);
        inte.setClass(getActivity(), LoginActivity.class);
        getActivity().startActivity(inte);
    }

    @Override
    public void onGetShareUrl(String string) {

    }

    @Override
    public void onWeiXinShareFinish(Integer obj) {

    }

    @Override
    public void onNoteTooMore() {

    }

    @Override
    public void onReadImgFromCache(Bitmap img, long l) {

    }

    @Override
    public void onReadActionInfo(ActionInfo info) {

    }

    @Override
    public void onReadCacheActionsFinish(boolean is_success, List<ActionOnlineInfo> actions) {

    }

    @Override
    public void onReadPopularActionsFinish(boolean isSuccess, String errorInfo, List<ActionInfo> actions) {
        //UbtLog.d(TAG,"onReadPopularActionsFinish =  "+actions.size());
        dismissDialog();
        if(isSuccess && actions != null && actions.size() > 0){
            mPopularRecommendActionsDatasCache.clear();
            mPopularRecommendActionsDatasCache.addAll(actions);
        }

        setActionsPopular(actions);
    }

    @Override
    public void onReadThemeRecommondFinish(boolean isSuccess, String errorInfo, List<BannerInfo> themes) {
        dismissDialog();

        if(isSuccess){
            if(isSuccess && themes!= null && themes.size() > 0){
                mThemeRecommendDatasCache.clear();
                mThemeRecommendDatasCache.addAll(themes);
            }
            setActionsThemeRecommend(themes);
        }
    }

    @Override
    public void onReadOriginalListActionsFinish(boolean isSuccess, String errorInfo, List<ActionInfo> actions) {
        //UbtLog.d(TAG,"onReadOriginalListActionsFinish::"+actions.size());
        dismissDialog();

        if(isSuccess && actions != null && actions.size() > 0){
            mOriginalListActionsDatasCache.clear();
            mOriginalListActionsDatasCache.addAll(actions);
        }
        setActionsOriginalList(actions);

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {

    }

    @Override
    public void notePlayChargingError() {

    }

    @Override
    public void notePlayCycleNext(String action_name) {

    }

    @Override
    public void onReadCollocationRecordFinish(boolean isSuccess, String errorInfo, List<ActionColloInfo> history) {

    }

    @Override
    public void onDelRecordFinish() {

    }

    @Override
    public void onRecordFinish(long action_id) {

    }

    @Override
    public void onCollocateFinish(long action_id, boolean isSuccess, String error) {

    }

    @Override
    public void onCollocateRmoveFinish(boolean b) {

    }

    @Override
    public void onGetFileLenth(ActionInfo action, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(ActionInfo action, State state) {

    }

    @Override
    public void onReportProgress(ActionInfo action, double progess) {
        // MyLog.writeLog("收到下载动作进度汇报-->" + action.actionId + "," + progess);
        noteOnActionDownLoadProgress(action,progess);
    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, State state) {
        noteOnActionDownLoadFinish(action, state);
    }

    @Override
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {

        // 历史记录读取完毕
        setActionsDownLoadHistory(history);

        if(isQuestRefresh){

            mHelper.doGetActionsOnLineNewSimple();
            mHelper.doGetActionsOnLinePopularSimple(1,6);
            mHelper.doGetThemeRecommendSimple();
            mHelper.doGetActionsOnLineOriginalListSimple(5);
        }else{

            UbtLog.d(TAG," mThemeRecommendDatasCache ：" + mThemeRecommendDatasCache.size());
            UbtLog.d(TAG," mNewActionsLatestCache ：" + mNewActionsLatestCache.size());
            UbtLog.d(TAG," mPopularRecommendActionsDatasCache ：" + mPopularRecommendActionsDatasCache.size());
            UbtLog.d(TAG," mOriginalListActionsDatasCache ：" + mOriginalListActionsDatasCache.size());

            boolean hasRequestData = false;
            //主题有就取缓存，没有就请求网络
            if(mThemeRecommendDatasCache.size() >= 4){
                setActionsThemeRecommend(mThemeRecommendDatasCache);
            }else {
                hasRequestData = true;
                mHelper.doGetThemeRecommendSimple();
            }

            //最新有就取缓存，没有就请求网络
            if(mNewActionsLatestCache.size() >= 6){
                setActionsNew(mNewActionsLatestCache);
            }else {
                hasRequestData = true;
                mHelper.doGetActionsOnLineNewSimple();
            }

            //热门有就取缓存，没有就请求网络
            if(mPopularRecommendActionsDatasCache.size() >= 6){
                setActionsPopular(mPopularRecommendActionsDatasCache);
            }else {
                hasRequestData = true;
                mHelper.doGetActionsOnLinePopularSimple(1,6);
            }

            //原创榜单有就取缓存，没有就请求网络
            if(mOriginalListActionsDatasCache.size() >= 5){
                setActionsOriginalList(mOriginalListActionsDatasCache);
            }else {
                hasRequestData = true;
                mHelper.doGetActionsOnLineOriginalListSimple(5);
            }

            UbtLog.d(TAG,"hasRequestData ：" + hasRequestData);
            //全部取缓存，无需请求网络的
            if(!hasRequestData){
                try {
                    dismissDialog();
                    if(getActivity() != null){
                        showUI();
                    }
                } catch (Exception e) {
                    UbtLog.e(TAG,e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo) {

    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }

    public static class LocalImageHolderView implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, String data) {
            //UbtLog.d(TAG,"data : " + data);
            Glide.with(context).load(data).fitCenter()
                    .placeholder(R.drawable.action_online_nointroduction_icon).into(imageView);
        }
    }

    private AppGuideView.AppGuideCloseListener listener = new AppGuideView.AppGuideCloseListener() {
        @Override
        public void appGuideClose(boolean finish) {
            ((BaseActivity)getActivity()).doWriteLauncherState();
            if(finish && !mHelper.hasGetScheme){
                getScheme();
            }
        }
    };

}
