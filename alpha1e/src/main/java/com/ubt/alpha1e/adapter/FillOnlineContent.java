package com.ubt.alpha1e.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.OngetThumbnailsListener;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.ui.ActionsLibPreviewWebActivity;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.ImageDetailActivity;
import com.ubt.alpha1e.ui.MyActionsActivity;
import com.ubt.alpha1e.ui.RobotConnectedActivity;
import com.ubt.alpha1e.ui.VideoPlayerActivity;
import com.ubt.alpha1e.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.ui.helper.ActionsOnlineHelper;
import com.ubt.alpha1e.utils.ResourceUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/12.
 */
public class FillOnlineContent {

    private static final String TAG = "FillOnlineContent";

    public static HashMap<String,String> thumbnailDownloadingMap = new HashMap<>();
    public static HashMap<String,Bitmap> thumbnailMap = new HashMap<>();
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
        UbtLog.d(TAG,"lihai---------mBitmap:"+mBitmap + "  fOut="+fOut + " PNG:"+ Bitmap.CompressFormat.PNG + " f:"+f.isFile() + " :"+f.getPath());
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
    public static void fillActionSquareContent(final Activity activity,final Context mContext, final VideoSquareHolder holder,
                                               final Map<String, Object> actionInfo,
                                               final ActionsOnlineHelper mHelper,
                                               final int position) {

        final ActionOnlineInfo info = (ActionOnlineInfo) actionInfo.get(ActionsLibHelper.map_val_action);

        if(ActionsOnlineHelper.actionLocalSonType != 0){
            holder.txt_official.setVisibility(View.VISIBLE);
            holder.img_official.setVisibility(View.VISIBLE);
            holder.txt_official.setText(ResourceUtils.getActionType(info.actionSonType,(BaseActivity) activity));
            holder.img_official.setImageResource(ResourceUtils.getActionTypeImage(info.actionSonType,(BaseActivity) activity));
        }else{
            holder.txt_official.setVisibility(View.GONE);
            holder.img_official.setVisibility(View.GONE);
        }

        //官方推荐 暂时注释
        /*holder.txt_official.setVisibility(info.actionResource==0?View.VISIBLE:View.INVISIBLE);
        holder.img_official.setVisibility(info.actionResource==0?View.VISIBLE:View.INVISIBLE);*/

        if (holder.getItemViewType() == 0)//video
        {
            holder.img_square_content.setVisibility(View.GONE);
            holder.img_play.setVisibility(View.VISIBLE);
            holder.mVideoView.setVisibility(View.VISIBLE);
            UbtLog.d(TAG,"videoPath::" + "  bitmap:"+thumbnailMap.get(info.actionVideoPath) + "     position:"+position + "  videoPath:"+ info.actionVideoPath) ;
            if(thumbnailMap.get(info.actionVideoPath)!=null)
            {
                Bitmap bitmap = thumbnailMap.get(info.actionVideoPath);
                holder.mVideoView.setImageBitmap(bitmap);

            }else
            {
                String actionVideoPath = info.actionVideoPath;
                if(!TextUtils.isEmpty(actionVideoPath)){
                    actionVideoPath = actionVideoPath.replaceAll(" ","%20");
                }

                if(thumbnailDownloadingMap.containsKey(info.actionVideoPath)){
                    //已经在下载中，返回
                    UbtLog.d(TAG,"缩略图已经在下载中：" + info.actionVideoPath);
                }else {
                    thumbnailDownloadingMap.put(info.actionVideoPath,info.actionVideoPath);
                    GetDataFromWeb.getWebVideoThumbnails(actionVideoPath, 640, 480, new OngetThumbnailsListener() {
                        @Override
                        public void onGetVideoThumbnail(Bitmap bitmap) {
                            UbtLog.d(TAG,"lihai-----------videoPath:>" + "  bitmap:"+bitmap + "     position:"+position + "    info:"+info.actionDesciber + "  videoPath:"+ info.actionVideoPath) ;
                            if(bitmap!=null){
                                thumbnailMap.put(info.actionVideoPath,bitmap);
                                //saveMyBitmap(info.actionName,bitmap);
                            }
                            mHelper.doUpdateItem(position);
                        }
                    });
                }
            }
            holder.img_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoPlayerActivity.launchActivity(mContext,info);

                }
            });
        } else {
            holder.img_square_content.setVisibility(View.VISIBLE);
            holder.img_play.setVisibility(View.GONE);
            holder.mVideoView.setVisibility(View.GONE);
            //UbtLog.d(TAG,"lihai--------userName::"+info.userName + "   actionName:"+info.actionName + " actionDesciber:"+info.actionDesciber + "  actionImagePath:"+ info.actionImagePath);

            //Glide 有缓存，只有第一次会请求网络
            Glide.with(mContext).load(info.actionImagePath).crossFade().into(holder.img_square_content);

        }
        if (actionInfo.get(ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.download_finish) {
            holder.txt_progress.setVisibility(View.GONE);
            holder.img_square_item_down.clearAnimation();
            holder.img_square_item_down.setImageResource(R.drawable.actions_square_detail_downloaded);
            stopLoadingAnimation(holder, mContext);
        } else if (actionInfo.get(ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.not_download) {
            holder.txt_progress.setVisibility(View.GONE);
            holder.img_square_item_down.clearAnimation();
            holder.img_square_item_down.setImageResource(R.drawable.actions_square_detail_download);
            stopLoadingAnimation(holder, mContext);

        } else if (actionInfo.get(ActionsOnlineHelper.map_val_action_download_state) == ActionsOnlineHelper.Action_download_state.downing) {
            holder.img_square_item_down.setImageResource(R.drawable.actionlib_item_downloading_icon);
            holder.img_square_item_down.setVisibility(View.VISIBLE);

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
        UbtLog.d("FillOnlineContent","info.userImage->"+info.userImage+"    info.userName="+info.userName + "   "+(info.actionResume == null?info.actionDesciber:info.actionResume) + "    "+info.actionImagePath);

        if(ActionsOnlineHelper.actionLocalSonType == 0){
            Glide.with(mContext).load(info.userImage).centerCrop().placeholder(R.drawable.online_profile_ubt).into(holder.img_square_profile);
        }else {
            Glide.with(mContext).load(info.actionImagePath).centerCrop().placeholder(R.drawable.sec_action_logo).into(holder.img_square_profile);
        }

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

        holder.txt_sees.setText(info.actionBrowseTime>=9999?"9999+":info.actionBrowseTime+"");
        holder.txt_comments.setText(info.actionCommentTime>=9999?"9999+":info.actionCommentTime +"");

        if(ActionsOnlineHelper.actionLocalSonType == 0){
            holder.txt_profile_name.setText(info.userName);
        }else{
            holder.txt_profile_name.setText(info.actionName);
        }
        //holder.txt_profile_name.setText(info.userName);

        holder.txt_square_description.setText(info.actionResume == null?info.actionDesciber:info.actionResume);
        holder.txt_square_item_like.setText(info.actionPraiseTime + "");
        holder.txt_square_item_star.setText(info.actionCollectTime+ "");
        holder.txt_square_item_down.setText(info.actionDownloadTime + "");
        holder.img_square_item_like.setImageResource(info.isPraise == 1 ?
                R.drawable.actions_square_detail_like_s : R.drawable.actions_square_detail_like);
        holder.img_square_item_star.setImageResource(info.isCollect == 1 ?
                R.drawable.actions_square_detail_favorite_s : R.drawable.actions_square_detail_favorite);

        holder.img_square_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDetailActivity.launchActivity(mContext,info.actionImagePath);
            }
        });
        /**点击点赞*/
        holder.rl_like.setOnClickListener(new View.OnClickListener() {
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
        });

        /**点击收藏*/
        holder.rl_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.isCollect == 0) {
                    if(mHelper.doCollocatWeb(info))
                    {
                        holder.img_square_item_star.setImageResource(R.drawable.actions_square_detail_favorite_s);
                    }
                } else {
                    if(mHelper.doRemoveCollectWeb(info))
                    {
                        holder.img_square_item_star.setImageResource(R.drawable.actions_square_detail_favorite);
                    }

                }
                holder.txt_square_item_star.setText(info.actionCollectTime + "");
