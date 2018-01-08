package com.ubt.alpha1e.maincourse.courselayout;

import android.app.Activity;
import android.content.Context;
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
import com.ubt.alpha1e.action.actioncreate.DialogMusic;
import com.ubt.alpha1e.action.model.ActionConstant;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.data.model.FrameActionInfo;
import com.ubt.alpha1e.maincourse.adapter.ActionCourseTwoUtil;
import com.ubt.alpha1e.maincourse.adapter.CourseArrowAminalUtil;
import com.ubt.alpha1e.maincourse.adapter.CourseItemAdapter;
import com.ubt.alpha1e.maincourse.adapter.CourseProgressListener;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.Map;


/**
 * @author：liuhai
 * @date：2017/11/20 15:08
 * @modifier：ubt
 * @modify_date：2017/11/20 15:08
 * [A brief description]
 * version
 */

public class CourseLevelNineLayout extends BaseActionEditLayout {
    private String TAG = CourseLevelNineLayout.class.getSimpleName();
    private ImageView ivAddLibArrow;
    private ImageView ivLeftArrow1;

    private ImageView ivLeftHandArrow;

    private ImageView ivRightHandArrow;
    private ImageView ivUpdateArrow;

    private ImageView ivAutoReadArrow;

    private ImageView ivPlayArrow;

    private ImageView ivAddArrow;
    ActionCourseTwoUtil mActionCourseTwoUtil;
    private boolean isRlInstruction;

    RelativeLayout mRlInstruction;
    private TextView mTextView;
    CourseProgressListener courseProgressListener;
    private ImageView ivBackInStruction;
    /**
     * 当前课时
     */
    private int currentCourse = 1;


    public CourseLevelNineLayout(Context context) {
        super(context);
    }

    public CourseLevelNineLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseLevelNineLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            if (course == 9) {
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
            isRlInstruction = true;
            mRlInstruction.setVisibility(View.VISIBLE);
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor30.hts");
        } else if (currentCourse == 2) {
            ivPlay.setEnabled(true);
            CourseArrowAminalUtil.startViewAnimal(true, ivPlayArrow, 2);
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
        mRlInstruction = findViewById(R.id.rl_instruction);
        mTextView = findViewById(R.id.tv_all_introduc);
        mTextView.setText("你知道吗？还有一种超简单的编辑动作的好方法哦。");

        ivLeftHandArrow = findViewById(R.id.iv_left_arrow);
        ivLeftHandArrow.setOnClickListener(this);

        ivRightHandArrow = findViewById(R.id.iv_right_arrow);
        ivRightHandArrow.setOnClickListener(this);
        ivLeftHandArrow();
        ivPlayArrow = findViewById(R.id.iv_play_arrow);
        ivUpdateArrow = findViewById(R.id.iv_update_arrow);

        ivAutoReadArrow = findViewById(R.id.iv_auto_read_arrow);
        ivAutoReadArrow.setOnClickListener(this);

        ivAddArrow = findViewById(R.id.iv_add_frame_arrow);
        ivAddArrow.setOnClickListener(this);

        ivHandLeft.setEnabled(false);
        ivHandRight.setEnabled(false);

        ivBackInStruction = findViewById(R.id.iv_back_instruction);
        ivBackInStruction.setOnClickListener(this);
    }

