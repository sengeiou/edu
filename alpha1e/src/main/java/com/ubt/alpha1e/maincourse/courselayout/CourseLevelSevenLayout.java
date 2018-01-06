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
import com.ubt.alpha1e.action.model.PrepareDataModel;
import com.ubt.alpha1e.action.model.PrepareMusicModel;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.data.model.FrameActionInfo;
import com.ubt.alpha1e.maincourse.adapter.ActionCourseTwoUtil;
import com.ubt.alpha1e.maincourse.adapter.CourseArrowAminalUtil;
import com.ubt.alpha1e.maincourse.adapter.CourseItemAdapter;
import com.ubt.alpha1e.maincourse.adapter.CourseMusicDialogUtil;
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

public class CourseLevelSevenLayout extends BaseActionEditLayout implements ActionCourseTwoUtil.OnCourseDialogListener {
    private String TAG = CourseLevelSevenLayout.class.getSimpleName();
    private ImageView ivAddLibArrow;
    private ImageView ivLeftArrow1;

    private ImageView ivLeftHandArrow;

    private ImageView ivUpdateArrow;

    private ImageView ivEditArrow;

    private ImageView ivPlayArrow;

    private ImageView ivAddArrow;
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

    public CourseLevelSevenLayout(Context context) {
        super(context);
    }

    public CourseLevelSevenLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseLevelSevenLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            if (course == 7) {
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
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor26.hts");
        } else if (currentCourse == 2) {
            ivActionLib.setEnabled(false);
            CourseArrowAminalUtil.startViewAnimal(true, ivLeftHandArrow, 1);
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
        mTextView.setText("知道吗？对阿尔法的动作不满意，你还可以修改呢。");

        ivLeftHandArrow = findViewById(R.id.iv_left_arrow);
        ivLeftHandArrow.setOnClickListener(this);
        ivLeftHandArrow();
        ivPlayArrow = findViewById(R.id.iv_play_arrow);
        ivUpdateArrow = findViewById(R.id.iv_update_arrow);

        ivEditArrow = findViewById(R.id.iv_edit_arrow);
        ivEditArrow.setOnClickListener(this);

        ivAddArrow = findViewById(R.id.iv_add_frame_arrow);
        ivAddArrow.setOnClickListener(this);
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
            case R.id.iv_action_lib:
                showActionDialog();
                break;
            case R.id.iv_add_action_arrow:
                showActionDialog();
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
                CourseArrowAminalUtil.startViewAnimal(true, ivUpdateArrow, 2);
                ivAddFrame.setEnabled(false);
                isOnCourse = false;
                break;
            case R.id.iv_left_arrow:
                lostRightLeg = true;
                lostLeft();
                CourseArrowAminalUtil.startViewAnimal(false, ivLeftHandArrow, 1);
                CourseArrowAminalUtil.startViewAnimal(true, ivUpdateArrow, 2);
                ivAddFrame.setEnabled(false);
                isOnCourse = false;
                break;

            case R.id.iv_edit_arrow:
                editUpdate();
                break;

            case R.id.iv_change:
                editUpdate();
                break;
            case R.id.iv_add_frame_arrow:
                addFrameOnClick();
                isOnCourse = true;
                ivPlay.setEnabled(true);
                ivPlay.setImageResource(R.drawable.ic_play_enable);
                CourseArrowAminalUtil.startViewAnimal(true, ivPlayArrow, 2);
                CourseArrowAminalUtil.startViewAnimal(false, ivAddArrow, 1);
                break;
            case R.id.iv_add_frame:
                addFrameOnClick();
                isOnCourse = true;
                ivPlay.setEnabled(true);
                ivPlay.setImageResource(R.drawable.ic_play_enable);
                CourseArrowAminalUtil.startViewAnimal(true, ivPlayArrow, 2);
                CourseArrowAminalUtil.startViewAnimal(false, ivAddArrow, 1);
                break;
            default:
        }
    }

    /**
     * 点击修改按钮，先复原机器人
     */
    private void editUpdate() {
        ((ActionsEditHelper) mHelper).stopAction();

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

        CourseArrowAminalUtil.startViewAnimal(false, ivEditArrow, 1);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doChangeItem();
                autoRead = true;
                mHandler.sendEmptyMessage(MSG_AUTO_READ);
                isCourseReading = true;
                ivAddFrame.setEnabled(false);
                ivAddFrame.setImageResource(R.drawable.ic_confirm);
            }
        }, 1500);

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
                ivAddFrame.setImageResource(R.drawable.ic_confirm);
            }
        }
    };
    private String autoAng = "";

    /**
     * 重写父类的方法判断角度变化了
     */
    @Override
    public void addFrame() {
        if (isChangeData) {
            super.addFrame();
            return;
        }
        UbtLog.d(TAG, "addFrame:================");
        String angles = "";
        for (int i = 0; i < init.length; i++) {
            angles += init[i] + "#";
        }
        UbtLog.d(TAG, "angles:" + angles);

        UbtLog.d(TAG, "autoAng:" + autoAng + "angles:" + angles);
        if (autoAng.equals(angles)) {
            mHandler.sendEmptyMessage(MSG_AUTO_READ);

        } else {
            UbtLog.d(TAG, "autoAng:" + autoAng + "angles:" + angles);
            if (autoAng.equals("")) {
                autoAng = angles;
                mHandler.sendEmptyMessage(MSG_AUTO_READ);
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
                } else {
                    onReacHandData();
                }
            }
        }
    }

    private boolean isChangeData;

    @Override
    public void onReacHandData() {
        super.onReacHandData();
        isChangeData = true;
        UbtLog.d(TAG, "机器人角度变化了呢！！");
        autoRead = false;
        mHandler.removeMessages(MSG_AUTO_READ);
        setButtonEnable(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivAddFrame.setEnabled(true);
                ivAddFrame.setImageResource(R.drawable.ic_confirm);
                CourseArrowAminalUtil.startViewAnimal(true, ivAddArrow, 1);
            }
        }, 2000);

    }

    @Override
    public void showEditFrameLayout() {
        if (currentCourse==2) {
            rlEditFrame.setVisibility(View.VISIBLE);
            CourseArrowAminalUtil.startViewAnimal(true, ivEditArrow, 2);
            CourseArrowAminalUtil.startViewAnimal(false, ivUpdateArrow, 1);
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor27.hts");
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
            ivActionLib.setEnabled(true);
            CourseArrowAminalUtil.startViewAnimal(true, ivAddLibArrow, 2);
        } else if (currentCourse == 2) {
            autoRead = false;
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
        CourseItemAdapter itemAdapter = new CourseItemAdapter(R.layout.layout_action_course_dialog, ActionCourseDataManager.getCourseActionModel(7, current));
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
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNextDialog(2);
            }
        }, 1000);
    }

    @Override
    public void playCourseAction(PrepareDataModel prepareDataModel, int type) {
        isPlayAction = true;
        //  playAction(prepareDataModel);
        UbtLog.d(TAG, "playCourseAction===" + currentCourse);
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


}