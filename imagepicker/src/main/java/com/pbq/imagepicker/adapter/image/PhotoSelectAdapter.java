package com.pbq.imagepicker.adapter.image;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.pbq.imagepicker.ImagePicker;
import com.pbq.imagepicker.R;
import com.pbq.imagepicker.bean.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class PhotoSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = PhotoSelectAdapter.class.getSimpleName();

    private Context mContext;
    public List<ImageItem> mDatas = new ArrayList<>();
    private View mView;
    private Handler mHandler = null;

    public PhotoSelectAdapter(Context mContext, List<ImageItem> list, Handler handler) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        this.mHandler = handler;
    }

    public void setData(List<ImageItem>  data) {
        this.mDatas = data;
    }
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyPhotoHolder myHolder = (MyPhotoHolder)holder;
        ImageItem imageItem = mDatas.get(position);
        ImagePicker.getInstance().getImageLoader().displayImage((Activity) mContext, imageItem.path, myHolder.ivPhoto, 0, 0);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.imagepick_select_item_image, parent, false);
        MyPhotoHolder myFeedbackHolder = new MyPhotoHolder(mView);
        return myFeedbackHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class MyPhotoHolder extends RecyclerView.ViewHolder
    {

        private ImageView ivPhoto;
        public MyPhotoHolder(View view)
        {
            super(view);

            ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
        }
    }

}
