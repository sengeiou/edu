package com.ubt.alpha1e.usercenter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.BaseMvpActivity;
import com.ubt.alpha1e.usercenter.contact.UserCenterContact;
import com.ubt.alpha1e.usercenter.model.LeftMenuModel;
import com.ubt.alpha1e.usercenter.prenster.UserCenterImpPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liuhai
 * @date：2017/10/27 11:52
 * @modifier：ubt
 * @modify_date：2017/10/27 11:52
 * 个人中心主界面
 * version
 */
public class UserCenterActivity extends BaseMvpActivity<UserCenterContact.UserCenterView, UserCenterImpPresenter> implements UserCenterContact.UserCenterView {
    private List<LeftMenuModel> mMenuModels = new ArrayList<>();//左侧菜单栏信息
    private List<Fragment> mFragmentList = new ArrayList<>();
      private RecyclerView mRecyclerView;
    BaseQuickAdapter mBaseQuickAdapter;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        mPresenter.initData();
        mPresenter.initFragmentData();
        initUI();
    }

    @Override
    protected UserCenterImpPresenter createPresenter() {
        mPresenter = new UserCenterImpPresenter();
        return mPresenter;
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initUI() {
        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
         mFragmentTransaction.add(R.id.view_pager, mFragmentList.get(0));
        mFragmentTransaction.commit();
        mCurrentFragment = mFragmentList.get(0);
         mRecyclerView = (RecyclerView) findViewById(R.id.rl_leftmenu);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBaseQuickAdapter = new LeftAdapter(R.layout.layout_usercenter_left_item, mMenuModels);
        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(UserCenterActivity.this, "position==" + position, Toast.LENGTH_LONG).show();
                loadFragment(mFragmentList.get(position));
            }


        });
        mRecyclerView.setAdapter(mBaseQuickAdapter);

    }

    private void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
       // UbtLog.d(TAG,"targetFragment.isAdded()->>>"+(!targetFragment.isAdded()));
        if (!targetFragment.isAdded()) {
            mCurrentFragment.onStop();

            transaction
                    .hide(mCurrentFragment)
                    .add(R.id.view_pager, targetFragment)
                    .commit();
        } else {
            mCurrentFragment.onStop();
            targetFragment.onResume();

            transaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void loadData(List<LeftMenuModel> list) {
        mMenuModels.clear();
        mMenuModels.addAll(list);
    }

    @Override
    public void loadFragmentData(List<Fragment> fragments) {
        mFragmentList.clear();
        mFragmentList.addAll(fragments);

    }

    public class LeftAdapter extends BaseQuickAdapter<LeftMenuModel, BaseViewHolder> {

        public LeftAdapter(@LayoutRes int layoutResId, @Nullable List<LeftMenuModel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, LeftMenuModel item) {
            helper.setText(R.id.tv_item_name, item.getNameString());
        }
    }
}
