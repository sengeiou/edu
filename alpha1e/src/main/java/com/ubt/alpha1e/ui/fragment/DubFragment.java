package com.ubt.alpha1e.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.czt.mp3recorder.MP3Recorder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.TimeTools;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.DubActivity;
import com.ubt.alpha1e.ui.custom.ShapedImageView;
import com.ubt.alpha1e.utils.AudioRecorder2Mp3Util;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifImageView;


/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */


public class DubFragment extends BaseFragment{

    private static final String TAG = DubFragment.class.getSimpleName();

    private View view;
    private DubActivity dubActivity;

    private ShapedImageView ivActionIcon;
    private TextView tvActionName, tvActionTime;
    private TextView tvStart, tvRepeat, tvFinish;
    private ImageView ivStart, ivRepeat, ivFinish;
    private LinearLayout llStart, llRepeat, llFinish;

    private ImageView ivDubRipple;
    private GifImageView gifDubRipple;

    private int type = -1;
    private DubCountDown dubCountDown;
    AudioRecorder2Mp3Util util = null;
    private NewActionInfo oldNewActionInfo;
    private NewActionInfo newNewActionInfo = null;

    private long actionTime;
    private String actionHeadUrl;
    private String actionName;

    private String mSchemeId = "";
    private String mSchemeName = "";

    private long actionId;

    private boolean isDubbing = false;
    private boolean dubState = false;

    private ImageView ivBlink;
    private final long BLINK_TIME = 1000;
    private TextView tvCountDown;

    private long dubbingTime = 0;

    MP3Recorder mRecorder;
    private MyCountDown countDown;
    private int count = 3;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dubActivity = (DubActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dub, null);
        Bundle data = getArguments();
        initData(data);

