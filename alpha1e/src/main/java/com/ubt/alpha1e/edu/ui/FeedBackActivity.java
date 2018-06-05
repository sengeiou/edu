package com.ubt.alpha1e.edu.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.DataCheckTools;
import com.ubt.alpha1e.edu.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e.edu.ui.helper.FeedBackHelper;
import com.ubt.alpha1e.edu.ui.helper.IFeedBackUI;

import java.io.UnsupportedEncodingException;

public class FeedBackActivity extends BaseActivity implements IFeedBackUI {


    private Button btn_submit;
    private EditText edt_content;
    private EditText edt_email;
    private TextView txt_max_length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        mHelper = new FeedBackHelper(this, this);
        initUI();
        initControlListener();
    }

    @Override
    protected void initUI() {

        btn_submit = (Button) findViewById(R.id.btn_submit);
        edt_content = (EditText) findViewById(R.id.edt_content);
        edt_email = (EditText) findViewById(R.id.edt_email);
        txt_max_length = (TextView) findViewById(R.id.txt_max_length);
    }

    @Override
    protected void initControlListener() {

        initTitle("");
        edt_content.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                txt_max_length.setVisibility(arg0.length() > 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
        btn_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int lenth = 0;
                try {
                    lenth = edt_content.getText().toString().getBytes("GBK").length;
                } catch (UnsupportedEncodingException e) {
                    lenth = 0;
                }

                if (lenth > 400) {
                    FeedBackActivity.this.showToast("ui_about_feedback_input_too_long");
                    return;
                }

                String feed_back_txt = edt_content.getText().toString();
                String feed_back_email = edt_email.getText().toString();
                if (feed_back_txt.equals("")) {
                    FeedBackActivity.this.showToast("ui_about_feedback_empty");
                    return;

                }

                if (feed_back_email.equals("")
                        && mHelper.getCurrentUser() == null) {
                    FeedBackActivity.this.showToast("ui_login_email_placeholder");
                    return;

                }

                if (!feed_back_email.equals("")
                        && !DataCheckTools.isEmail(feed_back_email)) {
                    FeedBackActivity.this.showToast("ui_login_prompt_email_wrong_format");
                    return;
                }

                ((FeedBackHelper) mHelper).doFeedBack(feed_back_txt,
                        feed_back_email);

                try {
                    mCoonLoadingDia.cancel();
                } catch (Exception e) {
                }
                mCoonLoadingDia = SLoadingDialog
                        .getInstance(FeedBackActivity.this);
                mCoonLoadingDia.show();

            }
        });
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFeedBackFinish(boolean isSuccess, final String error_msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mCoonLoadingDia.cancel();
                } catch (Exception e) {
                }
                Toast.makeText(FeedBackActivity.this, error_msg, Toast.LENGTH_SHORT).show();
            }
        });
        if (isSuccess) {
            this.finish();
        }
    }

}
