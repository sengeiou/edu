package com.ubt.alpha1e.action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.NewActionPlayer;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.AlphaStatics;
import com.ubt.alpha1e.data.model.FrameActionInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.ActionsEditSaveActivity;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.FrameRecycleViewAdapter;
import com.ubt.alpha1e.ui.TimesHideRecycleViewAdapter;
import com.ubt.alpha1e.ui.TimesRecycleViewAdapter;
import com.ubt.alpha1e.ui.WebContentActivity;
import com.ubt.alpha1e.ui.custom.ActionGuideView;
import com.ubt.alpha1e.ui.dialog.IMessageListeter;
import com.ubt.alpha1e.ui.dialog.MyAlertDialog;
import com.ubt.alpha1e.ui.fragment.SaveSuccessFragment;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.IEditActionUI;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.TimeUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class ActionsCreateActivity extends BaseActivity implements IEditActionUI,SaveSuccessFragment.OnFragmentInteractionListener {

    public static String TAG = ActionsCreateActivity.class.getSimpleName();

    private ImageView ivRobot;
    private ImageView ivHandLeft, ivHandRight, ivLegLeft, ivLegRight;
    private RecyclerView recyclerViewFrames;
    private List<Map<String, Object>> list_frames ;

    private List<Map<String, Object>> list_autoFrames = new ArrayList<Map<String, Object>>();

    private FrameRecycleViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private LinearLayoutManager layoutManagerTime;

    private ImageView ivAddFrame;
    private ImageView ivBack, ivReset, ivAutoRead, ivSave, ivHelp;
    private ImageView ivActionLib, ivActionLibMore, ivActionBgm;

    public NewActionInfo mCurrentNewAction;
    public ActionsEditHelper.StartType mCurrentStartType = ActionsEditHelper.StartType.new_type;
    public static String SCHEME_ID = "SCHEME_ID";
    public static String SCHEME_NAME = "SCHEME_NAME";
    private String mSchemeId = "";
    private String mSchemeName = "";

    private boolean isSaveSuccess = false;
    private SaveSuccessFragment saveSuccessFragment;

    private boolean autoRead = false;


    //action edit frame view
    private RelativeLayout rlEditFrame;
    private ImageView ivPreview, ivCopy, ivChange, ivCut, ivPaste, ivDelete;
    private TextView tvCut;

    private Map<String, Object> mCurrentEditItem;
    private boolean change = false;
    private boolean copy = false;
    private boolean cut = false;
    private Map<String, Object> mCutItem = new HashMap<String, Object>();
    private int selectPos = -1;
    private Map<String, Object> mCopyItem = new HashMap<String, Object>();


//    private RelativeLayout rlRoot;



    //    private FastScroller fastScroller;
    private int firstVisibleItemPosition = -1;
    private int lastVisibleItemPosition = -1;

    private RecyclerView recyclerViewTimes;
    private TimesRecycleViewAdapter timeAdapter;
    private List<Map<String, Object>> timeDatas = new ArrayList<Map<String, Object>>();
    public static final String TIME = "time";
    public static final String SHOW = "show";
    //    private FastScroller timeFastScroll;
    private SeekBar sbTime;
    private int current =0;

//    private String init = "\"90#90#90#90#90#90#90#60#76#110#90#90#120#104#70#90\"";

    public String [] init = {"90", "90", "90", "90", "90","90", "90", "60","76","110", "90", "90",
            "120", "104", "70", "90"};
    private boolean lostLeftHand = false;
    private boolean lostRightHand = false;
    private boolean lostLeftLeg = false;
    private boolean lostRightLeg = false;
    private boolean needAdd = false;
    private List<Integer> ids = new ArrayList<Integer>();
    private int readCount = -1;
    private String autoAng = "";

    private ImageView ivZoomPlus, ivZoomMinus;
    private int currentPlus = 1;
    private int currentMinus = 1;
    private TextView tvZoomPlus, tvZoomMinus;

    private SeekBar sbVoice;
    private int touch = 0;

    private int timePosition = 0;

    private MediaPlayer mediaPlayer;
    private String mDir = "";
    private int musicTimes = 0;

    private ImageView ivPlay;
    private TextView tvMusicTime;

    private boolean playFinish = true;

    private long clickTime = 0;

    private ImageView ivResetIndex;

    private List<Map<String, Object>>  listActionLib;
    public static final String ACTION_TIME = "action_time";
    public static final String ACTION_ANGLE = "action_angle";
    public static final String ACTION_NAME = "action_name";
    public static final String ACTION_ICON = "action_icon";

    private List<Map<String, Object>> listWarrior = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listStoop = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listSquat = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listLeftHand = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listRightHand = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listMechDance1 = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listMechDance2 = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listHug = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listHappy = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listSalute = new ArrayList<Map<String, Object>>();

    private List<Map<String, Object>> listWalk = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listTwist = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listSteppin = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listBent = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listArm = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listDance1 = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listDance2 = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listCurtain = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listBye = new ArrayList<Map<String, Object>>();



    private List<Map<String, Object>> listHighActionLib = new ArrayList<Map<String, Object>>();
    private String [] highActionName;
    private int [] advanceIconID ;
    private String [] basicAction;
    private int[] basicIconID ;
    private List<Map<String, Object>> listBasicActionLib = new ArrayList<Map<String, Object>>();


    private String [] songs = {"","flexin", "jingle bells", "london bridge is falling down",
            "twinkle twinkle little star", "yankee doodle dandy","kind of light", "so good",
            "Sun Indie Pop", "The little robot", "zombie"};
    public static final String SONGS_NAME= "songs_name";
    public static final String SONGS_TYPE = "songs_type"; //用来区分是内置音乐还是录音
    private List<Map<String, Object>> listSongs = new ArrayList<Map<String, Object>>();


    private ImageView ivCancelChange;

    private float density = 1;

    private ImageView ivZhen;
    private ImageView ivDeleteMusic;
    private RelativeLayout rl_delete_music;
    private ImageView iv_del_music;
    private TextView tvDeleteMusic;

    private int time;

    //指引暂时没用
    private TextView tvClickRobot;
    private TextView tvClickBasic;
    private TextView tvClickAdvance;
    private TextView tvClickMusic;
    private TextView tvClickReset;
    private TextView tvClickAuto;
    private TextView tvClickHelp;
    private TextView tvClickResetIndex;
    private TextView tvClickMark;
    private TextView tvClickAddFrame;
    private TextView tvClickItem;
    private TextView tvClickChangeTime;


    private ActionGuideView actionGuideView;

    private boolean doPlayPreview = false;

    private RecyclerView recyclerViewTimesHide;
    private TimesHideRecycleViewAdapter timeHideAdapter;
    private LinearLayoutManager layoutManagerTimeHide;

    private boolean isFinishFramePlay = true;

    public static String BACK_UP = "back_up";

    private int currentIndex = 1;
    private int scroll = 0;



    public static void launchActivity(Activity context, ActionsEditHelper.StartType type, android.os.Parcelable parcelable, int requestCode, String schemeId, String schemeName)
    {

        Intent intent = new Intent();
        intent.putExtra(SCHEME_ID,schemeId);
        intent.putExtra(SCHEME_NAME,schemeName);
        intent.putExtra(ActionsEditHelper.StartTypeStr,type);
        intent.putExtra(ActionsEditHelper.NewActionInfo,parcelable);
        intent.setClass(context,ActionsCreateActivity.class);
        context.startActivityForResult(intent,requestCode);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UbtLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if(AlphaApplication.isPad()){
            setContentView(R.layout.activity_new_edit_for_pad);
        }else{
//            setContentView(R.layout.activity_new_edit);
            setContentView(R.layout.activity_create_action);
        }

        mHelper = new ActionsEditHelper(this, this);
//        initActionsData();
        doReset();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        density = metrics.density;
        UbtLog.d(TAG, "density:" + density);

        initUI();
        initGuide();
        initControlListener();

        mCurrentStartType = (ActionsEditHelper.StartType) this.getIntent().getExtras()
                .get(ActionsEditHelper.StartTypeStr);
        mSchemeId = getIntent().getStringExtra(SCHEME_ID);
        mSchemeName = getIntent().getStringExtra(SCHEME_NAME);


        if (mCurrentStartType == ActionsEditHelper.StartType.new_type) {
            mCurrentNewAction = new NewActionInfo();
        } else {
            mCurrentNewAction = this.getIntent().getParcelableExtra(ActionsEditHelper.NewActionInfo);
            // 解析帧数据
            releaseFrameDatas();
            adapter.notifyDataSetChanged();
        }

//        ids = new ArrayList<Integer>();

        initMediaPlayer();
        initActionsData();
        initActionLibs();

    }

    private void initActionsData(){
        UbtLog.d(TAG, "highActionName:" + highActionName + "---basicAction:" + basicAction);

        if(highActionName == null){
            highActionName = new String[]{getStringResources("ui_advance_action_walk"), getStringResources("ui_advance_action_twist"), getStringResources("ui_advance_action_steppin"),getStringResources("ui_advance_action_bent"), getStringResources("ui_advance_action_arm"),
                    getStringResources("ui_advance_action_dance1"), getStringResources("ui_advance_action_dance2"), getStringResources("ui_advance_action_curtain"), getStringResources("ui_advance_action_bye")};

        }

        if(basicAction == null){
            basicAction = new String[]{getStringResources("ui_basic_action_warrior"), getStringResources("ui_basic_action_stoop"), getStringResources("ui_basic_action_squat"), getStringResources("ui_basic_action_left_hand"), getStringResources("ui_basic_action_right_hand"),
                    getStringResources("ui_basic_action_mech_dance1"), getStringResources("ui_basic_action_mech_dance2"), getStringResources("ui_basic_action_hug"),getStringResources("ui_basic_action_happy"),getStringResources("ui_basic_action_salute")};

        }

        if(advanceIconID == null){
            advanceIconID = new int[] {R.drawable.xingzou, R.drawable.niuyao,
                    R.drawable.dianjiao,R.drawable.cewanyao, R.drawable.shoubi,R.drawable.wudao1,
                    R.drawable.wudao2, R.drawable.xiemu,R.drawable.zaijian};
        }

        if(basicIconID == null){
            basicIconID = new int[] {R.drawable.chuzhao,R.drawable.xiayao,R.drawable.dunxia,
                    R.drawable.zuotaishou,R.drawable.taiyoushou, R.drawable.jixie1, R.drawable.jixie2,
                    R.drawable.baobao,R.drawable.kaixin,R.drawable.jinli};
        }



    }


    private void initGuide() {
        tvClickRobot = (TextView) findViewById(R.id.tv_click_robot);
        tvClickBasic = (TextView) findViewById(R.id.tv_click_basic);
        tvClickAdvance = (TextView) findViewById(R.id.tv_click_advance);
        tvClickMusic = (TextView) findViewById(R.id.tv_click_music);
        tvClickReset = (TextView) findViewById(R.id.tv_click_reset);
        tvClickAuto = (TextView) findViewById(R.id.tv_click_auto);
        tvClickHelp = (TextView) findViewById(R.id.tv_click_help);
        tvClickResetIndex = (TextView) findViewById(R.id.tv_click_reset_index);
        tvClickMark = (TextView) findViewById(R.id.tv_click_mark);
        tvClickAddFrame = (TextView) findViewById(R.id.tv_click_add);
        tvClickItem = (TextView) findViewById(R.id.tv_click_item);
//        tvClickChangeTime = ;
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                UbtLog.d(TAG, "播放完毕:" + isFinishFramePlay);

                playFinish = true;
                mHandler.removeMessages(0);
                mediaPlayer.seekTo(0);
                sbVoice.setProgress(0);
                tvMusicTime.setText(TimeUtils.getTimeFromMillisecond((long)handleMusicTime(mediaPlayer.getDuration())));
                ivPlay.setImageResource(R.drawable.button_play);
                ivAddFrame.setEnabled(true);
                ivAddFrame.setImageResource(R.drawable.icon_add_nor);
                recyclerViewTimesHide.setVisibility(View.GONE);
//                if(isFinishFramePlay){
                    setEnable(true);
//                }
                if(list_frames != null && list_frames.size()>0){
                    if(isFinishFramePlay){
                        layoutManager.scrollToPosition(0);
//                        recyclerViewFrames.smoothScrollToPosition(0);
                    }

                }
                if(recyclerViewTimes != null){
                    recyclerViewTimes.smoothScrollToPosition(0);
                }
            }
        });
    }


    public void play(String mp3){
        String path = null;
        if(mp3.equals("")){
            path = mDir + File.separator+"a.mp3";
//            final File file = new File(path);
        }else{
            path = mDir + File.separator + mp3;
        }

        File file = new File(path);
        UbtLog.d(TAG, "path:" + path);
        if (file.exists()) {

            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "file is not exit", Toast.LENGTH_SHORT).show();
        }

    }


    private void play() {
        try {
            if(mDir.equals("") || mDir.equals(null)){
                return;
            }
            playFinish = false;
            mediaPlayer.start();

            //后台线程发送消息进行更新进度条
            final int milliseconds = 100;
            new Thread(){
                @Override
                public void run(){
                    while(true && !playFinish && !ActionsCreateActivity.this.isDestroyed()){
                        try {
                            sleep(milliseconds);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        mHandler.sendEmptyMessage(0);
                    }
                }
            }.start();

//            mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public  void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            UbtLog.d(TAG, "mediaPlayer stop");
            playFinish = true;
            mHandler.removeMessages(0);
            mediaPlayer.stop();
            ivPlay.setImageResource(R.drawable.button_play);
        }
    }

    private void initTimeFrame() {

//        timeDatas = new ArrayList<Map<String, Object>>();
        Map<String, Object> timeMap = new HashMap<String, Object>() ;
        for(int i=0; i< musicTimes/100; i ++){
            timeMap.put(TIME, "100");
            timeMap.put(SHOW, "0");
            timeDatas.add(timeMap);
        }
        UbtLog.d(TAG, "size:" + timeDatas.size());

        timeHideAdapter = new TimesHideRecycleViewAdapter(this, timeDatas);
        recyclerViewTimesHide.setAdapter(timeHideAdapter);
        timeHideAdapter.setOnItemListener(new TimesHideRecycleViewAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, int pos, Map<String, Object> data) {
                UbtLog.d(TAG, "onItemClick pos:" + pos);
                data.put(SHOW, "0");
                data.put(TIME, "100");
                timeDatas.set(pos,data);
                timeHideAdapter.notifyDataSetChanged();
                timeAdapter.notifyDataSetChanged();
            }
        });


        timeAdapter = new TimesRecycleViewAdapter(this, timeDatas);
        recyclerViewTimes.setAdapter(timeAdapter);
        timeAdapter.setOnItemListener(new TimesRecycleViewAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, int pos, Map<String, Object> data) {
                UbtLog.d(TAG, "timeAdapter onItemClick:" + pos);

            }
        });
        recyclerViewTimes.smoothScrollToPosition(0);
        recyclerViewTimes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                firstVisibleItemPosition = layoutManagerTime.findFirstVisibleItemPosition();
                lastVisibleItemPosition = layoutManagerTime.findLastVisibleItemPosition();

                UbtLog.d(TAG, "firstVisibleItemPosition:" + firstVisibleItemPosition +
                        "-lastVisibleItemPosition:" + lastVisibleItemPosition);
