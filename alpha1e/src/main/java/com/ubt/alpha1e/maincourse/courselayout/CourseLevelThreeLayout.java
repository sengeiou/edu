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
import com.ubt.alpha1e.action.model.PrepareMusicModel;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.maincourse.adapter.CourseArrowAminalUtil;
import com.ubt.alpha1e.maincourse.adapter.CourseItemAdapter;
import com.ubt.alpha1e.maincourse.adapter.CourseMusicDialogUtil;
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
import zhy.com.highlight.position.OnRightLocal1PosCallback;
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

public class CourseLevelThreeLayout extends BaseActionEditLayout implements CourseMusicDialogUtil.OnMusicDialogListener {
    private String TAG = CourseLevelThreeLayout.class.getSimpleName();
    private ImageView ivMusicArror;
    private ImageView playArrow;


    AnimationDrawable animation1;
    AnimationDrawable animation2;
    AnimationDrawable animation3;


    RelativeLayout mRlInstruction;
    private TextView mTextView;
    private boolean isInstruction;
    private ImageView ivBackInStruction;
    CourseMusicDialogUtil mMusicDialogUtil;

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

    public CourseLevelThreeLayout(Context context) {
        super(context);
    }

    public CourseLevelThreeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseLevelThreeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            if (course == 3) {
                if (recordlevel == 0 || recordlevel == 2) {
                    level = 1;
                } else if (recordlevel == 1) {
                    level = 2;
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
            ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor14.hts");
        } else if (currentCourse == 2) {
            CourseArrowAminalUtil.startViewAnimal(true, ivMusicArror, 2);
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
        playArrow = findViewById(R.id.iv_play_arrow);
        playArrow.setOnClickListener(this);

        ivMusicArror = findViewById(R.id.iv_music_arrow);
        ivMusicArror.setOnClickListener(this);

        mRlInstruction = (RelativeLayout) findViewById(R.id.rl_instruction);
        mTextView = (TextView) findViewById(R.id.tv_all_introduc);
        mTextView.setText(ResourceManager.getInstance(mContext).getStringResources("action_course_card3_1_all"));
        ivBackInStruction = findViewById(R.id.iv_back_instruction);
        ivBackInStruction.setOnClickListener(this);
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
    public void setActionMusicButton() {
        setImageViewBg();
        ivActionBgm.setEnabled(true);
        ivActionBgm.setImageResource(R.drawable.ic_add_music);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回按钮
                if (null != courseProgressListener) {
                    courseProgressListener.finishActivity();
                }
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
            case R.id.iv_back_instruction:
                if (null != courseProgressListener) {
                    courseProgressListener.finishActivity();
                }
                break;
            default:
        }
    }

    /**
     * 点击弹出音乐对话框
     */
    private void showMusicDialog() {
        ivActionBgm.setEnabled(false);
        ivActionBgm.setImageResource(R.drawable.ic_add_music_disable);
        CourseArrowAminalUtil.startViewAnimal(false, ivMusicArror, 2);
        ((ActionsEditHelper) mHelper).stopAction();
        if (null == mMusicDialogUtil) {
            mMusicDialogUtil = new CourseMusicDialogUtil(mContext);
        }
        mMusicDialogUtil.showMusicDialog(1, this);
    }


    /**
     * 播放按钮，过3秒钟结束
     */
    private void playAction() {
        startPlayAction();
        CourseArrowAminalUtil.startViewAnimal(false, playArrow, 1);
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


    /**
     * 音乐播放结束,并结束课程
     */
    @Override
    public void onPlayMusicComplete() {
        if (courseProgressListener != null) {
            courseProgressListener.completeCurrentCourse(2);
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
     * 播放音频或者动作结束
     */
    public void playComplete() {
        UbtLog.d(TAG, "播放完成");
        if (((Activity) mContext).isFinishing()) {
            return;
        }

        if (currentCourse == 1) {
            if (isInstruction) {
                isInstruction = false;
                mRlInstruction.setVisibility(View.GONE);
                showMusicLight();
            }
        } else if (currentCourse == 2) {
            UbtLog.d(TAG, "playComplete==" + 2);
            if (secondIndex == 1) {
                lostRightLeg = true;
                lostLeft();
                CourseArrowAminalUtil.startViewAnimal(false, ivMusicArror, 2);
            }

        }
    }


    /**
     * Activity执行onPause方法
     */
    public void onPause() {
        if (null != mMusicDialogUtil) {
            mMusicDialogUtil.pause();
        }
        pause();

    }


    /**
     * 音乐库介绍
     */
    private void showMusicLight() {
        setActionMusicButton();
        mHightLight = new HighLight(mContext)//
                .autoRemove(false)//设置背景点击高亮布局自动移除为false 默认为true
                .intercept(true)//设置拦截属性为false 高亮布局不影响后面布局
                .enableNext()
                .maskColor(0xAA000000)
                // .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(R.id.iv_action_bgm, R.layout.layout_pop_course_right_level, new OnRightLocal1PosCallback(30), new RectLightShape())
                .setOnShowCallback(new HighLightInterface.OnShowCallback() {
                    @Override
                    public void onShow(HightLightView hightLightView) {
                        HighLight.ViewPosInfo viewPosInfo = hightLightView.getCurentViewPosInfo();
                        if (null != viewPosInfo) {
                            int layoutId = viewPosInfo.layoutId;
                            View tipView = hightLightView.findViewById(layoutId);
                            tv = tipView.findViewById(R.id.tv_content);
                            tv.setText("音乐库");
                            UbtLog.d(TAG, "======onShow====showMusicLight1");
                        }
                    }
                });

        mHightLight.show();
        // ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor15.hts");
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
            showNextDialog(2);
            ((ActionsEditHelper) mHelper).stopAction();
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
        title.setText("第三关 了解音乐库");

        Button button = contentView.findViewById(R.id.btn_pos);
        button.setText("下一节");

        RecyclerView mrecyle = contentView.findViewById(R.id.recyleview_content);
        mrecyle.setLayoutManager(new LinearLayoutManager(mContext));

        CourseItemAdapter itemAdapter = new CourseItemAdapter(R.layout.layout_action_course_dialog, ActionCourseDataManager.getCourseActionModel(3, current));
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
                            currentIndex = 0;
                            setLayoutByCurrentCourse();
                            dialog.dismiss();
                        }
                    }
                })
                .setCancelable(false)
                .create().show();
    }


    /**
     * 点击添加音乐按钮
     *
     * @param prepareMusicModel
     */
    @Override
    public void onMusicConfirm(PrepareMusicModel prepareMusicModel) {
        super.onMusicConfirm(prepareMusicModel);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pause();
                ((ActionsEditHelper) mHelper).playAction(Constant.COURSE_ACTION_PATH + "AE_action editor15.hts");
                CourseArrowAminalUtil.startViewAnimal(true, playArrow, 2);
                ivPlay.setEnabled(true);
                ivActionBgm.setEnabled(false);
            }
        }, 1000);
    }

    @Override
    public void onStopRecord(PrepareMusicModel prepareMusicModel, int type) {

    }


}