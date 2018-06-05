package com.ubt.alpha1e_edu.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.model.ActionInfo;
import com.ubt.alpha1e_edu.data.model.NewActionInfo;
import com.ubt.alpha1e_edu.ui.RemoteRoleActionsAddActivity;
import com.ubt.alpha1e_edu.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e_edu.ui.helper.MyActionsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/13.
 */
public class MyRoleAcitonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MyRoleAcitonAdapter";
    private Context mContext;
    private View mView;
    private List<Map<String, Object>> mDatas = new ArrayList<>();
    private Handler mHandler = null;
    private int type = 0;
    public static final int DOWNLOAD_ACTIONS = 1;
    public static final int CREATE_ACTIONS = 2;

    public MyRoleAcitonAdapter(Context mContext,List<Map<String, Object>> data,int type,Handler handler) {
        this.mContext = mContext;
        this.mDatas = data;
        this.type = type;
        this.mHandler = handler;
    }

    public void setData(List<Map<String, Object>>  data) {
        this.mDatas = data;
    }

    public class MyRoleActionHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl_info,rl_state;
        public ImageView img_action_logo, img_type_logo,img_select,img_state;
        public TextView txt_action_name, txt_time, txt_des, txt_type_des;
        public LinearLayout layout_img_select;
        public MyRoleActionHolder(View view) {
            super(view);
            img_action_logo = (ImageView) view.findViewById(R.id.action_logo);
            img_type_logo = (ImageView) view.findViewById(R.id.img_type_logo);
            img_select = (ImageView) view.findViewById(R.id.img_select);
            img_state = (ImageView) view.findViewById(R.id.img_state);
            rl_info  = (RelativeLayout) view.findViewById(R.id.rl_logo_info);
            txt_action_name = (TextView) view.findViewById(R.id.txt_action_name);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            txt_des = (TextView) view.findViewById(R.id.txt_disc);
            txt_type_des = (TextView) view.findViewById(R.id.txt_type_des);
            layout_img_select = (LinearLayout)view.findViewById(R.id.layout_img_select);
            rl_state = (RelativeLayout) view.findViewById(R.id.lay_state);
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_my_role_action_item, parent, false);
        MyRoleActionHolder myRoleActionHolder = new MyRoleActionHolder(mView);
        return myRoleActionHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mHolder, final int position) {
        try {
            MyRoleActionHolder holder = (MyRoleActionHolder) mHolder;
            final Map<String, Object> actionList = mDatas.get(position);

            if (type == DOWNLOAD_ACTIONS) {
                Glide.with(mContext)
                        .load(((ActionInfo) actionList
                                .get(MyActionsHelper.map_val_action)).actionImagePath)
                        .fitCenter().placeholder(R.drawable.sec_action_logo)
                        .into(holder.img_action_logo);
            } else if (type == CREATE_ACTIONS) {
                Glide.with(mContext)
                        .load(((NewActionInfo) actionList
                                .get(MyActionsHelper.map_val_action)).actionHeadUrl)
                        .fitCenter().placeholder(R.drawable.sec_action_logo)
                        .into(holder.img_action_logo);

            } else {
                Glide.with(mContext)
                        .load(R.drawable.sec_action_logo)
                        .fitCenter()
                        .into(holder.img_action_logo);
            }

            String action_name = actionList.get(ActionsLibHelper.map_val_action_name) + "";
            if (action_name.startsWith("@") || action_name.startsWith("#") || action_name.startsWith("%")) {
                action_name = action_name.substring(1);
            }

            holder.txt_action_name.setText(action_name);
            holder.txt_des.setText(actionList.get(ActionsLibHelper.map_val_action_disc) + "");
            holder.img_type_logo.setImageResource((int) actionList.get(ActionsLibHelper.map_val_action_type_logo_res));
            holder.txt_type_des.setText(actionList.get(MyActionsHelper.map_val_action_type_name) + "");
            holder.txt_time.setText(actionList.get(ActionsLibHelper.map_val_action_time) + "");

            if((Boolean) actionList.get(MyActionsHelper.map_val_action_selected))
            {
                holder.img_select.setImageResource(R.drawable.mynew_actions_selected);
            }else {
                holder.img_select.setImageResource(R.drawable.myactions_normal);
            }
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isSelect = (Boolean) actionList.get (MyActionsHelper.map_val_action_selected);
                    if(RemoteRoleActionsAddActivity.selectItemNum >= 20 && !isSelect){
                        mHandler.sendEmptyMessage(RemoteRoleActionsAddActivity.SHOW_SELECT_LIMITE);
                    }else{
                        Message msg = new Message();
                        msg.what = RemoteRoleActionsAddActivity.UPDATE_SELECT_ITEM;
                        msg.obj = !isSelect;
                        msg.arg1 = type;
                        msg.arg2 = position;
                        mHandler.sendMessage(msg);
                    }

                }
            };
            holder.layout_img_select.setOnClickListener(listener);
            holder.rl_info.setOnClickListener(listener);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


}