//                sbVoice.setProgress((firstVisibleItemPosition*100/5000)*100);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(touch == 2){
                    return;

                }
                UbtLog.d(TAG,"ivZhen scrollBy dx:" + dx + "-dy:" + dy);
                ivZhen.scrollTo(500, 500);
//                recyclerViewFrames.scrollBy(dx, dy);
            }
        });

        recyclerViewTimes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch = 1;
                return false;
            }
        });

        //根据添加的音乐自动补全动作
        if(list_frames.size() == 0){
            FrameActionInfo info = new FrameActionInfo();
            info.eng_angles = "";

            info.eng_time = musicTimes;
            info.totle_time = musicTimes;

            Map map = new HashMap<String, Object>();
            map.put(ActionsEditHelper.MAP_FRAME, info);
            String item_name = ActionsCreateActivity.this.getStringResources("ui_readback_index");
            item_name = item_name.replace("#", (list_frames.size() + 1) + "");
            //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
            map.put(ActionsEditHelper.MAP_FRAME_NAME, (list_frames.size() + 1) + "");
            map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);
            list_frames.add(list_frames.size(), map);
            adapter.setMusicTime(musicTimes);
            adapter.notifyDataSetChanged();


        }else{

            //根据当前已添加的动作帧时间计算补全帧时长
            handleAddFrame();

        }

    }


    private void handleAddFrame(){

        int time = 0;

        for(int i=0; i<list_frames.size(); i++){
            time += (int)list_frames.get(i).get(ActionsEditHelper.MAP_FRAME_TIME);
        }

        UbtLog.d(TAG, "handleAddFrame time:" + time);

        int backupTime = musicTimes -time;
        UbtLog.d(TAG, "handleAddFrame backupTime:" + backupTime);
        if(backupTime <=0){
            return;
        }


        FrameActionInfo info = new FrameActionInfo();
        info.eng_angles = "";

        info.eng_time = backupTime;
        info.totle_time = backupTime;

        Map map = new HashMap<String, Object>();
        map.put(ActionsEditHelper.MAP_FRAME, info);
        String item_name = ActionsCreateActivity.this.getStringResources("ui_readback_index");
        item_name = item_name.replace("#", (list_frames.size() + 1) + "");
        //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
        map.put(ActionsEditHelper.MAP_FRAME_NAME, (list_frames.size() + 1) + "");
        map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);
        list_frames.add(list_frames.size(), map);

        adapter.setMusicTime(backupTime);
        adapter.notifyDataSetChanged();


    }




    private int handleMusicTime(int time){
        int handleTime = 0;
        int yushu = time%100;
        handleTime = time+(100-yushu);
        UbtLog.d(TAG, "handleTime:" + handleTime);
        return handleTime;

    }


    @Override
    protected void onResume() {
        setCurrentActivityLable(ActionsCreateActivity.class.getSimpleName());
        super.onResume();
//        initActionsData();
//        initActionLibs();
        String step = readGuideStep();
        UbtLog.d(TAG, "step:" + step);
        if(!step.equals("12")){
            if(actionGuideView == null){
                actionGuideView = new ActionGuideView(ActionsCreateActivity.this, null,density);
            }

        }

        if(tvDeleteMusic != null){
            tvDeleteMusic.setText(getStringResources("ui_create_delete_music"));
        }

        if(tvCut != null){
            tvCut.setText(getStringResources("ui_create_menu_cut"));
        }



    }






    public String readGuideStep() {
        return BasicSharedPreferencesOperator.getInstance(ActionsCreateActivity.this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(BasicSharedPreferencesOperator.KEY_ACTION_CUIDE_STEP);
    }


    private int temp = 0;
    @Override
    protected void initUI() {


        ivZhen = (ImageView) findViewById(R.id.iv_zhen);
//        ivZhen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//                temp = temp +100;
//                params.leftMargin = temp ;
//                ivZhen.setLayoutParams(params);
//            }
//        });

        ivPlay = (ImageView) findViewById(R.id.iv_play_music);
        tvMusicTime = (TextView) findViewById(R.id.tv_play_time);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerViewTimesHide.setVisibility(View.GONE);
                rl_delete_music.setVisibility(View.GONE);

                //取消修改状态
                goneEditFrameLayout();
                change =false;
                if(list_frames.size()>0){
                    ivAddFrame.setImageResource(R.drawable.icon_add_nor);
                    adapter.setDefSelect(-1);
                }

                if(mDir.equals("") && list_frames.size() <= 0){
                    return;
                }

                if(list_frames.size() >0){
                    if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(ActionsCreateActivity.this)){
                        UbtLog.d(TAG, "边充边玩未打开");
                        Toast.makeText(ActionsCreateActivity.this, AlphaApplication.getBaseActivity().getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                if(mediaPlayer!= null && mediaPlayer.isPlaying() && !mDir.equals("")){
                    ivPlay.setImageResource(R.drawable.button_play);
                    pause();
                    doPlayCurrentFrames();
                    UbtLog.d(TAG, "setEnable true");
                    setEnable(true);
                    ivAddFrame.setEnabled(true);
                    ivAddFrame.setImageResource(R.drawable.icon_add_nor);
                    playFinish = true;
                }else{
                    UbtLog.d(TAG,  "doPlayCurrentFrames");
                    if(recyclerViewTimes != null){
                        recyclerViewTimes.smoothScrollToPosition(0);
                    }
                    if(list_frames.size() >0){
                        UbtLog.d(TAG, "getNewPlayerState:" + ((ActionsEditHelper) mHelper).getNewPlayerState());
                        if(scroll==0 && ((ActionsEditHelper) mHelper).getNewPlayerState() != NewActionPlayer.PlayerState.PLAYING){
                            UbtLog.d(TAG, "recyclerViewFrames smoothScrollToPosition");
                            recyclerViewFrames.smoothScrollToPosition(0);
                        }

                    }
                    ivPlay.setImageResource(R.drawable.icon_pause_nor);
                    doPlayCurrentFrames();
                    play();

                }

            }
        });

        ivCancelChange = (ImageView) findViewById(R.id.iv_cancel_update);
        ivCancelChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCancelChange.setVisibility(View.INVISIBLE);
                goneEditFrameLayout();
                change =false;
                ivAddFrame.setImageResource(R.drawable.icon_add_nor);
                adapter.setDefSelect(-1);
            }
        });

        ivRobot = (ImageView) findViewById(R.id.iv_robot);
        ivHandLeft = (ImageView) findViewById(R.id.iv_hand_left);
        ivHandRight = (ImageView) findViewById(R.id.iv_hand_right);
        ivLegLeft = (ImageView) findViewById(R.id.iv_leg_left);
        ivLegRight = (ImageView) findViewById(R.id.iv_leg_right);
        initRobot();

        recyclerViewFrames = (RecyclerView) findViewById(R.id.rcv_actions);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewFrames.setLayoutManager(layoutManager);
        list_frames = new ArrayList<Map<String, Object>>();
        adapter = new FrameRecycleViewAdapter(this, list_frames, density);
        recyclerViewFrames.setAdapter(adapter);
//        fastScroller = (FastScroller) findViewById(R.id.fastscroll);
//        fastScroller.setRecyclerView(recyclerViewFrames);

        recyclerViewFrames.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                UbtLog.d(TAG, "firstVisibleItemPosition:" + firstVisibleItemPosition +
                        "-lastVisibleItemPosition:" + lastVisibleItemPosition);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(touch ==1){
                    return;
                }

                if(mediaPlayer != null && !mediaPlayer.isPlaying()){
                    if(touch ==2){
                        UbtLog.d(TAG,"onScrolled recyclerViewFrames  dx:" + dx );
                        recyclerViewTimes.scrollBy(dx,dy);
                        recyclerViewTimesHide.scrollBy(dx,dy);
                    }

                }

            }
        });

        recyclerViewFrames.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(musicTimes !=0 && list_frames.size() == 1){
                    return true;
                }
                touch = 2;
                return false;
            }
        });




        ivAddFrame = (ImageView) findViewById(R.id.iv_add_frame);
        ivAddFrame.setEnabled(true);
        ivAddFrame.setImageResource(R.drawable.icon_add_nor);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivReset = (ImageView) findViewById(R.id.iv_reset);
        ivAutoRead = (ImageView) findViewById(R.id.iv_auto_read);
        ivSave = (ImageView) findViewById(R.id.iv_save_action);
        ivHelp = (ImageView) findViewById(R.id.iv_help);

        ivActionLib = (ImageView) findViewById(R.id.iv_action_lib);
        ivActionLibMore = (ImageView) findViewById(R.id.iv_action_lib_more);
        ivActionBgm = (ImageView) findViewById(R.id.iv_action_bgm);

        initEditFrameLayout();


