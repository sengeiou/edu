package com.pbq.imagepicker.ui.image;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pbq.imagepicker.ImagePicker;
import com.pbq.imagepicker.R;
import com.pbq.imagepicker.adapter.image.PhotoSelectAdapter;
import com.pbq.imagepicker.bean.ImageItem;
import com.pbq.imagepicker.view.SuperCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImagePreviewActivity extends ImagePreviewBaseActivity implements ImagePicker.OnImageSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = ImagePreviewActivity.class.getSimpleName();

    public static final String ISORIGIN = "isOrigin";

    private boolean isOrigin;                      //是否选中原图
    private SuperCheckBox mCbCheck;                //是否选中当前图片的CheckBox
    private SuperCheckBox mCbOrigin;               //原图
    private TextView tvFinish;                     //确认图片的选择
    private ImageView ivBack;                     //返回
    private View bottomBar;

    private RecyclerView rvSelectPhoto = null;
    private PhotoSelectAdapter mAdapter;
    private List<ImageItem> mSelectImageList; //当前选择的所有图片

    private ImagePreviewActivity mActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = ImagePreviewActivity.this;

        isOrigin = getIntent().getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
        imagePicker.addOnImageSelectedListener(this);

        tvFinish = (TextView) findViewById(R.id.tv_finish);
        tvFinish.setOnClickListener(this);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);

        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setVisibility(View.VISIBLE);

        mCbCheck = (SuperCheckBox) findViewById(R.id.cb_check);
        mCbOrigin = (SuperCheckBox) findViewById(R.id.cb_origin);
        mCbOrigin.setText(getString(R.string.origin));
        mCbOrigin.setOnCheckedChangeListener(this);
        mCbOrigin.setChecked(isOrigin);
        rvSelectPhoto = (RecyclerView) findViewById(R.id.rv_select_photo);

        initRecyclerViews();

        //初始化当前页面的状态
        onImageSelected(0, null, false);
        ImageItem item = mImageItems.get(mCurrentPosition);
        boolean isSelected = imagePicker.isSelect(item);
        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
        mCbCheck.setChecked(isSelected);
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                ImageItem item = mImageItems.get(mCurrentPosition);
                boolean isSelected = imagePicker.isSelect(item);
                mCbCheck.setChecked(isSelected);
                mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
            }
        });
        //当点击当前选中按钮的时候，需要根据当前的选中状态添加和移除图片
        mCbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageItem imageItem = mImageItems.get(mCurrentPosition);
                int selectLimit = imagePicker.getSelectLimit();

                if(mCbCheck.isChecked()){
                    if(selectedImages.size() > 0){
                        ImageItem imageItem0 =  selectedImages.get(0);
                        if(imageItem0.isVideo() != imageItem.isVideo()){
                            Toast.makeText(mActivity, mActivity.getString(R.string.select_video_image_limit), Toast.LENGTH_SHORT).show();
                            mCbCheck.setChecked(false);
                            return;
                        }

                        if(imageItem0.isVideo() && imageItem.isVideo()){
                            Toast.makeText(mActivity, mActivity.getString(R.string.select_video_one_limit), Toast.LENGTH_SHORT).show();
                            mCbCheck.setChecked(false);
                            return;
                        }
                    }

                    if(selectedImages.size() >= selectLimit){
                        Toast.makeText(mActivity, mActivity.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                        mCbCheck.setChecked(false);
                    }else {
                        imagePicker.addSelectedImageItem(mCurrentPosition, imageItem, mCbCheck.isChecked());
                    }
                }else {
                    imagePicker.addSelectedImageItem(mCurrentPosition, imageItem, mCbCheck.isChecked());
                }
            }
        });
    }

    /**
     * 图片添加成功后，修改当前图片的选中数量
     * 当调用 addSelectedImageItem 或 deleteSelectedImageItem 都会触发当前回调
     */
    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (imagePicker.getSelectImageCount() > 0) {

            //tvFinish.setText(getString(R.string.select_complete, imagePicker.getSelectImageCount(), imagePicker.getSelectLimit()));
            tvFinish.setText(getString(R.string.select_complete_single, imagePicker.getSelectImageCount()));
            tvFinish.setEnabled(true);
        } else {

            tvFinish.setText(getString(R.string.complete));
            tvFinish.setEnabled(false);
        }

        mSelectImageList.clear();
        mSelectImageList.addAll(imagePicker.getSelectedImages());
        mAdapter.notifyDataSetChanged();

        if (mCbOrigin.isChecked()) {
            long size = 0;
            for (ImageItem imageItem : selectedImages){
                size += imageItem.size;
            }
            String fileSize = Formatter.formatFileSize(this, size);
            mCbOrigin.setText(getString(R.string.origin_size, fileSize));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok || id == R.id.tv_finish) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
            setResult(ImagePicker.RESULT_IMAGE_ITEMS, intent);
            finish();
        } else if (id == R.id.iv_back) {
            onBack();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack(){
        Intent intent = new Intent();
        intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
        setResult(ImagePicker.RESULT_IMAGE_BACK, intent);
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_origin) {
            if (isChecked) {
                long size = 0;
                for (ImageItem item : selectedImages)
                    size += item.size;
                String fileSize = Formatter.formatFileSize(this, size);
                isOrigin = true;
                mCbOrigin.setText(getString(R.string.origin_size, fileSize));
            } else {
                isOrigin = false;
                mCbOrigin.setText(getString(R.string.origin));
            }
        }
    }

    public void initRecyclerViews() {
        mSelectImageList = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSelectPhoto.setLayoutManager(mLayoutManager);
        rvSelectPhoto.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 15;
                outRect.left = 15;
            }
        });

        mAdapter = new PhotoSelectAdapter(this, mSelectImageList, null);
        rvSelectPhoto.setAdapter(mAdapter);
        Log.d(TAG,"imagePicker.getSelectImageCount() = " +imagePicker.getSelectImageCount());
        if(imagePicker.getSelectImageCount() > 0){
            mSelectImageList.addAll(imagePicker.getSelectedImages());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        imagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    /** 单击时，隐藏头和尾 */
    @Override
    public void onImageSingleTap() {
        if (bottomBar.getVisibility() == View.VISIBLE) {
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.imagepick_fade_out));
            bottomBar.setVisibility(View.GONE);
        } else {
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.imagepick_fade_in));
            bottomBar.setVisibility(View.VISIBLE);
        }
    }
}