//                mHelper.doUpdateItem(position);

            }
        });

        /**点击分享*/
        holder.rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.doShareToOthers(info);

            }
        });

        /**点击下载*/
        holder.rl_down.setOnClickListener(new View.OnClickListener() {
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
                            MyActionsActivity.launchActivity(mContext,1);
                        }
                    }
                    return;
                }

                actionInfo.put(ActionsOnlineHelper.map_val_action_download_state,ActionsOnlineHelper.Action_download_state.downing);
                mHelper.doUpdateItem(position);
                mHelper.doDownLoad(info);
            }
        });

        holder.ly_detail_square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionsLibPreviewWebActivity.launchActivity(activity, info,getDownloadState(actionInfo),(double)actionInfo.get(
                        ActionsOnlineHelper.map_val_action_download_progress),10086);
                info.actionBrowseTime++;
                mHelper.updateActionOnlineCache(info);
            }
        });

    }

    private static void startLoadingAnimation(VideoSquareHolder holder, Context mContext) {
        if (holder.operatingAnim == null) {
            holder.operatingAnim = AnimationUtils.loadAnimation(mContext,
                    R.anim.turn_around_anim);
            holder.operatingAnim.setInterpolator(new LinearInterpolator());
        }
        if (!holder.operatingAnim.hasStarted())
            holder.img_square_item_down.startAnimation(holder.operatingAnim);
    }

    private static void stopLoadingAnimation(VideoSquareHolder holder, Context mContext) {
        if (holder.operatingAnim != null && holder.operatingAnim.hasStarted()) {
            holder.img_square_item_down.clearAnimation();
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
}
