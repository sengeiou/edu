package com.ubt.alpha1e.edu.blockly;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.ui.helper.ScanHelper;

import java.util.List;
import java.util.Map;

/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */



public  class RobotRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private Context context;
    private List<Map<String, Object>> datas;
    private HistoryViewHolder viewHolder;

    public RobotRecyclerViewAdapter(Context context,  List<Map<String, Object>> datas) {
        this.context = context;
        this.datas = datas;
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , Map<String, Object> data);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_robot, parent, false);

        viewHolder = new HistoryViewHolder(view);
        view.setOnClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        viewHolder.deviceName.setText(datas.get(position).get(ScanHelper.map_val_robot_name).toString());
        viewHolder.deviceMac.setText(datas.get(position).get(ScanHelper.map_val_robot_mac).toString());
        viewHolder.itemView.setTag(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(Map<String, Object>)v.getTag());
        }

    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder{

        ImageView deviceIcon;
        TextView deviceName;
        TextView deviceMac;


        public HistoryViewHolder(View itemView) {
            super(itemView);

            deviceIcon = (ImageView) itemView.findViewById(R.id.iv_device_icon);
            deviceName = (TextView) itemView.findViewById(R.id.tv_device_name);
            deviceMac = (TextView) itemView.findViewById(R.id.tv_device_mac);
        }
    }

}
