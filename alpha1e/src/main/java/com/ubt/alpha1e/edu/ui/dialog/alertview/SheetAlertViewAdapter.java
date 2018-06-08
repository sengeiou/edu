package com.ubt.alpha1e.edu.ui.dialog.alertview;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;

import java.util.List;

/**
 * Created by Sai on 15/8/9.
 */
public class SheetAlertViewAdapter extends BaseAdapter{
    private List<SpannableString> mDatas;
    private List<SpannableString> mDestructive;
    public SheetAlertViewAdapter(List<SpannableString> datas, List<SpannableString> destructive){
        this.mDatas =datas;
        this.mDestructive =destructive;
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpannableString data= mDatas.get(position);
        Holder holder=null;
        View view =convertView;
        if(view==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view=inflater.inflate(R.layout.item_alertbutton, null);
            holder=creatHolder(view);
            view.setTag(holder);
        }
        else{
            holder=(Holder) view.getTag();
        }
        if(position == 0){
            holder.dividerLineView.setVisibility(View.INVISIBLE);
        }else{
            holder.dividerLineView.setVisibility(View.VISIBLE);
        }
        holder.UpdateUI(parent.getContext(),data,position);
        return view;
    }
    public Holder creatHolder(View view){
        return new Holder(view);
    }
    class Holder {
        private TextView tvAlert;
        private View dividerLineView;

        public Holder(View view){
            tvAlert = (TextView) view.findViewById(R.id.tvAlert);
            dividerLineView = (View) view.findViewById(R.id.divider_line);
        }
        public void UpdateUI(Context context,SpannableString data,int position){
            tvAlert.setText(data);
            if (mDestructive!= null && mDestructive.contains(data)){
                tvAlert.setTextColor(context.getResources().getColor(R.color.T1));
            }
            else{
                tvAlert.setTextColor(context.getResources().getColor(R.color.textColor_alert_button_others));
            }
        }
    }
}