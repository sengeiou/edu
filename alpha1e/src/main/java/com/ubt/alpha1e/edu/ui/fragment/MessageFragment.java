package com.ubt.alpha1e.edu.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.adapter.MessageAdapter;
import com.ubt.alpha1e.edu.business.MessageRecordManager;
import com.ubt.alpha1e.edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e.edu.data.model.MessageInfo;
import com.ubt.alpha1e.edu.data.model.MessageSubInfo;
import com.ubt.alpha1e.edu.library.AutoVideo.visibility.calculator.SingleListViewItemActiveCalculator;
import com.ubt.alpha1e.edu.library.AutoVideo.visibility.scroll.ItemsPositionGetter;
import com.ubt.alpha1e.edu.library.AutoVideo.visibility.scroll.RecyclerViewItemPositionGetter;
import com.ubt.alpha1e.edu.library.ptr.PtrClassicFrameLayout;
import com.ubt.alpha1e.edu.library.ptr.PtrDefaultHandler;
import com.ubt.alpha1e.edu.library.ptr.PtrFrameLayout;
import com.ubt.alpha1e.edu.library.ptr.PtrHandler;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.MessageActivity;
import com.ubt.alpha1e.edu.ui.custom.MyLinearLayoutManager;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDataDialog;
import com.ubt.alpha1e.edu.ui.helper.IMessageUI;
import com.ubt.alpha1e.edu.ui.helper.MessageHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class MessageFragment extends BaseFragment implements IMessageUI,BaseDiaUI {

    private static final String TAG = "MessageFragment";

    public static final int UPDATE_ITEMS = 0;
    public static final int READ_CACHE_DATA_FINISH = 1;
    public static final int DISPLAY_DIALOG = 2;
    public static final int REQUEST_DATA_FINISH = 3;

    private RecyclerView mRecyclerView;
    private MyLinearLayoutManager mLayoutManager;
    public MessageAdapter mMessageAdapter;
    public BaseActivity mActivity;
    public Context mContext;
    public List<MessageInfo> mDatas = new ArrayList<>();
    private MessageHelper mMessageHelper;
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

    private boolean isRequestDataFinish = true;
    boolean isQuestRefresh = false;

    private final static int MESSAGE_TYPE_COLLECT = 6; //点赞收藏
    private final static int MESSAGE_TYPE_COMMENT = 3; //评论

    private LoadingDataDialog mLoadingDialog;
    private View view;
    private LinearLayout emptyView;


    public Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case UPDATE_ITEMS:
                    mMessageAdapter.notifyItemChanged((int)msg.obj);
                    break;
                case READ_CACHE_DATA_FINISH:

                    setRefreshComplete();
                    break;
                case DISPLAY_DIALOG:
                    dismissProgress();
                    break;
                case REQUEST_DATA_FINISH:
                    mMessageAdapter.notifyDataSetChanged();
                    setRefreshComplete();
                    if(mDatas.size() == 0){
                        showEmptyView();
                    }else {
                        hideEmptyView();
                    }

                    break;
                default:
                    break;

            }
        }
    };


    public MessageFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public MessageFragment(BaseActivity mBaseActivity, MessageHelper mHelper) {
        UbtLog.d(TAG, "--NoticeFragment--");
        mActivity = (MessageActivity) mBaseActivity;
        mMessageHelper = new MessageHelper(mActivity,this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        isQuestRefresh = false;
        view =  inflater.inflate(R.layout.fragment_message, null);
        initRecyclerView(view);
        requestNoticeData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 显示空页面
     */
    private void showEmptyView(){
        emptyView.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏空页面
     */
    private void hideEmptyView(){
        emptyView.setVisibility(View.GONE);
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

        if(mMessageHelper!=null){
            mMessageHelper.DistoryHelper();
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
        //showEmptyView();
    }

    private void initRecyclerView(View view)
    {
        emptyView = (LinearLayout) view.findViewById(R.id.message_empty);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_message);
        mLayoutManager = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mMessageAdapter = new MessageAdapter((BaseActivity) getActivity(),mContext,mDatas,mRecyclerView,mMessageHelper);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMessageAdapter);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        mRecyclerView.getItemAnimator().setSupportsChangeAnimations(false);
        mCalculator = new SingleListViewItemActiveCalculator(mMessageAdapter,
                new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView));
        ptrClassicFrameLayout = (PtrClassicFrameLayout)view.findViewById(R.id.pull_to_refresh);
        ptrClassicFrameLayout.setKeepHeaderWhenRefresh(true);
        ptrClassicFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                isNoMoreData = false;
                isQuestRefresh = true;
                mCurrentPage = 1;
                requestData(mCurrentPage);
            }
        });

        setAutoRefresh();

        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mScrollState = newState;
                if(newState == RecyclerView.SCROLL_STATE_IDLE && mMessageAdapter.getItemCount() > 0){
                    mCalculator.onScrollStateIdle();
                }
                if(newState == RecyclerView.SCROLL_STATE_IDLE && mMessageAdapter.getItemCount()-mLastVisibleItem<=1 && isNeedLoadingData)
                {

                    if(!isRequestDataFinish){
                        return;
                    }

                    if(!isNoMoreData){
                        ++mCurrentPage;
                        isRequestDataFinish = false;
                        showProgress();
                        requestData(mCurrentPage);
                        //requestOnlineData(mHistoryRecord,mCurrentPage);//滑到倒数第三个item时就默认加载下一页
                    }
                }

                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE: // The RecyclerView is not currently scrolling.
                        //对于滚动不加载图片的尝试
                        mMessageAdapter.setScrolling(false);
                        mMessageAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING: // The RecyclerView is currently being dragged by outside input such as user touch input.
                        mMessageAdapter.setScrolling(true);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING: // The RecyclerView is currently animating to a final position while not under
                        mMessageAdapter.setScrolling(true);
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

        //requestData();
    }

    /**
     * 请求通知数据
     * */
    public void requestNoticeData()
    {
        UbtLog.d(TAG,"----requestNoticeData--" + mMessageHelper);
        if(mMessageHelper != null){
            mMessageHelper.getNewMessages(MessageRecordManager.TYPE_NOTICE,1,10);
        }
    }

    /**
     * 请求信息数据
     * */
    public void requestData(int page)
    {

        UbtLog.d(TAG,"----requestData--" + mMessageHelper);
        if(mMessageHelper != null){
            mMessageHelper.getNewMessages(MessageRecordManager.TYPE_MESSAGE,page,10);
        }
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
    public void onAddNoReadMessage() {

    }

    @Override
    public void onReadUnReadRecords(List<Long> ids) {
        ((MessageActivity)getActivity()).refreshNoReadData(ids);
    }

    @Override
    public void onGetNewMessages(boolean isSuccess, final String errorInfo, List<MessageInfo> messages,int type) {
        if(type == MessageRecordManager.TYPE_NOTICE){
            ((MessageActivity)getActivity()).refreshMessageData(isSuccess,messages,type,mMessageHelper);
            return;
        }

        if(isSuccess){
            if(messages != null){
                if(isQuestRefresh){
                    mDatas.clear();
                    isQuestRefresh = false;
                    if(messages.size() > 0){
                        mMessageHelper.setMessageIdRecord(String.valueOf(messages.get(0).messageId));
                    }
                }

                if(messages.size() < 10){
                    isNoMoreData = true;
                }

                for (MessageInfo msg : messages){
                    //UbtLog.d(TAG,"messages = " + MessageInfo.getModelStr(msg));
                    msg.subInfo = jsonStrToMsgInfo(msg.extra,msg.messageType);
                    mDatas.add(msg);
                }

                isRequestDataFinish = true;

                mHandler.sendEmptyMessage(REQUEST_DATA_FINISH);

            }
        }else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    setRefreshComplete();
                    Toast.makeText(mActivity, errorInfo, Toast.LENGTH_SHORT).show();
                }
            });
        }
        isRequestDataFinish = true;

        mHandler.sendEmptyMessageDelayed(DISPLAY_DIALOG,100);

    }

    /**
     * 将json转成MessageSubInfo
     * @param extra
     * @param messageType
     * @return
     */
    private MessageSubInfo jsonStrToMsgInfo(String extra,int messageType){
        MessageSubInfo subInfo = null;
        try {
            //UbtLog.d(TAG,"extra = " + extra);

            subInfo = new MessageSubInfo();
            JSONObject extraObj = new JSONObject(extra);
            subInfo.userName = extraObj.getString("userName");
            subInfo.userImage = extraObj.getString("userImage");
            subInfo.actionId = extraObj.getLong("actionId");
            subInfo.actionName = extraObj.getString("actionName");
            subInfo.actionImagePath = extraObj.getString("actionImagePath");
            subInfo.actionHeadUrl = extraObj.getString("actionHeadUrl");
            subInfo.actionResume = extraObj.getString("actionResume");
            subInfo.actionPath = extraObj.getString("actionPath");
            subInfo.actionVideoPath = extraObj.getString("actionVideoPath");
            subInfo.actionUserName = extraObj.getString("actionUserName");
            if(messageType == MESSAGE_TYPE_COLLECT){
                subInfo.collectTime = extraObj.getString("collectTime");
            }else if(messageType == MESSAGE_TYPE_COMMENT){
                subInfo.commentTime = extraObj.getString("commentTime");
                subInfo.reCommentContext = extraObj.getString("reCommentContext");
                subInfo.reCommentUser = extraObj.getString("reCommentUser");
                subInfo.commentContext = extraObj.getString("commentContext");
            }

            //后台脏数据，暂时对null字符串处理
            if("null".equals(subInfo.actionResume)){
                subInfo.actionResume = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return subInfo;

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    public void showProgress()
    {
        if(mLoadingDialog == null){
            mLoadingDialog = LoadingDataDialog.getInstance(getActivity(),this);
        }

        if(!mLoadingDialog.isShowing()){
            mLoadingDialog.show();
        }
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
