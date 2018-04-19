package com.pbq.imagepicker.camera2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pbq.imagepicker.R;
import com.pbq.imagepicker.jcamera.ReturnButton;
import com.pbq.imagepicker.jcamera.listener.ClickListener;
import com.pbq.imagepicker.jcamera.listener.ReturnListener;
import com.pbq.imagepicker.jcamera.listener.TypeListener;

public class CaptureLayout2 extends FrameLayout {

    private static final String TAG = CaptureLayout2.class.getSimpleName();

    private CaptureListener2 captureLisenter;    //拍照按钮监听
    private TypeListener typeLisenter;          //拍照或录制后接结果按钮监听
    private ReturnListener returnListener;      //退出按钮监听
    private ClickListener leftClickListener;    //左边按钮监听
    private ClickListener rightClickListener;   //右边按钮监听

    public void setTypeLisenter(TypeListener typeLisenter) {
        this.typeLisenter = typeLisenter;
    }

    public void setCaptureLisenter(CaptureListener2 captureLisenter) {
        this.captureLisenter = captureLisenter;
    }

    public void setReturnLisenter(ReturnListener returnListener) {
        this.returnListener = returnListener;
    }

    private CaptureButton2 btn_capture;  //拍照按钮
    private Button btn_confirm;          //确认按钮
    private Button btn_cancel;           //取消按钮
    private ReturnButton btn_return;     //返回按钮
    private ImageView iv_custom_left;    //左边自定义按钮
    private ImageView iv_custom_right;   //右边自定义按钮
    private TextView txt_tip;            //提示文本

    private int layout_width;
    private int layout_height;
    private int button_size;
    private int iconLeft = 0;
    private int iconRight = 0;

    private float mDensity = 0;

    private boolean isFirst = true;

    public CaptureLayout2(Context context) {
        this(context, null);
    }

    public CaptureLayout2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        /*if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layout_width = outMetrics.widthPixels;
        } else {
            layout_width = outMetrics.widthPixels / 2;
        }

        button_size = (int) (layout_width / 4.5f);
        layout_height = button_size + (button_size / 5) * 2 + 100;*/
        mDensity = outMetrics.density;

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layout_height = outMetrics.heightPixels;
        } else {
            layout_height = outMetrics.heightPixels / 2;
        }

        button_size = (int) (layout_height / 4.5f);
        layout_width = button_size + (button_size / 5) * 2 + 100;

        initView();
        initEvent();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(layout_width, layout_height);
    }

    public void initEvent() {
        //默认Typebutton为隐藏
        iv_custom_right.setVisibility(GONE);
        btn_cancel.setVisibility(GONE);
        btn_confirm.setVisibility(GONE);
    }

    public void startTypeBtnAnimator() {
        //拍照录制结果后的动画
        if (this.iconLeft != 0){
            iv_custom_left.setVisibility(GONE);
        } else {
            btn_return.setVisibility(GONE);
        }
        if (this.iconRight != 0){
            iv_custom_right.setVisibility(GONE);
        }

        btn_capture.setVisibility(GONE);
        btn_cancel.setVisibility(VISIBLE);
        btn_confirm.setVisibility(VISIBLE);
        btn_cancel.setClickable(false);
        btn_confirm.setClickable(false);

        ObjectAnimator animator_cancel = ObjectAnimator.ofFloat(btn_cancel, "translationY", layout_height / 4, 0);
        ObjectAnimator animator_confirm = ObjectAnimator.ofFloat(btn_confirm, "translationY", -layout_height / 4, 0);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator_cancel, animator_confirm);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btn_cancel.setClickable(true);
                btn_confirm.setClickable(true);
            }
        });
        set.setDuration(200);
        set.start();
    }


    private void initView() {
        setWillNotDraw(false);
        Log.d(TAG,"button_size = " + button_size);
        //拍照按钮
        btn_capture = new CaptureButton2(getContext(), button_size);
        LayoutParams btn_capture_param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        btn_capture_param.gravity = Gravity.CENTER;
        btn_capture.setLayoutParams(btn_capture_param);
        btn_capture.setCaptureListener(new CaptureListener2() {
            @Override
            public void takePictures() {
                if (captureLisenter != null) {
                    captureLisenter.takePictures();
                }
            }

            @Override
            public void recordShort(long time) {
                if (captureLisenter != null) {
                    captureLisenter.recordShort(time);
                }
                startAlphaAnimation();
            }

            @Override
            public void recordStart() {
                if (captureLisenter != null) {
                    captureLisenter.recordStart();
                }
                startAlphaAnimation();
            }

            @Override
            public void recordEnd(long time) {
                if (captureLisenter != null) {
                    captureLisenter.recordEnd(time);
                }
                startAlphaAnimation();
                startTypeBtnAnimator();
            }

            @Override
            public void recordZoom(Rect zoom) {
                if (captureLisenter != null) {
                    captureLisenter.recordZoom(zoom);
                }
            }

            @Override
            public void recordError() {
                if (captureLisenter != null) {
                    captureLisenter.recordError();
                }
            }
        });

        //取消按钮
        btn_cancel = new Button(getContext());
        final LayoutParams btn_cancel_param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btn_cancel_param.gravity = Gravity.CENTER_HORIZONTAL;
        btn_cancel_param.setMargins(0, (layout_height / 4) - button_size / 2, 0, 0);
        btn_cancel_param.height = (int)(mDensity*60);
        btn_cancel_param.width = (int)(mDensity*60);

        btn_cancel.setBackgroundResource(R.drawable.imagepick_icon_camera_back);
        btn_cancel.setLayoutParams(btn_cancel_param);
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeLisenter != null) {
                    typeLisenter.cancel();
                }
                startAlphaAnimation();
