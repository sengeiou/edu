package com.ubt.alpha1e.maincourse.courselayout;

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
import com.ubt.alpha1e.action.model.PrepareDataModel;
import com.ubt.alpha1e.base.popup.EasyPopup;
import com.ubt.alpha1e.base.popup.HorizontalGravity;
import com.ubt.alpha1e.base.popup.VerticalGravity;
import com.ubt.alpha1e.maincourse.adapter.ActionCourseTwoUtil;
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

public class CourseTwoLayout extends BaseActionEditLayout implements ActionCourseTwoUtil.OnCourseDialogListener {
    private String TAG = CourseTwoLayout.class.getSimpleName();
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvCourseContent;
    private RelativeLayout rlContent;

    ActionCourseTwoUtil mActionCourseTwoUtil;
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
            tvCourseContent.setText("1.基础模板");
            showPop(0);
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
        } else if (currentCourse == 2) {
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            tvCourseContent.setText("2.高级模板");
            showPop1();
        } else if (currentCourse == 3) {
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            tvCourseContent.setText("3.任务添加出招");
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
        setEnabled(false);
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
    public void setLibMoreButton() {
        setImageViewBg();
        ivActionLibMore.setEnabled(true);
        ivActionLib.setEnabled(false);
    }

    /**
     * 设置播放按钮高亮
     */
    public void setLibButton() {
        setImageViewBg();
        ivActionLibMore.setEnabled(false);
        ivActionLib.setEnabled(true);

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
            } else if (msg.what == 1116) {
                if (null != mCirclePop) {
                    mCirclePop.dismiss();
                }
                ivLeft.setEnabled(true);
            }
        }
    };

    /**
     * Activity onPause
     */
    public void onPause() {
        mHandler.removeMessages(1111);
        mHandler.removeMessages(1112);
        mHandler.removeMessages(1113);
        mHandler.removeMessages(1116);
        dismiss();
    }

    public void dismiss() {
        if (null != mCirclePop) {
            mCirclePop.dismiss();
        }
    }

    /**
     * 音频播放完成
     */
    public void playComplete() {
        if (null != mCirclePop) {
            mCirclePop.dismiss();
        }
        if (currentCourse == 1) {
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(1);
            }
            currentIndex = 0;
            mHandler.sendEmptyMessageDelayed(1111, 1000);
        } else if (currentCourse == 2) {
            if (courseProgressListener != null) {
                courseProgressListener.completeCurrentCourse(2);
            }
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            mHandler.sendEmptyMessageDelayed(1112, 1000);
        } else if (currentCourse == 3) {
            ivLeft.setEnabled(false);
            ivRight.setEnabled(false);
            mHandler.sendEmptyMessageDelayed(1113, 1000);
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
                    courseProgressListener.finishActivity(isSaveAction);

                }
                break;
            case R.id.iv_action_lib:
                if (currentCourse == 3) {
                    if (null == mActionCourseTwoUtil) {
                        mActionCourseTwoUtil = new ActionCourseTwoUtil(mContext);
                    }
                    mActionCourseTwoUtil.showActionDialog(1, this);
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
        setLibButton();
        CourseOne1Content oneContent = mContents.get(0).getList().get(0);
        UbtLog.d(TAG, "第一课时oneContent==" + oneContent.toString());
        //播放音频
        ((ActionsEditHelper) mHelper).playAction(oneContent.getActionPath());
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        UbtLog.d(TAG, mContents.get(currentCourse - 1).getList().toString());
        textView.setText(oneContent.getContent());
        textView.setBackgroundResource(oneContent.getDirection() == 0 ? R.drawable.bubble_left : R.drawable.bubble_right);
        View archView = findViewById(oneContent.getId());
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .createPopup();

        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
    }

    /**
     * 第二课时
     */

    private void showPop1() {
        setLibMoreButton();
        CourseOne1Content oneContent = mContents.get(1).getList().get(0);
        UbtLog.d(TAG, "第二课时oneContent==" + oneContent.toString());
        //播放音频
        ((ActionsEditHelper) mHelper).playAction(oneContent.getActionPath());
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        textView.setText(oneContent.getContent());
        textView.setBackgroundResource(oneContent.getDirection() == 0 ? R.drawable.bubble_left : R.drawable.bubble_right);
        View archView = findViewById(oneContent.getId());
        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .createPopup();

        mCirclePop.showAtAnchorView(archView, oneContent.getVertGravity(), oneContent.getHorizGravity(), oneContent.getX(), oneContent.getY());
    }

    private void showPop2() {
        setLibButton();
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_course_one, null);
        TextView textView = contentView.findViewById(R.id.tv_content);
        UbtLog.d(TAG, mContents.get(currentCourse - 1).getList().toString());
        textView.setText("学习完动作库的知识，现在让我们来完成一个小任务吧，你会把基础动作“出招”添加到时间轴上吗？");
        textView.setBackgroundResource(R.drawable.bubble_left);
        View archView = findViewById(R.id.iv_action_lib);

        mCirclePop = new EasyPopup(mContext)
                .setContentView(contentView)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .createPopup()
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivActionLib.setEnabled(true);

                    }
                });

        mCirclePop.showAtAnchorView(archView, VerticalGravity.CENTER, HorizontalGravity.RIGHT, 0, 0);
        mHandler.sendEmptyMessageDelayed(1116, 3000);

    }

    @Override
    public void onCourseConfirm(PrepareDataModel prepareDataModel) {
        UbtLog.d(TAG, "课时一完成");
        onActionConfirm(prepareDataModel);
        isSaveAction = true;
        if (courseProgressListener != null) {
            courseProgressListener.completeCurrentCourse(3);
        }
    }

    @Override
    public void playCourseAction(PrepareDataModel prepareDataModel) {
        playAction(prepareDataModel);
    }


    public interface CourseProgressListener {
        void completeCurrentCourse(int current);

        void finishActivity(boolean isComplete);
    }

}