package com.ubt.alpha1e.edu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.czt.mp3recorder.MP3Recorder;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.ResourceManager;
import com.ubt.alpha1e.edu.base.ToastUtils;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.maincourse.adapter.CourseArrowAminalUtil;
import com.ubt.alpha1e.edu.maincourse.adapter.CourseMusicDialogUtil;
import com.ubt.alpha1e.edu.maincourse.adapter.NextDialog;
import com.ubt.alpha1e.edu.utils.NameLengthFilter;
import com.ubt.alpha1e.edu.utils.TimeUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class DialogDub extends Dialog {


    private static final String TAG = "DialogDub";

    private CourseMusicDialogUtil.OnMusicDialogListener mOnMusicDialogListener;
    private DialogDub dialogDub;
    private Context context;
    private TextView tvNumber;
    private RelativeLayout rlDao;
    private RelativeLayout rlRecord;

    private RelativeLayout llOperation;
    private TextView tvRecordTime;
    private LinearLayout llRetry, llBottom, llMid;
    private RelativeLayout llSave;
    private DaoCountDown daoCountDown;
    MP3Recorder mRecorder;

    private long time = 0;
    private boolean finish = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                UbtLog.d(TAG, "msg what 2");
                finish = true;
                mRecorder.stop();
                llRetry.setVisibility(View.VISIBLE);
                llSave.setVisibility(View.VISIBLE);
                tvRecordTime.setVisibility(View.VISIBLE);
                mHandler.removeMessages(0);
                if (type == 1) {
                    if (null != mOnMusicDialogListener) {
                        mOnMusicDialogListener.onStopRecord(null, 1);
                    }
                    new NextDialog(context).builder()
                            .setPositiveButton(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CourseArrowAminalUtil.startViewAnimal(true, ivSaveArrow, 2);
                                }
                            })
                            .show();
                }
            } else if (msg.what == 1) {

                if (!finish) {
                    time += 1000;
                    UbtLog.d(TAG, "time:" + time);
                    tvRecordTime.setText("" + TimeUtils.getTimeFromMillisecond(time));
                }

            }
        }
    };


    private EditText editText;
    private RelativeLayout rlEditName;
    private TextView tvCancel, tvConfirm;
    private ImageView ivRecordArrow;
    private int type = -1;

    private ImageView ivSaveArrow;

    private ImageView ivAddArrow;

    String mDir = FileTools.record;

    private ImageView ivStop;

    private ImageView ivDao;


    public DialogDub(Context context) {
        super(context);
        this.context = context;
        dialogDub = this;
    }

    public DialogDub(Context context, int type, CourseMusicDialogUtil.OnMusicDialogListener mOnMusicDialogListener) {
        super(context);
        this.type = type;
        this.context = context;
        dialogDub = this;
        this.mOnMusicDialogListener = mOnMusicDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogDub.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_dub);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogDub.getWindow().getAttributes();
        lp.width = (int) ((display.getWidth()) * 0.8); //设置宽度
        dialogDub.getWindow().setAttributes(lp);
        dialogDub.setCanceledOnTouchOutside(false);
        dialogDub.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.action_dialog_filter_rect));
        initUI();
        initRecord();

        daoCountDown = new DaoCountDown(4000, 1000);
