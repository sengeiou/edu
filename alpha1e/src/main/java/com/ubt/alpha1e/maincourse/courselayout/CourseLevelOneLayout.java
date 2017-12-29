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
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.maincourse.adapter.CourseItemAdapter;
import com.ubt.alpha1e.maincourse.adapter.CourseProgressListener;
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
import zhy.com.highlight.position.OnRightLocalPosCallback;
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
    private ImageView ivLeftArrow;
    private ImageView ivRightArrow;
    AnimationDrawable animation1;
    AnimationDrawable animation2;
    AnimationDrawable animation3;
    private RelativeLayout rlActionCenter;
    private ImageView ivCenterAction;

    RelativeLayout mRlInstruction;
    private TextView mTextView;
    private boolean isInstruction;

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
        setImageViewBg();
        UbtLog.d(TAG, "currentCourse==" + currentCourse);
        if (currentCourse == 1) {
            isInstruction = true;
            mRlInstruction.setVisibility(View.VISIBLE);
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor1.hts");
        } else if (currentCourse == 2) {
            showAddLight();

//            showLeftArrow(true);
//            ((ActionsEditHelper) mHelper).playSoundAudio("{\"filename\":\"动作添加.mp3\",\"playcount\":1}");
//            secondIndex = 1;
        } else if (currentCourse == 3) {
//            list_frames.clear();
//            adapter.notifyDataSetChanged();//清除列表数据
            showPlayLight();
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
        ivLeftArrow = findViewById(R.id.iv_left_arrow);
        ivLeftArrow.setOnClickListener(this);
        initLeftArrow();
        ivRightArrow = findViewById(R.id.iv_add_frame_arrow);

        rlActionCenter = findViewById(R.id.rl_action_animal);
        ivCenterAction = findViewById(R.id.iv_center_action);
        mRlInstruction = (RelativeLayout) findViewById(R.id.rl_instruction);
        mTextView = (TextView) findViewById(R.id.tv_all_introduc);
        mTextView.setText(ResourceManager.getInstance(mContext).getStringResources("action_course_card1_1_all"));
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
        ivAddFrame.setEnabled(true);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_enable);
    }

    /**
     * 设置播放按钮高亮
     */
    public void setPlayButton() {
        ivPlay.setEnabled(true);
    }

    /**
     * 设置添加按钮高亮
     */
    public void setActionMusicButton() {
        setImageViewBg();
        ivActionBgm.setEnabled(true);
        ivActionBgm.setImageResource(R.drawable.ic_add_music);
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
            case R.id.iv_back://返回按钮
                if (null != courseProgressListener) {
                    courseProgressListener.finishActivity();
                }
                break;
            case R.id.iv_hand_left:
                ((ActionsEditHelper) mHelper).stopSoundAudio();
                lostRightLeg = true;
                lostLeft();
                startEditLeftHand();
                showLeftArrow(false);
                break;
            case R.id.iv_add_frame://课时二添加动作按钮完成课时二
                addFrameOnClick();
                lostLeftHand = false;
                lostRightLeg = false;
                ivHandLeft.setSelected(false);
                isCourseReading = false;
                showRightArrow(false);
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(2);
                }
                ((ActionsEditHelper) mHelper).stopSoundAudio();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showNextDialog(3);
                    }
                }, 1000);

                break;
            default:
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
     * 完成播放
     */
    public void playComplete() {
        UbtLog.d(TAG, "播放完成" + currentCourse);
        if (((Activity) mContext).isFinishing()) {
            return;
        }

        if (currentCourse == 1) {
            if (isInstruction) {
                isInstruction = false;
                mRlInstruction.setVisibility(View.GONE);
                showCourseOne();
            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clickKnown();
                    }
                }, 1000);
            }
        } else if (currentCourse == 2) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clickKnown();
                }
            }, 1000);
            UbtLog.d(TAG, "playComplete==" + 2);
            if (secondIndex == 1) {
                lostRightLeg = true;
                lostLeft();
                showLeftArrow(false);
                startEditLeftHand();
            }

        } else if (currentCourse == 3) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clickKnown();
                }
            }, 1000);
        }
    }

    /**
     * 第二关卡摆动机器人手臂
     */
    public void startEditLeftHand() {
        secondIndex = 2;
        rlActionCenter.setVisibility(View.VISIBLE);
        ivCenterAction.setImageResource(R.drawable.animal_action_center);
        animation2 = (AnimationDrawable) ivCenterAction.getDrawable();
        animation2.start();
        autoRead = true;
        mHandler.sendEmptyMessage(MSG_AUTO_READ);
        isCourseReading = true;
        ivAddFrame.setEnabled(false);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_disable);
        ((ActionsEditHelper) mHelper).playSoundAudio("{\"filename\":\"动作添加1.mp3\",\"playcount\":1}");
    }


    @Override
    public void onReacHandData() {
        super.onReacHandData();
        UbtLog.d(TAG, "机器人角度变化了呢！！");
        autoRead = false;
        ((ActionsEditHelper) mHelper).stopSoundAudio();
        mHandler.removeMessages(MSG_AUTO_READ);
        setButtonEnable(false);
        ivAddFrame.setEnabled(true);
        ivAddFrame.setImageResource(R.drawable.ic_addaction_enable);
        rlActionCenter.setVisibility(View.GONE);
        if (null != animation2) {
            animation2.stop();
        }
        showRightArrow(true);
        secondIndex = 3;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ActionsEditHelper) mHelper).playSoundAudio("{\"filename\":\"动作添加2.mp3\",\"playcount\":1}");
            }
        }, 1000);
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
        setAddButton();
        setPlayButton();
        mHightLight = new HighLight(mContext)//
                .autoRemove(false)//设置背景点击高亮布局自动移除为false 默认为true
                .intercept(true)//设置拦截属性为false 高亮布局不影响后面布局
                .enableNext()
                .maskColor(0xAA000000)
                // .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(R.id.ll_add, R.layout.layout_pop_course_right_level, new HightLightTopCallback(100), new RectLightShape())
                .addHighLight(R.id.rl_musicz_zpne, R.layout.layout_pop_course_right_level, new HightLightTopCallback(100), new RectLightShape())
                .addHighLight(R.id.ll_add_frame, R.layout.layout_pop_course_right_level, new HightLightTopCallback(100), new RectLightShape())
                .addHighLight(R.id.rl_zoom1, R.layout.layout_pop_course_left_level, new OnLeftLocalPosCallback(30), new RectLightShape())
                .setOnNextCallback(new HighLightInterface.OnNextCallback() {
                    @Override
                    public void onNext(HightLightView hightLightView, View targetView, View tipView) {//动态设置文字
                        UbtLog.d(TAG, "当前的是那个View  onNext====" + currentIndex + "  size===" + mOne1ContentList.size());
                        tv = tipView.findViewById(R.id.tv_content);
                        if (currentIndex < mOne1ContentList.size()) {
                            CourseOne1Content oneContent = mOne1ContentList.get(currentIndex);
                            tv.setText(oneContent.getTitle());
//                            if (currentIndex == 3) {
//                                ((ActionsEditHelper) mHelper).playSoundAudio("{\"filename\":\"AE_action editor5.mp3\",\"playcount\":1}");
//                            } else {
//                                ((ActionsEditHelper) mHelper).playAction(oneContent.getActionPath());
//                            }
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
        //  ((ActionsEditHelper) mHelper).playAction(oneContent.getActionPath());
    }


    /**
     * 添加按钮介绍
     */
    private void showAddLight() {
        setAddButton();
        mHightLight = new HighLight(mContext)//
                .autoRemove(false)//设置背景点击高亮布局自动移除为false 默认为true
                .intercept(true)//设置拦截属性为false 高亮布局不影响后面布局
                .enableNext()
                .maskColor(0xAA000000)
                // .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(R.id.iv_add_frame, R.layout.layout_pop_course_left_level, new OnLeftLocalPosCallback(30), new RectLightShape())
                .setOnShowCallback(new HighLightInterface.OnShowCallback() {
                    @Override
                    public void onShow(HightLightView hightLightView) {
                        HighLight.ViewPosInfo viewPosInfo = hightLightView.getCurentViewPosInfo();
                        if (null != viewPosInfo) {
                            int layoutId = viewPosInfo.layoutId;
                            View tipView = hightLightView.findViewById(layoutId);
                            tv = tipView.findViewById(R.id.tv_content);
                            tv.setText("添加按钮");
                            UbtLog.d(TAG, "======onShow====showMusicLight1");
                        }
                    }
                });

        mHightLight.show();
        ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor6.hts");
    }


    /**
     * 播放按钮介绍
     */
    private void showPlayLight() {
        setPlayButton();
        mHightLight = new HighLight(mContext)//
                .autoRemove(false)//设置背景点击高亮布局自动移除为false 默认为true
                .intercept(true)//设置拦截属性为false 高亮布局不影响后面布局
                .enableNext()
                .maskColor(0xAA000000)
                // .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(R.id.iv_play_music, R.layout.layout_pop_course_right_level, new OnRightLocalPosCallback(30), new RectLightShape())
                .setOnShowCallback(new HighLightInterface.OnShowCallback() {
                    @Override
                    public void onShow(HightLightView hightLightView) {
                        HighLight.ViewPosInfo viewPosInfo = hightLightView.getCurentViewPosInfo();
                        if (null != viewPosInfo) {
                            int layoutId = viewPosInfo.layoutId;
                            View tipView = hightLightView.findViewById(layoutId);
                            tv = tipView.findViewById(R.id.tv_content);
                            tv.setText("播放按钮");
                            UbtLog.d(TAG, "======onShow====showMusicLight1");
                        }
                    }
                });

        mHightLight.show();
        ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor7.hts");
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
        if (currentCourse == 1) {
            if (currentIndex == 4) {
                ((ActionsEditHelper) mHelper).stopAction();
                doReset();
                showNextDialog(2);
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(1);
                }
            }
        } else if (currentCourse == 2) {
            ((ActionsEditHelper) mHelper).stopAction();
            doReset();
            showNextDialog(3);
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(2);
            }
        } else if (currentCourse == 3) {
            ((ActionsEditHelper) mHelper).stopAction();
            doReset();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (courseProgressListener != null) {
                        courseProgressListener.completeCurrentCourse(3);
                    }
                }
            }, 1000);

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
                            setLayoutByCurrentCourse();
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create().show();
    }


}