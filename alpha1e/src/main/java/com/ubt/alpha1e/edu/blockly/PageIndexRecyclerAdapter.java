package com.ubt.alpha1e.edu.blockly;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.model.LessonTaskInfo;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class PageIndexRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "PageIndexRecyclerAdapter";

    private Context mContext;
    public List<LessonTaskInfo> mDatas = new ArrayList<>();
    private View mView;

    public PageIndexRecyclerAdapter(Context mContext, List<LessonTaskInfo> mDatas) {
        super();
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    public void setData(List<LessonTaskInfo>  data) {
        this.mDatas = data;
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        PageIndexHolder myHolder = (PageIndexHolder)holder;
        LessonTaskInfo data = mDatas.get(position);
        UbtLog.d(TAG,"data.status = " + data.status);
        if(data.status == 1){
            myHolder.imgPageIndex.setImageResource(R.drawable.lesson_task_pass);
        }else {
            myHolder.imgPageIndex.setImageResource(R.drawable.lesson_task_no_pass);
        }

        if(data.is_current_show == 1){
            myHolder.imgPageIndex.setImageResource(R.drawable.lesson_task_current);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_task_item_index, parent, false);
        PageIndexHolder mPageIndexHolder = new PageIndexHolder(mView);
        return mPageIndexHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class PageIndexHolder extends RecyclerView.ViewHolder
    {
        public ImageView imgPageIndex;
        public PageIndexHolder(View view)
        {
            super(view);
            imgPageIndex = (ImageView) view.findViewById(R.id.img_page_index);
        }
    }
}
