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
import com.ubt.alpha1e.edu.ui.helper.RobotInfoHelper;

public class RoborRenameActivity extends BaseActivity {

    private EditText edt_name;
    private Button btn_cancel;
    private Button btn_save;
    private Button btn_cancel_name;
    private TextView txt_base_title_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robor_rename);
        mHelper = new RobotInfoHelper(this);
        initUI();
        initControlListener();
    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub
        btn_cancel_name = (Button) findViewById(R.id.btn_cancel_name);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        txt_base_title_name = (TextView) findViewById(R.id.txt_base_title_name);
        edt_name = (EditText) findViewById(R.id.edt_name);
        txt_base_title_name.setText(((RobotInfoHelper) mHelper).getRobotName());
        edt_name.setHint(((RobotInfoHelper) mHelper).getRobotName());

    }

    @Override
    protected void initControlListener() {
        // TODO Auto-generated method stub
        btn_cancel_name.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                edt_name.setText("");
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                RoborRenameActivity.this.finish();
            }
        });
        btn_save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String name = edt_name.getText().toString();

                if (name.equals("")) {
                    Toast.makeText(
                            RoborRenameActivity.this,
                            RoborRenameActivity.this
                                    .getString(R.string.ui_action_name_empty),
                            500).show();
                    return;
                }

                if (!name.toLowerCase().startsWith("alpha_")) {
                    name = "alpha_" + name;
                }

                if (!mHelper.isRightName(name, 16, false, "[0-9A-Za-z_]*")) {
                    return;
                }

                ((RobotInfoHelper) mHelper).doRenameRobot(name);

                Toast.makeText(
                        RoborRenameActivity.this,
                        RoborRenameActivity.this
                                .getString(R.string.ui_settings_success),
                        500).show();
                RoborRenameActivity.this.finish();

            }
        });
        edt_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                if (arg0.length() > 0) {
                    btn_cancel_name
                            .setBackgroundResource(R.drawable.sec_delete_icon);
                } else {

                    btn_cancel_name
                            .setBackgroundResource(R.drawable.sec_delete_icon_low);

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }
}
