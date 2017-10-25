package com.ubt.alpha1e.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ubt.alpha1e.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/10.
 */
public class ActionsSimpleAdapter extends BaseAdapter {

    private static final String TAG = "ActionsSimpleAdapter";
    private LayoutInflater mInflater;
    private List<Map<String, Object>> actionList = null;
    private BaseActivity mActivity = null;
    private Context mContext;
    private int mActionType = -1;
    public static final int ACTION_TYPE_LATEST = 1;
    public static final int ACTION_TYPE_POPULAR = 2;


    private ActionsLibHelper mHelper;

    public ActionsSimpleAdapter(BaseActivity activity,List<Map<String, Object>> actions,ActionsLibHelper helper,int actionType){
        mActivity = activity;
        this.mInflater = LayoutInflater.from(activity);
        actionList = actions;
        mContext = activity.getApplicationContext();
        mHelper = helper;
        mActionType = actionType;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return actionList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setDatas( List<Map<String, Object>> actions)
    {
        actionList = actions;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        SimpleViewHolder myHolder = null;
        if (convertView == null) {
            myHolder = new SimpleViewHolder();
            convertView = mInflater.inflate(R.layout.layout_actions_simple_item, null);
            myHolder.actionLogoImg = (ImageView)convertView.findViewById(R.id.img_action_logo);
            myHolder.actionTypeImg = (ImageView)convertView.findViewById(R.id.img_action_type);
            myHolder.actionState = (ImageView)convertView.findViewById(R.id.img_state);

            myHolder.actionName = (TextView)convertView.findViewById(R.id.txt_action_name);
            myHolder.layDownload = (LinearLayout) convertView.findViewById(R.id.lay_download);

            myHolder.dowmloadProgress = (TextView)convertView.findViewById(R.id.txt_download_progress);
            myHolder.actionDownloadNum = (TextView)convertView.findViewById(R.id.txt_square_item_down_num);

            convertView.setTag(myHolder);
        }else {
            myHolder = (SimpleViewHolder)convertView.getTag();
        }

        if(actionList.size() > position){
            final ActionInfo actionInfo =(ActionInfo) actionList.get(position).get(ActionsLibHelper.map_val_action);
            if(actionInfo != null){
                myHolder.actionLogoImg.setVisibility(View.VISIBLE);
                myHolder.actionName.setVisibility(View.VISIBLE);
                myHolder.layDownload.setVisibility(View.VISIBLE);

                myHolder.actionName.setText(actionInfo.actionName + "");
                myHolder.actionDownloadNum.setText(actionInfo.actionDownloadTime > 9999 ? "9999+" : actionInfo.actionDownloadTime + "");

                myHolder.dowmloadProgress.setTextColor(getColorRes("download_progress_text_color_ft"));
                myHolder.actionDownloadNum.setTextColor(getColorRes("download_number_text_color_ft"));

                if (actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.download_finish) {
                    myHolder.dowmloadProgress.setVisibility(View.GONE);
                    myHolder.actionState.clearAnimation();
                    myHolder.actionState.setImageDrawable(getDrawableRes("my_actions_play_simple_item_ft"));
                    stopLoadingAnimation(myHolder);
                } else if (actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.not_download) {
                    myHolder.dowmloadProgress.setVisibility(View.GONE);
                    myHolder.actionState.clearAnimation();
                    myHolder.actionState.setImageDrawable(getDrawableRes("actions_online_download_ft"));
                    stopLoadingAnimation(myHolder);

                } else if (actionList.get(position).get(ActionsLibHelper.map_val_action_download_state) == ActionsLibHelper.Action_download_state.downing) {

                    myHolder.actionState.setImageDrawable(getDrawableRes("actionlib_item_downloading_icon_ft"));
                    myHolder.actionState.setVisibility(View.VISIBLE);
                    if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                        myHolder.dowmloadProgress.setVisibility(View.GONE);
                    }else {
                        myHolder.dowmloadProgress.setVisibility(View.VISIBLE);
                        Double b = (Double) actionList.get(position).get(ActionsLibHelper.map_val_action_download_progress);
                        int i = (int) (b + 0);
                        myHolder.dowmloadProgress.setText(i + "%");
                    }
                    startLoadingAnimation(myHolder);
                }
                if(actionInfo.actionResource==0)
                {
                    myHolder.actionTypeImg.setVisibility(View.VISIBLE);
                }else{
                    myHolder.actionTypeImg.setVisibility(View.GONE);
                }

                Glide.with(myHolder.actionLogoImg.getContext())
                        .load(actionInfo.actionHeadUrl)
                        .fitCenter().placeholder(R.drawable.sec_action_logo)
                        .into(myHolder.actionLogoImg);

                convertView.setOnClickListener(new View.OnClickListener() {
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

                myHolder.layDownload.setOnClickListener(new View.OnClickListener() {
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
                        notifyDataSetChanged();

                        mHelper.doDownLoad((ActionInfo) actionList.get(position).get(ActionsLibHelper.map_val_action));
                    }
                });
            }else {
                myHolder.actionLogoImg.setVisibility(View.INVISIBLE);
                myHolder.actionTypeImg.setVisibility(View.INVISIBLE);
                myHolder.actionName.setVisibility(View.INVISIBLE);
                myHolder.layDownload.setVisibility(View.INVISIBLE);
            }
        }else {
            //有时候报数组下标越界，暂时找不出原因
            UbtLog.e(TAG,"actionList.size = " + actionList.size() + "     position = " + position);

            myHolder.actionLogoImg.setVisibility(View.INVISIBLE);
            myHolder.actionTypeImg.setVisibility(View.INVISIBLE);
            myHolder.actionName.setVisibility(View.INVISIBLE);
            myHolder.layDownload.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    /**
     * 启动下载转圈动画
     * @param myHolder
     */
    private void startLoadingAnimation(SimpleViewHolder myHolder)
    {
        if(myHolder.operatingAnim==null)
        {
            myHolder.operatingAnim = AnimationUtils.loadAnimation(mContext,R.anim.turn_around_anim);
            myHolder.operatingAnim.setInterpolator(new LinearInterpolator());
        }

        if(!myHolder.operatingAnim.hasStarted()){
            myHolder.actionState.startAnimation(myHolder.operatingAnim);
        }
    }

    /**
     * 停止下载转圈动画
     * @param myHolder
     */
    private void stopLoadingAnimation(SimpleViewHolder myHolder)
    {
        if(myHolder.operatingAnim!=null&&myHolder.operatingAnim.hasStarted())
        {
            myHolder.actionState.clearAnimation();
            myHolder.operatingAnim.cancel();
            myHolder.operatingAnim = null;
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

class SimpleViewHolder{
    public Animation operatingAnim;
    public ImageView actionLogoImg;
    public ImageView actionTypeImg;
    public ImageView actionState;
    public TextView actionName;
    public TextView actionDownloadNum;
    public TextView dowmloadProgress;
    public LinearLayout layDownload;
}


