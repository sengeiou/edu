package com.ubt.alpha1e.action.actioncreate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.model.FrameActionInfo;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.List;
import java.util.Map;

/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class FrameRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FrameRecycleViewAdapter";
    private Context context;
    private List<Map<String, Object>> datas;
    private FrameViewHolder viewHolder;


    private int defItem = -1;
    private OnItemListener onItemListener;

    private int zoom = 1;

    private float density  = 1;

    private int changeTime = 10;

    private int playIndex = -1;

    private int musicTime = 0;

    OnchangeCurrentItemTimeListener onchangeCurrentItemTimeListener;


    public FrameRecycleViewAdapter(Context context, List<Map<String, Object>> datas, float density, OnchangeCurrentItemTimeListener onchangeCurrentItemTimeListener) {
        this.context = context;
        this.datas = datas;
        this.density = density;
        this.onchangeCurrentItemTimeListener = onchangeCurrentItemTimeListener;

    }


    //define interface
    public  interface OnItemListener {
        void onItemClick(View view, int pos, Map<String, Object> data);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public void setDefSelect(int position) {
        this.defItem = position;
        notifyDataSetChanged();
    }

    public void setPlayIndex(int index){
        this.playIndex = index;
        notifyDataSetChanged();
    }

    public int getTime(){
        return  changeTime;
    }

    public void setTime() {
        changeTime  = 10;
    }

    public void scaleItem(int scale){
        zoom = scale;
        notifyDataSetChanged();
    }

    public void setMusicTime(int time) {
        musicTime = time;
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UbtLog.d(TAG, "onCreateViewHolder");


        View view = LayoutInflater.from(context).inflate(R.layout.layout_new_action_item_bak_1b, parent, false);

        viewHolder = new FrameViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UbtLog.d(TAG, "onBindViewHolder");

        final FrameViewHolder viewHolder = (FrameViewHolder)holder;
        FrameActionInfo info = ((FrameActionInfo) datas
                .get(position).get(ActionsEditHelper.MAP_FRAME));
        String time = String.valueOf(info.totle_time);
        UbtLog.d(TAG, "onBindViewHolder time:" + time);
        changeViewLen(viewHolder.rlRoot,zoom, time);

        if(musicTime>0){
            if(position == datas.size()-1){
                viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_trans);
                viewHolder.tv_frame_time.setVisibility(View.INVISIBLE);
                viewHolder.tv_frame_index.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_ba);
                viewHolder.tv_frame_time.setVisibility(View.VISIBLE);
                viewHolder.tv_frame_index.setVisibility(View.VISIBLE);
            }

            if (defItem != -1) {
                if (defItem == position) {
                    UbtLog.d(TAG, "onBindViewHolder select");
                    viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_ba_on);
                    viewHolder.rlNormal.setVisibility(View.GONE);
                    viewHolder.rlEditLayout.setVisibility(View.VISIBLE);
                    changeViewLen(viewHolder.rlRoot, 1, "1000");
                    changeViewLen(viewHolder.timeView,1, time);
                    viewHolder.sbChange.setProgress(Integer.valueOf(time)/10);
                } else {
                    UbtLog.d(TAG, "onBindViewHolder select no");
                    if(position == datas.size()-1){
                        viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_trans);
                    }else{
                        viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_ba);
                    }

                    viewHolder.rlNormal.setVisibility(View.VISIBLE);
                    viewHolder.rlEditLayout.setVisibility(View.GONE);

                }


            }else{
                UbtLog.d(TAG, "no select");

                UbtLog.d(TAG, "is 222");
                if(position == datas.size()-1){
                    viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_trans);
                }else{
                    viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_ba);
                }

                viewHolder.rlNormal.setVisibility(View.VISIBLE);
                viewHolder.rlEditLayout.setVisibility(View.GONE);
            }



        }else{
            if (defItem != -1) {
                if (defItem == position) {
                    UbtLog.d(TAG, "onBindViewHolder select");
                    viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_trans);
                    viewHolder.rlNormal.setVisibility(View.GONE);
                    viewHolder.rlEditLayout.setVisibility(View.VISIBLE);
                    changeViewLen(viewHolder.rlRoot, 1, "1000");
                    changeViewLen(viewHolder.timeView,1, time);
                    viewHolder.sbChange.setProgress(Integer.valueOf(time)/10);
                } else {
                    UbtLog.d(TAG, "onBindViewHolder select no");
                    viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_ba);
                    viewHolder.rlNormal.setVisibility(View.VISIBLE);
                    viewHolder.rlEditLayout.setVisibility(View.GONE);

                }


            }else{
                UbtLog.d(TAG, "no select");

                UbtLog.d(TAG, "is 222");
                viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_ba);
                viewHolder.rlNormal.setVisibility(View.VISIBLE);
                viewHolder.rlEditLayout.setVisibility(View.GONE);
            }

            if(playIndex != -1){
                if(position == playIndex){
                    UbtLog.d(TAG, "playing view VISIBLE");
                    viewHolder.rlNormal.setVisibility(View.VISIBLE);
                    UbtLog.d(TAG, "is 333");
                    viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_ba_on);

                }else{
                    viewHolder.playingView.setVisibility(View.GONE);
                    viewHolder.rlNormal.setVisibility(View.VISIBLE);

                    UbtLog.d(TAG, "is 444");
                    viewHolder.itemView.setBackgroundResource(R.drawable.buttonstyle_ba);

                }
            }


        }






        String  index = String.valueOf(datas.get(position).get(ActionsEditHelper.MAP_FRAME_NAME));
        UbtLog.d(TAG, "index:" + index);

        viewHolder.tv_frame_time.setText(time);
        viewHolder.tv_frame_index.setText(index);
        viewHolder.tvTime.setText(time);

        viewHolder.sbChange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                UbtLog.d(TAG, "progress:" + progress);
                if(progress <10){
                    progress = 10;
                    viewHolder.sbChange.setProgress(progress);
                }

                changeTime = progress*10;
                viewHolder.tvTime.setText(changeTime + "");
                changeViewLen(viewHolder.timeView, 1, "" + progress*10);
                if(onchangeCurrentItemTimeListener != null){
                    onchangeCurrentItemTimeListener.changeCurrentItemTime(changeTime);
                }
