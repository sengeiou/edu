package com.ubt.alpha1e_edu.ui.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.adapter.FillLocalContent;
import com.ubt.alpha1e_edu.business.ActionPlayer;
import com.ubt.alpha1e_edu.business.ActionsDownLoadManager;
import com.ubt.alpha1e_edu.data.FileTools;
import com.ubt.alpha1e_edu.data.model.ActionColloInfo;
import com.ubt.alpha1e_edu.data.model.ActionInfo;
import com.ubt.alpha1e_edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e_edu.data.model.NewActionInfo;
import com.ubt.alpha1e_edu.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.ui.LoginActivity;
import com.ubt.alpha1e_edu.ui.MyActionsSyncActivity;
import com.ubt.alpha1e_edu.ui.MyMainActivity;
import com.ubt.alpha1e_edu.ui.custom.DubGuideView;
import com.ubt.alpha1e_edu.ui.helper.ActionsHelper;
import com.ubt.alpha1e_edu.ui.helper.IActionsUI;
import com.ubt.alpha1e_edu.ui.helper.MyActionsHelper;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyActionsDownloadFragment extends BaseMyActionsFragment implements IActionsUI {
    private static final String TYPE = "type";
    public static String PLAY_ACTION_ID = "PLAY_ACTION_ID";
    private int type = -1;
    private View mSyncView;
    private boolean isStartReadActions = false;
    private List<String> mSynchronizedActions = new ArrayList<>();
    private static final String TAG = "MyActionsDownloadFragment";
    boolean isFirstLoadData = false;
    private DubGuideView guideView;
    private long mPlayActionId = -1;//传过来需要播放的动作ID
    private int mPlayActionIndex = -1;//传过来需要播放的动作下标索引

    public MyActionsDownloadFragment() {

    }

    public static MyActionsDownloadFragment newInstance(int pos,long mActionId)
    {
        MyActionsDownloadFragment fragment = new MyActionsDownloadFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, pos);
        args.putLong(PLAY_ACTION_ID, mActionId);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UbtLog.d(TAG,"--MyActionsDownloadFragment------onCreate------");
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE);//类型
            mPlayActionId = getArguments().getLong(PLAY_ACTION_ID);//类型
            super.type = type;
        }
    }

    @Override
    public void initDatas(Activity activity) {
        UbtLog.d(TAG,"---MyActionsDownloadFragment-----initDatas------");
            mHelper = MyActionsHelper.getInstance(mActivity);
            mHelper.registerListeners(this);
            mHelper.setManagerListeners(mActivity);
    }


    @Override
    public void updateViews(boolean isLogin, int hasData) {
        if(!isLogin)
        {
            ViewStub viewStub = (ViewStub)mView.findViewById(R.id.empty_viewstub);
            if(viewStub!=null)
            {
                mEmptyView = viewStub.inflate();
                ((TextView)mEmptyView.findViewById(R.id.txt_unlogin)).setText(getResourceString("ui_myaction_download_need_login"));
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
                ((TextView)mEmptyView.findViewById(R.id.txt_unlogin)).setText(getResourceString("ui_myaction_download_empty"));
                Button btn_login = (Button)mEmptyView.findViewById(R.id.btn_login);
                Button btn_check = (Button)mEmptyView.findViewById(R.id.btn_check_list);
                btn_check.setText(getResourceString("ui_myaction_view_download_history"));
                btn_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyActionsSyncActivity.launchActivity(mActivity,type,10010);
                    }
                });
                btn_login.setText(getResourceString("ui_myaction_goto_square"));
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ActionsLibMainActivity.launchActivity(mActivity,12306);
                        MyMainActivity.launchActivity(mActivity,12306);
                    }
                });

            }else if(hasData ==1)
            {
                View view = mView.findViewById(R.id.sync_download_viewstub);
                if(view!=null)
                {
                    TextView txt = (TextView) view.findViewById(R.id.txt_sync);
                    txt.setText(getResourceString("ui_myaction_synchronize_download_history"));
                    view.setVisibility(View.VISIBLE);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyActionsSyncActivity.launchActivity(getActivity(),type,10010);
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

    @Override
    public void firstLoadData() {
        isFirstLoadData = true;
        if(isLogin(getActivity()) && (mActivity.fragment instanceof MyActionsDownloadFragment))
        {
            UbtLog.e(TAG,"firstLoadData MyActionsDownloadFragment");
            showLoadingDialog();
            if(((BaseActivity)mActivity).isBulueToothConnected())
            {
                mHelper.initActionPlayer(this);
                mHelper.initNewActionPlayer(this);
                if(mActivity.mInsideDatas.size()==0)
                {
                    mHelper.doReadActions();
                    isStartReadActions = true;
                }else
                    mHelper.doReadMyDownLoadActions();
            }else{
                mHelper.doReadMyDownLoadActions();
            }

        }

    }

    @Override
    public void onResume() {
        UbtLog.d(TAG,"----MyActionsDownloadFragment----onResume-----");
        mHelper.registerListeners(this);
        if(((BaseActivity)mActivity).isBulueToothConnected())
        {
            mHelper.initActionPlayer(this);
            mHelper.initNewActionPlayer(this);
        }

        if(mActivity.fragment instanceof MyActionsDownloadFragment){
//            showDubGuide();
        }


        super.onResume();
    }

    /**
     * 显示配音导航
     */
    private void showDubGuide(){
        if(guideView == null && !isFirstLoadData && !DubGuideView.isShowDubGuide(mActivity) && mDatas.size()>0){
            guideView = new DubGuideView(getActivity());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mHelper!=null){
            mHelper.unRegisterListeners(this);
        }

        if(guideView != null){
            guideView.closeDubGuideView();
            guideView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if(mHelper!=null)
//            mHelper.unRegisterListeners(this);
    }

    @Override
    public void onPause() {
        UbtLog.d(TAG,"---MyActionsDownloadFragment-----onPause------");
        super.onPause();
        if(mHelper!=null)
           mHelper.unRegisterListeners(this);
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
    public void onReadMyDownLoadHistory(String hashCode, List<ActionRecordInfo> history) {

        List<ActionInfo> mLocalDownLoading =  ActionsDownLoadManager.getInstance(mActivity).getDownList();
        List<ActionInfo> mRobotDownLoading = new ArrayList<>();

        if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
            //判断机器人是否还在下载
            mRobotDownLoading =  ActionsDownLoadManager.getInstance(mActivity).getRobotDownList();
        }

        isLoadData = true;
        if(history.size()==0 && mLocalDownLoading.size() == 0 && mRobotDownLoading.size() == 0)
        {
            sendMessage(0,UPDATE_VIEWS);
        }else
        {
            mDatas = mHelper.loadDatas(history,mLocalDownLoading,mRobotDownLoading,mActivity.mInsideDatas);
            if(mHelper.getPlayerState() == ActionPlayer.Play_state.action_playing || mPlayActionId != -1){
                for(int i=0;i<mDatas.size();i++){
                    Map<String,Object> map = mDatas.get(i);
                    UbtLog.d("MyActionsDownload", "actionName=" +((ActionInfo)map.get(ActionsHelper.map_val_action)) +
                            "mHelper.getCurrentPlayName()=" + mHelper.getCurrentPlayName());
                    String htsName = ((ActionInfo)map.get(ActionsHelper.map_val_action)).hts_file_name;
                    long mActionId = ((ActionInfo)map.get(ActionsHelper.map_val_action)).actionId;
                    if(TextUtils.isEmpty(htsName)){
                        continue;
                    }

                    if("#@%".contains(htsName.toCharArray()[0] + "")){
                            htsName = htsName.substring(1);
                    }

                    htsName = htsName.substring(0, htsName.length()-4);
                    if(htsName.equals(mHelper.getCurrentPlayName()))
                    {
                            UbtLog.d(TAG, "actionName= set action playing state true");
                            map.put(ActionsHelper.map_val_action_is_playing,true);
                    }else{
                            map.put(ActionsHelper.map_val_action_is_playing,false);
                    }

                    if(mActionId == mPlayActionId){
                        mPlayActionIndex = i;
                    }
                }
            }

            mAdapter.setData(mDatas);
            sendMessage(1,UPDATE_VIEWS);
        }
        UbtLog.d(TAG,"--isFirstLoadData--" + isFirstLoadData);
        if(isFirstLoadData){
            isFirstLoadData = false;
//            showDubGuide();
            moveToPositionAndPlay();
        }
    }

    private void moveToPositionAndPlay() {

        if(mPlayActionIndex == -1 || mPlayActionId == -1){
            return;
        }
        //播放一次后重置-1
        mPlayActionId = -1;

        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = mLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLayoutManager.findLastVisibleItemPosition();
        UbtLog.d(TAG,"moveToPosition firstItem = " + firstItem + "    lastItem = " + lastItem + "   mPlayActionId = " + mPlayActionId + " mPlayActionIndex = " + mPlayActionIndex);
        //然后区分情况
        if (mPlayActionIndex <= firstItem ){
            //当要置顶的项在当前显示的第一个项的前面时
            mRecyclerView.scrollToPosition(mPlayActionIndex);
        }else if ( mPlayActionIndex <= lastItem ){
            //当要置顶的项已经在屏幕上显示时
            int top = mRecyclerView.getChildAt(mPlayActionIndex - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        }else{
            //当要置顶的项在当前显示的最后一项的后面时
            mRecyclerView.scrollToPosition(mPlayActionIndex);
            //这里这个变量是用在RecyclerView滚动监听里面的
            //move = true;
        }

        FillLocalContent.playDownloadAction(getActivity(),mHelper,mDatas.get(mPlayActionIndex),mPlayActionIndex);
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
        UbtLog.d(TAG, "--onReadActionsFinish");
        mSynchronizedActions = names;
        if(isLogin(getActivity())&&isVisible()&&isStartReadActions){
            mHelper.doReadMyDownLoadActions();
        }
    }

    @Override
    public void onNoteVol(int vol_pow) {

    }

    @Override
    public void onNoteVolState(boolean vol_state) {

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
    public void onSendFileFinish(int pos) {//pos 当前将要播放的index

    }

    @Override
    public void onSendFileCancel() {

    }

    @Override
    public void onSendFileUpdateProgress(String progress) {
        UbtLog.d(TAG,"onSendFileUpdateProgress:"+progress);
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

    }

    @Override
    public void noteDeleteActionStart(int pos) {

    }

    @Override
    public void noteDeleteActionFinish(boolean isOk, String str) {

    }

    /**
     * action play start
     * */
    @Override
    public void notePlayStart(List<String> mSourceActionNameList, ActionInfo action, ActionPlayer.Play_type mCurrentPlayType) {
//        if(!(mActivity.fragment instanceof MyActionsDownloadFragment)) return;
        if(action == null) return;
        int pos = mHelper.getCurrentPlayPosition(mDatas,action);
        if(pos!=-1)
        {
            if(!((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                mHelper.doPlayMp3ForMyDownload(action);
            }
            sendMessage(pos,UPDATE_ITEMS);
        }

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        for(int i=0;i<mDatas.size();i++){
            Map<String,Object> map = mDatas.get(i);
            UbtLog.d("MyActionsDownload", "actionName=" +((ActionInfo)map.get(ActionsHelper.map_val_action)) +
                    "mHelper.getCurrentPlayName()=" + mHelper.getCurrentPlayName());
            if(((ActionInfo)map.get(ActionsHelper.map_val_action)).hts_file_name != null){
                String htsName = ((ActionInfo)map.get(ActionsHelper.map_val_action)).hts_file_name;
                if("#@%".contains(htsName.toCharArray()[0] + "")){
                    htsName = htsName.substring(1);
                }
                htsName = htsName.substring(0, htsName.length()-4);
                UbtLog.d("MyActionsDownload", "htsname=" + htsName);
                if(htsName .equals(mHelper.getCurrentPlayName()))
                {
                    UbtLog.d(TAG, "actionName= set action playing state true");
                    map.put(ActionsHelper.map_val_action_is_playing,false);
                    sendMessage(i,UPDATE_ITEMS);
                    break;
                }
            }
        }
    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType) {
        for(int i=0;i<mDatas.size();i++){
            Map<String,Object> map = mDatas.get(i);
            UbtLog.d("MyActionsDownload", "actionName=" +((ActionInfo)map.get(ActionsHelper.map_val_action)) +
                    "mHelper.getCurrentPlayName()=" + mHelper.getCurrentPlayName());
            String htsName = ((ActionInfo)map.get(ActionsHelper.map_val_action)).hts_file_name;
            if("#@%".contains(htsName.toCharArray()[0] + "")){
                htsName = htsName.substring(1);
            }
            htsName = htsName.substring(0, htsName.length()-4);
            UbtLog.d("MyActionsDownload", "htsname=" + htsName);
            if(htsName .equals(mHelper.getCurrentPlayName()))
            {
                UbtLog.d(TAG, "actionName= set action playing state true");
                map.put(ActionsHelper.map_val_action_is_playing,true);
                sendMessage(i,UPDATE_ITEMS);
                break;
            }


        }
    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList, ActionPlayer.Play_type mCurrentPlayType, String hashCode) {
        if(!((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
            mHelper.doStopMp3ForMyDownload();
        }

        int pos = mHelper.getCurrentPlayPosition(mDatas);
        if(pos !=-1){
            sendMessage(pos,UPDATE_ITEMS);
        }

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

    }

    @Override
    public void onGetFileLenth(ActionInfo action, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(ActionInfo action, State state) {

    }

    @Override
    public void onReportProgress(ActionInfo action, double progess) {
        UbtLog.d(TAG, "onReportProgress progess=" + progess);
        for (int i = 0; i < mDatas.size(); i++) {

            if (((ActionInfo) mDatas.get(i).get(
                    MyActionsHelper.map_val_action)).actionId == action.actionId) {
                mDatas.get(i).put(
                        MyActionsHelper.map_val_action_download_progress,
                        progess);
                mDatas.get(i).put(
                        MyActionsHelper.map_val_action_download_state,
                        MyActionsHelper.Action_download_state.downing);
                mAdapter.setData(mDatas);
                sendMessage(i,UPDATE_ITEMS);
            }
        }
    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, State state) {
        //UbtLog.d(TAG, "onDownLoadFileFinish ActionInfo=" + action.toString() + "---state=" + state );
        int updateIndex = 0;
        if(state.equals(FileDownloadListener.State.success) ){
            for(int i=0; i < mDatas.size(); i++) {
                if (mDatas.get(i) == null)
                    continue;
                if (((ActionInfo) mDatas.get(i).get(
                        MyActionsHelper.map_val_action)).actionId == action.actionId) {
                    updateIndex = i;
                    mDatas.get(i).put(
                            MyActionsHelper.map_val_action_download_state,
                            MyActionsHelper.Action_download_state.download_finish);
                }
            }
        }else{
            for(int i=0; i < mDatas.size(); i++) {
                if (mDatas.get(i) == null){
                    continue;
                }

                if (((ActionInfo) mDatas.get(i).get(MyActionsHelper.map_val_action)).actionId == action.actionId) {
                    updateIndex = i;
                    if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                        File actionsFile = new File(FileTools.actions_download_cache + File.separator + action.actionId + File.separator + action.hts_file_name);
                        UbtLog.d(TAG,"actionsFile = " + actionsFile.getPath() + "   " + actionsFile.exists());
                        if(actionsFile.exists()){
                            //如果存在，说明是本地存在，机器人在下载
                        }else {
                            mDatas.remove(i);
                        }
                    }else {
                        mDatas.remove(i);
                    }
                }
            }
        }

        UbtLog.d(TAG,"updateIndex == " + updateIndex + " " + mDatas.size());
        mAdapter.setData(mDatas);
        if(mDatas.isEmpty()){
            sendMessage(0,UPDATE_VIEWS);
        }else {
            sendMessage(updateIndex,UPDATE_ITEMS);
        }
    }

    @Override
    public void onSyncHistoryFinish() {

    }

    @Override
    public void onReadHistoryFinish(List<ActionRecordInfo> history) {

        UbtLog.d(TAG, "--onReadHistoryFinish");

    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo) {

        //删除item
        int pos = -1;
        for (int i = 0; i < mDatas.size(); i++) {
            ActionInfo info = (ActionInfo) mDatas.get(i).get(MyActionsHelper.map_val_action);
            if (actionInfo.actionId==info.actionId) {
                pos = i;
                break;
            }
        }
        mDatas.remove(pos);
        if(mDatas.size()==0)
        {  sendMessage(0,UPDATE_VIEWS);
        }else
        {
            mAdapter.setData(mDatas);
            sendMessage(pos, DELETE_ITEMS);
        }

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
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {

    }

    @Override
    public void onChangeNewActionsFinish() {

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
    public void onNoteDataChaged(Bitmap img, long id) {

    }
}