//        rlRoot = (RelativeLayout) findViewById(R.id.rl_action_edit);



        recyclerViewTimes = (RecyclerView) findViewById(R.id.rcv_time);
        layoutManagerTime = new LinearLayoutManager(this);
        layoutManagerTime.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewTimes.setLayoutManager(layoutManagerTime);

        recyclerViewTimesHide = (RecyclerView) findViewById(R.id.rcv_time_hide);
        layoutManagerTimeHide = new LinearLayoutManager(this);
        layoutManagerTimeHide.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewTimesHide.setLayoutManager(layoutManagerTimeHide);

        recyclerViewTimesHide.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int firstVisibleItemPosition = layoutManagerTimeHide.findFirstVisibleItemPosition();
                UbtLog.d(TAG, "firstVisibleItemPosition:" + firstVisibleItemPosition + "total:" + timeDatas.size());
                String pro = accuracy(firstVisibleItemPosition, timeDatas.size(), 0);
                UbtLog.d(TAG, "pro:" + pro);
                if(recyclerViewTimesHide.getVisibility() == View.VISIBLE){
//                    sbVoice.setProgress(Integer.valueOf(pro)*100);
                }

            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                UbtLog.d(TAG, "onScrolled recyclerViewTimesHide");
                recyclerViewTimes.scrollBy(dx,dy);
                recyclerViewFrames.scrollBy(dx, dy);
            }
        });





        ivZoomPlus = (ImageView) findViewById(R.id.iv_zoom_plus);
        ivZoomMinus = (ImageView) findViewById(R.id.iv_zoom_minus);
        tvZoomPlus = (TextView) findViewById(R.id.tv_zoom_plus);
        tvZoomMinus = (TextView) findViewById(R.id.tv_zoom_minus);

        sbVoice = (SeekBar) findViewById(R.id.sb_voice);

        sbVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                UbtLog.d(TAG, "progress:" + getnum(progress, musicTimes));


                if(timeDatas.size() <=0) {
                    return;
                }
                float a = Float.valueOf(getnum(progress, musicTimes));
                UbtLog.d(TAG, "progress a:" + a + Math.round(a));


                timePosition = (musicTimes/100)*Math.round(a)/100;
                if(timePosition <= 0){
                    timePosition = 0;
                }else{
                    timePosition = timePosition -1;
                }

                UbtLog.d(TAG, "timePosition:" + timePosition);

//                recyclerViewTimes.smoothScrollToPosition(timePosition);
                UbtLog.d(TAG, "1progress:" + Math.round(a) + "rate:" + Math.round(a)/10 + "timePosition:" + timePosition);
                int rate = Math.round(a)/10;

               if (rate == 10) {
                    UbtLog.d(TAG, "timePosition:" + timePosition);
                    layoutManagerTime.scrollToPositionWithOffset(timePosition, 0);
                    layoutManagerTimeHide.scrollToPositionWithOffset(timePosition, 0);
                    layoutManagerTimeHide.setStackFromEnd(true);

                } else {
                    if( rate == 0){
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }else if(rate == 1){
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }
                    else if(rate ==2){
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate-1, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate-1, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }else if(rate == 3){
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate-2, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate-2, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }else if(rate == 4){
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate-3, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate-3, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }else if(rate == 5){
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate-4, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate-4, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }else if(rate == 6){
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate-5, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate-5, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }else if(rate == 7){
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate-6, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate-6, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }else if(rate == 8){
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate-7, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate-7, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }else if(rate == 9){
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate-8, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate-8, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }
                    else
                    {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate-1, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate-1, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }



                }



//                layoutManagerTime.setStackFromEnd(true);
                UbtLog.d(TAG, "recyclerViewFrames smoothScrollToPosition 1:" + scroll);
                recyclerViewFrames.scrollBy( (musicTimes)*Math.round(a)/100-scroll,0);
//                recyclerViewTimes.scrollBy((musicTimes)*Math.round(a)/100-scroll+10, 0);
//                recyclerViewTimesHide.scrollBy((musicTimes)*Math.round(a)/100-scroll,0);
                scroll = (musicTimes)*Math.round(a)/100;
//                recyclerViewFrames.smoothScrollToPosition(timePosition);

//                recyclerViewTimesHide.smoothScrollToPosition(timePosition);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UbtLog.d(TAG, "22 onStartTrackingTouch:" + currentTimeMillis());
                if(timeDatas.size() <=0) {
                    return;
                }
                clickTime = currentTimeMillis();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                UbtLog.d(TAG, "22 onStopTrackingTouch:" + currentTimeMillis());


                long dur = System.currentTimeMillis()-clickTime;
                if(System.currentTimeMillis()-clickTime < 100){
                    Map<String, Object> map  = new HashMap<String, Object>();
                    map.put(TIME, "100");
                    if(timeDatas.get(timePosition).get(SHOW).equals("1")){

                        map.put(SHOW, "1");
                    }else{
                        map.put(SHOW, "1");
                    }
                    UbtLog.d(TAG, "timePosition:" + timePosition + "--dur:" + dur);
                    timeDatas.set(timePosition, map);
                    timeAdapter.notifyDataSetChanged();
                    timeHideAdapter.notifyDataSetChanged();
                }else{

                }
            }
        });

        sbVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UbtLog.d(TAG, "sssss" + event.getX());