        initUI();
        return view;
    }

    private void initData(Bundle data){
        actionTime = data.getLong(dubActivity.ACTION_TIME);
        actionHeadUrl = data.getString(dubActivity.ACTION_HEAD_URL);
        actionName = data.getString(dubActivity.ACTION_NAME);
        actionId = data.getLong(dubActivity.ACTION_ID);
        type = data.getInt(dubActivity.ACTION_TYPE);
        dubState = data.getBoolean(dubActivity.DUB_STATE);

        newNewActionInfo = new NewActionInfo();
        newNewActionInfo.actionTime = actionTime;
        dubCountDown = new DubCountDown(actionTime *1000 +1000, 1000); //由于ontick回调进去时已经过了1s，所以在此增加1000ms

        //初始化录音功能
        if (mRecorder == null) {
            String mDir = FileTools.actions_new_cache;
            File file = new File(mDir);
            if(!file.exists()){
                file.mkdirs();
            }
            File mp3 = new File(mDir + File.separator+
                    "tep_audio_recorder_for_mp3.mp3");

            mRecorder = new MP3Recorder(mp3);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        UbtLog.d(TAG, " dubFragment1 onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, " dubFragment1 onDestroy");

        if(dubCountDown != null){
            dubCountDown.cancel();
        }

        if(countDown != null){
            countDown.cancel();
            count = 3;
        }
        if(mRecorder != null ){
            UbtLog.d(TAG, " dubFragment1 onDestroy isDubbing");
            try {
                mRecorder.stop();
            }catch (Exception e){
                UbtLog.e(TAG, "throw Ex=" + e.getMessage());
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    protected void initUI() {


        ivActionIcon = (ShapedImageView)view.findViewById(R.id.action_icon);
        tvActionName = (TextView)view. findViewById(R.id.action_name);
        tvActionTime = (TextView)view. findViewById(R.id.action_time);

        tvStart = (TextView)view. findViewById(R.id.start_stop_dub);
        tvRepeat = (TextView)view. findViewById(R.id.repeat_dub);
        tvFinish = (TextView) view.findViewById(R.id.finish_dub);

        ivStart = (ImageView) view.findViewById(R.id.iv_start_dub);
        ivRepeat = (ImageView) view.findViewById(R.id.iv_repeat_dub);
        ivFinish = (ImageView) view.findViewById(R.id.iv_finish_dub);

        llStart = (LinearLayout) view.findViewById(R.id.ll_start);
        llRepeat = (LinearLayout) view.findViewById(R.id.ll_again);
        llFinish = (LinearLayout) view.findViewById(R.id.ll_finish);

        ivDubRipple = (ImageView) view.findViewById(R.id.iv_dub_ripple);
        gifDubRipple = (GifImageView) view.findViewById(R.id.gif_dub_ripple);

        ivBlink = (ImageView) view.findViewById(R.id.iv_blink);
        tvCountDown = (TextView) view.findViewById(R.id.tv_countdown);

        UbtLog.e(TAG, "actionName=" + actionName);
        Glide.with(dubActivity).load(actionHeadUrl).placeholder(R.drawable.sec_action_logo).centerCrop().into(ivActionIcon);
        tvActionName.setText(actionName);
        tvActionTime.setText(TimeTools.getMMTimeForPublish((int)actionTime)+"");

        if(dubState) {
            llStart.setVisibility(View.GONE);
            llRepeat.setVisibility(View.VISIBLE);
            llFinish.setVisibility(View.VISIBLE);
        }

        ivStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbtLog.d(TAG, "tvStart onclick");
                dubActivity.isShowTips = true;
                dubActivity.doDefault();
                if(isDubbing){
                    dubCountDown.cancel();
                    try {
                        mRecorder.stop();
                    } catch (Exception e) {
                        UbtLog.e(TAG, "throw Ex=" + e.getMessage());
                    }

                    isDubbing = false;
                    llStart.setVisibility(View.GONE);
                    llRepeat.setVisibility(View.VISIBLE);
                    llFinish.setVisibility(View.VISIBLE);
                    ivDubRipple.setVisibility(View.VISIBLE);
                    gifDubRipple.setVisibility(View.GONE);
                    ivBlink.clearAnimation();
                    ivBlink.setVisibility(View.INVISIBLE);
                    tvActionTime.setText(TimeTools.getMMTimeForPublish(((int)(actionTime*1000-dubbingTime) + 1000)/1000)+"");
                }else{
                    count = 3;
                    countDown = new MyCountDown(4000, 1000);
                    countDown.cancel();
                    countDown.start();
                    llStart.setVisibility(View.GONE);
                    tvCountDown.setVisibility(View.VISIBLE);
                }

            }
        });

        ivRepeat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                llFinish.setVisibility(View.INVISIBLE);
                llRepeat.setVisibility(View.INVISIBLE);
                llStart.setVisibility(View.VISIBLE);
                ivStart.setImageResource(R.drawable.icon_start_dub);
                tvStart.setText(dubActivity.getStringResources("ui_dub_start"));
                tvActionTime.setText(TimeTools.getMMTimeForPublish((int)actionTime)+"");
            }
        });

        ivFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dubActivity.doDefault();
                dubActivity.switchToDubPreviewFragment();
            }
        });
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }



    public class MyCountDown extends CountDownTimer {

        public MyCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {

            count = 3;
            try {
                mRecorder.start();
            } catch (IOException e) {
                UbtLog.e(TAG, "throw Ex=" + e.getMessage());
            }

            isDubbing = true;
            ivDubRipple.setVisibility(View.GONE);
            gifDubRipple.setVisibility(View.VISIBLE);
            ivBlink.setVisibility(View.VISIBLE);
            viewStartBlink(ivBlink);
            tvCountDown.setVisibility(View.GONE);
            llStart.setVisibility(View.VISIBLE);
            dubCountDown.start();
            ivStart.setImageResource(R.drawable.icon_stop_dub);
            tvStart.setText(dubActivity.getStringResources("ui_dub_stop"));
            if(!dubActivity.isLostCoon()){
                dubActivity.doPlay();
            }


        }
        @Override
        public void onTick(long millisUntilFinished) {
            UbtLog.d(TAG, "onTick millisUntilFinished=" + millisUntilFinished + "count="+ count);
            if(count<1){
                count = 1;
            }
            tvCountDown.setText("" + /*(millisUntilFinished/1000)*/ count);
            count --;

        }

    }


    class DubCountDown extends CountDownTimer{

        public DubCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {

            tvActionTime.setText("00:00");
            tvActionTime.setText(TimeTools.getMMTimeForPublish((int)actionTime)+"");
            try{
                if(mRecorder != null){
                    mRecorder.stop();
                }
            }catch (Exception e){
               UbtLog.e(TAG, "throw Exception");
            }


            tvStart.setText(dubActivity.getStringResources("ui_dub_complete"));
            llStart.setVisibility(View.GONE);
            llRepeat.setVisibility(View.VISIBLE);
            llFinish.setVisibility(View.VISIBLE);
            ivDubRipple.setVisibility(View.VISIBLE);
            gifDubRipple.setVisibility(View.GONE);
            ivBlink.clearAnimation();
            ivBlink.setVisibility(View.INVISIBLE);
            isDubbing = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            UbtLog.d(TAG, "onTick millisUntilFinished=" + millisUntilFinished);
            dubbingTime = millisUntilFinished;
            tvActionTime.setText("" + TimeTools.getMMTimeForPublish((int)(millisUntilFinished/1000)));

        }
    }

    public  void  viewStartBlink(View v){
        Animation alphaAnimation = new AlphaAnimation( 1, 0 );
        alphaAnimation.setDuration( BLINK_TIME );
        alphaAnimation.setInterpolator( new LinearInterpolator( ) );
        alphaAnimation.setRepeatCount( Animation.INFINITE );
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        v.startAnimation( alphaAnimation );
    }





}
