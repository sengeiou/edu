package com.ubt.alpha1e.edu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.Constant;
import com.ubt.alpha1e.edu.ui.ActionsNewEditActivity;
import com.ubt.alpha1e.edu.ui.MyActionsActivity;
import com.ubt.alpha1e.edu.ui.RobotConnectedActivity;
import com.ubt.alpha1e.edu.ui.custom.SlideView;
import com.ubt.alpha1e.edu.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.edu.ui.helper.BaseHelper;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class MyActionsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "MyActionsRecyclerAdapter";

    private static final int NORMAL_VIEW = 1;
    private static final int FOOTER_VIEW = 0;
    private static final int ADDMORE_VIEW = -1;

    private Context mContext;
    public List<Map<String, Object>> mDatas = new ArrayList<>();
    private View mView;
    private int type = -1;
    private BaseHelper mHelper;
    private List<SlideView> openList=new ArrayList<SlideView>();
    public MyActionsRecyclerAdapter(Context mContext,List<Map<String, Object>> list,int type,BaseHelper helper) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        this.type = type;
        this.mHelper = helper;
    }


    public void setData(List<Map<String, Object>>  data) {
        this.mDatas = data;
    }
    @Override
    public int getItemViewType(int position) {

        if(mDatas.get(position)==null)
            return type==3?ADDMORE_VIEW:FOOTER_VIEW;//创建更多/加载更多
        else
            return NORMAL_VIEW;


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof  MyActionsHolder)
        {
            //UbtLog.d(TAG,"my action position onbind::"+position);
            MyActionsHolder myHolder = (MyActionsHolder)holder;
            FillLocalContent.fillMyActionItems(mContext,holder,mDatas.get(position),type,mHelper,position);
            myHolder.swipeLayout.setSwipeEnable(true);
            if(type == -1){//全部需要删除按钮
                myHolder.delLayout.setVisibility(View.GONE);
            }else{
                myHolder.delLayout.setVisibility(View.VISIBLE);
                myHolder.swipeLayout.setSwipeChangeListener(new SlideView.OnSwipeChangeListener() {
                    @Override
                    public void onDraging(SlideView mSwipeLayout) {
//                        UbtLog.d(TAG, "mSwipeLayout onDraging");
                    }

                    @Override
                    public void onOpen(SlideView mSwipeLayout) {
                        UbtLog.d(TAG, "mSwipeLayout onOpen");
                        for(SlideView layout:openList){
                            layout.close();
                        }
                        openList.clear();
                        UbtLog.d(TAG, "mSwipeLayout openList=" + openList.size());
                        openList.add(mSwipeLayout);
                    }

                    @Override
                    public void onClose(SlideView mSwipeLayout) {
                        UbtLog.d(TAG, "mSwipeLayout onClose");
                        openList.remove(mSwipeLayout);
                    }

                    @Override
                    public void onStartOpen(SlideView mSwipeLayout) {
                        UbtLog.d(TAG, "mSwipeLayout onStartOpen start");
                        for(SlideView layout:openList){
                            layout.close();
                        }
                        openList.clear();
                        UbtLog.d(TAG, "mSwipeLayout onStartOpen end");

                    }

                    @Override
                    public void onStartClose(SlideView mSwipeLayout) {


                    }
                });
            }


        }else if(holder instanceof  AddMoreViewHolder)
        {
            AddMoreViewHolder addMoreViewHolder = (AddMoreViewHolder)holder;
            addMoreViewHolder.ly_create_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mHelper.isLostCoon()) {
                        RobotConnectedActivity.launchActivity((MyActionsActivity)mContext,true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                        return;
                    }
                    ((MyActionsHelper)mHelper).getSchemeId();
//                    ActionsEditActivity.launchActivity((MyActionsActivity)mContext, ActionsEditHelper.StartType.new_type,null,12306,
//                            ((MyActionsHelper)mHelper).getSchemeId(),((MyActionsHelper)mHelper).getSchemeName());


                    ActionsNewEditActivity.launchActivity((MyActionsActivity)mContext, ActionsEditHelper.StartType.new_type,null,12306,
                            ((MyActionsHelper)mHelper).getSchemeId(),((MyActionsHelper)mHelper).getSchemeName());

                }
            });

        }else
        {
            Animation operatingAnim = AnimationUtils.loadAnimation(
                    mContext, R.anim.turn_around_anim);
            operatingAnim.setInterpolator(new LinearInterpolator());
            ((FooterViewHolder) holder).imgLoading.startAnimation(operatingAnim);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType)
        {
            case NORMAL_VIEW:
                mView = LayoutInflater.from(mContext).inflate(R.layout.layout_my_actions_item, parent, false);
                MyActionsHolder myActionsHolder = new MyActionsHolder(mView);
                return myActionsHolder;
            case FOOTER_VIEW:
                mView = LayoutInflater.from(mContext).inflate(R.layout.layout_load_more, parent, false);
                FooterViewHolder mFooterHolder = new FooterViewHolder(mView);
                return mFooterHolder;
            case ADDMORE_VIEW:
                mView =LayoutInflater.from(mContext).inflate(R.layout.layout_myactions_create_more, parent, false);
                AddMoreViewHolder mAddMoreViewHolder = new AddMoreViewHolder(mView);
                return mAddMoreViewHolder;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class AddMoreViewHolder extends  RecyclerView.ViewHolder
    {

        private ImageView addCreate;
        private LinearLayout ly_create_more;
        public  AddMoreViewHolder(View v)
        {
            super(v);
            addCreate = (ImageView)v.findViewById(R.id.img_add_create);
            ly_create_more = (LinearLayout)v.findViewById(R.id.ly_create_more);


        }
    }

    public static class FooterViewHolder extends  RecyclerView.ViewHolder
    {
        private ImageView imgLoading;

       public  FooterViewHolder(View v)
       {
           super(v);
           imgLoading = (ImageView)v.findViewById(R.id.img_add);
       }
    }
    public static class MyActionsHolder extends RecyclerView.ViewHolder
    {
        public ImageView img_action_logo, img_type_logo, img_state,img_action_stop_play,img_action_state;
        public RelativeLayout lay_state,rl_info,lay_center;
        public SlideView swipeLayout;//滑动视图
        public TextView txt_action_name, txt_time, txt_des, txt_type_des,  txt_progress,txt_sees;
        public GifImageView gif;
        public RelativeLayout playingLayout;
        public LinearLayout delLayout;
        public Button btn_delete;
        public Animation operatingAnim;
        public ImageView iv_more;
        public ProgressBar progress_bar;
        public MyActionsHolder(View view)
        {
            super(view);
            img_action_logo = (ImageView) view.findViewById(R.id.action_logo);
            img_type_logo = (ImageView) view.findViewById(R.id.img_type_logo);
            img_state = (ImageView) view.findViewById(R.id.img_state);
            img_action_stop_play = (ImageView) view.findViewById(R.id.img_action_stop_play);
            img_action_state = (ImageView) view.findViewById(R.id.img_action_state);
            lay_state = (RelativeLayout) view.findViewById(R.id.lay_state);
            rl_info  = (RelativeLayout) view.findViewById(R.id.rl_logo_info);
            lay_center  = (RelativeLayout) view.findViewById(R.id.lay_center);
            txt_action_name = (TextView) view.findViewById(R.id.txt_action_name);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            txt_des = (TextView) view.findViewById(R.id.txt_disc);
            txt_type_des = (TextView) view.findViewById(R.id.txt_type_des);
            txt_progress = (TextView) view.findViewById(R.id.txt_progress);
            txt_sees = (TextView)view.findViewById(R.id.txt_sees);
            swipeLayout =(SlideView)view.findViewById(R.id.swipe);
            gif = (GifImageView) view
                    .findViewById(R.id.gif_playing);
            btn_delete = (Button)view.findViewById(R.id.btn_del);
            playingLayout = (RelativeLayout) view.findViewById(R.id.playing_state);
            delLayout  = (LinearLayout) view.findViewById(R.id.lay_del);
            iv_more = (ImageView) view.findViewById(R.id.iv_more);
            progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);
        }

    }
}
