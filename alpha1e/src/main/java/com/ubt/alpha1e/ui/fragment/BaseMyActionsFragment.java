package com.ubt.alpha1e.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.MyActionsRecyclerAdapter;
import com.ubt.alpha1e.business.ActionsControllerListener;
import com.ubt.alpha1e.business.EndlessRecyclerOnScrollListener;
import com.ubt.alpha1e.business.MyActionsRecyclerListener;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.ui.MyActionsActivity;
import com.ubt.alpha1e.ui.dialog.ProgressDialog;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//onAttach --> onCreate --> onCreateView ---> onActivityCreate--> onStart --> onResume
public abstract class BaseMyActionsFragment extends Fragment implements MyActionsRecyclerListener,ActionsControllerListener{

    public static final String TAG = "BaseMyActionsFragment";
    public static final int SHOW_LOADING_DIALOG = 0x01;
    public static final int DISMISS_LOADING_DIALOG = 0x02;
    public static final int SHOW_PROGRESS_DIALOG = 0x03;
    public static final int DISMISS_PROGRESS_DIALOG = 0x04;
    public static final int UPDATE_VIEWS = 0x05;
    public static final int UPDATE_ITEMS = 0x06;
    public static final int DELETE_ITEMS = 0x07;
    public RecyclerView mRecyclerView;
    public LinearLayoutManager mLayoutManager;
    public MyActionsRecyclerAdapter mAdapter;
    public MyActionsActivity mActivity;
    public View mView,mEmptyView;
    public MyActionsHelper mHelper;
    public Dialog mLoading;
    public ProgressDialog mProgressDialog;
    public List<Map<String, Object>> mDatas = new ArrayList<>();
    public int type = -1;
    public boolean isUserLogin = false;
    private Button btn_stop;

    //控件是否已经初始化
    private boolean isCreateView = false;
    //是否已经加载过数据
    public  boolean isLoadData = false;
    public boolean isFragmentVisible = false;
    public onRefreshActivityView onRefreshActivityView;
    private OnInteractWithActivity mOnActivityListeners;


    public Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
//                case SHOW_LOADING_DIALOG:
//                    showLoadingDialog();
//                    break;
//                case DISMISS_LOADING_DIALOG:
//                    dismissLoadingDialog();
//                    break;
                case SHOW_PROGRESS_DIALOG:

