package com.ubt.alpha1e.edu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.Constant;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.TimeTools;
import com.ubt.alpha1e.edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.edu.ui.ActionsLibPreviewWebActivity;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.ImageDetailActivity;
import com.ubt.alpha1e.edu.ui.MyActionsActivity;
import com.ubt.alpha1e.edu.ui.RobotConnectedActivity;
import com.ubt.alpha1e.edu.ui.VideoPlayerActivity;
import com.ubt.alpha1e.edu.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.edu.ui.helper.ActionsOnlineHelper;
import com.ubt.alpha1e.edu.ui.helper.SettingHelper;
import com.ubt.alpha1e.edu.utils.ResourceUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/12.
 */
public class FillSquareContent {

    private static final String TAG = "FillSquareContent";

    /**
     * 填充微博图片列表,包括原创微博和转发微博中的图片都可以使用

     public static void fillWeiBoImgList(ArrayList<VideoListItem> listItems, Context context, RecyclerView imageList, int pos) {
     imageList.setVisibility(View.VISIBLE);
     ArrayList<String> imageDatas = listItems.get(pos).imageArray;
     GridLayoutManager gridLayoutManager = initGridLayoutManager(imageDatas, context);
     ImageAdapter imageAdapter = new ImageAdapter(imageDatas, context);
     imageList.setHasFixedSize(true);
     imageList.setAdapter(imageAdapter);
     imageList.setLayoutManager(gridLayoutManager);
     imageAdapter.setData(imageDatas);
     if (imageDatas == null || imageDatas.size() == 0) {
     imageList.setVisibility(View.GONE);
     }
     } */

    /**
     * 根据图片数量，初始化GridLayoutManager，并且设置列数，
     * 当图片 = 1 的时候，显示1列
     * 当图片<=4张的时候，显示2列
     * 当图片>4 张的时候，显示3列
     *
     * @return
     */
    private static GridLayoutManager initGridLayoutManager(ArrayList<String> imageDatas, Context context) {
        GridLayoutManager gridLayoutManager;
        if (imageDatas != null) {
            switch (imageDatas.size()) {
                case 1:
                    gridLayoutManager = new GridLayoutManager(context, 1);
                    break;
                case 2:
                    gridLayoutManager = new GridLayoutManager(context, 2);
                    break;
                case 3:
                    gridLayoutManager = new GridLayoutManager(context, 3);
                    break;
                case 4:
                    gridLayoutManager = new GridLayoutManager(context, 2);
                    break;
                default:
                    gridLayoutManager = new GridLayoutManager(context, 3);
                    break;
            }
        } else {
            gridLayoutManager = new GridLayoutManager(context, 3);
        }
        return gridLayoutManager;
    }


    /**
     * 填充微博列表图片
     *
     //     * @param context
     //     * @param datas
     //     * @param position
     //     * @param imageView

     public static void fillImageList(final Context context, final ArrayList<String> datas, final int position, final ImageView imageView) {

     ((CropImageView) imageView).setCropType(CropType.CENTER_TOP);

     //若是长微博，还需要纠正尺寸
     FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
     layoutParams.height = (int) context.getResources().getDimension(R.dimen.home_weiboitem_imagesize_vertical_rectangle_height);
     layoutParams.width = (int) context.getResources().getDimension(R.dimen.home_weiboitem_imagesize_vertical_rectangle_width);
     ((CropImageView) imageView).setCropType(CropType.CENTER_TOP);
     Glide.with(context).load(datas.get(position)).into(imageView);
     }*/


