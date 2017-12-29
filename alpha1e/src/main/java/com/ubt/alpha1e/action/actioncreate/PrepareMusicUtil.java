package com.ubt.alpha1e.action.actioncreate;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.model.ActionConstant;
import com.ubt.alpha1e.action.model.PrepareMusicModel;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.ui.dialog.DialogDub;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;
import java.io.IOException;
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

public class PrepareMusicUtil implements BaseQuickAdapter.OnItemClickListener, OnClickListener, BaseQuickAdapter.OnItemChildClickListener {
    private Context mContext;
    private int mType = -1;
    List<PrepareMusicModel> list = new ArrayList<>();
    MusicAdapter actionAdapter;
    private TextView tvCancle;
    private TextView tvConfirm;
    private PrepareActionUtil.OnDialogListener mDialogListener;
    private PrepareMusicModel selectDataModel;
    DialogPlus mDialogPlus;

    private boolean isShowDelete;
    private ImageView ivDelete;

    public PrepareMusicUtil(Context context) {
        this.mContext = context;
    }

    /**
     * 显示对话框
     */
    public void showMusicDialog(PrepareActionUtil.OnDialogListener mDialogListener) {
        isShowDelete = false;
        this.mDialogListener = mDialogListener;
        list = ActionConstant.getMusicList(mContext);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_aciton_select, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        TextView tvTitle = contentView.findViewById(R.id.title_actions);
        tvTitle.setText(ResourceManager.getInstance(mContext).getStringResources("ui_create_music"));
        ivDelete = contentView.findViewById(R.id.iv_delete);
        ivDelete.setVisibility(View.VISIBLE);
        tvCancle = contentView.findViewById(R.id.tv_cancel);
        tvConfirm = contentView.findViewById(R.id.tv_confirm);
        RecyclerView recyclerView = contentView.findViewById(R.id.rv_actions);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 5);
        recyclerView.setLayoutManager(layoutManager);
        actionAdapter = new MusicAdapter(R.layout.item_actions, list);
        actionAdapter.setOnItemClickListener(this);
        actionAdapter.setOnItemChildClickListener(this);
        recyclerView.setAdapter(actionAdapter);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.8); //设置宽度
        mDialogPlus = DialogPlus.newDialog(mContext)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentWidth(width)
                .setOnClickListener(this)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                        if (null != player) {
                            player.stop();
                        }
                    }
                })
                .setCancelable(true)
                .create();
        mDialogPlus.show();
        selectDataModel = null;
    }

    /**
     * 对话框点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //第一个位置弹出录音框
        if (position == 0) {
            PermissionUtils.getInstance(mContext).request(new PermissionUtils.PermissionLocationCallback() {
                @Override
                public void onSuccessful() {
                    if (null != mDialogPlus && mDialogPlus.isShowing()) {
                        mDialogPlus.dismiss();
                    }
                    DialogDub dialogDub = new DialogDub(mContext);
                    dialogDub.show();
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onRationSetting() {

                }

                @Override
                public void onCancelRationSetting() {
                }

            }, PermissionUtils.PermissionEnum.MICROPHONE,mContext);


        } else {
            if(isShowDelete){
                return;
            }
            selectDataModel = (PrepareMusicModel) adapter.getData().get(position);
            previewMusic(selectDataModel);
            for (int i = 0; i < list.size(); i++) {
                if (selectDataModel.getMusicName().equals(list.get(i).getMusicName())) {
                    list.get(i).setPlaying(true);
                } else {
                    list.get(i).setPlaying(false);
                }
            }
            tvConfirm.setTextColor(mContext.getResources().getColor(R.color.text_confirm_color));
            tvConfirm.setEnabled(true);
            actionAdapter.notifyDataSetChanged();
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
                    mDialogListener.onMusicConfirm(selectDataModel);
                }
                dialog.dismiss();

                break;
            case R.id.iv_delete:
                if(player != null){
                    player.stop();
                }
                if(isShowDelete){
                    isShowDelete = false;
                    ivDelete.setImageResource(R.drawable.icon_delete);
                }else{
                    isShowDelete = true;
                    ivDelete.setImageResource(R.drawable.icon_save);
                }

                actionAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    /**
     * 删除录音按钮事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        deleteMP3RecordFile(list.get(position).getMusicName());
        UbtLog.d("onItemChildClick", "list:" + list.get(position) );
        if(mDialogListener != null){
            mDialogListener.onMusicDelete(list.get(position));
        }
        list.remove(position);
        actionAdapter.notifyDataSetChanged();

    }


    private class MusicAdapter extends BaseQuickAdapter<PrepareMusicModel, BaseViewHolder> {


        public MusicAdapter(@LayoutRes int layoutResId, @Nullable List<PrepareMusicModel> data) {
            super(layoutResId, data);
        }


        @Override
        protected void convert(BaseViewHolder helper, PrepareMusicModel item) {
            helper.addOnClickListener(R.id.iv_del);
            ImageView ivSelect = helper.getView(R.id.iv_action_icon);
            ImageView gifView = helper.getView(R.id.gif_play);
            ImageView ivDelete = helper.getView(R.id.iv_del);
            TextView textView = helper.getView(R.id.tv_action_name);
            ImageView ivBackground = helper.getView(R.id.iv_action_select);
            if (TextUtils.isEmpty(item.getMusicName())) {
                ivSelect.setVisibility(View.VISIBLE);
                ivSelect.setImageResource(R.drawable.dottedline);
                gifView.setVisibility(View.GONE);
                ivDelete.setVisibility(View.GONE);
                textView.setVisibility(View.INVISIBLE);
            } else {
                ivSelect.setImageResource(R.drawable.bg_dottedline);
                textView.setVisibility(View.VISIBLE);
                textView.setText(item.getMusicName());
                gifView.setVisibility(View.VISIBLE);
                AnimationDrawable animationDrawable = (AnimationDrawable) gifView.getDrawable();
                if (item.isPlaying()) {
                    animationDrawable.start();
                } else {
                    animationDrawable.stop();
                }
                if (isShowDelete && item.getMusicType() == 1) {
                    ivDelete.setVisibility(View.VISIBLE);
                } else {
                    ivDelete.setVisibility(View.GONE);
                }
                if(isShowDelete){
                    if(animationDrawable != null){
                        selectDataModel = null;
                        tvConfirm.setTextColor(mContext.getResources().getColor(R.color.tv_user_edit_color));
                        tvConfirm.setEnabled(true);
                        item.setPlaying(false);
                        animationDrawable.stop();
                    }
                }
            }


        }
    }


    private MediaPlayer player;

    private void previewMusic(PrepareMusicModel prepareMusicModel) {
        UbtLog.d("PrepareMusicUtil", "previewMusic");
        if (isShowDelete) {
            return;
        }
        try {
            if (player != null) {
                player.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String name = prepareMusicModel.getMusicName();
        int songType = prepareMusicModel.getMusicType();
        String path = "";
        if (songType == 0) {
            path = setPlayFile(name);
        } else if (songType == 1) {
            path = FileTools.record + File.separator + name + ".mp3";
        }
        final File mp3 = new File(path);
        if (mp3.exists()) {

            if (player == null) {
                player = new MediaPlayer();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        UbtLog.d("onCompletion", "setOnCompletionListener");
                        stopPlayingAnimal();
                    }
                });
            }
            player.reset();

            try {
                player.setDataSource(path);
                player.setOnPreparedListener(onPreparedListener);
                player.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            //   actionAdapter.stopAnim(false);
        }
    };

    String mCurrentSourcePath = "";

    private String setPlayFile(String fileName) {

        mCurrentSourcePath = FileTools.tmp_file_cache + "/" + fileName + ".mp3";
        boolean isFileCreateSuccess = FileTools.writeAssetsToSd("music/" + fileName + ".mp3", mContext, mCurrentSourcePath);

        UbtLog.d("setPlayFile", "isFileCreateSuccess:" + isFileCreateSuccess);
        if (isFileCreateSuccess) {

            return mCurrentSourcePath;

        } else {
            return "";
        }

    }

    /**
     * 停止动画
     */
    private void stopPlayingAnimal() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setPlaying(false);
        }
        actionAdapter.notifyDataSetChanged();
    }


    //删除用户录音的临时文件
    public void deleteMP3RecordFile(String name) {
        UbtLog.d("deleteMP3RecordFile", "deleteMP3RecordFile:" + name);
        FileTools.DeleteFile(new File(FileTools.record + File.separator + name + ".mp3"));

    }

}
