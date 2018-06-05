package com.ubt.alpha1e_edu.behaviorhabits.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e_edu.behaviorhabits.playeventlist.PlayEventListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class EventListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = EventListRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    public List<PlayContentInfo> mDatas = new ArrayList<>();
    private View mView;
    private Handler mHandler = null;


    public EventListRecyclerAdapter(Context mContext, List<PlayContentInfo> list, Handler handler) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        this.mHandler = handler;
    }

    public void setData(List<PlayContentInfo>  data) {
        this.mDatas = data;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyPlayContentHolder myHolder  = (MyPlayContentHolder) holder;
        PlayContentInfo playContentInfo = mDatas.get(position);
        if("1".equals(playContentInfo.isSelect)){
            myHolder.ivPlay.setBackgroundResource(R.drawable.ic_music_list_pause);
            myHolder.playStatusAnim.start();
            myHolder.ivPlayStatus.setVisibility(View.VISIBLE);
        }else {
            myHolder.ivPlay.setBackgroundResource(R.drawable.ic_music_list_play);
            myHolder.playStatusAnim.stop();
            myHolder.ivPlayStatus.setVisibility(View.INVISIBLE);
        }

        myHolder.tvPlayContent.setText(playContentInfo.contentName);

        myHolder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                msg.what = PlayEventListActivity.DO_PLAY_OR_PAUSE;
                msg.arg1 = position;
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_play_event_item, parent, false);
        MyPlayContentHolder myPlayContentHolder = new MyPlayContentHolder(mView);
        return myPlayContentHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class MyPlayContentHolder extends RecyclerView.ViewHolder
    {

        public RelativeLayout rlPlayContentItem;
        public ImageView ivPlay, ivPlayStatus;
        public TextView tvPlayContent;
        private AnimationDrawable playStatusAnim = null;

        public MyPlayContentHolder(View view)
        {
            super(view);

            rlPlayContentItem  = (RelativeLayout) view.findViewById(R.id.rl_play_content_item);
            ivPlay = (ImageView) view.findViewById(R.id.iv_play);
            ivPlayStatus = (ImageView) view.findViewById(R.id.iv_play_status);
            tvPlayContent = (TextView) view.findViewById(R.id.tv_play_content);

            playStatusAnim = (AnimationDrawable)ivPlayStatus.getBackground();
            playStatusAnim.setOneShot(false);
            playStatusAnim.setVisible(true,true);
        }
    }

}
