package com.ubt.alpha1e.maincourse.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.model.ActionConstant;
import com.ubt.alpha1e.action.model.PrepareDataModel;
import com.ubt.alpha1e.action.model.PrepareMusicModel;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.popup.EasyPopup;
import com.ubt.alpha1e.base.popup.HorizontalGravity;
import com.ubt.alpha1e.base.popup.VerticalGravity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/16 14:07
 * @modifier：ubt
 * @modify_date：2017/11/16 14:07
 * [A brief description]
 * version
 */

public class ActionCourseTwoUtil implements BaseQuickAdapter.OnItemClickListener, OnClickListener {
    private Context mContext;
    private int mType = -1;
    List<PrepareDataModel> list = new ArrayList<>();
    ActionAdapter actionAdapter;
    private TextView tvCancle;
    private TextView tvConfirm;
    private OnCourseDialogListener mDialogListener;
    private PrepareDataModel selectDataModel;
    private boolean isShow = true;

    public ActionCourseTwoUtil(Context context) {
        this.mContext = context;
    }

    /**
     * 显示对话框
     *
     * @param type
     */
    public void showActionDialog(int type, OnCourseDialogListener mDialogListener) {
        this.mDialogListener = mDialogListener;
        this.mType = type;
        String title = "";
        if (type == 1) {
            title = ResourceManager.getInstance(mContext).getStringResources("ui_create_basic_action");
            list = ActionConstant.getBasicActionList(mContext);
        } else if (type == 2) {
            title = ResourceManager.getInstance(mContext).getStringResources("ui_create_advance_action");
            list = ActionConstant.getHighActionList(mContext);
        }
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_aciton_select, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        TextView tvTitle = contentView.findViewById(R.id.title_actions);
        tvTitle.setText(title);
        tvCancle = contentView.findViewById(R.id.tv_cancel);
        tvConfirm = contentView.findViewById(R.id.tv_confirm);
        RecyclerView recyclerView = contentView.findViewById(R.id.rv_actions);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 5);
        recyclerView.setLayoutManager(layoutManager);
        actionAdapter = new ActionAdapter(R.layout.item_actions1, list);
        actionAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(actionAdapter);
        actionAdapter.bindToRecyclerView(recyclerView);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.8); //设置宽度
        DialogPlus.newDialog(mContext)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentWidth(width)
                .setOnClickListener(this)
                .setCancelable(true)
                .create().show();
        // showPop(recyclerView);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                isShow = false;
//                actionAdapter.notifyDataSetChanged();
//            }
//        }, 2000);
    }

    private void showPop(View archView) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        textView.setText("点击基础动作模版  点击预览出招动作 点击添加");
        textView.setBackgroundResource(R.drawable.bubble_left);
        final EasyPopup mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .createPopup()
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //   ivActionLib.setEnabled(true);
                    }
                });

        mCirclePop.showAtAnchorView(archView, VerticalGravity.ALIGN_TOP, HorizontalGravity.ALIGN_RIGHT, 0, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCirclePop.dismiss();
            }
        }, 2000);
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        selectDataModel = (PrepareDataModel) adapter.getData().get(position);
        if (position == 0) {
            list.get(0).setSelected(true);
            tvConfirm.setTextColor(mContext.getResources().getColor(R.color.text_confirm_color));
            tvConfirm.setEnabled(true);
            actionAdapter.notifyDataSetChanged();
            if (mType == 1 || mType == 2) {
                if (null != mDialogListener) {
                    mDialogListener.playCourseAction(selectDataModel);
                }
            }
        }


    }

    @Override
    public void onClick(DialogPlus dialog, View view) {
        dialog.dismiss();
        switch (view.getId()) {
            case R.id.tv_cancel:

                break;
            case R.id.tv_confirm:
                if (null != mDialogListener) {
                    mDialogListener.onCourseConfirm(selectDataModel);
                }
                break;
            default:
                break;
        }
    }


    private class ActionAdapter extends BaseQuickAdapter<PrepareDataModel, BaseViewHolder> {


        public ActionAdapter(@LayoutRes int layoutResId, @Nullable List<PrepareDataModel> data) {
            super(layoutResId, data);
        }


        @Override
        protected void convert(BaseViewHolder helper, PrepareDataModel item) {
            helper.setText(R.id.tv_action_name, item.getPrepareName());
            ((ImageView) helper.getView(R.id.iv_action_icon)).setImageResource(item.getDrawableId());
            ImageView ivSelect = helper.getView(R.id.iv_action_select);
            ivSelect.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
            if (item.getPrepareName().equals(list.get(0).getPrepareName()) && isShow) {
                helper.getView(R.id.iv_hand).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.iv_hand).setVisibility(View.GONE);
            }

        }
    }


    public interface OnCourseDialogListener {

        void onCourseConfirm(PrepareDataModel prepareDataModel);

        void playCourseAction(PrepareDataModel prepareDataModel);

        void onMusicConfirm(PrepareMusicModel prepareMusicModel);
    }


}