    /** 保存方法 */
    /*Bitmap logoBitmap = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.arcnote_logo);
    ByteArrayOutputStream logoStream = new ByteArrayOutputStream();
    boolean res = logoBitmap.compress(Bitmap.CompressFormat.PNG,100,logoStream);
    //将图像读取到logoStream中
    byte[] logoBuf = logoStream.toByteArray();
    //将图像保存到byte[]中
    Bitmap temp = BitmapFactory.decodeByteArray(logoBuf,0,logoBuf.length);
    //将图像从byte[]中读取生成Bitmap 对象 temp
    saveMyBitmap("tttt",temp);*/
    //将图像保存到SD卡中
    public static void saveMyBitmap(String bitName,Bitmap mBitmap){

        File fdir = new File(FileTools.file_path+"/testBitmap");
        if(!fdir.exists()){
            fdir.mkdir();
        }

        File f = new File(fdir + "/" + new Date().getTime() + ".png");
        try {
            if(!f.exists()){
                f.createNewFile();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            UbtLog.d(TAG,"lihai-----"+e.getMessage());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UbtLog.d(TAG,"mBitmap:"+mBitmap + "  fOut="+fOut + " PNG:"+ Bitmap.CompressFormat.PNG + " f:"+f.isFile() + " :"+f.getPath());
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 填充动作广场列表
     */
    public static void fillActionSquareContent(final Activity activity,final Context mContext, final SquareHolder holder,
                                               final Map<String, Object> actionInfo,
                                               final ActionsOnlineHelper mHelper,
                                               final int position,
                                               final boolean isShowActionOnly) {



        final ActionOnlineInfo info = (ActionOnlineInfo) actionInfo.get(ActionsLibHelper.map_val_action);
        UbtLog.d(TAG,"info.actionName = " + info.actionName + "    isShowActionOnly = " + isShowActionOnly);
        Glide.with(holder.img_action_logo.getContext())
                .load(info.actionHeadUrl)
                .fitCenter().placeholder(R.drawable.sec_action_logo)
                .into(holder.img_action_logo);

        holder.txt_action_name.setText(info.actionName);
        holder.txt_des.setText(info.actionDesciber);
        holder.img_type_logo.setImageResource(ResourceUtils.getActionTypeImage(info.actionSonType,(BaseActivity) activity));
        holder.txt_type_des.setText(ResourceUtils.getActionType(info.actionSonType,(BaseActivity) activity));
        holder.txt_des.setText(info.actionDesciber);
        holder.txt_time.setText(TimeTools.getMMTime((int)info.actionTime * 1000) +"");
        holder.txt_sees.setText(info.actionBrowseTime>=9999?"9999+":info.actionBrowseTime+"");
        holder.txt_square_item_down_num.setText(info.actionDownloadTime + "");


        //if only show actions
        if(isShowActionOnly){
            holder.layout_square_main.setVisibility(View.GONE);
            holder.action_square_action_split.setVisibility(View.GONE);
            holder.action_square_item_split.setVisibility(View.GONE);
        }else {
            holder.layout_square_main.setVisibility(View.VISIBLE);
            holder.action_square_action_split.setVisibility(View.VISIBLE);
            holder.action_square_item_split.setVisibility(View.VISIBLE);

            if (holder.getItemViewType() == 0)//video
            {
                holder.rl_square_content_default.setVisibility(View.GONE);
                holder.img_square_content.setVisibility(View.GONE);
                holder.img_play.setVisibility(View.VISIBLE);
                holder.mVideoView.setVisibility(View.VISIBLE);

                String actionVideoPath = info.actionVideoPath;
                if(!TextUtils.isEmpty(actionVideoPath)){
                    //actionVideoPath = actionVideoPath.replaceAll(" ","%20") + "?vframe/png/offset/0";
                    actionVideoPath = actionVideoPath.replaceAll(" ","%20") + "?vframe/jpg/offset/0";
                }
                Glide.with(mContext).load(actionVideoPath).crossFade().into(holder.mVideoView);

                holder.img_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VideoPlayerActivity.launchActivity(mContext,info);
                    }
                });
            } else {
                holder.img_play.setVisibility(View.GONE);
                holder.mVideoView.setVisibility(View.GONE);

                //UbtLog.d(TAG,"lihai--------userName::"+info.userName + "   actionName:"+info.actionName + " actionDesciber:"+info.actionDesciber + "  actionImagePath:"+ info.actionImagePath);
                if(TextUtils.isEmpty(info.actionImagePath)){
                    holder.img_square_content.setVisibility(View.GONE);
                    holder.rl_square_content_default.setVisibility(View.VISIBLE);
                }else {
                    holder.rl_square_content_default.setVisibility(View.GONE);
                    holder.img_square_content.setVisibility(View.VISIBLE);
                    //Glide 有缓存，只有第一次会请求网络
                    Glide.with(mContext).load(info.actionImagePath).crossFade().into(holder.img_square_content);
                }
            }
        }



        holder.txt_square_item_down_num.setTextColor(getColorRes(mContext,"download_number_text_color_ft"));
        holder.txt_progress.setTextColor(getColorRes(mContext,"download_progress_text_color_ft"));
        if (actionInfo.get(ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.download_finish) {
            holder.txt_progress.setVisibility(View.GONE);
            holder.img_state.clearAnimation();
            holder.img_state.setImageDrawable(getDrawableRes(mContext,"my_actions_play_item_ft"));
            stopLoadingAnimation(holder, mContext);

        } else if (actionInfo.get(ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.not_download) {
            holder.txt_progress.setVisibility(View.GONE);
            holder.img_state.clearAnimation();
            holder.img_state.setImageDrawable(getDrawableRes(mContext,"actions_online_download_ft"));
            stopLoadingAnimation(holder, mContext);

        } else if (actionInfo.get(ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.downing) {

            holder.img_state.setImageDrawable(getDrawableRes(mContext,"actionlib_item_downloading_icon_ft"));

            if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                holder.txt_progress.setVisibility(View.GONE);
            }else {
                if (holder.txt_progress.getVisibility() != View.VISIBLE) {
                    holder.txt_progress.setVisibility(View.VISIBLE);
                }
                Double b = (Double) actionInfo.get(ActionsLibHelper.map_val_action_download_progress);
                int i = (int) (b + 0);
                if(i < 0){
                    i = 0;
                }
                holder.txt_progress.setText(i + "%");
            }

            startLoadingAnimation(holder, mContext);
        }
        //UbtLog.d("FillOnlineContent","info.userImage->"+info.userImage+"    info.userName="+info.userName + "   "+(info.actionResume == null?info.actionDesciber:info.actionResume) + "    "+info.actionImagePath);

        if(info.actionPraiseTime <= 0){
            if(info.isPraise == 1){
                info.actionPraiseTime = 1;
            }else {
                info.actionPraiseTime = 0;
            }
        }

        if(info.actionCollectTime <= 0){
            if(info.isCollect == 1){
                info.actionCollectTime = 1;
            }else {
                info.actionCollectTime = 0;
            }
        }

        holder.img_square_collect.setImageResource(info.isCollect == 1 ?
                R.drawable.actions_square_detail_favorite_s : R.drawable.actions_square_detail_favorite);
        holder.txt_square_collect.setText(info.actionCollectTime+ "");

        holder.img_square_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDetailActivity.launchActivity(mContext,info.actionImagePath);
            }
        });
        /**点击点赞*/
       /* holder.rl_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.isPraise == 1) {
                    Toast.makeText(
                            mContext,
                            mContext.getResources()
                                    .getString(R.string.ui_action_re_praise), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if(mHelper.doPraise(info))
                {
                    holder.img_square_item_like.setImageResource(R.drawable.actions_square_detail_like_s);
                }
                holder.txt_square_item_like.setText(info.actionPraiseTime + "");


            }
        });*/

