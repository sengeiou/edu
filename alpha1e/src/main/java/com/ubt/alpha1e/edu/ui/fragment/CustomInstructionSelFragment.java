package com.ubt.alpha1e.edu.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.adapter.CustomInstructionSelAdapter;
import com.ubt.alpha1e.edu.adapter.CustomInstructionTypeAdapter;
import com.ubt.alpha1e.edu.data.Constant;
import com.ubt.alpha1e.edu.data.model.InstructionInfo;
import com.ubt.alpha1e.edu.ui.CustomInstructionSelActivity;
import com.ubt.alpha1e.edu.utils.InstructionUtil;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名
 * @author lihai
 * @description 指令选择列表类。
 * @date 2017.04.11
 *
 */
public class CustomInstructionSelFragment extends BaseFragment {

    private static final String TAG = "CustomInstructionSelFragment";

    public static final String INSTRUCTION_TYPE_NAME = "INSTRUCTION_TYPE_NAME";
    public static final String INSTRUCTION_TYPE_SELECT = "INSTRUCTION_TYPE_SELECT";

    public static final int UPDATE_ITEMS = 0; //更新单个
    public static final int UPDATE_ALL = 1;  //更新全部
    public static final int INSTRUCTION_INFO_CLICK_POSITION = 2;  //点击Item
    public static final int INSTRUCTION_TYPE_CLICK_POSITION = 3;  //点击Item

    //定义类UI成员变量
    private View mView = null;
    private RecyclerView mRecyclerviewInstrucitonInfo;
    private RecyclerView mRecyclerviewInstrucitonType;

    //定义类本地成员变量
    private int mInstructionType = 0; // 指令类型 0为语音  1 为传感器
    private int mInstructionSubType = InstructionUtil.INSTRUCTION_TYPE_ALL; //指令语音子类 默认为全部-1

    private CustomInstructionSelAdapter mInstructionInfoAdapter; //指令适配器
    private CustomInstructionTypeAdapter mInstructionTypeAdapter; //指令类型适配器
    private List<InstructionInfo> mInstructionInfoList = new ArrayList<>(); //指令数据列表
    private List<Map<String,Object>> mInstructionTypeList = new ArrayList<>(); //指令类型数据列表

    //定义指令类型个数
    private int[] mInstructionTypes = new int[]{
            R.string.ui_instruction_voice_all,
            R.string.ui_instruction_voice_action,
            R.string.ui_instruction_voice_robot,
            R.string.ui_instruction_voice_common,
            R.string.ui_instruction_voice_interaction,
            R.string.ui_instruction_voice_emotion,
            R.string.ui_instruction_voice_network
            };

    public CustomInstructionSelActivity mActivity;
    public Context mContext;

    public Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case UPDATE_ITEMS:
                    mInstructionInfoAdapter.notifyItemChanged((int)msg.obj);
                    break;
                case UPDATE_ALL:
                    mInstructionInfoAdapter.notifyDataSetChanged();
                    break;
                case INSTRUCTION_INFO_CLICK_POSITION:
                    int position = msg.arg1;
                    for(int i=0; i<mInstructionInfoList.size(); i++ ){
                        if(position == i){
                            mInstructionInfoList.get(i).selected = true;
                        }else {
                            mInstructionInfoList.get(i).selected = false;
                        }
                    }
                    mInstructionInfoAdapter.notifyDataSetChanged();
                    break;
                case INSTRUCTION_TYPE_CLICK_POSITION:
                    int typePos = msg.arg1;
                    for(int i = 0; i<mInstructionTypeList.size(); i++ ){
                        if(typePos == i){
                            mInstructionTypeList.get(i).put(INSTRUCTION_TYPE_SELECT,true);
                        }else {
                            mInstructionTypeList.get(i).put(INSTRUCTION_TYPE_SELECT,false);
                        }
                    }
                    mInstructionSubType = typePos - 1;
                    mInstructionTypeAdapter.notifyDataSetChanged();

