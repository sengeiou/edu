package com.ubt.alpha1e.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;

/**
 * Created by Administrator on 2016/6/13.
 */
public class IshowSquareHolder extends RecyclerView.ViewHolder
{
    public ImageView mVideoView;
    public ImageView img_play,img_square_profile,img_square_content;
    public TextView txt_square_description,txt_profile_name;
    public Animation operatingAnim;

    public ImageView img_action_logo, img_type_logo, img_state,img_resources,img_square_collect,img_action_hot;
    public RelativeLayout rl_ishow_share;
    public TextView txt_action_name, txt_time, txt_des, txt_type_des, txt_sees, txt_progress,txt_square_item_down_num,txt_square_collect,txt_ishow_comment,txt_public_time;
    public RelativeLayout rl_square_action_detail,rl_square_main,rl_square_info;


    public ImageView ivDeleteDynamic;

    public IshowSquareHolder(View v)
    {
        super(v);

        mVideoView = (ImageView)v.findViewById(R.id.video_actions_square);
        img_square_content =(ImageView)v.findViewById(R.id.img_square_content);
        img_play = (ImageView)v.findViewById(R.id.img_play_actions_square);
        img_square_profile = (ImageView) v.findViewById(R.id.img_square_profile);

        txt_square_description = (TextView)v.findViewById(R.id.txt_square_description);
        txt_profile_name = (TextView)v.findViewById(R.id.txt_profile_name);

        txt_public_time = (TextView)v.findViewById(R.id.txt_actions_public_time);

        img_action_logo = (ImageView) v.findViewById(R.id.action_logo);
        img_type_logo = (ImageView) v.findViewById(R.id.img_type_logo);
        img_state = (ImageView) v.findViewById(R.id.img_state);
        img_resources = (ImageView) v.findViewById(R.id.img_action_type);
        img_square_collect = (ImageView) v.findViewById(R.id.img_square_collect);
        img_action_hot = (ImageView) v.findViewById(R.id.img_action_hot);
        txt_action_name = (TextView) v.findViewById(R.id.txt_action_name);
        txt_time = (TextView) v.findViewById(R.id.txt_time);
        txt_des = (TextView) v.findViewById(R.id.txt_disc);
        txt_type_des = (TextView) v.findViewById(R.id.txt_type_des);
        txt_sees = (TextView) v.findViewById(R.id.txt_sees);
        txt_ishow_comment = (TextView) v.findViewById(R.id.txt_ishow_comment);
        txt_square_item_down_num = (TextView) v.findViewById(R.id.txt_square_item_down_num);
        txt_progress = (TextView) v.findViewById(R.id.txt_progress);
        txt_square_collect = (TextView) v.findViewById(R.id.txt_square_collect);
        rl_ishow_share = (RelativeLayout) v.findViewById(R.id.rl_ishow_share);
        rl_square_action_detail = (RelativeLayout) v.findViewById(R.id.layout_square_action_detail);
        rl_square_main = (RelativeLayout) v.findViewById(R.id.layout_square_main);
        rl_square_info = (RelativeLayout) v.findViewById(R.id.rl_square_info);

        ivDeleteDynamic = (ImageView) v.findViewById(R.id.iv_delete_dynamic);

    }




}
