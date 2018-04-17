package com.pbq.imagepicker.adapter.image;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pbq.imagepicker.ImagePicker;
import com.pbq.imagepicker.R;
import com.pbq.imagepicker.Utils;
import com.pbq.imagepicker.VideoPicker;
import com.pbq.imagepicker.bean.ImageItem;
import com.pbq.imagepicker.ui.image.ImageBaseActivity;
import com.pbq.imagepicker.ui.image.ImageGridActivity;
import com.pbq.imagepicker.ui.media.MediaGridActivity;
import com.pbq.imagepicker.view.SuperCheckBox;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final String TAG = ImageGridAdapter.class.getSimpleName();

    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机

    private VideoPicker videoPicker;
    private ImagePicker imagePicker;
    private Activity mActivity;
    private ArrayList<ImageItem> images;       //当前需要显示的所有的图片数据
    private ArrayList<ImageItem> mSelectedImages; //全局保存的已经选中的图片数据
    private boolean isShowCamera;         //是否显示拍照按钮
    private int mImageSize;               //每个条目的大小
    private OnImageItemClickListener listener;   //图片被点击的监听

    public ImageGridAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        if (images == null || images.size() == 0) {
            this.images = new ArrayList<>();
        }else {
            this.images = images;
        }

        mImageSize = Utils.getImageItemWidth(mActivity);

        Log.d(TAG,"mImageSize = " + mImageSize);
        imagePicker = ImagePicker.getInstance();
        isShowCamera = imagePicker.isShowCamera();

        videoPicker = VideoPicker.getInstance();

        mSelectedImages = imagePicker.getSelectedImages();
    }

    public void refreshData(ArrayList<ImageItem> images) {
        if (images == null || images.size() == 0) {
            this.images = new ArrayList<>();
        } else {
            this.images = images;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) {
            return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return isShowCamera ? images.size() + 1 : images.size();
    }

    @Override
    public ImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) {
                return null;
            }
            return images.get(position - 1);
        } else {
            return images.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == ITEM_TYPE_CAMERA) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.imagepick_adapter_camera_item, parent, false);
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
            convertView.setTag(null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((ImageBaseActivity) mActivity).checkPermission(Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                    } else {
                        //imagePicker.takePicture(mActivity, ImagePicker.REQUEST_IMAGE_TAKE);

                        Log.d(TAG,"mActivity = " + mActivity);
                        if(mActivity instanceof MediaGridActivity){
                            ((MediaGridActivity)mActivity).startCapture();
                        }
                    }
                }
            });
        } else {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.imagepick_adapter_image_item, parent, false);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ImageItem imageItem = getItem(position);
            if(imageItem.isVideo()){
                holder.rlVideoMsg.setVisibility(View.VISIBLE);
                //显示时长 转化成分秒
                holder.tvTimeLong.setText(videoPicker.timeParse(imageItem.timeLong));
            }else {
                holder.rlVideoMsg.setVisibility(View.GONE);
            }

            holder.ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onImageItemClick(holder.rootView, imageItem, position);
                    }
                }
            });


            holder.cbCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectLimit = imagePicker.getSelectLimit();
                    Log.d(TAG,"isChecked() = " + holder.cbCheck.isChecked());

                    if (holder.cbCheck.isChecked()) {
                        Log.d(TAG,"selectLimit = " + selectLimit + "    mSelectedImages.size() => " + mSelectedImages.size());
                        if(mSelectedImages.size() > 0){
                            ImageItem imageItem0 =  mSelectedImages.get(0);
                            if(imageItem0.isVideo() != imageItem.isVideo()){
                                Toast.makeText(mActivity, mActivity.getString(R.string.select_video_image_limit), Toast.LENGTH_SHORT).show();
                                holder.cbCheck.setChecked(false);
                                return;
                            }

                            if(imageItem0.isVideo() && imageItem.isVideo()){
                                Toast.makeText(mActivity, mActivity.getString(R.string.select_video_one_limit), Toast.LENGTH_SHORT).show();
                                holder.cbCheck.setChecked(false);
                                return;
                            }
                        }

                        if(mSelectedImages.size() >= selectLimit){
                            Toast.makeText(mActivity, mActivity.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                            holder.cbCheck.setChecked(false);
                        }else {
                            imagePicker.addSelectedImageItem(position, imageItem, holder.cbCheck.isChecked());
                        }
                    } else {
                        imagePicker.addSelectedImageItem(position, imageItem, holder.cbCheck.isChecked());
                    }
                }
            });

            //根据是否多选，显示或隐藏checkbox
            if (imagePicker.isMultiMode()) {
                holder.cbCheck.setVisibility(View.VISIBLE);
                boolean checked = mSelectedImages.contains(imageItem);
                if (checked) {
                    holder.cbCheck.setChecked(true);
                } else {
                    holder.cbCheck.setChecked(false);
                }
            } else {

            }

            imagePicker.getImageLoader().displayImage(mActivity, imageItem.path, holder.ivThumb, mImageSize, mImageSize); //显示图片
        }
        return convertView;
    }

    private class ViewHolder {
        public View rootView;
        public ImageView ivThumb;
        public View mask;
        public SuperCheckBox cbCheck;
        public RelativeLayout rlVideoMsg;
        public TextView tvTimeLong;

        public ViewHolder(View view) {
            rootView = view;
            ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
            mask = view.findViewById(R.id.mask);
            cbCheck = (SuperCheckBox) view.findViewById(R.id.cb_check);
            rlVideoMsg = (RelativeLayout) view.findViewById(R.id.rl_video_msg);
            tvTimeLong = (TextView) view.findViewById(R.id.tv_time_long);
        }
    }

    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View view, ImageItem imageItem, int position);
    }
}