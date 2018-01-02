package com.ubt.alpha1e.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.MyActionsActivity;
import com.ubt.alpha1e.ui.helper.ActionsHelper;
import com.ubt.alpha1e.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.ui.helper.IActionsUI;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

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
    private boolean isStartLooping = false;

    private MyActionsHelper mHelper;
    private String currentCycleActionName;
    private TextView txt_title;


    public MyActionsCircleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_actions_circle, container, false);
        initViews();
        return mView;
    }

    private void initViews()
    {
        txt_title = (TextView) mView.findViewById(R.id.txt_base_title_name);
        txt_title.setText(getResourceString("ui_cycle_title"));
        ImageView img_cancel = (ImageView) mView.findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
//                onButtonPressed();
            }
        });
        mSyncRecyclerview = (RecyclerView) mView.findViewById(R.id.recyclerview_circle);
       // mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mSyncRecyclerview.setLayoutManager(gridLayoutManager);
        RecyclerView.ItemAnimator animator = mSyncRecyclerview.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        mSyncRecyclerview.getItemAnimator().setSupportsChangeAnimations(false);
        mAdapter = new ActionsCircleAdapter(getActivity(),type);
        mSyncRecyclerview.setAdapter(mAdapter);
        if(mHelper!=null) {
            mHelper.stopPlayAction();
        }
        MyActionsHelper.mCurrentSeletedNameList.clear();
        MyActionsHelper.mCurrentSeletedActionInfoMap.clear();

    }

    public void setDatas(List<Map<String,Object>> datas)
    {
        mDatas = datas;
        removeDuplicate(mDatas);
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
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
        UbtLog.d("wilson","MyActionsCircleFragment----onResume--");
        if(isStartLooping){
            if(mListener!=null) mListener.onHiddenLoopButton();
        }
//        mHelper.stopPlayAction();

        if(isStartLooping ){
            List<Map<String,Object>> playActionMap = new ArrayList<>();
            for(Map<String,Object> actionMap : mDatas){
                UbtLog.e(TAG, "mInsideDatas size=" + mDatas.size());
                if(MyActionsHelper.mCurrentSeletedActionInfoMap.get(actionMap.get(MyActionsHelper.map_val_action_name)) != null){
                    if(actionMap.get(ActionsHelper.map_val_action_name).equals(currentCycleActionName)){
                        actionMap.put(MyActionsHelper.map_val_action_is_playing,true);
                    }else{
                        actionMap.put(MyActionsHelper.map_val_action_is_playing,false);
                    }

                    actionMap.put(MyActionsHelper.map_val_action_selected,true);
                    playActionMap.add(actionMap);
                }
            }
            setDatas(playActionMap);
            if(mListener!=null) mListener.onHiddenLoopButton();
        }


//        mDatas = mActivity.mInsideDatas;
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
        AlphaApplication.setCycleFragmentShow(false);
        if(mHelper!= null){
            mHelper.unRegisterListeners(this);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
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
        if(hidden)
        {
            if(mHelper!=null)
                mHelper.removePlayerListeners(this);

        }else
        {
            if(mHelper!=null)
            {
                mHelper.addPlayerListeners(this);
                if(mHelper.getPlayerState()== ActionPlayer.Play_state.action_playing&& mHelper.getPlayerType() == ActionPlayer.Play_type.cycle_action)
                {
                    if(mListener!=null) mListener.onHiddenLoopButton();
                    isStartLooping = true;
                }else
                {
                    if(mListener!=null) mListener.onShowLoopButton();
                    isStartLooping = false;
                    MyActionsHelper.mCurrentSeletedNameList.clear();
                    MyActionsHelper.mCurrentSeletedActionInfoMap.clear();
                    for(Map<String,Object> item:mDatas)
                    {
                        item.put(MyActionsHelper.map_val_action_is_playing,false);
                        item.put(MyActionsHelper.map_val_action_selected,false);
                    }
                }
            }

        }
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
            if(mListener!=null)
                mListener.onHiddenLoopButton();
            for(String item:mSourceActionNameList)
            {
                for(Map<String,Object> map:mDatas)
                {
                    if(item.equals(map.get(MyActionsHelper.map_val_action_name)))
                    {
                        map.put(MyActionsHelper.map_val_action_is_playing,true);
                    }
                }
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

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {
        UbtLog.d(TAG, "notePlayFinish clear data");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_title.setText(getResourceString("ui_cycle_title"));
            }
        });

        if(!isStartLooping){
            if(mListener!=null)
            {  UbtLog.d(TAG, "notePlayFinish clear data mListener");
                MyActionsHelper.mCurrentSeletedNameList.clear();
                MyActionsHelper.mCurrentSeletedActionInfoMap.clear();
                mListener.onShowLoopButton();
            }
            for(Map<String,Object> item:mDatas)
            {
                item.put(MyActionsHelper.map_val_action_is_playing,false);
                item.put(MyActionsHelper.map_val_action_selected,false);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
//                    if(mListener!=null)
//                    {
//                        mListener.onShowLoopButton();
//                    }
                }
            });

            return;
        }
        if (mCurrentPlayType == ActionPlayer.Play_type.cycle_action) {
            UbtLog.d(TAG, "---show toast  notePlayFinish cycle !");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getActivity(), mActivity.getStringResources("ui_action_cycle_stop"), Toast.LENGTH_SHORT).show();
                }
            });
        }

        isStartLooping = false;
        MyActionsHelper.mCurrentSeletedNameList.clear();
        MyActionsHelper.mCurrentSeletedActionInfoMap.clear();
        for(Map<String,Object> item:mDatas)
        {
            item.put(MyActionsHelper.map_val_action_is_playing,false);
            item.put(MyActionsHelper.map_val_action_selected,false);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                if(mListener!=null)
                {
                    mListener.onShowLoopButton();
                }
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

        UbtLog.d(TAG, "---notePlayCycleNext--" + action_name);
        currentCycleActionName = action_name;
        isStartLooping = true;
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

    class ActionsCircleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;
        private View mView;
        private int type = -1;


        public ActionsCircleAdapter(Context mContext,int type) {
            this.mContext = mContext;
            this.type = type;
        }


        public class MyCircleHolder extends RecyclerView.ViewHolder {

            public RelativeLayout rl_info,rl_state;
            public ImageView img_action_logo, img_type_logo,img_select,img_state;
            public TextView txt_action_name, txt_time, txt_des, txt_type_des;
            public GifImageView gif;
            public LinearLayout layout_img_select;
            public MyCircleHolder(View view) {
                super(view);
                img_action_logo = (ImageView) view.findViewById(R.id.action_logo);
                img_type_logo = (ImageView) view.findViewById(R.id.img_type_logo);
                img_select = (ImageView) view.findViewById(R.id.img_select);
                img_state = (ImageView) view.findViewById(R.id.img_state);
                rl_info  = (RelativeLayout) view.findViewById(R.id.rl_logo_info);
                txt_action_name = (TextView) view.findViewById(R.id.txt_action_name);
                txt_time = (TextView) view.findViewById(R.id.txt_time);
                txt_des = (TextView) view.findViewById(R.id.txt_disc);
                txt_type_des = (TextView) view.findViewById(R.id.txt_type_des);
                gif = (GifImageView) view
                        .findViewById(R.id.gif_playing);
                layout_img_select = (LinearLayout)view.findViewById(R.id.layout_img_select);
                rl_state = (RelativeLayout) view.findViewById(R.id.lay_state);
            }

        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mView =LayoutInflater.from(mContext).inflate(R.layout.layout_myactions_sync_item, parent, false);
            MyCircleHolder myCircleHolder = new MyCircleHolder(mView);
            return myCircleHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder mHolder, final int position) {
            MyCircleHolder holder = (MyCircleHolder)mHolder;
            final Map<String,Object> actionList =mDatas.get(position);
            Glide.with(mContext)
                    .load(R.drawable.sec_action_logo)
                    .fitCenter()
                    .into(holder.img_action_logo);

            String action_name = actionList.get(ActionsLibHelper.map_val_action_name) + "";
            if(action_name.startsWith("@") || action_name.startsWith("#") || action_name.startsWith("%")){
                action_name = action_name.substring(1);
            }

            holder.txt_action_name.setText(action_name);
            holder.txt_des.setText(actionList.get(ActionsLibHelper.map_val_action_disc) + "");
            holder.img_type_logo.setImageResource((int) actionList.get(ActionsLibHelper.map_val_action_type_logo_res));
            holder.txt_type_des.setText(actionList.get(MyActionsHelper.map_val_action_type_name) + "");
            holder.txt_time.setText(actionList.get(ActionsLibHelper.map_val_action_time) + "");
            UbtLog.d(TAG, "-->isStartLooping=" + isStartLooping);
            if(isStartLooping)
            {
                holder.layout_img_select.setVisibility(View.GONE);
                holder.rl_state.setVisibility(View.VISIBLE);
                if(actionList.get(MyActionsHelper.map_val_action_is_playing)!=null)
                {
                    if ((Boolean) actionList.get(MyActionsHelper.map_val_action_is_playing)) {
                        holder.gif.setVisibility(View.VISIBLE);
                        holder.img_state.setVisibility(View.INVISIBLE);
                    } else {
                        holder.gif.setVisibility(View.INVISIBLE);
                        holder.img_state.setVisibility(View.VISIBLE);
                    }
                }
            }else
            {
                UbtLog.d(TAG, "is playing= " +  actionList.get(MyActionsHelper.map_val_action_is_playing));
                holder.layout_img_select.setVisibility(View.VISIBLE);
                holder.rl_state.setVisibility(View.GONE);
                if((Boolean) actionList.get(MyActionsHelper.map_val_action_selected))
                {
                    holder.img_select.setImageResource(R.drawable.mynew_actions_selected);

                }else {
                    holder.img_select.setImageResource(R.drawable.myactions_normal);

                }
                View.OnClickListener listener  = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionList.put(MyActionsHelper.map_val_action_selected,!(Boolean) actionList.get (MyActionsHelper.map_val_action_selected));
                        mAdapter.notifyItemChanged(position);
                        String actionName = (String)actionList.get(MyActionsHelper.map_val_action_name);
                        if(!MyActionsHelper.mCurrentSeletedNameList.contains(actionName))
                        {
                            MyActionsHelper.mCurrentSeletedNameList.add(actionName);
                            ActionInfo actionInfo = new ActionInfo();
                            actionInfo.actionName = actionName;
                            actionInfo.hts_file_name = (String)actionList.get(MyActionsHelper.map_val_action);
                            actionInfo.actionSize = position;//zan cun
                            MyActionsHelper.mCurrentSeletedActionInfoMap.put(actionName,actionInfo);

                            UbtLog.d(TAG,"lihai------actionName->"+actionName+"----position->"+position+"--"+actionList.get(MyActionsHelper.map_val_action));
                        }else{
                            MyActionsHelper.mCurrentSeletedNameList.remove(actionName);
                            MyActionsHelper.mCurrentSeletedActionInfoMap.remove(actionName);
                        }
                    }
                };
                holder.layout_img_select.setOnClickListener(listener);
                holder.rl_info.setOnClickListener(listener);
            }


        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }


    }


    private void removeDuplicate(List<Map<String, Object>> list) {
        UbtLog.d(TAG, "removeDuplicate");
        for ( int i = 0 ; i < list.size() - 1 ; i ++ ) {
            for ( int j = list.size() - 1 ; j > i; j -- ) {
                if (list.get(j).get(ActionsHelper.map_val_action_name).equals(list.get(i).get(ActionsHelper.map_val_action_name))) {
                    UbtLog.d(TAG, "removeDuplicate=" + list.get(j).toString());
                    list.remove(j);
                }
            }
        }
        System.out.println(list);
    }


}
