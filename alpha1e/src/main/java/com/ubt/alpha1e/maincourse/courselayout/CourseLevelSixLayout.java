package com.ubt.alpha1e.maincourse.courselayout;

import android.app.Activity;
import android.content.Context;
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
import com.ubt.alpha1e.maincourse.adapter.CourseArrowAminalUtil;
import com.ubt.alpha1e.maincourse.adapter.CourseItemAdapter;
import com.ubt.alpha1e.maincourse.adapter.CourseMusicDialogUtil;
import com.ubt.alpha1e.maincourse.adapter.CourseProgressListener;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.litepal.crud.DataSupport;


/**
 * @author：liuhai
 * @date：2017/11/20 15:08
 * @modifier：ubt
 * @modify_date：2017/11/20 15:08
 * [A brief description]
 * version
 */

public class CourseLevelSixLayout extends BaseActionEditLayout implements ActionCourseTwoUtil.OnCourseDialogListener, CourseMusicDialogUtil.OnMusicDialogListener {
    private String TAG = CourseLevelSixLayout.class.getSimpleName();
    private ImageView ivAddLibArrow;
    private ImageView ivLeftArrow1;

    private ImageView ivMusiArrow;

    private ImageView ivPlayArrow;
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


    private boolean isPlayAction;

    private CourseMusicDialogUtil mMusicDialogUtil;

    public CourseLevelSixLayout(Context context) {
        super(context);
    }

    public CourseLevelSixLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseLevelSixLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            if (course == 6) {
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
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor22.hts");
            //showOneCardContent();
        } else if (currentCourse == 2) {
            ivActionLib.setEnabled(true);
            ivActionLibMore.setEnabled(false);
        } else if (currentCourse == 3) {
            ivActionBgm.setEnabled(true);
            CourseArrowAminalUtil.startViewAnimal(true, ivMusiArrow, 2);
        } else if (currentCourse == 4) {
            ivPlay.setEnabled(true);
            CourseArrowAminalUtil.startViewAnimal(true, ivPlayArrow, 2);
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor25.hts");
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
        ivAddLibArrow = findViewById(R.id.iv_add_action_arrow);
        ivAddLibArrow.setOnClickListener(this);
        ivLeftArrow1 = findViewById(R.id.iv_add_action_arrow1);
        ivLeftArrow1.setOnClickListener(this);
        mRlInstruction = findViewById(R.id.rl_instruction);
        mTextView = findViewById(R.id.tv_all_introduc);
        mTextView.setText("现在给阿尔法的踢腿动作配一个声音吧！");

        ivMusiArrow = findViewById(R.id.iv_music_arrow);

        ivPlayArrow = findViewById(R.id.iv_play_arrow);
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

            case R.id.iv_action_bgm:
                showMusicDialog();
                break;

            case R.id.iv_music_arrow:
                showMusicDialog();
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
        CourseArrowAminalUtil.startViewAnimal(false, ivPlayArrow, 2);
        ivPlay.setEnabled(false);
        ivPlay.setImageResource(R.drawable.ic_pause);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pause();
                onPlayMusicComplete();
            }
        }, 10000);
    }

    @Override
    public void onFinishPlay() {
        UbtLog.d(TAG, "onFinishPlay=======");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor20.hts");
                ivAddFrame.setEnabled(false);
                ivPlay.setEnabled(false);
                ivPlay.setEnabled(true);
                ivPlay.setImageResource(R.drawable.ic_play_disable);
                ivReset.setEnabled(true);
                ivReset.setImageResource(R.drawable.ic_reset);
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(4);
                }
            }
        });

    }

    /**
     * 点击弹出音乐对话框
     */
    private void showMusicDialog() {
        CourseArrowAminalUtil.startViewAnimal(false, ivMusiArrow, 2);
        ((ActionsEditHelper) mHelper).stopAction();

        if (null == mMusicDialogUtil) {
            mMusicDialogUtil = new CourseMusicDialogUtil(mContext);
        }
        if (currentCourse == 1) {
            mMusicDialogUtil.showMusicDialog(0, this);
        } else if (currentCourse == 3) {
            mMusicDialogUtil.showMusicDialog(1000, this);
        }
    }

    /**
     * 显示动作对话框
     */
    public void showActionDialog() {
        CourseArrowAminalUtil.startViewAnimal(false, ivAddLibArrow, 1);
        ((ActionsEditHelper) mHelper).stopSoundAudio();
        if (null == mActionCourseTwoUtil) {
            mActionCourseTwoUtil = new ActionCourseTwoUtil(mContext);
        }

        mActionCourseTwoUtil.showActionDialog(1, this);

    }

    public void playComplete() {
        UbtLog.d(TAG, "播放完成");
        if (((Activity) mContext).isFinishing()) {
            return;
        }
        if (currentCourse == 1) {
            mRlInstruction.setVisibility(View.GONE);
            ivActionBgm.setEnabled(true);
            CourseArrowAminalUtil.startViewAnimal(true, ivMusiArrow, 2);
        } else if (currentCourse == 2) {

        } else if (currentCourse == 3) {

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

    }


    /**
     * 下一课时对话框
     *
     * @param current 跳转课程
     */
    private void showNextDialog(int current) {
        if (courseProgressListener != null) {
            courseProgressListener.completeCurrentCourse(current - 1);
        }
        currentCourse = current;
        UbtLog.d(TAG, "进入第二课时，弹出对话框");
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_action_course_content, null);
        TextView title = contentView.findViewById(R.id.tv_card_name);
        title.setText("第二关 创建指定动作");
        Button button = contentView.findViewById(R.id.btn_pos);
        button.setText("下一节");
        RecyclerView mrecyle = contentView.findViewById(R.id.recyleview_content);
        mrecyle.setLayoutManager(new LinearLayoutManager(mContext));
        CourseItemAdapter itemAdapter = new CourseItemAdapter(R.layout.layout_action_course_dialog, ActionCourseDataManager.getCourseActionModel(6, current));
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
                            setLayoutByCurrentCourse();
                        }
                        dialog.dismiss();
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
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNextDialog(4);

            }
        }, 1000);
    }

    @Override
    public void playCourseAction(PrepareDataModel prepareDataModel, int type) {
        isPlayAction = true;
        //  playAction(prepareDataModel);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mActionCourseTwoUtil) {
                    mActionCourseTwoUtil.showAddAnimal();
                }
            }
        }, 1000);
    }

    @Override
    public void onMusicConfirm(PrepareMusicModel prepareMusicModel) {
        super.onMusicConfirm(prepareMusicModel);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ivActionLib.setEnabled(true);
                ivActionBgm.setEnabled(false);
                CourseArrowAminalUtil.startViewAnimal(true, ivAddLibArrow, 2);

            }
        });
    }


    @Override
    public void onStopRecord(PrepareMusicModel prepareMusicModel, int type) {
        UbtLog.d(TAG, "onStopRecord===========type==" + type);

        if (type == 1) {
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(1);
            }
            currentCourse = 2;
        } else if (type == 2) {
            showNextDialog(3);
            ((ActionsEditHelper) mHelper).stopAction();
            doReset();
        } else if (type == 3) {
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor23.hts");
        } else if (type == 4) {
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor24.hts");
        }
    }
}