package com.ubt.alpha1e.ui.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.MyActionsActivity;
import com.ubt.alpha1e.ui.RobotConnectedActivity;
import com.ubt.alpha1e.ui.helper.ActionsHelper;
import com.ubt.alpha1e.ui.helper.IActionsUI;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.utils.log.MyLog;

import java.util.List;
import java.util.Map;

/**
 */
public class MyActionsLocalFragment extends BaseMyActionsFragment implements IActionsUI {
    private static final String TYPE = "type";
    private static final String TAG = "MyActionsLocalFragment";

    public MyActionsLocalFragment() {
        // Required empty public constructor
    }

    public static MyActionsLocalFragment newInstance(int pos)
    {
        MyActionsLocalFragment fragment = new MyActionsLocalFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        UbtLog.d("wilson","MyActionsLocalFragment----hidden-"+hidden);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UbtLog.d(TAG,"---MyActionsLocalFragment-----onCreate------");
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE);//类型
        }
    }


    @Override
    public void initDatas(Activity activity) {
        UbtLog.d(TAG,"---MyActionsLocalFragment-----initDatas------");
        mHelper = MyActionsHelper.getInstance(mActivity);
        mHelper.registerListeners(this);
        mActivity.mHelper = mHelper;
    }


    @Override
    public void firstLoadData() {
        Log.e("wilson$$$","----firstLoadData----local----");
        mHelper.registerListeners(this);
        if(mActivity.isBulueToothConnected() && (mActivity.fragment instanceof MyActionsLocalFragment))
        {
            UbtLog.e(TAG, "--firstLoadData MyActionsLocalFragment");
             mHelper.initActionPlayer(this);
             mHelper.initNewActionPlayer(this);
             showLoadingDialog();
             mHelper.doReadActions();
        }
    }

    @Override
    public void updateViews(boolean isLogin, int hasData) {

        if( mHelper.isLostCoon())
        {
            ViewStub viewStub = (ViewStub)mView.findViewById(R.id.empty_viewstub);
            if(viewStub!=null)
            {
                mEmptyView = viewStub.inflate();
                ((TextView)mEmptyView.findViewById(R.id.txt_unlogin)).setText(getResourceString("ui_connect_robot_tips"));
                Button btn_login = (Button)mEmptyView.findViewById(R.id.btn_login);
                btn_login.setText(getResourceString("ui_goto_connect_robot"));
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RobotConnectedActivity.launchActivity(mActivity,true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                    }
                });
            }
        }else
        {
            if(hasData==0){

            }else if(hasData ==1)
            {
                if(mEmptyView!=null)
                    mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        UbtLog.d(TAG,"---MyActionsLocalFragment-----onResume------");
        mHelper.registerListeners(this);
        if(((BaseActivity)mActivity).isBulueToothConnected())
        {
            mHelper.initActionPlayer(this);
            mHelper.initNewActionPlayer(this);
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mHelper!=null){
            mHelper.unRegisterListeners(this);
        }

    }


    @Override
    public void onNoteNoUser() {

    }

    @Override
    public void onReadActionsFinish(List<String> names) {
        UbtLog.d(TAG, " $syncServerDataEnd names=" + names.size());
        isLoadData = true;
        mDatas = mHelper.loadDatas();
        removeDuplicate(mDatas);
        mAdapter.setData(mDatas);
        sendMessage(1,UPDATE_VIEWS);
        mActivity.setmInsideDatas(mDatas);
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
    public void noteChangeFinish(boolean b, final String string) {


    }

    @Override
    public void noteTFPulled() {

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {
        if(action == null) return;
        int pos = 0;
        int startIndex = 0;
        if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_download_local){
            startIndex = MyActionsHelper.localSize;
        }

        if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_new_local){
            startIndex = MyActionsHelper.localSize + MyActionsHelper.myDownloadSize;
        }

        for(int i = startIndex;i<mDatas.size();i++)
        {
            Map<String,Object> map = mDatas.get(i);
            if(map.get(ActionsHelper.map_val_action_name).equals(action.actionName))
            {
                map.put(ActionsHelper.map_val_action_is_playing,true);
                pos = i;
                break;
            }
        }

        sendMessage(pos,UPDATE_ITEMS);

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "---wmma notePlayPause");
        if(!(mActivity.fragment instanceof MyActionsLocalFragment)){
            return;
        }
        int pos = mHelper.getCurrentPlayPosition(mDatas);
        String name = mHelper.getCurrentPlayName();
        UbtLog.d(TAG, "---wmma name=" + name);
        int startIndex = 0;
        if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_download_local){
            startIndex = MyActionsHelper.localSize;
        }

        if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_new_local){
            startIndex = MyActionsHelper.localSize + MyActionsHelper.myDownloadSize;
        }

        for(int i = startIndex;i<mDatas.size();i++)
        {
            Map<String,Object> map = mDatas.get(i);
            if(map.get(ActionsHelper.map_val_action_name).equals(name))
            {
                map.put(ActionsHelper.map_val_action_is_playing,false);
                pos = i;

                break;
            }
        }
        sendMessage(pos,UPDATE_ITEMS);

        //sendMessage( mHelper.getCurrentPlayPosition(mDatas),UPDATE_ITEMS);
    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        UbtLog.d(TAG, "---wmma notePlayContinue");
        if(!(mActivity.fragment instanceof MyActionsLocalFragment)){
            return;
        }
        int pos = mHelper.getCurrentPlayPosition(mDatas);
        String name = mHelper.getCurrentPlayName();
        UbtLog.d(TAG, "---wmma name=" + name);
        int startIndex = 0;
        if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_download_local){
            startIndex = MyActionsHelper.localSize;
        }

        if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_new_local){
            startIndex = MyActionsHelper.localSize + MyActionsHelper.myDownloadSize;
        }
        for(int i = startIndex;i<mDatas.size();i++)
        {
            Map<String,Object> map = mDatas.get(i);
            UbtLog.d(TAG, "---wmma name=" + map.get(ActionsHelper.map_val_action_name));
            String currentName = map.get(ActionsHelper.map_val_action_name).toString();
            if("#@%".contains(currentName.toCharArray()[0] + "")){
                currentName = currentName.substring(1);
            }

            if(currentName.equals(name))
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
//        ((ActionsHelper) mHelper).doStopMp3ForMyDownload();
        int pos = mHelper.getCurrentPlayPosition(mDatas);
        if(pos != -1){
            sendMessage(pos,UPDATE_ITEMS);
        }

    }

    @Override
    public void notePlayChargingError() {

         /*mHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(
                        mActivity,
                        ((BaseActivity) mActivity).getStringResources(
                                "ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT)
                        .show();
                mHelper.doActionCommand(MyActionsHelper.Command_type.Do_Stop,
                        "", MyActionsHelper.Action_type.My_download);
            }
        });*/
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
    public void onNoteTooMore() {

    }

    @Override
    public void onReadImgFromCache(Bitmap img, long l) {

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
    public void onClaerCache() {

    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

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
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {

    }

    @Override
    public void onChangeNewActionsFinish() {

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
    public void syncServerDataEnd(List<Map<String, Object>> data) {
        UbtLog.d(TAG, "$syncServerDataEnd data=" + data.size() + "---" + mHelper.getPlayerState());
        mDatas = data;
        removeDuplicate(mDatas);
        if(mHelper.getPlayerState().equals(ActionPlayer.Play_state.action_playing)){
            UbtLog.d(TAG, "hello world data=" + data.size());
            int startIndex = 0;
            if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_download_local){
                startIndex = MyActionsHelper.localSize;
            }

            if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_new_local){
                startIndex = MyActionsHelper.localSize + MyActionsHelper.myDownloadSize;
            }
            for(int i=startIndex;i<mDatas.size();i++){
                Map<String,Object> map = mDatas.get(i);
                String actionName = map.get(ActionsHelper.map_val_action_name).toString();
                if("#@%".contains(actionName.toCharArray()[0] + "")){
                    actionName = actionName.substring(1);
                }
                UbtLog.d(TAG, "hello world data=" + map.get(ActionsHelper.map_val_action_name) + "current=" + mHelper.getCurrentPlayName());
                if(actionName.equals(mHelper.getCurrentPlayName()))
                {
                    UbtLog.d(TAG, "actionName= set action playing state true getPlayerActionId=" + mHelper.getPlayerActionId());
                    map.put(ActionsHelper.map_val_action_is_playing,true);
                }else{
                    map.put(ActionsHelper.map_val_action_is_playing,false);
                }
            }

        }
        mAdapter.setData(mDatas);
        sendMessage(1,UPDATE_VIEWS);
        mActivity.setmInsideDatas(mDatas);
    }

    @Override
    public void noteDeleteActionStart(int pos) {

    }

    @Override
    public void noteDeleteActionFinish(boolean isOk,final String str) {
        UbtLog.d(TAG,"---noteDeleteActionFinish----delete result: " + isOk + "     " + mHelper.mCurrentRemovedItem);
        if(isOk){
            mDatas.remove(mHelper.mCurrentRemovedItem);
            MyActionsHelper.mCacheActionsNames.remove(mHelper.mCurrentRemovedItem);
            if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.Unkown){
                MyActionsHelper.localSize--;
            }else if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_download_local){
                MyActionsHelper.myDownloadSize--;
            }else if(MyActionsHelper.mCurrentLocalPlayType == MyActionsHelper.Action_type.My_new_local){
                MyActionsHelper.myNewSize--;
            }
            sendMessage(mHelper.mCurrentRemovedItem, BaseMyActionsFragment.DELETE_ITEMS);

        }else{
            getActivity().runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            ((MyActionsActivity)getActivity()).showToastDirect(str);
                        }
                    });
        }
    }


    private  void removeDuplicate(List<Map<String, Object>> list) {
        UbtLog.d(TAG, "removeDuplicate");
        for ( int i = 0 ; i < list.size() - 1 ; i ++ ) {
            MyLog.writeLog(TAG, "mData=" + list.get(i).toString());
            for ( int j = list.size() - 1 ; j > i; j -- ) {
                if (list.get(j).get(ActionsHelper.map_val_action_name).equals(list.get(i).get(ActionsHelper.map_val_action_name))) {
                    UbtLog.d(TAG, "removeDuplicate=" + list.get(j).toString());
//                    list.remove(j);
                }
            }
        }
        System.out.println(list);
    }
}
