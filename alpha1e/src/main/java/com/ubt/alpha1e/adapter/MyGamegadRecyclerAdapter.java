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

    private static final String TAG = "MyGamegadRecyclerAdapter";

    private static final int HEAD_VIEW = 0;
    private static final int NORMAL_VIEW = 1;
    private static final int NORMAL_VIEW_2 = 2;
    private static final int ADDMORE_VIEW = -1;

    private Context mContext;
    public List<RemoteRoleInfo> mDatas = new ArrayList<RemoteRoleInfo>();
    private View mView;
    private BaseHelper mHelper;
    private List<SlideView> openList=new ArrayList<SlideView>();
    private Handler mHandler = null;

    private int header_code = 0;

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

        if (position == 0 ) {
            UbtLog.d(TAG, "--wmma--head_view");
            return HEAD_VIEW;
        }else if(position == 1 || position == 2 || position == 3){
            return NORMAL_VIEW_2;
        }

        if (mDatas.get(position).roleName == null ) {
            UbtLog.d(TAG, "--wmma--ADDMORE_VIEW");
            return ADDMORE_VIEW;//创建更多
        } else {
            UbtLog.d(TAG, "--wmma--NORMAL_VIEW");
            return NORMAL_VIEW;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof  MyGamepadHolder)
        {
            MyGamepadHolder myHolder = (MyGamepadHolder)holder;
            fillGamepadItems(mContext,holder,mDatas.get(position),(RemoteHelper) mHelper,position);
            myHolder.swipeLayout.setSwipeEnable(true);
            if(position  < 4){
                myHolder.operateLayout.setVisibility(View.GONE);
            }else{
                myHolder.operateLayout.setVisibility(View.VISIBLE);
                myHolder.swipeLayout.setSwipeChangeListener(new SlideView.OnSwipeChangeListener() {
                    @Override
                    public void onDraging(SlideView mSwipeLayout) {

                    }

                    @Override
                    public void onOpen(SlideView mSwipeLayout) {
                        openList.add(mSwipeLayout);
                    }

                    @Override
                    public void onClose(SlideView mSwipeLayout) {
                        openList.remove(mSwipeLayout);
                    }

                    @Override
                    public void onStartOpen(SlideView mSwipeLayout) {
                        for(SlideView layout:openList){
                            layout.close();
                        }
                        openList.clear();

                    }

                    @Override
                    public void onStartClose(SlideView mSwipeLayout) {

                    }
                });
            }

        }else if(holder instanceof  AddMoreViewHolder)
        {
            AddMoreViewHolder addMoreViewHolder = (AddMoreViewHolder)holder;
            addMoreViewHolder.ly_create_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RemoteAddActivity.launchActivity(mContext,null);
                }
            });

        }else if(holder instanceof  HeadViewHolder ){
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            if(((RemoteHelper) mHelper).isShowRemoteHeadPormgt()){
                headViewHolder.headerRoot.setVisibility(View.VISIBLE);
            }else {
                headViewHolder.headerRoot.setVisibility(View.GONE);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)headViewHolder.headerRoot.getLayoutParams();
                layoutParams.height = 0;
                headViewHolder.headerRoot.setLayoutParams(layoutParams);
            }

            headViewHolder.iv_close_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UbtLog.d(TAG, "--wmma--header close clicked!");

                    ((RemoteHelper) mHelper).colseRemoteHeadPormgt();
                }

            });
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType)
        {
            case NORMAL_VIEW:
                mView = LayoutInflater.from(mContext).inflate(R.layout.layout_my_mygamepad_item, parent, false);
                MyGamepadHolder myGamepadHolder = new MyGamepadHolder(mView);
                return myGamepadHolder;
            case NORMAL_VIEW_2:
                //解决红米手机足球员格斗家可以滑动bug，故新new一个holder
                mView = LayoutInflater.from(mContext).inflate(R.layout.layout_my_mygamepad_item, parent, false);
                MyGamepadHolder myGamepadHolder_2 = new MyGamepadHolder(mView);
                return myGamepadHolder_2;
            case ADDMORE_VIEW:
                mView =LayoutInflater.from(mContext).inflate(R.layout.layout_mygamepad_create_more, parent, false);
                AddMoreViewHolder mAddMoreViewHolder = new AddMoreViewHolder(mView);
                return mAddMoreViewHolder;
            case HEAD_VIEW:
                mView =LayoutInflater.from(mContext).inflate(R.layout.layout_mygamepad_head, parent, false);
                HeadViewHolder mHeadViewHolder = new HeadViewHolder(mView);
                return mHeadViewHolder;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class AddMoreViewHolder extends  RecyclerView.ViewHolder
    {

        private LinearLayout ly_create_more;
        public  AddMoreViewHolder(View v)
        {
            super(v);
            ly_create_more = (LinearLayout)v.findViewById(R.id.ly_create_more);

        }
    }

    public static class HeadViewHolder extends  RecyclerView.ViewHolder
    {

        private RelativeLayout headerRoot;
        private ImageView iv_close_header;
        public  HeadViewHolder(View v)
        {
            super(v);
            headerRoot = (RelativeLayout)v.findViewById(R.id.rl_header_root);
            iv_close_header = (ImageView)v.findViewById(R.id.iv_close_header);

        }
    }

    public static class MyGamepadHolder extends RecyclerView.ViewHolder
    {
        public ImageView img_gamepad;
        public RelativeLayout rl_info;
        public SlideView swipeLayout;//滑动视图
        public TextView ui_remote_role_name, ui_remote_role_introduction;
        public Button btn_delete,btn_edit;
        public LinearLayout operateLayout;
        public Animation operatingAnim;
        public MyGamepadHolder(View view)
        {
            super(view);
            img_gamepad = (ImageView) view.findViewById(R.id.img_gamepad);
            rl_info  = (RelativeLayout) view.findViewById(R.id.rl_logo_info);
            ui_remote_role_name = (TextView) view.findViewById(R.id.ui_remote_role_name);
            ui_remote_role_introduction = (TextView) view.findViewById(R.id.ui_remote_role_introduction);
            swipeLayout =(SlideView)view.findViewById(R.id.gamepad_swipe);
            btn_delete = (Button)view.findViewById(R.id.btn_del);
            btn_edit = (Button)view.findViewById(R.id.btn_edit);
            operateLayout  = (LinearLayout) view.findViewById(R.id.lay_operate);
        }
    }

    public void fillGamepadItems(final Context mContext, RecyclerView.ViewHolder myHolder, final RemoteRoleInfo remoteRoleInfo, final RemoteHelper mHelper, final int position)
    {

        final MyGamegadRecyclerAdapter.MyGamepadHolder holder  = (MyGamegadRecyclerAdapter.MyGamepadHolder) myHolder;

        holder.ui_remote_role_name.setText(remoteRoleInfo.roleName);
        holder.ui_remote_role_introduction.setText(remoteRoleInfo.roleIntroduction);
        holder.img_gamepad.setImageResource(mHelper.getResId(remoteRoleInfo.roleIcon));

        holder.rl_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.swipeLayout.getStatus()== SlideView.Status.OPEN)
                {
                    holder.swipeLayout.close(true);
                }else
                {
                    Message msg = new Message();
                    msg.what = RemoteSelActivity.CLICK_REMOTE_ROLE;
                    msg.arg1 = position;
                    mHandler.sendMessage(msg);
                }
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.doDelRemoteRole(remoteRoleInfo);
                if(holder.swipeLayout.getStatus()== SlideView.Status.OPEN)
                {
                    holder.swipeLayout.close(true);
                }
            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoteAddActivity.launchActivity(mContext,remoteRoleInfo);
                if(holder.swipeLayout.getStatus()== SlideView.Status.OPEN)
                {
                    holder.swipeLayout.close(true);
                }
            }
        });
    }
}
