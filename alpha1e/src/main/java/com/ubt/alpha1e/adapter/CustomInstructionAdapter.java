package com.ubt.alpha1e.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.model.InstructionInfo;
import com.ubt.alpha1e.ui.CustomInstructionPlayActionActivity;
import com.ubt.alpha1e.ui.CustomInstructionPlayTextActivity;
import com.ubt.alpha1e.ui.custom.ExpandableTextView;
import com.ubt.alpha1e.ui.custom.InstructionAlertView;
import com.ubt.alpha1e.ui.custom.SlideView;
import com.ubt.alpha1e.ui.dialog.InstructionAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名
 * @author lihai
 * @description 指令列表适配器类。
 * @date 2017.04.11
 *
 */
public class CustomInstructionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final static String TAG = "CustomInstructionAdapter";

    //定义类成员变量
    private SparseBooleanArray mCollapsedStatus;
    private List<InstructionInfo> mDatas;
    private Activity mActivity;
    private List<SlideView> mOpenList=new ArrayList<SlideView>();
    private Context mContext;
    private Handler mHandler = null;

    /**
     * 构造函数
     * @param datas adapter 数据
     * @param activity 调用activity
     * @param handler handler处理类
     */
    public CustomInstructionAdapter(List<InstructionInfo> datas, Activity activity, Handler handler) {

        this.mDatas = datas;
        this.mActivity = activity;
        this.mHandler = handler;
        this.mCollapsedStatus = new SparseBooleanArray();
        this.mContext = mActivity.getApplicationContext();

    }

    /**
     * 定义Adapter UI holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public ImageView ivInstructionAction, ivInstructionReply, ivInstructionEdit;
        public SlideView swipeLayout; //滑动视图
        public RelativeLayout rlInstructionInfo;
        public LinearLayout llDelete;
        public Button btnDelete;
        public ExpandableTextView expandableTextView;
        public TextView tvInstructionActionContent;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            rlInstructionInfo = (RelativeLayout) view.findViewById(R.id.rl_instruction_info);
            btnDelete = (Button) view.findViewById(R.id.btn_del);
            ivInstructionAction = (ImageView) view.findViewById(R.id.iv_instruction_action);
            ivInstructionReply = (ImageView) view.findViewById(R.id.iv_instruction_reply);
            ivInstructionEdit = (ImageView) view.findViewById(R.id.iv_instruction_edit);
            expandableTextView = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
            tvInstructionActionContent = (TextView) view.findViewById(R.id.tv_instruction_action_content);

            swipeLayout =(SlideView)view.findViewById(R.id.swipe);
            llDelete  = (LinearLayout) view.findViewById(R.id.ll_del);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom_instruction_item, parent, false);

        RecyclerView.ViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myHolder = (MyViewHolder)holder;

        myHolder.tvInstructionActionContent.setText(mDatas.get(position).name);
        myHolder.expandableTextView.setText(mDatas.get(position).reply, mCollapsedStatus, position);

        //判断是否已经配置回复内容
        if(TextUtils.isEmpty(mDatas.get(position).reply)){
            myHolder.ivInstructionReply.setVisibility(View.GONE);
            myHolder.expandableTextView.setVisibility(View.GONE);
        }else {
            myHolder.ivInstructionReply.setVisibility(View.VISIBLE);
            myHolder.expandableTextView.setVisibility(View.VISIBLE);
        }

        //扩展控件监听器
        myHolder.expandableTextView.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                //UbtLog.d(TAG,"isExpanded = " + isExpanded + " position = " + position );
                if(isExpanded){
                    //展开，不可以删除
                    myHolder.swipeLayout.setSwipeEnable(false);
                }else {
                    myHolder.swipeLayout.setSwipeEnable(true);
                }
            }
        });

        //删除按钮监听器
        myHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myHolder.swipeLayout.getStatus()== SlideView.Status.OPEN)
                {
                    myHolder.swipeLayout.close(true);
                }
            }
        });

        //配置按钮监听器
        myHolder.ivInstructionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doEditInstruction(position, mDatas.get(position));
                if(myHolder.swipeLayout.getStatus()== SlideView.Status.OPEN)
                {
                    myHolder.swipeLayout.close(true);
                }
            }
        });

        //ITEM 监听器
        myHolder.rlInstructionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myHolder.swipeLayout.getStatus()== SlideView.Status.OPEN)
                {
                    myHolder.swipeLayout.close(true);
                }
            }
        });

        myHolder.swipeLayout.setSwipeEnable(true);
        myHolder.swipeLayout.setSwipeChangeListener(new SlideView.OnSwipeChangeListener() {
            @Override
            public void onDraging(SlideView mSwipeLayout) {

            }

            @Override
            public void onOpen(SlideView mSwipeLayout) {
                for(SlideView layout : mOpenList){
                    layout.close();
                }
                mOpenList.clear();
                mOpenList.add(mSwipeLayout);
            }

            @Override
            public void onClose(SlideView mSwipeLayout) {
                mOpenList.remove(mSwipeLayout);
            }

            @Override
            public void onStartOpen(SlideView mSwipeLayout) {
                for(SlideView layout : mOpenList){
                    layout.close();
                }
                mOpenList.clear();
            }

            @Override
            public void onStartClose(SlideView mSwipeLayout) {

            }
        });
    }

    private void doEditInstruction(final int position,final InstructionInfo instructionInfo){

        if(mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            InstructionAlertView.getInstace(mActivity)
                    .setScreenOrientation(mActivity.getRequestedOrientation())
                    .setPlayTextListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CustomInstructionPlayTextActivity.launchActivity(mActivity,
                                    mActivity.getRequestedOrientation(),
                                    position,
                                    instructionInfo.reply,
                                    Constant.INSTRUCTION_PLAY_TEXT_REQUEST_CODE);
                        }
                    })
                    .setPlayActionListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CustomInstructionPlayActionActivity.launchActivity(mActivity,
                                    mActivity.getRequestedOrientation(),
                                    position,
                                    instructionInfo,
                                    Constant.INSTRUCTION_PLAY_ACTION_REQUEST_CODE);
                        }
                    });
        }else {
            new InstructionAlertDialog(mActivity)
                    .builder()
                    .setCancelable(true)
                    .setPlayTextListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CustomInstructionPlayTextActivity.launchActivity(mActivity,
                                    mActivity.getRequestedOrientation(),
                                    position,
                                    instructionInfo.reply,
                                    Constant.INSTRUCTION_PLAY_TEXT_REQUEST_CODE);
                        }
                    })
                    .setPlayActionListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CustomInstructionPlayActionActivity.launchActivity(mActivity,
                                    mActivity.getRequestedOrientation(),
                                    position,
                                    instructionInfo,
                                    Constant.INSTRUCTION_PLAY_ACTION_REQUEST_CODE);
                        }
                    })
                    .show();
        }

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
