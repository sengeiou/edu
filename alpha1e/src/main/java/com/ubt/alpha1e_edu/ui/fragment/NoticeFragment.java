package com.ubt.alpha1e_edu.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.business.MessageRecordManager;
import com.ubt.alpha1e_edu.data.model.ActionInfo;
import com.ubt.alpha1e_edu.data.model.MessageInfo;
import com.ubt.alpha1e_edu.library.ptr.PtrClassicFrameLayout;
import com.ubt.alpha1e_edu.library.ptr.PtrFrameLayout;
import com.ubt.alpha1e_edu.library.ptr.PtrHandler;
import com.ubt.alpha1e_edu.ui.ActionsLibPreviewWebActivity;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.ui.MessageActivity;
import com.ubt.alpha1e_edu.ui.WebContentActivity;
import com.ubt.alpha1e_edu.ui.helper.ActionsLibHelper;
import com.ubt.alpha1e_edu.ui.helper.IMessageUI;
import com.ubt.alpha1e_edu.ui.helper.MessageHelper;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * NoticeFragment
 */
public class NoticeFragment extends BaseFragment implements IMessageUI {
    private static final String TAG = "NoticeFragment";
    private View mMainView;
    public static final int UPDATE_ITEMS = 0;
    public static final int UPDATE_VIEW = 1;

    private PtrClassicFrameLayout ptrClassicFrameLayout;
    public MessageActivity mActivity;
    private MessageHelper mMessageHelper = null;

    private List<MessageInfo> mMessages;
    private ListView lst_messages;
    private SimpleAdapter lst_messages_adapter;
    private List<Map<String, Object>> lst_messages_datas;
    private ImageView emptyView;
    private boolean isQuestRefresh = false;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_ITEMS:
                    break;

                case UPDATE_VIEW:
                    if(mMessages.size() > 0){
                        emptyView.setVisibility(View.GONE);
                    }else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    public NoticeFragment() {

    }

