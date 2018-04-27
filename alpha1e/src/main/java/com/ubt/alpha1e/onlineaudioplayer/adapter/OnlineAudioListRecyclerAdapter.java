package com.ubt.alpha1e.onlineaudioplayer.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.onlineaudioplayer.Fragment.OnlineAudioListFragment;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.PlayerEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class OnlineAudioListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = OnlineAudioListRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    public List<AudioContentInfo> mDatas = new ArrayList<>();
    private View mView;
    private Handler mHandler = null;

    public OnlineAudioListRecyclerAdapter(Context mContext, List<AudioContentInfo> list, Handler handler) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        this.mHandler = handler;
    }

    public void setData(List<AudioContentInfo>  data) {
        this.mDatas = data;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MyPlayContentHolder myHolder  = (MyPlayContentHolder) holder;
        AudioContentInfo playContentInfo = mDatas.get(position);
        if(OnlineAudioListFragment.mPlayContentInfoDatas.get(position).isPlaying){
            myHolder.playStatusAnim.start();
            myHolder.ivPlayStatus.setVisibility(View.VISIBLE);
            myHolder.tvPlayContent.setTextColor(mContext.getResources().getColor(R.color.tv_blue_color) );
        }else {
            myHolder.playStatusAnim.stop();
            myHolder.ivPlayStatus.setVisibility(View.INVISIBLE);
            myHolder.tvPlayContent.setTextColor(mContext.getResources().getColor(R.color.tv_center_color));
        }
        myHolder.tvPlayContent.setText(playContentInfo.contentName);
        myHolder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_onlineplay_event_item, parent, false);
        MyPlayContentHolder myPlayContentHolder = new MyPlayContentHolder(mView);
        return myPlayContentHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class MyPlayContentHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public RelativeLayout rlPlayContentItem;
        public ImageView  ivPlayStatus;
        public CheckBox ivPlay;
        public TextView tvPlayContent;
        private AnimationDrawable playStatusAnim = null;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        public MyPlayContentHolder(View view)
        {
            super(view);
            view.setOnClickListener(this);
            rlPlayContentItem  = (RelativeLayout) view.findViewById(R.id.rl_play_content_item);
            ivPlay = (CheckBox) view.findViewById(R.id.iv_play);
            ivPlayStatus = (ImageView) view.findViewById(R.id.iv_play_status);
            tvPlayContent = (TextView) view.findViewById(R.id.tv_play_content);

            playStatusAnim = (AnimationDrawable)ivPlayStatus.getBackground();
            playStatusAnim.setOneShot(false);
            playStatusAnim.setVisible(true,true);
        }

        @Override
        public void onClick(View view) {
//            if (selectedItems.get(getAdapterPosition(), false)) {
//                selectedItems.delete(getAdapterPosition());
//                view.setSelected(false);
//            }
//            else {
//                selectedItems.put(getAdapterPosition(), true);
//                view.setSelected(true);
//
//            }
        }
    }

}
