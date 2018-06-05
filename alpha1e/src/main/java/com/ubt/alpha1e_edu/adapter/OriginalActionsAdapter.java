package com.ubt.alpha1e_edu.adapter;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.Constant;
import com.ubt.alpha1e_edu.data.model.ActionInfo;
import com.ubt.alpha1e_edu.ui.ActionsLibPreviewWebActivity;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.ui.MyActionsActivity;
import com.ubt.alpha1e_edu.ui.RobotConnectedActivity;
import com.ubt.alpha1e_edu.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e_edu.ui.helper.SettingHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/18.
 */
public class OriginalActionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "OriginalActionsAdapter";

    private List<Map<String, Object>> actionList;
    private Activity mActivity;
    private Context mContext;
    private ActionsLibHelper mHelper;

    private Animation operatingAnim;

    public OriginalActionsAdapter(List<Map<String, Object>> actions, Activity activity, ActionsLibHelper helper) {

        this.actionList = actions;
        this.mHelper = helper;
        this.mActivity = activity;
        mContext = mActivity.getApplicationContext();

    }


    public void setDatas( List<Map<String, Object>> actions)
    {
        actionList = actions;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final ImageView img_action_ranking, img_action_publisher, img_action_type_logo,img_action_state;
        public final TextView txt_action_name, txt_action_publisher_name, txt_action_download_num, txt_action_download_progress,txt_action_ranking_num;

        public OriginalViewHolder(View view) {
            super(view);
            mView = view;
            img_action_ranking = (ImageView) view.findViewById(R.id.img_action_ranking);
            img_action_publisher = (ImageView) view.findViewById(R.id.img_action_publisher);
            img_action_type_logo = (ImageView) view.findViewById(R.id.img_action_type_logo);
            img_action_state = (ImageView) view.findViewById(R.id.img_action_state);

            txt_action_name = (TextView) view.findViewById(R.id.txt_action_name);
            txt_action_publisher_name = (TextView) view.findViewById(R.id.txt_action_publisher_name);
            txt_action_download_num = (TextView) view.findViewById(R.id.txt_action_download_num);
            txt_action_download_progress = (TextView) view.findViewById(R.id.txt_action_download_progress);
            txt_action_ranking_num = (TextView) view.findViewById(R.id.txt_action_ranking_num);

        }

        @Override
        public String toString() {
            return super.toString();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_original_actions_item, parent, false);
        RecyclerView.ViewHolder vh = new OriginalViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        OriginalViewHolder myHolder = (OriginalViewHolder) holder;
        final ActionInfo actionInfo =(ActionInfo) actionList.get(position).get(ActionsLibHelper.map_val_action);
        
        if(actionInfo == null){
            return;
        }
        Glide.with(mContext.getApplicationContext())
                .load(actionInfo.userImage)
                .centerCrop()
                .placeholder(R.drawable.register_login_default_head)
                .into(myHolder.img_action_publisher);

        myHolder.txt_action_publisher_name.setText(actionInfo.userName);
        myHolder.txt_action_name.setText(actionInfo.actionName + "");
        myHolder.img_action_type_logo.setImageResource((int) actionList.get(position).get(ActionsLibHelper.map_val_action_type_logo_res));

        if(position == 0){
            myHolder.img_action_ranking.setImageDrawable(getDrawableRes("actions_square_champion"));
            myHolder.txt_action_ranking_num.setVisibility(View.GONE);
        }else if(position == 1){
            myHolder.img_action_ranking.setImageDrawable(getDrawableRes("actions_square_runner_up"));
            myHolder.txt_action_ranking_num.setVisibility(View.GONE);
        }else if(position == 2){
            myHolder.img_action_ranking.setImageDrawable(getDrawableRes("actions_square_third"));
            myHolder.txt_action_ranking_num.setVisibility(View.GONE);
        }else {
            myHolder.img_action_ranking.setImageDrawable(getDrawableRes("actions_square_nunber"));
            myHolder.txt_action_ranking_num.setText((position + 1) + "");
            myHolder.txt_action_ranking_num.setVisibility(View.VISIBLE);
            myHolder.txt_action_ranking_num.setTextColor(getColorRes("action_ranking_text_color_ft"));
        }

        myHolder.txt_action_download_num.setText(actionInfo.actionDownloadTime + "");
        myHolder.txt_action_download_progress.setTextColor(getColorRes("download_progress_text_color_ft"));
        myHolder.txt_action_download_num.setTextColor(getColorRes("download_number_text_color_ft"));

        if (actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.download_finish) {
            myHolder.txt_action_download_progress.setVisibility(View.GONE);
            myHolder.img_action_state.clearAnimation();
            myHolder.img_action_state.setImageDrawable(getDrawableRes("my_actions_play_item_ft"));
            stopLoadingAnimation(myHolder.img_action_state);
        } else if (actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.not_download) {
            myHolder.txt_action_download_progress.setVisibility(View.GONE);
            myHolder.img_action_state.clearAnimation();
            myHolder.img_action_state.setImageDrawable(getDrawableRes("actions_online_download_ft"));
            stopLoadingAnimation(myHolder.img_action_state);

        } else if (actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.downing) {
            myHolder.img_action_state.setImageDrawable(getDrawableRes("actionlib_item_downloading_icon_ft"));
            myHolder.img_action_state.setVisibility(View.VISIBLE);

            if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                myHolder.txt_action_download_progress.setVisibility(View.GONE);
            }else {
                myHolder.txt_action_download_progress.setVisibility(View.VISIBLE);
                Double b = (Double) actionList.get(position).get(
                        ActionsLibHelper.map_val_action_download_progress);
                int i = (int) (b + 0);
                myHolder.txt_action_download_progress.setText(i + "%");
            }

            startLoadingAnimation(myHolder.img_action_state);
        }

        myHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int state = -1;
                if(actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.download_finish){
                    state = 1;
                }else if(actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.not_download){
                    state = 0;
                }else{
                    state = 2;
                }

                ActionsLibPreviewWebActivity.launchActivity(mContext,(ActionInfo) actionList.get(
                        position).get(ActionsLibHelper.map_val_action),state,(double)actionList.get(position).get(
                        ActionsLibHelper.map_val_action_download_progress));
            }
        });


        myHolder.img_action_state.setOnClickListener(new View.OnClickListener() {
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
                actionList.get(position).put(ActionsLibHelper.map_val_action_download_state,
                        ActionsLibHelper.Action_download_state.downing);
                notifyItemChanged(position);

                mHelper.doDownLoad((ActionInfo) actionList.get(position).get(ActionsLibHelper.map_val_action));

            }
        });

    }

    @Override
    public int getItemCount() {
        return actionList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
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
