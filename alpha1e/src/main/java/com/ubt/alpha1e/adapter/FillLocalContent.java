package com.ubt.alpha1e.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.baoyz.pg.PG;
import com.bumptech.glide.Glide;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.DataTools;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionColloInfo;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.ActionUnpublishedActivity;
import com.ubt.alpha1e.ui.ActionsLibPreviewWebActivity;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.DubActivity;
import com.ubt.alpha1e.ui.RobotConnectedActivity;
import com.ubt.alpha1e.ui.custom.SlideView;
import com.ubt.alpha1e.ui.dialog.alertview.AlertView;
import com.ubt.alpha1e.ui.dialog.alertview.OnItemClickListener;
import com.ubt.alpha1e.ui.helper.ActionsColloHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/16.
 */
public class FillLocalContent {

    private static final String TAG = "FillLocalContent";

    public static final int LOCAL_ACTIONS = 0;
    public static final int DOWNLOAD_ACTIONS = 1;
    public static final int COLLECT_ACTIONS = 2;
    public static final int CREATE_ACTIONS = 3;
    public static final int SYNC_DOWNLOAD_ACTIONS = 4;
    public static final int SYNC_CREATE_ACTIONS = 5;



    public static void fillMyActionItems(final Context mContext, RecyclerView.ViewHolder holder, final Map<String,Object> actionList, final int type, BaseHelper mHelper,int pos)

