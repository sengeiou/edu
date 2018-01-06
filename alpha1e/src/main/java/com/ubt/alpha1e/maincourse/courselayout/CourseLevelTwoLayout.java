package com.ubt.alpha1e.maincourse.courselayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
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
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.maincourse.adapter.ActionCourseTwoUtil;
import com.ubt.alpha1e.maincourse.adapter.CourseItemAdapter;
import com.ubt.alpha1e.maincourse.adapter.CourseProgressListener;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.litepal.crud.DataSupport;

import zhy.com.highlight.HighLight;


/**
 * @author：liuhai
 * @date：2017/11/20 15:08
 * @modifier：ubt
 * @modify_date：2017/11/20 15:08
 * [A brief description]
 * version
 */

public class CourseLevelTwoLayout extends BaseActionEditLayout implements ActionCourseTwoUtil.OnCourseDialogListener {
    private String TAG = CourseLevelTwoLayout.class.getSimpleName();
    private ImageView ivLeftArrow;
    private ImageView ivLeftArrow1;
    private ImageView playArrow;

    AnimationDrawable animation1;
    AnimationDrawable animation2;
    AnimationDrawable animation3;
    ActionCourseTwoUtil mActionCourseTwoUtil;
    /**
     * 高亮对话框的TextView显示
     */
    TextView tv;
    RelativeLayout mRlInstruction;
    private TextView mTextView;
    CourseProgressListener courseProgressListener;
    /**
     * 当前课时
     */
    private int currentCourse = 1;
    private int oneIndex = 0;
    private int secondIndex = 0;
    private int threeIndex = 0;

    private boolean isPlayAction;

    private HighLight mHightLight;

    public CourseLevelTwoLayout(Context context) {
        super(context);
    }

