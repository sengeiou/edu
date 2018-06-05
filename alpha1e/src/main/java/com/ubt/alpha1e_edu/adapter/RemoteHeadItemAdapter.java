package com.ubt.alpha1e_edu.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.ui.RemoteAddActivity;
import com.ubt.alpha1e_edu.ui.helper.RemoteHelper;

public class RemoteHeadItemAdapter extends BaseAdapter{

    Activity mActivity;
    private LayoutInflater layoutInflater;
    private List<Map<String,Object>> list;
    public Handler mHandler = null;
    public RemoteHelper mHelper = null;

    public RemoteHeadItemAdapter(Activity activity,List<Map<String,Object>> list,Handler handler,RemoteHelper helper)
    {
        super();
        mActivity = activity;
        mHandler = handler;
        mHelper = helper;
        if (list != null) {
            this.list = list;
        } else {
            this.list = new ArrayList<Map<String,Object>>();
        }
        layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        HeadItemHolder headItemHolder;
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.layout_remote_grid_item, null);
            headItemHolder = new HeadItemHolder();
            headItemHolder.imageView = (ImageView) convertView.findViewById(R.id.img_remote_head_item);
            headItemHolder.selectView = (ImageView) convertView.findViewById(R.id.img_remote_head_select);
            convertView.setTag(headItemHolder);
        } else
        {
            headItemHolder = (HeadItemHolder) convertView.getTag();
        }

        headItemHolder.imageView.setImageResource(mHelper.getResId((String) list.get(position).get(RemoteHelper.MAP_KEY_HEAD_IMAGE_ICON)));

        boolean isSelect = (boolean)list.get(position).get(RemoteHelper.MAP_KEY_HEAD_IMAGE_ICON_SELECT);
        if(isSelect){
            headItemHolder.selectView.setVisibility(View.VISIBLE);
        }else{
            headItemHolder.selectView.setVisibility(View.INVISIBLE);
        }

        final int selectPos = position;
        headItemHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                msg.what = RemoteAddActivity.UPDATE_HEAD;
                msg.obj = selectPos;
                mHandler.sendMessage(msg);
            }
        });

        return convertView;
    }

}

class HeadItemHolder
{
    public ImageView imageView;
    public ImageView selectView;
}

