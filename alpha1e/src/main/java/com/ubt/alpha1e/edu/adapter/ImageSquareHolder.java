package com.ubt.alpha1e.edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;

/**
 * Created by Administrator on 2016/6/13.
 */
public class ImageSquareHolder extends RecyclerView.ViewHolder
{
    public LinearLayout ly_detail_square;
    public ImageView img_play,img_square_profile,img_square_item_like,img_square_item_star,
            img_square_item_share,img_square_item_down,img_content;
    public TextView txt_square_description,txt_profile_name,txt_sees,
            txt_comments,txt_square_item_like,txt_square_item_star,txt_square_item_share,txt_square_item_down;

    public ImageSquareHolder(View v)
    {
        super(v);
        ly_detail_square = (LinearLayout)v.findViewById(R.id.ly_detail_square);
        img_content = (ImageView) v.findViewById(R.id.img_square_content);
        img_play = (ImageView)v.findViewById(R.id.img_play_actions_square);
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

    }


}