//                recyclerViewTimes.scrollBy((int)event.getX(), 0);
                return false;
            }
        });


        sbVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UbtLog.d(TAG, " sbVoice setOnClickListener");

                if(mediaPlayer != null){
                    mediaPlayer.getDuration();

                }
            }
        });


        ivResetIndex = (ImageView) findViewById(R.id.iv_reset_index);
        ivResetIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sbVoice.setProgress(0);
                recyclerViewFrames.smoothScrollToPosition(0);
                recyclerViewTimes.smoothScrollToPosition(0);
            }
        });


        ivDeleteMusic = (ImageView) findViewById(R.id.iv_music_icon);
        ivDeleteMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDir != "" && playFinish){
                    rl_delete_music.setVisibility(View.VISIBLE);
                }

            }
        });


        rl_delete_music = (RelativeLayout) findViewById(R.id.rl_delete_music);
        tvDeleteMusic = (TextView) findViewById(R.id.tv_del_music);
        iv_del_music = (ImageView) findViewById(R.id.iv_del_music);
        iv_del_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDir != ""){
                    stopMusic();
                    mDir = "";
                    musicTimes = 0;
                    sbVoice.setVisibility(View.INVISIBLE);
                    timeDatas.clear();
                    timeAdapter.notifyDataSetChanged();
                    Toast.makeText(ActionsCreateActivity.this, getStringResources("ui_create_delete_music_tips"), Toast.LENGTH_SHORT).show();
                    rl_delete_music.setVisibility(View.GONE);
                    tvMusicTime.setVisibility(View.INVISIBLE);
                    if(list_frames.size() >0){
                        list_frames.remove(list_frames.size()-1);
                        adapter.setMusicTime(0);
                        adapter.notifyDataSetChanged();
                    }

                    if(isFinishFramePlay){
                        setEnable(true);
                    }

                }
            }
        });


    }


    private void initRobot(){

        if(AlphaApplication.isPad()){
            UbtLog.d(TAG, "Pad Robot 1");

        }else{
            if(density == 3.0){
                ViewGroup.LayoutParams params = ivRobot.getLayoutParams();

                UbtLog.d(TAG, "width:"+ params.width + "--height:" + params.height);
                params.width = params.width/2*3;
                params.height = params.height/2*3;
                ivRobot.setLayoutParams(params);
                UbtLog.d(TAG, "ivRobot:" + ivRobot.getWidth() + "/" + ivRobot.getHeight());

                params = ivHandLeft.getLayoutParams();
                params.width = params.width/2*3;
                params.height = params.height/2*3;
                ivHandLeft.setLayoutParams(params);
                UbtLog.d(TAG, "ivHandLeft:" + ivHandLeft.getWidth() + "/" + ivHandLeft.getHeight());

                params = ivHandRight.getLayoutParams();
                params.width = params.width/2*3;
                params.height= params.height/2*3;
                ivHandRight.setLayoutParams(params);
                UbtLog.d(TAG, "ivHandRight:" + ivHandRight.getWidth() + "/" + ivHandRight.getHeight());

                params = ivLegLeft.getLayoutParams();
                params.width = params.width/2*3;
                params.height = params.height/2*3;
                ivLegLeft.setLayoutParams(params);
                UbtLog.d(TAG, "ivLegLeft:" + ivLegLeft.getWidth() + "/" + ivLegLeft.getHeight());

                params = ivLegRight.getLayoutParams();
                params.width = params.width/2*3;
                params.height = params.height/2*3;
                ivLegRight.setLayoutParams(params);
                UbtLog.d(TAG, "ivLegRight:" + ivLegRight.getWidth() + "/" + ivLegRight.getHeight());

            }else if(density == 4.0){
                ViewGroup.LayoutParams params = ivRobot.getLayoutParams();

                UbtLog.d(TAG, "width:"+ params.width + "--height:" + params.height);
                params.width = params.width/2*4;
                params.height = params.height/2*4;
                ivRobot.setLayoutParams(params);
                UbtLog.d(TAG, "ivRobot:" + ivRobot.getWidth() + "/" + ivRobot.getHeight());

                params = ivHandLeft.getLayoutParams();
                params.width = params.width/2*4;
                params.height = params.height/2*4;
                ivHandLeft.setLayoutParams(params);
                UbtLog.d(TAG, "ivHandLeft:" + ivHandLeft.getWidth() + "/" + ivHandLeft.getHeight());

                params = ivHandRight.getLayoutParams();
                params.width = params.width/2*4;
                params.height= params.height/2*4;
                ivHandRight.setLayoutParams(params);
                UbtLog.d(TAG, "ivHandRight:" + ivHandRight.getWidth() + "/" + ivHandRight.getHeight());

                params = ivLegLeft.getLayoutParams();
                params.width = params.width/2*4;
                params.height = params.height/2*4;
                ivLegLeft.setLayoutParams(params);
                UbtLog.d(TAG, "ivLegLeft:" + ivLegLeft.getWidth() + "/" + ivLegLeft.getHeight());

                params = ivLegRight.getLayoutParams();
                params.width = params.width/2*4;
                params.height = params.height/2*4;
                ivLegRight.setLayoutParams(params);
                UbtLog.d(TAG, "ivLegRight:" + ivLegRight.getWidth() + "/" + ivLegRight.getHeight());
            }else if(density == 5.0){
                ViewGroup.LayoutParams params = ivRobot.getLayoutParams();

                UbtLog.d(TAG, "width:"+ params.width + "--height:" + params.height);
                params.width = params.width/2*5;
                params.height = params.height/2*5;
                ivRobot.setLayoutParams(params);
                UbtLog.d(TAG, "ivRobot:" + ivRobot.getWidth() + "/" + ivRobot.getHeight());

                params = ivHandLeft.getLayoutParams();
                params.width = params.width/2*5;
                params.height = params.height/2*5;
                ivHandLeft.setLayoutParams(params);
                UbtLog.d(TAG, "ivHandLeft:" + ivHandLeft.getWidth() + "/" + ivHandLeft.getHeight());

                params = ivHandRight.getLayoutParams();
                params.width = params.width/2*5;
                params.height= params.height/2*5;
                ivHandRight.setLayoutParams(params);
                UbtLog.d(TAG, "ivHandRight:" + ivHandRight.getWidth() + "/" + ivHandRight.getHeight());

                params = ivLegLeft.getLayoutParams();
                params.width = params.width/2*5;
                params.height = params.height/2*5;
                ivLegLeft.setLayoutParams(params);
                UbtLog.d(TAG, "ivLegLeft:" + ivLegLeft.getWidth() + "/" + ivLegLeft.getHeight());

                params = ivLegRight.getLayoutParams();
                params.width = params.width/2*5;
                params.height = params.height/2*5;
                ivLegRight.setLayoutParams(params);
                UbtLog.d(TAG, "ivLegRight:" + ivLegRight.getWidth() + "/" + ivLegRight.getHeight());
            }
        }


    }



    private void initEditFrameLayout() {
        rlEditFrame = (RelativeLayout) findViewById(R.id.lay_frame_data_edit);
        ivPreview = (ImageView) findViewById(R.id.iv_preview);
        ivChange = (ImageView) findViewById(R.id.iv_change);
        ivCopy = (ImageView) findViewById(R.id.iv_copy);
        ivCut = (ImageView) findViewById(R.id.iv_cut);
        tvCut = (TextView) findViewById(R.id.tv_cut) ;
        ivPaste = (ImageView) findViewById(R.id.iv_paste);
        ivDelete = (ImageView) findViewById(R.id.iv_delete);

    }

    private void initActionData(String json, List<Map<String, Object>> list){
        try {
            JSONObject zuoJsonObject = new JSONObject(json);
            JSONArray zuoJsonArray= zuoJsonObject.getJSONArray("frame");
            for(int i=0; i<zuoJsonArray.length(); i++){
                Map<String, Object> map = new HashMap<String, Object>();
                JSONObject jsonObject = (JSONObject) zuoJsonArray.get(i);
                UbtLog.d(TAG, "jsonObject:" + jsonObject.toString());
                map.put(ACTION_TIME, jsonObject.get("-xmlRunTime"));
                map.put(ACTION_ANGLE, jsonObject.get("-xmldata"));
//                map.put(ACTION_NAME, jsonObject.get("xmlName"));
                list.add(i, map);
            }

            UbtLog.d(TAG, "list:" + list);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initActionLibs() {

        String chuzhao = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"220\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"39#13#18#91#149#137#83#59#75#110#100#102#120#104#70#80\",\n" +
                "        \"-xmlAllRunTime\": \"240\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"220\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"60#40#75#91#149#137#83#59#75#110#100#102#120#104#70#80\",\n" +
                "        \"-xmlAllRunTime\": \"240\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(chuzhao, listWarrior);

        String wanyao = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"440\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"90#17#90#90#160#90#85#76#112#88#95#95#102#71#86#85\",\n" +
                "        \"-xmlAllRunTime\": \"440\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1400\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"5#89#89#180#85#98#85#168#33#174#90#95#14#152#7#90\",\n" +
                "        \"-xmlAllRunTime\": \"1400\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(wanyao, listStoop);

        String dunxia = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"540\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"3\",\n" +
                "        \"-xmldata\": \"65#22#67#123#168#105#90#42#86#92#90#91#142#95#88#90\",\n" +
                "        \"-xmlAllRunTime\": \"540\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"540\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"3\",\n" +
                "        \"-xmldata\": \"38#23#3#140#180#157#90#22#9#140#90#91#157#172#42#90\",\n" +
                "        \"-xmlAllRunTime\": \"1660\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"540\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"3\",\n" +
                "        \"-xmldata\": \"60#20#61#117#160#125#90#63#76#110#90#91#120#106#72#90\",\n" +
                "        \"-xmlAllRunTime\": \"540\",\n" +
                "        \"-xmlFrameIndex\": \"3\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(dunxia, listSquat);

        String leftHand = " {\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"440\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"90#30#60#90#80#30#80#60#76#110#100#80#120#104#70#95\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"440\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"90#20#70#90#160#110#90#60#76#110#94#90#120#104#70#86\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(leftHand, listLeftHand);

        String rightHand = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"440\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"90#110#150#90#150#120#100#60#76#110#84#99#120#104#70#77\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"440\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"90#20#70#90#160#110#90#60#76#110#94#90#120#104#70#86\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(rightHand, listRightHand);

        String mechDance1 = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"420\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"47#13#20#109#22#12#81#53#57#120#96#83#118#113#62#95\",\n" +
                "        \"-xmlAllRunTime\": \"420\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"220\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"64#13#20#109#22#12#81#93#85#120#96#83#79#85#62#95\",\n" +
                "        \"-xmlAllRunTime\": \"440\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(mechDance1, listMechDance1);

        String mechDance2 = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"420\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"72#164#161#134#177#147#92#53#57#120#88#96#126#124#62#84\",\n" +
                "        \"-xmlAllRunTime\": \"420\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"220\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"72#164#161#118#177#147#92#78#76#120#88#96#100#99#62#85\",\n" +
                "        \"-xmlAllRunTime\": \"440\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";


        initActionData(mechDance2, listMechDance2);


        String hug= "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1000\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"17#27#57#162#144#133#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"1000\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"500\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"17#7#66#166#169#114#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"500\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"17#27#57#162#144#133#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"3\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1500\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"93#20#66#86#156#127#90#74#95#101#89#89#104#81#80#89\",\n" +
                "        \"-xmlAllRunTime\": \"1500\",\n" +
                "        \"-xmlFrameIndex\": \"4\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(hug, listHug);

        String happy = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"300\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"0#6#72#166#172#91#90#37#47#115#106#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"300\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"200\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"32#15#72#180#171#94#90#60#76#110#90#90#139#134#60#74\",\n" +
                "        \"-xmlAllRunTime\": \"200\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"200\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"0#6#72#166#172#91#90#37#47#115#106#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"200\",\n" +
                "        \"-xmlFrameIndex\": \"3\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"200\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"32#15#72#180#171#94#90#60#76#110#90#90#139#134#60#74\",\n" +
                "        \"-xmlAllRunTime\": \"200\",\n" +
                "        \"-xmlFrameIndex\": \"4\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"200\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"0#6#72#166#172#91#90#37#47#115#106#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"200\",\n" +
                "        \"-xmlFrameIndex\": \"5\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1000\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"90#5#90#89#172#91#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"1000\",\n" +
                "        \"-xmlFrameIndex\": \"6\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(happy, listHappy);

        String salute = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1000\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"3\",\n" +
                "        \"-xmldata\": \"95#15#76#86#170#97#90#74#95#101#89#89#104#81#80#89\",\n" +
                "        \"-xmlAllRunTime\": \"1000\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"500\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"3\",\n" +
                "        \"-xmldata\": \"95#15#76#55#37#0#90#74#95#101#89#89#104#81#80#89\",\n" +
                "        \"-xmlAllRunTime\": \"2000\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1500\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"3\",\n" +
                "        \"-xmldata\": \"93#20#66#86#156#127#90#74#95#101#89#89#104#81#80#89\",\n" +
                "        \"-xmlAllRunTime\": \"1500\",\n" +
                "        \"-xmlFrameIndex\": \"3\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";


        initActionData(salute, listSalute);

        String walk = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"460\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"127#16#72#134#160#111#90#37#47#115#106#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"460\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"39#16#72#64#164#111#90#60#76#110#90#90#139#134#60#74\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(walk, listWalk);

        String twist = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"420\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"91#45#95#124#175#155#79#59#75#110#100#91#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"420\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"220\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"90#45#95#132#172#163#58#73#70#119#110#70#101#95#67#100\",\n" +
                "        \"-xmlAllRunTime\": \"240\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"420\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"91#45#95#124#175#155#79#59#75#110#100#91#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"420\",\n" +
                "        \"-xmlFrameIndex\": \"3\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"220\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"90#45#95#132#172#163#58#73#70#119#110#70#101#95#67#100\",\n" +
                "        \"-xmlAllRunTime\": \"240\",\n" +
                "        \"-xmlFrameIndex\": \"4\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(twist, listTwist);

        String steppin = " {\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"440\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"90#160#90#90#20#90#90#60#76#110#110#90#120#104#70#70\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"440\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"90#20#90#90#160#90#90#60#76#110#94#90#120#104#70#86\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";


        initActionData(steppin, listSteppin);

        String bent = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1340\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"98#149#155#84#0#74#22#91#129#80#137#51#121#132#50#106\",\n" +
                "        \"-xmlAllRunTime\": \"1340\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1500\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"2\",\n" +
                "        \"-xmldata\": \"98#149#155#84#1#74#95#88#129#78#111#124#128#154#36#81\",\n" +
                "        \"-xmlAllRunTime\": \"1500\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(bent, listBent);

        String arm = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"500\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"5\",\n" +
                "        \"-xmldata\": \"88#24#82#92#152#101#90#37#47#115#106#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"500\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"5\",\n" +
                "        \"-xmldata\": \"87#170#82#92#24#101#90#60#76#110#90#90#139#134#60#74\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"500\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"5\",\n" +
                "        \"-xmldata\": \"87#66#82#92#117#101#90#37#47#115#106#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"3\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"500\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"5\",\n" +
                "        \"-xmldata\": \"88#24#82#92#152#101#90#60#76#110#90#90#139#134#60#74\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"4\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"500\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"5\",\n" +
                "        \"-xmldata\": \"88#29#49#91#150#132#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"5\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";


        initActionData(arm, listArm);

        String dance1 = " {\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"440\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"89#69#166#90#160#90#90#60#76#110#94#90#120#104#70#86\",\n" +
                "        \"-xmlAllRunTime\": \"600\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"440\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"89#68#166#94#118#6#90#60#76#110#94#90#120#104#70#86\",\n" +
                "        \"-xmlAllRunTime\": \"600\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"600\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"89#162#130#90#27#43#90#60#76#110#94#90#120#104#70#86\",\n" +
                "        \"-xmlAllRunTime\": \"700\",\n" +
                "        \"-xmlFrameIndex\": \"3\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"700\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"4\",\n" +
                "        \"-xmldata\": \"91#26#64#90#153#116#90#60#76#110#94#90#120#104#70#86\",\n" +
                "        \"-xmlAllRunTime\": \"700\",\n" +
                "        \"-xmlFrameIndex\": \"4\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        initActionData(dance1, listDance1);

        String dance2 = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"380\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"90#90#90#90#90#90#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"380\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"300\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"95#11#78#91#162#103#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"300\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"400\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"95#11#78#91#162#103#83#60#76#110#99#81#120#104#70#100\",\n" +
                "        \"-xmlAllRunTime\": \"400\",\n" +
                "        \"-xmlFrameIndex\": \"3\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"400\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"159#6#89#135#161#122#78#41#119#53#118#81#129#104#70#100\",\n" +
                "        \"-xmlAllRunTime\": \"460\",\n" +
                "        \"-xmlFrameIndex\": \"4\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"400\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"95#11#78#91#162#103#83#60#76#110#99#81#120#104#70#100\",\n" +
                "        \"-xmlAllRunTime\": \"440\",\n" +
                "        \"-xmlFrameIndex\": \"5\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"400\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"33#2#74#3#161#106#95#53#76#110#84#94#142#76#116#66\",\n" +
                "        \"-xmlAllRunTime\": \"460\",\n" +
                "        \"-xmlFrameIndex\": \"6\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"400\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"95#11#78#91#162#103#95#60#76#110#84#93#120#104#70#84\",\n" +
                "        \"-xmlAllRunTime\": \"440\",\n" +
                "        \"-xmlFrameIndex\": \"7\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"400\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"159#6#89#136#161#122#78#41#119#53#118#81#129#104#70#100\",\n" +
                "        \"-xmlAllRunTime\": \"460\",\n" +
                "        \"-xmlFrameIndex\": \"8\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"400\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"95#11#78#91#162#103#83#60#76#110#97#83#120#104#70#98\",\n" +
                "        \"-xmlAllRunTime\": \"440\",\n" +
                "        \"-xmlFrameIndex\": \"9\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"400\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"169#11#78#144#165#127#82#104#82#147#114#83#120#104#70#98\",\n" +
                "        \"-xmlAllRunTime\": \"460\",\n" +
                "        \"-xmlFrameIndex\": \"10\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"300\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"95#11#78#91#162#103#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"300\",\n" +
                "        \"-xmlFrameIndex\": \"11\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"200\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"95#11#78#91#162#103#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"200\",\n" +
                "        \"-xmlFrameIndex\": \"12\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"300\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"113#17#80#103#152#121#90#35#36#124#117#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"480\",\n" +
                "        \"-xmlFrameIndex\": \"13\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"300\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"71#12#85#64#166#108#90#60#76#110#93#90#138#131#62#73\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"14\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"300\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"113#17#80#103#152#121#90#35#36#124#117#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"480\",\n" +
                "        \"-xmlFrameIndex\": \"15\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"300\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"16\",\n" +
                "        \"-xmldata\": \"71#12#85#64#166#108#90#60#76#110#93#90#138#131#62#73\",\n" +
                "        \"-xmlAllRunTime\": \"500\",\n" +
                "        \"-xmlFrameIndex\": \"16\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";


        initActionData(dance2, listDance2);

        String curtain = " {\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1500\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"5\",\n" +
                "        \"-xmldata\": \"90#90#90#90#90#90#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"1500\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1500\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"5\",\n" +
                "        \"-xmldata\": \"120#40#59#133#133#107#90#57#140#70#90#90#125#46#108#90\",\n" +
                "        \"-xmlAllRunTime\": \"1500\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"800\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"5\",\n" +
                "        \"-xmldata\": \"120#35#39#133#163#157#90#57#140#70#90#90#125#46#108#90\",\n" +
                "        \"-xmlAllRunTime\": \"1500\",\n" +
                "        \"-xmlFrameIndex\": \"3\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"800\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"5\",\n" +
                "        \"-xmldata\": \"120#40#59#133#133#107#90#57#140#70#90#90#125#48#107#90\",\n" +
                "        \"-xmlAllRunTime\": \"800\",\n" +
                "        \"-xmlFrameIndex\": \"4\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"800\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"5\",\n" +
                "        \"-xmldata\": \"90#35#48#90#154#130#90#60#76#110#90#90#120#104#70#90\",\n" +
                "        \"-xmlAllRunTime\": \"800\",\n" +
                "        \"-xmlFrameIndex\": \"5\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";


        initActionData(curtain,listCurtain);

        String bye = "{\n" +
                "    \"frame\": [\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"820\",\n" +
                "        \"-xmlFrameStatus\": \"1\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"119#172#139#86#155#126#90#74#95#101#89#89#104#81#80#89\",\n" +
                "        \"-xmlAllRunTime\": \"820\",\n" +
                "        \"-xmlFrameIndex\": \"1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"200\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"119#150#121#86#155#126#90#74#95#101#89#89#104#81#80#89\",\n" +
                "        \"-xmlAllRunTime\": \"200\",\n" +
                "        \"-xmlFrameIndex\": \"2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"140\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"119#180#121#86#156#126#90#74#95#101#89#89#104#81#80#89\",\n" +
                "        \"-xmlAllRunTime\": \"140\",\n" +
                "        \"-xmlFrameIndex\": \"3\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"200\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"119#150#121#86#155#126#90#74#95#101#89#89#104#81#80#89\",\n" +
                "        \"-xmlAllRunTime\": \"200\",\n" +
                "        \"-xmlFrameIndex\": \"4\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"140\",\n" +
                "        \"-xmlFrameStatus\": \"2\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"119#180#121#86#156#126#90#74#95#101#89#89#104#81#80#89\",\n" +
                "        \"-xmlAllRunTime\": \"140\",\n" +
                "        \"-xmlFrameIndex\": \"5\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"-xmlRunTime\": \"1500\",\n" +
                "        \"-xmlFrameStatus\": \"3\",\n" +
                "        \"-xmlFrameAll\": \"6\",\n" +
                "        \"-xmldata\": \"93#20#66#86#156#127#90#74#95#101#89#89#104#81#80#89\",\n" +
                "        \"-xmlAllRunTime\": \"1500\",\n" +
                "        \"-xmlFrameIndex\": \"6\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";


        initActionData(bye, listBye);







        //初级动作
//        String actionJsonDate = "{\n" +
//                "    \"frame\": [\n" +
//                "      {\n" +
//                "        \"xmlRunTime\": \"1000\",\n" +
//                "        \"xmldata\": \"89#57#91#89#172#94#49#33#39#116#135#87#144#180#13#94\",\n" +
//                "        \"xmlName\": \"出右腿\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"xmlRunTime\": \"1000\",\n" +
//                "        \"xmldata\": \"89#5#90#89#132#88#91#37#0#162#86#137#124#124#57#43\",\n" +
//                "        \"xmlName\": \"出左腿\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"xmlRunTime\": \"1000\",\n" +
//                "        \"xmldata\": \"89#178#91#89#178#89#90#60#76#110#90#90#120#104#70#90\",\n" +
//                "        \"xmlName\": \"抬右手\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"xmlRunTime\": \"1000\",\n" +
//                "        \"xmldata\": \"89#0#92#90#1#88#90#60#76#110#90#90#120#104#70#90\",\n" +
//                "        \"xmlName\": \"抬左手\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"xmlRunTime\": \"1000\",\n" +
//                "        \"xmldata\": \"89#88#94#90#172#91#90#60#76#110#90#90#120#104#70#90\",\n" +
//                "        \"xmlName\": \"右手\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"xmlRunTime\": \"1000\",\n" +
//                "        \"xmldata\": \"89#0#92#90#91#88#90#60#76#110#90#90#120#104#70#90\",\n" +
//                "        \"xmlName\": \"左手\"\n" +
//                "      }\n" +
//                "    ]\n" +
//                "  }";
//
//        listActionLib = new ArrayList<Map<String, Object>>();
//
//        try {
//            JSONObject jsObject = new JSONObject(actionJsonDate);
//            JSONArray jsonArray= jsObject.getJSONArray("frame");
//            UbtLog.d(TAG, "jsonArray:" + jsonArray.toString());
//            for(int i=0; i<jsonArray.length(); i++){
//                Map<String, Object> map = new HashMap<String, Object>();
//                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                UbtLog.d(TAG, "jsonObject:" + jsonObject.toString());
//                map.put(ACTION_TIME, jsonObject.get("xmlRunTime"));
//                map.put(ACTION_ANGLE, jsonObject.get("xmldata"));
//                map.put(ACTION_NAME, basicAction[i]);
//                listActionLib.add(i, map);
//            }
//
//            Map<String, Object> tempMap = new HashMap<String, Object>();
//            tempMap.put(ACTION_NAME, basicAction[basicAction.length-1]);
//            listActionLib.add(tempMap);
//
//
//
//
//            UbtLog.d(TAG, "listActionLib:" + listActionLib.toString());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


//        String zuo = "{\n" +
//                "    \"frame\": [\n" +
//                "      {\n" +
//                "        \"-xmlFrameIndex\": \"1\",\n" +
//                "        \"-xmlAllRunTime\": \"760\",\n" +
//                "        \"-xmlFrameStatus\": \"1\",\n" +
//                "        \"-xmlFrameAll\": \"6\",\n" +
//                "        \"-xmlRunTime\": \"760\",\n" +
//                "        \"-xmldata\": \"87#42#12#81#129#178#90#60#76#110#90#90#120#104#70#90\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"-xmlFrameIndex\": \"2\",\n" +
//                "        \"-xmlAllRunTime\": \"600\",\n" +
//                "        \"-xmlFrameStatus\": \"2\",\n" +
//                "        \"-xmlFrameAll\": \"6\",\n" +
//                "        \"-xmlRunTime\": \"300\",\n" +
//                "        \"-xmldata\": \"0#3#86#84#133#180#90#60#76#110#90#90#137#133#59#75\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"-xmlFrameIndex\": \"3\",\n" +
//                "        \"-xmlAllRunTime\": \"760\",\n" +
//                "        \"-xmlFrameStatus\": \"2\",\n" +
//                "        \"-xmlFrameAll\": \"6\",\n" +
//                "        \"-xmlRunTime\": \"760\",\n" +
//                "        \"-xmldata\": \"87#42#12#81#129#178#90#60#76#110#90#90#120#104#70#90\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"-xmlFrameIndex\": \"4\",\n" +
//                "        \"-xmlAllRunTime\": \"600\",\n" +
//                "        \"-xmlFrameStatus\": \"2\",\n" +
//                "        \"-xmlFrameAll\": \"6\",\n" +
//                "        \"-xmlRunTime\": \"300\",\n" +
//                "        \"-xmldata\": \"0#3#86#84#133#180#90#60#76#110#90#90#137#133#59#75\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"-xmlFrameIndex\": \"5\",\n" +
//                "        \"-xmlAllRunTime\": \"780\",\n" +
//                "        \"-xmlFrameStatus\": \"2\",\n" +
//                "        \"-xmlFrameAll\": \"6\",\n" +
//                "        \"-xmlRunTime\": \"760\",\n" +
//                "        \"-xmldata\": \"87#42#12#81#129#178#90#60#76#110#90#90#120#104#70#90\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"-xmlFrameIndex\": \"6\",\n" +
//                "        \"-xmlAllRunTime\": \"600\",\n" +
//                "        \"-xmlFrameStatus\": \"3\",\n" +
//                "        \"-xmlFrameAll\": \"6\",\n" +
//                "        \"-xmlRunTime\": \"300\",\n" +
//                "        \"-xmldata\": \"0#3#86#84#133#180#90#60#76#110#90#90#137#133#59#75\"\n" +
//                "      }\n" +
//                "    ]\n" +
//                "  }";
//
//        try {
//
//
//            JSONObject zuoJsonObject = new JSONObject(zuo);
//            JSONArray zuoJsonArray= zuoJsonObject.getJSONArray("frame");
//            for(int i=0; i<zuoJsonArray.length(); i++){
//                Map<String, Object> map = new HashMap<String, Object>();
//                JSONObject jsonObject = (JSONObject) zuoJsonArray.get(i);
//                UbtLog.d(TAG, "jsonObject:" + jsonObject.toString());
//                map.put(ACTION_TIME, jsonObject.get("-xmlRunTime"));
//                map.put(ACTION_ANGLE, jsonObject.get("-xmldata"));
////                map.put(ACTION_NAME, jsonObject.get("xmlName"));
////                listBasicActionLib.add(i, map);
//            }
//
//            UbtLog.d(TAG, "listBasicActionLib:" + listBasicActionLib);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }






        listHighActionLib.clear();
        for(int i=0;  i<highActionName.length; i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ACTION_NAME, highActionName[i]);
            map.put(ACTION_ICON, advanceIconID[i]);
            listHighActionLib.add(i, map);
            UbtLog.d(TAG, "listHighActionLib:" + listHighActionLib);
        }

        listBasicActionLib.clear();
        for(int i=0; i<basicAction.length; i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ACTION_NAME, basicAction[i]);
            map.put(ACTION_ICON, basicIconID[i]);
            listBasicActionLib.add(i, map);
            UbtLog.d(TAG, "listBasicActionLib:" + listBasicActionLib);
        }


    }


    private void initSongs() {
        for(int i=0;  i<songs.length; i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(SONGS_NAME, songs[i]);
            map.put(SONGS_TYPE, 0);
            listSongs.add(i, map);
            UbtLog.d(TAG, "listSongs:" + listSongs);
        }

        for(int i=0; i<listRecord.size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
            String name = listRecord.get(i);
            name = name.substring(0, name.length()-4);
            UbtLog.d(TAG, "record name:" + name);
            map.put(SONGS_NAME, name);
            map.put(SONGS_TYPE, 1);
            listSongs.add(map);
        }

    }

    private List<String> listRecord = new ArrayList<String>();
    public List<String> readMP3RecordFiles(){
        UbtLog.d(TAG, "record:" + FileTools.readFiles(FileTools.record).toString());
        listRecord = FileTools.readFiles(FileTools.record);
        return listRecord;
    }


    @Override
    protected void initControlListener() {


        ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language = getStandardLocale(getAppCurrentLanguage());

//                String url = HttpAddress
//                        .getRequestUrl(HttpAddress.Request_type.action_help)
//                        + HttpAddress.getParamsForGet(
//                        new String[] { language },
//                        HttpAddress.Request_type.action_help);
//                UbtLog.d(TAG, "url:" +url);
                String url ="https://services.ubtrobot.com/actionHelp/actionHelp.html?lang=" + language;  //暂时这样
                UbtLog.d(TAG, "url:" +url);
                Intent intent = new Intent();
                intent.putExtra(WebContentActivity.SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                intent.putExtra(WebContentActivity.WEB_TITLE, "");
                intent.putExtra(WebContentActivity.WEB_URL, url);
                intent.setClass(ActionsCreateActivity.this, WebContentActivity.class);
                startActivity(intent);
            }
        });

        ivHandLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHandLeft.setSelected(true);
                if(lostLeftHand){
                    return;
                }
                ((ActionsEditHelper) mHelper).doLostLeftHandAndRead();
                lostLeftHand = true;
                ids.add(1);
                ids.add(2);
                ids.add(3);
                updateAddViewEnable();
                if(!lostRightLeg && !lostLeftLeg && !lostRightHand) {
//                    DialogTips dialogTips = new DialogTips(ActionsCreateActivity.this, ActionsCreateActivity.this.getStringResources("ui_create_click_robot"), 0, ActionsCreateActivity.this);
//                    dialogTips.show();
                }
            }
        });

        ivHandRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHandRight.setSelected(true);
                if(lostRightHand){
                    return;
                }
                lostRightHand = true;
                ((ActionsEditHelper) mHelper).doLostRightHandAndRead();
                ids.add(4);
                ids.add(5);
                ids.add(6);
                updateAddViewEnable();
                if(!lostRightLeg && !lostLeftLeg && !lostLeftHand) {
//                    DialogTips dialogTips = new DialogTips(ActionsCreateActivity.this, ActionsCreateActivity.this.getStringResources("ui_create_click_robot"), 0, ActionsCreateActivity.this);
//                    dialogTips.show();
                }

            }
        });

        ivLegLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lostLeftLeg){
                    return;
                }

                if(lostRightLeg == false){
//                    DialogTips dialog = new DialogTips(ActionsCreateActivity.this, ActionsCreateActivity.this.getStringResources("ui_create_holde_robot"), 1, ActionsCreateActivity.this);
//                    dialog.show();
                }else{
                    lostLeftLeg();
                }



            }
        });

        ivLegRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lostRightLeg){
                    return;
                }
                if(lostLeftLeg == false){
//                    DialogTips dialog = new DialogTips(ActionsCreateActivity.this, ActionsCreateActivity.this.getStringResources("ui_create_holde_robot"), 2, ActionsCreateActivity.this);
//                    dialog.show();
                }else{
                    lostRightLeg();
                }


            }
        });




        ivAddFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbtLog.d(TAG, "ivAddFrame");




                if(autoRead){
                    mHandler.removeMessages(MSG_AUTO_READ);
                    autoRead = false;
                    needAdd = false;
                    autoAng = "";
                    ivAddFrame.setImageResource(R.drawable.icon_add_nor);
                    setButtonEnable(true);
//                    DialogPreview dialogPreview = new DialogPreview(ActionsCreateActivity.this, list_autoFrames, ActionsCreateActivity.this);
//                    dialogPreview.show();
                    UbtLog.d(TAG, "list_autoFrames:" + list_autoFrames.toString());
                }else if(cut) {
                    adapter.notifyDataSetChanged();
                    adapter.setDefSelect(-1);
                    ivCancelChange.setVisibility(View.INVISIBLE);
                    cut = false;
                }
                else{
                    if(ids.size() <=0){
//                        DialogTips dialogTips = new DialogTips(ActionsCreateActivity.this, ActionsCreateActivity.this.getStringResources("ui_create_click_to_cutoff"), 0, ActionsCreateActivity.this);
//                        dialogTips.show();
                        goneEditFrameLayout();
                        adapter.setDefSelect(-1);
                        return;
                    }
                    if (((ActionsEditHelper) mHelper).getNewPlayerState() == NewActionPlayer.PlayerState.PLAYING) {
                        return;
                    }
                    needAdd = true;
                    readCount = ids.size();
                    UbtLog.d(TAG, "ivAddFrame:" + readCount);
                    readEngOneByOne();
                    goneEditFrameLayout();
                }


            }
        });

        adapter.setOnItemListener(new FrameRecycleViewAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view,int pos, Map<String, Object> data) {
                UbtLog.d(TAG, "getNewPlayerState:"  + ((ActionsEditHelper) mHelper).getNewPlayerState());

                if (((ActionsEditHelper) mHelper).getNewPlayerState() == NewActionPlayer.PlayerState.PLAYING) {
                    return;
                }

                if(musicTimes !=0){
                    if(pos == list_frames.size()-1){
                        return;
                    }
                }

                adapter.setDefSelect(pos);

                selectPos = pos;
                mCurrentEditItem = data;
                showEditFrameLayout();
                updateAddViewEnable();
//                ivAddFrame.setImageResource(R.drawable.icon_confirm_nor);
//                change = true;
//                ivCancelChange.setVisibility(View.VISIBLE);

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doBack();
            }
        });

        ivReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReset();
                resetState();
                ivAddFrame.setEnabled(true);
                ivAddFrame.setImageResource(R.drawable.icon_add_nor);
            }
        });

        ivAutoRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ids.size() <=0){
//                    DialogTips dialogTips = new DialogTips(ActionsCreateActivity.this, ActionsCreateActivity.this.getStringResources("ui_create_click_to_cutoff"), 0, ActionsCreateActivity.this);
//                    dialogTips.show();
                }else{
//                    DialogMusic dialogMusic = new DialogMusic(ActionsCreateActivity.this, ActionsCreateActivity.this, 1);
//                    dialogMusic.show();
                }


            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewAction();
            }
        });

        ivActionLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                initActionsData();
