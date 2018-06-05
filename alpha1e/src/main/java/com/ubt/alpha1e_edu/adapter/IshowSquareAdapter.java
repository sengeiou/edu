package com.ubt.alpha1e_edu.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e_edu.library.AutoVideo.visibility.items.ListItem;
import com.ubt.alpha1e_edu.library.AutoVideo.visibility.items.VideoItem;
import com.ubt.alpha1e_edu.library.AutoVideo.visibility.scroll.ItemsProvider;
import com.ubt.alpha1e_edu.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e_edu.ui.helper.ActionsOnlineHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/11.
 */
public class IshowSquareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>implements ItemsProvider{

    private static final String TAG = "ActionsSquareAdapter";

    private static final int TYPE_VIEDO_ITEM = 0;
    private static final int TYPE_PICTURE_ITEM = 1;
    private Context mContext;
    private Activity activity;
    private List<Map<String,Object>> mDatas;
    private View mView;
    public List<VideoItem> videoListItems = new ArrayList<>();
    public RecyclerView mRecyclerView;
    public ActionsOnlineHelper mHelper;

    public boolean isScolling = false;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == TYPE_VIEDO_ITEM) {
//            mView = LayoutInflater.from(mContext).inflate(R.layout.actions_square_video_item, parent, false);
//            VideoSquareHolder mVideoHolder = new VideoSquareHolder(mView);
//            return mVideoHolder;
//        } else if (viewType == TYPE_PICTURE_ITEM) {
//            mView = LayoutInflater.from(mContext).inflate(R.layout.actions_square_image_item, parent, false);
//            ImageSquareHolder mImageHolder = new ImageSquareHolder(mView);
//            return mImageHolder;
//        }
        mView = LayoutInflater.from(mContext).inflate(R.layout.actions_square_ishow_item, parent, false);
        IshowSquareHolder mVideoHolder = new IshowSquareHolder(mView);
//        if (viewType == TYPE_VIEDO_ITEM) {
//            mVideoHolder.img_square_content.setVisibility(View.INVISIBLE);
//            mVideoHolder.img_play.setVisibility(View.VISIBLE);
//        }else
//        {
//            mVideoHolder.img_square_content.setVisibility(View.VISIBLE);
//            mVideoHolder.img_play.setVisibility(View.VISIBLE);
//        }
        return mVideoHolder;

//        return null;
    }


    public IshowSquareAdapter(Activity mActivity, Context context, List<Map<String,Object>> mDatas,
                              RecyclerView recyclerView, ActionsOnlineHelper mHelper)
    {
        this.activity = mActivity;
        this.mContext = context;
        this.mDatas = mDatas;
        this.mRecyclerView = recyclerView;
        this.mHelper = mHelper;
    }

    public void setDatas(List<Map<String,Object>> mDatas, List<VideoItem> listItems)
    {
        this.mDatas = mDatas;
        this.videoListItems = listItems;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        FillIshowContent.fillIshowSquareContent(activity,mContext,(IshowSquareHolder) holder,mDatas.get(position),mHelper,position,this.isScolling);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        ActionOnlineInfo info = (ActionOnlineInfo) mDatas.get(position).get(ActionsLibHelper.map_val_action);
        if (!TextUtils.isEmpty(info.actionVideoPath)&&info.actionVideoPath.startsWith("http")) {
            return TYPE_VIEDO_ITEM;
        } else {
            return TYPE_PICTURE_ITEM;
        }
    }



    @Override
    public ListItem getListItem(int position) {
        RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder instanceof ListItem) {
            return (ListItem) holder;
        }
        return null;
    }

    @Override
    public int listItemSize() {
        return videoListItems.size();
    }

    public void setScrolling(boolean isScolling){
        this.isScolling = isScolling;
    }
}

