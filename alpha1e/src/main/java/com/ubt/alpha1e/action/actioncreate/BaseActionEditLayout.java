package com.ubt.alpha1e.action.actioncreate;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.model.ActionConstant;
import com.ubt.alpha1e.action.model.ActionDataModel;
import com.ubt.alpha1e.action.model.PrepareDataModel;
import com.ubt.alpha1e.action.model.PrepareMusicModel;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.business.NewActionPlayer;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.FrameActionInfo;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.FrameRecycleViewAdapter;
import com.ubt.alpha1e.ui.TimesHideRecycleViewAdapter;
import com.ubt.alpha1e.ui.TimesRecycleViewAdapter;
import com.ubt.alpha1e.ui.custom.ActionGuideView;
import com.ubt.alpha1e.ui.dialog.DialogTips;
import com.ubt.alpha1e.ui.fragment.SaveSuccessFragment;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.SettingHelper;
import com.ubt.alpha1e.utils.TimeUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;

import java.io.File;
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
 * @author：liuhai
 * @date：2017/11/15 15:30
 * @modifier：ubt
 * @modify_date：2017/11/15 15:30
 * [A brief description]
 * version
 */

public abstract class BaseActionEditLayout extends LinearLayout implements View.OnClickListener, PrepareActionUtil.OnDialogListener, DialogTips.OnLostClickListener, DialogMusic.OnMusicDialogListener {
    private static final String TAG = "BaseActionEditLayout";

    private ImageView ivRobot;
    private ImageView ivHandLeft, ivHandRight, ivLegLeft, ivLegRight;
    private RecyclerView recyclerViewFrames;
    private List<Map<String, Object>> list_frames;

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
    private int current = 0;

//    private String init = "\"90#90#90#90#90#90#90#60#76#110#90#90#120#104#70#90\"";

    public String[] init = {"90", "90", "90", "90", "90", "90", "90", "60", "76", "110", "90", "90",
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

    private List<Map<String, Object>> listActionLib;
    public static final String ACTION_TIME = "action_time";
    public static final String ACTION_ANGLE = "action_angle";
    public static final String ACTION_NAME = "action_name";
    public static final String ACTION_ICON = "action_icon";


    private List<Map<String, Object>> listHighActionLib = new ArrayList<Map<String, Object>>();
    private String[] highActionName;
    private int[] advanceIconID;
    private String[] basicAction;
    private int[] basicIconID;
    private List<Map<String, Object>> listBasicActionLib = new ArrayList<Map<String, Object>>();


    private String[] songs = {"", "flexin", "jingle bells", "london bridge is falling down",
            "twinkle twinkle little star", "yankee doodle dandy", "kind of light", "so good",
            "Sun Indie Pop", "The little robot", "zombie"};
    public static final String SONGS_NAME = "songs_name";
    public static final String SONGS_TYPE = "songs_type"; //用来区分是内置音乐还是录音
    private List<Map<String, Object>> listSongs = new ArrayList<Map<String, Object>>();


    private ImageView ivCancelChange;

    private float density = 1;

    private ImageView ivDeleteMusic;
    private RelativeLayout rl_delete_music;
    private ImageView iv_del_music;
    private TextView tvDeleteMusic;

    private int time;

    private ActionGuideView actionGuideView;

    private boolean doPlayPreview = false;

    private RecyclerView recyclerViewTimesHide;
    private TimesHideRecycleViewAdapter timeHideAdapter;
    private LinearLayoutManager layoutManagerTimeHide;

    private boolean isFinishFramePlay = true;

    public static String BACK_UP = "back_up";

    private int currentIndex = 1;
    private int scroll = 0;

    private Context mContext;
    PrepareActionUtil mPrepareActionUtil;
    PrepareMusicUtil mPrepareMusicUtil;

    private BaseHelper mHelper;

    public BaseActionEditLayout(Context context) {
        super(context);
        mContext = context;
        init(context);
    }


    public BaseActionEditLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    public BaseActionEditLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    public abstract int getLayoutId();

    public void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        initUI();
    }

    public void setUp(BaseHelper baseHelper) {
        this.mHelper = baseHelper;
    }

