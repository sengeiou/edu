package com.ubt.alpha1e.edu.blockly;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.pg.PG;
import com.bumptech.glide.Glide;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.business.thrid_party.IWeiXinListener;
import com.ubt.alpha1e.edu.data.Constant;
import com.ubt.alpha1e.edu.data.ImageTools;
import com.ubt.alpha1e.edu.data.model.LessonInfo;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.event.LessonEvent;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.ui.LoginActivity;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LessonDetailDialog;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.helper.CourseHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @className BlocklyActivity
 *
 * @author wmma
 * @description Google Block逻辑编程页面
 * @date 2017/2/22
 * @update
 */


public class BlocklyCourseActivity extends BaseActivity implements BaseDiaUI,IUiListener,IWeiXinListener {

    private static final String TAG = "BlocklyCourseActivity";

    private static final int OPEN_LOCK_FINISH = 1;
    private static final int OPEN_LOCK_FINISH1 = 2;

    private HorizontalScrollView hsvLessons = null;
    private RelativeLayout rlLessons = null;

    private int scale = 0;

    private LayoutInflater mInflater;
    private View convertView = null;
    private ImageView ivBack = null;

    private int[] lessonItemMarginLeft = {90, 100, 110, 170, 130, 130, 130};
    private int[] lessonItemMarginTop =  {230, 80, 210, 190, 90, 200, 90};

    private int[] lessonItemMarginLeft_pad = {90, 100, 110, 170, 130, 140, 130};
    private int[] lessonItemMarginTop_pad =  {500, 225, 460, 400, 225, 430, 245};

    private LoadingDialog mLoadingDialog;

    private LessonDetailDialog mLessonDetailDialog = null;

    List<LessonHolder> mLessonHolderList = new ArrayList<>();
    List<LessonInfo> mLessonInfoList = new ArrayList<>();
    private LessonInfo waitLessonInfo = new LessonInfo();

    private boolean isPad = false;  //当前设备是否平板
    private boolean hasSetBshu = false; //是否已经设置背景大小
    private boolean hasShowCurrent = false; //是否显示过当前
    private boolean hasSyncTaskSuccess = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case OPEN_LOCK_FINISH:
                    UbtLog.d(TAG,"=======OPEN_LOCK_FINISH===");

