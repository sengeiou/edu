package com.ubt.alpha1e.maincourse.courselayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.actioncreate.BaseActionEditLayout;
import com.ubt.alpha1e.action.model.PrepareDataModel;
import com.ubt.alpha1e.action.model.PrepareMusicModel;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.maincourse.adapter.ActionCourseTwoUtil;
import com.ubt.alpha1e.maincourse.adapter.CourseItemAdapter;
import com.ubt.alpha1e.maincourse.adapter.CourseMusicDialogUtil;
import com.ubt.alpha1e.maincourse.adapter.CourseProgressListener;
import com.ubt.alpha1e.maincourse.model.CourseOne1Content;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import zhy.com.highlight.HighLight;


/**
 * @author：liuhai
 * @date：2017/11/20 15:08
 * @modifier：ubt
 * @modify_date：2017/11/20 15:08
 * [A brief description]
 * version
 */

public class CourseLevelFourLayout extends BaseActionEditLayout implements ActionCourseTwoUtil.OnCourseDialogListener, CourseMusicDialogUtil.OnMusicDialogListener {
    private String TAG = CourseLevelFourLayout.class.getSimpleName();
    private ImageView ivMusicArror;
    private ImageView ivRightArrow;
    private ImageView playArrow;

    private ImageView ivActionMore;
    private ImageView ivMoreArrow;
    AnimationDrawable animation1;
    AnimationDrawable animation2;
    AnimationDrawable animation3;
    private RelativeLayout rlActionCenter;
    private ImageView ivCenterAction;

    RelativeLayout mRlInstruction;
    private TextView mTextView;
    private boolean isInstruction;

    CourseMusicDialogUtil mMusicDialogUtil;//音乐对话框

    ActionCourseTwoUtil mActionCourseTwoUtil;//动作对话框

    /**
     * 高亮对话框的TextView显示
     */
    TextView tv;


    private List<CourseOne1Content> mOne1ContentList = new ArrayList<>();

    CourseProgressListener courseProgressListener;
    /**
     * 当前课时
     */
    private int currentCourse = 1;

    /**
     * 课时一
     */
    private int currentIndex = 0;
    //课时3顺序
    private int secondIndex = 0;
    private HighLight mHightLight;

    public CourseLevelFourLayout(Context context) {
        super(context);
    }

    public CourseLevelFourLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseLevelFourLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutId() {
        return R.layout.action_course_one;
    }