    /**
     * 初始化UI
     */
    protected void initUI() {
        mCurrentNewAction = new NewActionInfo();
        ivPlay = (ImageView) findViewById(R.id.iv_play_music);
        tvMusicTime = (TextView) findViewById(R.id.tv_play_time);
        ivPlay.setOnClickListener(this);
        ivCancelChange = (ImageView) findViewById(R.id.iv_cancel_update);
        ivCancelChange.setOnClickListener(this);

        ivRobot = (ImageView) findViewById(R.id.iv_robot);
        ivHandLeft = (ImageView) findViewById(R.id.iv_hand_left);
        ivHandLeft.setOnClickListener(this);
        ivHandRight = (ImageView) findViewById(R.id.iv_hand_right);
        ivHandRight.setOnClickListener(this);
        ivLegLeft = (ImageView) findViewById(R.id.iv_leg_left);
        ivLegLeft.setOnClickListener(this);
        ivLegRight = (ImageView) findViewById(R.id.iv_leg_right);
        ivLegRight.setOnClickListener(this);
        initRobot();

        recyclerViewFrames = (RecyclerView) findViewById(R.id.rcv_actions);
        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewFrames.setLayoutManager(layoutManager);
        list_frames = new ArrayList<Map<String, Object>>();
        adapter = new FrameRecycleViewAdapter(mContext, list_frames, density);
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
                if (touch == 1) {
                    return;
                }

                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    if (touch == 2) {
                        UbtLog.d(TAG, "onScrolled recyclerViewFrames  dx:" + dx);
                        recyclerViewTimes.scrollBy(dx, dy);
                        recyclerViewTimesHide.scrollBy(dx, dy);
                    }

                }

            }
        });

        recyclerViewFrames.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (musicTimes != 0 && list_frames.size() == 1) {
                    return true;
                }
                touch = 2;
                return false;
            }
        });

        ivAddFrame = (ImageView) findViewById(R.id.iv_add_frame);
        ivAddFrame.setEnabled(true);
        ivAddFrame.setImageResource(R.drawable.icon_add_nor);
        ivAddFrame.setOnClickListener(this);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivReset = (ImageView) findViewById(R.id.iv_reset);
        ivReset.setOnClickListener(this);
        ivAutoRead = (ImageView) findViewById(R.id.iv_auto_read);
        ivSave = (ImageView) findViewById(R.id.iv_save_action);
        ivHelp = (ImageView) findViewById(R.id.iv_help);
        ivHelp.setOnClickListener(this);

        ivActionLib = (ImageView) findViewById(R.id.iv_action_lib);
        ivActionLib.setOnClickListener(this);
        ivActionLibMore = (ImageView) findViewById(R.id.iv_action_lib_more);
        ivActionLibMore.setOnClickListener(this);
        ivActionBgm = (ImageView) findViewById(R.id.iv_action_bgm);
        ivActionBgm.setOnClickListener(this);
        initEditFrameLayout();
//        rlRoot = (RelativeLayout) findViewById(R.id.rl_action_edit);
        recyclerViewTimes = (RecyclerView) findViewById(R.id.rcv_time);
        layoutManagerTime = new LinearLayoutManager(mContext);
        layoutManagerTime.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewTimes.setLayoutManager(layoutManagerTime);

        recyclerViewTimesHide = (RecyclerView) findViewById(R.id.rcv_time_hide);
        layoutManagerTimeHide = new LinearLayoutManager(mContext);
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
                if (recyclerViewTimesHide.getVisibility() == View.VISIBLE) {
//                    sbVoice.setProgress(Integer.valueOf(pro)*100);
                }

            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                UbtLog.d(TAG, "onScrolled recyclerViewTimesHide");
                recyclerViewTimes.scrollBy(dx, dy);
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

                if (timeDatas.size() <= 0) {
                    return;
                }
                float a = Float.valueOf(getnum(progress, musicTimes));
                UbtLog.d(TAG, "progress a:" + a + Math.round(a));

                timePosition = (musicTimes / 100) * Math.round(a) / 100;
                if (timePosition <= 0) {
                    timePosition = 0;
                } else {
                    timePosition = timePosition - 1;
                }

                UbtLog.d(TAG, "timePosition:" + timePosition);

//                recyclerViewTimes.smoothScrollToPosition(timePosition);
                UbtLog.d(TAG, "1progress:" + Math.round(a) + "rate:" + Math.round(a) / 10 + "timePosition:" + timePosition);
                int rate = Math.round(a) / 10;

                if (rate == 10) {
                    UbtLog.d(TAG, "timePosition:" + timePosition);
                    layoutManagerTime.scrollToPositionWithOffset(timePosition, 0);
                    layoutManagerTimeHide.scrollToPositionWithOffset(timePosition, 0);
                    layoutManagerTimeHide.setStackFromEnd(true);

                } else {
                    if (rate == 0) {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    } else if (rate == 1) {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    } else if (rate == 2) {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate - 1, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate - 1, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    } else if (rate == 3) {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate - 2, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate - 2, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    } else if (rate == 4) {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate - 3, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate - 3, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    } else if (rate == 5) {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate - 4, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate - 4, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    } else if (rate == 6) {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate - 5, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate - 5, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    } else if (rate == 7) {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate - 6, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate - 6, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    } else if (rate == 8) {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate - 7, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate - 7, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    } else if (rate == 9) {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate - 8, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate - 8, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    } else {
                        layoutManagerTime.scrollToPositionWithOffset(timePosition - rate - 1, 0);
                        layoutManagerTimeHide.scrollToPositionWithOffset(timePosition - rate - 1, 0);
                        layoutManagerTimeHide.setStackFromEnd(true);
                    }


                }