                    refrechData();
                    break;
                default:
                    break;

            }
       }
    };

    public CustomInstructionSelFragment() {
        // Required empty public constructor
    }

    public static CustomInstructionSelFragment newInstance(int instructionType) {
        CustomInstructionSelFragment fragment = new CustomInstructionSelFragment();
        Bundle args = new Bundle();
        args.putInt(Constant.INSTRUCTION_TYPE, instructionType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mInstructionType = getArguments().getInt(Constant.INSTRUCTION_TYPE, InstructionInfo.InstructionType.voice.ordinal());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        UbtLog.d(TAG,"onCreateView");
        mActivity = (CustomInstructionSelActivity) getActivity();
        mContext  = getActivity();

        mView =  inflater.inflate(R.layout.fragment_custom_instruction_sel, container, false);

        initUI();
        initRecyclerView();
        initData();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeListeners();
    }

    public void removeListeners(){

    }

    public void registerListeners(){

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView()
    {
        //初始化指令类型对象
        mRecyclerviewInstrucitonType = (RecyclerView)mView.findViewById(R.id.recyclerview_custom_instruction_type);
        LinearLayoutManager linearLayoutManagerType = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        mInstructionTypeAdapter = new CustomInstructionTypeAdapter(mInstructionTypeList,getActivity(),mHandler);
        mRecyclerviewInstrucitonType.setLayoutManager(linearLayoutManagerType);
        mRecyclerviewInstrucitonType.setAdapter(mInstructionTypeAdapter);

        //初始化指令数据对象
        mRecyclerviewInstrucitonInfo = (RecyclerView)mView.findViewById(R.id.recyclerview_custom_instruction_sel);
        LinearLayoutManager linearLayoutManagerSel = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mInstructionInfoAdapter = new CustomInstructionSelAdapter(mInstructionInfoList,getActivity(),mHandler);
        mRecyclerviewInstrucitonInfo.setLayoutManager(linearLayoutManagerSel);
        mRecyclerviewInstrucitonInfo.setAdapter(mInstructionInfoAdapter);

        //判断是否语言指令判断显示指令子类型
        if(mInstructionType == InstructionInfo.InstructionType.voice.ordinal()){
            mRecyclerviewInstrucitonType.setVisibility(View.VISIBLE);
        }else {
            mRecyclerviewInstrucitonType.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化数据
     */
    public void initData(){

        //初始化指令类型数据
        Map<String,Object> typeMap = null;
        for(int i = 0;i < mInstructionTypes.length ; i++){
            typeMap = new HashMap<>();
            typeMap.put(INSTRUCTION_TYPE_NAME,getActivity().getString(mInstructionTypes[i]));
            if((mInstructionSubType + 1) == i){
                typeMap.put(INSTRUCTION_TYPE_SELECT,true);
            }else {
                typeMap.put(INSTRUCTION_TYPE_SELECT,false);
            }
            mInstructionTypeList.add(typeMap);
        }
        mInstructionTypeAdapter.notifyDataSetChanged();

        List<InstructionInfo> mInstrctionInfoList = InstructionUtil.getInstructionInfoListByType(mInstructionType + "", mInstructionSubType + "");
        mInstructionInfoList.addAll(mInstrctionInfoList);
        UbtLog.d(TAG,"mInstructionInfoList = " + mInstructionInfoList.size());
        mHandler.sendEmptyMessage(UPDATE_ALL);
    }

    /**
     * 刷新指令数据
     */
    public void refrechData(){
        mInstructionInfoList.clear();
        List<InstructionInfo> mInstrctionInfoList = InstructionUtil.getInstructionInfoListByType(mInstructionType + "", mInstructionSubType + "");
        mInstructionInfoList.addAll(mInstrctionInfoList);
        mHandler.sendEmptyMessage(UPDATE_ALL);
    }

    /**
     * 获取当前选中指令
     * */
    public InstructionInfo getCurrentSelectInstructionInfo()
    {
        for(InstructionInfo instructionInfo : mInstructionInfoList){
            if(instructionInfo.selected){
                return instructionInfo;
            }
        }
        return null;
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

}
