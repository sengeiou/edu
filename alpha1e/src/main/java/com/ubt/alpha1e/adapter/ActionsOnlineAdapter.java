package com.ubt.alpha1e.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.ui.ActionsLibPreviewWebActivity;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.MyActionsActivity;
import com.ubt.alpha1e.ui.RobotConnectedActivity;
import com.ubt.alpha1e.ui.custom.SlideView;
import com.ubt.alpha1e.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.ui.helper.SettingHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/18.
 */
public class ActionsOnlineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "ActionsOnlineAdapter";

    private List<Map<String, Object>> actionList;
    private Activity mActivity;
    private Context mContext;
    private ActionsLibHelper mHelper;
    public final static int TYPE_NORMAL = 1;//正常类型
    public final static int TYPE_FOOTER = 2;//底部--往往是loading_more

    private Animation operatingAnim;


    public ActionsOnlineAdapter( List<Map<String, Object>> actions, Activity activity, ActionsLibHelper helper) {

        this.actionList = actions;
        this.mHelper = helper;
        this.mActivity = activity;
        mContext = mActivity.getApplicationContext();

    }


    public void setDatas( List<Map<String, Object>> actions)
    {
        actionList = actions;
    }


    public class FooterViewHolder extends RecyclerView.ViewHolder {

        private View layout_load_more;

        public FooterViewHolder(View itemView) {
            super(itemView);
            layout_load_more = itemView.findViewById(R.id.img_add);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final ImageView img_action_logo, img_type_logo, img_state,img_resources;
        public final RelativeLayout lay_state;
        public SlideView swipeLayout;//滑动视图
        public final TextView txt_action_name, txt_time, txt_des, txt_type_des, txt_sees, txt_progress,txt_action_item_down_num;
        public final RelativeLayout relativeLayout;
        public LinearLayout delLayout;
//        public Animation operatingAnim;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            img_action_logo = (ImageView) view.findViewById(R.id.action_logo);
            img_type_logo = (ImageView) view.findViewById(R.id.img_type_logo);
            img_state = (ImageView) view.findViewById(R.id.img_state);
            img_resources = (ImageView) view.findViewById(R.id.img_action_type);
            lay_state = (RelativeLayout) view.findViewById(R.id.lay_state);
            txt_action_name = (TextView) view.findViewById(R.id.txt_action_name);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            txt_des = (TextView) view.findViewById(R.id.txt_disc);
            txt_type_des = (TextView) view.findViewById(R.id.txt_type_des);
            txt_sees = (TextView) view.findViewById(R.id.txt_sees);
            txt_progress = (TextView) view.findViewById(R.id.txt_progress);
            txt_action_item_down_num = (TextView) view.findViewById(R.id.txt_action_item_down_num);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.lay_info);
            swipeLayout =(SlideView)view.findViewById(R.id.swipe);
            delLayout  = (LinearLayout) view.findViewById(R.id.lay_del);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        View view;
        switch (viewType) {

            default:
            case TYPE_NORMAL:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_my_actions_item, parent, false);
                vh = new MyViewHolder(view);
                return vh;
            case TYPE_FOOTER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_load_more, parent, false);
                vh = new FooterViewHolder(view);
                return vh;

        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof FooterViewHolder) {
            Animation operatingAnim = AnimationUtils.loadAnimation(mActivity, R.anim.turn_around_anim);
            operatingAnim.setInterpolator(new LinearInterpolator());
            ((FooterViewHolder) holder).layout_load_more.startAnimation(operatingAnim);

        } else if (holder instanceof MyViewHolder) {
            MyViewHolder myHolder = (MyViewHolder) holder;
            myHolder.swipeLayout.setSwipeEnable(true);
            myHolder.delLayout.setVisibility(View.GONE);
            final ActionInfo actionInfo =(ActionInfo) actionList.get(position).get(ActionsLibHelper.map_val_action);
            myHolder.txt_action_name.setText(actionInfo.actionName + "");
            myHolder.img_type_logo.setImageResource((int) actionList.get(position).get(ActionsLibHelper.map_val_action_type_logo_res));
            myHolder.txt_type_des.setText(actionList.get(position).get(ActionsLibHelper.map_val_action_type_logo_res_des) + "");
            myHolder.txt_des.setText(actionInfo.actionDesciber);
            myHolder.txt_time.setText(actionList.get(position).get(ActionsLibHelper.map_val_action_time) + "");
            myHolder.txt_sees.setText(actionInfo.actionBrowseTime + "");
            myHolder.txt_progress.setTextColor(getColorRes("download_progress_text_color_ft"));

            myHolder.txt_action_item_down_num.setText(actionInfo.actionDownloadTime+"");
            myHolder.txt_action_item_down_num.setTextColor(getColorRes("download_number_text_color_ft"));
            myHolder.txt_action_item_down_num.setVisibility(View.VISIBLE);

            if (actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.download_finish) {
                myHolder.txt_progress.setVisibility(View.GONE);
                myHolder.img_state.clearAnimation();
                myHolder.img_state.setImageDrawable(getDrawableRes("my_actions_play_item_ft"));
                stopLoadingAnimation(myHolder.img_state);
            } else if (actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.not_download) {
                myHolder.txt_progress.setVisibility(View.GONE);
                myHolder.img_state.clearAnimation();
                myHolder.img_state.setImageDrawable(getDrawableRes("actions_online_download_ft"));
                stopLoadingAnimation(myHolder.img_state);

            } else if (actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.downing) {
                myHolder.img_state.setImageDrawable(getDrawableRes("actionlib_item_downloading_icon_ft"));
                myHolder.img_state.setVisibility(View.VISIBLE);
                if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                    myHolder.txt_progress.setVisibility(View.GONE);
                }else {
                    myHolder.txt_progress.setVisibility(View.VISIBLE);
                    Double b = (Double) actionList.get(position).get(
                            ActionsLibHelper.map_val_action_download_progress);
                    int i = (int) (b + 0);
                    myHolder.txt_progress.setText(i + "%");
                }
                startLoadingAnimation(myHolder.img_state);
            }
            if(actionInfo.actionResource==0)
            {
                myHolder.img_resources.setVisibility(View.VISIBLE);
            }else{
                myHolder.img_resources.setVisibility(View.GONE);
            }

            Glide.with(myHolder.img_action_logo.getContext())
                    .load(actionInfo.actionHeadUrl)
                    .fitCenter().placeholder(R.drawable.sec_action_logo)
                    .into(myHolder.img_action_logo);
            myHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int state = -1;
                    if(actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.download_finish)
                    {
                        state = 1;
                    }else if(actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.not_download)
                    {
                        state = 0;
                    }else{
                        state = 2;
                    }

                    ActionsLibPreviewWebActivity.launchActivity(mContext,(ActionInfo) actionList.get(
                                    position).get(ActionsLibHelper.map_val_action),state,(double)actionList.get(position).get(
                            ActionsLibHelper.map_val_action_download_progress));
                }
            });
            myHolder.lay_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ActionsLibHelper.Action_download_state current_state = (ActionsLibHelper.Action_download_state) actionList
                            .get(position)
                            .get(ActionsLibHelper.map_val_action_download_state);

                    if (current_state != ActionsLibHelper.Action_download_state.not_download) {
                        if (current_state == ActionsLibHelper.Action_download_state.download_finish) {
                            if (mHelper.isLostCoon()) {
                                RobotConnectedActivity.launchActivity(mActivity,true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                            } else {
                                if(mHelper.isLogin()){
                                    //MyActionsActivity.launchActivity(mContext,1);//跳转到我的动作---我的下载

                                    //检测是否在充电状态和边充边玩状态是否打开
                                    if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(mContext)){
                                        Toast.makeText(mContext, AlphaApplication.getBaseActivity().getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    MyActionsActivity.launchActivity(mContext,1,actionInfo.actionId);//跳转到我的动作---我的下载,并播放
                                }
                            }
                        }

                        return;
                    }

                    /*由于之前的方法在收到下载失败后才开始更新下载进度状态，导致无网络情况下一直显示0%进度，所以修改为一点击下载则显示进度状态，
                     *当下在失败或者成功后都会改变按钮状态。
                     **/
                    actionList.get(position).put(ActionsLibHelper.map_val_action_download_state,ActionsLibHelper.Action_download_state.downing);
                    notifyItemChanged(position);

                    mHelper.doDownLoad((ActionInfo) actionList.get(position).get(ActionsLibHelper.map_val_action));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return actionList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(actionList.get(position) == null){
            return TYPE_FOOTER;
        }else{
            return TYPE_NORMAL;
        }
    }

    private void startLoadingAnimation(View v)
    {
        if(operatingAnim==null)
        {
            operatingAnim = AnimationUtils.loadAnimation(mContext,
                    R.anim.turn_around_anim);
            operatingAnim.setInterpolator(new LinearInterpolator());
        }
        if(!operatingAnim.hasStarted()){
            v.startAnimation(operatingAnim);
        }
    }

    private void stopLoadingAnimation(View v)
    {
        if(operatingAnim!=null&&operatingAnim.hasStarted())
        {
            v.clearAnimation();
            operatingAnim.cancel();
            operatingAnim = null;
        }

    }

    /**
     * 动态获取图片
     * @param DrawableKey 图片Key
     * @return
     */
    private Drawable getDrawableRes(String DrawableKey){
        return ((BaseActivity)mActivity).getDrawableRes(DrawableKey);
    }

    /**
     * 动态获取颜色值
     * @param colorKey 颜色值Key
     * @return
     */
    public ColorStateList getColorRes(String colorKey){
        return ((BaseActivity)mActivity).getColorRes(colorKey);
    }
}