    /**
     * 设置课程数据,开始播放数据
     *
     * @param courseProgressListener 回调监听
     */
    public void setData(CourseProgressListener courseProgressListener) {
        mOne1ContentList.clear();
        mOne1ContentList.addAll(ActionCourseDataManager.getCardOneList(mContext));
        int level = 1;// 当前第几个课时
        LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);
        if (null != record) {
            UbtLog.d(TAG, "record===" + record.toString());
            int course = record.getCourseLevel();
            int recordlevel = record.getPeriodLevel();
            if (course == 4) {
                if (recordlevel == 0 || recordlevel == 2) {
                    level = 1;
                } else if (recordlevel == 1) {
                    level = 2;
                }
            }
        }
        this.currentCourse = level;
        this.courseProgressListener = courseProgressListener;
        setLayoutByCurrentCourse();
        isSaveAction = true;
    }

    /**
     * 根据当前是第几个关卡显示对应的提示
     * 根据当前课时显示界面
     */
    public void setLayoutByCurrentCourse() {
        setImageViewBg();
        UbtLog.d(TAG, "currentCourse==" + currentCourse);
        if (currentCourse == 1) {
            isInstruction = true;
            mRlInstruction.setVisibility(View.VISIBLE);
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor16.hts");
        } else if (currentCourse == 2) {
            showMusicArrow(true);
        } else if (currentCourse == 3) {
            showPlayArrow1(true);
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor17.hts");
        }

    }

    /**
     * 初始化数据
     *
     * @param context
     */
    @Override
    public void init(Context context) {
        super.init(context);
        isOnCourse = true;
        ivAddFrame.setEnabled(false);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_disable);
        setImageViewBg();
        playArrow = findViewById(R.id.iv_play_arrow);
        playArrow.setOnClickListener(this);

        ivMusicArror = findViewById(R.id.iv_music_arrow);
        ivMusicArror.setOnClickListener(this);
        ivRightArrow = findViewById(R.id.iv_add_frame_arrow);
        rlActionCenter = findViewById(R.id.rl_action_animal);
        ivCenterAction = findViewById(R.id.iv_center_action);

        ivActionMore = findViewById(R.id.iv_action_lib_more);
        ivActionMore.setOnClickListener(this);
        ivMoreArrow = findViewById(R.id.iv_add_action_arrow1);
        ivMoreArrow.setOnClickListener(this);

        mRlInstruction = (RelativeLayout) findViewById(R.id.rl_instruction);
        mTextView = (TextView) findViewById(R.id.tv_all_introduc);
        mTextView.setText("现在，让我们完整地添加一个有音乐的舞蹈动作吧！");
    }

    /**
     * 设置按钮不可点击
     */
    public void setImageViewBg() {
        ivReset.setEnabled(false);
        ivAutoRead.setEnabled(false);
        ivSave.setEnabled(false);
        ivActionLib.setEnabled(false);
        ivActionLibMore.setEnabled(false);
        ivActionBgm.setEnabled(false);
        ivPlay.setEnabled(false);
        ivHelp.setEnabled(false);
        ivAddFrame.setEnabled(false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回按钮
                if (null != courseProgressListener) {
                    courseProgressListener.finishActivity();
                }
                break;

            case R.id.iv_action_bgm:
                showMusicDialog();
                break;

            case R.id.iv_music_arrow:
                showMusicDialog();
                break;

            case R.id.iv_action_lib_more:
                showActionDialog();
                break;

            case R.id.iv_add_action_arrow1:
                showActionDialog();
                break;

            case R.id.iv_play_music:
                playAction();
                break;
            case R.id.iv_play_arrow:
                playAction();
                break;


            default:
        }
    }

    /**
     * 点击弹出音乐对话框
     */
    private void showMusicDialog() {
        showMusicArrow(false);
        ((ActionsEditHelper) mHelper).stopSoundAudio();
        if (null == mMusicDialogUtil) {
            mMusicDialogUtil = new CourseMusicDialogUtil(mContext);
        }
        mMusicDialogUtil.showMusicDialog(3, this);
    }


    /**
     * 显示动作对话框
     */
    public void showActionDialog() {
        ((ActionsEditHelper) mHelper).stopSoundAudio();
        if (null == mActionCourseTwoUtil) {
            mActionCourseTwoUtil = new ActionCourseTwoUtil(mContext);
        }

        showActionMoreArrow(false);
        mActionCourseTwoUtil.showActionDialog(2, this);

    }

    /**
     * 播放按钮，过3秒钟结束
     */
    private void playAction() {
        ((ActionsEditHelper) mHelper).stopAction();
        startPlayAction();
        showPlayArrow1(false);
        ivPlay.setEnabled(false);
        ivPlay.setImageResource(R.drawable.ic_pause);
    }


    /**
     * 音乐播放结束,并结束课程
     */
    @Override
    public void onPlayMusicComplete() {
        if (courseProgressListener != null) {
            courseProgressListener.completeCurrentCourse(3);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_AUTO_READ) {
                needAdd = true;
                UbtLog.d(TAG, "adddddd:" + autoRead);
                if (autoRead) {
                    readEngOneByOne();
                }
            }
        }
    };


    /**
     * hts播放完成
     */
    public void playComplete() {
        UbtLog.d(TAG, "播放完成");
        if (((Activity) mContext).isFinishing()) {
            return;
        }

        if (currentCourse == 1) {
            if (isInstruction) {//第一课程
                isInstruction = false;
                mRlInstruction.setVisibility(View.GONE);
                showActionMoreArrow(true);
            }
        } else if (currentCourse == 2) {
            UbtLog.d(TAG, "playComplete==" + 2);
        }
    }


    /**
     * 左边箭头动效
     *
     * @param flag true 播放 false 结束
     */
    private void showMusicArrow(boolean flag) {
        ivActionBgm.setEnabled(flag);
        if (flag) {
            //((ActionsEditHelper) mHelper).playSoundAudio("{\"filename\":\"音乐库.mp3\",\"playcount\":1}");
            ivMusicArror.setVisibility(View.VISIBLE);
            ivMusicArror.setImageResource(R.drawable.animal_left_arrow);
            animation1 = (AnimationDrawable) ivMusicArror.getDrawable();
            animation1.start();
        } else {
            ivMusicArror.setVisibility(View.GONE);
            if (null != animation1) {
                animation1.stop();
            }
        }
    }


    /**
     * 右边箭头动效
     *
     * @param flag true 播放 false 结束
     */
    private void showActionMoreArrow(boolean flag) {
        ivActionLibMore.setEnabled(flag);
        if (flag) {
            ivMoreArrow.setVisibility(View.VISIBLE);
            ivMoreArrow.setImageResource(R.drawable.animal_left_arrow);
            animation3 = (AnimationDrawable) ivMoreArrow.getDrawable();
            animation3.start();
        } else {
            ivMoreArrow.setVisibility(View.GONE);
            if (null != animation3) {
                animation3.stop();
            }
        }
    }

    /**
     * 右边箭头动效
     *
     * @param flag true 播放 false 结束
     */
    private void showRightArrow(boolean flag) {
        if (flag) {
            ivRightArrow.setVisibility(View.VISIBLE);
            ivRightArrow.setImageResource(R.drawable.animal_right_arrow);
            animation3 = (AnimationDrawable) ivRightArrow.getDrawable();
            animation3.start();
        } else {
            ivRightArrow.setVisibility(View.GONE);
            if (null != animation3) {
                animation3.stop();
            }
        }
    }


    /**
     * 播放按钮箭头动效
     *
     * @param flag true 播放 false 结束
     */
    private void showPlayArrow1(boolean flag) {
        ivPlay.setEnabled(true);
        ivBack.setEnabled(false);
        ivAddFrame.setEnabled(false);
        ivActionBgm.setEnabled(false);
        if (flag) {
            playArrow.setVisibility(View.VISIBLE);
            playArrow.setImageResource(R.drawable.animal_left_arrow);
            animation3 = (AnimationDrawable) playArrow.getDrawable();
            animation3.start();
        } else {
            playArrow.setVisibility(View.GONE);
            if (null != animation3) {
                animation3.stop();
            }
        }
    }

    /**
     * Activity执行onPause方法
     */
    public void onPause() {
        mHandler.removeMessages(1111);
        mHandler.removeMessages(1112);
        mHandler.removeMessages(1113);
        mHandler.removeMessages(1115);

    }


    /**
     * 响应所有R.id.iv_known的控件的点击事件
     * <p>
     * 移除高亮布局
     * </p>
     */
    public void clickKnown() {
        UbtLog.d(TAG, "currindex==" + currentIndex);

    }


    /**
     * 下一课时对话框
     *
     * @param current 跳转课程
     */
    private void showNextDialog(int current) {
        currentCourse = current;
        UbtLog.d(TAG, "进入第二课时，弹出对话框");
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_action_course_content, null);
        TextView title = contentView.findViewById(R.id.tv_card_name);
        title.setText("第四关 添加动作＋音频");

        Button button = contentView.findViewById(R.id.btn_pos);
        button.setText("下一节");

        RecyclerView mrecyle = contentView.findViewById(R.id.recyleview_content);
        mrecyle.setLayoutManager(new LinearLayoutManager(mContext));

        CourseItemAdapter itemAdapter = new CourseItemAdapter(R.layout.layout_action_course_dialog, ActionCourseDataManager.getCourseActionModel(4, current));
        mrecyle.setAdapter(itemAdapter);

        ViewHolder viewHolder = new ViewHolder(contentView);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.6); //设置宽度

        DialogPlus.newDialog(mContext)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentWidth(width)
                .setContentBackgroundResource(R.drawable.action_dialog_filter_rect)
                .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_pos) {
                            currentIndex = 0;
                            setLayoutByCurrentCourse();
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create().show();
    }


    @Override
    public void onCourseConfirm(PrepareDataModel prepareDataModel) {
        super.onActionConfirm(prepareDataModel);
        ivPlay.setEnabled(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(1);
                }
                showNextDialog(2);
            }
        }, 1000);

    }

    @Override
    public void playCourseAction(PrepareDataModel prepareDataModel, int type) {

    }

    /**
     * 点击添加音乐按钮
     *
     * @param prepareMusicModel
     */
    @Override
    public void onMusicConfirm(PrepareMusicModel prepareMusicModel) {
        super.onMusicConfirm(prepareMusicModel);
        ivPlay.setEnabled(false);
        ((ActionsEditHelper) mHelper).stopSoundAudio();
        mHandler.postDelayed(new Runnable() {//延迟一秒播放语音
            @Override
            public void run() {
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(2);
                }
                showNextDialog(3);
            }
        }, 1000);
    }


}