package com.ubt.alpha1e.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.model.MessageInfo;
import com.ubt.alpha1e.library.AutoVideo.visibility.items.ListItem;
import com.ubt.alpha1e.library.AutoVideo.visibility.scroll.ItemsProvider;

import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.helper.MessageHelper;

import java.util.List;

/**
 * Created by Administrator on 2016/5/11.
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemsProvider{

    private static final String TAG = "ActionsSquareAdapter";


    private Context mContext;
    private BaseActivity activity;
    private List<MessageInfo> mDatas;
    private View mView;
    public RecyclerView mRecyclerView;
    public MessageHelper mHelper;

    public boolean isScolling = false;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.message_item, parent, false);
        MessageHolder mMessageHolder = new MessageHolder(mView);

        return mMessageHolder;
    }


    public MessageAdapter(BaseActivity mActivity, Context context, List<MessageInfo> mDatas,
                          RecyclerView recyclerView, MessageHelper mHelper)
    {
        this.activity = mActivity;
        this.mContext = context;
        this.mDatas = mDatas;
        this.mRecyclerView = recyclerView;
        this.mHelper = mHelper;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        FillMessageContent.fillMessageContent(activity,mContext,(MessageHolder) holder,mDatas.get(position),mHelper,position,this.isScolling);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setScrolling(boolean isScolling){
        this.isScolling = isScolling;
    }

    @Override
    public ListItem getListItem(int position) {
        return null;
    }

    @Override
    public int listItemSize() {
        return 0;
    }
}

