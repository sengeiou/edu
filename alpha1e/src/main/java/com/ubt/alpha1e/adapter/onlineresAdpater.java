package com.ubt.alpha1e.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.OnlineresList;
import com.ubt.alpha1e.onlineaudioplayer.Fragment.OnlineAudioResourcesFragment;
import com.ubt.alpha1e.ui.dialog.WifiSelectAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class onlineresAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "onlineresAdpater";

    private Context mContext;
    public List<OnlineresList> mDatas = new ArrayList<>();
    private View mView;
    private Handler mHandler;

    /**
     * 类构造函数
     * @param mContext 上下文
     * @param list 数据列表
     */
    public onlineresAdpater(Context mContext, List<OnlineresList> list, Handler handler) {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final onlineresAdpater.OnlineResHolder myHolder  = (onlineresAdpater.OnlineResHolder) holder;
        final OnlineresList onlineres = mDatas.get(position);

        myHolder.res_name.setText(onlineres.getRes_name());
        String word = onlineres.getRes_name().substring(0,1);
        myHolder.first_word.setText(word);
        if(word.equals("语") || word.equals("精") || word.equals("国")){
            myHolder.online_res_swipe.setBackgroundResource(R.drawable.bg_resources_b);
        }else if( word.equals("趣") || word.equals("经") ){
            myHolder.online_res_swipe.setBackgroundResource(R.drawable.bg_resources_r);
        }else if(word.equals("英") || word.equals("8")){
            myHolder.online_res_swipe.setBackgroundResource(R.drawable.bg_resources_p);
        }else if(word.equals("中") || word.equals("音") || word.equals("动")){
            myHolder.online_res_swipe.setBackgroundResource(R.drawable.bg_resources_y);
        }
        if(myHolder.res_name.length()==5){
            myHolder.res_name.setTextSize(15);
        }else if(myHolder.res_name.length()==6){
            myHolder.res_name.setTextSize(14);
        }
        myHolder.res_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = OnlineAudioResourcesFragment.LAUNCH_ALBUM_ITEM;
                msg.obj = onlineres.getRes_id();
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_onlineres_item, parent, false);
        OnlineResHolder myWifiHolder = new OnlineResHolder(mView);
        return myWifiHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class OnlineResHolder extends RecyclerView.ViewHolder
    {

        public RelativeLayout online_res_swipe;
        public TextView first_word;
        public TextView res_name;
        public OnlineResHolder(View view)
        {
            super(view);
            online_res_swipe = (RelativeLayout) view.findViewById(R.id.online_res_swipe);
            first_word = (TextView) view.findViewById(R.id.first_word);
            res_name = (TextView) view.findViewById(R.id.res_name);


        }
    }
}
