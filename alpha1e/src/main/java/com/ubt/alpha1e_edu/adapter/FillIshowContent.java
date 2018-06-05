package com.ubt.alpha1e_edu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.Constant;
import com.ubt.alpha1e_edu.data.TimeTools;
import com.ubt.alpha1e_edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e_edu.ui.ActionsLibPreviewWebActivity;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.ui.ImageDetailActivity;
import com.ubt.alpha1e_edu.ui.MyActionsActivity;
import com.ubt.alpha1e_edu.ui.MyDynamicActivity;
import com.ubt.alpha1e_edu.ui.RobotConnectedActivity;
import com.ubt.alpha1e_edu.ui.VideoPlayerActivity;
import com.ubt.alpha1e_edu.ui.dialog.AlertDialog;
import com.ubt.alpha1e_edu.ui.dialog.alertview.AlertView;
import com.ubt.alpha1e_edu.ui.dialog.alertview.OnItemClickListener;
import com.ubt.alpha1e_edu.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e_edu.ui.helper.ActionsOnlineHelper;
import com.ubt.alpha1e_edu.ui.helper.SettingHelper;
import com.ubt.alpha1e_edu.utils.ResourceUtils;
import com.ubt.alpha1e_edu.utils.TimeUtils;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/6/12.
 */
public class FillIshowContent {

    private static final String TAG = "FillIshowContent";