    public CourseLevelTwoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseLevelTwoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        int level = 1;// 当前第几个课时
        LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);
        if (null != record) {
            UbtLog.d(TAG, "record===" + record.toString());
            int course = record.getCourseLevel();
            int recordlevel = record.getPeriodLevel();
            if (course == 2) {
                if (recordlevel == 0) {
                    level = 1;
                } else if (recordlevel == 1) {
                    level = 2;
                } else if (recordlevel == 2) {
                    level = 3;
                } else if (recordlevel == 3) {
                    level = 1;
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
            mRlInstruction.setVisibility(View.VISIBLE);
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor8.hts");
            //showOneCardContent();
        } else if (currentCourse == 2) {
            ivActionLib.setEnabled(true);
            ivActionLibMore.setEnabled(false);
            showLeftArrow(true);
            secondIndex = 1;
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor9.hts");
            // ((ActionsEditHelper) mHelper).playSoundAudio("{\"filename\":\"AE_action editor9.mp3\",\"playcount\":1}");
        } else if (currentCourse == 3) {
            ivActionLib.setEnabled(false);
            ivActionLibMore.setEnabled(true);
            showLeftArrow1(true);
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor12.hts");
            // ((ActionsEditHelper) mHelper).playSoundAudio("{\"filename\":\"AE_action editor12.mp3\",\"playcount\":1}");
            threeIndex = 1;
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
        ivLeftArrow = findViewById(R.id.iv_add_action_arrow);
        ivLeftArrow.setOnClickListener(this);
        ivLeftArrow1 = findViewById(R.id.iv_add_action_arrow1);
        ivLeftArrow1.setOnClickListener(this);
        playArrow = findViewById(R.id.iv_play_arrow);
        playArrow.setOnClickListener(this);
        mRlInstruction = findViewById(R.id.rl_instruction);
        mTextView = findViewById(R.id.tv_all_introduc);
        mTextView.setText(ResourceManager.getInstance(mContext).getStringResources("action_course_card2_1_all"));
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

    /**
     * 左边箭头动效
     *
     * @param flag true 播放 false 结束
     */
    private void showLeftArrow(boolean flag) {
        if (flag) {
            ivLeftArrow.setVisibility(View.VISIBLE);
            ivLeftArrow.setImageResource(R.drawable.animal_left_arrow);
            animation1 = (AnimationDrawable) ivLeftArrow.getDrawable();
            animation1.start();
        } else {
            ivLeftArrow.setVisibility(View.GONE);
            if (null != animation1) {
                animation1.stop();
            }
        }
    }

    /**
     * 左边箭头动效
     *
     * @param flag true 播放 false 结束
     */
    private void showLeftArrow1(boolean flag) {
        if (flag) {
            ivLeftArrow1.setVisibility(View.VISIBLE);
            ivLeftArrow1.setImageResource(R.drawable.animal_left_arrow);
            animation2 = (AnimationDrawable) ivLeftArrow1.getDrawable();
            animation2.start();
        } else {
            ivLeftArrow1.setVisibility(View.GONE);
            if (null != animation2) {
                animation2.stop();
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
        ivActionLibMore.setEnabled(false);
        ivAddFrame.setEnabled(false);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_disable);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回按钮
                if (null != courseProgressListener) {
                    courseProgressListener.finishActivity();
                }
                break;
            case R.id.iv_action_lib:
                showActionDialog();
                break;
            case R.id.iv_add_action_arrow:
                showActionDialog();
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
     * 播放按钮，过3秒钟结束
     */
    private void playAction() {
        startPlayAction();
        showPlayArrow1(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(3);
                }
            }
        }, 4000);

    }

    /**
     * 显示动作对话框
     */
    public void showActionDialog() {
        ((ActionsEditHelper) mHelper).stopSoundAudio();
        if (null == mActionCourseTwoUtil) {
            mActionCourseTwoUtil = new ActionCourseTwoUtil(mContext);
        }
        if (currentCourse == 2) {
            secondIndex = 0;
            showLeftArrow(false);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor10.hts");
                    // ((ActionsEditHelper) mHelper).playSoundAudio("{\"filename\":\"AE_action editor10.mp3\",\"playcount\":1}");
                }
            }, 1000);
            mActionCourseTwoUtil.showActionDialog(1, this);
        } else if (currentCourse == 3) {
            threeIndex = 0;
            showLeftArrow1(false);
            mActionCourseTwoUtil.showActionDialog(2, this);
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ((ActionsEditHelper) mHelper).playSoundAudio("{\"filename\":\"任务指引5.mp3\",\"playcount\":1}");
//                }
//            }, 1000);
        }
    }

    /**
     * 机器人播放音乐和动作回调
     */
    public void playComplete() {
        UbtLog.d(TAG, "播放完成");
        if (((Activity) mContext).isFinishing()) {
            return;
        }
        if (currentCourse == 1) {
            mRlInstruction.setVisibility(View.GONE);
            showNextDialog(2);
        } else if (currentCourse == 2) {
            UbtLog.d(TAG, "secondIndex==" + secondIndex);
            if (secondIndex == 1) {
                secondIndex = 2;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 500);
            }
        } else if (currentCourse == 3) {
            if (threeIndex == 1) {
                threeIndex = 2;
            }
        }
        UbtLog.d(TAG, "isPlayAction==" + isPlayAction);

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
        if (mHightLight.isShowing() && mHightLight.isNext())//如果开启next模式
        {
            mHightLight.next();
        } else {
            remove(null);
        }
        if (currentCourse == 1 && tv.getText().equals("高级模板")) {
            ((ActionsEditHelper) mHelper).stopAction();
            doReset();
            showNextDialog(2);
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(1);
            }
        } else if (currentCourse == 2) {
            ((ActionsEditHelper) mHelper).stopAction();
            doReset();
            showNextDialog(3);
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(2);
            }
        }
    }


    public void remove(View view) {
        mHightLight.remove();
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
        title.setText("第二关 创建指定动作");
        Button button = contentView.findViewById(R.id.btn_pos);
        button.setText("下一节");
        RecyclerView mrecyle = contentView.findViewById(R.id.recyleview_content);
        mrecyle.setLayoutManager(new LinearLayoutManager(mContext));
        CourseItemAdapter itemAdapter = new CourseItemAdapter(R.layout.layout_action_course_dialog, ActionCourseDataManager.getCourseActionModel(2, current));
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
                            setLayoutByCurrentCourse();
                            dialog.dismiss();
                        }
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    /**
     * 点击添加按钮
     *
     * @param prepareDataModel
     */
    @Override
    public void onCourseConfirm(PrepareDataModel prepareDataModel) {
        UbtLog.d(TAG, "onCourseConfirm====" + currentCourse);
        onActionConfirm(prepareDataModel);
        ((ActionsEditHelper) mHelper).stopSoundAudio();
        isSaveAction = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentCourse == 2) {
                    showNextDialog(3);
                    UbtLog.d(TAG, "showNextDialog====");
                } else if (currentCourse == 3) {
                    showPlayArrow1(true);

                }
            }
        }, 1000);

    }

    @Override
    public void playCourseAction(PrepareDataModel prepareDataModel, int type) {
        isPlayAction = true;
        playAction(prepareDataModel);
        UbtLog.d(TAG, "playCourseAction===" + currentCourse);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mActionCourseTwoUtil) {
                    mActionCourseTwoUtil.showAddAnimal();
                }
                ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor11.hts");
                // ((ActionsEditHelper) mHelper).playSoundAudio("{\"filename\":\"AE_action editor11.mp3\",\"playcount\":1}");
            }
        }, 1200);

    }


}