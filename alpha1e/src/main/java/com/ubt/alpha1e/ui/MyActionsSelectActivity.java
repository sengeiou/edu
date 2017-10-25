package com.ubt.alpha1e.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.bumptech.glide.Glide;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.TimeTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.helper.ActionsHelper;
import com.ubt.alpha1e.ui.helper.IActionsUI;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.utils.ResourceUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */


public class MyActionsSelectActivity extends BaseActivity implements IActionsUI{

    private String TAG = "MyActionsSelectActivity";

    private TextView tvTitle;
    private LinearLayout llNext;
    private TextView btnBack;
    private TextView tvNext;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ActionsAdapter mAdapter;
    private List<Map<String, Object>> mDatas = new ArrayList<>();
    private MyActionsHelper helper;
    private NewActionInfo selectActionInfo ;
    public View mView,mEmptyView;

    private int sendType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycreate_action_select);
        sendType = getIntent().getIntExtra(DynamicActivity.SEND_TYPE, -1);
        initUI();
        initHelper();
        helper.doReadMyNewAction();
    }

    @Override
    protected void initUI() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llNext = (LinearLayout) findViewById(R.id.ll_next);
        btnBack = (TextView) findViewById(R.id.tv_base_back);
        tvNext = (TextView) findViewById(R.id.tv_next);
        llNext.setEnabled(false);
        tvNext.setTextColor(getResources().getColor(R.color.T7));

        btnBack.setText(getStringResources("ui_common_back"));
        tvTitle.setText(getStringResources("ui_myaction_creation"));
        tvNext.setText(getStringResources("ui_perfect_next"));

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_action_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ActionsAdapter(this, mDatas);
        mRecyclerView.setAdapter(mAdapter);

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(ActionsHelper.TRANSFOR_PARCEBLE, PG.convertParcelable(selectActionInfo));
                if(sendType == 0){
                    intent.putExtra(DynamicActivity.SEND_TYPE, sendType);
                    intent.setClass(MyActionsSelectActivity.this, DynamicActivity.class);
                    startActivity(intent);
                }else{
                    setResult(RESULT_OK, intent);

                }
                finish();
            }
        });

        ViewStub viewStub = (ViewStub)findViewById(R.id.empty_viewstub);
        UbtLog.d(TAG," viewStub-----");
        if(viewStub!=null)
        {
            mEmptyView = viewStub.inflate();
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private void initHelper() {
        helper = MyActionsHelper.getInstance(this);
        helper.registerListeners(this);
    }

    @Override
    protected void initControlListener() {

    }


    @Override
    public void onReadNewActionsFinish(List<NewActionInfo> actions) {
        initData(actions);
        if(mDatas.size() == 0){
            ((TextView)mEmptyView.findViewById(R.id.txt_unlogin)).setText(getStringResources("ui_release_empty_action"));
            mEmptyView.findViewById(R.id.btn_login).setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);

        }else {
            mEmptyView.setVisibility(View.GONE);
            mAdapter.setData(mDatas);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initData(List<NewActionInfo> actions) {
        for(int i=0; i<actions.size(); i++){
            Map<String, Object> action_item = new HashMap<String, Object>();
            NewActionInfo info = actions.get(i);
            info.actionSonType =info.actionType;
            if(info.actionStatus != 1 && info.actionStatus !=9){
                //UbtLog.d(TAG, "info=" + info.toString());
                action_item.put(MyActionsHelper.map_val_action,
                        (ActionInfo)info);
                action_item.put(MyActionsHelper.map_val_action_logo_res,
                        R.drawable.sec_action_logo);
                action_item.put(MyActionsHelper.map_val_action_name,
                        info.actionName);
                action_item.put(MyActionsHelper.map_val_action_browse_time,
                        0);
                action_item.put(MyActionsHelper.map_val_action_type_name, ResourceUtils.getActionType(info.actionSonType,this));
                action_item.put(MyActionsHelper.map_val_action_type_logo_res,
                        ResourceUtils.getActionTypeImage(info.actionSonType,this));
                int actionTime = (int)info.actionTime;
                action_item.put(MyActionsHelper.map_val_action_time, TimeTools
                        .getMMTime(actionTime>0?actionTime*1000:info.getTitleTime()));
                action_item.put(MyActionsHelper.map_val_action_disc,
                        info.actionDesciber);
                action_item.put(MyActionsHelper.map_val_action_selected,false);
                mDatas.add(action_item);
            }



        }
        UbtLog.d(TAG, "mDatas size=" + mDatas.size());
    }

    class ActionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context mContext;
        private List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
        private ActionsHolder mHolder;

        public ActionsAdapter(Context context,List<Map<String, Object>> list) {
            mContext = context;
            mList = list;
        }

        public void setData(List<Map<String, Object>>  data) {
            mList = data;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            mHolder = (ActionsHolder) holder;
            mHolder.txt_action_name.setText(mList.get(position).get(MyActionsHelper.map_val_action_name) + "");
            mHolder.txt_des.setText(mList.get(position).get(MyActionsHelper.map_val_action_disc) + "");
            mHolder.img_action_logo.setImageResource(R.drawable.sec_action_logo);
            mHolder.img_type_logo.setImageResource((int) mList.get(position).get(MyActionsHelper.map_val_action_type_logo_res));
            mHolder.txt_type_des.setText(mList.get(position).get(MyActionsHelper.map_val_action_type_name) + "");
            mHolder.txt_time.setText(mList.get(position).get(MyActionsHelper.map_val_action_time) + "");
            Glide.with(mContext)
                    .load(((NewActionInfo) (mList.get(position))
                            .get(MyActionsHelper.map_val_action)).actionHeadUrl)
                    .fitCenter().placeholder(R.drawable.sec_action_logo)
                    .into(mHolder.img_action_logo);

            if((Boolean) mList.get(position).get(MyActionsHelper.map_val_action_selected))
            {
                mHolder.img_select.setImageResource(R.drawable.icon_action_selected);
            }else {
                mHolder.img_select.setImageResource(R.drawable.myactions_normal);
            }
            View.OnClickListener listener  = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i=0; i<mList.size(); i++) {
                        mList.get(i).put(MyActionsHelper.map_val_action_selected,false);
                    }
                    mList.get(position).put(MyActionsHelper.map_val_action_selected,!(Boolean) mList.get(position).get(MyActionsHelper.map_val_action_selected));
                    selectActionInfo = (NewActionInfo)mList.get(position).get(MyActionsHelper.map_val_action);
                    mAdapter.notifyDataSetChanged();
                    llNext.setEnabled(true);
                    tvNext.setTextColor(getResources().getColor(R.color.T5));
                }
            };
            mHolder.img_select.setOnClickListener(listener);
            mHolder.rl_info.setOnClickListener(listener);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_myactions_sync_item, parent, false);
            mHolder = new ActionsHolder(view);
            return mHolder;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ActionsHolder extends  RecyclerView.ViewHolder {

            public RelativeLayout rl_info;
            public ImageView img_action_logo, img_type_logo,img_select;
            public TextView txt_action_name, txt_time, txt_des, txt_type_des;
            public ActionsHolder(View view){
                super(view);
                img_action_logo = (ImageView) view.findViewById(R.id.action_logo);
                img_type_logo = (ImageView) view.findViewById(R.id.img_type_logo);
                img_select = (ImageView) view.findViewById(R.id.img_select);
                rl_info  = (RelativeLayout) view.findViewById(R.id.rl_logo_info);
                txt_action_name = (TextView) view.findViewById(R.id.txt_action_name);
                txt_time = (TextView) view.findViewById(R.id.txt_time);
                txt_des = (TextView) view.findViewById(R.id.txt_disc);
                txt_type_des = (TextView) view.findViewById(R.id.txt_type_des);
            }

        }




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
    public void onChangeNewActionsFinish() {

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
    protected void initBoardCastListener() {

    }
}
