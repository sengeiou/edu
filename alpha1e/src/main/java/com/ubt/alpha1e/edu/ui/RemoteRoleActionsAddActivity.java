package com.ubt.alpha1e.edu.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.adapter.MyRoleAcitonAdapter;
import com.ubt.alpha1e.edu.business.ActionPlayer.Play_type;
import com.ubt.alpha1e.edu.business.ActionsDownLoadManager;
import com.ubt.alpha1e.edu.business.EndlessRecyclerOnScrollListener;
import com.ubt.alpha1e.edu.data.DB.RemoteRecordOperater;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.RemoteItem;
import com.ubt.alpha1e.edu.data.model.ActionColloInfo;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.ActionRecordInfo;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;
import com.ubt.alpha1e.edu.data.model.RemoteRoleActionInfo;
import com.ubt.alpha1e.edu.data.model.RemoteRoleInfo;
import com.ubt.alpha1e.edu.ui.dialog.IMessageListeter;
import com.ubt.alpha1e.edu.ui.dialog.MyAlertDialog;
import com.ubt.alpha1e.edu.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.edu.ui.helper.IActionsUI;
import com.ubt.alpha1e.edu.ui.helper.IRemoteUI;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.edu.ui.helper.RemoteHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoteRoleActionsAddActivity extends BaseActivity implements IRemoteUI,IActionsUI {

    private static final String TAG = "RemoteRoleActionsAddActivity";

    private static final String REMOTE_ROLE_INFO_PARAM = "remoteroleInfo";

    public static final int UPDATE_DOWNLOAD = 100;
    public static final int UPDATE_CREATION = 101;
    public static final int UPDATE_SELECT_ITEM = 102;
    public static final int SHOW_SELECT_LIMITE = 103;
    public static final int ADD_ROLE_ACTION_FINISH = 104;
    public static final int UPDATE_SELECT_NUM = 105;
    public static final int GOTO_GAMEPAD_PAGE = 106;

    private RelativeLayout lay_my_download_actions = null;
    private RelativeLayout lay_my_creation_actions = null;
    private RelativeLayout lay_title = null;
    private LinearLayout lay_remote_role_bottom = null;
    private ImageView img_my_download_show = null;
    private ImageView img_my_creation_show = null;
    public TextView txt_select_num = null;
    public Button btn_finish = null;
    public TextView btn_back = null;

    public RecyclerView recyclerview_my_download_actions = null;
    public RecyclerView recyclerview_my_creation_actions = null;
    public LinearLayoutManager mLayoutManager = null;
    public LinearLayoutManager mLayoutManager1 = null;
    public MyRoleAcitonAdapter mDownloadAdapter = null;
    public MyRoleAcitonAdapter mCreationAdapter = null;

    private ArrayList<Map<String,Object>> listItems = null;
    public List<Map<String,Object>> mAllMydownloadDatas = null;
    public List<Map<String,Object>> mAllMyCreationDatas = null;
    private RemoteRoleInfo mRemoteRoleInfo = null;
    private boolean isEdit = false;
    public MyActionsHelper mMyActionHelper = null;
    public int screenHeight = 0;
    private RelativeLayout.LayoutParams layoutParams = null;
    private boolean creationShow = true;
    private boolean downloadShow = true;
    public static int selectItemNum = 0;

    private static final int TIEM_OUT = 0x0010;

    private List<RemoteRoleActionInfo> oldRoleAactions = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_DOWNLOAD:
                    //mAllMydownloadDatas = (List<Map<String,Object>>)msg.obj;
                    mDownloadAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_CREATION:
                    //mAllMyCreationDatas = (List<Map<String,Object>>)msg.obj;
                    mCreationAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_SELECT_NUM:
                    txt_select_num.setText(selectItemNum+"");
                    if(selectItemNum == 0){
                        btn_finish.setBackgroundColor(getResources().getColor(R.color.A12));
                    }else{
                        btn_finish.setBackgroundColor(getResources().getColor(R.color.B8));
                    }
                    break;
                case UPDATE_SELECT_ITEM:
                    boolean isSelect = (boolean)msg.obj;
                    int type = msg.arg1;
                    int position = msg.arg2;
                    if(type == MyRoleAcitonAdapter.DOWNLOAD_ACTIONS){
                        mAllMydownloadDatas.get(position).put(MyActionsHelper.map_val_action_selected,isSelect);
                        mDownloadAdapter.notifyItemChanged(position);
                    }else{
                        mAllMyCreationDatas.get(position).put(MyActionsHelper.map_val_action_selected,isSelect);
                        mCreationAdapter.notifyItemChanged(position);
                    }
                    if(isSelect){
                        selectItemNum++;
                    }else{
                        selectItemNum--;
                    }
                    //UbtLog.d(TAG,"lihai-------isSelect->"+isSelect+"----"+selectItemNum);

                    mHandler.sendEmptyMessage(UPDATE_SELECT_NUM);
                    break;
                case SHOW_SELECT_LIMITE:
                    showToast(getString(R.string.ui_remote_action_count_limit));
                    break;
                case ADD_ROLE_ACTION_FINISH:
                    boolean isSucess = (boolean)msg.obj;
                    if(isSucess){
                        if(isEdit){
                            //int roleId = msg.arg1;
                            RemoteHelper.mCurrentType = RemoteRecordOperater.ModelType.CUSTOM;
                            RemoteHelper.mCurrentInfo = RemoteRecordOperater.getInstance(RemoteRoleActionsAddActivity.this.getApplicationContext(),
                                    FileTools.db_log_cache, FileTools.db_log_name).getRemoteInfoByModel(RemoteHelper.mCurrentType, false,mRemoteRoleInfo.roleid+"");

                            Intent mIntent = new Intent();
                            mIntent.setClass(RemoteRoleActionsAddActivity.this,RemoteActivity.class);
                            mIntent.putExtra(RemoteHelper.REMOTE_ROLE_INFO_PARAM,mRemoteRoleInfo);
                            RemoteRoleActionsAddActivity.this.startActivity(mIntent);
                            RemoteRoleActionsAddActivity.this.finish();
                        }else {
                            mHandler.sendEmptyMessage(GOTO_GAMEPAD_PAGE);
                        }
                    }
                    break;
                case GOTO_GAMEPAD_PAGE:

                    Intent mIntent = new Intent();
                    mIntent.setClass(RemoteRoleActionsAddActivity.this,RemoteSelActivity.class);
                    RemoteRoleActionsAddActivity.this.startActivity(mIntent);
                    RemoteRoleActionsAddActivity.this.finish();
                    break;
                case TIEM_OUT:
                    break;
                default:
                    break;
            }
        }
    };

    public static void launchActivity(Activity mActivity, RemoteRoleInfo roleInfo, boolean isEdit) {
        Intent intent = new Intent();
        intent.setClass(mActivity,RemoteRoleActionsAddActivity.class);
        intent.putExtra("isEdit",isEdit);
        intent.putExtra(REMOTE_ROLE_INFO_PARAM,roleInfo);
        mActivity.startActivity(intent);
        mActivity.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectItemNum = 0;
        mAllMydownloadDatas  = new ArrayList<Map<String,Object>>();
        mAllMyCreationDatas  = new ArrayList<Map<String,Object>>();

        setContentView(R.layout.activity_remote_role_actions_add);
        isEdit = getIntent().getBooleanExtra("isEdit",false);
        mRemoteRoleInfo = (RemoteRoleInfo)getIntent().getSerializableExtra(REMOTE_ROLE_INFO_PARAM);

        WindowManager wm = this.getWindowManager();
        screenHeight = wm.getDefaultDisplay().getHeight();

        //UbtLog.d(TAG,"lihai------------RemoteRoleActionsAddActivity->-->"+mRemoteRoleInfo.roleid+"---"+screenHeight+"----"+wm.getDefaultDisplay().getWidth());
        mHelper = new RemoteHelper(this, this);
        initUI();
        initControlListener();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mMyActionHelper = MyActionsHelper.getInstance(this);
        mMyActionHelper.registerListeners(this);
        mMyActionHelper.setManagerListeners(this);

        if(isEdit){
            oldRoleAactions = ((RemoteHelper)mHelper).doReadRemoteRoleByRoleid(mRemoteRoleInfo.roleid);
        }else{
            oldRoleAactions = new ArrayList<RemoteRoleActionInfo>();
        }

        mMyActionHelper.doReadMyDownLoadActions();
        mMyActionHelper.doReadMyNewAction();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMyActionHelper!=null){
            mMyActionHelper.unRegisterListeners(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initUI() {
        initTitle(getStringResources("ui_readback_add_action"));

        lay_my_download_actions = (RelativeLayout) findViewById(R.id.lay_my_download_actions);
        lay_my_creation_actions = (RelativeLayout) findViewById(R.id.lay_my_creation_actions);
        lay_title = (RelativeLayout) findViewById(R.id.lay_title);
        lay_remote_role_bottom = (LinearLayout) findViewById(R.id.lay_remote_role_bottom);
        img_my_download_show = (ImageView) findViewById(R.id.img_my_download_show);
        img_my_creation_show = (ImageView) findViewById(R.id.img_my_creation_show);
        txt_select_num = (TextView) findViewById(R.id.txt_select_num);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_back = ((TextView) this.findViewById(R.id.tv_base_back));

        initRecyclerViews();
    }

    public void initRecyclerViews() {

        recyclerview_my_download_actions = (RecyclerView) findViewById(R.id.recyclerview_my_download_actions);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview_my_download_actions.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator animator = recyclerview_my_download_actions.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        recyclerview_my_download_actions.getItemAnimator().setSupportsChangeAnimations(false);
        mDownloadAdapter = new MyRoleAcitonAdapter(this,mAllMydownloadDatas,MyRoleAcitonAdapter.DOWNLOAD_ACTIONS,mHandler);
        recyclerview_my_download_actions.setAdapter(mDownloadAdapter);
        recyclerview_my_download_actions.addOnScrollListener(mOnScrollListener);

        recyclerview_my_creation_actions = (RecyclerView) findViewById(R.id.recyclerview_my_creation_actions);
        mLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview_my_creation_actions.setLayoutManager(mLayoutManager1);
        RecyclerView.ItemAnimator animator1 = recyclerview_my_creation_actions.getItemAnimator();
        if (animator1 instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator1).setSupportsChangeAnimations(false);
        }
//        recyclerview_my_creation_actions.getItemAnimator().setSupportsChangeAnimations(false);
        mCreationAdapter = new MyRoleAcitonAdapter(this,mAllMyCreationDatas,MyRoleAcitonAdapter.CREATE_ACTIONS,mHandler);
        recyclerview_my_creation_actions.setAdapter(mCreationAdapter);
        recyclerview_my_creation_actions.addOnScrollListener(mOnScrollListener);

        recyclerview_my_download_actions.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int childCount = mLayoutManager.getChildCount();
                if(childCount == 0){
                    layoutParams = (RelativeLayout.LayoutParams)lay_my_creation_actions.getLayoutParams();
                    layoutParams.topMargin = lay_title.getHeight()+lay_my_download_actions.getHeight();
                    lay_my_creation_actions.setLayoutParams(layoutParams);
                }else{
                    int contentHeigth = lay_title.getHeight() + lay_my_download_actions.getHeight() + mLayoutManager.getChildAt(0).getHeight()*childCount
                                   + lay_my_creation_actions.getHeight() + lay_remote_role_bottom.getHeight();

                    int topMarget = 0;
                    if(screenHeight - contentHeigth > 0){
                        topMarget = lay_title.getHeight() + lay_my_download_actions.getHeight() + mLayoutManager.getChildAt(0).getHeight()*childCount;
                    }else{
                        topMarget = screenHeight - (lay_my_creation_actions.getHeight() + lay_remote_role_bottom.getHeight());

                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)recyclerview_my_download_actions.getLayoutParams();
                        lp.height = screenHeight - (lay_my_creation_actions.getHeight() + lay_remote_role_bottom.getHeight() + lay_title.getHeight() + lay_my_download_actions.getHeight());
                        recyclerview_my_download_actions.setLayoutParams(lp);
                    }
                    layoutParams = (RelativeLayout.LayoutParams)lay_my_creation_actions.getLayoutParams();
                    layoutParams.topMargin = topMarget;
                    lay_my_creation_actions.setLayoutParams(layoutParams);
                }

            }
        });
    }

    @Override
    protected void initControlListener() {
        lay_my_download_actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(downloadShow){

                    downloadShow = false;
                    img_my_download_show.setImageResource(R.drawable.gamepad_down_arrow);
                    mDownloadAdapter.setData(new ArrayList<Map<String,Object>>());
                    mDownloadAdapter.notifyDataSetChanged();

                }else{
                    downloadShow = true;
                    img_my_download_show.setImageResource(R.drawable.gamepad_up_arrow);
                    mDownloadAdapter.setData(mAllMydownloadDatas);
                    mDownloadAdapter.notifyDataSetChanged();
                }
            }
        });


        lay_my_creation_actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutParams = (RelativeLayout.LayoutParams)lay_my_creation_actions.getLayoutParams();
                //判断是否在最底下
                if(layoutParams.topMargin == (screenHeight - (lay_my_creation_actions.getHeight() + lay_remote_role_bottom.getHeight()))){
                    if(downloadShow){
                        downloadShow = false;
                        img_my_download_show.setImageResource(R.drawable.gamepad_down_arrow);
                        mDownloadAdapter.setData(new ArrayList<Map<String,Object>>());
                        mDownloadAdapter.notifyDataSetChanged();
                        if(!creationShow){
                            creationShow = true;
                            img_my_creation_show.setImageResource(R.drawable.gamepad_up_arrow);
                            mCreationAdapter.setData(mAllMyCreationDatas);
                            mCreationAdapter.notifyDataSetChanged();
                        }
                        return;
                    }
                }

                if(creationShow){
                    creationShow = false;
                    img_my_creation_show.setImageResource(R.drawable.gamepad_down_arrow);
                    mCreationAdapter.setData(new ArrayList<Map<String,Object>>());
                    mCreationAdapter.notifyDataSetChanged();

                }else{
                    creationShow = true;
                    if(downloadShow){
                        downloadShow = false;
                        img_my_download_show.setImageResource(R.drawable.gamepad_down_arrow);
                        mDownloadAdapter.setData(new ArrayList<Map<String,Object>>());
                        mDownloadAdapter.notifyDataSetChanged();
                    }
                    img_my_creation_show.setImageResource(R.drawable.gamepad_up_arrow);
                    mCreationAdapter.setData(mAllMyCreationDatas);
                    mCreationAdapter.notifyDataSetChanged();
                }

            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<RemoteRoleActionInfo> roleActionInfos = new ArrayList<RemoteRoleActionInfo>();
                RemoteRoleActionInfo roleActionInfo = null;
                for(Map<String,Object> actionMap : mAllMydownloadDatas){
                    if(actionMap.get(MyActionsHelper.map_val_action_selected) != null && (boolean)actionMap.get(MyActionsHelper.map_val_action_selected)){
                        roleActionInfo = new RemoteRoleActionInfo();
                        roleActionInfo.roleid = mRemoteRoleInfo.roleid;
                        roleActionInfo.actionName = actionMap.get(ActionsLibHelper.map_val_action_name)+"";
                        roleActionInfo.actionFileName = ((ActionInfo) actionMap.get(MyActionsHelper.map_val_action)).hts_file_name+"";
                        roleActionInfo.actionIcon = ((ActionInfo) actionMap.get(MyActionsHelper.map_val_action)).actionImagePath+"";
                        roleActionInfo.actionType = MyRoleAcitonAdapter.DOWNLOAD_ACTIONS;
                        roleActionInfo.actionId = ((ActionInfo) actionMap.get(MyActionsHelper.map_val_action)).actionId+"";
                        roleActionInfo.actionPath = ((ActionInfo) actionMap.get(MyActionsHelper.map_val_action)).actionPath+"";
                        roleActionInfos.add(roleActionInfo);
                        UbtLog.d(TAG,"lihai-----roleActionInfo.actionIcon->"+roleActionInfo.actionIcon);
                    }
                }

                for(Map<String,Object> actionMap : mAllMyCreationDatas){
                    if(actionMap.get(MyActionsHelper.map_val_action_selected) != null && (boolean)actionMap.get(MyActionsHelper.map_val_action_selected)){
                        roleActionInfo = new RemoteRoleActionInfo();
                        roleActionInfo.roleid = mRemoteRoleInfo.roleid;
                        roleActionInfo.actionName = actionMap.get(ActionsLibHelper.map_val_action_name)+"";
                        roleActionInfo.actionFileName = ((NewActionInfo) actionMap.get(MyActionsHelper.map_val_action)).actionOriginalId+".hts";
                        roleActionInfo.actionIcon = ((NewActionInfo) actionMap.get(MyActionsHelper.map_val_action)).actionHeadUrl;
                        roleActionInfo.actionType = MyRoleAcitonAdapter.CREATE_ACTIONS;
                        roleActionInfo.actionId = ((NewActionInfo) actionMap.get(MyActionsHelper.map_val_action)).actionId+"";
                        String actionPath = ((NewActionInfo) actionMap.get(MyActionsHelper.map_val_action)).actionPath_local;

                        if(new File(actionPath).isFile()){
                            roleActionInfo.actionPath = actionPath;
                        }else {
                            roleActionInfo.actionPath = FileTools.actions_new_cache + File.separator + roleActionInfo.actionId + File.separator+roleActionInfo.actionFileName;
                        }
                        roleActionInfos.add(roleActionInfo);
                    }
                }
                if(roleActionInfos.size() > 0){
                    if(isEdit){
                        ((RemoteHelper)mHelper).doUpdateRemoteRoleAndAction(mRemoteRoleInfo,roleActionInfos);
                    }else {
                        ((RemoteHelper)mHelper).doAddRemoteRoleAndAction(mRemoteRoleInfo,roleActionInfos);
                    }
                }

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doBack();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == event.ACTION_DOWN
                && event.getKeyCode() == event.KEYCODE_BACK) {
            return doBack();
        }
        return false;
    }

    private boolean doBack() {

        MyAlertDialog.getInstance(this,
                getStringResources("ui_remote_cancel_alter_tip"),
                getStringResources("ui_common_cancel"),
                getStringResources("ui_remote_cancel_alter_giveup"),
                    new IMessageListeter() {
                        @Override
                        public void onViewAction(boolean isOk) {
                            if (isOk) {
                                mHandler.sendEmptyMessage(GOTO_GAMEPAD_PAGE);
                            } else {

                            }
                        }
                }).show();
        return true;
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
    public void onReadActionsFinish(List<String> mActionsNames) {

    }

    @Override
    public void onNoteVol(int vol_pow) {

    }

    @Override
    public void onNoteVolState(boolean vol_state) {

    }

    @Override
    public void onReadMyDownLoadHistory(String hashCode, List<ActionRecordInfo> history) {

        List<ActionInfo> mMyDownLoading =  ActionsDownLoadManager.getInstance(this).getDownList();
        List<ActionInfo> mRobotDownLoading = null;
        if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
            //判断机器人是否还在下载
            mRobotDownLoading =  ActionsDownLoadManager.getInstance(this).getRobotDownList();
        }

        //UbtLog.d(TAG,"lihai-------history->"+history.size()+"----mMyDownLoading->"+mMyDownLoading.size()+"---"+oldRoleAactions.size());
        mAllMydownloadDatas = mMyActionHelper.loadDatas(history,mMyDownLoading,mRobotDownLoading, null);
        for(int i=0;i<mAllMydownloadDatas.size();i++){
            String action_file_name = ((ActionInfo) mAllMydownloadDatas.get(i).get(MyActionsHelper.map_val_action)).hts_file_name+"";
            boolean isSelect = false;
            for(RemoteRoleActionInfo roleActionInfo : oldRoleAactions){
                if(action_file_name.equals(roleActionInfo.actionFileName)){
                    isSelect = true;
                    selectItemNum ++;
                    break;
                }
            }
            mAllMydownloadDatas.get(i).put(MyActionsHelper.map_val_action_selected,isSelect);
        }
        mDownloadAdapter.setData(mAllMydownloadDatas);
        mDownloadAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessage(UPDATE_SELECT_NUM);

    }

    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {

        mAllMyCreationDatas =  mMyActionHelper.loadCreatedDatas(actions,null);

        //UbtLog.d(TAG,"mAllMyCreationDatas-->"+mAllMyCreationDatas.size());
        for(int i=0;i<mAllMyCreationDatas.size();i++){
            String action_file_name = ((NewActionInfo) mAllMyCreationDatas.get(i).get(MyActionsHelper.map_val_action)).actionId+".hts";
            boolean isSelect = false;
            for(RemoteRoleActionInfo roleActionInfo : oldRoleAactions){
                if(action_file_name.equals(roleActionInfo.actionFileName) ){
                    isSelect = true;
                    selectItemNum ++;
                    break;
                }
            }
            mAllMyCreationDatas.get(i).put(MyActionsHelper.map_val_action_selected,isSelect);
        }

        mCreationAdapter.setData(mAllMyCreationDatas);
        mCreationAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessage(UPDATE_SELECT_NUM);
        /*Message msg = new Message();
        msg.what = UPDATE_CREATION;
        msg.obj = mAllMyCreationDatas;
        mHandler.sendMessage(msg);*/
        //mHandler.sendEmptyMessage(UPDATE_CREATION);
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
    protected void initBoardCastListener() {

    }

    @Override
    public void notePlayStart(List<String> mSourceActionNameList,
                              ActionInfo action, Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayPause(List<String> mSourceActionNameList,
                              Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayContinue(List<String> mSourceActionNameList,
                                 Play_type mCurrentPlayType) {

    }

    @Override
    public void notePlayFinish(List<String> mSourceActionNameList,
                               Play_type mCurrentPlayType, String hashCode) {

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
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendFileFinish(boolean isSuccess) {

    }

    @Override
    public void onPlayActionFileNotExist() {

    }

    @Override
    public void onReadSettingItem(List<RemoteItem> items) {

    }

    @Override
    public void onReadRemoteRoleFinish(List<RemoteRoleInfo> mRemoteRoles) {

    }

    @Override
    public void onAddRemoteRole(boolean isSuccess,int roleId) {

    }

    @Override
    public void onUpdateRemoteRole(boolean isSuccess, RemoteRoleInfo roleInfo) {

    }

    @Override
    public void onDelRemoteRole(boolean isSuccess, RemoteRoleInfo roleInfo) {

    }

    @Override
    public void onAddRemoteRoleActions(boolean isSuccess,int roleId) {
        Message msg = new Message();
        msg.what = ADD_ROLE_ACTION_FINISH;
        msg.obj = isSuccess;
        msg.arg1 = roleId;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onDelRemoteHeadPrompt(boolean isSuccess) {

    }

    public EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);

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
    public void onChangeNewActionsFinish() {

    }
}
