package com.ubt.alpha1e.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.dialog.WifiSelectAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/5/13.
 */
public class WifiInfoAdapter_list extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "WifiInfoAdapter";

    private Context mContext;
    public List<ScanResult> mDatas = new ArrayList<>();
    private View mView;
    private Handler mHandler = null;
    private String mCurrentSelectWifiName = null;

    /**
     * 类构造函数
     * @param mContext 上下文
     * @param list 数据列表
     * @param handler handler
     * @param wifiName 当前Wifi名称
     */
    public WifiInfoAdapter_list(Context mContext, List<ScanResult> list, Handler handler, String wifiName) {
        super();
        this.mContext = mContext;
        this.mDatas = list;
        this.mHandler = handler;
        this.mCurrentSelectWifiName = wifiName;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final WifiInfoAdapter_list.MyWifiHolder myHolder  = (WifiInfoAdapter_list.MyWifiHolder) holder;
        final ScanResult wifiInfo = mDatas.get(position);

        myHolder.tvWifiName.setText(wifiInfo.SSID);

        //处理是否有密码
        if(wifiInfo.capabilities.contains("WPA") || wifiInfo.capabilities.contains("WEP")){
            myHolder.ivWifiPwd.setVisibility(View.VISIBLE);
        }else {
            myHolder.ivWifiPwd.setVisibility(View.GONE);
        }

        //处理是否有当前选择
        if(wifiInfo.SSID.equals(mCurrentSelectWifiName)){
            myHolder.ivWifiSelect.setVisibility(View.VISIBLE);
        }else {
            myHolder.ivWifiSelect.setVisibility(View.INVISIBLE);
        }

        //处理WIFI信号图片
        int strength = WifiManager.calculateSignalLevel(mDatas.get(position).level, 4);
        myHolder.ivWifiLogo.setImageLevel(strength);
        myHolder.ivWifiLogo.setImageResource(R.drawable.wifi_signal);

        myHolder.rlWifiInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = WifiSelectAlertDialog.SELECT_POSITION;
                msg.obj = wifiInfo;
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_wifi_item_list, parent, false);
        MyWifiHolder myWifiHolder = new MyWifiHolder(mView);
        return myWifiHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class MyWifiHolder extends RecyclerView.ViewHolder
    {
        public ImageView ivWifiLogo,ivWifiPwd,ivWifiSelect;
        public RelativeLayout rlWifiInfo;
        public TextView tvWifiName;
        public Animation mOperatingAnim;
        public MyWifiHolder(View view)
        {
            super(view);
            ivWifiLogo = (ImageView) view.findViewById(R.id.img_wifi_logo);
            ivWifiPwd = (ImageView) view.findViewById(R.id.img_wifi_pwd);
            ivWifiSelect = (ImageView) view.findViewById(R.id.img_wifi_select);
            rlWifiInfo  = (RelativeLayout) view.findViewById(R.id.lay_wifi_info);
            tvWifiName = (TextView) view.findViewById(R.id.txt_wifi_name);
        }
    }
}
