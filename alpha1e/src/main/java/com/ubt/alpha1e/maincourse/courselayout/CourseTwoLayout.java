package com.ubt.alpha1e.maincourse.courselayout;

import android.content.Context;
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
import com.ubt.alpha1e.maincourse.model.ActionCourseOneContent;
import com.ubt.alpha1e.maincourse.model.CourseOne1Content;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zyyoona7.lib.EasyPopup;

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

public class CourseTwoLayout extends BaseActionEditLayout {
    private String TAG = CourseTwoLayout.class.getSimpleName();
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

    public CourseTwoLayout(Context context) {
        super(context);
    }

    public CourseTwoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseTwoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(1);
            }
            ivLeft.setEnabled(true);
            ivRight.setEnabled(true);
            tvCourseContent.setText(mContents.get(1).getCourseName());
            showPop1();
        } else if (currentCourse == 3) {
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(2);
            }
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

                break;
            case R.id.iv_right:

                break;

            case R.id.iv_back:
                if (null != courseProgressListener) {
                    courseProgressListener.finishActivity();
                }
                break;
            default:

        }
    }

    EasyPopup mCirclePop = null;


    /**
     * 第一课时
     *
     * @param index
     */
    private void showPop(int index) {
        if (index >= mContents.get(currentCourse - 1).getList().size()) {
            currentCourse++;
            currentIndex = 0;
            setLayoutByCurrentCourse();
            return;
        }
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        UbtLog.d(TAG, mContents.get(currentCourse - 1).getList().toString());
        CourseOne1Content oneContent = mContents.get(currentCourse - 1).getList().get(index);
        textView.setText(oneContent.getContent());
        textView.setBackgroundResource(oneContent.getDirection() == 0 ? R.drawable.bubble_guide_left : R.drawable.bubble_guide_right);
        View archView = findViewById(oneContent.getId());
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCirclePop.dismiss();
            }
        });
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .createPopup()
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        currentIndex++;
                        showPop(currentIndex);
                    }
                });

        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
        //mCirclePop.showAtAnchorView(recyclerViewFrames, VerticalGravity.ABOVE, HorizontalGravity.ALIGN_RIGHT);
    }

    /**
     * 第二课时
     */

    private void showPop1() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        UbtLog.d(TAG, mContents.get(currentCourse - 1).getList().toString());
        CourseOne1Content oneContent = mContents.get(currentCourse - 1).getList().get(0);
        textView.setText(oneContent.getContent());
        textView.setBackgroundResource(oneContent.getDirection() == 0 ? R.drawable.bubble_guide_left : R.drawable.bubble_guide_right);
        View archView = findViewById(oneContent.getId());
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCirclePop.dismiss();
                setLayoutByCurrentCourse();
            }
        });
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .createPopup()
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        UbtLog.d(TAG, "课时一完成");
                         
                    }
                });

        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
    }

    private void showPop2() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        UbtLog.d(TAG, mContents.get(currentCourse - 1).getList().toString());
        CourseOne1Content oneContent = mContents.get(currentCourse - 1).getList().get(0);
        textView.setText(oneContent.getContent());
        textView.setBackgroundResource(oneContent.getDirection() == 0 ? R.drawable.bubble_guide_left : R.drawable.bubble_guide_right);
        View archView = findViewById(oneContent.getId());
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCirclePop.dismiss();
                setLayoutByCurrentCourse();
            }
        });
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .createPopup()
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        UbtLog.d(TAG, "课时一完成");
                        if (courseProgressListener != null) {
                            courseProgressListener.completeCurrentCourse(3);
                        }
                    }
                });

        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
    }


    public interface CourseProgressListener {
        void completeCurrentCourse(int current);

        void finishActivity();
    }

}