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
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class HabitsEventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = HabitsEventRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    public List<HabitsEvent> mDatas = new ArrayList<>();
    private View mView;
    private boolean isSinpleShow;
    private Handler mHandler = null;

    public HabitsEventRecyclerAdapter(Context mContext, List<HabitsEvent> list, boolean isSinpleShow, Handler handler) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        this.mHandler = handler;
        this.isSinpleShow = isSinpleShow;
    }

    public void setData(List<HabitsEvent>  data) {
        this.mDatas = data;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyHabitsEventHolder myHolder  = (MyHabitsEventHolder) holder;
        HabitsEvent habitsEventInfo = mDatas.get(position);

        if("1".equals(habitsEventInfo.status) || isSinpleShow){
            myHolder.ivEventSwitch.setBackgroundResource(R.drawable.icon_habits_switch_open);
        }else {
            myHolder.ivEventSwitch.setBackgroundResource(R.drawable.icon_habits_switch_close);
        }

        myHolder.ivEventLogo.setBackgroundResource(getBackgroundViewId(habitsEventInfo.eventType, habitsEventInfo.status));
        myHolder.tvEventTime.setText(habitsEventInfo.eventTime);
        myHolder.tvEventName.setText(habitsEventInfo.eventName);

        if(isSinpleShow){
            myHolder.ivEventSwitch.setVisibility(View.GONE);
            myHolder.tvRight.setVisibility(View.GONE);

            myHolder.rlRight.setVisibility(View.VISIBLE);
            if(habitsEventInfo.score == "5"){
                myHolder.ivStar1.setBackgroundResource(R.drawable.icon_habits_star_highlight);
                myHolder.ivStar2.setBackgroundResource(R.drawable.icon_habits_star_grey);
            }else if(habitsEventInfo.score == "10"){
                myHolder.ivStar1.setBackgroundResource(R.drawable.icon_habits_star_highlight);
                myHolder.ivStar2.setBackgroundResource(R.drawable.icon_habits_star_highlight);
            }else {
                myHolder.ivStar1.setBackgroundResource(R.drawable.icon_habits_star_grey);
                myHolder.ivStar2.setBackgroundResource(R.drawable.icon_habits_star_grey);
            }

            myHolder.tvScore.setText(habitsEventInfo.score + "分");
        }else {
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

        public RelativeLayout rlHibitsEventInfo,rlRight;
        public ImageView ivEventSwitch,ivEventLogo,ivStar1,ivStar2;
        public TextView tvEventTime,tvEventName,tvScore,tvRight;

        public MyHabitsEventHolder(View view)
        {
            super(view);

            rlHibitsEventInfo  = (RelativeLayout) view.findViewById(R.id.rl_hibits_event_info);
            rlRight  = (RelativeLayout) view.findViewById(R.id.rl_right);
            ivEventSwitch = (ImageView) view.findViewById(R.id.iv_event_switch);
            ivEventLogo = (ImageView) view.findViewById(R.id.iv_event_logo);
            ivStar1 = (ImageView) view.findViewById(R.id.iv_star_1);
            ivStar2 = (ImageView) view.findViewById(R.id.iv_star_2);
            tvEventTime = (TextView) view.findViewById(R.id.tv_event_time);
            tvEventName = (TextView) view.findViewById(R.id.tv_event_name);
            tvScore = (TextView) view.findViewById(R.id.tv_score);
            tvRight = (TextView) view.findViewById(R.id.tv_right);
        }
    }

    /**
     *
     * eventType 1：起床 2：午休 3：睡晚觉 4：洗漱 5：早餐 6：午餐 7：晚餐 8：上学 9：作业 10：阅读 11：编程 12：娱乐 13：洗澡
     * @param eventType
     * @param status
     * @return
     */
    private int getBackgroundViewId(String eventType,String status){
        int imageId = 0;
        if("1".equals(status)){
            if("1".equals(eventType)){
                imageId = R.drawable.icon_habits_awake_highlight;
            }else if("2".equals(eventType)){
                imageId = R.drawable.icon_habits_sleep_highlight;
            }else if("3".equals(eventType)){
                imageId = R.drawable.icon_habits_sleep_highlight;
            }else if("4".equals(eventType)){
                imageId = R.drawable.icon_habits_wash_highlight;
            }else if("5".equals(eventType)){
                imageId = R.drawable.icon_habits_breackfast_highlight;
            }else if("6".equals(eventType)){
                imageId = R.drawable.icon_habits_lunch_highlight;
            }else if("7".equals(eventType)){
                imageId = R.drawable.icon_habits_dinner_highlight;
            }else if("8".equals(eventType)){
                imageId = R.drawable.icon_habits_english_highlight;
            }else if("9".equals(eventType)){
                imageId = R.drawable.icon_habits_homework_highlight;
            }else if("10".equals(eventType)){
                imageId = R.drawable.icon_habits_read_highlight;
            }else if("11".equals(eventType)){
                imageId = R.drawable.icon_habits_homework_highlight;
            }else if("12".equals(eventType)){
                imageId = R.drawable.icon_habits_entertainment_highlight;
            }else{
                imageId = R.drawable.icon_habits_homework_highlight;
            }
        }else {
            if("1".equals(eventType)){
                imageId = R.drawable.icon_habits_awake_grey;
            }else if("2".equals(eventType)){
                imageId = R.drawable.icon_habits_sleep_grey;
            }else if("3".equals(eventType)){
                imageId = R.drawable.icon_habits_sleep_grey;
            }else if("4".equals(eventType)){
                imageId = R.drawable.icon_habits_wash_grey;
            }else if("5".equals(eventType)){
                imageId = R.drawable.icon_habits_breackfast_grey;
            }else if("6".equals(eventType)){
                imageId = R.drawable.icon_habits_lunch_grey;
            }else if("7".equals(eventType)){
                imageId = R.drawable.icon_habits_dinner_grey;
            }else if("8".equals(eventType)){
                imageId = R.drawable.icon_habits_english_grey;
            }else if("9".equals(eventType)){
                imageId = R.drawable.icon_habits_homework_grey;
            }else if("10".equals(eventType)){
                imageId = R.drawable.icon_habits_read_grey;
            }else if("11".equals(eventType)){
                imageId = R.drawable.icon_habits_homework_grey;
            }else if("12".equals(eventType)){
                imageId = R.drawable.icon_habits_entertainment_grey;
            }else{
                imageId = R.drawable.icon_habits_homework_grey;
            }
        }
        return imageId;
    }
}
