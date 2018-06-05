package com.ubt.alpha1e.edu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.action.ActionsCreateActivity;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.ui.ActionsAdapter;
import com.ubt.alpha1e.edu.ui.ActionsNewEditActivity;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class DialogActions extends Dialog {

    private static final String TAG = "DialogActions";

    private String title = "";
    private List<Map<String, Object>> data;

    private TextView tvTitle;
    private RecyclerView rvActions;
    private TextView tvCancel;
    private TextView tvConfirm;

    private BaseActivity context;
    private ActionsAdapter adapter;
    private DialogActions dialog;

    private Map<String, Object> selectData = new HashMap<String, Object>();

    private int type = 0;  // 0,表示基本动作， 1表示高级动作， 2表示音乐

    private ImageView ivDelete;
    private boolean show = false;

    private  Date lastTime_play = null;


    public DialogActions(BaseActivity context, String title, List<Map<String, Object>> data, int type) {
        super(context);
        this.context = context;
        this.title = title;
        this.data = data;
        dialog = this;
        this.type = type;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_aciton_select);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int)((display.getWidth())*0.8); //设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);
        initView();
    }



    @Override
    public void dismiss() {
        super.dismiss();
        if(player != null){
            player.stop();
        }
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.title_actions);

        rvActions = (RecyclerView) findViewById(R.id.rv_actions);
//        rvActions.addItemDecoration(new SpaceItemDecoration(2));
        GridLayoutManager layoutManager = new GridLayoutManager(context, 5);
        rvActions.setLayoutManager(layoutManager);
        adapter = new ActionsAdapter(context, data, type, dialog);
        rvActions.setAdapter(adapter);

        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);

        tvTitle.setText(title);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(player != null){
                    player.stop();
                }
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null){
                    player.stop();
                }
                UbtLog.d(TAG, "selectData size:" + selectData.size());
                if(selectData.size() ==0){
                    return;
                }
                dismiss();
                ((ActionsCreateActivity)context).addLibAction(selectData, type);


            }
        });


        adapter.setOnItemListener(new ActionsAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, int pos, Map<String, Object> data) {




                UbtLog.d(TAG, "data:" + data);
                if(type == 2 ){
                    if(pos == 0){
                        DialogDub dialogDub = new DialogDub(context);
                        dialogDub.show();
                        dismiss();
                    }else{

                        adapter.stopAnim(true);
                        adapter.setDefSelect(pos);
                        selectData = data;
                        tvConfirm.setTextColor(context.getResources().getColor(R.color.text_confirm_color));;
                        tvConfirm.setEnabled(true);
                        previewMusic(data);
                    }

                }else{
                    adapter.setDefSelect(pos);
                    selectData = data;
                    tvConfirm.setTextColor(context.getResources().getColor(R.color.text_confirm_color));;
                    tvConfirm.setEnabled(true);
                    ((ActionsCreateActivity)context).doReset();
                    ((ActionsCreateActivity)context).previewAction(data, type);

                }


            }
        });

        ivDelete = (ImageView) findViewById(R.id.iv_delete);
        if(type == 2){
            ivDelete.setVisibility(View.VISIBLE);
        }else{
            ivDelete.setVisibility(View.INVISIBLE);
        }

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null){
                    player.stop();
                    adapter.stopAnim(true);
                }
                if(show){
                    show = false;
                    adapter.showDelete(show);
                    ivDelete.setImageResource(R.drawable.icon_delete);
                }else{
                    show = true;
                    adapter.showDelete(show);
                    ivDelete.setImageResource(R.drawable.icon_save);
                }

            }
        });



    }


    private MediaPlayer player;
    private void previewMusic(Map<String, Object> map){
        UbtLog.d(TAG, "previewMusic");
        if(show){
            return;
        }

        ((ActionsCreateActivity)context).stopMusic();

        try {
            if(player != null){
                player.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String name = (String)map.get(ActionsNewEditActivity.SONGS_NAME);
        int songType = (int) map.get(ActionsNewEditActivity.SONGS_TYPE);
        String path = "";
        if(songType == 0){

            path = setPlayFile(name);


        }else if(songType == 1){
            path = FileTools.record + File.separator + name+ ".mp3";
        }
        final File mp3 = new File(path);
        if (mp3.exists()) {


            if (player == null) {
                player = new MediaPlayer();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        UbtLog.d(TAG, "setOnCompletionListener");
                        adapter.stopAnim(true);
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
            adapter.stopAnim(false);
        }
    };


    String mCurrentSourcePath = "";

    private String setPlayFile(String fileName) {

        mCurrentSourcePath = FileTools.tmp_file_cache + "/" + fileName + ".mp3";
        boolean isFileCreateSuccess = FileTools.writeAssetsToSd("music/" + fileName + ".mp3", context, mCurrentSourcePath);

        UbtLog.d(TAG, "isFileCreateSuccess:" + isFileCreateSuccess);
        if(isFileCreateSuccess){

            return  mCurrentSourcePath;

        }else{
            return  "" ;
        }

    }


    //删除用户录音的临时文件
    public void deleteMP3RecordFile(String name) {

        UbtLog.d(TAG, "deleteMP3RecordFile:" + name);
        FileTools.DeleteFile(new File(FileTools.record + File.separator+name + ".mp3"));

    }









}
