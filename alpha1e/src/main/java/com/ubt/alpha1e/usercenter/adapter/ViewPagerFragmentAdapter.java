package com.ubt.alpha1e.usercenter.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ubt.alpha1e.base.BaseMvpFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liuhai
 * @date：2017/10/27 12:34
 * @modifier：ubt
 * @modify_date：2017/10/27 12:34
 * [A brief description]
 * version
 */

public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {

    private List<BaseMvpFragment> mList = new ArrayList<BaseMvpFragment>();

    public ViewPagerFragmentAdapter(FragmentManager fm, List<BaseMvpFragment> list) {
        super(fm);
        this.mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }
}