    /**
     * 填充动作广场列表
     */
    public static void fillIshowSquareContent(final Activity activity,final Context mContext, final IshowSquareHolder holder,
                                               final Map<String, Object> actionInfo,
                                               final ActionsOnlineHelper mHelper,
                                               final int position,boolean isScolling) {

        final ActionOnlineInfo info = (ActionOnlineInfo) actionInfo.get(ActionsLibHelper.map_val_action);


        if(isStringNumber(info.actionDate)){
            holder.txt_public_time.setText(TimeUtils.format(Long.parseLong(info.actionDate)));
        }

        //显示图片视频区域
        if(TextUtils.isEmpty(info.actionVideoPath) && TextUtils.isEmpty(info.actionImagePath)){
            holder.rl_square_main.setVisibility(View.GONE);
        }else{
            holder.rl_square_main.setVisibility(View.VISIBLE);
            if (holder.getItemViewType() == 0)//video
            {
                holder.img_square_content.setVisibility(View.GONE);
                holder.img_play.setVisibility(View.VISIBLE);
                holder.mVideoView.setVisibility(View.VISIBLE);

                String actionVideoPath = info.actionVideoPath;
                if(!TextUtils.isEmpty(actionVideoPath)){
                    actionVideoPath = actionVideoPath.replaceAll(" ","%20") + "?vframe/jpg/offset/0";
                }
                //UbtLog.d(TAG,"actionVideoPath = " + actionVideoPath);
                Glide.with(mContext).load(actionVideoPath).crossFade().into(holder.mVideoView);

                holder.img_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VideoPlayerActivity.launchActivity(mContext,info);
                    }
                });
            } else {
                holder.img_square_content.setVisibility(View.VISIBLE);
                holder.img_play.setVisibility(View.GONE);
                holder.mVideoView.setVisibility(View.GONE);

                holder.img_square_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageDetailActivity.launchActivity(mContext,info.actionImagePath);
                    }
                });

                //UbtLog.d(TAG,"lihai--------userName::"+info.userName + "   actionName:"+info.actionName + " actionDesciber:"+info.actionDesciber + "  actionImagePath:"+ info.actionImagePath);

                if(!isScolling){
                    //Glide 有缓存，只有第一次会请求网络
                    Glide.with(mContext.getApplicationContext()).load(info.actionImagePath).crossFade().into(holder.img_square_content);
                }else {
                    holder.img_square_content.setImageDrawable(null);
                }
            }
        }

        if(!isScolling){
            Glide.with(mContext.getApplicationContext()).load(info.userImage).centerCrop().placeholder(R.drawable.register_login_default_head).into(holder.img_square_profile);
        }else {
            //活动过程中，设置默认占位符
            holder.img_square_profile.setImageResource(R.drawable.register_login_default_head);
            //UbtLog.d(TAG,"holder.img_square_profile ==>> " + holder.img_square_profile);
            //Glide.with(mContext.getApplicationContext()).load(info.userImage).centerCrop().thumbnail(0.1f).into(holder.img_square_profile);
        }

        //置顶
        if(info.actionHot != 0){
            holder.img_action_hot.setVisibility(View.VISIBLE);
        }else {
            holder.img_action_hot.setVisibility(View.GONE);
        }

        if(info.actionCollectTime <= 0){
            if(info.isCollect == 1){
                info.actionCollectTime = 1;
            }else {
                info.actionCollectTime = 0;
            }
        }
        holder.txt_ishow_comment.setText(info.actionCommentTime>=9999?"9999+":info.actionCommentTime +"");
        holder.txt_profile_name.setText(info.userName);

        //显示描述
        String description = info.actionResume == null?info.actionDesciber:info.actionResume;
        if(TextUtils.isEmpty(description)){
            holder.txt_square_description.setVisibility(View.GONE);
        }else {
            holder.txt_square_description.setVisibility(View.VISIBLE);
            holder.txt_square_description.setText(info.actionResume == null?info.actionDesciber:info.actionResume);
        }

        holder.img_square_collect.setImageResource(info.isCollect == 1 ?
                R.drawable.actions_square_detail_favorite_s : R.drawable.actions_square_detail_favorite_ishow);
        holder.txt_square_collect.setText(info.actionCollectTime+ "");

        //显示动作
        if(TextUtils.isEmpty(info.actionName)){
            holder.rl_square_action_detail.setVisibility(View.GONE);
        }else{
            holder.rl_square_action_detail.setVisibility(View.VISIBLE);
            if(!isScolling) {
                Glide.with(mContext.getApplicationContext())
                        .load(info.actionHeadUrl)
                        .fitCenter().placeholder(R.drawable.sec_action_logo)
                        .into(holder.img_action_logo);
            }else {
                holder.img_action_logo.setImageDrawable(null);
            }

            holder.txt_action_name.setText(info.actionName);
            holder.txt_des.setText(info.actionDesciber);
            holder.img_type_logo.setImageResource(ResourceUtils.getActionTypeImage(info.actionSonType,(BaseActivity) activity));
            holder.txt_type_des.setText(ResourceUtils.getActionType(info.actionSonType,(BaseActivity) activity));
            holder.txt_time.setText(TimeTools.getMMTime((int)info.actionTime * 1000) +"");
            holder.txt_sees.setText(info.actionBrowseTime + "");
            holder.txt_square_item_down_num.setText(info.actionDownloadTime + "");

            holder.txt_square_item_down_num.setTextColor(getColorRes(mContext,"download_number_text_color_ft"));
            holder.txt_progress.setTextColor(getColorRes(mContext,"download_progress_text_color_ft"));

            if (actionInfo.get(ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.download_finish) {
                holder.txt_progress.setVisibility(View.GONE);
                holder.img_state.clearAnimation();
                holder.img_state.setImageDrawable(getDrawableRes(mContext,"my_actions_play_item_ft"));
                stopLoadingAnimation(holder, mContext);

            } else if (actionInfo.get(ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.not_download) {
                holder.txt_progress.setVisibility(View.GONE);
                holder.img_state.clearAnimation();
                holder.img_state.setImageDrawable(getDrawableRes(mContext,"actions_online_download_ft"));
                stopLoadingAnimation(holder, mContext);

            } else if (actionInfo.get(ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.downing) {
                holder.img_state.setImageDrawable(getDrawableRes(mContext,"actionlib_item_downloading_icon_ft"));

                if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                    holder.txt_progress.setVisibility(View.GONE);
                }else {
                    holder.txt_progress.setVisibility(View.VISIBLE);
                    Double b = (Double) actionInfo.get(
                            ActionsLibHelper.map_val_action_download_progress);
                    int i = (int) (b + 0);
                    if(i < 0){
                        i = 0;
                    }
                    holder.txt_progress.setText(i + "%");
                }

                startLoadingAnimation(holder, mContext);
            }

            /**点击下载*/
            holder.img_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionsOnlineHelper.Action_download_state current_state =
                            (ActionsOnlineHelper.Action_download_state) actionInfo
                                    .get(ActionsOnlineHelper.map_val_action_download_state);
                    if (current_state != ActionsOnlineHelper.Action_download_state.not_download) {
                        if (current_state == ActionsOnlineHelper.Action_download_state.download_finish) {
                            if (mHelper.isLostCoon()) {
                                RobotConnectedActivity.launchActivity(activity,true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                            } else {
                                if(mHelper.isLogin()){
                                    //检测是否在充电状态和边充边玩状态是否打开
                                    if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(mContext)){
                                        Toast.makeText(mContext, AlphaApplication.getBaseActivity().getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    MyActionsActivity.launchActivity(mContext,1,info.actionId);
                                }
                            }
                        }
                        return;
                    }

                    actionInfo.put(ActionsOnlineHelper.map_val_action_download_state,
                            ActionsOnlineHelper.Action_download_state.downing);
                    mHelper.doUpdateItem(position);
                    mHelper.doDownLoad(info);
                }
            });

            holder.rl_square_action_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UbtLog.d(TAG,"info1 = " + ActionOnlineInfo.getModelStr(info));
                    ActionsLibPreviewWebActivity.launchActivity(activity, info,getDownloadState(actionInfo),(double)actionInfo.get(
                            ActionsOnlineHelper.map_val_action_download_progress),10086);
                    info.actionBrowseTime++;
                    mHelper.updateActionOnlineCache(info);
                }
            });
        }

        if(activity instanceof MyDynamicActivity){
            holder.ivDeleteDynamic.setVisibility(View.VISIBLE);
            holder.ivDeleteDynamic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    new AlertView(null, null, ((BaseActivity)mContext).getStringResources("ui_common_cancel"), new String[]{((BaseActivity)mContext).getStringResources("ui_common_delete"),
                    },
                            null,
                            mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                        public void onItemClick(Object o,int position){
                            switch (position)
                            {
                                case 0:
                                    new AlertDialog(mContext).builder().setMsg(((BaseActivity)mContext).getStringResources("ui_dynamic_delete_warning")).setCancelable(true).
                                            setPositiveButton(((BaseActivity)mContext).getStringResources("ui_common_confirm"), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    mHelper.deleteMyDynamic(info);
                                                }
                                            }).setNegativeButton(((BaseActivity)mContext).getStringResources("ui_common_cancel"), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).show();

                                    break;
                                case 1:
                                    break;
                            }

                        }
                    }).show();
                }
            });
        }else{
            holder.ivDeleteDynamic.setVisibility(View.GONE);
        }

        /**点击收藏*/
        holder.img_square_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.isCollect == 0) {
                    if(mHelper.doCollocatWeb(info))
                    {
                        holder.img_square_collect.setImageResource(R.drawable.actions_square_detail_favorite_s);
                    }
                } else {
                    if(mHelper.doRemoveCollectWeb(info))
                    {
                        holder.img_square_collect.setImageResource(R.drawable.actions_square_detail_favorite_ishow);
                    }
                }
                holder.txt_square_collect.setText(info.actionCollectTime + "");
