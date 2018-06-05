package com.ubt.alpha1e.edu.userinfo.mainuser;

/*import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;*/

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e.edu.userinfo.notice.NoticeFragment;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author：liuhai
 * @date：2017/10/27 11:52
 * @modifier：ubt
 * @modify_date：2017/10/27 11:52
 * 个人中心主界面
 * version
 */
public class UserCenterActivity extends MVPBaseActivity<UserCenterContact.UserCenterView, UserCenterImpPresenter> implements UserCenterContact.UserCenterView, NoticeFragment.CallBackListener {
    private static final String TAG = UserCenterActivity.class.getSimpleName();

    @BindView(R.id.tv_main_title)
    TextView mTvTitle;
    @BindView(R.id.rl_leftmenu)
    RecyclerView mRecyclerView;
    @BindView(R.id.fl_main_content)
    FrameLayout mViewPager;
    @BindView(R.id.iv_main_back)
    ImageView mIvMainBack;
    private List<LeftMenuModel> mMenuModels = new ArrayList<>();//左侧菜单栏信息
    private List<Fragment> mFragmentList = new ArrayList<>();
    private BaseQuickAdapter mBaseQuickAdapter;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment mCurrentFragment;

    private int mCurrentPosition = 0;

    public static String USER_CURRENT_POSITION = "current_check_position";

    String[] mStrings = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_user_center);
        mPresenter.initData(this);
        mPresenter.getUnReadMessage();
        mCurrentPosition = getIntent().getIntExtra(USER_CURRENT_POSITION, 0);
        UbtLog.d(TAG, "------------onCreate----------" + mCurrentPosition);
        initUI();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        UbtLog.d(TAG, "------------onNewIntent----------" + mCurrentPosition);
        int position = intent.getIntExtra(USER_CURRENT_POSITION, 0);
        mMenuModels.get(mCurrentPosition).setChick(false);
        mCurrentPosition = position;
        initUI();
        mPresenter.getUnReadMessage();
        if (null != mCurrentFragment && mCurrentFragment instanceof NoticeFragment) {
            NoticeFragment fragment = (NoticeFragment) mCurrentFragment;
            UbtLog.d(TAG, "------------refreshNewData----------" + mCurrentPosition);
            fragment.refreshNewData();
        }
    }


    @Override
    public int getContentViewId() {
        return R.layout.activity_user_center;
    }


    @OnClick(R.id.iv_main_back)
    public void onBack(View view) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UbtLog.d(TAG, "------onPause---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "------onDestroy---");
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initUI() {
        mFragmentManager = this.getSupportFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentList.get(mCurrentPosition);
        if (!fragment.isAdded()) {
            mFragmentTransaction.add(R.id.fl_main_content, fragment);
            mFragmentTransaction.commit();
        } else {
            loadFragment(fragment);
        }
        mCurrentFragment = fragment;
        mRecyclerView = (RecyclerView) findViewById(R.id.rl_leftmenu);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mTvTitle.setText(mMenuModels.get(0).getNameString());
        mMenuModels.get(mCurrentPosition).setChick(true);
        mBaseQuickAdapter = new LeftAdapter(R.layout.layout_usercenter_left_item, mMenuModels);

        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LeftMenuModel menuModel = mMenuModels.get(position);
                // mTvTitle.setText(menuModel.getNameString());
                mCurrentPosition = position;
                loadFragment(mFragmentList.get(position));
                for (int i = 0; i < mMenuModels.size(); i++) {
                    if (mMenuModels.get(i).getNameString().equals(menuModel.getNameString())) {
                        mMenuModels.get(i).setChick(true);
                    } else {
                        mMenuModels.get(i).setChick(false);
                    }
                }
                mBaseQuickAdapter.notifyDataSetChanged();
            }


        });
        mRecyclerView.setAdapter(mBaseQuickAdapter);
    }

    /**
     * 重新加载Fragment
     *
     * @param targetFragment 加载的Fragment
     */
    private void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // UbtLog.d(TAG,"targetFragment.isAdded()->>>"+(!targetFragment.isAdded()));
        if (!targetFragment.isAdded()) {
            mCurrentFragment.onStop();
            transaction
                    .hide(mCurrentFragment)
                    .add(R.id.fl_main_content, targetFragment)
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
    public void loadData(List<LeftMenuModel> list, List<Fragment> fragments) {
        mMenuModels.clear();
        mMenuModels.addAll(list);
        mFragmentList.clear();
        mFragmentList.addAll(fragments);
    }

    @Override
    public void getUnReadMessage(boolean isSuccess, int count) {
        if (count > 0) {
            mMenuModels.get(2).setCountUnRead(count);
            mBaseQuickAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onChangeUnReadMessage() {
        UbtLog.d("Notice", "onChangeUnReadMessage==");
        int unReadCount = mMenuModels.get(2).getCountUnRead();
        mMenuModels.get(2).setCountUnRead(unReadCount - 1);
        mBaseQuickAdapter.notifyDataSetChanged();
    }


    public class LeftAdapter extends BaseQuickAdapter<LeftMenuModel, BaseViewHolder> {

        public LeftAdapter(@LayoutRes int layoutResId, @Nullable List<LeftMenuModel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, LeftMenuModel item) {
            helper.setText(R.id.tv_item_name, item.getNameString());
            TextView barView = helper.getView(R.id.bar_num);
            Drawable drawable = getResources().getDrawable(item.getImageId());
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            TextView tv = helper.getView(R.id.tv_item_name);
            tv.setCompoundDrawables(drawable, null, null, null);
            if (item.isChick()) {
                tv.setEnabled(true);
                //  helper.setBackgroundColor(R.id.tv_item_name, getContext().getResources().getColor(R.color.txt_info));
            } else {
                //  helper.setBackgroundColor(R.id.tv_item_name, getContext().getResources().getColor(R.color.white));
                tv.setEnabled(false);
            }
            if (item.getNameString().equals("消息")) {
                if (item.getCountUnRead() > 0) {
                    barView.setVisibility(View.VISIBLE);
                    if (item.getCountUnRead() < 100) {
                        barView.setText(item.getCountUnRead() + "");
                    } else {
                        barView.setText("99+");
                    }
                }
            } else {
                barView.setVisibility(View.GONE);
            }

        }
    }
}
