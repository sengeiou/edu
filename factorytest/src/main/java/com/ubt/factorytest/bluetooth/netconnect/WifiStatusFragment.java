package com.ubt.factorytest.bluetooth.netconnect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubt.factorytest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/15 12:57
 * @描述:
 */

public class WifiStatusFragment extends SupportFragment {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.tv_wifi_ip)
    TextView tvWifiIp;
    Unbinder unbinder;


    String wifiName = "";
    String wifiIP = "";


    public static WifiStatusFragment newInstance(String name, String ip) {
        Bundle args = new Bundle();
        args.putString("wifiName", name);
        args.putString("wifiIP",ip);
        WifiStatusFragment fragment = new WifiStatusFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_status, container, false);
        unbinder = ButterKnife.bind(this, view);
        initToolbarNav(toolbar);
        Bundle data = getArguments();
        wifiName = data.getString("wifiName");
        wifiIP = data.getString("wifiIP");
        String str = "wifiName:    "+wifiName;
        tvWifiName.setText(str);
        str = "wifiIP:    "+wifiIP;
        tvWifiIp.setText(str);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1) {
//                    mPresenter.disconnectBT();
                    pop();
                } else {
                    getActivity().finish();
                }
            }
        });
    }
}
