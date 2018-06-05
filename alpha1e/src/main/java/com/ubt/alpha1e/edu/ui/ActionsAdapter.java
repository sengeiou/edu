package com.ubt.alpha1e.edu.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.ui.dialog.DialogActions;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.List;
import java.util.Map;

/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class ActionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ActionsAdapter";

    private Context context;
    private List<Map<String, Object>> data;
    private ActionsViewHolder viewHolder;

    private int defItem = -1;
    private OnItemListener onItemListener;

    private int type = 0;
    private boolean show = false;

    private DialogActions dialogActions;
    private AnimationDrawable animationDrawable;
    private boolean playComplete = true;

    public ActionsAdapter(Context context, List<Map<String, Object>> data, int type, DialogActions dialogActions) {
        this.context = context;
        this.data = data;
        this.type = type;
        this.dialogActions = dialogActions;
    }


    public  interface OnItemListener {
        void onItemClick(View view , int pos, Map<String, Object> data);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public void setDefSelect(int position) {
        this.defItem = position;
        notifyDataSetChanged();
    }

    public void showDelete(boolean show){
        this.show = show;
        notifyDataSetChanged();
    }

    public void stopAnim(boolean complete){
        playComplete = complete;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_actions, parent, false);

        viewHolder = new ActionsAdapter.ActionsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        ActionsViewHolder viewHolder = (ActionsViewHolder)holder;


        UbtLog.d(TAG, "defItem:" + defItem + "drawable:"+  data.get(position).get(ActionsNewEditActivity.ACTION_ICON));

        if (defItem != -1) {
            if (defItem == position) {
                UbtLog.d(TAG, "onBindViewHolder select");
                viewHolder.ivSelect.setVisibility(View.VISIBLE);

            } else {
                UbtLog.d(TAG, "onBindViewHolder select no");
                viewHolder.ivSelect.setVisibility(View.GONE);

            }

        }
        UbtLog.d(TAG, "onBindViewHolderdata:" + data.get(position));
        if(type == 2){
            if(position == 0){
                viewHolder.deviceIcon.setImageResource(R.drawable.dottedline);
                viewHolder. ivDel.setVisibility(View.GONE);
                viewHolder.gif_play.setVisibility(View.GONE);
            }else{
                viewHolder.deviceIcon.setImageResource(R.drawable.bg_dottedline);
                viewHolder.gif_play.setVisibility(View.VISIBLE);
                animationDrawable = (AnimationDrawable) viewHolder.gif_play.getDrawable();
                if(defItem == position){
                    if(!playComplete){
                        animationDrawable.start();
                    }
                }else{
                    animationDrawable.stop();
                }
                if(show){
                    if((int)data.get(position).get(ActionsNewEditActivity.SONGS_TYPE) == 1){
                        viewHolder. ivDel.setVisibility(View.VISIBLE);
                    }else{
                        viewHolder. ivDel.setVisibility(View.GONE);
                    }

                }else{
                    viewHolder. ivDel.setVisibility(View.GONE);
                }



            }

            viewHolder.deviceName.setText((String)data.get(position).get(ActionsNewEditActivity.SONGS_NAME));
        }else{
            viewHolder.deviceName.setText((String)data.get(position).get(ActionsNewEditActivity.ACTION_NAME));
            viewHolder.deviceIcon.setImageResource((int) data.get(position).get(ActionsNewEditActivity.ACTION_ICON));
        }

        viewHolder.ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogActions.deleteMP3RecordFile((String)data.get(position).get(ActionsNewEditActivity.SONGS_NAME));
                data.remove(position);
                notifyDataSetChanged();
            }
        });



    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ActionsViewHolder extends RecyclerView.ViewHolder{

        ImageView deviceIcon;
        TextView deviceName;
        ImageView ivDel;
        ImageView ivSelect;
        ImageView gif_play;


        public ActionsViewHolder(View itemView) {
            super(itemView);

            deviceIcon = (ImageView) itemView.findViewById(R.id.iv_action_icon);
            deviceName = (TextView) itemView.findViewById(R.id.tv_action_name);
            ivDel = (ImageView) itemView.findViewById(R.id.iv_del);
            ivSelect = (ImageView) itemView.findViewById(R.id.iv_action_select) ;
            gif_play = (ImageView) itemView.findViewById(R.id.gif_play);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemListener != null) {
                        onItemListener.onItemClick(v,getLayoutPosition(),data.get(getLayoutPosition()));
                    }
                }
            });
        }
    }



}