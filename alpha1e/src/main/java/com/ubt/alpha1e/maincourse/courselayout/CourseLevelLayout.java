package com.ubt.alpha1e.maincourse.courselayout;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.actioncreate.BaseActionEditLayout;
import com.ubt.alpha1e.base.popup.EasyPopup;
import com.ubt.alpha1e.base.popup.HorizontalGravity;
import com.ubt.alpha1e.base.popup.VerticalGravity;
import com.ubt.alpha1e.maincourse.model.ActionCourseOneContent;
import com.ubt.alpha1e.maincourse.model.CourseOne1Content;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.interfaces.HighLightInterface;
import zhy.com.highlight.position.OnLeftPosCallback;
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

public class CourseLevelLayout extends BaseActionEditLayout {
    private String TAG = CourseLevelLayout.class.getSimpleName();
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvCourseContent;
    private RelativeLayout rlContent;


    /**
     * 第一关卡所有课时列表
     */
    private List<ActionCourseOneContent> mContents = new ArrayList<>();
    private int currentCard = 1;//当前关卡

    CourseProgressListener courseProgressListener;
    /**
     * 当前课时
     */
    private int currentCourse = 1;


    private int currentIndex = 0;

    private HighLight mHightLight;

    public CourseLevelLayout(Context context) {
        super(context);
    }

    public CourseLevelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseLevelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutId() {
        return R.layout.action_course_one;
    }

