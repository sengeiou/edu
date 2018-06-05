package com.ubt.alpha1e_edu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.ui.helper.BaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wilson on 2016/5/18.
 */
public class ActionsSyncRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private View mView;
    private List<Map<String, Object>> mDatas = new ArrayList<>();
    private int type = -1;
    private BaseHelper mHelper;


    public void setDatas(List<Map<String, Object>> mDatas )
    {
        this.mDatas = mDatas;
    }

    public ActionsSyncRecyclerAdapter(Context mContext, List<Map<String, Object>> list, int type, BaseHelper helper) {
        this.mContext = mContext;
        this.mDatas = list;
        this.type = type;
        this.mHelper = helper;
    }


    public class MySyncViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl_info;
        public ImageView img_action_logo, img_type_logo,img_select;
        public TextView txt_action_name, txt_time, txt_des, txt_type_des;
        public MySyncViewHolder(View view) {
            super(view);
            img_action_logo = (ImageView) view.findViewById(R.id.action_logo);
            img_type_logo = (ImageView) view.findViewById(R.id.img_type_logo);
            img_select = (ImageView) view.findViewById(R.id.img_select);
            rl_info  = (RelativeLayout) view.findViewById(R.id.rl_logo_info);
            txt_action_name = (TextView) view.findViewById(R.id.txt_action_name);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            txt_des = (TextView) view.findViewById(R.id.txt_disc);
            txt_type_des = (TextView) view.findViewById(R.id.txt_type_des);
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView =LayoutInflater.from(mContext).inflate(R.layout.layout_myactions_sync_item, parent, false);
        MySyncViewHolder mySyncViewHolder = new MySyncViewHolder(mView);
        return mySyncViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        FillLocalContent.fillMyActionItems(mContext,holder,mDatas.get(position),type,mHelper,position);

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


}