    /**
     * 初始化箭头图片宽高
     */
    private void ivLeftHandArrow() {
        // 获取屏幕密度（方法2）
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        float density = dm.density;
        UbtLog.d(TAG, "density:" + density);
        if (AlphaApplication.isPad()) {
            UbtLog.d(TAG, "Pad Robot 1");
        } else {
            ivLeftHandArrow.setLayoutParams(ActionConstant.getIvRobotParams(density, ivLeftHandArrow));
            UbtLog.d(TAG, "ivLeftArrow:" + ivLeftHandArrow.getWidth() + "/" + ivLeftHandArrow.getHeight());

            ivRightHandArrow.setLayoutParams(ActionConstant.getIvRobotParams(density, ivRightHandArrow));
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
            case R.id.iv_hand_left:
                lostRightLeg = true;
                lostLeft();
                CourseArrowAminalUtil.startViewAnimal(false, ivLeftHandArrow, 1);
                CourseArrowAminalUtil.startViewAnimal(true, ivRightHandArrow, 2);
                showAutoRead();
                ivHandRight.setEnabled(true);
                break;
            case R.id.iv_left_arrow:
                lostRightLeg = true;
                lostLeft();
                CourseArrowAminalUtil.startViewAnimal(false, ivLeftHandArrow, 1);
                CourseArrowAminalUtil.startViewAnimal(true, ivRightHandArrow, 2);
                ivAddFrame.setEnabled(false);
                ivHandRight.setEnabled(true);
                break;

            case R.id.iv_hand_right:
                lostLeftLeg = true;
                lostRight();
                CourseArrowAminalUtil.startViewAnimal(false, ivRightHandArrow, 1);
                showAutoRead();
                break;
            case R.id.iv_right_arrow:
                lostLeftLeg = true;
                lostRight();
                CourseArrowAminalUtil.startViewAnimal(false, ivRightHandArrow, 1);
                ivAddFrame.setEnabled(false);
                showAutoRead();
                break;

            case R.id.iv_add_frame_arrow:
                addFrameClick();
                break;
            case R.id.iv_add_frame:
                addFrameClick();
                break;

            case R.id.iv_auto_read:
                DialogMusic dialogMusic = new DialogMusic(mContext, this, 3);
                dialogMusic.show();
                CourseArrowAminalUtil.startViewAnimal(false, ivAutoReadArrow, 1);
                ivAddFrame.setEnabled(false);
                ivAddFrame.setImageResource(R.drawable.ic_stop);
                break;
            case R.id.iv_auto_read_arrow:
                DialogMusic dialogMusic1 = new DialogMusic(mContext, this, 3);
                dialogMusic1.show();
                CourseArrowAminalUtil.startViewAnimal(false, ivAutoReadArrow, 1);
                ivAddFrame.setEnabled(false);
                ivAddFrame.setImageResource(R.drawable.ic_stop);
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
     * 点击添加按钮事件
     */
    private void addFrameClick() {
        mHandler.removeMessages(MSG_AUTO_READ);
        autoRead = false;
        needAdd = false;
        autoAng = "";
        ivAddFrame.setImageResource(R.drawable.ic_addaction_enable);
        CourseArrowAminalUtil.startViewAnimal(false, ivAddArrow, 1);
        showNextDialog(2);
    }


    private void showAutoRead() {
        if (lostLeftLeg && lostRightLeg) {
            ivAutoRead.setEnabled(true);
            CourseArrowAminalUtil.startViewAnimal(true, ivAutoReadArrow, 1);
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

    }

    @Override
    public void onFinishPlay() {
        UbtLog.d(TAG, "onFinishPlay=======");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ivPlay.setEnabled(false);
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(2);
                }
            }
        });

    }

    /**
     * 添加自动回读数据
     */
    @Override
    public void addFrame() {
        super.addFrame();
        UbtLog.d(TAG, "addframe==========" + list_autoFrames.size());
        ivAddFrame.setEnabled(false);
        ivAddFrame.setImageResource(R.drawable.ic_stop);
        if (list_autoFrames.size() > 4) {
            CourseArrowAminalUtil.startViewAnimal(true, ivAddArrow, 1);
            ivAddFrame.setEnabled(true);
            ivAddFrame.setImageResource(R.drawable.ic_stop);
        }
    }

    public void playComplete() {
        UbtLog.d(TAG, "播放完成");
        if (((Activity) mContext).isFinishing()) {
            return;
        }
        if (currentCourse == 1) {
            if (isRlInstruction) {
                isRlInstruction = false;
                mRlInstruction.setVisibility(View.GONE);
                ivActionLib.setEnabled(true);
                ivHandLeft.setEnabled(true);
                CourseArrowAminalUtil.startViewAnimal(true, ivLeftHandArrow, 1);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor31.hts");
                    }
                }, 1000);
            }
            //  CourseArrowAminalUtil.startViewAnimal(true, ivRightHandArrow, 2);
        } else if (currentCourse == 2) {
            autoRead = false;
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
        CourseItemAdapter itemAdapter = new CourseItemAdapter(R.layout.layout_action_course_dialog, ActionCourseDataManager.getCourseActionModel(9, current));
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


    @Override
    public void startAutoRead() {
        ((ActionsEditHelper) mHelper).stopAction();
        doReset1();
        setButtonEnable(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoRead = true;
                ivAddFrame.setImageResource(R.drawable.ic_stop);
                mHandler.sendEmptyMessage(MSG_AUTO_READ);
            }
        }, 1000);
    }

    public void doReset1() {
        UbtLog.d(TAG, "doReset");

        String angles = "93#20#66#86#156#127#90#74#95#104#89#89#104#81#76#90";

//        String angles = "90#90#90#90#90#90#90#60#76#110#90#90#120#104#70#90";
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


    }
}