    {


        fillBasicItems(mContext,holder,actionList,type);
        switch (type)
        {
            case LOCAL_ACTIONS:
                fillLocalActionsContent(mContext, holder, actionList,(MyActionsHelper)mHelper,pos);
                break;
            case DOWNLOAD_ACTIONS:
                fillDownLoadActionsContent(mContext, holder, actionList,(MyActionsHelper)mHelper,pos);
                break;
            case COLLECT_ACTIONS:
                fillCollectActionsContent(mContext, holder, actionList,(MyActionsHelper)mHelper,pos);
                break;
            case CREATE_ACTIONS:
                fillCreateActionsContent(mContext, holder, actionList,(MyActionsHelper)mHelper,pos);
                break;
            case SYNC_DOWNLOAD_ACTIONS:
//                fillDownloadSyncContent(mContext, holder, actionList,(MyActionsHelper)mHelper,pos);
//                break;
            case SYNC_CREATE_ACTIONS:
                fillCreationSyncContent(mContext, holder, actionList,(MyActionsHelper)mHelper,pos,type);
                break;
        }

        }
    public static void fillBasicItems(Context mContext, RecyclerView.ViewHolder myHolder, final Map<String,Object> actionList,int type)
    {

        if(type==SYNC_DOWNLOAD_ACTIONS || type ==SYNC_CREATE_ACTIONS)
        {
            ActionsSyncRecyclerAdapter.MySyncViewHolder holder  = (ActionsSyncRecyclerAdapter.MySyncViewHolder) myHolder;
            holder.txt_action_name.setText(actionList.get(MyActionsHelper.map_val_action_name) + "");
            holder.txt_des.setText(actionList.get(MyActionsHelper.map_val_action_disc) + "");
            holder.img_type_logo.setImageResource((int) actionList.get(MyActionsHelper.map_val_action_type_logo_res));
            holder.txt_type_des.setText(actionList.get(MyActionsHelper.map_val_action_type_name) + "");
            holder.txt_time.setText(actionList.get(MyActionsHelper.map_val_action_time) + "");
        }else
        {
            MyActionsRecyclerAdapter.MyActionsHolder holder  = (MyActionsRecyclerAdapter.MyActionsHolder) myHolder;
            if(type == DOWNLOAD_ACTIONS || type == CREATE_ACTIONS){
                holder.iv_more.setVisibility(View.VISIBLE);
            }else{
                holder.iv_more.setVisibility(View.GONE);
            }

            String action_name = actionList.get(MyActionsHelper.map_val_action_name) + "";
            if(action_name.startsWith("@") || action_name.startsWith("#") || action_name.startsWith("%")){
                action_name = action_name.substring(1);
            }
            holder.txt_action_name.setText(action_name);
            holder.txt_des.setText(actionList.get(MyActionsHelper.map_val_action_disc) + "");
            holder.img_type_logo.setImageResource((int) actionList.get(MyActionsHelper.map_val_action_type_logo_res));
            //UbtLog.d(TAG, "type=" + actionList.get(MyActionsHelper.map_val_action_type_name));
            holder.txt_type_des.setText(actionList.get(MyActionsHelper.map_val_action_type_name) + "");
            if(type == LOCAL_ACTIONS){
                holder.lay_center.setVisibility(View.GONE);
                holder.txt_des.setVisibility(View.GONE);
            }else if(type == DOWNLOAD_ACTIONS)
            {
                ActionInfo actionInfo = (ActionInfo) actionList.get(MyActionsHelper.map_val_action);
                holder.txt_sees.setText(actionInfo.actionBrowseTime+"");
            }else{
                holder.txt_sees.setText(actionList.get(MyActionsHelper.map_val_action_browse_time) == null ?
                        "0" : actionList.get(MyActionsHelper.map_val_action_browse_time) + "");
            }
            holder.txt_time.setText(actionList.get(MyActionsHelper.map_val_action_time) + "");
            //UbtLog.d(TAG,"lihai------------notePlayFinish-map_val_action_is_playing:"+actionList.get(MyActionsHelper.map_val_action_is_playing));
            if(actionList.get(MyActionsHelper.map_val_action_is_playing)!=null)//我的收藏不能播放
            {
                UbtLog.d(TAG, "readCurrentPlayingActionName=" + ((BaseActivity)mContext).readCurrentPlayingActionName());
                if ((Boolean) actionList.get(MyActionsHelper.map_val_action_is_playing) && !((BaseActivity)mContext).readCurrentPlayingActionName().equals("")
                        && !((BaseActivity)mContext).readCurrentPlayingActionName().equals("NO_VALUE") ) {
                    holder.playingLayout.setVisibility(View.VISIBLE);
//                    holder.gif.setVisibility(View.VISIBLE);
                    holder.img_state.setVisibility(View.INVISIBLE);
                } else {
//                    holder.gif.setVisibility(View.INVISIBLE);
                    holder.playingLayout.setVisibility(View.INVISIBLE);
                    holder.img_state.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    /**
    *本地动作
    * */
    public static void fillLocalActionsContent(final Context mContext, RecyclerView.ViewHolder myHolder, final Map<String,Object> actionList
                                               ,final MyActionsHelper mHelper,final int pos)
    {

        final MyActionsRecyclerAdapter.MyActionsHolder holder  = (MyActionsRecyclerAdapter.MyActionsHolder) myHolder;
        holder.img_state.setImageDrawable(getDrawableRes(mContext,"my_actions_play_item_ft"));
        Glide.with(mContext)
                .load("")
                .fitCenter().placeholder(R.drawable.sec_action_logo)
                .into(holder.img_action_logo);
        holder.lay_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    //执行播放逻辑
                if (mHelper.isLostCoon()) {
                    RobotConnectedActivity.launchActivity(AlphaApplication.getBaseActivity(),true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                    return;
                }

               //检测是否在充电状态和边充边玩状态是否打开
               if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(mContext)){
                   Toast.makeText(mContext, AlphaApplication.getBaseActivity().getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
                   return;
               }

                if(isFirstPlayAction(mHelper)){
                    return;
                }

                if ((Boolean) actionList.get(MyActionsHelper.map_val_action_is_playing)) {
                    return;
                }
//                MyActionsHelper.Action_download_state currentState = (MyActionsHelper.Action_download_state) actionList
//                        .get(ActionsLibHelper.map_val_action_download_state);
//                // 没有同步
//                if (currentState == MyActionsHelper.Action_download_state.download_finish) {
////                    mCurrentSendActionItem = item;
//                    mHelper.mCurrentIntendtoPlay = pos;
//                    mHelper.sendActionFileToRobot(((ActionInfo) actionList
//                            .get(ActionsLibHelper.map_val_action)));
//                }
                // 如果已经同步
//                else if (currentState == MyActionsHelper.Action_download_state.send_finish) {

                    MyActionsHelper.Action_type playActionType = MyActionsHelper.Action_type.Unkown;
                    if(pos < MyActionsHelper.localSize){
                        MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.Unkown;
                    }else if(pos >= MyActionsHelper.localSize  &&  pos < (MyActionsHelper.myDownloadSize + MyActionsHelper.localSize) ){
                        MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_download_local;
                    }else if(pos >= (MyActionsHelper.myDownloadSize + MyActionsHelper.localSize) ){
                        MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_new_local;
                    }

                    Map<String,Object> actionInfoMap = new HashMap<String, Object>();
                    ActionInfo actionInfo = new ActionInfo();
                    actionInfo.actionName = (String)actionList.get(MyActionsHelper.map_val_action_name);
                    actionInfo.hts_file_name = (String)actionList.get(MyActionsHelper.map_val_action);
                    actionInfoMap.put("map_val_action",actionInfo);
                    mHelper.startPlayAction(actionInfoMap,pos, MyActionsHelper.Action_type.Unkown);
//                }
            }
        });

        holder.playingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.stopPlayAction();
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String filePath = (String)actionList.get(MyActionsHelper.map_val_action);
                if(pos < MyActionsHelper.localSize){
                    MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.Unkown;
                    filePath = FileTools.action_robot_file_path + "/" + filePath;

                }else if(pos >= MyActionsHelper.localSize  &&  pos < (MyActionsHelper.myDownloadSize + MyActionsHelper.localSize) ){
                    MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_download_local;
                    filePath = FileTools.actions_download_robot_path + "/" + filePath;

                }else if(pos >= (MyActionsHelper.myDownloadSize + MyActionsHelper.localSize) ){
                    MyActionsHelper.mCurrentLocalPlayType = MyActionsHelper.Action_type.My_new_local;
                    filePath = FileTools.actions_creation_robot_path + "/" + filePath;
                }

                if(holder.swipeLayout.getStatus()== SlideView.Status.OPEN)
                {
                    holder.swipeLayout.close(true);
                }
                mHelper.mCurrentRemovedItem = pos;
                mHelper.doDelAction(filePath,MyActionsHelper.Action_type.Unkown);
            }
        });
    }

    /**
     *下载动作
     * */
    public static void fillDownLoadActionsContent(final Context mContext, RecyclerView.ViewHolder myHolder,
                                                  final Map<String,Object> actionList, final MyActionsHelper mHelper, final int pos)
    {
        final MyActionsRecyclerAdapter.MyActionsHolder holder  = (MyActionsRecyclerAdapter.MyActionsHolder) myHolder;
        UbtLog.d(TAG,"map_val_action_download_state = " + actionList.get(MyActionsHelper.map_val_action_download_state));
        if (actionList.get(MyActionsHelper.map_val_action_download_state) == MyActionsHelper.Action_download_state.download_finish
                || actionList.get(MyActionsHelper.map_val_action_download_state) == MyActionsHelper.Action_download_state.send_finish) {

            holder.txt_progress.setVisibility(View.GONE);
            holder.img_state.clearAnimation();
            holder.img_state.setImageDrawable(getDrawableRes(mContext,"my_actions_play_item_ft"));
            stopLoadingAnimation(holder,mContext);

        } else if (actionList.get(MyActionsHelper.map_val_action_download_state) == MyActionsHelper.Action_download_state.not_download) {
            holder.txt_progress.setVisibility(View.GONE);
            holder.img_state.clearAnimation();
            holder.img_state.setImageDrawable(getDrawableRes(mContext,"actions_online_download_ft"));
            stopLoadingAnimation(holder,mContext);

        } else if (actionList.get(MyActionsHelper.map_val_action_download_state) == MyActionsHelper.Action_download_state.downing) {

            holder.img_state.setImageDrawable(getDrawableRes(mContext,"actionlib_item_downloading_icon_ft"));
            holder.img_state.setVisibility(View.VISIBLE);
            if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                holder.txt_progress.setVisibility(View.GONE);
            }else {
                if (holder.txt_progress.getVisibility() != View.VISIBLE) {
                    holder.txt_progress.setVisibility(View.VISIBLE);
                }
                Double b = (Double) actionList.get(MyActionsHelper.map_val_action_download_progress);
                int i = (int) (b + 0);
                holder.txt_progress.setText(i + "%");
            }
            startLoadingAnimation(holder,mContext);
        }
        holder.txt_progress.setTextColor(getColorRes(mContext,"download_progress_text_color_ft"));

        UbtLog.d(TAG,"actionImagePath:" + ((ActionInfo) actionList
                .get(MyActionsHelper.map_val_action)).actionImagePath + "       actionHeadUrl: " + ((ActionInfo) actionList
                .get(MyActionsHelper.map_val_action)).actionHeadUrl);
        Glide.with(mContext)
                .load(((ActionInfo) actionList
                        .get(MyActionsHelper.map_val_action)).actionImagePath)
                .fitCenter().placeholder(R.drawable.sec_action_logo)
                .into(holder.img_action_logo);

        holder.rl_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.swipeLayout.getStatus()== SlideView.Status.OPEN)
                {
                    holder.swipeLayout.close(true);
                }else
                {
                    ActionInfo actionInfo =(ActionInfo) actionList.get(MyActionsHelper.map_val_action);
                    String actionString = GsonImpl.get().toJson(actionInfo);
                    ActionsLibPreviewWebActivity.launchActivity(mContext,actionInfo,getDownloadState(actionList),(double)actionList.get(
                            MyActionsHelper.map_val_action_download_progress));
                }
            }
        });

        holder.lay_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playDownloadAction(mContext,mHelper,actionList,pos);

            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.doDelAction(actionList.get(MyActionsHelper.map_val_action), MyActionsHelper.Action_type.My_download);
            }
        });

        holder.playingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.stopPlayAction();
            }
        });

        //recycler View need reset in here
        holder.iv_more.setImageDrawable(getDrawableRes(mContext,"icon_for_dub_ft"));
        holder.img_action_stop_play.setImageDrawable(getDrawableRes(mContext,"action_stop_inside_ft"));
        Drawable progressBarDrawable = getDrawableRes(mContext,"progress_playing_ft");
        progressBarDrawable.setBounds(1,1, (int) DataTools.dip2px(mContext, 30),(int) DataTools.dip2px(mContext, 30));
        holder.progress_bar.setIndeterminateDrawable(progressBarDrawable);

        holder.iv_more.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final MyActionsHelper.Action_download_state currentState = (MyActionsHelper.Action_download_state) actionList
                        .get(MyActionsHelper.map_val_action_download_state);
                new AlertView(null, null, ((BaseActivity)mContext).getStringResources("ui_common_cancel"), new String[]{((BaseActivity)mContext).getStringResources("ui_dub_for_actions"),
                        ((BaseActivity)mContext).getStringResources("ui_common_delete")/*,
                        ((BaseActivity)mContext).getStringResources("ui_common_cancel")*/},
                        null,
                        mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                    public void onItemClick(Object o,int position){
                        switch (position)
                        {
                            case 0:
                                mHelper.stopPlayAction();
                                ActionInfo actionInfo =(ActionInfo) actionList.get(MyActionsHelper.map_val_action);
                                DubActivity.launchActivity(mContext,  PG.convertParcelable(actionInfo), DubActivity.TYPE_DOWNLOAD,currentState.toString() );
                                break;
                            case 1:
                                mHelper.doDelAction(actionList.get(MyActionsHelper.map_val_action), MyActionsHelper.Action_type.My_download);                                break;
                            case 2:
                                //
                                break;
                        }

                    }
                }).show();
            }

        });

    }

    /**
     * 播放下载动作
     * @param mContext
     * @param mHelper
     * @param actionList
     * @param pos
     */
    public static void playDownloadAction(Context mContext,MyActionsHelper mHelper,Map<String,Object> actionList,int pos ){

        //执行播放逻辑
        if (mHelper.isLostCoon()) {
            RobotConnectedActivity.launchActivity(AlphaApplication.getBaseActivity(),true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
            return;
        }

        //检测是否在充电状态和边充边玩状态是否打开
//        if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(mContext)){
//            Toast.makeText(mContext, mContext.getResources().getString(R.string.ui_settings_play_during_charging_tips), Toast.LENGTH_SHORT).show();
//            return;
//        }

        if(isFirstPlayAction(mHelper)){
            return;
        }

        if ((Boolean) actionList.get(MyActionsHelper.map_val_action_is_playing)) {
            return;
        }

        MyActionsHelper.Action_download_state currentState = (MyActionsHelper.Action_download_state) actionList
                .get(MyActionsHelper.map_val_action_download_state);
        // 没有同步
        if (currentState == MyActionsHelper.Action_download_state.download_finish) {
//                    mCurrentSendActionItem = item;
            mHelper.stopPlayAction();

            if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){

                UbtLog.d(TAG,"actionList : " + actionList.get(MyActionsHelper.map_val_action_download_state));
                mHelper.doDownLoad(((ActionInfo) actionList.get(MyActionsHelper.map_val_action)),true);
                //mHelper.checkActionFileExist(actionList);
            }else {
                mHelper.mCurrentIntendtoPlay = pos;
                mHelper.sendActionFileToRobot(((ActionInfo) actionList.get(MyActionsHelper.map_val_action)));
            }
        }
        // 如果已经同步
        else if (currentState == MyActionsHelper.Action_download_state.send_finish) {
            mHelper.startPlayAction(actionList,pos, MyActionsHelper.Action_type.My_download);
            //mHelper.checkActionFileExist(actionList);
        }
    }

    /**
     *收藏动作
     * */
    public static void fillCollectActionsContent(final Context mContext, final RecyclerView.ViewHolder myHolder,
                                                 final Map<String,Object> actionList, final MyActionsHelper mHelper,final int pos)
    {
        MyActionsRecyclerAdapter.MyActionsHolder holder  = (MyActionsRecyclerAdapter.MyActionsHolder) myHolder;
        holder.img_state.setVisibility(View.INVISIBLE);
        Glide.with(mContext)
                .load(((ActionColloInfo) actionList
                        .get(MyActionsHelper.map_val_action)).collectImage)
                .fitCenter().placeholder(R.drawable.sec_action_logo)
                .into(holder.img_action_logo);
        holder.rl_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionColloInfo c_info = ((ActionColloInfo)
                        actionList.get(ActionsColloHelper.map_val_action));
                ActionInfo info = new ActionInfo();
                info.actionId = c_info.collectRelationId;
                info.actionDesciber = c_info.collectDescriber;
                info.actionName = c_info.collectName;
                if(TextUtils.isEmpty(c_info.extendInfo))
                {
                    mHelper.noteCollectDeleted(mContext,c_info,pos);
                    return;
                }
                Double d = (Double) actionList.get(
                        MyActionsHelper.map_val_action_download_progress);
                ActionsLibPreviewWebActivity.launchActivity(mContext, info,getDownloadState(actionList),d);
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.mCurrentRemovedItem = pos;
                UbtLog.d("wilson","delete position:"+pos);
                mHelper.doRemoveCollectWeb((ActionColloInfo) actionList.get(MyActionsHelper.map_val_action));
            }
        });

    }

    /**
     *创建动作
     * */
    public static void fillCreateActionsContent(final Context mContext, RecyclerView.ViewHolder myHolder, final Map<String,Object> actionList,final MyActionsHelper mHelper,final int pos)
    {
        final MyActionsRecyclerAdapter.MyActionsHolder holder  = (MyActionsRecyclerAdapter.MyActionsHolder) myHolder;
        final NewActionInfo actionInfo = (NewActionInfo) actionList.get(MyActionsHelper.map_val_action);

        Glide.with(mContext)
                .load(((NewActionInfo) actionList
                        .get(MyActionsHelper.map_val_action)).actionHeadUrl)
                .fitCenter().placeholder(R.drawable.sec_action_logo)
                .into(holder.img_action_logo);

          holder.lay_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playCreateAction(mContext,actionList,mHelper,pos);
            }
        });

        holder.rl_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyActionsHelper.Action_download_state currentState = (MyActionsHelper.Action_download_state) actionList
                        .get(MyActionsHelper.map_val_action_download_state);
                if(holder.swipeLayout.getStatus()== SlideView.Status.OPEN)
                {
                    holder.swipeLayout.close(true);
                }else
                {
                    switch (actionInfo.actionStatus)
                    {
                        //9 正在审核
                        //1 审核通过
                        //2 审核失败
                        case 9:
                        case 1:
//                        case 2:
                            ActionInfo actionInfo1 =(ActionInfo) actionList.get(MyActionsHelper.map_val_action);
                            actionInfo1.isFromCreate = true;
                            String actionString = GsonImpl.get().toJson(actionInfo1);
                            ActionsLibPreviewWebActivity.launchActivity((BaseActivity) mContext,actionInfo1,getDownloadState(actionList),100, Constant.FROM_CREATE_ACTION_TO_DETAIL);
                            //ActionsLibPreviewWebActivity.launchActivity(mContext,actionInfo1,getDownloadState(actionList),100);
                            break;
                        default:
                            ActionUnpublishedActivity.launchActivity(mContext, PG.convertParcelable(actionInfo),mHelper.getSchemeId(),mHelper.getSchemeName(),currentState.toString());
                            break;
                    }
                }
            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mHelper.mCurrentRemovedItem = pos;
                mHelper.doDelAction(actionList.get(MyActionsHelper.map_val_action), MyActionsHelper.Action_type.My_new);


            }
        });

        holder.playingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.stopPlayAction();
            }
        });

        //recycler View need reset in here
        holder.img_state.setImageDrawable(getDrawableRes(mContext,"my_actions_play_item_ft"));
        holder.iv_more.setImageDrawable(getDrawableRes(mContext,"icon_for_dub_ft"));
        holder.img_action_stop_play.setImageDrawable(getDrawableRes(mContext,"action_stop_inside_ft"));
        Drawable progressBarDrawable = getDrawableRes(mContext,"progress_playing_ft");
        progressBarDrawable.setBounds(1,1, (int) DataTools.dip2px(mContext, 30),(int) DataTools.dip2px(mContext, 30));
        holder.progress_bar.setIndeterminateDrawable(progressBarDrawable);

        //1/9 has pubish
        if(actionInfo.actionStatus == 1 || actionInfo.actionStatus == 9){
            holder.img_action_state.setVisibility(View.VISIBLE);
        }else {
            holder.img_action_state.setVisibility(View.GONE);
        }

        holder.iv_more.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final MyActionsHelper.Action_download_state currentState = (MyActionsHelper.Action_download_state) actionList
                        .get(MyActionsHelper.map_val_action_download_state);

                new AlertView(null, null, ((BaseActivity)mContext).getStringResources("ui_common_cancel"), new String[]{((BaseActivity)mContext).getStringResources("ui_dub_for_actions"),
                        ((BaseActivity)mContext).getStringResources("ui_common_delete")},
                        null,
                        mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                    public void onItemClick(Object o,int position){
                        switch (position)
                        {
                            case 0:
                                mHelper.stopPlayAction();
                                DubActivity.launchActivity(mContext,  PG.convertParcelable(actionInfo), DubActivity.TYPE_CREATE, currentState.toString());
                                break;
                            case 1:
                                mHelper.mCurrentRemovedItem = pos;
                                mHelper.doDelAction(actionList.get(MyActionsHelper.map_val_action), MyActionsHelper.Action_type.My_new);

                            case 2:
                                //TODO 删除该动作
                                break;
                        }

                    }
                }).show();
            }

        });
    }

    /**
     * 播放创建的动作
     * @param mContext
     * @param actionList
     * @param mHelper
     * @param pos
     */
    public static void playCreateAction(Context mContext, Map<String,Object> actionList,MyActionsHelper mHelper,int pos){

        //执行播放逻辑
        if (mHelper.isLostCoon()) {
            RobotConnectedActivity.launchActivity(AlphaApplication.getBaseActivity(),true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
            return;
        }

        //检测是否在充电状态和边充边玩状态是否打开
//        if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(mContext)){
//            Toast.makeText(mContext, mContext.getResources().getString(R.string.ui_settings_play_during_charging_tips), Toast.LENGTH_SHORT).show();
//            return;
//        }

        if(isFirstPlayAction(mHelper)){
            return;
        }

        if ((Boolean) actionList.get(MyActionsHelper.map_val_action_is_playing)) {
            return;
        }

        MyActionsHelper.Action_download_state currentState = (MyActionsHelper.Action_download_state) actionList
                .get(MyActionsHelper.map_val_action_download_state);
        // 没有同步
        if (currentState == MyActionsHelper.Action_download_state.download_finish) {
//                    mCurrentSendActionItem = item;
            mHelper.stopPlayAction();

            mHelper.mCurrentIntendtoPlay = pos;
            mHelper.sendActionFileToRobot(((NewActionInfo) actionList
                    .get(MyActionsHelper.map_val_action)));
        }
        // 如果已经同步
        else if (currentState == MyActionsHelper.Action_download_state.send_finish) {
            mHelper.startPlayAction(actionList,pos,MyActionsHelper.Action_type.My_new);
        }
    }


    private static boolean isFirstPlayAction(MyActionsHelper mHelper){
        if(mHelper.isFirstPlayAction()){
            mHelper.setIsFirstPlayAction();
            mHelper.showFirstPlayTip();
            return true;
        }else {
            return false;
        }
    }

    /***
     * my creation sync list
     * @param mContext
     * @param myHolder
     * @param actionList
     * @param mHelper
     * @param pos
     */

    public static void fillCreationSyncContent(final Context mContext, RecyclerView.ViewHolder myHolder, final Map<String,Object> actionList,
                                               final MyActionsHelper mHelper,final int pos,int type)
    {
        final ActionsSyncRecyclerAdapter.MySyncViewHolder holder  = (ActionsSyncRecyclerAdapter.MySyncViewHolder) myHolder;
        String actionImage = ((ActionInfo) actionList
                .get(MyActionsHelper.map_val_action)).actionHeadUrl;
        if(type == SYNC_DOWNLOAD_ACTIONS)
        {
            actionImage = ((ActionInfo) actionList
                    .get(MyActionsHelper.map_val_action)).actionImagePath;
        }
        Glide.with(mContext)
                .load(actionImage)
                .fitCenter().placeholder(R.drawable.sec_action_logo)
                .into(holder.img_action_logo);
        if((Boolean) actionList.get(MyActionsHelper.map_val_action_selected))
        {
            holder.img_select.setImageResource(R.drawable.mynew_actions_selected);
        }else {
            holder.img_select.setImageResource(R.drawable.myactions_normal);
        }
        View.OnClickListener listener  = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionList.put(MyActionsHelper.map_val_action_selected,!(Boolean) actionList.get(MyActionsHelper.map_val_action_selected));
                mHelper.notifyAdapters(pos);
            }
        };
        holder.img_select.setOnClickListener(listener);
        holder.rl_info.setOnClickListener(listener);



    }

    /***
     * my download sync list
     * @param mContext
     * @param myHolder
     * @param actionList
     * @param mHelper
     * @param pos
     */
    public static void fillDownloadSyncContent(final Context mContext, RecyclerView.ViewHolder myHolder, final Map<String,Object> actionList,final MyActionsHelper mHelper,final int pos)
    {

    }


    /**
     * 获取下载状态
     * */
    public static int getDownloadState(Map<String,Object> actionList)
    {

        int state = -1;
        if(actionList.get( MyActionsHelper.map_val_action_download_state) == MyActionsHelper.Action_download_state.not_download)
        {
            state = 0;
        }else if(actionList.get( MyActionsHelper.map_val_action_download_state) == MyActionsHelper.Action_download_state.downing){
            state = 2;
        }else if(actionList.get(MyActionsHelper.map_val_action_download_state) == MyActionsHelper.Action_download_state.download_finish
                || actionList.get(MyActionsHelper.map_val_action_download_state) == MyActionsHelper.Action_download_state.send_finish ){
            state = 1;
        }

        return  state;
    }

    private static void startLoadingAnimation(MyActionsRecyclerAdapter.MyActionsHolder holder,Context mContext)
    {
        if(holder.operatingAnim==null)
        {
            holder.operatingAnim = AnimationUtils.loadAnimation(mContext,
                    R.anim.turn_around_anim);
            holder.operatingAnim.setInterpolator(new LinearInterpolator());
        }
        if(!holder.operatingAnim.hasStarted())
            holder.img_state.startAnimation(holder.operatingAnim);
    }

    private static void stopLoadingAnimation(MyActionsRecyclerAdapter.MyActionsHolder holder, Context mContext)
    {
        if(holder.operatingAnim!=null&&holder.operatingAnim.hasStarted())
        {
            holder.img_state.clearAnimation();
            holder.operatingAnim.cancel();
            holder.operatingAnim = null;
        }

    }

    /**
     * 动态获取图片
     * @param DrawableKey 图片Key
     * @return
     */
    private static Drawable getDrawableRes(Context mActivity, String DrawableKey){
        return ((BaseActivity)mActivity).getDrawableRes(DrawableKey);
    }

    /**
     * 动态获取颜色值
     * @param colorKey 颜色值Key
     * @return
     */
    public static ColorStateList getColorRes(Context mActivity, String colorKey){
        return ((BaseActivity)mActivity).getColorRes(colorKey);
    }
}