//        daoCountDown.start();

    }


    private void initUI() {
        tvNumber = (TextView) findViewById(R.id.tv_number);
        rlDao = (RelativeLayout) findViewById(R.id.rl_dao);
        rlRecord = (RelativeLayout) findViewById(R.id.rl_record);

        llOperation = (RelativeLayout) findViewById(R.id.ll_operation);
        tvRecordTime = (TextView) findViewById(R.id.tv_record_time);
        llRetry = (LinearLayout) findViewById(R.id.ll_retry);
        llSave = (RelativeLayout) findViewById(R.id.ll_finish);
        llMid = (LinearLayout) findViewById(R.id.ll_mid);

        ivSaveArrow = findViewById(R.id.iv_complete_arrow);

        ivAddArrow = findViewById(R.id.iv_add_arrow);

        if (type == 1) {
            ivRecordArrow = findViewById(R.id.iv_record_arrow);
            CourseArrowAminalUtil.startViewAnimal(true, ivRecordArrow, 2);
        }
        llRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != 1) {
                    UbtLog.d(TAG, "msg what 3");
//                finish = false;
                    llMid.setVisibility(View.GONE);
                    rlRecord.setVisibility(View.GONE);
                    llOperation.setVisibility(View.INVISIBLE);
                    rlDao.setVisibility(View.VISIBLE);
                    ivDao.setImageResource(R.drawable.bgbegin_recording);
                    ivDao.setEnabled(true);
//                time = 0;
//                tvRecordTime.setText("00:00");
//                daoCountDown.start();
                }
            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlDao.setVisibility(View.GONE);
                rlRecord.setVisibility(View.GONE);
                llOperation.setVisibility(View.GONE);
                llMid.setVisibility(View.GONE);
                rlEditName.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.VISIBLE);
                if (type == 1) {
                    CourseArrowAminalUtil.startViewAnimal(false, ivSaveArrow, 2);
                    if (null != mOnMusicDialogListener) {
                        mOnMusicDialogListener.onStopRecord(null, 3);
                    }
                }

            }
        });

        editText = (EditText) findViewById(R.id.et_record_name);
        InputFilter[] filters = {new NameLengthFilter(16)};
        editText.setFilters(filters);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 这里可以知道你已经输入的字数，大家可以自己根据需求来自定义文本控件实时的显示已经输入的字符个数
                Log.e("此时你已经输入了", "" + s.length());
                if (type == 1) {
                    int after_length = s.length();// 输入内容后编辑框所有内容的总长度
                    // 如果字符添加后超过了限制的长度，那么就移除后面添加的那一部分，这个很关键
                    if (after_length > 1) {
                        //editText.setFocusable(false);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); //强制隐藏键盘
                        CourseArrowAminalUtil.startViewAnimal(true, ivAddArrow, 2);
                        if (null != mOnMusicDialogListener) {
                            mOnMusicDialogListener.onStopRecord(null, 4);
                        }

                    }
                }
            }
        });

        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        rlEditName = (RelativeLayout) findViewById(R.id.rl_edit_name);

        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != 1) {
                    deleteMP3RecordFile("");
                    dismiss();
                }
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString().trim();
                UbtLog.d(TAG, "name length:" + name.length());

                String input = name.toString();
                String str = stringFilter(input);
                UbtLog.d(TAG, "input:" + input + "---str:" + str);
                if (!input.equals(str)) {
                    ToastUtils.showShort(ResourceManager.getInstance(context).getStringResources("ui_action_name_error"));
                    return;
                }

                if (name.length() > 0) {
                    saveRecord(name);
                } else {
                    ToastUtils.showShort(ResourceManager.getInstance(context).getStringResources("ui_create_record_empty_name"));
                }

            }
        });

        ivStop = (ImageView) findViewById(R.id.iv_stop_dub);
        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbtLog.d(TAG, "msg what 0 1");
