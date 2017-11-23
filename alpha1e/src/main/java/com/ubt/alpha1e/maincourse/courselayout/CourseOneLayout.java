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
import com.ubt.alpha1e.action.model.ActionConstant;
import com.ubt.alpha1e.base.popup.EasyPopup;
import com.ubt.alpha1e.maincourse.model.ActionCourseOneContent;
import com.ubt.alpha1e.maincourse.model.CourseOne1Content;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

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

public class CourseOneLayout extends BaseActionEditLayout {
    private String TAG = CourseOneLayout.class.getSimpleName();
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvCourseContent;
    private RelativeLayout rlContent;
    /**
     * 第一关卡所有课时列表
     */
    private List<ActionCourseOneContent> mContents = new ArrayList<>();

    CourseProgressListener courseProgressListener;
    /**
     * 当前课时
     */
    private int currentCourse = 1;


    private int currentIndex = 0;

    public CourseOneLayout(Context context) {
        super(context);
    }

    public CourseOneLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseOneLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    public void setData(List<ActionCourseOneContent> list, int currentCourse, CourseProgressListener courseProgressListener) {
        this.mContents = list;
        this.currentCourse = currentCourse;
        this.courseProgressListener = courseProgressListener;
        setLayoutByCurrentCourse();
        onActionConfirm(ActionConstant.getBasicActionList(mContext).get(0));
        isSaveAction = true;
    }

    /**
     * 根据当前课时显示界面
     */
    public void setLayoutByCurrentCourse() {
        UbtLog.d(TAG, "currentCourse==" + currentCourse);
        rlContent.setVisibility(View.VISIBLE);
        if (currentCourse == 1) {
            tvCourseContent.setText(mContents.get(0).getCourseName());
            showPop(0);
            ivLeft.setEnabled(false);
            ivRight.setEnabled(true);
        } else if (currentCourse == 2) {
            ivLeft.setEnabled(true);
            ivRight.setEnabled(true);
            showPop1();
            tvCourseContent.setText(mContents.get(1).getCourseName());
        } else if (currentCourse == 3) {
            ivLeft.setEnabled(true);
            ivRight.setEnabled(false);
            tvCourseContent.setText(mContents.get(2).getCourseName());
            showPop2();
        }
    }

    @Override
    public void init(Context context) {
        super.init(context);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvCourseContent = (TextView) findViewById(R.id.tv_course_index);
        rlContent = findViewById(R.id.rl_course_content);
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
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
            if (currentIndex < mContents.get(0).getList().size()) {
                showPop(currentIndex);
            } else {
                if (courseProgressListener != null) {
                    courseProgressListener.completeCurrentCourse(1);
                }
                currentIndex = 0;
                mHandler.sendEmptyMessageDelayed(1111, 3000);
            }
        } else if (currentCourse == 2) {
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(2);
            }
            ivLeft.setEnabled(true);
            ivRight.setEnabled(false);
            mHandler.sendEmptyMessageDelayed(1112, 3000);
        } else if (currentCourse == 3) {
            ivLeft.setEnabled(true);
            ivRight.setEnabled(false);
            mHandler.sendEmptyMessageDelayed(1113, 3000);
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(3);
            }
        }


    }


    EasyPopup mCirclePop = null;


    /**
     * 第一课时
     *
     * @param index
     */
    private void showPop(int index) {

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        UbtLog.d(TAG, mContents.get(currentCourse - 1).getList().toString());
        CourseOne1Content oneContent = mContents.get(currentCourse - 1).getList().get(index);
        textView.setText(oneContent.getContent());
        textView.setBackgroundResource(oneContent.getDirection() == 0 ? R.drawable.bubble_guide_left : R.drawable.bubble_guide_right);
        View archView = findViewById(oneContent.getId());
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .createPopup()
                .setBackgroundDimEnable(true)
                .setDimValue(0.4f)
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        currentIndex++;
                    }
                });

        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
        ((ActionsEditHelper) mHelper).playCourse(oneContent.getVoiceName());
        UbtLog.d("EditHelper", oneContent.getVoiceName());
    }

    /**
     * 第二课时
     */
    private void showPop1() {
        mCirclePop = new EasyPopup(mContext)
                .setContentView(R.layout.layout_pop_course_one_two)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .createPopup()
                .setBackgroundDimEnable(true)
                .setDimValue(0.4f)
        ;
        CourseOne1Content oneContent = mContents.get(currentCourse - 1).getList().get(0);
        View archView = findViewById(oneContent.getId());
        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
        ((ActionsEditHelper) mHelper).playCourse(oneContent.getVoiceName());
        UbtLog.d("EditHelper", oneContent.getVoiceName());
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
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        UbtLog.d(TAG, mContents.get(currentCourse - 1).getList().toString());
        CourseOne1Content oneContent = mContents.get(currentCourse - 1).getList().get(0);
        textView.setText(oneContent.getContent());
        textView.setBackgroundResource(oneContent.getDirection() == 0 ? R.drawable.bubble_guide_left : R.drawable.bubble_guide_right);
        View archView = findViewById(oneContent.getId());
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .setBackgroundDimEnable(true)
                .setDimValue(0.4f)
                .createPopup();

        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
        ((ActionsEditHelper) mHelper).playCourse(oneContent.getVoiceName());
        UbtLog.d("EditHelper", oneContent.getVoiceName());
    }


    public interface CourseProgressListener {
        void completeCurrentCourse(int current);

        void finishActivity();
    }

}