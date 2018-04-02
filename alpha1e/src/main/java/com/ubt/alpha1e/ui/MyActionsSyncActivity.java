package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.ActionsSyncRecyclerAdapter;
import com.ubt.alpha1e.adapter.FillLocalContent;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MyActionsSyncActivity extends BaseActivity implements View.OnClickListener {


    private RecyclerView mSyncRecyclerview;

    private LinearLayoutManager mLayoutManager;
    private ActionsSyncRecyclerAdapter mAdapter;
    private List<Map<String, Object>> mDatas = new ArrayList<>();
    private List<ActionInfo> mSyncList = new ArrayList<>();
    private int type = -1;//属于哪个模块(local/download/collect/create/sync_download/sync_create)
    private TextView txt_delete, txt_download, txt_select_all;
    private ImageView img_select_all, img_cancel,img_circle;
    private boolean isSelectAll;
    private int sync_type = -1;//属于哪个模块的同步(下载/创建)

    private MyActionsHelper mHelper;


    public static void launchActivity(Activity activity,int sync_type, int requestCode) {
//        Intent intent = new Intent();
//        intent.setClass(activity,
//                MyActionsSyncActivity.class);
//        intent.putExtra(MyActionsHelper.ACTIONS_SYNC_TYPE,sync_type);
//        activity.startActivityForResult(intent, requestCode);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_actions_sync);
        sync_type = (int)getIntent().getExtras().get(MyActionsHelper.ACTIONS_SYNC_TYPE);
        type = sync_type == FillLocalContent.CREATE_ACTIONS? FillLocalContent.SYNC_CREATE_ACTIONS: FillLocalContent.SYNC_DOWNLOAD_ACTIONS;
        new Exception().printStackTrace();
        initUI();
        initControlListener();
        requestData(sync_type);
    }

    /***
     * request sync data
     */
    private void requestData(final int type) {
        UserInfo userInfo = ((AlphaApplication) this
                .getApplicationContext()).getCurrentUserInfo();
        long actionUserId = userInfo==null?0:userInfo.userId;
        String token = userInfo==null?"":userInfo.token;

        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.createaction_find);
        String params = HttpAddress
                .getParamsForPost(
                        new String[]{actionUserId + ""},
                        HttpAddress.Request_type.createaction_find,
                        this);
        if(type== FillLocalContent.DOWNLOAD_ACTIONS)
        {
            url = HttpAddress.getRequestUrl(HttpAddress.Request_type.download_find);
            params = HttpAddress
                    .getParamsForPost(
                            new String[]{actionUserId + "",token},
                            HttpAddress.Request_type.download_find,
                            this);
        }

        OkHttpClientUtils
                .getJsonByPostRequest(url,params)
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String json,int i) {
                        UbtLog.d("wilson", "onResponse:" + json);
                        if(sync_type== FillLocalContent.CREATE_ACTIONS)
                            parseCreationDatas(json);
                        else
                            paraseDownloadDatas(json);
                    }

                    @Override
                    public void onError(Call call, Exception e,int i) {
                        UbtLog.d("wilson", "onResponse:" + e.getMessage());
                    }
                });
    }


    private void parseCreationDatas(String json)
    {
        if (JsonTools.getJsonStatus(json)) {
            // 请求成功
            JSONArray j_list = JsonTools.getJsonModels(json);
            for (int i = 0; i < j_list.length(); i++) {
                try {
                    NewActionInfo info = new NewActionInfo()
                            .getThiz(j_list.get(i).toString());
                    mSyncList.add(info);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mDatas = mHelper.loadSyncDatas(mSyncList,sync_type);
            mAdapter.setDatas(mDatas);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void paraseDownloadDatas(String json)
    {
        if (JsonTools.getJsonStatus(json)) {
            // 请求成功

            try {
                BaseResponseModel<List<ActionInfo>> baseResponseModel = GsonImpl.get().toObject(json,
                        new TypeToken<BaseResponseModel<List<ActionInfo>>>(){}.getType() );
                mSyncList.addAll(baseResponseModel.models);
            }catch (Exception e)
            {

            }
            mDatas = mHelper.loadSyncDatas(mSyncList,sync_type);
            mAdapter.setDatas(mDatas);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        TextView txt_title = (TextView) this.findViewById(R.id.txt_base_title_name);
        if(sync_type== FillLocalContent.DOWNLOAD_ACTIONS)
        {
            txt_title.setText(getStringResources("ui_myaction_synchronize_download_history"));
        }else
        {
            txt_title.setText(getStringResources("ui_creation_history"));
        }
    }

    @Override
    protected void initUI() {
        img_cancel = (ImageView) findViewById(R.id.img_cancel);
        txt_delete = (TextView) findViewById(R.id.txt_myaction_delete);
        txt_download = (TextView) findViewById(R.id.txt_myaction_download);
        img_select_all = (ImageView) findViewById(R.id.img_myaction_select_all);
        txt_select_all = (TextView) findViewById(R.id.txt_myaction_select_all);
        img_cancel.setOnClickListener(this);
        txt_delete.setOnClickListener(this);
        txt_download.setOnClickListener(this);
        img_select_all.setOnClickListener(this);
        //img_circle.setOnClickListener(this);
        txt_select_all.setOnClickListener(this);
        mHelper = MyActionsHelper.getInstance(this);
        mSyncRecyclerview = (RecyclerView) findViewById(R.id.recyclerview_sync);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSyncRecyclerview.setLayoutManager(mLayoutManager);
        mAdapter = new ActionsSyncRecyclerAdapter(this, mDatas, type, mHelper);
        mSyncRecyclerview.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_myaction_select_all:
            case R.id.img_myaction_select_all:
                isSelectAll = !isSelectAll;
                doSelectAll(isSelectAll);
                break;
            case R.id.txt_myaction_delete:
                for(Map<String,Object> item:mDatas)
                {
                    if(!(Boolean)item.get(MyActionsHelper.map_val_action_selected))
                    {
                        doDeleteActions(false);
                        return;
                    }
                }
                new AlertDialog(this).builder().setMsg(getStringResources("ui_myaction_delete_all_history_warning")).setCancelable(true).
                            setPositiveButton(getStringResources("ui_common_confirm"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    doDeleteActions(true);
                                }
                            }).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();

                break;
            case R.id.txt_myaction_download:
                doDownloadActions();
                break;
            case R.id.img_cancel:
                this.finish();
                break;

        }

    }

    private void doSelectAll(boolean isSelectAll) {
        if (mDatas.size() == 0) return;
        updateTextViews(isSelectAll);
        img_select_all.setImageResource(isSelectAll ? R.drawable.mynew_actions_selected : R.drawable.myactions_normal);
        for (Map<String, Object> item : mDatas) {
            item.put(MyActionsHelper.map_val_action_selected, isSelectAll);
        }
        mAdapter.notifyDataSetChanged();

    }

    public void notifyAdapters(int pos) {
        if (mAdapter != null)
            mAdapter.notifyItemChanged(pos);
        for (Map<String, Object> item : mDatas) {
            if ((Boolean) item.get(MyActionsHelper.map_val_action_selected)) {
                updateTextViews(true);
                return;
            }
        }
        updateTextViews(false);
    }

    public void updateTextViews(boolean isNeedHighlight) {
        int deleteColor = isNeedHighlight ? getResources().getColor(R.color.T11) : getResources().getColor(R.color.T7);
        int downloadColor = isNeedHighlight ? getResources().getColor(R.color.T5) : getResources().getColor(R.color.T7);
        txt_delete.setTextColor(deleteColor);
        txt_download.setTextColor(downloadColor);
    }

    /**
     * delete actions selected
     */
    public void doDeleteActions(final boolean isAll) {
        if (mDatas.size() == 0)
            return;
        List<Map<String, Object>> mTempList = new ArrayList<>();
        for(Map<String,Object>  map:mDatas)
        {
            mTempList.add(map);
        }
        StringBuilder sb = new StringBuilder();
        for(Map<String,Object> item:mTempList)
        {
            int pos = mDatas.indexOf(item);
            if(isAll||(Boolean)item.get(MyActionsHelper.map_val_action_selected))
            {
                mDatas.remove(pos);
                mAdapter.notifyItemRemoved(pos);
                mAdapter.notifyItemRangeChanged(pos,mAdapter.getItemCount());
                if(type == FillLocalContent.SYNC_DOWNLOAD_ACTIONS)
                sb.append(((ActionInfo) item.get(MyActionsHelper.map_val_action)).downloadId+",");
                else
                    sb.append(((ActionInfo) item.get(MyActionsHelper.map_val_action)).actionOriginalId+",");

            }

        }
        if(sb.length()<=0)
            return;
        sb.deleteCharAt(sb.length()-1);
        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.createaction_delete);
        String params = HttpAddress.getParamsForPost(new String[]{sb.toString()},
                HttpAddress.Request_type.createaction_delete,
                this);
        if(type == FillLocalContent.SYNC_DOWNLOAD_ACTIONS)
        {
            url =  HttpAddress.getRequestUrl(HttpAddress.Request_type.download_delete);
            params = HttpAddress.getParamsForPost(new String[]{sb.toString()},
                    HttpAddress.Request_type.download_delete,
                    this);
        }
        OkHttpClientUtils
                .getJsonByPostRequest(url,params)
                .execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int i) {
                UbtLog.d("wilson","Exception:"+e.getMessage());
                if(isAll) MyActionsSyncActivity.this.finish();

            }

            @Override
            public void onResponse(String s,int i) {
                UbtLog.d("wilson","onResponse:"+s.toString());
                if(isAll) MyActionsSyncActivity.this.finish();
            }
        });

    }

    /**
     * download actions selected async
     */
    public void doDownloadActions() {
        ArrayList<String> mDownloadList = new ArrayList<>();
        for(Map<String,Object> item:mDatas)
        {
            if((Boolean)item.get(MyActionsHelper.map_val_action_selected))
            {
                ActionInfo info = (ActionInfo) item.get(MyActionsHelper.map_val_action);
                mDownloadList.add(ActionInfo.getString(info));
            }
        }
        if(mDownloadList.size()<=0)
            return;
        Intent intent = new Intent();
        intent.putStringArrayListExtra(MyActionsHelper.TRANSFOR_PARCEBLE,mDownloadList);//activity传递序列化,最好是parcelable
        MyActionsSyncActivity.this.setResult(RESULT_OK,intent);
        this.finish();

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }



}
