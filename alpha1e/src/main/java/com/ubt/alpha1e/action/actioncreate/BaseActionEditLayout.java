package com.ubt.alpha1e.action.actioncreate;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.FrameRecycleViewAdapter;
import com.ubt.alpha1e.ui.TimesHideRecycleViewAdapter;
import com.ubt.alpha1e.ui.TimesRecycleViewAdapter;
import com.ubt.alpha1e.ui.custom.ActionGuideView;
import com.ubt.alpha1e.ui.fragment.SaveSuccessFragment;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author：liuhai
 * @date：2017/11/15 15:30
 * @modifier：ubt
 * @modify_date：2017/11/15 15:30
 * [A brief description]
 * version
 */

public abstract class BaseActionEditLayout extends LinearLayout {
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

    private Context mContext;


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
    }


}
