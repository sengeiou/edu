package com.ubt.alpha1e.edu.blockly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.ImageTools;
import com.ubt.alpha1e.edu.data.model.LessonInfo;
import com.ubt.alpha1e.edu.data.model.LessonTaskInfo;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.helper.CourseHelper;
import com.ubt.alpha1e.edu.utils.SizeUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *LessonTaskView
 * @author wmma
 * @description 全局浮动控制窗口
 * @date 2016/10/25
 */


public class LessonTaskView {

    private static final String TAG = "LessonTaskView";

    //定义浮动窗口布局
    private LinearLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    //control
    private BaseActivity mBaseActivity;

    private Context mContext;
    private static LessonTaskView mLessonTaskView = null;

    private ViewPager mViewPager;
    private ImageView ivLessonTaskOperation,ivLessonTaskDetail;
    private RelativeLayout rlLessonTaskOperation,rlLessonTaskDetail,rl_notice,rlLessonTask;
    private LinearLayout llLessonTask;
    private ImageView gifNotice = null;
    private ImageView ivNoticeHorn = null;

    private RecyclerView rwPageIndex;
    private LinearLayoutManager mLayoutManager;
    private PageIndexRecyclerAdapter mAdapter;
    private List<LessonTaskInfo> mLessonTaskList = null;
    private List<LessonTaskInfo> mLessonUnlockTaskList = null;
    private TaskPagerAdapter mTaskPagerAdapter = null;
    private LessonInfo mLessonInfo = null;
    private int mCurrentTaskIndex = 0;
    private boolean isMp3Playing = false;

    private boolean isShowLessonTaskDetail = true;