    /**
     * 设置课程数据
     *
     * @param list                   课程列表
     * @param currentCourse          当前第几个课时
     * @param courseProgressListener 回调监听
     */
    public void setData(List<ActionCourseOneContent> list, int currentCard, int currentCourse, CourseProgressListener courseProgressListener) {
        this.mContents = list;
        this.currentCard = currentCard;
        this.currentCourse = currentCourse;
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
        rlContent.setVisibility(View.VISIBLE);

        if (currentCourse == 1) {
            tvCourseContent.setText(mContents.get(0).getCourseName());
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            //showPop(0);
            showCourseOne();
        } else if (currentCourse == 2) {
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            tvCourseContent.setText(mContents.get(1).getCourseName());
            showPop1();
        } else if (currentCourse == 3) {
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            tvCourseContent.setText(mContents.get(2).getCourseName());
            showPop2();
        }

    }

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
    }


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
            case R.id.iv_add_frame:
                addFrameOnClick();
                ivLeft.setEnabled(false);
                ivRight.setEnabled(false);
                mHandler.sendEmptyMessageDelayed(1112, 2000);
                lostLeftHand = false;
                lostRightLeg = false;
                ivHandLeft.setSelected(false);
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

            } else if (msg.what == 1115) {
                if (null != mCirclePop) {
                    mCirclePop.dismiss();
                }
                ivAddFrame.setEnabled(true);
                ivAddFrame.setImageResource(R.drawable.ic_addaction_enable);
                showAddPop();
            }
        }
    };


    public void playComplete() {
        UbtLog.d("EditHelper", "播放完成");
        if (((Activity) mContext).isFinishing()) {
            return;
        }
        if (null != mCirclePop) {
            mCirclePop.dismiss();
        }
        if (currentCourse == 1) {
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            if (currentIndex <= mContents.get(0).getList().size()) {
                // showPop(currentIndex);
                clickKnown();
            } else {
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(1);
                }
                setImageViewBg();
                currentIndex = 0;
                mHandler.sendEmptyMessageDelayed(1111, 500);
            }
        } else if (currentCourse == 2) {
            UbtLog.d(TAG, "playComplete==" + 2);
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(2);
            }
            ivLeft.setEnabled(true);
            ivRight.setEnabled(false);
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
     * Activity执行onPause方法
     */
    public void onPause() {
        mHandler.removeMessages(1111);
        mHandler.removeMessages(1112);
        mHandler.removeMessages(1113);
        mHandler.removeMessages(1115);
        if (null != mCirclePop) {
            mCirclePop.dismiss();
        }
    }


    /**
     * 第一课时界面显示
     */
    private void showCourseOne() {
        if (currentCard == 1) {
            showOneCardContent();
        } else if (currentCard == 2) {

        }
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
                .addHighLight(R.id.ll_frame, R.layout.layout_pop_course_level, new HightLightTopCallback(45), new RectLocalLightShap())
                .addHighLight(R.id.rl_musicz_zpne, R.layout.layout_pop_course_level, new HightLightTopCallback(100), new RectLocalLightShap())
                .addHighLight(R.id.ll_add_frame, R.layout.layout_pop_course_level, new HightLightTopCallback(20), new RectLocalLightShap())
                .addHighLight(R.id.iv_add_frame1, R.layout.layout_pop_course_level, new OnLeftPosCallback(20), new RectLocalLightShap())
                .addHighLight(R.id.iv_play_music, R.layout.layout_pop_course_level, new OnRightPosCallback(12), new RectLocalLightShap())
                .setOnNextCallback(new HighLightInterface.OnNextCallback() {
                    @Override
                    public void onNext(HightLightView hightLightView, View targetView, View tipView) {
                        tv = tipView.findViewById(R.id.tv_content);
                        if (currentIndex < mContents.get(0).getList().size()) {
                            tv.setText(mContents.get(0).getList().get(currentIndex).getContent());
                            CourseOne1Content oneContent = mContents.get(currentCourse - 1).getList().get(currentIndex);
                            ((ActionsEditHelper) mHelper).playAction(oneContent.getActionPath());
                            UbtLog.d(TAG, " onNext====" + oneContent.getContent());
                            UbtLog.d(TAG, " onNext====od====" + targetView.getId());
                            currentIndex++;
                        }
                        UbtLog.d(TAG, "当前的是那个View  onNext====" + currentIndex + "  size===" + mContents.get(0).getList().size());
                    }
                });
        mHightLight.show();
        CourseOne1Content oneContent = mContents.get(currentCourse - 1).getList().get(0);
        ((ActionsEditHelper) mHelper).playAction(oneContent.getActionPath());
    }

    TextView tv;

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
    }


    public void remove(View view) {
        mHightLight.remove();
    }

    EasyPopup mCirclePop = null;


    /**
     * 第一课时
     *
     * @param index
     */
    private void showPop(int index) {
        if (index == 3) {
            setAddButton();
        } else if (index == 4) {
            setPlayButton();
        }
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        UbtLog.d(TAG, mContents.get(currentCourse - 1).getList().toString());
        CourseOne1Content oneContent = mContents.get(currentCourse - 1).getList().get(index);
        textView.setText(oneContent.getContent());
        textView.setBackgroundResource(oneContent.getDirection() == 0 ? R.drawable.bubble_left : R.drawable.bubble_right);
        View archView = findViewById(oneContent.getId());
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .createPopup()
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        currentIndex++;
                    }
                });

        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
        // ((ActionsEditHelper) mHelper).playCourse(oneContent.getVoiceName());
        ((ActionsEditHelper) mHelper).playAction(oneContent.getActionPath());
        UbtLog.d("EditHelper", oneContent.getActionPath());
    }

    /**
     * 第二课时
     */
    private void showPop1() {
        showAddLeftPop();
        lostRightLeg = true;
        lostLeft();
        mHandler.sendEmptyMessageDelayed(1115, 3000);


    }


    /**
     * 左手臂弹框
     */
    private void showAddLeftPop() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one1, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        textView.setText("点击机器人的关节部分机器人掉电，掰动机器人的关节至理想的动作");
        textView.setBackgroundResource(R.drawable.bubble_right);
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .createPopup()

        ;

        mCirclePop.showAtAnchorView(findViewById(R.id.iv_hand_left), VerticalGravity.CENTER, HorizontalGravity.LEFT, 0, 0);


    }


    /**
     * 添加按钮
     */
    private void showAddPop() {
        UbtLog.d(TAG, "showAddPop");
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);

        textView.setText("点击时间轴的添加按钮 完成动作添加");
        textView.setBackgroundResource(R.drawable.bubble_right);
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .createPopup()
        ;

        mCirclePop.showAtAnchorView(ivAddFrame, VerticalGravity.CENTER, HorizontalGravity.LEFT, 20, 0);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCirclePop.dismiss();
            }
        }, 2000);
    }


    public void dismiss() {
        if (null != mCirclePop) {
            mCirclePop.dismiss();
        }
    }

    /**
     * 播放第三课时
     */
    private void showPop2() {
        setImageViewBg();
        ivActionBgm.setEnabled(true);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        UbtLog.d(TAG, mContents.get(currentCourse - 1).getList().toString());
        CourseOne1Content oneContent = mContents.get(currentCourse - 1).getList().get(0);
        textView.setText(oneContent.getContent());
        textView.setBackgroundResource(oneContent.getDirection() == 0 ? R.drawable.bubble_left : R.drawable.bubble_right);
        View archView = findViewById(oneContent.getId());
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
//                .setBackgroundDimEnable(true)
//                .setDimValue(0.4f)
                .createPopup();

        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
        ((ActionsEditHelper) mHelper).playAction(oneContent.getActionPath());
        UbtLog.d("EditHelper", oneContent.getVoiceName());
    }


    public interface CourseProgressListener {
        void completeCurrentCourse(int current);

        void finishActivity();
    }

}