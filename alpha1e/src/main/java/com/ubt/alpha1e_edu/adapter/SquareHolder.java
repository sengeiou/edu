package com.ubt.alpha1e_edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.ui.custom.SlideView;

/**
 * Created by Administrator on 2016/6/13.
 */
public class SquareHolder extends RecyclerView.ViewHolder
{

    public ImageView mVideoView;
    public ImageView img_play,img_square_content;
    public Animation operatingAnim;

    public ImageView img_action_logo, img_type_logo, img_state,img_resources,img_square_collect;
    public SlideView swipeLayout;//滑动视图
    public TextView txt_action_name, txt_time, txt_des, txt_type_des, txt_sees, txt_progress,txt_square_item_down_num,txt_square_collect;
    public RelativeLayout layout_square_action_detail,rl_square_content_default,layout_square_main;
    public View action_square_action_split,action_square_item_split;

    public SquareHolder(View v)
    {
        super(v);

        mVideoView = (ImageView)v.findViewById(R.id.video_actions_square);
        img_play = (ImageView)v.findViewById(R.id.img_play_actions_square);
        img_square_content =(ImageView)v.findViewById(R.id.img_square_content);

        img_action_logo = (ImageView) v.findViewById(R.id.action_logo);
        img_type_logo = (ImageView) v.findViewById(R.id.img_type_logo);
        img_state = (ImageView) v.findViewById(R.id.img_state);
        img_resources = (ImageView) v.findViewById(R.id.img_action_type);
        img_square_collect = (ImageView) v.findViewById(R.id.img_square_collect);
        txt_action_name = (TextView) v.findViewById(R.id.txt_action_name);
        txt_time = (TextView) v.findViewById(R.id.txt_time);
        txt_des = (TextView) v.findViewById(R.id.txt_disc);
        txt_type_des = (TextView) v.findViewById(R.id.txt_type_des);
        txt_sees = (TextView) v.findViewById(R.id.txt_sees);
        txt_square_item_down_num = (TextView) v.findViewById(R.id.txt_square_item_down_num);
        txt_progress = (TextView) v.findViewById(R.id.txt_progress);
        txt_square_collect = (TextView) v.findViewById(R.id.txt_square_collect);
        layout_square_action_detail = (RelativeLayout) v.findViewById(R.id.layout_square_action_detail);
        rl_square_content_default = (RelativeLayout) v.findViewById(R.id.rl_square_content_default);
        layout_square_main = (RelativeLayout) v.findViewById(R.id.layout_square_main);

        action_square_action_split = v.findViewById(R.id.action_square_action_split);
        action_square_item_split = v.findViewById(R.id.action_square_item_split);

        swipeLayout =(SlideView)v.findViewById(R.id.swipe);

    }




}
