package com.ubt.alpha1e_edu.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.ui.helper.RemoteHelper;

public class CustomActionItemAdapter extends BaseAdapter{
    
    Activity mActivity;
 	private LayoutInflater layoutInflater;
 	private List<Map<String,Object>> list;
 	private Handler handler;
 
    public CustomActionItemAdapter(Activity activity, Handler handler, List<Map<String,Object>> list)
    { 
        super(); 
        mActivity = activity;
        if (list != null) {
        	 this.list = list;
		} else {
			this.list = new ArrayList<Map<String,Object>>();
		}
		layoutInflater = LayoutInflater.from(activity);
		this.handler = handler;
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
        CustomActionItemHolder mCustomActionItemHolder;
        if (convertView == null) 
        { 
            convertView = layoutInflater.inflate(R.layout.layout_remote_costom_setting_item, null);
            mCustomActionItemHolder = new CustomActionItemHolder();
            mCustomActionItemHolder.txt_remote_action_name = (TextView) convertView.findViewById(R.id.txt_remote_action_name);
            mCustomActionItemHolder.img_remote_action_icon = (ImageView) convertView.findViewById(R.id.img_remote_action_icon);

            convertView.setTag(mCustomActionItemHolder);
        } else
        {
            mCustomActionItemHolder = (CustomActionItemHolder) convertView.getTag();
        }
        mCustomActionItemHolder.txt_remote_action_name.setText((String)list.get(position).get(RemoteHelper.MAP_KEY_ACTION_ITEM_NAME));

        Glide.with(mActivity).load(list.get(position).get(RemoteHelper.MAP_KEY_ACTION_ITEM_ICON))
                .centerCrop()
                .placeholder(R.drawable.sec_action_logo)
                .into(mCustomActionItemHolder.img_remote_action_icon);

        return convertView;
    }
	
}
class CustomActionItemHolder
{
    public ImageView img_remote_action_icon;
    public TextView txt_remote_action_name;
} 

