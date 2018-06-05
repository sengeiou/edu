package com.ubt.alpha1e_edu.utils.test;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.FileTools.State;
import com.ubt.alpha1e_edu.ui.BaseActivity;
import com.ubt.alpha1e_edu.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e_edu.ui.helper.IEditActionUI;

public class EngTestActivity extends BaseActivity implements IEditActionUI {

    private Button btn_read_all_eng;
    private Button btn_send;
    private TextView txt_result;
    private byte[] engs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eng_test);
        mHelper = new ActionsEditHelper(this, this);
        initUI();
        initControlListener();
    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub
        btn_read_all_eng = (Button) findViewById(R.id.btn_read_all_eng);
        btn_send = (Button) findViewById(R.id.btn_send);
        txt_result = (TextView) findViewById(R.id.txt_result);
    }

    @Override
    protected void initControlListener() {
        // TODO Auto-generated method stub
        btn_read_all_eng.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((ActionsEditHelper) mHelper).doReadAllEng();
            }
        });
        btn_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // ((ActionsEditHelper) mHelper).doCtrlAllEng(engs, 200, 200);
            }
        });
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadEng(byte[] _end_angle) {
        // TODO Auto-generated method stub
        engs = _end_angle;
        String text = "";
        for (int i = 0; i < engs.length; i++) {
            text += engs[i];
        }
        txt_result.setText(text);
    }

    @Override
    public void onPlaying() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPausePlay() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinishPlay() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChangeActionFinish() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFrameDo(int index) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result,
                                    boolean result_state, long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result,
                                     long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWriteDataFinish(long requestCode, State state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadCacheSize(int size) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClearCache() {
        // TODO Auto-generated method stub

    }

    @Override
    public void notePlayChargingError() {

    }
}
