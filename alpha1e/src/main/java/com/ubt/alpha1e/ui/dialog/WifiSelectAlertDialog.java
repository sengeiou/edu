package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.WifiInfoAdapter;
import com.ubt.alpha1e.event.NetworkEvent;
import com.ubt.alpha1e.ui.helper.WifiHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络搜索选择类
 * Created by lihai on 04/04/17.
 */
public class WifiSelectAlertDialog {
    private static final String TAG = "WifiSelectAlertDialog";

    public static final int UPDATE_DATA = 1;
    public static final int SELECT_POSITION = 2;

    private Context mContext;
    private Dialog mDialog;
    private LinearLayout llBg;
    private TextView tvTitle;
    private TextView tvMsg;
    private Button btnNeg;
    private Button btnPos;
    private ImageView ivLine;
    public RelativeLayout rlNoWifi;

    public WifiHelper mWifiHelper = null;

    public RecyclerView mRecyclerview;
    public LinearLayoutManager mLayoutManager;
    public WifiInfoAdapter mAdapter;
    private String mCurrentSelectWifiName = null;
    private ScanResult mScanResult = null;

    // 扫描出的网络连接列表
    private List<ScanResult> mWifiListItem = new ArrayList<>();

    private Display mDisplay;
    private boolean mShowTitle = false;
    private boolean mShowMsg = false;
    private boolean mShowPosBtn = false;
    private boolean mShowNegBtn = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_DATA:
                    mAdapter.notifyDataSetChanged();
                    break;
                case SELECT_POSITION:
                    mScanResult = (ScanResult)msg.obj;
                    UbtLog.d(TAG,"mScanResult = " + mScanResult.SSID);

                    //将选择WIFI名称传输回上一页面
                    NetworkEvent mWifiEvent = new NetworkEvent(NetworkEvent.Event.CHANGE_SELECT_WIFI);
                    mWifiEvent.setSelectWifiName(mScanResult.SSID);
                    EventBus.getDefault().post(mWifiEvent);
                    mDialog.dismiss();
                    break;
                case WifiHelper.REFRESH_WIFI_DATA:
                    mWifiListItem.clear();
                    mWifiListItem.addAll((List<ScanResult>) msg.obj);
                    mAdapter.notifyDataSetChanged();

                    if(mWifiListItem.isEmpty()){
                        mRecyclerview.setVisibility(View.GONE);
                        rlNoWifi.setVisibility(View.VISIBLE);
                    }else {
                        mRecyclerview.setVisibility(View.VISIBLE);
                        rlNoWifi.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 类构造函数
     * @param context 上下文
     */
    public WifiSelectAlertDialog(Context context) {
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();

        mWifiHelper = new WifiHelper(context,mHandler);
    }

    public WifiSelectAlertDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.view_wifi_alertdialog, null);

        initBaseView(view);
        initRecyclerViews(view);

        // 定义Dialog布局和参数
        mDialog = new Dialog(mContext, R.style.AlertDialogStyle);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        mDialog.setContentView(view);
        mDialog.setOnDismissListener(mOnDismissListener);

        // 调整dialog背景大小
        llBg.setLayoutParams(new FrameLayout.LayoutParams((int) (mDisplay
                .getWidth() * 0.7), (int)(mDisplay.getHeight() * 0.7)));


////        WindowManager m = getWindowManager();
////        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//        p.height = (int) (mDisplay.getHeight() * 0.5); // 高度设置为屏幕的0.6
//        p.width = (int) (mDisplay.getWidth() * 0.7); // 宽度设置为屏幕的0.65
//        dialogWindow.setAttributes(p);


