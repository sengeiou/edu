package com.ubt.alpha1e.edu.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.adapter.ActionSelectAdapter;
import com.ubt.alpha1e.edu.event.ActionEvent;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.CustomInstructionPlayActionActivity;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名
 * @author lihai
 * @description 指令动作配置选择列表类。
 * @date 2017.04.11
 *
 */
public class CustomInstructionPlayActionFragment extends BaseFragment {

    private static final String TAG = "CustomInstructionPlayActionFragment";
    private static final String REQUEST_POSITION = "REQUEST_POSITION";


    public static final int UPDATE_ITEMS = 0; //单个更新Adapter常量
    public static final int UPDATE_ALL = 1; //更新整个Adapter 常量
    public static final int CLICK_POSITION = 2;  //点击Item

    private View mView = null;
    private RecyclerView mRecyclerview;
    private LinearLayoutManager mLinearLayoutManager;
    private ActionSelectAdapter mAdapter;
    private List<Map<String,Object>> mDatas = new ArrayList<>();
    private int mRequestPosition = 0; // 请求下标索引 0为内置动作  1 为我的下载  2 我的创建

    private MyActionsHelper mHelper;
    private LoadingDialog mLoadingDialog;

    public Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case UPDATE_ITEMS:
                    mAdapter.notifyItemChanged((int)msg.obj);
                    break;

                case UPDATE_ALL:
                    mAdapter.notifyDataSetChanged();
                    break;
                case CLICK_POSITION:
                    int position = msg.arg1;
                    for (int i = 0; i < mDatas.size(); i++){
                        if(i == position){
                            mDatas.get(i).put(MyActionsHelper.map_val_action_selected,true);
                        }else {
                            mDatas.get(i).put(MyActionsHelper.map_val_action_selected,false);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;

            }
       }
    };

    public CustomInstructionPlayActionFragment() {
        // Required empty public constructor
    }

    public static CustomInstructionPlayActionFragment newInstance(int requestPosition) {
        CustomInstructionPlayActionFragment fragment = new CustomInstructionPlayActionFragment();
        Bundle args = new Bundle();
        args.putInt(REQUEST_POSITION, requestPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mRequestPosition = getArguments().getInt(REQUEST_POSITION,0);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mHelper = MyActionsHelper.getInstance((BaseActivity) getActivity());
        mView =  inflater.inflate(R.layout.fragment_custom_instruction_playction, container, false);

        initUI();
        initRecyclerView();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        registerListeners();
        showDialog();
        mHelper.doReadActions();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeListeners();
    }

    /**
     * 注册相关监听器
     */
    public void registerListeners(){
        EventBus.getDefault().register(this);
        mHelper.RegisterHelper();
    }

    /**
     * 取消已经注册的监听器
     */
    public void removeListeners(){
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        mHelper.UnRegisterHelper();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {

        dismissDialog();
        super.onDestroy();
    }

    private void initRecyclerView()
    {

        mRecyclerview = (RecyclerView)mView.findViewById(R.id.recyclerview_action_select);
        mLinearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mAdapter = new ActionSelectAdapter(getActivity(),mDatas,mHandler);
        mRecyclerview.setLayoutManager(mLinearLayoutManager);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setHasFixedSize(true);
        RecyclerView.ItemAnimator animator = mRecyclerview.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        mRecyclerview.getItemAnimator().setSupportsChangeAnimations(false);
    }

    /**
     * 监听Eventbus消息方法
     * @param event
     */
    @Subscribe
    public void onEventNetwork(ActionEvent event) {
        if(event.getEvent() == ActionEvent.Event.READ_ACTIONS_FINISH){
            dealActionData(event.getActionsNames());
            dismissDialog();
        }
    }

    /**
     * 处理动作数据
     * @param actionNames
     */
    private void dealActionData(List<String> actionNames){

        if(mRequestPosition == 0){//内置动作
            for(int i = 0; i < MyActionsHelper.localSize; i++){
                mDatas.add(getDataMap(actionNames.get(i)));
            }
        }else if(mRequestPosition == 1){//我的下载
            for(int i = MyActionsHelper.localSize; i < (MyActionsHelper.localSize + MyActionsHelper.myDownloadSize); i++){
                mDatas.add(getDataMap(actionNames.get(i)));
            }
        }else if(mRequestPosition == 2){//我的创建
            for(int i = (MyActionsHelper.localSize + MyActionsHelper.myDownloadSize); i < actionNames.size(); i++){
                mDatas.add(getDataMap(actionNames.get(i)));
            }
        }

        mHandler.sendEmptyMessage(UPDATE_ALL);
        //UbtLog.d(TAG,"actionNames = " + actionNames.size() + "      mDatas = " + mDatas.size());
    }

    public String getSelectAction(){
        String selectActionName = "";
        for (int i = 0; i < mDatas.size(); i++){
            if((boolean) mDatas.get(i).get(MyActionsHelper.map_val_action_selected)){
                selectActionName = (String) mDatas.get(i).get(MyActionsHelper.map_val_action_name);
            }
        }
        return selectActionName;
    }

    /**
     * 通过actionName 转成Map
     * @param actionName
     * @return
     */
    private Map<String,Object> getDataMap(String actionName){
        Map<String,Object> map = new HashMap<>();
        map.put(MyActionsHelper.map_val_action_selected,false);
        map.put(MyActionsHelper.map_val_action_name,actionName);
        return map;
    }

    /**
     * 显示转动对话框
     */
    public void showDialog() {
        if(mLoadingDialog!=null && !mLoadingDialog.isShowing())
        {
            mLoadingDialog.show();
        }
    }

    /**
     * 消失对话框
     */
    public void dismissDialog() {

        if(mLoadingDialog != null && mLoadingDialog.isShowing() && !getActivity().isFinishing())
        {
            mLoadingDialog.cancel();
        }
    }

    @Override
    protected void initUI() {

        mLoadingDialog = LoadingDialog.getInstance(getActivity(),(CustomInstructionPlayActionActivity) getActivity());
        mLoadingDialog.setDoCancelable(true,6);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

}
