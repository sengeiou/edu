package com.ubt.alpha1e.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.OnlineResRearchList;
import com.ubt.alpha1e.ui.dialog.WifiSelectAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class OnlineresRearchResultListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "onlineresAdpater";

    private Context mContext;
    public List<OnlineResRearchList> mDatas = new ArrayList<>();
    private View mView;

    /**
     * 类构造函数
     * @param mContext 上下文
     * @param list 数据列表
     */
    public OnlineresRearchResultListAdpter(Context mContext, List<OnlineResRearchList> list) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final OnlineresRearchResultListAdpter.OnlineResHolder myHolder  = (OnlineresRearchResultListAdpter.OnlineResHolder) holder;
        final OnlineResRearchList onlineres = mDatas.get(position);


        myHolder.online_res_name.setText(onlineres.getRes_name());

        myHolder.online_res_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = WifiSelectAlertDialog.SELECT_POSITION;
                msg.obj = onlineres;
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.onlineres_rearch_result_item, parent, false);
        OnlineResHolder myWifiHolder = new OnlineResHolder(mView);
        return myWifiHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class OnlineResHolder extends RecyclerView.ViewHolder
    {

        public TextView online_res_name;
        public OnlineResHolder(View view)
        {
            super(view);
            online_res_name = (TextView) view.findViewById(R.id.online_res_name);
        }
    }
}
