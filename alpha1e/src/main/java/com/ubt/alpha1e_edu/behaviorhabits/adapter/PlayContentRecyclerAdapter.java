package com.ubt.alpha1e_edu.behaviorhabits.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.behaviorhabits.model.PlayContentInfo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class PlayContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = PlayContentRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    public List<PlayContentInfo> mDatas = new ArrayList<>();
    private View mView;
    private Handler mHandler = null;

    public PlayContentRecyclerAdapter(Context mContext, List<PlayContentInfo> list, Handler handler) {
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
            myHolder.ivSelect.setBackgroundResource(R.drawable.icon_habits_choose_in);
        }else {
            myHolder.ivSelect.setBackgroundResource(R.drawable.icon_habits_choose_out);
        }

        myHolder.tvPlayContent.setText(playContentInfo.contentName);

        myHolder.rlPlayContentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换开关
                if("1".equals(mDatas.get(position).isSelect)){
                    mDatas.get(position).isSelect = "0";
                }else {
                    mDatas.get(position).isSelect = "1";
                }
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_play_content_item, parent, false);
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
        public ImageView ivSelect;
        public TextView tvPlayContent;

        public MyPlayContentHolder(View view)
        {
            super(view);

            rlPlayContentItem  = (RelativeLayout) view.findViewById(R.id.rl_play_content_item);
            ivSelect = (ImageView) view.findViewById(R.id.iv_select);
            tvPlayContent = (TextView) view.findViewById(R.id.tv_play_content);
        }
    }

}
