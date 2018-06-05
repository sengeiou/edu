package com.ubt.alpha1e_edu.userinfo.helpfeedback.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.userinfo.helpfeedback.feedback.FeedbackActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class PhotoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = PhotoRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    public List<Map<String,Object>> mDatas = new ArrayList<>();
    private View mView;
    private Handler mHandler = null;

    public PhotoRecyclerAdapter(Context mContext, List<Map<String,Object>> list, Handler handler) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        this.mHandler = handler;
    }

    public void setData(List<Map<String,Object>>  data) {
        this.mDatas = data;
    }
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyPhotoHolder myHolder = (MyPhotoHolder)holder;
        myHolder.ivAddPhoto.setImageBitmap((Bitmap) mDatas.get(position).get("bitmap"));
        myHolder.ivDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatas.remove(position);
                notifyDataSetChanged();
                mHandler.sendEmptyMessage(FeedbackActivity.DEL_PHOTO_UPDATE);
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_feedback_photo, parent, false);
        MyPhotoHolder myFeedbackHolder = new MyPhotoHolder(mView);
        return myFeedbackHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class MyPhotoHolder extends RecyclerView.ViewHolder
    {

        public RelativeLayout rlAddPhoto;
        public ImageView ivAddPhoto,ivDeletePhoto;
        public MyPhotoHolder(View view)
        {
            super(view);

            rlAddPhoto  = (RelativeLayout) view.findViewById(R.id.rl_add_photo);
            ivAddPhoto = (ImageView) view.findViewById(R.id.iv_add_photo);
            ivDeletePhoto = (ImageView) view.findViewById(R.id.iv_delete_photo);
        }
    }

}
