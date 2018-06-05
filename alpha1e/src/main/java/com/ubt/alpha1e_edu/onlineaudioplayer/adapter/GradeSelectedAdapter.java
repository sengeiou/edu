package com.ubt.alpha1e_edu.onlineaudioplayer.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.onlineaudioplayer.Fragment.OnlineAlbumListFragment;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

/**
 * @作者：ubt
 * @日期: 2018/4/10 19:54
 * @描述:
 */


public class GradeSelectedAdapter extends BaseAdapter {
    private Handler mHandler;
    private String[] mTextGrade={"幼儿园大班","小学一年级","小学二年级","小学三年级","小学四年级","小学五年级","小学六年级","初中一年级"};
    public GradeSelectedAdapter(Handler Handler){
         mHandler=Handler;
    }
    @Override
    public int getCount() {
        return OnlineAlbumListFragment.mGradData.size();
    }


    @Override
    public Object getItem(int i) {
        return OnlineAlbumListFragment.mGradData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) OnlineAlbumListFragment.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.dialog_online_player, null);
            }
            if(OnlineAlbumListFragment.mGradData.get(i)!=null)
            ((TextView) convertView.findViewById(R.id.grade_name)).setText(mTextGrade[Integer.parseInt(OnlineAlbumListFragment.mGradData.get(i))]);
            final CheckBox mCheckBox = ((CheckBox) convertView.findViewById(R.id.grade_select));
            mCheckBox.setChecked(OnlineAlbumListFragment.mGradeSelectedData.get(i));
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCheckBox.isChecked()) {
                        Message msg = new Message();
                        msg.what = OnlineAlbumListFragment.GRADE_SELECT_ADD;
                        msg.obj = OnlineAlbumListFragment.mGradData.get(i);
                        mHandler.sendMessage(msg);
                        OnlineAlbumListFragment.mGradeSelectedData.add(i, true);
                    } else {
                        Message msg = new Message();
                        msg.what = OnlineAlbumListFragment.GRADE_UNSELECT_DELETE;
                        msg.obj = OnlineAlbumListFragment.mGradData.get(i);
                        mHandler.sendMessage(msg);
                        OnlineAlbumListFragment.mGradeSelectedData.add(i, false);
                    }
                    UbtLog.d("GradeSelectedAdapter", "POSITION " + i + "state "+ OnlineAlbumListFragment.mGradeSelectedData.get(i));

                }
            });
            return convertView;
    }
}