//                ((ActionsCreateActivity)context).changeCurrentItemTime(changeTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void changePlayView(final View view, String time){
        view.clearAnimation();
        ScaleAnimation anmi = new ScaleAnimation(0, Integer.valueOf(time) , 1, 1, 0, 0.5f);
        anmi.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                view.setVisibility(View.INVISIBLE);
            }
        });
        anmi.setFillAfter(false);
        anmi.setDuration((long)Integer.valueOf(time));
        view.startAnimation(anmi);
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {

        return (Integer) datas.get(position).get(ActionsEditHelper.MAP_FRAME_TIME);
    }



    public class FrameViewHolder extends RecyclerView.ViewHolder{

        TextView tv_frame_time;
        TextView tv_frame_index;
        RelativeLayout rlRoot;
        ImageView ivLeftDrag;
        RelativeLayout rlNormal;

        RelativeLayout rlEditLayout;
        View timeView;
        SeekBar sbChange;
        TextView tvTime;
        View playingView;



        public FrameViewHolder(final View view) {
            super(view);
            tv_frame_time = (TextView) itemView.findViewById(R.id.txt_action_frame_time);
            tv_frame_index = (TextView) itemView.findViewById(R.id.txt_action_frame_name_);
            rlRoot = (RelativeLayout) itemView.findViewById(R.id.rlRoot);
            ivLeftDrag = (ImageView) itemView.findViewById(R.id.view_left);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemListener != null) {
                        onItemListener.onItemClick(v,getLayoutPosition(),datas.get(getLayoutPosition()));
                    }
                }
            });

            rlNormal = (RelativeLayout) itemView.findViewById(R.id.rl_normal);

            rlEditLayout = (RelativeLayout) itemView.findViewById(R.id.lay_frame_data_edit);
            timeView = (View) itemView.findViewById(R.id.vew_play_frame_time_long);
            sbChange = (SeekBar) itemView.findViewById(R.id.skb_time);
            tvTime = (TextView) itemView.findViewById(R.id.txt_edit_frame_time);
            playingView= (View) itemView.findViewById(R.id.vew_play_frame_item_progress);


        }
    }

    private void changeViewLen(View view, int weight, String time) {
        changeViewLen(view, 0, weight, time);
    }

    private void changeViewLen(View view, float start_weight, int weight, String time) {

        UbtLog.d(TAG, "width:" + view.getWidth() + "weight:" + weight);

        int width = 0;
        int times = Integer.valueOf(time);
        width = times;

        if(weight == 1){
            width = width*1;
        }else if(weight == 2){
            width = width*2;
        }else if(weight == 3){
            width = width*3;
        }else if(weight == 4){
            width =width*4;
        }
        else if(weight == -1){
            width = width/2;
        }else if(weight == -2){
            width = width/3;
        }else if(weight == -3){
            width = width/4;
        }

        ViewGroup.LayoutParams param = view.getLayoutParams();
        int height = param.height;
        UbtLog.d(TAG, "height:" + height + "--width:" + param.width);
 /*       if(density == 2.0){
            height = 100;
        }else if(density == 1.0){
            height = 50;
        }else if(density == 1.5){
            height = 75;
        }else if(density == 3.0){
            height = 150;
        }else if(density == 3.5){
            height = 175;
        }else if(density == 4.0){
            height = 200;
        }
*/


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        view.setLayoutParams(params);
    }


    public interface  OnchangeCurrentItemTimeListener{
        void changeCurrentItemTime(int time);
    }


}