//                mHelper.doUpdateItem(position);

            }
        });

        /**点击分享*/
        holder.rl_ishow_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.doShareToOthers(info);

            }
        });

        holder.rl_square_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionsLibPreviewWebActivity.launchActivity(activity, info,getDownloadState(actionInfo),(double)actionInfo.get(
                        ActionsOnlineHelper.map_val_action_download_progress),10086);
                info.actionBrowseTime++;
                mHelper.updateActionOnlineCache(info);
            }
        });

    }

    private static void startLoadingAnimation(IshowSquareHolder holder, Context mContext) {
        if (holder.operatingAnim == null) {
            holder.operatingAnim = AnimationUtils.loadAnimation(mContext,
                    R.anim.turn_around_anim);
            holder.operatingAnim.setInterpolator(new LinearInterpolator());
        }
        if (!holder.operatingAnim.hasStarted()){
            holder.img_state.startAnimation(holder.operatingAnim);
        }

    }

    private static void stopLoadingAnimation(IshowSquareHolder holder, Context mContext) {
        if (holder.operatingAnim != null && holder.operatingAnim.hasStarted()) {
            holder.img_state.clearAnimation();
            holder.operatingAnim.cancel();
            holder.operatingAnim = null;
        }

    }


    /**
     * 获取下载状态
     * */
    public static int getDownloadState(Map<String,Object> actionList)
    {

        int state = -1;
        if(actionList.get( ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.download_finish)
        {
            state = 1;
        }else if(actionList.get( ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.not_download)
        {
            state = 0;
        }else if(actionList.get( ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.downing){
            state = 2;
        }
        return  state;
    }

    /***
     * 判断字符串是否都是数字
     */
    public static boolean isStringNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
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
