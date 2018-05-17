package com.ubt.alpha1e.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.BuildConfig;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.Constant;

import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.behaviorhabits.model.behaviourHabitModel;
import com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.course.feature.FeatureActivity;
import com.ubt.alpha1e.course.merge.MergeActivity;
import com.ubt.alpha1e.course.principle.PrincipleActivity;
import com.ubt.alpha1e.course.split.SplitActivity;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.ui.MyActionsActivity;
import com.ubt.alpha1e.ui.RemoteActivity;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.dialog.IDismissCallbackListener;
import com.ubt.alpha1e.ui.helper.ActionsHelper;
import com.ubt.alpha1e.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IActionsUI;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.RobotInnerActionInfoUtil;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyActionsCircleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MyActionsCircleFragment extends BaseMyActionsFragment implements /*ActionPlayerListener ,*/IActionsUI{

    private static final String TAG = "MyActionsCircleFragment";
    private OnFragmentInteractionListener mListener;
    private RecyclerView mSyncRecyclerview;
    private LinearLayoutManager mLayoutManager;
    private ActionsCircleAdapter mAdapter;
    private List<Map<String, Object>> mDatas = new ArrayList<>();
    private View mView;
    private int type = 6;
    private MyActionsActivity mActivity;
    private  boolean isStartLooping = false;

    private MyActionsHelper mHelper;
    private String currentCycleActionName;
    private TextView txt_title;

    private RelativeLayout rlCircle;
    private TextView tvCircle;
    private ImageView ivCircle;
    private RelativeLayout mServoGuide;
    private ImageView mCloseGuide;
    private boolean isShowHibitsDialog = false;


    public MyActionsCircleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_actions_circle, container, false);
        isStartLooping=mHelper.getLoopingFlag();
        UbtLog.d(TAG,"isStartLooping flag"+isStartLooping);
        initViews();
        EventBus.getDefault().register(this);
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UbtLog.d(TAG,"onDestroyView");
    }

    private void initViews()
    {
        txt_title = (TextView) mView.findViewById(R.id.txt_base_title_name);
        txt_title.setText(getResourceString("ui_cycle_title"));
        ImageView img_cancel = (ImageView) mView.findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                //没有使用功能换皮肤功能
                UbtLog.d(TAG,"onClick finished ");
                getActivity().finish();
            }
        });

        rlCircle=(RelativeLayout) mView.findViewById(R.id.rl_cycle);
        ivCircle=(ImageView)mView.findViewById(R.id.btn_start_cycle);
        tvCircle=(TextView) mView.findViewById(R.id.tv_start_cycle);
        mServoGuide=(RelativeLayout)mView.findViewById(R.id.servo_guide);
        mServoGuide.setVisibility(View.GONE);
        mCloseGuide=(ImageView)mView.findViewById(R.id.iv_close_header);
        mCloseGuide.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                SPUtils.getInstance().put(Constant.SP_SHOW_SERVO_GUIDE, true);
                mServoGuide.setVisibility(View.GONE);
            }
        });
        ivCircle.setImageDrawable(mActivity.getDrawableRes("ic_circle_play_disable"));
        ivCircle.setAlpha(0.5f);
        tvCircle.setText("循环播放");
        tvCircle.setAlpha(0.5f);
        rlCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //循环播放
                if (MyActionsHelper.mCurrentSeletedNameList.size() > 0) {
                    if (!isStartLooping) {
                        if (lowBatteryNotExecutedAction()) {
                            return;
                        }
                        ;
                        if (!SPUtils.getInstance().getBoolean(Constant.SP_SHOW_SERVO_GUIDE, false)) {
                            mServoGuide.setVisibility(View.VISIBLE);
                        }
                        UbtLog.d(TAG, "BEGIN CIRCLE PLAY");
                        setActionPlayType(true);
                        JSONArray action_cyc_list = new JSONArray(MyActionsHelper.mCurrentSeletedNameList);
                        try {
                            mHelper.doCycle(action_cyc_list);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    } else {
                        UbtLog.d(TAG, "STOP CIRCLE PLAY");
                        //先复位标志isStartLooping
                        setActionPlayType(false);
                        //在调用STOP，然后回调到notePlayFinish
                        mHelper.stopPlayAction();
                    }
                    updateCircleButton();
                }
            }
        });
        mSyncRecyclerview = (RecyclerView) mView.findViewById(R.id.recyclerview_circle);
        //修改LAYOUT格式，有LinearLayoutManager到GridLayoutManager样式，一行3个图标
        // mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mSyncRecyclerview.setLayoutManager(gridLayoutManager);
        RecyclerView.ItemAnimator animator = mSyncRecyclerview.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        mSyncRecyclerview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 20;
                outRect.top = 20;
                outRect.left = 30;
                outRect.right = 30;
            }
        });

        mAdapter = new ActionsCircleAdapter(getActivity(),type);
        mSyncRecyclerview.setAdapter(mAdapter);

    }

    public void setDatas(List<Map<String,Object>> datas)
    {
       // new Exception().printStackTrace();
        mDatas=datas;
        if(mHelper!=null) {
            mHelper.setPlayContent(mDatas);
           // debugActionList(mDatas);
        }
        //MyActionHelper trigger setDatas
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
            updateCircleButton();
        }
    }

    @Override
    public void updateViews(boolean isLogin, int hasData) {

    }

    @Override
    public void firstLoadData() {
        UbtLog.d("wilson","MyActionsCircleFragment----setUserVisibleHint--");
    }

    @Override
    public void initDatas(Activity activity) {

    }

    @Override
    public void clearDatas() {

    }

    @Override
    public void onResume() {
        super.onResume();
        UbtLog.d(TAG,"MyActionsCircleFragment----mDatas.size()--"+mDatas.size());
        if(mHelper!=null) {
           // mHelper.doReadActions();
        }
        if(mHelper.isStartHibitsProcess()){
            isShowHibitsDialog = true;

            String msg = "行为习惯正在进行中，请先完成";
            String position = "好的";
            new ConfirmDialog(mActivity)
                    .builder()
                    .setMsg(msg)
                    .setCancelable(false)
                    .setPositiveButton(position, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            isShowHibitsDialog = false;
                            if(mHelper.isStartHibitsProcess()){
                                //行为习惯流程未结束，退出当前流程
                                if(mActivity != null){
                                    mActivity.finish();
                                }
                            }
                        }
                    }).show();

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        UbtLog.d(TAG, "onStop isCycleActionFragment=" + false);
//        AlphaApplication.setCycleFragmentShow(false);
//        if(mHelper!= null){
//            mHelper.unRegisterListeners(this);
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "onDestroy isCycleActionFragment=" + false);
        EventBus.getDefault().unregister(this);
        AlphaApplication.setCycleFragmentShow(false);
        if(mHelper!= null){
            mHelper.unRegisterListeners(this);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null){
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mActivity = ((MyActionsActivity)context);
        AlphaApplication.setCycleFragmentShow(true);
        UbtLog.d(TAG, "onAttach isCycleActionFragment=" + AlphaApplication.isCycleActionFragment());
        mDatas = mActivity.mInsideDatas;
//        mHelper = MyActionsHelper.getInstance(mActivity);
        mHelper =  mActivity.mHelper; /*= mHelper;*/
        if(mHelper!=null && (mActivity.fragment instanceof MyActionsCircleFragment)) {
            UbtLog.e(TAG, "--firstLoadData MyActionsCircleFragment");
            mHelper.registerListeners(this);
//            mHelper.addPlayerListeners(this);
            //读取动作列表，向机器人发送指令，获取机器人动作列表
            mHelper.doReadActions();
        }
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        UbtLog.d("wilson","MyActionsCircleFragment----onHiddenChanged----"+hidden);
        UbtLog.d(TAG, "-->onHiddenChanged");
//        if(hidden)
//        {
//            if(mHelper!=null)
//                mHelper.removePlayerListeners(this);
//
//        }else
//        {
//            if(mHelper!=null)
//            {
//                mHelper.addPlayerListeners(this);
//                if(mHelper.getPlayerState()== ActionPlayer.Play_state.action_playing&& mHelper.getPlayerType() == ActionPlayer.Play_type.cycle_action)
//                {
//                    if(mListener!=null) mListener.onHiddenLoopButton();
//                    isStartLooping = true;
//                }else
//                {
//                    if(mListener!=null) mListener.onShowLoopButton();
//                    isStartLooping = false;
//                    MyActionsHelper.mCurrentSeletedNameList.clear();
//                    MyActionsHelper.mCurrentSeletedActionInfoMap.clear();
//                    for(Map<String,Object> item:mDatas)
//                    {
//                        item.put(MyActionsHelper.map_val_action_is_playing,false);
//                        item.put(MyActionsHelper.map_val_action_selected,false);
//                    }
//                }
//            }
//
//        }
    }

    @Override
    public void onStartPlay() {

    }

    @Override
    public void onStopPlay() {
        UbtLog.d(TAG, "-->onStopPlay");
//        mHelper.stopPlayAction();

    }

    @Override
    public void OnStartLoop() {
        UbtLog.d(TAG, "-->OnStartLoop");
    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {

        if(mSourceActionNameList!=null)
        {
            UbtLog.d(TAG,"action = " + action + "   mSourceActionNameList = " +mSourceActionNameList.size());
            if(mListener!=null)
                mListener.onHiddenLoopButton();
            for(String item:mSourceActionNameList)
            {
                for(Map<String,Object> map:mDatas)
                {
                    if(item.equals(map.get(MyActionsHelper.map_val_action_name)))
                    {
                        UbtLog.d(TAG,"item = " + item);
                        map.put(MyActionsHelper.map_val_action_is_playing,true);
                        break;
                    }
                }
                break;
            }
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {

        String name = mHelper.getCurrentPlayName();
        UbtLog.d(TAG, "---notePlayPause wmma name=" + currentCycleActionName + "---pos="+ mHelper.getCycleNum());

        for(int i = 0;i<mDatas.size();i++)
        {
            Map<String,Object> map = mDatas.get(i);
            if(map.get(ActionsHelper.map_val_action_name).equals(currentCycleActionName))
            {
                map.put(ActionsHelper.map_val_action_is_playing,false);

                break;
            }
        }

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        int pos = mHelper.getCurrentPlayPosition(mDatas);
        String name = mHelper.getCurrentPlayName();

        for(Map<String,Object> item:mDatas)
        {
            if(currentCycleActionName.equals(item.get(MyActionsHelper.map_val_action_name)))
            {
                item.put(MyActionsHelper.map_val_action_is_playing,true);
            }else{
                item.put(MyActionsHelper.map_val_action_is_playing,false);
            }

        }

        mAdapter.notifyDataSetChanged();

    }

    /**
     * 单曲播放或者多曲循环结束的回调接口
     * @param mSourceActionNameList
     * @param mCurrentPlayType
     * @param hashCode
     */
    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_title.setText(getResourceString("ui_cycle_title"));
            }
        });
        //点击单独播放停止或者循环播放停止
        if (!isStartLooping) {
            UbtLog.d(TAG, "notePlayFinish clear data"+ mAdapter.getItemCount());
            for(int i=0;i<mSourceActionNameList.size();i++){
                UbtLog.d(TAG,"GET SOURCE "+mSourceActionNameList.get(i));
            }
            changeAdapter(isStartLooping);
        } else {
            UbtLog.d(TAG, "notePlayFinish clear data 2");
            //拍头执行到这里
            if (mCurrentPlayType == ActionPlayer.Play_type.cycle_action) {
                UbtLog.d(TAG, "---show toast  notePlayFinish cycle !");
                UbtLog.d(TAG, "---isStartLooping = " + isStartLooping);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!lowBatteryNotExecutedAction()) {
                            Toast.makeText(
                                    getActivity(), mActivity.getStringResources("ui_action_cycle_stop"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                setActionPlayType(false);
            }
            changeAdapter(isStartLooping);

        }


    }

    private void changeAdapter(boolean isLoop) {
        if(!isLoop) {
            MyActionsHelper.mCurrentSeletedNameList.clear();
            MyActionsHelper.mCurrentSeletedActionInfoMap.clear();
        }
        for (Map<String, Object> item : mDatas) {
            item.put(MyActionsHelper.map_val_action_is_playing, false);
            item.put(MyActionsHelper.map_val_action_selected, false);
        }
        AlphaApplication.getBaseActivity().saveCurrentPlayingActionName("");
        getActivity().runOnUiThread(new Runnable() {
          @Override
           public void run() {
              mAdapter.notifyDataSetChanged();
              updateCircleButton();
          }
      });
    }

    @Override
    public void onPlaying() {

    }

    @Override
    public void onPausePlay() {

    }

    @Override
    public void onFinishPlay() {

    }

    @Override
    public void onFrameDo(int index) {

    }

    @Override
    public void notePlayChargingError() {

    }

    @Override
    public void notePlayCycleNext(String action_name) {
        if("初始化".equals(action_name) || "default".equals(action_name)){
            return;
        }

        UbtLog.d(TAG, "---notePlayCycleNext--" + action_name);
        currentCycleActionName = action_name;
        //modify False to true
//        isStartLooping = true;
        for(Map<String,Object> item:mDatas)
        {
            if(action_name.equals(item.get(MyActionsHelper.map_val_action_name)))
            {
                item.put(MyActionsHelper.map_val_action_is_playing,true);
            }else
                item.put(MyActionsHelper.map_val_action_is_playing,false);
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_title.setText(getResourceString("ui_action_cycle_name"));
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onNoteNoUser() {

    }

    @Override
    public void onNoteTooMore() {

    }

    @Override
    public void onReadImgFromCache(Bitmap img, long l) {

    }

    @Override
    public void onReadActionsFinish(List<String> names) {
        //机器人端读取到的数据，
        UbtLog.d(TAG,"onReadActionFinish");
        mDatas = mHelper.loadDatas();
        setDatas(mDatas);

//        sendMessage(1,UPDATE_VIEWS);
//        mActivity.setmInsideDatas(mDatas);
    }

    @Override
    public void onNoteVol(int vol_pow) {

    }

    @Override
    public void onNoteVolState(boolean vol_state) {

    }

    @Override
    public void onReadMyDownLoadHistory(String hashCode, List<ActionRecordInfo> history) {

    }

    @Override
    public void onSendFileStart() {

    }

    @Override
    public void onSendFileBusy() {

    }

    @Override
    public void onSendFileError() {

    }

    @Override
    public void onSendFileFinish(int pos) {

    }

    @Override
    public void onSendFileCancel() {

    }

    @Override
    public void onSendFileUpdateProgress(String progress) {

    }

    @Override
    public void noteLightOn() {

    }

    @Override
    public void noteLightOff() {

    }

    @Override
    public void noteChangeFinish(boolean b, String string) {

    }

    @Override
    public void noteTFPulled() {

    }

    @Override
    public void syncServerDataEnd(List<Map<String, Object>> data) {
        UbtLog.d(TAG, "wmma-syncServerDataEnd--" +data.size());
        //Deal Server Reply result
        setDatas(data);

    }

    @Override
    public void noteDeleteActionStart(int pos) {

    }

    @Override
    public void noteDeleteActionFinish(boolean isOk, String str) {

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

    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, State state) {

    }

    @Override
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {

    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo) {

    }

    @Override
    public void onGetFileLenth(long request_code, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(long request_code, State state) {

    }

    @Override
    public void onReportProgress(long request_code, double progess) {

    }

    @Override
    public void onDownLoadFileFinish(long request_code, State state) {

    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result, boolean result_state, long request_code) {

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result, long request_code) {

    }

    @Override
    public void onWriteDataFinish(long requestCode, FileTools.State state) {

    }

    @Override
    public void onReadCacheSize(int size) {

    }

    @Override
    public void onClearCache() {

    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {

    }

    @Override
    public void onChangeNewActionsFinish() {

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();

        void onShowLoopButton();

        void onHiddenLoopButton();
    }

    public class MyCircleHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl_info;
        public ImageView img_action_logo,img_select,img_pause,img_play;
        public TextView txt_action_name;
        private TextView txt_action_time;
        public ImageView gif;
        AnimationDrawable waveShapingAnim = null;
        public LinearLayout ll_select;
        public MyCircleHolder(View view) {
            super(view);
            img_action_logo = (ImageView) view.findViewById(R.id.action_logo);
            //图标右侧的选择CHECKBOX
            img_select = (ImageView) view.findViewById(R.id.img_select);
            //图标中的播放ICON
            img_play = (ImageView) view.findViewById(R.id.img_play);
            //图标中的暂停ICON
            img_pause = (ImageView) view.findViewById(R.id.img_pause);
            rl_info  = (RelativeLayout) view.findViewById(R.id.rl_logo_info);
            txt_action_name = (TextView) view.findViewById(R.id.txt_action_name);
            txt_action_time = (TextView) view.findViewById(R.id.txt_time_duration);
            //循环播放的时候在动作图标上的动画效果
            gif = (ImageView) view.findViewById(R.id.gif_playing);
            waveShapingAnim= (AnimationDrawable)gif.getBackground();
            ll_select = (LinearLayout)view.findViewById(R.id.ll_select);
        }

    }

    class ActionsCircleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;
        private View mView;


        public ActionsCircleAdapter(Context mContext, int type) {
            this.mContext = mContext;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.layout_myactions_circle_play_item, parent, false);
            MyCircleHolder myCircleHolder = new MyCircleHolder(mView);
            return myCircleHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder mHolder, final int position) {
           // UbtLog.d(TAG,"onBindViewHolder   "+mDatas.size());
            int imageLogo=R.drawable.sec_action_logo;
            final MyCircleHolder holder = (MyCircleHolder) mHolder;
            final Map<String, Object> actionList = mDatas.get(position);
            String action_name = actionList.get(ActionsLibHelper.map_val_action_name) + "";
            String action_time = actionList.get(ActionsLibHelper.map_val_action_time) + "";
            String action_headurl = actionList.get(ActionsLibHelper.map_val_action_type_logo_res) + "";
            String action_player_name=actionList.get(ActionsLibHelper.map_val_action) + "";
            RobotInnerActionInfoUtil.getImageIcon(action_player_name);
            if(!TextUtils.isEmpty(RobotInnerActionInfoUtil.getImageIcon(action_player_name))){
            if(RobotInnerActionInfoUtil.getImageIcon(action_player_name).equals("story")){
                imageLogo=R.drawable.ic_story;
            }else if(RobotInnerActionInfoUtil.getImageIcon(action_player_name).equals("dance")){
                imageLogo=R.drawable.ic_dance;
            }else if(RobotInnerActionInfoUtil.getImageIcon(action_player_name).equals("yoga")){
                imageLogo=R.drawable.ic_yoga;
            }else if(RobotInnerActionInfoUtil.getImageIcon(action_player_name).equals("sport")){
                imageLogo=R.drawable.ic_sport;
            }}else {
                imageLogo=R.drawable.sec_action_logo;
            }
            if(imageLogo!=R.drawable.sec_action_logo) {
                Glide.with(mContext)
                        .load(imageLogo)
                        .fitCenter()
                        .into(holder.img_action_logo);
            }else {
                Glide.with(mContext)
                        .load(action_headurl)
                        .fitCenter()
                        .into(holder.img_action_logo);
            }
            if (action_name.startsWith("@") || action_name.startsWith("#") || action_name.startsWith("%")) {
                action_name = action_name.substring(1);
            }
            holder.txt_action_name.setText(action_name);
            if(TextUtils.isEmpty(RobotInnerActionInfoUtil.getTime(action_player_name))){
                String action_time_adapter="未知";
                if(Integer.parseInt(action_time)<60){
                    if(Integer.parseInt(action_time)<10){
                        action_time_adapter = "0:0"+action_time;
                    }else {
                        action_time_adapter="0:"+action_time;
                    }
                }
                holder.txt_action_time.setText(action_time_adapter+"");
            }else {
                holder.txt_action_time.setText(RobotInnerActionInfoUtil.getTime(action_player_name));
            }
            for (int i = 0; i < MyActionsHelper.mCurrentSeletedNameList.size(); i++) {
                if (MyActionsHelper.mCurrentSeletedNameList.get(i).equals(action_player_name)) {
                    UbtLog.d(TAG, "onBindViewHolder current select is looping:" + isStartLooping + "name :" + action_name + "size" + MyActionsHelper.mCurrentSeletedNameList.size());
                    actionList.put(MyActionsHelper.map_val_action_selected, true);
                    break;
                }
            }
            if (AlphaApplication.getBaseActivity().readCurrentPlayingActionName().equals(action_player_name)) {
                UbtLog.d(TAG,"onBindViewHolder map_val_action_is_playing  "+ action_player_name);
                actionList.put(MyActionsHelper.map_val_action_is_playing, true);
            }

            if ((Boolean) actionList.get(MyActionsHelper.map_val_action_selected)) {
                holder.img_select.setImageResource(R.drawable.mynew_actions_selected);
            } else {
                holder.img_select.setImageResource(R.drawable.myactions_normal);
            }
            if (isStartLooping) {
                //循环播放的时候
                if (actionList.get(MyActionsHelper.map_val_action_is_playing) != null) {
                    if ((Boolean) actionList.get(MyActionsHelper.map_val_action_is_playing)) {
                        UbtLog.d(TAG,"onBindViewHolder animation "+ action_player_name);
                        holder.gif.setVisibility(View.VISIBLE);
                        holder.waveShapingAnim.setOneShot(false);
                        holder.waveShapingAnim.setVisible(true, true);
                        holder.waveShapingAnim.start();
                        holder.img_pause.setVisibility(View.INVISIBLE);
                        holder.img_play.setVisibility(View.INVISIBLE);
                    } else {
                        holder.gif.setVisibility(View.INVISIBLE);
                        holder.waveShapingAnim.setVisible(false, false);
                        holder.waveShapingAnim.stop();
                        holder.img_pause.setVisibility(View.INVISIBLE);
                        holder.img_play.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                //再次进入不停止开始
                if (mHelper.getCurrentPlayName().equals(action_name)) {
                    actionList.put(MyActionsHelper.map_val_action_is_playing, true);
                }
                //再次进入不停止结束
                if (actionList.get(MyActionsHelper.map_val_action_is_playing) != null) {
                    if ((Boolean) actionList.get(MyActionsHelper.map_val_action_is_playing)) {
                        holder.gif.setVisibility(View.INVISIBLE);
//                        stopWaveAnimation();
                        holder.waveShapingAnim.setVisible(false, false);
                        holder.waveShapingAnim.stop();
                        holder.rl_info.findViewById(R.id.img_pause).setVisibility(View.VISIBLE);
                        holder.rl_info.findViewById(R.id.img_play).setVisibility(View.INVISIBLE);
                    } else {
                        holder.gif.setVisibility(View.INVISIBLE);
//                        stopWaveAnimation();
                        holder.waveShapingAnim.setVisible(false, false);
                        holder.waveShapingAnim.stop();
                        holder.rl_info.findViewById(R.id.img_pause).setVisibility(View.INVISIBLE);
                        holder.rl_info.findViewById(R.id.img_play).setVisibility(View.VISIBLE);
                    }
                }

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionList.put(MyActionsHelper.map_val_action_selected, !(Boolean) actionList.get(MyActionsHelper.map_val_action_selected));
                        String actionName = (String) actionList.get(MyActionsHelper.map_val_action);
                        if (!MyActionsHelper.mCurrentSeletedNameList.contains(actionName)) {
                            MyActionsHelper.mCurrentSeletedNameList.add(actionName);
                            ActionInfo actionInfo = new ActionInfo();
                            actionInfo.actionName = (String) actionList.get(MyActionsHelper.map_val_action_name);
                            actionInfo.hts_file_name = (String) actionList.get(MyActionsHelper.map_val_action);
                            actionInfo.actionSize = position;//zan cun
                            MyActionsHelper.mCurrentSeletedActionInfoMap.put(actionName, actionInfo);
                            UbtLog.d(TAG, "lihai------actionName->" + actionName + "----position->" + position + "--" + actionList.get(MyActionsHelper.map_val_action));
                        } else {
                            MyActionsHelper.mCurrentSeletedNameList.remove(actionName);
                            MyActionsHelper.mCurrentSeletedActionInfoMap.remove(actionName);
                        }
                        //循环播放的控制按钮
                        UbtLog.d(TAG, "MyActionsHelper.mCurrentSeletedNameList.size() = " + MyActionsHelper.mCurrentSeletedNameList.size());
                        mAdapter.notifyItemChanged(position);
                        updateCircleButton();
                    }
                };
                //动作播放的CHECKBOX点击事件
                holder.ll_select.setOnClickListener(listener);

                View.OnClickListener actioniconlistener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isStartLooping) {
                            if(lowBatteryNotExecutedAction()){
                                return;
                            };
                            for (int i = 0; i < mDatas.size(); i++) {
                                if (i != position) {
                                    mDatas.get(i).put(MyActionsHelper.map_val_action_is_playing, false);
                                }
                            }
                            actionList.put(MyActionsHelper.map_val_action_is_playing, !(Boolean) actionList.get(MyActionsHelper.map_val_action_is_playing));
                            String actionName = (String) actionList.get(MyActionsHelper.map_val_action_name);

                            if ((Boolean) actionList.get(MyActionsHelper.map_val_action_is_playing)) {
                                ActionInfo actionInfo = new ActionInfo();
                                actionInfo.actionName = actionName;
                                actionInfo.hts_file_name = (String) actionList.get(MyActionsHelper.map_val_action);
                                actionInfo.actionSize = position;//zan cun
                                recoveryPlayType();

                                if (position < MyActionsHelper.localSize) {
                                    MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.Unkown;
                                } else {
                                    MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_download_local;
                                }

                                mHelper.doPlay(actionInfo);
                                UbtLog.d(TAG, "REFACTOR lihai------actionName->" + actionName + "----position->" + position + "--" + actionList.get(MyActionsHelper.map_val_action));
                            } else {
                                mHelper.stopPlayAction();
                            }
                            mAdapter.notifyItemChanged(position);
                        }
                    }
                };
                //动作的图片点击事件
                holder.rl_info.findViewById(R.id.action_logo).setOnClickListener(actioniconlistener);
            }

        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }


    }


    private void debugActionList(List<Map<String, Object>> list) {
        for ( int i = 0 ; i < list.size() - 1 ; i ++ ) {
                UbtLog.d(TAG,"INDEX "+list.get(i).get(ActionsHelper.map_val_action_name));
        }

        System.out.println(list);
    }



    /**
     *  防止进入遥控器后，状态没有复位，导致动作播放的路径不对
     */
    private void recoveryPlayType(){
        MyActionsHelper.mCurrentLocalPlayType=null;
    }

    /**
     * 循环播放按钮刷新
     */
    private void updateCircleButton(){
        //刚进入列表的时候，循环播放按钮状态，退出播放列表后，还能够继续播放开始
        if (isStartLooping) {
            //正在播放，并且大小大于1
            if (MyActionsHelper.mCurrentSeletedActionInfoMap.size() >= 1) {
                ivCircle.setImageDrawable(mActivity.getDrawableRes("ic_circle_stop"));
                ivCircle.setAlpha(1.0f);
                tvCircle.setText("停止播放");
                tvCircle.setAlpha(1.0f);
            }else {
                //没播放，大小小于1
                ivCircle.setImageDrawable(mActivity.getDrawableRes("ic_circle_stop"));
                ivCircle.setAlpha(0.5f);
                tvCircle.setText("循环播放");
                tvCircle.setAlpha(0.5f);
            }
        }else {
            //HIGHLIGHT
            if (MyActionsHelper.mCurrentSeletedNameList.size() >=1) {
                ivCircle.setImageDrawable(mActivity.getDrawableRes("ic_circle_play"));
                ivCircle.setAlpha(1.0f);
                tvCircle.setText("循环播放");
                tvCircle.setAlpha(1.0f);
            }else{
                //MASK
                ivCircle.setImageDrawable(mActivity.getDrawableRes("ic_circle_play"));
                ivCircle.setAlpha(0.5f);
                tvCircle.setText("循环播放");
                tvCircle.setAlpha(0.5f);
            }
        }
    }

    /**
     * @param status
     *  status=true ：进入循环播放
     *  status=false ：单独动作播放
     */
    private void setActionPlayType(boolean status ){
        isStartLooping = status;
        mHelper.setLooping(status);
    }
    private boolean getActionPlayType(){
        return isStartLooping;
    }
    public boolean isBulueToothConnected() {

        if (((AlphaApplication) getActivity().getApplicationContext())
                .getCurrentBluetooth() == null) {
            return false;
        } else {
            return true;
        }
    }

    //显示蓝牙连接对话框
    void showBluetoothConnectDialog() {
        new ConfirmDialog(getContext()).builder()
                .setTitle("提示")
                .setMsg("请先连接蓝牙和Wi-Fi")
                .setCancelable(true)
                .show();
    }
