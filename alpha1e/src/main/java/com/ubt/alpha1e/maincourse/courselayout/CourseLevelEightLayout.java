package com.ubt.alpha1e.maincourse.courselayout;

import android.app.Activity;
import android.content.Context;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.actioncreate.BaseActionEditLayout;
import com.ubt.alpha1e.action.model.ActionConstant;
import com.ubt.alpha1e.action.model.PrepareMusicModel;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.maincourse.adapter.CourseArrowAminalUtil;
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


/**
 * @author：liuhai
 * @date：2017/11/20 15:08
 * @modifier：ubt
 * @modify_date：2017/11/20 15:08
 * [A brief description]
 * version
 */

public class CourseLevelEightLayout extends BaseActionEditLayout implements CourseMusicDialogUtil.OnMusicDialogListener {
    private String TAG = CourseLevelEightLayout.class.getSimpleName();
    private ImageView ivAddFrameArrow;
    private ImageView playArrow;

    private ImageView ivRightLegArrow;

    private ImageView resetArrow;


    RelativeLayout mRlInstruction;
    private TextView mTextView;
    private boolean isInstruction;
    private ImageView ivBackInStruction;

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

    public CourseLevelEightLayout(Context context) {
        super(context);
    }

    public CourseLevelEightLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseLevelEightLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            if (course == 8) {
                if (recordlevel == 0 || recordlevel == 1) {
                    level = 1;
                } else if (recordlevel == 2) {
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
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor28.hts");
        } else if (currentCourse == 2) {
            ivPlay.setEnabled(true);
            CourseArrowAminalUtil.startViewAnimal(true, playArrow, 2);
        }
        if (courseProgressListener != null) {
            courseProgressListener.completeCurrentCourse(currentCourse);
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

        ivAddFrameArrow = findViewById(R.id.iv_add_frame_arrow);
        resetArrow = findViewById(R.id.iv_reset_arrow);
        resetArrow.setOnClickListener(this);
        mRlInstruction = (RelativeLayout) findViewById(R.id.rl_instruction);
        mTextView = (TextView) findViewById(R.id.tv_all_introduc);
        mTextView.setText("我可是会标准的中国功夫哦，快来给我创建一个标准的踢腿动作吧。");
        ivRightLegArrow = findViewById(R.id.iv_right_leg_arrow);
        ivRightLegArrow.setOnClickListener(this);
        initRightLegArrow();
        ivBackInStruction = findViewById(R.id.iv_back_instruction);
        ivBackInStruction.setOnClickListener(this);

    }

    /**
     * 初始化箭头图片宽高
     */
    private void initRightLegArrow() {
        // 获取屏幕密度（方法2）
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        float density = dm.density;
        UbtLog.d(TAG, "density:" + density);
        if (AlphaApplication.isPad()) {
            UbtLog.d(TAG, "Pad Robot 1");
        } else {
            ivRightLegArrow.setLayoutParams(ActionConstant.getIvRobotParams(density, ivRightLegArrow));
            UbtLog.d(TAG, "ivLeftArrow:" + ivRightLegArrow.getWidth() + "/" + ivRightLegArrow.getHeight());
        }
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
            case R.id.iv_play_music:
                playAction();
                break;
            case R.id.iv_play_arrow:
                playAction();
                break;

            case R.id.iv_right_leg_arrow:
                startEditRightLeg();

                break;
            case R.id.iv_leg_right:
                startEditRightLeg();
                break;
            case R.id.iv_add_frame://课时二添加动作按钮完成课时二
                autoRead = false;
                addFrameOnClick();
                lostLeftHand = false;
                lostRightLeg = false;
                ivLegRight.setSelected(false);
                isCourseReading = false;
                CourseArrowAminalUtil.startViewAnimal(false, ivAddFrameArrow, 2);
                showNextDialog(2);
                break;
            case R.id.iv_back_instruction:
                if (null != courseProgressListener) {
                    courseProgressListener.finishActivity();
                }
                break;
            default:
        }
    }


    /**
     * 播放按钮，过3秒钟结束
     */
    private void playAction() {

        startPlayAction();
        CourseArrowAminalUtil.startViewAnimal(false, playArrow, 2);
        ivPlay.setEnabled(false);
        ivPlay.setImageResource(R.drawable.ic_pause);
    }


    @Override
    public void onFinishPlay() {
        UbtLog.d(TAG, "onFinishPlay=======");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ivAddFrame.setEnabled(false);
                ivPlay.setEnabled(false);
                ivPlay.setImageResource(R.drawable.ic_play_disable);
                if (courseProgressListener != null) {
                    courseProgressListener.completeSuccess(true);
                }
            }
        });

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
     * 播放音频或者动作结束
     */
    public void playComplete() {
        UbtLog.d(TAG, "播放完成");
        if (((Activity) mContext).isFinishing()) {
            return;
        }
         if (currentCourse == 1) {
            if (isInstruction) {
                mRlInstruction.setVisibility(View.GONE);
                CourseArrowAminalUtil.startViewAnimal(true, ivRightLegArrow, 2);
            }
        } else if (currentCourse == 2) {

        }
    }

    /**
     * 第二关卡摆动机器人手臂
     */
    public void startEditRightLeg() {
        CourseArrowAminalUtil.startViewAnimal(false, ivRightLegArrow, 2);
//        ((ActionsEditHelper) mHelper).stopAction();
        doReset();
        autoRead = true;
        mHandler.sendEmptyMessage(MSG_AUTO_READ);
        isCourseReading = true;
        lostLeftHand = true;
        lostRightLeg();
        ivAddFrame.setEnabled(false);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_disable);
    }

    private boolean isChangeData;

    @Override
    public void onReacHandData() {
        if (isChangeData) {
            return;
        }
        isChangeData = true;
        UbtLog.d(TAG, "机器人角度变化了呢！！");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoRead = false;
                ((ActionsEditHelper) mHelper).stopSoundAudio();
                CourseArrowAminalUtil.startViewAnimal(false, ivRightLegArrow, 2);
                mHandler.removeMessages(MSG_AUTO_READ);
                setButtonEnable(false);
                ivAddFrame.setEnabled(true);
                ivAddFrame.setImageResource(R.drawable.ic_addaction_enable);
                CourseArrowAminalUtil.startViewAnimal(true, ivAddFrameArrow, 1);
            }
        }, 5000);

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
        title.setText("第五关 创建动作");

        Button button = contentView.findViewById(R.id.btn_pos);
        button.setText("下一节");

        RecyclerView mrecyle = contentView.findViewById(R.id.recyleview_content);
        mrecyle.setLayoutManager(new LinearLayoutManager(mContext));

        CourseItemAdapter itemAdapter = new CourseItemAdapter(R.layout.layout_action_course_dialog, ActionCourseDataManager.getCourseActionModel(8, current));
        mrecyle.setAdapter(itemAdapter);

        ViewHolder viewHolder = new ViewHolder(contentView);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int screenHeight = (int) (display.getHeight() * 0.6);
        int screenWidth = (int) (display.getWidth() * 0.6);
        int width = Math.max(screenWidth, screenHeight); //设置宽度

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
                            dialog.dismiss();
                        }
                    }
                })
                .setCancelable(false)
                .create().show();
    }


    /**
     * 点击添加音乐按钮
     *
     * @param prepareMusicModel
     */
    @Override
    public void onMusicConfirm(PrepareMusicModel prepareMusicModel) {
        super.onMusicConfirm(prepareMusicModel);

    }

    @Override
    public void onStopRecord(PrepareMusicModel prepareMusicModel, int type) {

    }


}