//                if(mHandler.hasMessages(0)){
//                    mHandler.removeMessages(0);
//                }

                mHandler.sendEmptyMessage(0);


            }
        });

        ivDao = (ImageView) findViewById(R.id.iv_dao);
        ivDao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1 && null != ivRecordArrow) {
                    CourseArrowAminalUtil.startViewAnimal(false, ivRecordArrow, 1);
                }
                ivDao.setImageResource(R.drawable.countdown);
                tvNumber.setVisibility(View.VISIBLE);
                daoCountDown.start();
                ivDao.setEnabled(false);
            }
        });


    }


    private void initRecord() {
        //初始化录音功能
        if (mRecorder == null) {

            File file = new File(mDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            File mp3 = new File(mDir + File.separator +
                    "tep_audio_recorder.mp3");

            mRecorder = new MP3Recorder(mp3);
        }
    }


    public void saveRecord(final String name) {
        if (FileTools.checkFile(mDir + File.separator + name + ".mp3")) {
            if (type == 1) {
                ToastUtils.showShort("音频名字重复");
                return;
            }
            MyAlertDialog.getInstance(
                    context,
                    ResourceManager.getInstance(context).getStringResources("ui_resave_tip"),
                    ResourceManager.getInstance(context).getStringResources("ui_common_cancel"),
                    ResourceManager.getInstance(context).getStringResources("ui_common_confirm"), new IMessageListeter() {

                        @Override
                        public void onViewAction(boolean isOk) {
                            if (isOk) {
                                boolean success = FileTools.renameFile(mDir + File.separator + "tep_audio_recorder.mp3", mDir + File.separator + name + ".mp3");
                                if (success) {
                                    dismiss();
                                } else {
                                    Toast.makeText(context, context.getString(R.string.ui_create_record_save_failed), Toast.LENGTH_SHORT);
                                }
                            } else {
                            }
                        }
                    }).show();
            return;
        } else {
            boolean success = FileTools.renameFile(mDir + File.separator + "tep_audio_recorder.mp3", mDir + File.separator + name + ".mp3");
            if (success) {
                if (null != mOnMusicDialogListener) {
                    mOnMusicDialogListener.onStopRecord(null, 2);
                }
                dismiss();
            } else {
                Toast.makeText(context, context.getString(R.string.ui_create_record_save_failed), Toast.LENGTH_SHORT);
            }
        }

    }

    public void deleteMP3RecordFile(String name) {
        if (name.equals("") || name.equals(null)) {
            FileTools.DeleteFile(new File(mDir + File.separator + "tep_audio_recorder.mp3"));
        } else {
            UbtLog.d(TAG, "deleteMP3RecordFile:" + name);
            FileTools.DeleteFile(new File(mDir + File.separator + name));
        }

    }


    @Override
    protected void onStop() {
        UbtLog.d(TAG, "Dialog Dub onStop");
        super.onStop();

        if (daoCountDown != null) {
            daoCountDown.cancel();
        }

        if (mRecorder != null) {
            try {
                mRecorder.stop();
            } catch (Exception e) {
                UbtLog.e(TAG, "throw Ex=" + e.getMessage());
            }

        }


    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    // 设置过滤字符函数(过滤掉我们不需要的字符)
    public static String stringFilter(String str) throws PatternSyntaxException {
        //定义一个无效字符的正则表达式
        String regEx = "[^a-zA-Z0-9\\u4E00-\\u9FA5_\\s]";
        //生成Pattern对象并且编译一个正则表达式regEx
        Pattern p = Pattern.compile(regEx);
        //用Pattern类的matcher()方法生成一个Matcher对象
        Matcher m = p.matcher(str);
        //如果输入为无效字符则替代为""
        return m.replaceAll("");
    }


    class DaoCountDown extends CountDownTimer {

        public DaoCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvNumber.setText("" + (millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            rlDao.setVisibility(View.GONE);
            llMid.setVisibility(View.VISIBLE);
            rlRecord.setVisibility(View.VISIBLE);
            llOperation.setVisibility(View.VISIBLE);
            tvRecordTime.setVisibility(View.VISIBLE);
            llSave.setVisibility(View.INVISIBLE);
            llRetry.setVisibility(View.INVISIBLE);
            finish = false;
            time = 0;
            tvRecordTime.setText("00:00");

            try {
                mRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final int milliseconds = 1000;
            UbtLog.d(TAG, "sendEmptyMessage 1:" + finish);
            new Thread() {
                @Override
                public void run() {
                    while (true && !finish) {
                        try {
                            sleep(milliseconds);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        mHandler.sendEmptyMessage(1);
                    }
                }
            }.start();

            mHandler.sendEmptyMessageDelayed(0, 11 * 1000);

        }
    }


}
