package com.ubt.alpha1e.edu.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.bumptech.glide.Glide;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.model.BannerInfo;
import com.ubt.alpha1e.edu.ui.BannerDetailActivity;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e.edu.utils.SizeUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.List;

/**
 * Created by Administrator on 2017/2/10.
 */
public class ActionsThemeSimpleAdapter extends BaseAdapter {

    private static final String TAG = "ActionsThemeSimpleAdapter";
    private LayoutInflater mInflater;
    private List<BannerInfo> themeList = null;
    private BaseActivity mActivity = null;
    private boolean isPad = false;


    public ActionsThemeSimpleAdapter(BaseActivity activity, List<BannerInfo> themes, ActionsLibHelper helper){
        mActivity = activity;
        this.mInflater = LayoutInflater.from(activity);
        this.themeList = themes;
        this.isPad = AlphaApplication.isPad();
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return themeList.size();
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

    public void setDatas( List<BannerInfo> shemes)
    {
        themeList = shemes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ThemeViewHolder myHolder = null;
        if (convertView == null) {
            myHolder = new ThemeViewHolder();
            convertView = mInflater.inflate(R.layout.layout_actions_theme_item, null);
            myHolder.themeImg = (ImageView)convertView.findViewById(R.id.img_theme_logo);
            myHolder.themeName = (TextView)convertView.findViewById(R.id.txt_theme_name);
            convertView.setTag(myHolder);
        }else {
            myHolder = (ThemeViewHolder)convertView.getTag();
        }

        if(themeList.size() > position){

            if(isPad){
                LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) myHolder.themeImg.getLayoutParams();
                llParams.height = SizeUtils.dip2px(mActivity,150);
                myHolder.themeImg.setLayoutParams(llParams);
            }

            final BannerInfo theme = themeList.get(position);
            if(!TextUtils.isEmpty(theme.recommendImage)){
                Glide.with(myHolder.themeImg.getContext())
                        .load(theme.recommendImage)
                        .fitCenter()
                        .into(myHolder.themeImg);

                myHolder.themeName.setText(theme.actionTitle);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BannerDetailActivity.launchActivity(mActivity, PG.convertParcelable(theme));
                    }
                });
            }
        }else {
            //有时候报数组下标越界，暂时找不出原因
            UbtLog.e(TAG,"themeList.size = " + themeList.size() + "     position = " + position);
        }
        return convertView;
    }
}

class ThemeViewHolder{
    public ImageView themeImg;
    public TextView themeName;
}