        /**点击收藏*/
        holder.img_square_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.isCollect == 0) {
                    if(mHelper.doCollocatWeb(info))
                    {
                        holder.img_square_collect.setImageResource(R.drawable.actions_square_detail_favorite_s);
                    }
                } else {
                    if(mHelper.doRemoveCollectWeb(info))
                    {
                        holder.img_square_collect.setImageResource(R.drawable.actions_square_detail_favorite);
                    }
                }
                holder.txt_square_collect.setText(info.actionCollectTime + "");
//                mHelper.doUpdateItem(position);

            }
        });

        /**点击分享*/
        /*holder.rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.doShareToOthers(info);

            }
        });*/

        /**点击下载*/
        holder.img_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionsOnlineHelper.Action_download_state current_state =
                        (ActionsOnlineHelper.Action_download_state) actionInfo
                        .get(ActionsOnlineHelper.map_val_action_download_state);
                if (current_state != ActionsOnlineHelper.Action_download_state.not_download) {
                    if (current_state == ActionsOnlineHelper.Action_download_state.download_finish) {
                        if (mHelper.isLostCoon()) {
                            RobotConnectedActivity.launchActivity(activity,true, Constant.BLUETOOTH_CONNECT_REQUEST_CODE);
                        } else {
                            if(mHelper.isLogin()){
                                //检测是否在充电状态和边充边玩状态是否打开
                                if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(mContext)){
                                    Toast.makeText(mContext, AlphaApplication.getBaseActivity().getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                MyActionsActivity.launchActivity(mContext,1,info.actionId);
                            }
                        }
                    }
                    return;
                }

                actionInfo.put(ActionsOnlineHelper.map_val_action_download_state,
                        ActionsOnlineHelper.Action_download_state.downing);
                mHelper.doUpdateItem(position);
                mHelper.doDownLoad(info);
            }
        });

        holder.layout_square_action_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionsLibPreviewWebActivity.launchActivity(activity, info,getDownloadState(actionInfo),(double)actionInfo.get(
                        ActionsOnlineHelper.map_val_action_download_progress),10086);
                info.actionBrowseTime++;
                mHelper.updateActionOnlineCache(info);
            }
        });

    }

    private static void startLoadingAnimation(SquareHolder holder, Context mContext) {
        if (holder.operatingAnim == null) {
            holder.operatingAnim = AnimationUtils.loadAnimation(mContext,
                    R.anim.turn_around_anim);
            holder.operatingAnim.setInterpolator(new LinearInterpolator());
        }
        if (!holder.operatingAnim.hasStarted()){
            holder.img_state.startAnimation(holder.operatingAnim);
        }

    }

    private static void stopLoadingAnimation(SquareHolder holder, Context mContext) {
        if (holder.operatingAnim != null && holder.operatingAnim.hasStarted()) {
            holder.img_state.clearAnimation();
            holder.operatingAnim.cancel();
            holder.operatingAnim = null;
        }

    }


    /**
     * 获取下载状态
     * */
    public static int getDownloadState(Map<String,Object> actionList)
    {

        int state = -1;
        if(actionList.get( ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.download_finish)
        {
            state = 1;
        }else if(actionList.get( ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.not_download)
        {
            state = 0;
        }else if(actionList.get( ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.downing){
            state = 2;
        }
        return  state;
    }

    /**
     * 动态获取图片
     * @param DrawableKey 图片Key
     * @return
     */
    private static Drawable getDrawableRes(Context mActivity, String DrawableKey){
        return ((BaseActivity)mActivity).getDrawableRes(DrawableKey);
    }

    /**
     * 动态获取颜色值
     * @param colorKey 颜色值Key
     * @return
     */
    public static ColorStateList getColorRes(Context mActivity, String colorKey){
        return ((BaseActivity)mActivity).getColorRes(colorKey);
    }
}
