package com.ubt.alpha1e.behaviorhabits.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.fragment.ParentManageCenterFragment;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEventInfo;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class HabitsEventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = HabitsEventRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    public List<HabitsEventInfo> mDatas = new ArrayList<>();
    private View mView;
    private Handler mHandler = null;

    public HabitsEventRecyclerAdapter(Context mContext, List<HabitsEventInfo> list, Handler handler) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        this.mHandler = handler;
    }

    public void setData(List<HabitsEventInfo>  data) {
        this.mDatas = data;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyHabitsEventHolder myHolder  = (MyHabitsEventHolder) holder;
        HabitsEventInfo habitsEventInfo = mDatas.get(position);
        if("1".equals(habitsEventInfo.eventSwitch)){
            myHolder.ivEventSwitch.setBackgroundResource(R.drawable.icon_habits_switch_open);
            myHolder.ivEventLogo.setBackgroundResource(R.drawable.icon_habits_awake_highlight);
        }else {
            myHolder.ivEventSwitch.setBackgroundResource(R.drawable.icon_habits_switch_close);
            myHolder.ivEventLogo.setBackgroundResource(R.drawable.icon_habits_awake_grey);
        }

        myHolder.tvEventTime.setText(habitsEventInfo.eventTime);
        myHolder.tvEventName.setText(habitsEventInfo.eventName);

        myHolder.ivEventSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                msg.what = ParentManageCenterFragment.CLICK_SWITCH_EVENT;
                msg.arg1 = position;
                mHandler.sendMessage(msg);
            }
        });

        myHolder.rlHibitsEventInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                msg.what = ParentManageCenterFragment.SHOW_EVENT_INFO;
                msg.arg1 = position;
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_hibits_event_item, parent, false);
        MyHabitsEventHolder myHabitsEventHolder = new MyHabitsEventHolder(mView);
        return myHabitsEventHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class MyHabitsEventHolder extends RecyclerView.ViewHolder
    {

        public RelativeLayout rlHibitsEventInfo;
        public ImageView ivEventSwitch,ivEventLogo,ivRight;
        public TextView tvEventTime,tvEventName;

        public MyHabitsEventHolder(View view)
        {
            super(view);

            rlHibitsEventInfo  = (RelativeLayout) view.findViewById(R.id.rl_hibits_event_info);
            ivEventSwitch = (ImageView) view.findViewById(R.id.iv_event_switch);
            ivEventLogo = (ImageView) view.findViewById(R.id.iv_event_logo);
            ivRight = (ImageView) view.findViewById(R.id.iv_right);
            tvEventTime = (TextView) view.findViewById(R.id.tv_event_time);
            tvEventName = (TextView) view.findViewById(R.id.tv_event_name);
        }
    }

}
