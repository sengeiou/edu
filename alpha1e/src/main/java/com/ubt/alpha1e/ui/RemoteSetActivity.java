package com.ubt.alpha1e.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.CustomActionItemAdapter;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.data.DB.RemoteRecordOperater;
import com.ubt.alpha1e.data.RemoteItem;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.RemoteInfo;
import com.ubt.alpha1e.data.model.RemoteRoleInfo;
import com.ubt.alpha1e.ui.dialog.IMessageListeter;
import com.ubt.alpha1e.ui.dialog.MyAlertDialog;
import com.ubt.alpha1e.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e.ui.helper.IRemoteUI;
import com.ubt.alpha1e.ui.helper.RemoteHelper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteSetActivity extends BaseActivity implements IRemoteUI {

    private static final String TAG = "RemoteSetActivity";

    private SimpleAdapter lst_actions_adapter;
    private List<Map<String, Object>> lst_actions_datas;
    private GridView grv_remote_actions;

    private ImageButton btn_1;
    private ImageButton btn_2;
    private ImageButton btn_3;
    private ImageButton btn_4;
    private ImageButton btn_5;
    private ImageButton btn_6;

    private ImageView img_action_1;
    private ImageView img_action_2;
    private ImageView img_action_3;
    private ImageView img_action_4;
    private ImageView img_action_5;
    private ImageView img_action_6;

    private LinearLayout lay_1;
    private LinearLayout lay_2;
    private LinearLayout lay_3;
    private LinearLayout lay_4;
    private LinearLayout lay_5;
    private LinearLayout lay_6;

    private List<View> mButtoms;
    private List<LinearLayout> mLays;

    private RemoteInfo mCurrentEditInfo;
    private int mCurrentSetIndex = -1;

    private LinearLayout lay_save;
    private LinearLayout lay_back;

    private String roleId = "";
    public CustomActionItemAdapter mCustomActionItemAdapter = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.CUSTOM){
            roleId = getIntent().getStringExtra("roleId");
            setContentView(R.layout.activity_remote_costom_set);
        }else{
            setContentView(R.layout.activity_remote_set);
        }

    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(RemoteActivity.class.getSimpleName());
        mHelper = new RemoteHelper(this, this);
        ((RemoteHelper) mHelper).doReadActions();
        mCurrentEditInfo = RemoteHelper.mCurrentInfo.doCopy();
        super.onResume();
        initUI();
        initControlListener();
        ((RemoteHelper) mHelper).doReadActionsSettingItem(roleId);
    }

    @Override
    protected void onDestroy() {
        if(mCoonLoadingDia != null && mCoonLoadingDia.isShowing()){
            mCoonLoadingDia.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void initUI() {
        mButtoms = new ArrayList<>();
        btn_1 = (ImageButton) findViewById(R.id.btn_1);
        btn_2 = (ImageButton) findViewById(R.id.btn_2);
        btn_3 = (ImageButton) findViewById(R.id.btn_3);
        btn_4 = (ImageButton) findViewById(R.id.btn_4);
        btn_5 = (ImageButton) findViewById(R.id.btn_5);
        btn_6 = (ImageButton) findViewById(R.id.btn_6);

        img_action_1 = (ImageView) findViewById(R.id.img_action_1);
        img_action_2 = (ImageView) findViewById(R.id.img_action_2);
        img_action_3 = (ImageView) findViewById(R.id.img_action_3);
        img_action_4 = (ImageView) findViewById(R.id.img_action_4);
        img_action_5 = (ImageView) findViewById(R.id.img_action_5);
        img_action_6 = (ImageView) findViewById(R.id.img_action_6);

        if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.CUSTOM){
            mButtoms.add(img_action_1);
            mButtoms.add(img_action_2);
            mButtoms.add(img_action_3);
            mButtoms.add(img_action_4);
            mButtoms.add(img_action_5);
            mButtoms.add(img_action_6);
        }else{
            mButtoms.add(btn_1);
            mButtoms.add(btn_2);
            mButtoms.add(btn_3);
            mButtoms.add(btn_4);
            mButtoms.add(btn_5);
            mButtoms.add(btn_6);
        }

        doSetItemBg();

        mLays = new ArrayList<>();
        lay_1 = (LinearLayout) findViewById(R.id.lay_1);
        mLays.add(lay_1);
        lay_2 = (LinearLayout) findViewById(R.id.lay_2);
        mLays.add(lay_2);
        lay_3 = (LinearLayout) findViewById(R.id.lay_3);
        mLays.add(lay_3);
        lay_4 = (LinearLayout) findViewById(R.id.lay_4);
        mLays.add(lay_4);
        lay_5 = (LinearLayout) findViewById(R.id.lay_5);
        mLays.add(lay_5);
        lay_6 = (LinearLayout) findViewById(R.id.lay_6);
        mLays.add(lay_6);

        grv_remote_actions = (GridView) findViewById(R.id.grv_remote_actions);
        lst_actions_datas = new ArrayList<Map<String, Object>>();
        if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.CUSTOM){

            mCustomActionItemAdapter = new CustomActionItemAdapter(this,handler,lst_actions_datas);
            grv_remote_actions.setAdapter(mCustomActionItemAdapter);
        }else{
            lst_actions_adapter = new SimpleAdapter(this, lst_actions_datas, R.layout.layout_remote_setting_item,
                    new String[]{RemoteHelper.MAP_KEY_ACTION_ITEM_ICON, RemoteHelper.MAP_KEY_ACTION_ITEM_NAME},
                    new int[]{R.id.img_remote_action_icon, R.id.txt_remote_action_name});
            grv_remote_actions.setAdapter(lst_actions_adapter);
        }
        lay_save = (LinearLayout) findViewById(R.id.lay_save);
        lay_back = (LinearLayout) findViewById(R.id.lay_back);
    }

    /**
     * 设置背景图片
     */
    private void doSetItemBg() {
        if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.CUSTOM){
            replaceImageView1To6(img_action_1,RemoteHelper.mCurrentInfo.do_1);
            replaceImageView1To6(img_action_2,RemoteHelper.mCurrentInfo.do_2);
            replaceImageView1To6(img_action_3,RemoteHelper.mCurrentInfo.do_3);
            replaceImageView1To6(img_action_4,RemoteHelper.mCurrentInfo.do_4);
            replaceImageView1To6(img_action_5,RemoteHelper.mCurrentInfo.do_5);
            replaceImageView1To6(img_action_6,RemoteHelper.mCurrentInfo.do_6);
        }else {
            btn_1.setBackgroundResource(((RemoteHelper) mHelper).getResId(mCurrentEditInfo.do_1.image_name));
            btn_2.setBackgroundResource(((RemoteHelper) mHelper).getResId(mCurrentEditInfo.do_2.image_name));
            btn_3.setBackgroundResource(((RemoteHelper) mHelper).getResId(mCurrentEditInfo.do_3.image_name));
            btn_4.setBackgroundResource(((RemoteHelper) mHelper).getResId(mCurrentEditInfo.do_4.image_name));
            btn_5.setBackgroundResource(((RemoteHelper) mHelper).getResId(mCurrentEditInfo.do_5.image_name));
            btn_6.setBackgroundResource(((RemoteHelper) mHelper).getResId(mCurrentEditInfo.do_6.image_name));
        }
    }

    /**
     * 更新右边6个图片按钮
     * @param imageView
     * @param mRemoteItem
     */
    private void replaceImageView1To6(ImageView imageView, RemoteItem mRemoteItem){
        String image_name = mRemoteItem.image_name;
        if(mRemoteItem.show_name != null && mRemoteItem.show_name.length() > 0){
            Glide.with(this).load(image_name).centerCrop().placeholder(R.drawable.sec_action_logo).into(imageView);
        }

    }

    @Override
    public void onReadSettingItem(List<RemoteItem> items) {
        lst_actions_datas.clear();
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put(RemoteHelper.MAP_KEY_ACTION_ITEM, items.get(i));
            if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.CUSTOM){
                item.put(RemoteHelper.MAP_KEY_ACTION_ITEM_ICON, items.get(i).image_name);
            }else{
                item.put(RemoteHelper.MAP_KEY_ACTION_ITEM_ICON, ((RemoteHelper) mHelper).getResId(items.get(i).image_name));
            }
            item.put(RemoteHelper.MAP_KEY_ACTION_ITEM_NAME, items.get(i).show_name);
            lst_actions_datas.add(item);
        }
        if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.CUSTOM){
            mCustomActionItemAdapter.notifyDataSetChanged();
        }else{
            lst_actions_adapter.notifyDataSetChanged();
        }

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

    }

    @Override
    public void onDelRemoteHeadPrompt(boolean isSuccess) {

    }

    private void doSave() {
        ((RemoteHelper) mHelper).doSaveRemoteEdit(mCurrentEditInfo,roleId);
        mCoonLoadingDia = SLoadingDialog.getInstance(RemoteSetActivity.this);
        mCoonLoadingDia.show();
    }

    @Override
    protected void initControlListener() {
        for (int i = 0; i < mButtoms.size(); i++) {
            mButtoms.get(i).setOnClickListener(controlListener);
        }
        onSelected(mButtoms.get(0).getId());

        grv_remote_actions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RemoteRecordOperater.setItemByIndex(mCurrentSetIndex, ((RemoteItem) lst_actions_datas.get(position).get(RemoteHelper.MAP_KEY_ACTION_ITEM)).doCopy(), mCurrentEditInfo);
                if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.CUSTOM){

                    Glide.with(RemoteSetActivity.this)
                            .load(lst_actions_datas.get(position).get(RemoteHelper.MAP_KEY_ACTION_ITEM_ICON))
                            .centerCrop()
                            .placeholder(R.drawable.sec_action_logo).into(((ImageView)mButtoms.get(mCurrentSetIndex-7)));

                }else{
                    doSetItemBg();
                }
            }
        });

        lay_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSave();
            }
        });
        lay_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReturn();

            }
        });
    }

    private void doReturn() {
        MyAlertDialog.getInstance(RemoteSetActivity.this, RemoteSetActivity.this.getStringResources("ui_remote_cancel_alter_tip"), RemoteSetActivity.this.getStringResources("ui_remote_cancel_alter_giveup"), RemoteSetActivity.this.getStringResources("ui_common_save"), new IMessageListeter() {
            @Override
            public void onViewAction(boolean isOk) {
                if (isOk) {
                    RemoteSetActivity.this.finish();
                } else {
                    doSave();
                }
            }
        }).show();
    }

    private void onSelected(int id) {
        for (int i = 0; i < mButtoms.size(); i++) {
            if (mButtoms.get(i).getId() == id) {
                if (RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.FIGHTER) {
                    mLays.get(i).setBackgroundResource(R.drawable.gamepad_settings_item_bg_fighter);

                }else if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.FOOTBALL_PLAYER){
                    mLays.get(i).setBackgroundResource(R.drawable.gamepad_settings_item_bg_football);
                } else {
                    mLays.get(i).setBackgroundResource(R.drawable.gamepad_settings_item_bg_costom);
                }
                mCurrentSetIndex = 7 + i;

            } else {
                mLays.get(i).setBackgroundColor(0x00000000);
            }
        }
    }

    private View.OnClickListener controlListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            onSelected(arg0.getId());
        }
    };

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void onReadActionsFinish(List<String> mActionsNames) {

    }

    @Override
    public void noteTFPulled() {

    }

    @Override
    public void onSendFileFinish(boolean isSuccess) {
        try {
            mCoonLoadingDia.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isSuccess) {
            this.finish();
        } else {
            //告知同步失败
            MyAlertDialog.getInstance(this, this.getStringResources("ui_remote_initialize_failed"), this.getStringResources("ui_common_cancel"), this.getStringResources("ui_remote_initialize_retry"), new IMessageListeter() {
                        @Override
                        public void onViewAction(boolean isOk) {
                            ((RemoteHelper) mHelper).sendFiles(RemoteRecordOperater.ModelType.FOOTBALL_PLAYER);
                            ((SLoadingDialog) mCoonLoadingDia).show(RemoteSetActivity.this.getStringResources("ui_remote_initializing"));
                        }
                    }
            );
        }
    }

    @Override
    public void onPlayActionFileNotExist() {

    }

    @Override
    public void onSendFileStart() {

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
    public void notePlayChargingError() {

    }

    @Override
    public void notePlayCycleNext(String action_name) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doReturn();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == event.ACTION_DOWN
                && event.getKeyCode() == event.KEYCODE_BACK) {
            doReturn();
            return true;
        }
        return false;
    }

}
