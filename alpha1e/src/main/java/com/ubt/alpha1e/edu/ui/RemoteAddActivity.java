package com.ubt.alpha1e.edu.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.adapter.RemoteHeadItemAdapter;
import com.ubt.alpha1e.edu.business.ActionPlayer.Play_type;
import com.ubt.alpha1e.edu.data.RemoteItem;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.RemoteRoleInfo;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;

import com.ubt.alpha1e.edu.ui.dialog.IMessageListeter;
import com.ubt.alpha1e.edu.ui.dialog.MyAlertDialog;
import com.ubt.alpha1e.edu.ui.helper.IRemoteUI;
import com.ubt.alpha1e.edu.ui.helper.RemoteHelper;
import com.ubt.alpha1e.edu.utils.NameLengthFilter;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteAddActivity extends BaseActivity implements IRemoteUI, BaseDiaUI {

    private static final String TAG = "RemoteAddActivity";

    private static final String REMOTE_ROLE_INFO_PARAM = "remoteroleInfo";
    public static final int UPDATE_HEAD = 100;
    public static final int ADD_ROLE_FINISH = 101;


    private EditText ui_remote_role_name = null;
    private EditText ui_remote_role_describe = null;
    private RemoteRoleInfo mRemoteRoleInfo = null;
    public TextView btn_back = null;

    private GridView mGridView;
    private RemoteHeadItemAdapter mRemoteHeadItemAdapter = null;
    private ArrayList<Map<String,Object>> listItems = null;
    private List<RemoteRoleInfo> mAllRemoteRoleDatas = null;

    /*private int[] imageIds = {R.drawable.gamepad_head_1, R.drawable.gamepad_head_2, R.drawable.gamepad_head_3,
            R.drawable.gamepad_head_4, R.drawable.gamepad_head_5, R.drawable.gamepad_head_6};*/

    private String[] imageNames = {"gamepad_head_1.png", "gamepad_head_2.png", "gamepad_head_3.png",
            "gamepad_head_4.png", "gamepad_head_5.png", "gamepad_head_6.png"};

    private static final int TIEM_OUT = 0x0010;
    private String headicon = "";
    boolean isEditRole = false;
    private RemoteRoleInfo roleInfo = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_HEAD:
                    int selectPos = (int)msg.obj;
                    for(int i = 0;i<listItems.size();i++){
                        if(i == selectPos){
                            headicon = (String) listItems.get(i).get(RemoteHelper.MAP_KEY_HEAD_IMAGE_ICON);
                            listItems.get(i).put(RemoteHelper.MAP_KEY_HEAD_IMAGE_ICON_SELECT,true);
                        }else{
                            listItems.get(i).put(RemoteHelper.MAP_KEY_HEAD_IMAGE_ICON_SELECT,false);
                        }
                    }
                    mRemoteHeadItemAdapter.notifyDataSetChanged();
                    break;
                case ADD_ROLE_FINISH:
                    boolean isSuccess = (boolean)msg.obj;
                    if(isSuccess){
                        int rowId = msg.arg1;
                        roleInfo.roleid = rowId;
                        gotoNextAddActions(false);
                    }
                    break;
                case TIEM_OUT:
                    break;
                default:
                    break;
            }
        }
    };

    public static void launchActivity(Context mContext,RemoteRoleInfo roleInfo) {
        Intent intent = new Intent();
        intent.putExtra(REMOTE_ROLE_INFO_PARAM,roleInfo);
        intent.setClass(mContext,RemoteAddActivity.class);
        mContext.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        float scale = this.getResources().getDisplayMetrics().density;
        float fontScale = this.getResources().getDisplayMetrics().scaledDensity;
        //UbtLog.d(TAG,"lihai------------RemoteAddActivity---scale->>>"+scale+"----"+(int)(30 / scale + 0.5f));
        //UbtLog.d(TAG,"lihai------------RemoteAddActivity---fontScale->"+fontScale+"----"+(int)(30 / fontScale + 0.5f));
        setContentView(R.layout.activity_remote_add);
        mRemoteRoleInfo = (RemoteRoleInfo)getIntent().getSerializableExtra(REMOTE_ROLE_INFO_PARAM);
        if(mRemoteRoleInfo != null){
            isEditRole = true;
        }
        mHelper = new RemoteHelper(this, this);
        mAllRemoteRoleDatas = new ArrayList<RemoteRoleInfo>();
        ((RemoteHelper) mHelper).doReadAllRemoteRole();
        initUI();
        initControlListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void initUI() {
        if(mRemoteRoleInfo != null){
            initTitle(getStringResources("ui_common_edit"));
        }else{
            initTitle(getStringResources("ui_remote_add_new_role"));
        }


        initTitleSave(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRemoteRole();
            }
        },getStringResources("ui_perfect_next"));

        ui_remote_role_name = (EditText) findViewById(R.id.ui_remote_role_name);
        InputFilter[] filters = { new NameLengthFilter(20) };
        ui_remote_role_name.setFilters(filters);

        ui_remote_role_describe = (EditText) findViewById(R.id.ui_remote_role_describe);
        btn_back = ((TextView) this.findViewById(R.id.tv_base_back));
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doBack();
            }
        });

        initGrids();
    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        ((Button)findViewById(R.id.btn_base_save)).setText(getStringResources("ui_perfect_next"));
    }

    private void initGrids() {
        headicon = "";
        mGridView = (GridView) findViewById(R.id.lay_remote_role);
        listItems = new ArrayList<Map<String,Object>>();

        for (int i = 0; i < 6; i++) {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put(RemoteHelper.MAP_KEY_HEAD_IMAGE_ICON,imageNames[i]);
            map.put(RemoteHelper.MAP_KEY_HEAD_IMAGE_ICON_SELECT,false);
            listItems.add(map);
        }

        mRemoteHeadItemAdapter = new RemoteHeadItemAdapter(this,listItems,mHandler,(RemoteHelper)mHelper);
        mGridView.setAdapter(mRemoteHeadItemAdapter);
    }

    private void initData(){
        if(isEditRole){
            headicon = mRemoteRoleInfo.roleIcon;
            int pos = -1;
            for(int i=0;i<imageNames.length;i++){
                if(imageNames[i].equals(mRemoteRoleInfo.roleIcon)){
                    pos = i;
                }
            }
            listItems.get(pos).put(RemoteHelper.MAP_KEY_HEAD_IMAGE_ICON_SELECT,true);
            mRemoteHeadItemAdapter.notifyDataSetChanged();
            ui_remote_role_name.setText(mRemoteRoleInfo.roleName);
            ui_remote_role_describe.setText(mRemoteRoleInfo.roleIntroduction);
        }else {
            headicon = imageNames[0];
            listItems.get(0).put(RemoteHelper.MAP_KEY_HEAD_IMAGE_ICON_SELECT,true);
            mRemoteHeadItemAdapter.notifyDataSetChanged();
        }
    }

    public void saveRemoteRole(){
        String remote_name = ui_remote_role_name.getText().toString();
        String remote_destribe = ui_remote_role_describe.getText().toString();
        if(TextUtils.isEmpty(remote_name.trim())){
            showToast(getStringResources("ui_remote_role_name_placeholder"));
            return;
        }
        if(TextUtils.isEmpty(remote_destribe.trim())){
            showToast(getStringResources("ui_remote_role_desc_placeholder"));
            return;
        }

        if(!isEditRole && isExistRole(remote_name)){
            showToast(getStringResources("ui_action_name_exist"));
            return;
        }

        roleInfo = new RemoteRoleInfo();
        roleInfo.roleIcon = headicon;
        roleInfo.roleName = remote_name;
        roleInfo.roleIntroduction = remote_destribe;

        if(isEditRole){
            roleInfo.roleid = mRemoteRoleInfo.roleid;
            gotoNextAddActions(isEditRole);
        }else{
            gotoNextAddActions(false);
            //((RemoteHelper)mHelper).doAddRemoteRole(roleInfo);
        }

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
        if(ui_remote_role_name.getText().toString().length() == 0 && ui_remote_role_describe.getText().toString().length() == 0 && "".equals(headicon)){
            RemoteAddActivity.this.finish();
            return false;
        }

        MyAlertDialog.getInstance(this,
                getStringResources("ui_remote_cancel_alter_tip"),
                getStringResources("ui_common_cancel"),
                getStringResources("ui_remote_cancel_alter_giveup"),
                new IMessageListeter() {
                    @Override
                    public void onViewAction(boolean isOk) {
                        if (isOk) {
                            RemoteAddActivity.this.finish();
                        } else {

                        }
                    }
                }).show();
        return true;
    }

    public void gotoNextAddActions(boolean isEdit){
        UbtLog.d(TAG,"lihai----------roleInfo->"+roleInfo.toString());
        RemoteRoleActionsAddActivity.launchActivity(this,roleInfo,isEdit);
    }

    /**
     * 出来足球员、格斗家 国际化显示
     * @param roles
     * @return
     */
    private List<RemoteRoleInfo> dealrRoleInfos(List<RemoteRoleInfo> roles){
        List<RemoteRoleInfo> newList = new ArrayList<RemoteRoleInfo>();
        RemoteRoleInfo roleInfo = null;
        for(int i = 0;i < roles.size();i++){
            roleInfo = roles.get(i);
            if(roleInfo.roleid == 1){
                roleInfo.roleName = getStringResources("ui_remote_role_footballer");
            }else if(roleInfo.roleid == 2){
                roleInfo.roleName = getStringResources("ui_remote_role_fighter");
            }else if(roleInfo.roleid == 3){
                roleInfo.roleName = getStringResources("ui_remote_role_dancer");
            }
            newList.add(roleInfo);
        }
        return newList;
    }

    private boolean isExistRole(String roleName){
        boolean flag = false;
        for(RemoteRoleInfo mRemoteRole : mAllRemoteRoleDatas){
            if(mRemoteRole.roleName.trim().equals(roleName.trim())){
                flag = true;
                break;
            }
        }


        return flag;
    }
    @Override
    public void onReadActionsFinish(List<String> mActionsNames) {

    }

    @Override
    public void noteTFPulled() {

    }

    @Override
    protected void initControlListener() {

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
    public void onSendFileStart() {

    }

    @Override
    public void onReadSettingItem(List<RemoteItem> items) {

    }

    @Override
    public void onReadRemoteRoleFinish(List<RemoteRoleInfo> mRemoteRoles) {
        mAllRemoteRoleDatas.clear();
        mAllRemoteRoleDatas.addAll(dealrRoleInfos(mRemoteRoles));
    }

    @Override
    public void onAddRemoteRole(boolean isSuccess,int roleId) {
        Message msg = new Message();
        msg.what = ADD_ROLE_FINISH;
        msg.obj = isSuccess;
        msg.arg1 = roleId;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onUpdateRemoteRole(boolean isSuccess, RemoteRoleInfo roleInfo) {

    }

    @Override
    public void onDelRemoteRole(boolean isSuccess, RemoteRoleInfo roleInfo) {

    }

    @Override
    public void onAddRemoteRoleActions(boolean isSuccess,int roleId) {

    }

    @Override
    public void onDelRemoteHeadPrompt(boolean isSuccess) {

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }
}
