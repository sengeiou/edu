package com.ubt.alpha1e.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.NewActionPlayer.PlayerState;
import com.ubt.alpha1e.data.FileTools.State;
import com.ubt.alpha1e.data.model.AlphaStatics;
import com.ubt.alpha1e.data.model.FrameActionInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.ui.custom.IntroductionUIOpreater;
import com.ubt.alpha1e.ui.dialog.IMessageListeter;
import com.ubt.alpha1e.ui.dialog.IntroductionUIListener;
import com.ubt.alpha1e.ui.dialog.MyAlertDialog;
import com.ubt.alpha1e.ui.fragment.SaveSuccessFragment;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper.Command_type;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper.StartType;
import com.ubt.alpha1e.ui.helper.IEditActionUI;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionsEditActivity extends BaseActivity implements IEditActionUI,
        IntroductionUIListener,SaveSuccessFragment.OnFragmentInteractionListener {

    private static final String TAG = "ActionsEditActivity";
    public StartType mCurrentStartType;
    public NewActionInfo mCurrentNewAction;

    private ListView lst_actions;
    private SimpleAdapter lst_actions_adapter;
    private List<Map<String, Object>> lst_actions_adapter_data;
    private boolean is_first_add_frame = false;
    private boolean can_play = false;
    private int index_playing = -1;
    private int index_first_visible = 0;
    private int index_last_visible = 0;
    private View lay_add_frame;
    private Button btn_add_frame;
    private Button btn_cancel_add;
    private Button btn_play;
    private Button btn_help;
    // -------------------------------------------------
    private RelativeLayout lay_frame_data_edit;
    private LinearLayout lay_play;
    private TextView txt_edit_frame_name;
    private TextView txt_edit_frame_time;
    private SeekBar skb_time;
    private Button btn_low_time;
    private Button btn_add_time;
    private TextView txt_frame_time;
    private Map<String, Object> mCurremtEditItem;
    private boolean isDoCopy = false;
    private int past_index;
    private int delete_index;
    private Button btn_preview;
    private ImageView img_preview;
    private boolean isChange = false;
    private Button btn_change;
    private ImageView img_change;
    private Button btn_delete;
    private ImageView img_delete;
    private Button btn_past;
    private ImageView img_past;
    private Button btn_copy;
    private ImageView img_copy;
    private static Date lastTime_play = null;
    // -------------------------------------------------
    private RelativeLayout lay_change_frame;
    private Button btn_change_cancel;
    private Button btn_change_ok;
    private TextView txt_base_save;
    // -------------------------------------------------
    private SaveSuccessFragment saveSuccessFragment;
    private boolean isSaveSuccess = false;
    public static String SCHEME_ID = "SCHEME_ID";
    public static String SCHEME_NAME = "SCHEME_NAME";
    private String mSchemeId = "";
    private String mSchemeName = "";
    private int defaultMinTime = 200;
    private RelativeLayout layTitle = null;
    private boolean isPasteReFresh = false;
    private boolean isDeleteReFresh = false;

    public static void launchActivity(Activity context, StartType type, android.os.Parcelable parcelable,int requestCode,String schemeId, String schemeName)
    {

        Intent intent = new Intent();
        intent.putExtra(SCHEME_ID,schemeId);
        intent.putExtra(SCHEME_NAME,schemeName);
        intent.putExtra(ActionsEditHelper.StartTypeStr,type);
        intent.putExtra(ActionsEditHelper.NewActionInfo,parcelable);
        intent.setClass(context,ActionsEditActivity.class);
        context.startActivityForResult(intent,requestCode);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_edit);
        mCurrentStartType = (StartType) this.getIntent().getExtras()
                .get(ActionsEditHelper.StartTypeStr);
        mSchemeId = getIntent().getStringExtra(SCHEME_ID);
        mSchemeName = getIntent().getStringExtra(SCHEME_NAME);

        mHelper = new ActionsEditHelper(this, this);

        if (((ActionsEditHelper) mHelper).isFirstEditMain()) {

            IntroductionUIOpreater intro = new IntroductionUIOpreater(
                    (RelativeLayout) this.findViewById(R.id.lay_main), this,
                    this);

            int[] intro_ress = null;

            if (getAppCurrentLanguage()
                    .contains("zh")) {
                intro_ress = new int[]{R.drawable.sec_new_actions_intro_1_cn,
                        R.drawable.sec_new_actions_intro_2_cn,
                        R.drawable.sec_new_actions_intro_3_cn,
                        R.drawable.sec_new_actions_intro_4_cn,
                        R.drawable.sec_new_actions_intro_5_cn};
            } else {
                intro_ress = new int[]{R.drawable.sec_new_actions_intro_1,
                        R.drawable.sec_new_actions_intro_2,
                        R.drawable.sec_new_actions_intro_3,
                        R.drawable.sec_new_actions_intro_4,
                        R.drawable.sec_new_actions_intro_5};
            }

            intro.setIntroductionViews(R.layout.layout_introduction_edit,
                    R.id.img_bg_main, intro_ress, new int[][]{new int[]{},
                            new int[]{}, new int[]{}, new int[]{},
                            new int[]{}});

            intro.showIntroductions();

            ((ActionsEditHelper) mHelper).changeFirstUseEditState();
        }

        initUI();
        initControlListener();

    }

    @Override
    protected void initUI() {
        chanegViewLen(findViewById(R.id.vew_time_long), 1000f / 3000f);
        initTitle(getStringResources("ui_home_create_action"));
        setTitleBack(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                doBack();
            }
        });
        layTitle = (RelativeLayout)findViewById(R.id.lay_title);
        txt_base_save = (TextView) findViewById(R.id.txt_base_save);
        btn_help = (Button) findViewById(R.id.btn_help);
        lst_actions = (ListView) findViewById(R.id.lst_actions);
        lst_actions_adapter_data = new ArrayList<Map<String, Object>>();
        lst_actions_adapter = new SimpleAdapter(this, lst_actions_adapter_data,
                R.layout.layout_new_action_item, new String[]{
                ActionsEditHelper.MAP_FRAME_NAME,
                ActionsEditHelper.MAP_FRAME_TIME},
                new int[]{R.id.txt_action_frame_name,
                        R.id.txt_action_frame_time}) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View root_view = super.getView(position, convertView, parent);
                FrameActionInfo info = ((FrameActionInfo) lst_actions_adapter_data
                        .get(position).get(ActionsEditHelper.MAP_FRAME));
                // ----------------------------------------------------
                int show_time = -1;
                try {

                    show_time = (Integer) lst_actions_adapter_data.get(position).get(ActionsEditHelper.MAP_FRAME_SHOW_TIME);
                } catch (Exception e) {
                    show_time = -1;
                }

                //在中间粘贴，要更新后面的item,中间删除，后面的要更新
                if (show_time != info.totle_time
                        || (isPasteReFresh && position > past_index)
                        || (isDeleteReFresh && position >= delete_index)) {
                    chanegViewLen(root_view.findViewById(R.id.vew_time_long),
                            (info.totle_time + 0f) / 3000f);
                    lst_actions_adapter_data.get(position).put(
                            ActionsEditHelper.MAP_FRAME_SHOW_TIME,
                            info.totle_time);
                }

                // ----------------------------------------------------
                if (index_playing == position
                        && index_playing >= index_first_visible
                        && index_playing <= index_last_visible) {
                    index_playing = -1;
                    chanegPlayViewLen(
                            root_view.findViewById(R.id.vew_play_progress),
                            (info.totle_time + 0f));
                }

                if((position + 1) == getCount()){
                    isPasteReFresh = false;
                    isDeleteReFresh = false;
                }

                return root_view;
            }

        };
        lay_add_frame = View.inflate(this, R.layout.layout_new_frame, null);
        lst_actions.addFooterView(lay_add_frame);
        lst_actions.setAdapter(lst_actions_adapter);

        btn_add_frame = (Button) lay_add_frame.findViewById(R.id.btn_add_frame);
        btn_cancel_add = (Button) lay_add_frame
                .findViewById(R.id.btn_cancel_add);

        lay_play = (LinearLayout) findViewById(R.id.lay_play);
        btn_play = (Button) findViewById(R.id.btn_play);

        lay_frame_data_edit = (RelativeLayout) findViewById(R.id.lay_frame_data_edit);
        txt_edit_frame_name = (TextView) findViewById(R.id.txt_edit_frame_name);
        txt_edit_frame_time = (TextView) findViewById(R.id.txt_edit_frame_time);
        skb_time = (SeekBar) findViewById(R.id.skb_time);
        btn_low_time = (Button) findViewById(R.id.btn_low_time);
        btn_add_time = (Button) findViewById(R.id.btn_add_time);
        txt_frame_time = (TextView) findViewById(R.id.txt_frame_time);

        lay_change_frame = (RelativeLayout) findViewById(R.id.lay_change_frame);
        btn_change_cancel = (Button) findViewById(R.id.btn_change_cancel);
        btn_change_ok = (Button) findViewById(R.id.btn_change_ok);

        btn_preview = (Button) findViewById(R.id.btn_preview);
        img_preview = (ImageView) findViewById(R.id.img_preview);
        btn_change = (Button) findViewById(R.id.btn_change);
        img_change = (ImageView) findViewById(R.id.img_change);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        img_delete = (ImageView) findViewById(R.id.img_delete);
        btn_past = (Button) findViewById(R.id.btn_past);
        img_past = (ImageView) findViewById(R.id.img_past);
        btn_copy = (Button) findViewById(R.id.btn_copy);
        img_copy = (ImageView) findViewById(R.id.img_copy);

        if (mCurrentStartType == StartType.new_type) {
            mCurrentNewAction = new NewActionInfo();
        } else {
            mCurrentNewAction = this.getIntent().getParcelableExtra(ActionsEditHelper.NewActionInfo);
            // 解析帧数据
            releaseFrameDatas();
            lst_actions_adapter.notifyDataSetChanged();
        }

    }


    private void releaseFrameDatas() {

        int frame_size = -1;
        try {
            frame_size = mCurrentNewAction.frameActions.size();
        } catch (Exception e) {
            frame_size = -1;
        }
        if (frame_size == -1) {
            return;
        }
        for (int i = 0; i < frame_size; i++) {

            FrameActionInfo info = mCurrentNewAction.frameActions.get(i);

            Map map = new HashMap<String, Object>();
            map.put(ActionsEditHelper.MAP_FRAME, info);
            /*map.put(ActionsEditHelper.MAP_FRAME_NAME, this.getResources()
                    .getString(R.string.ui_readback_index)
                    + (lst_actions_adapter_data.size() + 1));*/
            map.put(ActionsEditHelper.MAP_FRAME_NAME, (lst_actions_adapter_data.size() + 1));
            map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time + "ms");

            lst_actions_adapter_data.add(map);
        }

    }

    public void showEditLay(int index) {

        Map<String, Object> _item = lst_actions_adapter_data.get(index);
        if (!isDoCopy) {
            mCurremtEditItem = _item;
            delete_index = index;
            img_past.setBackgroundResource(R.drawable.sec_paste_not_select_icon);
            btn_past.setClickable(false);
            btn_preview.setClickable(true);
            img_preview
                    .setBackgroundResource(R.drawable.sec_preview_select_icon);
            btn_change.setClickable(true);
            img_change.setBackgroundResource(R.drawable.sec_afresh_select_icon);
            btn_delete.setClickable(true);
            img_delete.setBackgroundResource(R.drawable.sec_delete_select_icon);
            btn_copy.setClickable(true);
            img_copy.setBackgroundResource(R.drawable.sec_copy_select_icon);
            skb_time.setEnabled(true);
        } else {
            isDoCopy = false;
            past_index = index;
            img_past.setBackgroundResource(R.drawable.sec_paste_select_icon);
            btn_past.setClickable(true);
            btn_preview.setClickable(false);
            img_preview
                    .setBackgroundResource(R.drawable.sec_preview_not_select_icon);
            btn_change.setClickable(false);
            img_change
                    .setBackgroundResource(R.drawable.sec_afresh_not_select_icon);
            btn_delete.setClickable(false);
            img_delete
                    .setBackgroundResource(R.drawable.sec_delete_not_select_icon);
            btn_copy.setClickable(false);
            img_copy.setBackgroundResource(R.drawable.sec_copy_not_select_icon);
            skb_time.setEnabled(false);
        }

        lay_frame_data_edit.clearAnimation();
        Animation anim_in = AnimationUtils.loadAnimation(
                ActionsEditActivity.this, R.anim.ctrl_main_anim_in);
        lay_frame_data_edit.setVisibility(View.VISIBLE);
        lay_frame_data_edit.setAnimation(anim_in);
        lay_play.setVisibility(View.GONE);

        chanegViewLen(
                findViewById(R.id.vew_play_frame_time_long),
                (((FrameActionInfo) mCurremtEditItem
                        .get(ActionsEditHelper.MAP_FRAME)).totle_time + 0f) / 3000f);

        txt_edit_frame_name.setText((String) mCurremtEditItem
                .get(ActionsEditHelper.MAP_FRAME_NAME));
        txt_edit_frame_time.setText((String) mCurremtEditItem
                .get(ActionsEditHelper.MAP_FRAME_TIME));

        skb_time.setProgress(((FrameActionInfo) mCurremtEditItem
                .get(ActionsEditHelper.MAP_FRAME)).totle_time);
        txt_frame_time.setText(ActionsEditActivity.this
                .getResources()
                .getString(R.string.ui_readback_action_total_time)
                .replace(
                        "#",
                        (String) mCurremtEditItem
                                .get(ActionsEditHelper.MAP_FRAME_TIME)));

    }

    private void saveNewAction() {

        if (lst_actions_adapter_data.size() < 1) {
            MyAlertDialog.getInstance(
                    ActionsEditActivity.this,
                    getStringResources("ui_readback_not_null"),
                    getStringResources("ui_common_cancel"),
                    getStringResources("ui_common_confirm"), null).show();
            return;
        }

        Intent inte = new Intent();
        inte.setClass(this, ActionsEditSaveActivity.class);
//        inte.putExtra(ActionsEditHelper.NewActionInfo,
//                NewActionInfo.getModelStr(getEditingActions()));
        inte.putExtra(ActionsEditHelper.NewActionInfo, PG.convertParcelable(getEditingActions()));
        inte.putExtra(SCHEME_ID,mSchemeId);
        inte.putExtra(SCHEME_NAME,mSchemeName);
        this.startActivityForResult(inte, ActionsEditHelper.SaveActionReq);
        MobclickAgent.onEvent(this.getApplication(), AlphaStatics.ON_NEW_ACTION);//用户点击保存表示使用一次动作编辑
    }

    private void GoneEditLay() {

        lay_frame_data_edit.clearAnimation();
        Animation anim_out = AnimationUtils.loadAnimation(
                ActionsEditActivity.this, R.anim.ctrl_main_anim_out);
        anim_out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                lay_frame_data_edit.setVisibility(View.GONE);
                lst_actions_adapter.notifyDataSetChanged();
                lay_play.setVisibility(View.VISIBLE);
            }
        });
        lay_frame_data_edit.setAnimation(anim_out);

    }

    @Override
    protected void initControlListener() {

        txt_base_save.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        saveNewAction();
                    }
                }
        );

        lst_actions.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                index_first_visible = firstVisibleItem;
                index_last_visible = firstVisibleItem + visibleItemCount - 1;
                for (int i = 0; i < lst_actions_adapter_data.size(); i++) {
                    if (i < index_first_visible || i > index_last_visible) {
                        // 看不见的项目
                        lst_actions_adapter_data.get(i).put(
                                ActionsEditHelper.MAP_FRAME_SHOW_TIME, 0);
                    }
                }
            }
        });

        btn_add_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int progress = skb_time.getProgress() + 100;
                if (progress > 3000) {
                    progress = 3000;
                }

                skb_time.setProgress((progress));

            }
        });

        btn_low_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int progress = skb_time.getProgress() - 100;
                if (progress < defaultMinTime) {
                    progress = defaultMinTime;
                }

                skb_time.setProgress((progress));

            }
        });

        btn_help.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String language = getStandardLocale(getAppSetLanguage());

                Intent inte = new Intent();
                inte.putExtra(
                        WebContentActivity.WEB_TITLE,
                        getStringResources("ui_about_help"));
                inte.putExtra(
                        WebContentActivity.WEB_URL,
                        HttpAddress
                                .getRequestUrl(Request_type.get_new_action_help)
                                + HttpAddress.getParamsForGet(
                                new String[]{language},
                                Request_type.get_new_action_help));
                inte.setClass(ActionsEditActivity.this,
                        WebContentActivity.class);
                ActionsEditActivity.this.startActivity(inte);
            }
        });

        lst_actions.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (((ActionsEditHelper) mHelper).getNewPlayerState() == PlayerState.PLAYING) {
                    return;
                }

                showEditLay(arg2);
            }
        });

        lay_frame_data_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                GoneEditLay();

            }
        });

        lay_add_frame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!is_first_add_frame) {

                    MyAlertDialog.getInstance(
                            ActionsEditActivity.this,
                            getStringResources("ui_readback_cutoff_tip"),
                            getStringResources("ui_common_cancel"),
                            getStringResources("ui_common_confirm"),
                            new IMessageListeter() {
                                @Override
                                public void onViewAction(boolean isOk) {
                                    if (isOk) {
                                        can_play = false;
                                        is_first_add_frame = true;
                                        doChangePlayState();
                                        lay_add_frame.findViewById(
                                                R.id.lay_add_frame)
                                                .setVisibility(View.VISIBLE);
                                        ((ActionsEditHelper) mHelper)
                                                .doLostPower();
                                    }
                                }
                            }).show();

                }
            }
        });

        btn_add_frame.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (((ActionsEditHelper) mHelper).getNewPlayerState() == PlayerState.PLAYING) {
                    return;
                }
                ((ActionsEditHelper) mHelper).doReadAllEng();
            }
        });

        btn_cancel_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (is_first_add_frame) {
                    is_first_add_frame = false;
                    can_play = true;
                    doChangePlayState();
                    lay_add_frame.findViewById(R.id.lay_add_frame)
                            .setVisibility(View.GONE);
                }

            }
        });

        btn_play.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //检测是否在充电状态和边充边玩状态是否打开
                if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(ActionsEditActivity.this)){
                    Toast.makeText(ActionsEditActivity.this, getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
                    return;
                }

                // 防止过快点击-----------start
                Date curDate = new Date(System.currentTimeMillis());
                float time_difference = 500;
                if (lastTime_play != null) {
                    time_difference = curDate.getTime()
                            - lastTime_play.getTime();
                }
                lastTime_play = curDate;
                if (time_difference < 500) {
                    return;
                }
                // 防止过快点击-----------end
                doPlayCurrentFrames();
            }
        });

        skb_time.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

                MyLog.writeLog("动作编程", "设置时长" + arg1);
                //设置最小时长
                if(arg1 < defaultMinTime){
                    skb_time.setProgress(defaultMinTime);
                    return;
                }
                changeDataOnFrame(arg1);
            }
        });

        btn_preview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ((ActionsEditHelper) mHelper)
                        .doCtrlAllEng(((FrameActionInfo) mCurremtEditItem
                                .get(ActionsEditHelper.MAP_FRAME)).getData());

                chanegPlayViewLen(
                        findViewById(R.id.vew_play_frame_item_progress),
                        ((FrameActionInfo) mCurremtEditItem
                                .get(ActionsEditHelper.MAP_FRAME)).totle_time);
            }
        });

        btn_change.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((ActionsEditHelper) mHelper).doLostPower();
                lay_frame_data_edit.setVisibility(View.GONE);
                lay_change_frame.setVisibility(View.VISIBLE);

            }
        });

        btn_change_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                lay_change_frame.setVisibility(View.GONE);
            }
        });

        btn_change_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                isChange = true;
                ((ActionsEditHelper) mHelper).doReadAllEng();
                lay_change_frame.setVisibility(View.GONE);
            }
        });

        btn_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                isDeleteReFresh = true;
                lst_actions_adapter_data.remove(mCurremtEditItem);
                lst_actions_adapter.notifyDataSetChanged();
                GoneEditLay();
            }
        });
        btn_copy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isDoCopy = true;
                GoneEditLay();
            }
        });

        btn_past.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isPasteReFresh = true;
                lst_actions_adapter_data.add(past_index + 1,
                        copyItem(mCurremtEditItem));

                GoneEditLay();
                lst_actions_adapter.notifyDataSetChanged();
            }
        });
    }

    public Map<String, Object> copyItem(Map<String, Object> item) {
        Map<String, Object> c_item = new HashMap<String, Object>();
        c_item.put(ActionsEditHelper.MAP_FRAME, ((FrameActionInfo) item
                .get(ActionsEditHelper.MAP_FRAME)).doCopy());
        c_item.put(ActionsEditHelper.MAP_FRAME_NAME,
                item.get(ActionsEditHelper.MAP_FRAME_NAME));
        c_item.put(ActionsEditHelper.MAP_FRAME_TIME,
                item.get(ActionsEditHelper.MAP_FRAME_TIME));

        return c_item;
    }

    private int lastTime = 0;

    public void changeDataOnFrame(int time) {

        mCurremtEditItem.put(ActionsEditHelper.MAP_FRAME_TIME, time + "ms");
        ((FrameActionInfo) mCurremtEditItem.get(ActionsEditHelper.MAP_FRAME)).eng_time = time;
        ((FrameActionInfo) mCurremtEditItem.get(ActionsEditHelper.MAP_FRAME)).totle_time = time;
        txt_edit_frame_name.setText((String) mCurremtEditItem
                .get(ActionsEditHelper.MAP_FRAME_NAME));
        txt_edit_frame_time.setText((String) mCurremtEditItem
                .get(ActionsEditHelper.MAP_FRAME_TIME));
        txt_frame_time.setText(ActionsEditActivity.this
                .getResources()
                .getString(R.string.ui_readback_action_total_time)
                .replace("#", (String) mCurremtEditItem.get(ActionsEditHelper.MAP_FRAME_TIME)));

        chanegViewLen(findViewById(R.id.vew_play_frame_time_long),
                (lastTime + 0f) / 3000f, (time + 0f) / 3000f);

        lastTime = time;
    }

    private void doPlayCurrentFrames() {
        if (((ActionsEditHelper) mHelper).getNewPlayerState() == PlayerState.PLAYING) {
            ((ActionsEditHelper) mHelper).doActionCommand(Command_type.Do_Stop,
                    getEditingActions());

            setPlayButtonText(true);

        } else {
            ((ActionsEditHelper) mHelper).doActionCommand(Command_type.Do_play,
                    getEditingActions());

            setPlayButtonText(false);

        }

    }

    private NewActionInfo getEditingActions() {

        List<FrameActionInfo> frames = new ArrayList<FrameActionInfo>();
        frames.add(FrameActionInfo.getDefaultFrame());
        for (int i = 0; i < lst_actions_adapter_data.size(); i++) {
            frames.add(((FrameActionInfo) lst_actions_adapter_data.get(i).get(
                    ActionsEditHelper.MAP_FRAME)));
        }
        mCurrentNewAction.frameActions = frames;
        return mCurrentNewAction;
    }

    private void doChangePlayState() {
        if (btn_play == null){
            return;
        }
        if (can_play) {
            lay_play.setVisibility(View.VISIBLE);
        } else {
            lay_play.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    private void chanegViewLen(View view, float weight) {
        chanegViewLen(view, 0, weight);
    }

    private void chanegViewLen(View view, float start_weight, float weight) {
        view.clearAnimation();
        ScaleAnimation anmi = new ScaleAnimation(start_weight, weight, 1, 1, 0,
                0.5f);
        anmi.setFillAfter(true);
        anmi.setDuration(0);
        view.startAnimation(anmi);
    }

    private void chanegPlayViewLen(final View view, float time) {
        view.clearAnimation();
        ScaleAnimation anmi = new ScaleAnimation(0, time / 3000f, 1, 1, 0, 0.5f);
        anmi.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                view.setVisibility(View.INVISIBLE);
            }
        });
        anmi.setFillAfter(false);
        anmi.setDuration((long) time);
        view.startAnimation(anmi);
    }

    @Override
    public void onReadEng(byte[] eng_angle) {

        String angles = "";
        for (int i = 0; i < eng_angle.length; i++) {
            angles += eng_angle[i] + "#";
        }

        if (isChange) {
            isChange = false;
            ((FrameActionInfo) mCurremtEditItem
                    .get(ActionsEditHelper.MAP_FRAME)).eng_angles = angles;
        } else {

            FrameActionInfo info = new FrameActionInfo();
            info.eng_angles = angles;
            info.eng_time = 1000;
            info.totle_time = 1000;

            Map map = new HashMap<String, Object>();
            map.put(ActionsEditHelper.MAP_FRAME, info);
            String item_name = ActionsEditActivity.this.getStringResources("ui_readback_index");
            item_name = item_name.replace("#", (lst_actions_adapter_data.size() + 1) + "");
            //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
            map.put(ActionsEditHelper.MAP_FRAME_NAME, (lst_actions_adapter_data.size() + 1) + "");
            map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time + "ms");

            lst_actions_adapter_data.add(map);
            lst_actions_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPlaying() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPausePlay() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinishPlay() {
        // TODO Auto-generated method stub
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                setPlayButtonText(true);
            }
        });
    }

    @Override
    public void onFrameDo(final int index) {
        UbtLog.d(TAG, "onFrameDo:" + index);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (index == 0) {
                    chanegPlayViewLen(findViewById(R.id.vew_play_progress),
                            1000);
                } else {
                    index_playing = index - 1;
                    lst_actions_adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void onChangeActionFinish() {
        // TODO Auto-generated method stub

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        isSaveSuccess =(Boolean) data.getExtras().get(
                ActionsEditHelper.SaveActionResult);
        if (requestCode == ActionsEditHelper.SaveActionReq
                && isSaveSuccess) {
            showToast("ui_save_action_success");
            NewActionInfo actionInfo = ((ActionsEditHelper)mHelper).getNewActionInfo();
            if(saveSuccessFragment==null){
                saveSuccessFragment = SaveSuccessFragment.newInstance(actionInfo,mSchemeId,mSchemeName);
            }
            layTitle.setVisibility(View.INVISIBLE);
            lst_actions.setVisibility(View.INVISIBLE);
            lay_change_frame.setVisibility(View.INVISIBLE);
            lay_play.setVisibility(View.INVISIBLE);
            getSupportFragmentManager().beginTransaction().add(R.id.rl_fragment_content,saveSuccessFragment).commit();
        }
    }

    @Override
    public void onIntroduceFinish() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(ActionsEditActivity.class.getSimpleName());
        super.onResume();
        if (((ActionsEditHelper) mHelper).getNewPlayerState() == PlayerState.PLAYING) {
            setPlayButtonText(false);
        } else {
            setPlayButtonText(true);
        }
    }


    /**
     * 设置播放按钮文本
     * @param isShowPlay
     */
    private void setPlayButtonText(boolean isShowPlay){

        if(isShowPlay){
            //文本内容
            String playStr = getStringResources("ui_readback_play");
            SpannableString playSpan = new SpannableString(playStr);

            //设置字符颜色
            playSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.T5)),
                    0, playStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            btn_play.setText(playSpan);
        }else{
            //文本内容
            String stopStr = getStringResources("ui_readback_stop");
            SpannableString stopSpan = new SpannableString(stopStr);

            //设置字符颜色
            stopSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.sec_txt_red)),
                    0, stopStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            btn_play.setText(stopSpan);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == event.ACTION_DOWN
                && event.getKeyCode() == event.KEYCODE_BACK) {

            ((ActionsEditHelper) mHelper).doActionCommand(Command_type.Do_Stop,
                    null);

            return doBack();

        }
        return false;
    }

    private boolean doBack() {

        if (lst_actions_adapter_data.size() < 1) {
            ActionsEditActivity.this.finish();
            return false;
        }
        if(isSaveSuccess)
        {
            ActionsEditActivity.this.finish();
            return true;
        }
        MyAlertDialog.getInstance(
                ActionsEditActivity.this,

                getStringResources("ui_readback_quit_tip"),
                getStringResources("ui_common_cancel"),
                getStringResources("ui_common_save"), new IMessageListeter() {

                    @Override
                    public void onViewAction(boolean isOk) {
                        if (isOk) {
                            saveNewAction();
                        } else {

                            ActionsEditActivity.this.finish();
                        }
                    }
                }).show();
        return true;
    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result,
                                    boolean result_state, long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result,
                                     long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWriteDataFinish(long requestCode, State state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadCacheSize(int size) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClearCache() {
        // TODO Auto-generated method stub

    }

    @Override
    public void notePlayChargingError() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(
                        ActionsEditActivity.this,
                        getStringResources("ui_settings_play_during_charging_tips")
                        , Toast.LENGTH_SHORT)
                        .show();
                ((ActionsEditHelper) mHelper).doActionCommand(Command_type.Do_Stop,
                        null);
            }
        });
    }

    @Override
    public void onFragmentInteraction() {
        this.finish();
    }
}
