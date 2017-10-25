package com.ubt.alpha1e.ui.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.DynamicActivity;
import com.ubt.alpha1e.ui.MyActionsSelectActivity;
import com.ubt.alpha1e.ui.MyMainActivity;
import com.ubt.alpha1e.ui.dialog.alertview.AlertView;
import com.ubt.alpha1e.ui.dialog.alertview.OnItemClickListener;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */


public class IShowPublishView {


    private String TAG = "IShowPublishView";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;

    private View view;
    private ImageView ivClose;
    private GridView mGridView;
    private SimpleAdapter simpleAdapter;


    private boolean created = false;

    public IShowPublishView(Context context) {
        mContext = context;
        createGuideView();

    }

    private void createGuideView() {

        if(created){
            UbtLog.d(TAG, "app guide view already created!");
            return;
        }
        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN ;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(180, 0, 0, 0));

        LayoutInflater inflater = LayoutInflater.from(mContext.getApplicationContext());
        view = (View) inflater.inflate(R.layout.dialog_ishow, null);
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(colorDrawable);
        }else{
            view.setBackgroundDrawable(colorDrawable);
        }
        initView(view);

        mWindowManager.addView(view, wmParams);
        created = true;

    }

    private void initView(View view) {
        ivClose = (ImageView) view.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                closePublishView();
            }
        });
        mGridView = (GridView) view.findViewById(R.id.gv_type);
        ArrayList<Map<String, Object>> listItems = new ArrayList<>();
        int[] imageIds = {R.drawable.icon_actions, R.drawable.icon_text, R.drawable.icon_photo,
                R.drawable.icon_video, R.drawable.icon_empty, R.drawable.icon_empty};
        String[] imageNames = {
                ((BaseActivity)mContext).getStringResources("ui_home_actions"),
                ((BaseActivity)mContext).getStringResources("ui_dynamic_release_text"),
                ((BaseActivity)mContext).getStringResources("ui_dynamic_release_photo"),
                ((BaseActivity)mContext).getStringResources("ui_dynamic_release_video"), "", ""};
        for (int i = 0; i < 6; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("image", imageIds[i]);
            item.put("name", imageNames[i]);
            listItems.add(item);
        }
        simpleAdapter = new SimpleAdapter(mContext, listItems, R.layout.layout_dynamic_publish_grid_item,
                new String[]{
                        "image", "name"
                }, new int[]{
                R.id.img_actions_item, R.id.img_actions_des
        });
        mGridView.setAdapter(simpleAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.putExtra(DynamicActivity.SEND_TYPE, position);
                if(position == 0){
                    intent.setClass(mContext, MyActionsSelectActivity.class);
                    mContext.startActivity(intent);
                }else if(position == 1){
                    intent.setClass(mContext, DynamicActivity.class);
                    mContext.startActivity(intent);

                }else if(position ==2){
                    new AlertView(null, null, ((BaseActivity)mContext).getStringResources("ui_common_cancel"), new String[]{
                            ((BaseActivity)mContext).getStringResources("ui_distribute_take_photo"),
                            ((BaseActivity)mContext).getStringResources("ui_distribute_phone_library")},
                            null,
                            mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                        public void onItemClick(Object o,int position){
                            switch (position)
                            {
                                case 0:
                                    ((MyMainActivity)mContext).fromTakingPhoto();
                                    break;
                                case 1:
                                    ((MyMainActivity)mContext).fromFileSelect();
                                    break;
                            }

                        }
                    }).show();
                }else if(position == 3){
                    ((MyMainActivity)mContext).takeVideo();
                }


                closePublishView();
//                ActionsPublishActivity.launchActivity((BaseActivity)mContext, null, 0, null, null);
            }
        });
    }



    public void closePublishView() {
        if(mWindowManager != null){
            mWindowManager.removeView(view);
            mWindowManager = null;
        }
    }



}