//                resetCaptureLayout();
            }
        });

        //确认按钮
        btn_confirm = new Button(getContext());
        LayoutParams btn_confirm_param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btn_confirm_param.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        btn_confirm_param.setMargins(0, 0, 0, (layout_height / 4) - button_size / 2);
        btn_confirm_param.height = (int)(mDensity*60);
        btn_confirm_param.width = (int)(mDensity*60);

        btn_confirm.setBackgroundResource(R.drawable.imagepick_icon_camera_complete);
        btn_confirm.setLayoutParams(btn_confirm_param);
        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeLisenter != null) {
                    typeLisenter.confirm();
                }
                startAlphaAnimation();
//                resetCaptureLayout();
            }
        });



        //返回按钮
        btn_return = new ReturnButton(getContext(), (int) (button_size / 2.5f));
        LayoutParams btn_return_param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btn_return_param.gravity = Gravity.CENTER_HORIZONTAL;
        //btn_return_param.setMargins(layout_width / 6, 0, 0, 0);
        btn_return_param.setMargins(0, (layout_height - layout_height / 5), 0, 0);
        btn_return.setLayoutParams(btn_return_param);
        btn_return.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftClickListener != null) {
                    leftClickListener.onClick();
                }
            }
        });



        //左边自定义按钮
        iv_custom_left = new ImageView(getContext());
        LayoutParams iv_custom_param_left = new LayoutParams((int) (button_size / 2.5f), (int) (button_size / 2.5f));
        iv_custom_param_left.gravity = Gravity.CENTER_VERTICAL;
        iv_custom_param_left.setMargins(layout_width / 6, 0, 0, 0);
        iv_custom_left.setLayoutParams(iv_custom_param_left);
        iv_custom_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftClickListener != null) {
                    leftClickListener.onClick();
                }
            }
        });

        //右边自定义按钮
        iv_custom_right = new ImageView(getContext());
        LayoutParams iv_custom_param_right = new LayoutParams((int) (button_size / 2.5f), (int) (button_size / 2.5f));
        iv_custom_param_right.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        iv_custom_param_right.setMargins(0, 0, layout_width / 6, 0);
        iv_custom_right.setLayoutParams(iv_custom_param_right);
        iv_custom_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightClickListener != null) {
                    rightClickListener.onClick();
                }
            }
        });

        txt_tip = new TextView(getContext());
        LayoutParams txt_param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        txt_param.gravity = Gravity.CENTER_HORIZONTAL;
        txt_param.setMargins(0, (layout_height - layout_height / 3), 0, 0);
        txt_tip.setText("按住拍摄\n轻触拍照");
        txt_tip.setTextColor(0xFFFFFFFF);
        txt_tip.setGravity(Gravity.CENTER);
        txt_tip.setLayoutParams(txt_param);

        this.addView(btn_capture);
        this.addView(btn_cancel);
        this.addView(btn_confirm);
        this.addView(btn_return);
        this.addView(iv_custom_left);
        this.addView(iv_custom_right);
        this.addView(txt_tip);

        btn_return.setVisibility(GONE);
    }

    /**************************************************
     * 对外提供的API                      *
     **************************************************/
    public void resetCaptureLayout() {
        btn_capture.resetState();
        btn_cancel.setVisibility(GONE);
        btn_confirm.setVisibility(GONE);
        btn_capture.setVisibility(VISIBLE);
        if (this.iconLeft != 0)
            iv_custom_left.setVisibility(VISIBLE);
        else
            btn_return.setVisibility(GONE);
        if (this.iconRight != 0)
            iv_custom_right.setVisibility(VISIBLE);
    }


    public void startAlphaAnimation() {
        if (isFirst) {
            ObjectAnimator animator_txt_tip = ObjectAnimator.ofFloat(txt_tip, "alpha", 1f, 0f);
            animator_txt_tip.setDuration(500);
            animator_txt_tip.start();
            isFirst = false;
        }
    }

    public void setTextWithAnimation(String tip) {
        txt_tip.setText(tip);
        ObjectAnimator animator_txt_tip = ObjectAnimator.ofFloat(txt_tip, "alpha", 0f, 1f, 1f, 0f);
        animator_txt_tip.setDuration(2500);
        animator_txt_tip.start();
    }

    public void setDuration(int duration) {
        btn_capture.setDuration(duration);
    }

    public void setButtonFeatures(int state) {
        btn_capture.setButtonFeatures(state);
    }

    public void setTip(String tip) {
        txt_tip.setText(tip);
    }

    public void showTip() {
        txt_tip.setVisibility(VISIBLE);
    }

    public void setIconSrc(int iconLeft, int iconRight) {
        this.iconLeft = iconLeft;
        this.iconRight = iconRight;
        if (this.iconLeft != 0) {
            iv_custom_left.setImageResource(iconLeft);
            iv_custom_left.setVisibility(VISIBLE);
            btn_return.setVisibility(GONE);
        } else {
            iv_custom_left.setVisibility(GONE);
            btn_return.setVisibility(GONE);
        }
        if (this.iconRight != 0) {
            iv_custom_right.setImageResource(iconRight);
            iv_custom_right.setVisibility(VISIBLE);
        } else {
            iv_custom_right.setVisibility(GONE);
        }
    }

    public void setLeftClickListener(ClickListener leftClickListener) {
        this.leftClickListener = leftClickListener;
    }

    public void setRightClickListener(ClickListener rightClickListener) {
        this.rightClickListener = rightClickListener;
    }

    public void setCharacteristics(CameraCharacteristics characteristics){
        btn_capture.setCharacteristics(characteristics);
    }
}