//                initActionLibs();
//                DialogActions dialogActions = new DialogActions(ActionsCreateActivity.this, getStringResources("ui_create_basic_action"), listBasicActionLib, 0);
//                dialogActions.show();
            }
        });

        ivActionLibMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                initActionsData();
//                initActionLibs();
//                DialogActions dialogActions = new DialogActions(ActionsCreateActivity.this, getStringResources("ui_create_advance_action"), listHighActionLib, 1);
//                dialogActions.show();
            }
        });

        ivActionBgm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBgm();
//                DialogActions dialogActions = new DialogActions(ActionsCreateActivity.this, getStringResources("ui_create_music"), listSongs, 2);
//                dialogActions.show();
            }
        });

        initEditFrameLayoutOnclick();

    }


    private void initBgm() {
        listRecord.clear();
        listSongs.clear();
        readMP3RecordFiles();
        initSongs();
    }

    private void initEditFrameLayoutOnclick() {

        rlEditFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlEditFrame.setVisibility(View.GONE);
                adapter.setDefSelect(-1);
            }
        });



        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActionsEditHelper) mHelper)
                        .doCtrlAllEng(((FrameActionInfo) mCurrentEditItem
                                .get(ActionsEditHelper.MAP_FRAME)).getData());
                resetState();
                ivAddFrame.setEnabled(true);
                ivAddFrame.setImageResource(R.drawable.icon_add_nor);
            }
        });

        ivChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ids.size() <=0){
                    goneEditFrameLayout();
//                    DialogTips dialogTips = new DialogTips(ActionsCreateActivity.this, ActionsCreateActivity.this.getStringResources("ui_create_click_to_cutoff"), 0, ActionsCreateActivity.this);
//                    dialogTips.show();
                    return;

                }
                change = true;
                ivCancelChange.setVisibility(View.VISIBLE);
                ivAddFrame.setImageResource(R.drawable.icon_confirm_nor);