    @SuppressLint("ValidFragment")
    public NoticeFragment(BaseActivity mBaseActivity, List<MessageInfo> noticeList) {
        UbtLog.d(TAG, "--NoticeFragment--");
        mActivity = (MessageActivity) mBaseActivity;
        mMessageHelper = new MessageHelper(mBaseActivity,this);
        mMessages = noticeList;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (MessageActivity) getActivity();
        mMainView = inflater.inflate(R.layout.fragment_notice, null);

        initUI();
        initControlListener();
        requestData();

        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMessages != null && mMessages.size() > 0) {
            lst_messages_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub

        ptrClassicFrameLayout = (PtrClassicFrameLayout) mMainView.findViewById(R.id.pull_to_refresh);
        ptrClassicFrameLayout.setKeepHeaderWhenRefresh(true);
        ptrClassicFrameLayout.disableWhenHorizontalMove(true);
        ptrClassicFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //不可以下拉刷新，最多显示10条
                return false;
                //return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();//5000ms超时
                    }
                },5000);
                //isQuestRefresh = true;
                //requestData();
            }
        });

        lst_messages = (ListView) mMainView.findViewById(R.id.lst_messages);
        lst_messages_datas = new ArrayList<Map<String, Object>>();
        int id = R.layout.layout_message;

        lst_messages_adapter = new SimpleAdapter(mActivity, lst_messages_datas, id,
                new String[]{MessageHelper.MAP_KEY_MSG_DISC,
                        MessageHelper.MAP_KEY_MSG_DATE,
                        MessageHelper.MAP_KEY_MSG_TITLE}, new int[]{
                R.id.txt_message_disc, R.id.txt_date,
                R.id.txt_message_title}) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View thiz = super.getView(position, convertView, parent);
                ImageView img_logo = (ImageView) thiz.findViewById(R.id.action_logo);
                if (!(Boolean) lst_messages_datas.get(position).get(
                        MessageHelper.MAP_KEY_IS_UNREAD)) {
                    thiz.findViewById(R.id.img_unread).setVisibility(View.GONE);
                    img_logo.setImageResource(R.drawable.main_message_readed);
                } else {
                    thiz.findViewById(R.id.img_unread).setVisibility(View.GONE);
                    img_logo.setImageResource(R.drawable.main_message_unread);
                }

                return thiz;
            }
        };

        lst_messages.setAdapter(lst_messages_adapter);
        emptyView = (ImageView) mMainView.findViewById(R.id.empty_view);
    }

    /**
     * 请求数据
     */
    private void requestData(){
        UbtLog.d(TAG,"--requestData--");
        if(mMessageHelper != null){
            mMessageHelper.getNewMessages(MessageRecordManager.TYPE_NOTICE,1,10);
        }
    }

    public  void onRefreshComplete()
    {
        if(ptrClassicFrameLayout==null) {
            return;
        }

        ptrClassicFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                ptrClassicFrameLayout.refreshComplete();
            }
        });
    }

    public void sendMessage(Object object, int what) {
        Message msg = new Message();
        msg.obj = object;
        msg.what = what;
        if (mHandler != null){
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void initControlListener() {
        // TODO Auto-generated method stub
        lst_messages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
                final MessageInfo info = (MessageInfo) lst_messages_datas.get(
                        arg2).get(MessageHelper.MAP_KEY_MSG);
                UbtLog.d(TAG,"info.messageId = " + info.messageId);
                if (info.messageType == 1) {
                    // ��ͨ����
                    ActionInfo act = new ActionInfo();
                    try {
                        act.actionId = Long.parseLong(info.extra);
                        act.actionDesciber = "";
                        act.actionName = "";
                        int state = -1;
                        if (lst_messages_datas.get(arg2).get(ActionsLibHelper.map_val_action_download_state)
                                == ActionsLibHelper.Action_download_state.download_finish) {
                            state = 1;
                        } else if (lst_messages_datas.get(arg2).get(ActionsLibHelper.map_val_action_download_state)
                                == ActionsLibHelper.Action_download_state.not_download) {
                            state = 0;
                        } else{
                            state = 2;
                        }

                        ActionsLibPreviewWebActivity.launchActivity(mActivity, act, 0, -1);
                        mMessageHelper.doRecordMessage(info.messageId);
                        lst_messages_datas.get(arg2).put(MessageHelper.MAP_KEY_IS_UNREAD, false);

                    } catch (Exception e) {
                        showToast("ui_home_message_open_fail");
                    }

                } else if (info.messageType == 2) {
                    // url
                    Intent inte = new Intent();
                    inte.putExtra(WebContentActivity.WEB_TITLE, info.title);
                    inte.putExtra(WebContentActivity.WEB_URL, info.extra);
                    inte.putExtra(WebContentActivity.WEB_IS_SHARE, true);
                    inte.setClass(mActivity,
                            WebContentActivity.class);
                    mActivity.startActivity(inte);

                    mMessageHelper.doRecordMessage(info.messageId);

                    lst_messages_datas.get(arg2).put(MessageHelper.MAP_KEY_IS_UNREAD, false);
                }

            }
        });
    }

    public void showToast(String str) {
        Toast.makeText(mActivity.getApplicationContext(), mActivity.getStringResources(str), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub
    }


    @Override
    public void onAddNoReadMessage() {
        ((MessageActivity)getActivity()).onAddNoReadMessageSuccess();
    }

    @Override
    public void onReadUnReadRecords(List<Long> ids) {

        if(ids != null && ids.size() > 0){
            for(int i = 0;i < lst_messages_datas.size();i++){
                MessageInfo info = (MessageInfo) lst_messages_datas.get(i).get(MessageHelper.MAP_KEY_MSG);
                if(ids.contains(info.messageId)){
                    lst_messages_datas.get(i).put(MessageHelper.MAP_KEY_IS_UNREAD, false);
                }else {
                    lst_messages_datas.get(i).put(MessageHelper.MAP_KEY_IS_UNREAD, true);
                }
            }
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                lst_messages_adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onGetNewMessages(boolean isSuccess, final String errorInfo, List<MessageInfo> messages,int type) {

        if (isSuccess) {
            dealData(messages);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, errorInfo, Toast.LENGTH_SHORT).show();
                }
            });
        }

        onRefreshComplete();
    }

    /**
     * 处理收到的数据
     * @param messages
     */
    public void dealData(List<MessageInfo> messages){
        mMessages = messages;
        setListData();
        List<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < mMessages.size(); i++) {
            ids.add(mMessages.get(i).messageId);
        }
        mHandler.sendEmptyMessage(UPDATE_VIEW);
    }

    private void setListData() {
        if (mMessages == null || mMessages.size() == 0){
            return;
        }

        if(isQuestRefresh){
            lst_messages_datas.clear();
            isQuestRefresh = false;
        }

        for (int i = 0; i < mMessages.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(MessageHelper.MAP_KEY_MSG, mMessages.get(i));
            item.put(MessageHelper.MAP_KEY_MSG_DATE,getShortTime(mMessages.get(i).pushTime));
            item.put(MessageHelper.MAP_KEY_MSG_TITLE, mMessages.get(i).title);

            item.put(MessageHelper.MAP_KEY_MSG_DISC,mMessages.get(i).messageContent);
            item.put(MessageHelper.MAP_KEY_IS_UNREAD, true);
            UbtLog.d(TAG,"mMessages = " + mMessages.get(i).title + "   " + mMessages.get(i).messageContent);
            lst_messages_datas.add(item);
        }


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                lst_messages_adapter.notifyDataSetChanged();
            }
        });

    }

    private String getShortTime(String str) {
        String result = str;
        try {
            result = result.split(" ")[0];
            result = result.substring(result.indexOf("-") + 1);
            return result;
        } catch (Exception e) {
            result = str;
        }
        return result;
    }
}
