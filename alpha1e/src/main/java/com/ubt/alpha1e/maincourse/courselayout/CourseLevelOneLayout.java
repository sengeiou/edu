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
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.maincourse.adapter.CourseItemAdapter;
import com.ubt.alpha1e.maincourse.model.CourseOne1Content;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.interfaces.HighLightInterface;
import zhy.com.highlight.position.HightLightTopCallback;
import zhy.com.highlight.position.OnLeftLocalPosCallback;
import zhy.com.highlight.position.OnRightPosCallback;
import zhy.com.highlight.shape.RectLightShape;
import zhy.com.highlight.view.HightLightView;


/**
 * @author：liuhai
 * @date：2017/11/20 15:08
 * @modifier：ubt
 * @modify_date：2017/11/20 15:08
 * [A brief description]
 * version
 */

public class CourseLevelOneLayout extends BaseActionEditLayout {
    private String TAG = CourseLevelOneLayout.class.getSimpleName();
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvCourseContent;
    private RelativeLayout rlContent;
    private ImageView ivLeftArrow;
    private ImageView ivRightArrow;
    AnimationDrawable animation1;
    AnimationDrawable animation2;
    AnimationDrawable animation3;
    private RelativeLayout rlActionCenter;
    private ImageView ivCenterAction;
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

    private int secondIndex = 0;
    private HighLight mHightLight;

    public CourseLevelOneLayout(Context context) {
        super(context);
    }

    public CourseLevelOneLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseLevelOneLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            if (course == 1) {
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
        UbtLog.d(TAG, "currentCourse==" + currentCourse);
        // rlContent.setVisibility(View.VISIBLE);
        if (currentCourse == 1) {
            tvCourseContent.setText("认识时间轴");
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            showCourseOne();
        } else if (currentCourse == 2) {
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            tvCourseContent.setText("熟悉动作添加");
            // TODO Auto-generated method stub
            showLeftArrow(true);
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "添加按钮.hts");
            secondIndex = 1;
        } else if (currentCourse == 3) {
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            tvCourseContent.setText("了解音乐库");
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
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvCourseContent = (TextView) findViewById(R.id.tv_course_index);
        rlContent = findViewById(R.id.rl_course_content);
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        ivAddFrame.setEnabled(false);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_disable);
        setImageViewBg();
        ivLeftArrow = findViewById(R.id.iv_left_arrow);
        initLeftArrow();
        ivRightArrow = findViewById(R.id.iv_add_frame_arrow);