        mWifiHelper.doStartScan();
        return this;
    }

    /**
     * 初始基本控件
     * @param view
     */
    public void initBaseView(View view){

        // 获取自定义Dialog布局中的控件
        rlNoWifi = (RelativeLayout) view.findViewById(R.id.rl_no_wifi);
        llBg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        tvTitle = (TextView) view.findViewById(R.id.txt_title);
        tvTitle.setVisibility(View.GONE);
        tvMsg = (TextView) view.findViewById(R.id.txt_msg);
        tvMsg.setVisibility(View.GONE);
        btnNeg = (Button) view.findViewById(R.id.btn_neg);
        btnNeg.setVisibility(View.GONE);
        btnPos = (Button) view.findViewById(R.id.btn_pos);
        btnPos.setVisibility(View.GONE);
        ivLine = (ImageView) view.findViewById(R.id.img_line);
        ivLine.setVisibility(View.GONE);
    }

    /**
     * 初始化RecyclerView控件
     * @param view
     */
    public void initRecyclerViews(View view) {

        mRecyclerview = (RecyclerView) view.findViewById(R.id.recyclerview_wifi);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator animator = mRecyclerview.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
//        mRecyclerview.getItemAnimator().setSupportsChangeAnimations(false);
        mAdapter = new WifiInfoAdapter(mContext,mWifiListItem,mHandler,mCurrentSelectWifiName);
        mRecyclerview.setAdapter(mAdapter);
    }

    /**
     * 注册对话框消失监听器
     */
    public DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener(){

        @Override
        public void onDismiss(DialogInterface dialog) {
            mWifiHelper.onDestroy();
        }
    };

    /**
     * 设置对话框标题
     * @param title
     * @return
     */
    public WifiSelectAlertDialog setTitle(String title) {
        mShowTitle = true;
        if ("".equals(title)) {
            tvTitle.setText("标题");
        } else {
            tvTitle.setText(title);
        }
        return this;
    }

    /**
     * 设置主显示内容
     * @param msg
     * @return
     */
    public WifiSelectAlertDialog setMsg(String msg) {
        mShowMsg = true;
        if ("".equals(msg)) {
            tvMsg.setText("内容");
        } else {
            tvMsg.setText(msg);
        }
        return this;
    }

    /**
     * 设置是否点击周围可以取消
     * @param cancel
     * @return
     */
    public WifiSelectAlertDialog setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    /**
     * 设置确定按钮监听器
     * @param text 按钮文本
     * @param listener 按键监听器
     * @return
     */
    public WifiSelectAlertDialog setPositiveButton(String text,
                                                   final View.OnClickListener listener) {
        mShowPosBtn = true;
        if ("".equals(text)) {
            btnPos.setText("确定");
        } else {
            btnPos.setText(text);
        }
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                mDialog.dismiss();
            }
        });
        return this;
    }

    /**
     * 设置取消按钮监听器
     * @param text 按钮文本
     * @param listener 按键监听器
     * @return
     */
    public WifiSelectAlertDialog setNegativeButton(String text,
                                                   final View.OnClickListener listener) {
        mShowNegBtn = true;
        if ("".equals(text)) {
            btnNeg.setText("取消");
        } else {
            btnNeg.setText(text);
        }
        btnNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                mDialog.dismiss();
            }
        });
        return this;
    }

    /**
     * 初始化当前选中的wifi
     * @param wifiName
     */
    public WifiSelectAlertDialog setmCurrentSelectWifiName(String wifiName){
        this.mCurrentSelectWifiName = wifiName;
        return this;
    }

    /**
     * 根据不同状态，显示页面
     */
    private void setLayout() {
        if (!mShowTitle && !mShowMsg) {
            tvTitle.setText("提示");
            tvTitle.setVisibility(View.VISIBLE);
        }

        if (mShowTitle) {
            tvTitle.setVisibility(View.VISIBLE);
        }

        if (mShowMsg) {
            tvMsg.setVisibility(View.VISIBLE);
        }

        if (!mShowPosBtn && !mShowNegBtn) {
            btnPos.setText("确定");
            btnPos.setVisibility(View.VISIBLE);
            btnPos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btnPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
        }

        if (mShowPosBtn && mShowNegBtn) {
            btnPos.setVisibility(View.VISIBLE);
            btnPos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btnNeg.setVisibility(View.VISIBLE);
            btnNeg.setBackgroundResource(R.drawable.alertdialog_left_selector);
            ivLine.setVisibility(View.VISIBLE);
        }

        if (mShowPosBtn && !mShowNegBtn) {
            btnPos.setVisibility(View.VISIBLE);
            btnPos.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        if (!mShowPosBtn && mShowNegBtn) {
            btnNeg.setVisibility(View.VISIBLE);
            btnNeg.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
    }

    public void show() {
        setLayout();
        mDialog.show();
    }
}
