package com.ubt.alpha1e.blocklycourse;


import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotListener;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.blockly.BlocklyActivity;
import com.ubt.alpha1e.blocklycourse.model.CourseData;
import com.ubt.alpha1e.blocklycourse.model.UpdateCourseRequest;
import com.ubt.alpha1e.blocklycourse.videoPlayer.BlocklyVideoPlayer;
import com.ubt.alpha1e.blocklycourse.videoPlayer.BlocklyVideoPlayerListener;
import com.ubt.alpha1e.blocklycourse.videoPlayer.OnTransitionListener;
import com.ubt.alpha1e.blocklycourse.videoPlayer.ViewListener;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.ubt.alpha1e.base.Constant.SP_CURRENT_BLOCK_COURSE_ID;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class BlocklyCourseActivity extends MVPBaseActivity<BlocklyCourseContract.View, BlocklyCoursePresenter> implements BlocklyCourseContract.View, ViewListener {

    private static final String TAG = "BlocklyCourseActivity";

    @BindView(R.id.video_player)
    BlocklyVideoPlayer videoPlayer;
    @BindView(R.id.rl_go_pro)
    RelativeLayout rlGoPro;
    @BindView(R.id.iv_go_pro)
    ImageView ivGoPro;
    @BindView(R.id.iv_pause)
    ImageView ivPause;


    OrientationUtils orientationUtils;

    private boolean isTransition;

    private Transition transition;
    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";
    public final static String VIDEO_URL = "VIDEO_URL";
    public final static String COURSE_DATA = "COURSE_DATA";

    private CourseData courseData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTransition = getIntent().getBooleanExtra(TRANSITION, false);
        courseData = (CourseData)getIntent().getSerializableExtra(COURSE_DATA);
        UbtLog.d(TAG, "onCreate courseData:" + courseData);
        initVideoPlayer();

    }

    private void initVideoPlayer() {
//        String url = "http://7xr4xn.media1.z0.glb.clouddn.com/snh48sxhsy.mp4";
        UbtLog.d(TAG, "initVideoPlayer path:" + courseData.getLocalVideoPath());
        videoPlayer.setUp(courseData.getLocalVideoPath(), false, null, null);

        //videoPlayer.setSpeed(2f);
        orientationUtils = new OrientationUtils(this, videoPlayer);
        orientationUtils.setRotateWithSystem(false);
        orientationUtils.setEnable(false);

        videoPlayer.setViewListener(this); //设置UI显示回调

        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);

        videoPlayer.getStartButton().setVisibility(View.GONE);
        videoPlayer.setHideKey(false); //设置全屏不隐藏虚拟按键

        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        GSYVideoManager.instance().setOptionModelList(list);


        //videoPlayer.setBottomProgressBarDrawable(getResources().getDrawable(R.drawable.video_new_progress));
        //videoPlayer.setDialogVolumeProgressBar(getResources().getDrawable(R.drawable.video_new_volume_progress_bg));
        //videoPlayer.setDialogProgressBar(getResources().getDrawable(R.drawable.video_new_progress));
        //videoPlayer.setBottomShowProgressBarDrawable(getResources().getDrawable(R.drawable.video_new_seekbar_progress),
        //getResources().getDrawable(R.drawable.video_new_seekbar_thumb));
        //videoPlayer.setDialogProgressColor(getResources().getColor(R.color.colorAccent), -11);

        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(false);
        videoPlayer.setIfCurrentIsFullscreen(true);

        //设置横屏锁住
        videoPlayer.setLockLand(true);
        //点击屏幕播放
        videoPlayer.setThumbPlay(false);
        videoPlayer.setIsTouchWigetFull(false);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbtLog.d(TAG, "back");
                onBackPressedSupport();
            }
        });

        ivGoPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPlayer.taskShotPic(new GSYVideoShotListener() {
                    @Override
                    public void getBitmap(final Bitmap bitmap) {
                        if(bitmap != null){

                            try {
                                BlocklyUtil.saveBitmap(bitmap, BlocklyActivity.SHOTCUT_NAME);
                                UbtLog.d(TAG, "jump block bitmap");
                                Intent intent = new Intent();
                                intent.putExtra(BlocklyActivity.FROM_VIDEO, true);
                                intent.setClass(BlocklyCourseActivity.this, BlocklyActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.activity_open_up_down, 0);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }
        });

        videoPlayer.setStandardVideoAllCallBack(new BlocklyVideoPlayerListener(){

            @Override
            public void onClickStop(String s, Object... objects) {
                UbtLog.d(TAG, "onClickStop");
                ivPause.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClickResume(String s, Object... objects) {
                UbtLog.d(TAG, "onClickResume");
                ivPause.setVisibility(View.GONE);
            }

            @Override
            public void onClickStopFullscreen(String s, Object... objects) {
                UbtLog.d(TAG, "onClickStopFullscreen");
                ivPause.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClickResumeFullscreen(String s, Object... objects) {
                UbtLog.d(TAG, "onClickResumeFullscreen");
                ivPause.setVisibility(View.GONE);
            }

            @Override
            public void onAutoComplete(String s, Object... objects) {
                UbtLog.d(TAG, "videoPlayer onAutoComplete");
                showResultDialog(1, true);
            }

            @Override
            public void onPlayError(String s, Object... objects) {
                super.onPlayError(s, objects);
                ToastUtils.showShort("视频播放错误，即将退出");
                courseData.setLocalVideoPath("");
                courseData.updateAll("cid = ?", ""+courseData.getCid());
                finish();
            }
        });

        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UbtLog.d(TAG, "ivPause");
                videoPlayer.clickPauseIcon();
            }
        });

        //过渡动画
        initTransition();


    }

    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(videoPlayer, IMG_TRANSITION);
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            videoPlayer.startPlayLogic();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new OnTransitionListener(){
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    videoPlayer.startPlayLogic();
                    transition.removeListener(this);
                }
            });
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressedSupport() {
        //释放所有
        videoPlayer.setStandardVideoAllCallBack(null);
        GSYVideoPlayer.releaseAllVideos();
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressedSupport();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            }, 500);
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_blockly_video;
    }

    @Override
    public void onClickUiToggle() {
        UbtLog.d(TAG, "onClickUiToggle");
        if(rlGoPro.getVisibility() == View.VISIBLE){
            rlGoPro.setVisibility(View.GONE);
        }else{
            rlGoPro.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideAllWidget() {
        UbtLog.d(TAG, "hideAllWidget");
        rlGoPro.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);

    }



    /**
     * 显示完成结果
     *
     * @param course
     * @param result
     */
    public void showResultDialog(final int course, boolean result) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_action_course_result, null);
        TextView tvResult = contentView.findViewById(R.id.tv_result);
        tvResult.setText(result ? "闯关成功" : "闯关失败");
        TextView title = contentView.findViewById(R.id.tv_card_name);
        title.setText("" + courseData.getName());
        ((ImageView) contentView.findViewById(R.id.iv_result)).setImageResource(result ? R.drawable.img_level_success : R.drawable.img_level_fail);
        ViewHolder viewHolder = new ViewHolder(contentView);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.6); //设置宽度

        DialogPlus.newDialog(this)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentWidth(width)
                .setContentBackgroundResource(android.R.color.transparent)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_retry) {

                            if(courseData.getCid() >= courseData.getCurrGraphProgramId()){
                                UpdateCourseRequest courseRequest = new UpdateCourseRequest();
                                UbtLog.d(TAG, "cid:" + courseData.getCid());
                                courseRequest.setCurrGraphProgramId(courseData.getCid()+1);
                                OkHttpClientUtils.getJsonByPostRequest(HttpEntity.UPDATE_BLOCKLY_COURSE, courseRequest, 0).execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        UbtLog.e(TAG, "updateCurrentCourse onError:" + e.getMessage());
                                        //保存失败则主动更新本地数据，并保存当前进度
                                        ContentValues values = new ContentValues();
                                        values.put("currGraphProgramId", courseData.getCid() + 1);
                                        DataSupport.updateAll(CourseData.class, values);
                                        SPUtils.getInstance().put(SP_CURRENT_BLOCK_COURSE_ID, courseData.getCid() + 1);
                                        CourseData updateCourseData = new CourseData();
                                        updateCourseData.setStatus("1");
                                        updateCourseData.update(courseData.getCid() + 1);

                                        onBackPressed();
                                        dialog.dismiss();

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        UbtLog.d(TAG, "updateCurrentCourse onResponse:" + response.toString());
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean status = jsonObject.getBoolean("status");
                                            if(status){
                                                onBackPressed();
                                                dialog.dismiss();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }else{
                                onBackPressed();
                                dialog.dismiss();
                            }


                        }
                    }
                })
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    @Override
    public void updateSuccess() {

    }

    @Override
    public void updateFail() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }


}
