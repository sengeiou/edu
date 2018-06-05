package com.ubt.alpha1e.edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;

/**
 * Created by Administrator on 2016/6/13.
 */
public class MessageHolder extends RecyclerView.ViewHolder
{
    public ImageView img_message_appraiser_head,img_message_public_logo,img_message_video_play;
    public TextView txt_message_appraiser_name,txt_message_appraiser_time,txt_reply_content,txt_message_public_content,txt_message_reply,txt_message_reontent;
    public RelativeLayout rl_message_info,rl_message_public_content;

    public MessageHolder(View v)
    {
        super(v);

        img_message_appraiser_head =(ImageView)v.findViewById(R.id.img_message_appraiser_head);
        img_message_public_logo =(ImageView)v.findViewById(R.id.img_message_public_logo);
        img_message_video_play =(ImageView)v.findViewById(R.id.img_message_video_play);

        txt_message_appraiser_name = (TextView)v.findViewById(R.id.txt_message_appraiser_name);
        txt_message_appraiser_time = (TextView)v.findViewById(R.id.txt_message_appraiser_time);
        txt_reply_content = (TextView)v.findViewById(R.id.txt_reply_content);
        txt_message_public_content = (TextView)v.findViewById(R.id.txt_message_public_content);
        txt_message_reply = (TextView)v.findViewById(R.id.txt_message_reply);
        txt_message_reontent = (TextView)v.findViewById(R.id.txt_message_reontent);
        rl_message_info = (RelativeLayout) v.findViewById(R.id.rl_message_info);
        rl_message_public_content = (RelativeLayout) v.findViewById(R.id.rl_message_public_content);

    }

}