    private static final int UPDATE_UI = 1;
    private static final int UPDATE_DETAIL_PIC = 2;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_UI:
                    mAdapter.notifyDataSetChanged();
                    mTaskPagerAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_DETAIL_PIC:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    ivLessonTaskDetail.setImageBitmap(bitmap);
                    break;
                default:
                    break;
            }

        }
    };

    public static LessonTaskView getInstace(Context context){
        if(mLessonTaskView != null){
            mLessonTaskView.onDestroy();
            mLessonTaskView = null;
        }
        mLessonTaskView = new LessonTaskView(context);
        return mLessonTaskView;
    }

    public static LessonTaskView getSimpleInstace(){
        return mLessonTaskView;
    }

    public static void closeLessonTaskView(){
        if(mLessonTaskView != null){
            mLessonTaskView.onDestroy();
            mLessonTaskView = null;
        }
    }

    public LessonTaskView(Context context) {
        Log.d(TAG, "Float View  Created!");
        mContext = context;
    }

    private void initHelper() {
        Log.d(TAG, "----init MyActionHelper!");
        mBaseActivity = AlphaApplication.getBaseActivity();

    }

    public LessonTaskView setTaskData(LessonInfo lessonInfo, List<LessonTaskInfo> taskInfoList){
        if(mLessonTaskList == null){
            mLessonTaskList = new ArrayList<>();
        }else {
            mLessonTaskList.clear();
        }

        if(mLessonUnlockTaskList == null){
            mLessonUnlockTaskList = new ArrayList<>();
        }else {
            mLessonUnlockTaskList.clear();
        }
        for(LessonTaskInfo taskInfo : taskInfoList){
            if(taskInfo.is_unlock == 1){
                mLessonUnlockTaskList.add(taskInfo);
            }
        }

        mLessonTaskList.addAll(taskInfoList);
        mLessonInfo = lessonInfo;
        return mLessonTaskView;
    }

    public void unLockNext(LessonTaskInfo taskInfo){

        boolean hasAdd = false;
        for(LessonTaskInfo info : mLessonUnlockTaskList){
            if(info.task_id == taskInfo.task_id){
                hasAdd = true;
                break;
            }
        }
        if(!hasAdd){
            mLessonUnlockTaskList.add(taskInfo);
        }

        for(LessonTaskInfo info : mLessonTaskList){
            if(info.task_id == taskInfo.task_id){
                info.is_unlock = taskInfo.is_unlock;
                break;
            }
        }
        mHandler.sendEmptyMessage(UPDATE_UI);
    }

    public void playAgain(){
        refreshBlocklyTaskData(mCurrentTaskIndex);
    }

    public void gotoNext(){
        int index = mViewPager.getCurrentItem();

        UbtLog.d(TAG,"index = " + index + "    getChildCount =  " + mViewPager.getChildCount()
                + "  " + mLessonUnlockTaskList.size() + "    " + mViewPager.getAdapter().getCount());
        if((index + 1) < mLessonUnlockTaskList.size()){
            mViewPager.setCurrentItem(index+1);
        }
    }

    public void setMp3PlayFinish(){
        isMp3Playing = false;
        gifNotice.setVisibility(View.INVISIBLE);
        ivNoticeHorn.setVisibility(View.VISIBLE);
    }

    public void show(){
        initHelper();
        createFloatView();
    }

    private void createFloatView() {
        boolean isPad = AlphaApplication.isPad();
        int layoutId = R.layout.view_lesson_task;
        if(isPad){
            layoutId = R.layout.view_lesson_task_pad;
        }

        UbtLog.d(TAG, "------createFloatView-------" + isPad);

        wmParams = new WindowManager.LayoutParams();
        //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final float scale = mBaseActivity.getResources().getDisplayMetrics().density;

        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.CENTER | Gravity.TOP;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(layoutId, null);

        initView(mFloatLayout);
        initControlListener();
        initData();

        llLessonTask.setPadding(0,0,0,0);

        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "--LessonTask onTouched--");
                llLessonTask.setPadding(0,0,0,0);
                return false;
            }
        });

        mWindowManager.addView(mFloatLayout, wmParams);
        //默认隐藏，显示由blockly那边调
        mFloatLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化控件和设置点击事件
     * @param view
     */
    private void initView(View view) {
        rlLessonTask = (RelativeLayout) view.findViewById(R.id.rl_lesson_task);

        if(!AlphaApplication.isPad()){
            LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) rlLessonTask.getLayoutParams();
            //UbtLog.d(TAG,"getScreenWidth = " + SizeUtils.getScreenWidth(mContext) + "   density = " + mContext.getResources().getDisplayMetrics().density);
            int screenWidth = SizeUtils.getScreenWidth(mContext);
            float density = mContext.getResources().getDisplayMetrics().density;
            if(screenWidth == 1920 && density == 3){
                llParams.width = SizeUtils.dip2px(mContext, 400);
            }else {
                llParams.width = SizeUtils.dip2px(mContext, 360);
            }
            rlLessonTask.setLayoutParams(llParams);
        }

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        ivLessonTaskOperation = (ImageView)view.findViewById(R.id.iv_lesson_task_operation);
        ivLessonTaskDetail = (ImageView)view.findViewById(R.id.iv_lesson_task_detail);
        rlLessonTaskOperation = (RelativeLayout) view.findViewById(R.id.rl_lesson_task_operation);
        rlLessonTaskDetail = (RelativeLayout) view.findViewById(R.id.rl_lesson_task_detail);
        rl_notice = (RelativeLayout) view.findViewById(R.id.rl_notice);
        llLessonTask = (LinearLayout) view.findViewById(R.id.ll_lesson_task);
        gifNotice = (ImageView) view.findViewById(R.id.gif_notice);
        ivNoticeHorn = (ImageView) view.findViewById(R.id.iv_notice_horn);

        mTaskPagerAdapter = new TaskPagerAdapter(mContext,mLessonUnlockTaskList);
        mViewPager.setAdapter(mTaskPagerAdapter);
        mViewPager.setOnPageChangeListener(onPageChangeListener);

        initRecyclerViews(view);
    }

    public void initRecyclerViews(View view) {

        rwPageIndex = (RecyclerView) view.findViewById(R.id.recyclerview_page_index);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rwPageIndex.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator animator = rwPageIndex.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mAdapter = new PageIndexRecyclerAdapter(mContext,mLessonTaskList);
        rwPageIndex.setAdapter(mAdapter);
    }


    private void initControlListener(){

        rlLessonTaskOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrHiddenTaskDetail();
            }
        });

        gifNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMp3();
            }
        });

        ivNoticeHorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMp3();
            }
        });
    }

    private void initData(){
        mCurrentTaskIndex = 0;
        for(int i=0; i <mLessonUnlockTaskList.size();i++){
            if(mLessonUnlockTaskList.get(i).status == 0){
                mCurrentTaskIndex = i;
                break;
            }
        }

        mViewPager.setCurrentItem(mCurrentTaskIndex);
        UbtLog.d(TAG,"mCurrentTaskIndex = " + mCurrentTaskIndex + " " + mViewPager.getChildCount());
        refreshDetailPic(mCurrentTaskIndex);
        refreshBlocklyTaskData(mCurrentTaskIndex);
        refreshPageIndex(mCurrentTaskIndex);
    }

    private void playMp3(){
        if(isMp3Playing){
            isMp3Playing = false;
            ((BlocklyActivity) mContext).stopMP3Play();
            gifNotice.setVisibility(View.INVISIBLE);
            ivNoticeHorn.setVisibility(View.VISIBLE);
        }else {
            LessonTaskInfo currentTaskInfo = mLessonTaskList.get(mCurrentTaskIndex);
            File voiceFile = CourseHelper.getLocalTaskFile(mLessonInfo,currentTaskInfo.task_voice);
            UbtLog.d(TAG,"task_voice = " + currentTaskInfo.task_voice + "   " + voiceFile.getPath());

            if(voiceFile.exists()){
                isMp3Playing = true;
                ((BlocklyActivity) mContext).playMP3(voiceFile.getPath());
                gifNotice.setVisibility(View.VISIBLE);
                ivNoticeHorn.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setShowOrHidden(boolean isShow){
        if(isShow){
            mFloatLayout.setVisibility(View.VISIBLE);
        }else {
            mFloatLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void doHiddenTaskDetail(){
        showOrHiddenTaskDetail();
    }

    private void showOrHiddenTaskDetail(){

        if(isShowLessonTaskDetail){
            isShowLessonTaskDetail = false;
            Animation slideOutAnimation = AnimationUtils.loadAnimation(mContext,R.anim.slide_out_up);
            rlLessonTaskDetail.startAnimation(slideOutAnimation);
            ivLessonTaskOperation.setImageResource(R.drawable.task_down_arrow);
            rlLessonTaskDetail.setVisibility(View.GONE);
            ((BlocklyActivity) mContext).showOrHiddenBlankPage(false);
            slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rlLessonTaskDetail.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }else {
            isShowLessonTaskDetail = true;

            Animation slideInAnimation = AnimationUtils.loadAnimation(mContext,R.anim.slide_in_up);
            rlLessonTaskDetail.startAnimation(slideInAnimation);
            rlLessonTaskDetail.setVisibility(View.VISIBLE);
            ivLessonTaskOperation.setImageResource(R.drawable.task_up_arrow);

            ((BlocklyActivity) mContext).showOrHiddenBlankPage(true);
        }
    }

    public boolean isShowLessonTaskDetail(){
        return isShowLessonTaskDetail;
    }

    public void onDestroy() {
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }

    private void refreshPageIndex(int position){

        for(int i = 0; i<mLessonTaskList.size();i++){
            LessonTaskInfo taskInfo = mLessonTaskList.get(i);
            if(i == position){
                taskInfo.is_current_show = 1;
            }else {
                taskInfo.is_current_show = 0;
            }
        }
        mHandler.sendEmptyMessage(UPDATE_UI);
    }

    private void refreshDetailPic(int position){

        File picFile = CourseHelper.getLocalTaskFile(mLessonInfo,mLessonTaskList.get(position).task_pic);
        UbtLog.d(TAG,"picFile = " + picFile.getPath() + "   " + picFile.exists());
        if(picFile.exists()){
            Bitmap bitmap = ImageTools.compressImage(picFile, 0, 0, false);
            Message msg = new Message();
            msg.what = UPDATE_DETAIL_PIC;
            msg.obj = bitmap;
            mHandler.sendMessage(msg);

        }
    }

    /**
     * 刷新blockly数据
     * @param position
     */
    private void refreshBlocklyTaskData(int position){
        UbtLog.d(TAG,"refreshBlocklyTaskData = " + mLessonTaskList.get(position));
        ((BlocklyActivity) mContext).refreshBlocklyTaskData(mLessonTaskList.get(position));
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //UbtLog.d(TAG,"onPageScrolled =  " + position + "    positionOffset = " + positionOffset + " positionOffsetPixels = " + positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            UbtLog.d(TAG,"onPageSelected = " + position);
            mCurrentTaskIndex = position;
            refreshPageIndex(position);
            refreshDetailPic(position);
            refreshBlocklyTaskData(position);

            if(isMp3Playing){
                isMp3Playing = false;
                ((BlocklyActivity) mContext).stopMP3Play();
                gifNotice.setVisibility(View.INVISIBLE);
                ivNoticeHorn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            UbtLog.d(TAG,"onPageScrollStateChanged =  " + state);
        }
    };

    class TaskPagerAdapter extends PagerAdapter {

        private List<LessonTaskInfo> mData = null;
        private Context mContext;

        TaskPagerAdapter(Context mContext, List<LessonTaskInfo> mData)
        {
            this.mData = mData;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            UbtLog.d(TAG,"position == " + position);

            View view = View.inflate(mContext,R.layout.layout_task_item, null);
            TextView tvTaskContent  = (TextView) view.findViewById(R.id.tv_task_content);

            String taskText = mData.get(position).task_text;
            UbtLog.d(TAG,"task_text => " + taskText + "    main_text = " + mData.get(position).main_text);

            SpannableString taskGuideStyle = new SpannableString(taskText);
            String[] mainKey = null;
            if(!TextUtils.isEmpty(mData.get(position).main_text)){
                mainKey = mData.get(position).main_text.split(",");
            }
            if(mainKey != null){
                for(String key : mainKey){
                    List<Integer> indexs = getIndex(taskText,key);
                    UbtLog.d(TAG,"taskGuide = " + taskText + "   key = " + key + "     indexs = " + indexs);
                    for(int startIndex : indexs){
                        taskGuideStyle.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.T11)),startIndex,startIndex + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            tvTaskContent.setText(taskGuideStyle);
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 获取strings字符串中所有str字符所在的下标
         * @param strings 母字符串
         * @param str 子字符串
         * @return 字符串在母字符串中下标集合，如果母字符串中不包含子字符串，集合长度为零
         */
        public List<Integer> getIndex(String strings, String str){
            List<Integer> list=new ArrayList();
            int flag=0;
            while (strings.indexOf(str)!=-1){
                //截取包含自身在内的前边部分
                String aa= strings.substring(0,strings.indexOf(str)+str.length());
                flag=flag+aa.length();
                list.add(flag-str.length());
                strings=strings.substring(strings.indexOf(str)+str.length());
            }
            return list;
        }


    }


}


