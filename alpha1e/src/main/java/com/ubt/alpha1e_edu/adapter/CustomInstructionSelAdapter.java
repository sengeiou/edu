package com.ubt.alpha1e_edu.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.model.InstructionInfo;
import com.ubt.alpha1e_edu.ui.fragment.CustomInstructionSelFragment;

import java.util.List;

/**
 * Created by Administrator on 2016/3/18.
 */
public class CustomInstructionSelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "CustomInstructionSelAdapter";

    private List<InstructionInfo> mDatas;
    private Activity mActivity;
    private Handler mHandler;

    public CustomInstructionSelAdapter(List<InstructionInfo> datas, Activity activity, Handler handler) {

        this.mDatas = datas;
        this.mActivity = activity;
        this.mHandler = handler;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public ImageView ivInstructionSel;
        public TextView tvInstructionActionContent;
        public RelativeLayout rlInstructionSelInfo;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            rlInstructionSelInfo = (RelativeLayout) view.findViewById(R.id.rl_instruction_sel_info);
            ivInstructionSel = (ImageView) view.findViewById(R.id.iv_instruction_sel);
            tvInstructionActionContent = (TextView) view.findViewById(R.id.tv_instruction_action_content);

        }

        @Override
        public String toString() {
            return super.toString();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom_instruction_sel_item, parent, false);

        RecyclerView.ViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myHolder = (MyViewHolder)holder;

        InstructionInfo instructionInfo = mDatas.get(position);
        if(instructionInfo.selected){
            myHolder.ivInstructionSel.setImageResource(R.drawable.icon_action_selected);
        }else {
            myHolder.ivInstructionSel.setImageResource(R.drawable.myactions_normal);
        }

        myHolder.tvInstructionActionContent.setText(mDatas.get(position).name);
        myHolder.rlInstructionSelInfo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = CustomInstructionSelFragment.INSTRUCTION_INFO_CLICK_POSITION;
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
