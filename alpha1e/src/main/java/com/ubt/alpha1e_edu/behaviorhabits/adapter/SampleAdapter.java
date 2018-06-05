package com.ubt.alpha1e_edu.behaviorhabits.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;


import com.ubt.alpha1e_edu.behaviorhabits.drag.OnItemChangeListener;
import com.ubt.alpha1e_edu.behaviorhabits.model.SampleEntity;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.Collections;
import java.util.List;


/**
 * User : Cyan(newbeeeeeeeee@gmail.com)
 * Date : 2017/1/4
 */
public abstract class SampleAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> implements OnItemChangeListener {
    protected Context context;
    protected volatile List<SampleEntity> data;

    public SampleAdapter(Context context, List<SampleEntity> list) {
        this.context = context;
        this.data = list;
    }

    @Override
    public void onItemMoved(int form, int target) {
        //UbtLog.e("SampleAdapter","onItemMoved form = " + form + "   target = " + target + "  data = " + data.size());
        try {
            if (form < target) {
                // after
                for (int i = form; i < target; i++) {
                    Collections.swap(data, i, i + 1);
                }
            } else {
                // before
                for (int i = form; i > target; i--) {
                    Collections.swap(data, i, i - 1);
                }
            }
            notifyItemMoved(form, target);
        }catch (Exception ex){
            UbtLog.e("SampleAdapter","onItemMoved form = " + form + "   target = " + target + "  data = " + data.size());
            UbtLog.e("SampleAdapter","onItemMoved ex = " + ex.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public boolean onItemDrag(int position) {
        UbtLog.e("SampleAdapter","onItemDrag position =" + position);
        if(position == -1 || position + 1 > data.size() ){
            return false;
        }
        return data.get(position).isDragEnable();
    }

    @Override
    public boolean onItemDrop(int position) {
        UbtLog.e("SampleAdapter","onItemDrop position =" + position);
        if(position == -1 || position + 1 > data.size() ){
            return false;
        }
        return data.get(position).isDropEnable();
    }
}