        rlActionCenter = findViewById(R.id.rl_action_animal);
        ivCenterAction = findViewById(R.id.iv_center_action);

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
     * 设置添加按钮高亮
     */
    public void setAddButton() {
        setImageViewBg();
        ivAddFrame.setEnabled(true);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_enable);
    }

    /**
     * 设置播放按钮高亮
     */
    public void setPlayButton() {
        setImageViewBg();
        ivAddFrame.setEnabled(false);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_disable);
        ivPlay.setEnabled(true);
    }

    /**
     * 初始化箭头图片宽高
     */
    private void initLeftArrow() {
        // 获取屏幕密度（方法2）
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        float density = dm.density;
        UbtLog.d(TAG, "density:" + density);
        if (AlphaApplication.isPad()) {
            UbtLog.d(TAG, "Pad Robot 1");
        } else {
            ivLeftArrow.setLayoutParams(ActionConstant.getIvRobotParams(density, ivLeftArrow));
            UbtLog.d(TAG, "ivLeftArrow:" + ivLeftArrow.getWidth() + "/" + ivLeftArrow.getHeight());

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                currentCourse--;
                setLayoutByCurrentCourse();
                break;
            case R.id.iv_right:
                if (currentCourse == 1) {
                    currentCourse++;
                } else if (currentCourse == 2) {
                    currentCourse++;
                }
                setLayoutByCurrentCourse();
                break;

            case R.id.iv_back:
                if (null != courseProgressListener) {
                    courseProgressListener.finishActivity();
                }
                break;
            case R.id.iv_hand_left:
                lostLeft();
                break;
            case R.id.iv_add_frame:
                addFrameOnClick();
                ivLeft.setEnabled(false);
                ivRight.setEnabled(false);
                mHandler.sendEmptyMessageDelayed(1112, 2000);
                lostLeftHand = false;
                lostRightLeg = false;
                ivHandLeft.setSelected(false);
                isCourseReading = false;
                showNextDialog(3);
                showRightArrow(false);
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(2);
                }
                break;
            default:
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1111) {
                ivLeft.setEnabled(false);
                ivRight.setEnabled(true);
            } else if (msg.what == 1112) {
                ivLeft.setEnabled(true);
                ivRight.setEnabled(true);
            } else if (msg.what == 1113) {
                ivLeft.setEnabled(true);
                ivRight.setEnabled(false);
            } else if (msg.what == MSG_AUTO_READ) {
                needAdd = true;
                UbtLog.d(TAG, "adddddd:" + autoRead);

                if (autoRead) {
                    readEngOneByOne();
                }
            }
        }
    };


    public void playComplete() {
        UbtLog.d(TAG, "播放完成");
        if (((Activity) mContext).isFinishing()) {
            return;
        }

        if (currentCourse == 1) {
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            if (currentIndex < mOne1ContentList.size()) {
                // clickKnown();
            } else {
                // clickKnown();
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(1);
                }
                setImageViewBg();
                mHandler.sendEmptyMessageDelayed(1111, 500);
            }
        } else if (currentCourse == 2) {
            UbtLog.d(TAG, "playComplete==" + 2);
            if (secondIndex == 1) {
                lostRightLeg = true;
                lostLeft();
                showLeftArrow(false);
                startEditLeftHand();
            }

        } else if (currentCourse == 3) {
            ivLeft.setEnabled(true);
            ivRight.setEnabled(false);
            mHandler.sendEmptyMessageDelayed(1113, 1000);
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(3);
            }
        }
    }

    /**
     * 第二关卡摆动机器人手臂
     */
    public void startEditLeftHand() {
        rlActionCenter.setVisibility(View.VISIBLE);
        ivCenterAction.setImageResource(R.drawable.animal_action_center);
        animation2 = (AnimationDrawable) ivCenterAction.getDrawable();
        animation2.start();
        // ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "添加按钮.hts");
        autoRead = true;
        mHandler.sendEmptyMessage(MSG_AUTO_READ);
        isCourseReading = true;
        ivAddFrame.setEnabled(false);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_disable);
    }


    @Override
    public void onReacHandData() {
        super.onReacHandData();
        UbtLog.d(TAG, "机器人角度变化了呢！！");
        autoRead = false;
        ((ActionsEditHelper) mHelper).stopAction();
        mHandler.removeMessages(MSG_AUTO_READ);
        setButtonEnable(false);
        ivAddFrame.setEnabled(true);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_enable);
        ivLeft.setEnabled(true);
        ivRight.setEnabled(false);
        rlActionCenter.setVisibility(View.GONE);
        if (null != animation2) {
            animation2.stop();
        }

        showRightArrow(true);
    }

    /**
     * 左边箭头动效
     *
     * @param flag true 播放 false 结束
     */
    private void showLeftArrow(boolean flag) {
        if (flag) {
            ivLeftArrow.setVisibility(View.VISIBLE);
            ivLeftArrow.setImageResource(R.drawable.animal_right_arrow);
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
     * Activity执行onPause方法
     */
    public void onPause() {
        mHandler.removeMessages(1111);
        mHandler.removeMessages(1112);
        mHandler.removeMessages(1113);
        mHandler.removeMessages(1115);

    }


    /**
     * 第一课时界面显示
     */
    private void showCourseOne() {
        showOneCardContent();
    }


    /**
     * 第一个关卡第一个课时
     */
    private void showOneCardContent() {
        mHightLight = new HighLight(mContext)//
                .autoRemove(false)//设置背景点击高亮布局自动移除为false 默认为true
                .intercept(true)//设置拦截属性为false 高亮布局不影响后面布局
                .enableNext()
                .maskColor(0xAA000000)
                // .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(R.id.ll_add, R.layout.layout_pop_course_right_level, new HightLightTopCallback(100), new RectLightShape())
                .addHighLight(R.id.rl_musicz_zpne, R.layout.layout_pop_course_right_level, new HightLightTopCallback(100), new RectLightShape())
                .addHighLight(R.id.ll_add_frame, R.layout.layout_pop_course_right_level, new HightLightTopCallback(100), new RectLightShape())
                .addHighLight(R.id.iv_add_frame1, R.layout.layout_pop_course_left_level, new OnLeftLocalPosCallback(30), new RectLightShape())
                .addHighLight(R.id.iv_play_music, R.layout.layout_pop_course_right_level, new OnRightPosCallback(30), new RectLightShape())
                .setOnNextCallback(new HighLightInterface.OnNextCallback() {
                    @Override
                    public void onNext(HightLightView hightLightView, View targetView, View tipView) {
                        tv = tipView.findViewById(R.id.tv_content);
                        if (currentIndex < mOne1ContentList.size()) {
                            CourseOne1Content oneContent = mOne1ContentList.get(currentIndex);
                            tv.setText(oneContent.getTitle());
                            ((ActionsEditHelper) mHelper).playAction(oneContent.getActionPath());
                            UbtLog.d(TAG, " onNext====" + oneContent.getTitle());
                            UbtLog.d(TAG, " oneContent====" + oneContent.toString());
                            currentIndex++;
                        }
                        UbtLog.d(TAG, "当前的是那个View  onNext====" + currentIndex + "  size===" + mOne1ContentList.size());
                    }
                });
        mHightLight.show();
        CourseOne1Content oneContent = mOne1ContentList.get(0);
        ((ActionsEditHelper) mHelper).playAction(oneContent.getActionPath());
    }


    /**
     * 响应所有R.id.iv_known的控件的点击事件
     * <p>
     * 移除高亮布局
     * </p>
     */
    public void clickKnown() {
        UbtLog.d(TAG, "currindex==" + currentIndex);
        if (mHightLight.isShowing() && mHightLight.isNext())//如果开启next模式
        {
            mHightLight.next();
        } else {
            remove(null);
            UbtLog.d(TAG, "=====remove=========");
        }
        if (currentIndex == 5) {
            ((ActionsEditHelper) mHelper).stopAction();
            showNextDialog(2);
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(1);
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
        UbtLog.d(TAG, "进入第二课时，弹出对话框");
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_action_course_content, null);
        TextView title = contentView.findViewById(R.id.tv_card_name);
        title.setText("第一关 基本概念");

        Button button = contentView.findViewById(R.id.btn_pos);
        button.setText("下一节");

        RecyclerView mrecyle = contentView.findViewById(R.id.recyleview_content);
        mrecyle.setLayoutManager(new LinearLayoutManager(mContext));

        CourseItemAdapter itemAdapter = new CourseItemAdapter(R.layout.layout_action_course_dialog, ActionCourseDataManager.getCourseActionModel(1, current));
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
                            currentCourse = 2;
                            setLayoutByCurrentCourse();
                        }
                        dialog.dismiss();
                    }
                })

                .setCancelable(false)
                .create().show();
    }


    public interface CourseProgressListener {
        void completeCurrentCourse(int current);

        void finishActivity();
    }


}