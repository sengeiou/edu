package com.ubt.alpha1e.edu.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.MessageInfo;
import com.ubt.alpha1e.edu.data.model.MessageSubInfo;
import com.ubt.alpha1e.edu.ui.ActionsLibPreviewWebActivity;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.helper.ActionsOnlineHelper;
import com.ubt.alpha1e.edu.ui.helper.MessageHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.Map;

/**
 * Created by Administrator on 2016/6/12.
 */
public class FillMessageContent {

    private static final String TAG = "FillMessageContent";

    /**
     * 填充动作广场列表
     */
    public static void fillMessageContent(final BaseActivity activity,final Context mContext, final MessageHolder holder,
                                               final MessageInfo messageInfo,
                                               final MessageHelper mHelper,
                                               final int position,boolean isScolling) {
        final MessageSubInfo subInfo = messageInfo.subInfo;

        if(subInfo != null){

            Glide.with(mContext.getApplicationContext()).load(subInfo.userImage).centerCrop().placeholder(R.drawable.register_login_default_head).into(holder.img_message_appraiser_head);

            holder.txt_message_reontent.setVisibility(View.GONE);
            if(messageInfo.messageType == 3){//评论
                //UbtLog.d(TAG,"subInfo.commentContext == " + subInfo.commentContext + "     " + subInfo.reCommentContext);

                holder.txt_message_appraiser_name.setText(subInfo.userName);
                holder.txt_reply_content.setVisibility(View.VISIBLE);
                holder.txt_reply_content.setText(subInfo.commentContext.trim().replaceAll("\n"," "));
                holder.txt_message_appraiser_time.setText(subInfo.commentTime);

                if(!TextUtils.isEmpty(subInfo.reCommentContext)){

                    String reContent = subInfo.reCommentUser + " : " + subInfo.reCommentContext.trim().replaceAll("\n"," ");
                    SpannableString style=new SpannableString(reContent);
                    style.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.T1)),0,(subInfo.reCommentUser+"").length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    style.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.T2)),(subInfo.reCommentUser+"").length() + 2, reContent.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    holder.txt_message_reontent.setText(style);
                    holder.txt_message_reontent.setVisibility(View.VISIBLE);
                }
            }else {
                holder.txt_reply_content.setVisibility(View.GONE);
                holder.txt_message_appraiser_name.setText(subInfo.userName + " " + activity.getStringResources("ui_message_like"));
                holder.txt_message_appraiser_time.setText(subInfo.collectTime);
            }

            holder.img_message_video_play.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(subInfo.actionPath)){
                //UbtLog.d(TAG,"subInfo.actionPath : " + subInfo.actionPath);
                Glide.with(mContext.getApplicationContext()).load(subInfo.actionHeadUrl).fitCenter().placeholder(R.drawable.sec_action_logo).into(holder.img_message_public_logo);
                holder.img_message_public_logo.setVisibility(View.VISIBLE);

            }else if(!TextUtils.isEmpty(subInfo.actionVideoPath)){
                String actionVideoPath = subInfo.actionVideoPath;
                if(!TextUtils.isEmpty(actionVideoPath)){
                    actionVideoPath = actionVideoPath.replaceAll(" ","%20") + "?vframe/jpg/offset/0";
                }
                //UbtLog.d(TAG,"subInfo.actionVideoPath : " + actionVideoPath);
                Glide.with(mContext.getApplicationContext()).load(actionVideoPath).fitCenter().into(holder.img_message_public_logo);
                holder.img_message_public_logo.setVisibility(View.VISIBLE);

                holder.img_message_video_play.setImageResource(R.drawable.actions_square_detail_play);
                holder.img_message_video_play.setVisibility(View.VISIBLE);
            }else if(!TextUtils.isEmpty(subInfo.actionImagePath)){
                UbtLog.d(TAG,"subInfo.actionImagePath : " + subInfo.actionImagePath);
                Glide.with(mContext.getApplicationContext()).load(subInfo.actionImagePath).fitCenter().into(holder.img_message_public_logo);
                holder.img_message_public_logo.setVisibility(View.VISIBLE);
            }else {
                holder.img_message_public_logo.setVisibility(View.GONE);
            }


            holder.txt_message_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoMessageDetail(activity,subInfo);
                    //mHelper.updateActionOnlineCache(info);
                }
            });

            holder.rl_message_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoMessageDetail(activity,subInfo);
                    //mHelper.updateActionOnlineCache(info);
                }
            });

            holder.rl_message_public_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoMessageDetail(activity,subInfo);
                    //mHelper.updateActionOnlineCache(info);
                }
            });

            fillPublicContent(subInfo,activity,holder);
        }

    }

    public static void gotoMessageDetail(BaseActivity activity,MessageSubInfo subInfo){
        ActionInfo info = new ActionInfo();
        info.actionId = subInfo.actionId;
        info.actionDesciber = "";
        info.actionName = "";
        int state = 0;
        UbtLog.d(TAG,"subInfo.actionId : " + subInfo.actionId);
        ActionsLibPreviewWebActivity.launchActivity(activity, info, state, -1);
    }

    /**
     * 填充发布的内容
     * @param subInfo
     * @param activity
     * @param holder
     */
    private static void fillPublicContent(MessageSubInfo subInfo,BaseActivity activity,MessageHolder holder){
        //用户名 + 一个冒号与两个空格 + 发布文字描述
        String publicContent = subInfo.actionUserName + " : " ;
        if(!TextUtils.isEmpty(subInfo.actionName)){
            publicContent = publicContent + "#" + subInfo.actionName + "# " + subInfo.actionResume;
        }else if(!TextUtils.isEmpty(subInfo.actionResume)) {
            publicContent = publicContent + subInfo.actionResume;
        }else if(!TextUtils.isEmpty(subInfo.actionVideoPath)){
            publicContent = publicContent + activity.getStringResources("ui_message_share_video");
        }else if(!TextUtils.isEmpty(subInfo.actionImagePath)){
            publicContent = publicContent + activity.getStringResources("ui_message_share_photo");
        }

        SpannableString style=new SpannableString(publicContent);
        style.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.T1)),0,subInfo.actionUserName.length() + 2 , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.T2)),subInfo.actionUserName.length() + 2, publicContent.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.txt_message_public_content.setText(style);
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

}
