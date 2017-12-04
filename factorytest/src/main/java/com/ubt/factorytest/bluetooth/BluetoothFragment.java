package com.ubt.factorytest.bluetooth;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ubt.factorytest.R;
import com.ubt.factorytest.bluetooth.recycleview.BTAdapter;
import com.ubt.factorytest.test.TestFragment;
import com.ubt.factorytest.test.TestPresenter;
import com.ubt.factorytest.test.data.DataServer;
import com.ubt.factorytest.utils.ContextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;


/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/21 09:45
 * @描述:
 */

public class BluetoothFragment extends SupportFragment implements BluetoothContract.View {
    private static final String TAG = "BTBluetoothFragment";

    private static final int REQ_CODE = 100;

    private static final int MSG_IS_CONNECTING = 1;
    private static final int MSG_IS_CONNECT_OK = 2;
    private static final int MSG_IS_PROCESS_ON = 3;

    @BindView(R.id.pg_bar)
    ProgressBar mPgBar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_bluetooth)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private BluetoothContract.Presenter mPresenter;

    private BTAdapter mAdapter;
    private Context mContext;

    private Handler mHandler;
    private String mConnectedMAC;
    private String mBTRSSI;

    public static BluetoothFragment newInstance() {

        Bundle args = new Bundle();

        BluetoothFragment fragment = new BluetoothFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setPresenter(BluetoothContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        mContext = ContextUtils.getContext();
        unbinder = ButterKnife.bind(this, view);
        mToolbar.setTitle(R.string.bt_title);
        mToolbar.inflateMenu(R.menu.menu_bluetooth);
        initRecyclerView();
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_scan) {
                    startStopScan();
                }
                return true;
            }
        });

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG_IS_CONNECTING:
                        mToolbar.setTitle(R.string.bt_connecting);
                        break;
                    case MSG_IS_CONNECT_OK:
                        boolean isOK = msg.getData().getBoolean("isConnectOK");
                        if(isOK){
                            TestFragment fragment = TestFragment.newInstance(mConnectedMAC, mBTRSSI);
                            new TestPresenter(fragment, new DataServer(mContext));
                            startForResult(fragment, REQ_CODE);
                            mToolbar.setTitle(R.string.bt_title);
                            mPresenter.cleanBTList();
                            notifyDataSetChanged();
                        }else{
                            mToolbar.setTitle(R.string.bt_connectFail);
                        }

                        break;
                    case MSG_IS_PROCESS_ON:
                        boolean isOn = msg.getData().getBoolean("isProcessOn");
                        if(isOn){
                            mPgBar.setVisibility(View.VISIBLE);
                        }else{
                            mPgBar.setVisibility(View.GONE);
                        }
                        break;
                }

            }
        };
        return view;
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        initBTViewAdapter();
    }

    private void initBTViewAdapter() {
        mAdapter = new BTAdapter(mPresenter.getBTList());
        mAdapter.openLoadAnimation(BTAdapter.SCALEIN);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.d(TAG,"onItemClick position="+position );
                mConnectedMAC = mAdapter.getItem(position).getBtMac();
                mBTRSSI = mAdapter.getItem(position).getBtRSSI();
                mPresenter.connectBT(mConnectedMAC);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void startStopScan() {
        if (!mPresenter.isBTScaning()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                int permission = ActivityCompat.checkSelfPermission(
                        mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }

            if (!mPresenter.isBTEnabled()) {
                mPresenter.openBluetooth();
                return;
            }

            mPresenter.cleanBTList();
            notifyDataSetChanged();
            mPresenter.bStartBTScan();
        } else {
            mPresenter.bCancelBTScan();
        }
    }

    @Override
    public void setProcessBarOn(final boolean isOn) {
        Bundle data = new Bundle();
        data.putBoolean("isProcessOn",isOn);
        Message msg = new Message();
        msg.what = MSG_IS_PROCESS_ON;
        msg.setData(data);
        mHandler.sendMessage(msg);
    }

    @Override
    public void setScanMenuTitle(String title) {
        mToolbar.getMenu().findItem(R.id.action_scan).setTitle(title);
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void btConnectting() {
        mHandler.sendEmptyMessage(MSG_IS_CONNECTING);
    }

    @Override
    public void isBTConnectOK(boolean isOK) {
        Bundle data = new Bundle();
        data.putBoolean("isConnectOK",isOK);
        Message msg = new Message();
        msg.what = MSG_IS_CONNECT_OK;
        msg.setData(data);
        mHandler.sendMessage(msg);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.reInitBT();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        Log.d(TAG,"onFragmentResult  requestCode="+requestCode);
        if(requestCode == requestCode){
            if(mToolbar != null) {
                mToolbar.setTitle(R.string.bt_title);
            }
            mPresenter.reInitBT();
            mPresenter.initBTListener();
        }
    }
}