//    private void startWaveAnimation(){
//        UbtLog.d(TAG,"startWaveAnimation");
//        holder.waveShapingAnim.setOneShot(false);
//        waveShapingAnim.setVisible(true,true);
//        waveShapingAnim.start();
//
//    }
//    private void stopWaveAnimation(){
//        UbtLog.d(TAG,"stopWaveAnimation");
//        if(waveShapingAnim!=null) {
//            waveShapingAnim.setVisible(false,false);
//            waveShapingAnim.stop();
//        }
//    }


    @Subscribe
    public void onEventRobot(RobotEvent event) {
        if (event.getEvent() == RobotEvent.Event.HIBITS_PROCESS_STATUS) {
            //流程开始，收到行为提醒状态改变，开始则退出流程，并Toast提示
            UbtLog.d(TAG, "onEventRobot = obj == 2" + event.isHibitsProcessStatus());
            if (event.isHibitsProcessStatus() && !isShowHibitsDialog) {
                UbtLog.d(TAG, "onEventRobot = obj == 3");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new ConfirmDialog(mActivity).builder()
                                .setTitle("提示")
                                .setMsg(mActivity.getStringResources("ui_habits_process_start"))
                                .setCancelable(false)
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UbtLog.d(TAG, "确定");
                                        if(mActivity != null){
                                            mActivity.finish();
                                        }
                                    }
                                }).show();
                    }
                });
                //行为习惯流程未结束，退出当前流程
            }
        }else if(event.getEvent() == RobotEvent.Event.DISCONNECT){
            UbtLog.d(TAG,"DISCONNECT THE BLUETOOTH");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setActionPlayType(false);
                    showBluetoothDisconnect();
                }
            });

        }
    }
    void showBluetoothDisconnect() {
        ConfirmDialog dialog = null;
        dialog = new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
                .setTitle("提示")
                .setMsg("蓝牙连接断开，请重新连接")
                .setCancelable(true)
                .setPositiveButton("去连接", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "去连接蓝牙 ");
                        gotoConnectBluetooth();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UbtLog.d(TAG, "取消 ");
                        AppManager.getInstance().finishUseBluetoothActivity();
                    }
                });
        dialog.show();
    }
    //去连接蓝牙
    void gotoConnectBluetooth() {
        boolean isfirst = SPUtils.getInstance().getBoolean("firstBluetoothConnect", true);
        Intent bluetoothConnectIntent = new Intent();
        if (isfirst) {
            UbtLog.d(TAG, "第一次蓝牙连接");
            SPUtils.getInstance().put("firstBluetoothConnect", false);
            bluetoothConnectIntent.setClass(AppManager.getInstance().currentActivity(), BluetoothguidestartrobotActivity.class);
        } else {
            bluetoothConnectIntent.setClass(AppManager.getInstance().currentActivity(), BluetoothandnetconnectstateActivity.class);
        }
        startActivityForResult(bluetoothConnectIntent, 100);

        if (AppManager.getInstance().currentActivity() != null
                && (AppManager.getInstance().currentActivity() instanceof PrincipleActivity
                || AppManager.getInstance().currentActivity() instanceof SplitActivity
                || AppManager.getInstance().currentActivity() instanceof MergeActivity
                || AppManager.getInstance().currentActivity() instanceof FeatureActivity)) {
            UbtLog.d(TAG, "有需要关闭的课程界面 ");
            AlphaApplication.setmNeedOpenActivity(AppManager.getInstance().currentActivity().getClass().getSimpleName());
            AppManager.getInstance().currentActivity().finish();
        }
        getActivity().overridePendingTransition(R.anim.activity_open_up_down, 0);
    }
  private boolean  lowBatteryNotExecutedAction(){
      if(BaseHelper.isLowBatteryNotExecuteAction){
          new ConfirmDialog(AppManager.getInstance().currentActivity()).builder()
                  .setTitle("提示")
                 // .setMsg("机器人电量低不能够执行动作，请充电！")
                  .setMsg("电量低于5%,请充电后播放动作!")
                  .setCancelable(true)
                  .setPositiveButton("确定", new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          UbtLog.d(TAG, "确定 ");
                          getActivity().finish();
                      }
                  }).show();
      }
      return BaseHelper.isLowBatteryNotExecuteAction;
  }




}
