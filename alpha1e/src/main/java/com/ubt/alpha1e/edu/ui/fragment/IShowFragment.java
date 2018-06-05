package com.ubt.alpha1e.edu.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.adapter.IshowSquareAdapter;
import com.ubt.alpha1e.edu.business.ActionPlayer;
import com.ubt.alpha1e.edu.data.model.ActionColloInfo;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e.edu.data.model.BannerInfo;
import com.ubt.alpha1e.edu.data.model.CommentInfo;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.library.AutoVideo.visibility.calculator.SingleListViewItemActiveCalculator;
import com.ubt.alpha1e.edu.library.AutoVideo.visibility.items.VideoItem;
import com.ubt.alpha1e.edu.library.AutoVideo.visibility.scroll.ItemsPositionGetter;
import com.ubt.alpha1e.edu.library.AutoVideo.visibility.scroll.RecyclerViewItemPositionGetter;
import com.ubt.alpha1e.edu.library.ptr.PtrClassicFrameLayout;
import com.ubt.alpha1e.edu.library.ptr.PtrDefaultHandler;
import com.ubt.alpha1e.edu.library.ptr.PtrFrameLayout;
import com.ubt.alpha1e.edu.library.ptr.PtrHandler;
import com.ubt.alpha1e.edu.library.ptr.PtrUIHandler;
import com.ubt.alpha1e.edu.library.ptr.indicator.PtrIndicator;
import com.ubt.alpha1e.edu.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.DynamicActivity;
import com.ubt.alpha1e.edu.ui.LoginActivity;
import com.ubt.alpha1e.edu.ui.MyDynamicActivity;
import com.ubt.alpha1e.edu.ui.MyMainActivity;
import com.ubt.alpha1e.edu.ui.custom.DynamicGuideView;
import com.ubt.alpha1e.edu.ui.custom.MyLinearLayoutManager;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDataDialog;
import com.ubt.alpha1e.edu.ui.helper.ActionsOnlineHelper;
import com.ubt.alpha1e.edu.ui.helper.IActionsLibUI;
import com.ubt.alpha1e.edu.utils.connect.ActionOnlineInfoCallback;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 */
public class IShowFragment extends BaseFragment implements IActionsLibUI,BaseDiaUI {

    private static final String TAG = "IShowFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int UPDATE_ITEMS = 0;
    public static final int READ_CACHE_DATA_FINISH = 1;
    public static final int DISPLAY_DIALOG = 2;
    private int actionSortType;
    private int actionSonType;
    private RecyclerView mRecyclerView;
    private MyLinearLayoutManager mLayoutManager;
    public IshowSquareAdapter mIshowSquareAdapter;
    public BaseActivity mActivity;
    public Context mContext;
    public List<Map<String,Object>> mDatas = new ArrayList<>();
    public List<VideoItem> videoListItems = new ArrayList<>();
    public ActionsOnlineHelper mHelper;
    private int mScrollState;
    private SingleListViewItemActiveCalculator mCalculator;
    private ItemsPositionGetter mItemsPositionGetter;
    private PtrClassicFrameLayout ptrClassicFrameLayout;
    public boolean isRefreshComplete = false;
    private List<ActionRecordInfo> mHistoryRecord = new ArrayList<>();
    private int mLastVisibleItem = 0;//当前显示的最后一个可见item
    public int mCurrentPage = 1;
    private boolean isNoMoreData = false;
    private boolean isNeedLoadingData = true;
    private static boolean isNeedLoadCacheData = true;

    private boolean isRequestDataFinish = true;

    private LoadingDataDialog mLoadingDialog;

    private View view;
    private LinearLayout emptyView;

    private DynamicGuideView guideView;

    public Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case UPDATE_ITEMS:
                    mIshowSquareAdapter.notifyItemChanged((int)msg.obj);
                    break;
                case READ_CACHE_DATA_FINISH:
                    List<ActionOnlineInfo> cacheData = (List<ActionOnlineInfo>)msg.obj;
                    isNoMoreData = cacheData.size() == 0?true:false;
                    mDatas = mHelper.loadDatas(cacheData,mHistoryRecord,false,actionSonType,actionSortType);
                    videoListItems = mHelper.getVideoListItems(mDatas);
                    mIshowSquareAdapter.setDatas(mDatas,videoListItems);
                    mIshowSquareAdapter.notifyDataSetChanged();
                    setRefreshComplete();
                    break;
                case DISPLAY_DIALOG:
                    dismissProgress();
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    public IShowFragment() {
        // Required empty public constructor
    }

