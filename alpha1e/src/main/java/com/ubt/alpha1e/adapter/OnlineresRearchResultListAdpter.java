package com.ubt.alpha1e.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.OnlineResRearchList;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.onlinereSrearch.OnlineResRearchActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

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
    Handler mHandler;

    /**
     * 类构造函数
     * @param mContext 上下文
     * @param list 数据列表
     */
    public OnlineresRearchResultListAdpter(Context mContext, List<OnlineResRearchList> list, Handler handler) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        mHandler=handler;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final OnlineresRearchResultListAdpter.OnlineResHolder myHolder  = (OnlineresRearchResultListAdpter.OnlineResHolder) holder;
        final OnlineResRearchList onlineres = mDatas.get(position);


        myHolder.online_res_name.setText(onlineres.getRes_name());

//        myHolder.online_res_name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Message msg = new Message();
////                msg.what = WifiSelectAlertDialog.SELECT_POSITION;
////                msg.obj = onlineres;
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UbtLog.d(TAG, "onClick ......" +mDatas.get(position).res_id);
                List<AlbumContentInfo> mAlbum=new ArrayList<>();
                AlbumContentInfo mItem=new AlbumContentInfo();
                mItem.grade=mDatas.get(position).getGrade();
                mItem.albumName=mDatas.get(position).getRes_name();
                mItem.albumId=mDatas.get(position).getRes_id();
                mAlbum.add(0,mItem);
                Message msg = new Message();
                msg.what = OnlineResRearchActivity.SEARCH_RESULT_ALBUM;
                msg.obj =mAlbum;
                mHandler.sendMessage(msg);
//                Intent i = new Intent(mContext,  OnlineAudioAlbumPlayerFragment.class);
//                mContext.startActivity(i);
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
