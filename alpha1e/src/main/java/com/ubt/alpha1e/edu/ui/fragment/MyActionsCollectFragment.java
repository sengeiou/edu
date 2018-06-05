package com.ubt.alpha1e.edu.ui.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.business.ActionPlayer;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.model.ActionColloInfo;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;
import com.ubt.alpha1e.edu.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.LoginActivity;
import com.ubt.alpha1e.edu.ui.MyMainActivity;
import com.ubt.alpha1e.edu.ui.helper.IActionsUI;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;

import java.util.List;
import java.util.Map;

public class MyActionsCollectFragment extends BaseMyActionsFragment implements IActionsUI {


    private static final String TYPE = "type";
    private int type = -1;

    private List<ActionRecordInfo> mDownLoadHistory = null;

    public MyActionsCollectFragment() {
    }

    public static MyActionsCollectFragment newInstance(int pos)
    {
        MyActionsCollectFragment fragment = new MyActionsCollectFragment();
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
        if(mHelper!=null)
            mHelper.unRegisterListeners(this);
    }

    @Override
    public void initDatas(Activity activity) {
            mHelper = MyActionsHelper.getInstance(mActivity);
            mHelper.registerListeners(this);
            mHelper.setManagerListeners(mActivity);
    }

    @Override
    public void firstLoadData() {
        if(isLogin(getActivity()))
        {
            showLoadingDialog();
            mHelper.doReadMyDownLoadActions();
            //mHelper.doReadCollcationRecord();
            Log.e("wilson$$$","----firstLoadData----collect----");
//             sendHandlerMessage(SHOW_LOADING_DIALOG);
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
                ((TextView)mEmptyView.findViewById(R.id.txt_unlogin)).setText(getResourceString("ui_myaction_collection_empty"));
                Button btn_login = (Button)mEmptyView.findViewById(R.id.btn_login);
                btn_login.setText(getResourceString("ui_myaction_goto_square"));
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ActionsLibMainActivity.launchActivity(mActivity,12306);
                        MyMainActivity.launchActivity(mActivity,12306);
                    }
                });
            }else
            {
                if(mEmptyView!=null)
                    mEmptyView.setVisibility(View.GONE);
            }
        }

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
    public void onReadActionsFinish(List<String> names) {

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

    }

    @Override
    public void noteDeleteActionStart(int pos) {

    }

    @Override
    public void noteDeleteActionFinish(boolean isOk, String str) {

    }

    @Override
    public void onNoteTooMore() {

    }

    @Override
    public void onReadImgFromCache(Bitmap img, long l) {

    }

    @Override
    public void onReadCollocationRecordFinish(boolean isSuccess, String errorInfo, List<ActionColloInfo> history) {
//        if (!isSuccess) {
//            mHandler.post(new Runnable() {
//
//                @Override
//                public void run() {
//                    Toast.makeText(mActivity, errorInfo, Toast.LENGTH_SHORT)
//                            .show();
//                }
//            });
//        }
        isLoadData = true;
        if(history.size()==0)
        {
            sendMessage(0,UPDATE_VIEWS);
        }else
        {
            mDatas = mHelper.loadDatas(history,mDownLoadHistory);
            mAdapter.setData(mDatas);
            sendMessage(1,UPDATE_VIEWS);
        }

    }

    @Override
    public void clearDatas() {

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

    }

    @Override
    public void onDownLoadFileFinish(ActionInfo action, FileDownloadListener.State state) {

    }

    @Override
    public void onSyncHistoryFinish() {

    }
    @Override

    public void onReadHistoryFinish(List<ActionRecordInfo> history) {
        mDownLoadHistory = history;
        mHelper.doReadCollcationRecord();
    }

    @Override
    public void onChangeFinish(ActionInfo actionInfo) {

    }

    @Override
    public void onNoteDataChaged(Bitmap img, long id) {

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

}
