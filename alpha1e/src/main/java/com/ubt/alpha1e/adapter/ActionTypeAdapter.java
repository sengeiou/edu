package com.ubt.alpha1e.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/10.
 */
public class ActionTypeAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Map<String, Object>> mData = null;

    public ActionTypeAdapter(Context context,ArrayList<Map<String, Object>> listItems){
        this.mInflater = LayoutInflater.from(context);
        mData = listItems;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder=new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_playground_actions_grid_item, null);
            holder.actionImg = (ImageView)convertView.findViewById(R.id.img_actions_item);
            holder.actionName = (TextView)convertView.findViewById(R.id.img_actions_des);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.actionImg.setImageDrawable((Drawable) mData.get(position).get("image"));
        holder.actionName.setText((String)mData.get(position).get("name"));

        return convertView;
    }

}

class ViewHolder{
    public ImageView actionImg;
    public TextView actionName;
}
