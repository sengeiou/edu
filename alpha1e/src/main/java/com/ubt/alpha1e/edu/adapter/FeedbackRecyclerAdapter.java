package com.ubt.alpha1e.edu.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.model.FeedbackInfo;
import com.ubt.alpha1e.edu.ui.custom.SlideView;
import com.ubt.alpha1e.edu.userinfo.helpfeedback.feedbackdetail.FeedbackDetailActivity;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class FeedbackRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = FeedbackRecyclerAdapter.class.getSimpleName();

    private static final int HEAD_VIEW = 0;
    private static final int NORMAL_VIEW = 1;

    private Context mContext;
    public List<FeedbackInfo> mDatas = new ArrayList<>();
    private View mView;
    private Handler mHandler = null;
    private String searchName = "";

    public FeedbackRecyclerAdapter(Context mContext, List<FeedbackInfo> list, Handler handler) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        this.mHandler = handler;
    }

    public void setData(List<FeedbackInfo>  data) {
        this.mDatas = data;
    }
    @Override
    public int getItemViewType(int position) {

        if (position == 0 ) {
            return HEAD_VIEW;
        }else {
            return NORMAL_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof  MyFeedbackHolder)
        {
            MyFeedbackHolder myHolder = (MyFeedbackHolder)holder;
            fillFeedbackItems(mContext,holder,mDatas.get(position),position);
            myHolder.feedbackSwipe.setSwipeEnable(true);
            myHolder.operateLayout.setVisibility(View.GONE);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType)
        {
            case NORMAL_VIEW:
            case HEAD_VIEW:
                mView = LayoutInflater.from(mContext).inflate(R.layout.layout_feedback_item, parent, false);
                MyFeedbackHolder myFeedbackHolder = new MyFeedbackHolder(mView);
                return myFeedbackHolder;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class MyFeedbackHolder extends RecyclerView.ViewHolder
    {

        public RelativeLayout rlFeedbackInfo;
        public SlideView feedbackSwipe;//滑动视图
        public ImageView ivLogo,ivRight;
        public TextView tvFeedbackName;
        public LinearLayout operateLayout;
        public Animation operatingAnim;
        public MyFeedbackHolder(View view)
        {
            super(view);

            rlFeedbackInfo  = (RelativeLayout) view.findViewById(R.id.rl_feedback_info);
            feedbackSwipe =(SlideView)view.findViewById(R.id.feedback_swipe);
            ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
            ivRight = (ImageView) view.findViewById(R.id.iv_right);
            tvFeedbackName = (TextView) view.findViewById(R.id.tv_feedback_name);
            operateLayout  = (LinearLayout) view.findViewById(R.id.ll_operate);
        }
    }

    public void fillFeedbackItems(final Context mContext, RecyclerView.ViewHolder myHolder, final FeedbackInfo feedbackInfo, final int position)
    {

        final FeedbackRecyclerAdapter.MyFeedbackHolder holder  = (FeedbackRecyclerAdapter.MyFeedbackHolder) myHolder;

        UbtLog.d(TAG,"holder.tvFeedbackName => " + holder.tvFeedbackName);
        SpannableString feedbackNameStyle = new SpannableString(feedbackInfo.feedbackName);
        if(!TextUtils.isEmpty(searchName)){
            int startIndex = feedbackInfo.feedbackName.indexOf(searchName);
            feedbackNameStyle.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.T22)),startIndex,startIndex + searchName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.tvFeedbackName.setText(feedbackNameStyle);

        holder.ivLogo.setVisibility(View.GONE);
        holder.rlFeedbackInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FeedbackDetailActivity.LaunchActivity(mContext,feedbackInfo);
            }
        });
    }

    public void setSearchName(String searchName){
        this.searchName = searchName;
    }
}
