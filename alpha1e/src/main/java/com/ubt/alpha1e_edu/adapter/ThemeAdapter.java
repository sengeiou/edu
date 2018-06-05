package com.ubt.alpha1e_edu.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.bumptech.glide.Glide;
import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.model.BannerInfo;
import com.ubt.alpha1e_edu.ui.BannerDetailActivity;
import com.ubt.alpha1e_edu.utils.SizeUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/3/18.
 */
public class ThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "ThemeAdapter";

    private List<BannerInfo> themeList;
    private Activity mActivity;
    private boolean isPad = false;

    public ThemeAdapter(List<BannerInfo> themes, Activity activity) {
        this.themeList = themes;
        this.mActivity = activity;
        this.isPad = AlphaApplication.isPad();
    }

    public void setDatas( List<BannerInfo> themes)
    {
        themeList = themes;
    }

    public class ThemeViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final ImageView themeImg;
        public final TextView themeName;

        public ThemeViewHolder(View view) {
            super(view);
            mView = view;
            themeImg = (ImageView) view.findViewById(R.id.img_theme_logo);
            themeName = (TextView) view.findViewById(R.id.txt_theme_name);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activty_action_theme_item, parent, false);
        RecyclerView.ViewHolder vh = new ThemeViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ThemeViewHolder myHolder = (ThemeViewHolder) holder;
        final BannerInfo theme = themeList.get(position);

        if(isPad){
            LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) myHolder.themeImg.getLayoutParams();
            llParams.height = SizeUtils.dip2px(mActivity,200);
            myHolder.themeImg.setLayoutParams(llParams);
        }

        Glide.with(myHolder.themeImg.getContext())
                .load(theme.recommendImage)
                .fitCenter()
                .into(myHolder.themeImg);

        myHolder.themeName.setText(theme.actionTitle);

        myHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerDetailActivity.launchActivity(mActivity, PG.convertParcelable(theme));
            }
        });
    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }
}
