package com.ubt.alpha1e.action.actioncreate;

import android.content.Context;
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

public class PrepareActionUtil implements BaseQuickAdapter.OnItemClickListener, OnClickListener {
    private Context mContext;
    private int mType = -1;
    List<PrepareDataModel> list = new ArrayList<>();
    ActionAdapter actionAdapter;
    private TextView tvCancle;
    private TextView tvConfirm;
    private OnDialogListener mDialogListener;
    private PrepareDataModel selectDataModel;

    public PrepareActionUtil(Context context) {
        this.mContext = context;
    }

    /**
     * 显示对话框
     *
     * @param type
     */
    public void showActionDialog(int type, OnDialogListener mDialogListener) {
        this.mDialogListener = mDialogListener;
        this.mType = type;
        String title="";
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
        actionAdapter = new ActionAdapter(R.layout.item_actions, list);
        actionAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(actionAdapter);
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
        selectDataModel = null;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        selectDataModel = (PrepareDataModel) adapter.getData().get(position);
        for (int i = 0; i < list.size(); i++) {
            if (selectDataModel.getPrepareName().equals(list.get(i).getPrepareName())) {
                list.get(i).setSelected(true);
            } else {
                list.get(i).setSelected(false);
            }
        }
        tvConfirm.setTextColor(mContext.getResources().getColor(R.color.text_confirm_color));
        tvConfirm.setEnabled(true);
        actionAdapter.notifyDataSetChanged();
        if (mType == 1 || mType == 2) {
            if (null != mDialogListener) {
                mDialogListener.playAction(selectDataModel);
            }
        }

    }

    @Override
    public void onClick(DialogPlus dialog, View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dialog.dismiss();
                break;
            case R.id.tv_confirm:
                if(selectDataModel == null){
                    return;
                }
                if (null != mDialogListener) {
                    mDialogListener.onActionConfirm(selectDataModel);
                }
                dialog.dismiss();
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
        }
    }


    public interface OnDialogListener {

        void onActionConfirm(PrepareDataModel prepareDataModel);

        void playAction(PrepareDataModel prepareDataModel);

        void onMusicConfirm(PrepareMusicModel prepareMusicModel);
    }


}
