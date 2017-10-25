package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.library.PhotoView.PhotoView;
import com.ubt.alpha1e.library.PhotoView.PhotoViewAttacher;
import com.ubt.alpha1e.ui.custom.HackyViewPager;

public class ImageDetailActivity extends BaseActivity  {
    private ViewPager mViewPager;
    private String mImageUrl="";

    public static void launchActivity(Context mContext,String imageUrl)
    {
        Intent intent = new Intent();
        intent.putExtra("imageUrl",imageUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(mContext,ImageDetailActivity.class);
        mContext.startActivity(intent);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        if(getIntent().getExtras()!=null)
        mImageUrl = (String)getIntent().getExtras().get("imageUrl");
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        setContentView(mViewPager);
        mViewPager.setAdapter(new SamplePagerAdapter(this,mImageUrl));
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    class SamplePagerAdapter extends PagerAdapter {

        private String mImageUrl ="";
        private Activity mContext;

        SamplePagerAdapter(Activity mContext, String mImageUrl)
        {
            this.mImageUrl = mImageUrl;
            this.mContext =mContext;
        }
        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                         mContext.finish();
                }

                @Override
                public void onOutsidePhotoTap() {
                }
            });
            Glide.with(mContext).load(mImageUrl).fitCenter().into(photoView);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
