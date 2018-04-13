package com.ubt.alpha1e.onlineaudioplayer.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.onlineaudioplayer.Fragment.OnlineAudioAlbumPlayerFragment;
import com.ubt.alpha1e.onlineaudioplayer.playeventlist.OnlineAudioEventListActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

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
        return OnlineAudioAlbumPlayerFragment.mGradData.size();
    }

    @Override
    public Object getItem(int i) {
        return OnlineAudioAlbumPlayerFragment.mGradData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) OnlineAudioAlbumPlayerFragment.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.dialog_online_player, null);
            }
            if(OnlineAudioAlbumPlayerFragment.mGradData.get(i)!=null)
            ((TextView) convertView.findViewById(R.id.grade_name)).setText(mTextGrade[Integer.parseInt(OnlineAudioAlbumPlayerFragment.mGradData.get(i))]);
            final CheckBox mCheckBox = ((CheckBox) convertView.findViewById(R.id.grade_select));
            mCheckBox.setChecked(OnlineAudioAlbumPlayerFragment.mGradeSelectedData.get(i));
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCheckBox.isChecked()) {
                        Message msg = new Message();
                        msg.what = OnlineAudioAlbumPlayerFragment.GRADE_SELECT_ADD;
                        msg.obj = OnlineAudioAlbumPlayerFragment.mGradData.get(i);
                        mHandler.sendMessage(msg);
                        OnlineAudioAlbumPlayerFragment.mGradeSelectedData.add(i, true);
                    } else {
                        Message msg = new Message();
                        msg.what = OnlineAudioAlbumPlayerFragment.GRADE_UNSELECT_DELETE;
                        msg.obj = OnlineAudioAlbumPlayerFragment.mGradData.get(i);
                        mHandler.sendMessage(msg);
                        OnlineAudioAlbumPlayerFragment.mGradeSelectedData.add(i, false);
                    }
                    UbtLog.d("GradeSelectedAdapter", "POSITION " + i + "state ");

                }
            });
            return convertView;
    }
}
