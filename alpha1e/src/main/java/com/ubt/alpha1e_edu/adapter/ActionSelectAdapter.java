package com.ubt.alpha1e_edu.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e_edu.R;

import com.ubt.alpha1e_edu.ui.fragment.CustomInstructionPlayActionFragment;
import com.ubt.alpha1e_edu.ui.helper.MyActionsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名 ActionSelectAdapter
 * @author lihai
 * @description 指令配置动作列表适配器类。
 * @date 2017.04.11
 *
 */
public class ActionSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
    private ActionsHolder mHolder;
    private Handler mHandler;

    public ActionSelectAdapter(Context context, List<Map<String, Object>> list, Handler handler) {
        mContext = context;
        mList = list;
        mHandler = handler;
    }

    public void setData(List<Map<String, Object>>  data) {
        mList = data;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        mHolder = (ActionsHolder) holder;


        /*mHolder.txt_action_name.setText(mList.get(position).get(MyActionsHelper.map_val_action_name) + "");
        mHolder.txt_des.setText(mList.get(position).get(MyActionsHelper.map_val_action_disc) + "");
        mHolder.img_action_logo.setImageResource(R.drawable.sec_action_logo);
        mHolder.img_type_logo.setImageResource((int) mList.get(position).get(MyActionsHelper.map_val_action_type_logo_res));
        mHolder.txt_type_des.setText(mList.get(position).get(MyActionsHelper.map_val_action_type_name) + "");
        mHolder.txt_time.setText(mList.get(position).get(MyActionsHelper.map_val_action_time) + "");
        Glide.with(mContext)
                .load(((NewActionInfo) (mList.get(position))
                        .get(MyActionsHelper.map_val_action)).actionHeadUrl)
                .fitCenter().placeholder(R.drawable.sec_action_logo)
                .into(mHolder.img_action_logo);
        */

        String action_name = mList.get(position).get(MyActionsHelper.map_val_action_name) + "";
        if(action_name.startsWith("@") || action_name.startsWith("#") || action_name.startsWith("%")){
            action_name = action_name.substring(1);
        }

        mHolder.tv_action_name.setText(action_name);

        if((Boolean) mList.get(position).get(MyActionsHelper.map_val_action_selected))
        {
            mHolder.iv_select.setImageResource(R.drawable.icon_action_selected);
        }else {
            mHolder.iv_select.setImageResource(R.drawable.myactions_normal);
        }

        Glide.with(mContext)
                .load("")
                .fitCenter().placeholder(R.drawable.sec_action_logo)
                .into(mHolder.iv_action_logo);

        mHolder.txt_des.setVisibility(View.GONE);
        mHolder.rlCenter.setVisibility(View.GONE);

        /**
         * 定义点击监听器
         */
        View.OnClickListener mViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                msg.what = CustomInstructionPlayActionFragment.CLICK_POSITION;
                msg.arg1 = position;
                mHandler.sendMessage(msg);
            }
        };

        mHolder.iv_select.setOnClickListener(mViewClickListener);
        mHolder.rl_info.setOnClickListener(mViewClickListener);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_myactions_sync_item, parent, false);
        mHolder = new ActionsHolder(view);
        return mHolder;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ActionsHolder extends  RecyclerView.ViewHolder {

        public RelativeLayout rl_info,rlCenter;
        public ImageView iv_action_logo, img_type_logo,iv_select;
        public TextView tv_action_name, txt_time, txt_des, txt_type_des;
        public ActionsHolder(View view){
            super(view);
            iv_action_logo = (ImageView) view.findViewById(R.id.action_logo);
            img_type_logo = (ImageView) view.findViewById(R.id.img_type_logo);
            iv_select = (ImageView) view.findViewById(R.id.img_select);
            rl_info  = (RelativeLayout) view.findViewById(R.id.rl_logo_info);
            rlCenter  = (RelativeLayout) view.findViewById(R.id.lay_center);
            tv_action_name = (TextView) view.findViewById(R.id.txt_action_name);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            txt_des = (TextView) view.findViewById(R.id.txt_disc);
            txt_type_des = (TextView) view.findViewById(R.id.txt_type_des);
        }

    }
}
