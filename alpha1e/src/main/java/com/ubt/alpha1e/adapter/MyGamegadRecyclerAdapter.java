package com.ubt.alpha1e.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.model.RemoteRoleInfo;
import com.ubt.alpha1e.ui.RemoteAddActivity;
import com.ubt.alpha1e.ui.RemoteSelActivity;
import com.ubt.alpha1e.ui.custom.SlideView;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.RemoteHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class MyGamegadRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = MyGamegadRecyclerAdapter.class.getSimpleName();

    private static final int NORMAL_VIEW = 1;

    private Context mContext;
    public List<RemoteRoleInfo> mDatas = new ArrayList<RemoteRoleInfo>();
    private View mView;
    private BaseHelper mHelper;
    private Handler mHandler = null;

    public MyGamegadRecyclerAdapter(Context mContext, List<RemoteRoleInfo> list, BaseHelper helper,Handler handler) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        this.mHelper = helper;
        this.mHandler = handler;
    }


    public void setData(List<RemoteRoleInfo>  data) {
        this.mDatas = data;
    }
    @Override
    public int getItemViewType(int position) {

        return NORMAL_VIEW;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof  MyGamepadHolder)
        {
            MyGamepadHolder myHolder = (MyGamepadHolder)holder;
            fillGamepadItems(mContext,holder,mDatas.get(position),(RemoteHelper) mHelper,position);

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType)
        {
            case NORMAL_VIEW:
                //mView = LayoutInflater.from(mContext).inflate(R.layout.layout_my_mygamepad_item, parent, false);
                mView = LayoutInflater.from(mContext).inflate(R.layout.layout_main_remote_item, parent, false);
                MyGamepadHolder myGamepadHolder = new MyGamepadHolder(mView);
                return myGamepadHolder;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class MyGamepadHolder extends RecyclerView.ViewHolder
    {
        public ImageView ivRemote;
        public RelativeLayout rlRemoteItem;
        public TextView tvRemoteName;

        public MyGamepadHolder(View view)
        {
            super(view);
            rlRemoteItem  = (RelativeLayout) view.findViewById(R.id.rl_remote_item);
            ivRemote = (ImageView) view.findViewById(R.id.iv_remote);
            tvRemoteName = (TextView) view.findViewById(R.id.tv_remote_name);

        }
    }

    public void fillGamepadItems(final Context mContext, RecyclerView.ViewHolder myHolder, final RemoteRoleInfo remoteRoleInfo, final RemoteHelper mHelper, final int position)
    {

        final MyGamegadRecyclerAdapter.MyGamepadHolder holder  = (MyGamegadRecyclerAdapter.MyGamepadHolder) myHolder;

        holder.tvRemoteName.setText(remoteRoleInfo.roleName);
        int remoteLogoId = R.drawable.icon_dancer;
        if(position == 0){
            remoteLogoId = R.drawable.icon_football;
        }else if(position == 1){
            remoteLogoId = R.drawable.icon_fighter;
        }
        holder.ivRemote.setImageResource(remoteLogoId);

        holder.rlRemoteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = RemoteSelActivity.CLICK_REMOTE_ROLE;
                msg.arg1 = position;
                mHandler.sendMessage(msg);
            }
        });

    }
}