//                layoutManagerTime.setStackFromEnd(true);
                UbtLog.d(TAG, "recyclerViewFrames smoothScrollToPosition 1:" + scroll);
                recyclerViewFrames.scrollBy((musicTimes) * Math.round(a) / 100 - scroll, 0);
//                recyclerViewTimes.scrollBy((musicTimes)*Math.round(a)/100-scroll+10, 0);
//                recyclerViewTimesHide.scrollBy((musicTimes)*Math.round(a)/100-scroll,0);
                scroll = (musicTimes) * Math.round(a) / 100;
//                recyclerViewFrames.smoothScrollToPosition(timePosition);

//                recyclerViewTimesHide.smoothScrollToPosition(timePosition);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UbtLog.d(TAG, "22 onStartTrackingTouch:" + currentTimeMillis());
                if (timeDatas.size() <= 0) {
                    return;
                }
                clickTime = currentTimeMillis();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                UbtLog.d(TAG, "22 onStopTrackingTouch:" + currentTimeMillis());

                long dur = System.currentTimeMillis() - clickTime;
                if (System.currentTimeMillis() - clickTime < 100) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(TIME, "100");
                    if (timeDatas.get(timePosition).get(SHOW).equals("1")) {

                        map.put(SHOW, "1");
                    } else {
                        map.put(SHOW, "1");
                    }
                    UbtLog.d(TAG, "timePosition:" + timePosition + "--dur:" + dur);
                    timeDatas.set(timePosition, map);
                    timeAdapter.notifyDataSetChanged();
                    timeHideAdapter.notifyDataSetChanged();
                } else {

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

                if (mediaPlayer != null) {
                    mediaPlayer.getDuration();

                }
            }
        });

        ivResetIndex = (ImageView) findViewById(R.id.iv_reset_index);
        ivResetIndex.setOnClickListener(this);
        ivDeleteMusic = (ImageView) findViewById(R.id.iv_music_icon);
        ivDeleteMusic.setOnClickListener(this);
        rl_delete_music = (RelativeLayout) findViewById(R.id.rl_delete_music);
        tvDeleteMusic = (TextView) findViewById(R.id.tv_del_music);
        iv_del_music = (ImageView) findViewById(R.id.iv_del_music);
        iv_del_music.setOnClickListener(this);
    }


    private void initEditFrameLayout() {
        rlEditFrame = (RelativeLayout) findViewById(R.id.lay_frame_data_edit);
        ivPreview = (ImageView) findViewById(R.id.iv_preview);
        ivChange = (ImageView) findViewById(R.id.iv_change);
        ivCopy = (ImageView) findViewById(R.id.iv_copy);
        ivCut = (ImageView) findViewById(R.id.iv_cut);
        tvCut = (TextView) findViewById(R.id.tv_cut);
        ivPaste = (ImageView) findViewById(R.id.iv_paste);
        ivDelete = (ImageView) findViewById(R.id.iv_delete);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel_update:
                break;
            case R.id.iv_play_music:
                break;
            case R.id.iv_del_music:
                break;
            case R.id.iv_music_icon:
                break;

            case R.id.iv_reset:
                doReset();
                resetState();
                ivAddFrame.setEnabled(true);
                ivAddFrame.setImageResource(R.drawable.icon_add_nor);
                break;
            case R.id.iv_reset_index:
                break;
            case R.id.sb_voice:
                break;
            case R.id.iv_help:
                ToastUtils.showShort("删除");
                break;

            case R.id.iv_add_frame:
                addFrameOnClick();
                break;
            case R.id.iv_hand_left:
                lostLeft();
                break;
            case R.id.iv_hand_right:
                lostRight();
                break;
            case R.id.iv_leg_left:
                if (lostLeftLeg) {
                    return;
                }
                if (lostRightLeg == false) {
                    showLostDialog(1, ResourceManager.getInstance(mContext).getStringResources("ui_create_holde_robot"));
                } else {
                    lostLeftLeg();
                }
                break;
            case R.id.iv_leg_right:
                if (lostRightLeg) {
                    return;
                }
                if (lostLeftLeg == false) {
                    showLostDialog(2, ResourceManager.getInstance(mContext).getStringResources("ui_create_holde_robot"));
                } else {
                    lostRightLeg();
                }
                break;

            case R.id.iv_action_lib:
                showPrepareActionDialog(1);
                break;
            case R.id.iv_action_lib_more:
                showPrepareActionDialog(2);
                break;
            case R.id.iv_action_bgm:
                showPrepareMusicDialog();
                break;
            default:
        }
    }

    /**
     * 左侧基础动作跟高级动作
     *
     * @param type 1基础动作 2高级动作
     */
    public void showPrepareActionDialog(int type) {
        if (null == mPrepareActionUtil) {
            mPrepareActionUtil = new PrepareActionUtil(mContext);
        }
        mPrepareActionUtil.showActionDialog(type, this);
    }


    public void showPrepareMusicDialog() {
        if (null == mPrepareMusicUtil) {
            mPrepareMusicUtil = new PrepareMusicUtil(mContext);
        }
        mPrepareMusicUtil.showMusicDialog(this);
    }

    /**
     * 初始化Robot图片宽高
     */
    private void initRobot() {
        // 获取屏幕密度（方法2）
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        float density = dm.density;
        if (AlphaApplication.isPad()) {
            UbtLog.d(TAG, "Pad Robot 1");
        } else {
            ivRobot.setLayoutParams(ActionConstant.getIvRobotParams(density, ivRobot));
            UbtLog.d(TAG, "ivRobot:" + ivRobot.getWidth() + "/" + ivRobot.getHeight());
            ivHandLeft.setLayoutParams(ActionConstant.getIvRobotParams(density, ivHandLeft));
            UbtLog.d(TAG, "ivHandLeft:" + ivHandLeft.getWidth() + "/" + ivHandLeft.getHeight());
            ivHandRight.setLayoutParams(ActionConstant.getIvRobotParams(density, ivHandRight));
            UbtLog.d(TAG, "ivHandRight:" + ivHandRight.getWidth() + "/" + ivHandRight.getHeight());
            ivLegLeft.setLayoutParams(ActionConstant.getIvRobotParams(density, ivLegLeft));
            UbtLog.d(TAG, "ivLegLeft:" + ivLegLeft.getWidth() + "/" + ivLegLeft.getHeight());
            ivLegRight.setLayoutParams(ActionConstant.getIvRobotParams(density, ivLegRight));
            UbtLog.d(TAG, "ivLegRight:" + ivLegRight.getWidth() + "/" + ivLegRight.getHeight());
        }

    }

    /**
     * 基本动作和高级动作回调添加
     *
     * @param prepareDataModel
     */
    @Override
    public void onActionConfirm(PrepareDataModel prepareDataModel) {
        List<ActionDataModel> list = prepareDataModel.getList();
        for (int i = 0; i < list.size(); i++) {
            String time = list.get(i).getXmlRunTime();
            String angles = list.get(i).getXmldata();

            FrameActionInfo info = new FrameActionInfo();
            info.eng_angles = angles;
            info.eng_time = Integer.valueOf(time);      //暂时用100ms,用实际的有点问题
            info.totle_time = Integer.valueOf(time);

            Map addMap = new HashMap<String, Object>();
            addMap.put(ActionsEditHelper.MAP_FRAME, info);
            String item_name = ResourceManager.getInstance(mContext).getStringResources("ui_readback_index");
            addMap.put(ActionsEditHelper.MAP_FRAME_NAME, (currentIndex) + "");
            addMap.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);

            UbtLog.d(TAG, "list_frames size:" + list_frames.size());
            if (musicTimes == 0) {
                list_frames.add(addMap);
                currentIndex++;
            } else {
                handleFrameAndTime(addMap);
            }

        }
        adapter.notifyDataSetChanged();
    }

    public static final int MSG_AUTO_READ = 1000;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_AUTO_READ) {
                needAdd = true;
                UbtLog.d(TAG, "adddddd:" + autoRead);

                if (autoRead) {
                    readEngOneByOne();
                }

            } else if (msg.what == 0) {

                //更新进度
                if (playFinish) {
                    return;
                }
                int position = mediaPlayer.getCurrentPosition();
                UbtLog.d(TAG, "msg what 0 position:" + position);
                if (position != 0) {
                    tvMusicTime.setText(TimeUtils.getTimeFromMillisecond((long) position));
                }

                int time = mediaPlayer.getDuration();
                int max = sbVoice.getMax();

                sbVoice.setProgress(mediaPlayer.getCurrentPosition());
            } else if (msg.what == 1) {
                if (playFinish) {
                    ivPlay.setImageResource(R.drawable.button_play);
                }
            }
        }
    };

    public void addFrameOnClick() {
        UbtLog.d(TAG, "ivAddFrame");

        if (autoRead) {
            mHandler.removeMessages(MSG_AUTO_READ);
            autoRead = false;
            needAdd = false;
            autoAng = "";
            ivAddFrame.setImageResource(R.drawable.icon_add_nor);
            setButtonEnable(true);
//            DialogPreview dialogPreview = new DialogPreview(ActionsNewEditActivity.this, list_autoFrames, ActionsNewEditActivity.this);
//            dialogPreview.show();
            UbtLog.d(TAG, "list_autoFrames:" + list_autoFrames.toString());
        } else if (cut) {
            adapter.notifyDataSetChanged();
            adapter.setDefSelect(-1);
            ivCancelChange.setVisibility(View.INVISIBLE);
            cut = false;
        } else {
            if (ids.size() <= 0) {
                showLostDialog(0, ResourceManager.getInstance(mContext).getStringResources("ui_create_click_to_cutoff"));

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

    /**
     * 音乐回调
     */
    @Override
    public void onMusicConfirm(PrepareMusicModel prepareMusicModel) {
        String name = prepareMusicModel.getMusicName();
        int songType = prepareMusicModel.getMusicType();
        UbtLog.d(TAG, "name:" + name + "songType:" + songType);
        setPlayFile(name + ".mp3", songType);
    }




    String mCurrentSourcePath;
    private void setPlayFile(String fileName, int type) {

        mCurrentSourcePath = FileTools.tmp_file_cache + "/" + fileName;
        boolean isFileCreateSuccess = false;
        if (type == 0) {
            isFileCreateSuccess = FileTools.writeAssetsToSd("music/" + fileName, mContext, mCurrentSourcePath);
        } else if (type == 1) {
            isFileCreateSuccess = FileTools.copyFile(FileTools.record + File.separator + fileName, mCurrentSourcePath, true);
        }

        UbtLog.d(TAG, "isFileCreateSuccess:" + isFileCreateSuccess);
        if (isFileCreateSuccess) {

            UbtLog.d(TAG, "mDir:" + mDir);

            if (mDir.equals("")) {
                try {
//                    mDir = mCurrentSourcePath;
//                    sbVoice.setVisibility(View.VISIBLE);
                    setMusic();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                DialogMusic dialogMusic = new DialogMusic(mContext, this, 0);
                dialogMusic.show();
            }


        }

    }

    @Override
    public void setMusic() {
        try {

            UbtLog.d(TAG, "setMusic");
            //先清除之前的标记
            mDir = "";
            sbVoice.setVisibility(View.INVISIBLE);
            timeDatas.clear();
            if (timeAdapter != null) {
                timeAdapter.notifyDataSetChanged();
            }
            tvMusicTime.setText("00:00");

            if (list_frames.size() > 0) {
                if (musicTimes != 0) {
                    list_frames.remove(list_frames.size() - 1);
                    adapter.setMusicTime(0);
                    adapter.notifyDataSetChanged();
                }

            }
            musicTimes = 0;

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
                    long time = musicTimes;
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

    @Override
    public void startAutoRead() {
        setButtonEnable(false);

        autoRead = true;
        ivAddFrame.setImageResource(R.drawable.icon_stop_nor);
        mHandler.sendEmptyMessage(MSG_AUTO_READ);
    }

    private void initTimeFrame() {

//        timeDatas = new ArrayList<Map<String, Object>>();
        Map<String, Object> timeMap = new HashMap<String, Object>();
        for (int i = 0; i < musicTimes / 100; i++) {
            timeMap.put(TIME, "100");
            timeMap.put(SHOW, "0");
            timeDatas.add(timeMap);
        }
        UbtLog.d(TAG, "size:" + timeDatas.size());

        timeHideAdapter = new TimesHideRecycleViewAdapter(mContext, timeDatas);
        recyclerViewTimesHide.setAdapter(timeHideAdapter);
        timeHideAdapter.setOnItemListener(new TimesHideRecycleViewAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, int pos, Map<String, Object> data) {
                UbtLog.d(TAG, "onItemClick pos:" + pos);
                data.put(SHOW, "0");
                data.put(TIME, "100");
                timeDatas.set(pos, data);
                timeHideAdapter.notifyDataSetChanged();
                timeAdapter.notifyDataSetChanged();
            }
        });

        timeAdapter = new TimesRecycleViewAdapter(mContext, timeDatas);
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
                if (touch == 2) {
                    return;

                }
                UbtLog.d(TAG, "ivZhen scrollBy dx:" + dx + "-dy:" + dy);
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
        if (list_frames.size() == 0) {
            FrameActionInfo info = new FrameActionInfo();
            info.eng_angles = "";

            info.eng_time = musicTimes;
            info.totle_time = musicTimes;

            Map map = new HashMap<String, Object>();
            map.put(ActionsEditHelper.MAP_FRAME, info);
            String item_name = ResourceManager.getInstance(mContext).getStringResources("ui_readback_index");
            item_name = item_name.replace("#", (list_frames.size() + 1) + "");
            //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
            map.put(ActionsEditHelper.MAP_FRAME_NAME, (list_frames.size() + 1) + "");
            map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);
            list_frames.add(list_frames.size(), map);
            adapter.setMusicTime(musicTimes);
            adapter.notifyDataSetChanged();


        } else {

            //根据当前已添加的动作帧时间计算补全帧时长
            handleAddFrame();

        }

    }

    private void handleAddFrame() {

        int time = 0;

        for (int i = 0; i < list_frames.size(); i++) {
            time += (int) list_frames.get(i).get(ActionsEditHelper.MAP_FRAME_TIME);
        }

        UbtLog.d(TAG, "handleAddFrame time:" + time);

        int backupTime = musicTimes - time;
        UbtLog.d(TAG, "handleAddFrame backupTime:" + backupTime);
        if (backupTime <= 0) {
            return;
        }

        FrameActionInfo info = new FrameActionInfo();
        info.eng_angles = "";

        info.eng_time = backupTime;
        info.totle_time = backupTime;

        Map map = new HashMap<String, Object>();
        map.put(ActionsEditHelper.MAP_FRAME, info);
        String item_name = ResourceManager.getInstance(mContext).getStringResources("ui_readback_index");
        item_name = item_name.replace("#", (list_frames.size() + 1) + "");
        //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
        map.put(ActionsEditHelper.MAP_FRAME_NAME, (list_frames.size() + 1) + "");
        map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);
        list_frames.add(list_frames.size(), map);

        adapter.setMusicTime(backupTime);
        adapter.notifyDataSetChanged();


    }

    private int handleMusicTime(int time) {
        int handleTime = 0;
        int yushu = time % 100;
        handleTime = time + (100 - yushu);
        UbtLog.d(TAG, "handleTime:" + handleTime);
        return handleTime;

    }

    @Override
    public void playAction(PrepareDataModel prepareDataModel) {
        UbtLog.d(TAG, "previewAction:" + prepareDataModel.toString());
        for (int i = 0; i < prepareDataModel.getList().size(); i++) {
            String time = prepareDataModel.getList().get(i).getXmlRunTime();
            String angles = prepareDataModel.getList().get(i).getXmldata();

            FrameActionInfo info = new FrameActionInfo();
            info.eng_angles = angles;
            info.eng_time = Integer.valueOf(time);
            info.totle_time = Integer.valueOf(time);

            Map addMap = new HashMap<String, Object>();
            addMap.put(ActionsEditHelper.MAP_FRAME, info);
            String item_name = ResourceManager.getInstance(mContext).getStringResources("ui_readback_index");
            item_name = item_name.replace("#", (list_frames.size() + 1) + "");
            //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
            addMap.put(ActionsEditHelper.MAP_FRAME_NAME, (list_frames.size() + 1) + "");
            addMap.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);

            UbtLog.d(TAG, "list_frames size:" + list_frames.size());
            previewList.add(addMap);
        }
        doPlayPreviewFrames();
        previewList.clear();
    }


    /**
     * 左手掉电
     */
    public void lostLeft() {
        ivHandLeft.setSelected(true);
        if (lostLeftHand) {
            return;
        }
        ((ActionsEditHelper) mHelper).doLostLeftHandAndRead();
        lostLeftHand = true;
        ids.add(1);
        ids.add(2);
        ids.add(3);
        updateAddViewEnable();
        if (!lostRightLeg && !lostLeftLeg && !lostRightHand) {
            showLostDialog(0, ResourceManager.getInstance(mContext).getStringResources("ui_create_click_robot"));
        }
    }

    /**
     * 右手掉电
     */
    public void lostRight() {
        ivHandRight.setSelected(true);
        if (lostRightHand) {
            return;
        }
        lostRightHand = true;
        ids.add(4);
        ids.add(5);
        ids.add(6);
        updateAddViewEnable();
        if (!lostRightLeg && !lostLeftLeg && !lostLeftHand) {
            ((ActionsEditHelper) mHelper).doLostRightHandAndRead();
            showLostDialog(0, ResourceManager.getInstance(mContext).getStringResources("ui_create_click_robot"));
        }
    }


    @Override
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
        if (!lostRightLeg && !lostRightHand && !lostLeftHand) {
            showLostDialog(0, ResourceManager.getInstance(mContext).getStringResources("ui_create_click_robot"));

        }
    }

    @Override
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
        if (!lostLeftLeg && !lostRightHand && !lostLeftHand) {
            showLostDialog(0, ResourceManager.getInstance(mContext).getStringResources("ui_create_click_robot"));
        }
    }


    private void updateAddViewEnable() {
        ivAddFrame.setEnabled(true);
        ivAddFrame.setImageResource(R.drawable.icon_add_nor);
    }


    /**
     * 掉电提示
     *
     * @param type
     * @param content
     */
    public void showLostDialog(final int type, String content) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_tips, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        TextView tvContent = contentView.findViewById(R.id.tv_content);
        tvContent.setText(content);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.6); //设置宽度
        DialogPlus.newDialog(mContext)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentWidth(width)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        dialog.dismiss();
                        if (type == 1) {
                            lostLeftLeg();
                        } else if (type == 2) {
                            lostLeftLeg();
                        }
                    }
                })
                .setCancelable(true)
                .create().show();


    }

    public void doReset() {
        UbtLog.d(TAG, "doReset");

        if (mHelper.getChargingState() && !SettingHelper.isPlayCharging(mContext)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.ui_settings_play_during_charging_tips), Toast.LENGTH_SHORT).show();
            return;
        }

        String angles = "90#90#90#90#90#90#90#60#76#110#90#90#120#104#70#90";
        FrameActionInfo info = new FrameActionInfo();
        info.eng_angles = angles;

        info.eng_time = 600;
        info.totle_time = 600;

        Map map = new HashMap<String, Object>();
        map.put(ActionsEditHelper.MAP_FRAME, info);
        String item_name = ResourceManager.getInstance(mContext).getStringResources("ui_readback_index");
        item_name = item_name.replace("#", 1 + "");
        //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
        map.put(ActionsEditHelper.MAP_FRAME_NAME, 1 + "");
        map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);

        ((ActionsEditHelper) mHelper)
                .doCtrlAllEng(((FrameActionInfo) map
                        .get(ActionsEditHelper.MAP_FRAME)).getData());

        ids.clear();


    }

    private void resetState() {
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

    private void setButtonEnable(boolean enable) {
        ivReset.setEnabled(enable);
        ivAutoRead.setEnabled(enable);
        ivSave.setEnabled(enable);
        ivActionLib.setEnabled(enable);
        ivActionLibMore.setEnabled(enable);
        ivActionBgm.setEnabled(enable);
        ivPlay.setEnabled(enable);
        ivHelp.setEnabled(enable);
    }

    private void setEnable(boolean enable) {
        ivReset.setEnabled(enable);
        ivAutoRead.setEnabled(enable);
        ivSave.setEnabled(enable);
        ivActionLib.setEnabled(enable);
        ivActionLibMore.setEnabled(enable);
        ivActionBgm.setEnabled(enable);
        ivHelp.setEnabled(enable);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    private int i = 0;

    private void readEngOneByOne() {
        if (ids.size() > 0) {
            UbtLog.d(TAG, "readEngOneByOne:" + i + "  ids.size==" + ids.size());
            if (i == ids.size()) {
                i = 0;
                addFrame();
            } else {
                ((ActionsEditHelper) mHelper).doLostOnePower(ids.get(i));
            }

        } else {
            UbtLog.d(TAG, "laizhelile");
            change = false;
            adapter.setDefSelect(-1);
            ivAddFrame.setImageResource(R.drawable.icon_add_nor);
            ivCancelChange.setVisibility(View.INVISIBLE);
        }
    }


    public void changeCurrentItemTime(int time) {
        ((FrameActionInfo) mCurrentEditItem
                .get(ActionsEditHelper.MAP_FRAME)).totle_time = time;
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
            if (time != 10) {
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
            if (autoRead) {
                info.eng_time = 200;
                info.totle_time = 200;
            } else {
                info.eng_time = 470;
                info.totle_time = 470;
            }

            Map map = new HashMap<String, Object>();
            map.put(ActionsEditHelper.MAP_FRAME, info);
            String item_name = ResourceManager.getInstance(mContext).getStringResources("ui_readback_index");
            item_name = item_name.replace("#", (list_frames.size() + 1) + "");
            //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
            if (musicTimes == 0) {
//                map.put(ActionsEditHelper.MAP_FRAME_NAME, (list_frames.size() + 1) + "");
                map.put(ActionsEditHelper.MAP_FRAME_NAME, (currentIndex) + "");
            } else {
//                map.put(ActionsEditHelper.MAP_FRAME_NAME, list_frames.size()  + "");
                map.put(ActionsEditHelper.MAP_FRAME_NAME, currentIndex + "");
            }

            map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);
            if (autoRead) {
                UbtLog.d(TAG, "autoAng:" + autoAng + "angles:" + angles);

                if (autoAng.equals(angles)) {
                    mHandler.sendEmptyMessage(MSG_AUTO_READ);
                    return;
                } else {
                    UbtLog.d(TAG, "autoAng:" + autoAng + "angles:" + angles);
                    if (autoAng.equals("")) {
                        autoAng = angles;
                        list_autoFrames.add(map);
                    } else {
                        String[] auto = autoAng.split("#");
                        String[] ang = angles.split("#");
                        boolean isNeedAdd = false;
                        for (int i = 0; i < auto.length; i++) {

                            int abs = Integer.valueOf(auto[i]) - Integer.valueOf(ang[i]);
                            if (Math.abs(abs) > 5) {
                                autoAng = angles;
                                isNeedAdd = true;
                                break;
                            }
                        }
                        if (!isNeedAdd) {
                            UbtLog.d(TAG, "no need");
                            mHandler.sendEmptyMessage(MSG_AUTO_READ);
                            return;
                        } else {
                            list_autoFrames.add(map);
                        }
                    }

                }

            }

            UbtLog.d(TAG, "list_frames add");
            if (musicTimes == 0) {
                list_frames.add(map);
                currentIndex++;
            } else {
                // 添加位置和最后补全的时间计算
                handleFrameAndTime(map);
            }

            adapter.notifyDataSetChanged();
            if (musicTimes == 0) {
                recyclerViewFrames.smoothScrollToPosition(list_frames.size() - 1);
            } else {
                recyclerViewFrames.smoothScrollToPosition(list_frames.size() - 2);
            }


        }
        needAdd = false;
        UbtLog.d(TAG, "continue read！");
        if (autoRead) {
            mHandler.sendEmptyMessage(MSG_AUTO_READ);
        }
    }

    private void handleFrameAndTime(Map<String, Object> map) {
        if (list_frames.size() == 0) {
            UbtLog.d(TAG, "list_frames size 0");
            list_frames.add(list_frames.size(), map);
            currentIndex++;
            adapter.notifyDataSetChanged();
        } else {
            list_frames.add(list_frames.size() - 1, map);
            currentIndex++;
            handleFrameTime();


        }
    }


    private void handleFrameTime() {

        int time = 0;

        for (int i = 0; i < list_frames.size() - 1; i++) {
            time += (int) list_frames.get(i).get(ActionsEditHelper.MAP_FRAME_TIME);
        }

        UbtLog.d(TAG, "handleFrameTime time:" + time + "---musicTimes:" + musicTimes);

        int backupTime = musicTimes - time;
        UbtLog.d(TAG, "handleFrameTime backupTime:" + backupTime);
        if (backupTime <= 0) {
            list_frames.remove(list_frames.size() - 1);
            adapter.notifyDataSetChanged();
            return;
        }

        FrameActionInfo info = new FrameActionInfo();
        info.eng_angles = "";

        info.eng_time = backupTime;
        info.totle_time = backupTime;

        Map map = new HashMap<String, Object>();
        map.put(ActionsEditHelper.MAP_FRAME, info);
        String item_name = ResourceManager.getInstance(mContext).getStringResources("ui_readback_index");
        item_name = item_name.replace("#", (list_frames.size() + 1) + "");
        //map.put(ActionsEditHelper.MAP_FRAME_NAME, item_name);
        map.put(ActionsEditHelper.MAP_FRAME_NAME, (list_frames.size() + 1) + "");
        map.put(ActionsEditHelper.MAP_FRAME_TIME, info.totle_time);
        list_frames.set(list_frames.size() - 1, map);

        adapter.setMusicTime(backupTime);
        adapter.notifyDataSetChanged();
        layoutManager.scrollToPosition(list_frames.size() - 2);
//        layoutManager.scrollToPositionWithOffset(list_frames.size()-2, 0);
//        recyclerViewFrames.smoothScrollToPosition(list_frames.size()-2);

    }

    private List<Map<String, Object>> previewList = new ArrayList<Map<String, Object>>();


    private Date lastTime_play = null;

    private void doPlayPreviewFrames() {

        if (mHelper.getChargingState() && !SettingHelper.isPlayCharging(mContext)) {
            Toast.makeText(mContext, ResourceManager.getInstance(mContext).getStringResources("ui_settings_play_during_charging_tips"), Toast.LENGTH_SHORT).show();
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

        resetState();
        ivAddFrame.setEnabled(true);
        ivAddFrame.setImageResource(R.drawable.icon_add_nor);

        doPlayPreview = true;
        if (((ActionsEditHelper) mHelper).getNewPlayerState() == NewActionPlayer.PlayerState.PLAYING) {
            UbtLog.d(TAG, "doPlayPreviewFrames Do_Stop");
            ((ActionsEditHelper) mHelper).doActionCommand(ActionsEditHelper.Command_type.Do_Stop,
                    getPreviewActions());


        } else {
            UbtLog.d(TAG, "doPlayPreviewFrames Do_play doPlayPreview:" + doPlayPreview);
            ((ActionsEditHelper) mHelper).doActionCommand(ActionsEditHelper.Command_type.Do_play,
                    getPreviewActions());
            doPlayPreview = true;


        }
    }


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


    public String getnum(int num1, int num2) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) num1 / (float) num2 * 100);
        return result;
    }


    public static String accuracy(double num, double total, int scale) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //可以设置精确几位小数
        df.setMaximumFractionDigits(scale);
        //模式 例如四舍五入
        df.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy_num = num / total * 100;
        return df.format(accuracy_num);
    }

    public void onPlaying() {

    }


    public void onPausePlay() {

    }


    public void onFinishPlay() {

    }


    public void onFrameDo(int index) {

    }


    public void onReadEng(byte[] eng_angle) {

        UbtLog.d(TAG, "onReadEng:" + needAdd);

        if (needAdd) {
            UbtLog.d(TAG, "onReadEng:" + readCount + "--" + eng_angle[0] + "--" + eng_angle[1]);
            init[ByteHexHelper.byteToInt(eng_angle[0]) - 1] = String.valueOf(ByteHexHelper.byteToInt(eng_angle[1]));
            i++;
            readEngOneByOne();


        }
    }

}