    public static IShowFragment newInstance(int param1, int param2) {
        IShowFragment fragment = new IShowFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNeedLoadCacheData = true;
        if (getArguments() != null) {
            actionSortType = getArguments().getInt(ARG_PARAM1);
            actionSonType = getArguments().getInt(ARG_PARAM2);
            ActionsOnlineHelper.actionLocalSonType = actionSonType;
            ActionsOnlineHelper.actionLocalSortType = actionSortType;
        }

    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        UbtLog.d(TAG,"onCreateView->");
        mActivity = (BaseActivity) getActivity();
        mContext  = getActivity();
        mHelper = new ActionsOnlineHelper(this,mActivity);
        view =  inflater.inflate(R.layout.fragment_actions_ishow_detail, container, false);
        initRecyclerView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListeners();
        /*if(mHelper!=null)
            mHelper.registerLisenters();*/
        if(mActivity instanceof MyMainActivity){
            showGuideView();
        }
    }

    private void showGuideView() {
        if(guideView == null && !guideView.isShowDynamicGuide(mActivity)){
            guideView = new DynamicGuideView(mActivity);
        }
    }


    private void showEmptyView(){

        if(mDatas.size() ==0 && mActivity instanceof  MyDynamicActivity){
                emptyView.setVisibility(View.VISIBLE);
                ((TextView)emptyView.findViewById(R.id.txt_unlogin)).setText(mActivity.getStringResources("ui_mine_like_empty"));
                Button btn_login = (Button)emptyView.findViewById(R.id.btn_login);
                Button btn_check = (Button)emptyView.findViewById(R.id.btn_check_list);
                btn_login.setText(mActivity.getStringResources("ui_mine_like_release"));
                btn_check.setVisibility(View.INVISIBLE);
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mActivity, DynamicActivity.class);
                        intent.putExtra(DynamicActivity.EMPTY_DATA, true);
                        mActivity.startActivityForResult(intent, MyDynamicActivity.SEND_DYNAMIC_CODE);
                    }
                });


        }else{
                emptyView.setVisibility(View.GONE);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        UbtLog.d(TAG,"lihai----------onPause--------");
        removeListeners();
    }

    public void removeListeners(){
        UbtLog.d(TAG,"lihai----------removeListeners--------"+mHelper);
        if(mHelper!=null)
        {
            mHelper.removeListeners();
            mHelper.UnRegisterHelper();
        }
    }

    public void registerListeners(){
        if(mHelper!=null){
            UbtLog.d(TAG, "registerListeners 111");
            mHelper.registerLisenters();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        UbtLog.d(TAG,"----------onStop--------");
        if(guideView != null){
            guideView.closeDubGuideView();
            guideView = null;
        }
    }

    @Override
    public void onDestroy() {

        if(mHelper!=null){
            mHelper.DistoryHelper();
        }
        dismissProgress();
        super.onDestroy();
    }

    public void setAutoRefresh()
    {
        if(ptrClassicFrameLayout==null) {
            return;
        }
        ptrClassicFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                ptrClassicFrameLayout.autoRefresh(true);
            }
        });
        isRefreshComplete = false;
    }

    private void setRefreshComplete()
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
        isRefreshComplete = true;
        showEmptyView();
    }

    private void initRecyclerView(View view)
    {
        emptyView = (LinearLayout) view.findViewById(R.id.dynamic_empty);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_square);
        mLayoutManager = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mIshowSquareAdapter = new IshowSquareAdapter(getActivity(),mContext,mDatas,mRecyclerView,mHelper);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mIshowSquareAdapter);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        mRecyclerView.getItemAnimator().setSupportsChangeAnimations(false);
        mCalculator = new SingleListViewItemActiveCalculator(mIshowSquareAdapter,
                new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView));
        ptrClassicFrameLayout = (PtrClassicFrameLayout)view.findViewById(R.id.refresh_fragment_square);
        ptrClassicFrameLayout.setKeepHeaderWhenRefresh(true);
        ptrClassicFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                if(mActivity instanceof  MyMainActivity){
                    ((MyMainActivity)mActivity).setDisableTabClickListener(true);
                }

                //UbtLog.d(TAG,"lihai-----onRefreshBegin-------"+isNeedLoadCacheData);
                requestData();
            }

        });
        ptrClassicFrameLayout.addPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIReset(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {
                if(mActivity instanceof  MyMainActivity){
                    ((MyMainActivity)mActivity).setDisableTabClickListener(false);
                }
            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

            }
        });
        setAutoRefresh();
        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mScrollState = newState;
                if(newState == RecyclerView.SCROLL_STATE_IDLE && mIshowSquareAdapter.getItemCount() > 0){
                    mCalculator.onScrollStateIdle();
                }
                if(newState == RecyclerView.SCROLL_STATE_IDLE&&mIshowSquareAdapter.getItemCount()-mLastVisibleItem<=1&&isNeedLoadingData)
                {

                    if(!isRequestDataFinish){
                        return;
                    }

                    if(!isNoMoreData){
                        ++mCurrentPage;
                        isRequestDataFinish = false;
                        showProgress();
                        requestOnlineData(mHistoryRecord,mCurrentPage);//滑到倒数第三个item时就默认加载下一页
                    }
                }

                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE: // The RecyclerView is not currently scrolling.
                        //对于滚动不加载图片的尝试
                        mIshowSquareAdapter.setScrolling(false);
                        mIshowSquareAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING: // The RecyclerView is currently being dragged by outside input such as user touch input.
                        mIshowSquareAdapter.setScrolling(true);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING: // The RecyclerView is currently animating to a final position while not under
                        mIshowSquareAdapter.setScrolling(true);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy<=0) {
                    isNeedLoadingData = false;
                }else {
                    isNeedLoadingData = true;
                }
                if(mCalculator!=null){
                    mCalculator.onScrolled(mScrollState);
                }
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
        requestData();
    }

    /**
     * 请求数据
     * */
    public  void requestData()
    {
        if(mHelper!=null){
            mHelper.doReadDownLoadHistory();
        }
    }

    public void requestOnlineData(final List<ActionRecordInfo> history,final int page)
    {

        UserInfo userInfo  = ((AlphaApplication) mActivity
                .getApplicationContext()).getCurrentUserInfo();
        String url = "";
        String params = "";
        if(mActivity instanceof MyDynamicActivity){
            url = HttpAddress.getRequestUrl(HttpAddress.Request_type.getMyShow);
            params = HttpAddress.
                    getParamsForPost(new String[]{page+"", 10+"",1+"",0+"",userInfo==null?"":userInfo.countryCode,
                                    userInfo==null?"":userInfo.userId+"",actionSonType==0?1+"":0+""},
                            HttpAddress.Request_type.getMyShow,mActivity);
        }else if(mActivity instanceof  MyMainActivity){
            url = HttpAddress.getRequestUrl(HttpAddress.Request_type.getIshowList);
            params = HttpAddress.
                    getParamsForPost(new String[]{page+"", 10+"",actionSortType+"",actionSonType+"",userInfo==null?"":userInfo.countryCode,
                                    userInfo==null?"":userInfo.userId+"",actionSonType==0?1+"":0+""},
                            HttpAddress.Request_type.getIshowList,mActivity);
        }

        OkHttpClientUtils
                .getJsonByPostRequest(url,params)
                .execute(new ActionOnlineInfoCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            UbtLog.d(TAG,"onResponse:>>> "+e.getMessage());
                            setRefreshComplete();
                            isRequestDataFinish = true;
                            mHandler.sendEmptyMessageDelayed(DISPLAY_DIALOG,100);
                        }
                        @Override
                        public void onResponse(List<ActionOnlineInfo> actionOnlineInfo, int id) {
                            UbtLog.d(TAG,"--isRequestDataFinish-->");
                            isNoMoreData = actionOnlineInfo.size() == 0?true:false;
                            if(mCurrentPage==1)
                            {
                                mDatas = mHelper.loadDatas(actionOnlineInfo,history,true,actionSonType,actionSortType);
                            }else{
                                mDatas.addAll(mHelper.loadDatas(actionOnlineInfo,history,true,actionSonType,actionSortType));
                            }
                            videoListItems = mHelper.getVideoListItems(mDatas);
                            mIshowSquareAdapter.setDatas(mDatas,videoListItems);
                            mIshowSquareAdapter.notifyDataSetChanged();
                            setRefreshComplete();
                            isRequestDataFinish = true;
                            mHandler.sendEmptyMessageDelayed(DISPLAY_DIALOG,100);
                        }
                    });

    }

    public void requestLocalCacheData(){
        mHelper.getAllOnlineCache(actionSonType,actionSortType);
    }

    public void refreshData(int type)
    {
        this.actionSortType = type;
        setAutoRefresh();
    }

    public void sendMessage(Object object,int what)
    {
        Message msg = new Message();
        msg.obj = object;
        msg.what = what;
        if(mHandler!=null){
            mHandler.sendMessage(msg);
        }
    }

    public void updateItemView(int position)
    {
        UbtLog.d(TAG,"lihai-------updateItemView--" + position);
        if(mIshowSquareAdapter!=null/*&&mScrollState==RecyclerView.SCROLL_STATE_IDLE*/&&!mRecyclerView.isComputingLayout()){
            mIshowSquareAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onReadActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {

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
        LoginActivity.launchActivity(getActivity(),true,12306);
        /*new AlertDialog(mContext).builder().setMsg(getStringRes("ui_leftmenu_click_to_login")).setCancelable(true).
                setPositiveButton(getStringRes("ui_guide_login"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoginActivity.launchActivity(getActivity(),true,12306);
                    }
                }).setNegativeButton(getStringRes("ui_common_cancel"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();*/
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
    public void onStopDownloadFile(ActionInfo action, FileDownloadListener.State state) {

    }

    @Override
    public void onReportProgress(ActionInfo action, double progess) {
        UbtLog.d(TAG,"onReportProgress:"+action.actionId+",progress:"+progess);
        for (int i = 0; i < mDatas.size(); i++) {

            if (((ActionOnlineInfo) mDatas.get(i).get(
                    ActionsOnlineHelper.map_val_action)).actionId == action.actionId) {
                mDatas.get(i).put(
                        ActionsOnlineHelper.map_val_action_download_progress,
                        progess);
                mDatas.get(i).put(
                        ActionsOnlineHelper.map_val_action_download_state,
                        ActionsOnlineHelper.Action_download_state.downing);
                mIshowSquareAdapter.setDatas(mDatas,videoListItems);
                sendMessage(i,UPDATE_ITEMS);
            }
        }
    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, FileDownloadListener.State state) {
        UbtLog.d(TAG,"onDownLoadFileFinish:"+action.actionId);
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i) == null){
                continue;
            }
            if (((ActionOnlineInfo) mDatas.get(i).get(
                    ActionsOnlineHelper.map_val_action)).actionId == action.actionId) {
                if (state == FileDownloadListener.State.success) {
                    mDatas.get(i).put(
                            ActionsOnlineHelper.map_val_action_download_state,
                            ActionsOnlineHelper.Action_download_state.download_finish);

                    ActionOnlineInfo actionOnlineInfo = (ActionOnlineInfo) mDatas.get(i).get(ActionsOnlineHelper.map_val_action);
                    actionOnlineInfo.actionDownloadTime++;
                    mHelper.updateActionOnlineCache(actionOnlineInfo);

                } else {
                    mDatas.get(i).put(
                            ActionsOnlineHelper.map_val_action_download_state,
                            ActionsOnlineHelper.Action_download_state.not_download);
                }
                mIshowSquareAdapter.setDatas(mDatas,videoListItems);
                sendMessage(i,UPDATE_ITEMS);
            }
        }
    }

    @Override
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {
        mHistoryRecord = history;
        mCurrentPage = 1;
        UbtLog.d(TAG,"isNeedLoadCacheData="+isNeedLoadCacheData+"  isHidden="+this.isHidden());
        if(isNeedLoadCacheData){
            requestLocalCacheData();
        }else{
            requestOnlineData(mHistoryRecord,mCurrentPage);//上拉刷新和第一次请求时，只请求一页
        }

    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo) {

    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void onDestroyView() {
        if(mHandler != null && mHandler.hasMessages(DISPLAY_DIALOG)){
            mHandler.removeMessages(DISPLAY_DIALOG);
        }
        super.onDestroyView();
    }

    @Override
    public void onReadCacheActionsFinish(boolean is_success, List<ActionOnlineInfo> actions) {
        isNeedLoadCacheData = false;
        if(is_success && actions.size() != 0){
            mCurrentPage = (actions.size()/10 + (actions.size()%10 > 0 ? 1 : 0));
            UbtLog.d(TAG,"lihai--------onReadCacheActionsFinish->"+actions.size()+"--mCurrentPage-"+mCurrentPage);
            sendMessage(actions,READ_CACHE_DATA_FINISH);
        }else{
            requestOnlineData(mHistoryRecord,mCurrentPage);//上拉刷新和第一次请求时，只请求一页
        }

    }

    @Override
    public void onReadPopularActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {

    }

    @Override
    public void onReadThemeRecommondFinish(boolean is_success, String error_msg, List<BannerInfo> themes) {

    }

    @Override
    public void onReadOriginalListActionsFinish(boolean is_success, String error_msg, List<ActionInfo> actions) {

    }

    public void showProgress()
    {
        if(mLoadingDialog == null)
            mLoadingDialog = LoadingDataDialog.getInstance(getActivity(),this);
        if(!mLoadingDialog.isShowing())
            mLoadingDialog.show();
    }

    public void dismissProgress()
    {
        if(mLoadingDialog!=null)
        {
            if(mLoadingDialog.isShowing())
            {
                mLoadingDialog.cancel();
                mLoadingDialog = null;
            }
        }

    }
}