                    int unlockIndex = msg.arg1;
                    LessonHolder lessonHolder = mLessonHolderList.get(unlockIndex);
                    lessonHolder.rlOpemLock.setVisibility(View.INVISIBLE);
                    lessonHolder.pbRotate.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockly_course);
        scale = (int) this.getResources().getDisplayMetrics().density;
        isPad = AlphaApplication.isPad();
        hasSyncTaskSuccess = false;
        hasSetBshu = false;
        if(isPad){
            lessonItemMarginTop = lessonItemMarginTop_pad;
            for(int i = 0; i< lessonItemMarginLeft.length;i++){
                lessonItemMarginLeft[i] = (int)(lessonItemMarginLeft[i]*1.3);
            }
        }

        initUI();
        mHelper = new CourseHelper(this);

    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(BlocklyCourseActivity.class.getSimpleName());
        super.onResume();

        UbtLog.d(TAG, "onResume isPad = " + AlphaApplication.isPad() + "    mCourseAccessToken = " + mHelper.mCourseAccessToken);
        if(((AlphaApplication)getApplicationContext()).getCurrentUserInfo() != null){

            showLoading();
            if(TextUtils.isEmpty(mHelper.mCourseAccessToken)){
                mHelper.initCourseAccessToken();
            }else {
                if(hasSyncTaskSuccess){
                    ((CourseHelper)mHelper).getLessons(CourseHelper.mCurrentCourseId);
                }else {
                    ((CourseHelper)mHelper).syncBatchData(CourseHelper.mCurrentCourseId);
                }
            }
        }

        rlLessons.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
             @Override
             public void onGlobalLayout() {
                 rlLessons.post(new Runnable() {
                        public void run() {
                            //图片大小 2668*750
                            FrameLayout.LayoutParams llParams = (FrameLayout.LayoutParams) rlLessons.getLayoutParams();

                            float hf = rlLessons.getHeight() / 750f;//缩放倍数
                            if(rlLessons.getWidth() < 2668 * hf && !hasSetBshu){
                                hasSetBshu = true;
                                llParams.width = (int)(2668 * hf);
                                rlLessons.setLayoutParams(llParams);
                            }
                        }
                    });
                 }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "onDestroy");
        dismissLoading();
    }

    @Subscribe
    public void onEventLesson(LessonEvent event){
        UbtLog.d(TAG,"onEventLesson = " + event.getEvent());
        if(event.getEvent() == LessonEvent.Event.DO_GET_LESSON){
            dealLessonUI(event.getLessonInfoList());
        }else if(event.getEvent() == LessonEvent.Event.DO_GET_COURSE_ACCESS_TOKEN_SUCCESS){

            //如果获取token成功，则同步数据，否则直接查课程
            ((CourseHelper)mHelper).syncBatchData(CourseHelper.mCurrentCourseId);

        }else if(event.getEvent() == LessonEvent.Event.DO_GET_COURSE_ACCESS_TOKEN_FAIL
                || event.getEvent() == LessonEvent.Event.DO_SYNC_TASK_DATA_FAIL
                || event.getEvent() == LessonEvent.Event.DO_SYNC_TASK_DATA_SUCCESS){

            if(event.getEvent() == LessonEvent.Event.DO_SYNC_TASK_DATA_SUCCESS){
                hasSyncTaskSuccess = true;
            }
            ((CourseHelper)mHelper).getLessons(CourseHelper.mCurrentCourseId);
        }else if(event.getEvent() == LessonEvent.Event.DO_TOKEN_ERROR){
            resetUserData();
            //((CourseHelper)mHelper).getLessons(CourseHelper.mCurrentCourseId);
        }
    }

    private void resetUserData(){
        hasSyncTaskSuccess = false;
        dismissLoading();
        ((AlphaApplication) getApplicationContext()).clearCurrentUserInfo();
        LoginActivity.launchActivity(BlocklyCourseActivity.this,true,Constant.USER_LOGIN_REQUEST_CODE);
    }

    /**
     * 根据数据处理UI
     * @param lessonInfoList
     */
    private void dealLessonUI(List<LessonInfo> lessonInfoList){
        if(lessonInfoList != null && !lessonInfoList.isEmpty()){
            UbtLog.d(TAG,"lessonInfoList == " + lessonInfoList.size());

            hasShowCurrent = false;
            mLessonHolderList.clear();
            mLessonInfoList.clear();
            mLessonInfoList.addAll(lessonInfoList);

            waitLessonInfo.lessonName = getStringResources("ui_remote_comming_soon");
            mLessonInfoList.add(waitLessonInfo);

            rlLessons.removeAllViews();

            //添加连线
            for(int i = 0; i < mLessonInfoList.size()-1; i++){
                addLessonLine(i);
            }

            //填充课时Item
            for(int i = 0; i < mLessonInfoList.size(); i++){
                addLessonView(mLessonInfoList.get(i), i, mLessonInfoList.size());
            }

            int unlockIndex = getNeedUnlockLessonIndex(mLessonInfoList);
            if(unlockIndex != -1){
                unlockTaskByIndex(unlockIndex);
            }
        }
        dismissLoading();
        //LessonTaskGuide.getInstace(this).show();

        mHandler.sendEmptyMessage(OPEN_LOCK_FINISH1);
    }

    public int getNeedUnlockLessonIndex(List<LessonInfo> lessonInfoList){
        int unlockIndex = -1; //解锁下标
        for(int i = 0;i < lessonInfoList.size()-1 ; i++){
            LessonInfo lessonInfo = lessonInfoList.get(i);
            if(i == 0 && lessonInfo.status == 0){
                unlockIndex = i;
                break;
            }

            if(lessonInfo.taskDown == lessonInfo.taskTotal && (i+1) != lessonInfoList.size()-1){
                LessonInfo info = lessonInfoList.get(i+1);
                if(info.status == 0){
                    unlockIndex = i+1;
                    break;
                }
            }
        }
        return unlockIndex;
    }

    @Override
    protected void initUI() {
        mInflater = LayoutInflater.from(this);

        hsvLessons = (HorizontalScrollView) findViewById(R.id.hsv_lessons);
        rlLessons = (RelativeLayout) findViewById(R.id.ll_lessons);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    /**
     * 增加线
     */
    private void addLessonLine(int index){

        int leftMargin = 0;
        int nextLeftMargin = 0;
        for(int i = index; i >= 0; i--){
            leftMargin += lessonItemMarginLeft[i%7];
        }

        for(int i = index+1; i>=0; i--){
            nextLeftMargin += lessonItemMarginLeft[i%7];
        }

        int startX = leftMargin;
        int startY = lessonItemMarginTop[index%7];

        int endX = nextLeftMargin;
        int endY = lessonItemMarginTop[(index+1)%7];


        int rotation = getRotationBetweenPoint(startX,startY,endX,endY);
        int distance = getDistanceBetweenPoint(startX,startY,endX,endY);
        UbtLog.d(TAG,"rotation = " + rotation + "   distance = " + distance + "     index = " + index + "   startY = " + startY + " endY = " + endY);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.lesson_line);
        imageView.setPivotX(0);
        imageView.setPivotY(0);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setRotation(rotation);

        rlLessons.addView(imageView);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.leftMargin = leftMargin * scale;
        layoutParams.topMargin = lessonItemMarginTop[index%7] * scale;
        layoutParams.width = distance * scale;
        imageView.setLayoutParams(layoutParams);

        UserInfo userInfo = ((AlphaApplication)getApplicationContext()).getCurrentUserInfo();
        if(userInfo != null){
            //UbtLog.d(TAG,"userInfo = " + userInfo.token );
        }

    }


    /**
     *获取两点的夹角
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @return
     */
    public static int getRotationBetweenPoint(int startX, float startY, float endX, float endY) {
        double rotation = 0;

        //atan2(y2-y1,x2-x1);
        double rotationValue = Math.atan2(endY - startY,endX - startX);
        //将rotationValue转成角度值
        rotation = 180* rotationValue / Math.PI;

        return (int) rotation;
    }

    /**
     *获取两点的长度
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @return
     */
    public static int getDistanceBetweenPoint(int startX, float startY, float endX, float endY) {

        double _x = Math.abs(endX - startX);
        double _y = Math.abs(endY - startY);
        return (int)Math.sqrt(_x * _x + _y * _y);
    }

    /**
     * 添加课时Item
     */
    private void addLessonView(final LessonInfo info, int index, int totalSize){

        int leftMargin = 0;
        for(int i = index; i>=0; i--){
            leftMargin += lessonItemMarginLeft[i%7];
        }

        convertView = mInflater.inflate(R.layout.layout_lesson_item, null);

        final LessonHolder lessonHolder = new LessonHolder(convertView);

        if(index == totalSize - 1){//最后一个,敬请期待
            lessonHolder.ivLessonLogo.setImageResource(R.drawable.icon_lesson_more);
            lessonHolder.tvLessonName.setText(getStringResources("ui_remote_comming_soon"));

            lessonHolder.pbRotate.setVisibility(View.INVISIBLE);
            lessonHolder.rlOpemLock.setVisibility(View.VISIBLE);
        }else {
            File cacheFile = ((CourseHelper)mHelper).loadLocalImage(info.lessonIcon);
            if(cacheFile.exists()){
                Bitmap bitmap = ImageTools.compressImage(cacheFile, 0, 0, false);
                lessonHolder.ivLessonLogo.setImageBitmap(bitmap);
                UbtLog.d(TAG,"bitmap = " + bitmap);
            }else {
                Glide.with(this)
                        .load(info.lessonIcon)
                        .fitCenter().placeholder(R.drawable.icon_lesson_more)
                        .into(lessonHolder.ivLessonLogo);
            }

            lessonHolder.tvLessonName.setText(info.lessonName);
            lessonHolder.tvLessonNum.setText(info.taskDown + "/" + info.taskTotal);

            UbtLog.d(TAG,"info.lessonName = " + info.lessonName + "   info.status = " +info.status);
            if(info.status == 0){
                lessonHolder.pbRotate.setVisibility(View.INVISIBLE);
                lessonHolder.rlOpemLock.setVisibility(View.VISIBLE);
            }else {
                lessonHolder.rlOpemLock.setVisibility(View.INVISIBLE);
                if(info.taskDown < info.taskTotal && !hasShowCurrent){
                    hasShowCurrent = true;
                    lessonHolder.pbRotate.setVisibility(View.VISIBLE);
                }else {
                    if(!hasShowCurrent && (index+1) == mLessonInfoList.size()-1){
                        lessonHolder.pbRotate.setVisibility(View.VISIBLE);
                    }else {
                        lessonHolder.pbRotate.setVisibility(View.INVISIBLE);
                    }
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(info.status == 0){//是否为解锁

                    }else {
                        //((CourseHelper)mHelper).getUserTaskResultFromRemote(info.lessonId);

                        showLessonDetailDialog(info);
                    }

                }
            });
        }

        rlLessons.addView(convertView);
        mLessonHolderList.add(lessonHolder);

        RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) convertView.getLayoutParams();
        UbtLog.d(TAG,"rlParams = " + rlParams + "   rlParams.getChildCount = " + rlLessons.getChildCount());

        //45为图标的1/2 大小
        rlParams.leftMargin = (leftMargin - 45) * scale;
        rlParams.topMargin =  (lessonItemMarginTop[index%7] - 45) * scale;
        convertView.setLayoutParams(rlParams);
    }


    public void unlockTaskByIndex(int unlockIndex){

       LessonHolder lessonHolder = mLessonHolderList.get(unlockIndex);
       LessonInfo lessonInfo = mLessonInfoList.get(unlockIndex);

       final AnimationDrawable animation = (AnimationDrawable)lessonHolder.ivOpemLock.getBackground();

       if(lessonInfo != null && lessonInfo.status == 0){
           animation.start();
           // 计算动态图片所花费的事件
           int durationTime = 0;
           for (int i = 0; i < animation.getNumberOfFrames(); i++) {
               durationTime += animation.getDuration(i);
           }

           lessonInfo.status = 1;
           UbtLog.d(TAG,"info.status = " + lessonInfo.status);
           ((CourseHelper)mHelper).updateLesson(lessonInfo);

           Message msg = new Message();
           msg.what = OPEN_LOCK_FINISH;
           msg.arg1 = unlockIndex;
           mHandler.sendMessageDelayed(msg,durationTime);
       }

    }


    private void doShowLessonTask(LessonInfo info){
        Intent intent = new Intent(this,BlocklyActivity.class);
        intent.putExtra(Constant.IS_FROM_COURSE,true);
        intent.putExtra(Constant.LESSON_INFO, PG.convertParcelable(info));
        startActivity(intent);
    }



    private void showLessonDetailDialog(final LessonInfo info){
        if(mLessonDetailDialog == null){
            mLessonDetailDialog = new LessonDetailDialog(BlocklyCourseActivity.this)
                    .builder()
                    .setCancelable(true);
        }

        mLessonDetailDialog
                .setMsg(info.lessonText)
                .setImage(info.lessonPic)
                .setPositiveButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doShowLessonTask(info);
                    }
                });

        mLessonDetailDialog.show();
    }

    private void showLoading() {

        if (mLoadingDialog == null){
            mLoadingDialog = LoadingDialog.getInstance(this, this);
        }
        mLoadingDialog.setDoCancelable(true, 10);
        mLoadingDialog.show();
    }

    public void dismissLoading() {
      if (mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.cancel();
      }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constant.USER_LOGIN_REQUEST_CODE){
            if(((AlphaApplication)getApplicationContext()).getCurrentUserInfo() == null){
                BlocklyCourseActivity.this.finish();
            }
        }
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    @Override
    public void onComplete(Object o) {
        UbtLog.d(TAG,"onComplete = " + o);
    }

    @Override
    public void onError(UiError uiError) {
        UbtLog.d(TAG,"UiError = " + uiError);
    }

    @Override
    public void onCancel() {
        UbtLog.d(TAG,"onCancel = " );
    }

    @Override
    public void noteWeixinNotInstalled() {
        Toast.makeText(this,getStringResources("ui_action_share_no_wechat"),Toast.LENGTH_SHORT).show();
    }

    class LessonHolder {

        public ImageView ivLessonLogo,ivOpemLock;
        public TextView tvLessonName,tvLessonNum;
        public RelativeLayout rlOpemLock;
        public ProgressBar pbRotate;

        public LessonHolder(View view){
            ivLessonLogo = (ImageView)view.findViewById(R.id.iv_lesson_logo);
            tvLessonName = (TextView)view.findViewById(R.id.tv_lesson_name);
            tvLessonNum = (TextView)view.findViewById(R.id.tv_lesson_num);

            rlOpemLock = (RelativeLayout) view.findViewById(R.id.rl_open_lock);
            ivOpemLock = (ImageView)view.findViewById(R.id.iv_open_lock);

            pbRotate = (ProgressBar)view.findViewById(R.id.pb_rotate);
        }
    }
}
