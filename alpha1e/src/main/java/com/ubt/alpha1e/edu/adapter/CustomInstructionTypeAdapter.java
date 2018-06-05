package com.ubt.alpha1e.edu.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.ui.fragment.CustomInstructionSelFragment;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/18.
 */
public class CustomInstructionTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "CustomInstructionTypeAdapter";

    private List<Map<String,Object>> mDatas;
    private Activity mActivity;
    private Handler mHandler;

    public CustomInstructionTypeAdapter(List<Map<String,Object>> datas, Activity activity, Handler handler) {

        this.mDatas = datas;
        this.mActivity = activity;
        this.mHandler = handler;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public TextView tvInstructionType;
        public RelativeLayout rlInstructionTypeInfo;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            rlInstructionTypeInfo = (RelativeLayout) view.findViewById(R.id.rl_instruction_type_info);
            tvInstructionType = (TextView) view.findViewById(R.id.tv_instruction_type);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom_instruction_type_item, parent, false);

        RecyclerView.ViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myHolder = (MyViewHolder)holder;

        Map<String,Object> mapData = mDatas.get(position);
        myHolder.tvInstructionType.setText((String)mapData.get(CustomInstructionSelFragment.INSTRUCTION_TYPE_NAME));

        if((Boolean) mapData.get(CustomInstructionSelFragment.INSTRUCTION_TYPE_SELECT)){
            myHolder.tvInstructionType.setTextColor(mActivity.getResources().getColor(R.color.T5));
        }else {
            myHolder.tvInstructionType.setTextColor(mActivity.getResources().getColor(R.color.T1));
        }

        myHolder.rlInstructionTypeInfo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = CustomInstructionSelFragment.INSTRUCTION_TYPE_CLICK_POSITION;
                msg.arg1 = position;
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