//                goneEditFrameLayout();
                //改变最右边的添加按钮，并提示用户修改好动作后点击修改
//                ivAddFrame.setImageResource(getResources().getDrawable());

            }
        });

        ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy = true;
                ivPaste.setEnabled(true);
                ivPaste.setImageResource(R.drawable.icon_paste_nor);
                mCopyItem = mCurrentEditItem;
            }
        });

        ivCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cut = true;
                ivPaste.setEnabled(true);
                ivPaste.setImageResource(R.drawable.icon_paste_nor);
                mCutItem = mCurrentEditItem;
            }
        });

        ivPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String index = (String)mCurrentEditItem.get(ActionsEditHelper.MAP_FRAME_NAME);
                int index = selectPos;
                UbtLog.d(TAG, "index:" + index);
                if(copy){
                    list_frames.add(index+1, copyItem(mCopyItem));
                }else if(cut){
                    UbtLog.d(TAG, "index:" + index);
                    list_frames.add(index+1, copyItem(mCutItem));
                    list_frames.remove(mCutItem);
                    mCutItem.clear();
                }
                goneEditFrameLayout();
                adapter.notifyDataSetChanged();

            }
        });


        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_frames.remove(mCurrentEditItem);
                adapter.notifyDataSetChanged();
                adapter.setDefSelect(-1);
                goneEditFrameLayout();
                ivAddFrame.setImageResource(R.drawable.icon_add_nor);
                ivCancelChange.setVisibility(View.INVISIBLE);
            }
        });


        ivZoomPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbtLog.d(TAG, "currentPlus:" + currentPlus);

                if(list_frames.size() <= 0) {
                    return;
                }

                if(currentMinus != 1){
                    adapter.scaleItem(1);

                    currentPlus = 1;
                    currentMinus = 1;
                    if(timeDatas.size() >0){
                        timeAdapter.scaleItem(1);
                    }

                    tvZoomPlus.setText("");
                    tvZoomMinus.setText("");

                }else
                if(currentPlus  == 1){
                    adapter.scaleItem(2);
                    if(timeDatas.size() >0){
                        timeAdapter.scaleItem(2);
                    }
                    tvZoomPlus.setText("2");
                    currentPlus = 2;
                }else if(currentPlus == 2){
                    adapter.scaleItem(3);
                    if(timeDatas.size() >0){
                        timeAdapter.scaleItem(3);
                    }
                    tvZoomPlus.setText("3");
                    currentPlus = 3;
                }else if(currentPlus == 3) {
                    adapter.scaleItem(4);
                    if(timeDatas.size() >0){
                        timeAdapter.scaleItem(4);
                    }
                    tvZoomPlus.setText("4");
                    currentPlus = 4;
                }


            }
        });

        ivZoomMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UbtLog.d(TAG, "currentMinus:" + currentMinus);

                if(list_frames.size() <= 0) {
                    return;
                }


                if(currentPlus != 1){
                    adapter.scaleItem(1);
                    if(timeDatas.size() >0){
                        timeAdapter.scaleItem(1);
                    }
                    currentMinus = 1;
                    currentPlus = 1;
                    tvZoomPlus.setText("");
                    tvZoomMinus.setText("");
                }else
                if(currentMinus == 1){
                    adapter.scaleItem(-1);
                    if(timeDatas.size() >0){
                        timeAdapter.scaleItem(-1);
                    }
                    tvZoomMinus.setText("2");
                    currentMinus = -1;
                }else if(currentMinus == -1){
                    adapter.scaleItem(-2);
                    if(timeDatas.size() >0){
                        timeAdapter.scaleItem(-2);
                    }
                    tvZoomMinus.setText("3");
                    currentMinus = -2;
                }else if(currentMinus == -2){
                    adapter.scaleItem(-3);
                    if(timeDatas.size() >0){
                        timeAdapter.scaleItem(-3);
                    }
                    tvZoomMinus.setText("4");
                    currentMinus = -4;
                }
            }
        });

    }

    private void showEditFrameLayout() {
        rlEditFrame.setVisibility(View.VISIBLE);

    }

    private void goneEditFrameLayout() {
        rlEditFrame.setVisibility(View.GONE);
        copy = false;
//        cut = false;
        ivPaste.setEnabled(false);
        ivPaste.setImageResource(R.drawable.icon_paste_dis);
        adapter.setDefSelect(-1);
        ivAddFrame.setEnabled(true);
        ivAddFrame.setImageResource(R.drawable.icon_add_nor);


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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == event.ACTION_DOWN
                && event.getKeyCode() == event.KEYCODE_BACK) {

            ((ActionsEditHelper) mHelper).doActionCommand(ActionsEditHelper.Command_type.Do_Stop,
                    null);

            return doBack();

        }
        return false;
    }

    public void doReset() {
        UbtLog.d(TAG, "doReset");

        if(mHelper.getChargingState() && !SettingHelper.isPlayCharging(ActionsCreateActivity.this)){
            Toast.makeText(ActionsCreateActivity.this, ActionsCreateActivity.this.getResources().getString(R.string.ui_settings_play_during_charging_tips), Toast.LENGTH_SHORT).show();
            return;
        }

        String angles = "90#90#90#90#90#90#90#60#76#110#90#90#120#104#70#90";
        FrameActionInfo info = new FrameActionInfo();
        info.eng_angles = angles;

        info.eng_time = 600;
        info.totle_time = 600;

        Map map = new HashMap<String, Object>();
        map.put(ActionsEditHelper.MAP_FRAME, info);
        String item_name = ActionsCreateActivity.this.getStringResources("ui_readback_index");
        item_name = item_name.replace("#",  1 + "");
        //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
        map.put(ActionsEditHelper.MAP_FRAME_NAME, 1 + "");
        map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);

        ((ActionsEditHelper) mHelper)
                .doCtrlAllEng(((FrameActionInfo) map
                        .get(ActionsEditHelper.MAP_FRAME)).getData());


        ids.clear();


    }


    private boolean doBack() {

        if(musicTimes == 0){
            if (list_frames.size() < 1) {
                finish();
                return false;
            }
        }else{
            if (list_frames.size() < 2) {
                finish();
                return false;
            }
        }


        if(isSaveSuccess){
            finish();
            return true;
        }
        MyAlertDialog.getInstance(
                ActionsCreateActivity.this,

                getStringResources("ui_readback_quit_tip"),
                getStringResources("ui_common_cancel"),
                getStringResources("ui_common_save"), new IMessageListeter() {

                    @Override
                    public void onViewAction(boolean isOk) {
                        if (isOk) {
                            saveNewAction();
                        } else {
                            doReset();
                            if(mediaPlayer != null){
                                mediaPlayer.stop();
                                playFinish = true;
                            }
                            finish();
                        }
                    }
                }).show();
        return true;
    }


    private void saveNewAction() {

        if(musicTimes == 0){
            if (list_frames.size() < 1) {
                MyAlertDialog.getInstance(
                        ActionsCreateActivity.this,
                        getStringResources("ui_readback_not_null"),
                        getStringResources("ui_common_cancel"),
                        getStringResources("ui_common_confirm"), null).show();
                return;
            }
        }else{
            if (list_frames.size() < 2) {
                MyAlertDialog.getInstance(
                        ActionsCreateActivity.this,
                        getStringResources("ui_readback_not_null"),
                        getStringResources("ui_common_cancel"),
                        getStringResources("ui_common_confirm"), null).show();
                return;
            }
        }




        Intent inte = new Intent();
        inte.setClass(this, ActionsEditSaveActivity.class);
        inte.putExtra(ActionsEditHelper.NewActionInfo, PG.convertParcelable(getEditingActions()));
        inte.putExtra(SCHEME_ID,mSchemeId);
        inte.putExtra(SCHEME_NAME,mSchemeName);
        inte.putExtra(Constant.SCREEN_ORIENTATION, 0);
        if(mDir != ""){
            inte.putExtra(ActionsEditSaveActivity.MUSIC_DIR, mDir);
        }
        this.startActivityForResult(inte, ActionsEditHelper.SaveActionReq);
        MobclickAgent.onEvent(this.getApplication(), AlphaStatics.ON_NEW_ACTION);//用户点击保存表示使用一次动作编辑
    }

    private NewActionInfo getEditingActions() {

        List<FrameActionInfo> frames = new ArrayList<FrameActionInfo>();
        frames.add(FrameActionInfo.getDefaultFrame());
        if(musicTimes == 0){
            for (int i = 0; i < list_frames.size(); i++) {
                frames.add(((FrameActionInfo) list_frames.get(i).get(
                        ActionsEditHelper.MAP_FRAME)));
            }
        }else{
            for (int i = 0; i < list_frames.size()-1; i++) {
                frames.add(((FrameActionInfo) list_frames.get(i).get(
                        ActionsEditHelper.MAP_FRAME)));
            }
        }

        mCurrentNewAction.frameActions = frames;
        UbtLog.d(TAG, "mCurrentNewAction:" + mCurrentNewAction.frameActions.toString());
        return mCurrentNewAction;
    }

    private NewActionInfo getEditingPreviewActions() {
        List<FrameActionInfo> frames = new ArrayList<FrameActionInfo>();
        frames.add(FrameActionInfo.getDefaultFrame());
        for (int i = 0; i < list_autoFrames.size(); i++) {
            frames.add(((FrameActionInfo) list_autoFrames.get(i).get(
                    ActionsEditHelper.MAP_FRAME)));
        }
        mCurrentNewAction.frameActions = frames;
        UbtLog.d(TAG, "mCurrentNewAction:" + mCurrentNewAction.frameActions.toString());
        return mCurrentNewAction;
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
            map.put(ActionsEditHelper.MAP_FRAME_NAME, (list_frames.size() + 1));
            map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time + "ms");

            list_frames.add(map);
        }

    }


    @Override
    protected void initBoardCastListener() {

    }


    @Override
    public void onReadEng(byte[] eng_angle) {

        UbtLog.d(TAG, "onReadEng:" + needAdd);

        if(needAdd){
            UbtLog.d(TAG, "onReadEng:"+readCount + "--" + eng_angle[0] + "--" + eng_angle[1]);
            init[ByteHexHelper.byteToInt(eng_angle[0])-1] = String.valueOf(ByteHexHelper.byteToInt(eng_angle[1]));
            i++;
            readEngOneByOne();


        }

    }

    @Override
    public void onChangeActionFinish() {

    }

    @Override
    public void onPlaying() {
        UbtLog.d(TAG, "onPlaying");

    }

    @Override
    public void onPausePlay() {
        UbtLog.d(TAG, "onPausePlay");
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                ivPlay.setImageResource(R.drawable.button_play);
                setEnable(true);
                ivAddFrame.setEnabled(true);
                ivAddFrame.setImageResource(R.drawable.icon_add_nor);


            }
        });
    }

    @Override
    public void onFinishPlay() {
        UbtLog.d(TAG, "onFinishPlay");
        if(doPlayPreview){
            doPlayPreview = false;
        }
        isFinishFramePlay = true;
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if(playFinish){
                    ivPlay.setImageResource(R.drawable.button_play);
                    setEnable(true);
                }
                ivAddFrame.setEnabled(true);
                ivAddFrame.setImageResource(R.drawable.icon_add_nor);
                adapter.setPlayIndex(-1);

            }
        });


    }

    @Override
    public void onFrameDo(final int index) {
        UbtLog.d(TAG, "onFrameDo:" + index + "--doPlayPreview:" + doPlayPreview);
        if(doPlayPreview){
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (index == 0) {

                } else {
                    if(musicTimes  == 0){
                        layoutManager.scrollToPositionWithOffset(index-1, 0);
                    }

//                    layoutManager.setStackFromEnd(true);
//                    recyclerViewFrames.smoothScrollToPosition(index-1);
                    adapter.setPlayIndex(index-1);

                }
            }
        });


    }

    @Override
    public void notePlayChargingError() {

    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result, boolean result_state, long request_code) {

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result, long request_code) {

    }

    @Override
    public void onWriteDataFinish(long requestCode, FileTools.State state) {

    }

    @Override
    public void onReadCacheSize(int size) {

    }

    @Override
    public void onClearCache() {

    }


    @Override
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
//            layTitle.setVisibility(View.INVISIBLE);
//            lst_actions.setVisibility(View.INVISIBLE);
//            lay_change_frame.setVisibility(View.INVISIBLE);
//            lay_play.setVisibility(View.INVISIBLE);
            getSupportFragmentManager().beginTransaction().add(R.id.rl_fragment_content,saveSuccessFragment).commit();
        }
    }

    @Override
    public void onFragmentInteraction() {
        finish();
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }




    private int i=0;
    private void readEngOneByOne (){
        if(ids.size() >0){
            UbtLog.d(TAG, "readEngOneByOne:" + i);
            if(i==ids.size()){
                i=0;
                addFrame();
            }else{
                ((ActionsEditHelper) mHelper).doLostOnePower(ids.get(i));
            }

        }else{
            UbtLog.d(TAG,"laizhelile");
            change = false;
            adapter.setDefSelect(-1);
            ivAddFrame.setImageResource(R.drawable.icon_add_nor);
            ivCancelChange.setVisibility(View.INVISIBLE);
        }
    }



    private void addFrame() {
        String angles = "";
        for (int i = 0; i < init.length; i++) {
            angles += init[i] + "#";
        }

        UbtLog.d(TAG, "angles:" + angles);

        if (change) {
            change = false;
            ((FrameActionInfo) mCurrentEditItem
                    .get(ActionsEditHelper.MAP_FRAME)).eng_angles = angles;
            UbtLog.d(TAG, "addFrame time:" + adapter.getTime());
            int time = adapter.getTime();
            if(time != 10){
                ((FrameActionInfo) mCurrentEditItem
                        .get(ActionsEditHelper.MAP_FRAME)).totle_time = adapter.getTime();
            }

            ivAddFrame.setImageResource(R.drawable.icon_add_nor);
            ivCancelChange.setVisibility(View.INVISIBLE);
            adapter.setDefSelect(-1);
            adapter.setTime();
            adapter.notifyDataSetChanged();
        } else {

            FrameActionInfo info = new FrameActionInfo();
            info.eng_angles = angles;
            if(autoRead){
                info.eng_time = 200;
                info.totle_time = 200;
            }else{
                info.eng_time = 470;
                info.totle_time = 470;
            }


            Map map = new HashMap<String, Object>();
            map.put(ActionsEditHelper.MAP_FRAME, info);
            String item_name = ActionsCreateActivity.this.getStringResources("ui_readback_index");
            item_name = item_name.replace("#", (list_frames.size() + 1) + "");
            //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
            if(musicTimes ==0){
//                map.put(ActionsEditHelper.MAP_FRAME_NAME, (list_frames.size() + 1) + "");
                map.put(ActionsEditHelper.MAP_FRAME_NAME, (currentIndex ) + "");
            }else{
//                map.put(ActionsEditHelper.MAP_FRAME_NAME, list_frames.size()  + "");
                map.put(ActionsEditHelper.MAP_FRAME_NAME, currentIndex  + "");
            }

            map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);
            if(autoRead){
                UbtLog.d(TAG, "autoAng:" + autoAng + "angles:" + angles);

                if(autoAng.equals(angles)){
                    mHandler.sendEmptyMessage(MSG_AUTO_READ);
                    return;
                }else{
                    UbtLog.d(TAG, "autoAng:" + autoAng + "angles:" + angles);
                    if(autoAng.equals("")){
                        autoAng = angles;
                        list_autoFrames.add(map);
                    }else{
                        String [] auto = autoAng.split("#");
                        String [] ang = angles.split("#");
                        boolean isNeedAdd = false;
                        for(int i=0; i<auto.length; i++){

                            int abs =Integer.valueOf(auto[i])-Integer.valueOf(ang[i]);
                            if(Math.abs(abs)>5){
                                autoAng = angles;
                                isNeedAdd = true;
                                break;
                            }
                        }
                        if(!isNeedAdd){
                            UbtLog.d(TAG, "no need");
                            mHandler.sendEmptyMessage(MSG_AUTO_READ);
                            return;
                        }else{
                            list_autoFrames.add(map);
                        }
                    }

                }

            }

            UbtLog.d(TAG, "list_frames add");
            if(musicTimes == 0){
                list_frames.add(map);
                currentIndex ++;
            }else{
               // 添加位置和最后补全的时间计算
                handleFrameAndTime(map);
            }

            adapter.notifyDataSetChanged();
            if(musicTimes ==0){
                recyclerViewFrames.smoothScrollToPosition(list_frames.size()-1);
            }else{
                recyclerViewFrames.smoothScrollToPosition(list_frames.size()-2);
            }


        }
        needAdd = false;
        UbtLog.d(TAG, "continue read！");
        if(autoRead){
            mHandler.sendEmptyMessage(MSG_AUTO_READ);
        }
    }

    private void handleFrameAndTime(Map<String, Object> map){
        if(list_frames.size()==0){
            UbtLog.d(TAG, "list_frames size 0");
            list_frames.add(list_frames.size(),map);
            currentIndex ++;
            adapter.notifyDataSetChanged();
        }else {
            list_frames.add(list_frames.size()-1, map);
            currentIndex ++;
            handleFrameTime();


        }
    }


    private void handleFrameTime(){

        int time = 0;

        for(int i=0; i<list_frames.size()-1; i++){
            time += (int)list_frames.get(i).get(ActionsEditHelper.MAP_FRAME_TIME);
        }

        UbtLog.d(TAG, "handleFrameTime time:" + time + "---musicTimes:" + musicTimes);

        int backupTime = musicTimes -time;
        UbtLog.d(TAG, "handleFrameTime backupTime:" + backupTime);
        if(backupTime <=0){
            list_frames.remove(list_frames.size()-1);
            adapter.notifyDataSetChanged();
            return;
        }


        FrameActionInfo info = new FrameActionInfo();
        info.eng_angles = "";

        info.eng_time = backupTime;
        info.totle_time = backupTime;

        Map map = new HashMap<String, Object>();
        map.put(ActionsEditHelper.MAP_FRAME, info);
        String item_name = ActionsCreateActivity.this.getStringResources("ui_readback_index");
        item_name = item_name.replace("#", (list_frames.size() + 1) + "");
        //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
        map.put(ActionsEditHelper.MAP_FRAME_NAME, (list_frames.size() + 1) + "");
        map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);
        list_frames.set(list_frames.size()-1, map);

        adapter.setMusicTime(backupTime);
        adapter.notifyDataSetChanged();
        layoutManager.scrollToPosition(list_frames.size()-2);
