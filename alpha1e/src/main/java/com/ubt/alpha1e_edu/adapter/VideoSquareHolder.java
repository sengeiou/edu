package com.ubt.alpha1e_edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;

/**
 * Created by Administrator on 2016/6/13.
 */
public class VideoSquareHolder extends RecyclerView.ViewHolder
{
    public LinearLayout ly_detail_square;
    public RelativeLayout rl_like,rl_collect,rl_share,rl_down;
    public ImageView mVideoView,img_official;
    public ImageView img_play,img_square_profile,img_square_item_like,img_square_item_star,
            img_square_item_share,img_square_item_down,img_square_content;
    public TextView txt_square_description,txt_profile_name,txt_sees,
            txt_comments,txt_square_item_like,txt_square_item_star,
            txt_square_item_share,txt_square_item_down,txt_progress,txt_official;
    public Animation operatingAnim;
    //public HashMap<String,Bitmap> thumbnailMap = new HashMap<>();

    public VideoSquareHolder(View v)
    {
        super(v);
        rl_like = (RelativeLayout)v.findViewById(R.id.rl_actions_square_like);
        rl_collect = (RelativeLayout)v.findViewById(R.id.rl_actions_square_collect);
        rl_share = (RelativeLayout)v.findViewById(R.id.rl_actions_square_share);
        rl_down = (RelativeLayout)v.findViewById(R.id.rl_actions_square_down);
        ly_detail_square = (LinearLayout)v.findViewById(R.id.ly_detail_square);
        mVideoView = (ImageView)v.findViewById(R.id.video_actions_square);
        img_square_content =(ImageView)v.findViewById(R.id.img_square_content);
        img_play = (ImageView)v.findViewById(R.id.img_play_actions_square);
        img_official = (ImageView)v.findViewById(R.id.img_ubt);
        img_square_profile = (ImageView) v.findViewById(R.id.img_square_profile);
        img_square_item_like = (ImageView)v.findViewById(R.id.img_square_item_like);
        img_square_item_star = (ImageView)v.findViewById(R.id.img_square_item_star);
        img_square_item_share = (ImageView)v.findViewById(R.id.img_square_item_share);
        img_square_item_down = (ImageView)v.findViewById(R.id.img_square_item_down);
        txt_square_description = (TextView)v.findViewById(R.id.txt_square_description);
        txt_profile_name = (TextView)v.findViewById(R.id.txt_profile_name);
        txt_sees = (TextView)v.findViewById(R.id.txt_action_browse);
        txt_comments = (TextView)v.findViewById(R.id.txt_comments);
        txt_square_item_like = (TextView)v.findViewById(R.id.txt_square_item_like);
        txt_square_item_star = (TextView)v.findViewById(R.id.txt_square_item_star);
        txt_square_item_share = (TextView)v.findViewById(R.id.txt_square_item_share);
        txt_square_item_down = (TextView)v.findViewById(R.id.txt_square_item_down);
        txt_progress = (TextView)v.findViewById(R.id.txt_progress);
        txt_official = (TextView)v.findViewById(R.id.txt_ubt);
    }




}
