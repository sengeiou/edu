package com.ubt.alpha1e.behaviorhabits.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.drag.OnItemChangeListener;
import com.ubt.alpha1e.behaviorhabits.model.SampleEntity;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class FlowPlayContentRecyclerAdapter extends SampleAdapter<FlowPlayContentRecyclerAdapter.MyPlayContentHolder> implements OnItemChangeListener {

    private static final String TAG = FlowPlayContentRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    public List<SampleEntity> mDatas = new ArrayList<>();
    private View mView;
    private Handler mHandler = null;

    public FlowPlayContentRecyclerAdapter(Context mContext, List<SampleEntity> list, Handler handler) {
        super(mContext,list);
        this.mContext = mContext;
        this.mDatas = list;
        this.mHandler = handler;
    }

    @Override
    public void onBindViewHolder(FlowPlayContentRecyclerAdapter.MyPlayContentHolder holder, final int position) {

        MyPlayContentHolder myHolder  =  holder;
        SampleEntity sampleEntity = mDatas.get(position);

        myHolder.tvPlayContent.setText(sampleEntity.getPlayContentInfo().contentName);

    }

    @Override
    public FlowPlayContentRecyclerAdapter.MyPlayContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_play_content_item_simple, parent, false);
        MyPlayContentHolder myPlayContentHolder = new MyPlayContentHolder(mView);
        return myPlayContentHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class MyPlayContentHolder extends RecyclerView.ViewHolder
    {

        //public RelativeLayout rlPlayContentItem;
        public TextView tvPlayContent;

        public MyPlayContentHolder(View view)
        {
            super(view);

            //rlPlayContentItem  = (RelativeLayout) view.findViewById(R.id.rl_play_content_info);
            tvPlayContent = (TextView) view.findViewById(R.id.tv_play_content);
        }
    }

}
