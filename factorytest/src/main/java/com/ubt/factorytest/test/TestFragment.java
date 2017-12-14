package com.ubt.factorytest.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ubt.factorytest.ActionPlay.ActionFragment;
import com.ubt.factorytest.ActionPlay.ActionPresenter;
import com.ubt.factorytest.R;
import com.ubt.factorytest.test.recycleview.TestItemClickAdapter;
import com.ubt.factorytest.utils.ContextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/16 17:55
 * @描述:
 */

public class TestFragment extends SupportFragment implements TestContract.View {
    private static final String TAG = "TestFragment";

    private static final int MSG_SHOW_TOAST = 1;
    private static final int MSG_DATA_CHANGE = 2;
    private static final int MSG_ITEM_CHANGE = 3;

    @BindView(R.id.rv_test)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private TestItemClickAdapter mAdapter;

    Unbinder unbinder;
    Context mContext;
    private TestContract.Presenter mPresenter;

    private Handler mHandler;


    public static TestFragment newInstance(String mac, String rssi) {

        Bundle args = new Bundle();
        args.putString("bt_mac", mac);
        args.putString("bt_rssi", rssi);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        mContext = ContextUtils.getContext();
        unbinder = ButterKnife.bind(this, view);
        mPresenter.reInitBT();
        mPresenter.initBTListener();
        initToolbarNav(toolbar);
        initRecyclerView();
        toolbar.setTitle("测试开机时间");
        Bundle data = getArguments();
        String mac = data.getString("bt_mac");
        mPresenter.saveBTMac(mac);
        mPresenter.setBTRSSI(data.getString("bt_rssi"));
        toolbar.inflateMenu(R.menu.menu_testfragment);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.action_stop_record:
                        mPresenter.stopRobotRecord();
                        break;
                    case R.id.action_wifi_conf:
                        startWifiConfig();
                        break;
                    case R.id.action_action_test:
                        ActionFragment fragment = ActionFragment.newInstance();
                        new ActionPresenter(fragment);
                        startForResult(fragment, 100);
                        break;
                    case R.id.action_ageing_test:
                        mPresenter.startAgeing();
                        break;
                }

                return true;
            }
        });

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG_SHOW_TOAST:
                        String content = (String)msg.obj;
                        Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_DATA_CHANGE:
                        mAdapter.notifyDataSetChanged();
                        break;
                    case MSG_ITEM_CHANGE:
                        int position = msg.arg1;
                        mAdapter.notifyItemChanged(position, "1234");
                        break;
                }
            }
        };
        return view;
    }


    @Override
    public void setPresenter(@NonNull TestContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
//        mPresenter.start(); //重新加载数据
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        initItemClickAdapter();
    }

    public void initItemClickAdapter() {
        mAdapter = new TestItemClickAdapter(mPresenter.getTestInitData());
        mAdapter.openLoadAnimation(TestItemClickAdapter.SCALEIN);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                toolbar.setTitle("测试"+mAdapter.getItem(position).getTestItem());
                mAdapter.getItem(position).setImgID(R.mipmap.click_test);
                adapter.notifyItemChanged(position, "123121");
//                Log.i(TAG,"DATACACHE="+mPresenter.getDataCache().toString());
                mPresenter.startTest(mAdapter.getItem(position));
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch(view.getId()){
                    case R.id.btn_ok:
                        boolean isPass =  mAdapter.getItem(position).isPass();
                        mAdapter.getItem(position).setPass(!isPass);
                        adapter.notifyItemChanged(position, "123121");
                        break;
                    case R.id.btn_vol_sub:
                        Log.i(TAG,"btn_vol_sub！！");
                        mPresenter.adjustVolume(TestContract.ADJUST_SUB);
                        break;
                    case R.id.btn_vol_add:
                        Log.i(TAG,"btn_vol_add！！");
                        mPresenter.adjustVolume(TestContract.ADJUST_ADD);
                        break;
                }

            }
        });
    }

    @Override
    public void notifyDataSetChanged() {
        mHandler.sendEmptyMessage(MSG_DATA_CHANGE);
    }

    @Override
    public void notifyItemChanged(final int position) {
        Message msg = mHandler.obtainMessage(MSG_ITEM_CHANGE);
        msg.arg1 = position;
        mHandler.sendMessage(msg);
    }

    @Override
    public void btDisconnected() {
        Log.e(TAG,"btDisconnected 蓝牙断开！！");
        Message msg = mHandler.obtainMessage(MSG_SHOW_TOAST);
        msg.obj = "蓝牙断开！！！";
        mHandler.sendMessage(msg);
        pop();
    }

    @Override
    public void showToast(final String msg) {
        Message hmsg = mHandler.obtainMessage(MSG_SHOW_TOAST);
        hmsg.obj = msg;
        mHandler.sendMessage(hmsg);
    }

    @Override
    public void startWifiConfig() {

    }

    @Override
    public void startActionTest() {

    }

    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    mPresenter.disconnectBT();
                    pop();
                } else{
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            mPresenter.reInitBT();
            mPresenter.initBTListener();
        }
    }
}
