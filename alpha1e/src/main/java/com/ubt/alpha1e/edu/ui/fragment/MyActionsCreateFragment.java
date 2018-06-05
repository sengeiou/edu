package com.ubt.alpha1e.edu.ui.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.adapter.FillLocalContent;
import com.ubt.alpha1e.edu.business.ActionPlayer;
import com.ubt.alpha1e.edu.data.Constant;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.model.ActionColloInfo;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;
import com.ubt.alpha1e.edu.ui.ActionsNewEditActivity;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.LoginActivity;
import com.ubt.alpha1e.edu.ui.MyActionsSyncActivity;
import com.ubt.alpha1e.edu.ui.RobotConnectedActivity;
import com.ubt.alpha1e.edu.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.edu.ui.helper.ActionsHelper;
import com.ubt.alpha1e.edu.ui.helper.IActionsUI;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.List;
import java.util.Map;

public class MyActionsCreateFragment extends BaseMyActionsFragment implements IActionsUI {

    private static final String TAG = "MyActionsCreateFragment";
    private static final String TYPE = "type";
    private int type = -1;


    public MyActionsCreateFragment() {
        // Required empty public constructor
    }

    public static MyActionsCreateFragment newInstance(int pos)
    {
        MyActionsCreateFragment fragment = new MyActionsCreateFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, pos);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE);//类型
            super.type = type;
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        UbtLog.d(TAG,"----MyActionsCreateFragment----onResume------");
//        mHelper.registerListeners(this);
        mHelper.setManagerListeners(mActivity);
        mHelper.registerListeners(this);
        if(((BaseActivity)mActivity).isBulueToothConnected())
        {
            mHelper.initActionPlayer(this);
            mHelper.initNewActionPlayer(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mHelper!=null){
            mHelper.unRegisterListeners(this);
        }
//        mHelper.removePlayerListeners(this);
    }

    @Override
    public void onDestroy() {
        UbtLog.d(TAG, "onDestroy");
        super.onDestroy();
        if(mHelper!=null){
            mHelper.unRegisterListeners(this);
        }
        mHelper.removePlayerListeners(this);
    }

    @Override
    public void initDatas(Activity activity) {
        UbtLog.d(TAG, "initDatas");
        mHelper = MyActionsHelper.getInstance(mActivity);
        mHelper.setManagerListeners(mActivity);
        mHelper.registerListeners(this);

    }
    @Override
    public void firstLoadData() {
        if(isLogin(getActivity()) && (mActivity.fragment instanceof MyActionsCreateFragment) && !AlphaApplication.isCycleActionFragment())
        {
            UbtLog.e(TAG,"----firstLoadData MyActionsCreateFragment");
            showLoadingDialog();
            if(((BaseActivity)mActivity).isBulueToothConnected() /*&& !AlphaApplication.isCycleActionFragment()*/){
                if(mActivity.mInsideDatas.size() == 0){
                    UbtLog.e(TAG, "firstLoadData mActivity.mInsideDatas.size() == 0");
                    mHelper.doReadActions();
                }else {
                    UbtLog.e(TAG, "firstLoadData mActivity.mInsideDatas.size() != 0");
                    mHelper.doReadMyNewAction();
                }
            }else{
                UbtLog.e(TAG, "firstLoadData doReadMyNewAction");
                mHelper.doReadMyNewAction();
            }
        }

    }


    @Override
    public void updateViews(boolean isLogin, int hasData) {

        if(!isLogin)
        {
            ViewStub viewStub = (ViewStub)mView.findViewById(R.id.empty_viewstub);
            if(viewStub!=null)
            {
                mEmptyView = viewStub.inflate();
                ((TextView)mEmptyView.findViewById(R.id.txt_unlogin)).setText(getResourceString("ui_myaction_collection_need_login"));
                Button btn_login = (Button)mEmptyView.findViewById(R.id.btn_login);
                btn_login.setText(getResourceString("ui_guide_login"));
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoginActivity.launchActivity(mActivity,true,12306);
                    }
                });
            }
        }else
        {
            if(hasData==0)
            {
                if(mEmptyView!=null)
                {
                    mEmptyView.setVisibility(View.VISIBLE);
                }else
                {
                    ViewStub viewStub = (ViewStub)mView.findViewById(R.id.empty_viewstub);
                    if(viewStub!=null)
                    {
                        mEmptyView = viewStub.inflate();
                    }
                }
                ((TextView)mEmptyView.findViewById(R.id.txt_unlogin)).setText(getResourceString("ui_myaction_creation_empty"));
                Button btn_login = (Button)mEmptyView.findViewById(R.id.btn_login);
                Button btn_check = (Button)mEmptyView.findViewById(R.id.btn_check_list);
                btn_check.setText(getResourceString("ui_myaction_view_creation_history"));
                btn_login.setText(getResourceString("ui_myaction_goto_create"));
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mHelper.isLostCoon()) {
                            RobotConnectedActivity.launchActivity(mActivity,true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                            return;
                        }
//                        ActionsEditActivity.launchActivity(mActivity, ActionsEditHelper.StartType.new_type,null,12306,
//                                mHelper.getSchemeId(),mHelper.getSchemeName());
                        ActionsNewEditActivity.launchActivity(mActivity, ActionsEditHelper.StartType.new_type,null,12306,
                                mHelper.getSchemeId(),mHelper.getSchemeName());

                    }
                });
                btn_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyActionsSyncActivity.launchActivity(getActivity(), FillLocalContent.CREATE_ACTIONS,10010);
                    }
                });
            }else if(hasData ==1)
            {
                View view = mView.findViewById(R.id.sync_download_viewstub);

                if(view!=null)
                {
                    TextView txt = (TextView) view.findViewById(R.id.txt_sync);
                    txt.setText(getResourceString("ui_myaction_synchronize_creation_history"));
                    view.setVisibility(View.VISIBLE);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyActionsSyncActivity.launchActivity(getActivity(), FillLocalContent.CREATE_ACTIONS,10010);
                        }
                    });
                }
                if(mEmptyView!=null)
                {
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 从详情点击播放跳转回来调用
     * 根据actionId播放动作
     * @param actionId
     */
    public void playActionById(long actionId){
        for(int i = 0; i < mDatas.size();i++ ){
            if(actionId == ((ActionInfo)mDatas.get(i).get(ActionsHelper.map_val_action)).actionId){
                FillLocalContent.playCreateAction(getActivity(),mDatas.get(i),mHelper,i);
                break;
            }
        }
    }

    @Override
    public void clearDatas() {

    }

    @Override
    public void onStartPlay() {

    }

    @Override
    public void onStopPlay() {
        mHelper.stopPlayAction();
    }

    @Override
    public void OnStartLoop() {

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
        mActivity.setmInsideDatas(mHelper.loadDatas());
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
//        sendMessage(getActivity().getString(R.string.ui_remote_select_robot_synchoronizing),SHOW_PROGRESS_DIALOG);
    }

    @Override
    public void onSendFileBusy() {
//        Toast.makeText(mActivity, mActivity.getString(R.string.ui_remote_synchoronize_busy), Toast.LENGTH_SHORT)
//                .show();
//        mHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
    }

    @Override
    public void onSendFileError() {
//        Toast.makeText(mActivity, mActivity.getString(R.string.ui_remote_select_robot_synchoronize_failed), Toast.LENGTH_SHORT)
//                .show();
//        mHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
    }

    @Override
    public void onSendFileFinish(int pos) {//pos 当前将要播放的index
//        Toast.makeText(mActivity, mActivity.getString(R.string.ui_remote_select_robot_synchoronize_success),
//                Toast.LENGTH_SHORT).show();
//        mHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
//        mHelper.startPlayAction(mDatas.get(pos),pos,MyActionsHelper.Action_type.My_new);

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
        mActivity.setmInsideDatas(data);
        mHelper.doReadMyNewAction();
    }

    @Override
    public void noteDeleteActionStart(int pos) {

    }

    @Override
    public void noteDeleteActionFinish(boolean isOk, String str) {

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {
        if(!(mActivity.fragment instanceof MyActionsCreateFragment) || action == null ){
            return;
        }

        int pos = 0;
        for(int i=0;i<mDatas.size();i++){
            Map<String,Object> map = mDatas.get(i);
            if(map == null){
                continue;
            }
//            UbtLog.d(TAG, "map.get=" + map.get(ActionsHelper.map_val_action));
            if(!map.get(ActionsHelper.map_val_action).toString().startsWith("ActionInfo")){
                return;
            }
            if(((ActionInfo)map.get(ActionsHelper.map_val_action)).actionOriginalId == action.actionOriginalId)
            {
                UbtLog.d(TAG, "action.actionId=" + action.actionId);
                map.put(ActionsHelper.map_val_action_is_playing,true);
                pos = i;
                break;
            }else{
                map.put(ActionsHelper.map_val_action_is_playing,false);
            }
        }
        sendMessage(pos,UPDATE_ITEMS);
        mHelper.doPlayMp3ForMyCreate(action);
    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "---wmma notePlayPause" + (mActivity.fragment instanceof MyActionsCreateFragment));
        if(!(mActivity.fragment instanceof MyActionsCreateFragment)  || mCurrentPlayType.equals(ActionPlayer.Play_type.cycle_action)){
            return;
        }
        int pos = mHelper.getCurrentPlayPosition(mDatas);
        String name = mHelper.getCurrentPlayName();

        for(int i = 0;i<mDatas.size();i++)
        {
            Map<String,Object> map = mDatas.get(i);
            if(map == null){
                //处理添加动作按钮
                continue;
            }
            long id = ((ActionInfo)map.get(ActionsHelper.map_val_action)).actionOriginalId;
            if(String.valueOf(id).equals(name))
            {
                map.put(ActionsHelper.map_val_action_is_playing,false);
                pos = i;

                break;
            }
        }
        sendMessage(pos,UPDATE_ITEMS);
    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "---wmma notePlayContinue");
        if(!(mActivity.fragment instanceof MyActionsCreateFragment) || mCurrentPlayType.equals(ActionPlayer.Play_type.cycle_action) ){
            return;
        }
        int pos = mHelper.getCurrentPlayPosition(mDatas);
        String name = mHelper.getCurrentPlayName();

        for(int i = 0;i<mDatas.size();i++)
        {
            Map<String,Object> map = mDatas.get(i);
            if(map == null){
                //处理添加动作按钮
                continue;
            }
            long id = ((ActionInfo)map.get(ActionsHelper.map_val_action)).actionOriginalId;
            if(String.valueOf(id).equals(name))
            {
                map.put(ActionsHelper.map_val_action_is_playing,true);
                pos = i;

                break;
            }
        }
        sendMessage(pos,UPDATE_ITEMS);
    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {

        if(!(mActivity.fragment instanceof MyActionsCreateFragment)){
            return;
        }
        mHelper.doStopMp3ForMyDownload();
        int pos = mHelper.getCurrentPlayPosition(mDatas);
        if(pos != -1){
            sendMessage(pos,UPDATE_ITEMS);
        }
    }

    @Override
    public void onPlaying() {
        UbtLog.d(TAG, "---onPlaying");
   /*     mHandler.post(new Runnable() {

            @Override
            public void run() {
                try {

                    String name = ((MyActionsHelper) mHelper).getNewPlayerName();
                    long actionId = ((MyActionsHelper) mHelper).getNewPlayerActionId();

                        for(int i=0; i< mDatas.size();i++){
                            if(mDatas.get(i) != null){
                                NewActionInfo actionInfo = (NewActionInfo)mDatas.get(i).get(MyActionsHelper.map_val_action);
                                if(actionId == actionInfo.actionId){
                                    mDatas.get(i).put(MyActionsHelper.map_val_action_is_playing,true);
                                    sendMessage(i,UPDATE_ITEMS);
                                    break;
                                }
                            }
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    @Override
    public void onPausePlay() {
        UbtLog.d(TAG, "onPausePlay");

    }

    @Override
    public void onFinishPlay() {
        UbtLog.d(TAG, "onFinishPlay");
    }

    @Override
    public void onFrameDo(int index) {

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
        UbtLog.d(TAG, "onReadImageFinish");
    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result, boolean result_state, long request_code) {
        UbtLog.d(TAG, "onReadFileStrFinish");
    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result, long request_code) {
        UbtLog.d(TAG, "onWriteFileStrFinish");
    }

    @Override
    public void onWriteDataFinish(long requestCode, FileTools.State state) {
        UbtLog.d(TAG, "onWriteDataFinish");
    }

    @Override
    public void onReadCacheSize(int size) {
        UbtLog.d(TAG, "onReadCacheSize=" + size);

    }

    @Override
    public void onClearCache() {
        UbtLog.d(TAG, "onClaerCache");
    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

    }

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {
        UbtLog.d(TAG, "onReadNewActionsFinish");
        isLoadData = true;
        if(actions.size()==0)
        {
            sendMessage(0,UPDATE_VIEWS);
        }else
        {
            mDatas =  mHelper.loadCreatedDatas(actions,mActivity.mInsideDatas);
            if(mHelper.getPlayerState() == ActionPlayer.Play_state.action_playing){
                for(int i=0;i<mDatas.size();i++){
                    Map<String,Object> map = mDatas.get(i);
                    UbtLog.d(TAG, "actionName=" +((ActionInfo)map.get(ActionsHelper.map_val_action)) +
                            "mHelper.getCurrentPlayName()=" + mHelper.getCurrentPlayName());
                    if(((ActionInfo)map.get(ActionsHelper.map_val_action)).actionOriginalId == mHelper.getPlayerActionId())
                    {
                        UbtLog.d(TAG, "actionName= set action playing state true getPlayerActionId=" + mHelper.getPlayerActionId());
                        map.put(ActionsHelper.map_val_action_is_playing,true);
                    }else{
                        map.put(ActionsHelper.map_val_action_is_playing,false);
                    }
                }

            }
            mDatas.add(null);
            mAdapter.setData(mDatas);
            sendMessage(1,UPDATE_VIEWS);
        }
    }

    @Override
    public void onChangeNewActionsFinish() {
        //我的创建写文件成功
//         firstLoadData();
    }
}