                    showProgressDialog((String)msg.obj);
                    break;
                case DISMISS_PROGRESS_DIALOG:
                    dismissProgressDialog();
                    break;
                case UPDATE_VIEWS:
                    UbtLog.d(TAG,"---wmma UPDATE_VIEWS");
                    if((int)msg.obj == 1)
                    {
                        mAdapter.notifyDataSetChanged();
                    }
                    updateViews(true,(int)msg.obj);
                    dismissLoadingDialog();
//                    dismissProgressDialog();
                    break;
                case UPDATE_ITEMS:
                    UbtLog.d(TAG,"---wmma UPDATE_ITEMS");
                    mAdapter.notifyItemChanged((int)msg.obj);
                    dismissProgressDialog();
                    break;
                case DELETE_ITEMS:
                    int pos = (int)msg.obj;
                    mAdapter.notifyItemRemoved(pos);
                    mAdapter.notifyItemRangeChanged(pos,mAdapter.getItemCount());
                    dismissProgressDialog();
                    break;
            }
        }
    };


    public BaseMyActionsFragment() {
        // Required empty public constructor
    }


    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MyActionsActivity) activity;
        if(activity instanceof OnInteractWithActivity)
        {
            mOnActivityListeners  = (OnInteractWithActivity)activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        onRefreshActivityView = mActivity;
        initDatas(mActivity);
        mView = inflater.inflate(R.layout.fragment_my_actions, container, false);
        initRecyclerViews();
        initUI();
        updateViews(isLogin(mActivity),-1);
        isCreateView = true;
        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(mHelper!=null)
            mHelper.RegisterHelper();

    }

    @Override
    public void onPause() {
        super.onPause();
//        if(mHelper!=null)
//            mHelper.UnRegisterHelper();
    }

    @Override
    public void onDestroyView() {
        dismissLoadingDialog();
        super.onDestroyView();
        Log.e("wilson$$$","----onDestroyView--------");
        clearDatas();
        if(mHelper!=null)
        {
//            mHelper.UnRegisterHelper();
//            mHelper.DistoryHelper();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getUserVisibleHint())//第一次进入时，第一个fragment能加载数据
        {
            UbtLog.e("wilson","onActivityCreated");
            lazyLoad();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        UbtLog.d("wilson","BaseMyActionsFragment---setUserVisibleHint----"+isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isCreateView) {//先于oncreateView执行
            isFragmentVisible = true;
            UbtLog.d("wilson","BaseMyActionsFragment---lazyLoad----");
            lazyLoad();
        }
    }

    public void userVisible(boolean isUserVisible) {
        if(isUserVisible)
        {
            if(mHelper!=null)
                mHelper.RegisterHelper();
        }else
        {
            if(mHelper!=null)
                mHelper.UnRegisterHelper();
        }


    }

    public void lazyLoad()
    {
        if(!isLoadData){
            //加载数据操作
            if(type == 0||isLogin(getActivity()))//内置动作不需要登录 type == 0
             firstLoadData();

        }
    }


    public void sendMessage(Object object,int what)
    {
        UbtLog.d(TAG, "---wmma sendMessage!");
        Message msg = new Message();
        msg.obj = object;
        msg.what = what;
        if(mHandler!=null)
            mHandler.sendMessage(msg);
    }
    public void showLoadingDialog()
    {
        if (mOnActivityListeners!= null) {
            mOnActivityListeners.showDialog();
        }
    }

    public void dismissLoadingDialog()
    {

        if (mOnActivityListeners!= null) {
            mOnActivityListeners.dismissDialog();
        }

//        ProgressUtils.dimissLoadingProgressBar();
//        if(mLoading!=null)
//        {
//            if(mLoading.isShowing())
//                mLoading.cancel();
//            mLoading = null;
//        }
    }


    public void showProgressDialog(String str)
    {
        if(mProgressDialog!=null&&mProgressDialog.isShowing())
        {
            mProgressDialog.cancel();
        }
        mProgressDialog = ProgressDialog.getInstance(mActivity);
        if(TextUtils.isEmpty(str))
        mProgressDialog.show();
        else
            mProgressDialog.showMsg(str);
    }

    public void dismissProgressDialog()
    {
        if(mProgressDialog!=null)
        {
            if(mProgressDialog.isShowing())
                mProgressDialog.cancel();
            mProgressDialog = null;
        }

    }

   public  void initUI()
   {

   }


    public  boolean isLogin(Activity context) {
        UserInfo current_user = ((AlphaApplication) context
                .getApplicationContext()).getCurrentUserInfo();
        return current_user==null?false:true;
    }

    @Override
    public void initRecyclerViews() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview_my_actions);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        mRecyclerView.getItemAnimator().setSupportsChangeAnimations(false);
        mAdapter = new MyActionsRecyclerAdapter(getActivity(),mDatas,type,mHelper);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    public void showOrHideLayout(boolean isShow)
    {
        RelativeLayout rl = (RelativeLayout) mView.findViewById(R.id.rl_sync_download);
        rl.setVisibility(isShow?View.VISIBLE: View.GONE);
    }

    private void requestMoreData()
    {
        mDatas.add(null);//11
        mAdapter.notifyItemInserted(mDatas.size()-1);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDatas.remove(mDatas.size()-1);//10
                mAdapter.notifyItemRemoved(mDatas.size());
                for(int i = 0;i<10;i++)
                {
                    Map<String, Object> map = new HashMap<String, Object>();
                    mDatas.add(map);
                    mAdapter.notifyItemInserted(mDatas.size()-1);
                }
                mOnScrollListener.isLoadingMore = false;
            }
        },2000);


    }


    public interface onRefreshActivityView
    {

        void updateUI(int refreshType);
    }

    public interface  OnInteractWithActivity
    {
        void showDialog();

        void dismissDialog();

    }
    public String getResourceString(String s)
    {
        return mActivity.getStringResources(s);
    }

    public EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);

            if(mDatas.size()!=0)
            {
//                requestMoreData();
            }

        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    break;
            }

        }
    };
}