//        layoutManager.scrollToPositionWithOffset(list_frames.size()-2, 0);
//        recyclerViewFrames.smoothScrollToPosition(list_frames.size()-2);


    }





    private void updateAddViewEnable() {
        ivAddFrame.setEnabled(true);
        ivAddFrame.setImageResource(R.drawable.icon_add_nor);
    }



    public static final int MSG_AUTO_READ = 1000;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_AUTO_READ){
                needAdd = true;
                UbtLog.d(TAG, "adddddd:" + autoRead);

                if(autoRead){
                    readEngOneByOne();
                }

            }else if(msg.what == 0){

                //更新进度
                if(playFinish){
                    return;
                }
                int position = mediaPlayer.getCurrentPosition();
                UbtLog.d(TAG, "msg what 0 position:" + position);
                if(position != 0){
                    tvMusicTime.setText(TimeUtils.getTimeFromMillisecond((long)position));
                }

                int time = mediaPlayer.getDuration();
                int max = sbVoice.getMax();

                sbVoice.setProgress(mediaPlayer.getCurrentPosition());
            }else if(msg.what == 1){
                if(playFinish){
                    ivPlay.setImageResource(R.drawable.button_play);
                }
            }
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        UbtLog.d(TAG, "onStop");
//        if(mediaPlayer != null){
//            mediaPlayer.release();
//        }
//        playFinish = true;
        if(actionGuideView != null){
            actionGuideView.closeAppGuideView();
            actionGuideView = null;
        }

        if(mediaPlayer!= null && mediaPlayer.isPlaying() && !mDir.equals("")){
            ivPlay.setImageResource(R.drawable.button_play);
            pause();
            doPlayCurrentFrames();
            playFinish = true;
        }




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
        playFinish = true;

    }

    public String  getnum(int num1, int num2){
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) num1 / (float) num2 * 100);
        return result;
    }



    private List<Map<String, Object>> previewList = new ArrayList<Map<String, Object>>();


    private  Date lastTime_play = null;

    private NewActionInfo getPreviewActions() {
        List<FrameActionInfo> frames = new ArrayList<FrameActionInfo>();
        frames.add(FrameActionInfo.getDefaultFrame());
        for (int i = 0; i < previewList.size(); i++) {
            frames.add(((FrameActionInfo) previewList.get(i).get(
                    ActionsEditHelper.MAP_FRAME)));
        }
        mCurrentNewAction.frameActions = frames;
        UbtLog.d(TAG, "mCurrentNewAction:" + mCurrentNewAction.frameActions.toString());
        return mCurrentNewAction;
    }


    String mCurrentSourcePath;



    public void setMusic() {
        try {

            UbtLog.d(TAG, "setMusic");
            //先清除之前的标记
            mDir = "";
            sbVoice.setVisibility(View.INVISIBLE);
            timeDatas.clear();
            if(timeAdapter != null){
                timeAdapter.notifyDataSetChanged();
            }
            tvMusicTime.setText("00:00");

            if(list_frames.size() >0){
                if(musicTimes !=0){
                    list_frames.remove(list_frames.size()-1);
                    adapter.setMusicTime(0);
                    adapter.notifyDataSetChanged();
                }

            }
            musicTimes =0;



            mDir = mCurrentSourcePath;
            sbVoice.setVisibility(View.VISIBLE);

            mediaPlayer.reset();
            mediaPlayer.setDataSource(mDir);
            mediaPlayer.prepareAsync();//数据缓冲
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
//                    mp.start();
                    mp.seekTo(0);
                    sbVoice.setProgress(0);
                    musicTimes = handleMusicTime(mediaPlayer.getDuration());
                    UbtLog.d(TAG, "play musicTimes:" + musicTimes);
                    long time  = musicTimes;
                    tvMusicTime.setVisibility(View.VISIBLE);
                    tvMusicTime.setText(TimeUtils.getTimeFromMillisecond(time));
                    initTimeFrame();
                    sbVoice.setMax(mediaPlayer.getDuration());
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private void doPlayCurrentFrames() {

        UbtLog.d(TAG, "state:" + ((ActionsEditHelper) mHelper).getNewPlayerState());
        resetState();
        if (((ActionsEditHelper) mHelper).getNewPlayerState() == NewActionPlayer.PlayerState.PLAYING) {
            ((ActionsEditHelper) mHelper).doActionCommand(ActionsEditHelper.Command_type.Do_pause_or_continue,
                    getEditingActions());


        } else if(((ActionsEditHelper) mHelper).getNewPlayerState() == NewActionPlayer.PlayerState.PAUSING){
            ((ActionsEditHelper) mHelper).doActionCommand(ActionsEditHelper.Command_type.Do_pause_or_continue,
                    getEditingActions());
        }
        else {

//            if(musicTimes !=0){
//                if(list_frames.size()>1){
//                    setEnable(false);
//                }
//
//            }else{
//                if(list_frames.size()>0){
//                    setEnable(false);
//                }
//            }

            setEnable(false);

            if(musicTimes != 0){
                if(list_frames.size() <2){
                    return;
                }
            }else{
                if(list_frames.size() == 0){
                    return;
                }
            }

            isFinishFramePlay = false;

            if(mDir != "" && mediaPlayer != null){
                if(mediaPlayer.getCurrentPosition() == 0){
                    UbtLog.d(TAG, "只在音频播完状态下才可以从头开始播:" + mediaPlayer.getCurrentPosition());
                    ((ActionsEditHelper) mHelper).doActionCommand(ActionsEditHelper.Command_type.Do_play,
                            getEditingActions());

                }
            }else {
                ((ActionsEditHelper) mHelper).doActionCommand(ActionsEditHelper.Command_type.Do_play,
                        getEditingActions());
            }


        }



    }




    public void lostLeftLeg() {
        ivLegLeft.setSelected(true);
        lostLeftLeg = true;
        ((ActionsEditHelper) mHelper).doLostLeftFootAndRead();
        ids.add(7);
        ids.add(8);
        ids.add(9);
        ids.add(10);
        ids.add(11);
        updateAddViewEnable();
        if(!lostRightLeg && !lostRightHand && !lostLeftHand) {
//            DialogTips dialogTips = new DialogTips(ActionsCreateActivity.this, ActionsCreateActivity.this.getStringResources("ui_create_click_robot"), 0, ActionsCreateActivity.this);
//            dialogTips.show();
        }
    }

    public void lostRightLeg() {
        ivLegRight.setSelected(true);
        lostRightLeg = true;
        ((ActionsEditHelper) mHelper).doLostRightFootAndRead();
        ids.add(12);
        ids.add(13);
        ids.add(14);
        ids.add(15);
        ids.add(16);
        updateAddViewEnable();
        if(!lostLeftLeg && !lostRightHand && !lostLeftHand) {
//            DialogTips dialogTips = new DialogTips(ActionsCreateActivity.this, ActionsCreateActivity.this.getStringResources("ui_create_click_robot"), 0, ActionsCreateActivity.this);
//            dialogTips.show();
        }
    }



    private void setButtonEnable(boolean enable){
        ivReset.setEnabled(enable);
        ivAutoRead.setEnabled(enable);
        ivSave.setEnabled(enable);
        ivActionLib.setEnabled(enable);
        ivActionLibMore.setEnabled(enable);
        ivActionBgm.setEnabled(enable);
        ivPlay.setEnabled(enable);
        ivHelp.setEnabled(enable);
    }

    private void setEnable(boolean enable){
        ivReset.setEnabled(enable);
        ivAutoRead.setEnabled(enable);
        ivSave.setEnabled(enable);
        ivActionLib.setEnabled(enable);
        ivActionLibMore.setEnabled(enable);
        ivActionBgm.setEnabled(enable);
        ivHelp.setEnabled(enable);
    }


    private void resetState(){
        lostLeftHand = false;
        lostRightHand = false;
        lostLeftLeg = false;
        lostRightLeg = false;
        ivHandLeft.setSelected(false);
        ivHandRight.setSelected(false);
        ivLegLeft.setSelected(false);
        ivLegRight.setSelected(false);
        ivAddFrame.setEnabled(false);
        ivAddFrame.setImageResource(R.drawable.icon_add_dis);
        ids.clear();
    }


    public void cancelAutoData(){
        int count = list_autoFrames.size();
        currentIndex = currentIndex - count;
        UbtLog.d(TAG, "count:" + count);
        for(int i=0; i<count; i++){
            list_frames.remove(list_frames.size()-1);
        }
        adapter.notifyDataSetChanged();
    }


    public static String accuracy(double num, double total, int scale){
        DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();
        //可以设置精确几位小数
        df.setMaximumFractionDigits(scale);
        //模式 例如四舍五入
        df.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy_num = num / total * 100;
        return df.format(accuracy_num);
    }





}
