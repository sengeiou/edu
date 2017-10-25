package com.ubt.alpha1e.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.MessageRecordManager;
import com.ubt.alpha1e.data.model.MessageInfo;
import com.ubt.alpha1e.ui.fragment.MessageFragment;
import com.ubt.alpha1e.ui.fragment.NoticeFragment;
import com.ubt.alpha1e.ui.helper.MessageHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MessageActivity extends BaseActivity{

    private static final String TAG = "MessageActivity";

    private TextView messageView = null;
    private TextView noticeView = null;
    private LinearLayout lay_message_title;
    private LinearLayout lay_notice_title;
    private RelativeLayout lay_no_read_num;
    private TextView noReadNum;

    private static final int MESSAGE_TYPE_NOTICE = 1;
    private static final int MESSAGE_TYPE_MSG = 2;
    private static final int SHOW_NO_READ_MESSAGE = 100;
    private int noReadMsgNum = 0;
    private List<MessageInfo> noticeList = new ArrayList();

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private MessageFragment messageFragment;
    public Fragment mCurrentFragment;
    private LinkedHashMap<Integer, Fragment> mFragmentCache = new LinkedHashMap<>();

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NO_READ_MESSAGE:
                    UbtLog.d(TAG,"noReadMsgNum = " + noReadMsgNum);
                    if(noReadMsgNum > 0){
                        lay_no_read_num.setVisibility(View.VISIBLE);
                        noReadNum.setText(noReadMsgNum+"");
                    }else {
                        lay_no_read_num.setVisibility(View.GONE);
                        noReadNum.setText("");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);
        mFragmentManager = this.getFragmentManager();

        initUI();
        initControlListener();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initUI() {


        initTitle(getStringResources("ui_home_message_title"));

        noReadMsgNum = 0;
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        messageFragment = new MessageFragment(this,(MessageHelper)mHelper);
        mFragmentTransaction.add(R.id.lay_page, messageFragment);
        mFragmentTransaction.commit();
        mCurrentFragment = messageFragment;
        mFragmentCache.put(MESSAGE_TYPE_MSG,messageFragment);

        lay_message_title = (LinearLayout) findViewById(R.id.lay_message_title);
        lay_notice_title = (LinearLayout) findViewById(R.id.lay_notice_title);
        lay_no_read_num = (RelativeLayout) findViewById(R.id.lay_no_read_num);

        messageView = (TextView) findViewById(R.id.txt_base_title_name);
        noticeView = (TextView) findViewById(R.id.txt_base_notice_title_name);
        noReadNum = (TextView) findViewById(R.id.txt_no_read_num);
    }

    @Override
    protected void initControlListener() {

        lay_message_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment f = mFragmentCache.containsKey(MESSAGE_TYPE_MSG) ? mFragmentCache.get(MESSAGE_TYPE_MSG)
                        : new MessageFragment(MessageActivity.this,(MessageHelper)mHelper);
                if (!mFragmentCache.containsKey(MESSAGE_TYPE_MSG)) {
                    mFragmentCache.put(MESSAGE_TYPE_MSG, f);
                }
                if(!(mCurrentFragment instanceof MessageFragment)){
                    loadFragment(f);
                }

                lay_message_title.setBackground(getDrawableRes("message_title_select_bg_ft"));
                lay_notice_title.setBackground(getDrawableRes("notice_title_unselect_bg_ft"));

                messageView.setTextColor(getColorRes("message_title_text_select_color_ft"));
                noticeView.setTextColor(getColorRes("message_title_text_unselect_color_ft"));

                lay_message_title.setPadding(0,0,0,0);
                lay_notice_title.setPadding(0,0,0,0);
            }
        });

        lay_notice_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = mFragmentCache.containsKey(MESSAGE_TYPE_NOTICE) ? mFragmentCache.get(MESSAGE_TYPE_NOTICE)
                        : new NoticeFragment(MessageActivity.this,noticeList);
                if (!mFragmentCache.containsKey(MESSAGE_TYPE_NOTICE)) {
                    mFragmentCache.put(MESSAGE_TYPE_NOTICE, f);
                }

                if(!(mCurrentFragment instanceof NoticeFragment)){
                    loadFragment(f);
                }

                lay_message_title.setBackground(getDrawableRes("message_title_unselect_bg_ft"));
                lay_notice_title.setBackground(getDrawableRes("notice_title_select_bg_ft"));

                messageView.setTextColor(getColorRes("message_title_text_unselect_color_ft"));
                noticeView.setTextColor(getColorRes("message_title_text_select_color_ft"));

                lay_message_title.setPadding(0,0,0,0);
                lay_notice_title.setPadding(0,0,0,0);
            }
        });
    }

    /**
     * 刷新未读通知
     */
    public void onAddNoReadMessageSuccess(){
        noReadMsgNum-- ;
        mHandler.sendEmptyMessage(SHOW_NO_READ_MESSAGE);
    }

    /**
     * 刷新未读通知
     * @param ids
     */
    public void refreshNoReadData(List<Long> ids){
        UbtLog.d(TAG,"refreshNoReadData = " + ids);
        for(MessageInfo messageInfo : noticeList){
            if(!ids.contains(messageInfo.messageId)){
                noReadMsgNum++ ;
            }
        }
        mHandler.sendEmptyMessage(SHOW_NO_READ_MESSAGE);
    }

    /**
     * 刷新收到的通知数据
     * @param isSuccess
     * @param messages
     * @param type
     * @param messageHelper
     */
    public void refreshMessageData(boolean isSuccess,List<MessageInfo> messages,int type,MessageHelper messageHelper){
        if(type == MessageRecordManager.TYPE_NOTICE && isSuccess){
            if(messages != null){
                noReadMsgNum = 0;
                noticeList.clear();
                noticeList.addAll(messages);
            }
        }
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    private void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        UbtLog.d(TAG,"targetFragment = " + targetFragment);
        if (!targetFragment.isAdded()) {
            mCurrentFragment.onStop();
            transaction
                    .hide(mCurrentFragment)
                    .add(R.id.lay_page, targetFragment)
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
}
